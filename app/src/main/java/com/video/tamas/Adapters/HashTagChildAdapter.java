package com.video.tamas.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.video.tamas.Fragments.PopularFragment;
import com.video.tamas.Models.tag.VideoData;
import com.video.tamas.R;
import com.video.tamas.Utils.Common;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class HashTagChildAdapter extends RecyclerView.Adapter<HashTagChildAdapter.HashTagChildViewHolder> {
    private Activity context;
    private List<VideoData> dataList;

    public HashTagChildAdapter(Activity context, List<VideoData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public HashTagChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.hash_tag_child_layout, viewGroup, false);
        return new HashTagChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HashTagChildViewHolder holder, int i) {
        VideoData data = dataList.get(i);
        Glide.with(context).load(data.getImageName()).into(holder.ivGifChildHashTagTab);
        //Picasso.get().load(data.getImageName()).into(holder.ivGifChildHashTagTab);
        holder.itemView.setOnClickListener(view -> {
            PopularFragment popularFragment = new PopularFragment(i, String.valueOf(data.getCategoriesId()), "Search_Fragment", String.valueOf(data.getId()));
            Common.changeFragment((FragmentActivity) context, popularFragment);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class HashTagChildViewHolder extends RecyclerView.ViewHolder {
        private GifImageView ivGifChildHashTagTab;

        public HashTagChildViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGifChildHashTagTab = itemView.findViewById(R.id.ivGifChildHashTagTab);
        }
    }
}
