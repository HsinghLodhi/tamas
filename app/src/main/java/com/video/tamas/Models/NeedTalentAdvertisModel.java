package com.video.tamas.Models;

/**
 * Created by sonika-mac on 12/11/19.
 */

public class NeedTalentAdvertisModel {
    String producerUserId;
    String producerUserName;
    String producerImage;
    String advertisementDiscription;
    String totalAppliedCount;
    String createdDate;
    String aapliedStatus;
    String talentId;

    public String getTalentId() {
        return talentId;
    }

    public void setTalentId(String talentId) {
        this.talentId = talentId;
    }

    public String getProducerUserId() {
        return producerUserId;
    }

    public void setProducerUserId(String producerUserId) {
        this.producerUserId = producerUserId;
    }

    public String getProducerUserName() {
        return producerUserName;
    }

    public void setProducerUserName(String producerUserName) {
        this.producerUserName = producerUserName;
    }

    public String getProducerImage() {
        return producerImage;
    }

    public void setProducerImage(String producerImage) {
        this.producerImage = producerImage;
    }

    public String getAdvertisementDiscription() {
        return advertisementDiscription;
    }

    public void setAdvertisementDiscription(String advertisementDiscription) {
        this.advertisementDiscription = advertisementDiscription;
    }

    public String getTotalAppliedCount() {
        return totalAppliedCount;
    }

    public void setTotalAppliedCount(String totalAppliedCount) {
        this.totalAppliedCount = totalAppliedCount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAapliedStatus() {
        return aapliedStatus;
    }

    public void setAapliedStatus(String aapliedStatus) {
        this.aapliedStatus = aapliedStatus;
    }
}
