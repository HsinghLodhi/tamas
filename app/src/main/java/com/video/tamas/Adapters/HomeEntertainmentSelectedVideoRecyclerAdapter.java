package com.video.tamas.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.VideoListBySongIdActivity;
import com.video.tamas.Activities.ViewUserProfileActivity;
import com.video.tamas.Fragments.HomeEntertainmentSelectedVideoShowFragment;
import com.video.tamas.Listeners.VideoPlayingListener;
import com.video.tamas.Models.EntertainmentSelectedVideoModel;

import com.video.tamas.R;
import com.video.tamas.Utils.Common;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.VideoPlayerConfig;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class HomeEntertainmentSelectedVideoRecyclerAdapter extends RecyclerView.Adapter<HomeEntertainmentSelectedVideoRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
private List<EntertainmentSelectedVideoModel> homePopularModelList = new ArrayList<>();
    DeviceResourceManager resourceManager;
    HomeEntertainmentSelectedVideoShowFragment homeEntertainmentSelectedVideoShowFragment;
    private String userId;
    VideoPlayingListener videoPlayingListener;
    VideoView customVideo;
    PlayerView custom_exo_Player;
    private boolean paused = false;
    int v_position;
    MediaPlayer mp_adapter;
    SimpleExoPlayer player = null;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = -10.0f * 360.0f;
    Animation animFadein, animslideup;
    public HomeEntertainmentSelectedVideoRecyclerAdapter(Activity activity, List<EntertainmentSelectedVideoModel> homePopularModelList, HomeEntertainmentSelectedVideoShowFragment homeEntertainmentSelectedVideoShowFragment) {
        this.activity = activity;
        this.homePopularModelList = homePopularModelList;
        this.homeEntertainmentSelectedVideoShowFragment = homeEntertainmentSelectedVideoShowFragment;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(activity).inflate(R.layout.item_popular, parent, false);
        CatalogListViewHolder catalogListViewHolder = new CatalogListViewHolder(rowView);
        resourceManager = new DeviceResourceManager(activity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        return catalogListViewHolder;
    }


    @Override
    public void onBindViewHolder(final HomeEntertainmentSelectedVideoRecyclerAdapter.CatalogListViewHolder holder, final int position) {

        EntertainmentSelectedVideoModel homePopularModel = homePopularModelList.get(position);
        String userProfileId = homePopularModel.getUserProfileId();
        String videoDescription = homePopularModel.getVideoDescription();
        String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(videoDescription);
        String songName = homePopularModel.getVideoSongName();
        String songId = homePopularModel.getVideoSongId();
        String videoUrl = homePopularModel.getVideoUrl();
        String videoId = homePopularModel.getVideoId();
        String userProfileName = homePopularModel.getUserProfileName();
        String likeCount = homePopularModel.getTotalLikeCount();
        String commentCount = homePopularModel.getTotalCommentCount();
        String songImage = homePopularModel.getSongImage();
        String isFollow = homePopularModel.getIsFollow();
        String commentStatus = homePopularModel.getCommentStatus();
        String totalShareCount=homePopularModel.getTotalShareCount();
        String totalViewCount=homePopularModel.getVideoLikesCount();

        customVideo = holder.videoView;
        custom_exo_Player = holder.exoplayerView;

        holder.tvUserProfileId.setText(userProfileName);
        holder.tvVideoDescription.setText(fromServerUnicodeDecoded);
        if (songName.equals("false")){}
        else{
            holder.tvSongName.setText(songName);
            holder.tvSongName.setSelected(true);
            holder.tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.tvSongName.setSingleLine(true);
            holder.tvSongName.setMarqueeRepeatLimit(-1);
            holder.tvSongName.setFocusableInTouchMode(true);
            holder.tvSongName.setFocusable(true);}
        holder.tvShareCount.setText(totalShareCount);
        holder.tvViewCount.setText(totalViewCount);

        if (userProfileId.equals(userId)) {
            holder.ivFollow.setVisibility(View.GONE);
        } else {
            holder.ivFollow.setVisibility(View.VISIBLE);
        }
        if (isFollow.equals("1")) {
            Glide.with(activity)
                    .load(R.drawable.ic_right_member)
                    .into(holder.ivFollow);

        } else {
            Glide.with(activity)
                    .load(R.drawable.ic_add_user)
                    .into(holder.ivFollow);
        }


        holder.tvLikeCount.setText(likeCount);
        holder.tvCommentCount.setText(commentCount);
        if (!TextUtils.isEmpty(userId)) {
            String isLike = homePopularModel.getIsLike();
            if (isLike.equals("1")) {
                holder.ivLike.setLiked(true);
            } else {
                holder.ivLike.setLiked(false);
            }

        } else {
            holder.ivLike.setLiked(false);
        }
        final Uri video = Uri.parse(videoUrl);
        holder.videoView.setVideoURI(video);
        holder.videoView.start();

        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               /* if (!holder.videoView.isPlaying()) {
                    mp.setLooping(true);
                    holder.videoView.start();
                    popularFragment.addVideoCount(videoId);
                    popularFragment.setFirstTimeValue(false);

                } else {
                    holder.videoView.stopPlayback();
                }*/
                mp.setLooping(true);
                //holder.videoView.start();
                mp_adapter = mp;
                homeEntertainmentSelectedVideoShowFragment.addVideoCount(videoId);
                //homeEntertainmentSelectedVideoShowFragment.setFirstTimeValue(false);
            }
        });
        v_position = 0;
        holder.videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (holder.videoView.isPlaying()){

                    v_position = holder.videoView.getCurrentPosition();
                    holder.videoView.pause();
                    Log.e("STOPPED  ","------ "+v_position);
                }
                else{
                    holder.videoView.seekTo(v_position);
                    holder.videoView.start();
                    Log.e("STARTED  ","------ "+v_position);
                }
                return false;
            }
        });


        Glide.with(activity)
                .load(homePopularModel.getVideoProfileImage())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                .into(holder.ivProfilePic);
//        holder.ivSongImage.setImageResource(R.drawable.circle);
//         holder.ivProfilePic.setBackgroundResource(R.drawable.circle_imageview);
        Glide.with(activity)
                .load(songImage)
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.drawable.ic_cd))
                .into(holder.ivSongImage);
        Common.imageRotation(holder.ivSongImage);

        ///////////////

        holder.music_tone.clearAnimation();
    /*    RotateAnimation r = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.27f, Animation.RELATIVE_TO_SELF, 0.18f);*/
        RotateAnimation r = new RotateAnimation(30, 360 , holder.music_tone.getX()+holder.ivSongImage.getWidth()/2 ,
                holder.music_tone.getY()+holder.ivSongImage.getHeight()/2);
        r.setDuration(9000);
        r.setInterpolator(new LinearInterpolator());
        r.setRepeatMode(Animation.RESTART);
        r.setRepeatCount(Animation.INFINITE);
        holder.music_tone.startAnimation(r);

        holder.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentStatus.equals("1")) {
                    homeEntertainmentSelectedVideoShowFragment.getCommentList(videoId);
                } else {
                    homeEntertainmentSelectedVideoShowFragment.showNoCommentDialog();
                }

            }
        });
        holder.ivLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid");
                    int pos = holder.getAdapterPosition();
                    Log.w("adapterPos", String.valueOf(pos));
                    homeEntertainmentSelectedVideoShowFragment.likeVideoApi(videoId);

                } else {
                    Log.wtf("userid", "notuserid");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.recreate();
                }

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid");
                    int pos = holder.getAdapterPosition();
                    Log.w("adapterPos", String.valueOf(pos));
                    homeEntertainmentSelectedVideoShowFragment.likeVideoApi(videoId);
                } else {
                    Log.wtf("userid", "notuserid");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.recreate();
                }
            }
        });
        holder.ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid");
                    homeEntertainmentSelectedVideoShowFragment.followApi(userProfileId);
                } else {
                    Log.wtf("userid", "notuserid");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });
       /* holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid");

                    File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String fileName = homePopularModel.getVideoUrl().substring(homePopularModel.getVideoUrl().lastIndexOf('/') + 1);
                    File myFile = new File(sdcard, fileName);

                    if (myFile.exists()) {
                        homeEntertainmentSelectedVideoShowFragment.shareVideo("tamas1",myFile.getAbsolutePath());
                        homeEntertainmentSelectedVideoShowFragment.addShareCount(videoId);
                    } else {
                        homeEntertainmentSelectedVideoShowFragment.videoDownload(videoUrl);
                        homeEntertainmentSelectedVideoShowFragment.addShareCount(videoId);
                    }

                } else {
                    Log.wtf("userid", "notuserid");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }

            }
        });
        */
        holder.ivSongImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, VideoListBySongIdActivity.class);
                intent.putExtra("songId", songId);
                activity.startActivity(intent);
            }
        });

        holder.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ViewUserProfileActivity.class);
                intent.putExtra("userId", userProfileId);
                intent.putExtra("isFollow", isFollow);
                intent.putExtra("userName", userProfileName);
                activity.startActivity(intent);
            }
        });

        /// Exo Player ///////////////////
        if (homePopularModelList.get(position).getShow_video()==1) {

            try {

                if (player == null) {
                    holder.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    holder.exoplayerView.setControllerAutoShow(false);
                    holder.exoplayerView.setUseController(false);
                    holder.exoplayerView.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (player.getPlayWhenReady()) {
                                holder.play_pause_img.setVisibility(View.VISIBLE);
                                player.setPlayWhenReady(false);
                                //Toast.makeText(activity, "Stop Video...", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.play_pause_img.setVisibility(View.GONE);
                                player.setPlayWhenReady(true);
                                //Toast.makeText(activity, "Start Video...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // 1. Create a default TrackSelector
                    LoadControl loadControl = new DefaultLoadControl(
                            new DefaultAllocator(true, 16),
                            VideoPlayerConfig.MIN_BUFFER_DURATION,
                            VideoPlayerConfig.MAX_BUFFER_DURATION,
                            VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                            VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    TrackSelector trackSelector =
                            new DefaultTrackSelector(videoTrackSelectionFactory);
                    // 2. Create the player
                    player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(activity), trackSelector,
                            loadControl);
                    holder.exoplayerView.setPlayer(player);

                    buildMediaSource(video);
                } else {
                    player.stop();
                    player.release();
//                player.setPlayWhenReady(false);
//                player=null;
                    holder.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    holder.exoplayerView.setControllerAutoShow(false);
                    holder.exoplayerView.setUseController(false);
                    holder.exoplayerView.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (player.getPlayWhenReady()) {
                                holder.play_pause_img.setVisibility(View.VISIBLE);
                                player.setPlayWhenReady(false);
                                //Toast.makeText(activity, "Stop Video...", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.play_pause_img.setVisibility(View.GONE);
                                player.setPlayWhenReady(true);
                                //Toast.makeText(activity, "Start Video...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    // 1. Create a default TrackSelector
                    LoadControl loadControl = new DefaultLoadControl(
                            new DefaultAllocator(true, 16),
                            VideoPlayerConfig.MIN_BUFFER_DURATION,
                            VideoPlayerConfig.MAX_BUFFER_DURATION,
                            VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                            VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    TrackSelector trackSelector =
                            new DefaultTrackSelector(videoTrackSelectionFactory);
                    // 2. Create the player
                    player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(activity), trackSelector,
                            loadControl);
                    holder.exoplayerView.setPlayer(player);

                    buildMediaSource(video);
                    //Toast.makeText(activity, "New data ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{ }

        /// End Of Exo Player /////////////

    }

    @Override
    public int getItemCount() {
        return homePopularModelList.size();
    }

    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserProfileId, tvVideoDescription, tvSongName, tvFollow,tvShareCount,tvViewCount;
        ImageView ivProfilePic, ivComment, ivSongImage, ivShare, ivFollow, play_pause_img, music_tone;
        VideoView videoView;
        TextView tvLikeCount, tvCommentCount;
        LikeButton ivLike;
        PlayerView exoplayerView;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
            exoplayerView = rowView.findViewById(R.id.exo_playerView);
            tvUserProfileId = rowView.findViewById(R.id.tvUserProfileId);
            tvVideoDescription = rowView.findViewById(R.id.tvVideoDescription);
            tvSongName = rowView.findViewById(R.id.tvSongName);
            tvLikeCount = rowView.findViewById(R.id.tvLikeCount);
            tvCommentCount = rowView.findViewById(R.id.tvCommentCount);
            ivProfilePic = rowView.findViewById(R.id.ivProfilePic);
            ivLike = rowView.findViewById(R.id.ivLike);
            ivFollow = rowView.findViewById(R.id.ivFollow);
            ivShare = rowView.findViewById(R.id.ivShare);
            ivComment = rowView.findViewById(R.id.ivComment);
            ivSongImage = rowView.findViewById(R.id.ivSongImage);
            videoView = rowView.findViewById(R.id.videoView);
            tvShareCount=rowView.findViewById(R.id.tvShareCount);
            tvViewCount=rowView.findViewById(R.id.tvViewCount);
            play_pause_img=rowView.findViewById(R.id.play_pause_img);
            music_tone = rowView.findViewById(R.id.ivSong_tone);
        }
    }

    public void onDestroy() {
        customVideo.stopPlayback();
        if(player!=null){
            player.release();
            player.stop();
            player.setPlayWhenReady(false);
            player = null;
            custom_exo_Player = null;
//        Toast.makeText(activity, "Player Stopped.....", Toast.LENGTH_SHORT).show();
        }
    }

    public void chg_Video(int position){
        for (int i=0 ; i<homePopularModelList.size() ; i++){
            homePopularModelList.get(i).setShow_video(0);
        }
        homePopularModelList.get(position).setShow_video(1);
    }


    private void buildMediaSource(Uri mUri) {
        String app_nm = activity.getResources().getString(R.string.app_name);
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(activity,
                Util.getUserAgent(activity, app_nm) , bandwidthMeter);
        // This is the MediaSource representing the media to be played..
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        // player.addListener(this);
    }


}
