package com.infotech.wishmaplus;

import android.annotation.SuppressLint;
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
import com.infotech.wishmaplus.Api.Object.PackageResult;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.Utils.CustomLoader;
import com.infotech.wishmaplus.Utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class CommentsBottomSheet extends BottomSheetDialogFragment {

    private int reelId;
    private RecyclerView commentsRecycler;
    private EditText commentInput;
    private ImageView sendBtn;
    private TextView commentCountHeader;
    private CommentsAdapter commentsAdapter;
    private List<CommentItems> commentList = new ArrayList<>();
    private TextView replyIndicator;
    private CommentItems replyingTo = null;
    private ProgressBar commentsLoader;

    // Pagination
    private int pageNumber = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;
    private boolean hasMore = true;
    ProgressBar paginationLoader;
    LinearLayout emptyView;

    CustomLoader customLoader;
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
        if (getArguments() != null)
            reelId = getArguments().getInt("reel_id");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container, @Nullable Bundle saved) {
        return inf.inflate(R.layout.bottom_sheet_comments, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customLoader = new CustomLoader(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        commentsRecycler = view.findViewById(R.id.commentsRecycler);
        commentInput = view.findViewById(R.id.commentInput);
        sendBtn = view.findViewById(R.id.sendBtn);
        commentCountHeader = view.findViewById(R.id.commentCountHeader);
        replyIndicator = view.findViewById(R.id.replyIndicator);
        commentsLoader = view.findViewById(R.id.commentsLoader);
        paginationLoader = view.findViewById(R.id.paginationLoader);
        emptyView = view.findViewById(R.id.emptyView);
        // Expand fully
        if (getDialog() != null) {
            getDialog().setOnShowListener(d -> {
                FrameLayout bs = getDialog().findViewById(
                        com.google.android.material.R.id.design_bottom_sheet);
                if (bs != null) {
                    BottomSheetBehavior<FrameLayout> b =
                            BottomSheetBehavior.from(bs);
                    b.setState(BottomSheetBehavior.STATE_EXPANDED);
                    b.setSkipCollapsed(true);
                }
            });
        }
        commentsAdapter = new CommentsAdapter(commentList,
                comment -> {
                    replyingTo = comment;
                    replyIndicator.setVisibility(View.VISIBLE);
                    replyIndicator.setText(
                            "Replying to @" + comment.getFullName());
                    commentInput.setHint(
                            "Reply to " + comment.getFullName() + "...");
                    commentInput.requestFocus();
                },
                this::deleteComment // long press delete
        );

        commentsRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        commentsRecycler.setAdapter(commentsAdapter);

        // Pagination on scroll
        commentsRecycler.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                        LinearLayoutManager lm =
                                (LinearLayoutManager) rv.getLayoutManager();
                        if (lm == null) return;
                        if (!isLoading && hasMore
                                && lm.findLastVisibleItemPosition()
                                >= lm.getItemCount() - 2) {
                            loadComments();
                        }
                    }
                });

        replyIndicator.setOnClickListener(v -> cancelReply());

        commentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int st, int c, int a) {
            }

            @SuppressLint("NewApi")
            @Override
            public void onTextChanged(
                    CharSequence s, int st, int b, int c) {
                sendBtn.setAlpha(!s.isEmpty() ? 1f : 0.4f);
                sendBtn.setClickable(!s.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sendBtn.setAlpha(0.4f);
        sendBtn.setClickable(false);
        sendBtn.setOnClickListener(v -> submitComment());

        loadComments(); // initial load
    }

    // ── GET /GetReelComment ──────────────────────────────────────────────
    private void loadComments() {
        if (isLoading || !hasMore) return;
        isLoading = true;

        // First page = header loader, next pages = bottom pagination loader
        if (pageNumber == 1) {
            commentsLoader.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            paginationLoader.setVisibility(View.VISIBLE);
        }
        commentsLoader.setVisibility(View.VISIBLE);
        UtilMethods.INSTANCE.getReelComments(
                reelId, pageNumber, pageSize,customLoader,
                new UtilMethods.ApiCallBackMulti() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(Object response) {
                        GeetReelCommentsResponse data =
                               (GeetReelCommentsResponse)response;
                        requireActivity().runOnUiThread(() -> {
                            isLoading = false;
                            commentsLoader.setVisibility(View.GONE);
                            if (data.getResult()!= null
                                    && data.getResult().getComments() != null) {
                                commentList.addAll(data.getResult().getComments());
                                commentsAdapter.notifyDataSetChanged();
                                hasMore = data.getResult().isHasMore();
                                pageNumber++;
                                updateHeader(data.getResult().getTotalCount());
                            }
                        });
                    }

                    @Override
                    public void onError(String e) {
                        requireActivity().runOnUiThread(() -> {
                            isLoading = false;
                            commentsLoader.setVisibility(View.GONE);
                        });
                    }
                });
    }

    // ── POST /AddReelComment ─────────────────────────────────────────────
    @SuppressLint("NotifyDataSetChanged")
    private void submitComment() {
        String text = commentInput.getText().toString().trim();
        if (text.isEmpty()) return;
        int parentId = replyingTo != null
                ? replyingTo.getCommentId() : 0;
        UtilMethods.INSTANCE.addReelComment(
                reelId, text, parentId,customLoader,
                new UtilMethods.ApiCallBackMulti() {
                    @Override
                    public void onSuccess(Object response) {
                        requireActivity().runOnUiThread(() -> {
                            commentList.clear();
                            pageNumber = 1;
                            hasMore = true;
                            loadComments();
                            commentInput.setText("");
                            cancelReply();
                        });
                    }

                    @Override
                    public void onError(String e) {
                        Toast.makeText(requireContext(), e,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── DELETE /DeleteReelComment ────────────────────────────────────────
    @SuppressLint("NotifyDataSetChanged")
    private void deleteComment(CommentItems comment) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Comment")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (d, w) -> {
                    UtilMethods.INSTANCE.deleteReelComment(
                            comment.getCommentId(),customLoader,
                            new UtilMethods.ApiCallBackMulti() {
                                @Override
                                public void onSuccess(Object r) {
                                    requireActivity().runOnUiThread(() -> {
                                        commentList.remove(comment);
                                        commentsAdapter.notifyDataSetChanged();
                                        updateHeader(commentList.size());
                                    });
                                }

                                @Override
                                public void onError(String e) {
                                    Toast.makeText(requireContext(), e,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null).show();
    }

    private void cancelReply() {
        replyingTo = null;
        replyIndicator.setVisibility(View.GONE);
        commentInput.setHint("Add a comment...");
    }

    @SuppressLint("SetTextI18n")
    private void updateHeader(int count) {
        commentCountHeader.setText(count + " Comments");
    }

    // ════════════════════════════════════════════════════════════════════
    // Adapter
    // ════════════════════════════════════════════════════════════════════
    static class CommentsAdapter extends
            RecyclerView.Adapter<CommentsAdapter.CVH> {

        interface OnReplyClick {
            void onReply(CommentItems c);
        }

        interface OnDeleteClick {
            void onDelete(CommentItems c);
        }

        private final List<CommentItems> list;
        private final OnReplyClick replyL;
        private final OnDeleteClick deleteL;

        CommentsAdapter(List<CommentItems> list,
                        OnReplyClick r, OnDeleteClick d) {
            this.list = list;
            this.replyL = r;
            this.deleteL = d;
        }

        @NonNull
        @Override
        public CVH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new CVH(LayoutInflater.from(p.getContext())
                    .inflate(R.layout.item_comment, p, false));
        }

        @Override
        public void onBindViewHolder(
                @NonNull CVH h, int pos) {
            h.bind(list.get(pos), replyL, deleteL);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class CVH extends RecyclerView.ViewHolder {
            ImageView avatar;
            TextView userName, commentText, timeAgo, replyBtn;
            ImageView btnLikeComment;

            CVH(View v) {
                super(v);
                avatar = v.findViewById(R.id.commentAvatar);
                userName = v.findViewById(R.id.commentUserName);
                commentText = v.findViewById(R.id.commentText);
                timeAgo = v.findViewById(R.id.commentTime);
                btnLikeComment = v.findViewById(R.id.btnLikeComment);
                replyBtn = v.findViewById(R.id.btnReply);

            }

            void bind(CommentItems c,
                      OnReplyClick replyL, OnDeleteClick deleteL) {
                userName.setText(c.getFullName());
                commentText.setText(c.getCommentText());
                timeAgo.setText(c.getCreatedAt());

                Glide.with(itemView.getContext())
                        .load(c.getProfilePictureUrl())
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.circle_background)
                        .into(avatar);

                // Like state
                updateLike(c);
                btnLikeComment.setOnClickListener(v -> {
                    c.setLiked(!c.isLiked());
                    updateLike(c);
                });

                // Reply
                replyBtn.setOnClickListener(v -> replyL.onReply(c));

                // Long press = delete (only if isOwner)
                itemView.setOnLongClickListener(v -> {
                    if (c.isOwner()) {
                        deleteL.onDelete(c);
                        return true;
                    }
                    return false;
                });
            }

            private void updateLike(CommentItems c) {
                btnLikeComment.setImageResource(c.isLiked()
                        ? R.drawable.ic_favorite_filled
                        : R.drawable.ic_favorite_border);
                btnLikeComment.setColorFilter(
                        c.isLiked() ? 0xFFFF3B30 : 0xFF888888);
            }
        }
    }
}