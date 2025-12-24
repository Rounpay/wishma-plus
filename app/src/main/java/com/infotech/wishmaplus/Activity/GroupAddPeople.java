package com.infotech.wishmaplus.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.InvitePeopleAdapter;
import com.infotech.wishmaplus.R;

import java.util.Arrays;
import java.util.List;

public class GroupAddPeople extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_add_people);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView rv = findViewById(R.id.rvPeople);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<String> names = Arrays.asList(
                "Rahul Prajapati",
                "Ups Chandan",
                "Saurabh Bhatt",
                "Abhishek Gupta",
                "Shiwani Singh Rajput"
        );

        rv.setAdapter(new InvitePeopleAdapter(names));
        findViewById(R.id.search_button).setOnClickListener(view -> {
            Intent intent = new Intent(GroupAddPeople.this, AddCoverPhotoGroup.class);
            startActivity(intent);
        });

    }
}