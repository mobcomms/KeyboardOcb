package com.enliple.keyboard.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.enliple.keyboard.R;

public class StrokeTextView extends TextView {

    private boolean stroke = false;
    private float strokeWidth = 0.0f;
    private int strokeColor;

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView(context, attrs);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context, attrs);
    }

    public StrokeTextView(Context context) {
        super(context);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
        stroke = a.getBoolean(R.styleable.StrokeTextView_textStroke, true);
        strokeWidth = a.getFloat(R.styleable.StrokeTextView_textStrokeWidth, 9.0f);
        strokeColor = a.getColor(R.styleable.StrokeTextView_textStrokeColor, 0xff333333);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (stroke) {
            ColorStateList states = getTextColors();
            getPaint().setStyle(Paint.Style.STROKE);
            getPaint().setStrokeWidth(strokeWidth);
            setTextColor(strokeColor);
            super.onDraw(canvas);

            getPaint().setStyle(Paint.Style.FILL);
            setTextColor(states);
        }

        super.onDraw(canvas);
    }
}