

package com.video.tamas.Activities;

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

import com.afollestad.materialdialogs.MaterialDialog;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Adapters.LikeListRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.LikeModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LikeListActivity extends AppCompatActivity {
    private static final String TAG = LikeListActivity.class.getSimpleName();
    private NetworkUtils networkUtils;
    RecyclerView rvSongList;
    DeviceResourceManager resourceManager;
    private String userId, userProfileId;
    TextView tvErrorMessage;
    private LazyLoader lazyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lazyLoader = findViewById(R.id.lazyLoaderLike);
        rvSongList = findViewById(R.id.rvSongList);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        networkUtils = new NetworkUtils(this);
        Intent intent = getIntent();
        userProfileId = intent.getStringExtra("userId");
        if (!TextUtils.isEmpty(userProfileId)) {
            getLikeList(userProfileId);
        } else {
            getLikeList(userId);
        }
    }

    public void getLikeList(String userId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.show_like_list, jsonBody, new VolleyCallback() {
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
                            List<LikeModel> followerModelList = ParcingUtils.parseLikeModelList(jsonArray);
                            LikeListRecyclerAdapter followerListRecyclerAdapter = new LikeListRecyclerAdapter(LikeListActivity.this, followerModelList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LikeListActivity.this);
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

}
