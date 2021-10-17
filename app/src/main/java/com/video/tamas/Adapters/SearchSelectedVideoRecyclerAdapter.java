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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.VideoListBySongIdActivity;
import com.video.tamas.Activities.ViewUserProfileActivity;
import com.video.tamas.Fragments.SearchSelectedVideoShowFragment;
import com.video.tamas.Models.SearchModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Common;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class SearchSelectedVideoRecyclerAdapter extends RecyclerView.Adapter<SearchSelectedVideoRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<SearchModel> homePopularModelList = new ArrayList<>();
    DeviceResourceManager resourceManager;
    SearchSelectedVideoShowFragment homeEntertainmentSelectedVideoShowFragment;
    private String userId;

    public SearchSelectedVideoRecyclerAdapter(Activity activity, List<SearchModel> homePopularModelList, SearchSelectedVideoShowFragment homeEntertainmentSelectedVideoShowFragment) {
        this.activity = activity;
        this.homePopularModelList = homePopularModelList;
        this.homeEntertainmentSelectedVideoShowFragment = homeEntertainmentSelectedVideoShowFragment;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(activity).inflate(R.layout.item_selected_search_video, parent, false);
        CatalogListViewHolder catalogListViewHolder = new CatalogListViewHolder(rowView);
        resourceManager = new DeviceResourceManager(activity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        return catalogListViewHolder;
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        SearchModel homePopularModel = homePopularModelList.get(position);
        String userProfileId = homePopularModel.getUserProfileId();
        String videoDescription = homePopularModel.getVideoDescription();
        String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(videoDescription);
        String songName = homePopularModel.getVideoSongName();
        String videoUrl = homePopularModel.getVideoUrl();
        String videoId = homePopularModel.getVideoId();
        String userProfileName = homePopularModel.getUserProfileName();
        String likeCount = homePopularModel.getTotalLikeCount();
        String commentCount = homePopularModel.getTotalCommentCount();
        String songImage = homePopularModel.getSongImage();
        String isFollow = homePopularModel.getIsFollow();
        String commentStatus = homePopularModel.getCommentStatus();
        String categoryId = homePopularModel.getCategoryId();
        String songId = homePopularModel.getVideoSongId();
        String totalShareCount=homePopularModel.getTotalShareCount();
        String totalViewCount=homePopularModel.getVideoLikesCount();

        holder.tvUserProfileId.setText(userProfileName);
        holder.tvVideoDescription.setText(fromServerUnicodeDecoded);
        holder.tvSongName.setText(songName);
        holder.tvSongName.setSelected(true);
        holder.tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvSongName.setSingleLine(true);
        holder.tvSongName.setMarqueeRepeatLimit(-1);
        holder.tvSongName.setFocusableInTouchMode(true);
        holder.tvSongName.setFocusable(true);

        holder.tvShareCount.setText(totalShareCount);
        holder.tvViewCount.setText(totalViewCount);
        if (userProfileId.equals(userId)) {
            holder.ivFollow.setVisibility(View.GONE);
        } else {
            holder.ivFollow.setVisibility(View.VISIBLE);
        }
        if (categoryId.equals("2")) {
            holder.ivSongImage.setVisibility(View.GONE);
        } else {
            holder.ivSongImage.setVisibility(View.VISIBLE);
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
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                holder.videoView.start();
            }
        });
        Glide.with(activity)
                .load(songImage)
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.drawable.ic_cd))
                .into(holder.ivSongImage);
        Common.imageRotation(holder.ivSongImage);
        Glide.with(activity)
                .load(homePopularModel.getVideoProfileImage())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                .into(holder.ivProfilePic);

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
        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userId)) {
                    File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String fileName = homePopularModel.getVideoUrl().substring(homePopularModel.getVideoUrl().lastIndexOf('/') + 1);
                    File myFile = new File(sdcard, fileName);

                    if (myFile.exists()) {
                        homeEntertainmentSelectedVideoShowFragment.shareVideo("Tamas", myFile.getAbsolutePath());
                    } else {
                        homeEntertainmentSelectedVideoShowFragment.videoDownload(videoUrl);
                    }
                } else {
                    Log.wtf("userid", "notuserid");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }

            }
        });
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
    }

    @Override
    public int getItemCount() {
        return homePopularModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserProfileId, tvVideoDescription, tvSongName, tvFollow,tvShareCount,tvViewCount;
        ImageView ivProfilePic, ivComment, ivSongImage, ivFollow, ivShare;
        VideoView videoView;
        LikeButton ivLike;
        TextView tvLikeCount, tvCommentCount;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
            tvUserProfileId = rowView.findViewById(R.id.tvUserProfileId);
            tvVideoDescription = rowView.findViewById(R.id.tvVideoDescription);
            tvSongName = rowView.findViewById(R.id.tvSongName);
            ivProfilePic = rowView.findViewById(R.id.ivProfilePic);
            videoView = rowView.findViewById(R.id.videoView);
            ivLike = rowView.findViewById(R.id.ivLike);
            ivFollow = rowView.findViewById(R.id.ivFollow);
            ivComment = rowView.findViewById(R.id.ivComment);
            ivShare = rowView.findViewById(R.id.ivShare);
            ivSongImage = rowView.findViewById(R.id.ivSongImage);
            tvLikeCount = rowView.findViewById(R.id.tvLikeCount);
            tvCommentCount = rowView.findViewById(R.id.tvCommentCount);
            tvShareCount=rowView.findViewById(R.id.tvShareCount);
            tvViewCount=rowView.findViewById(R.id.tvViewCount);
        }
    }
}
