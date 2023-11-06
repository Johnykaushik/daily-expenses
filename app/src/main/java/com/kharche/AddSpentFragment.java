package com.kharche;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kharche.dao.CategoryDao;
import com.kharche.dao.SpentDao;
import com.kharche.helpers.DatePickerHelper;
import com.kharche.interfaces.IDatePicker;
import com.kharche.interfaces.IToolbarHeadingTitle;
import com.kharche.model.Category;
import com.kharche.model.Spent;
import com.kharche.utils.AlertPop;
import com.kharche.utils.SpeechParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AddSpentFragment extends Fragment implements IDatePicker {
    private Spinner category_base;
    private ArrayAdapter<String> dynamic_category_list;
    private HashMap<String, Integer> category_list;
    private String selectedCategory;
    EditText spent_date;
    private IToolbarHeadingTitle iToolbarHeadingTitle;
    DatePickerHelper datePickerHelper;
    private ActivityResultLauncher<Intent> speechRecognitionLauncher;


    public AddSpentFragment(IToolbarHeadingTitle iToolbarHeadingTitle) {
        // Required empty public constructor
        this.iToolbarHeadingTitle = iToolbarHeadingTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_spent, container, false);
        iToolbarHeadingTitle.setToolBarTitle(R.string.add_spent);

        datePickerHelper = new DatePickerHelper(requireContext(), this);

        category_base = view.findViewById(R.id.price_category);
        dynamic_category_list = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        dynamic_category_list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_base.setAdapter(dynamic_category_list);

        EditText spent_amount = view.findViewById(R.id.spent_amount);
        EditText spent_on = view.findViewById(R.id.spent_on);
        Button submit_btn = view.findViewById(R.id.submit_btn);
        spent_date = view.findViewById(R.id.spent_date);

        // select category from options list
        category_base.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedValue = (String) parentView.getItemAtPosition(position);
                selectedCategory = "";
                String regex = "\\(\\d+\\)";
                if (!selectedValue.isEmpty()) {
                    selectedValue = selectedValue.replaceAll(regex, "");
                }
                selectedCategory = selectedValue;
                System.out.println("Selected category " + selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nothing selected if needed
            }
        });

        spent_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerHelper.showDatePicker(true, null);
            }
        });

        // handle microphone click
        ImageView microphone = view.findViewById(R.id.microphone);

        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleMicClick();
            }
        });

        // handle submit form
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertPop alertPop = new AlertPop(requireContext());
                try {
                    String price_input = spent_amount.getText().toString();
                    Integer price = 0;
                    if (!price_input.isEmpty()) {
                        try {
                            price = Integer.valueOf(price_input);
                        } catch (NumberFormatException e) {
                            System.out.println("Number casting error " + e.getMessage());
                        }
                    }

                    String spent_title = spent_on.getText().toString();
                    if (price == 0 || spent_title.isEmpty()) {
                        alertPop.showAlertDialog(getResources().getString(R.string.spent_amount_and_spent_on));
                    } else {
                        Spent spent = new Spent();
                        spent.setAmount(price);
                        spent.setDescription(spent_title);

                        String date = spent_date.getText().toString();
                        if (date != null && !date.isEmpty()) {
                            String[] dateSpl = date.split("-");
                            if (dateSpl.length == 3) {
                                spent.setCreatedAt(dateSpl[2] + "-" + dateSpl[1] + "-" + dateSpl[0] + " 00:00:00");
                            }
                        }

                        Category category = new Category();
                        if (!selectedCategory.isEmpty()) {
                            if (category_list.get(selectedCategory) != null) {
                                category.setId(category_list.get(selectedCategory));
                            }
                        }
                        spent.setCategoryId(category);
                        SpentDao spentDao = new SpentDao(requireContext());
//                        System.out.println("spent adding  " + spent.toString());
                        Long isAdded = spentDao.addSpent(spent);
                        if (isAdded > 0) {
                            spent_amount.setText("");
                            spent_on.setText("");
                            alertPop.showAlertDialog(getResources().getString(R.string.spent_amount_add_success));
                            moveToList();
                        } else {
                            alertPop.showAlertDialog(getResources().getString(R.string.unable_to_process));
                        }
                    }
                } catch (Exception ex) {
                    Log.d("TAG", "onClick 444 : " + ex.getMessage() + ex.getStackTrace() + ex.fillInStackTrace());
                }
            }
        });
        updateSpinnerData();
        speechRecognitionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::handleSpeechRecognitionResult);
        return view;
    }

    private void updateSpinnerData() {
        category_list = new HashMap<>();
        ArrayAdapter<String> existingCate = (ArrayAdapter<String>) category_base.getAdapter();

        for (int i = 0; i < existingCate.getCount(); i++) {
            dynamic_category_list.add(existingCate.getItem(i));
        }

        CategoryDao categoryDao = new CategoryDao(requireContext());
        List<Category> categories = categoryDao.getAllCategories(false);
        for (Category category : categories) {
            int categoryId = category.getId();
            String categoryName = category.getCategoryName();
            category_list.put(categoryName, categoryId);
            int associatedSpent = category.getAssociatedSpent();
            if (associatedSpent > 0) {
                categoryName += "(" + associatedSpent + ")";
            }
            dynamic_category_list.add(categoryName);
        }
        dynamic_category_list.notifyDataSetChanged();
    }

    public void getPickerDate(boolean isStartDate, int day, int month, int year) {
        month = month + 1;
        String monthName = String.valueOf(month);
        String dayName = String.valueOf(day);
        if (month < 10) {
            monthName = "0" + monthName;
        }
        if (day < 10) {
            dayName = "0" + dayName;
        }
        String setDate = dayName + "-" + monthName + "-" + year;
        spent_date.setText(setDate);
    }

    private void moveToList() {
//        try {
//            Thread.sleep(500);
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            SpentListFragment spentListFragment = new SpentListFragment();
//            transaction.replace(R.id.fragment_container, spentListFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        } catch (InterruptedException e) {
//            // Handle the exception if needed
//        }
    }

    private void handleMicClick() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            speechRecognitionLauncher.launch(intent);
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

    private void handleSpeechRecognitionResult(ActivityResult result) {
        if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
            ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String recognizedText = matches.get(0);
                if (recognizedText != null && !recognizedText.isEmpty()) {
                    SpeechParser speechParser = new SpeechParser(recognizedText);
                    Map<String, String> results = new HashMap<>();

                    results = speechParser.proccessUserVoiceText();
                }
            }
        }
    }

}