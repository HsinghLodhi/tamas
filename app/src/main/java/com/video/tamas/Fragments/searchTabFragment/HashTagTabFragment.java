package com.video.tamas.Fragments.searchTabFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HashTagTabFragment extends Fragment {
    private RecyclerView recyclerView;
    private LazyLoader lazyLoader;
    private View view;
    private NetworkUtils networkUtils;
    private DeviceResourceManager resourceManager;
    private Activity mActivity = getActivity();
    private TextView tvErrMsg;


    public HashTagTabFragment() {
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
        view = inflater.inflate(R.layout.fragment_hash_tag_tab, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resourceManager = new DeviceResourceManager(mActivity);
        networkUtils = new NetworkUtils(mActivity);
        recyclerView = view.findViewById(R.id.rvHashTagTab);
        lazyLoader = view.findViewById(R.id.lazyLoaderHashTagTab);
        tvErrMsg = view.findViewById(R.id.tvHashTagTabErrMsg);
    }

    @Override
    public void onResume() {
        super.onResume();
        //  SearchFragment.setOnSearchClickListener(this);
    }


    public void getHashTag(String search) {
        lazyLoader.setVisibility(View.VISIBLE);
        tvErrMsg.setVisibility(View.GONE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("type", "10");
            jsonBody.put("search_text", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.search, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    if (status.equalsIgnoreCase("success")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        System.out.println("SearchResponse:" + jsonArray.toString());
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
}
