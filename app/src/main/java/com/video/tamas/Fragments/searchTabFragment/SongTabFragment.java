package com.video.tamas.Fragments.searchTabFragment;

import android.app.Activity;
import android.media.MediaPlayer;
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
import com.google.gson.Gson;
import com.video.tamas.Activities.VolleyCallback;
import com.video.tamas.Adapters.SongTabAdapter;
import com.video.tamas.Fragments.SearchFragment;
import com.video.tamas.Models.search.SongTabResponse;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SongTabFragment extends Fragment implements SearchFragment.OnSearchClickListener, SongTabAdapter.OnMusicPlayerListener {
    private RecyclerView recyclerView;
    private View view;
    private NetworkUtils networkUtils;
    private DeviceResourceManager resourceManager;
    private Activity mActivity;
    private LazyLoader lazyLoader;
    private TextView tvErrMsg;
    private String searchText = null;
    private boolean isVisible = false, play = false;
    private MediaPlayer mediaPlayer;


    public SongTabFragment() {
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
        view = inflater.inflate(R.layout.fragment_song_tab, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        networkUtils = new NetworkUtils(mActivity);
        recyclerView = view.findViewById(R.id.rvSongTabFragment);
        lazyLoader = view.findViewById(R.id.lazyLoaderSongsTab);
        tvErrMsg = view.findViewById(R.id.tvSongsTabErrMsg);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        setUserVisibleHint(isVisible);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisibleToUser && mActivity != null) {
            resourceManager = new DeviceResourceManager(mActivity);
            searchText = resourceManager.getDataFromSharedPref("SEARCH_TEXT", "");
            if (searchText != null && !searchText.equals("")) {
                getSong(searchText);
                resourceManager.clearSharedPref("SEARCH_TEXT");
            } else {

            }
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        SearchFragment.setOnSearchClickListener(this);
        SongTabAdapter.setOnMusicPlayerListener(this);
    }

    @Override
    public void onMusicListener(boolean play, MediaPlayer mediaPlayer) {
        this.play = play;
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onSearch(String search, boolean click) {
        if (click) {
            getSong(search);
        }
    }

    public void getSong(String search) {
        lazyLoader.setVisibility(View.VISIBLE);
        tvErrMsg.setVisibility(View.GONE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("type", "11");
            jsonBody.put("search_text", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.search, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                Gson gson = new Gson();
                SongTabResponse songTabResponse = gson.fromJson(result, SongTabResponse.class);
                if (songTabResponse.getStatus().equals("success")) {
                    if (songTabResponse.getData() != null) {
                        SongTabAdapter songTabAdapter = new SongTabAdapter(mActivity, songTabResponse.getData());
                        recyclerView.setAdapter(songTabAdapter);
                    } else {
                        tvErrMsg.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailureResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
            }
        });
    }
}
