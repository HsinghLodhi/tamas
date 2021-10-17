package com.video.tamas.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.video.tamas.Adapters.ProfileVideoWriterAdapter;
import com.video.tamas.Adapters.ProfileWriterTextListRecyclerAdapter;
import com.video.tamas.Models.ProfileModelClass;
import com.video.tamas.Models.ProfileWriterTextModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class ViewUserProfileActivity extends AppCompatActivity {
    NetworkUtils networkUtils;
    RecyclerView rvVideos, rvText;
    TextView tvErrorMessage;
    DeviceResourceManager resourceManager;
    private String userId;
    private String isUserFollow = "false";
    private String isViewerFollow = "false";
    TextView tvFollower, tvFollowing, tvUserName, tvTamasUserName, tvLikes;
    ImageView ivProfilePic;
    LinearLayout llFollower, llFollowing;
    Button btnEditProfile;
    String userProfileId, isFollow, userProfileName;
    TextView tvVideo, tvText;
    String subcategoryId;
    LinearLayout llRvVideos, llRvText;
    List<ProfileModelClass> profileModelClassList = new ArrayList<>();
    List<ProfileWriterTextModel> profileWriterTextModelList = new ArrayList<>();
    private LazyLoader lazyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_view_user_profile);
        rvVideos = findViewById(R.id.rvVideos);
        lazyLoader = findViewById(R.id.lazyLoaderViewUser);
        rvText = findViewById(R.id.rvText);
        tvVideo = findViewById(R.id.tvVideos);
        tvText = findViewById(R.id.tvText);
        networkUtils = new NetworkUtils(this);
        llRvVideos = findViewById(R.id.llrvVideos);
        llRvText = findViewById(R.id.llrvText);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        llFollower = findViewById(R.id.llFollower);
        btnEditProfile = findViewById(R.id.btnFollowUnfollow);
        tvFollower = findViewById(R.id.tvFollower);
        tvFollowing = findViewById(R.id.tvFollowing);
        tvTamasUserName = findViewById(R.id.tvTamasUserName);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        llFollowing = findViewById(R.id.llFollowing);
        tvLikes = findViewById(R.id.tvLikes);
        tvUserName = findViewById(R.id.tvUserName);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        Intent intent = getIntent();
        userProfileId = intent.getStringExtra("userId");
        userProfileName = intent.getStringExtra("userName");
        getSupportActionBar().setTitle(userProfileName + " 's profile");
        isFollow = intent.getStringExtra("isFollow");
        Log.wtf("userId", userProfileId);
        if (userProfileId.equals(userId)) {
            btnEditProfile.setText("Edit Profile");
        }
        if (isFollow.equals("1")) {
            btnEditProfile.setText("Following");
        } else if (isFollow.equals("0") && userProfileId.equals(userId)) {
            btnEditProfile.setText("Edit Profile");
        } else {
            btnEditProfile.setText("Follow");
        }
        getUserData();
        btnEditProfile.setOnClickListener(v -> {
            if (btnEditProfile.getText().equals("Edit Profile")) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
            } else {
                followApi(userProfileId);
            }

        });
        llFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUserProfileActivity.this, FollowerListActivity.class);
                intent.putExtra("userId", userProfileId);
                startActivity(intent);
            }
        });
        llFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUserProfileActivity.this, FollowingListActivity.class);
                intent.putExtra("userId", userProfileId);
                startActivity(intent);
            }
        });
        tvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvVideo.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvText.setTextColor(getResources().getColor(R.color.colorBlack));
                llRvText.setVisibility(View.GONE);
                llRvVideos.setVisibility(View.VISIBLE);
                getVideoData();

            }
        });
        tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvVideo.setTextColor(getResources().getColor(R.color.colorBlack));
                tvText.setTextColor(getResources().getColor(R.color.colorPrimary));
                getTextData();
                llRvVideos.setVisibility(View.GONE);
                llRvText.setVisibility(View.VISIBLE);

            }
        });
        getTextData();
    }

    public void getUserData() {
        profileModelClassList.clear();
        profileWriterTextModelList.clear();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userProfileId);
            jsonBody.put("viewer_id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getUserVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
//Log.v("dip","view profile response : "+result);

                    String status = jsonObject.optString("status");
                    String profilePic = jsonObject.optString("profile_image");
                    String follower = jsonObject.optString("followers");
                    String following = jsonObject.optString("following");
                    String likes = jsonObject.optString("total_likes");
                    String userName = jsonObject.optString("username");
                    isUserFollow = jsonObject.optString("usersfollow");
                    isViewerFollow = jsonObject.optString("viewerfollow");
                    Log.v("dip", "both json value : " + isUserFollow + " " + isViewerFollow);
                    if (userProfileId.equalsIgnoreCase(userId)) {
                        Log.v("dip", "id match if");
                        btnEditProfile.setText("Edit Profile");

                    } else if (isUserFollow.equalsIgnoreCase("true") && isViewerFollow.equalsIgnoreCase("false")) {
                        Log.v("dip", "isFollowBAck if");
                        btnEditProfile.setText("Follow Back");
                    } else if (isViewerFollow.equalsIgnoreCase("true")) {
                        Log.v("dip", "following if");
                        btnEditProfile.setText("following");

                    } else if (isViewerFollow.equalsIgnoreCase("false")) {
                        Log.v("dip", "follow if");
                        btnEditProfile.setText("Follow");

                    }
                    tvTamasUserName.setText(userName);
                    tvFollower.setText(follower);
                    tvFollowing.setText(following);
                    tvLikes.setText(likes);
                    Glide.with(ViewUserProfileActivity.this)
                            .load(profilePic)
                            .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.drawable.default_user))
                            .into(ivProfilePic);
                    tvVideo.performClick();

                    if (!status.equalsIgnoreCase("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            // List<ProfileModelClass> profileModelClassList = ParcingUtils.parseProfileVideoModelList(jsonArray);
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
                                subcategoryId = jsonObject1.optString("sub_category");
//                                if (!subcategoryId.equals("4")) {
//                                    ProfileModelClass profileModelClass = new ProfileModelClass();
//                                    profileModelClass.setVideoId(videoId);
//                                    profileModelClass.setVideoDescription(videoDesp);
//                                    profileModelClass.setVideoGif(videoGif);
//                                    profileModelClass.setVideoUrl(videoUrl);
//                                    profileModelClass.setVideoLikesCount(videoLikesCount);
//                                    profileModelClass.setVideoProfileImage(videoProfileImage);
//                                    profileModelClass.setVideoSongId(videoSongId);
//                                    profileModelClass.setVideoSongName(videoSongName);
//                                    profileModelClass.setIsDraftVideo(isDraftVideo);
//                                    profileModelClass.setProfileId(userProfileId);
//                                    profileModelClassList.add(profileModelClass);
//                                } else {
//                                    ProfileWriterTextModel profileWriterTextModel = new ProfileWriterTextModel();
//                                    profileWriterTextModel.setId(videoId);
//                                    profileWriterTextModel.setDescription(videoUrl);
//                                    profileWriterTextModel.setLikeCount(videoLikesCount);
//                                    profileWriterTextModel.setTitle(videoDesp);
//                                    profileWriterTextModel.setUserId(userProfileId);
//                                    profileWriterTextModelList.add(profileWriterTextModel);
//
//                                }

                            }
//                            if (profileModelClassList.size() == 0) {
//                                llRvVideos.setVisibility(View.GONE);
//
//                            } else {
//                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ViewUserProfileActivity.this, 3);
//                                ProfileVideoWriterAdapter profileVideoAdapter = new ProfileVideoWriterAdapter(ViewUserProfileActivity.this, profileModelClassList);
//                                rvVideos.setNestedScrollingEnabled(false);
//                                rvVideos.setLayoutManager(mLayoutManager);
//                                rvVideos.setAdapter(profileVideoAdapter);
//                            }
//                            if (profileWriterTextModelList.size() == 0) {
//                                llRvText.setVisibility(View.GONE);
//                            } else {
//                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ViewUserProfileActivity.this);
//                                ProfileWriterTextListRecyclerAdapter songListRecyclerAdapter = new ProfileWriterTextListRecyclerAdapter(ViewUserProfileActivity.this, null, profileWriterTextModelList);
//                                rvText.setNestedScrollingEnabled(false);
//                                rvText.setLayoutManager(layoutManager);
//                                rvText.setAdapter(songListRecyclerAdapter);
//                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.v("dip", "error : " + e.getMessage());
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void followApi(String userProfileId) {
        lazyLoader.setVisibility(View.VISIBLE);
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
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("followResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(ViewUserProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        getUserData();
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

    public void getTextData() {
        profileModelClassList.clear();
        profileWriterTextModelList.clear();
        lazyLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userProfileId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getUserVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("textData", result);

                    String status = jsonObject.optString("status");
                    if (!status.equalsIgnoreCase("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            // List<ProfileModelClass> profileModelClassList = ParcingUtils.parseProfileVideoModelList(jsonArray);
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
                                subcategoryId = jsonObject1.optString("sub_category");
                                if (subcategoryId.equals("4")) {
                                    ProfileWriterTextModel profileWriterTextModel = new ProfileWriterTextModel();
                                    profileWriterTextModel.setId(videoId);
                                    profileWriterTextModel.setDescription(videoUrl);
                                    profileWriterTextModel.setLikeCount(videoLikesCount);
                                    profileWriterTextModel.setTitle(videoDesp);
                                    profileWriterTextModel.setUserId(userProfileId);
                                    profileWriterTextModelList.add(profileWriterTextModel);

                                }
                            }
                            Collections.reverse(profileWriterTextModelList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ViewUserProfileActivity.this);
                            ProfileWriterTextListRecyclerAdapter songListRecyclerAdapter = new ProfileWriterTextListRecyclerAdapter(ViewUserProfileActivity.this, null, profileWriterTextModelList);
                            rvText.setNestedScrollingEnabled(false);
                            rvText.setLayoutManager(layoutManager);
                            rvText.setAdapter(songListRecyclerAdapter);
                            tvText.setText("Writing (" + profileWriterTextModelList.size() + ")");

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

    public void getVideoData() {
        profileModelClassList.clear();
        profileWriterTextModelList.clear();
        lazyLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userProfileId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getUserVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.wtf("videoData", result);

                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String status = jsonObject.optString("status");

                    if (!status.equalsIgnoreCase("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            // List<ProfileModelClass> profileModelClassList = ParcingUtils.parseProfileVideoModelList(jsonArray);
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
                                subcategoryId = jsonObject1.optString("sub_category");
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
                                    profileModelClass.setProfileId(userProfileId);
                                    profileModelClassList.add(profileModelClass);
                                }

                            }
                            Collections.reverse(profileModelClassList);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ViewUserProfileActivity.this, 3);
                            ProfileVideoWriterAdapter profileVideoAdapter = new ProfileVideoWriterAdapter(ViewUserProfileActivity.this, profileModelClassList);
                            rvVideos.setNestedScrollingEnabled(false);
                            rvVideos.setLayoutManager(mLayoutManager);
                            rvVideos.setAdapter(profileVideoAdapter);
                            tvVideo.setText("Videos (" + profileModelClassList.size() + ")");


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
}
