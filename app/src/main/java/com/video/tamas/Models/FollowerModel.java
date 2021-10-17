package com.video.tamas.Models;

public class FollowerModel {
    String followerId;
    String followerName;
    String followerPic;
String me_following;

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }

    public String getFollowerPic() {
        return followerPic;
    }

    public void setFollowerPic(String followerPic) {
        this.followerPic = followerPic;
    }

    public String getMe_following() {
        return me_following;
    }

    public void setMe_following(String me_following) {
        this.me_following = me_following;
    }
}
