package com.video.tamas.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.CommentListRecyclerAdapter;
import com.video.tamas.Adapters.HomePopularRecyclerAdapter;
import com.video.tamas.Adapters.VerticalPagerAdaptor;
import com.video.tamas.BuildConfig;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.CommentModel;
import com.video.tamas.Models.HomePopularModel;
import com.video.tamas.Models.tag.Data1;
import com.video.tamas.Models.tag.HashTagResponse;
import com.video.tamas.Models.tag.VideoData;
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
import java.util.Collections;
import java.util.List;


public class PopularFragment extends Fragment implements HomeFragment.OnTabChangeListener {
    Activity mActivity;
    public static DirectionalViewPager videoViewPager;
    public static int currenpage = 0;
    FragmentManager videoFragmentManager;
    boolean isPause = false, pause = false;
    View view;
    private int pageNum = 1;
    NetworkUtils networkUtils;
    DiscreteScrollView rvVideos;
    BottomSheetDialog dialog;
    DeviceResourceManager resourceManager;
    private String userId;
    public static boolean isPopulerFragmentVisible = false;
    TextView tvErrorMessage;
    View commentView;
    HomePopularRecyclerAdapter homePopularRecyclerAdapter;
    boolean firstTime = true;
    List<HomePopularModel> homePopularModelList;
    MaterialDialog materialDialog;
    LazyLoader tashieLoader;
    public static int pageChangeStatus = 0;
    public static VerticalPagerAdaptor verticalPagerAdaptor;
    private int selectedPosition = 0;
    private String categoryId = "";
    private String from = "";
    private List<VideoData> videoDataList;
    private String videoId = "";

    public PopularFragment() {
    }

    public PopularFragment(int selectedPosition, String categoryId, String from, List<VideoData> videoDataList) {
        this.videoDataList = videoDataList;
        this.selectedPosition = selectedPosition;
        this.categoryId = categoryId;
        this.from = from;
    }

    public PopularFragment(int selectedPosition, String categoryId, String from) {
        this.selectedPosition = selectedPosition;
        this.categoryId = categoryId;
        this.from = from;
    }

    public PopularFragment(int selectedPosition, String categoryId, String from, String videoId) {
        this.selectedPosition = selectedPosition;
        this.categoryId = categoryId;
        this.from = from;
        this.videoId = videoId;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setUserVisibleHint(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_popular, container, false);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        rvVideos = view.findViewById(R.id.rvVideos);
        videoViewPager = (DirectionalViewPager) view.findViewById(R.id.videoViewPager);
        networkUtils = new NetworkUtils(mActivity);
        resourceManager = new DeviceResourceManager(mActivity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        currenpage = 0;
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        tashieLoader = (LazyLoader) view.findViewById(R.id.myLoader);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("STOP_PLAYING"));
        if (from.equalsIgnoreCase("other")) {
            isPopulerFragmentVisible = true;
            getVideo();
            setVideoData((ArrayList<HomePopularModel>) homePopularModelList, selectedPosition);
        } else if (from.equalsIgnoreCase("search")) {
            isPopulerFragmentVisible = true;
            getSearchVideo();

        } else if (from.equalsIgnoreCase("selfProfile")) {
            isPopulerFragmentVisible = true;
            getSelfProfileVideo();
        } else if (from.equalsIgnoreCase("Search_Fragment")) {
            getSearchFragmentVideo();
            isPopulerFragmentVisible = true;
        } else {
            getVideo();
            isPopulerFragmentVisible = true;
        }

        // getVideo();
    }


    private void getSearchFragmentVideo() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            Log.wtf("Popularjson", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.wtf("PopularserviceCall", Config.MethodName.TagSort);
        networkUtils.postDataVolley(Config.MethodName.TagSort, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                //settingsDialog.dismiss();
                tashieLoader.removeAllViews();
                tashieLoader.setVisibility(View.GONE);
                try {
                    Gson gson = new Gson();
                    HashTagResponse hashTagResponse = gson.fromJson(result, HashTagResponse.class);

                    if (hashTagResponse.getResponse()) {

                        List<Data1> data1List = hashTagResponse.getData();
                        List<VideoData> videoDataList = new ArrayList<>();
                        for (Data1 data1 : data1List) {
                            for (VideoData videoData : data1.getData()) {
                                videoDataList.add(videoData);
                            }
                        }
                        transformModelList(videoDataList);

                    } else {
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                //settingsDialog.dismiss();
                Log.v("dip", "error" + result.toString());
                tashieLoader.removeAllViews();
                tashieLoader.setVisibility(View.GONE);
            }

        });
    }

    private void transformModelList(List<VideoData> videoDataList) {
        homePopularModelList = new ArrayList<>();
        for (VideoData videoData : videoDataList) {
            HomePopularModel model = new HomePopularModel();
            model.setCategoryId(String.valueOf(videoData.getCategoriesId()));
            model.setUserProfileId(String.valueOf(videoData.getUserId()));
            model.setCommentStatus(videoData.getVideoCommentStatus());
            model.setIsFollow(String.valueOf(videoData.getFollowStatus()));
            model.setIsLike(String.valueOf(videoData.getUserLikeStatus()));
            model.setVideoLikesCount(videoData.getViewsCount());
            model.setVideoUrl(videoData.getFileName());
            model.setVideoId(String.valueOf(videoData.getId()));
            model.setVideoGif(videoData.getImageName());
            model.setSongImage(videoData.getSongImage());
            model.setTotalCommentCount(String.valueOf(videoData.getTotalComments()));
            model.setTotalLikeCount(String.valueOf(videoData.getTotalLikes()));
            model.setTotalShareCount(videoData.getTotalShareCount());
            model.setUserProfileName(videoData.getUsername());
            model.setVideoDescription(videoData.getDescription());
            model.setVideoProfileImage(videoData.getProfileImage());
            model.setVideoSongId(String.valueOf(videoData.getSongId()));
            model.setVideoSongName(String.valueOf(videoData.getSongName()));
            homePopularModelList.add(model);
        }
        for (int i = 0; i < homePopularModelList.size(); i++) {
            if (videoId.equals(homePopularModelList.get(i).getVideoId())) {
                selectedPosition = i;
            }
        }
        setVideoData((ArrayList<HomePopularModel>) homePopularModelList, selectedPosition);
    }


    public void getVideo() {
//        Dialog settingsDialog = new Dialog(mActivity);
//        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.gif_progress_dialog
//                , null));
//        ImageView iv = settingsDialog.findViewById(R.id.imageView);
//        Glide.with(mActivity).load(R.drawable.gif_tamas).into(iv);
//        settingsDialog.show();
//        settingsDialog.setCancelable(false);
//        LazyLoader loader = new LazyLoader(mActivity, 10, 10, ContextCompat.getColor(mActivity, R.color.colorPrimary),
//                ContextCompat.getColor(mActivity, R.color.colorRed),
//                ContextCompat.getColor(mActivity, R.color.colorPrimary));
//        loader.setAnimDuration(500);
//        loader.setFirstDelayDuration(100);
//        loader.setSecondDelayDuration(200);
//        loader.setInterpolator(new LinearInterpolator());
//        tashieLoader.addView(loader);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("category_id", categoryId);
            jsonBody.put("user_id", userId);
            Log.wtf("Popularjson", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.wtf("PopularserviceCall", Config.MethodName.getVideos);
        networkUtils.postDataVolley(Config.MethodName.getVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                //settingsDialog.dismiss();
                tashieLoader.removeAllViews();
                tashieLoader.setVisibility(View.GONE);
                try {
                    Log.v("dip", "Popular  List : " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    if (!status.equalsIgnoreCase("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.e("Popular Fragment", " " + jsonArray);
                        try {
                            homePopularModelList = ParcingUtils.parseHomePopularModelList(jsonArray);
                            Log.v("dip", "inside api calll before data set :");
                            for (int i = 0; i < homePopularModelList.size(); i++) {
                                if (videoId.equals(homePopularModelList.get(i).getVideoId())) {
                                    selectedPosition = i;
                                }
                            }
                            setVideoData((ArrayList<HomePopularModel>) homePopularModelList, selectedPosition);
                            //rvVideos.setItemViewCacheS      ize(10);
                            //rvVideos.setOrientation(DSVOrientation.VERTICAL);
//                            SnapHelper snapHelper = new PagerSnapHelper();
//                            snapHelper.attachToRecyclerView(rvVideos);
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
                //settingsDialog.dismiss();
                Log.v("dip", "error" + result.toString());
                tashieLoader.removeAllViews();
                tashieLoader.setVisibility(View.GONE);
            }

        });
    }

    public void POS_ITEM() {
        rvVideos.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                //Toast.makeText(mActivity, "------>>>"+rvVideos.getCurrentItem(), Toast.LENGTH_SHORT).show();
                homePopularRecyclerAdapter.chg_Video(rvVideos.getCurrentItem());
                homePopularRecyclerAdapter.notifyItemChanged(rvVideos.getCurrentItem());
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isPopulerFragmentVisible = isVisibleToUser;
        Log.v("dip", "is visible by populer : " + isVisibleToUser);
        if (isVisibleToUser) {

            Log.v("visible", "popularVisible");
            if (verticalPagerAdaptor != null) {
                if (homePopularModelList != null) {

                    if (pause) {
                        pause = false;
                    } else {
                        ExoPlayerFragment exoPlayerFragment = verticalPagerAdaptor.getCachedItem(currenpage);
                        if (exoPlayerFragment != null) {
                            Log.e("Jay", "in Home");
                            exoPlayerFragment.resumePlayer();
                        }
                    }
                }
            }
            // getVideo();
        } else {
            if (verticalPagerAdaptor != null) {
                if (homePopularModelList != null) {
                    if (isPause) {
                        isPause = false;
                    } else {
                        ExoPlayerFragment exoPlayerFragment = verticalPagerAdaptor.getCachedItem(currenpage);
                        if (exoPlayerFragment != null) {
                            Log.e("Jay", "in Home");
                            exoPlayerFragment.pausePlayer();
                        }
                    }
                }
            }
        }
        //onDestroy();
//            if (from.equalsIgnoreCase("other")) {
//                isPopulerFragmentVisible = true;
//                // getVideo();
//
//            }
//            try {
//                //homePopularRecyclerAdapter.onDestroy();
//                if (firstTime) {
//                } else {
//                    homePopularRecyclerAdapter.onDestroy();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

    }

    @Override
    public void onTabChange(int pageNumber, boolean page) {
        System.out.println("page number: " + pageNumber);
        pageNum = pageNumber;
        if (pageNumber == 1) {
            // getVideo();
        }
    }

    public void setFirstTimeValue(boolean fromAdapter) {
        firstTime = fromAdapter;
    }

    @Override
    public void onPause() {
        super.onPause();
        //firstTime = true;
        if (verticalPagerAdaptor != null) {
            ExoPlayerFragment exoPlayerFragment = verticalPagerAdaptor.getCachedItem(currenpage);
            if (exoPlayerFragment != null) {
                Log.e("Jay", "in Home");
                isPause = true;
                pause = true;
                exoPlayerFragment.releasePlayer();
            }
        }


    }


    public void getCommentList(String videoId) {
        Dialog settingsDialog = new Dialog(mActivity);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.gif_progress_dialog
                , null));
        ImageView iv = settingsDialog.findViewById(R.id.imageView);
        Glide.with(mActivity).load(R.drawable.gif_tamas).into(iv);
        settingsDialog.show();
        settingsDialog.setCancelable(false);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("video_id", videoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getCommentList, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                settingsDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    commentView = getLayoutInflater().inflate(R.layout.comment_layout, null);
                    dialog = new BottomSheetDialog(mActivity);
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
                                startActivity(new Intent(mActivity, LoginActivity.class));
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
                            CommentListRecyclerAdapter commentListRecyclerAdapter = new CommentListRecyclerAdapter(mActivity, commentModelList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
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
                settingsDialog.dismiss();
            }

        });
    }

    public void sendComment(String commentMessage, String videoId) {
        tashieLoader.setVisibility(View.VISIBLE);
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
                tashieLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        getCommentList(videoId);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                tashieLoader.setVisibility(View.GONE);
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
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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
        tashieLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("to_follow_id", userProfileId);
            Log.wtf("followJson", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.followUnfollow, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                tashieLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("followResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        getVideo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                tashieLoader.setVisibility(View.GONE);
            }

        });
    }

    public void videoDownload(String videoPath) {
        materialDialog = new MaterialDialog.Builder(mActivity)
                .content("downloading video ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        Log.wtf("downloadedClicked", "download");
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = videoPath.substring(videoPath.lastIndexOf('/') + 1);
        File myDir = new File(sdcard, fileName);
        Log.wtf("videoPathDir", myDir.getAbsolutePath());
        com.liulishuo.filedownloader.FileDownloader.setup(mActivity);
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


    public void shareVideo(final String title, String path) {
        MediaScannerConnection.scanFile(getActivity(), new String[]{path},
                null, (path1, uri) -> {
                    Intent shareIntent = new Intent(
                            Intent.ACTION_SEND);
                    shareIntent.setType("video/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    String shareMessage = "\n" + " " + "\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    shareIntent
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    mActivity.startActivity(Intent.createChooser(shareIntent, ("Share")));

                });
    }

    public void showNoCommentDialog() {
        commentView = getLayoutInflater().inflate(R.layout.no_comment_layout, null);
        dialog = new BottomSheetDialog(mActivity);
        dialog.setContentView(commentView);
        dialog.show();
    }

    public void addShareCount(String videoId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("video_id", videoId);
            Log.wtf("jsonshare", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.add_share_count, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("shareCountResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (verticalPagerAdaptor != null) {
            if (homePopularModelList != null)
                for (int i = 0; i < homePopularModelList.size(); i++) {
                    ExoPlayerFragment exoPlayerFragment = verticalPagerAdaptor.getCachedItem(i);
                    if (exoPlayerFragment != null) {
                        Log.e("Jay", "in Home");
                        exoPlayerFragment.releasePlayer();

                    }
                }
            // vPagervideo.
        }
        verticalPagerAdaptor = null;

        if (homePopularRecyclerAdapter != null) {
            homePopularRecyclerAdapter.onDestroy();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String status = intent.getStringExtra("status");
                if (Integer.parseInt(status) == 1) {
                    if (homePopularRecyclerAdapter != null) {
                        homePopularRecyclerAdapter.onDestroy();
                    }
                }
            }
        }
    };

//    public void setVideoData(ArrayList<HomePopularModel> videoList, int videoPosition) {
//        if (videoList != null) {
//            videoFragmentManager = getFragmentManager();
//            //allVideoInfoList = videoList;
//            verticalPagerAdaptor = new VerticalPagerAdaptor(videoFragmentManager, videoList, from);
//            verticalPagerAdaptor.notifyDataSetChanged();
//            videoViewPager.setAdapter(verticalPagerAdaptor);
//            videoViewPager.setOrientation(1);
//            videoViewPager.setOffscreenPageLimit(1);
//            videoViewPager.setCurrentItem(videoPosition);
//            setVideoViewPagerData();
//        }
//    }
//
//    //for setting data according to categorywies
//    private void setVideoViewPagerData() {
//        videoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//                Log.v("dip", "page scroll method value :" + i + " " + v + " " + i1);
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//                Log.v("dip", "scroll position :" + i);
//                if (verticalPagerAdaptor != null) {
//                    ExoPlayerFragment lastFragment = verticalPagerAdaptor.getCachedItem(currenpage);
//                    if (lastFragment != null) {
//                        Log.v("dip", "inside last fragment");
//                        lastFragment.releasePlayer();
//                    }
//                }
//                currenpage = i;
//                if (verticalPagerAdaptor != null) {
//                    ExoPlayerFragment newFragment = verticalPagerAdaptor.getCachedItem(currenpage);
//                    if (newFragment != null) {
//                        Log.v("dip", "inside new fragment");
//                        newFragment.getVideoUriPath();
//                    }
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//                Log.v("dip", "page status :" + i);
//                pageChangeStatus = i;
//            }
//        });
//    }


    public void setVideoData(ArrayList<HomePopularModel> videoList, int videoPosition) {
        if (videoList != null) {
            FragmentManager videoFragmentManager = getFragmentManager();

            PopularFragment.videoViewPager.setOrientation(1);

            PopularFragment.videoViewPager.setOffscreenPageLimit(1);

            //allVideoInfoList = videoList;
            PopularFragment.verticalPagerAdaptor = new VerticalPagerAdaptor(videoFragmentManager, videoList, from);

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


    public void getSearchVideo() {
        tashieLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("search_text", categoryId);
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.search, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                tashieLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    try {
                        homePopularModelList = ParcingUtils.parseHomePopularModelList(jsonArray);
                        Log.v("dip", "inside api calll before data set :");
                        setVideoData((ArrayList<HomePopularModel>) homePopularModelList, selectedPosition);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                tashieLoader.setVisibility(View.GONE);
            }

        });
    }

    public void getSelfProfileVideo() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", categoryId);
            Log.wtf("json", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getUserVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                tashieLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    homePopularModelList = ParcingUtils.parseHomePopularModelList(jsonArray);
                    System.out.println("json array user data:" + jsonArray.toString());
                    Collections.reverse(homePopularModelList);
                    Log.v("dip", "inside api calll before data set :");
                    setVideoData((ArrayList<HomePopularModel>) homePopularModelList, selectedPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                materialDialog.dismiss();
                tashieLoader.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeFragment.setOnTabChangeListener(this);
        if (isPause) {
////            if (verticalPagerAdaptor != null) {
////                ExoPlayerFragment exoPlayerFragment = verticalPagerAdaptor.getCachedItem(currenpage);
////                if (exoPlayerFragment != null) {
////                    Log.e("Jay", "in Home");
////                    isPause = false;
////                    exoPlayerFragment.getVideoUriPath();
////                }
////            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}