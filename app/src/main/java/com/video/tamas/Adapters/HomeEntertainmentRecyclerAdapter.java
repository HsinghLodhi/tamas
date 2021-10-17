package com.video.tamas.Adapters;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
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
import com.video.tamas.Fragments.PopularFragment;
import com.video.tamas.Models.HomeEntertainmentModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Common;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class HomeEntertainmentRecyclerAdapter extends RecyclerView.Adapter<HomeEntertainmentRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<HomeEntertainmentModel> catalogListModelList;
    private String userId;
    DeviceResourceManager resourceManager;

    public HomeEntertainmentRecyclerAdapter(Activity activity, List<HomeEntertainmentModel> catalogListModelList) {
        this.activity = activity;
        this.catalogListModelList = catalogListModelList;

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entertainment, parent, false);
        resourceManager = new DeviceResourceManager(activity);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        HomeEntertainmentModel catalogListModel = catalogListModelList.get(position);
        holder.tvLikeCount.setText(catalogListModel.getTotalLikeCount());
        String description = catalogListModel.getVideoDescription();
        String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(description);
        holder.tvDescription.setText(fromServerUnicodeDecoded);
        if (!TextUtils.isEmpty(userId)) {
            String isLike = catalogListModel.getIsLike();
            if (isLike.equals("1")) {
                holder.ivLike.setLiked(true);
            } else {
                holder.ivLike.setLiked(false);
            }

        } else {
            holder.ivLike.setLiked(false);
        }
        Glide.with(activity).load(catalogListModel.getVideoGif()).into(holder.gifIv);
        Glide.with(activity)
                .load(catalogListModel.getVideoProfileImage())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                .into(holder.ivProfilePic);
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            Log.w("adapterPos", String.valueOf(pos));
            PopularFragment dashBoardTradesFragment = new PopularFragment(pos, "1", "other",catalogListModel.getVideoId());
            /*HomeEntertainmentSelectedVideoShowFragment dashBoardTradesFragment = new HomeEntertainmentSelectedVideoShowFragment();
            Bundle bundle = new Bundle();
            bundle.putString("adapterPosition", String.valueOf(pos));
            dashBoardTradesFragment.setArguments(bundle);
            */
            Common.changeFragment((FragmentActivity) activity, dashBoardTradesFragment);
        });


    }

    @Override
    public int getItemCount() {
        return catalogListModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        GifImageView gifIv;
        ImageView ivProfilePic;
        TextView tvLikeCount, tvDescription;
        LikeButton ivLike;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
            gifIv = rowView.findViewById(R.id.ivCategory);
            ivProfilePic = rowView.findViewById(R.id.ivProfilePic);
            ivLike = rowView.findViewById(R.id.ivLike);
            tvLikeCount = rowView.findViewById(R.id.tvLikeCount);
            tvDescription = rowView.findViewById(R.id.tvDescription);
        }
    }
}
