package com.video.tamas.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Adapters.VerticalPagerAdaptor;
import com.video.tamas.Fragments.ExoPlayerFragment;
import com.video.tamas.Fragments.PopularFragment;
import com.video.tamas.Models.HomePopularModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.DirectionalViewPager;
import com.video.tamas.Utils.NetworkUtils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;

public class EntertainmentVideoListActivity extends AppCompatActivity {
    NetworkUtils networkUtils;
    DiscreteScrollView rvVideos;
    BottomSheetDialog dialog;
    DeviceResourceManager resourceManager;
    private String userId;
    TextView tvErrorMessage;
    View commentView;
    ArrayList<HomePopularModel> homePopularModelList;
    FloatingActionButton btnMakeVideo;
    private MaterialDialog materialDialog;
    String subcategoryId, subcategoryName;
    private LazyLoader lazyLoader;
    private int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment_video_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Entertainment");
        lazyLoader = findViewById(R.id.lazyLoaderEntertainmentVideoList);
        PopularFragment.videoViewPager = (DirectionalViewPager) findViewById(R.id.videoViewPager1);
        rvVideos = findViewById(R.id.rvVideos1);
        networkUtils = new NetworkUtils(this);
        btnMakeVideo = findViewById(R.id.btnMakeVideo1);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        tvErrorMessage = findViewById(R.id.tvErrorMessage1);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        homePopularModelList = (ArrayList<HomePopularModel>) args.getSerializable("ARRAYLIST");
        pos = intent.getIntExtra("pos", 0);
        setVideoData(homePopularModelList, pos);
        btnMakeVideo.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(userId)) {
                Log.wtf("userid", "userid");
                startActivity(new Intent(getApplicationContext(), EntertainmentVideoMakeActivity.class)
                        .putExtra("from", "entertainment")
                        .putExtra("subcategoryId", "0"));
            } else {
                Log.wtf("userid", "notuserid");
                //startActivity(new Intent(getApplicationContext(), EntertainmentVideoMakeActivity.class));
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }


    public void setVideoData(ArrayList<HomePopularModel> videoList, int videoPosition) {
        if (videoList != null) {
            FragmentManager videoFragmentManager = getSupportFragmentManager();

            PopularFragment.videoViewPager.setOrientation(1);

            PopularFragment.videoViewPager.setOffscreenPageLimit(1);

            //allVideoInfoList = videoList;
            PopularFragment.verticalPagerAdaptor = new VerticalPagerAdaptor(videoFragmentManager, videoList, "other");

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

}
