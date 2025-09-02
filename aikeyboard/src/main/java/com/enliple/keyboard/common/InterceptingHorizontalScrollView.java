package com.enliple.keyboard.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;

public class InterceptingHorizontalScrollView extends HorizontalScrollView {

    public InterceptingHorizontalScrollView(Context context) {
        super(context);
    }
    public InterceptingHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public InterceptingHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onOverScrolled (int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX,scrollY,clampedX,clampedY);
        // if clampedX == true, we've reached the end of the HorizontalScrollView so
        // allow parent to intercept
        if(clampedX) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
    }
}