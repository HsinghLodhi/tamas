package com.video.tamas.Models;

import java.io.Serializable;

public class HomePopularModel implements Serializable {
    private String userProfileId;
    private String videoId;
    private String videoDescription;
    private String videoUrl;
    private String videoGif;
    private String videoProfileImage;
    private String videoSongName;
    private String videoSongId;
    private String videoLikesCount;
    private String userProfileName;
    private String totalLikeCount;
    private String totalCommentCount;
    private String isLike;
    private String isFollow;
    private String isFollowing;
    private String songImage;
    private String commentStatus;
    private String totalShareCount;
    private int show_video;
    private String categoryId;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTotalShareCount() {
        return totalShareCount;
    }

    public void setTotalShareCount(String totalShareCount) {
        this.totalShareCount = totalShareCount;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(String isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }

    public String getTotalLikeCount() {
        return totalLikeCount;
    }

    public void setTotalLikeCount(String totalLikeCount) {
        this.totalLikeCount = totalLikeCount;
    }

    public String getTotalCommentCount() {
        return totalCommentCount;
    }

    public void setTotalCommentCount(String totalCommentCount) {
        this.totalCommentCount = totalCommentCount;
    }

    public String getUserProfileName() {
        return userProfileName;
    }

    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getVideoProfileImage() {
        return videoProfileImage;
    }

    public void setVideoProfileImage(String videoProfileImage) {
        this.videoProfileImage = videoProfileImage;
    }

    public String getVideoSongName() {
        return videoSongName;
    }

    public void setVideoSongName(String videoSongName) {
        this.videoSongName = videoSongName;
    }

    public String getVideoSongId() {
        return videoSongId;
    }

    public void setVideoSongId(String videoSongId) {
        this.videoSongId = videoSongId;
    }

    public String getVideoLikesCount() {
        return videoLikesCount;
    }

    public void setVideoLikesCount(String videoLikesCount) {
        this.videoLikesCount = videoLikesCount;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoGif() {
        return videoGif;
    }

    public void setVideoGif(String videoGif) {
        this.videoGif = videoGif;
    }

    public int getShow_video() {
        return show_video;
    }

    public void setShow_video(int show_video) {
        this.show_video = show_video;
    }
}
