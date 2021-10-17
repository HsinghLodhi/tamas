
package com.video.tamas.Models.tag;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.video.tamas.Models.HomePopularModel;

public class Data1 {

    @SerializedName("data")
    @Expose
    private List<VideoData> data = null;
    @SerializedName("tag_name")
    @Expose
    private String tagName;

    public List<VideoData> getData() {
        return data;
    }

    public void setData(List<VideoData> data) {
        this.data = data;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
