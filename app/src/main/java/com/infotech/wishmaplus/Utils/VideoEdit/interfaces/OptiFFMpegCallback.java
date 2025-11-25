package com.infotech.wishmaplus.Utils.VideoEdit.interfaces;

import java.io.File;

/**
 * Created by Vishnu Agarwal on 08-10-2024.
 */

public interface OptiFFMpegCallback {
    void onProgress(String progress );

    void onSuccess(File convertedFile , int type );

    void onFailure(Exception error);

   // void onNotAvailable(Exception error);

    void onCancel();
}
