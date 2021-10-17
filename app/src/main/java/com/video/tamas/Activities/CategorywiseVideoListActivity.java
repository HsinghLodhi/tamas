package com.video.tamas.Activities;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.video.tamas.Adapters.CategorywiseVideoListRecyclerAdapter;
import com.video.tamas.Adapters.CommentListRecyclerAdapter;
import com.video.tamas.Adapters.VerticalPagerAdaptor;
import com.video.tamas.Fragments.ExoPlayerFragment;
import com.video.tamas.Fragments.PopularFragment;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.CommentModel;
import com.video.tamas.Models.HomePopularModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.DirectionalViewPager;
import com.video.tamas.Utils.NetworkUtils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CategorywiseVideoListActivity extends AppCompatActivity {

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
    String subcategoryId, subcategoryName;
    private LazyLoader lazyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorywise_video_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lazyLoader = findViewById(R.id.lazyLoaderCategoryList);
        PopularFragment.videoViewPager = (DirectionalViewPager) findViewById(R.id.videoViewPager);
        rvVideos = findViewById(R.id.rvVideos);
        networkUtils = new NetworkUtils(this);
        btnMakeVideo = findViewById(R.id.btnMakeVideo);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        Intent intent = getIntent();
        subcategoryId = intent.getStringExtra("subcategoryId");
        subcategoryName = intent.getStringExtra("subcategoryName");
        getSupportActionBar().setTitle(subcategoryName);
        Log.wtf("subCat", subcategoryId);
        //getVideo();

        btnMakeVideo.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), EntertainmentVideoMakeActivity.class);
            intent1.putExtra("from", "category");
            intent1.putExtra("subcategoryId", subcategoryId);
            startActivity(intent1);
            resourceManager.addToSharedPref("IS_TALENT_PAGE", true);
            finish();
        });
    }

    public void getVideo() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("category_id", "2");
            jsonBody.put("user_id", userId);
            jsonBody.put("sub_category", subcategoryId);
            Log.wtf("jsonBody", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    if (!status.equalsIgnoreCase("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            homePopularModelList = ParcingUtils.parseHomePopularModelList(jsonArray);
                            PopularFragment.isPopulerFragmentVisible = true;
                            setVideoData((ArrayList<HomePopularModel>) homePopularModelList, 0);

                            /*rvVideos.setItemViewCacheSize(10);
                            rvVideos.setOrientation(DSVOrientation.VERTICAL);
                            SnapHelper snapHelper = new PagerSnapHelper();
                            snapHelper.attachToRecyclerView(rvVideos);
                            rvVideos.setItemTransformer(new ScaleTransformer.Builder()
                                    .setMaxScale(1.05f)
                                    .setMinScale(0.8f)
                                    .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                                    .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                                    .build());
                            categorywiseVideoListRecyclerAdapter = new CategorywiseVideoListRecyclerAdapter(CategorywiseVideoListActivity.this, homePopularModelList);
                            rvVideos.setAdapter(categorywiseVideoListRecyclerAdapter);*/

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
            }

        });
    }

    public void getCommentList(String videoId) {
        lazyLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("video_id", videoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getCommentList, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    commentView = getLayoutInflater().inflate(R.layout.comment_layout, null);
                    dialog = new BottomSheetDialog(CategorywiseVideoListActivity.this);
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
                                startActivity(new Intent(CategorywiseVideoListActivity.this, LoginActivity.class));
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
                            CommentListRecyclerAdapter commentListRecyclerAdapter = new CommentListRecyclerAdapter(CategorywiseVideoListActivity.this, commentModelList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CategorywiseVideoListActivity.this);
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
                lazyLoader.setVisibility(View.GONE);
            }

        });
    }

    public void sendComment(String commentMessage, String videoId) {
        lazyLoader.setVisibility(View.VISIBLE);
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
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(CategorywiseVideoListActivity.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        getCommentList(videoId);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
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
                        Toast.makeText(CategorywiseVideoListActivity.this, message, Toast.LENGTH_SHORT).show();
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
        lazyLoader.setVisibility(View.VISIBLE);
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
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("followResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(CategorywiseVideoListActivity.this, message, Toast.LENGTH_SHORT).show();
                        getVideo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
            }

        });
    }

    public void videoDownload(String videoPath) {
        materialDialog = new MaterialDialog.Builder(CategorywiseVideoListActivity.this)
                .content("downloading video ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        Log.wtf("downloadedClicked", "download");
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = videoPath.substring(videoPath.lastIndexOf('/') + 1);
        File myDir = new File(sdcard, fileName);
        Log.wtf("videoPathDir", myDir.getAbsolutePath());
        com.liulishuo.filedownloader.FileDownloader.setup(CategorywiseVideoListActivity.this);
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
                        //String shareMessage = "\n" + getResources().getString(R.string.sharecontent) + "\n\n";
                        //shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                        //shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void addVideoCount(String videoId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("video_id", videoId);
            Log.wtf("jsonshareVideo", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.add_view_count, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("shareCountVideoResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(CategorywiseVideoListActivity.this, message, Toast.LENGTH_SHORT).show();

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

    public void setVideoData(ArrayList<HomePopularModel> videoList, int videoPosition) {
        if (videoList != null) {
            FragmentManager videoFragmentManager = getSupportFragmentManager();

            PopularFragment.videoViewPager.setOrientation(1);

            PopularFragment.videoViewPager.setOffscreenPageLimit(1);

            //allVideoInfoList = videoList;
            PopularFragment.verticalPagerAdaptor = new VerticalPagerAdaptor(videoFragmentManager, videoList, "category");

            PopularFragment.videoViewPager.setAdapter(PopularFragment.verticalPagerAdaptor);
            PopularFragment.verticalPagerAdaptor.notifyDataSetChanged();

            setVideoViewPagerData();
            PopularFragment.videoViewPager.setCurrentItem(videoPosition);
        }
    }

    //for setting data according to categorywies
    private void setVideoViewPagerData() {
        PopularFragment.videoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                Log.v("dip", "page scroll method value :" + i + " " + v + " " + i1);
            }

            @Override
            public void onPageSelected(int i) {
                Log.v("dip", "scroll position :" + i);
                ExoPlayerFragment lastFragment = PopularFragment.verticalPagerAdaptor.getCachedItem(PopularFragment.currenpage);
                if (lastFragment != null) {
                    Log.v("dip", "inside last fragment");
                    lastFragment.releasePlayer();
                }
                PopularFragment.currenpage = i;

                ExoPlayerFragment newFragment = PopularFragment.verticalPagerAdaptor.getCachedItem(PopularFragment.currenpage);
                if (newFragment != null) {
                    Log.v("dip", "inside new fragment");
                    newFragment.getVideoUriPath();
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Log.v("dip", "page status :" + i);

                PopularFragment.pageChangeStatus = i;

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (PopularFragment.verticalPagerAdaptor != null) {
            if (homePopularModelList != null) {
                for (int i = 0; i < homePopularModelList.size(); i++) {
                    ExoPlayerFragment exoPlayerFragment = PopularFragment.verticalPagerAdaptor.getCachedItem(i);
                    if (exoPlayerFragment != null) {
                        Log.e("Jay", "in Home");
                        exoPlayerFragment.releasePlayer();

                    }
                }
            }
            // vPagervideo.
        }
        PopularFragment.verticalPagerAdaptor = null;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (PopularFragment.verticalPagerAdaptor != null) {
            if (homePopularModelList != null) {
                for (int i = 0; i < homePopularModelList.size(); i++) {
                    ExoPlayerFragment exoPlayerFragment = PopularFragment.verticalPagerAdaptor.getCachedItem(i);
                    if (exoPlayerFragment != null) {
                        Log.e("Jay", "in Home");
                        exoPlayerFragment.releasePlayer();

                    }
                }
            }
            // vPagervideo.
        }
        PopularFragment.verticalPagerAdaptor = null;
        resourceManager.addToSharedPref("CATEGORY_PAGE_LIST", false);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
