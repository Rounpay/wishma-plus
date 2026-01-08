package com.infotech.wishmaplus.Activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsDashboard extends AppCompatActivity {
    BarChart barChart;
    PieChart pieChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analytics_dashboard);
        AppCompatImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        barChart = findViewById(R.id.barChart);
        setupChart();
        pieChart = findViewById(R.id.pieChart);

        setupPieChart();

        LineChart lineChart = findViewById(R.id.lineChart);

        /* DATA POINTS (replicates the spikes in image) */
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        entries.add(new Entry(1, 0));
        entries.add(new Entry(2, 0));
        entries.add(new Entry(3, 6));   // Nov 25 spike
        entries.add(new Entry(4, 0));
        entries.add(new Entry(5, 0));
        entries.add(new Entry(6, 0));
        entries.add(new Entry(7, 0));
        entries.add(new Entry(8, 1));   // Dec 9 small spike
        entries.add(new Entry(9, 0));
        entries.add(new Entry(10, 5));  // Dec 10 spike
        entries.add(new Entry(11, 0));

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#1A73E8"));
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);

        /* NO FILL */
        dataSet.setDrawFilled(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        /* CHART SETTINGS */
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);

        /* X AXIS */
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return switch ((int) value) {
                    case 0 -> "Nov 18";
                    case 1 -> "Nov 21";
                    case 2 -> "Nov 24";
                    case 3 -> "Nov 27";
                    case 4 -> "Nov 30";
                    case 5 -> "Dec 3";
                    case 6 -> "Dec 6";
                    case 7 -> "Dec 9";
                    case 8 -> "Dec 12";
                    case 9 -> "Dec 15";
                    case 10 -> "Dec 18";
                    case 11 -> "Dec 21";
                    default -> "";
                };
            }
        });

        /* Y AXIS */
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#EEEEEE"));
        leftAxis.setTextColor(Color.GRAY);

        lineChart.getAxisRight().setEnabled(false);

        /* REFRESH */
        lineChart.invalidate();

    }
    private void setupChart() {

        // Followers data
        ArrayList<BarEntry> followers = new ArrayList<>();
        followers.add(new BarEntry(0, 40f)); // Text
        followers.add(new BarEntry(1, 20f));  // Photo

        // Non-followers data
        ArrayList<BarEntry> nonFollowers = new ArrayList<>();
        nonFollowers.add(new BarEntry(0, 40f)); // Text
        nonFollowers.add(new BarEntry(1, 30f)); // Photo

        BarDataSet set1 = new BarDataSet(followers, "Followers");
        set1.setColor(Color.parseColor("#1A73E8"));

        BarDataSet set2 = new BarDataSet(nonFollowers, "Non-followers");
        set2.setColor(Color.parseColor("#0B3C5D"));

        BarData data = new BarData(set1, set2);
        data.setBarWidth(0.20f);

        barChart.setData(data);

        // X Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return switch ((int) value) {
                    case 0 -> "Text";
                    case 1 -> "Photo";
                    default -> "";
                };
            }
        });
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(Arrays.asList("Text", "Photo")));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // Y Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(10f);

        barChart.getAxisRight().setEnabled(false);

        // Group bars
        barChart.groupBars(0f, 0.25f, 0.05f);

        // Disable interactions
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawGridBackground(false);

        barChart.invalidate();
    }
    private void setupPieChart() {

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(41f));
        entries.add(new PieEntry(59f));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#1E88E5"), // Followers
                Color.parseColor("#0D47A1")  // Non-followers
        );
        dataSet.setDrawValues(false);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(70f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);

        pieChart.invalidate(); // refresh
    }
}