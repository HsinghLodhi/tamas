package com.video.tamas.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.video.tamas.Models.TalentCategoryModel;
import com.video.tamas.R;

import java.util.List;


public class DiscreteScrollViewAdapter extends RecyclerView.Adapter<DiscreteScrollViewAdapter.MyViewHolder> {

    Activity searchFragment;
    List<TalentCategoryModel> imageList;

    public DiscreteScrollViewAdapter(Activity searchFragment, List<TalentCategoryModel> imageList) {
        this.searchFragment = searchFragment;
        this.imageList = imageList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.descrete_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TalentCategoryModel descreteModel = imageList.get(position);
        String image = descreteModel.getCatImage();
        Glide.with(searchFragment)
                .load(image)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return imageList.size();//TODO count is neccessary
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.banner_image);


        }
    }
}
