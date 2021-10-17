package com.video.tamas.Adapters;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.video.tamas.R;
import com.video.tamas.Models.FollowerModel;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class FollowerListRecyclerAdapter extends RecyclerView.Adapter<FollowerListRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
    public List<FollowerModel> followerModelList = new ArrayList<>();
    private View.OnClickListener mClickLisner;
    MediaPlayer mp;

    public FollowerListRecyclerAdapter(Activity activity, List<FollowerModel> followerModelList, View.OnClickListener clickLisner) {
        this.activity = activity;
        this.followerModelList = followerModelList;
this.mClickLisner=clickLisner;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follower_list, parent, false);

        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        FollowerModel followerModel = followerModelList.get(position);
        String pic=followerModel.getFollowerPic();
        Log.wtf("pic",pic);
        Glide.with(activity)
                .load(followerModel.getFollowerPic())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.drawable.default_user))
                .into(holder.ivProfilePic);

        holder.tvFollowerName.setText(followerModel.getFollowerName());
        holder.llProfileImg.setTag(position);
        holder.llProfileImg.setOnClickListener(mClickLisner);
        holder.btnFollowUnFollow.setTag(position);
        holder.btnFollowUnFollow.setOnClickListener(mClickLisner);
        if (followerModel.getMe_following().equalsIgnoreCase("1")){

            //holder.btnFollowUnFollow.setVisibility(View.GONE);
            holder.btnFollowUnFollow.setText("UnFollow");
            holder.btnFollowUnFollow.setBackgroundColor(activity.getResources().getColor(R.color.colorRed));
        }else {
            holder.btnFollowUnFollow.setText("Follow Back");
            holder.btnFollowUnFollow.setText("Follow Back");
            holder.btnFollowUnFollow.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));


        }

    }

    @Override
    public int getItemCount() {
        return followerModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvFollowerName;
Button btnFollowUnFollow;
LinearLayout llProfileImg;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
            tvFollowerName = rowView.findViewById(R.id.tvFollowerName);
            ivProfilePic = rowView.findViewById(R.id.ivProfilePic);
btnFollowUnFollow=(Button)rowView.findViewById(R.id.btnFollowUnfollow);
llProfileImg=(LinearLayout) rowView.findViewById(R.id.llProfileImg);

        }
    }
}
