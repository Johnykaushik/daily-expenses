package com.kharche;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kharche.adapters.CategoryAdapter;
import com.kharche.dao.CategoryDao;
import com.kharche.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends Fragment {
    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        RecyclerView categoryListContainer = (RecyclerView) view.findViewById(R.id.category_list_container);
        CategoryAdapter categoryAdapter = new CategoryAdapter(getCategories());
        categoryListContainer.setAdapter(categoryAdapter);
        categoryListContainer.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    private List<Category> getCategories() {
        List<Category> cateList = new ArrayList<>();

        CategoryDao categoryDao = new CategoryDao(requireContext());
        List<Category> categories = categoryDao.getAllCategories();
        for (Category category : categories) {
            int categoryId = category.getId();
            String categoryName = category.getCategoryName();

            Category setCat = new Category();
//            setCat.setId(categoryId);
            setCat.setCategoryName(categoryName);

            cateList.add(setCat);
        }
        return cateList;
    }
}
