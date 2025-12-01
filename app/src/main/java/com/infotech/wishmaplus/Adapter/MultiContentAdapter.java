package com.infotech.wishmaplus.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Activity.ImageZoomViewActivity;
import com.infotech.wishmaplus.Activity.MainActivity;
import com.infotech.wishmaplus.Activity.ProfileActivity;
import com.infotech.wishmaplus.Activity.VideoViewActivity;
import com.infotech.wishmaplus.Adapter.Interfaces.CountChangeCallBack;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Object.StoryResult;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Fragments.ShareDialogFragment;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.ApiClient;
import com.infotech.wishmaplus.Utils.ApplicationConstant;
import com.infotech.wishmaplus.Utils.CommentDialog;
import com.infotech.wishmaplus.Utils.EndPointInterface;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.Utils.Utility;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.CustomRecyclerView;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.DownloadManagerService;
import com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo.VideoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultiContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int screenWidth;
    private final String userId, UserUnikID;
    private final AudioManager am;
    private RequestOptions requestOptionsUserImage = null;
    private RequestOptions requestOptionsImage, requestOptionsCoverImage, requestOptionsVideo;
    private final List<ContentResult> contentList;
    private final PreferencesManager tokenManager;
    FragmentActivity context;

    // Define view types
    public static final int VIEW_TYPE_LOADING = 111;
    public static final int VIEW_TYPE_PROFILE = 222;
    public static final int VIEW_TYPE_POST = 333;

    private static final int VIEW_TYPE_TEXT = 1;
    private static final int VIEW_TYPE_VIDEO = 2;
    private static final int VIEW_TYPE_IMAGE = 3;
    ClickCallBack clickCallBack;
    private Dialog alertDialogComment;
    private CustomRecyclerView mRecyclerView;
    private boolean isMute;
    int videoMaxHeight;

    private CommentDialog commentDialog;
    private String isFollowed;
    private int requestSentStatus;
    private boolean isRequestPending;

    public MultiContentAdapter(String UserUnikID, List<ContentResult> contentList, CustomRecyclerView recyclerView, PreferencesManager tokenManager, FragmentActivity context, ClickCallBack clickCallBack) {
        if (requestOptionsUserImage == null) {
            requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
        }
        if (requestOptionsImage == null) {
            requestOptionsImage = UtilMethods.INSTANCE.getRequestOption_With_PlaceHolder();
        }
        if (requestOptionsCoverImage == null) {
            requestOptionsCoverImage = UtilMethods.INSTANCE.getRequestOption_With_CoverImage();
        }

        if (requestOptionsVideo == null) {
            requestOptionsVideo = UtilMethods.INSTANCE.getRequestOption_WithOut_PlaceHolder();
        }
        this.mRecyclerView = recyclerView;
        this.clickCallBack = clickCallBack;
        this.contentList = contentList;
        this.tokenManager = tokenManager;
        this.context = context;
        commentDialog = new CommentDialog(context, tokenManager);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        //videoMaxHeight = (int) context.getResources().getDimension(com.intuit.sdp.R.dimen._380sdp);
        videoMaxHeight = (int) (displayMetrics.heightPixels / 1.4);
        this.userId = tokenManager.getUserId();
        this.UserUnikID = UserUnikID;

        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return contentList.get(position).getContentTypeId() > 0 ? contentList.get(position).getContentTypeId() : (context instanceof ProfileActivity) ? VIEW_TYPE_PROFILE : VIEW_TYPE_POST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_LOADING) {
            View view = inflater.inflate(R.layout.adapter_loading, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == VIEW_TYPE_POST) {
            View view = inflater.inflate(R.layout.adapter_post, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_TYPE_PROFILE) {
            View view = inflater.inflate(R.layout.adapter_user_details, parent, false);
            return new ProfileViewHolder(view);
        } else if (viewType == VIEW_TYPE_TEXT) {
            View view = inflater.inflate(R.layout.adapter_text, parent, false);
            return new TextViewHolder(view);
        } else if (viewType == VIEW_TYPE_VIDEO) {
            View view = inflater.inflate(R.layout.adapter_video, parent, false);
            return new VideoViewHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE) {
            View view = inflater.inflate(R.layout.adapter_photo, parent, false);
            return new ImageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.adapter_nothing, parent, false);
            return new NothingHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContentResult content = contentList.get(position);

        if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).bind(content);
        } else if (holder instanceof PostViewHolder) {
            ((PostViewHolder) holder).bind(content);
        } else if (holder instanceof ProfileViewHolder) {
            ((ProfileViewHolder) holder).bind(content, position);
        } else if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bind(content, position);
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).bind(content, position);
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(content, position);
        } else {
            ((NothingHolder) holder).bind(content, position);
        }
    }


    class LoadingViewHolder extends RecyclerView.ViewHolder {


        public LoadingViewHolder(View itemView) {
            super(itemView);

        }

        public void bind(ContentResult content) {


        }
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView profile_picture, profile, cover_photo, editProfileIcon, packageImage;
        ImageButton editCoverIcon;

        View line_1;
        /*MaterialButtonadd_story_button,edit_profile_button;*/ AppCompatTextView addPostTitle, user_name, storyAddBtn, packageTitle, packageName, bioTv, location, posts_tab, photos_tab, videos_tab, noDataTv, searchBar, edit_public_details, joiningDate, followers, subscribers, followersView, followingView, friendUnfriend, addFriend;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            profile_picture = itemView.findViewById(R.id.profile_picture);
            profile = itemView.findViewById(R.id.profile);
            packageImage = itemView.findViewById(R.id.packageImage);
            packageTitle = itemView.findViewById(R.id.packageTitle);
            packageName = itemView.findViewById(R.id.packageName);
            cover_photo = itemView.findViewById(R.id.cover_photo);
            editProfileIcon = itemView.findViewById(R.id.edit_profile_icon);
            editCoverIcon = itemView.findViewById(R.id.edit_cover_icon);
            followersView = itemView.findViewById(R.id.followersView);
            followingView = itemView.findViewById(R.id.followingView);
            friendUnfriend = itemView.findViewById(R.id.friendUnfriend);
            addFriend = itemView.findViewById(R.id.addFriend);
            user_name = itemView.findViewById(R.id.user_name);
            addPostTitle = itemView.findViewById(R.id.addPostTitle);
            line_1 = itemView.findViewById(R.id.line_1);
            bioTv = itemView.findViewById(R.id.bioTv);
            location = itemView.findViewById(R.id.location);
            posts_tab = itemView.findViewById(R.id.posts_tab);
            photos_tab = itemView.findViewById(R.id.photos_tab);
            videos_tab = itemView.findViewById(R.id.videos_tab);
            edit_public_details = itemView.findViewById(R.id.edit_public_details);
            noDataTv = itemView.findViewById(R.id.noDataTv);
            searchBar = itemView.findViewById(R.id.search_bar);
            storyAddBtn = itemView.findViewById(R.id.storyAddBtn);
            joiningDate = itemView.findViewById(R.id.joiningDate);
            followers = itemView.findViewById(R.id.followers);
            subscribers = itemView.findViewById(R.id.subscribers);
        }

        @SuppressLint("SetTextI18n")
        public void bind(ContentResult content, int position) {
            /*if(videoHolder!=null && !videoHolder.isPlaying()) {
                videoHolder = null;
            }*/

            if (contentList.size() > 1) {
                noDataTv.setVisibility(GONE);
            } else {
                if (context instanceof ProfileActivity && ((ProfileActivity) context).buttonContentTypeId == 2) {
                    noDataTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_video_library_big, 0, 0);
                    noDataTv.setText(R.string.video_is_not_available);
                } else if (context instanceof ProfileActivity && ((ProfileActivity) context).buttonContentTypeId == 3) {
                    noDataTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_image_big, 0, 0);
                    noDataTv.setText(R.string.photo_is_not_available);
                } else {
                    noDataTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_post_add_big, 0, 0);
                    noDataTv.setText(R.string.post_is_not_available);
                }
                noDataTv.setVisibility(VISIBLE);
            }
            boolean isProfessional = content.getUserDetail().isProfessional();
            if (isProfessional) {
                friendUnfriend.setVisibility(VISIBLE);
            } else {
                friendUnfriend.setVisibility(GONE);
            }
            if (content.getUserDetail() != null) {
                Glide.with(context).load(content.getUserDetail().getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile_picture);
                Glide.with(context).load(content.getUserDetail().getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile);

                Glide.with(context).load(content.getUserDetail().getCoverPictureUrl()).apply(requestOptionsCoverImage).into(cover_photo);
                user_name.setText(content.getUserDetail().getFisrtName() + " " + content.getUserDetail().getLastName());


                // FOLLOWERS
                if (content.getUserDetail().getFollower() != null && !content.getUserDetail().getFollower().isEmpty()) {
                    String count = content.getUserDetail().getFollower();
                    String label = " followers";
                    SpannableString ss = new SpannableString(count + label);
                    ss.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new AbsoluteSizeSpan(16, true), 0, count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    followersView.setText(ss);
                }
                // FOLLOWING
                if (content.getUserDetail().getFollowing() != null && !content.getUserDetail().getFollowing().isEmpty()) {

                    String count = content.getUserDetail().getFollowing();
                    String label = " following";

                    SpannableString ss = new SpannableString(count + label);
                    ss.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new AbsoluteSizeSpan(16, true), 0, count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    followingView.setText(ss);
                }
                isFollowed = content.getUserDetail().getIsFollowed();
                requestSentStatus = content.getUserDetail().getRequestSentStatus();
                isRequestPending = content.getUserDetail().isRequestPending();

                /* Showing requestSentStatus  */
                if (isRequestPending && requestSentStatus == 0) {
                    addFriend.setText("Respond"); /* showing Respond */
                    addFriend.setBackgroundResource(R.drawable.bg_blue_rounded);
                    addFriend.setTextColor(ContextCompat.getColor(context, R.color.color_white));
                    addFriend.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.main_blue_color));
                } else if (requestSentStatus == 0 || requestSentStatus == 3) {
                    addFriend.setText("Add Friend");  /* showing Add Friend */
                    addFriend.setBackgroundResource(R.drawable.bg_blue_rounded);
                    addFriend.setTextColor(ContextCompat.getColor(context, R.color.color_white));
                    addFriend.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.main_blue_color));
                } else if (requestSentStatus == 1) {
                    addFriend.setText("Cancel Request");/* showing Cancel Request */
                    addFriend.setBackgroundResource(R.drawable.rounded_corners);
                    addFriend.setTextColor(ContextCompat.getColor(context, R.color.black_alpha_55));
                    addFriend.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey_1));
                } else if (requestSentStatus == 2) {
                    addFriend.setText("Friends");/* showing Friends */
                    addFriend.setBackgroundResource(R.drawable.bg_blue_rounded);
                    addFriend.setTextColor(ContextCompat.getColor(context, R.color.color_white));
                    addFriend.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.main_blue_color));
                }
                /* Showing requestSentStatus  */

                if ("0".equals(isFollowed)) {
                    friendUnfriend.setText("Follow");
                    friendUnfriend.setBackgroundResource(R.drawable.bg_blue_rounded);
                    friendUnfriend.setTextColor(ContextCompat.getColor(context, R.color.color_white));
                    friendUnfriend.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.main_blue_color));
                } else {
                    friendUnfriend.setText("Following");
                    friendUnfriend.setBackgroundResource(R.drawable.rounded_corners);
                    friendUnfriend.setTextColor(ContextCompat.getColor(context, R.color.black_alpha_55));
                    friendUnfriend.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey_1));
                }
                addFriend.setOnClickListener(v -> {
                    if (requestSentStatus == 1) {
                        UtilMethods.INSTANCE.openAcceptRequestBottomSheetDialog(context, content.getUserDetail().getUserId(),content.getUserDetail().getUserId(), new UtilMethods.ApiCallBackMulti() {
                            @Override
                            public void onSuccess(Object object) {
                                BasicResponse basicResponse =(BasicResponse) object;
                                UtilMethods.INSTANCE.Success(context,basicResponse.getResponseText());
                                updateFollowUnfollowState(0, friendUnfriend, position);
                                if (context instanceof ProfileActivity) {
                                    ((ProfileActivity) context).refresh();

                                }
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        }, 0);
                    } else if (requestSentStatus == 0 && isRequestPending) {
                        UtilMethods.INSTANCE.openAcceptRequestBottomSheetDialog(context, content.getUserDetail().getUserId(),content.getUserDetail().getFisrtName(), new UtilMethods.ApiCallBackMulti() {
                            @Override
                            public void onSuccess(Object object) {
                                updateFollowUnfollowState(0, friendUnfriend, position);
                                if (context instanceof ProfileActivity) {
                                    ((ProfileActivity) context).refresh();

                                }
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        }, 3);
                    } else if (requestSentStatus == 0 || requestSentStatus == 3){
                        addFriend(content.getUserDetail().getUserId(), friendUnfriend, position);
                    }else if (requestSentStatus ==2) {
                        UtilMethods.INSTANCE.openAcceptRequestBottomSheetDialog(context, content.getUserDetail().getUserId(),content.getUserDetail().getFisrtName(), new UtilMethods.ApiCallBackMulti() {
                            @Override
                            public void onSuccess(Object object) {
                                updateFollowUnfollowState(0, friendUnfriend, position);
                                if (context instanceof ProfileActivity) {
                                    ((ProfileActivity) context).refresh();

                                }
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        }, 4);
                    }

                });

                friendUnfriend.setOnClickListener(v -> {
                    if ("1".equals(isFollowed)) {
                        UtilMethods.INSTANCE.openAcceptRequestBottomSheetDialog(context, content.getUserDetail().getUserId(), content.getUserDetail().getUserId(),new UtilMethods.ApiCallBackMulti() {
                            @Override
                            public void onSuccess(Object object) {
                                int statusCode = (int) object;
                                updateFollowUnfollowState(statusCode, friendUnfriend, position);
                                if (context instanceof ProfileActivity) {
                                    ((ProfileActivity) context).refresh();

                                }
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        }, 1);
                    } else {
                        followUser(content.getUserDetail().getUserId(), friendUnfriend, position);
                    }

                });

                if (content.getUserDetail().getBio() != null && !content.getUserDetail().getBio().isEmpty()) {
                    bioTv.setVisibility(VISIBLE);
                    bioTv.setText(content.getUserDetail().getBio());
                } else {
                    bioTv.setVisibility(GONE);
                }

                if (content.getUserDetail().getCityName() != null && !content.getUserDetail().getCityName().isEmpty()) {
                    location.setVisibility(VISIBLE);
                    location.setText(Html.fromHtml(context.getResources().getString(R.string.lives_in, content.getUserDetail().getCityName(), content.getUserDetail().getStateName()), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    location.setVisibility(GONE);
                }

                joiningDate.setText("Joined on " + Utility.INSTANCE.formatedDateMonthYear(content.getUserDetail().getJoiningDate()));
                if (content.getUserDetail().getTotalDownline() > 0) {
                    followers.setVisibility(VISIBLE);
                    followers.setText("Followed by " + content.getUserDetail().getTotalDownline() + " people");
                } else {
                    followers.setVisibility(GONE);
                }

                if (content.getUserDetail().getPaidDownline() > 0) {
                    subscribers.setVisibility(VISIBLE);
                    subscribers.setText("Subscribed by " + content.getUserDetail().getPaidDownline() + " people");
                } else {
                    subscribers.setVisibility(GONE);
                }

                if (content.getUserDetail().getPackageDetail() != null) {
                    packageImage.setVisibility(VISIBLE);
                    packageName.setVisibility(VISIBLE);
                    packageTitle.setVisibility(VISIBLE);
                    Glide.with(context).load(content.getUserDetail().getPackageDetail().getImageUrl()).apply(requestOptionsImage).into(packageImage);
                    packageName.setText(content.getUserDetail().getPackageDetail().getPackageName() + " (" + Utility.INSTANCE.formattedAmountWithRupees(content.getUserDetail().getPackageDetail().getPackageCost()) + ")");
                } else {
                    packageImage.setVisibility(GONE);
                    packageName.setVisibility(GONE);
                    packageTitle.setVisibility(GONE);
                }
            }
            editProfileIcon.setOnClickListener(view -> {
                if (context instanceof ProfileActivity) {
                    ((ProfileActivity) context).selectProfileImage();
                }

            });
            editCoverIcon.setOnClickListener(view -> {
                if (context instanceof ProfileActivity) {
                    ((ProfileActivity) context).selectCoverImage();
                }

            });

            if (!content.getUserDetail().getUserId().equals(userId)) {
                edit_public_details.setVisibility(GONE);
                line_1.setVisibility(GONE);
                addPostTitle.setVisibility(GONE);
                storyAddBtn.setVisibility(GONE);
                profile.setVisibility(GONE);
                searchBar.setVisibility(GONE);
                // friendUnfriend.setVisibility(VISIBLE);
                addFriend.setVisibility(VISIBLE);

            }
            edit_public_details.setOnClickListener(view -> {
                if (context instanceof ProfileActivity) {
                    ((ProfileActivity) context).updateUserDetails();
                }

            });

            if (context instanceof ProfileActivity && ((ProfileActivity) context).buttonContentTypeId == 2) {
                ViewCompat.setBackgroundTintList(posts_tab, context.getColorStateList(R.color.bgColor));
                ViewCompat.setBackgroundTintList(photos_tab, context.getColorStateList(R.color.bgColor));
                ViewCompat.setBackgroundTintList(videos_tab, context.getColorStateList(R.color.light_blue));
                posts_tab.setTextColor(context.getColorStateList(R.color.grey_6));
                photos_tab.setTextColor(context.getColorStateList(R.color.grey_6));
                videos_tab.setTextColor(context.getColorStateList(R.color.colorPrimaryLight));
            } else if (context instanceof ProfileActivity && ((ProfileActivity) context).buttonContentTypeId == 3) {
                ViewCompat.setBackgroundTintList(posts_tab, context.getColorStateList(R.color.bgColor));
                ViewCompat.setBackgroundTintList(photos_tab, context.getColorStateList(R.color.light_blue));
                ViewCompat.setBackgroundTintList(videos_tab, context.getColorStateList(R.color.bgColor));
                posts_tab.setTextColor(context.getColorStateList(R.color.grey_6));
                photos_tab.setTextColor(context.getColorStateList(R.color.colorPrimaryLight));
                videos_tab.setTextColor(context.getColorStateList(R.color.grey_6));
            } else {
                ViewCompat.setBackgroundTintList(posts_tab, context.getColorStateList(R.color.light_blue));
                ViewCompat.setBackgroundTintList(photos_tab, context.getColorStateList(R.color.bgColor));
                ViewCompat.setBackgroundTintList(videos_tab, context.getColorStateList(R.color.bgColor));
                posts_tab.setTextColor(context.getColorStateList(R.color.colorPrimaryLight));
                photos_tab.setTextColor(context.getColorStateList(R.color.grey_6));
                videos_tab.setTextColor(context.getColorStateList(R.color.grey_6));
            }

            posts_tab.setOnClickListener(view -> {
                if (context instanceof ProfileActivity) {
                    ((ProfileActivity) context).buttonContentTypeId = 0;
                    //notifyItemChanged(0);
                    ((ProfileActivity) context).refresh();
                }

            });
            photos_tab.setOnClickListener(view -> {
                if (context instanceof ProfileActivity) {

                    ((ProfileActivity) context).buttonContentTypeId = 3;
                    // notifyItemChanged(0);
                    ((ProfileActivity) context).refresh();
                }

            });
            videos_tab.setOnClickListener(view -> {

                if (context instanceof ProfileActivity) {
                    ((ProfileActivity) context).buttonContentTypeId = 2;
                    // notifyItemChanged(0);
                    ((ProfileActivity) context).refresh();
                }

            });

            searchBar.setOnClickListener(view -> {
                if (clickCallBack != null) {
                    clickCallBack.onClickCreatePost("0");
                }
            });

            storyAddBtn.setOnClickListener(view -> {
                if (clickCallBack != null) {
                    clickCallBack.onClickCreateStory("0");
                }
            });

        }
    }



    private void followUser(String userId, AppCompatTextView friendUnfriend, int position) {
        UtilMethods.INSTANCE.doFollow(context, userId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                int statusCode = (int) object;
                updateFollowUnfollowState(statusCode, friendUnfriend, position);

            }

            @Override
            public void onError(String msg) {

            }
        });


    }

    private void addFriend(String userId, AppCompatTextView addFriend, int position) {
        UtilMethods.INSTANCE.createRequest(context, userId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                BasicResponse basicResponse = (BasicResponse) object;
                if(basicResponse.getStatusCode()==1){
                    updateFollowUnfollowState(0, addFriend, position);
                }else{
                    UtilMethods.INSTANCE.Error(context,basicResponse.getResponseText());
                }

            }

            @Override
            public void onError(String msg) {

            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void updateFollowUnfollowState(int statusCode, AppCompatTextView friendUnfriend, int position) {
        notifyItemChanged(position);
        if (context instanceof ProfileActivity) {
            ((ProfileActivity) context).refresh();

        }

    }


    class PostViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView profile;

        AppCompatTextView searchBar;
        RecyclerView storyRecyclerView;
        View line2;

        public PostViewHolder(View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            searchBar = itemView.findViewById(R.id.search_bar);
            storyRecyclerView = itemView.findViewById(R.id.storyRecyclerView);
            storyRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            line2 = itemView.findViewById(R.id.line2);
        }

        public void bind(ContentResult content) {
            /*if(videoHolder!=null && !videoHolder.isPlaying()) {
                videoHolder = null;
            }*/
            if (content.getUserDetail() != null) {
                Glide.with(context).load(content.getUserDetail().getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile);

            }
            if (content.getStoryList() != null && content.getStoryList().size() > 0) {
                storyRecyclerView.setVisibility(VISIBLE);
                line2.setVisibility(VISIBLE);
                /*
                 */
                storyRecyclerView.setAdapter(new StoryAdapter(content.getStoryList(), context, content.getUserDetail(), new StoryAdapter.ClickCallBack() {
                    @Override
                    public void onClickCreateStory(String storyId) {
                        if (clickCallBack != null) {
                            clickCallBack.onClickCreateStory(storyId);
                        }
                    }

                    @Override
                    public void onOpenStory(ArrayList<StoryResult> list, int position, StoryResult result) {
                        if (clickCallBack != null) {
                            clickCallBack.onOpenStory(list, position, result);
                        }

                    }
                }));

            } else {
                storyRecyclerView.setVisibility(GONE);
                line2.setVisibility(GONE);
            }

            profile.setOnClickListener(view -> {
                if (clickCallBack != null) {
                    clickCallBack.onClickProfile(content.getUserDetail() != null ? content.getUserDetail().getUserId() : "");
                }
            });
            searchBar.setOnClickListener(view -> {
                if (clickCallBack != null) {
                    clickCallBack.onClickCreatePost("0");
                }
            });

        }
    }

    class NothingHolder extends RecyclerView.ViewHolder {


        public NothingHolder(View itemView) {
            super(itemView);

        }

        public void bind(ContentResult content, int position) {
            /*if(videoHolder!=null && !videoHolder.isPlaying()) {
                videoHolder = null;
            }*/

        }
    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        MaterialButton likeBtn, commentBtn, whatsAppBtn, shareBtn;
        TextView nameTv, nameParentTv, timeParentTv, postParentTxt, timeTv, like_count, comment_count, share_count;

        ImageView profile, moreBTn, profileParent;
        View ownerView;

        public TextViewHolder(View itemView) {
            super(itemView);
            ownerView = itemView.findViewById(R.id.ownerView);
            nameParentTv = itemView.findViewById(R.id.nameParentTv);
            timeParentTv = itemView.findViewById(R.id.timeParentTv);
            postParentTxt = itemView.findViewById(R.id.postParentTxt);
            profileParent = itemView.findViewById(R.id.profileParent);
            moreBTn = itemView.findViewById(R.id.moreBTn);
            nameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            profile = itemView.findViewById(R.id.profile);
            textView = itemView.findViewById(R.id.textView);
            like_count = itemView.findViewById(R.id.like_count);
            comment_count = itemView.findViewById(R.id.comment_count);
            share_count = itemView.findViewById(R.id.share_count);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            whatsAppBtn = itemView.findViewById(R.id.whatsAppBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
        }

        public void bind(ContentResult content, int position) {
            /*if(videoHolder!=null && !videoHolder.isPlaying()) {
                videoHolder = null;
            }*/

           /* if (content.getUserId().equalsIgnoreCase(userId)) {
                moreBTn.setVisibility(View.VISIBLE);
            } else {
                moreBTn.setVisibility(View.GONE);
            }*/


            if (content.getParsedSharedData() != null) {
                ownerView.setVisibility(VISIBLE);
                nameTv.setText(content.getParsedSharedData().getFisrtName() + " " + content.getParsedSharedData().getLastName());
                timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getParsedSharedData().getEntryAt()));
                if (content.getParsedSharedData().getCaption() != null && !content.getParsedSharedData().getCaption().trim().isEmpty()) {
                    textView.setText(content.getParsedSharedData().getCaption().trim());
                    textView.setVisibility(VISIBLE);
                } else {
                    textView.setVisibility(GONE);
                }
                Glide.with(context).load(content.getParsedSharedData().getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile);

                nameParentTv.setText(content.getFisrtName() + " " + content.getLastName());
                timeParentTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getEntryAt()));
                if (content.getCaption() != null && !content.getCaption().trim().isEmpty()) {
                    postParentTxt.setText(content.getCaption().trim());
                    postParentTxt.setVisibility(VISIBLE);
                } else {
                    postParentTxt.setVisibility(GONE);
                }
                Glide.with(context).load(content.getProfilePictureUrl()).apply(requestOptionsUserImage).into(profileParent);
            } else {
                ownerView.setVisibility(GONE);
                nameTv.setText(content.getFisrtName() + " " + content.getLastName());
                timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getEntryAt()));

                Glide.with(context).load(content.getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile);
                textView.setText((content.getPostContent() + "").trim());
            }


            if (content.getTotalLikes() > 0) {
                like_count.setVisibility(VISIBLE);
                like_count.setText(content.getTotalLikes() + "");
            } else {
                like_count.setVisibility(GONE);
            }
            if (content.getTotalComments() > 0) {
                comment_count.setVisibility(VISIBLE);
                comment_count.setText(content.getTotalComments() + " Comments");
            } else {
                comment_count.setVisibility(GONE);
            }
            if (content.getTotalShares() > 0) {
                share_count.setVisibility(VISIBLE);
                share_count.setText(content.getTotalShares() + " Share");
            } else {
                share_count.setVisibility(GONE);
            }

            commentBtn.setOnClickListener(v -> commentDialog.showCommentsDialog(content.getPostId(), size -> {
                comment_count.setText(size + " Comments");
                comment_count.setVisibility(VISIBLE);
                contentList.get(position).setTotalComments(size);

            }/*position, comment_count*/));
            comment_count.setOnClickListener(v -> commentDialog.showCommentsDialog(content.getPostId(), size -> {
                comment_count.setText(size + " Comments");
                comment_count.setVisibility(VISIBLE);
                contentList.get(position).setTotalComments(size);

            }/*position, comment_count*/));


            if (content.isLiked()) {
                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.colorFwd));
                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorFwd));
            } else {
                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.grey_5));
                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.grey_5));
            }


            likeBtn.setOnClickListener(v -> {
                UtilMethods.INSTANCE.triggerLikeApi(context, content.getPostId(), "" /*,!content.isLiked(), likeBtn, like_count, position*/, new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object object) {
                        boolean isLiked = (boolean) object;

                        updateLikeState(/*content,*/ isLiked, position, likeBtn, like_count);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
            });
            moreBTn.setOnClickListener(view -> showPopupMenu(view, content, position, null));
            shareBtn.setOnClickListener(view -> {
                ShareDialogFragment bottomSheetDialogFragment = ShareDialogFragment.newInstance(content, (int typeId) -> {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).refresh(typeId);
                    } else if (context instanceof ProfileActivity) {
                        ((ProfileActivity) context).refresh();

                    }
                });
                if (context instanceof MainActivity) {
                    bottomSheetDialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "ShareBottomSheetDialog");
                } else if (context instanceof ProfileActivity) {
                    bottomSheetDialogFragment.show(((ProfileActivity) context).getSupportFragmentManager(), "ShareBottomSheetDialog");
                }


            });
            whatsAppBtn.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://api.whatsapp.com/send?text=" + ApplicationConstant.INSTANCE.postUrl + content.getPostId()));
                context.startActivity(intent);
            });
            profile.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", content.getUserId());
                context.startActivity(intent);
            });
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    public class VideoViewHolder extends RecyclerView.ViewHolder {

        MaterialButton likeBtn, commentBtn, whatsAppBtn, shareBtn;
        View ownerView;
        TextView nameTv, nameParentTv, timeParentTv, postParentTxt, timeTv, postTxt, like_count, comment_count, share_count;
        public ImageView playBtn, volBtn, profile, profileParent, thumbnail, moreBTn;
        RelativeLayout container;
        /* boolean isMuted;*/
        public PlayerView videoView;
        /*public boolean isPause = false;*/
        // MediaPlayer mediaPlayer;
        ProgressBar progress;
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(
                /* minBufferMs= */ 2000,   // 2 seconds
                /* maxBufferMs= */ 4000,   // 4 seconds
                /* bufferForPlaybackMs= */ 1000,  // 1 second for playback start
                /* bufferForPlaybackAfterRebufferMs= */ 2000  // 2 seconds after rebuffering
        ).build();

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context).setEnableDecoderFallback(true).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

        private AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    if (videoView != null && videoView.getPlayer() != null) {
                        thumbnail.setVisibility(VISIBLE);
                        playBtn.setVisibility(VISIBLE);
                        videoView.getPlayer().pause();
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    if (videoView.getPlayer() != null) {
                        videoView.getPlayer().play();
                    }
                }
            }
        };

        public VideoViewHolder(View itemView) {
            super(itemView);
            ownerView = itemView.findViewById(R.id.ownerView);
            profileParent = itemView.findViewById(R.id.profileParent);
            nameParentTv = itemView.findViewById(R.id.nameParentTv);
            timeParentTv = itemView.findViewById(R.id.timeParentTv);
            postParentTxt = itemView.findViewById(R.id.postParentTxt);
            progress = itemView.findViewById(R.id.progress);
            videoView = itemView.findViewById(R.id.videoView);
            videoView.setPlayer(new ExoPlayer.Builder(context, renderersFactory).setLoadControl(loadControl).build());
            container = itemView.findViewById(R.id.container);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            nameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            postTxt = itemView.findViewById(R.id.postTxt);
            profile = itemView.findViewById(R.id.profile);
            like_count = itemView.findViewById(R.id.like_count);
            comment_count = itemView.findViewById(R.id.comment_count);
            share_count = itemView.findViewById(R.id.share_count);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            moreBTn = itemView.findViewById(R.id.moreBTn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            whatsAppBtn = itemView.findViewById(R.id.whatsAppBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            playBtn = itemView.findViewById(R.id.playBtn);
            volBtn = itemView.findViewById(R.id.volBtn);


        }

        public void bind(ContentResult content, int position) {


            if (content.getHeight() > 0 && content.getWidth() > 0) {
                double aspectRatio = content.getWidth() / content.getHeight();
                double showImageHeight = screenWidth / aspectRatio;
                ViewGroup.LayoutParams params = container.getLayoutParams();
                if (showImageHeight > videoMaxHeight) {
                    params.height = videoMaxHeight; // Set the height in pixels
                } else {
                    params.height = (int) showImageHeight;
                }
                container.setLayoutParams(params);
                Glide.with(context).load(content.getPostContent()).apply(requestOptionsVideo).into(thumbnail);

            } else {
                Glide.with(context).asBitmap().load(content.getPostContent()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).apply(requestOptionsVideo).listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        // Handle error if loading fails
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        // Get the height of the loaded image

                        double imageHeight = resource.getHeight();
                        double imageWidth = resource.getWidth();
                        // Log.d("ImageSize", "Width: " + imageWidth + ", Height: " + imageHeight);
                        double aspectRatio = imageWidth / imageHeight;
                        double showImageHeight = screenWidth / aspectRatio;
                        ViewGroup.LayoutParams params = container.getLayoutParams();
                        if (showImageHeight > videoMaxHeight) {
                            params.height = videoMaxHeight; // Set the height in pixels
                        } else {
                            params.height = (int) showImageHeight;
                        }
                        container.setLayoutParams(params);
                        // thumbnail.setImageBitmap(resource);
                        return false;
                    }
                }).into(thumbnail);
            }


            String savePath = VideoUtils.getString(context, content.getPostContent());
            if (savePath != null && new File(savePath).exists()) {
                Uri uri = Uri.parse(savePath);
                MediaItem mediaItem = MediaItem.fromUri(uri);
                videoView.getPlayer().setMediaItem(mediaItem);
            } else {
                Uri uri = Uri.parse(content.getPostContent());
                MediaItem mediaItem = MediaItem.fromUri(uri);
                videoView.getPlayer().setMediaItem(mediaItem);
                Intent serviceIntent = new Intent(context, DownloadManagerService.class);
                context.startService(serviceIntent);
                new VideoUtils().startDownloadInBackground(context, content.getPostContent(), context.getExternalCacheDir() + "/MyVideo/");
            }

            videoView.getPlayer().prepare();
            //videoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            videoView.getPlayer().setPlayWhenReady(false);
            videoView.getPlayer().addListener(new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isMute && videoView.getPlayer().getVolume() != 0) {
                        videoView.getPlayer().setVolume(0f);
                        volBtn.setImageResource(R.drawable.ic_mute);
                    } else if (!isMute && videoView.getPlayer().getVolume() == 0) {
                        videoView.getPlayer().setVolume(1f);
                        volBtn.setImageResource(R.drawable.ic_unmute);
                    }
                    if (/*am.isMusicActive() && */videoView.getPlayer().getVolume() != 0) {
                        if (isPlaying) {
                            am.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                        } else {
                            am.abandonAudioFocus(focusChangeListener);
                        }
                    }
                    //Player.Listener.super.onIsPlayingChanged(isPlaying);
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        mRecyclerView.playingTimeMap.put(videoView, 0L);
                        videoView.getPlayer().seekTo(0);
                        mRecyclerView.playingPauseMap.put(videoView, false);
                        videoView.getPlayer().play(); // Start playback again
                        mRecyclerView.playingVideoView = videoView;
                        mRecyclerView.thumbnail = thumbnail;
                        mRecyclerView.playBtn = playBtn;
                    } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
                        progress.setVisibility(VISIBLE);
                    } else if (playbackState == ExoPlayer.STATE_READY) {
                        if (mRecyclerView.isScreenPaused() || mRecyclerView.playingPauseMap.containsKey(videoView) && mRecyclerView.playingPauseMap.get(videoView) == true) {
                            if (videoView.getPlayer().getCurrentPosition() > 0) {
                                mRecyclerView.playingTimeMap.put(videoView, videoView.getPlayer().getCurrentPosition());
                            }
                            videoView.getPlayer().pause();
                            thumbnail.setVisibility(VISIBLE);
                            playBtn.setVisibility(VISIBLE);

                        } /*else if (videoView.getPlayer().isPlaying()) {
                            isPause = false;
                            thumbnail.setVisibility(View.GONE);
                            playBtn.setVisibility(View.GONE);
                        }*/
                        progress.setVisibility(GONE);
                    } else {
                        progress.setVisibility(GONE);
                    }
                }
            });
           /* if((position==0 || contentList.get(0).getContentTypeId()==VIEW_TYPE_POST && position==1) && !videoView.getPlayer().isPlaying()) {
                videoView.post(() -> {
                    thumbnail.setVisibility(View.GONE);
                    playBtn.setVisibility(View.GONE);
                    videoView.getPlayer().play();
                    mRecyclerView.playingVideoView = videoView;
                    mRecyclerView.thumbnail = thumbnail;
                    mRecyclerView.playBtn = playBtn;
                });

            }*/

            if (isMute) {
                videoView.getPlayer().setVolume(0f);
                volBtn.setImageResource(R.drawable.ic_mute);
            } else {
                videoView.getPlayer().setVolume(1f);
                volBtn.setImageResource(R.drawable.ic_unmute);
            }

            //optional - true by default
            container.setOnClickListener(view -> {
                VideoViewActivity.callBack = new CountChangeCallBack() {
                    @Override
                    public void onRefresh(int typeId) {
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).refresh(typeId);
                        } else if (context instanceof ProfileActivity) {
                            ((ProfileActivity) context).refresh();

                        }
                    }

                    @Override
                    public void onChangeCallBack(String editPostId, Boolean isLiked, int commentCount, int likeCount, int shareCount, int changePosition, int deletePosition) {
                        if (editPostId != null && !editPostId.isEmpty()) {
                            if (clickCallBack != null) {
                                clickCallBack.onClickCreatePost(content.getPostId());
                            }
                        }

                        if (deletePosition != -1) {


                            if (videoView != null) {
                                mRecyclerView.deleteVideo(videoView);
                            }
                            if (clickCallBack != null) {
                                clickCallBack.onDelete(deletePosition);
                            }
                        }

                        if (isLiked != null) {
                            contentList.get(changePosition).setLiked(isLiked);
                            if (isLiked) {
                                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.colorFwd));
                                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorFwd));
                            } else {
                                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.grey_5));
                                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.grey_5));
                            }
                        }
                        if (likeCount != -1) {
                            contentList.get(changePosition).setTotalLikes(likeCount);
                            if (likeCount > 0) {
                                like_count.setVisibility(VISIBLE);
                                like_count.setText(likeCount + "");
                            } else {
                                like_count.setVisibility(GONE);
                            }
                        }
                        if (commentCount != -1) {
                            contentList.get(changePosition).setTotalComments(commentCount);
                            if (commentCount > 0) {
                                comment_count.setVisibility(VISIBLE);
                                comment_count.setText(commentCount + " Comments");
                            } else {
                                comment_count.setVisibility(GONE);
                            }
                        }
                        if (shareCount != -1) {
                            contentList.get(changePosition).setTotalShares(shareCount);
                            if (shareCount > 0) {
                                share_count.setVisibility(VISIBLE);
                                share_count.setText(shareCount + " Share");
                            } else {
                                share_count.setVisibility(GONE);
                            }
                        }
                    }
                };

                context.startActivity(new Intent(context, VideoViewActivity.class).putExtra("Position", position).putExtra("VideoData", content));

            });

           /* playBtn.setOnClickListener(v -> {
                if (videoView.getPlayer().isPlaying()) {
                    videoView.getPlayer().pause();
                    isPause = true;
                    playBtn.setVisibility(View.VISIBLE);
                    thumbnail.setVisibility(View.VISIBLE);
                } else {
                    isPause = false;
                    videoView.getPlayer().play();
                    playBtn.setVisibility(View.GONE);
                    thumbnail.setVisibility(View.GONE);
                }
            });*/

            //to mute/un-mute video (optional)
            volBtn.setOnClickListener(v -> {
                if (isMute) {
                    if (videoView.getPlayer() != null) {
                        videoView.getPlayer().setVolume(1f);
                        volBtn.setImageResource(R.drawable.ic_unmute);
                        if (/*am.isMusicActive() &&  */videoView.getPlayer().isPlaying()) {
                            am.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                        }
                    }
                } else {
                    if (videoView.getPlayer() != null) {
                        videoView.getPlayer().setVolume(0f);
                        volBtn.setImageResource(R.drawable.ic_mute);

                        //if (am.isMusicActive()) {
                        am.abandonAudioFocus(focusChangeListener);
                        //}
                    }

                }
                isMute = !isMute;
            });

            /*if (content.getContentTypeId() == 2) {
                volBtn.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.VISIBLE);
            } else {
                volBtn.setVisibility(View.GONE);
                playBtn.setVisibility(View.GONE);
            }*/

            if (content.getParsedSharedData() != null) {
                ownerView.setVisibility(VISIBLE);
                nameTv.setText(content.getParsedSharedData().getFisrtName() + " " + content.getParsedSharedData().getLastName());
                timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getParsedSharedData().getEntryAt()));
                if (content.getParsedSharedData().getCaption() != null && !content.getParsedSharedData().getCaption().trim().isEmpty()) {
                    postTxt.setText(content.getParsedSharedData().getCaption().trim());
                    postTxt.setVisibility(VISIBLE);
                } else {
                    postTxt.setVisibility(GONE);
                }
                Glide.with(context).load(content.getParsedSharedData().getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile);

                nameParentTv.setText(content.getFisrtName() + " " + content.getLastName());
                timeParentTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getEntryAt()));
                if (content.getCaption() != null && !content.getCaption().trim().isEmpty()) {
                    postParentTxt.setText(content.getCaption().trim());
                    postParentTxt.setVisibility(VISIBLE);
                } else {
                    postParentTxt.setVisibility(GONE);
                }
                Glide.with(context).load(content.getProfilePictureUrl()).apply(requestOptionsUserImage).into(profileParent);
            } else {
                ownerView.setVisibility(GONE);
                nameTv.setText(content.getFisrtName() + " " + content.getLastName());
                timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getEntryAt()));
                if (content.getCaption() != null && !content.getCaption().trim().isEmpty()) {
                    postTxt.setText(content.getCaption().trim());
                    postTxt.setVisibility(VISIBLE);
                } else {
                    postTxt.setVisibility(GONE);
                }
                Glide.with(context).load(content.getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile);
            }


            if (content.getTotalLikes() > 0) {
                like_count.setVisibility(VISIBLE);
                like_count.setText(content.getTotalLikes() + "");
            } else {
                like_count.setVisibility(GONE);
            }
            if (content.getTotalComments() > 0) {
                comment_count.setVisibility(VISIBLE);
                comment_count.setText(content.getTotalComments() + " Comments");
            } else {
                comment_count.setVisibility(GONE);
            }
            if (content.getTotalShares() > 0) {
                share_count.setVisibility(VISIBLE);
                share_count.setText(content.getTotalShares() + " Share");
            } else {
                share_count.setVisibility(GONE);
            }

            commentBtn.setOnClickListener(v -> commentDialog.showCommentsDialog(content.getPostId(), size -> {
                comment_count.setText(size + " Comments");
                comment_count.setVisibility(VISIBLE);
                contentList.get(position).setTotalComments(size);

            }/*position, comment_count*/));
            comment_count.setOnClickListener(v -> commentDialog.showCommentsDialog(content.getPostId(), size -> {
                comment_count.setText(size + " Comments");
                comment_count.setVisibility(VISIBLE);
                contentList.get(position).setTotalComments(size);

            }/*position, comment_count*/));
            if (content.isLiked()) {
                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.colorFwd));
                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorFwd));
            } else {
                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.grey_5));
                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.grey_5));
            }


            likeBtn.setOnClickListener(v -> {

                UtilMethods.INSTANCE.triggerLikeApi(context, content.getPostId(), "" /*,!content.isLiked(), likeBtn, like_count, position*/, new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object object) {
                        boolean isLiked = (boolean) object;

                        updateLikeState(/*content,*/ isLiked, position, likeBtn, like_count);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
            });
            moreBTn.setOnClickListener(view -> showPopupMenu(view, content, position, videoView));
            shareBtn.setOnClickListener(view -> {
                ShareDialogFragment bottomSheetDialogFragment = ShareDialogFragment.newInstance(content, (int typeId) -> {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).refresh(typeId);
                    } else if (context instanceof ProfileActivity) {
                        ((ProfileActivity) context).refresh();

                    }
                });
                if (context instanceof MainActivity) {
                    bottomSheetDialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "ShareBottomSheetDialog");
                } else if (context instanceof ProfileActivity) {
                    bottomSheetDialogFragment.show(((ProfileActivity) context).getSupportFragmentManager(), "ShareBottomSheetDialog");
                }


            });

            whatsAppBtn.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://api.whatsapp.com/send?text=" + ApplicationConstant.INSTANCE.postUrl + content.getPostId()));
                context.startActivity(intent);
            });
            profile.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", content.getUserId());
                context.startActivity(intent);
            });
        }
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, moreBTn;
        MaterialButton likeBtn, commentBtn, whatsAppBtn, shareBtn;
        TextView nameTv, nameParentTv, timeParentTv, postParentTxt, timeTv, postTxt, like_count, comment_count, share_count;
        ImageView profile, profileParent;
        View ownerView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ownerView = itemView.findViewById(R.id.ownerView);
            nameParentTv = itemView.findViewById(R.id.nameParentTv);
            timeParentTv = itemView.findViewById(R.id.timeParentTv);
            postParentTxt = itemView.findViewById(R.id.postParentTxt);
            profileParent = itemView.findViewById(R.id.profileParent);
            moreBTn = itemView.findViewById(R.id.moreBTn);
            nameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            postTxt = itemView.findViewById(R.id.postTxt);
            profile = itemView.findViewById(R.id.profile);
            imageView = itemView.findViewById(R.id.container);
            like_count = itemView.findViewById(R.id.like_count);
            comment_count = itemView.findViewById(R.id.comment_count);
            share_count = itemView.findViewById(R.id.share_count);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            whatsAppBtn = itemView.findViewById(R.id.whatsAppBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
        }

        public void bind(ContentResult content, int position) {
            /*if(videoHolder!=null && !videoHolder.isPlaying()) {
                videoHolder = null;
            }*/
            if (content.getHeight() > 0 && content.getWidth() > 0) {
                double aspectRatio = content.getWidth() / content.getHeight();
                double showImageHeight = screenWidth / aspectRatio;
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                if (showImageHeight > videoMaxHeight) {
                    params.height = videoMaxHeight; // Set the height in pixels
                } else {
                    params.height = (int) showImageHeight;
                }
                imageView.setLayoutParams(params);
                Glide.with(context).load(content.getPostContent()).apply(requestOptionsImage).into(imageView);
            } else {
                Glide.with(context).asBitmap().load(content.getPostContent()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).apply(requestOptionsVideo).listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        // Handle error if loading fails
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        // Get the height of the loaded image

                        double imageHeight = resource.getHeight();
                        double imageWidth = resource.getWidth();
                        // Log.d("ImageSize", "Width: " + imageWidth + ", Height: " + imageHeight);
                        double aspectRatio = imageWidth / imageHeight;
                        double showImageHeight = screenWidth / aspectRatio;
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        if (showImageHeight > videoMaxHeight) {
                            params.height = videoMaxHeight; // Set the height in pixels
                        } else {
                            params.height = (int) showImageHeight;
                        }
                        imageView.setLayoutParams(params);
                        // thumbnail.setImageBitmap(resource);
                        return false;
                    }
                }).into(imageView);
            }
            /*if (content.getUserId().equalsIgnoreCase(userId)) {
                moreBTn.setVisibility(View.VISIBLE);
            } else {
                moreBTn.setVisibility(View.GONE);
            }*/


            if (content.getParsedSharedData() != null) {
                ownerView.setVisibility(VISIBLE);
                nameTv.setText(content.getParsedSharedData().getFisrtName() + " " + content.getParsedSharedData().getLastName());
                timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getParsedSharedData().getEntryAt()));
                if (content.getParsedSharedData().getCaption() != null && !content.getParsedSharedData().getCaption().trim().isEmpty()) {
                    postTxt.setText(content.getParsedSharedData().getCaption().trim());
                    postTxt.setVisibility(VISIBLE);
                } else {
                    postTxt.setVisibility(GONE);
                }
                Glide.with(context).load(content.getParsedSharedData().getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile);

                nameParentTv.setText(content.getFisrtName() + " " + content.getLastName());
                timeParentTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getEntryAt()));
                if (content.getCaption() != null && !content.getCaption().trim().isEmpty()) {
                    postParentTxt.setText(content.getCaption().trim());
                    postParentTxt.setVisibility(VISIBLE);
                } else {
                    postParentTxt.setVisibility(GONE);
                }
                Glide.with(context).load(content.getProfilePictureUrl()).apply(requestOptionsUserImage).into(profileParent);
            } else {
                ownerView.setVisibility(GONE);
                nameTv.setText(content.getFisrtName() + " " + content.getLastName());
                timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(content.getEntryAt()));
                if (content.getCaption() != null && !content.getCaption().trim().isEmpty()) {
                    postTxt.setText(content.getCaption().trim());
                    postTxt.setVisibility(VISIBLE);
                } else {
                    postTxt.setVisibility(GONE);
                }
                Glide.with(context).load(content.getProfilePictureUrl()).apply(requestOptionsUserImage).into(profile);
            }


            if (content.getTotalLikes() > 0) {
                like_count.setVisibility(VISIBLE);
                like_count.setText(content.getTotalLikes() + "");
            } else {
                like_count.setVisibility(GONE);
            }
            if (content.getTotalComments() > 0) {
                comment_count.setVisibility(VISIBLE);
                comment_count.setText(content.getTotalComments() + " Comments");
            } else {
                comment_count.setVisibility(GONE);
            }
            if (content.getTotalShares() > 0) {
                share_count.setVisibility(VISIBLE);
                share_count.setText(content.getTotalShares() + " Share");
            } else {
                share_count.setVisibility(GONE);
            }
            if (content.isLiked()) {
                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.colorFwd));
                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorFwd));
            } else {
                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.grey_5));
                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.grey_5));
            }
            commentBtn.setOnClickListener(v -> commentDialog.showCommentsDialog(content.getPostId(), size -> {
                comment_count.setText(size + " Comments");
                comment_count.setVisibility(VISIBLE);
                contentList.get(position).setTotalComments(size);

            }/*position, comment_count*/));
            comment_count.setOnClickListener(v -> commentDialog.showCommentsDialog(content.getPostId(), size -> {
                comment_count.setText(size + " Comments");
                comment_count.setVisibility(VISIBLE);
                contentList.get(position).setTotalComments(size);

            }/*position, comment_count*/));

            imageView.setOnClickListener(view -> {
                ImageZoomViewActivity.callBack = new CountChangeCallBack() {
                    @Override
                    public void onRefresh(int typeId) {
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).refresh(typeId);
                        } else if (context instanceof ProfileActivity) {
                            ((ProfileActivity) context).refresh();

                        }
                    }

                    @Override
                    public void onChangeCallBack(String editPostId, Boolean isLiked, int commentCount, int likeCount, int shareCount, int changePosition, int deletePosition) {

                        if (editPostId != null && !editPostId.isEmpty()) {
                            if (clickCallBack != null) {
                                clickCallBack.onClickCreatePost(content.getPostId());
                            }
                        }

                        if (deletePosition != -1) {
                            if (clickCallBack != null) {
                                clickCallBack.onDelete(deletePosition);
                            }
                        }

                        if (isLiked != null) {
                            contentList.get(changePosition).setLiked(isLiked);
                            if (isLiked) {
                                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.colorFwd));
                                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorFwd));
                            } else {
                                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.grey_5));
                                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.grey_5));
                            }
                        }
                        if (likeCount != -1) {
                            contentList.get(changePosition).setTotalLikes(likeCount);
                            if (likeCount > 0) {
                                like_count.setVisibility(VISIBLE);
                                like_count.setText(likeCount + "");
                            } else {
                                like_count.setVisibility(GONE);
                            }
                        }
                        if (commentCount != -1) {
                            contentList.get(changePosition).setTotalComments(commentCount);
                            if (commentCount > 0) {
                                comment_count.setVisibility(VISIBLE);
                                comment_count.setText(commentCount + " Comments");
                            } else {
                                comment_count.setVisibility(GONE);
                            }
                        }
                        if (shareCount != -1) {
                            contentList.get(changePosition).setTotalShares(shareCount);
                            if (shareCount > 0) {
                                share_count.setVisibility(VISIBLE);
                                share_count.setText(shareCount + " Share");
                            } else {
                                share_count.setVisibility(GONE);
                            }
                        }
                    }
                };
                context.startActivity(new Intent(context, ImageZoomViewActivity.class).putExtra("Position", position).putExtra("ImageData", content));

                //videoHolder=null;
            });
            likeBtn.setOnClickListener(v -> {
                UtilMethods.INSTANCE.triggerLikeApi(context, content.getPostId(), "" /*,!content.isLiked(), likeBtn, like_count, position*/, new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object object) {
                        boolean isLiked = (boolean) object;

                        updateLikeState(/*content,*/ isLiked, position, likeBtn, like_count);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
            });

            moreBTn.setOnClickListener(view -> showPopupMenu(view, content, position, null));
            shareBtn.setOnClickListener(view -> {
                ShareDialogFragment bottomSheetDialogFragment = ShareDialogFragment.newInstance(content, (int typeId) -> {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).refresh(typeId);
                    } else if (context instanceof ProfileActivity) {
                        ((ProfileActivity) context).refresh();

                    }
                });
                if (context instanceof MainActivity) {
                    bottomSheetDialogFragment.show(((MainActivity) context).getSupportFragmentManager(), "ShareBottomSheetDialog");
                } else if (context instanceof ProfileActivity) {
                    bottomSheetDialogFragment.show(((ProfileActivity) context).getSupportFragmentManager(), "ShareBottomSheetDialog");
                }


            });

            whatsAppBtn.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://api.whatsapp.com/send?text=" + ApplicationConstant.INSTANCE.postUrl + content.getPostId()));
                context.startActivity(intent);
            });

            profile.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", content.getUserId());
                context.startActivity(intent);
            });
        }
    }


    private void showPopupMenu(View view, ContentResult content, int position, PlayerView playerView) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_post_popup, null);

        // Initialize the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView, (int) context.getResources().getDimension(com.intuit.sdp.R.dimen._160sdp), ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set up views in popup layout
        TextView edit = popupView.findViewById(R.id.edit);
        TextView delete = popupView.findViewById(R.id.delete);
        TextView report = popupView.findViewById(R.id.report);
        TextView copyLink = popupView.findViewById(R.id.copyLink);
        View editLine = popupView.findViewById(R.id.editLine);
        View deleteLine = popupView.findViewById(R.id.deleteLine);
        View copyLinkLine = popupView.findViewById(R.id.copyLinkLine);

        if (content.getUserId().equalsIgnoreCase(userId) || content.getParsedSharedData() != null && content.getParsedSharedData().getUserId().equalsIgnoreCase(userId)) {
            report.setVisibility(GONE);
            copyLinkLine.setVisibility(GONE);
        } else {
            edit.setVisibility(GONE);
            editLine.setVisibility(GONE);
            delete.setVisibility(GONE);
            deleteLine.setVisibility(GONE);
        }
        edit.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (clickCallBack != null) {
                clickCallBack.onClickCreatePost(content.getPostId());
            }
        });
        delete.setOnClickListener(v -> {
            popupWindow.dismiss();
            showDeleteConfirmationDialog(content, position, playerView);
        });
        report.setOnClickListener(v -> {
            popupWindow.dismiss();
            UtilMethods.INSTANCE.openReportBottomSheetDialog(context, content.getPostId());
        });
        copyLink.setOnClickListener(v -> {
            popupWindow.dismiss();
            Utility.INSTANCE.setClipboard(context, ApplicationConstant.INSTANCE.postUrl + content.getPostId(), "Copy Link");
        });
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());

    }


   /* @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }*/

    private void scrollToCenter(int position) {
       /* mRecyclerView.pauseVideo();
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(mRecyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        layoutManager.startSmoothScroll(smoothScroller);*/
    }

    private void showDeleteConfirmationDialog(ContentResult content, int position, PlayerView playerView) {
        new AlertDialog.Builder(context).setTitle("Delete Content").setMessage("Are you sure you want to delete this content?").setPositiveButton("Delete", (dialog, which) -> {
            deleteContentFromServer(content.getPostId(), position, playerView);  // Call API to delete content
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
    }


    private void updateLikeState(/*ContentResult content,*/ boolean liked, int position, MaterialButton likeBtn, TextView likeCount) {
        //content.setLiked(liked);
        contentList.get(position).setLiked(liked);

        int newLikesCount = liked ? contentList.get(position).getTotalLikes() + 1 : contentList.get(position).getTotalLikes() - 1;
        // content.setTotalLikes(newLikesCount);
        contentList.get(position).setTotalLikes(newLikesCount);


        if (newLikesCount > 0) {
            likeCount.setVisibility(VISIBLE);
            likeCount.setText(newLikesCount + "");
        } else {
            likeCount.setVisibility(GONE);
        }
        if (liked) {
            likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.colorFwd));
            likeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorFwd));
        } else {
            likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.grey_5));
            likeBtn.setTextColor(ContextCompat.getColor(context, R.color.grey_5));
        }

        //notifyItemChanged(position);
    }


    /*private void showCommentInputDialog(String postId, TextView commentsTextView) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_text_input, null);
        final EditText input = dialogView.findViewById(R.id.edit_text_input);
        builder.setView(dialogView)
                .setTitle("Write a Comment....")
                .setPositiveButton("Post", (dialog, which) -> {
                    String text = input.getText().toString();
                    if (!text.isEmpty()) {
                        postComment(postId,  text, commentsTextView);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .setIcon(R.drawable.baseline_draw_24);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            Button negativeButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(ContextCompat.getColor(context, R.color.colorFwd));
        });
        dialog.show();
    }*/


   /* private void showCommentsDialog(String postId, int position, TextView commentCountTv) {

        if (alertDialogComment != null && alertDialogComment.isShowing()) {
            return;
        }

        alertDialogComment = new Dialog(context, R.style.full_screen_dialog);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_comments, null);
        alertDialogComment.setContentView(dialogView);

        ImageView backButton = dialogView.findViewById(R.id.back_button);
        TextView noComment = dialogView.findViewById(R.id.noComment);
        TextView replyingTo = dialogView.findViewById(R.id.replyingTo);
        TextView replyingToCancel = dialogView.findViewById(R.id.replyingToCancel);
        View loaderView = dialogView.findViewById(R.id.loaderView);
        RecyclerView recyclerViewComments = dialogView.findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(context));
        EditText editTextComment = dialogView.findViewById(R.id.editTextComment);
        ImageButton buttonPostComment = dialogView.findViewById(R.id.buttonPostComment);
        backButton.setOnClickListener(view -> alertDialogComment.dismiss());
        List<CommentResponse> listComment = new ArrayList<>();


        commentParent = null;
        commentReply = null;
        commentReplyReply = null;
        replyFullName = "";
        positionParent = -1;
        positionReply = -1;
        positionReplyReply = -1;

        CommentsAdapter commentsAdapter = new CommentsAdapter(listComment, context, tokenManager, postId, (commentParent, positionParent, commentReply, positionReply, commentReplyReply, positionReplyReply) -> {
            this.commentParent = commentParent;
            this.commentReply = commentReply;
            this.commentReplyReply = commentReplyReply;
            this.positionParent = positionParent;
            this.positionReply = positionReply;
            this.positionReplyReply = positionReplyReply;
            replyingTo.setVisibility(View.VISIBLE);
            replyingToCancel.setVisibility(View.VISIBLE);
            String fullName = "";
            if (commentReplyReply != null) {
                fullName = commentReplyReply.getFisrtName() + " " + commentReplyReply.getLastName();
            } else if (commentReply != null) {
                fullName = commentReply.getFisrtName() + " " + commentReply.getLastName();
            } else {
                fullName = commentParent.getFisrtName() + " " + commentParent.getLastName();
            }

            String inputText = editTextComment.getText().toString();
            if (replyFullName.trim().length() > 0 && !fullName.trim().equalsIgnoreCase(replyFullName.trim())) {
                inputText = inputText.replace(replyFullName.trim() + " ", "");
            }
            replyFullName = fullName;
            replyingTo.setText(Html.fromHtml(context.getResources().getString(R.string.replying_to, fullName), Html.FROM_HTML_MODE_LEGACY));
            SpannableString spannableString;
            if (inputText.startsWith(fullName)) {
                spannableString = new SpannableString(inputText);
            } else {
                spannableString = new SpannableString(fullName + " " + inputText);
            }


            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, fullName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new BackgroundColorSpan(ContextCompat.getColor(context, R.color.colorAccentLight)), 0, fullName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Change color if needed
            editTextComment.setText(spannableString);
            editTextComment.setSelection(editTextComment.getText().length());
            *//*editTextComment.setText(fullName);*//*
        });
        recyclerViewComments.setAdapter(commentsAdapter);
        replyingToCancel.setOnClickListener(view -> {
            if (editTextComment.getText().toString().contains(replyFullName)) {
                editTextComment.setText(editTextComment.getText().toString().replace(replyFullName, "").replaceFirst(" ", ""));
            }

            commentParent = null;
            commentReply = null;
            commentReplyReply = null;
            replyFullName = "";
            positionParent = -1;
            positionReply = -1;
            positionReplyReply = -1;
            replyingTo.setVisibility(View.GONE);
            replyingToCancel.setVisibility(View.GONE);
        });

        UtilMethods.INSTANCE.fetchCommentsForPost(postId, null, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                noComment.setVisibility(View.GONE);
                loaderView.setVisibility(View.GONE);
                listComment.addAll((List<CommentResponse>) object);
                commentsAdapter.notifyItemChanged(0, listComment.size());
                recyclerViewComments.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String msg) {
                loaderView.setVisibility(View.GONE);
                if (listComment.size() == 0) {
                    noComment.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonPostComment.setOnClickListener(v -> {
            String commentText = editTextComment.getText().toString();

            if (commentReplyReply != null) {
                String fullName = commentReplyReply.getFisrtName() + " " + commentReplyReply.getLastName();
                if (commentText.contains(fullName)) {
                    commentText = commentText.replaceAll(fullName, "<b><font color='#F6A400'>" + fullName + "</font></b>");
                }
            } else if (commentReply != null) {
                String fullName = commentReply.getFisrtName() + " " + commentReply.getLastName();
                if (commentText.contains(fullName)) {
                    commentText = commentText.replaceAll(fullName, "<b><font color='#F6A400'>" + fullName + "</font></b>");
                }
            } else if (commentParent != null) {
                String fullName = commentParent.getFisrtName() + " " + commentParent.getLastName();
                if (commentText.contains(fullName)) {
                    commentText = commentText.replaceAll(fullName, "<b><font color='#F6A400'>" + fullName + "</font></b>");
                }
            }
            if (!commentText.isEmpty()) {

                UtilMethods.INSTANCE.postComment(context, postId, commentReplyReply != null ? commentReplyReply.getCommentId() : (commentReply != null ? commentReply.getCommentId() : (commentParent != null ? commentParent.getCommentId() : null)), commentText, new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object object) {
                        CommentResponse comment = (CommentResponse) object;
                        if (commentReplyReply != null && positionReplyReply != -1) {
                            if (listComment.get(positionParent).getReplies().get(positionReply).getReplies().get(positionReplyReply).getReplies() != null) {
                                ArrayList<CommentResponse> replyList = listComment.get(positionParent).getReplies().get(positionReply).getReplies().get(positionReplyReply).getReplies();
                                replyList.add(0, comment);
                                listComment.get(positionParent).getReplies().get(positionReply).getReplies().get(positionReplyReply).setReplies(replyList);
                            } else {
                                ArrayList<CommentResponse> replyList = new ArrayList<>();
                                replyList.add(comment);
                                listComment.get(positionParent).getReplies().get(positionReply).getReplies().get(positionReplyReply).setReplies(replyList);
                            }
                            commentsAdapter.notifyItemChanged(positionParent);
                            commentsAdapter.notifyItemRangeChanged(positionParent, listComment.size());
                            recyclerViewComments.smoothScrollToPosition(positionParent);
                        } else if (commentReply != null && positionReply != -1) {
                            if (listComment.get(positionParent).getReplies().get(positionReply).getReplies() != null) {
                                ArrayList<CommentResponse> replyList = listComment.get(positionParent).getReplies().get(positionReply).getReplies();
                                replyList.add(0, comment);
                                listComment.get(positionParent).getReplies().get(positionReply).setReplies(replyList);
                            } else {
                                ArrayList<CommentResponse> replyList = new ArrayList<>();
                                replyList.add(comment);
                                listComment.get(positionParent).getReplies().get(positionReply).setReplies(replyList);
                            }
                            commentsAdapter.notifyItemChanged(positionParent);
                            commentsAdapter.notifyItemRangeChanged(positionParent, listComment.size());
                            recyclerViewComments.smoothScrollToPosition(positionParent);
                        } else if (commentParent != null && positionParent != -1) {
                            if (listComment.get(positionParent).getReplies() != null) {
                                ArrayList<CommentResponse> replyList = listComment.get(positionParent).getReplies();
                                replyList.add(0, comment);
                                listComment.get(positionParent).setReplies(replyList);
                            } else {
                                ArrayList<CommentResponse> replyList = new ArrayList<>();
                                replyList.add(comment);
                                listComment.get(positionParent).setReplies(replyList);
                            }
                            commentsAdapter.notifyItemChanged(positionParent);
                            commentsAdapter.notifyItemRangeChanged(positionParent, listComment.size());
                            recyclerViewComments.smoothScrollToPosition(positionParent);
                        } else {
                            listComment.add(0, comment);
                            commentsAdapter.notifyItemInserted(0);
                            commentsAdapter.notifyItemRangeChanged(0, listComment.size());
                            recyclerViewComments.smoothScrollToPosition(0);
                        }


                        if (listComment.size() > 0) {
                            noComment.setVisibility(View.GONE);
                            recyclerViewComments.setVisibility(View.VISIBLE);
                        }
                        commentCountTv.setText(listComment.size() + " Comments");
                        commentCountTv.setVisibility(View.VISIBLE);
                        contentList.get(position).setTotalComments(listComment.size());
                        editTextComment.setText("");
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });

            } else {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogComment.show();


    }*/

    private void deleteContentFromServer(String postId, int position, PlayerView playerView) {
        EndPointInterface apiService = ApiClient.getClient().create(EndPointInterface.class);
        Call<BasicResponse> call = apiService.deleteComment("Bearer " + tokenManager.getAccessToken(), postId);

        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    // Content deleted successfully, remove it from the list and notify adapter


                    if (playerView != null) {
                        mRecyclerView.deleteVideo(playerView);
                    }
                    if (clickCallBack != null) {
                        clickCallBack.onDelete(position);
                    }

                    Toast.makeText(context, "Content deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete content", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface ClickCallBack {
        void onClickCreatePost(String postId);

        void onClickCreateStory(String storyId);

        void onClickProfile(String userId);

        void onOpenStory(ArrayList<StoryResult> list, int position, StoryResult result);

        void onDelete(int position);
    }


   /* public interface ApiCallBack {
        void onSuccess(Object object);

        void onError(String msg);
    }*/
}





