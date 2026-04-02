package com.infotech.wishmaplus;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;

public class CommentsBottomSheet extends BottomSheetDialogFragment {

    private String reelId;
    private RecyclerView commentsRecycler;
    private EditText commentInput;
    private ImageView sendBtn;
    private TextView commentCountHeader;
    private CommentsAdapter commentsAdapter;
    private List<CommentModel> commentList = new ArrayList<>();
    private CommentModel replyingTo = null;
    private TextView replyIndicator;

    public static CommentsBottomSheet newInstance(String reelId) {
        CommentsBottomSheet sheet = new CommentsBottomSheet();
        Bundle args = new Bundle();
        args.putString("reel_id", reelId);
        sheet.setArguments(args);
        return sheet;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetStyle);
        if (getArguments() != null) reelId = getArguments().getString("reel_id");
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup container,
                             @Nullable Bundle saved) {
        return inf.inflate(R.layout.bottom_sheet_comments, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commentsRecycler  = view.findViewById(R.id.commentsRecycler);
        commentInput      = view.findViewById(R.id.commentInput);
        sendBtn           = view.findViewById(R.id.sendBtn);
        commentCountHeader= view.findViewById(R.id.commentCountHeader);
        replyIndicator    = view.findViewById(R.id.replyIndicator);

        // Expand to full height
        if (getDialog() != null) {
            getDialog().setOnShowListener(d -> {
                FrameLayout bs = getDialog().findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
                if (bs != null) {
                    BottomSheetBehavior<FrameLayout> b = BottomSheetBehavior.from(bs);
                    b.setState(BottomSheetBehavior.STATE_EXPANDED);
                    b.setSkipCollapsed(true);
                }
            });
        }

        loadDummyComments();

        commentsAdapter = new CommentsAdapter(commentList, comment -> {
            // Reply clicked
            replyingTo = comment;
            replyIndicator.setVisibility(View.VISIBLE);
            replyIndicator.setText("Replying to @" + comment.getUserName());
            commentInput.setHint("Reply to @" + comment.getUserName() + "...");
            commentInput.requestFocus();
        });
        commentsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentsRecycler.setAdapter(commentsAdapter);

        // Cancel reply
        replyIndicator.setOnClickListener(v -> cancelReply());

        // Send button enable/disable
        commentInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                sendBtn.setAlpha(s.length() > 0 ? 1f : 0.4f);
                sendBtn.setClickable(s.length() > 0);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        sendBtn.setAlpha(0.4f);
        sendBtn.setClickable(false);
        sendBtn.setOnClickListener(v -> submitComment());

        updateHeader();
    }

    private void loadDummyComments() {
        List<CommentModel> r1 = new ArrayList<>();
        r1.add(new CommentModel("r1", "user_meena", null,
            "Haha so relatable! 😂", "2m", 5, false));

        commentList.add(new CommentModel("c1", "Rahul_99", null,
            "This is absolutely amazing! 🔥🔥🔥", "2h", 245, true, r1));
        commentList.add(new CommentModel("c2", "priya.dance", null,
            "Love this so much! Keep it up ✨", "1h", 89, false, new ArrayList<>()));
        commentList.add(new CommentModel("c3", "arjun_fit", null,
            "Bhai kitna talent hai yaar 😍", "45m", 32, false, new ArrayList<>()));
        commentList.add(new CommentModel("c4", "shreya_art", null,
            "Dropped everything to watch this 💙", "30m", 18, false, new ArrayList<>()));
        commentList.add(new CommentModel("c5", "rohit.official", null,
            "First! 🎉 Been following you for years", "10m", 4, false, new ArrayList<>()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void submitComment() {
        String text = commentInput.getText().toString().trim();
        if (text.isEmpty()) return;

        CommentModel newComment = new CommentModel(
            "new_" + System.currentTimeMillis(),
            "You", null, text, "Just now", 0, false, new ArrayList<>()
        );

        if (replyingTo != null) {
            replyingTo.getReplies().add(newComment);
            cancelReply();
        } else {
            commentList.add(0, newComment);
        }

        commentsAdapter.notifyDataSetChanged();
        commentsRecycler.scrollToPosition(0);
        commentInput.setText("");
        updateHeader();
    }

    private void cancelReply() {
        replyingTo = null;
        replyIndicator.setVisibility(View.GONE);
        commentInput.setHint("Add a comment...");
    }

    private void updateHeader() {
        commentCountHeader.setText(commentList.size() + " Comments");
    }

    // ════════════════════════════════════════════════════════════════════════
    // Comments RecyclerView Adapter
    // ════════════════════════════════════════════════════════════════════════

    static class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CVH> {

        interface OnReplyClick { void onReply(CommentModel comment); }

        private final List<CommentModel> comments;
        private final OnReplyClick replyListener;

        CommentsAdapter(List<CommentModel> comments, OnReplyClick replyListener) {
            this.comments      = comments;
            this.replyListener = replyListener;
        }

        @NonNull @Override
        public CVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
            return new CVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CVH holder, int position) {
            holder.bind(comments.get(position), replyListener);
        }

        @Override public int getItemCount() { return comments.size(); }

        static class CVH extends RecyclerView.ViewHolder {
            ImageView avatar;
            TextView userName, commentText, timeAgo, likeCount, replyBtn, showRepliesBtn;
            ImageView btnLikeComment;
            LinearLayout repliesContainer;

            CVH(View v) {
                super(v);
                avatar         = v.findViewById(R.id.commentAvatar);
                userName       = v.findViewById(R.id.commentUserName);
                commentText    = v.findViewById(R.id.commentText);
                timeAgo        = v.findViewById(R.id.commentTime);
                likeCount      = v.findViewById(R.id.commentLikeCount);
                btnLikeComment = v.findViewById(R.id.btnLikeComment);
                replyBtn       = v.findViewById(R.id.btnReply);
                showRepliesBtn = v.findViewById(R.id.showRepliesBtn);
                repliesContainer = v.findViewById(R.id.repliesContainer);
            }

            @SuppressLint("SetTextI18n")
            void bind(CommentModel c, OnReplyClick replyListener) {
                userName.setText(c.getUserName());
                commentText.setText(c.getText());
                timeAgo.setText(c.getTimeAgo());
                likeCount.setText(c.getLikes() > 0 ? String.valueOf(c.getLikes()) : "");

                Glide.with(itemView.getContext())
                    .load(c.getAvatarUrl())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.circle_background)
                    .into(avatar);

                // Like comment
                updateLikeState(c);
                btnLikeComment.setOnClickListener(v -> {
                    c.setLiked(!c.isLiked());
                    c.setLikes(c.isLiked() ? c.getLikes() + 1 : c.getLikes() - 1);
                    updateLikeState(c);
                    likeCount.setText(c.getLikes() > 0 ? String.valueOf(c.getLikes()) : "");
                });

                // Reply
                replyBtn.setOnClickListener(v -> replyListener.onReply(c));

                // Show/hide replies
                if (c.getReplies() != null && !c.getReplies().isEmpty()) {
                    showRepliesBtn.setVisibility(View.VISIBLE);
                    showRepliesBtn.setText("View " + c.getReplies().size() + " replies");
                    showRepliesBtn.setOnClickListener(v -> {
                        if (repliesContainer.getVisibility() == View.VISIBLE) {
                            repliesContainer.setVisibility(View.GONE);
                            showRepliesBtn.setText("View " + c.getReplies().size() + " replies");
                        } else {
                            repliesContainer.setVisibility(View.VISIBLE);
                            showRepliesBtn.setText("Hide replies");
                            buildReplies(c.getReplies());
                        }
                    });
                } else {
                    showRepliesBtn.setVisibility(View.GONE);
                    repliesContainer.setVisibility(View.GONE);
                }
            }

            private void updateLikeState(CommentModel c) {
                btnLikeComment.setImageResource(c.isLiked()
                    ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
                btnLikeComment.setColorFilter(c.isLiked() ? 0xFFFF3B30 : 0xFF888888);
            }

            private void buildReplies(List<CommentModel> replies) {
                repliesContainer.removeAllViews();
                for (CommentModel reply : replies) {
                    View rv = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.item_comment_reply, repliesContainer, false);

                    TextView rName = rv.findViewById(R.id.replyUserName);
                    TextView rText = rv.findViewById(R.id.replyText);
                    TextView rTime = rv.findViewById(R.id.replyTime);
                    ImageView rAvatar = rv.findViewById(R.id.replyAvatar);

                    rName.setText(reply.getUserName());
                    rText.setText(reply.getText());
                    rTime.setText(reply.getTimeAgo());
                    Glide.with(itemView.getContext())
                        .load(reply.getAvatarUrl()).transform(new CircleCrop())
                        .placeholder(R.drawable.circle_background).into(rAvatar);

                    repliesContainer.addView(rv);
                }
            }
        }
    }
}
