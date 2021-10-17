package com.video.tamas.Utils;

public class Config {
    //File upload url (replace the ip with your server address)
    //public static final String SERVICE_URL = "http://192.168.0.132/AndroidFileUpload/fileUpload.php";
    //public static final String SERVICE_URL = "http://www.tamas.in/api/VideoApp/mobileapi/service.php?servicename=";
    // Directory name to store captured images and videos
    public static final String SERVICE_URL = "http://www.tamas.in/api/VideoApp/mobileapi/service.php?servicename=";
    public static final String IMAGE_DIRECTORY_NAME = "Tamas Videos";
    public static final String USER_ID = "UserID";
    public static final String USER_NAME = "UserName";
    public static final String LoginType = "LoginType";
    public static final String AppLanguage = "AppLanguage";
    public static final String EMAIL_ADDRESS = "EmailAddress";

    public interface MethodName {
        String TagSort = SERVICE_URL + "tagsort";
        String userStatus = SERVICE_URL + "checkuser_status";
        String login = SERVICE_URL + "Login";
        String Register = SERVICE_URL + "Register";
        String CheckEmail = SERVICE_URL + "check_email";
        String RegFB = SERVICE_URL + "reg_fb";
        String ForgotPassword = SERVICE_URL + "forget_password";
        String CheckOtp = SERVICE_URL + "check_otp";
        String UpdatePassword = SERVICE_URL + "update_password";
        String aboutUs = SERVICE_URL + "about_us";
        String privacyPolicy = SERVICE_URL + "privacy_policy";
        String TermsAndCondition = SERVICE_URL + "terms_condition";
        String getVideos = SERVICE_URL + "randomvideo";
        String getUserVideos = SERVICE_URL + "uservideo";
        String uploadVideos = SERVICE_URL + "videoupload";
        String uploadDraftVideos = SERVICE_URL + "savedraft";
        String getCategories = SERVICE_URL + "get_maincategory";
        String getSubcategory = SERVICE_URL + "get_category";
        String uploadSong = SERVICE_URL + "songupload";
        String getSongList = SERVICE_URL + "getsongs";
        String like = SERVICE_URL + "likeupdate";
        String followUnfollow = SERVICE_URL + "follow";
        String comment = SERVICE_URL + "videocomment";
        String getCommentList = SERVICE_URL + "get_comment_list";
        String getFollowerList = SERVICE_URL + "followers_list";
        String getFollowingList = SERVICE_URL + "followings_list";
        String getVideoBySongId = SERVICE_URL + "get_video_songid";
        String getWriterList = SERVICE_URL + "writer_api";
        String search = SERVICE_URL + "search_apis";
        String videoDelete = SERVICE_URL + "video_delete";
        String getUserInfo = SERVICE_URL + "userinfo";
        String editProfile = SERVICE_URL + "update_user_profile";

        String add_share_count = SERVICE_URL + "share_count";
        String add_view_count = SERVICE_URL + "count_video";
        String show_like_list = SERVICE_URL + "total_like_list";

        //need talent
        String need_talent_advt_list = SERVICE_URL + "need_talent_show";
        String need_talent_apply = SERVICE_URL + "need_talent_apply";

        //notification
        String notificationList = SERVICE_URL + "notification_list";

    }
}
