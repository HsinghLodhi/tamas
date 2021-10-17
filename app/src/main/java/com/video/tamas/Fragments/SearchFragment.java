package com.video.tamas.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.google.gson.Gson;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.DiscreteScrollViewAdapter;
import com.video.tamas.Adapters.HashTagTabAdapter;
import com.video.tamas.Adapters.SearchFragmentAdapter;
import com.video.tamas.Fragments.searchTabFragment.ActorTabFragment;
import com.video.tamas.Fragments.searchTabFragment.DancerTabFragment;
import com.video.tamas.Fragments.searchTabFragment.DirectorTabFragment;
import com.video.tamas.Fragments.searchTabFragment.HashTagTabFragment;
import com.video.tamas.Fragments.searchTabFragment.NeedTalentTab;
import com.video.tamas.Fragments.searchTabFragment.SingerTabFragment;
import com.video.tamas.Fragments.searchTabFragment.SongTabFragment;
import com.video.tamas.Fragments.searchTabFragment.UserTabFragment;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.DescreteModel;
import com.video.tamas.Models.TalentCategoryModel;
import com.video.tamas.Models.tag.Data1;
import com.video.tamas.Models.tag.HashTagResponse;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchFragment extends Fragment {
    Activity mActivity;
    View view;
    NetworkUtils networkUtils;
    private static RecyclerView bannerRecyclerView;
    private static DiscreteScrollViewAdapter discreteScrollViewAdapter;
    DeviceResourceManager resourceManager;
    private String userId;
    List<DescreteModel> descreteModelList = new ArrayList<>();
    RecyclerView rvVideos;
    TextView tvErrorMsg;
    EditText etSearch;
    private SearchFragmentAdapter searchFragmentAdapter;
    ImageButton btnSearch;
    final int time = 4000;
    private RadioButton rbSelectedType;
    private RadioGroup rgType;
    private String selectedType = "2";
    private TabLayout tabLayoutSearch;
    private ViewPager viewPagerSearch;
    private LinearLayout llTabAndViewPager, llBanner, llErrorMsg;
    private static OnSearchClickListener onSearchClickListener;
    private LazyLoader lazyLoader;

    public SearchFragment() {
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
        view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mActivity = getActivity();
        resourceManager = new DeviceResourceManager(mActivity);
        networkUtils = new NetworkUtils(mActivity);

//        rgType = view.findViewById(R.id.rgSearchType);
        llBanner = view.findViewById(R.id.llBanner);
        llTabAndViewPager = view.findViewById(R.id.llTabAndViewPager);
        bannerRecyclerView = view.findViewById(R.id.descreteRv);
        tabLayoutSearch = view.findViewById(R.id.tabLayoutSearch);
        viewPagerSearch = view.findViewById(R.id.viewPagerSearch);
        lazyLoader = view.findViewById(R.id.lazyLoaderSearchFrag);
        llErrorMsg = view.findViewById(R.id.llErrorMsg);

        tabLayoutSearch = view.findViewById(R.id.tabLayoutSearch);
        viewPagerSearch = view.findViewById(R.id.viewPagerSearch);
        searchFragmentAdapter = new SearchFragmentAdapter(getChildFragmentManager());
        searchFragmentAdapter.addFragment(new UserTabFragment(), "User");
        searchFragmentAdapter.addFragment(new HashTagTabFragment(), "Hashtag");
        searchFragmentAdapter.addFragment(new SongTabFragment(), "Song");
        searchFragmentAdapter.addFragment(new DancerTabFragment(), "Dancer");
        searchFragmentAdapter.addFragment(new ActorTabFragment(), "Actor");
        searchFragmentAdapter.addFragment(new SingerTabFragment(), "Singer");
        searchFragmentAdapter.addFragment(new DirectorTabFragment(), "Director");
        searchFragmentAdapter.addFragment(new NeedTalentTab(), "Need Talent");


        viewPagerSearch.setAdapter(searchFragmentAdapter);
        tabLayoutSearch.setupWithViewPager(viewPagerSearch);
        viewPagerSearch.post(new Runnable() {
            @Override
            public void run() {
                viewPagerSearch.setCurrentItem(0);
            }
        });
        viewPagerSearch.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutSearch));
        rvVideos = view.findViewById(R.id.rvVideos);
        rvVideos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvVideos.setHasFixedSize(true);
        tvErrorMsg = view.findViewById(R.id.tvErrorMessage);
        tvErrorMsg.setVisibility(View.GONE);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        //rgType.check(R.id.rbVideo);
        getBanner();
        getVideo();
        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                lazyLoader.setVisibility(View.GONE);
                if (etSearch.getText().toString().length() != 0) {
                    if (onSearchClickListener != null) {
                        onSearchClickListener.onSearch(etSearch.getText().toString(), true);

                    }
                } else {
                    etSearch.setError("Please enter video name and hashtag");
                    etSearch.requestFocus();
                }
                return true;
            }
            return false;
        });

        etSearch.setOnFocusChangeListener((view, b) -> {
            lazyLoader.setVisibility(View.GONE);
            if (b) {
                llBanner.setVisibility(View.GONE);
                llTabAndViewPager.setVisibility(View.VISIBLE);
                llErrorMsg.setVisibility(View.GONE);
            }
        });
        btnSearch.setOnClickListener(v -> {
            lazyLoader.setVisibility(View.GONE);
            if (etSearch.getText().toString().length() != 0) {

                if (onSearchClickListener != null) {
                    onSearchClickListener.onSearch(etSearch.getText().toString(), true);
                }
            } else {
                etSearch.setError("Please enter video name and hashtag");
                etSearch.requestFocus();
            }
        });
        tabLayoutSearch.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerSearch.setCurrentItem(tab.getPosition());
                if (etSearch.getText().toString().length() != 0) {
                    resourceManager.addToSharedPref("SEARCH_TEXT", etSearch.getText().toString());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public void getBanner() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("category_id", "2");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getSubcategory, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    try {
                        List<TalentCategoryModel> talentCategoryModelList = ParcingUtils.parseTalentCatModelList(jsonArray);
//
                        DiscreteScrollViewAdapter homePopularRecyclerAdapter = new DiscreteScrollViewAdapter(mActivity, talentCategoryModelList);
                        LinearLayoutManager linearLayoutManager
                                = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
                        bannerRecyclerView.setLayoutManager(linearLayoutManager);
                        bannerRecyclerView.setAdapter(homePopularRecyclerAdapter);
                        final LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
                        linearSnapHelper.attachToRecyclerView(bannerRecyclerView);

                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < (homePopularRecyclerAdapter.getItemCount() - 1)) {

                                    linearLayoutManager.smoothScrollToPosition(bannerRecyclerView, null, linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1);
                                } else if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == (homePopularRecyclerAdapter.getItemCount() - 1)) {
                                    linearLayoutManager.smoothScrollToPosition(bannerRecyclerView, null, 0);
                                }
                            }
                        }, 0, time);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {

            }

        });
    }

    public void getVideo() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.TagSort, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    Gson gson = new Gson();
                    HashTagResponse hashTagResponse = gson.fromJson(result, HashTagResponse.class);

                    if (hashTagResponse.getResponse()) {
                        List<Data1> data1List = hashTagResponse.getData();

                        HashTagTabAdapter hashTagTabAdapter = new HashTagTabAdapter(mActivity, data1List);
                        rvVideos.setAdapter(hashTagTabAdapter);
                    }


                    //JSONObject jsonObject = new JSONObject(result);

//                    String response = jsonObject.optString("response");
//                    if (response.equalsIgnoreCase("true")) {
//                        tvErrorMsg.setVisibility(View.GONE);
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                        System.out.println("SearchResponse:" + jsonArray.toString());
//                        try {
//                            List<Data1> data1List = ParcingUtils.parseTagModelList(jsonArray);
//                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), getActivity().getResources().getInteger(R.integer.number_of_grid_items));
//                            SearchAdapter searchAdapter = new SearchAdapter(mActivity, searchModelList, etSearch.getText().toString());
//                            rvVideos.setLayoutManager(mLayoutManager);
//                            rvVideos.setAdapter(searchAdapter);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        tvErrorMsg.setVisibility(View.VISIBLE);
//                        rvVideos.setVisibility(View.GONE);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
            }
        });
    }

    public interface OnSearchClickListener {
        void onSearch(String search, boolean click);
    }

    public static void setOnSearchClickListener(OnSearchClickListener listener) {
        onSearchClickListener = listener;
    }
}
