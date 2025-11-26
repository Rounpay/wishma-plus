package com.infotech.wishmaplus.Fragments.ProfessionalDashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.infotech.wishmaplus.Activity.CreateGroupActivity;
import com.infotech.wishmaplus.Adapter.FeatureAdapter;
import com.infotech.wishmaplus.Adapter.MessageAdapter;
import com.infotech.wishmaplus.Adapter.MonetizationAdapter;
import com.infotech.wishmaplus.Adapter.StatsAdapter;
import com.infotech.wishmaplus.Api.Response.FeatureItem;
import com.infotech.wishmaplus.Api.Response.MessageModel;
import com.infotech.wishmaplus.Api.Response.StatCard;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;


public class ProfessionalHome extends Fragment {


    public ProfessionalHome() {
        // Required empty public constructor
    }

    public static ProfessionalHome newInstance() {
        ProfessionalHome fragment = new ProfessionalHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    LineChart lineChart;
    BottomSheetDialog bottomSheetDialogReport;
    RecyclerView engagementRecyclerView;
    RecyclerView monRecycler;
    RecyclerView statsRecycler;
    List<MessageModel> list = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_professional_home, container, false);

        // Find the LineChart inside the inflated view
        lineChart = view.findViewById(R.id.lineChart);
        view.findViewById(R.id.infoButton).setOnClickListener(view1 -> openReportBottomSheetDialog(requireActivity()));
        statsRecycler = view.findViewById(R.id.statsRecycler);
        monRecycler = view.findViewById(R.id.monRecycler);
        engagementRecyclerView = view.findViewById(R.id.engagementRecyclerView);




        // Setup the chart
        setupChart();
        stats();
        setEngagementRecyclerView();
        monetization();


        return view;
    }

    public void stats(){
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        statsRecycler.setLayoutManager(layoutManager);


        List<StatCard> list = new ArrayList<>();
        list.add(new StatCard(R.drawable.globe, "2", "Engagement"));
        list.add(new StatCard(R.drawable.ic_users_big, "9", "Net followers"));
        list.add(new StatCard(R.drawable.ic_bell, "7", "Notifications"));
        list.add(new StatCard(R.drawable.ic_comment, "5", "Comments"));

        StatsAdapter adapter = new StatsAdapter(list);

        statsRecycler.setAdapter(adapter);
    }
    public void monetization(){
        LinearLayoutManager monLayoutManager =
                new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        monRecycler.setLayoutManager(monLayoutManager);

        List<StatCard> monList = new ArrayList<>();
        monList.add(new StatCard(R.drawable.globe, "Stars", "2 of 4 criteria met"));
        monList.add(new StatCard(R.drawable.ic_users_big, "Subscription", "2 of 4 criteria met"));
        monList.add(new StatCard(R.drawable.ic_bell, "Content monetization", "Invite only"));
        monList.add(new StatCard(R.drawable.ic_comment, "Storefront", "Invite only"));
        MonetizationAdapter monAdapter = new MonetizationAdapter(monList);
        monRecycler.setAdapter(monAdapter);
    }

    public  void  setEngagementRecyclerView(){
        list.add(new MessageModel("Hello", "Test · 4 mins"));
        list.add(new MessageModel("Hii", "Test · 2 hrs"));

        MessageAdapter adapter = new MessageAdapter(requireActivity(), list);
        engagementRecyclerView.setAdapter(adapter);
    }

    public  void openReportBottomSheetDialog(Activity context){
        if (bottomSheetDialogReport != null && bottomSheetDialogReport.isShowing()) {
            return;
        }
        bottomSheetDialogReport = new BottomSheetDialog(context, R.style.DialogStyle);
        bottomSheetDialogReport.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.info_bottom_sheet, null);



        bottomSheetDialogReport.setContentView(sheetView);
        BottomSheetBehavior
                .from(bottomSheetDialogReport.findViewById(com.google.android.material.R.id.design_bottom_sheet))
                .setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialogReport.show();

    }
    private void setupChart() {
        if (lineChart == null) return;

        // Example data: views over specific dates
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0));   // Nov 01
        entries.add(new Entry(1, 4));   // Nov 06
        entries.add(new Entry(2, 0));   // Nov 11
        entries.add(new Entry(3, 8));   // Nov 12
        entries.add(new Entry(4, 0));   // Nov 15
        entries.add(new Entry(5, 0));   // Nov 20

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#4A90E2"));
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false); // Remove dots
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#B3D4FF"));
        dataSet.setFillAlpha(180);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        // XAxis setup
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6, true);

        // Custom labels for X-axis
        final String[] dates = new String[] {"Nov 01", "Nov 06", "Nov 11", "Nov 12", "Nov 15", "Nov 20"};
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dates.length) {
                    return dates[index];
                } else {
                    return "";
                }
            }
        });

        // YAxis setup
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(true); // Show horizontal lines
        leftAxis.setGridColor(Color.LTGRAY); // optional color
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);

        lineChart.invalidate();
    }


}