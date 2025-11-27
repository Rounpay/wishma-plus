package com.infotech.wishmaplus.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.infotech.wishmaplus.Api.Object.BalanceResult;
import com.infotech.wishmaplus.Api.Response.BasicObjectResponse;
import com.infotech.wishmaplus.Fragments.FriendListFragment;
import com.infotech.wishmaplus.Fragments.HomeFragment;
import com.infotech.wishmaplus.Fragments.MoreFragment;
import com.infotech.wishmaplus.Fragments.NotificationFragment;
import com.infotech.wishmaplus.Fragments.UsersFragment;
import com.infotech.wishmaplus.Fragments.VideoFragment;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    String name, email;
    MaterialButton balance;
    ImageView homeTab, videoTab, usersTab, notificationTab, menuTab;
    View homeLine, videoLine, usersLine, notificationLine, menuLine;
    View selectedLine;
    public CustomLoader loader;
    public PreferencesManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM_TOKEN", "Token: " + token);

                    PreferencesManager mAppPreferences = new PreferencesManager(this,2);
                    mAppPreferences.setNonRemoval(ApplicationConstant.INSTANCE.regFCMKeyPref, token);
                });
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);
        tokenManager = new PreferencesManager(this,1);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");

        balance = findViewById(R.id.balance);

        homeTab = findViewById(R.id.homeTab);
        videoTab = findViewById(R.id.videoTab);
        usersTab = findViewById(R.id.usersTab);
        notificationTab = findViewById(R.id.notificationTab);
        menuTab = findViewById(R.id.menuTab);
        homeLine = findViewById(R.id.homeLine);
        videoLine = findViewById(R.id.videoLine);
        usersLine = findViewById(R.id.usersLine);
        notificationLine = findViewById(R.id.notificationLine);
        menuLine = findViewById(R.id.menuLine);


        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new HomeFragment(), "Home").commit();

        selectedLine = homeLine;
        homeTab.setOnClickListener(view -> {
            if (selectedLine != homeLine) {
                selectedLine.setBackgroundColor(Color.WHITE);
                homeLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                selectedLine = homeLine;
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new HomeFragment(), "Home").commit();
            }
        });

        videoTab.setOnClickListener(view -> {
            if (selectedLine != videoLine) {
                selectedLine.setBackgroundColor(Color.WHITE);
                videoLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                selectedLine = videoLine;
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new VideoFragment(), "Video").commit();
            }
        });

        usersTab.setOnClickListener(view -> {
            if (selectedLine != usersLine) {
                selectedLine.setBackgroundColor(Color.WHITE);
                usersLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                selectedLine = usersLine;
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new /*UsersFragment()*/FriendListFragment(), "User").commit();
            }
        });

        notificationTab.setOnClickListener(view -> {
            if (selectedLine != notificationLine) {
                selectedLine.setBackgroundColor(Color.WHITE);
                notificationLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                selectedLine = notificationLine;
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new NotificationFragment(), "Notification").commit();
            }
        });

        menuTab.setOnClickListener(view -> {
            if (selectedLine != menuLine) {
                selectedLine.setBackgroundColor(Color.WHITE);
                menuLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                selectedLine = menuLine;
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new MoreFragment()).commit();
            }
        });

        findViewById(R.id.addPost).setOnClickListener(view -> {
            showPopupMenu(view);

        });
        getBalance();
    }

    public void refresh(int typeId){

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.flFragment);
        if (f instanceof HomeFragment) {
            ((HomeFragment) f).refresh();
        } else if (f instanceof VideoFragment && typeId == UtilMethods.INSTANCE.VIDEO_TYPE) {
            ((VideoFragment) f).refresh();
        }
    }

    ActivityResultLauncher<Intent> postActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    int typeId=1;
                    if (result.getData() != null) {
                        typeId  = result.getData().getIntExtra("Type", 1);
                    }
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.flFragment);
                    if (f instanceof HomeFragment) {
                        ((HomeFragment) f).refresh();
                    } else if (f instanceof VideoFragment && typeId == UtilMethods.INSTANCE.VIDEO_TYPE) {
                        ((VideoFragment) f).refresh();
                    }

                }
            });

    ActivityResultLauncher<Intent> storyActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.flFragment);
                    if (f instanceof HomeFragment) {
                        ((HomeFragment) f).refreshStory();
                    }

                }
            });

   /* @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        if (item.getItemId() == R.id.homeMenu) {
            selectedFragment = new HomeFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString("name", name);
            mBundle.putString("email", email);
            selectedFragment.setArguments(mBundle);
        } else if (item.getItemId() == R.id.video) {
            selectedFragment = new VideoFragment();
        } else if (item.getItemId() == R.id.users) {
            selectedFragment = new UsersFragment();
        } else if (item.getItemId() == R.id.notification) {
            selectedFragment = new NotificationFragment();
        } else if (item.getItemId() == R.id.more) {
            selectedFragment = new MoreFragment();
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, selectedFragment).commit();
            return true;
        }
        return false;
    }*/


    public void getBalance() {
       /* try {

            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicObjectResponse<BalanceResult>> call = git.getBalance("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<BasicObjectResponse<BalanceResult>>() {
                @Override
                public void onResponse(@NonNull Call<BasicObjectResponse<BalanceResult>> call, @NonNull Response<BasicObjectResponse<BalanceResult>> response) {

                    try {
                        BasicObjectResponse<BalanceResult> balanceResponse = response.body();
                        if (balanceResponse != null) {
                            if (balanceResponse.getStatusCode() == 1) {
                                if (balanceResponse.getResult() != null ) {
                                    balance.setText(Utility.INSTANCE.formattedAmountWithRupees(balanceResponse.getResult().getBalance()));


                                }
                            } else {
                                UtilMethods.INSTANCE.Error(MainActivity.this, balanceResponse.getResponseText());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilMethods.INSTANCE.Error(MainActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicObjectResponse<BalanceResult>> call, @NonNull Throwable t) {
                    try {
                        UtilMethods.INSTANCE.apiFailureError(MainActivity.this, t);
                    } catch (IllegalStateException ise) {
                        UtilMethods.INSTANCE.Error(MainActivity.this, ise.getMessage());
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            UtilMethods.INSTANCE.Error(MainActivity.this, e.getMessage());
        }*/
    }

    private void showPopupMenu(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_add_post_popup, null);

        // Initialize the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView, (int) getResources().getDimension(com.intuit.sdp.R.dimen._140sdp), ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set up views in popup layout
        TextView addPost = popupView.findViewById(R.id.addPost);
        TextView addStory = popupView.findViewById(R.id.addStory);
        addPost.setOnClickListener(v -> {
            popupWindow.dismiss();
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
             intent.putExtra("userData", UtilMethods.INSTANCE.getUserDetailResponse(tokenManager));
            intent.putExtra("postId", "0");
            intent.putExtra("postType", 1);
            postActivityResultLauncher.launch(intent);
        });
        addStory.setOnClickListener(v -> {

            popupWindow.dismiss();
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            intent.putExtra("userData", UtilMethods.INSTANCE.getUserDetailResponse(tokenManager));
            intent.putExtra("postId", "0");
            intent.putExtra("postType", 2);
            storyActivityResultLauncher.launch(intent);
        });

        // Display the popup window at the center of the screen
        popupWindow.showAsDropDown(view,  0, 0,Gravity.BOTTOM);
    }
}