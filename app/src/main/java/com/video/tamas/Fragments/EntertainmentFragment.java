package com.video.tamas.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.HomeEntertainmentRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.HomeEntertainmentModel;
import com.video.tamas.Models.HomePopularModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;


public class EntertainmentFragment extends Fragment {
    Activity mActivity;
    View view;
    NetworkUtils networkUtils;
    RecyclerView rvVideos;
    TextView tvErrorMessage;
    DeviceResourceManager resourceManager;
    private LazyLoader lazyLoader;
    private String userId;

    public EntertainmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_entertainment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        rvVideos = view.findViewById(R.id.rvVideos);
        networkUtils = new NetworkUtils(mActivity);
        lazyLoader = view.findViewById(R.id.lazyLoaderEnter);
        resourceManager = new DeviceResourceManager(mActivity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        //getVideo();
    }

    public void getVideo() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("category_id", "1");
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getVideos, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    Log.v("dip", "HomeEntertainmentModel response : " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    if (!status.equalsIgnoreCase("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            List<HomeEntertainmentModel> homeEntertainmentModelList = ParcingUtils.parseHomeEntertainmentModelList(jsonArray);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Objects.requireNonNull(getActivity()).getResources().getInteger(R.integer.number_of_grid_items));
                            HomeEntertainmentRecyclerAdapter homeEntertainmentRecyclerAdapter = new HomeEntertainmentRecyclerAdapter(mActivity, homeEntertainmentModelList);
                            rvVideos.setLayoutManager(mLayoutManager);
                            rvVideos.setAdapter(homeEntertainmentRecyclerAdapter);
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
        getVideo();

    }
}
