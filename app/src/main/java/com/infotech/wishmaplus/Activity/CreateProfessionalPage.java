package com.infotech.wishmaplus.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CreateProfessionalPage extends AppCompatActivity {

    AppCompatTextView headingView;
    AutoCompleteTextView categorySearch;
    ChipGroup selectedChipGroup;

    List<CategoryResponse> categoryList = new ArrayList<>();
    List<CategoryResponse> selectedCategories = new ArrayList<>();
    String pageName;

    ChipGroup popularChipGroup;

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
        popularChipGroup = findViewById(R.id.popularChipGroup);
        headingView.setText("What category best describes " + pageName);
        AppCompatTextView nextButton = findViewById(R.id.continueBtn);

        nextButton.setOnClickListener(v -> {

            // CHECK 1: Minimum 1 category required
            if (selectedCategories.isEmpty()) {
                Toast.makeText(this, "Please select at least one category", Toast.LENGTH_SHORT).show();
                return;
            }

            // GET IDs and Names
            String selectedIDs = getSelectedCategoryIDs();
            String selectedNames = getSelectedCategoryNames();

            // LOG RESULT
            Log.d("CATEGORY_RESULT", "IDs: " + selectedIDs);
            Log.d("CATEGORY_RESULT", "Names: " + selectedNames);

            // SHOW TOAST (Optional)
            Toast.makeText(this, "Selected: " + selectedNames, Toast.LENGTH_LONG).show();

            // SEND TO NEXT ACTIVITY

            // OR → API CALL
            // sendSelectedCategoriesToServer(selectedIDs);
        });

        callApi();

        // AutoComplete selection
        categorySearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = parent.getItemAtPosition(position).toString();

            CategoryResponse selected = findCategory(selectedName);
            if (selected == null) return;

            if (isAlreadyAdded(selected)) {
                Toast.makeText(this, "Category already added", Toast.LENGTH_SHORT).show();
                categorySearch.setText("");
                return;
            }

            if (selectedCategories.size() == 3) {
                showMaxDialog();
                categorySearch.setText("");
                return;
            }

            addCategory(selected);
            categorySearch.setText("");
            updateSearchVisibility();
        });
    }

    // FIND CATEGORY BY NAME
    private CategoryResponse findCategory(String name) {
        for (CategoryResponse c : categoryList) {
            if (c.getCategoryName().equals(name))
                return c;
        }
        return null;
    }

    // CHECK IF ALREADY ADDED
    private boolean isAlreadyAdded(CategoryResponse c) {
        for (CategoryResponse s : selectedCategories) {
            if (s.getCategoryID() == c.getCategoryID()) return true;
        }
        return false;
    }
    // ADD CHIP

    // ADD CATEGORY
    private void addCategory(CategoryResponse category) {
        selectedCategories.add(category);
        Chip chip = new Chip(this);

        chip.setText(category.getCategoryName());
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.grey_1);
        chip.setTextColor(Color.BLACK);

        chip.setOnCloseIconClickListener(v -> {
            selectedChipGroup.removeView(chip);
            selectedCategories.remove(category);
            updateSearchVisibility();
        });

        selectedChipGroup.addView(chip);
    }
    // RANDOM POPULAR 3 CATEGORIES
    private void loadPopularCategories() {
        Collections.shuffle(categoryList);
        List<CategoryResponse> randomList = categoryList.subList(0, Math.min(4, categoryList.size()));

        for (CategoryResponse c : randomList) {
            Chip chip = new Chip(this);
            chip.setText(c.getCategoryName());
            chip.setChipBackgroundColorResource(R.color.grey_1);
            chip.setTextColor(Color.BLACK);

            chip.setOnClickListener(v -> {
                if (isAlreadyAdded(c)) {
                    Toast.makeText(this, "Category already added", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedCategories.size() == 3) {
                    showMaxDialog();
                    return;
                }

                addCategory(c);
                updateSearchVisibility();
            });

            popularChipGroup.addView(chip);
        }
    }

    // HIDE SHOW SEARCH BOX
    private void updateSearchVisibility() {
        if (selectedCategories.size() == 3)
            categorySearch.setVisibility(View.GONE);
        else
            categorySearch.setVisibility(View.VISIBLE);
    }
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
        UtilMethods.INSTANCE.getPageCategories(this, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                categoryList = (List<CategoryResponse>) object;

                List<String> names = new ArrayList<>();
                for (CategoryResponse c : categoryList)
                    names.add(c.getCategoryName());

                categorySearch.setAdapter(new CategoryFilterAdapter(
                        CreateProfessionalPage.this,
                        android.R.layout.simple_dropdown_item_1line,
                        names
                ));

                loadPopularCategories();
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
