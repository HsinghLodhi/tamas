package com.video.tamas.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.video.tamas.Activities.VideoBySongDetailedListActivity;
import com.video.tamas.Models.VideoBySongIdModel;
import com.video.tamas.R;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by user1 on 2/13/2017.
 */

public class VideoBySongIdAdapter extends RecyclerView.Adapter<VideoBySongIdAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<VideoBySongIdModel> videoBySongIdModels = new ArrayList<>();

    public VideoBySongIdAdapter(Activity activity, List<VideoBySongIdModel> videoBySongIdModels) {
        this.activity = activity;
        this.videoBySongIdModels = videoBySongIdModels;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(activity).inflate(R.layout.item_videobysongid, parent, false);
        CatalogListViewHolder catalogListViewHolder = new CatalogListViewHolder(rowView);
        return catalogListViewHolder;
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        VideoBySongIdModel videoBySongIdModel = videoBySongIdModels.get(position);
        String songId = videoBySongIdModel.getSongId();
        String songName=videoBySongIdModel.getSongName();

        Glide.with(activity).load(videoBySongIdModel.getVideoGif()).into(holder.gifIv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Intent intent = new Intent(activity, VideoBySongDetailedListActivity.class);
                intent.putExtra("songId", songId);
                intent.putExtra("songName", songName);
                intent.putExtra("adapterPosition", String.valueOf(pos));
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoBySongIdModels.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        GifImageView gifIv;
        TextView tvDraft;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
            gifIv = rowView.findViewById(R.id.ivCategory);
            tvDraft = rowView.findViewById(R.id.tvDraft);

        }
    }
}
