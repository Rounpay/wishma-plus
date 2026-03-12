package com.infotech.wishmaplus.Adapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Object.StoryResult;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.zego.LivePageActivity;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private RequestOptions requestOptionsUserImage, requestOptionsUserIconSquare;
    private RequestOptions requestOptionsImage;
    private final ArrayList<StoryResult> storyList;
    FragmentActivity context;
    public PreferencesManager tokenManager;

    // Define view types


    private static final int VIEW_TYPE_STORY = 0;
    public static final int VIEW_TYPE_CREATE = 1;

    public static final int LIVE_STREAM = 4;
    public static final int VIEW_TYPE_LOADING = 2;
    ClickCallBack mClickCallBack;
    UserDetailResponse userDetailResponse;


    public StoryAdapter(ArrayList<StoryResult> storyList, FragmentActivity context, UserDetailResponse userDetailResponse, ClickCallBack mClickCallBack) {
        if (requestOptionsUserImage == null) {
            requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
        }
        if (requestOptionsUserIconSquare == null) {
            requestOptionsUserIconSquare = UtilMethods.INSTANCE.getRequestOption_With_UserIcon_square();
        }
        if (requestOptionsImage == null) {
            requestOptionsImage = UtilMethods.INSTANCE.getRequestOption_With_PlaceHolder();
        }

        this.storyList = storyList;
        this.userDetailResponse = userDetailResponse;
        this.context = context;
        this.mClickCallBack = mClickCallBack;
        tokenManager = new PreferencesManager(context, 1);


    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return storyList.get(position).getContentTypeId();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_LOADING) {
            View view = inflater.inflate(R.layout.adapter_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        else if (viewType == VIEW_TYPE_CREATE) {
            View view = inflater.inflate(R.layout.adapter_story_create, parent, false);
            return new CreateStoryViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_STORY) {
            View view = inflater.inflate(R.layout.adapter_story, parent, false);
            return new StoryViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.adapter_nothing, parent, false);
            return new NothingHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoryResult content = storyList.get(position);

        if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).bind(content);
        }
        else if (holder instanceof CreateStoryViewHolder) {
            ((CreateStoryViewHolder) holder).bind(content);
        } else if (holder instanceof StoryViewHolder) {
            ((StoryViewHolder) holder).bind(content, position);
        } else {
            ((NothingHolder) holder).bind(content, position);
        }
    }


    class LoadingViewHolder extends RecyclerView.ViewHolder {


        public LoadingViewHolder(View itemView) {
            super(itemView);

        }

        public void bind(StoryResult content) {


        }
    }

    class StoryViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView profile, image,liveStreamProfile,liveStreamImage;
        AppCompatTextView user_name, content,liveStreamName,liveStreamContent;
        View parentView;
        ConstraintLayout storyInfo,liveStreamInfo;



        public StoryViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            profile = itemView.findViewById(R.id.profile);
            image = itemView.findViewById(R.id.image);
            user_name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            storyInfo = itemView.findViewById(R.id.storyInfo);
            liveStreamInfo = itemView.findViewById(R.id.liveStreamInfo);
            liveStreamProfile = itemView.findViewById(R.id.liveStreamProfile);
            liveStreamName = itemView.findViewById(R.id.liveStreamName);
            liveStreamContent = itemView.findViewById(R.id.liveStreamContent);
            liveStreamImage = itemView.findViewById(R.id.liveStreamImage);

        }

        public void bind(StoryResult result, int position) {

            liveStreamInfo.setVisibility(View.GONE);
                if (result.getStories() != null && result.getStories().size() > 0) {

                    Glide.with(context)
                            .load(result.getProfilePictureUrl())
                            .apply(requestOptionsUserImage)
                            .into(liveStreamProfile);


                    liveStreamName.setText(result.getFirstName() + " " + result.getLastName());

                    if(result.getStories().get(0).getContentTypeId()==LIVE_STREAM){
                        liveStreamInfo.setVisibility(View.VISIBLE);
                        storyInfo.setVisibility(View.GONE);

                        parentView.setOnClickListener(v -> {

                            requestPermissionIfNeeded(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), new RequestCallback() {
                                @Override
                                public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                                    if (allGranted) {
                                        Toast.makeText(context, "All permissions have been granted.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(context, LivePageActivity.class);
                            intent.putExtra("userID", tokenManager.getUserId());
                            intent.putExtra("userName", tokenManager.getUserName());
                            intent.putExtra("roomID", result.getStories().get(0).getRoomId());
                            intent.putExtra("isHost", false);
                            intent.putExtra("isMicEnabled", false);
                            intent.putExtra("isCameraEnabled", false);
                            intent.putExtra("isFrontCamera", false);
                            context.startActivity(intent);
                                    } else {
                                        Toast.makeText(context, "Some permissions have not been granted.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        });
                    }else {

                        Glide.with(context)
                                .load(result.getProfilePictureUrl())
                                .apply(requestOptionsUserImage)
                                .into(profile);


                        user_name.setText(result.getFirstName() + " " + result.getLastName());

                        if (result.getStories().get(0).getContentTypeId() == UtilMethods.INSTANCE.VIDEO_TYPE || result.getStories().get(0).getContentTypeId() == UtilMethods.INSTANCE.IMAGE_TYPE) {
                            content.setVisibility(View.GONE);
                            image.setVisibility(View.VISIBLE);
                            Glide.with(context)
                                    .load(result.getStories().get(0).getPostContent())
                                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .apply(requestOptionsImage)
                                    .into(image);
                        } else {
                            content.setVisibility(View.VISIBLE);
                            image.setVisibility(View.GONE);
                            content.setText(result.getStories().get(0).getPostContent());
                        }
                        parentView.setOnClickListener(view -> {
                            if(mClickCallBack!=null){
                                mClickCallBack.onOpenStory(new ArrayList<>(storyList.subList(1,storyList.size())),position - 1,result);
                            }
               /* startActivity(new Intent(context, StoryViewActivity.class)
                        .putParcelableArrayListExtra("List", new ArrayList<>(storyList.subList(1,storyList.size())))
                        .putExtra("SelectedPosition", position - 1)
                        .putExtra("Data", result));*/
                        });

                    }

                }










        }
    }

    private void requestPermissionIfNeeded(List<String> permissions, RequestCallback requestCallback) {
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }
        if (allGranted) {
            requestCallback.onResult(true, permissions, new ArrayList<>());
            return;
        }

        PermissionX.init(context).permissions(permissions).onExplainRequestReason((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(Manifest.permission.CAMERA)) {
                    message = context.getString(R.string.permission_explain_camera);
                } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                    message = context.getString(R.string.permission_explain_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(Manifest.permission.CAMERA)) {
                        message = context.getString(R.string.permission_explain_camera);
                    } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                        message = context.getString(R.string.permission_explain_mic);
                    }
                } else {
                    message = context.getString(R.string.permission_explain_camera_mic);
                }
            }
            scope.showRequestReasonDialog(deniedList, message, context.getString(R.string.ok));
        }).onForwardToSettings((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(Manifest.permission.CAMERA)) {
                    message = context.getString(R.string.settings_camera);
                } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                    message = context.getString(R.string.settings_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(Manifest.permission.CAMERA)) {
                        message = context.getString(R.string.settings_camera);
                    } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                        message = context.getString(R.string.settings_mic);
                    }
                } else {
                    message = context.getString(R.string.settings_camera_mic);
                }
            }
            scope.showForwardToSettingsDialog(deniedList, message, context.getString(R.string.settings),
                    context.getString(R.string.cancel));
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

    class CreateStoryViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView profile;
        View parentView;


        public CreateStoryViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            profile = itemView.findViewById(R.id.profile);

        }

        public void bind(StoryResult content) {
            /*if(videoHolder!=null && !videoHolder.isPlaying()) {
                videoHolder = null;
            }*/
            if (userDetailResponse != null) {
                Glide.with(context)
                        .load(userDetailResponse.getProfilePictureUrl())
                        .apply(requestOptionsUserIconSquare)
                        .into(profile);

            }


            parentView.setOnClickListener(view -> {
                if (mClickCallBack != null) {
                    mClickCallBack.onClickCreateStory("0");
                }
            });


        }
    }

    class NothingHolder extends RecyclerView.ViewHolder {


        public NothingHolder(View itemView) {
            super(itemView);

        }

        public void bind(StoryResult content, int position) {
            /*if(videoHolder!=null && !videoHolder.isPlaying()) {
                videoHolder = null;
            }*/

        }
    }



    public interface ClickCallBack {
        void onClickCreateStory(String storyId);
        void onOpenStory(ArrayList<StoryResult> list, int position, StoryResult result);

    }
}





