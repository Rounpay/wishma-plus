package com.infotech.wishmaplus.Adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.infotech.wishmaplus.Adapter.Interfaces.ReplyCallBack;
import com.infotech.wishmaplus.Api.Object.CommentResult;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.PreferencesManager;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;


public class CommentsReplyAdapter extends RecyclerView.Adapter<CommentsReplyAdapter.CommentViewHolder> {
    private RequestOptions requestOptionsUserImage = null;
    private List<CommentResult> commentList;
    private Activity context;
    private PreferencesManager tokenManager;
    private String postId;
    private ReplyCallBack replyTo;
    private CommentResult commentParent;
    private int positionParent;

    public CommentsReplyAdapter(List<CommentResult> commentList, CommentResult commentParent, int positionParent, Activity context, PreferencesManager tokenManager, String postId, ReplyCallBack replyTo) {
        this.commentList = commentList;
        this.context = context;
        this.tokenManager = tokenManager;
        this.postId = postId;
        this.replyTo = replyTo;
        this.commentParent = commentParent;
        this.positionParent = positionParent;
        if (requestOptionsUserImage == null) {
            requestOptionsUserImage = UtilMethods.INSTANCE.getRequestOption_With_UserIcon();
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_comment_reply, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentResult comment = commentList.get(position);
        holder.bind(comment, position);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView textViewUserName, timeTv, textViewComment, like_count, replyExpendTv;
        MaterialButton likeBtn, replyBtn;
        RecyclerView replyRecyclerView;
        ImageView profile;
        ProgressBar progress;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            timeTv = itemView.findViewById(R.id.timeTv);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            progress = itemView.findViewById(R.id.progress);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            replyBtn = itemView.findViewById(R.id.replyBtn);
            like_count = itemView.findViewById(R.id.like_count);
            profile = itemView.findViewById(R.id.profile);
            replyExpendTv = itemView.findViewById(R.id.replyExpendTv);
            replyRecyclerView = itemView.findViewById(R.id.replyRecyclerView);
            replyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        public void bind(CommentResult comment, int position) {
            if (comment.getReplies() != null && comment.getReplies().size() > 0) {
                replyExpendTv.setVisibility(View.GONE);
                replyRecyclerView.setVisibility(View.VISIBLE);
                replyRecyclerView.setAdapter(new CommentsReplyReplyAdapter(comment.getReplies(),commentParent,positionParent,comment,position, context, tokenManager, postId,
                        ( commentParent, positionParent, commentReply, positionReply,  commentReplyReply, positionReplyReply) -> {
                            if(replyTo!=null){
                                replyTo.onReplyClick(commentParent, positionParent, commentReply, positionReply,  commentReplyReply, positionReplyReply);
                            }
                }));
            } else {
                if (comment.getReplyCount() > 0) {
                    replyExpendTv.setVisibility(View.VISIBLE);
                    replyExpendTv.setText("View "+comment.getReplyCount()+(comment.getReplyCount()>1?" replies...":" reply..."));
                } else {
                    replyExpendTv.setVisibility(View.GONE);
                }
                replyRecyclerView.setVisibility(View.GONE);
            }
            textViewUserName.setText(comment.getFisrtName() + " " + comment.getLastName());
            textViewComment.setText(Html.fromHtml(comment.getComment().trim(), Html.FROM_HTML_MODE_LEGACY));
            like_count.setText(comment.getTotalLikes());
            timeTv.setText(UtilMethods.INSTANCE.covertTimeToText(comment.getCommentedAt()));
            try {
                int count = Integer.parseInt(comment.getTotalLikes());
                if (count > 0) {
                    like_count.setVisibility(View.VISIBLE);
                } else {
                    like_count.setVisibility(View.GONE);
                }
            } catch (NumberFormatException nfe) {

            }
            if (comment.isLiked()) {
                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.colorFwd));
                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorFwd));
            } else {
                likeBtn.setIconTint(ContextCompat.getColorStateList(context, R.color.grey_5));
                likeBtn.setTextColor(ContextCompat.getColor(context, R.color.grey_5));
            }
            // Set the like count visibility and text


            Glide.with(context)
                    .load(comment.getProfilePictureUrl())
                    .apply(requestOptionsUserImage)
                    .into(profile);


            likeBtn.setOnClickListener(v -> {
                triggerLikeApi(comment.getCommentId(), postId, position);
            });

            replyBtn.setOnClickListener(v -> {

                if (replyTo != null) {
                    replyTo.onReplyClick(commentParent, positionParent, comment, position, null, -1);
                }
            });

            replyExpendTv.setOnClickListener(view -> {
                progress.setVisibility(View.VISIBLE);
                replyExpendTv.setVisibility(View.GONE);
                UtilMethods.INSTANCE.fetchCommentsForPost(postId,comment.getCommentId(), new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object object) {
                        ArrayList<CommentResult> list = (ArrayList<CommentResult>) object;
                        if(list!=null && list.size()>0){

                            commentList.get(position).setReplies(list);
                            notifyItemChanged(position);
                            notifyItemRangeChanged(position,commentList.size());
                            progress.setVisibility(View.GONE);
                            replyExpendTv.setVisibility(View.GONE);
                        }else {
                            progress.setVisibility(View.GONE);
                            replyExpendTv.setVisibility(View.VISIBLE);
                        }
                        /*noComment.setVisibility(View.GONE);
                        loaderView.setVisibility(View.GONE);
                        listComment.addAll((List<CommentResponse>) object);
                        commentsAdapter.notifyItemChanged(0, contentList.size());
                        recyclerViewComments.setVisibility(View.VISIBLE);*/
                    }

                    @Override
                    public void onError(String msg) {
                        progress.setVisibility(View.GONE);
                        replyExpendTv.setVisibility(View.VISIBLE);
                    }
                });
            });
        }


    }

    /*public void addComment(CommentResponse newComment) {
        commentList.add(newComment);
        notifyItemInserted(commentList.size() - 1);
        notifyDataSetChanged();
    }*/
    private void triggerLikeApi(String commentId, String postId, int position) {

        UtilMethods.INSTANCE.triggerLikeApi(context, postId, commentId, new UtilMethods.ApiCallBackMulti() {
            @Override
            public void onSuccess(Object object) {
                boolean isLiked = (boolean) object;
                commentList.get(position).setLiked(isLiked);
                if(isLiked){
                    commentList.get(position).setTotalLikes((Integer.parseInt(commentList.get(position).getTotalLikes())+1)+"");
                }else {
                    commentList.get(position).setTotalLikes((Integer.parseInt(commentList.get(position).getTotalLikes())-1)+"");
                }

                notifyItemChanged(position);
            }

            @Override
            public void onError(String msg) {

            }
        });


       /* try {
            EndPointInterface apiService = ApiClient.getClient().create(EndPointInterface.class);
            Call<LoginResponse> call = apiService.likePost("Bearer " + tokenManager.getAccessToken(), new LikeRequest(postId, commentId));

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        try {
                            if (commentList.get(position).isLiked()) {
                                commentList.get(position).setLiked(false);
                                commentList.get(position).setTotalLikes((Integer.parseInt(commentList.get(position).getTotalLikes()) - 1) + "");
                            } else {
                                commentList.get(position).setLiked(true);
                                commentList.get(position).setTotalLikes((Integer.parseInt(commentList.get(position).getTotalLikes()) + 1) + "");
                            }

                            notifyItemChanged(position);
                        } catch (NumberFormatException nfe) {

                        }

                    } else {
                        Toast.makeText(context, "Failed to like comment", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


}
