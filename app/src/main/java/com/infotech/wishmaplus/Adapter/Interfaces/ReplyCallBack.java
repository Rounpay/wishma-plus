package com.infotech.wishmaplus.Adapter.Interfaces;

import com.infotech.wishmaplus.Api.Object.CommentResult;

/**
 * Created by Vishnu Agarwal on 18-10-2024.
 */

public interface ReplyCallBack {
    void onReplyClick(CommentResult commentParent, int positionParent, CommentResult commentReply, int positionReply, CommentResult commentReplyReply, int positionReplyReply);

}
