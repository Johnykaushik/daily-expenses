package com.kharche;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.kharche.adapters.SpentAdapter;
import com.kharche.dao.CategoryDao;
import com.kharche.dao.SpentDao;
import com.kharche.helpers.DatePickerHelper;
import com.kharche.interfaces.IDatePicker;
import com.kharche.interfaces.IToolbarHeadingTitle;
import com.kharche.interfaces.OnItemClickListener;
import com.kharche.model.Category;
import com.kharche.model.Spent;
import com.kharche.utils.ConfirmPop;
import com.kharche.utils.DateFilterType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SpentListFragment extends Fragment implements IDatePicker, OnItemClickListener {
    private Spinner category_base;
    private ArrayAdapter<String> dynamic_category_list;
    private HashMap<String, Integer> category_list;
    private String selectedCategory;
    private HashMap<String, Integer> startDateVal = new HashMap<>();
    private HashMap<String, Integer> endDateVal = new HashMap<>();
    EditText start_date;
    EditText end_date;
    Calendar startDateUnix;
    Calendar endDateUnix;
    Map<String, Object> filterMap = new HashMap<>();
    private IToolbarHeadingTitle iToolbarHeadingTitle;
    private boolean sortByPrice;

    DatePickerHelper datePickerHelper;
    List<Spent> spentDynamicList;
    SpentAdapter spentAdapter;
    View pageView;
    TextView data_not_available;

    public SpentListFragment(IToolbarHeadingTitle iToolbarHeadingTitle) {
        // Required empty public constructor
        this.iToolbarHeadingTitle = iToolbarHeadingTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spent_list, container, false);
        pageView = view;
        iToolbarHeadingTitle.setToolBarTitle(R.string.spent_list);
        datePickerHelper = new DatePickerHelper(requireContext(), this);

        // hide no data available label initially

        data_not_available = view.findViewById(R.id.data_not_available);
        data_not_available.setVisibility(View.GONE);

        //  set default date that is current week
        setDefaultDate();
        // initial show all data from db
        RecyclerView spentListContainer = (RecyclerView) view.findViewById(R.id.spent_list_container);
        spentDynamicList = getAllSpent();
        spentAdapter = new SpentAdapter(spentDynamicList, this);
        spentListContainer.setAdapter(spentAdapter);
        spentListContainer.setLayoutManager(new LinearLayoutManager(requireContext()));

        startDateUnix = Calendar.getInstance();
        endDateUnix = Calendar.getInstance();

        Spinner spentDateType = view.findViewById(R.id.spent_date_type);
        LinearLayout dateContainer = view.findViewById(R.id.date_filter_container);
        dateContainer.setVisibility(View.GONE);

        //  hide all filter default
        RelativeLayout mainFilterContainer = view.findViewById(R.id.main_filter_container);


        // set dynamic category
        category_base = view.findViewById(R.id.search_category);
        dynamic_category_list = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        dynamic_category_list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_base.setAdapter(dynamic_category_list);

        Button submitButton = view.findViewById(R.id.submit_filter_btn);
        Button resetFilter = view.findViewById(R.id.reset_filter);
        ImageView searchIcon = view.findViewById(R.id.search_spent_icon);

        EditText searchInput = view.findViewById(R.id.search_spent_input);
        resetFilter.setVisibility(View.GONE); // hide reset button initially

        mainFilterContainer.setVisibility(View.GONE);
        spentDateType.setSelection(3); //set default selection LastWeek

        Switch sort_max_price_top = view.findViewById(R.id.sort_max_price_top);


        // toggle all filter with filter button
        ImageView search_spent_toggle = view.findViewById(R.id.search_spent_toggle);
        search_spent_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainFilterContainer.getVisibility() == View.VISIBLE) {
                    Animation slideOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out);
                    mainFilterContainer.startAnimation(slideOutAnimation);
                    mainFilterContainer.setVisibility(View.GONE);
                } else {
                    Animation slideInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in);
                    mainFilterContainer.startAnimation(slideInAnimation);
                    mainFilterContainer.setVisibility(View.VISIBLE);
                    resetFilter.setVisibility(View.VISIBLE);
                }
            }
        });
        // reset button click handle
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText("");
                category_base.setSelection(0);
                spentDateType.setSelection(3);

                // reset all values
                selectedCategory = "";
                setDefaultDate();
                sortByPrice = false;
                sort_max_price_top.setChecked(false);
                resetFilter.setVisibility(View.GONE);
                submitButton.performClick();
                submitButton.setPressed(true);
            }
        });

        // Date type dropdown
        spentDateType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String) parent.getItemAtPosition(position);
                DateFilterType selectedEnum = null;
                if (selectedValue != null) {
                    for (DateFilterType type : DateFilterType.values()) {
                        if (type.getValue().equalsIgnoreCase(selectedValue)) {
                            selectedEnum = type;
                            break;
                        }
                    }
                }
                if (position == 0 || selectedEnum == DateFilterType.SELECT_DATE_TYPE) {
                    dateContainer.setVisibility(View.GONE);
                } else {
                    if (selectedEnum == DateFilterType.CUSTOM_DATE) {
                        dateContainer.setVisibility(View.VISIBLE);
                    } else {
                        setStartEndTime(selectedEnum);
                        dateContainer.setVisibility(View.GONE);
                    }
                    resetFilter.setVisibility(View.VISIBLE); // show reset button
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("select date type BB ");
            }
        });

        // category selection
        category_base.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedValue = (String) parentView.getItemAtPosition(position);
                if (position != 0) {
                    selectedCategory = selectedValue;
                    resetFilter.setVisibility(View.VISIBLE); // show reset button
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nothing selected if needed
            }
        });

        // set calender for start and end
        start_date = view.findViewById(R.id.start_date);
        end_date = view.findViewById(R.id.end_date);

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerHelper.showDatePicker(true, startDateUnix);
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerHelper.showDatePicker(false, startDateUnix);
            }
        });

        //   price on top toggle button
        sort_max_price_top.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sortByPrice = isChecked;
            }
        });
        // handle submit filter
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMap.put("start_date", startDateUnix);
                filterMap.put("end_date", endDateUnix);
                Log.d("TAG", "onClick: " + startDateUnix.getTime() + " end " + endDateUnix.getTime());
                String searchInputData = searchInput.getText().toString();
                filterMap.put("search", searchInputData);
                filterMap.put("category_id", "");
                filterMap.put("sort_price", sortByPrice);

                if (selectedCategory != null) {
                    if (!selectedCategory.isEmpty()) {
                        if (category_list.get(selectedCategory) != null) {
                            filterMap.put("category_id", category_list.get(selectedCategory));
                        }
                    }
                }
                List<Spent> searchList = getAllSpent();
                spentAdapter.updateData(searchList);
            }
        });

        // simple search on keyup of input search
        updateSpinnerData();
        return view;
    }


    private void setDefaultDate() {
        setStartEndTime(DateFilterType.CURRENT_WEEK);
        filterMap.put("start_date", startDateUnix);
        filterMap.put("end_date", endDateUnix);
    }

    private List<Spent> getAllSpent() {
        List<Spent> setSpentList = new ArrayList<>();
        int totalAmount = 0;
        SpentDao spentDao = new SpentDao(requireContext());
        List<Spent> spentList = spentDao.getAllSpents(filterMap);
        for (Spent spent : spentList) {
            Spent setSpent = new Spent();
            totalAmount = totalAmount + spent.getAmount();

            setSpent.setAmount(spent.getAmount());
            setSpent.setDescription(spent.getDescription());
            setSpent.setCreatedAt(spent.getCreatedAt());
            setSpent.setId(spent.getId());
            if (spent.getCategoryId() != null) {
                setSpent.setCategoryId(spent.getCategoryId());
            }
            setSpentList.add(setSpent);
        }

        setPageTitle(totalAmount);
        int visibility = setSpentList.size() == 0 ? View.VISIBLE : View.GONE;

        data_not_available.setVisibility(visibility);

        return setSpentList;
    }

    private void setPageTitle(int totalAmount) {
        String title = getString(R.string.spent_list);

        if (totalAmount > 0) {
            title += " (₹" + totalAmount + ")";
        }
        iToolbarHeadingTitle.setToolBarTitle(title);
    }

    private void updateSpinnerData() {
        category_list = new HashMap<>();
        ArrayAdapter<String> existingCate = (ArrayAdapter<String>) category_base.getAdapter();

        for (int i = 0; i < existingCate.getCount(); i++) {
            dynamic_category_list.add(existingCate.getItem(i));
        }

        String existingVal = getResources().getString(R.string.price_category_title);
        dynamic_category_list.add(existingVal);

        CategoryDao categoryDao = new CategoryDao(requireContext());
        List<Category> categories = categoryDao.getAllCategories(false);

        for (Category category : categories) {
            int categoryId = category.getId();
            String categoryName = category.getCategoryName();
            category_list.put(categoryName, categoryId);
            dynamic_category_list.add(categoryName);
        }
        dynamic_category_list.notifyDataSetChanged();
    }

    // show start/end date in view
    public void getPickerDate(boolean isStartDate, int day, int month, int year) {
        Log.d("Date picker", "getPickerDate:  2" + isStartDate + " year " + year + " month " + month + " day " + day);
        try {
            int _month = month;
            _month = _month + 1;
            String monthName = String.valueOf(_month);
            String dayName = String.valueOf(day);
            if (_month < 10) {
                monthName = "0" + monthName;
            }
            if (day < 10) {
                dayName = "0" + dayName;
            }
            String dateText = dayName + "-" + monthName + "-" + year;

            if (isStartDate) {
                start_date.setText(dateText);

                startDateUnix.set(Calendar.MONTH, month);
                startDateUnix.set(Calendar.YEAR, year);
                startDateUnix.set(Calendar.DAY_OF_MONTH, day);
            } else {
                end_date.setText(dateText);

                endDateUnix.set(Calendar.MONTH, month);
                endDateUnix.set(Calendar.YEAR, year);
                endDateUnix.set(Calendar.DAY_OF_MONTH, day);
            }
        } catch (Exception ex) {
            System.out.println("error found " + ex.getMessage());
        }
    }

    public void setStartEndTime(DateFilterType selectedEnum) {
        Utils utils = new Utils();
        switch (selectedEnum) {
            case TODAY:
                startDateUnix = utils.getStartToday();
                endDateUnix = utils.getEndToday();
                break;
            case YESTERDAY:
                startDateUnix = utils.getYesterdayStart();
                endDateUnix = utils.getYesterdayEnd();
                break;
            case CURRENT_WEEK:
                startDateUnix = utils.getCurrentWeekStart();
                endDateUnix = utils.getEndToday();
                break;
            case LAST_MONTH:
                startDateUnix = utils.getLastMonthStart();
                endDateUnix = utils.getLastMonthEnd();
                break;
            case CURRENT_MONTH:
                startDateUnix = utils.getCurrentMonthStart();
                endDateUnix = utils.getEndToday();
                break;
            default:
                startDateUnix = utils.getLastWeekStart();
                endDateUnix = utils.getLastWeekEnd();
                break;
        }
    }

    //  delete spent
    @Override
    public void onItemClick(int itemClicked) {
        if (spentDynamicList.get(itemClicked) != null) {
            ConfirmPop confirmPop = new ConfirmPop(requireContext());
            String label = getResources().getString(R.string.spent_amount_delete_ask);
            confirmPop.showAlertDialog(label, new ConfirmPop.OnConfirmListener() {
                @Override
                public void onYesClicked() {
                    SpentDao spentDao = new SpentDao(requireContext());
                    int id = spentDynamicList.get(itemClicked).getId();
                    int isDeleted = spentDao.deleteOne(id);

                    Log.d("TAG", "onYesClicked: " + isDeleted);
                    if (isDeleted > 0) {
                        Snackbar.make(pageView, R.string.spent_amount_deleted_success, Snackbar.LENGTH_LONG).show();
                        int itemAmount = spentDynamicList.get(itemClicked).getAmount();
                        spentDynamicList.remove(itemClicked);
                        spentAdapter.notifyDataSetChanged();

                        CharSequence getTitle = iToolbarHeadingTitle.getToolBarTitle();
                        int existingAmount = parseTitle(getTitle);

                        int finalAmount = existingAmount - itemAmount;
                        Log.d("TAG", "onYesClicked: " + getTitle);
                        setPageTitle(finalAmount);
                    } else {
                        Snackbar.make(pageView, R.string.unable_to_process, Snackbar.LENGTH_LONG).show();
                    }
                }
            });

        }
        Log.d("TAG", "onItemClick:  spents " + itemClicked);
    }

    private int parseTitle(CharSequence input) {
        Pattern pattern = Pattern.compile("\\d+"); // Match one or more digits
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String number = matcher.group();
            if (number != null) {
                return Integer.valueOf(number);
            }
        }
        return 0;
    }
}

