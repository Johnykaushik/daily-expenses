package com.kharche.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.kharche.interfaces.OnItemClickListener;
import com.kharche.views.CategoryViewHolder;
import com.kharche.R;
import com.kharche.model.Category;

import java.util.Collections;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private OnItemClickListener clickListener;

    List<Category> categoryList = Collections.emptyList();

    public CategoryAdapter(List<Category> categoryList, OnItemClickListener listener){
        this.categoryList = categoryList;
        this.clickListener = listener;
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
        String setCat = (index +1) +"). "+   categoryList.get(position).categoryName;
        if(categoryList.get(position).associatedSpent > 0){
           setCat += "(" + categoryList.get(position).associatedSpent  +  ")";
        }
        if(categoryList.get(position).spentAmount > 0){
            setCat += " ₹" + categoryList.get(position).spentAmount;
        }
        categoryViewHolder.getCategoryVName().setText(setCat);

        categoryViewHolder.getDeleteCategory().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(index);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public void updateData(List<Category> categoryList){
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }
}
