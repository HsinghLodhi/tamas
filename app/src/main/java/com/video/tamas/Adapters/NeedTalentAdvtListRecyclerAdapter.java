package com.video.tamas.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.video.tamas.Activities.LoginActivity;
import com.video.tamas.Activities.ViewUserProfileActivity;
import com.video.tamas.Activities.WriterListUploadActivity;
import com.video.tamas.Fragments.NeedTalentFragment;
import com.video.tamas.Fragments.UserPersonalVideoShowFragment;
import com.video.tamas.Models.NeedTalentAdvertisModel;
import com.video.tamas.Models.WriterListModel;
import com.video.tamas.R;
import com.video.tamas.Utils.Common;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class NeedTalentAdvtListRecyclerAdapter extends RecyclerView.Adapter<NeedTalentAdvtListRecyclerAdapter.CatalogListViewHolder> {

    private NeedTalentFragment activity;
    private List<NeedTalentAdvertisModel> writerListModelList = new ArrayList<>();
    MediaPlayer mp;
    DeviceResourceManager resourceManager;
    private String userId;

    public NeedTalentAdvtListRecyclerAdapter(NeedTalentFragment activity, List<NeedTalentAdvertisModel> writerListModelList) {
        this.activity = activity;
        this.writerListModelList = writerListModelList;

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_need_talent_advt_list, parent, false);
        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        NeedTalentAdvertisModel writerListModel = writerListModelList.get(position);
        String userProfileName = writerListModel.getProducerUserName();
        String userProfileId = writerListModel.getProducerUserId();
        String createdDate = writerListModel.getCreatedDate();
        String description = writerListModel.getAdvertisementDiscription();
        String appliedCount = writerListModel.getTotalAppliedCount();
        String appliedStatus=writerListModel.getAapliedStatus();
        String talentId=writerListModel.getTalentId();
        if (appliedStatus.equals(1)){
            holder.btnApply.setText("Applied");
            holder.btnApply.setClickable(false);

        }else {
            holder.btnApply.setText("Apply");

        }

        parseDateToddMMyyyy(createdDate);
        Log.wtf("datee", parseDateToddMMyyyy(createdDate));
        holder.tvDate.setText(parseDateToddMMyyyy(createdDate));
        String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(description);
        holder.tvdescription.setText(fromServerUnicodeDecoded);
        holder.tvUserName.setText(userProfileName);
        holder.tvTotalAppiledCount.setText(appliedCount);
        Glide.with(activity)
                .load(writerListModel.getProducerImage())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                .into(holder.ivUserImage);
        holder.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.applyTalent(userProfileId,talentId);

            }
        });



    }

    @Override
    public int getItemCount() {
        return writerListModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {

        TextView tvCommentMessage, tvUserName, tvDate, tvdescription, tvTotalAppiledCount;
        ImageView ivUserImage;
        Button btnApply;


        public CatalogListViewHolder(View rowView) {
            super(rowView);
            tvDate = rowView.findViewById(R.id.tvCommentDate);
            tvCommentMessage = rowView.findViewById(R.id.tvCommentMessage);
            tvUserName = rowView.findViewById(R.id.tvUserName);
            ivUserImage = rowView.findViewById(R.id.ivUserImage);
            tvdescription = rowView.findViewById(R.id.tvDescription);
            tvTotalAppiledCount = rowView.findViewById(R.id.tvLikeCount);
            btnApply=rowView.findViewById(R.id.btnApply);

        }
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
