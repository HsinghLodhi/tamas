package com.video.tamas.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SongListModel implements Parcelable {
    String songId;
    String songTitle;
    String songPath;
    String songDuration;
    String songImage;
    private boolean play;


    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public static Creator<SongListModel> getCREATOR() {
        return CREATOR;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public SongListModel(String songId, String songTitle, String songPath, String songDuration, String songImage, boolean play) {
        this.songId = songId;
        this.songTitle = songTitle;
        this.songPath = songPath;
        this.songDuration = songDuration;
        this.songImage = songImage;
        this.play = play;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songId);
        dest.writeString(this.songTitle);
        dest.writeString(this.songPath);
        dest.writeString(this.songDuration);
    }

    public SongListModel() {
    }

    protected SongListModel(Parcel in) {
        this.songId = in.readString();
        this.songTitle = in.readString();
        this.songPath = in.readString();
        this.songDuration = in.readString();
    }

    public static final Parcelable.Creator<SongListModel> CREATOR = new Parcelable.Creator<SongListModel>() {
        @Override
        public SongListModel createFromParcel(Parcel source) {
            return new SongListModel(source);
        }

        @Override
        public SongListModel[] newArray(int size) {
            return new SongListModel[size];
        }
    };
}
