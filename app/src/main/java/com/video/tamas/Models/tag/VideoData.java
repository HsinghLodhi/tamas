
package com.video.tamas.Models.tag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoData {

    @SerializedName("user_like_status")
    @Expose
    private Integer userLikeStatus;
    @SerializedName("user_comment_status")
    @Expose
    private Integer userCommentStatus;
    @SerializedName("follow_status")
    @Expose
    private Integer followStatus;
    @SerializedName("user_comment")
    @Expose
    private Object userComment;
    @SerializedName("user_comment_date")
    @Expose
    private Object userCommentDate;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("user_verify")
    @Expose
    private String userVerify;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("categories_id")
    @Expose
    private Integer categoriesId;
    @SerializedName("sub_category")
    @Expose
    private Integer subCategory;
    @SerializedName("actual_file")
    @Expose
    private String actualFile;
    @SerializedName("file_name")
    @Expose
    private String fileName;
    @SerializedName("image_name")
    @Expose
    private String imageName;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("views_count")
    @Expose
    private String viewsCount;
    @SerializedName("video_comment_status")
    @Expose
    private String videoCommentStatus;
    @SerializedName("total_share_count")
    @Expose
    private String totalShareCount;
    @SerializedName("total_likes")
    @Expose
    private Integer totalLikes;
    @SerializedName("total_comments")
    @Expose
    private Integer totalComments;
    @SerializedName("song_id")
    @Expose
    private Integer songId;
    @SerializedName("song_image")
    @Expose
    private String songImage;
    @SerializedName("song_name")
    @Expose
    private Boolean songName;
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

    public Integer getFollowStatus() {
        return followStatus;
    }

    public void setFollowStatus(Integer followStatus) {
        this.followStatus = followStatus;
    }

    public Object getUserComment() {
        return userComment;
    }

    public void setUserComment(Object userComment) {
        this.userComment = userComment;
    }

    public Object getUserCommentDate() {
        return userCommentDate;
    }

    public void setUserCommentDate(Object userCommentDate) {
        this.userCommentDate = userCommentDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Integer getCategoriesId() {
        return categoriesId;
    }

    public void setCategoriesId(Integer categoriesId) {
        this.categoriesId = categoriesId;
    }

    public Integer getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(Integer subCategory) {
        this.subCategory = subCategory;
    }

    public String getActualFile() {
        return actualFile;
    }

    public void setActualFile(String actualFile) {
        this.actualFile = actualFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(String viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getVideoCommentStatus() {
        return videoCommentStatus;
    }

    public void setVideoCommentStatus(String videoCommentStatus) {
        this.videoCommentStatus = videoCommentStatus;
    }

    public String getTotalShareCount() {
        return totalShareCount;
    }

    public void setTotalShareCount(String totalShareCount) {
        this.totalShareCount = totalShareCount;
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

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public Boolean getSongName() {
        return songName;
    }

    public void setSongName(Boolean songName) {
        this.songName = songName;
    }

    public Integer getSaveOption() {
        return saveOption;
    }

    public void setSaveOption(Integer saveOption) {
        this.saveOption = saveOption;
    }

}
