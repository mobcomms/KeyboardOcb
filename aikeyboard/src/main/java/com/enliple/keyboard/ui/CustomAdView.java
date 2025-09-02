package com.enliple.keyboard.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.KeyboardChatGptChatActivity;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.request.target.CustomTarget;
import com.enliple.keyboard.imgmodule.request.transition.Transition;
import com.enliple.keyboard.ui.common.LogPrint;

public class CustomAdView extends RelativeLayout {
    private String adType = "";
    public ClickListener listener;
    private boolean isRocket = false;
    private ImageView image, logo, coupang_logo;
    private Context context;

    public interface ClickListener {
        void onAdClick();
        void onLogoClick();
        void onImageSetted(boolean isSet);
    };

    public CustomAdView(Context context, ClickListener listener) {
        super(context);
        this.listener = listener;
        this.context = context;
        initViews(context);
    }

    public CustomAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CustomAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        this.context = context;
        String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(inflaterService);
        View view = layoutInflater.inflate(R.layout.aikbd_custom_ad_layer, this, false);
        addView(view);
        image = findViewById(R.id.image);
        logo = findViewById(R.id.logo);
        coupang_logo = findViewById(R.id.coupang_logo);

        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null ) {
                    listener.onAdClick();
                }
            }
        });

        logo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null ) {
                    listener.onLogoClick();
                }
            }
        });
    }
    public void setRocket(boolean isRocket) {
        this.isRocket = isRocket;
    }
    public void setAd(String adType, boolean isRocket, String path, String logoPath, int base_width, int base_height, int screenWidth) {
        this.adType = adType;
        this.isRocket = isRocket;
        if ( image != null && !TextUtils.isEmpty(path)) {
            image.setVisibility(View.GONE);
            if ( base_width > 0 && base_height > 0 ) {
                int targetWidth = screenWidth - Common.convertDpToPx(context,110);
                int targetHeight = targetWidth * base_height / base_width;
                CardView.LayoutParams params = new CardView.LayoutParams(targetWidth, targetHeight);
                setLayoutParams(params);

                ImageModule.with(CustomAdView.this).load(path).into(image);
                image.setVisibility(View.VISIBLE);
                if ( listener != null )
                    listener.onImageSetted(true);
            } else {
                ImageModule.with(context).asBitmap().load(path).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        if ( width > 0 && height > 0 ) {
                            int targetWidth = screenWidth - Common.convertDpToPx(context,110);
                            int targetHeight = targetWidth * height / width;
                            CardView.LayoutParams params = new CardView.LayoutParams(targetWidth, targetHeight);
                            setLayoutParams(params);
                        }
                        image.setImageBitmap(resource);
                        image.setVisibility(View.VISIBLE);

                        if (KeyboardChatGptChatActivity.AD_MOBON.equals(adType) ) {
                            if ( coupang_logo != null ) {
                                coupang_logo.setVisibility(View.GONE);
                            }
                            if ( logo != null && !TextUtils.isEmpty(logoPath)) {
                                logo.setVisibility(View.VISIBLE);
                                ImageModule.with(CustomAdView.this).load(logoPath).into(logo);
                            } else if ( logo != null && TextUtils.isEmpty(logoPath) ) {
                                logo.setVisibility(View.GONE);
                            }
                        } else if ( KeyboardChatGptChatActivity.AD_COUPANG.equals(adType) ) {
                            if ( logo != null ) {
                                logo.setVisibility(View.GONE);
                            }
                            if (coupang_logo != null ) {
                                coupang_logo.setVisibility(View.VISIBLE);
                                if ( isRocket ) {
                                    coupang_logo.setBackgroundResource(R.drawable.aikbd_chat_coupang_rocket_logo);
                                } else {
                                    coupang_logo.setBackgroundResource(R.drawable.aikbd_chat_coupang_logo);
                                }
                            }
                        }
                        if ( listener != null )
                            listener.onImageSetted(true);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        LogPrint.d("onLoadCleared CustomAdView");
                        if ( listener != null )
                            listener.onImageSetted(false);
                    }
                });
            }
        }
    }
}