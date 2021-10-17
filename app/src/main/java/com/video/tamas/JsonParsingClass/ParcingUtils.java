package com.video.tamas.JsonParsingClass;

import android.app.Activity;

import com.video.tamas.Models.CommentModel;
import com.video.tamas.Models.DescreteModel;
import com.video.tamas.Models.EntertainmentSelectedVideoModel;
import com.video.tamas.Models.FollowerModel;
import com.video.tamas.Models.FollowingModel;
import com.video.tamas.Models.HomeEntertainmentModel;
import com.video.tamas.Models.HomePopularModel;
import com.video.tamas.Models.LikeModel;
import com.video.tamas.Models.NeedTalentAdvertisModel;
import com.video.tamas.Models.NotificationModel;
import com.video.tamas.Models.ProfileModelClass;
import com.video.tamas.Models.SearchModel;
import com.video.tamas.Models.SongListModel;
import com.video.tamas.Models.TalentCategoryModel;
import com.video.tamas.Models.WriterListModel;
import com.video.tamas.Models.search.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParcingUtils {

//    public static List<Data1> parseTagModelList(JSONArray jsonArray) throws Exception {
//        List<Data1> data1List = new ArrayList<>();
//        Gson gson=new Gson();
//        HashTagResponse hashTagResponse=gson
//
//
//    }

    public static List<HomeEntertainmentModel> parseHomeEntertainmentModelList(JSONArray jsonArray) throws Exception {
        List<HomeEntertainmentModel> homeEntertainmentModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String videoId = jsonObject.optString("id");
            String videoDesp = jsonObject.optString("description");
            String videoUrl = jsonObject.optString("file_name");
            String videoGif = jsonObject.optString("image_name");
            String videoSongName = jsonObject.optString("song_name");
            String videoSongId = jsonObject.optString("song_id");
            String videoLikesCount = jsonObject.optString("views_count");
            String videoProfileImage = jsonObject.optString("profile_image");
            String userProfileName = jsonObject.optString("username");
            String totalLikeCount = jsonObject.optString("total_likes");
            String totalCommentCount = jsonObject.optString("total_comments");
            String isLike = jsonObject.optString("user_like_status");
            String isFollow = jsonObject.optString("follow_status");
            String commentStatus = jsonObject.optString("video_comment_status");
            HomeEntertainmentModel homeEntertainmentModel = new HomeEntertainmentModel();
            homeEntertainmentModel.setVideoId(videoId);
            homeEntertainmentModel.setVideoDescription(videoDesp);
            homeEntertainmentModel.setVideoGif(videoGif);
            homeEntertainmentModel.setVideoUrl(videoUrl);
            homeEntertainmentModel.setVideoLikesCount(videoLikesCount);
            homeEntertainmentModel.setVideoProfileImage(videoProfileImage);
            homeEntertainmentModel.setVideoSongId(videoSongId);
            homeEntertainmentModel.setVideoSongName(videoSongName);
            homeEntertainmentModel.setUserProfileName(userProfileName);
            homeEntertainmentModel.setTotalLikeCount(totalLikeCount);
            homeEntertainmentModel.setTotalCommentCount(totalCommentCount);
            homeEntertainmentModel.setTotalLikeCount(totalLikeCount);
            homeEntertainmentModel.setTotalCommentCount(totalCommentCount);
            homeEntertainmentModel.setIsLike(isLike);
            homeEntertainmentModel.setIsFollow(isFollow);
            homeEntertainmentModel.setCommentStatus(commentStatus);
            homeEntertainmentModelList.add(homeEntertainmentModel);
        }
        return homeEntertainmentModelList;
    }

    public static List<HomePopularModel> parseHomePopularModelList(JSONArray jsonArray) throws Exception {
        List<HomePopularModel> homePopularModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String videoId = jsonObject.optString("id");
            String videoDesp = jsonObject.optString("description");
            String videoUrl = jsonObject.optString("file_name");
            String videoGif = jsonObject.optString("image_name");
            String videoSongName = jsonObject.optString("song_name");
            String videoSongId = jsonObject.optString("song_id");
            String videoViewCount = jsonObject.optString("views_count");
            String videoProfileImage = jsonObject.optString("profile_image");
            String userProfileId = jsonObject.optString("user_id");
            String userProfileName = jsonObject.optString("username");
            String totalLikeCount = jsonObject.optString("total_likes");
            String totalCommentCount = jsonObject.optString("total_comments");
            String isLike = jsonObject.optString("user_like_status");
            String isFollow = jsonObject.optString("follow_status");
            String songImage = jsonObject.optString("song_image");
            String commentStatus = jsonObject.optString("video_comment_status");
            String totalShareCount = jsonObject.optString("total_share_count");
            String categoryId = jsonObject.optString("categories_id");
            HomePopularModel homePopularModel = new HomePopularModel();
            homePopularModel.setCategoryId(categoryId);
            homePopularModel.setVideoId(videoId);
            homePopularModel.setVideoDescription(videoDesp);
            homePopularModel.setVideoGif(videoGif);
            homePopularModel.setVideoUrl(videoUrl);
            homePopularModel.setVideoLikesCount(videoViewCount);
            homePopularModel.setVideoProfileImage(videoProfileImage);
            homePopularModel.setVideoSongId(videoSongId);
            homePopularModel.setVideoSongName(videoSongName);
            homePopularModel.setUserProfileId(userProfileId);
            homePopularModel.setUserProfileName(userProfileName);
            homePopularModel.setTotalLikeCount(totalLikeCount);
            homePopularModel.setTotalCommentCount(totalCommentCount);
            homePopularModel.setIsLike(isLike);
            homePopularModel.setIsFollow(isFollow);
            homePopularModel.setSongImage(songImage);
            homePopularModel.setCommentStatus(commentStatus);
            homePopularModel.setTotalShareCount(totalShareCount);
            homePopularModelList.add(homePopularModel);
        }
        return homePopularModelList;
    }

    public static List<EntertainmentSelectedVideoModel> parseHomeSelectedVideoModelList(JSONArray jsonArray) throws Exception {
        List<EntertainmentSelectedVideoModel> entertainmentSelectedVideoModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String videoId = jsonObject.optString("id");
            String videoDesp = jsonObject.optString("description");
            String videoUrl = jsonObject.optString("file_name");
            String videoGif = jsonObject.optString("image_name");
            String videoSongName = jsonObject.optString("song_name");
            String videoSongId = jsonObject.optString("song_id");
            String videoLikesCount = jsonObject.optString("views_count");
            String videoProfileImage = jsonObject.optString("profile_image");
            String userProfileId = jsonObject.optString("user_id");
            String userProfileName = jsonObject.optString("username");
            String totalLikeCount = jsonObject.optString("total_likes");
            String totalCommentCount = jsonObject.optString("total_comments");
            String isLike = jsonObject.optString("user_like_status");
            String isFollow = jsonObject.optString("follow_status");
            String songImage = jsonObject.optString("song_image");
            String commentStatus = jsonObject.optString("video_comment_status");
            String totalShareCount = jsonObject.optString("total_share_count");
            EntertainmentSelectedVideoModel entertainmentSelectedVideoModel = new EntertainmentSelectedVideoModel();
            entertainmentSelectedVideoModel.setVideoId(videoId);
            entertainmentSelectedVideoModel.setVideoDescription(videoDesp);
            entertainmentSelectedVideoModel.setVideoGif(videoGif);
            entertainmentSelectedVideoModel.setVideoUrl(videoUrl);
            entertainmentSelectedVideoModel.setVideoLikesCount(videoLikesCount);
            entertainmentSelectedVideoModel.setVideoProfileImage(videoProfileImage);
            entertainmentSelectedVideoModel.setVideoSongId(videoSongId);
            entertainmentSelectedVideoModel.setVideoSongName(videoSongName);
            entertainmentSelectedVideoModel.setUserProfileId(userProfileId);
            entertainmentSelectedVideoModel.setUserProfileName(userProfileName);
            entertainmentSelectedVideoModel.setTotalLikeCount(totalLikeCount);
            entertainmentSelectedVideoModel.setTotalCommentCount(totalCommentCount);
            entertainmentSelectedVideoModel.setIsLike(isLike);
            entertainmentSelectedVideoModel.setIsFollow(isFollow);
            entertainmentSelectedVideoModel.setSongImage(songImage);
            entertainmentSelectedVideoModel.setCommentStatus(commentStatus);
            entertainmentSelectedVideoModel.setTotalShareCount(totalShareCount);
            entertainmentSelectedVideoModelList.add(entertainmentSelectedVideoModel);
        }
        return entertainmentSelectedVideoModelList;
    }

    public static List<ProfileModelClass> parseProfileVideoModelList(JSONArray jsonArray) throws Exception {
        List<ProfileModelClass> profileModelClassList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String videoId = jsonObject.optString("id");
            String videoDesp = jsonObject.optString("description");
            String videoUrl = jsonObject.optString("file_name");
            String videoGif = jsonObject.optString("image_name");
            String videoSongName = jsonObject.optString("song_name");
            String videoSongId = jsonObject.optString("song_id");
            String videoLikesCount = jsonObject.optString("views_count");
            String videoProfileImage = jsonObject.optString("profile_image");
            String userProfileId = jsonObject.optString("user_id");
            String isDraftVideo = jsonObject.optString("draft");
            ProfileModelClass profileModelClass = new ProfileModelClass();
            profileModelClass.setVideoId(videoId);
            profileModelClass.setVideoDescription(videoDesp);
            profileModelClass.setVideoGif(videoGif);
            profileModelClass.setVideoUrl(videoUrl);
            profileModelClass.setVideoLikesCount(videoLikesCount);
            profileModelClass.setVideoProfileImage(videoProfileImage);
            profileModelClass.setVideoSongId(videoSongId);
            profileModelClass.setVideoSongName(videoSongName);
            profileModelClass.setIsDraftVideo(isDraftVideo);

            profileModelClassList.add(profileModelClass);
        }
        return profileModelClassList;
    }

    public static List<TalentCategoryModel> parseTalentCatModelList(JSONArray jsonArray) throws Exception {
        List<TalentCategoryModel> talentCategoryModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String catId = jsonObject.optString("id");
            String catName = jsonObject.optString("sub_category_name");
            String catImage = jsonObject.optString("category_image");
            TalentCategoryModel talentCategoryModel = new TalentCategoryModel();
            talentCategoryModel.setCatId(catId);
            talentCategoryModel.setCatName(catName);
            talentCategoryModel.setCatImage(catImage);

            talentCategoryModelList.add(talentCategoryModel);
        }
        return talentCategoryModelList;
    }

    public static List<SongListModel> parseSongListModelList(Activity activity, JSONArray jsonArray) throws Exception {
        List<SongListModel> songListModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String songId = jsonObject.optString("id");
            String songDuration = jsonObject.optString("duration");
            String songPath = jsonObject.optString("song_name");
            String songTitle = jsonObject.optString("song_title");
            String songImage = jsonObject.optString("song_image");
            SongListModel songListModel = new SongListModel();
            songListModel.setSongId(songId);
            songListModel.setSongDuration(songDuration);
            songListModel.setSongPath(songPath);
            songListModel.setSongTitle(songTitle);
            songListModel.setSongImage(songImage);

            songListModelList.add(songListModel);
        }
        return songListModelList;
    }

    public static List<CommentModel> parseCommentModelList(JSONArray jsonArray) throws Exception {
        List<CommentModel> commentModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String userName = jsonObject.optString("username");
            String userImage = jsonObject.optString("user_profile");
            String commentMessage = jsonObject.optString("comment");
            String commentDate = jsonObject.optString("comment_date");
            CommentModel commentModel = new CommentModel();
            commentModel.setUserName(userName);
            commentModel.setUserImage(userImage);
            commentModel.setCommentMessage(commentMessage);
            commentModel.setCommentDate(commentDate);
            commentModelList.add(commentModel);
        }
        return commentModelList;
    }

    public static List<FollowerModel> parseFollowerModelList(JSONArray jsonArray) throws Exception {
        List<FollowerModel> followerModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String followerId = jsonObject.optString("id");
            String followerName = jsonObject.optString("username");
            String followerPic = jsonObject.optString("profile_image");
            String me_following = jsonObject.optString("me_following");

            FollowerModel followerModel = new FollowerModel();
            followerModel.setFollowerId(followerId);
            followerModel.setFollowerName(followerName);
            followerModel.setFollowerPic(followerPic);
            followerModel.setMe_following(me_following);
            followerModelList.add(followerModel);
        }
        return followerModelList;
    }

    public static List<FollowingModel> parseFollowingModelList(JSONArray jsonArray) throws Exception {
        List<FollowingModel> followingModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String followerId = jsonObject.optString("id");
            String followerName = jsonObject.optString("username");
            String followerPic = jsonObject.optString("profile_image");
            FollowingModel followerModel = new FollowingModel();
            followerModel.setfollowingId(followerId);
            followerModel.setfollowingName(followerName);
            followerModel.setfollowingPic(followerPic);
            followingModelList.add(followerModel);
        }
        return followingModelList;
    }

    public static List<WriterListModel> parseWriterListModelList(JSONArray jsonArray) throws Exception {
        List<WriterListModel> writerListModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String userId = jsonObject.optString("user_id");
            String userName = jsonObject.optString("username");
            String profileImage = jsonObject.optString("user_profile");
            String text = jsonObject.optString("file_name");
            String description = jsonObject.optString("description");
            String createdDate = jsonObject.optString("text_created_date");
            String textId = jsonObject.optString("video_id");
            String isLike = jsonObject.optString("user_like_status");
            String likeCount = jsonObject.optString("total_likes");
            String isFollow = jsonObject.optString("follow_status");

            WriterListModel writerListModel = new WriterListModel();
            writerListModel.setUserName(userName);
            writerListModel.setUserId(userId);
            writerListModel.setDescription(description);
            writerListModel.setCreatedDate(createdDate);
            writerListModel.setText(text);
            writerListModel.setProfilePic(profileImage);
            writerListModel.setIsFollow(isFollow);
            writerListModel.setLikeCount(likeCount);
            writerListModel.setTextId(textId);
            writerListModel.setIsLike(isLike);
            writerListModelList.add(writerListModel);
        }
        return writerListModelList;
    }

    public static List<DescreteModel> parseDescreteImageModelList(JSONArray jsonArray) throws Exception {
        List<DescreteModel> descreteModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String image = jsonObject.optString("category_image");
            DescreteModel descreteModel = new DescreteModel();
            descreteModel.setImage(image);
            descreteModelList.add(descreteModel);
        }
        return descreteModelList;
    }

    public static List<SearchModel> parseSearchModelList(JSONArray jsonArray) throws Exception {
        List<SearchModel> homeEntertainmentModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String videoId = jsonObject.optString("video_id");
            String videoDesp = jsonObject.optString("description");
            String videoUrl = jsonObject.optString("video_file_name");
            String videoGif = jsonObject.optString("video_gif_file");
            String videoSongName = jsonObject.optString("song_name");
            String videoSongId = jsonObject.optString("song_id");
            String videoLikesCount = jsonObject.optString("video_count");
            String videoProfileImage = jsonObject.optString("user_profile");
            String userProfileName = jsonObject.optString("username");
            String userProfileId = jsonObject.optString("user_id");
            String totalLikeCount = jsonObject.optString("total_likes");
            String totalCommentCount = jsonObject.optString("total_comments");
            String isLike = jsonObject.optString("user_like_status");
            String isFollow = jsonObject.optString("user_follow_status");
            String commentStatus = jsonObject.optString("comment_status");
            String songImage = jsonObject.optString("song_img");
            String categoryId = jsonObject.optString("categories_id");
            String totalShareCount = jsonObject.optString("share_count");

            SearchModel homeEntertainmentModel = new SearchModel();
            homeEntertainmentModel.setVideoId(videoId);
            homeEntertainmentModel.setUserProfileId(userProfileId);
            homeEntertainmentModel.setVideoDescription(videoDesp);
            homeEntertainmentModel.setVideoGif(videoGif);
            homeEntertainmentModel.setVideoUrl(videoUrl);
            homeEntertainmentModel.setVideoLikesCount(videoLikesCount);
            homeEntertainmentModel.setVideoProfileImage(videoProfileImage);
            homeEntertainmentModel.setVideoSongId(videoSongId);
            homeEntertainmentModel.setVideoSongName(videoSongName);
            homeEntertainmentModel.setUserProfileName(userProfileName);
            homeEntertainmentModel.setTotalLikeCount(totalLikeCount);
            homeEntertainmentModel.setTotalCommentCount(totalCommentCount);
            homeEntertainmentModel.setTotalLikeCount(totalLikeCount);
            homeEntertainmentModel.setTotalCommentCount(totalCommentCount);
            homeEntertainmentModel.setIsLike(isLike);
            homeEntertainmentModel.setIsFollow(isFollow);
            homeEntertainmentModel.setSongImage(songImage);
            homeEntertainmentModel.setCommentStatus(commentStatus);
            homeEntertainmentModel.setCategoryId(categoryId);
            homeEntertainmentModel.setTotalShareCount(totalShareCount);
            homeEntertainmentModelList.add(homeEntertainmentModel);
        }
        return homeEntertainmentModelList;
    }

    public static List<LikeModel> parseLikeModelList(JSONArray jsonArray) throws Exception {
        List<LikeModel> followerModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String followerId = jsonObject.optString("user_id");
            String followerName = jsonObject.optString("user_name");
            String followerPic = jsonObject.optString("user_profile");
            LikeModel followerModel = new LikeModel();
            followerModel.setlikePersonId(followerId);
            followerModel.setLikePersonName(followerName);
            followerModel.setLikePersonPic(followerPic);
            followerModelList.add(followerModel);
        }
        return followerModelList;
    }

    public static List<NeedTalentAdvertisModel> parseNeedTalentAdvtListModelList(JSONArray jsonArray) throws Exception {
        List<NeedTalentAdvertisModel> followerModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String talentId = jsonObject.optString("id");
            String producerUserId = jsonObject.optString("talent_user_id");
            String producerUserName = jsonObject.optString("talent_username");
            String producerImage = jsonObject.optString("talent_user_profile");
            String advtDescp = jsonObject.optString("description");
            String appliedStatus = jsonObject.optString("your_applied_status");
            String createdDate = jsonObject.optString("created_date");
            String totalAppliedCount = jsonObject.optString("total_user_applied");
            NeedTalentAdvertisModel followerModel = new NeedTalentAdvertisModel();
            followerModel.setTalentId(talentId);
            followerModel.setProducerUserId(producerUserId);
            followerModel.setProducerUserName(producerUserName);
            followerModel.setProducerImage(producerImage);
            followerModel.setAdvertisementDiscription(advtDescp);
            followerModel.setAapliedStatus(appliedStatus);
            followerModel.setCreatedDate(createdDate);
            followerModel.setTotalAppliedCount(totalAppliedCount);
            followerModelList.add(followerModel);
        }
        return followerModelList;
    }

    public static List<NotificationModel> parseNotificationListModelList(JSONArray jsonArray) throws Exception {
        List<NotificationModel> followerModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String userName = jsonObject.optString("opp_username");
            String profilePic = jsonObject.optString("profile_pic");
            String message = jsonObject.optString("message");
            String date = jsonObject.optString("created_date");
            NotificationModel followerModel = new NotificationModel();
            followerModel.setUserName(userName);
            followerModel.setProfilePic(profilePic);
            followerModel.setMessage(message);
            followerModel.setDate(date);

            followerModelList.add(followerModel);
        }
        return followerModelList;
    }


    public static List<User> parseUserModelList(JSONArray jsonArray) throws Exception {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String username = jsonObject.optString("username");
            String email = jsonObject.optString("email");
            String mobile = jsonObject.optString("mobile");
            String userId = jsonObject.optString("user_id");
            String photoURL = jsonObject.optString("photoURL");
            String userFollow = jsonObject.optString("user_follow");
            String userFollowing = jsonObject.optString("user_following");
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setMobile(mobile);
            user.setUserId(userId);
            user.setPhotoURL(photoURL);
            user.setUserFollow(userFollow);
            user.setUserFollowing(userFollowing);
            userList.add(user);
        }
        return userList;
    }
}
