package com.enliple.keyboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.activity.SoftKeyboard;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;


import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CoupangAdPopup extends PopupWindow {
    private Context mContext;
    private View popupview;
    private ImageView adImage;
    private TextView adTitle;
    private Handler mHandler;

    public static final String ALGORITHM = "HmacSHA256";
    public static final Charset STANDARD_CHARSET = Charset.forName("UTF-8");
    /**
     * Used to build output as hex.
     */
    public static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    /**
     * Used to build output as hex.
     */
    public static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F' };

    public CoupangAdPopup(Context context, View targetView, AdVO advo, Handler chandler){
        super(context);
        mContext = context;
        mHandler = chandler;
        popupview = View.inflate(context, R.layout.dialog_coupang_ad, null);

        adImage = (ImageView)popupview.findViewById(R.id.adImage);
        adTitle = (TextView)popupview.findViewById(R.id.adTitle);
        ConstraintLayout parentView = (ConstraintLayout)popupview.findViewById(R.id.parentView);
        ImageLoader.with(context).from(advo.getImgUrl()).transform(ImageUtils.cropCenter()).load(adImage);
        String text = advo.getTitle();

        if(text != null && !text.isEmpty()) {
            text = text.replace("<b>", "<big><b>");
            text = text.replace("</b>", "</b></big>");
            adTitle.setText(Html.fromHtml(text));
        }

        ImageView closeBtn = (ImageView)popupview.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.setLong(mContext, Common.PREF_COUPANG_CLOSE_TIME, System.currentTimeMillis());
                dismiss();
            }
        });

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(advo != null){
                    Message msg = mHandler.obtainMessage();
                    msg.what = SoftKeyboard.OPEN_COUPANG_AD;
                    msg.obj = advo.getTargetUrl();
                    mHandler.sendMessage(msg);
                }
            }
        });

        setContentView(popupview);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(-1);
        setClippingEnabled(false);

        showAtLocation(targetView, Gravity.NO_GRAVITY, 0, -popupview.getMinimumHeight());
    }

    public void setVO(AdVO advo){
        ImageLoader.with(mContext).from(advo.getImgUrl()).transform(ImageUtils.cropCenter()).load(adImage);
        String text = advo.getTitle();
        if(text != null && !text.isEmpty()) {
            text = text.replace("<b>", "<big><b>");
            text = text.replace("</b>", "</b></big>");
            adTitle.setText(Html.fromHtml(text));
        }

        popupview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(advo != null){
                    Message msg = mHandler.obtainMessage();
                    msg.what = SoftKeyboard.OPEN_COUPANG_AD;
                    msg.obj = advo.getTargetUrl();
                    mHandler.sendMessage(msg);
                }
            }
        });

        update();
    }

    public static class AdVO{
        public String imgUrl;
        public String targetUrl;
        public String title;

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getTargetUrl() {
            return targetUrl;
        }

        public void setTargetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


    /**
     * Generate HMAC signature
     * @param method
     * @param uri http request uri
     * @param secretKey secret key that Coupang partner granted for calling open api
     * @param accessKey access key that Coupang partner granted for calling open api
     * @return HMAC signature
     */
    public static String generate(String method, String uri, String secretKey, String accessKey) {
        String[] parts = uri.split("\\?");
        if (parts.length > 2) {
            throw new RuntimeException("incorrect uri format");
        } else {
            String path = parts[0];
            String query = "";
            if (parts.length == 2) {
                query = parts[1];
            }

            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyMMdd'T'HHmmss'Z'");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            String datetime = dateFormatGmt.format(new Date());
            String message = datetime + method + path + query;

            String signature;
            try {
                SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(STANDARD_CHARSET), ALGORITHM);
                Mac mac = Mac.getInstance(ALGORITHM);
                mac.init(signingKey);
                byte[] rawHmac = mac.doFinal(message.getBytes(STANDARD_CHARSET));
                signature = encodeHexString(rawHmac);
            } catch (GeneralSecurityException e) {
                throw new IllegalArgumentException("Unexpected error while creating hash: " + e.getMessage(), e);
            }

            return String.format("CEA algorithm=%s, access-key=%s, signed-date=%s, signature=%s", "HmacSHA256", accessKey, datetime, signature);
        }
    }

    private static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    private static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }

    private static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    private static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        encodeHex(data, 0, data.length, toDigits, out, 0);
        return out;
    }

    private static void encodeHex(final byte[] data, final int dataOffset, final int dataLen, final char[] toDigits,
                                  final char[] out, final int outOffset) {
        for (int i = dataOffset, j = outOffset; i < dataOffset + dataLen; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
    }
}
