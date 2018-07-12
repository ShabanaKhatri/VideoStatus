package com.videoStatus.retrofit.Response;

public class Meme {

    private String meme_id;

    public String getMeme_id() {
        return meme_id;
    }

    public void setMeme_id(String meme_id) {
        this.meme_id = meme_id;
    }

    public String getMeme_name() {
        return meme_name;
    }

    public void setMeme_name(String meme_name) {
        this.meme_name = meme_name;
    }

    public String getViews_count() {
        return views_count;
    }

    public void setViews_count(String views_count) {
        this.views_count = views_count;
    }

    public String getMeme_link() {
        return meme_link;
    }

    public void setMeme_link(String meme_link) {
        this.meme_link = meme_link;
    }

    public String getTime_added() {
        return time_added;
    }

    public void setTime_added(String time_added) {
        this.time_added = time_added;
    }

    private String meme_name;
    private String views_count;
    private String meme_link;
    private String time_added;
}
