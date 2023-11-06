package com.kharche;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kharche.dao.CategoryDao;
import com.kharche.db.TableName;
import com.kharche.interfaces.IToolbarHeadingTitle;
import com.kharche.model.Category;
import com.kharche.utils.AlertPop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IToolbarHeadingTitle {
    String DATABASE_NAME =  TableName.DATABASE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            replaceAndRemoveFragments(new DashboardFragment(this));
        }
    }

    public void setToolBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setToolBarTitle(int title) {
        getSupportActionBar().setTitle(title);
    }

    public CharSequence getToolBarTitle(){
        return getSupportActionBar().getTitle();
    }
    private List<Category> getCategories() {
        List<Category> cateList = new ArrayList<>();

        CategoryDao categoryDao = new CategoryDao(this);
        List<Category> categories = categoryDao.getAllCategories(true);
        for (Category category : categories) {
            int categoryId = category.getId();
            String categoryName = category.getCategoryName();

            Category setCat = new Category();
            setCat.setId(categoryId);
            setCat.setCategoryName(categoryName);
            setCat.setAssociatedSpent(category.getAssociatedSpent());
            cateList.add(setCat);
        }
        return cateList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.dashboard) {
            replaceAndRemoveFragments(new DashboardFragment(this));
        } else if (itemId == R.id.add_spent_amount) {
            replaceAndRemoveFragments(new AddSpentFragment(this));
        } else if (itemId == R.id.spent_list) {
            replaceAndRemoveFragments(new SpentListFragment(this));
        } else if (itemId == R.id.add_category) {
            replaceAndRemoveFragments(new AddCategoryFragment(this));
        } else if (itemId == R.id.category_list) {
            replaceAndRemoveFragments(new CategoryListFragment(this));
        } else if (itemId == R.id.export_db) {
            exportDB();
        }else if (itemId == R.id.export_text_message){
            TextMessages textMessages = new TextMessages(this);
            textMessages.requestSmsPermission();
        } else {
            replaceAndRemoveFragments(new DashboardFragment(this));
        }
        return super.onContextItemSelected(item);
//        return true;
    }

    private void replaceAndRemoveFragments(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void exportDB() {
        try {
            File dbFile = new File(this.getDatabasePath(DATABASE_NAME).getAbsolutePath());
            Log.d("TAG", "exportDB:  exiting path  " + this.getDatabasePath(DATABASE_NAME).getAbsolutePath());
            if (dbFile.exists()) {
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                String fileName = "kharcha-" + cal.getTimeInMillis() + ".db";
                Log.d("TAG", "exportDB: file name " + fileName);
                File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),  fileName);
                // Perform the file copy
                FileInputStream inputStream = new FileInputStream(dbFile);
                FileOutputStream outputStream = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                AlertPop alertPop = new AlertPop(this);
                alertPop.showAlertDialog("Storage exported, please check you downloads");
                Log.d("TAG", "exportDB  File copied successfully");
            } else {
                // Handle the case where the database file doesn't exist
                Log.e("TAG", "exportDB  Database file doesn't exist.");
            }
        } catch (Exception e) {
            Log.e("TAG", "exportDB: " + e.getMessage());
        }
    }

}