package com.video.tamas.Adapters;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.ViewUserProfileActivity;
import com.video.tamas.Activities.WriterListUploadActivity;
import com.video.tamas.Models.WriterListModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class WriterListRecyclerAdapter extends RecyclerView.Adapter<WriterListRecyclerAdapter.CatalogListViewHolder> {

    private WriterListUploadActivity activity;
    private List<WriterListModel> writerListModelList = new ArrayList<>();
    MediaPlayer mp;
    DeviceResourceManager resourceManager;
    private String userId;

    public WriterListRecyclerAdapter(WriterListUploadActivity activity, List<WriterListModel> writerListModelList) {
        this.activity = activity;
        this.writerListModelList = writerListModelList;

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_writer_list, parent, false);
        resourceManager = new DeviceResourceManager(activity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        WriterListModel writerListModel = writerListModelList.get(position);
        String userProfileName = writerListModel.getUserName();
        String userProfileId = writerListModel.getUserId();
        String text = writerListModel.getText();
        String createdDate = writerListModel.getCreatedDate();
        String description = writerListModel.getDescription();
        String likeCount = writerListModel.getLikeCount();
        String textId = writerListModel.getTextId();
        String isLike = writerListModel.getIsLike();
        String isFollow = writerListModel.getIsFollow();
        if (!TextUtils.isEmpty(userId)) {
            if (isLike.equals("1")) {
                holder.ivLike.setLiked(true);
            } else {
                holder.ivLike.setLiked(false);
            }

        } else {
            holder.ivLike.setLiked(false);
        }

        parseDateToddMMyyyy(createdDate);
        Log.wtf("datee", parseDateToddMMyyyy(createdDate));
        holder.tvCommentDate.setText(parseDateToddMMyyyy(createdDate));
        String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(text);
        String serverDescription = StringEscapeUtils.unescapeJava(description);
        holder.tvCommentMessage.setText(fromServerUnicodeDecoded);
        holder.tvdescription.setText(serverDescription);
        holder.tvUserName.setText(userProfileName);
        holder.tvLikeCount.setText(likeCount);
        Glide.with(activity)
                .load(writerListModel.getProfilePic())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                .into(holder.ivUserImage);

        holder.ivLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid");
                    int pos = holder.getAdapterPosition();
                    Log.w("adapterPos", String.valueOf(pos));
                    activity.likeVideoApi(textId, String.valueOf(pos));

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
                    activity.likeVideoApi(textId, String.valueOf(pos));
                } else {
                    Log.wtf("userid", "notuserid");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.recreate();
                }
            }
        });
        holder.tvUserName.setOnClickListener(new View.OnClickListener() {
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
        return writerListModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {

        TextView tvCommentMessage, tvUserName, tvCommentDate, tvdescription, tvLikeCount;
        ImageView ivUserImage;
        LikeButton ivLike;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
            tvCommentDate = rowView.findViewById(R.id.tvCommentDate);
            tvCommentMessage = rowView.findViewById(R.id.tvCommentMessage);
            tvUserName = rowView.findViewById(R.id.tvUserName);
            ivUserImage = rowView.findViewById(R.id.ivUserImage);
            tvdescription = rowView.findViewById(R.id.tvDescription);
            tvLikeCount = rowView.findViewById(R.id.tvLikeCount);
            ivLike = rowView.findViewById(R.id.ivLike);
        }
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
