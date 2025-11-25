package com.infotech.wishmaplus.Utils.VideoEdit.AutoPlayVideo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech.wishmaplus.Adapter.MultiContentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by krupenghetiya on 16/12/16.
 */

public class CustomRecyclerView extends RecyclerView {

    private Activity _act;
    private boolean playOnlyFirstVideo = false;
    /*private boolean downloadVideos = false;*/
    private boolean checkForMp4 = true;
    private float visiblePercent = 100.0f;
    private String downloadPath = Environment.getExternalStorageDirectory() + "/MyVideo";
    private VideoUtils videoUtils;
    public PlayerView playingVideoView;
    public ImageView thumbnail, playBtn;
    private boolean isScreenPaused = false;
    public HashMap<PlayerView, Long> playingTimeMap = new HashMap<>();
    public HashMap<PlayerView, Boolean> playingPauseMap = new HashMap<>();
    Handler handler = new Handler(Looper.getMainLooper());
    List<Runnable> runnables = new ArrayList<>();
    private OnScrollListener onScrollListener;

    public CustomRecyclerView(Context context) {
        super(context);
        videoUtils = new VideoUtils();
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        videoUtils = new VideoUtils();

    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        videoUtils = new VideoUtils();
    }

    public void setActivity(Activity _act) {
        this._act = _act;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
       // addCustomOnScrollListener();

    }

   /* @Override
    public void addOnScrollListener(@NonNull OnScrollListener listener) {
        super.addOnScrollListener(listener);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
    }*/

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if(onScrollListener!=null){
            onScrollListener.onScrolled(dx, dy);
        }
        playAvailableVideos();
    }

    public void setScrollListener(OnScrollListener onScrollListener){
        this.onScrollListener=onScrollListener;
    }

    public interface OnScrollListener{
        void onScrolled(int dx, int dy);
    }

    /*private void addCustomOnScrollListener() {
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //playAvailableVideos(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                playAvailableVideos();

            }
        });
    }*/

    public void playAvailableVideos() {


        int firstVisiblePosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int lastVisiblePosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
//            Log.d("trace", "firstVisiblePosition: " + firstVisiblePosition + " |lastVisiblePosition: " + lastVisiblePosition);
        if (firstVisiblePosition >= 0) {
            Rect rect_parent = new Rect();
            getGlobalVisibleRect(rect_parent);
//                        Log.d("pos", "recyclerview left: " + rect_parent.left + " | right: " + rect_parent.right + " | top: " + rect_parent.top + " | bottom: " + rect_parent.bottom);
            if (playOnlyFirstVideo) {
                boolean foundFirstVideo = false;
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    final ViewHolder holder = findViewHolderForAdapterPosition(i);
                    try {
                        if (holder instanceof MultiContentAdapter.VideoViewHolder) {
                            MultiContentAdapter.VideoViewHolder vHolder = (MultiContentAdapter.VideoViewHolder) holder;

                            if (i >= 0 && vHolder != null || !checkForMp4) {
                                int[] location = new int[2];
                                vHolder.videoView.getLocationOnScreen(location);
                                Rect rect_child = new Rect(location[0], location[1], location[0] + vHolder.videoView.getWidth(), location[1] + vHolder.videoView.getHeight());
                                float rect_parent_area = (rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top);
                                float x_overlap = Math.max(0, Math.min(rect_child.right, rect_parent.right) - Math.max(rect_child.left, rect_parent.left));
                                float y_overlap = Math.max(0, Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(rect_child.top, rect_parent.top));
                                float overlapArea = x_overlap * y_overlap;
                                float percent = (overlapArea / rect_parent_area) * 100.0f;
                                if (!foundFirstVideo && percent >= visiblePercent) {
                                    foundFirstVideo = true;

                                        if (playingVideoView != null /*&& playingVideoView.getPlayer().isPlaying()*/ && playingVideoView != vHolder.videoView) {
                                            if(playingVideoView.getPlayer().getCurrentPosition()>0) {
                                                playingTimeMap.put(playingVideoView, playingVideoView.getPlayer().getCurrentPosition());
                                            }
                                            playingVideoView.getPlayer().pause();
                                            playingPauseMap.put(playingVideoView,true);
                                            thumbnail.setVisibility(View.VISIBLE);
                                            playBtn.setVisibility(View.VISIBLE);
                                            playingVideoView = null;
                                            thumbnail = null;
                                            playBtn = null;
                                        }
                                    Runnable myRunnable = () -> {

                                        if (!vHolder.videoView.getPlayer().isPlaying() && playingTimeMap.containsKey(vHolder.videoView) &&
                                                vHolder.videoView.getPlayer().getCurrentPosition() !=playingTimeMap.get(vHolder.videoView)) {
                                            vHolder.videoView.getPlayer().seekTo(playingTimeMap.get(vHolder.videoView));
                                        }
                                        if (!vHolder.videoView.getPlayer().isPlaying()) {
                                           playingPauseMap.put(vHolder.videoView,false);
                                            vHolder.videoView.getPlayer().play();
                                        }
                                        playingVideoView = vHolder.videoView;
                                        thumbnail = vHolder.thumbnail;
                                        playBtn = vHolder.playBtn;
                                    };
                                    vHolder.thumbnail.setVisibility(View.GONE);
                                    vHolder.playBtn.setVisibility(View.GONE);

                                    handler.post(myRunnable);
                                    runnables.add(myRunnable);
                                } else {
                                    if(vHolder.videoView.getPlayer().getCurrentPosition()>0) {
                                        playingTimeMap.put(vHolder.videoView, vHolder.videoView.getPlayer().getCurrentPosition());
                                    }
                                    playingPauseMap.put(vHolder.videoView,true);
                                    vHolder.videoView.getPlayer().pause();
                                    vHolder.videoView.getPlayer().setPlayWhenReady(false);
                                    vHolder.thumbnail.setVisibility(View.VISIBLE);
                                    vHolder.playBtn.setVisibility(View.VISIBLE);
                                    playingVideoView = null;
                                    thumbnail = null;
                                    playBtn = null;

                                }
                            } else {
                                if(vHolder.videoView.getPlayer().getCurrentPosition()>0) {
                                    playingTimeMap.put(vHolder.videoView, vHolder.videoView.getPlayer().getCurrentPosition());
                                }
                                playingPauseMap.put(vHolder.videoView,true);
                                vHolder.videoView.getPlayer().pause();
                                vHolder.videoView.getPlayer().setPlayWhenReady(false);
                                vHolder.thumbnail.setVisibility(View.VISIBLE);
                                vHolder.playBtn.setVisibility(View.VISIBLE);
                                playingVideoView = null;
                                thumbnail = null;
                                playBtn = null;
                            }
                        } else {
                                /*if (playingVideoView != null && playingVideoView.getPlayer().isPlaying()) {
                                     if(playingVideoView.getPlayer().getCurrentPosition()>0) {
                                                playingTimeMap.put(playingVideoView, playingVideoView.getPlayer().getCurrentPosition());
                                            }
                                    playingVideoView.getPlayer().pause();
                                    playingPauseMap.put(playingVideoView,true);
                                    thumbnail.setVisibility(View.VISIBLE);
                                    playBtn.setVisibility(View.VISIBLE);
                                    thumbnail = null;
                                    playBtn = null;
                                    playingVideoView = null;
                                }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    final ViewHolder holder = findViewHolderForAdapterPosition(i);
                    try {
                        if (holder instanceof MultiContentAdapter.VideoViewHolder) {
                            MultiContentAdapter.VideoViewHolder vHolder = (MultiContentAdapter.VideoViewHolder) holder;

                            if (i >= 0 && vHolder != null || !checkForMp4) {
                                int[] location = new int[2];
                                vHolder.videoView.getLocationOnScreen(location);
                                Rect rect_child = new Rect(location[0], location[1], location[0] + vHolder.videoView.getWidth(), location[1] + vHolder.videoView.getHeight());
                                float rect_parent_area = (rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top);
                                float x_overlap = Math.max(0, Math.min(rect_child.right, rect_parent.right) - Math.max(rect_child.left, rect_parent.left));
                                float y_overlap = Math.max(0, Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(rect_child.top, rect_parent.top));
                                float overlapArea = x_overlap * y_overlap;
                                float percent = (overlapArea / rect_parent_area) * 100.0f;
                                if (percent >= visiblePercent) {

                                    if (playingVideoView != null /*&& playingVideoView.getPlayer().isPlaying()*/ && playingVideoView != vHolder.videoView) {
                                        if(playingVideoView.getPlayer().getCurrentPosition()>0) {
                                            playingTimeMap.put(playingVideoView, playingVideoView.getPlayer().getCurrentPosition());
                                        }
                                        playingVideoView.getPlayer().pause();
                                        playingPauseMap.put(playingVideoView,true);
                                        thumbnail.setVisibility(View.VISIBLE);
                                        playBtn.setVisibility(View.VISIBLE);
                                        playingVideoView = null;
                                        thumbnail = null;
                                        playBtn = null;
                                    }
                                    Runnable myRunnable = () -> {

                                        if (!vHolder.videoView.getPlayer().isPlaying() && playingTimeMap.containsKey(vHolder.videoView) &&
                                                vHolder.videoView.getPlayer().getCurrentPosition() !=playingTimeMap.get(vHolder.videoView)) {
                                            vHolder.videoView.getPlayer().seekTo(playingTimeMap.get(vHolder.videoView));
                                        }
                                        if (!vHolder.videoView.getPlayer().isPlaying()) {
                                            playingPauseMap.put(vHolder.videoView,false);
                                            vHolder.videoView.getPlayer().play();
                                        }
                                        playingVideoView = vHolder.videoView;
                                        thumbnail = vHolder.thumbnail;
                                        playBtn = vHolder.playBtn;
                                    };
                                    vHolder.thumbnail.setVisibility(View.GONE);
                                    vHolder.playBtn.setVisibility(View.GONE);

                                    handler.post(myRunnable);
                                    runnables.add(myRunnable);
                                } else {
                                    if(vHolder.videoView.getPlayer().getCurrentPosition()>0) {
                                        playingTimeMap.put(vHolder.videoView, vHolder.videoView.getPlayer().getCurrentPosition());
                                    }
                                    playingPauseMap.put(vHolder.videoView,true);
                                    vHolder.videoView.getPlayer().pause();
                                    vHolder.videoView.getPlayer().setPlayWhenReady(false);
                                    vHolder.thumbnail.setVisibility(View.VISIBLE);
                                    vHolder.playBtn.setVisibility(View.VISIBLE);
                                    playingVideoView = null;
                                    thumbnail = null;
                                    playBtn = null;

                                }
                            } else {
                                if(vHolder.videoView.getPlayer().getCurrentPosition()>0) {
                                    playingTimeMap.put(vHolder.videoView, vHolder.videoView.getPlayer().getCurrentPosition());
                                }
                                playingPauseMap.put(vHolder.videoView,true);
                                vHolder.videoView.getPlayer().pause();
                                vHolder.videoView.getPlayer().setPlayWhenReady(false);
                                vHolder.thumbnail.setVisibility(View.VISIBLE);
                                vHolder.playBtn.setVisibility(View.VISIBLE);
                                playingVideoView = null;
                                thumbnail = null;
                                playBtn = null;
                            }
                        } else {
                                /*if (playingVideoView != null && playingVideoView.getPlayer().isPlaying()) {
                                     if(playingVideoView.getPlayer().getCurrentPosition()>0) {
                                                playingTimeMap.put(playingVideoView, playingVideoView.getPlayer().getCurrentPosition());
                                            }
                                    playingVideoView.getPlayer().pause();
                                    playingPauseMap.put(playingVideoView,true);
                                    thumbnail.setVisibility(View.VISIBLE);
                                    playBtn.setVisibility(View.VISIBLE);
                                    thumbnail = null;
                                    playBtn = null;
                                    playingVideoView = null;
                                }*/
                        }
                    } catch (Exception e) {

                    }

                }
            }
        }

    }

    public boolean isScreenPaused() {
        return isScreenPaused;
    }

    public void playVideo() {
        isScreenPaused = false;
        if (playingVideoView != null && !playingVideoView.getPlayer().isPlaying()) {
            if (playingTimeMap.containsKey(playingVideoView)) {
                playingVideoView.getPlayer().seekTo(playingTimeMap.get(playingVideoView));
            }
            playingVideoView.getPlayer().play();
            if (thumbnail != null) {
                thumbnail.setVisibility(View.GONE);
            }
            if (playBtn != null) {
                playBtn.setVisibility(View.GONE);
            }
        }
    }

    public void pauseVideo() {
        isScreenPaused = true;
        if (playingVideoView != null /*&& playingVideoView.getPlayer().isPlaying()*/) {
            playingTimeMap.put(playingVideoView, playingVideoView.getPlayer().getCurrentPosition());
            playingVideoView.getPlayer().pause();
            playingVideoView.getPlayer().setPlayWhenReady(false);
            if (thumbnail != null) {
                thumbnail.setVisibility(View.VISIBLE);
            }
            if (playBtn != null) {
                playBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public void deleteVideo(/*int position, */PlayerView playerView) {
        if (playingVideoView != null && playerView==playingVideoView) {
            playingVideoView.getPlayer().pause();
            playingVideoView.getPlayer().stop();
           // playingVideoView.getPlayer().release();
            playingVideoView = null;
            thumbnail = null;
            playBtn = null;
        }

       /* if (findViewHolderForAdapterPosition(position) instanceof MultiContentAdapter.VideoViewHolder) {
            final MultiContentAdapter.VideoViewHolder cvh = (MultiContentAdapter.VideoViewHolder) findViewHolderForAdapterPosition(position);*/
            if(playingPauseMap.containsKey(playerView)) {
                playingPauseMap.remove(playerView);
            }
            if(playingTimeMap.containsKey(playerView)) {
                playingTimeMap.remove(playerView);
            }
        playerView.getPlayer().pause();
        playerView.getPlayer().stop();
        //playerView.getPlayer().release();

       /* }*/
    }

    public void destroyVideo() {
        playingPauseMap.clear();
        playingTimeMap.clear();
        if (playingVideoView != null) {
            playingVideoView.getPlayer().pause();
            playingVideoView.getPlayer().stop();
            playingVideoView.getPlayer().release();
            playingVideoView = null;
            thumbnail = null;
            playBtn = null;

        }
        for (int i = 0; i < getChildCount(); i++) {
            // RecyclerView.ViewHolder a = findViewHolderForAdapterPosition(i);
            if (findViewHolderForAdapterPosition(i) instanceof MultiContentAdapter.VideoViewHolder) {
                final MultiContentAdapter.VideoViewHolder cvh = (MultiContentAdapter.VideoViewHolder) findViewHolderForAdapterPosition(i);
                /*if(playingPauseMap.containsKey(cvh.videoView)) {
                    playingPauseMap.remove(cvh.videoView);
                }*/
                cvh.videoView.getPlayer().pause();
                cvh.videoView.getPlayer().stop();
                cvh.videoView.getPlayer().release();

            }
        }

        if (runnables.size() > 0) {
            for (Runnable t : runnables) {
                handler.removeCallbacksAndMessages(t);
            }
            runnables.clear();
            //handlerThread.quit();
        }
    }


    public void setPlayOnlyFirstVideo(boolean playOnlyFirstVideo) {
        this.playOnlyFirstVideo = playOnlyFirstVideo;
    }

    @Override
    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        return super.getGlobalVisibleRect(r, globalOffset);
    }


    /*public void setDownloadVideos(boolean downloadVideos) {
        this.downloadVideos = downloadVideos;
    }*/

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void preDownload(List<String> urls) {
        if (!videoUtils.isConnected(_act)) return;
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(urls);
        urls.clear();
        urls.addAll(hashSet);
        //Log.e("Video Path",downloadPath);
        Intent serviceIntent = new Intent(_act, DownloadManagerService.class);
        _act.startService(serviceIntent);
        for (int i = 0; i < urls.size(); i++) {
            videoUtils.startDownloadInBackground(_act, urls.get(i), downloadPath);
        }
    }

    public void setCheckForMp4(boolean checkForMp4) {
        this.checkForMp4 = checkForMp4;
    }


    public void setVisiblePercent(float visiblePercent) {
        this.visiblePercent = visiblePercent;
    }
}
