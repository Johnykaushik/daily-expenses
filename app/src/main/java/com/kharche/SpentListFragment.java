package com.kharche;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kharche.adapters.CategoryAdapter;
import com.kharche.adapters.SpentAdapter;
import com.kharche.dao.SpentDao;
import com.kharche.model.Spent;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SpentListFragment extends Fragment {


    public SpentListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spent_list, container, false);

        RecyclerView spentListContainer = (RecyclerView) view.findViewById(R.id.spent_list_container);
        SpentAdapter spentAdapter = new SpentAdapter(getAllSpent());
        spentListContainer.setAdapter(spentAdapter);
        spentListContainer.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    private List<Spent> getAllSpent() {
        List<Spent> setSpentList  = new ArrayList<>();

        SpentDao spentDao = new SpentDao(requireContext());
        List<Spent> spentList = spentDao.getAllSpents();
        for (Spent spent : spentList) {
            Spent setSpent = new Spent();

            setSpent.setAmount(spent.getAmount());
            setSpent.setDescription(spent.getDescription());
            setSpent.setCreatedAt(spent.getCreatedAt());
            setSpent.setId(spent.getId());
            if(spent.getCategoryId() != null){
                setSpent.setCategoryId(spent.getCategoryId());
            }

            setSpentList.add(setSpent);
        }
        return setSpentList;
    }
}