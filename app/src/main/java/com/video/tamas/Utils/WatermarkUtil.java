package com.video.tamas.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.video.tamas.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WatermarkUtil {
    private static FFmpeg ffmpeg;
    private static OnMergingListener onMergingListener;

    public static void addWaterMarkOnVideo(final String src, final String dst, final Context context,MaterialDialog materialDialog) {

        Runnable runnable = () -> {
            int duration = (int) videoDurationFromUrl(src);
            System.out.println("url video duration:" + duration);
            String[] cmd = new String[]{"-y", "-i", src, "-i", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "logo.png", "-i", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "logo.png", "-filter_complex", "[0:v][1:v]overlay=5:5:enable='between(t,0," + duration / 2 + ")'[one];[one][2:v]overlay=W-w-5:H-h-5:enable='between(t," + duration / 2 + "," + duration + ")'", "-codec:a", "copy", "-preset", "ultrafast", "-crf", "28", "-strict", "experimental", dst};
            saveImage(context);
            ffmpeg = FFmpeg.getInstance(context);
            try {
                ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                    @Override
                    public void onFailure() {
                    }

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {
                        //showToastMsg(context, "binary load finish");
                    }
                });

                try {
                    ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) {
                            // showToastMsg(context, message);
                        }

                        @Override
                        public void onProgress(String message) {
                        }

                        @Override
                        public void onFailure(String message) {
                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }
                        }

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onFinish() {
                            AppUtil.shareVideo("Tamas", dst);
                            if (materialDialog != null && materialDialog.isShowing()) {
                                materialDialog.dismiss();
                            }
                            if (ffmpeg.isFFmpegCommandRunning()) {
                                ffmpeg.killRunningProcesses();
                            }
                        }
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                    if (ffmpeg.isFFmpegCommandRunning()) {
                        ffmpeg.killRunningProcesses();
                    }
                }

            } catch (FFmpegNotSupportedException e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }

    public static void addAudioOnVideo(String src, String dst, Context context, ProgressDialog progressDialog, String audioPath) {

        String[] cmd = new String[]{"-y", "-i", src, "-i", audioPath, "-c:v", "copy", "-acodec", "copy", "-map", "0:v:0", "-map", "1:a:0", "-shortest", dst};
        saveImage(context);
        ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    //showToastMsg(context, "binary load finish");
                }
            });

            try {
                ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                    @Override
                    public void onSuccess(String message) {
                        // showToastMsg(context, message);
                    }

                    @Override
                    public void onProgress(String message) {


                    }

                    @Override
                    public void onFailure(String message) {

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                        if (onMergingListener != null)
                            onMergingListener.onMerge(true);
                        progressDialog.dismiss();
                        if (ffmpeg.isFFmpegCommandRunning()) {
                            ffmpeg.killRunningProcesses();
                        }
                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
                if (ffmpeg.isFFmpegCommandRunning()) {
                    ffmpeg.killRunningProcesses();
                }
            }

        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }


    private static void saveImage(Context context) {
        //TODO Change logo.png in drawable folder for watermark

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_final);
        Bitmap bitmap = Bitmap.createScaledBitmap(bm, 130, 100, true);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, "logo.png");
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long videoDurationFromUrl(String src) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(src, new HashMap<String, String>());
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        retriever.release();
        return convertMillieToSecond(timeInMillisec); //use this duration
    }

    private static long convertMillieToSecond(long millie) {
//        long seconds = (millie / 1000);
//        long second = seconds % 60;
//        long minute = (seconds / 60) % 60;
//        long hour = (seconds / (60 * 60)) % 24;
//
//        String result = "";
//        if (hour > 0) {
//            return String.format("%02d:%02d:%02d", hour, minute, second);
//        } else {
//            return String.format("%02d:%02d", minute, second);
//        }

        return TimeUnit.MILLISECONDS.toSeconds(millie);
    }

//    private static void videoDuartionFromUrl1(String src)
//    {
//        FFmpegMediaMetadataRetriever mFFmpegMediaMetadataRetriever = new MediaMetadataRetriever();
//        mFFmpegMediaMetadataRetriever .setDataSource("Your video url");
//        String mVideoDuration =  mFFmpegMediaMetadataRetriever .extractMetadata(FFmpegMediaMetadataRetriever .METADATA_KEY_DURATION);
//        long mTimeInMilliseconds= Long.parseLong(mVideoDuration);
//    }

    public interface OnMergingListener {
        void onMerge(boolean merge);
    }

    public static void setOnMergingListener(OnMergingListener listener) {
        onMergingListener = listener;
    }
}

