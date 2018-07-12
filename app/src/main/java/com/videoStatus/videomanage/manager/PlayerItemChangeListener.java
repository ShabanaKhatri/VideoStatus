package com.videoStatus.videomanage.manager;


import com.videoStatus.videomanage.meta.MetaData;

public interface PlayerItemChangeListener {
    void onPlayerItemChanged(MetaData currentItemMetaData);
}
