package com.kharche;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.kharche.dao.CategoryDao;
import com.kharche.model.Category;

import java.util.List;

public class CategoryListFragment extends Fragment {
    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        getCategories();

        return view;
    }

    private void getCategories() {
        CategoryDao categoryDao = new CategoryDao(requireContext());
        List<Category> categories = categoryDao.getAllCategories();
        for (Category category : categories) {
            int categoryId = category.getId();
            String categoryName = category.getCategoryName();
        }
    }
}
