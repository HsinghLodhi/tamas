package com.video.tamas.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.video.tamas.Fragments.UserPersonalVideoShowFragment;
import com.video.tamas.Models.ProfileModelClass;
import com.video.tamas.R;
import com.video.tamas.Utils.Common;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by user1 on 2/13/2017.
 */

public class ProfileVideoAdapter extends RecyclerView.Adapter<ProfileVideoAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<ProfileModelClass> profileModelClassList = new ArrayList<>();

    public ProfileVideoAdapter(Activity activity, List<ProfileModelClass> profileModelClassList) {
        this.activity = activity;
        this.profileModelClassList = profileModelClassList;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(activity).inflate(R.layout.item_profile, parent, false);
        CatalogListViewHolder catalogListViewHolder = new CatalogListViewHolder(rowView);
        return catalogListViewHolder;
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        ProfileModelClass catalogListModel = profileModelClassList.get(position);
        String isDraftVideo = catalogListModel.getIsDraftVideo();
        String userId=catalogListModel.getProfileId();
        if (isDraftVideo.equals("1")) {
            holder.tvDraft.setVisibility(View.VISIBLE);
            holder.tvDraft.setText("Drafts");

        }

        Glide.with(activity).load(catalogListModel.getVideoGif()).into(holder.gifIv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Log.w("adapterPos", String.valueOf(pos));
                UserPersonalVideoShowFragment dashBoardTradesFragment = new UserPersonalVideoShowFragment();
                Bundle bundle = new Bundle();
                bundle.putString("adapterPosition", String.valueOf(pos));
                bundle.putString("userId",userId);
                dashBoardTradesFragment.setArguments(bundle);
                Common.changeFragment((FragmentActivity) activity, dashBoardTradesFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileModelClassList.size();
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
