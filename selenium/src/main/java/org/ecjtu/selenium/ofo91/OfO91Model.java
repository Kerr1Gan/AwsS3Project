package org.ecjtu.selenium.ofo91;

import java.io.Serializable;

public class OfO91Model implements Serializable{

    private static final long serialVersionUID = 1L;

    private String title;
    private String imageUrl;
    private String videoUrl;
    private String innerVideoUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getInnerVideoUrl() {
        return innerVideoUrl;
    }

    public void setInnerVideoUrl(String innerVideoUrl) {
        this.innerVideoUrl = innerVideoUrl;
    }
}
