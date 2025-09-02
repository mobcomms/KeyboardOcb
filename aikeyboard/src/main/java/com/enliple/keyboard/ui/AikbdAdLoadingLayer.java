package com.enliple.keyboard.ui;


import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.enliple.keyboard.R;
import com.enliple.keyboard.imgmodule.ImageModule;

public class AikbdAdLoadingLayer extends RelativeLayout {
    private ImageView img_loading;
    private RelativeLayout ad_fail_layer;
    private Context context;
    public AikbdAdLoadingLayer(Context context) {
        super(context);
        initViews(context);
    }

    public AikbdAdLoadingLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public AikbdAdLoadingLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        this.context = context;
        String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(inflaterService);
        View view = layoutInflater.inflate(R.layout.aikbd_ad_loading_layer, this, false);
        addView(view);
        img_loading = findViewById(R.id.img_loading);
        ad_fail_layer = findViewById(R.id.ad_fail_layer);
    }

    public void setLoadingImage() {
        if ( ad_fail_layer != null ) {
            ad_fail_layer.setVisibility(View.GONE);
        }
        if ( img_loading != null ) {
            img_loading.setVisibility(View.VISIBLE);
            ImageModule.with(AikbdAdLoadingLayer.this).load(R.raw.aikbd_chat_gpt_loading).into(img_loading);
        }
    }

    public void setAdFailImage() {
        if ( ad_fail_layer != null ) {
            ad_fail_layer.setVisibility(View.VISIBLE);
        }
        if ( img_loading != null ) {
            img_loading.setVisibility(View.GONE);
        }
    }

    public boolean isLoadingImageVisible() {
        if ( img_loading != null ) {
            if ( img_loading.getVisibility() == View.VISIBLE )
                return true;
            else
                return false;
        }
        return false;
    }

    public boolean isAdFailImageVisible() {
        if ( ad_fail_layer != null ) {
            if ( ad_fail_layer.getVisibility() == View.VISIBLE )
                return true;
            else
                return false;
        }
        return false;
    }
}
