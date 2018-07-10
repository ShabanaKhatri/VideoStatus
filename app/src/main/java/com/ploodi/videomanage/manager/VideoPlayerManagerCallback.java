package com.ploodi.videomanage.manager;


import com.ploodi.videomanage.PlayerMessageState;
import com.ploodi.videomanage.meta.MetaData;
import com.ploodi.videomanage.ui.VideoPlayerView;

/**
 * This callback is used by {@link com.ploodi.videomanage.playermessages.PlayerMessage}
 * to get and set data it needs
 */
public interface VideoPlayerManagerCallback {

    void setCurrentItem(MetaData currentItemMetaData, VideoPlayerView newPlayerView);

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
