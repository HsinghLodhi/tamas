package com.video.tamas.Activities;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.video.tamas.Adapters.CategorywiseVideoListRecyclerAdapter;
import com.video.tamas.Adapters.CommentListRecyclerAdapter;
import com.video.tamas.Adapters.VideoBySongDetailedRecyclerAdapter;
import com.video.tamas.BuildConfig;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.CommentModel;
import com.video.tamas.Models.HomePopularModel;
import com.video.tamas.Models.VideoBySongIdModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoBySongDetailedListActivity extends AppCompatActivity {
    NetworkUtils networkUtils;
    DiscreteScrollView rvVideos;
    BottomSheetDialog dialog;
    DeviceResourceManager resourceManager;
    private String userId;
    TextView tvErrorMessage;
    View commentView;
    CategorywiseVideoListRecyclerAdapter categorywiseVideoListRecyclerAdapter;
    List<HomePopularModel> homePopularModelList;
    FloatingActionButton btnMakeVideo;
    private MaterialDialog materialDialog;
    String songId, selectedPosition, songName;
    List<VideoBySongIdModel> videoBySongIdModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videobysong_detail_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvVideos = findViewById(R.id.rvVideos);
        networkUtils = new NetworkUtils(this);
        btnMakeVideo = findViewById(R.id.btnMakeVideo);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        Intent intent = getIntent();
        songId = intent.getStringExtra("songId");
        songName = intent.getStringExtra("songName");
        selectedPosition = intent.getStringExtra("adapterPosition");
        Log.wtf("songId", songId);
        Log.wtf("selectedPosition", selectedPosition);
        getVideo();


    }

    public void getVideo() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("song_id", songId);
            jsonBody.put("user_id", userId);
            Log.wtf("jsonBody", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getVideoBySongId, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    if (!status.equalsIgnoreCase("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String videoPath = jsonObject2.optString("file_name_mp4");
                            String videoGif = jsonObject2.optString("file_name_gif");
                            String videoId = jsonObject2.optString("id");
                            String userName = jsonObject2.optString("username");
                            String userId = jsonObject2.optString("user_id");
                            String likeCount = jsonObject2.optString("total_likes");
                            String commentCount = jsonObject2.optString("total_comments");
                            String commentStatus = jsonObject2.optString("video_comment_status");
                            String isLike = jsonObject2.optString("user_like_status");
                            String isFollow = jsonObject2.optString("following_status");

                            Log.wtf("gif", videoGif);
                            VideoBySongIdModel videoBySongIdModel = new VideoBySongIdModel();
                            videoBySongIdModel.setVideoGif(videoGif);
                            videoBySongIdModel.setSongId(songId);
                            videoBySongIdModel.setVideoPath(videoPath);
                            videoBySongIdModel.setVideoId(videoId);
                            videoBySongIdModel.setCommentCount(commentCount);
                            videoBySongIdModel.setLikeCount(likeCount);
                            videoBySongIdModel.setUserId(userId);
                            videoBySongIdModel.setUserName(userName);
                            videoBySongIdModel.setSongName(songName);
                            videoBySongIdModel.setCommentStatus(commentStatus);
                            videoBySongIdModel.setIsLike(isLike);
                            videoBySongIdModel.setIsFollow(isFollow);
                            videoBySongIdModels.add(videoBySongIdModel);


                        }
                        rvVideos.setOrientation(DSVOrientation.VERTICAL);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rvVideos.scrollToPosition(Integer.parseInt(selectedPosition));
                            }
                        }, 200);

                        rvVideos.setItemTransformer(new ScaleTransformer.Builder()
                                .setMaxScale(1.05f)
                                .setMinScale(0.8f)
                                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                                .build());
                        VideoBySongDetailedRecyclerAdapter homePopularRecyclerAdapter = new VideoBySongDetailedRecyclerAdapter(VideoBySongDetailedListActivity.this, videoBySongIdModels);
                        rvVideos.setAdapter(homePopularRecyclerAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                materialDialog.dismiss();
            }

        });
    }

    public void getCommentList(String videoId) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("video_id", videoId);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        networkUtils.postDataVolley(Config.MethodName.getCommentList, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    commentView = getLayoutInflater().inflate(R.layout.comment_layout, null);
                    dialog = new BottomSheetDialog(VideoBySongDetailedListActivity.this);
                    dialog.setContentView(commentView);
                    EditText etCommentMessage = commentView.findViewById(R.id.etCommentMessage);
                    Button btnSendComment = commentView.findViewById(R.id.btnSendComment);
                    ImageView ivCancel = commentView.findViewById(R.id.ivCancel);
                    btnSendComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(userId)) {
                                Log.wtf("userid", "userid");
                                String commentMessage = etCommentMessage.getText().toString();
                                String toServerUnicodeEncoded = StringEscapeUtils.escapeJava(commentMessage);
                                sendComment(toServerUnicodeEncoded, videoId);
                            } else {
                                Log.wtf("userid", "notuserid");
                                startActivity(new Intent(VideoBySongDetailedListActivity.this, LoginActivity.class));
                            }


                        }
                    });
                    ivCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    if (!status.equals("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            List<CommentModel> commentModelList = ParcingUtils.parseCommentModelList(jsonArray);
                            CommentListRecyclerAdapter commentListRecyclerAdapter = new CommentListRecyclerAdapter(VideoBySongDetailedListActivity.this, commentModelList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoBySongDetailedListActivity.this);
                            RecyclerView rvComment = commentView.findViewById(R.id.rvComment);


                            rvComment.setLayoutManager(layoutManager);
                            rvComment.setAdapter(commentListRecyclerAdapter);

                            dialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        TextView tvErrorMsg = commentView.findViewById(R.id.tvErrorMessage);
                        tvErrorMsg.setVisibility(View.VISIBLE);
                        dialog.show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                materialDialog.dismiss();
            }

        });
    }

    public void sendComment(String commentMessage, String videoId) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(VideoBySongDetailedListActivity.this)
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("video_id", videoId);
            jsonBody.put("comment", commentMessage);
            Log.wtf("json", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.comment, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(VideoBySongDetailedListActivity.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        getCommentList(videoId);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                materialDialog.dismiss();
            }

        });
    }

    public void likeVideoApi(String videoId, String position) {


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("video_id", videoId);
            Log.wtf("jsonLike", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.like, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("likeResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(VideoBySongDetailedListActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.wtf("likePosition", position);
                        getVideo();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {

            }

        });
    }

    public void followApi(String userProfileId) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(VideoBySongDetailedListActivity.this)
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("to_follow_id", userProfileId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.followUnfollow, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("followResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(VideoBySongDetailedListActivity.this, message, Toast.LENGTH_SHORT).show();
                        getVideo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                materialDialog.dismiss();
            }

        });
    }

    public void videoDownload(String videoPath) {
        materialDialog = new MaterialDialog.Builder(VideoBySongDetailedListActivity.this)
                .content("downloading video ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        Log.wtf("downloadedClicked", "download");
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = videoPath.substring(videoPath.lastIndexOf('/') + 1);
        File myDir = new File(sdcard, fileName);
        Log.wtf("videoPathDir", myDir.getAbsolutePath());
        com.liulishuo.filedownloader.FileDownloader.setup(VideoBySongDetailedListActivity.this);
        com.liulishuo.filedownloader.FileDownloader.getImpl().create(videoPath)
                .setPath(myDir.getAbsolutePath())
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        Log.wtf("downloadedStart", "download");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        Log.wtf("downloadedProgress", "download");
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        materialDialog.dismiss();
                        Log.wtf("downloaded", "download");
                        Log.wtf("videoPath", myDir.getAbsolutePath());
                        shareVideo("Tamas", myDir.getAbsolutePath());

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.wtf("error", "error");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getVideo();
    }

    public void shareVideo(final String title, String path) {
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{path},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        String shareMessage = "\n" +"   " + "\n\n";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivity(Intent.createChooser(shareIntent, ("Share")));

                    }
                });
    }

    public void showNoCommentDialog() {
        commentView = getLayoutInflater().inflate(R.layout.no_comment_layout, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(commentView);
        dialog.show();
    }
}
