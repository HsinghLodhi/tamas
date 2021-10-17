package com.video.tamas.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.video.tamas.Activities.EntertainmentVideoMakeActivity;
import com.video.tamas.Models.search.Songs;
import com.video.tamas.R;
import com.video.tamas.Utils.DeviceResourceManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class SongTabAdapter extends RecyclerView.Adapter<SongTabAdapter.SongTabViewHolder> {
    private Context context;
    private List<Songs> songsList;
    public static MediaPlayer mp;
    private DeviceResourceManager resourceManager;
    private static OnMusicPlayerListener onMusicPlayerListener;

    public SongTabAdapter(Context context, List<Songs> songsList) {
        this.context = context;
        this.songsList = songsList;
        resourceManager = new DeviceResourceManager(context);
    }

    @NonNull
    @Override
    public SongTabViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.songs_tab_list_layout, viewGroup, false);
        return new SongTabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongTabViewHolder holder, int position) {
        Songs songs = songsList.get(position);
        if (songs.isPlay()) {
            holder.ibPlaySong.setImageResource(R.drawable.ic_stop_white_24dp);
        } else {
            holder.ibPlaySong.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
        String songPath = songs.getSongName();

        Glide.with(context)
                .load(songs.getSongImage())
                .apply(placeholderOf(R.drawable.ic_musical_note).transform(new CircleCrop()).error(R.drawable.ic_musical_note))
                .into(holder.imageView);
        holder.tvSongTitle.setText(songs.getSongTitle());
        holder.ibPlaySong.setOnClickListener(v -> {
            for (int i = 0; i < songsList.size(); i++) {
                if (i == position) {
                    continue;
                } else {
                    songsList.get(i).setPlay(false);
                }
            }
            notifyDataSetChanged();
            if (mp != null) {
                mp.stop();
            }

            if (songsList.get(position).isPlay()) {
                songsList.get(position).setPlay(false);

            } else {
                mp = MediaPlayer.create(context, Uri.parse(songPath));
                mp.start();
                songsList.get(position).setPlay(true);
                if (onMusicPlayerListener != null) {
                    onMusicPlayerListener.onMusicListener(true, mp);
                }
            }
            notifyDataSetChanged();
        });
        holder.btnUseSong.setOnClickListener(v -> {
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.stop();
                    for (int i = 0; i < songsList.size(); i++) {
                        songsList.get(i).setPlay(false);
                    }
                    notifyDataSetChanged();
                }
            }
            Intent intent1 = new Intent(context, EntertainmentVideoMakeActivity.class);
            intent1.putExtra("songId", songs.getId());
            intent1.putExtra("songPath", songPath);
            intent1.putExtra("songDuration", songs.getDuration());
            intent1.putExtra("songName", songs.getSongTitle());
            context.startActivity(intent1);
            resourceManager.addToSharedPref("USE_SONG", true);
        });

    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class SongTabViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView tvSongTitle;
        private ImageButton ibPlaySong;
        private Button btnUseSong;

        public SongTabViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSongsTab);
            tvSongTitle = itemView.findViewById(R.id.tvSongsTabTitle);
            ibPlaySong = itemView.findViewById(R.id.ibPlaySongsTab);
            btnUseSong = itemView.findViewById(R.id.btnUseSongsTab);
        }
    }

    public interface OnMusicPlayerListener {
        void onMusicListener(boolean play, MediaPlayer mediaPlayer);
    }

    public static void setOnMusicPlayerListener(OnMusicPlayerListener listener) {
        onMusicPlayerListener = listener;
    }
}
