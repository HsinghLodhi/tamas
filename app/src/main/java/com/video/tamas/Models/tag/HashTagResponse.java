
package com.video.tamas.Models.tag;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HashTagResponse {

    @SerializedName("data")
    @Expose
    private List<Data1> data = null;
    @SerializedName("response")
    @Expose
    private Boolean response;

    public List<Data1> getData() {
        return data;
    }

    public void setData(List<Data1> data) {
        this.data = data;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
}
