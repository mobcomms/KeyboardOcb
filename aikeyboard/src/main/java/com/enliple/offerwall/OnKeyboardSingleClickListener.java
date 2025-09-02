package com.enliple.offerwall;

import android.os.SystemClock;
import android.view.View;

public abstract  class OnKeyboardSingleClickListener implements View.OnClickListener {
    private static final long MIN_CLICK_INTERFAL = 1000L;
    private long mLastClickTime = 0;
    protected abstract void onSingleClick(View v);
    @Override
    public void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;

        if ( elapsedTime > MIN_CLICK_INTERFAL ) {
            onSingleClick(v);
        }
    }

}
