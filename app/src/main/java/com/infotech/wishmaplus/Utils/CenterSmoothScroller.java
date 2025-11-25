package com.infotech.wishmaplus.Utils;

import android.content.Context;

import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * Created by Vishnu Agarwal on 10-10-2024.
 */

public class CenterSmoothScroller extends LinearSmoothScroller {

    public CenterSmoothScroller(Context context) {
        super(context);
    }

    @Override
    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
    }
}
