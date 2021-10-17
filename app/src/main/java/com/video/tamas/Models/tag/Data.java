
package com.video.tamas.Models.tag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("user_like_status")
    @Expose
    private Integer userLikeStatus;
    @SerializedName("user_comment_status")
    @Expose
    private Integer userCommentStatus;
    @SerializedName("user_follow_status")
    @Expose
    private Integer userFollowStatus;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("video_id")
    @Expose
    private Integer videoId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cleantitle")
    @Expose
    private String cleantitle;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("user_verify")
    @Expose
    private String userVerify;
    @SerializedName("user_profile")
    @Expose
    private String userProfile;
    @SerializedName("categories_id")
    @Expose
    private Integer categoriesId;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("sub_category")
    @Expose
    private Integer subCategory;
    @SerializedName("sub_category_name")
    @Expose
    private Object subCategoryName;
    @SerializedName("video_file_name")
    @Expose
    private String videoFileName;
    @SerializedName("video_gif_file")
    @Expose
    private String videoGifFile;
    @SerializedName("draft")
    @Expose
    private Integer draft;
    @SerializedName("comment_status")
    @Expose
    private Integer commentStatus;
    @SerializedName("video_status")
    @Expose
    private Integer videoStatus;
    @SerializedName("video_created_date")
    @Expose
    private String videoCreatedDate;
    @SerializedName("total_likes")
    @Expose
    private Integer totalLikes;
    @SerializedName("total_comments")
    @Expose
    private Integer totalComments;
    @SerializedName("song_id")
    @Expose
    private Integer songId;
    @SerializedName("song_img")
    @Expose
    private String songImg;
    @SerializedName("song_name")
    @Expose
    private Boolean songName;
    @SerializedName("song_title")
    @Expose
    private Object songTitle;
    @SerializedName("song_duration")
    @Expose
    private Object songDuration;
    @SerializedName("song_count")
    @Expose
    private Object songCount;
    @SerializedName("song_date")
    @Expose
    private Object songDate;
    @SerializedName("share_count")
    @Expose
    private String shareCount;
    @SerializedName("video_count")
    @Expose
    private String videoCount;
    @SerializedName("save_option")
    @Expose
    private Integer saveOption;

    public Integer getUserLikeStatus() {
        return userLikeStatus;
    }

    public void setUserLikeStatus(Integer userLikeStatus) {
        this.userLikeStatus = userLikeStatus;
    }

    public Integer getUserCommentStatus() {
        return userCommentStatus;
    }

    public void setUserCommentStatus(Integer userCommentStatus) {
        this.userCommentStatus = userCommentStatus;
    }

    public Integer getUserFollowStatus() {
        return userFollowStatus;
    }

    public void setUserFollowStatus(Integer userFollowStatus) {
        this.userFollowStatus = userFollowStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCleantitle() {
        return cleantitle;
    }

    public void setCleantitle(String cleantitle) {
        this.cleantitle = cleantitle;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserVerify() {
        return userVerify;
    }

    public void setUserVerify(String userVerify) {
        this.userVerify = userVerify;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public Integer getCategoriesId() {
        return categoriesId;
    }

    public void setCategoriesId(Integer categoriesId) {
        this.categoriesId = categoriesId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(Integer subCategory) {
        this.subCategory = subCategory;
    }

    public Object getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(Object subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public String getVideoGifFile() {
        return videoGifFile;
    }

    public void setVideoGifFile(String videoGifFile) {
        this.videoGifFile = videoGifFile;
    }

    public Integer getDraft() {
        return draft;
    }

    public void setDraft(Integer draft) {
        this.draft = draft;
    }

    public Integer getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(Integer commentStatus) {
        this.commentStatus = commentStatus;
    }

    public Integer getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(Integer videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getVideoCreatedDate() {
        return videoCreatedDate;
    }

    public void setVideoCreatedDate(String videoCreatedDate) {
        this.videoCreatedDate = videoCreatedDate;
    }

    public Integer getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Integer totalComments) {
        this.totalComments = totalComments;
    }

    public Integer getSongId() {
        return songId;
    }

    public void setSongId(Integer songId) {
        this.songId = songId;
    }

    public String getSongImg() {
        return songImg;
    }

    public void setSongImg(String songImg) {
        this.songImg = songImg;
    }

    public Boolean getSongName() {
        return songName;
    }

    public void setSongName(Boolean songName) {
        this.songName = songName;
    }

    public Object getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(Object songTitle) {
        this.songTitle = songTitle;
    }

    public Object getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(Object songDuration) {
        this.songDuration = songDuration;
    }

    public Object getSongCount() {
        return songCount;
    }

    public void setSongCount(Object songCount) {
        this.songCount = songCount;
    }

    public Object getSongDate() {
        return songDate;
    }

    public void setSongDate(Object songDate) {
        this.songDate = songDate;
    }

    public String getShareCount() {
        return shareCount;
    }

    public void setShareCount(String shareCount) {
        this.shareCount = shareCount;
    }

    public String getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(String videoCount) {
        this.videoCount = videoCount;
    }

    public Integer getSaveOption() {
        return saveOption;
    }

    public void setSaveOption(Integer saveOption) {
        this.saveOption = saveOption;
    }

}
