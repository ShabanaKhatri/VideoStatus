package com.brucetoo.videoplayer.tracker;

import com.brucetoo.videoplayer.tracker.IViewTracker;

/**
 * Created by Zerones
 */

public interface VisibleChangeListener {

    void onVisibleChange(float visibleRatio, IViewTracker tracker);

}
