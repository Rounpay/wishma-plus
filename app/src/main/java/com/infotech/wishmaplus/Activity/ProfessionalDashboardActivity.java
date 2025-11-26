package com.infotech.wishmaplus.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.infotech.wishmaplus.Fragments.HomeFragment;
import com.infotech.wishmaplus.Fragments.HomeFragmentProfessional;
import com.infotech.wishmaplus.Fragments.InsightsProfessionalFragment;
import com.infotech.wishmaplus.Fragments.ProfessionalDashboard.Engagement;
import com.infotech.wishmaplus.Fragments.ProfessionalDashboard.ProfessionalHome;
import com.infotech.wishmaplus.R;

public class ProfessionalDashboardActivity extends AppCompatActivity {
    AppCompatTextView home, insights, content, engagement, monetization;
    AppCompatTextView selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professional_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        home = findViewById(R.id.home);
        insights = findViewById(R.id.insights);
        content = findViewById(R.id.content);
        engagement = findViewById(R.id.engagement);
        monetization = findViewById(R.id.monetization);
        selectedTab = home;
        setSelectedTab(home);
        home.setOnClickListener(v -> setSelectedTab(home));
        insights.setOnClickListener(v -> setSelectedTab(insights));
        content.setOnClickListener(v -> setSelectedTab(content));
        engagement.setOnClickListener(v -> setSelectedTab(engagement));
        monetization.setOnClickListener(v -> setSelectedTab(monetization));
    }

    private void setSelectedTab(AppCompatTextView newTab) {
        if (selectedTab != null) {
            selectedTab.setBackgroundResource(R.drawable.tab_unselected_bg);
            selectedTab.setTextColor(getResources().getColor(R.color.new_gray));
        }
        newTab.setBackgroundResource(R.drawable.tab_selected_bg);
        newTab.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        selectedTab = newTab;
        int id = newTab.getId();
        if (id == R.id.home) {
            loadFragment(new ProfessionalHome());
        } else if (id == R.id.insights) {
            loadFragment(new InsightsProfessionalFragment());
        } else if (id == R.id.content) {
            loadFragment(new HomeFragmentProfessional());
        } else if (id == R.id.engagement) {
            loadFragment(new Engagement());
        } else if (id == R.id.monetization) {
            loadFragment(new HomeFragmentProfessional());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}