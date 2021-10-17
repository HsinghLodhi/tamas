package com.video.tamas.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Adapters.FollowingListRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.FollowingModel;
import com.video.tamas.R;
import com.video.tamas.Utils.AppAlerts;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FollowingListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SongListActivity.class.getSimpleName();
    private NetworkUtils networkUtils;
    private FollowingListRecyclerAdapter followerListRecyclerAdapter;
    RecyclerView rvSongList;
    DeviceResourceManager resourceManager;
    private String userId, userProfileId;
    TextView tvErrorMessage;

    private LazyLoader lazyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lazyLoader = findViewById(R.id.lazyLoaderFollowing);
        rvSongList = findViewById(R.id.rvSongList);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        networkUtils = new NetworkUtils(this);
        Intent intent = getIntent();
        userProfileId = intent.getStringExtra("userId");
        if (!TextUtils.isEmpty(userProfileId)) {
            getFollowingList(userProfileId);
        } else {
            getFollowingList(userId);
        }


    }

    public void getFollowingList(String userId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            Log.wtf("json", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getFollowingList, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            List<FollowingModel> followerModelList = ParcingUtils.parseFollowingModelList(jsonArray);
                            followerListRecyclerAdapter = new FollowingListRecyclerAdapter(FollowingListActivity.this, followerModelList, FollowingListActivity.this);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FollowingListActivity.this);
                            rvSongList.setLayoutManager(layoutManager);
                            rvSongList.setAdapter(followerListRecyclerAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        rvSongList.setVisibility(View.GONE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llProfileImg:
                int selectedPositionInt = (int) view.getTag();
                FollowingModel getModel = followerListRecyclerAdapter.followingModelList.get(selectedPositionInt);
                Intent intent = new Intent(FollowingListActivity.this, ViewUserProfileActivity.class);
                intent.putExtra("userId", getModel.getfollowingId());
                intent.putExtra("isFollow", "1");
                intent.putExtra("userName", getModel.getfollowingName());
                startActivity(intent);

                break;

            case R.id.btnFollowUnfollow:
                int selectedPosition = (int) view.getTag();
                String userFollowingId = followerListRecyclerAdapter.followingModelList.get(selectedPosition).getfollowingId();
                new AppAlerts().showAlertWithAction(FollowingListActivity.this, "Confirmation", "Do you want to UnFollow this user ?", "Yes", "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        followApi(userFollowingId);
                    }
                }, true);
                break;

        }
    }

    public void followApi(String userFollowerId) {
        lazyLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("to_follow_id", userFollowerId);
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
                        Toast.makeText(FollowingListActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (!TextUtils.isEmpty(userProfileId)) {
                            getFollowingList(userProfileId);
                        } else {
                            getFollowingList(userId);

                        }
                    }
                } catch (Exception e) {
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
