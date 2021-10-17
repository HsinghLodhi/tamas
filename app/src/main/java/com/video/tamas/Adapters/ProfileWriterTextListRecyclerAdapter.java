package com.video.tamas.Adapters;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.like.LikeButton;
import com.video.tamas.Activities.ViewUserProfileActivity;
import com.video.tamas.Fragments.ProfileFragment;
import com.video.tamas.Models.ProfileWriterTextModel;
import com.video.tamas.R;
import com.video.tamas.Utils.DeviceResourceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user1 on 2/13/2017.
 */

public class ProfileWriterTextListRecyclerAdapter extends RecyclerView.Adapter<ProfileWriterTextListRecyclerAdapter.CatalogListViewHolder> {

    private ViewUserProfileActivity activity;
    private ProfileFragment fragment;
    private List<ProfileWriterTextModel> writerListModelList = new ArrayList<>();
    MediaPlayer mp;
    DeviceResourceManager resourceManager;
    private String userId;

    public ProfileWriterTextListRecyclerAdapter(ViewUserProfileActivity activity, ProfileFragment profileFragment,List<ProfileWriterTextModel> writerListModelList) {
        this.activity = activity;
        this.fragment=profileFragment;
        this.writerListModelList = writerListModelList;

    }



    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_writer_profile_list, parent, false);
        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        ProfileWriterTextModel writerListModel = writerListModelList.get(position);
        String id = writerListModel.getId();
        String userProfileId = writerListModel.getUserId();
        String title = writerListModel.getTitle();
        String description = writerListModel.getDescription();
        String likeCount = writerListModel.getLikeCount();
        holder.ivLike.setVisibility(View.GONE);
        holder.tvLikeCount.setText(likeCount + " likes");
        holder.tvCommentMessage.setText(description);
        holder.tvdescription.setText(title);


    }

    @Override
    public int getItemCount() {
        return writerListModelList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {

        TextView tvCommentMessage, tvdescription, tvLikeCount;
        LikeButton ivLike;

        public CatalogListViewHolder(View rowView) {
            super(rowView);

            tvCommentMessage = rowView.findViewById(R.id.tvCommentMessage);
            tvdescription = rowView.findViewById(R.id.tvDescription);
            tvLikeCount = rowView.findViewById(R.id.tvLikeCount);
            ivLike = rowView.findViewById(R.id.ivLike);
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
