package com.video.tamas.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.video.tamas.Activities.CategorywiseVideoListActivity;
import com.video.tamas.Activities.WriterListUploadActivity;
import com.video.tamas.Models.TalentCategoryModel;
import com.video.tamas.R;
import com.video.tamas.Utils.DeviceResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 2/13/2017.
 */

public class HomeTalentCatRecyclerAdapter extends RecyclerView.Adapter<HomeTalentCatRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<TalentCategoryModel> talentCategoryModelList = new ArrayList<>();
    private DeviceResourceManager resourceManager;

    public HomeTalentCatRecyclerAdapter(Activity activity, List<TalentCategoryModel> talentCategoryModelList) {
        this.activity = activity;
        this.talentCategoryModelList = talentCategoryModelList;
        resourceManager = new DeviceResourceManager(activity);

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_talent_category, parent, false);

        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        TalentCategoryModel talentCategoryModel = talentCategoryModelList.get(position);
        String catId = talentCategoryModel.getCatId();
        String catName = talentCategoryModel.getCatName();

        Glide.with(activity)
                .load(talentCategoryModel.getCatImage())
                .into(holder.ivCategory);
        holder.tvCatName.setText(talentCategoryModel.getCatName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (catId.equals("4")) {
                    Intent intent1 = new Intent(activity, WriterListUploadActivity.class);
                    intent1.putExtra("subcategoryId", catId);
                    activity.startActivity(intent1);
                } else {
                    Intent intent = new Intent(activity, CategorywiseVideoListActivity.class);
                    intent.putExtra("subcategoryId", catId);
                    intent.putExtra("subcategoryName", catName);
                    activity.startActivity(intent);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return talentCategoryModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategory;
        TextView tvCatName;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
            tvCatName = rowView.findViewById(R.id.tvCatName);
            ivCategory = rowView.findViewById(R.id.ivCategory);
        }
    }
}
