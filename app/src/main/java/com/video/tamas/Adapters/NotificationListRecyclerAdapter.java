package com.video.tamas.Adapters;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.video.tamas.Models.FollowerModel;
import com.video.tamas.Models.NotificationModel;
import com.video.tamas.R;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class NotificationListRecyclerAdapter extends RecyclerView.Adapter<NotificationListRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<NotificationModel> followerModelList = new ArrayList<>();
    MediaPlayer mp;

    public NotificationListRecyclerAdapter(Activity activity, List<NotificationModel> followerModelList) {
        this.activity = activity;
        this.followerModelList = followerModelList;

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_list, parent, false);

        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        NotificationModel followerModel = followerModelList.get(position);
        String pic=followerModel.getProfilePic();
        Log.wtf("pic",pic);
        Glide.with(activity)
                .load(followerModel.getProfilePic())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.drawable.default_user))
                .into(holder.ivProfilePic);

        holder.tvFollowerName.setText(followerModel.getMessage());

    }

    @Override
    public int getItemCount() {
        return followerModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvFollowerName;


        public CatalogListViewHolder(View rowView) {
            super(rowView);
            tvFollowerName = rowView.findViewById(R.id.tvFollowerName);
            ivProfilePic = rowView.findViewById(R.id.ivProfilePic);

        }
    }
}
