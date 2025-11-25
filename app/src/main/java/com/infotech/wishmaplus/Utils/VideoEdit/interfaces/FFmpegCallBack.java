package com.infotech.wishmaplus.Utils.VideoEdit.interfaces;

import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;

/**
 * Created by Vishnu Agarwal on 09-10-2024.
 */

public interface FFmpegCallBack {
    void process(LogMessage logMessage );
    void statisticsProcess(Statistics statistics ) ;
    void success();
    void cancel();
    void failed();
}
