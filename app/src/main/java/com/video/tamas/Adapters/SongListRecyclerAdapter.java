package com.video.tamas.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.video.tamas.Models.SongListModel;
import com.video.tamas.R;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by user1 on 2/13/2017.
 */

public class SongListRecyclerAdapter extends RecyclerView.Adapter<SongListRecyclerAdapter.CatalogListViewHolder> {

    private Activity activity;
    private List<SongListModel> songListModelArrayList = new ArrayList<>();
    private static OnMediaPlayerListener onMediaPlayerListener;
    MediaPlayer mp;
    MaterialDialog materialDialog;
    private boolean play = true, pause = false;

    public SongListRecyclerAdapter(Activity activity, List<SongListModel> songListModelArrayList) {
        this.activity = activity;
        this.songListModelArrayList = songListModelArrayList;

    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_list, parent, false);

        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogListViewHolder holder, final int position) {
        SongListModel songListModel = songListModelArrayList.get(position);
        if (songListModel.isPlay()) {
            holder.btnListenSong.setImageResource(R.drawable.ic_stop_white_24dp);
        } else {
            holder.btnListenSong.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
        String songPath = songListModel.getSongPath();
        String songDuration = songListModel.getSongDuration();
        Glide.with(activity)
                .load(songListModel.getSongImage())
                .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                .into(holder.ivSongImage);
        holder.tvSongTitle.setText(songListModel.getSongTitle());
        holder.tvSongDuration.setText(songDuration);
        holder.btnListenSong.setOnClickListener(v -> {
            for (int i = 0; i < songListModelArrayList.size(); i++) {
                if (i == position) {
                    continue;
                } else {
                    songListModelArrayList.get(i).setPlay(false);
                }
            }
            notifyDataSetChanged();
            if (mp != null) {
                mp.stop();
                onMediaPlayerListener.onPlay(false, mp);

            }

            if (songListModelArrayList.get(position).isPlay()) {
                songListModelArrayList.get(position).setPlay(false);

            } else {
                mp = MediaPlayer.create(activity, Uri.parse(songPath));
                mp.start();
                songListModelArrayList.get(position).setPlay(true);
                onMediaPlayerListener.onPlay(true, mp);
            }
            notifyDataSetChanged();
        });
        holder.btnSelectSong.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("songListModel", songListModel);
//                resultIntent.putExtra("songTitle",songListModel.getSongTitle());
            //resultIntent.putExtra("songPath",songListModel.getSongPath());
            //resultIntent.putExtra("songId",songListModel.getSongId());
            activity.setResult(Activity.RESULT_OK, resultIntent);
            //mp.stop();
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
            }
            activity.finish();
        });

        // Glide.with(activity).load(songListModel.get()).into(holder.gifIv);
    }

    @Override
    public int getItemCount() {
        return songListModelArrayList.size();
    }


    public static class CatalogListViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSongImage;
        TextView tvSongTitle, tvSongDuration;
        Button btnSelectSong;
        ImageButton btnListenSong;

        public CatalogListViewHolder(View rowView) {
            super(rowView);
            ivSongImage = rowView.findViewById(R.id.ivSongImage);
            tvSongTitle = rowView.findViewById(R.id.tvSongTitle);
            tvSongDuration = rowView.findViewById(R.id.tvSongDuration);
            btnListenSong = rowView.findViewById(R.id.btnListenSong);
            btnSelectSong = rowView.findViewById(R.id.btnSelectSong);
        }
    }

    public interface OnMediaPlayerListener {
        void onPlay(boolean play, MediaPlayer player);

    }

    public static void setOnMediaPlayerListener(OnMediaPlayerListener playerListener) {
        onMediaPlayerListener = playerListener;
    }
}
