package com.video.tamas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.tamas.Models.HomePopularModel;
import com.video.tamas.Models.tag.Data;
import com.video.tamas.Models.tag.Data1;
import com.video.tamas.Models.tag.VideoData;
import com.video.tamas.R;

import java.util.List;
import java.util.Random;

public class HashTagTabAdapter extends RecyclerView.Adapter<HashTagTabAdapter.HashTagTabViewHolder> {
    private Activity context;
    private List<Data1> hashTagList;

    public HashTagTabAdapter(Activity context, List<Data1> hashTagList) {
        this.context = context;
        this.hashTagList = hashTagList;
    }


    @NonNull
    @Override
    public HashTagTabViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.hash_tag_parent_layout, viewGroup, false);
        return new HashTagTabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HashTagTabViewHolder holder, int position) {
        int total = 0;
        List<VideoData> data = hashTagList.get(position).getData();
        String tagName = hashTagList.get(position).getTagName().replace("#", "");
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getViewsCount() != null)
                total += Integer.parseInt(data.get(i).getViewsCount());
        }
        holder.tvHashTag.setText(tagName);
        holder.tvTotalView.setText(total + " views");
        holder.rvChildHashTagTab.setAdapter(new HashTagChildAdapter(context, data));
    }

    private int getRandomViews() {
        Random random = new Random();
        int num = random.nextInt(100);
        return num;
    }

    @Override
    public int getItemCount() {
        return hashTagList.size();
    }

    class HashTagTabViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivHashTag;
        private TextView tvHashTag, tvTotalView;
        private RecyclerView rvChildHashTagTab;

        public HashTagTabViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHashTag = itemView.findViewById(R.id.ivHashTag);
            tvHashTag = itemView.findViewById(R.id.tvHashTag);
            tvTotalView = itemView.findViewById(R.id.tvTotalView);
            rvChildHashTagTab = itemView.findViewById(R.id.rvChildHashTagTab);
            rvChildHashTagTab.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rvChildHashTagTab.setHasFixedSize(true);
        }
    }
}
