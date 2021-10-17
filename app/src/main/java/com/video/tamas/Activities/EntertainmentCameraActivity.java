package com.video.tamas.Activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLException;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daasuu.camerarecorder.CameraRecordListener;
import com.daasuu.camerarecorder.CameraRecorder;
import com.daasuu.camerarecorder.CameraRecorderBuilder;
import com.daasuu.camerarecorder.LensFacing;
import com.deep.videotrimmer.DeepVideoTrimmer;
import com.deep.videotrimmer.interfaces.OnTrimVideoListener;
import com.video.tamas.Adapters.CameraFilterListAdapter;
import com.video.tamas.Models.SongListModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.WatermarkUtil;
import com.video.tamas.Utils.serv;
import com.video.tamas.VideoRecordingUtil.Filters;
import com.video.tamas.VideoRecordingUtil.SampleGLView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * Created by sudamasayuki2 on 2018/07/02.
 */

public class EntertainmentCameraActivity extends AppCompatActivity implements CameraFilterListAdapter.OnSelectFilterListener, WatermarkUtil.OnMergingListener {
    public static int STATIC_INTEGER_VALUE = 500;
    public static final int TALENT_VIDEO_DURATION = 120;
    public static final int NORMAL_VIDEO_DURATION = 15;
    public static final int TALENT_VIDEO_FILE_SIZE = 1024 * 2;
    public static final int NORMAL_VIDEO_FILE_SIZE = 512 * 2;
    MediaPlayer mp;
    private CameraFilterListAdapter cameraFilterListAdapter;
    private RecyclerView rvFilter;
    private ConstraintLayout clCameraFilterLayout;
    private SampleGLView sampleGLView;
    protected CameraRecorder cameraRecorder;
    private Uri filepath;
    private TextView recordBtn;
    protected LensFacing lensFacing = LensFacing.BACK;
    protected int cameraWidth = 1280;
    protected int cameraHeight = 720;
    protected int videoWidth = 720;
    protected int videoHeight = 720;
    private AlertDialog filterDialog;
    private boolean toggleClick = false;
    private Uri fileUri;
    String filepat;
    private MaterialDialog materialDialog;
    TextView tvAddSound, tvTimeLimit, tvPercent, tvPercentValue, tvTimerPercent, tvTimerPercentValue, tvTimer;
    ImageView ivCancel, imgPlayPause, imgTimer;
    Button btnDone, btnTimerDone;
    ProgressBar progressBar;
    SeekBar sb, sbTimer;
    private boolean isVideoPause = false;
    LinearLayout llAddSound;
    PopupWindow popupWindow, TimerpopupWindow;
    int selectedSound = 0, mediaDuration;
    boolean isRecording = true, selected = false;
    private CountDownTimer mCountDownTimer;
    String songTitle, songPath = "";
    String songId = "", selectedVideoPath;
    DeviceResourceManager resourceManager;
    private int maxSelectedRecodingTime = 0;
    ImageView imgAutoStartTime;
    private String from = "";
    private String subCategoryId = "";
    private Handler videoRecodingHandler;
    private String userId;
    ArrayList<EpVideo> epVideos = new ArrayList<>();
    ImageView ivVideoFromGallery, imgVideotime;
    int REQUEST_CODE = 1001;
    int autoVideoStartTime = 0;
    private DeepVideoTrimmer videoTrimmer;
    private String finalVideoPath = null;

    protected void onCreateActivity() {
        getSupportActionBar().hide();
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recordBtn = findViewById(R.id.btn_record);
        tvAddSound = findViewById(R.id.tvAddSound);
        ivCancel = findViewById(R.id.ivCancel);
        ivVideoFromGallery = findViewById(R.id.ivVideoFromGallery);
        llAddSound = findViewById(R.id.llAddSound);
        tvTimeLimit = findViewById(R.id.tvTimeLimit);
        imgVideotime = findViewById(R.id.imgVideoTime);
        imgAutoStartTime = (ImageView) findViewById(R.id.imgAutoTimer);
        progressBar = findViewById(R.id.progressbar);
        imgPlayPause = (ImageView) findViewById(R.id.btnPlayPause);
        tvTimer = findViewById(R.id.tvTimer);
        rvFilter = findViewById(R.id.rvFilter);
        videoTrimmer = findViewById(R.id.videoTrimmer);
        //imgTimer = findViewById(R.id.imgTimer);
        Log.wtf("isRecording", String.valueOf(isRecording));
        Intent intent = getIntent();
        if (getIntent() != null) {

            if (resourceManager.getDataFromSharedPref("USE_SONG", false)) {
                songId = intent.getStringExtra("songId");
                songPath = intent.getStringExtra("songPath");
                songTitle = intent.getStringExtra("songName");
                maxSelectedRecodingTime = 15;
                tvAddSound.setText(songTitle);
                tvAddSound.setSelected(true);
                tvAddSound.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tvAddSound.setSingleLine(true);
                tvAddSound.setMarqueeRepeatLimit(-1);
                tvAddSound.setFocusableInTouchMode(true);
                tvAddSound.setFocusable(true);
                resourceManager.clearSharedPref("USE_SONG");
            }
            from = intent.getStringExtra("from");
            if (from != null)
                if (from.equalsIgnoreCase("song")) {
                    songId = intent.getStringExtra("songId");
                    songPath = intent.getStringExtra("songPath");
                    songTitle = intent.getStringExtra("songName");
                    Log.wtf("songIdByVideo", songId);
                    Log.wtf("songpathByVideo", songPath);
                    Log.wtf("songnameByVideo", songTitle);
                } else {
                    subCategoryId = intent.getStringExtra("subcategoryId");
                }
        } else {
            songId = "";
        }
        if (TextUtils.isEmpty(songTitle)) {
            tvAddSound.setText("Add a sound");
        } else {
            tvAddSound.setText(songTitle);
            tvAddSound.setSelected(true);
            tvAddSound.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tvAddSound.setSingleLine(true);
            tvAddSound.setMarqueeRepeatLimit(-1);
            tvAddSound.setFocusableInTouchMode(true);
            tvAddSound.setFocusable(true);
        }

        tvTimer.setVisibility(View.GONE);
        //here default play pause will be invisible & when recording is on it will be visible.
        imgPlayPause.setVisibility(View.INVISIBLE);
        imgPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //done
                if (isVideoPause) {


                    if (mp != null) {
                        mp.start();
                    }
                    recordBtn.setText(getString(R.string.app_record));
                    startVideoRecodingTimer(maxSelectedRecodingTime);
                    filepath = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    filepat = filepath.getPath();
                    cameraRecorder.start(filepat);
                    Log.wtf("soundDuration", String.valueOf(mediaDuration));

                    recordBtn.setText("");

                    isVideoPause = false;
                } else {
                    isVideoPause = true;
//mCountDownTimer.cancel();
                    if (videoRecodingHandler != null) {
                        videoRecodingHandler.removeCallbacksAndMessages(null);
                    }
                    if (mp != null) {
                        mp.pause();
                    }
                    cameraRecorder.stop();
                    if (selectedSound == 0) {
                        Log.w("sound", "nahi select kia");
                    } else {
                        //mp.pause();
                    }
                    //stopService(new Intent(EntertainmentCameraActivity.this, serv.class));
                    //recordBtn.setText(getString(R.string.app_record));

                }
            }
        });
        ivVideoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent(Intent.ACTION_PICK);
                videoIntent.setType("video/*");
                startActivityForResult(videoIntent, REQUEST_CODE);

            }
        });

        //for video recording limit
        if (from != null)
            if (from.equalsIgnoreCase("category")) {

                maxSelectedRecodingTime = 120;

            } else {
                maxSelectedRecodingTime = 15;
            }

        imgVideotime.setOnClickListener(v -> {
            LayoutInflater layoutInflater = (LayoutInflater) EntertainmentCameraActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = layoutInflater.inflate(R.layout.pop_up, null);
            sb = (SeekBar) customView.findViewById(R.id.sb);
            btnDone = (Button) customView.findViewById(R.id.btnDone);
            tvPercentValue = (TextView) customView.findViewById(R.id.percent_value);
            tvPercent = (TextView) customView.findViewById(R.id.percent);
            if (from != null)
                if (from.equalsIgnoreCase("category")) {
                    sb.setMax(120);
                } else {
                    sb.setMax(15);
                }
            popupWindow = new PopupWindow(customView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.showAtLocation(llAddSound, Gravity.CENTER, 0, 0);
//sb.setMax(30);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //tvPercent.setText("%");
                    tvPercentValue.setText("" + i + " Seconds");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int sbProgress = sb.getProgress();
                    if (sbProgress == 0) {
                        Toast.makeText(EntertainmentCameraActivity.this, "Please select video recording time between 1 Second to 2 minute.", Toast.LENGTH_SHORT).show();
                    } else {

                        maxSelectedRecodingTime = sbProgress;
//startVideoRecodingTimer(sbProgress);
                        popupWindow.dismiss();
                    }

                }
            });


        });


        imgAutoStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflatertimer = (LayoutInflater) EntertainmentCameraActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View custom = layoutInflatertimer.inflate(R.layout.pop_up_timer, null);
                RadioButton rbThreeSecond = (RadioButton) custom.findViewById(R.id.rb1);
                RadioButton rbFiveSecond = (RadioButton) custom.findViewById(R.id.rb2);
                RadioButton rbTenSecond = (RadioButton) custom.findViewById(R.id.rb3);
                Button btnAutoDone = (Button) custom.findViewById(R.id.btnDone);

                btnAutoDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!rbThreeSecond.isChecked() && !rbFiveSecond.isChecked() && !rbTenSecond.isChecked()) {
                            Toast.makeText(EntertainmentCameraActivity.this, "Please select Auto video startTime for proceed.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (rbThreeSecond.isChecked()) {
                                autoVideoStartTime = 3;
                            } else if (rbFiveSecond.isChecked()) {
                                autoVideoStartTime = 5;
                            } else if (rbTenSecond.isChecked()) {
                                autoVideoStartTime = 10;
                            } else {
                                autoVideoStartTime = 0;
                            }
                            TimerpopupWindow.dismiss();
                        }
                    }
                });

                //instantiate popup window
                TimerpopupWindow = new PopupWindow(custom, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                //display the popup window
                TimerpopupWindow.showAtLocation(llAddSound, Gravity.CENTER, 0, 0);
//sb.setMax(30);


            }
        });


        recordBtn.setOnClickListener(v -> {

            if (recordBtn.getText().equals(getString(R.string.app_record))) {
                if (autoVideoStartTime == 0) {
                    startVideoRecoding();
                } else {
                    tvTimer.setVisibility(View.VISIBLE);
                    tvTimer.setText("" + autoVideoStartTime);
                    startAutoTimerForAutoRecording();
                }

            } else {
                try {
                    if (!isVideoPause) {
                        //mCountDownTimer.cancel();
                        if (videoRecodingHandler != null) {
                            videoRecodingHandler.removeCallbacksAndMessages(null);
                        }
                        if (mp != null) {
                            mp.stop();
                        }
                        cameraRecorder.stop();
                        if (selectedSound == 0) {
                            Log.w("sound", "nahi select kia");
                        } else {
                            mp.stop();
                        }
                        //stopService(new Intent(EntertainmentCameraActivity.this, serv.class));
                        recordBtn.setText(getString(R.string.app_record));
                    } else {

                        if (epVideos.isEmpty() || epVideos.size() == 1) {
                            stopService(new Intent(EntertainmentCameraActivity.this, serv.class));
                            exportMp4ToGallery(getApplicationContext(), filepat);
                            Log.w("filepath", filepat);
                            if (filepath != null) {
                                final ProgressDialog ringProgressDialog = ProgressDialog.show(EntertainmentCameraActivity.this, "Tamas", "Processing your video!!", true);
                                //you usually don't want the user to stop the current process, and this will make sure of that
                                ringProgressDialog.setCancelable(false);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mergeVideos(ringProgressDialog);
                                    }
                                }, 5000);
                            }


                        } else {
                            Log.v("dip", "marging list size : " + epVideos.size());
                            final ProgressDialog ringProgressDialog = ProgressDialog.show(EntertainmentCameraActivity.this, "Tamas", "Processing your video!!", true);
                            //you usually don't want the user to stop the current process, and this will make sure of that
                            ringProgressDialog.setCancelable(false);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //ringProgressDialog.dismiss();
                                    mergeVideos(ringProgressDialog);

                                }
                            }, 5000);


                        }

                    }

                } catch (Exception e) {
                    Log.v("dip", "error in stop : " + e.getMessage());

                }
            }

        });
        findViewById(R.id.btn_flash).setOnClickListener(v -> {
            if (cameraRecorder != null && cameraRecorder.isFlashSupport()) {
                cameraRecorder.switchFlashMode();
                cameraRecorder.changeAutoFocus();
            }
        });

        findViewById(R.id.btn_switch_camera).setOnClickListener(v -> {
            releaseCamera();
            if (lensFacing == LensFacing.BACK) {
                lensFacing = LensFacing.FRONT;
            } else {
                lensFacing = LensFacing.BACK;
            }
            toggleClick = true;
        });

        findViewById(R.id.btn_filter).setOnClickListener(v -> {

            if (selected) {
                selected = false;
                rvFilter.setVisibility(View.GONE);
            } else {
                selected = true;
                rvFilter.setVisibility(View.VISIBLE);
                cameraFilterListAdapter = new CameraFilterListAdapter(EntertainmentCameraActivity.this);
                rvFilter.setAdapter(cameraFilterListAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EntertainmentCameraActivity.this, LinearLayoutManager.HORIZONTAL, false);
                rvFilter.setLayoutManager(linearLayoutManager);
                rvFilter.setHasFixedSize(true);

            }

//            try {
//                if (filterDialog == null) {
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                    builder.setTitle("Choose a filter");
//                    builder.setOnDismissListener(dialog -> {
//                        filterDialog = null;
//                    });
//
//                    final Filters[] filters = Filters.values();
//                    CharSequence[] charList = new CharSequence[filters.length];
//                    for (int i = 0, n = filters.length; i < n; i++) {
//                        charList[i] = filters[i].name();
//                    }
//                    builder.setItems(charList, (dialog, item) -> {
//                        changeFilter(filters[item]);
//                    });
//                    filterDialog = builder.show();
//                } else {
//                    filterDialog.dismiss();
//                }
//            } catch (Exception e) {
//                Log.v("dip", "filter error : " + e.getMessage());
//
//            }
        });

        findViewById(R.id.btn_image_capture).setOnClickListener(v -> {
            captureBitmap(bitmap -> {
                new Handler().post(() -> {
                    String imagePath = getImageFilePath();
                    saveAsPngImage(bitmap, imagePath);
                    exportPngToGallery(getApplicationContext(), imagePath);
                });
            });
        });


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp != null) {
                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                }
                finish();

            }
        });
        findViewById(R.id.tvAddSound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EntertainmentCameraActivity.this, SongListActivity.class);
                startActivityForResult(i, STATIC_INTEGER_VALUE);
            }
        });

    }


    private void startAutoTimerForAutoRecording() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                autoVideoStartTime = autoVideoStartTime - 1;
                if (autoVideoStartTime == 0) {
                    tvTimer.setVisibility(View.GONE);
                    startVideoRecoding();
                } else {
                    tvTimer.setText("" + autoVideoStartTime);
                    startAutoTimerForAutoRecording();

                }
            }
        }, 1000);
    }

    private void startVideoRecoding() {
        imgPlayPause.setVisibility(View.VISIBLE);
        Log.wtf("songIDSelected", songId);
        epVideos.clear();

        if (songId != null) {
            if (!songId.isEmpty()) {

                mp = MediaPlayer.create(EntertainmentCameraActivity.this, Uri.parse(songPath));
                Log.wtf("songPath", String.valueOf(Uri.parse(songPath)));
                if (mp != null) {

                    mediaDuration = mp.getDuration();
                    //startTimer();
                    mp.setLooping(false);
                    mp.start();

                }
            }

        }
        recordBtn.setText(getString(R.string.app_record));

        progressBar.setMax(maxSelectedRecodingTime);
        progressBar.setProgress(0);
//        setProgressAnimate(progressBar,0);
        startVideoRecodingTimer(maxSelectedRecodingTime);
        filepath = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        filepat = filepath.getPath();
        cameraRecorder.start(filepat);
        Log.wtf("soundDuration", String.valueOf(mediaDuration));

        recordBtn.setText("");


    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor =
                    context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void startVideoRecodingTimer(int remainingTime) {
        videoRecodingHandler = new Handler();
        videoRecodingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int remainingRecordingTime = remainingTime - 1;
                maxSelectedRecodingTime = remainingRecordingTime;
                if (remainingRecordingTime <= 0) {
                    Toast.makeText(EntertainmentCameraActivity.this, "Recoding TimeUp !", Toast.LENGTH_SHORT).show();
                    //mCountDownTimer.cancel();
                    if (mp != null) {
                        mp.stop();
                    }
                    if (cameraRecorder != null)
                        cameraRecorder.stop();
                    //stopService(new Intent(EntertainmentCameraActivity.this, serv.class));
                    recordBtn.setText(getString(R.string.app_record));
                } else {
                    int currentProgress = progressBar.getProgress() + 1;
                    progressBar.setProgress(currentProgress);
                    //setProgressAnimate(progressBar,currentProgress);
                    startVideoRecodingTimer(remainingRecordingTime);

                }
            }
        }, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (500): {
                if (resultCode == Activity.RESULT_OK) {
//                    songPath = data.getStringExtra("songPath");
//                    songTitle = data.getStringExtra("songTitle");

                    SongListModel songListModel = data.getParcelableExtra("songListModel");
                    songTitle = songListModel.getSongTitle();
                    songPath = songListModel.getSongPath();
                    songId = songListModel.getSongId();
                    Log.wtf("song", songPath);
                    Log.wtf("songTitle", songTitle);
                    // TODO Update your TextView.
                    tvAddSound.setText(songTitle);
                    tvAddSound.setSelected(true);
                    tvAddSound.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    tvAddSound.setSingleLine(true);
                    tvAddSound.setMarqueeRepeatLimit(-1);
                    tvAddSound.setFocusableInTouchMode(true);
                    tvAddSound.setFocusable(true);
                }
                break;
            }
            case (1001): {
                if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                    String path = getRealPathFromUri(EntertainmentCameraActivity.this, data.getData());
                    System.out.println("path:" + path);
                    if (videoTrimmer != null) {
//                        File root = new File(Environment.getExternalStorageDirectory(), "/TrimVideo1" + File.separator);  //you can replace RecordVideo by the specific folder where you want to save the video
//                        if (!root.exists()) {
//                            System.out.println("No directory");
//                            root.mkdirs();
//                        }
                        if (!subCategoryId.equals("0")) {
                            videoTrimmer.setVisibility(View.VISIBLE);
                            videoTrimmer.setVideoURI(Uri.parse(path));
                            videoTrimmer.setMaxDuration(TALENT_VIDEO_DURATION);
                            videoTrimmer.setMaxFileSize(TALENT_VIDEO_FILE_SIZE);
                        } else {
                            videoTrimmer.setVisibility(View.VISIBLE);
                            videoTrimmer.setVideoURI(Uri.parse(path));
                            videoTrimmer.setMaxDuration(NORMAL_VIDEO_DURATION);
                            videoTrimmer.setMaxFileSize(NORMAL_VIDEO_FILE_SIZE);

                        }
                        //   videoTrimmer.setDestinationPath(root.getAbsolutePath());
                        videoTrimmer.setOnTrimVideoListener(new OnTrimVideoListener() {
                            @Override
                            public void getResult(Uri uri) {
                                System.out.println("uri:" + uri.getPath());
                                EntertainmentCameraActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        selectedVideoPath = uri.getPath();
                                        if (selectedVideoPath != null) {
                                            final ProgressDialog ringProgressDialog = ProgressDialog.show(EntertainmentCameraActivity.this, "Tamas", "Processing your video!!", true);
                                            //you usually don't want the user to stop the current process, and this will make sure of that
                                            ringProgressDialog.setCancelable(false);
                                            new Handler().postDelayed(() -> {
                                                Intent i = new Intent(EntertainmentCameraActivity.this, EntertainmentVideoUploadActivity.class);
                                                i.putExtra("filePath", selectedVideoPath);
                                                i.putExtra("songId", "0");
                                                i.putExtra("from", from);
                                                i.putExtra("subcategoryId", subCategoryId);
                                                startActivity(i);
                                                ringProgressDialog.dismiss();
                                                finish();
                                                System.out.println("path all:" + selectedVideoPath);
                                            }, 2000);
                                        }
                                    }
                                });

                            }

                            @Override
                            public void cancelAction() {
                                materialDialog = new MaterialDialog.Builder(EntertainmentCameraActivity.this)
                                        .content("Dont want to record video ?")
                                        .contentColor(getResources().getColor(R.color.colorWhite))
                                        .backgroundColor(getResources().getColor(R.color.colorTableGrey))
                                        .positiveText("Yes")
                                        .negativeText("No")
                                        .cancelable(false)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                                materialDialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                                materialDialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });


                    }


//                    try {
//                        AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
//                        FileInputStream fis = videoAsset.createInputStream();
//                        File root = new File(Environment.getExternalStorageDirectory(), "/TrimVideo/");  //you can replace RecordVideo by the specific folder where you want to save the video
//                        if (!root.exists()) {
//                            System.out.println("No directory");
//                            root.mkdirs();
//                        }
//
//                        File file;
//                        file = new File(root, "android_" + System.currentTimeMillis() + ".mp4");
//
//                        resourceManager.addToSharedPref("TRIM_VIDEO_PATH", file.getAbsolutePath());
//                        System.out.println("file path:"+file.getAbsolutePath());
//                        FileOutputStream fos = new FileOutputStream(file);
//
//                        byte[] buf = new byte[1024];
//                        int len;
//                        while ((len = fis.read(buf)) > 0) {
//                            fos.write(buf, 0, len);
//                        }
//                        fis.close();
//                        fos.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }


                //selectedVideoPath = getRealPathFromUri(EntertainmentCameraActivity.this, data.getData());

            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpCamera();
        CameraFilterListAdapter.setOnFilterListener(this);
        WatermarkUtil.setOnMergingListener(this);

    }

    @Override
    public void onSelectFilter(Filters filters) {
        changeFilter(filters);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    private void releaseCamera() {
        if (sampleGLView != null) {
            sampleGLView.onPause();
        }

        if (cameraRecorder != null) {
            cameraRecorder.stop();
            cameraRecorder.release();
            cameraRecorder = null;
        }

        if (sampleGLView != null) {
            ((FrameLayout) findViewById(R.id.wrap_view)).removeView(sampleGLView);
            sampleGLView = null;
        }
    }


    private void setUpCameraView() {
        runOnUiThread(() -> {
            FrameLayout frameLayout = findViewById(R.id.wrap_view);
            frameLayout.removeAllViews();
            sampleGLView = null;
            sampleGLView = new SampleGLView(getApplicationContext());
            sampleGLView.setTouchListener((event, width, height) -> {
                if (cameraRecorder == null) return;
                cameraRecorder.changeManualFocusPoint(event.getX(), event.getY(), width, height);
            });
            frameLayout.addView(sampleGLView);
        });
    }


    private void setUpCamera() {
        setUpCameraView();

        cameraRecorder = new CameraRecorderBuilder(this, sampleGLView)
                //.recordNoFilter(true)
                .cameraRecordListener(new CameraRecordListener() {
                    @Override
                    public void onGetFlashSupport(boolean flashSupport) {
                        runOnUiThread(() -> {
                            findViewById(R.id.btn_flash).setEnabled(flashSupport);
                        });
                    }

                    @Override
                    public void onRecordComplete() {

                        exportMp4ToGallery(getApplicationContext(), filepat);
                        Log.w("filepath", filepat);
                        if (filepath != null) {
                            epVideos.add(new EpVideo(filepat));


                            if (!isVideoPause) {
                                if (!isFinishing()) {
                                    final ProgressDialog ringProgressDialog = ProgressDialog.show(EntertainmentCameraActivity.this, "Tamas", "Processing your video!!", true);
                                    //you usually don't want the user to stop the current process, and this will make sure of that
                                    ringProgressDialog.setCancelable(false);
                                    new Handler().postDelayed(() -> {
                                        mergeVideos(ringProgressDialog);
                                        //finish();
                                    }, 2000);
                                }
                            }
                        }


                    }

                    @Override
                    public void onRecordStart() {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("CameraRecorder", exception.toString());
                    }

                    @Override
                    public void onCameraThreadFinish() {
                        if (toggleClick) {
                            runOnUiThread(() -> {
                                setUpCamera();
                            });
                        }
                        toggleClick = false;
                    }
                })
                .videoSize(videoWidth, videoHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build();
    }

    private void changeFilter(Filters filters) {
        try {
            cameraRecorder.setFilter(Filters.getFilterInstance(filters, getApplicationContext()));
        } catch (Exception e) {
            Log.v("dip", "filter error : " + e.getMessage());
        }
    }


    private interface BitmapReadyCallbacks {
        void onBitmapReady(Bitmap bitmap);

    }

    private void captureBitmap(final BitmapReadyCallbacks bitmapReadyCallbacks) {
        sampleGLView.queueEvent(() -> {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
            Bitmap snapshotBitmap = createBitmapFromGLSurface(sampleGLView.getMeasuredWidth(), sampleGLView.getMeasuredHeight(), gl);

            runOnUiThread(() -> {
                bitmapReadyCallbacks.onBitmapReady(snapshotBitmap);
            });
        });
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2, texturePixel, blue, red, pixel;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    texturePixel = bitmapBuffer[offset1 + j];
                    blue = (texturePixel >> 16) & 0xff;
                    red = (texturePixel << 16) & 0x00ff0000;
                    pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void saveAsPngImage(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }

//    public static String getVideoFilePath() {
//        //return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "tamas1.mp4";
//        return getOutputMediaFile(MEDIA_TYPE_VIDEO);
//        //return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.mp4";
//    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("op", "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private static void exportPngToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getImageFilePath() {
        return getAndroidImageFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.png";
    }

    public static File getAndroidImageFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    @Override
    public void onBackPressed() {
        materialDialog = new MaterialDialog.Builder(EntertainmentCameraActivity.this)
                .content("Do you want exit video recording?")
                .contentColor(getResources().getColor(R.color.colorWhite))
                .backgroundColor(getResources().getColor(R.color.colorTableGrey))
                .positiveText("Yes")
                .negativeText("No")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        materialDialog.dismiss();
                        finish();
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                mp.stop();
                            }
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        materialDialog.dismiss();
                    }
                })
                .show();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer((mediaDuration)/*seconds in millis */, 1000/*interval time*/) {
            public void onTick(long millisUntilFinished) {
                Toast.makeText(EntertainmentCameraActivity.this, "You can make video of  " + millisUntilFinished / 1000 + " Sec.", Toast.LENGTH_SHORT).show();
                //tvTimerText.setText("You can make video of  " + millisUntilFinished / 1000 + " Sec.");
            }

            public void onFinish() {
                Log.wtf("timer", "finish");
                cameraRecorder.stop();
                mp.stop();
            }
        }.start();
    }

    private void mergeVideos(ProgressDialog ringProgressDialog) {
        //final ProgressDialog ringProgressDialog = ProgressDialog.show(EntertainmentCameraActivity.this, "Tamas", "Processing your video!!", true);
        //you usually don't want the user to stop the current process, and this will make sure of that
        //ringProgressDialog.setCancelable(false);
        if (epVideos.isEmpty() || epVideos.size() == 1) {
            if (!songPath.isEmpty()) {
                String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File fileName = new File(externalStorageDirectory, "android_" + System.currentTimeMillis() + ".mp4");
                finalVideoPath = fileName.getAbsolutePath();
                WatermarkUtil.addAudioOnVideo(filepat, finalVideoPath, EntertainmentCameraActivity.this, ringProgressDialog, songPath);
            } else {
                Intent i = new Intent(EntertainmentCameraActivity.this, EntertainmentVideoUploadActivity.class);
                i.putExtra("filePath", filepat);
                i.putExtra("songId", songId);
                i.putExtra("from", from);
                i.putExtra("subcategoryId", subCategoryId);
                startActivity(i);
                finish();
                ringProgressDialog.dismiss();
            }
        } else {
            filepath = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
            //filepat = filepath.getPath();

            String fileOutput = filepath.getPath();
            EpEditor.OutputOption outputOption = new EpEditor.OutputOption(fileOutput);
            outputOption.setWidth(720);
            outputOption.setHeight(1280);
            outputOption.frameRate = 25;
            outputOption.bitRate = 10; //Default
            EpEditor.merge(epVideos, outputOption, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Log.d("Status", "Success");
                    exportMp4ToGallery(getApplicationContext(), fileOutput);
                    //Log.w("filepath", filepat);
                    if (!songPath.isEmpty()) {
                        String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
                        File fileName = new File(externalStorageDirectory, "android_" + System.currentTimeMillis() + ".mp4");
                        finalVideoPath = fileName.getAbsolutePath();
                        WatermarkUtil.addAudioOnVideo(fileOutput, finalVideoPath, EntertainmentCameraActivity.this, ringProgressDialog, songPath);
                    } else {
                        Intent i = new Intent(EntertainmentCameraActivity.this, EntertainmentVideoUploadActivity.class);
                        i.putExtra("filePath", fileOutput);
                        i.putExtra("songId", songId);
                        i.putExtra("from", from);
                        i.putExtra("subcategoryId", subCategoryId);
                        startActivity(i);
                        finish();
                        ringProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure() {
                    Log.d("Progress", "failer ");
                    ringProgressDialog.dismiss();
                }

                @Override
                public void onProgress(float progress) {
                    // Get processing progress here
                    Log.d("Progress", "" + progress + fileOutput);
                    //ringProgressDialog.setMessage("Video Merging : "+progress);
                }
            });
        }
    }


    @Override
    public void onMerge(boolean merge) {
        if (merge) {
            Intent i = new Intent(EntertainmentCameraActivity.this, EntertainmentVideoUploadActivity.class);
            i.putExtra("filePath", finalVideoPath);
            i.putExtra("songId", songId);
            i.putExtra("from", from);
            i.putExtra("subcategoryId", subCategoryId);
            startActivity(i);
            finish();
        }
    }

    private void setProgressAnimate(ProgressBar pb, int progressTo) {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), progressTo * 100);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

}
