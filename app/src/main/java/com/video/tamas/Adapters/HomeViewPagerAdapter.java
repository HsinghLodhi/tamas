package com.video.tamas.Adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.video.tamas.Fragments.EntertainmentFragment;
import com.video.tamas.Fragments.NeedTalentFragment;
import com.video.tamas.Fragments.PopularFragment;
import com.video.tamas.Fragments.TalentFragment;

import java.util.ArrayList;
import java.util.List;


public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
        ///
    }






  /*
    private FragmentManager activity;


    public HomeViewPagerAdapter(FragmentManager activity) {
        super(activity);
        this.activity = activity;

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0) {
            fragment = new EntertainmentFragment();

        } else if (position == 1) {
            fragment = new PopularFragment();
        } else if (position == 2) {
            fragment = new TalentFragment();
        } else if (position == 3) {
            fragment = new NeedTalentFragment();
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        super.destroyItem(container, position, object);

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        if (position == 0) {
            title = "Entertainment";
        } else if (position == 1) {
            title = "Popular";
        } else if (position == 2) {
            title = "Talent";
        } else if (position == 3) {
            title = "Need Talent";
        }
        return title;
    }*/

}
