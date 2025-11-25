package com.infotech.wishmaplus.Fragments;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Activity.PostActivity;
import com.infotech.wishmaplus.Adapter.MultiContentAdapter;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Object.StoryResult;
import com.infotech.wishmaplus.Api.Response.ContentResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoFragment extends Fragment {
    CustomRecyclerView recyclerView;
    private PreferencesManager tokenManager;
    ArrayList<ContentResult> contentlist = new ArrayList<>();
    MultiContentAdapter adapter;
    CustomLoader loader;
    int pageNumber = 1;
    int totalPost = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        loader = ((MainActivity)requireActivity()).loader;
        if(loader==null) {
            loader = new CustomLoader(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        }
        tokenManager = ((MainActivity)requireActivity()).tokenManager;
        if(tokenManager==null) {
            tokenManager = new PreferencesManager(requireActivity(),1);
        }
        final SwipeRefreshLayout pullToRefresh = v.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            refresh();
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
        recyclerView.setDownloadPath(requireActivity().getExternalCacheDir() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

        recyclerView.setVisiblePercent(70); // percentage of View that needs to be visible to start playing


        //call this functions when u want to start autoplay on loading async lists (eg firebase)
        /*recyclerView.smoothScrollBy(0, 1);
        recyclerView.smoothScrollBy(0, -1);*/
        recyclerView.setScrollListener((dx, dy) -> {
            // Check if the user is scrolling downwards
            if (dy > 0 && contentlist.get(contentlist.size() - 1).getContentTypeId() != MultiContentAdapter.VIEW_TYPE_LOADING && contentlist.size() < totalPost) {
                // Get the current item count and the position of the last visible item
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                // If we are at the end of the list and more data is available, load more data
                if ((visibleItemCount + lastVisibleItemPosition) >= totalItemCount) {
                    contentlist.add(new ContentResult(MultiContentAdapter.VIEW_TYPE_LOADING, null,null));
                    adapter.notifyItemInserted(contentlist.size());
                    pageNumber = pageNumber + 1;
                    showContent(false);
                    //loadData(++currentPage);  // Increment page and load next set of data
                }
            }
        });
       /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the user is scrolling downwards
                if (dy > 0 && contentlist.get(contentlist.size() - 1).getContentTypeId() != MultiContentAdapter.VIEW_TYPE_LOADING && contentlist.size() < totalPost) {
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

        return v;
    }

    private void setAdapter() {
        adapter = new MultiContentAdapter("0", contentlist, recyclerView, tokenManager, requireActivity(), new MultiContentAdapter.ClickCallBack() {
            @Override
            public void onClickCreatePost(String postId) {

                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("userData", UtilMethods.INSTANCE.getUserDetailResponse(tokenManager));
                intent.putExtra("postId", postId);
                intent.putExtra("postType", 1);
                postActivityResultLauncher.launch(intent);

            }

            @Override
            public void onClickCreateStory(String storyId) {

            }

            @Override
            public void onClickProfile(String userId) {

            }

            @Override
            public void onOpenStory(ArrayList<StoryResult> list, int position, StoryResult result) {

            }

            @Override
            public void onDelete(int position) {
                contentlist.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeRemoved(position,contentlist.size());
                totalPost=totalPost-1;
            }
        });

        recyclerView.setAdapter(adapter);
    }


    private void showContent(boolean isFromRefresh) {
        try {
            EndPointInterface git = ApiClient.getClient().create(EndPointInterface.class);
            Call<ContentResponse> call = git.getContent("Bearer " + tokenManager.getAccessToken(), "", pageNumber, 20, false, 2);
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
                                if(isFromRefresh){
                                    recyclerView.pauseVideo();
                                    recyclerView.destroyVideo();
                                }
                                contentlist = new ArrayList<>();
                                contentlist.addAll(resultList);
                                //adapter.notifyItemRangeChanged(0, contentlist.size());
                                setAdapter();
                               // if(pageNumber==1){
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
                            }
                        }
                    } else {
                        UtilMethods.INSTANCE.apiErrorHandle(requireActivity(),response.code(), response.message());
                        //Toast.makeText(getContext(), "Failed to fetch content", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ContentResponse> call, @NonNull Throwable t) {
                    UtilMethods.INSTANCE.apiFailureError(requireActivity(),t);
                    //Toast.makeText(getContext(), "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    if (loader != null) {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (loader != null) {
                if (loader.isShowing())
                    loader.dismiss();
            }
        }
    }

    ActivityResultLauncher<Intent> postActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refresh();
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
        recyclerView.playVideo();
        super.onResume();
    }


}
