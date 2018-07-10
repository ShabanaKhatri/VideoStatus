package com.ploodi.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Zerones
 * Disable touch event happened in RecyclerView
 */

public class DisableRecyclerView extends RecyclerView {

    private boolean mEnableScroll = true;

    public DisableRecyclerView(Context context) {
        super(context);
    }

    public DisableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DisableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!mEnableScroll)
            return true;
        return super.dispatchTouchEvent(ev);
    }

    public void setEnableScroll(boolean enableScroll) {
        this.mEnableScroll = enableScroll;
    }
}
