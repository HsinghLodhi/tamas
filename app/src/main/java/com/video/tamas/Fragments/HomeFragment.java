package com.video.tamas.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.video.tamas.Adapters.HomeViewPagerAdapter;
import com.video.tamas.R;


public class HomeFragment extends Fragment {
    Activity mActivity;
    ViewPager viewPager;
    TabLayout tabLayout;
    private static OnTabChangeListener onTabChangeListener;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        viewPager = mActivity.findViewById(R.id.viewpager);
        tabLayout = mActivity.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                if (onTabChangeListener != null) {
                    if (i == 1) {
                        onTabChangeListener.onTabChange(i, true);
                    } else {
                        onTabChangeListener.onTabChange(i, false);
                    }
                }
                // viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  Toast.makeText(mActivity, "Home Fragment Destroyed...", Toast.LENGTH_SHORT).show();
    }

    private void setupViewPager(ViewPager viewPager) {
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager());
        homeViewPagerAdapter.addFragment(new EntertainmentFragment(), "Entertainment");
        homeViewPagerAdapter.addFragment(new PopularFragment(0, "1", "Main"), "Popular");
        homeViewPagerAdapter.addFragment(new TalentFragment(), "Talent");
        homeViewPagerAdapter.addFragment(new NeedTalentFragment(), "Need Talent");
        viewPager.setAdapter(homeViewPagerAdapter);
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(1);
            }
        });

    }



    public interface OnTabChangeListener {
        void onTabChange(int pageNumber, boolean page);
    }

    public static void setOnTabChangeListener(OnTabChangeListener listener) {
        onTabChangeListener = listener;
    }
}
