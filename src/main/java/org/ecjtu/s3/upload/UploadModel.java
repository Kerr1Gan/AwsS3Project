package org.ecjtu.s3.upload;

import java.io.Serializable;

public class UploadModel implements Serializable {

    private String name;

    private String url;

    private Boolean hasFinished = false;

    public UploadModel() {
    }

    public UploadModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(Boolean hasFinished) {
        this.hasFinished = hasFinished;
    }
}
