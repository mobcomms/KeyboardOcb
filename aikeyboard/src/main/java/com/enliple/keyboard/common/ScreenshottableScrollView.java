package com.enliple.keyboard.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;

public class ScreenshottableScrollView extends HorizontalScrollView implements ViewTreeObserver.OnScrollChangedListener {

    public interface OnNewScreenshotListener {
        void onNewScreenshot(Bitmap bitmap);
    }

    private Bitmap screenshotBitmap = null;
    private Canvas screenshotCanvas = null;
    private int screenshotHeightPx = 0;
    private OnNewScreenshotListener listener = null;
    private Rect cropRect;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public ScreenshottableScrollView(Context context) {
        super(context);
        init();
    }

    public ScreenshottableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScreenshottableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScreenshottableScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setDrawingCacheEnabled(true);
        getViewTreeObserver().addOnScrollChangedListener(this);
    }

    public void setOnNewScreenshotListener(OnNewScreenshotListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (screenshotHeightPx != 0)
            makeScrenshotBitmap(w, h);
    }

    public void setScreenshotHeightPx(int q) {
        screenshotHeightPx = q;
        makeScrenshotBitmap(getWidth(), getHeight());
    }

    public void setBgAlpha(int _alpha) {
        paint.setAlpha(_alpha);
    }

    private void makeScrenshotBitmap(int width, int height) {

        if (screenshotBitmap != null) screenshotBitmap.recycle();

        if (width == 0 || height == 0) return;

        screenshotBitmap = Bitmap.createBitmap(width, screenshotHeightPx, Bitmap.Config.ARGB_8888);
        screenshotCanvas = new Canvas(screenshotBitmap);
        cropRect = new Rect(0, 0, width, screenshotHeightPx);


    }

    @Override
    public void onScrollChanged() {
        if (listener == null) return;

        Bitmap bitmap = getDrawingCache();

        screenshotCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        screenshotCanvas.drawBitmap(bitmap, cropRect, cropRect, paint);
        listener.onNewScreenshot(screenshotBitmap);
    }
}