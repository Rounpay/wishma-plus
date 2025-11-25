package com.infotech.wishmaplus.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.CommentsAdapter;
import com.infotech.wishmaplus.Api.Object.CommentResult;
import com.infotech.wishmaplus.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Vishnu Agarwal on 21-10-2024.
 */

public class CommentDialog {

    private Dialog alertDialogComment;
    private Activity context;
    private  PreferencesManager tokenManager;

    String replyFullName = "";
    CommentResult commentParent;
    CommentResult commentReply;
    CommentResult commentReplyReply;
    int positionParent = -1;
    int positionReply = -1;
    int positionReplyReply = -1;


    public CommentDialog(Activity context,PreferencesManager tokenManager){
        this.context=context;
        this.tokenManager=tokenManager;
    }


    public void showCommentsDialog(String postId,CallBack callBack) {

        if (alertDialogComment != null && alertDialogComment.isShowing()) {
            return;
        }

        alertDialogComment = new Dialog(context, R.style.full_screen_dialog);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_comments, null);
        alertDialogComment.setContentView(dialogView);
        Objects.requireNonNull(alertDialogComment.getWindow()).setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
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
        List<CommentResult> listComment = new ArrayList<>();


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
            /*editTextComment.setText(fullName);*/
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
                listComment.addAll((List<CommentResult>) object);
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
                        CommentResult comment = (CommentResult) object;
                        if (commentReplyReply != null && positionReplyReply != -1) {
                            if (listComment.get(positionParent).getReplies().get(positionReply).getReplies().get(positionReplyReply).getReplies() != null) {
                                ArrayList<CommentResult> replyList = listComment.get(positionParent).getReplies().get(positionReply).getReplies().get(positionReplyReply).getReplies();
                                replyList.add(0, comment);
                                listComment.get(positionParent).getReplies().get(positionReply).getReplies().get(positionReplyReply).setReplies(replyList);
                            } else {
                                ArrayList<CommentResult> replyList = new ArrayList<>();
                                replyList.add(comment);
                                listComment.get(positionParent).getReplies().get(positionReply).getReplies().get(positionReplyReply).setReplies(replyList);
                            }
                            commentsAdapter.notifyItemChanged(positionParent);
                            commentsAdapter.notifyItemRangeChanged(positionParent, listComment.size());
                            recyclerViewComments.smoothScrollToPosition(positionParent);
                        } else if (commentReply != null && positionReply != -1) {
                            if (listComment.get(positionParent).getReplies().get(positionReply).getReplies() != null) {
                                ArrayList<CommentResult> replyList = listComment.get(positionParent).getReplies().get(positionReply).getReplies();
                                replyList.add(0, comment);
                                listComment.get(positionParent).getReplies().get(positionReply).setReplies(replyList);
                            } else {
                                ArrayList<CommentResult> replyList = new ArrayList<>();
                                replyList.add(comment);
                                listComment.get(positionParent).getReplies().get(positionReply).setReplies(replyList);
                            }
                            commentsAdapter.notifyItemChanged(positionParent);
                            commentsAdapter.notifyItemRangeChanged(positionParent, listComment.size());
                            recyclerViewComments.smoothScrollToPosition(positionParent);
                        } else if (commentParent != null && positionParent != -1) {
                            if (listComment.get(positionParent).getReplies() != null) {
                                ArrayList<CommentResult> replyList = listComment.get(positionParent).getReplies();
                                replyList.add(0, comment);
                                listComment.get(positionParent).setReplies(replyList);
                            } else {
                                ArrayList<CommentResult> replyList = new ArrayList<>();
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
                        if(callBack!=null){
                            callBack.onChange(listComment.size());
                        }
                       /* commentCountTv.setText(listComment.size() + " Comments");
                        commentCountTv.setVisibility(View.VISIBLE);
                        contentList.get(position).setTotalComments(listComment.size());*/
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


    }


    public interface CallBack{
        void onChange(int size);
    }
}
