package com.kharche.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.kharche.interfaces.OnItemClickListener;
import com.kharche.model.Spent;
import com.kharche.views.CategoryViewHolder;
import com.kharche.R;
import com.kharche.model.Category;
import com.kharche.views.SpentViewHolder;

import java.util.Collections;
import java.util.List;

public class SpentAdapter extends RecyclerView.Adapter<SpentViewHolder> {

    List<Spent> spentList = Collections.emptyList();
    OnItemClickListener onItemClickListener;

    public SpentAdapter(List<Spent> spentList, OnItemClickListener onItemClickListener) {

        this.spentList = spentList;
        this.onItemClickListener = onItemClickListener;
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
        spentViewHolder.getSpentAmount().setText("₹"+amount);
        String category = "N/A";
        if(spentList.get(position).getCategoryId() != null){
            category = spentList.get(position).getCategoryId().getCategoryName();
        }
        spentViewHolder.getCategoryName().setText(category);
        spentViewHolder.getDescription().setText(spentList.get(position).description);
        spentViewHolder.getDateTime().setText(spentList.get(position).createdAt);

        spentViewHolder.getDeleteSpent().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return spentList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateData(List<Spent> spentList){
        this.spentList = spentList;
        notifyDataSetChanged();
    }
}
