package com.kharche.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.kharche.model.Spent;
import com.kharche.views.CategoryViewHolder;
import com.kharche.R;
import com.kharche.model.Category;
import com.kharche.views.SpentViewHolder;

import java.util.Collections;
import java.util.List;

public class SpentAdapter extends RecyclerView.Adapter<SpentViewHolder> {

    List<Spent> spentList = Collections.emptyList();

    public SpentAdapter(List<Spent> spentList) {

        this.spentList = spentList;
    }

    @Override
    public SpentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.spent_child_list_item, viewGroup, false);
        return new SpentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SpentViewHolder spentViewHolder, final int position) {

        final int index = spentViewHolder.getBindingAdapterPosition();
        String amount = String.valueOf(spentList.get(position).amount);
        spentViewHolder.getSpentAmount().setText(amount);
        String category = "N/A";
        if(spentList.get(position).getCategoryId() != null){
            category = spentList.get(position).getCategoryId().getCategoryName();
        }
        spentViewHolder.getCategoryName().setText(category);
        spentViewHolder.getDescription().setText(spentList.get(position).description);
        spentViewHolder.getDateTime().setText(spentList.get(position).createdAt);
    }

    @Override
    public int getItemCount() {
        return spentList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
