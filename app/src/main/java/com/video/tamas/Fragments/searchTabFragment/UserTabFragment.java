package com.video.tamas.Fragments.searchTabFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.UserTabListAdapter;
import com.video.tamas.Fragments.SearchFragment;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.search.User;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserTabFragment extends Fragment implements SearchFragment.OnSearchClickListener {
    private RecyclerView recyclerView;
    private View view;
    private NetworkUtils networkUtils;
    private DeviceResourceManager resourceManager;
    private Activity mActivity;
    private TextView tvErrMsg;
    private LazyLoader lazyLoader;
    private List<User> userList;
    private String searchText = null;
    private boolean isVisible = false;

    public UserTabFragment() {
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
        view = inflater.inflate(R.layout.fragment_user_tab, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        recyclerView = view.findViewById(R.id.rvUserTab);
        networkUtils = new NetworkUtils(mActivity);
        tvErrMsg = view.findViewById(R.id.tvUserTabErrMsg);
        lazyLoader = view.findViewById(R.id.lazyLoaderUserTab);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        setUserVisibleHint(isVisible);
    }

    @Override
    public void onSearch(String search, boolean click) {
        if (click) {
            getUser(search);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SearchFragment.setOnSearchClickListener(this);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisibleToUser && mActivity != null) {
            resourceManager = new DeviceResourceManager(mActivity);
            searchText = resourceManager.getDataFromSharedPref("SEARCH_TEXT", "");
            if (searchText != null && !searchText.equals("")) {
                getUser(searchText);
                resourceManager.clearSharedPref("SEARCH_TEXT");
            } else {

            }
        }
    }

    public void getUser(String search) {
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
                            userList = ParcingUtils.parseUserModelList(jsonArray);
                            UserTabListAdapter userTabListAdapter = new UserTabListAdapter(mActivity, userList);
                            recyclerView.setAdapter(userTabListAdapter);

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
