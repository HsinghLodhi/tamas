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

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.HomeTalentCatRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.TalentCategoryModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class TalentFragment extends Fragment {
    Activity mActivity;
    View view;
    NetworkUtils networkUtils;
    RecyclerView rvCategories;
    private LazyLoader lazyLoader;
    private static OnTalentSelectListener onTalentSelectListener;


    public TalentFragment() {
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
        view = inflater.inflate(R.layout.fragment_talent, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        rvCategories = view.findViewById(R.id.rvVideos);
        lazyLoader = view.findViewById(R.id.lazyLoaderTalent);
        networkUtils = new NetworkUtils(mActivity);
        //getCategory();
    }


    public void getCategory() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("category_id", "2");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getSubcategory, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.removeAllViews();
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    try {
                        List<TalentCategoryModel> talentCategoryModelList = ParcingUtils.parseTalentCatModelList(jsonArray);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), getActivity().getResources().getInteger(R.integer.number_of_grid_items));
                        HomeTalentCatRecyclerAdapter homeTalentCatRecyclerAdapter = new HomeTalentCatRecyclerAdapter(mActivity, talentCategoryModelList);
                        rvCategories.setLayoutManager(mLayoutManager);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (onTalentSelectListener != null) {
            if (isVisibleToUser) {
                onTalentSelectListener.onTalentSelect(true);
            } else {
                onTalentSelectListener.onTalentSelect(false);
            }
        }
        if (isVisibleToUser) {
            Log.w("visibleTalent", "Talent");
            getCategory();
        } else {
            Log.w("NotvisibleTalent", "Talent");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public interface OnTalentSelectListener {
        void onTalentSelect(boolean select);
    }

    public static void setOnTalentSelectListener(OnTalentSelectListener listener) {
        onTalentSelectListener = listener;
    }
}
