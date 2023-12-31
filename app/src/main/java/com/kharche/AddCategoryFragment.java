package com.kharche;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.kharche.dao.CategoryDao;
import com.kharche.interfaces.IToolbarHeadingTitle;
import com.kharche.model.Category;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AddCategoryFragment extends Fragment {
    // text view variable to set the color for GFG text
    private TextView gfgTextView;

    // two buttons to open color picker dialog and one to
    // set the color for GFG text
    private Button mSetColorButton, mPickColorButton;

    // view box to preview the selected color
    private View mColorPreview;

    // this is the default color of the preview box
    private int mDefaultColor;
    private IToolbarHeadingTitle iToolbarHeadingTitle;
    public AddCategoryFragment(IToolbarHeadingTitle iToolbarHeadingTitle) {
        // Required empty public constructor
        this.iToolbarHeadingTitle = iToolbarHeadingTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        iToolbarHeadingTitle.setToolBarTitle(R.string.add_category);

        // functionality start
        EditText category = view.findViewById(R.id.category_name);
        Button submitButton = view.findViewById(R.id.submit_btn);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = category.getText().toString();
                if (!categoryName.isEmpty()) {
                    CategoryDao categoryDao = new CategoryDao(requireContext());
                    Category existingCat = categoryDao.getCategory(categoryName);
                    if (existingCat.getId() > 0) {
                        Snackbar.make(v, R.string.category_exist, Snackbar.LENGTH_LONG).show();
                    } else {
                        Category categoryBase = new Category();
                        categoryBase.setCategoryName(categoryName);
                        Long isAdded = categoryDao.addCategory(categoryBase);
                        if (isAdded > 0) {
                            category.setText("");
                            Snackbar.make(v, R.string.category_add_success, Snackbar.LENGTH_LONG).show();
                            moveToList();
                        } else {
                            Snackbar.make(v, R.string.unable_to_process, Snackbar.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Snackbar.make(v, R.string.category_required, Snackbar.LENGTH_LONG).show();
                }

            }
        });


        // Functionality end
        return view;
    }

    private void moveToList() {
//        try {
//            Thread.sleep(500);
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            CategoryItemListFragment categoryListFragment = new CategoryItemListFragment();
//            transaction.replace(R.id.fragment_container, categoryListFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        } catch (InterruptedException e) {
//            // Handle the exception if needed
//        }
    }
}