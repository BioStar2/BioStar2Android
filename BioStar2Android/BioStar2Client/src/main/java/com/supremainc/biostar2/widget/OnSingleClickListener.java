package com.supremainc.biostar2.widget;

import android.os.SystemClock;
import android.view.View;

public abstract class OnSingleClickListener implements View.OnClickListener {
    private static final long MIN_CLICK_INTERVAL = 700;
    private long mLastClickTime;

    @Override
    public final void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            return;
        }
        mLastClickTime = currentClickTime;
        onSingleClick(v);
    }

    public abstract void onSingleClick(View v);

}
