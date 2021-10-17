package com.video.tamas.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.NeedTalentAdvtListRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.NeedTalentAdvertisModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class NeedTalentFragment extends Fragment {
    Activity mActivity;
    NetworkUtils networkUtils;
    RecyclerView rvCategories;
    View view;
    String userId;
    DeviceResourceManager resourceManager;
    private LazyLoader lazyLoader;

    public NeedTalentFragment() {
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
        view = inflater.inflate(R.layout.fragment_need_talent, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        rvCategories = view.findViewById(R.id.rvVideos);
        lazyLoader = view.findViewById(R.id.lazyLoaderNeed);
        networkUtils = new NetworkUtils(mActivity);
        resourceManager = new DeviceResourceManager(mActivity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
       // getNeedTalentAdvtList();
    }

    public void getNeedTalentAdvtList() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.need_talent_advt_list, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.removeAllViews();
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    try {
                        List<NeedTalentAdvertisModel> talentCategoryModelList = ParcingUtils.parseNeedTalentAdvtListModelList(jsonArray);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
                        NeedTalentAdvtListRecyclerAdapter homeTalentCatRecyclerAdapter = new NeedTalentAdvtListRecyclerAdapter(NeedTalentFragment.this, talentCategoryModelList);
                        rvCategories.setLayoutManager(layoutManager);
                        rvCategories.setAdapter(homeTalentCatRecyclerAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                lazyLoader.removeAllViews();
                lazyLoader.setVisibility(View.GONE);
            }

        });
    }

    public void applyTalent(String producerUserId, String talentId) {
        lazyLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", "1");
            jsonBody.put("post_user_id", producerUserId);
            jsonBody.put("need_talent_id", talentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.need_talent_apply, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.removeAllViews();
                lazyLoader.setVisibility(View.GONE);
                Log.wtf("result", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equalsIgnoreCase("error")) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                lazyLoader.removeAllViews();
                lazyLoader.setVisibility(View.GONE);
            }

        });
    }
}
