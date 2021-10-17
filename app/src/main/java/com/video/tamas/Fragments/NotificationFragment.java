package com.video.tamas.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.NotificationListRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.NotificationModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class NotificationFragment extends Fragment {
    Activity mActivity;
    View view;
    NetworkUtils networkUtils;
    private String userId;
    RecyclerView rv;
    TextView tvErrorMessage;
    DeviceResourceManager resourceManager;
    RelativeLayout containerLL;
    LazyLoader tashieLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("                         Notifications");
        rv = view.findViewById(R.id.rvList);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        resourceManager = new DeviceResourceManager(mActivity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        networkUtils = new NetworkUtils(mActivity);
        tashieLoader = view.findViewById(R.id.myLoader);
        getNotificationList();
    }

    public void getNotificationList() {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
//                .content("Loading...")
//                .cancelable(false)
//                .progress(true, 0)
//                .show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.notificationList, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                tashieLoader.setVisibility(View.GONE);
                Log.wtf("resp", result);
                //materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            List<NotificationModel> followerModelList = ParcingUtils.parseNotificationListModelList(jsonArray);
                            NotificationListRecyclerAdapter followerListRecyclerAdapter = new NotificationListRecyclerAdapter(mActivity, followerModelList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
                            rv.setLayoutManager(layoutManager);
                            rv.setAdapter(followerListRecyclerAdapter);
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
                //materialDialog.dismiss();
                tashieLoader.setVisibility(View.GONE);
            }

        });
    }
}
