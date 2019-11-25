package com.example.chatbox;

import java.io.Serializable;

public class MyMetaData implements Serializable {
    
    private String url = "";
    private String imageurl = "";
    private String title = "";
    private String description = "";
    private String sitename = "";
    private String mediatype = "";
    private String favicon = "";

    public MyMetaData(String url, String imageurl, String title, String description, String sitename, String mediatype, String favicon) {
        this.url = url;
        this.imageurl = imageurl;
        this.title = title;
        this.description = description;
        this.sitename = sitename;
        this.mediatype = mediatype;
        this.favicon = favicon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }
}
