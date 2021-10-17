package com.video.tamas .Adapters;

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
import com.video.tamas.Models.FollowingModel;
import com.video.tamas.R;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class FollowingListRecyclerAdapter extends RecyclerView.Adapter<FollowingListRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
    public List<FollowingModel> followingModelList = new ArrayList<>();
    private View.OnClickListener mClickLisner;
    MediaPlayer mp;

    public FollowingListRecyclerAdapter(Activity activity, List<FollowingModel> followingModelList, View.OnClickListener clickLisner) {
        this.activity = activity;
        this.followingModelList = followingModelList;
this.mClickLisner=clickLisner;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_following_list, parent, false);

        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        FollowingModel followingModel = followingModelList.get(position);
        String pic = followingModel.getfollowingPic();
        Log.wtf("pic", pic);
        Glide.with(activity)
                .load(pic)

                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.drawable.default_user))
                .into(holder.ivProfilePic);

        holder.tvFollowerName.setText(followingModel.getfollowingName());
holder.btnFollowUnfollow.setText("Following");
holder.llProfileImg.setTag(position);
holder.llProfileImg.setOnClickListener(mClickLisner);
holder.btnFollowUnfollow.setTag(position);
holder.btnFollowUnfollow.setOnClickListener(mClickLisner);
    }

    @Override
    public int getItemCount() {
        return followingModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvFollowerName;
Button btnFollowUnfollow;
LinearLayout llProfileImg;
        public CatalogListViewHolder(View rowView) {
            super(rowView);
            tvFollowerName = rowView.findViewById(R.id.tvFollowerName);
            ivProfilePic = rowView.findViewById(R.id.ivProfilePic);
btnFollowUnfollow=(Button)rowView.findViewById(R.id.btnFollowUnfollow);
llProfileImg=(LinearLayout) rowView.findViewById(R.id.llProfileImg);
        }
    }
}
