package com.video.tamas.Adapters;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.video.tamas.Fragments.ExoPlayerFragment;
import com.video.tamas.Models.HomePopularModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Dipendra Sharma  on 02-04-2019.
 */
public class VerticalPagerAdaptor extends FragmentStatePagerAdapter {
    String from = "";

    //integer to count number of tabs
    VerticalPagerAdaptor mAdaptor;
    Random rand = new Random();
    public List<HomePopularModel> videoDetailList;
    FragmentManager fm;

    public VerticalPagerAdaptor(FragmentManager fm, List<HomePopularModel> videoDetailLis, String from) {
        super(fm);
        this.fm = fm;
        this.mAdaptor = this;
        this.videoDetailList = videoDetailLis;
        this.from = from;
    }

    private SparseArray<ExoPlayerFragment> mFragmentsHolded = new SparseArray<>();

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object fragment = null;
        if (container != null && position != -1) {
            fragment = super.instantiateItem(container, position);
            if (fragment instanceof ExoPlayerFragment) {
                mFragmentsHolded.append(position, (ExoPlayerFragment) fragment);
            }
        }

        return fragment;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs

        if (videoDetailList.isEmpty()) {
            return new ExoPlayerFragment(from, -1, new HomePopularModel());

        } else {
            //int videoPosition=rand.nextInt(videoDetailList.size()-1);
            Log.v("dip", "video url :" + videoDetailList.get(position).getVideoUrl());
            return new ExoPlayerFragment(from, position, videoDetailList.get(position));
        }

    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        if (videoDetailList.size() == 0) {
            return 1;
        } else {
            return videoDetailList.size();
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mFragmentsHolded.delete(position);
        super.destroyItem(container, position, object);
    }

    public ExoPlayerFragment getCachedItem(int position) {
        return mFragmentsHolded.get(position, null);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }

    public void setVideoData(ArrayList<HomePopularModel> videoList) {
        for (int i = 0; i < videoDetailList.size(); i++) {
            ExoPlayerFragment removebleFragment = getCachedItem(i);
            if (removebleFragment != null) {
                fm.beginTransaction().remove(removebleFragment).commitNow();
            }
        }
        videoDetailList.clear();
        videoDetailList.addAll(videoList);
        mAdaptor.notifyDataSetChanged();
    }

}
