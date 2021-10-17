package com.video.tamas.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
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
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.CommentListRecyclerAdapter;
import com.video.tamas.Adapters.UserPersonalVideoRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.CommentModel;
import com.video.tamas.Models.ProfileModelClass;
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

import java.util.ArrayList;
import java.util.List;


public class UserPersonalVideoShowFragment extends Fragment {
    Activity mActivity;
    View view;
    NetworkUtils networkUtils;
    DiscreteScrollView rvVideos;
    String selectedPosition = "0";
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        mActivity = getActivity();
        rvVideos = view.findViewById(R.id.rvVideos);
        networkUtils = new NetworkUtils(mActivity);
        resourceManager = new DeviceResourceManager(mActivity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        Bundle args = getArguments();
        if (args != null) {
            selectedPosition = args.getString("adapterPosition");
            userId=args.getString("userId");
            Log.w("adapterPos", selectedPosition);
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
            jsonBody.put("user_id", userId);
            Log.wtf("json",jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getUserVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<ProfileModelClass> profileModelClassArrayList = new ArrayList<>();;
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String videoId = jsonObject1.optString("id");
                            String videoDesp = jsonObject1.optString("description");
                            String videoUrl = jsonObject1.optString("file_name");
                            String videoGif = jsonObject1.optString("image_name");
                            String videoSongName = jsonObject1.optString("song_name");
                            String videoSongId = jsonObject1.optString("song_id");
                            String videoLikesCount = jsonObject1.optString("likes");
                            String videoProfileImage = jsonObject1.optString("profile_image");
                            String userProfileId = jsonObject1.optString("user_id");
                            String isDraftVideo = jsonObject1.optString("draft");
                            String subcategoryId = jsonObject1.optString("sub_category");
                            String viewCount = jsonObject1.optString("views_count");
                            String commentCount=jsonObject1.optString("comment");
                            if (!subcategoryId.equals("4")) {
                                ProfileModelClass profileModelClass = new ProfileModelClass();
                                profileModelClass.setVideoId(videoId);
                                profileModelClass.setVideoDescription(videoDesp);
                                profileModelClass.setVideoGif(videoGif);
                                profileModelClass.setVideoUrl(videoUrl);
                                profileModelClass.setVideoLikesCount(videoLikesCount);
                                profileModelClass.setVideoProfileImage(videoProfileImage);
                                profileModelClass.setVideoSongId(videoSongId);
                                profileModelClass.setVideoSongName(videoSongName);
                                profileModelClass.setIsDraftVideo(isDraftVideo);
                                profileModelClass.setViewCount(viewCount);
                                profileModelClass.setVideoCommentCount(commentCount);
                                profileModelClass.setProfileId(userProfileId);
                                profileModelClassArrayList.add(profileModelClass);
                            }

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
                        UserPersonalVideoRecyclerAdapter homePopularRecyclerAdapter = new UserPersonalVideoRecyclerAdapter(mActivity, profileModelClassArrayList, UserPersonalVideoShowFragment.this);
                        rvVideos.setAdapter(homePopularRecyclerAdapter);
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

    @Override
    public void onResume() {
        super.onResume();
        getVideo();

    }

    public void showNoCommentDialog() {
        commentView = getLayoutInflater().inflate(R.layout.no_comment_layout, null);
        dialog = new BottomSheetDialog(mActivity);
        dialog.setContentView(commentView);
        dialog.show();
    }

    public void videoDelete(String videoId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("video_id", videoId);
            jsonBody.put("user_id ", userId);
            Log.wtf("jsonLike", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.videoDelete, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("likeResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("Success")) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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
}
