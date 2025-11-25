package com.infotech.wishmaplus.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.IncomeAdapter;
import com.infotech.wishmaplus.Api.Response.Income;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;

public class IncomeReportActivity extends AppCompatActivity {

    ArrayList<Income> incomeList = new ArrayList<Income>();
    IncomeAdapter adapter = new IncomeAdapter(incomeList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_income_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.rvIncome);
        ProgressBar progressBar = findViewById(R.id.progress);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.back_button).setOnClickListener(view -> finish());

        UtilMethods.INSTANCE.getIncomeResponse(this, object -> {
            incomeList.addAll((ArrayList<Income>) object);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
        });

    }
}