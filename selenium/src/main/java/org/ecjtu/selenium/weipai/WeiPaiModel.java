package org.ecjtu.selenium.weipai;

import java.io.Serializable;

public class WeiPaiModel implements Serializable {

    private static final long serialVersionUID = 1;

    private String title;
    private String href;
    private String imgUrl;
    private String videoUrl;
    private String videoImageUrl;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoImageUrl() {
        return videoImageUrl;
    }

    public void setVideoImageUrl(String videoImageUrl) {
        this.videoImageUrl = videoImageUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WeiPaiModel) {
            WeiPaiModel local = (WeiPaiModel) obj;
            return this.videoUrl.equals(local.videoUrl);
        }
        return super.equals(obj);
    }
}
