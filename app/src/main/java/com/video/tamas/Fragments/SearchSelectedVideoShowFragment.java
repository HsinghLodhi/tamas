package com.video.tamas.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.CommentListRecyclerAdapter;
import com.video.tamas.Adapters.SearchSelectedVideoRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.CommentModel;
import com.video.tamas.Models.HomePopularModel;
import com.video.tamas.Models.SearchModel;
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


public class SearchSelectedVideoShowFragment extends Fragment {
    Activity mActivity;
    View view;
    NetworkUtils networkUtils;
    ArrayList<HomePopularModel> homePopularModelArrayList = new ArrayList<>();
    DiscreteScrollView rvVideos;
    String selectedPosition = "0", searchText;
    DeviceResourceManager resourceManager;
    private String userId;
    BottomSheetDialog dialog;
    MaterialDialog materialDialog;
    View commentView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_selected_entertainment_video, container, false);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        rvVideos = view.findViewById(R.id.rvVideos);
        networkUtils = new NetworkUtils(mActivity);
        resourceManager = new DeviceResourceManager(mActivity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        Bundle args = getArguments();
        if (args != null) {
            selectedPosition = args.getString("adapterPosition");
            Log.w("adapterPos", selectedPosition);
            searchText = args.getString("searchText");
            Log.w("searchText", searchText);
            getVideo();
        }

    }

    public void getVideo() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("search_text", searchText);
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.search, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    try {
                        List<SearchModel> searchModelList = ParcingUtils.parseSearchModelList(jsonArray);
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

                        SearchSelectedVideoRecyclerAdapter searchAdapter = new SearchSelectedVideoRecyclerAdapter(mActivity, searchModelList, SearchSelectedVideoShowFragment.this);
                        rvVideos.setAdapter(searchAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
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
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
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
                materialDialog.dismiss();
            }

        });
    }

    public void sendComment(String commentMessage, String videoId) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
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
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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

    public void likeVideoApi(String videoId) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("video_id", videoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.like, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("likeResponse", jsonObject.toString());
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
                materialDialog.dismiss();
            }

        });
    }

    public void followApi(String userProfileId) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
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
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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
        materialDialog = new MaterialDialog.Builder(mActivity)
                .content("downloading video ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        Log.wtf("downloadedClicked", "download");
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = videoPath.substring(videoPath.lastIndexOf('/') + 1);
        File myDir = new File(sdcard, fileName);
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

    @Override
    public void onResume() {
        super.onResume();
        getVideo();

    }

    public void shareVideo(final String title, String path) {
        MediaScannerConnection.scanFile(getActivity(), new String[]{path},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(
                                Intent.EXTRA_TITLE, title);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        mActivity.startActivity(Intent.createChooser(shareIntent, ("Share")));

                    }
                });
    }

    public void showNoCommentDialog() {
        commentView = getLayoutInflater().inflate(R.layout.no_comment_layout, null);
        dialog = new BottomSheetDialog(mActivity);
        dialog.setContentView(commentView);
        dialog.show();
    }
}
