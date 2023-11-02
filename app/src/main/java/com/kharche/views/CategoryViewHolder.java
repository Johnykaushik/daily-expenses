package com.kharche.views;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kharche.R;


public class CategoryViewHolder extends RecyclerView.ViewHolder {

    TextView category_v_name;
    View view;
    ImageView delete_category;
    public CategoryViewHolder(View viewItem){
        super(viewItem);
        category_v_name = (TextView) viewItem.findViewById(R.id.category_v_name);
        delete_category = (ImageView) viewItem.findViewById(R.id.delete_category);
        view = viewItem;
    }

    public TextView getCategoryVName() {
        return category_v_name;
    }
    public ImageView getDeleteCategory() { return  delete_category; }
    public View getViewS(){
        return view;
    }
}
