package com.video.tamas.Adapters;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.video.tamas.Models.CommentModel;
import com.video.tamas.R;

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

public class CommentListRecyclerAdapter extends RecyclerView.Adapter<CommentListRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<CommentModel> commentModelArrayList = new ArrayList<>();
    MediaPlayer mp;

    public CommentListRecyclerAdapter(Activity activity, List<CommentModel> commentModelArrayList) {
        this.activity = activity;
        this.commentModelArrayList = commentModelArrayList;

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment_list, parent, false);

        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        CommentModel commentModel = commentModelArrayList.get(position);
        String commentMessage = commentModel.getCommentMessage();
        String commentDate = commentModel.getCommentDate();
//        StringTokenizer tokens = new StringTokenizer(commentDate, " ");
//        String first = tokens.nextToken();// this will contain "Fruit"
//        String second = tokens.nextToken();// this will contain " they taste good"
//        Log.wtf("first",first);
//        Log.wtf("second",second);
//        Log.wtf("date",formatDate(first, "EEEE, dd MMMM yyyy",first));
        parseDateToddMMyyyy(commentDate);
        Log.wtf("datee", parseDateToddMMyyyy(commentDate));
        holder.tvCommentDate.setText(parseDateToddMMyyyy(commentDate));
        String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(commentMessage);
        holder.tvCommentMessage.setText(fromServerUnicodeDecoded);
        holder.tvUserName.setText(commentModel.getUserName());
        Glide.with(activity)
                .load(commentModel.getUserImage())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                .into(holder.ivUserImage);
    }

    @Override
    public int getItemCount() {
        return commentModelArrayList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {

        TextView tvCommentMessage, tvUserName, tvCommentDate;
        ImageView ivUserImage;


        public CatalogListViewHolder(View rowView) {
            super(rowView);
            tvCommentDate = rowView.findViewById(R.id.tvCommentDate);
            tvCommentMessage = rowView.findViewById(R.id.tvCommentMessage);
            tvUserName = rowView.findViewById(R.id.tvUserName);
            ivUserImage = rowView.findViewById(R.id.ivUserImage);

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
