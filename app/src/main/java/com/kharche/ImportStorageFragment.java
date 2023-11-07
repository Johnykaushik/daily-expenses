package com.kharche;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kharche.dao.CategoryDao;
import com.kharche.dao.SpentDao;
import com.kharche.db.DatabaseHelper;
import com.kharche.db.TableName;
import com.kharche.helpers.FileUploadHelper;
import com.kharche.interfaces.IToolbarHeadingTitle;
import com.kharche.model.Category;
import com.kharche.model.Spent;

import java.util.HashMap;
import java.util.Map;

public class ImportStorageFragment extends Fragment {
    private IToolbarHeadingTitle iToolbarHeadingTitle;

    public ImportStorageFragment(IToolbarHeadingTitle iToolbarHeadingTitle) {
        this.iToolbarHeadingTitle = iToolbarHeadingTitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        iToolbarHeadingTitle.setToolBarTitle(R.string.import_db);
        View view = inflater.inflate(R.layout.fragment_import_storage, container, false);
        Button select_file = view.findViewById(R.id.select_file);
        Button submit_btn = view.findViewById(R.id.submit_file_btn);
        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUploadHelper.getInstance().selectAndSaveFile(requireActivity());
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dbPath = FileUploadHelper.getInstance().getExternalDbPath();
                if (dbPath != null) {
                    if (!dbPath.isEmpty()) {
                        syncDatabase(dbPath);
                    }
                }
            }
        });

        return view;
    }

    private void syncDatabase(String path) {
        DatabaseHelper databaseHelperV2 = new DatabaseHelper(requireContext(), path);
        try {

            SQLiteDatabase dbV2 = databaseHelperV2.getReadableDatabase();

            String cat_query = "SELECT * FROM " + TableName.CATEGORY;
            String spent_query = "SELECT * FROM " + TableName.SPENT_AMOUNT;

            Cursor cursor = dbV2.rawQuery(cat_query, null);

            CategoryDao categoryDao = new CategoryDao(requireContext()); // master db
            Map<Integer,Integer> categoryNewId = new HashMap<>();
            if (cursor != null) {
                while(cursor.moveToNext()){
                    int cat_id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String category_name = cursor.getString(cursor.getColumnIndexOrThrow("category"));

                    if(cat_id > 0 && category_name != null){
                      Category category = categoryDao.getCategory(category_name); // master db
                        if(category != null){
                            int id = category.getId();
                            if(id != cat_id){
                                Category addCat = new Category();
                                addCat.setCategoryName(category_name);

                                int added_id = (int) categoryDao.addCategory(addCat);
                                if(added_id > 0){
                                    categoryNewId.put(cat_id,added_id);
                                }
                            }else {
                                Log.d("TAG", "syncDatabase: keys: is same: " +id + " exst " + cat_id );

                            }
                        }
                    }
                }

                for(Map.Entry<Integer,Integer> data : categoryNewId.entrySet()){
                    Log.d("TAG", "syncDatabase: keys: " + data.getKey() + ", value : " + data.getValue());
                }

                Cursor spent_cursor = dbV2.rawQuery(spent_query, null);

                if(spent_cursor != null){
                    SpentDao spentDao = new SpentDao(requireContext());
                    while (spent_cursor.moveToNext()){
                        int id = spent_cursor.getInt(spent_cursor.getColumnIndexOrThrow("id"));
                        int category_id = spent_cursor.getInt(spent_cursor.getColumnIndexOrThrow("category_id"));
                        int amount = spent_cursor.getInt(spent_cursor.getColumnIndexOrThrow("amount"));
                        String description = spent_cursor.getString(spent_cursor.getColumnIndexOrThrow("description"));
                        String created_at = spent_cursor.getString(spent_cursor.getColumnIndexOrThrow("created_at"));

                        int addCatId = category_id;
                        if(categoryNewId.get(category_id) != null){
                            addCatId = categoryNewId.get(category_id);
                        }

                        if(addCatId > 0 && (created_at != null && created_at.contains(":"))){
                            Spent spent = new Spent();
                            Category addCat = new Category();
                            addCat.setId(addCatId);
                            spent.setCategoryId(addCat);
                            spent.setAmount(amount);
                            spent.setDescription(description);
                            spent.setCreatedAt(created_at);
                            spentDao.addSpent(spent);
                        }

                        Log.d("TAG", "syncDatabase: spent list  " + "id: " + id + ", category_id: " + category_id + " updated id " + addCatId +  ", amount: " + amount + ", description: "  +description + ", created_at: " + created_at);
                    }
                }
                spent_cursor.close();
            } else {
                Log.d("TAG", "getExternalCategoryList: null val ");
            }
            cursor.close();
        } catch (Exception ex) {
            ex.fillInStackTrace();
            Log.d("TAG", "getExternalCategoryList: error  " + ex.getMessage());
        }
    }
}

