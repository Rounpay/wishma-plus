package com.infotech.wishmaplus.Api.Request;

public class BlockUserRequest {

    private String userId;
    private int blockId;

    public BlockUserRequest() {
        // Required empty constructor for serialization
    }

    public BlockUserRequest(String userId, int blockId) {
        this.userId = userId;
        this.blockId = blockId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
}

