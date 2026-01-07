package com.infotech.wishmaplus.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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
import com.infotech.wishmaplus.Api.Response.GetContentDetailsToBoostResponse;
import com.infotech.wishmaplus.Api.Response.InsightsStatsResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostDetails extends AppCompatActivity {

    PieChart pieChart;
    private CustomLoader loader;
    GetContentDetailsToBoostResponse getContentDetailsToBoostResponse = new GetContentDetailsToBoostResponse();
    InsightsStatsResponse insightsStatsResponse = new InsightsStatsResponse();
    androidx.appcompat.widget.AppCompatImageView profile,containerImage;
    androidx.appcompat.widget.AppCompatTextView nameTv,timeTv,postTxt;
    View containerVideo;
    VideoView videoView;
    String postId ="";
    LineChart lineChart;
    TextView tvViews,viewsValue,tvEarning,earnValue,tvEngage,engageValue,tvClick,clickValue,totalViewer,tvReactions,tvComments,tvShares,tvClicks;

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
        postId = getIntent().getStringExtra("postId");
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        profile = findViewById(R.id.profile);
        nameTv = findViewById(R.id.nameTv);
        timeTv = findViewById(R.id.timeTv);
        postTxt = findViewById(R.id.postTxt);
        containerVideo = findViewById(R.id.containerVideo);
        videoView = findViewById(R.id.videoView);
        containerImage = findViewById(R.id.containerImage);
        tvViews = findViewById(R.id.tvViews);
        viewsValue = findViewById(R.id.viewsValue);
        tvEarning = findViewById(R.id.tvEarning);
        earnValue = findViewById(R.id.earnValue);
        tvEngage = findViewById(R.id.tvEngage);
        tvEngage = findViewById(R.id.tvEngage);
        engageValue = findViewById(R.id.engageValue);
        tvClick = findViewById(R.id.tvClick);
        clickValue = findViewById(R.id.clickValue);
        totalViewer = findViewById(R.id.totalViewer);
        tvReactions = findViewById(R.id.tvReactions);
        tvComments = findViewById(R.id.tvComments);
        tvShares = findViewById(R.id.tvShares);
        tvClicks = findViewById(R.id.tvClicks);
        getContentDetailsToBoostResponse(postId);
        getPostStats(postId);
        setupPieChart();

    }
    public void setupLineChart(){
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
    public void setupLineChart(List<InsightsStatsResponse.InsightDateWise> insightsDateWise) {

        // Safety check
        if (insightsDateWise == null || insightsDateWise.isEmpty()) {
            lineChart.clear();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();
        entries.add(new Entry(0, 0));
        xLabels.add(minusDaysFromDate(insightsDateWise.get(0).getInsightDate(), -7)); // 👈 SAME API DATE FORMAT
        // Convert API data to chart entries



        for (int i = 0; i < insightsDateWise.size(); i++) {
            InsightsStatsResponse.InsightDateWise item = insightsDateWise.get(i);
            Log.e("TAG", "setupLineChart: "+insightsDateWise.size() );
            entries.add(new Entry(i+1, item.getInsightCount()));
            xLabels.add(item.getInsightDate()); // 👈 SAME API DATE FORMAT
        }
        entries.add(new Entry(2, 0));
        xLabels.add(minusDaysFromDate(insightsDateWise.get(insightsDateWise.size()-1).getInsightDate(), 7));

        // Dataset
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#1A73E8"));
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawFilled(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Chart settings
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);

        // X Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setGranularity(1f);
//        xAxis.setLabelRotationAngle(-45f); // optional, helps avoid overlap

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < xLabels.size())
                        ? xLabels.get(index)
                        : "";
            }
        });

        // Y Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#EEEEEE"));
        leftAxis.setTextColor(Color.GRAY);

        lineChart.getAxisRight().setEnabled(false);

        // Refresh chart
        lineChart.invalidate();
    }

    public void getContentDetailsToBoostResponse(String postId){
        loader.show();
        UtilMethods.INSTANCE.getContentDetailsToBoost(postId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                getContentDetailsToBoostResponse =(GetContentDetailsToBoostResponse) object;
                if(getContentDetailsToBoostResponse.getStatusCode()==1){
                    GetContentDetailsToBoostResponse.PostInsights postInsights = getContentDetailsToBoostResponse.getPostInsights();
                    Glide.with(PostDetails.this).load(postInsights.getProfilePictureUrl()).placeholder(R.drawable.user_icon).into(profile);
                    nameTv.setText(postInsights.getUserName());
                    timeTv.setText(postInsights.getCreatedDate());
                    if(postInsights.getCaption()!=null) {
                        postTxt.setText(postInsights.getCaption());
                    }
                    if(postInsights.getContentTypeId()==1){//text
                        containerVideo.setVisibility(GONE);
                        containerImage.setVisibility(GONE);
                    }
                    else if(postInsights.getContentTypeId()==2) {//video
                        containerVideo.setVisibility(VISIBLE);
                        containerImage.setVisibility(GONE);
                        videoView.setVideoPath(postInsights.getPostContent());
                    }
                    else if(postInsights.getContentTypeId()==3) {//IMAGE
                        containerVideo.setVisibility(GONE);
                        containerImage.setVisibility(VISIBLE);
                        Glide.with(PostDetails.this).load(postInsights.getPostContent()).placeholder(R.drawable.app_logo).into(containerImage);
                    }
                }


            }

            @Override
            public void onError(String msg) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

            }
        });
    }
    public void getPostStats(String postId){
        loader.show();
        UtilMethods.INSTANCE.getPostStats(postId,0, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                insightsStatsResponse =(InsightsStatsResponse) object;
                if(insightsStatsResponse.getStatusCode()==1){
                    setupLineChart(insightsStatsResponse.getResult().getInsightsDateWise());
                    viewsValue.setText(insightsStatsResponse.getResult().getTotalInsights().getTotalViews()+"");
                    earnValue.setText(insightsStatsResponse.getResult().getTotalInsights().getTotalEarning()+"");
                    engageValue.setText(insightsStatsResponse.getResult().getTotalInsights().getEngagement()+"");
                    clickValue.setText(insightsStatsResponse.getResult().getTotalInsights().getClick()+"");
                    totalViewer.setText(insightsStatsResponse.getResult().getTotalInsights().getTotalViews()+"");
                    tvReactions.setText(insightsStatsResponse.getResult().getTotalInsights().getTotalLikes()+"");
                    tvComments.setText(insightsStatsResponse.getResult().getTotalInsights().getTotalComments()+"");
                    tvShares.setText(insightsStatsResponse.getResult().getTotalInsights().getTotalShares()+"");
                    tvClicks.setText(insightsStatsResponse.getResult().getTotalInsights().getClick()+"");
                }


            }

            @Override
            public void onError(String msg) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }

            }
        });
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

    public static String minusDaysFromDate(String dateStr, int daysToMinus) {
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            Date date = sdf.parse(dateStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, daysToMinus);

            return sdf.format(calendar.getTime());

        } catch (Exception e) {
            e.printStackTrace();
            return dateStr; // fallback
        }
    }
}