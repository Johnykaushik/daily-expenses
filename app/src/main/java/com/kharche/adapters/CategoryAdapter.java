package com.kharche.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.kharche.views.CategoryViewHolder;
import com.kharche.R;
import com.kharche.model.Category;

import java.util.Collections;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    List<Category> categoryList = Collections.emptyList();

    public CategoryAdapter(List<Category> categoryList){
        this.categoryList = categoryList;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_child_list_item, viewGroup, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder categoryViewHolder, final int position){
        final int index = categoryViewHolder.getBindingAdapterPosition();
        categoryViewHolder.getCategoryVName();
        categoryViewHolder.getCategoryVName().setText(categoryList.get(position).categoryName);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
