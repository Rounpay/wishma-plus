package com.infotech.wishmaplus.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.infotech.wishmaplus.Api.Object.StoryResult;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private RequestOptions requestOptionsUserImage, requestOptionsUserIconSquare;
    private RequestOptions requestOptionsImage;
    private final ArrayList<StoryResult> storyList;
    FragmentActivity context;

    // Define view types


    private static final int VIEW_TYPE_STORY = 0;
    public static final int VIEW_TYPE_CREATE = 1;

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
        } else if (viewType == VIEW_TYPE_CREATE) {
            View view = inflater.inflate(R.layout.adapter_story_create, parent, false);
            return new CreateStoryViewHolder(view);
        } else if (viewType == VIEW_TYPE_STORY) {
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
        } else if (holder instanceof CreateStoryViewHolder) {
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
        AppCompatImageView profile, image;
        AppCompatTextView user_name, content;
        View parentView;

        public StoryViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            profile = itemView.findViewById(R.id.profile);
            image = itemView.findViewById(R.id.image);

            user_name = itemView.findViewById(R.id.name);

            content = itemView.findViewById(R.id.content);

        }

        public void bind(StoryResult result, int position) {


            Glide.with(context)
                    .load(result.getProfilePictureUrl())
                    .apply(requestOptionsUserImage)
                    .into(profile);


            user_name.setText(result.getFirstName() + " " + result.getLastName());

            if (result.getStories() != null && result.getStories().size() > 0) {
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





