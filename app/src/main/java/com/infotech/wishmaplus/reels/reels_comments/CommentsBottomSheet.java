package com.infotech.wishmaplus.reels.reels_comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.GeetReelCommentsResponse;
import com.infotech.wishmaplus.LikeReelCommentResponse;
import com.infotech.wishmaplus.R;
import com.infotech.wishmaplus.Utils.UtilMethods;
import com.infotech.wishmaplus.reels.reels_comments.response.CommentItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommentsBottomSheet extends BottomSheetDialogFragment {

    private int reelId;
    private int parentCommentId = 0;
    private boolean isShowingReplies = false;

    // Views
    private RecyclerView commentsRecycler;
    private EditText commentInput;
    private ImageView sendBtn, closeIcon;
    private TextView commentCountHeader, replyIndicator;
    private ProgressBar commentsLoader, paginationLoader;
    private LinearLayout emptyView;

    // Data
    private CommentsAdapter commentsAdapter;
    private final List<CommentItems> commentList = new ArrayList<>();
    private CommentItems replyingTo = null;

    // Pagination
    private int pageNumber = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;
    private boolean hasMore = true;

    private OnDismissListener dismissListener;
    private int totalCommentCount;

    public interface OnDismissListener {
        void onDismiss();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }

    public interface OnCommentCountChanged {
        void onCountChanged(int reelId, int newCount);
    }

    private OnCommentCountChanged commentCountCallback;

    public void setOnCommentCountChanged(OnCommentCountChanged callback) {
        this.commentCountCallback = callback;
    }

    // ── Factory ──────────────────────────────────────────────────────────
    public static CommentsBottomSheet newInstance(int reelId) {
        CommentsBottomSheet s = new CommentsBottomSheet();
        Bundle b = new Bundle();
        b.putInt("reel_id", reelId);
        s.setArguments(b);
        return s;
    }

    @Override
    public void onCreate(@Nullable Bundle saved) {
        super.onCreate(saved);
        setStyle(STYLE_NORMAL, R.style.BottomSheetStyle);
        if (getArguments() != null) reelId = getArguments().getInt("reel_id");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup container, @Nullable Bundle saved) {
        return inf.inflate(R.layout.bottom_sheet_comments, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        expandBottomSheet();
        setupRecycler();
        setupInput();
        loadComments();
    }

    // ── Bind views ───────────────────────────────────────────────────────
    private void bindViews(View view) {
        commentsRecycler = view.findViewById(R.id.commentsRecycler);
        commentInput = view.findViewById(R.id.commentInput);
        sendBtn = view.findViewById(R.id.sendBtn);
        commentCountHeader = view.findViewById(R.id.commentCountHeader);
        replyIndicator = view.findViewById(R.id.replyIndicator);
        commentsLoader = view.findViewById(R.id.commentsLoader);
        paginationLoader = view.findViewById(R.id.paginationLoader);
        emptyView = view.findViewById(R.id.emptyView);
        closeIcon = view.findViewById(R.id.closeBtn);
        closeIcon.setOnClickListener(v -> dismiss());
    }

    // ── Expand bottom sheet ───────────────────────────────────────────────
    private void expandBottomSheet() {
        try {
            if (getDialog() != null) {
                getDialog().setOnShowListener(d -> {
                    try {
                        FrameLayout bs = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
                        if (bs != null) {
                            BottomSheetBehavior<FrameLayout> b = BottomSheetBehavior.from(bs);
                            b.setState(BottomSheetBehavior.STATE_EXPANDED);
                            b.setSkipCollapsed(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Recycler + Adapter ────────────────────────────────────────────────
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void setupRecycler() {
        commentsAdapter = new CommentsAdapter(
                commentList,

                // onReply
                comment -> {
                    try {
                        replyingTo = comment;
                        parentCommentId = comment.getCommentId();
                        isShowingReplies = true;

                        replyIndicator.setVisibility(View.VISIBLE);
                        replyIndicator.setText("↩  Replying to @" + comment.getFullName());
                        commentInput.setHint("Reply to " + comment.getFullName() + "...");
                        commentInput.requestFocus();
                        showKeyboard();

                        commentList.clear();
                        commentsAdapter.notifyDataSetChanged();
                        pageNumber = 1;
                        hasMore = true;
                        loadComments();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },

                // onDelete
                this::deleteComment,
                // ── onLike — CHANGED ──────────────────────────────────────
                // Top level comment → GetReelComment (refresh)
                // Nested/reply comment → DoLikeUnLikeReelComment
                (comment, position) -> {
                    if (isShowingReplies) {
                        doLikeUnlikeComment(comment, position);
                    } else {
                        // Top level comment — GetReelComment refresh karo
                        doLikeUnlikeComment(comment, position);
                    }
                }
        );

        commentsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentsRecycler.setAdapter(commentsAdapter);
        commentsRecycler.setNestedScrollingEnabled(true);

        // Pagination on scroll
        commentsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                try {
                    LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                    if (lm == null) return;
                    if (!isLoading && hasMore
                            && lm.findLastVisibleItemPosition() >= lm.getItemCount() - 2) {
                        loadComments();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ── Input ─────────────────────────────────────────────────────────────
    private void setupInput() {
        replyIndicator.setOnClickListener(v -> cancelReply());

        commentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                boolean has = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    has = !s.isEmpty();
                }
                sendBtn.setAlpha(has ? 1f : 0.4f);
                sendBtn.setClickable(has);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        sendBtn.setAlpha(0.4f);
        sendBtn.setClickable(false);
        sendBtn.setOnClickListener(v -> submitComment());

        commentInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                submitComment();
                return true;
            }
            return false;
        });
    }

    // ── GET comments / replies ────────────────────────────────────────────
    private void loadComments() {
        if (isLoading || !hasMore || !isAdded()) return;
        isLoading = true;

        if (pageNumber == 1) {
            commentsLoader.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            commentsRecycler.setVisibility(View.GONE);
        } else {
            paginationLoader.setVisibility(View.VISIBLE);
        }

        UtilMethods.INSTANCE.getReelComments(
                reelId,
                parentCommentId,
                pageNumber,
                pageSize,
                null,
                new UtilMethods.ApiCallBackMulti() {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(Object response) {
                        try {
                            if (!isAdded()) return;
                            GeetReelCommentsResponse data = (GeetReelCommentsResponse) response;

                            requireActivity().runOnUiThread(() -> {
                                try {
                                    isLoading = false;
                                    commentsLoader.setVisibility(View.GONE);
                                    paginationLoader.setVisibility(View.GONE);

                                    if (data.getResult() != null
                                            && data.getResult().getComments() != null) {

                                        int prev = commentList.size();
                                        commentList.addAll(data.getResult().getComments());

                                        if (prev == 0) {
                                            commentsAdapter.notifyDataSetChanged();
                                        } else {
                                            commentsAdapter.notifyItemRangeInserted(
                                                    prev, data.getResult().getComments().size());
                                        }

                                        hasMore = data.getResult().isHasMore();
                                        pageNumber++;
                                        updateHeader(data.getResult().getTotalCount());
                                    }

                                    boolean empty = commentList.isEmpty();
                                    emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
                                    commentsRecycler.setVisibility(
                                            empty ? View.GONE : View.VISIBLE);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String e) {
                        try {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                isLoading = false;
                                commentsLoader.setVisibility(View.GONE);
                                paginationLoader.setVisibility(View.GONE);
                                if (commentList.isEmpty())
                                    emptyView.setVisibility(View.VISIBLE);
                                Toast.makeText(requireContext(), e,
                                        Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    // ── Refresh top-level comments (like pe call hoga) ────────────────────
    @SuppressLint("NotifyDataSetChanged")
    private void refreshComments() {
        commentList.clear();
        commentsAdapter.notifyDataSetChanged();
        pageNumber = 1;
        hasMore = true;
        loadComments();
    }

    // ── DoLikeUnLikeReelComment (reply/nested comment like pe) ────────────
    private void doLikeUnlikeComment(CommentItems comment, int position) {
        try {
            // Optimistic UI update
            boolean nowLiked = !comment.isLiked();
            comment.setLiked(nowLiked);
            comment.setLikeCount(nowLiked
                    ? comment.getLikeCount() + 1
                    : Math.max(0, comment.getLikeCount() - 1));
            updateCommentLikeInView(position, comment);

            UtilMethods.INSTANCE.likeUnlikeReelComment(
                    comment.getCommentId(),
                    new UtilMethods.ApiCallBackMulti() {
                        @Override
                        public void onSuccess(Object r) {
                            try {
                                if (!isAdded()) return;
                                LikeReelCommentResponse response =
                                        (LikeReelCommentResponse) r;
                                requireActivity().runOnUiThread(() -> {
                                    try {
                                        if (response.getStatusCode() == 1) {
                                            // Server ki actual values set karo
                                            comment.setLiked(response.getLiked());
                                            comment.setLikeCount(response.getLikeCount());
                                            updateCommentLikeInView(position, comment);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String e) {
                            try {
                                if (!isAdded()) return;
                                requireActivity().runOnUiThread(() -> {
                                    try {
                                        // Revert optimistic update
                                        boolean revert = !comment.isLiked();
                                        comment.setLiked(revert);
                                        comment.setLikeCount(revert
                                                ? comment.getLikeCount() + 1
                                                : Math.max(0, comment.getLikeCount() - 1));
                                        updateCommentLikeInView(position, comment);
                                        Toast.makeText(requireContext(), e,
                                                Toast.LENGTH_SHORT).show();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Direct ViewHolder update — notifyItemChanged nahi ────────────────
    private void updateCommentLikeInView(int position, CommentItems comment) {
        try {
            RecyclerView.ViewHolder vh =
                    commentsRecycler.findViewHolderForAdapterPosition(position);
            if (vh instanceof CommentsAdapter.CVH) {
                CommentsAdapter.CVH cvh = (CommentsAdapter.CVH) vh;
                cvh.btnLikeComment.setImageResource(
                        comment.isLiked()
                                ? R.drawable.ic_favorite_filled
                                : R.drawable.ic_favorite_border
                );
                cvh.btnLikeComment.setColorFilter(
                        comment.isLiked() ? 0xFFFF3B30 : 0xFF888888
                );
                if (comment.getLikeCount() > 0) {
                    cvh.likeCountTv.setVisibility(View.VISIBLE);
                    cvh.likeCountTv.setText(
                            comment.getLikeCount() >= 1_000_000
                                    ? String.format(Locale.getDefault(), "%.1fM",
                                    comment.getLikeCount() / 1_000_000.0)
                                    : comment.getLikeCount() >= 1_000
                                    ? String.format(Locale.getDefault(), "%.1fK",
                                    comment.getLikeCount() / 1_000.0)
                                    : String.valueOf(comment.getLikeCount())
                    );
                } else {
                    cvh.likeCountTv.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── POST comment / reply ──────────────────────────────────────────────
    @SuppressLint("NotifyDataSetChanged")
    private void submitComment() {
        try {
            String text = commentInput.getText().toString().trim();
            if (text.isEmpty()) return;

            int parentId = replyingTo != null ? replyingTo.getCommentId() : 0;

            sendBtn.setAlpha(0.4f);
            sendBtn.setClickable(false);

            UtilMethods.INSTANCE.addReelComment(
                    reelId, text, parentId, null,
                    new UtilMethods.ApiCallBackMulti() {
                        @Override
                        public void onSuccess(Object response) {
                            try {
                                if (!isAdded()) return;
                                BasicResponse res = (BasicResponse) response;
                                requireActivity().runOnUiThread(() -> {
                                    try {
                                        sendBtn.setAlpha(1f);
                                        sendBtn.setClickable(true);
                                        commentInput.setText("");
                                        hideKeyboard();

                                        if (res.getStatusCode() == 1) {
                                            commentList.clear();
                                            pageNumber = 1;
                                            hasMore = true;
                                            loadComments();

                                            if (replyingTo == null) {
                                                cancelReply();
                                            }
                                        } else {
                                            Toast.makeText(requireContext(),
                                                    res.getResponseText(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String e) {
                            try {
                                if (!isAdded()) return;
                                requireActivity().runOnUiThread(() -> {
                                    sendBtn.setAlpha(1f);
                                    sendBtn.setClickable(true);
                                    Toast.makeText(requireContext(), e,
                                            Toast.LENGTH_SHORT).show();
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Like / Unlike comment ─────────────────────────────────────────────
    // NOTE: Yeh method ab use nahi hota — uski jagah doLikeUnlikeComment() aur refreshComments() hain
    private void likeComment(CommentItems comment, int position) {
        doLikeUnlikeComment(comment, position);
    }

    // ── DELETE comment ────────────────────────────────────────────────────
    @SuppressLint("NotifyDataSetChanged")
    private void deleteComment(CommentItems comment) {
        try {
            if (!isAdded()) return;
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Comment")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete", (d, w) -> {
                        try {
                            UtilMethods.INSTANCE.deleteReelComment(
                                    comment.getCommentId(), null,
                                    new UtilMethods.ApiCallBackMulti() {
                                        @Override
                                        public void onSuccess(Object r) {
                                            try {
                                                if (!isAdded()) return;
                                                requireActivity().runOnUiThread(() -> {
                                                    try {
                                                        commentList.remove(comment);
                                                        commentsAdapter.notifyDataSetChanged();
                                                        updateHeader(commentList.size());

                                                        if (commentList.isEmpty()) {
                                                            emptyView.setVisibility(View.VISIBLE);
                                                            commentsRecycler.setVisibility(View.GONE);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(String e) {
                                            try {
                                                if (!isAdded()) return;
                                                requireActivity().runOnUiThread(() ->
                                                        Toast.makeText(requireContext(), e,
                                                                Toast.LENGTH_SHORT).show());
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Cancel reply → back to top-level ─────────────────────────────────
    @SuppressLint("NotifyDataSetChanged")
    private void cancelReply() {
        try {
            replyingTo = null;
            parentCommentId = 0;
            isShowingReplies = false;

            replyIndicator.setVisibility(View.GONE);
            commentInput.setHint("Add a comment...");
            hideKeyboard();

            commentList.clear();
            commentsAdapter.notifyDataSetChanged();
            pageNumber = 1;
            hasMore = true;
            loadComments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateHeader(int count) {
        try {
            if (!isShowingReplies) {
                totalCommentCount = count;
                if (commentCountCallback != null) {
                    commentCountCallback.onCountChanged(reelId, totalCommentCount);
                }
            }
            String title = isShowingReplies ? "Replies" : "Comments";
            commentCountHeader.setText(count + " " + title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showKeyboard() {
        try {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.showSoftInput(commentInput,
                        android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard() {
        try {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getView() != null)
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (commentsRecycler != null) commentsRecycler.setAdapter(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // CommentsAdapter
    // ════════════════════════════════════════════════════════════════════
    static class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CVH> {

        interface OnReplyClick {
            void onReply(CommentItems c);
        }

        interface OnDeleteClick {
            void onDelete(CommentItems c);
        }

        interface OnLikeClick {
            void onLike(CommentItems c, int position);
        }

        private final List<CommentItems> list;
        private final OnReplyClick replyL;
        private final OnDeleteClick deleteL;
        private final OnLikeClick likeL;

        CommentsAdapter(List<CommentItems> list, OnReplyClick r,
                        OnDeleteClick d, OnLikeClick l) {
            this.list = list;
            this.replyL = r;
            this.deleteL = d;
            this.likeL = l;
        }

        @NonNull
        @Override
        public CVH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new CVH(LayoutInflater.from(p.getContext())
                    .inflate(R.layout.item_comment, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CVH h, int pos) {
            try {
                h.bind(list.get(pos), pos, replyL, deleteL, likeL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class CVH extends RecyclerView.ViewHolder {
            ImageView avatar, btnLikeComment;
            TextView userName, commentText, timeAgo, replyBtn,
                    showRepliesBtn, likeCountTv;

            CVH(View v) {
                super(v);
                avatar = v.findViewById(R.id.commentAvatar);
                userName = v.findViewById(R.id.commentUserName);
                commentText = v.findViewById(R.id.commentText);
                timeAgo = v.findViewById(R.id.commentTime);
                btnLikeComment = v.findViewById(R.id.btnLikeComment);
                likeCountTv = v.findViewById(R.id.commentLikeCount);
                replyBtn = v.findViewById(R.id.btnReply);
                showRepliesBtn = v.findViewById(R.id.showRepliesBtn);
            }

            @SuppressLint("SetTextI18n")
            void bind(CommentItems c, int position,
                      OnReplyClick replyL, OnDeleteClick deleteL,
                      OnLikeClick likeL) {
                try {
                    userName.setText(c.getFullName());
                    commentText.setText(c.getCommentText());
                    timeAgo.setText(c.getCreatedAt());

                    Glide.with(itemView.getContext())
                            .load(c.getProfilePictureUrl())
                            .transform(new CircleCrop())
                            .placeholder(R.drawable.circle_background)
                            .error(R.drawable.circle_background)
                            .into(avatar);

                    // ── Like UI — sirf display, toggle nahi ──────────────
                    updateLikeUI(c);

                    // ── Like button click ─────────────────────────────────
                    btnLikeComment.setOnClickListener(v -> {
                        // Callback ko call karo — wahi decide karega API
                        if (likeL != null) likeL.onLike(c, getBindingAdapterPosition());
                    });

                    // ── Reply ─────────────────────────────────────────────
                    replyBtn.setOnClickListener(v -> {
                        if (replyL != null) replyL.onReply(c);
                    });

                    // ── Long press delete ─────────────────────────────────
                    itemView.setOnLongClickListener(v -> {
                        if (c.isOwnerComment() && deleteL != null) {
                            deleteL.onDelete(c);
                            return true;
                        }
                        return false;
                    });

                    // ── View Replies button ───────────────────────────────
                    if (c.getReplyCount() > 0) {
                        showRepliesBtn.setVisibility(View.VISIBLE);
                        showRepliesBtn.setText("▼  View " + c.getReplyCount() + " replies");
                        showRepliesBtn.setOnClickListener(v -> {
                            if (replyL != null) replyL.onReply(c);
                        });
                    } else {
                        showRepliesBtn.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // ── Sirf display karo — state toggle mat karo ─────────────────
            void updateLikeUI(CommentItems c) {
                try {
                    btnLikeComment.setImageResource(
                            c.isLiked()
                                    ? R.drawable.ic_favorite_filled
                                    : R.drawable.ic_favorite_border
                    );
                    btnLikeComment.setColorFilter(
                            c.isLiked() ? 0xFFFF3B30 : 0xFF888888
                    );
                    if (c.getLikeCount() > 0) {
                        likeCountTv.setVisibility(View.VISIBLE);
                        likeCountTv.setText(formatCount(c.getLikeCount()));
                    } else {
                        likeCountTv.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private String formatCount(int count) {
                if (count >= 1_000_000)
                    return String.format(Locale.getDefault(),
                            "%.1fM", count / 1_000_000.0);
                if (count >= 1_000)
                    return String.format(Locale.getDefault(),
                            "%.1fK", count / 1_000.0);
                return String.valueOf(count);
            }
        }
    }
}