package com.kharche.views;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kharche.R;


public class CategoryViewHolder extends RecyclerView.ViewHolder {

    TextView category_v_name;
    View view;
    public CategoryViewHolder(View viewItem){
        super(viewItem);
        category_v_name = (TextView) viewItem.findViewById(R.id.category_v_name);
        view = viewItem;
    }

    public TextView getCategoryVName() {
        return category_v_name;
    }

    public View getViewS(){
        return view;
    }
}
