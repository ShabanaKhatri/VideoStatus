package com.videoStatus.retrofit;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class DataItem {

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("number_of_views")
    private String numberOfViews;

    @SerializedName("video")
    private String video;

    @SerializedName("title")
    private String title;

    @SerializedName("video_id")
    private String videoId;

    @SerializedName("TubeId")
    private String TubeId;

    @SerializedName("Title")
    private String Title;

    @SerializedName("Description")
    private String Description;

    @SerializedName("Link")
    private String Link;

    @SerializedName("Thumb")
    private String Thumb;

    @SerializedName("CategoryId")
    private String CategoryId;

    @SerializedName("ShareCount")
    private String ShareCount;

    public String getTubeId() {
        return TubeId;
    }

    public void setTubeId(String tubeId) {
        TubeId = tubeId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getThumb() {
        return Thumb;
    }

    public void setThumb(String thumb) {
        Thumb = thumb;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getShareCount() {
        return ShareCount;
    }

    public void setShareCount(String shareCount) {
        ShareCount = shareCount;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setNumberOfViews(String numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public String getNumberOfViews() {
        return numberOfViews;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideo() {
        return video;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoPlaying(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoPlaying() {
        return videoId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int position;

    public boolean isPlayVideo() {
        return isPlayVideo;
    }

    public void setPlayVideo(boolean playVideo) {
        isPlayVideo = playVideo;
    }

    private boolean isPlayVideo;

    public int getLastPlyesPosition() {
        return lastPlyesPosition;
    }

    public void setLastPlyesPosition(int lastPlyesPosition) {
        this.lastPlyesPosition = lastPlyesPosition;
    }

    public long getSeek_position() {
        return seek_position;
    }

    public void setSeek_position(long seek_position) {
        this.seek_position = seek_position;
    }

    private long seek_position;

    public boolean isSeekFlag() {
        return seekFlag;
    }

    public void setSeekFlag(boolean seekFlag) {
        this.seekFlag = seekFlag;
    }

    private boolean seekFlag;
    private int lastPlyesPosition;

    @Override
    public String toString() {
        return
                "DataItem{" +
                        "createdAt = '" + createdAt + '\'' +
                        ",thumbnail = '" + thumbnail + '\'' +
                        ",number_of_views = '" + numberOfViews + '\'' +
                        ",video = '" + video + '\'' +
                        ",title = '" + title + '\'' +
                        ",video_id = '" + videoId + '\'' +
                        "}";
    }
}