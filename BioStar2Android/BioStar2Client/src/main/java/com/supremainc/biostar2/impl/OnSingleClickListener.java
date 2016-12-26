package com.supremainc.biostar2.impl;

import android.os.SystemClock;
import android.view.View;

public abstract class OnSingleClickListener implements View.OnClickListener {
    private static final long MIN_CLICK_INTERVAL = 700;
    private long mLastClickTime;
    private int mID = -1;

    @Override
    public final void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            if (mID != -1 && mID == v.getId()) {
                return;
            }
        }
        mLastClickTime = currentClickTime;
        mID = v.getId();
        onSingleClick(v);
    }

    public abstract void onSingleClick(View v);

}
