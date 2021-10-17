package com.video.tamas.Adapters;

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
import com.video.tamas.Activities.CategorywiseVideoListActivity;
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.ViewUserProfileActivity;
import com.video.tamas.Listeners.VideoPlayingListener;
import com.video.tamas.Models.HomePopularModel;
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

public class CategorywiseVideoListRecyclerAdapter extends RecyclerView.Adapter<CategorywiseVideoListRecyclerAdapter.CatalogListViewHolder> {

    private CategorywiseVideoListActivity activity;
    private List<HomePopularModel> homePopularModelList = new ArrayList<>();
    DeviceResourceManager resourceManager;
    private String userId;
    VideoPlayingListener videoPlayingListener;
    VideoView customVideo;

    public CategorywiseVideoListRecyclerAdapter(CategorywiseVideoListActivity activity, List<HomePopularModel> homePopularModelList) {
        this.activity = activity;
        this.homePopularModelList = homePopularModelList;

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(activity).inflate(R.layout.item_subcategory_videos, parent, false);
        CatalogListViewHolder catalogListViewHolder = new CatalogListViewHolder(rowView);
        resourceManager = new DeviceResourceManager(activity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        return catalogListViewHolder;
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        HomePopularModel homePopularModel = homePopularModelList.get(position);
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
        String totalShareCount=homePopularModel.getTotalShareCount();
        String totalViewCount=homePopularModel.getVideoLikesCount();
        customVideo = holder.videoView;

        holder.tvUserProfileId.setText(userProfileName);
        holder.tvVideoDescription.setText(fromServerUnicodeDecoded);
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
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (!holder.videoView.isPlaying()) {
                    mp.setLooping(true);
                    holder.videoView.start();
                    activity.addVideoCount(videoId);
                } else {
                    holder.videoView.stopPlayback();
                }

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
        holder.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentStatus.equals("1")) {
                    activity.getCommentList(videoId);
                } else {
                    activity.showNoCommentDialog();
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
                    activity.likeVideoApi(videoId, String.valueOf(pos));


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
                    activity.followApi(userProfileId);

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
                        activity.shareVideo("Tamas", myFile.getAbsolutePath());
                    } else {
                        activity.videoDownload(videoUrl);
                    }
                } else {
                    Log.wtf("userid", "notuserid");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }

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
        ImageView ivProfilePic, ivComment, ivSongImage, ivShare, ivFollow;
        VideoView videoView;
        TextView tvLikeCount, tvCommentCount;
        LikeButton ivLike;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
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
        }
    }

    public void onDestroy() {
        customVideo.stopPlayback();

    }

}
