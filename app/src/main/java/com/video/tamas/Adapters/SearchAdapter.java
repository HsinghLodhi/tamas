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
import com.video.tamas.Fragments.PopularFragment;
import com.video.tamas.Fragments.SearchSelectedVideoShowFragment;
import com.video.tamas.Models.SearchModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Common;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by user1 on 2/13/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<SearchModel> searchModelArrayList = new ArrayList<>();
    private String searchText;

    public SearchAdapter(Activity activity, List<SearchModel> searchModelArrayList,String searchText) {
        this.activity = activity;
        this.searchModelArrayList = searchModelArrayList;
        this.searchText=searchText;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(activity).inflate(R.layout.item_search, parent, false);
        CatalogListViewHolder catalogListViewHolder = new CatalogListViewHolder(rowView);
        return catalogListViewHolder;
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        SearchModel searchModel = searchModelArrayList.get(position);
        Glide.with(activity).load(searchModel.getVideoGif()).into(holder.gifIv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Log.w("adapterPos", String.valueOf(pos));
                PopularFragment dashBoardTradesFragment = new PopularFragment(pos,"","search");
                /*SearchSelectedVideoShowFragment dashBoardTradesFragment = new SearchSelectedVideoShowFragment();
                Bundle bundle = new Bundle();
                bundle.putString("adapterPosition", String.valueOf(pos));
                bundle.putString("searchText", searchText);
                dashBoardTradesFragment.setArguments(bundle);
                */Common.changeFragment((FragmentActivity) activity, dashBoardTradesFragment);
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchModelArrayList.size();
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
