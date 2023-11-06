
package com.kharche.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kharche.R;


public class SpentViewHolder extends RecyclerView.ViewHolder {
    TextView spent_amount;
    TextView category_name;
    TextView date_time;
    TextView description;
ImageView delete_spent;
    View view;


    public SpentViewHolder(View viewItem) {
        super(viewItem);
        spent_amount = (TextView) viewItem.findViewById(R.id.spent_amount);
        category_name = (TextView) viewItem.findViewById(R.id.category_name);
        date_time = (TextView) viewItem.findViewById(R.id.date_time);
        description = (TextView) viewItem.findViewById(R.id.description);
        delete_spent = (ImageView) viewItem.findViewById(R.id.delete_spent);
        view = viewItem;
    }

    public TextView getCategoryName(){
        return  category_name;
    }

    public TextView getSpentAmount(){
        return spent_amount;
    }
    public TextView getDateTime(){
        return date_time;
    }
    public TextView getDescription(){
        return description;
    }
    public ImageView getDeleteSpent(){
        return  delete_spent;
    }
    public View getView(){
        return view;
    }
}

