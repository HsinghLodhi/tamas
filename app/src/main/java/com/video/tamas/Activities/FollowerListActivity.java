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
import com.video.tamas.Adapters.FollowerListRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.FollowerModel;
import com.video.tamas.R;
import com.video.tamas.Utils.AppAlerts;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FollowerListActivity extends AppCompatActivity implements View.OnClickListener {
    private NetworkUtils networkUtils;
    RecyclerView rvSongList;
    DeviceResourceManager resourceManager;
    private String userId, userProfileId;
    TextView tvErrorMessage;
    FollowerListRecyclerAdapter followerListRecyclerAdapter;
    private LazyLoader lazyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvSongList = findViewById(R.id.rvSongList);
        lazyLoader = findViewById(R.id.lazyLoaderFollower);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        networkUtils = new NetworkUtils(this);
        Intent intent = getIntent();
        userProfileId = intent.getStringExtra("userId");
        if (!TextUtils.isEmpty(userProfileId)) {
            getFollowerList(userProfileId);
        } else {
            getFollowerList(userId);
        }
    }

    public void getFollowerList(String userId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getFollowerList, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    Log.v("dip", "follower list response : " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            List<FollowerModel> followerModelList = ParcingUtils.parseFollowerModelList(jsonArray);
                            followerListRecyclerAdapter = new FollowerListRecyclerAdapter(FollowerListActivity.this, followerModelList, FollowerListActivity.this);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FollowerListActivity.this);
                            rvSongList.setLayoutManager(layoutManager);
                            rvSongList.setAdapter(followerListRecyclerAdapter);
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
                int selectedPosition = (int) view.getTag();
                FollowerModel getModel = followerListRecyclerAdapter.followerModelList.get(selectedPosition);
                Intent intent = new Intent(FollowerListActivity.this, ViewUserProfileActivity.class);
                intent.putExtra("userId", getModel.getFollowerId());
                intent.putExtra("isFollow", getModel.getMe_following());
                intent.putExtra("userName", getModel.getFollowerName());
                startActivity(intent);

                break;

            case R.id.btnFollowUnfollow:
                int selectedPositionInt = (int) view.getTag();
                String userFollowerId = followerListRecyclerAdapter.followerModelList.get(selectedPositionInt).getFollowerId();
                String meFollowStatus = followerListRecyclerAdapter.followerModelList.get(selectedPositionInt).getMe_following();
                if (meFollowStatus.equalsIgnoreCase("1")) {
                    new AppAlerts().showAlertWithAction(FollowerListActivity.this, "Confirmation", "Do you want to UnFollow this user ?", "Yes", "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            followApi(userFollowerId);
                        }
                    }, true);

                } else {
                    new AppAlerts().showAlertWithAction(FollowerListActivity.this, "Confirmation", "Do you want to Follow this user ?", "Yes", "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            followApi(userFollowerId);
                        }
                    }, true);
                }
                //followApi(userFollowerId);
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
                        Toast.makeText(FollowerListActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (!TextUtils.isEmpty(userProfileId)) {
                            getFollowerList(userProfileId);
                        } else {
                            getFollowerList(userId);

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

}
