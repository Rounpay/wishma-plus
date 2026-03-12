package com.infotech.wishmaplus.Activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.GetRoomIdResponse;
import com.infotech.wishmaplus.Fragments.FriendListFragment;
import com.infotech.wishmaplus.Fragments.HomeFragment;
import com.infotech.wishmaplus.Fragments.MoreFragment;
import com.infotech.wishmaplus.Fragments.NotificationFragment;
import com.infotech.wishmaplus.Fragments.VideoFragment;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;
import com.infotech.wishmaplus.zego.LivePageActivity;
import com.infotech.wishmaplus.zego.PreviewActivity;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.entity.ZegoEngineProfile;


public class MainActivity extends AppCompatActivity {
    String name, email;
    MaterialButton balance;
    ImageView homeTab, videoTab, usersTab, notificationTab, menuTab;
    View homeLine, videoLine, usersLine, notificationLine, menuLine;
    View selectedLine;
    public CustomLoader loader;
    public PreferencesManager tokenManager;
    String pageId = null;
    String finalPageId = null;
    private boolean isProfileType;

    public String postId = "";
    private long appID = 1481330104 ;
    private String appSign = "feaffdef861ae4d24300952320aeb17e8e4c14557380c19e1aa64d26d5985200" ;
    public boolean fromNotification  = false;
    String roomId="";


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
        tokenManager = new PreferencesManager(this, 1);
        if (getIntent().getStringExtra("pageId") != null) {
            pageId = getIntent().getStringExtra("pageId");
        }
        String savedPageId = tokenManager.getString("ACTIVE_PAGE_ID");
        isProfileType = tokenManager.getBooleanNonRemoval("PROFILE_TYPE");
        if (pageId != null && !pageId.isEmpty()) {
            finalPageId = pageId;
            tokenManager.set("ACTIVE_PAGE_ID", pageId);
        } else if (savedPageId != null && !savedPageId.isEmpty()) {
            finalPageId = savedPageId;
        } else {
            finalPageId = null;
        }
        createEngine();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM_TOKEN", "Token: " + token);

                    PreferencesManager mAppPreferences = new PreferencesManager(this, 2);
                    mAppPreferences.setNonRemoval(ApplicationConstant.INSTANCE.regFCMKeyPref, token);
                });
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar);

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
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            handleNotificationIntent(getIntent());
        }, 500);

        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, HomeFragment.newInstance(finalPageId,isProfileType), "Home").commit();

        selectedLine = homeLine;
        homeTab.setOnClickListener(view -> {
            if (selectedLine != homeLine) {
                selectedLine.setBackgroundColor(Color.WHITE);
                homeLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                selectedLine = homeLine;
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, HomeFragment.newInstance(finalPageId,isProfileType), "Home").commit();
            }
        });

        videoTab.setOnClickListener(view -> {
            if (selectedLine != videoLine) {
                selectedLine.setBackgroundColor(Color.WHITE);
                videoLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                selectedLine = videoLine;
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, VideoFragment.newInstance(finalPageId,isProfileType), "Video").commit();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, MoreFragment.newInstance(finalPageId,isProfileType)).commit();
            }
        });

        findViewById(R.id.addPost).setOnClickListener(view -> {
            showPopupMenu(view);

        });
        getBalance();
    }
    public void navigateToHome() {
        if (selectedLine != homeLine) {
            selectedLine.setBackgroundColor(Color.WHITE);
            homeLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            selectedLine = homeLine;
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, HomeFragment.newInstance(finalPageId,isProfileType), "Home").commit();
        }
    }

    void createEngine() {
        ZegoEngineProfile profile = new ZegoEngineProfile();

        // Get your AppID and AppSign from ZEGOCLOUD Console
        //[My Projects -> AppID] : https://console.zegocloud.com/project
        profile.appID = appID;
        profile.appSign = appSign;
        profile.scenario = ZegoScenario.BROADCAST; // General scenario.
        profile.application = getApplication();
        ZegoExpressEngine.createEngine(profile, null);
    }
    private void destroyEngine() {
        ZegoExpressEngine.destroyEngine(null);
    }

    private static String generateRandomID() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        while (builder.length() < 6) {
            int nextInt = random.nextInt(10);
            if (builder.length() == 0 && nextInt == 0) {
                continue;
            }
            builder.append(nextInt);
        }
        return builder.toString();
    }

    private void requestPermissionIfNeeded(List<String> permissions, RequestCallback requestCallback) {
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }
        if (allGranted) {
            requestCallback.onResult(true, permissions, new ArrayList<>());
            return;
        }

        PermissionX.init(this).permissions(permissions).onExplainRequestReason((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(Manifest.permission.CAMERA)) {
                    message = this.getString(R.string.permission_explain_camera);
                } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.permission_explain_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(Manifest.permission.CAMERA)) {
                        message = this.getString(R.string.permission_explain_camera);
                    } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                        message = this.getString(R.string.permission_explain_mic);
                    }
                } else {
                    message = this.getString(R.string.permission_explain_camera_mic);
                }
            }
            scope.showRequestReasonDialog(deniedList, message, getString(R.string.ok));
        }).onForwardToSettings((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(Manifest.permission.CAMERA)) {
                    message = this.getString(R.string.settings_camera);
                } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.settings_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(Manifest.permission.CAMERA)) {
                        message = this.getString(R.string.settings_camera);
                    } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                        message = this.getString(R.string.settings_mic);
                    }
                } else {
                    message = this.getString(R.string.settings_camera_mic);
                }
            }
            scope.showForwardToSettingsDialog(deniedList, message, getString(R.string.settings),
                    getString(R.string.cancel));
        }).request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                 @NonNull List<String> deniedList) {
                if (requestCallback != null) {
                    requestCallback.onResult(allGranted, grantedList, deniedList);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Handle notification click when app is in background/foreground
        handleNotificationIntent(intent);
    }

    /**
     * Handle notification click and open NotificationFragment
     */
    private void handleNotificationIntent(Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Intent is null");
            return;
        }

        Log.d(TAG, "Intent Action: " + intent.getAction());
        Log.d(TAG, "Intent Extras: " + intent.getExtras());

        // Extract unified data
        Bundle bundle = extractNotificationData(intent);

        if (bundle != null) {
            Log.d(TAG, "Notification data found → opening fragment");
            openNotificationFragment(bundle);
            return;
        }

        Log.d(TAG, "No notification data found");
    }

    /**
     * extractNotificationData
     */
    private Bundle extractNotificationData(Intent intent) {
        if (intent == null || intent.getExtras() == null) return null;
        Bundle bundle = new Bundle();
        Bundle extras = intent.getExtras();
        // Try custom data first
        String title = extras.getString("Title", extras.getString("title"));
        String message = extras.getString("Message", extras.getString("message"));
        String image = extras.getString("Image", extras.getString("image"));
        String url = extras.getString("Url", extras.getString("url"));
        String time = extras.getString("Time", extras.getString("time"));
        String type = extras.getString("Type", extras.getString("type"));
        int nid = extras.getInt("NotificationId", -1);

        // FCM Notification Payload fallback
        if (title == null)
            title = extras.getString("gcm.notification.title");
        if (message == null)
            message = extras.getString("gcm.notification.body");

        // If nothing found → return null
        if (title == null && message == null) return null;

        bundle.putString("Title", title != null ? title : "");
        bundle.putString("Message", message != null ? message : "");
        bundle.putString("Image", image != null ? image : "");
        bundle.putString("Url", url != null ? url : "");
        bundle.putString("Time", time != null ? time : "");
        bundle.putString("Type", type != null ? type : "");
        bundle.putInt("NotificationId", nid);

        return bundle;
    }


    /**
     * Open NotificationFragment with data
     */
    private void openNotificationFragment(Bundle data) {
        try {
            // Find fragment container
            int containerId = R.id.flFragment;

            // Check if container exists
            if (findViewById(containerId) == null) {
                Log.e(TAG, "Fragment container not found! Please add FrameLayout with id 'fragment_container' in activity_main.xml");
                return;
            }
            // Remove OLD NotificationFragment from backstack
            getSupportFragmentManager().popBackStack("NotificationFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            // Make fragment container visible
            findViewById(containerId).setVisibility(android.view.View.VISIBLE);

            // Create NotificationFragment instance
            Fragment notificationFragment = new NotificationFragment();
            selectedLine.setBackgroundColor(Color.WHITE);
            notificationLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            selectedLine = notificationLine;
            notificationFragment.setArguments(data);

            // Replace current fragment with NotificationFragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, notificationFragment, "NotificationFragment");
            transaction.addToBackStack("NotificationFragment");
            transaction.commitAllowingStateLoss();

            Log.d(TAG, "NotificationFragment opened successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error opening NotificationFragment: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void refresh(int typeId) {

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
                    int typeId = 1;
                    if (result.getData() != null) {
                        typeId = result.getData().getIntExtra("Type", 1);
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
        TextView liveVideo = popupView.findViewById(R.id.liveVideo);
        TextView joinNow = popupView.findViewById(R.id.joinNow);
        addPost.setOnClickListener(v -> {
            popupWindow.dismiss();
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            intent.putExtra("userData", UtilMethods.INSTANCE.getUserDetailResponse(tokenManager));
            intent.putExtra("postId", "0");
            intent.putExtra("postType", 1);
            intent.putExtra("pageId", pageId);
            postActivityResultLauncher.launch(intent);
        });
        addStory.setOnClickListener(v -> {

            popupWindow.dismiss();
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            intent.putExtra("userData", UtilMethods.INSTANCE.getUserDetailResponse(tokenManager));
            intent.putExtra("postId", "0");
            intent.putExtra("postType", 2);
            intent.putExtra("pageId", pageId);
            intent.putExtra("pageId", finalPageId);
            intent.putExtra("isProfileType", isProfileType);
            storyActivityResultLauncher.launch(intent);
        });
        liveVideo.setOnClickListener(v -> {

            popupWindow.dismiss();
// Before starting a live streaming, request the camera and recording permissions.
            requestPermissionIfNeeded(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                    if (allGranted) {
                        Toast.makeText(MainActivity.this, "All permissions have been granted.", Toast.LENGTH_SHORT).show();
//                        GetRoomId();
                        openLiveActivity(roomId);

                    } else {
                        Toast.makeText(MainActivity.this, "Some permissions have not been granted.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
        joinNow.setOnClickListener(v -> {

            popupWindow.dismiss();
// Before starting a live streaming, request the camera and recording permissions.
            requestPermissionIfNeeded(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                    if (allGranted) {
                        Toast.makeText(MainActivity.this, "All permissions have been granted.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LivePageActivity.class);
                        String userID = generateRandomID();
                        String userName = "Shivam";//"user_" + userID;
                        intent.putExtra("userID", userID);
                        intent.putExtra("userName", userName);
                        intent.putExtra("roomID", "12345");
                        intent.putExtra("isHost", false);
                        intent.putExtra("isMicEnabled", false);
                        intent.putExtra("isCameraEnabled", false);
                        intent.putExtra("isFrontCamera", false);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Some permissions have not been granted.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        // Display the popup window at the center of the screen
        popupWindow.showAsDropDown(view, 0, 0, Gravity.BOTTOM);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetRoomId();
        endLiveStream(roomId);
    }

    private void endLiveStream(String roomID) {
        UtilMethods.INSTANCE.endLive(roomID,new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                if(loader != null && loader.isShowing()){
                    loader.dismiss();
                }

                BasicResponse response = (BasicResponse) object;


            }

            @Override
            public void onError(String msg) {
                if(loader != null && loader.isShowing()){
                    loader.dismiss();
                }

            }
        });
    }

    public void GetRoomId(){
        loader.show();
        UtilMethods.INSTANCE.getRoomId(new UtilMethods.ApiCallBackMulti() {
             @Override
            public void onSuccess(Object object) {
                 if(loader != null && loader.isShowing()){
                    loader.dismiss();
                 }

                GetRoomIdResponse response = (GetRoomIdResponse) object;
                if(response.getStatusCode() == 1){
                    if(response.getResult() != null){
                         roomId = response.getResult().getRoomId();
                        Log.d("ROOM_ID", roomId);
//                        openLiveActivity(roomId);
                    }

                }

            }

            @Override
            public void onError(String msg) {
                  if(loader != null && loader.isShowing()){
                    loader.dismiss();
                }

            }
        });
    }

    private void openLiveActivity(String roomId){
        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
        intent.putExtra("userID", tokenManager.getUserId());
        intent.putExtra("userName", Utility.INSTANCE.getFullName(tokenManager.getFirstName(), tokenManager.getLastName()));
        intent.putExtra("roomID", roomId);
        intent.putExtra("isHost", true);
        startActivity(intent);

    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (selectedLine != homeLine) {
                selectedLine.setBackgroundColor(Color.WHITE);
                homeLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                selectedLine = homeLine;
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, HomeFragment.newInstance(finalPageId,isProfileType)).commit();
            }
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}