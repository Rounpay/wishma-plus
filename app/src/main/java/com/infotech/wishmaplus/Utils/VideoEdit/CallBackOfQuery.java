package com.infotech.wishmaplus.Utils.VideoEdit;

import android.os.Handler;
import android.os.Looper;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.infotech.wishmaplus.Utils.VideoEdit.interfaces.FFmpegCallBack;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


/**
 * Created by Vishnu Agarwal on 09-10-2024.
 */

public class CallBackOfQuery {
    public void callQuery(String[] query , FFmpegCallBack fFmpegCallBack ) {
        CyclicBarrier gate = new CyclicBarrier(2);
        new Thread() {
            @Override
            public void run() {
                try {
                    try {
                        gate.await();
                        process(query, fFmpegCallBack);
                    } catch (BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle the exception properly
                }
            }
        }.start();

        try {
            gate.await();
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void cancelProcess(long executionId) {
        if (executionId!=0) {
            FFmpeg.cancel(executionId);
        } else {
            FFmpeg.cancel();
        }
    }

    void cancelProcess() {
        FFmpeg.cancel();
    }

    private void process(String []query, FFmpegCallBack ffmpegCallBack) {
        Handler processHandler = new Handler(Looper.getMainLooper());
        Config.enableLogCallback(message -> {
            LogMessage  logs = new LogMessage(message.getExecutionId(), message.getLevel(), message.getText());
            processHandler.post(() -> ffmpegCallBack.process(logs));
        });
        Config.enableStatisticsCallback(statistics -> {
            Statistics statisticsLog= new Statistics(statistics.getExecutionId(),
                    statistics.getVideoFrameNumber(),
                    statistics.getVideoFps(),
                    statistics.getVideoQuality(),
                    statistics.getSize(),
                    statistics.getTime(),
                    statistics.getBitrate(),
                    statistics.getSpeed());
            processHandler.post(() -> ffmpegCallBack.statisticsProcess(statisticsLog));
        });
        FFmpeg.executeAsync(query, (executionId, returnCode) -> {
            if(returnCode== Config.RETURN_CODE_SUCCESS){
                processHandler.post(()-> {
                    ffmpegCallBack.success();
                    FFmpeg.cancel();
                });
            }else if(returnCode== Config.RETURN_CODE_CANCEL){
                processHandler.post(()-> {
                    ffmpegCallBack.cancel();
                    FFmpeg.cancel();
                });
            }else {
                processHandler.post(()-> {
                    ffmpegCallBack.failed();
                    FFmpeg.cancel();
                });
            }
        });
    }
}
