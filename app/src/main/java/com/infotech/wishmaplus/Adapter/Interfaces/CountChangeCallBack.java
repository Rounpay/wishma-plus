package com.infotech.wishmaplus.Adapter.Interfaces;

/**
 * Created by Vishnu Agarwal on 18-10-2024.
 */

public interface CountChangeCallBack {
    void onRefresh(int typeId);
    void onChangeCallBack(String editPostId,Boolean isLiked,int commentCount, int likeCount, int shareCount, int position, int deletePosition );

}
