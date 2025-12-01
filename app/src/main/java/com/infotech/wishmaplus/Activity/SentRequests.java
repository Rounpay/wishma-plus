package com.infotech.wishmaplus.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.FriendAdapter;
import com.infotech.wishmaplus.Api.Response.FriendRequestResponse;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class SentRequests extends AppCompatActivity {
    RecyclerView recyclerView;
    List<FriendRequestResponse> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sent_requests);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());

        recyclerView = findViewById(R.id.friendRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        list.add(new FriendRequestResponse("Anuj Panday", "17 mutual friends", R.drawable.user_icon));
        list.add(new FriendRequestResponse("Prasun Mishra", "9 mutual friends", R.drawable.user_icon));
        list.add(new FriendRequestResponse("Surjeet Yadav", "5 mutual friends", R.drawable.user_icon));
        list.add(new FriendRequestResponse("Karan Yadav", "5 mutual friends", R.drawable.user_icon));

        FriendAdapter adapter = new FriendAdapter(list,1);
        recyclerView.setAdapter(adapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}