package com.kharche;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.kharche.adapters.CategoryAdapter;
import com.kharche.dao.CategoryDao;
import com.kharche.interfaces.IToolbarHeadingTitle;
import com.kharche.interfaces.OnItemClickListener;
import com.kharche.model.Category;
import com.kharche.utils.AlertPop;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends Fragment implements OnItemClickListener {
    private CategoryAdapter categoryAdapter; // Declare the adapter as a class variable
    private List<Category> categoryList;
    private IToolbarHeadingTitle iToolbarHeadingTitle;
    public CategoryListFragment(IToolbarHeadingTitle iToolbarHeadingTitle) {
        // Required empty public constructor
        this.iToolbarHeadingTitle = iToolbarHeadingTitle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        iToolbarHeadingTitle.setToolBarTitle(R.string.category_list);


        Switch sort_max_category = view.findViewById(R.id.sort_max_category);

        RecyclerView categoryListContainer = (RecyclerView) view.findViewById(R.id.category_list_container);
        categoryList = getCategories(false);
        categoryAdapter = new CategoryAdapter(categoryList, this);
        categoryListContainer.setAdapter(categoryAdapter);

        categoryListContainer.setLayoutManager(new LinearLayoutManager(requireContext()));

        sort_max_category.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                categoryList = getCategories(isChecked);
                categoryAdapter.updateData(categoryList);
            }
        });

        return view;
    }

    //    Delete category
    public void onItemClick(int position) {
        long catId = categoryList.get(position).getId();
        CategoryDao categoryDao = new CategoryDao(requireContext());
        int deleteStatus = categoryDao.deleteCategory(catId);
        try {
            AlertPop alertPop = new AlertPop(requireContext());

            if (deleteStatus == 0) {
                String category_spent_associate = getResources().getString(R.string.category_spent_associate);
                alertPop.showAlertDialog(category_spent_associate);
            } else if (deleteStatus == 1) {
                categoryList.remove(position);
                String category_delete_success = getResources().getString(R.string.category_delete_success);
                alertPop.showAlertDialog(category_delete_success);

            } else {
                String unable_to_process = getResources().getString(R.string.unable_to_process);
                alertPop.showAlertDialog(unable_to_process);
            }
            categoryAdapter.notifyDataSetChanged();
            System.out.println("Item clicked " + position + " deleted status " + deleteStatus);
        } catch (Exception ex) {

        }
    }

    private List<Category> getCategories(boolean maxOnTop) {
        List<Category> cateList = new ArrayList<>();

        CategoryDao categoryDao = new CategoryDao(requireContext());
        List<Category> categories = categoryDao.getAllCategories(maxOnTop);
        for (Category category : categories) {
            int categoryId = category.getId();
            String categoryName = category.getCategoryName();

            Category setCat = new Category();
            setCat.setId(categoryId);
            setCat.setCategoryName(categoryName);
            setCat.setAssociatedSpent(category.getAssociatedSpent());
            setCat.setSpentAmount(category.getSpentAmount());
            cateList.add(setCat);
        }
        return cateList;
    }
}
