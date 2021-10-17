package com.video.tamas.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Fragments.UserPersonalVideoShowFragment;
import com.video.tamas.Models.ProfileModelClass;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 2/13/2017.
 */

public class UserPersonalVideoRecyclerAdapter extends RecyclerView.Adapter<UserPersonalVideoRecyclerAdapter.CatalogListViewHolder> {
    private Activity activity;
    private List<ProfileModelClass> homePopularModelList = new ArrayList<>();
    DeviceResourceManager resourceManager;
    UserPersonalVideoShowFragment homeEntertainmentSelectedVideoShowFragment;
    private String userId;

    public UserPersonalVideoRecyclerAdapter(Activity activity, List<ProfileModelClass> homePopularModelList, UserPersonalVideoShowFragment homeEntertainmentSelectedVideoShowFragment) {
        this.activity = activity;
        this.homePopularModelList = homePopularModelList;
        this.homeEntertainmentSelectedVideoShowFragment = homeEntertainmentSelectedVideoShowFragment;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(activity).inflate(R.layout.item_user_videos, parent, false);
        CatalogListViewHolder catalogListViewHolder = new CatalogListViewHolder(rowView);
        resourceManager = new DeviceResourceManager(activity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        return catalogListViewHolder;
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        ProfileModelClass homePopularModel = homePopularModelList.get(position);
        String videoDescription = homePopularModel.getVideoDescription();
        String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(videoDescription);
        String songName = homePopularModel.getVideoSongName();
        String videoUrl = homePopularModel.getVideoUrl();
        String videoId = homePopularModel.getVideoId();
        String likeCount = homePopularModel.getVideoLikesCount();
        String commentCount = homePopularModel.getVideoCommentCount();
        String commentStatus = homePopularModel.getCommentStatus();
        String viewCount=homePopularModel.getViewCount();
        String profileId=homePopularModel.getProfileId();
        if (profileId.equals(userId)){
            holder.ivDelete.setVisibility(View.VISIBLE);

        }else {
            holder.ivDelete.setVisibility(View.GONE);
        }


        holder.tvVideoDescription.setText(fromServerUnicodeDecoded);
        holder.tvSongName.setText(songName);
        holder.tvSongName.setSelected(true);
        holder.tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvSongName.setSingleLine(true);
        holder.tvSongName.setMarqueeRepeatLimit(-1);
        holder.tvSongName.setFocusableInTouchMode(true);
        holder.tvSongName.setFocusable(true);

        holder.tvLikeCount.setText(likeCount);
        holder.tvCommentCount.setText(commentCount);
        holder.tvViewCount.setText(viewCount);

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


        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeEntertainmentSelectedVideoShowFragment.videoDelete(videoId);
            }
        });

    }


    @Override
    public int getItemCount() {
        return homePopularModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserProfileId, tvVideoDescription, tvSongName, tvFollow,tvViewCount;
        ImageView ivProfilePic, ivComment, ivSongImage, ivFollow, ivShare, ivDelete;
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
            ivDelete = rowView.findViewById(R.id.ivDelete);
            ivSongImage = rowView.findViewById(R.id.ivSongImage);
            tvLikeCount = rowView.findViewById(R.id.tvLikeCount);
            tvCommentCount = rowView.findViewById(R.id.tvCommentCount);
            tvViewCount=rowView.findViewById(R.id.tvViewCount);

        }

    }


}
