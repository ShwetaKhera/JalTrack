package com.jaltrack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.database.Log;
import com.database.WaterLogDatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.helper.BarChartImageRenderer;
import com.project.shweta.jaltrack.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.helper.Constants.MONTHLY;
import static com.helper.Constants.MONTH_LIST;
import static com.helper.Constants.WEEKLY;
import static com.helper.Constants.YEARLY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;


public class HistoryFragment extends Fragment implements View.OnClickListener {

    private BarChart barChart;
    private ImageView imagePrev, imageNext;
    private TextView chartDataIntervalView;
    private Button btnWeekly, btnMonthly, btnYearly;
    private TextView weeklyAverageView, monthlyAverageView,
            averageCompletionView, drinkFrequencyView;
    private AdView adViewBanner;

    private String chartDataInterval = MONTHLY;

    private Bitmap[] crownBitmap;
    private SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
    private WaterLogDatabaseHelper db;
    private List<Log> allDataList;

    private int genWeek, genMonth, genYear, genDateOfMonth;
    private Calendar generalCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        db = new WaterLogDatabaseHelper(getActivity());
        allDataList = db.getAllLogs();

        genWeek = generalCalendar.get(Calendar.WEEK_OF_YEAR);
        genMonth = generalCalendar.get(Calendar.MONTH) + 1;
        genYear = generalCalendar.get(Calendar.YEAR);
        genDateOfMonth = generalCalendar.get(Calendar.DATE);


        chartDataIntervalView = view.findViewById(R.id.chart_data_interval);
        weeklyAverageView = view.findViewById(R.id.weekly_average);
        monthlyAverageView = view.findViewById(R.id.monthly_average);
        averageCompletionView = view.findViewById(R.id.average_completion);
        drinkFrequencyView = view.findViewById(R.id.drink_frequency);
        imagePrev = view.findViewById(R.id.image_prev);
        imagePrev.setOnClickListener(this);
        imageNext = view.findViewById(R.id.image_next);
        imageNext.setOnClickListener(this);
        btnWeekly = view.findViewById(R.id.button_weekly);
        btnWeekly.setOnClickListener(this);
        btnMonthly = view.findViewById(R.id.button_monthly);
        btnMonthly.setOnClickListener(this);
        btnYearly = view.findViewById(R.id.button_yearly);
        btnYearly.setOnClickListener(this);

        barChart = view.findViewById(R.id.barchart);
        activateBtnMonthly();

        calculateWeeklyAverage();
        calculateMonthlyAverage();
        calculateAverageCompletion();
        calculateDrinkFrequency();

        adViewBanner = view.findViewById(R.id.adView_banner);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == btnWeekly) {
            activateBtnWeekly();
        }
        if (view == btnMonthly) {
            activateBtnMonthly();
        }
        if (view == btnYearly) {
            activateBtnYearly();
        }
        if (view == imagePrev) {
            showPrevious();
        }
        if (view == imageNext) {
            showNext();
        }
    }

    private void setBarChartData() {
        List<Log> dataList;
        if (chartDataInterval.matches(MONTHLY)) {
            dataList = fetchMonthlyData();
        } else if (chartDataInterval.matches(YEARLY)) {
            dataList = fetchYearlyData();
        } else {
            dataList = fetchWeeklyData();
        }
        changeChartStyle(barChart);

        BarData data = new BarData(changeDataSetStyle(dataList));
        data.setBarWidth(0.65f);
        barChart.setRenderer(
                new BarChartImageRenderer(
                        barChart, barChart.getAnimator(), barChart.getViewPortHandler(), crownBitmap
                ));
        barChart.setData(data);
    }

    private List<Log> fetchWeeklyData() {
        ArrayList amountPerWeek = new ArrayList<>();

        Calendar localCal = Calendar.getInstance();
        localCal.setTimeInMillis(generalCalendar.getTimeInMillis());
        localCal.set(Calendar.WEEK_OF_YEAR, genWeek);
        localCal.set(Calendar.YEAR, genYear);
        localCal.set(Calendar.MONTH, genMonth - 1);
        localCal.set(Calendar.DATE, genDateOfMonth);

        int dayOfWeek = localCal.get(Calendar.DAY_OF_WEEK) - 1;
        localCal.add(Calendar.DATE, 6 - dayOfWeek);
        Date saturday = new Date(localCal.getTime().getTime());
        localCal.add(Calendar.DATE, -6);
        Date sunday = new Date(localCal.getTime().getTime());

        List<Log> logList = db.getWeeklyLogs(sunday, saturday);
//        if (saturday.getYear() != sunday.getYear()) {
//            int size = getNumberOfDaysInMonth(12,sunday.getYear()) +
//                    getNumberOfDaysInMonth(1,saturday.getYear());
//            crownBitmap = new Bitmap[size];
//        } else {
        crownBitmap = new Bitmap[getNumberOfDaysInMonth()];
//        }
        String monthName = MONTH_LIST[genMonth];
        chartDataIntervalView.setText(sunday.getDate() + " - " + saturday.getDate() + " , " + monthName + " " + genYear);

//        for (int i = 0; i <= 6; i++) {
//            if (i <= genDateOfMonth) {
//                int percent = getAmountForDate(logList, localCal.get(Calendar.DATE), true);
//                amountPerWeek.add(new BarEntry(localCal.get(Calendar.DATE), percent));
//
//                if (percent >= 80) {
////                    if (saturday.getYear() != sunday.getYear())
//                        crownBitmap[localCal.get(Calendar.DATE) - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.crown);
//                }
//            } else {
//                amountPerWeek.add(new BarEntry(localCal.get(Calendar.DATE), 0f));
//            }
//            localCal.add(Calendar.DATE, 1);
//        }
        BarEntry barEntry;
        for (int i = 0; i <= 6; i++) {
            int percent = getAmountForDate(logList, localCal.get(Calendar.DATE), true);

//            if (i <= genDateOfMonth) {
            barEntry = new BarEntry(localCal.get(Calendar.DATE), percent);
//            }
            amountPerWeek.add(new BarEntry(localCal.get(Calendar.DATE), percent));

//                if (percent >= 80) {
////                    if (saturday.getYear() != sunday.getYear())
//                    crownBitmap[localCal.get(Calendar.DATE) - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.crown);
//                }
//            } else {
//                amountPerWeek.add(new BarEntry(localCal.get(Calendar.DATE), 0f));
//            }
            localCal.add(Calendar.DATE, 1);
        }
//        inaa
        return amountPerWeek;
    }

    private List<Log> fetchMonthlyData() {
        ArrayList amountPerDay = new ArrayList<>();

        String monthAndYear = genMonth < 10 ? (genYear + "-0" + genMonth) : (genYear + "-" + genMonth);
        chartDataIntervalView.setText(MONTH_LIST[genMonth] + " " + genYear);

        List<Log> logList = db.getMonthlyLogs(monthAndYear);
        crownBitmap = new Bitmap[getNumberOfDaysInMonth()];

        for (int i = 1; i <= getNumberOfDaysInMonth(); i++) {
            if (i <= genDateOfMonth) {
                int percent = getAmountForDate(logList, i, true);
                amountPerDay.add(new BarEntry(i, percent));

                if (percent >= 80) {
                    crownBitmap[i - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.crown);
                }
            } else {
                amountPerDay.add(new BarEntry(i, 0f));
            }
        }
        return amountPerDay;
    }

    private List<Log> fetchYearlyData() {
        ArrayList amountPerMonth = new ArrayList<>();
        List<Log> logList = db.getYearlyLogs(genYear);
        crownBitmap = new Bitmap[13];

        chartDataIntervalView.setText(genYear + "");

        for (int i = 0; i <= 12; i++) {
            int percent;

            if (i == 0) percent = 0;
            else percent = getAmountForMonth(logList, i);

            amountPerMonth.add(new BarEntry(i, percent));
            if (percent >= 80) {
                crownBitmap[i] = BitmapFactory.decodeResource(getResources(), R.drawable.crown);
            }
        }
        return amountPerMonth;
    }

    private BarDataSet changeDataSetStyle(List amountPerDay) {
        BarDataSet dataSet = new BarDataSet(amountPerDay, "");
        dataSet.setColors(getResources().getColor(R.color.colorPrimaryDark));
        dataSet.setFormLineWidth(0);
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawIcons(true);
        dataSet.setValueTextColor(getResources().getColor(R.color.transparent));
        return dataSet;
    }

    private void changeChartStyle(BarChart chart) {

        chart.animateY(500);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(true);
        chart.setMaxVisibleValueCount(100);
        chart.getAxisLeft().setDrawTopYLabelEntry(false);

        final XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        if (chartDataInterval.matches(YEARLY)) {
            xAxisYearlyFormat(xAxis);
        } else if (chartDataInterval.matches(MONTHLY)) {
            xAxis.setLabelCount(6);
            xAxis.setValueFormatter(new DefaultAxisValueFormatter(6));
        } else {
            xAxis.setLabelCount(8);
            xAxis.setValueFormatter(new DefaultAxisValueFormatter(8));
        }

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(7, true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(120f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = chart.getLegend();
        l.setEnabled(false);
    }

    private void xAxisYearlyFormat(XAxis xAxis) {
        xAxis.setLabelCount(13);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) Math.ceil(value);
                if (index <= 12) {
                    return MONTH_LIST[index];
                } else {
                    return "";
                }
            }
        });
    }

    private int getAmountForDate(List<Log> logList, int i, boolean returnPercent) {
        int sumOfMl = 0;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (Log log : logList) {
            String timestamp = log.getTimestamp();
            String logDate = timestamp.substring(0, timestamp.indexOf(" "));
            Calendar localCal = Calendar.getInstance();
            localCal.setTimeInMillis(generalCalendar.getTimeInMillis());
            localCal.set(Calendar.DATE, i);
            String searchDate = fmt.format(localCal.getTime());
            if (logDate.equals(searchDate)) {
                sumOfMl += log.getAmount();
            }
        }
        if (returnPercent) {
            int calcPercent = (sumOfMl * 100) / 1400;
            if (calcPercent >= 100) {
                return 100;
            } else {
                return calcPercent;
            }
        } else {
            return sumOfMl;
        }
    }

    private int getAmountForMonth(List<Log> logList, int month) {
        int sumOfMl = 0;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Log log : logList) {
            try {
                String timestamp = log.getTimestamp();
                Date d = fmt.parse(timestamp);
                if (month == d.getMonth() + 1) {
                    System.out.println("Timestamp:" + timestamp + " " + d.getMonth());
                    sumOfMl += log.getAmount();
                }
            } catch (ParseException p) {
                p.printStackTrace();
            }
        }

        int calcPercent = (sumOfMl * 100) / (1400 * getNumberOfDaysInMonth(month, genYear));
        if (calcPercent >= 100) {
            return 100;
        } else {
            return calcPercent;
        }
    }

    private int getNumberOfDaysInMonth(int month, int year) {
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else if (month == 2) {
            if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 31;
        }
    }

    private int getNumberOfDaysInMonth() {
        if (genMonth == 4 || genMonth == 6 || genMonth == 9 || genMonth == 11) {
            return 30;
        } else if (genMonth == 2) {
            if (((genYear % 4 == 0) && (genYear % 100 != 0)) || (genYear % 400 == 0)) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 31;
        }
    }

    private void activateBtnWeekly() {
        btnWeekly.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        btnMonthly.setTextColor(getResources().getColor(R.color.colorGrey));
        btnYearly.setTextColor(getResources().getColor(R.color.colorGrey));
        chartDataInterval = WEEKLY;
        setBarChartData();
    }

    private void activateBtnMonthly() {
        btnWeekly.setTextColor(getResources().getColor(R.color.colorGrey));
        btnMonthly.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        btnYearly.setTextColor(getResources().getColor(R.color.colorGrey));
        chartDataInterval = MONTHLY;
        setBarChartData();
    }

    private void activateBtnYearly() {
        btnWeekly.setTextColor(getResources().getColor(R.color.colorGrey));
        btnMonthly.setTextColor(getResources().getColor(R.color.colorGrey));
        btnYearly.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        chartDataInterval = YEARLY;
        setBarChartData();
    }

    private void showPrevious() {
        if (chartDataInterval.matches(WEEKLY)) {
            generalCalendar.add(Calendar.DATE, -7);
        } else if (chartDataInterval.matches(MONTHLY)) {
            generalCalendar.add(Calendar.MONTH, -1);
        } else if (chartDataInterval.matches(YEARLY)) {
            generalCalendar.add(Calendar.YEAR, -1);
        }
        reset();
        setBarChartData();
    }

    private void reset() {
        genYear = generalCalendar.get(Calendar.YEAR);
        genMonth = generalCalendar.get(Calendar.MONTH) + 1;
        genDateOfMonth = generalCalendar.get(Calendar.DATE);
        genWeek = generalCalendar.get(Calendar.WEEK_OF_YEAR);
    }

    private void showNext() {
        if (chartDataInterval.matches(WEEKLY)) {
            generalCalendar.add(Calendar.DATE, 7);
        } else if (chartDataInterval.matches(MONTHLY)) {
            generalCalendar.add(Calendar.MONTH, 1);
        } else if (chartDataInterval.matches(YEARLY)) {
            generalCalendar.add(Calendar.YEAR, 1);
        }
        reset();
        setBarChartData();
    }


    private void calculateWeeklyAverage() {
        int sumOfAmount = 0;
        for (Log log : allDataList) {
            sumOfAmount = sumOfAmount + log.getAmount();
        }
        if (allDataList.size() == 0) {
            weeklyAverageView.setText("0 ml / day");
            return;
        }
        try {
            Calendar localCal = Calendar.getInstance();
            int currentWeek = localCal.get(Calendar.WEEK_OF_YEAR);

            Date firstDate = fmtOut.parse(allDataList.get(allDataList.size() - 1).getTimestamp());
            localCal.setTime(firstDate);
            int startingWeek = localCal.get(Calendar.WEEK_OF_YEAR);

            int differenceInWeeks;
            if (localCal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                differenceInWeeks = currentWeek - startingWeek + 1;
            } else {
                differenceInWeeks = (52 - startingWeek) + currentWeek
                        + (51 * (Calendar.getInstance().get(Calendar.YEAR) - localCal.get(Calendar.YEAR) - 1));
            }

            int weeklyAverage = sumOfAmount / differenceInWeeks;
            weeklyAverageView.setText(weeklyAverage + " ml / day");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateMonthlyAverage() {
        int sumOfAmount = 0;
        for (Log log : allDataList) {
            sumOfAmount = sumOfAmount + log.getAmount();
        }
        if (allDataList.size() == 0) {
            monthlyAverageView.setText("0 ml / day");
            return;
        }
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Date firstDate = fmtOut.parse(allDataList.get(allDataList.size() - 1).getTimestamp());

                long totalNumberOfMonths = getNumberOfMonths(firstDate);

                if (totalNumberOfMonths > 0) {
                    long monthlyAverage = sumOfAmount / totalNumberOfMonths;
                    monthlyAverageView.setText(monthlyAverage + " ml / day");
                } else {
                    long monthlyAverage;
                    if (getNumberOfDays(firstDate) > 0) {
                        monthlyAverage = sumOfAmount / getNumberOfDays(firstDate);
                    } else {
                        monthlyAverage = sumOfAmount;
                    }
                    monthlyAverageView.setText(monthlyAverage + " ml / day");
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void calculateAverageCompletion() {
        if (allDataList.size() == 0) {
            averageCompletionView.setText("0 %");
            return;
        }
        try {
            Calendar localCal = Calendar.getInstance();
            int numberOfDaysCompleted = 0;

            localCal.add(Calendar.DATE, 1);
            String d = fmtOut.format(localCal.getTime());

            Date firstDate = fmtOut.parse(allDataList.get(allDataList.size() - 1).getTimestamp());
            localCal.setTime(firstDate);
            while (!d.equals(fmtOut.format(localCal.getTime()))) {
                int amountPercent = getAmountForDate(allDataList, localCal.get(Calendar.DATE), true);
                if (amountPercent >= 80) {
                    numberOfDaysCompleted++;
                }
                localCal.add(Calendar.DATE, 1);
            }
            int numOfDays = getNumberOfDays(firstDate) + 1;
            int daysPercent = (numberOfDaysCompleted * 100) / numOfDays;
            averageCompletionView.setText(daysPercent + " %");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void calculateDrinkFrequency() {
        if (allDataList.size() == 0) {
            drinkFrequencyView.setText("0 times / day");
            return;
        }
        try {
            Calendar localCal = Calendar.getInstance();
            int sumOfGlasses = 0;

            localCal.add(Calendar.DATE, 1);
            String d = fmtOut.format(localCal.getTime());

            Date firstDate = fmtOut.parse(allDataList.get(allDataList.size() - 1).getTimestamp());
            localCal.setTime(firstDate);
            while (!d.equals(fmtOut.format(localCal.getTime()))) {
                int amount = getAmountForDate(
                        allDataList, localCal.get(Calendar.DATE), false);
                int glasses = getNumberOfGlasses(amount);
                sumOfGlasses = sumOfGlasses + glasses;

                localCal.add(Calendar.DATE, 1);
            }
            int numOfDays = getNumberOfDays(firstDate) + 1;
            int averageGlasses = sumOfGlasses / numOfDays;
            drinkFrequencyView.setText(averageGlasses + " times / day");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private int getNumberOfGlasses(int amount) {
        return amount / 175;
    }

    private int getNumberOfDays(Date firstDate) {
        Calendar localCal = Calendar.getInstance();
        localCal.setTime(firstDate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                return (int) DAYS.between(
                        LocalDate.of(
                                localCal.get(Calendar.YEAR),
                                localCal.get(Calendar.MONTH),
                                localCal.get(Calendar.DATE)),
                        LocalDate.of(
                                Calendar.getInstance().get(Calendar.YEAR),
                                Calendar.getInstance().get(Calendar.MONTH),
                                Calendar.getInstance().get(Calendar.DATE)));
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    private int getNumberOfMonths(Date firstDate) {
        Calendar localCal = Calendar.getInstance();
        localCal.setTime(firstDate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                return (int) MONTHS.between(
                        LocalDate.of(
                                localCal.get(Calendar.YEAR),
                                localCal.get(Calendar.MONTH),
                                localCal.get(Calendar.DATE)),
                        LocalDate.of(
                                Calendar.getInstance().get(Calendar.YEAR),
                                Calendar.getInstance().get(Calendar.MONTH),
                                Calendar.getInstance().get(Calendar.DATE)));
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

}
