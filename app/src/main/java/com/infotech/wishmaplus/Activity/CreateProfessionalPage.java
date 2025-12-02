package com.infotech.wishmaplus.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.infotech.wishmaplus.Adapter.CategoryFilterAdapter;
import com.infotech.wishmaplus.Api.Response.CategoryResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateProfessionalPage extends AppCompatActivity {

    AppCompatTextView headingView;
    AutoCompleteTextView categorySearch;
    ChipGroup selectedChipGroup;

    List<CategoryResponse> categoryList = new ArrayList<>();
    List<CategoryResponse> selectedCategories = new ArrayList<>();
    String pageName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_professional_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getIntent().getStringExtra("pageName") != null &&
                !Objects.requireNonNull(getIntent().getStringExtra("pageName")).isEmpty()) {
            pageName = getIntent().getStringExtra("pageName");
        }
        headingView = findViewById(R.id.headingView);
        categorySearch = findViewById(R.id.categorySearch);
        selectedChipGroup = findViewById(R.id.selectedChipGroup);
        headingView.setText("What category best describes " + pageName);

        callApi();

        // AutoComplete selection
        categorySearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = parent.getItemAtPosition(position).toString();
            // Find category object
            CategoryResponse selected = null;
            for (CategoryResponse c : categoryList) {
                if (c.getCategoryName().equals(selectedName)) {
                    selected = c;
                    break;
                }
            }

            if (selected == null) return;

            // Already selected check
            for (CategoryResponse c : selectedCategories) {
                if (c.getCategoryID() == selected.getCategoryID()) {
                    categorySearch.setText("");
                    return;
                }
            }

            // Max 3 limit
            if (selectedCategories.size() >= 3) {
                showMaxDialog();
                categorySearch.setText("");
                return;
            }

            selectedCategories.add(selected);
            addChip(selected);

            categorySearch.setText(""); // clear input
        });
    }


    // ADD CHIP
    private void addChip(CategoryResponse category) {
        Chip chip = new Chip(this);
        chip.setText(category.getCategoryName());
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.grey_1);
        chip.setTextColor(Color.BLACK);

        chip.setOnCloseIconClickListener(v -> {
            selectedChipGroup.removeView(chip);

            // remove from selected list
            for (int i = 0; i < selectedCategories.size(); i++) {
                if (selectedCategories.get(i).getCategoryID() == category.getCategoryID()) {
                    selectedCategories.remove(i);
                    break;
                }
            }
        });

        selectedChipGroup.addView(chip);
    }

    // MAX 3 DIALOG
    private void showMaxDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Limit Reached")
                .setMessage("You can add up to 3 categories only.")
                .setPositiveButton("OK", null)
                .show();
    }


    // API CALL
    private void callApi() {
        UtilMethods.INSTANCE.getPageCategories(CreateProfessionalPage.this, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                categoryList = (List<CategoryResponse>) object;

                List<String> names = new ArrayList<>();
                for (CategoryResponse c : categoryList) {
                    names.add(c.getCategoryName());
                }

                // Set custom filter adapter
                CategoryFilterAdapter adapter = new CategoryFilterAdapter(
                        CreateProfessionalPage.this,
                        android.R.layout.simple_dropdown_item_1line,
                        names
                );

                categorySearch.setAdapter(adapter);
            }

            @Override
            public void onError(String msg) {
                UtilMethods.INSTANCE.Error(CreateProfessionalPage.this, msg);
            }
        });
    }

    // GET COMMA SEPARATED IDs
    public String getSelectedCategoryIDs() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedCategories.size(); i++) {
            builder.append(selectedCategories.get(i).getCategoryID());
            if (i < selectedCategories.size() - 1) builder.append(",");
        }
        return builder.toString();
    }

    // GET COMMA SEPARATED NAMES (if needed)
    public String getSelectedCategoryNames() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedCategories.size(); i++) {
            builder.append(selectedCategories.get(i).getCategoryName());
            if (i < selectedCategories.size() - 1) builder.append(",");
        }
        return builder.toString();
    }
}
