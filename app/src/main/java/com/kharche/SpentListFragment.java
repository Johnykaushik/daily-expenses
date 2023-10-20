package com.kharche;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kharche.dao.CategoryDao;
import com.kharche.dao.SpentDao;
import com.kharche.model.Category;
import com.kharche.model.Spent;

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
        getAllSpents();

        return view;
    }

    private void getAllSpents() {
        SpentDao spentDao = new SpentDao(requireContext());
        List<Spent> spentList = spentDao.getAllSpents();
        for (Spent spent : spentList) {
            System.out.println("all  spent 1 " + spent.toString() + spent.getCategoryId());
            if(spent.getCategoryId() != null){
                System.out.println("all  spent b " + spent.getCategoryId().toString());
            }
        }
    }
}