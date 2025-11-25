package com.infotech.wishmaplus.Utils.VideoEdit;

/**
 * Created by Vishnu Agarwal on 08-10-2024.
 */

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.infotech.wishmaplus.Utils.VideoEdit.interfaces.FFmpegCallBack;
import com.infotech.wishmaplus.Utils.VideoEdit.interfaces.OptiFFMpegCallback;


import java.io.File;
import java.io.IOException;

public class VideoEditor {

    private String tagName = VideoEditor.class.getSimpleName();
    private File videoFile = null;
    private File videoFileTwo = null;
    private OptiFFMpegCallback callback = null;
    private String outputFilePath = "";
    private int type;
    private String position = null;
    //for adding text
    private File font = null;
    private String text = null;
    private String color = null;
    private String size = null;
    private String border = null;
    private String BORDER_FILLED = ": box=1: boxcolor=black@0.5:boxborderw=5";
    private String BORDER_EMPTY = "";
    //for clip art
    private String imagePath = null;
    private String audioDuration;
    //for play back speed
    private boolean havingAudio = true;
    private String ffmpegFS = null;
    //for merge audio video
    private String startTime = "00:00:00";
    private String endTime = "00:00:00";
    private File audioFile = null;
    //for filter
    private String filterCommand = null;
    Activity context;

    String POSITION_BOTTOM_RIGHT = "x=w-tw-10:y=h-th-10";
    String POSITION_TOP_RIGHT = "x=w-tw-10:y=10";
    String POSITION_TOP_LEFT = "x=10:y=10";
    String POSITION_BOTTOM_LEFT = "x=10:h-th-10";
    String POSITION_CENTER_BOTTOM = "x=(main_w/2-text_w/2):y=main_h-(text_h*2)";
    String POSITION_CENTER_ALLIGN = "x=(w-text_w)/2: y=(h-text_h)/3";

    //for adding clipart
    String BOTTOM_RIGHT = "overlay=W-w-5:H-h-5";
    String TOP_RIGHT = "overlay=W-w-5:5";
    String TOP_LEFT = "overlay=5:5";
    String BOTTOM_LEFT = "overlay=5:H-h-5";
    String CENTER_ALLIGN = "overlay=(W-w)/2:(H-h)/2";
    private CallBackOfQuery callBackOfQuery;


    public VideoEditor(Activity context) {
        this.context = context;
        /*try {
            FFmpeg.getInstance(context).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.v("FFMpeg", "Failed to load FFMpeg library.");
                }

                @Override
                public void onSuccess() {
                    Log.v("FFMpeg", "FFMpeg Library loaded!");
                }

                @Override
                public void onStart() {
                    Log.v("FFMpeg", "FFMpeg Started");
                }

                @Override
                public void onFinish() {
                    Log.v("FFMpeg", "FFMpeg Stopped");
                }
            });

        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    public VideoEditor setType(int type) {
        this.type = type;
        return this;
    }

    public VideoEditor setFile(File file) {
        this.videoFile = file;
        return this;
    }

    public VideoEditor setFileTwo(File file) {
        this.videoFileTwo = file;
        return this;
    }

    public VideoEditor setAudioFile(File file) {
        this.audioFile = file;
        return this;
    }

    public VideoEditor setCallback(OptiFFMpegCallback callback) {
        this.callback = callback;
        return this;
    }

    public VideoEditor setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public VideoEditor setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
        return this;
    }

    public VideoEditor setOutputPath(String outputPath) {
        this.outputFilePath = outputPath;
        return this;
    }

    public VideoEditor setFont(File font) {
        this.font = font;
        return this;
    }

    public VideoEditor setText(String text) {
        this.text = text;
        return this;
    }

    public VideoEditor setPosition(String position) {
        this.position = position;
        return this;
    }

    public VideoEditor setColor(String color) {
        this.color = color;
        return this;
    }

    public VideoEditor setSize(String size) {
        this.size = size;
        return this;
    }

    public VideoEditor addBorder(boolean isBorder) {
        if (isBorder)
            this.border = BORDER_FILLED;
        else
            this.border = BORDER_EMPTY;
        return this;
    }

    public VideoEditor setIsHavingAudio(boolean havingAudio) {
        this.havingAudio = havingAudio;
        return this;
    }

    public VideoEditor setSpeedTempo(String playbackSpeed, String tempo) {
        this.ffmpegFS = havingAudio ? "[0:v]setpts=$playbackSpeed*PTS[v];[0:a]atempo=$tempo[a]" : "setpts=$playbackSpeed*PTS";
        return this;
    }

    public VideoEditor setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public VideoEditor setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public VideoEditor setFilter(String filter) {
        this.filterCommand = filter;
        return this;
    }

    public void main() {
        if (type == VideoEditConstant.INSTANCE.AUDIO_TRIM) {
            if (audioFile == null || !audioFile.exists()) {
                callback.onFailure(new IOException("File not exists"));
                return;
            }
            if (!audioFile.canRead()) {
                callback.onFailure(new IOException("Can't read the file. Missing permission?"));
                return;
            }
        } else if (type == VideoEditConstant.INSTANCE.IMAGE_AUDIO_MERGE) {
            if (imagePath == null || !new File(imagePath).exists()) {
                callback.onFailure(new IOException("File not exists"));
                return;
            }
            if (!new File(imagePath).canRead()) {
                callback.onFailure(new IOException("Can't read the file. Missing permission?"));
                return;
            }
        } else {
            if (videoFile == null || !videoFile.exists()) {
                callback.onFailure(new IOException("File not exists"));
                return;
            }
            if (!videoFile.canRead()) {
                callback.onFailure(new IOException("Can't read the file. Missing permission?"));
                return;
            }
        }


        File outputFile = new File(outputFilePath);
        String[] cmd = null;
        String fileExtension = audioFile.getPath().substring(audioFile.getPath().lastIndexOf(".") + 1).toLowerCase();


         if (type == VideoEditConstant.INSTANCE.AUDIO_TRIM) {

            //Audio trim - Need audio file, start time, end time & output file
             if (fileExtension.equals("mp3")|| fileExtension.equals("aac")){
                 cmd = new String[]{"-y",
                         "-i", audioFile.getPath(),
                         "-ss", startTime,
                         "-to", endTime,
                         "-c", "copy",
                         outputFile.getPath()};
             }else{
                 cmd = new String[]{"-y",
                         "-i", audioFile.getPath(),
                         "-ss", startTime,
                         "-to", endTime,
                         "-c:a", "libmp3lame",
                         outputFile.getPath()};
             }
        } else if (type == VideoEditConstant.INSTANCE.VIDEO_AUDIO_MERGE) {
            //Video audio merge - Need audio file, video file & output file


             if (fileExtension.equals("mp3") || fileExtension.equals("aac")) {
                 // MP3 or AAC input: Use -c copy (fastest)
                 cmd = new String[]{"-y",
                         "-i", videoFile.getPath(),
                         "-i", audioFile.getPath(),
                         "-c", "copy",
                         "-map", "0:v:0",
                         "-map", "1:a:0",
                         "-t", audioDuration,  // Set video duration to match audio
                         "-shortest",  // Ensure the video ends when the audio ends
                         outputFile.getPath()};
             } else {
                 // Non-MP3/AAC input (M4A, WAV, etc.): Convert audio to AAC (compatible)
                 cmd = new String[]{"-y",
                         "-i", videoFile.getPath(),
                         "-i", audioFile.getPath(),
                         "-c:v", "copy",
                         "-c:a", "aac",  // Convert audio to AAC for compatibility
                         "-map", "0:v:0",
                         "-map", "1:a:0",
                         "-t", audioDuration,
                         "-shortest",
                         outputFile.getPath()};
             }
        } else if (type == VideoEditConstant.INSTANCE.IMAGE_AUDIO_MERGE) {

            //Image audio merge - Need audio file, image file & output file
            //cmd = new String[]{"-y","-loop","1", "-i", imagePath, "-i",audioFile.getPath(), "-shortest", "-c:a", "copy","-preset","ultrafast", outputFile.getPath()};
           // cmd = new String[]{"-y", "-i", imagePath, "-i", audioFile.getPath(), "-vf", "scale=1920:1080", "-c:a", "copy", "-preset", "ultrafast", outputFile.getPath()};
            cmd = new String[]{"-y",
                   /* "-loop", "1",*/
                    "-i", imagePath,  // Path to your image file
                    "-i", audioFile.getPath(),  // Path to your audio file
                    "-vf", "scale=1920:1080",  // Rescale the image to 1080p
                    "-c:v", "libx264",  // Use H.264 video codec
                    "-c:a", "aac",  // Encode the audio as AAC
                    "-b:a", "192k",  // Audio bitrate
                    "-preset", "ultrafast",
                    "-pix_fmt", "yuv420p",// Encoding speed optimization
                    "-t", audioDuration,  // Set video duration to match audio
                    /*"-shortest",  // Ensure the video ends when the audio ends*/
                    outputFile.getPath() };

        }


        try {

            //val query:Array<String> = FFmpegQueryExtension().cutVideo(inputPath, startTimeString, endTimeString, outputPath)
            callBackOfQuery = new CallBackOfQuery();
            callBackOfQuery.callQuery(cmd, new FFmpegCallBack() {
                @Override
                public void process(@NonNull LogMessage logMessage) {
                    //Log.e("FFMPEG LOG : ", logMessage.getText());
                    callback.onProgress(logMessage.getText());
                }

                @Override
                public void statisticsProcess(@NonNull Statistics statistics) {
                    //Log.e("FFMPEG LOG : ", statistics.getVideoFrameNumber()+"");
                }

                @Override
                public void success() {
                    callback.onSuccess(outputFile, type);
                }

                @Override
                public void cancel() {
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    callback.onCancel();
                }

                @Override
                public void failed() {
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    callback.onFailure(new IOException("Something error"));
                }
            });
            /*FFmpeg.getInstance(context).execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(String message) {
                    callback.onProgress(message);
                }

                @Override
                public void onSuccess(String message) {
                    callback.onSuccess(outputFile, VideoEditConstant.INSTANCE.TYPE_VIDEO);
                }

                @Override
                public void onFailure(String message) {
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    callback.onFailure(new IOException(message));
                }

                @Override
                public void onFinish() {
                    callback.onFinish();
                }
            });*/
        } /*catch (FFmpegCommandAlreadyRunningException e2) {
            callback.onNotAvailable(e2);
        }*/ catch (Exception e) {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            callback.onFailure(e);
        }
    }

    public static int[] getVideoDimensions(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);

            // Get width and height from the metadata
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

            // Convert width and height to integers and return as an array
            return new int[]{Integer.parseInt(width), Integer.parseInt(height)};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                retriever.release();  // Always release the retriever to free resources
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopRunningProcess() {
        if (callBackOfQuery != null) {
            callBackOfQuery.cancelProcess();
        }
        //FFmpeg.getInstance(context).killRunningProcesses();
    }

    public boolean isRunning() {
        return /*FFmpeg.getInstance(context).isFFmpegCommandRunning()*/false;
    }

    public void showInProgressToast() {
        Toast.makeText(context, "Operation already in progress! Try again in a while.", Toast.LENGTH_SHORT).show();
    }
}