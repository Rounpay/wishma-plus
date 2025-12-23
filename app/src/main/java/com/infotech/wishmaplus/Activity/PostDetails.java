package com.infotech.wishmaplus.Activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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

public class PostDetails extends AppCompatActivity {

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        LineChart lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);

        setupPieChart();

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
                switch ((int) value) {
                    case 0: return "Nov 18";
                    case 1: return "Nov 21";
                    case 2: return "Nov 24";
                    case 3: return "Nov 27";
                    case 4: return "Nov 30";
                    case 5: return "Dec 3";
                    case 6: return "Dec 6";
                    case 7: return "Dec 9";
                    case 8: return "Dec 12";
                    case 9: return "Dec 15";
                    case 10: return "Dec 18";
                    case 11: return "Dec 21";
                    default: return "";
                }
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