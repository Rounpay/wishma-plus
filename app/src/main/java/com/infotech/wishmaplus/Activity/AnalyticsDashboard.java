package com.infotech.wishmaplus.Activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.infotech.wishmaplus.Adapter.ContentSummaryAdapter;
import com.infotech.wishmaplus.Api.Response.AnalyticsDetailsResponse;
import com.infotech.wishmaplus.Api.Response.AnalyticsResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsDashboard extends AppCompatActivity {
    BarChart barChart;
    PieChart pieChart;
    androidx.appcompat.widget.AppCompatTextView dateRange;
    int days=0;

    ImageButton filterButton;
    private CustomLoader loader;
    ImageView imgPost;
    VideoView videoPost;
    TextView postCaption, commentsValue, engagementValue, earningsValue, likesValue,totalEngagementValue,totalShareValue,totalCommentsValue,totalLikesValue;
    AnalyticsDetailsResponse analyticsResponse = new AnalyticsDetailsResponse();
    private RecyclerView recyclerView;

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
        dateRange = findViewById(R.id.subTitle);
        filterButton = findViewById(R.id.filter_button);
        imgPost = findViewById(R.id.imagePost);
        videoPost = findViewById(R.id.videoPost);
        postCaption = findViewById(R.id.postCaption);
        commentsValue = findViewById(R.id.commentsValue);
        engagementValue = findViewById(R.id.engagementValue);
        earningsValue = findViewById(R.id.earningsValue);
        likesValue = findViewById(R.id.likesValue);
        totalEngagementValue = findViewById(R.id.totalEngagementValue);
        totalShareValue = findViewById(R.id.totalShareValue);
        totalCommentsValue = findViewById(R.id.totalCommentsValue);
        totalLikesValue = findViewById(R.id.totalLikesValue);
        recyclerView = findViewById(R.id.rvContentSummary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filterButton.setOnClickListener(view -> UtilMethods.INSTANCE.selectDateRangeBottomSheetNew(this, dateRange, this::updateDateFilter,true));
        barChart = findViewById(R.id.barChart);
        setupChart();
        pieChart = findViewById(R.id.pieChart);
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);

        setupPieChart();

        LineChart lineChart = findViewById(R.id.lineChart);
        getDateWiseAnalytic(days);

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

    @SuppressLint("SetTextI18n")
    private void updateDateFilter(int days) {
        this.days = days;
        getDateWiseAnalytic(days);
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

    public void getDateWiseAnalytic(int dateRange){
        loader.show();
        UtilMethods.INSTANCE.getDateWiseAnalytic(dateRange, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if (loader != null) {
                    if (loader.isShowing()) {
                        loader.dismiss();
                    }
                }
                analyticsResponse = (AnalyticsDetailsResponse) object;
                if(analyticsResponse.getStatusCode()==1){
                    totalLikesValue.setText(analyticsResponse.getResult().getAnalytic().getTotalLikes()+"");
                    totalCommentsValue.setText(analyticsResponse.getResult().getAnalytic().getTotalComments()+"");
                    totalShareValue.setText(analyticsResponse.getResult().getAnalytic().getTotalShares()+"");
                    totalEngagementValue.setText(analyticsResponse.getResult().getAnalytic().getTotalEngagement()+"");
                    setContentSummary(analyticsResponse.getResult().getContent());
//                    profile_name.setText(analyticsResponse.getResult().getProfile().getFullName());
//                    Glide.with(ProfessionalDashBoardPersonal.this)
//                            .load(analyticsResponse.getResult().getProfile().getProfilePictureUrl())
//                            .apply(UtilMethods.INSTANCE.getRequestOption_With_UserIcon())
//                            .into(profile_image);
//                    int progress = analyticsResponse.getResult().getProfile().getWeelkyProgress();
//                    ObjectAnimator anim = ObjectAnimator.ofInt(progressCircle, "progress", progress);
//                    anim.setDuration(700);
//                    anim.setInterpolator(new DecelerateInterpolator());
//                    anim.start();
//                    tvPercent.setText(progress + "%");
//                    progressCircle.setProgress(progress);
//                    tvLikeValue.setText(analyticsResponse.getResult().getAnalytic().getTotalLikes()+"");
//                    tvCommentsValue.setText(analyticsResponse.getResult().getAnalytic().getTotalComments()+"");
//                    tvSharesValue.setText(analyticsResponse.getResult().getAnalytic().getTotalShares()+"");
//                    tvEngagementValue.setText(analyticsResponse.getResult().getAnalytic().getTotalEngagement()+"");
                    imgPost.setVisibility(View.GONE);
                    videoPost.setVisibility(View.GONE);

                    if (analyticsResponse.getResult().getLatestPosts().getContentTypeId() == 3) {  // IMAGE
                        imgPost.setVisibility(View.VISIBLE);
                        if(analyticsResponse.getResult().getLatestPosts().getCaption() != null)
                            postCaption.setText(analyticsResponse.getResult().getLatestPosts().getCaption());
                        Glide.with(AnalyticsDashboard.this)
                                .load(analyticsResponse.getResult().getLatestPosts().getPostContent())
                                .placeholder(R.drawable.app_logo)
                                .into(imgPost);
                    }

                    else if (analyticsResponse.getResult().getLatestPosts().getContentTypeId() == 2) {  // VIDEO
                        videoPost.setVisibility(View.VISIBLE);
                        videoPost.setVideoPath(analyticsResponse.getResult().getLatestPosts().getPostContent());
                        videoPost.seekTo(1); // show first frame
                        if(analyticsResponse.getResult().getLatestPosts().getCaption() != null)
                            postCaption.setText(analyticsResponse.getResult().getLatestPosts().getCaption());
                    }

                    else if (analyticsResponse.getResult().getLatestPosts().getContentTypeId() == 1) {  // TEXT
                        imgPost.setVisibility(View.VISIBLE);
                        postCaption.setText(analyticsResponse.getResult().getLatestPosts().getCaption());
                        Glide.with(AnalyticsDashboard.this)
                                .load(analyticsResponse.getResult().getLatestPosts().getPostContent())
                                .placeholder(R.drawable.app_logo)
                                .into(imgPost);

                    }

                    likesValue.setText(analyticsResponse.getResult().getLatestPosts().getTotalLikes()+"");
                    earningsValue.setText("₹"+analyticsResponse.getResult().getLatestPosts().getPostEarning()+"");
                    engagementValue.setText(analyticsResponse.getResult().getLatestPosts().getEngagement()+"");
                    commentsValue.setText(analyticsResponse.getResult().getLatestPosts().getTotalComments()+"");


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
    private void setContentSummary(
            List<AnalyticsDetailsResponse.ContentSummary> list
    ) {
        int maxValue = getMaxValue(list);

        ContentSummaryAdapter adapter =
                new ContentSummaryAdapter(list, maxValue);

        recyclerView.setAdapter(adapter);
    }

    private int getMaxValue(
            List<AnalyticsDetailsResponse.ContentSummary> list
    ) {
        int max = 0;
        for (AnalyticsDetailsResponse.ContentSummary item : list) {
            if (item.getTotal() > max) {
                max = item.getTotal();
            }
        }
        return max;
    }

}