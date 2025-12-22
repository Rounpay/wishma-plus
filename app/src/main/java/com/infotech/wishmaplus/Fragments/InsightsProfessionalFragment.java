package com.infotech.wishmaplus.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class InsightsProfessionalFragment extends Fragment {
    AppCompatTextView dropdownText, tvPercentageChange, tvTotalView;
    AppCompatImageView ivArrow, ivInfo;
    AppCompatTextView tvSelectedRange, tvCurrentValue, tvPreviousValue,previousDaysEarning;
    ProgressBar progressCurrent, progressPrevious;
    LinearLayout layoutPrevious, layoutCurrent;
    LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insights_professional, container, false);
        dropdownText = view.findViewById(R.id.dropdownText);
        tvPercentageChange = view.findViewById(R.id.tvPercentageChange);
        ivArrow = view.findViewById(R.id.ivArrow);
        ivInfo = view.findViewById(R.id.ivInfo);
        tvTotalView = view.findViewById(R.id.tvTotalView);

        tvSelectedRange = view.findViewById(R.id.tvSelectedRange);
        tvCurrentValue = view.findViewById(R.id.tvCurrentValue);
        tvPreviousValue = view.findViewById(R.id.tvPreviousValue);

        progressCurrent = view.findViewById(R.id.progressCurrent);
        progressPrevious = view.findViewById(R.id.progressPrevious);
        previousDaysEarning = view.findViewById(R.id.previousDaysEarning);

        layoutPrevious = view.findViewById(R.id.layoutPrevious);
        layoutCurrent = view.findViewById(R.id.layoutCurrent);
        lineChart = view.findViewById(R.id.lineChart);

        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setGranularity(10f);
        lineChart.getAxisRight().setEnabled(false);

        ivInfo.setOnClickListener(v -> UtilMethods.INSTANCE.InsightsBottomSheetDialog(requireActivity()));
        updatePercentage(28);
        dropdownText.setOnClickListener(v -> UtilMethods.INSTANCE.selectDateRangeBottomSheet(requireActivity(), dropdownText, this::updatePercentage,false));
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void updatePercentage(int days) {
        int currentViews = 0;
        int prevViews = 0;
        int greenColor = ContextCompat.getColor(requireContext(), R.color.color_green);
        int views = 0;
        int earning = 0;  // NEW
        String percentageText = "";
        int color = Color.BLACK; // default black for number

        switch (days) {
            case 7:
                currentViews = 0;
                prevViews = 0;
                views = 0;
                earning = 0;
                percentageText = "100% from previous 7 days";
                color = Color.RED;
                break;
            case 14:
                currentViews = 10;
                prevViews = 5;
                views = 0;
                earning = 100;
                percentageText = "-100% from previous 14 days";
                color = Color.RED;
                break;
            case 28:
                currentViews = 12;
                prevViews = 0;
                views = 12;
                earning = 20;   // NEW
                percentageText = "500% from previous 28 days";
                color = greenColor;
                break;
            case 90:
                currentViews = 5;
                prevViews = 2;
                views = 5;
                earning = 150;   // NEW
                percentageText = "50% from previous 90 days";
                color = greenColor;
                break;
            case 1:
                currentViews = 0;
                prevViews = 0;
                views = 0;
                earning = 0;   // NEW
                percentageText = "10% from previous day";
                color = greenColor;
                break;
        }

        // Update Views TextView

        tvTotalView.setText("Total Views: " + views);

        // Update percentage with colored number only
        int percentEndIndex = percentageText.indexOf("%") + 1; // end of number
        SpannableString spannable = new SpannableString(percentageText);
        spannable.setSpan(new ForegroundColorSpan(color), 0, percentEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPercentageChange.setText(spannable);

        // Set arrow color
        if (color == greenColor) {
            ivArrow.setImageResource(R.drawable.arrow_up);
            ivArrow.setColorFilter(greenColor);
        } else {
            ivArrow.setImageResource(R.drawable.arrow_downward);
            ivArrow.setColorFilter(Color.RED);
        }
        tvCurrentValue.setText(String.valueOf(currentViews));
        progressCurrent.setProgress(Math.min(currentViews, 100));
        tvPreviousValue.setText(String.valueOf(prevViews));
        layoutPrevious.setVisibility(View.VISIBLE);
        progressPrevious.setProgress(Math.min(prevViews, 100));
        if (earning == 0) {
            previousDaysEarning.setText("--- from previous " + days + " days");
        } else {
            previousDaysEarning.setText("$" + earning + " from previous " + days + " days");
        }
        updateChart(days,earning);
    }
    public void updateChart(int daysSelected, int earning) {

        // ----- Generate Fake Earning Data (replace with API data later) -----
        List<Entry> entries = new ArrayList<>();
        List<String> bottomDates = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        for (int i = daysSelected - 1; i >= 0; i--) {

            calendar.add(Calendar.DAY_OF_YEAR, -1);

            entries.add(new Entry(daysSelected - i, earning));

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
            bottomDates.add(sdf.format(calendar.getTime()));
        }

        // ----- Line DataSet -----
        LineDataSet dataSet = new LineDataSet(entries, "Earnings");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawFilled(true);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(bottomDates));

        lineChart.invalidate(); // refresh chart
    }

}