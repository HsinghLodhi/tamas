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
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.VideoBySongDetailedListActivity;
import com.video.tamas.Activities.ViewUserProfileActivity;
import com.video.tamas.Models.VideoBySongIdModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class VideoBySongDetailedRecyclerAdapter extends RecyclerView.Adapter<VideoBySongDetailedRecyclerAdapter.CatalogListViewHolder> {

    private VideoBySongDetailedListActivity activity;
    private List<VideoBySongIdModel> videoBySongIdModelList = new ArrayList<>();
    DeviceResourceManager resourceManager;
    private String userId;

    public VideoBySongDetailedRecyclerAdapter(VideoBySongDetailedListActivity activity, List<VideoBySongIdModel> videoBySongIdModelList) {
        this.activity = activity;
        this.videoBySongIdModelList = videoBySongIdModelList;

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(activity).inflate(R.layout.item_selected_videobysong_detail, parent, false);
        CatalogListViewHolder catalogListViewHolder = new CatalogListViewHolder(rowView);
        resourceManager = new DeviceResourceManager(activity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        return catalogListViewHolder;
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        VideoBySongIdModel videoBySongIdModel = videoBySongIdModelList.get(position);
        String videoId = videoBySongIdModel.getVideoId();
        String videoUrl = videoBySongIdModel.getVideoPath();
        String userProfileId = videoBySongIdModel.getUserId();
        String userName = videoBySongIdModel.getUserName();
        String likeCount = videoBySongIdModel.getLikeCount();
        String commentCount = videoBySongIdModel.getCommentCount();
        String songName = videoBySongIdModel.getSongName();
        String songImage = videoBySongIdModel.getSongImage();
        String isLike = videoBySongIdModel.getIsLike();
        String isFollow = videoBySongIdModel.getIsFollow();
        String commentStatus = videoBySongIdModel.getCommentStatus();

        holder.tvUserProfileId.setText(userName);
        holder.tvVideoDescription.setText("");
        holder.tvSongName.setText(songName);
        holder.tvSongName.setSelected(true);
        holder.tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvSongName.setSingleLine(true);
        holder.tvSongName.setMarqueeRepeatLimit(-1);
        holder.tvSongName.setFocusableInTouchMode(true);
        holder.tvSongName.setFocusable(true);

        holder.tvLikeCount.setText(likeCount);
        holder.tvCommentCount.setText(commentCount);

        Glide.with(activity)
                .load(videoBySongIdModel.getProfilePic())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                .into(holder.ivProfilePic);

        if (userProfileId.equals(userId)) {
            holder.ivFollow.setVisibility(View.GONE);
        } else {
            holder.ivFollow.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(userId)) {
            if (isLike.equals("1")) {
                holder.ivLike.setLiked(true);
            } else {
                holder.ivLike.setLiked(false);
            }

        } else {
            holder.ivLike.setLiked(false);
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
        final Uri video = Uri.parse(videoUrl);
        holder.videoView.setVideoURI(video);
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                holder.videoView.start();
            }
        });

        holder.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentStatus.equals("1")) {
                    activity.getCommentList(videoId);
                } else {
                    activity.showNoCommentDialog();
                }
                activity.getCommentList(videoId);
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
                    activity.likeVideoApi(videoId, String.valueOf(pos));
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
                    activity.followApi(userId);
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
                    String fileName = videoBySongIdModel.getVideoPath().substring(videoBySongIdModel.getVideoPath().lastIndexOf('/') + 1);
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
                intent.putExtra("userName", userName);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoBySongIdModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserProfileId, tvVideoDescription, tvSongName, tvFollow;
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
        }
    }
}

