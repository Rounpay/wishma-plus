package com.infotech.wishmaplus.Activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.infotech.wishmaplus.R;

public class CreateNewProfilePage extends AppCompatActivity {

    private LinearLayout optionOneLayout, optionTwoLayout;
    private RadioButton optionOneRadio, optionTwoRadio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_profile_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        optionOneLayout = findViewById(R.id.optionOneLayout);
        optionTwoLayout = findViewById(R.id.optionTwoLayout);
        optionOneRadio = findViewById(R.id.optionOneRadio);
        optionTwoRadio = findViewById(R.id.optionTwoRadio);

        AppCompatImageButton back_button = findViewById(R.id.back_button);
        TextView cancel = findViewById(R.id.cancel_button);

        back_button.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        cancel.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        optionOneLayout.setOnClickListener(v -> {
            optionOneRadio.setChecked(true);
            optionTwoRadio.setChecked(false);

            optionOneLayout.setBackgroundResource(R.drawable.blue_border);
            optionTwoLayout.setBackgroundResource(R.drawable.gray_border);
        });

        optionTwoLayout.setOnClickListener(v -> {
            optionTwoRadio.setChecked(true);
            optionOneRadio.setChecked(false);

            optionTwoLayout.setBackgroundResource(R.drawable.blue_border);
            optionOneLayout.setBackgroundResource(R.drawable.gray_border);
        });

        optionOneRadio.setOnClickListener(v -> {
            optionOneLayout.performClick();
        });

        optionTwoRadio.setOnClickListener(v -> {
            optionTwoLayout.performClick();
        });
        AppCompatTextView nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            if (optionOneRadio.isChecked()) {
                Intent intent = new Intent(this, PersonalProfileActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, ProfessionalProfileActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}