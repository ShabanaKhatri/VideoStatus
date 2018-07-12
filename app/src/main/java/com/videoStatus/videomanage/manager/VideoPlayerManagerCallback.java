package com.videoStatus.videomanage.manager;


import com.videoStatus.videomanage.PlayerMessageState;
import com.videoStatus.videomanage.meta.MetaData;
import com.videoStatus.videomanage.ui.VideoPlayerView;

/**
 * This callback is used by {@link com.videoStatus.videomanage.playermessages.PlayerMessage}
 * to get and set data it needs
 */
public interface VideoPlayerManagerCallback {

    void setCurrentItem(MetaData currentItemMetaData, VideoPlayerView newPlayerView);

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
