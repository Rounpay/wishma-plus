package com.infotech.wishmaplus.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Activity.PostActivity;
import com.infotech.wishmaplus.Activity.ProfileActivity;
import com.infotech.wishmaplus.Activity.StoryViewActivity;
import com.infotech.wishmaplus.Adapter.MultiContentAdapter;
import com.infotech.wishmaplus.Adapter.StoryAdapter;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Object.StoryResult;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.Api.Response.ContentResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.CustomRecyclerView;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.DownloadManagerService;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.VideoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private static final String ARG_PAGE_ID = "pageId";
    private static final String ARG_PAGE_TYPE = "isProfileType";
    private String pageId = "";
    private boolean isProfile;
    CustomRecyclerView recyclerView;
    private PreferencesManager tokenManager;

    /* View overlay;
     FloatingActionButton fab;
     PopupWindow popupWindow;*/
    ArrayList<ContentResult> contentlist = new ArrayList<>();
    MultiContentAdapter adapter;
    CustomLoader loader;
    UserDetailResponse userDetailResponse;
    int pageNumber = 1;
    int totalPost = 0;
    public boolean isScreenPause;
    ArrayList<StoryResult> storyList = new ArrayList<>();
    private String postId;

    public static HomeFragment newInstance(String pageId, boolean isProfileType) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAGE_ID, pageId);
        args.putBoolean(ARG_PAGE_TYPE, isProfileType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageId = getArguments().getString(ARG_PAGE_ID);
            isProfile = getArguments().getBoolean(ARG_PAGE_TYPE, false);
            if (isProfile) {
                pageId = "";
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        loader = ((MainActivity) requireActivity()).loader;
        if (loader == null) {
            loader = new CustomLoader(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        }
        tokenManager = ((MainActivity) requireActivity()).tokenManager;
        if (tokenManager == null) {
            tokenManager = new PreferencesManager(requireActivity(), 1);
        }
        userDetailResponse = UtilMethods.INSTANCE.getUserDetailResponse(tokenManager);
        final SwipeRefreshLayout pullToRefresh = v.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            MainActivity main = (MainActivity) requireActivity();
            main.postId = "";
            main.fromNotification = false;
            refresh();
            getStory(true);
            getUserDetail(pageId, isProfile);
            ((MainActivity) requireActivity()).getBalance();
            pullToRefresh.setRefreshing(false);
        });


        /*overlay = v.findViewById(R.id.overlay);
        fab = v.findViewById(R.id.fab);*/
        recyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setActivity(getActivity());

        //optional - to play only first visible video
        recyclerView.setPlayOnlyFirstVideo(true); // false by default

        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        recyclerView.setCheckForMp4(false); //true by default

        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        recyclerView.setDownloadPath(requireActivity().getExternalCacheDir() + "/MyVideo/"); // (Environment.getExternalStorageDirectory() + "/Video") by default


        recyclerView.setVisiblePercent(70);
        // percentage of View that needs to be visible to start playing

        storyList.add(new StoryResult(StoryAdapter.VIEW_TYPE_CREATE));
        contentlist.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_POST, userDetailResponse, storyList));
        setAdapter();
        /*//call this functions when u want to start autoplay on loading async lists (eg firebase)
        recyclerView.smoothScrollBy(0, 1);
        recyclerView.smoothScrollBy(0, -1);*/
        recyclerView.setScrollListener((dx, dy) -> {
            if (dy > 0 && contentlist.get(contentlist.size() - 1).getContentTypeId() != MultiContentAdapter.VIEW_TYPE_LOADING && (contentlist.size() - 1) < totalPost) {
                // Get the current item count and the position of the last visible item
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                // If we are at the end of the list and more data is available, load more data
                if ((visibleItemCount + lastVisibleItemPosition) >= totalItemCount) {
                    contentlist.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_LOADING, null, null));
                    adapter.notifyItemInserted(contentlist.size());
                    pageNumber = pageNumber + 1;
                    showContent(false);
                    //loadData(++currentPage);  // Increment page and load next set of data
                }
            }
        });
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the user is scrolling downwards
                if (dy > 0 && contentlist.get(contentlist.size() - 1).getContentTypeId() != MultiContentAdapter.VIEW_TYPE_LOADING && (contentlist.size() - 1) < totalPost) {
                    // Get the current item count and the position of the last visible item
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    // If we are at the end of the list and more data is available, load more data
                    if ((visibleItemCount + lastVisibleItemPosition) >= totalItemCount) {
                        contentlist.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_LOADING, null));
                        adapter.notifyItemInserted(contentlist.size());
                        pageNumber = pageNumber + 1;
                        showContent();
                        //loadData(++currentPage);  // Increment page and load next set of data
                    }
                }
            }
        });*/

        loader.show();
        showContent(false);
        getStory(false);
        getUserDetail(pageId, isProfile);


       /* fab.setOnClickListener(v1 -> {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                showFabOptions();
            }
        });*/


        return v;
    }

    private void setAdapter() {
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        adapter = new MultiContentAdapter("0", contentlist, recyclerView, tokenManager, activity, new MultiContentAdapter.ClickCallBack() {
            @Override
            public void onClickCreatePost(String postId) {

                Intent intent = new Intent(requireActivity(), PostActivity.class);
                intent.putExtra("userData", userDetailResponse);
                intent.putExtra("postId", postId);
                intent.putExtra("pageId", pageId);
                intent.putExtra("isProfile", isProfile);
                intent.putExtra("postType", 1);
                postActivityResultLauncher.launch(intent);

            }

            @Override
            public void onClickCreateStory(String storyId) {
                Intent intent = new Intent(requireActivity(), PostActivity.class);
                intent.putExtra("userData", userDetailResponse);
                intent.putExtra("postId", storyId);
                intent.putExtra("pageId", pageId);
                intent.putExtra("isProfile", isProfile);
                intent.putExtra("postType", 2);
                storyActivityResultLauncher.launch(intent);
            }

            @Override
            public void onClickProfile(String userId, ContentResult content) {
                if (!(content.getGroupId() == null) && !content.getGroupId().isEmpty()) {
                    profileActivityResultLauncher.launch(new Intent(requireActivity(), ProfileActivity.class)
                            .putExtra("groupId", content.getGroupId())
                    );
                } else {
                    profileActivityResultLauncher.launch(new Intent(requireActivity(), ProfileActivity.class)
                            .putExtra("userData", userDetailResponse)
                            .putExtra("pageId", pageId)
                            .putExtra("isProfile", isProfile)
                    );
                }
            }

            @Override
            public void onOpenStory(ArrayList<StoryResult> list, int position, StoryResult result) {
                storyActivityResultLauncher.launch(new Intent(requireActivity(), StoryViewActivity.class)
                        .putParcelableArrayListExtra("List", list)
                        .putExtra("SelectedPosition", position)
                        .putExtra("Data", result));
            }

            @Override
            public void onDelete(int position) {
                contentlist.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeRemoved(position, contentlist.size());
                totalPost = totalPost - 1;

            }
        }, true);
        recyclerView.setAdapter(adapter);
    }


    private void getUserDetail(String pageId, boolean isProfile) {
        if (pageId != null && !isProfile) {
            UtilMethods.INSTANCE.getPageDetail(requireActivity(), pageId, loader, tokenManager, object -> {
                userDetailResponse = (UserDetailResponse) object;
                contentlist.set(0, new ContentResult(MultiContentAdapter.VIEW_TYPE_POST, userDetailResponse, storyList));

                adapter.notifyItemChanged(0);

            });
        } else {
            UtilMethods.INSTANCE.userDetail(requireActivity(), pageId, "", loader, tokenManager, object -> {
                userDetailResponse = (UserDetailResponse) object;
                contentlist.set(0, new ContentResult(MultiContentAdapter.VIEW_TYPE_POST, userDetailResponse, storyList));
                adapter.notifyItemChanged(0);

            });
        }
    }



    /*private void showFabOptions() {
        if (requireActivity() == null || getView() == null) {
            return;
        }

        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_menu, null);
        popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = popupView.getMeasuredWidth();
        int popupHeight = popupView.getMeasuredHeight();
        int[] fabLocation = new int[2];
        fab.getLocationOnScreen(fabLocation);
        int xOffset = -popupWidth;
        int yOffset = -popupHeight;
        popupWindow.showAtLocation(fab, Gravity.NO_GRAVITY, fabLocation[0] + xOffset, fabLocation[1] + yOffset);
        overlay.setVisibility(View.VISIBLE);

        TextView textItem = popupView.findViewById(R.id.menu_item_text);
        TextView videoItem = popupView.findViewById(R.id.menu_item_video);
        TextView imageItem = popupView.findViewById(R.id.menu_item_image);

        textItem.setOnClickListener(v -> {
            promptForTextInput();
            popupWindow.dismiss();
        });

        videoItem.setOnClickListener(v -> {
            selectMedia(2);
            popupWindow.dismiss();
        });

        imageItem.setOnClickListener(v -> {
            selectMedia(3);
            popupWindow.dismiss();
        });

        popupWindow.setOnDismissListener(() -> overlay.setVisibility(View.GONE));
    }*/
    /*private void promptForTextInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_text_input, null);
        final EditText input = dialogView.findViewById(R.id.edit_text_input);
        builder.setView(dialogView)
                .setTitle("What's on your mind?")
                .setPositiveButton("Post", (dialog, which) -> {
                    String text = input.getText().toString();
                    if (!text.isEmpty()) {
                        postContent("0", 1, text, "Text Post", null);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .setIcon(R.drawable.ic_draw);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary));
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorFwd));
        });
        dialog.show();
    }*/


    /* private void selectMedia(int mediaType) {
         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
         intent.setType(mediaType == 2 ? "video/*" : "image/*");
         startActivityForResult(intent, mediaType);
     }*/
    private void showContent(boolean isFromRefresh) {
        MainActivity main = (MainActivity) requireActivity();
        postId = main.postId;
        boolean isFromNotification = !main.postId.isEmpty();
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<ContentResponse> call = git.getContent("Bearer " + tokenManager.getAccessToken(), postId, "", pageNumber, 20, false, pageId, "", 0, isFromNotification);
            call.enqueue(new Callback<ContentResponse>() {
                @Override
                public void onResponse(@NonNull Call<ContentResponse> call, @NonNull Response<ContentResponse> response) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    if (response.isSuccessful()) {
                        ContentResponse contentResponse = response.body();
                        if (contentResponse != null && contentResponse.getResult() != null) {
                            totalPost = contentResponse.getTotalPost();
                            List<ContentResult> resultList = contentResponse.getResult();
                            //extra - start downloading all videos in background before loading RecyclerView
                            List<String> urls = new ArrayList<>();
                            for (ContentResult object : resultList) {
                                if (object.getContentTypeId() == 2 && object.getPostContent() != null && object.getPostContent().contains("http"))
                                    urls.add(object.getPostContent());
                            }
                            recyclerView.preDownload(urls);


                            if (pageNumber == 1) {
                                if (isFromRefresh) {
                                    recyclerView.pauseVideo();
                                    recyclerView.destroyVideo();
                                }
                                contentlist = new ArrayList<>();
                                contentlist.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_POST, userDetailResponse, storyList));
                                contentlist.addAll(resultList);
                                // adapter.notifyItemRangeChanged(0, contentlist.size());
                                setAdapter();
                                //if(pageNumber==1){
                                //call this functions when u want to start autoplay on loading async lists (eg firebase)
                                recyclerView.smoothScrollBy(0, 1);
                                recyclerView.smoothScrollBy(0, -1);
                                //  }
                            } else {
                                contentlist.remove(contentlist.size() - 1);
                                adapter.notifyItemRemoved(contentlist.size() - 1);
                                int size = contentlist.size();
                                contentlist.addAll(resultList);
                                adapter.notifyItemRangeChanged(size, contentlist.size());
                            }

                        }
                    } else {
                        UtilMethods.INSTANCE.apiErrorHandle(requireActivity(), response.code(), response.message());
                        // Toast.makeText(requireActivity(), "Failed to fetch content", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ContentResponse> call, @NonNull Throwable t) {
                    UtilMethods.INSTANCE.apiFailureError(requireActivity(), t);
                    //Toast.makeText(requireActivity(), "API call failed: " + t.getMessage()+"", Toast.LENGTH_SHORT).show();
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireActivity(), "API call failed: " + e.getMessage() + "", Toast.LENGTH_SHORT).show();
            if (loader != null) {
                if (loader.isShowing())
                    loader.dismiss();
            }
        }
        //   main.postId = "";
    }

    private void getStory(boolean isFromRefresh) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<BasicListResponse<StoryResult>> call = git.getStory("Bearer " + tokenManager.getAccessToken());
            call.enqueue(new Callback<BasicListResponse<StoryResult>>() {
                @Override
                public void onResponse(@NonNull Call<BasicListResponse<StoryResult>> call, @NonNull Response<BasicListResponse<StoryResult>> response) {
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                    if (response.isSuccessful()) {
                        BasicListResponse<StoryResult> storyResponse = response.body();
                        if (storyResponse != null && storyResponse.getResult() != null) {

                            storyList.clear();
                            storyList.add(new StoryResult(StoryAdapter.VIEW_TYPE_CREATE));
                            storyList.addAll(storyResponse.getResult());
                            contentlist.set(0, new ContentResult(MultiContentAdapter.VIEW_TYPE_POST, userDetailResponse, storyList));
                            adapter.notifyItemChanged(0);

                            for (StoryResult item : storyResponse.getResult()) {
                                for (ContentResult content : item.getStories()) {
                                    if (content.getContentTypeId() == UtilMethods.INSTANCE.VIDEO_TYPE) {
                                        Context context = getContext();
                                        if (context != null && content.getPostContent() != null) {
                                            String savePath = VideoUtils.getString(context, content.getPostContent());
                                            //  String savePath = VideoUtils.getString(requireActivity(), content.getPostContent());
                                            if (savePath == null || !new File(savePath).exists()) {
                                                Intent serviceIntent = new Intent(requireActivity(), DownloadManagerService.class);
                                                requireActivity().startService(serviceIntent);
                                                new VideoUtils().startDownloadInBackground(requireActivity(), content.getPostContent(), requireActivity().getExternalCacheDir() + "/MyVideo/");
                                            }
                                        }
                                    }
                                }
                            }


                            /*List<ContentResult> resultList = contentResponse.getResult();
                            //extra - start downloading all videos in background before loading RecyclerView
                            List<String> urls = new ArrayList<>();
                            for (ContentResult object : resultList) {
                                if (object.getContentTypeId() == 2 && object.getPostContent() != null && object.getPostContent().contains("http"))
                                    urls.add(object.getPostContent());
                            }
                            recyclerView.preDownload(urls);


                            if (pageNumber == 1) {
                                if(isFromRefresh){
                                    recyclerView.pauseVideo();
                                    recyclerView.destroyVideo();
                                }
                                contentlist= new ArrayList<>();
                                contentlist.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_POST, userDetailResponse,storyList));
                                contentlist.addAll(resultList);
                                // adapter.notifyItemRangeChanged(0, contentlist.size());
                                setAdapter();
                                //if(pageNumber==1){
                                //call this functions when u want to start autoplay on loading async lists (eg firebase)
                                recyclerView.smoothScrollBy(0, 1);
                                recyclerView.smoothScrollBy(0, -1);
                                //  }
                            } else {
                                contentlist.remove(contentlist.size() - 1);
                                adapter.notifyItemRemoved(contentlist.size() - 1);
                                int size =contentlist.size();
                                contentlist.addAll(resultList);
                                adapter.notifyItemRangeChanged(size, contentlist.size());
                            }*/

                        }
                    } else {
                        UtilMethods.INSTANCE.apiErrorHandle(requireActivity(), response.code(), response.message());
                        // Toast.makeText(requireActivity(), "Failed to fetch content", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BasicListResponse<StoryResult>> call, @NonNull Throwable t) {
                    UtilMethods.INSTANCE.apiFailureError(requireActivity(), t);
                    //Toast.makeText(requireActivity(), "API call failed: " + t.getMessage()+"", Toast.LENGTH_SHORT).show();
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireActivity(), "API call failed: " + e.getMessage() + "", Toast.LENGTH_SHORT).show();
            if (loader != null) {
                if (loader.isShowing())
                    loader.dismiss();
            }
        }
    }

  /*  private void postContent(String postId, int typeId, String content, String caption, @Nullable File file) {
        loader.show();
        EndPointInterface service = ApiClient.getClient().create(EndPointInterface.class);
        RequestBody postIdPart = createPartFromString(postId);
        RequestBody typeIdPart = createPartFromString(String.valueOf(typeId));
        RequestBody contentPart = createPartFromString(content == null ? "" : content);
        RequestBody captionPart = createPartFromString(caption == null ? "" : caption);

        MultipartBody.Part extraParam = null;

        if (file != null) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            if (file.getName().endsWith(".mp4")) {
                mimeType = "video/mp4";
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            extraParam = MultipartBody.Part.createFormData("ExtraParam", file.getName(), requestFile);
        }
        Call<LoginResponse> call = service.postContent(
                "Bearer " + tokenManager.getAccessToken(),
                postIdPart,
                typeIdPart,
                contentPart,
                captionPart,
                extraParam
        );
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (loader != null) {
                    if (loader.isShowing())
                        loader.dismiss();
                }
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        if (loginResponse.getStatusCode() == 1) {
                            UtilMethods.INSTANCE.SuccessfulWithDismiss(requireActivity(), loginResponse.getResponseText());
                        } else {
                            UtilMethods.INSTANCE.Error(requireActivity(), loginResponse.getResponseText());
                        }
                    }
                } else {
                    Log.d("API Error", "Error Response: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (loader != null) {
                    if (loader.isShowing())
                        loader.dismiss();
                }
                Log.e("API Failure", "Failure due to: " + t.getMessage());
            }
        });
    }

    private RequestBody createPartFromString(String description) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), description);
    }*/
   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedMediaUri = data.getData();
            if (selectedMediaUri != null) {
                String filePath = FileUtils.getPath(requireActivity(), selectedMediaUri);
            File file = new File(filePath);
            postContent("0", requestCode, "12", "45", file);
          }
        }
    }*/

    ActivityResultLauncher<Intent> postActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refresh();
                }
            });


    ActivityResultLauncher<Intent> storyActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refreshStory();
                }
            });

    ActivityResultLauncher<Intent> profileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    int refreshType = result.getData().getIntExtra("RefreshType", 0);
                    if (refreshType == 1) {
                        //UserDetails
                        getUserDetail(pageId, isProfile);
                        refresh();
                    } else if (refreshType == 2) {
                        //Story
                        refreshStory();
                    } else {
                        //Post
                        refresh();
                    }


                }
            });

    public void refresh() {
       /*recyclerView.pauseVideo();
        recyclerView.destroyVideo();*/
        pageNumber = 1;
        showContent(true);
    }

    @Override
    public void onDestroy() {
        recyclerView.destroyVideo();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        recyclerView.pauseVideo();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (!isAdded()) return;
        getUserDetail(pageId, isProfile);
        MainActivity main = (MainActivity) getActivity();
        if (main == null) return;
        if (!main.fromNotification) {
            main.postId = "";
        }
        main.fromNotification = false;
        refresh();
        recyclerView.playVideo();
        super.onResume();
    }

    public void refreshStory() {
        getStory(true);
    }
}

