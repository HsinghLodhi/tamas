package com.video.tamas.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.bumptech.glide.Glide;
import com.video.tamas.Activities.EditProfileActivity;
import com.video.tamas.Activities.FollowerListActivity;
import com.video.tamas.Activities.FollowingListActivity;
import com.video.tamas.Activities.LikeListActivity;
import com.video.tamas.Activities.SettingActivity;
import com.video.tamas.Activities.VolleyCallback;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;


public class ProfileFragment extends Fragment {
    Activity mActivity;
    View view;
    NetworkUtils networkUtils;
    ArrayList<ProfileModelClass> homeEntertainmentClassArrayList = new ArrayList<>();
    RecyclerView rvVideos, rvText;
    TextView tvErrorMessage;
    DeviceResourceManager resourceManager;
    private String userId;
    TextView tvFollower, tvFollowing, tvUserName, tvTamasUserName, tvLikes, tvTotalVideo;
    CircleImageView ivProfilePic;
    LinearLayout llFollower, llFollowing, llLike;
    Button btnEditProfile;
    TextView tvVideos, tvText;
    String subcategoryId;
    LinearLayout llRvVideos, llRvText;
    List<ProfileModelClass> profileModelClassList = new ArrayList<>();
    List<ProfileWriterTextModel> profileWriterTextModelList = new ArrayList<>();
    private LazyLoader lazyLoader;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        showBackButton();

    }

    public void showBackButton() {
        if (getActivity() instanceof SettingActivity) {
            ((SettingActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("                            Profile");
        lazyLoader = view.findViewById(R.id.lazyLoaderProfile);
        rvVideos = view.findViewById(R.id.rvVideos);
        rvText = view.findViewById(R.id.rvText);
        tvVideos = view.findViewById(R.id.tvVideos);
        tvText = view.findViewById(R.id.tvText);
        llRvVideos = view.findViewById(R.id.llrvVideos);
        llRvText = view.findViewById(R.id.llrvText);
        networkUtils = new NetworkUtils(mActivity);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        llFollower = view.findViewById(R.id.llFollower);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvFollower = view.findViewById(R.id.tvFollower);
        tvFollowing = view.findViewById(R.id.tvFollowing);
        // tvTotalVideo=view.findViewById(R.id.tvTotalVideo);
        tvTamasUserName = view.findViewById(R.id.tvTamasUserName);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        Glide.with(mActivity).load(R.drawable.default_user).into(ivProfilePic);
        llFollowing = view.findViewById(R.id.llFollowing);
        llLike = view.findViewById(R.id.llLike);
        tvLikes = view.findViewById(R.id.tvLikes);
        tvUserName = view.findViewById(R.id.tvUserName);
        resourceManager = new DeviceResourceManager(mActivity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        //getUserData();
        getTextData();
        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(mActivity, EditProfileActivity.class)));
        llFollower.setOnClickListener(v -> startActivity(new Intent(mActivity, FollowerListActivity.class)));
        llFollowing.setOnClickListener(v -> startActivity(new Intent(mActivity, FollowingListActivity.class)));
        llLike.setOnClickListener(v -> startActivity(new Intent(mActivity, LikeListActivity.class)));
        tvVideos.setOnClickListener(v -> {
            tvVideos.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.colorPrimary));
            tvText.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.colorBlack));
            llRvText.setVisibility(View.GONE);
            llRvVideos.setVisibility(View.VISIBLE);
            getVideoData();
        });
        tvText.setOnClickListener(v -> {
            tvVideos.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
            tvText.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
            getTextData();
            llRvVideos.setVisibility(View.GONE);
            llRvText.setVisibility(View.VISIBLE);

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                Intent intent = new Intent(mActivity, SettingActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
                break;

        }
        return super.onOptionsItemSelected(item);


    }

    public void getUserData() {
        profileModelClassList.clear();
        profileWriterTextModelList.clear();
        lazyLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getUserVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String status = jsonObject.optString("status");
                    String profilePic = jsonObject.optString("profile_image");
                    String follower = jsonObject.optString("followers");
                    String following = jsonObject.optString("following");
                    String likes = jsonObject.optString("total_likes");
                    String totalVideo = jsonObject.optString("total_videos");
                    String userName = jsonObject.optString("username");
                    tvTamasUserName.setText(userName);
                    tvFollower.setText(follower);
                    tvFollowing.setText(following);
                    tvLikes.setText(likes);
                    //tvTotalVideo.setText("User Video :  " + totalVideo + " videos");

                    Glide.with(mActivity)
                            .load(profilePic)
                            .apply(placeholderOf(R.drawable.default_user))
                            .into(ivProfilePic);
                    tvVideos.performClick();
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
                                String subcategoryId = jsonObject1.optString("sub_category");
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
//                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mActivity, 3);
//                                ProfileVideoAdapter profileVideoAdapter = new ProfileVideoAdapter(mActivity, profileModelClassList);
//                                rvVideos.setLayoutManager(mLayoutManager);
//                                rvVideos.setNestedScrollingEnabled(false);
//                                rvVideos.setAdapter(profileVideoAdapter);
//                            }
//                            if (profileWriterTextModelList.size() == 0) {
//                                llRvText.setVisibility(View.GONE);
//                            } else {
//                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
//                                ProfileWriterTextListRecyclerAdapter songListRecyclerAdapter = new ProfileWriterTextListRecyclerAdapter(null, ProfileFragment.this, profileWriterTextModelList);
//                                rvText.setNestedScrollingEnabled(false);
//                                rvText.setLayoutManager(layoutManager);
//                                rvText.setAdapter(songListRecyclerAdapter);
//                            }
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


    @Override
    public void onResume() {
        super.onResume();
        getUserData();
    }

    public void getTextData() {
        profileModelClassList.clear();
        profileWriterTextModelList.clear();
        lazyLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);

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
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
                            ProfileWriterTextListRecyclerAdapter songListRecyclerAdapter = new ProfileWriterTextListRecyclerAdapter(null, ProfileFragment.this, profileWriterTextModelList);
                            rvText.setNestedScrollingEnabled(false);
                            rvText.setLayoutManager(layoutManager);
                            rvText.setAdapter(songListRecyclerAdapter);
                            tvText.setText("Writing  (" + profileWriterTextModelList.size() + ")");

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
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
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
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mActivity, 3);
                            ProfileVideoWriterAdapter profileVideoAdapter = new ProfileVideoWriterAdapter(mActivity, profileModelClassList);
                            rvVideos.setNestedScrollingEnabled(false);
                            rvVideos.setLayoutManager(mLayoutManager);
                            rvVideos.setAdapter(profileVideoAdapter);
                            tvVideos.setText("Videos (" + profileModelClassList.size() + ")");


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
