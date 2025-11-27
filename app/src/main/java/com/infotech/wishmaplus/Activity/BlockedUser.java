package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.BlockedAdapter;
import com.infotech.wishmaplus.Api.Response.UserModel;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;

public class BlockedUser extends AppCompatActivity {

    RecyclerView recyclerBlocked;
    ArrayList<UserModel> list;
    BlockedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blocked_user);
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerBlocked = findViewById(R.id.recyclerBlocked);

        list = new ArrayList<>();
        list.add(new UserModel("User Name 1", R.drawable.user_icon));
        list.add(new UserModel("User Name 2", R.drawable.user_icon));

        adapter = new BlockedAdapter(this, list,new BlockedAdapter.UnblockClickListener() {
            @Override
            public void onUnblockClicked(int position) {
                showUnblockDialog();
            }
        });



        recyclerBlocked.setLayoutManager(new LinearLayoutManager(this));
        recyclerBlocked.setAdapter(adapter);
    }
    public void showUnblockDialog() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        blurBackground(rootView);

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_unblock);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnUnblock = dialog.findViewById(R.id.btnUnblock);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);

        dialog.setOnDismissListener(dialogInterface -> removeBlur(rootView));
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnUnblock.setOnClickListener(v -> {
            // TODO: Add your unblock code here
            Toast.makeText(this, "User Unblocked", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
    private void blurBackground(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RenderEffect blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP);
            rootView.setRenderEffect(blurEffect);
        }
    }
    private void removeBlur(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rootView.setRenderEffect(null);
        }
    }
}