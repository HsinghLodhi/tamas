package com.video.tamas.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.video.tamas.Adapters.VideoBySongIdAdapter;
import com.video.tamas.Models.VideoBySongIdModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class VideoListBySongIdActivity extends AppCompatActivity {
    private NetworkUtils networkUtils;
    RecyclerView rvSongList;
    DeviceResourceManager resourceManager;
    private String userId;
    TextView tvErrorMessage;
    ImageView ivProfilePic;
    TextView tvUserName, tvSongCount;
    String songId;
    List<VideoBySongIdModel> videoBySongIdModels = new ArrayList<>();
    Button btnSongSelected;
    String songPath, songDuration, songName, songCount;
    private LazyLoader lazyLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list_by_song_id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lazyLoader = findViewById(R.id.lazyLoaderUseSongList);
        rvSongList = findViewById(R.id.rvVideos);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnSongSelected = findViewById(R.id.btnSongSelected);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        networkUtils = new NetworkUtils(this);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvUserName = findViewById(R.id.tvSongUploadUserName);
        tvSongCount = findViewById(R.id.tvVideoCount);
        Intent intent = getIntent();
        songId = intent.getStringExtra("songId");
        Log.wtf("songId", songId);
        getVideoList(songId);
        btnSongSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), EntertainmentVideoMakeActivity.class);
                intent1.putExtra("songId", songId);
                intent1.putExtra("songPath", songPath);
                intent1.putExtra("songDuration", songDuration);
                intent1.putExtra("songName", songName);
                startActivity(intent1);
                resourceManager.addToSharedPref("USE_SONG", true);
                finish();
            }
        });
    }

    public void getVideoList(String songId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("song_id", songId);
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getVideoBySongId, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                btnSongSelected.setEnabled(true);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("status");


                    if (status.equalsIgnoreCase("success")) {
                        JSONObject jsonSongData = jsonObject.getJSONObject("song_data");
                        songPath = jsonSongData.optString("song_location");
                        songDuration = jsonSongData.optString("duration");
                        songName = jsonSongData.optString("song_name");
                        songCount = jsonSongData.optString("count");
                        tvSongCount.setText(songCount + " Videos");
                    }


                    if (status.equalsIgnoreCase("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("song_user_data");
                        String userPic = jsonObject1.optString("photoURL");
                        String userName = jsonObject1.optString("username");
                        tvUserName.setText(userName);
                        Log.wtf("picPath", userPic);
                        Glide.with(VideoListBySongIdActivity.this)
                                .load(userPic)
                                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.drawable.default_user))
                                .into(ivProfilePic);
                    }


                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String videoPath = jsonObject2.optString("file_name_mp4");
                            String videoGif = jsonObject2.optString("file_name_gif");
                            Log.wtf("gif", videoGif);
                            VideoBySongIdModel videoBySongIdModel = new VideoBySongIdModel();
                            videoBySongIdModel.setVideoGif(videoGif);
                            videoBySongIdModel.setSongId(songId);
                            videoBySongIdModel.setSongName(songName);
                            videoBySongIdModels.add(videoBySongIdModel);
                        }
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(VideoListBySongIdActivity.this, 3);
                        VideoBySongIdAdapter profileVideoAdapter = new VideoBySongIdAdapter(VideoListBySongIdActivity.this, videoBySongIdModels);
                        rvSongList.setLayoutManager(mLayoutManager);
                        rvSongList.setAdapter(profileVideoAdapter);

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
}
