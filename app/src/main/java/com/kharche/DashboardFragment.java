package com.kharche;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.kharche.dao.SpentDao;
import com.kharche.interfaces.IToolbarHeadingTitle;
import com.kharche.model.Spent;
import com.kharche.utils.DateFilterType;
import com.kharche.utils.IDateMonthYearType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class DashboardFragment extends Fragment {
    private PieChart pieChart;
    private SpentDao spentDao;
    private List<Spent> spentList;
    Calendar startDateUnix;
    Calendar endDateUnix;

    private Map<String, Object> whereFilter;
    private IToolbarHeadingTitle iToolbarHeadingTitle;

    private LineChart lineChart;

    public DashboardFragment(IToolbarHeadingTitle iToolbarHeadingTitle) {
        // Required empty public constructor
        this.iToolbarHeadingTitle = iToolbarHeadingTitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        iToolbarHeadingTitle.setToolBarTitle(R.string.dashboard);
//
        whereFilter = new HashMap<>();
        spentDao = new SpentDao(requireContext());
//
        startDateUnix = Calendar.getInstance();
        endDateUnix = Calendar.getInstance();
//        pieChart = view.findViewById(R.id.pieChart);

        lineChart = view.findViewById(R.id.getTheGraph);
//        Spinner spentDateType = view.findViewById(R.id.spent_date_type);
//
//        // Date type dropdown
//        spentDateType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedValue = (String) parent.getItemAtPosition(position);
//                DateFilterType selectedEnum = null;
//                if (selectedValue != null) {
//                    for (DateFilterType type : DateFilterType.values()) {
//                        if (type.getValue().equalsIgnoreCase(selectedValue)) {
//                            selectedEnum = type;
//                            break;
//                        }
//                    }
//                }
//                setStartEndTime(selectedEnum);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                System.out.println("select date type BB ");
//            }
//        });

//        Button submitButton = view.findViewById(R.id.dashboard_submit);

        // handle submit filter
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<Spent> spentList = spentDao.spentCategoryDashboard(startDateUnix,endDateUnix);
//                generateChart(spentList);
//            }
//        });

        Spinner date_type_filter = view.findViewById(R.id.date_type_filter);

        date_type_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String) parent.getItemAtPosition(position);
                getSelectedItem(selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    public IDateMonthYearType parseDateFilter(String val) {
        try {
            val = val.toUpperCase();
            IDateMonthYearType type = IDateMonthYearType.valueOf(val);
            Log.d("TAG", "parseDateFilter: " + type + " val " + val);
            return type;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void getSelectedItem(String val) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        IDateMonthYearType type = parseDateFilter(val);
        Log.d("TAG", "getSelectedItem: " + type + " val " + val + "  >> " + IDateMonthYearType.DAY);
        spentDao.getWeekMonthData(IDateMonthYearType.DAY);
        if (type != null) {
            switch (type) {
                case DAY:
                    Log.d("TAG", "Selected is : day");
                    dataList = spentDao.getWeekMonthData(IDateMonthYearType.DAY);
                    break;
                case WEEK:
                    Log.d("TAG", "Selected is : week");

                    dataList = spentDao.getWeekMonthData(IDateMonthYearType.WEEK);
                    break;
                case MONTH:
                    Log.d("TAG", "Selected is : month");

                    dataList = spentDao.getWeekMonthData(IDateMonthYearType.MONTH);
                    break;
                case YEAR:
                    Log.d("TAG", "Selected is : year");

                    dataList = spentDao.getWeekMonthData(IDateMonthYearType.YEAR);
                    break;
            }
        }

        Log.d("TTT", "getSelectedItem: sized " + dataList.size());

        generateLineChart(dataList, type);
    }
//
    public void generateLineChart(List<Map<String, Object>> dataList, IDateMonthYearType type) {

        ArrayList<Entry> entries = new ArrayList<>();
        List<String> labelList = new ArrayList<>();
    try {
        String[] monthName = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        String[] weekName = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int it = 0;
        for (Map<String, Object> data : dataList) {
            Log.d("TAG", "generateLineChart: " + data.toString());
            float value = Float.valueOf(data.get("amount").toString());
            int day = (int)data.get("day");
            int week = Integer.valueOf(data.get("week").toString());
            int month = Integer.valueOf(data.get("month").toString());
            int year = (int) data.get("year");
            if (year > 0) {

                Log.d("TAG", "generateLineChart: " + value + " day: " + day + " week: " + week + " month: " + month + " year: " + year);
                entries.add(new Entry(it, value));
                String label = "";
                if (type == IDateMonthYearType.DAY) {
                    String day_s = day < 10 ? ("0" + day) : "" + day;
                    String month_s = month < 10 ? ("0" + month) : "" + month;
                    label = day_s + "-" + month_s + "-" + year;
                } else if (type == IDateMonthYearType.WEEK) {
                    label = (week == 0 ? "1" : week) + " " + type + " " + year;
                } else if (type == IDateMonthYearType.MONTH) {
                    label = monthName[month - 1] + "-" + year + "(" + ((int) value) + ")";
                } else if (type == IDateMonthYearType.YEAR) {
                    label = year + "(" + ((int) value) + ")";
                }
                labelList.add(label);
                it++;

            }
        }

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelList));


        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45);


        Log.d("TAG", "generateLineChart: " + entries.size());
        LineDataSet dataSet = new LineDataSet(entries, "Price");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getDescription().setEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.invalidate();

    }catch(Exception ex){
        Log.d("TAG", "generateLineChart: EE  " + ex.getMessage());
    }

    }

//    public void generatePieChart(List<Spent> spentList) {
//        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
//        int[] colors = new int[]{
//                Color.RED,
//                Color.GREEN,
//                Color.BLUE,
//                Color.CYAN,
//                Color.DKGRAY,
//                Color.GRAY,
//                Color.MAGENTA,
//                Color.YELLOW
//        };
//        HashMap<String, Integer> categoryColors = new HashMap<>();
//
//        int colorIndex = 0;
//        List<LegendEntry> legendEntries = new ArrayList<>();
//
//        for (Spent spent : spentList) {
//            String categoryName = "Uncategorised";
//            if (spent.getCategoryId() != null) {
//                if(spent.getCategoryId().getCategoryName() != null){
//                    categoryName = spent.getCategoryId().getCategoryName();
//                }
//            }
//            Random random = new Random();
//            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
//
////            int categoryColor = colors[colorIndex % colors.length];
//            categoryColors.put(categoryName, color);
//            Log.d("TAG", "generateChart:  " + spent.amount + " set val " + Float.valueOf(spent.amount));
//            entries.add(new PieEntry(spent.amount, categoryName));
//            legendEntries.add(new LegendEntry(categoryName, Legend.LegendForm.DEFAULT, 8f, 8f, null, color));
//            colorIndex++;
//        }
//
//        PieDataSet dataSet = new PieDataSet(entries, "Categories");
//        dataSet.setColors(new ArrayList<>(categoryColors.values()));
//
//        PieData data = new PieData(dataSet);
//        data.setValueTextSize(15f);
//
//        Legend legend = pieChart.getLegend();
//        legend.setCustom(legendEntries);
//        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // Adjust the vertical alignment as needed
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT); // Adjust the horizontal alignment as needed
//        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
//        pieChart.getDescription().setText("");
//
//        pieChart.setData(data);
//        pieChart.setUsePercentValues(false);
//        pieChart.setTransparentCircleColor(Color.WHITE);
//        pieChart.setHoleRadius(58f);
//        pieChart.animateY(1400, Easing.EaseInOutQuad);
//    }

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
}