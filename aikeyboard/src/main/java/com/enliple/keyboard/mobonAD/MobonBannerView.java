package com.enliple.keyboard.mobonAD;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.request.target.CustomTarget;
import com.enliple.keyboard.imgmodule.request.transition.Transition;
import com.enliple.keyboard.mobonAD.manager.AdapterObject;
import com.enliple.keyboard.mobonAD.manager.MediationManager;
import com.enliple.keyboard.mobonAD.manager.iMobonMediationCallback;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.Context.POWER_SERVICE;
import static com.enliple.keyboard.common.Common.GetColors;
import static com.enliple.keyboard.common.Common.convertDpToPx;

public class MobonBannerView extends RelativeLayout {
    private ImageView mContentIv;
    private TextView mTitle;
    private TextView mPrice;
    private CheckBox mCheckbox;

    //private final int[] Banner_50_Layout_ids = {R.layout.mobon_banner_50_theme3_layout, R.layout.mobon_banner_50_theme4_layout, R.layout.mobon_banner_50_theme5_red_layout, R.layout.mobon_banner_50_theme5_gray_layout, R.layout.mobon_banner_50_theme5_blue_layout};

    private final int[] Banner_100_Layout_ids = {R.layout.mobon_banner_50_theme1_layout, R.layout.mobon_banner_50_theme2_layout, R.layout.mobon_banner_100_theme4_layout, R.layout.mobon_banner_100_theme5_red_layout, R.layout.mobon_banner_100_theme5_gray_layout, R.layout.mobon_banner_100_theme5_blue_layout};
    // private final int[] Banner_50_Land_Layout_ids = {R.layout.mobon_banner_sr_land_layout, R.layout.mobon_banner_sr_land_layout2};

    private final Context mContext;
    private RelativeLayout mMainLayout;
    private ImageView mMobonMark;

    private final AtomicInteger RetryCount = new AtomicInteger(0);
    private int mParentHeight = 0;
    private String mBannerType = MobonBannerType.BANNER_320x50;
    private boolean mIsBacon;
    private boolean isbaconInstalled;
    private ColorStateList mBannerBgColor;
    private String mBannerScaleType;
    private int mXmlHeight;
    private int mInterval;
    private String mBannerSiteCode = null;
    private String mBarconSiteCode = null;
    private iSimpleMobonBannerCallback mIBannerAdCallback = null;
    private iDelClickCallback mDelClickCallback = null;
    private int CALL_AD_COUNT;
    private int adView_count;
    private boolean viewVisible;
    private String mAdData;
    private String mScriptCode;
    private String mScriptUrl;
    private int mImageLimit = -1;
    private LinearLayout image_layout;
    private Handler mScheduleHandler;
    private boolean isUpdateUI;
    private boolean isParantRemove;
    private Object mMediationAdView;
    private boolean isExtractColor = true;
    private String mobonInfo;
    private long mClickedTime;
    private static boolean isClearAuid;
    private MediationManager mediationManager;
    private WebView mWebview;
    private ImageView dummy_image;
    private AdapterObject adapterObject;
    private String mScriptNo = "";
    private RelativeLayout ad_del;
    private View left_bg, right_bg;

    private int leftColor = -1;
    private int rightColor = -1;
    private boolean isReward = false;
    public MobonBannerView(Context context) {
        super(context);

        this.mContext = context;
        mBannerType = MobonBannerType.BANNER_320x50;
        //   onInit();
    }

    public MobonBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //   onInit();

    }

    public MobonBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        onInit();
    }

    public MobonBannerView(Context context, String _adType) {
        super(context);
        this.mContext = context;
        mBannerType = _adType;
        //  onInit();

    }

    public MobonBannerView(Context context, String _adType, boolean isRun, boolean isReward) {
        super(context);
        this.mContext = context;
        mBannerType = _adType;
        this.isReward = isReward;
        int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
        if ( point <= 0 && isReward ) {
            this.isReward = false;
        }
        //  onInit();
        /**
         if (!isRun)
         webViewSetPath(context);**/

    }

    private void webViewSetPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(context);
            String packageName = context.getPackageName();
            if (!packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    private String getProcessName(Context context) {
        if (context == null) {
            return null;
        }

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    public MobonBannerView setImageSizeLimit(int limitKb) {
        mImageLimit = limitKb;
        return this;
    }


    public MobonBannerView setAdType(String _adType) {
        mBannerType = _adType;
        return this;
    }

    /**
     * false 일 경우 bg 안그림.
     * @param _is
     * @return
     */
    public MobonBannerView setExtractColor(boolean _is) {
        isExtractColor = _is;
        return this;
    }

    public void setBgColor(String _color) {
        try {
            int color = Color.parseColor(_color);
            mBannerBgColor = ColorStateList.valueOf(color);
        } catch (Exception e) {

        }
    }

    public void setBgColor(int _color) {
        try {
            mBannerBgColor = ColorStateList.valueOf(_color);
        } catch (Exception e) {

        }
    }

    public void setScaleType(String _scaleType) {
        mBannerScaleType = _scaleType;
    }

    public MobonBannerView setBacon() {
        mIsBacon = true;
        return this;
    }

    public MobonBannerView setBannerUnitId(String unitId) {
        mBannerSiteCode = unitId;
        return this;
    }

    public MobonBannerView setScriptUrl(String _url) {
        mScriptUrl = _url;
        return this;
    }

    public MobonBannerView setScriptCode(String s) {
        mScriptCode = s;
        return this;
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        LogPrint.d("onWindowVisibilityChanged = " + visibility);
        if (visibility == View.GONE) {
            viewVisible = false;
            if (mWebview != null)
                mWebview.onPause();
        } else if (visibility == View.VISIBLE) {
            viewVisible = true;
            if (mWebview != null)
                mWebview.onResume();

            if (image_layout != null) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ViewGroup.LayoutParams params = image_layout.getLayoutParams();
                            params.width = image_layout.getHeight();
                            image_layout.setLayoutParams(params);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }

        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        LogPrint.d("hasWindowFocus = " + hasWindowFocus);
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (!isParantRemove) {
            mInterval = 0;
            destroyAd();
        }
    }

    private void onInit() {
        if (MobonSimpleSDK.get(mContext) == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onInit();
                }
            }, 1000);
            return;
        }

        LogPrint.d("rectBanner onInit bannerId :: " + mBannerSiteCode);

        leftColor = -1;
        rightColor = -1;

        if (RetryCount.get() > 5) {
            RetryCount.set(0);
            return;
        }

        if (TextUtils.isEmpty(mBannerSiteCode))
            mBannerSiteCode = SPManager.getString(getContext(), MobonKey.MOBON_MEDIA_BANNER_S_VALUE);

        if (TextUtils.isEmpty(mBannerSiteCode)) {
            LogPrint.d("keyboardad mobon banner 설정값을 아직 받지못하여 재시도!!!");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    onInit();
                }
            }, 500 * RetryCount.incrementAndGet());
            return;
        }

        RetryCount.set(0);

        if (TextUtils.isEmpty(mBannerType))
            mBannerType = MobonBannerType.BANNER_320x50;

        setmMainLayout();
        isUpdateUI = false;
        isParantRemove = false;

    }

    protected void setmMainLayout() {

        if (mMainLayout != null)
            return;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setGravity(Gravity.CENTER_HORIZONTAL);
                LayoutParams subLayoutParam = null;
                mMainLayout = new RelativeLayout(mContext);
//                    if (getLayoutParams() != null && getLayoutParams().height < 0) {
                if (mParentHeight > 0) {
                    subLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, mParentHeight);
                    subLayoutParam.height = mParentHeight;
                } else {
                    if (mBannerType.equals(MobonBannerType.BANNER_320x50)) {
                        subLayoutParam = new LayoutParams((int) getResources().getDimension(R.dimen.mobon_banner_width), (int) getResources().getDimension(R.dimen.mobon_banner_height_50));
                        subLayoutParam.width = (int) getResources().getDimension(R.dimen.mobon_banner_width);
                        subLayoutParam.height = (int) getResources().getDimension(R.dimen.mobon_banner_height_50);
                    } else if (mBannerType.equals(MobonBannerType.BANNER_320x100)) {
                        subLayoutParam = new LayoutParams((int) getResources().getDimension(R.dimen.mobon_banner_width), (int) getResources().getDimension(R.dimen.mobon_banner_height_100));
                        subLayoutParam.width = (int) getResources().getDimension(R.dimen.mobon_banner_width);
                        subLayoutParam.height = (int) getResources().getDimension(R.dimen.mobon_banner_height_100);
                    } else if (mBannerType.equals(MobonBannerType.BANNER_300x250)) {
                        subLayoutParam = new LayoutParams((int) getResources().getDimension(R.dimen.mobon_banner_width_300), (int) getResources().getDimension(R.dimen.mobon_banner_height_250));
                        subLayoutParam.width = (int) getResources().getDimension(R.dimen.mobon_banner_width_300);
                        subLayoutParam.height = (int) getResources().getDimension(R.dimen.mobon_banner_height_250);
                    } else if (mBannerType.equals(MobonBannerType.BANNER_FILLx60)) {
                        subLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.mobon_banner_height_60));
                        subLayoutParam.height = (int) getResources().getDimension(R.dimen.mobon_banner_height_60);
                    } else if (mBannerType.equals(MobonBannerType.BANNER_600x600)) {
                        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            subLayoutParam = new LayoutParams(dm.widthPixels, dm.widthPixels);
                            subLayoutParam.width = subLayoutParam.height = dm.widthPixels;
                        } else {
                            subLayoutParam = new LayoutParams(dm.heightPixels, dm.heightPixels);
                            subLayoutParam.width = subLayoutParam.height = dm.heightPixels;
                        }
                    } else
                        subLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                }

                mMainLayout.setLayoutParams(subLayoutParam);
                mMainLayout.setGravity(Gravity.CENTER_HORIZONTAL);


                addView(mMainLayout);

            }
        });
    }

    public void loadAd() {
        onInit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterval > 0)
                    bannerSchedule();
                loadData();
            }
        }, 10);
    }

    public void loadCoupangAd() {
        onInit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UpdateScriptUI("coupang","");
            }
        }, 10);
    }




    private void loadMobonAd(String _data) {
        try {
            JSONObject obj = new JSONObject(_data);
            LogPrint.d("obj :: " + obj);
            int length = obj.getJSONArray("client").getJSONObject(0).optInt("length");
            if (length == 0) {
                if (mIBannerAdCallback != null)
                    mIBannerAdCallback.onLoadedAdInfo(false, MobonKey.NOFILL);
                mIBannerAdCallback = null;
                return;
            }
            if (MobonBannerView.this.getParent() != null) {
                isParantRemove = true;
                ((ViewGroup) MobonBannerView.this.getParent()).removeView(MobonBannerView.this);
            }

            if ( isReward ) {
                String t_sc = "";
                String s_link = "";
                JSONArray i_arr = obj.optJSONArray("client");
                if ( i_arr != null && i_arr.length() > 0 ) {
                    JSONObject jObj = i_arr.optJSONObject(0);
                    if ( jObj != null ) {
                        JSONArray d_arr = jObj.optJSONArray("data");
                        if ( d_arr != null && d_arr.length() > 0 ) {
                            JSONObject i_obj = d_arr.optJSONObject(0);
                            String pUrl = i_obj.optString("purl");
                            if ( !TextUtils.isEmpty(pUrl) ) {
                                t_sc = MobonUtils.getParameter(pUrl, "sc");
                                s_link = MobonUtils.getParameter(pUrl, "slink");
                            }
                        }
                    }
                }

                if ( !TextUtils.isEmpty(s_link) ) {
                    if ( s_link.startsWith("http://") ) {
                        if (mIBannerAdCallback != null)
                            mIBannerAdCallback.onLoadedAdInfo(false, "INVALID URL");
                    } else {
                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                        task.checkRewardSc(t_sc, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                            @Override
                            public void onResponse(boolean result, Object obj) {
                                if ( result ) {
                                    try {
                                        JSONObject i_obj = (JSONObject) obj;
                                        if ( i_obj != null ) {
                                            boolean rt = i_obj.optBoolean("Result");
                                            if ( rt ) {
                                                if (TextUtils.isEmpty(mBannerType))
                                                    mBannerType = MobonBannerType.BANNER_320x50;
                                                setmMainLayout();
                                                updateUI(_data, false);
                                            } else {
                                                if (mIBannerAdCallback != null)
                                                    mIBannerAdCallback.onLoadedAdInfo(false, "SC DUPLICATED");
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                }
            } else {
                if (TextUtils.isEmpty(mBannerType))
                    mBannerType = MobonBannerType.BANNER_320x50;
                setmMainLayout();
                updateUI(_data, false);
            }
        } catch (Exception e) {
            if (mIBannerAdCallback != null)
                mIBannerAdCallback.onLoadedAdInfo(false, e.getLocalizedMessage());
        }
    }

    private void loadData() {
        if (RetryCount.get() > 5) {
            if (mIBannerAdCallback != null)
                mIBannerAdCallback.onLoadedAdInfo(false, "Empty UnitId");
            RetryCount.getAndSet(0);
            return;
        }


        if (TextUtils.isEmpty(mBannerSiteCode)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 500 * RetryCount.incrementAndGet());
            return;
        }

        SPManager.setBoolean(mContext, MobonKey.BACON_BANNER_VISIBLE, !SPManager.getBoolean(mContext, MobonKey.BACON_BANNER_VISIBLE));

        Map<String, String> params = MobonUtils.getDefaultParams(mContext);
        params.put("s", mBannerSiteCode);

        MobonUtils.getMobonAdData(mContext, mBannerSiteCode, params, mInterval > 0 ? true : false, mImageLimit, false, new iCommonMobonAdCallback() {
            @Override
            public void onLoadedMobonAdData(boolean result, final JSONObject data, String errorStr) {
                if (result) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (data != null)
                                mAdData = data.toString();
                            if (mInterval > 0) {
                                adView_count = 0;
                            }

                            mediationManager = new MediationManager(mContext, data, mBannerType);
                            mediationManager.LoadMediation(new iMobonMediationCallback() {
                                @Override
                                public void onLoadedAdData(String data, AdapterObject adapter) {
                                    try {
                                        if (mMediationAdView != null) {
                                            if (mMainLayout != null)
                                                mMainLayout.removeAllViews();
                                            if (MobonBannerView.this.getParent() != null) {
                                                isParantRemove = true;
                                                ((ViewGroup) MobonBannerView.this.getParent()).removeView(MobonBannerView.this);
                                            }
                                        }

                                        if (adapter.getName().toLowerCase().equals("mbadapter") || adapter.getName().toLowerCase().equals("mbmixadapter")) {

                                            final String unitId = adapter.getUnitId();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    UpdateScriptUI(adapter.getName(), unitId);
                                                }
                                            }, 10);
                                        } else
                                            loadMobonAd(data);

                                        mMediationAdView = null;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        if (mIBannerAdCallback != null)
                                            mIBannerAdCallback.onLoadedAdInfo(false, e.getLocalizedMessage());

                                        return;
                                    }
                                }

                                @Override
                                public void onAdAdapter(AdapterObject adapter) {
                                    mMediationAdView = adapter.getAdView();
                                    if (mMediationAdView != null) {
                                        LogPrint.d("onAdAdapter " + adapter.getAdapterPackageName() + " getView Not NULL!!!! ");
                                        if (mMainLayout != null)
                                            mMainLayout.removeAllViews();
                                        if (MobonBannerView.this.getParent() != null) {
                                            isParantRemove = true;
                                            ((ViewGroup) MobonBannerView.this.getParent()).removeView(MobonBannerView.this);
                                        }
                                        mMainLayout.addView((View) mMediationAdView);
/**
                                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_ad_del_btn, null);
                                        layout.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                sendNewsViewCount(1);
                                                ((RelativeLayout) getParent()).setVisibility(View.GONE);
                                                if (mIBannerAdCallback != null)
                                                    mIBannerAdCallback.onCloseClicked();
                                            }
                                        });

                                        LayoutParams params = new LayoutParams(MobonUtils.convertDpToPx(mContext, 17), MobonUtils.convertDpToPx(mContext, 17));
                                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        layout.setLayoutParams(params);
                                    **/
                                        //mMainLayout.addView((View) layout);

                                        if (mIBannerAdCallback != null)
                                            mIBannerAdCallback.onLoadedAdInfo(true, "");
                                        isParantRemove = false;
                                    } else {
                                        if (mIBannerAdCallback != null)
                                            mIBannerAdCallback.onLoadedAdInfo(false, "ERROR " + adapter.getAdapterPackageName() + " load");

                                    }
                                }

                                @Override
                                public void onAdClosed() {

                                }

                                @Override
                                public void onAdCancel() {

                                }

                                @Override
                                public void onAdFailedToLoad(String errorMsg) {
                                    if (mIBannerAdCallback != null) {
                                        mIBannerAdCallback.onLoadedAdInfo(false, MobonKey.NOFILL);
                                        mIBannerAdCallback = null;
                                    }
                                }

                                @Override
                                public void onAdImpression() {

                                }

                                @Override
                                public void onAdClicked() {
                                    if (mIBannerAdCallback != null)
                                        mIBannerAdCallback.onAdClicked();

                                }

                                @Override
                                public void onAppFinish() {

                                }
                            });

                        }
                    });
                } else {
//                    if (TextUtils.equals(errorStr, MobonKey.NOFILL)) {
//                        scriptUI(mScriptCode);
//                    }
                    synchronized (this) {

                        if (mIBannerAdCallback != null)
                            mIBannerAdCallback.onLoadedAdInfo(false, errorStr);

                        mIBannerAdCallback = null;


                    }
                }
            }
        });

    }


    private void bannerSchedule() {

        if (mScheduleHandler == null) {
            mScheduleHandler = new Handler();
        } else {
            mScheduleHandler.removeCallbacksAndMessages(mScheduleHandler);
        }

        mScheduleHandler.postDelayed(runnableCode, mInterval * 1000);
    }


    private Runnable runnableCode = new Runnable() {
        @SuppressLint("InvalidWakeLockTag")
        @Override
        public void run() {
            if (mScheduleHandler != null) {
                PowerManager powerManager = (PowerManager) mContext.getSystemService(POWER_SERVICE);
                if (viewVisible && powerManager.isScreenOn()) {
                    loadData();
                }
                if (mScheduleHandler != null)
                    mScheduleHandler.postDelayed(runnableCode, mInterval * 1000);
            }

        }


    };


    protected void updateUI(String body, final boolean isBacon) {
        if (mMainLayout == null)
            return;
        isParantRemove = false;
        try {

            if (MobonBannerView.this.getParent() != null) {
                isParantRemove = true;
                ((ViewGroup) MobonBannerView.this.getParent()).removeView(MobonBannerView.this);

            }

            mMainLayout.removeAllViews();

            if (mBannerType == null)
                mBannerType = MobonBannerType.BANNER_320x50;

            JSONArray jArray = null;

            String adGubun = "";

            if (!isBacon) {
                JSONObject obj = new JSONObject(body);
                JSONObject jObj = obj.getJSONArray("client").getJSONObject(0);
                mobonInfo = jObj.optString("mobonInfo");
                jArray = jObj.getJSONArray("data");
                adGubun = jObj.optString("target");
                CALL_AD_COUNT = jArray.length();
            }

            if (mIBannerAdCallback != null)
                mIBannerAdCallback.onLoadedAdInfo(true, "");


            isUpdateUI = true;
            isParantRemove = false;

            final JSONObject item = isBacon ? new JSONObject(body) : jArray.getJSONObject(adView_count);
            final String pcode = item.optString("pcode");
            final String s = TextUtils.isEmpty(item.optString("s")) ? mBannerSiteCode : item.optString("s");
            final String site_url = item.optString("site_url", "");
            final String user_id = item.optString("user_id");
            final String drcLink = item.optString("drc_link", "");
            String drcUrl = isBacon ? drcLink : TextUtils.isEmpty(item.optString("drcUrl")) ? item.optString("purl") : item.optString("drcUrl");

            if (isBacon) {
                Uri uri = Uri.parse(drcUrl);
                String slink = uri.getQueryParameter("slink");
                if (!TextUtils.isEmpty(slink)) {
                    drcUrl = URLDecoder.decode(slink, "UTF-8");
                }
            }

            final String purl = drcUrl;
            final String site_name = isBacon ? URLDecoder.decode(item.optString("site_title"), "UTF-8") : item.optString("site_name");
            final String site_code = isBacon ? URLDecoder.decode(item.optString("site_code"), "UTF-8") : MobonUtils.getParameter(purl, "sc");
            final String increaseViewKey = item.optString("increaseViewKey");
            final String finalAdGubun = adGubun;
            final String advrtsReplcCode = item.optString("advrtsReplcCode", "");
            final String cta_text = item.optString("cta_text", "");

            if (!TextUtils.isEmpty(increaseViewKey))
                MobonUtils.sendAdImpression(mContext, MobonUrl.API_AD_IMPRESSION + s + "/VIEW", increaseViewKey, finalAdGubun, 1);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (MobonBannerView.this.getParent() != null) {
                        ((ViewGroup) MobonBannerView.this.getParent()).setVisibility(View.VISIBLE);
                        ((ViewGroup) MobonBannerView.this.getParent()).setBackgroundResource(R.drawable.aikbd_btn_checkbox_off);
                    }
                    mMainLayout.removeAllViews();

                    if (purl.contains("adgubun=AT") && !TextUtils.isEmpty(site_code))
                        MobonUtils.setApFrequency(mContext, site_code);

                    if (TextUtils.isEmpty(pcode)) // ad
                    {
                        if (!mBannerType.equals(MobonBannerType.BANNER_300x250) && !mBannerType.equals(MobonBannerType.BANNER_600x600)) {

                            final ArrayList<String> imgArray = new ArrayList<>();

                            if (mBannerType.equals(MobonBannerType.BANNER_320x100)) {
                                imgArray.add(item.optString("mimg_320_100"));
                                imgArray.add(item.optString("mimg_300_180"));
                                imgArray.add(item.optString("mimg_640_350"));
                                imgArray.add(item.optString("mimg_300_65"));
                                imgArray.add(item.optString("mimg_250_250"));
                                imgArray.add(item.optString("img"));
                            } else {
                                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    imgArray.add(item.optString("mimg_720_120"));
                                    imgArray.add(item.optString("mimg_728_90"));
                                }
                                imgArray.add(item.optString("mimg_250_250"));
                                imgArray.add(item.optString("img"));

                            }
                            int size = imgArray.size();
                            for (int i = 0; i < size; i++) {
                                String ext = imgArray.get(i).substring(imgArray.get(i).lastIndexOf(".") + 1);

                                if (ext.equals("jpg") || ext.equals("png") || ext.equals("gif")) {
                                    if (i >= size - 2) {

                                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                            inflate(mContext, R.layout.mobon_banner_ad_layout2, mMainLayout); // possible
                                            LogPrint.d("inflate 2");

                                            String title = !TextUtils.isEmpty(item.optString("site_desc1")) ? item.optString("site_desc1") : item.optString("desc_web");
                                            if (!TextUtils.isEmpty(title)) {
                                                try {
                                                    ((TextView) mMainLayout.findViewById(R.id.t_title)).setText(isBacon ? URLDecoder.decode(title, "UTF-8") : title);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else {
                                            inflate(mContext, R.layout.mobon_banner_ad_land_layout2, mMainLayout); // possible
                                            LogPrint.d("inflate 3");
                                            if (!mBannerType.equals(MobonBannerType.BANNER_CUSTOM)) {
                                                RelativeLayout layout = mMainLayout.findViewById(R.id.root_layout);
                                                if (layout != null) {
                                                    LayoutParams lp = (LayoutParams) layout.getLayoutParams();
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                                        lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                                                    }
                                                    layout.setLayoutParams(lp);
                                                }
                                            }

                                            String title = !TextUtils.isEmpty(item.optString("pnm")) ? item.optString("pnm") : item.optString("site_name");

                                            if (!TextUtils.isEmpty(title)) {
                                                try {
                                                    ((TextView) mMainLayout.findViewById(R.id.t_title)).setText(isBacon ? URLDecoder.decode(title, "UTF-8") : title);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            String desc = !TextUtils.isEmpty(item.optString("site_desc1")) ? item.optString("site_desc1") : item.optString("desc_web");
                                            if (!TextUtils.isEmpty(desc)) {
                                                try {
                                                    ((TextView) mMainLayout.findViewById(R.id.t_desc)).setText(isBacon ? URLDecoder.decode(desc, "UTF-8") : desc);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        final ImageView mark_img = mMainLayout.findViewById(R.id.mark_img);
                                        String logo2 = item.optString("logo2");
                                        if (!TextUtils.isEmpty(logo2) && mark_img != null)
//                                            mImageModule.load(logo2).into(mark_img);
                                            try {
                                                ImageLoader.with(mContext).from(logo2).load(mark_img);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        mContentIv = mMainLayout.findViewById(R.id.t_img);
                                        final int finalI = i;

//                                        mImageModule.load(imgArray.get(finalI)).fit().centerInside().into(mContentIv);
                                        mMainLayout.setVisibility(View.VISIBLE);
                                        //ImageLoader.with(mContext).from(imgArray.get(finalI)).load(mContentIv);

                                        String imgLink = imgArray.get(finalI);
                                        LogPrint.d("imgLink :: " + imgLink);
                                        try {
                                            ImageModule.with(mContext).asBitmap().load(imgArray.get(finalI)).into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    mContentIv.setImageBitmap(resource);
                                                    mContentIv.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                    leftColor = getColorFromBitmap(resource, true);
                                                    rightColor = getColorFromBitmap(resource, false);
                                                    if (mIBannerAdCallback != null) {
                                                        mIBannerAdCallback.onBannerLoaded( leftColor, rightColor);
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {

                                        if (getResources().getConfiguration().orientation ==
                                                Configuration.ORIENTATION_PORTRAIT) {
                                            LogPrint.d("inflate 4");
                                            inflate(mContext, R.layout.mobon_banner_ad_layout, mMainLayout); // possible
                                        } else {
                                            inflate(mContext, R.layout.mobon_banner_ad_land_layout, mMainLayout); // possible
                                            LogPrint.d("inflate 5");
                                        }

                                        left_bg = mMainLayout.findViewById(R.id.left_bg);
                                        right_bg = mMainLayout.findViewById(R.id.right_bg);

                                        mContentIv = mMainLayout.findViewById(R.id.t_img);
                                        final int finalI = i;

                                        if (!mBannerType.equals(MobonBannerType.BANNER_320x50))
                                            mContentIv.setAdjustViewBounds(true);

//                                        if (mBannerBgColor != null)
//                                            setBackgroundColor(mBannerBgColor.getDefaultColor());

                                        if (mBannerScaleType == null)
                                            mBannerScaleType = "fit";

                                        mMainLayout.setVisibility(View.VISIBLE);

                                        //ImageLoader.with(mContext).from(imgArray.get(finalI)).load(mContentIv);

                                        String imgLink = imgArray.get(finalI);
                                        LogPrint.d("keyboardad mobon imgLink :: " + imgLink);
                                        /*
                                        ImageModule.with(mContext).asBitmap().load(t_url).listener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable ImageModuleException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                LogPrint.d("image load exception :: ");
                                                e.printStackTrace();
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                mContentIv.setImageBitmap(resource);
                                                mContentIv.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                int leftColor = getColorFromBitmap(resource, true);
                                                int rightColor = getColorFromBitmap(resource, false);
                                                if (mIBannerAdCallback != null) {
                                                    mIBannerAdCallback.onBannerLoaded(leftColor, rightColor);
                                                }
                                                LogPrint.d("leftColor :: " + leftColor + " , rightColor :: " + rightColor);

                                                if ( left_bg != null && right_bg != null ) {
                                                    left_bg.setBackgroundColor(leftColor);
                                                    right_bg.setBackgroundColor(rightColor);
                                                }
                                                return false;
                                            }
                                        });*/

                                        try {
                                            ImageModule.with(mContext).asBitmap().load(imgArray.get(finalI)).into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    mContentIv.setImageBitmap(resource);
                                                    mContentIv.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                    int leftColor = getColorFromBitmap(resource, true);
                                                    int rightColor = getColorFromBitmap(resource, false);
                                                    if (mIBannerAdCallback != null) {
                                                        mIBannerAdCallback.onBannerLoaded(leftColor, rightColor);
                                                    }
                                                    LogPrint.d("keyboardad mobon leftColor :: " + leftColor + " , rightColor :: " + rightColor);

                                                    if ( left_bg != null && right_bg != null ) {
                                                        left_bg.setBackgroundColor(leftColor);
                                                        right_bg.setBackgroundColor(rightColor);
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                                }
                            }
                        } else {
                            final ArrayList<String> imgArray = new ArrayList<>();

                            if (mBannerType.equals(MobonBannerType.BANNER_300x250)) {
                                imgArray.add(item.optString("mimg_300_250"));
                                imgArray.add(item.optString("mimg_640_350"));
                                imgArray.add(item.optString("mimg_850_800"));
                                imgArray.add(item.optString("mimg_250_250"));
                                imgArray.add(item.optString("img"));
                            } else {
                                imgArray.add(item.optString("mimg_850_800"));
                                imgArray.add(item.optString("mimg_250_250"));
                                imgArray.add(item.optString("img"));
                                imgArray.add(item.optString("img"));
                            }

                            for (int i = 0; i < imgArray.size(); i++) {
                                String ext = imgArray.get(i).substring(imgArray.get(i).lastIndexOf(".") + 1);
                                if (ext.equals("jpg") || ext.equals("png") || ext.equals("gif")) {
                                    if (mBannerType.equals(MobonBannerType.BANNER_300x250)) {
                                        LogPrint.d("inflate 6");
                                        inflate(mContext, R.layout.mobon_banner_ad_300_250_layout, mMainLayout); // not use
                                    } else {
                                        LogPrint.d("inflate 7");
                                        inflate(mContext, R.layout.mobon_banner_ad_600_600_layout, mMainLayout); // not use
                                    }


                                    mContentIv = mMainLayout.findViewById(R.id.t_img);
                                    final int finalI = i;
//                                    mImageModule.load(imgArray.get(finalI)).fit().centerCrop().into(mContentIv);
                                    //ImageLoader.with(mContext).from(imgArray.get(finalI)).load(mContentIv);
                                    String imgLink = imgArray.get(finalI);
                                    LogPrint.d("keyboardad mobon imgLink :: " + imgLink);
                                    try {
                                        ImageModule.with(mContext).asBitmap().load(imgArray.get(finalI)).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                mContentIv.setImageBitmap(resource);
                                                mContentIv.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                int leftColor = getColorFromBitmap(resource, true);
                                                int rightColor = getColorFromBitmap(resource, false);
                                                if (mIBannerAdCallback != null) {
                                                    mIBannerAdCallback.onBannerLoaded(leftColor, rightColor);
                                                }
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    mMainLayout.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                        }
                    } else { // 타게팅일 때
                        if (mBannerType.equals(MobonBannerType.BANNER_300x250)) {
                            LogPrint.d("inflate 8");
                            inflate(mContext, R.layout.mobon_banner_sr_300_250_layout, mMainLayout); // not
                        } else if (mBannerType.equals(MobonBannerType.BANNER_600x600)) {
                            LogPrint.d("inflate 9");
                            inflate(mContext, R.layout.mobon_banner_sr_600_600_layout, mMainLayout); // not
                        } else {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                if (mBannerType.equals(MobonBannerType.BANNER_320x100)) {
                                    LogPrint.d("inflate 10");
                                    inflate(mContext, Banner_100_Layout_ids[new Random().nextInt(Banner_100_Layout_ids.length)], mMainLayout); // not
                                } else {
                                    LogPrint.d("inflate 11");
                                    // inflate(mContext, Banner_50_Layout_ids[new Random().nextInt(Banner_50_Layout_ids.length)], mMainLayout); // 타게팅 세로일 경우 ui
                                    inflate(mContext, R.layout.mobon_banner_50_theme5_red_layout, mMainLayout); // possible
                                    leftColor = Color.parseColor("#e06473");
                                    rightColor = Color.parseColor("#e06473");
                                    if (mIBannerAdCallback != null) {
                                        mIBannerAdCallback.onBannerLoaded(leftColor, rightColor);
                                    }
                                }
                            } else {
                                LogPrint.d("inflate 12");
                                // inflate(mContext, Banner_50_Land_Layout_ids[new Random().nextInt(Banner_50_Land_Layout_ids.length)], mMainLayout); // not use
                                inflate(mContext, R.layout.mobon_banner_sr_land_layout, mMainLayout); // use 타게팅 가로일 경우

                                if (!mBannerType.equals(MobonBannerType.BANNER_CUSTOM)) {
                                    RelativeLayout layout = mMainLayout.findViewById(R.id.root_layout);
                                    try {
                                        if (layout != null) {
                                            LayoutParams lp = (LayoutParams) layout.getLayoutParams();
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                                lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                                            }
                                            layout.setLayoutParams(lp);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }

                        mContentIv = (ImageView) mMainLayout.findViewById(R.id.t_img);
                        mTitle = (TextView) mMainLayout.findViewById(R.id.t_title);
                        mPrice = (TextView) mMainLayout.findViewById(R.id.t_price);
                        final ImageView mark_img = (ImageView) mMainLayout.findViewById(R.id.mark_img);
                        final ImageView blur_img = (ImageView) mMainLayout.findViewById(R.id.blur_img);
                        image_layout = mMainLayout.findViewById(R.id.image_layout);

                        final String title = item.optString("pnm");
                        final String price = item.optString("price");


                        if (mBannerType.equals(MobonBannerType.BANNER_CUSTOM)) {
                            mTitle.setMaxLines(1);
                            mTitle.setSingleLine(true);
                        }

                        mTitle.setText(title);

                        if (TextUtils.equals(advrtsReplcCode, "02") || TextUtils.equals(advrtsReplcCode, "2")) {

                            ViewGroup.LayoutParams params = mPrice.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                            params.height = MobonUtils.convertDpToPx(mContext, 9);
                            mPrice.setLayoutParams(params);
                            mPrice.setGravity(Gravity.CENTER);
                            mPrice.setTextColor(Color.WHITE);
                            mPrice.setText(cta_text);
                            mPrice.setTextSize(MobonUtils.convertDpToPx(mContext, 3));
                            mPrice.setBackground(getResources().getDrawable(R.drawable.mobon_round_banner_text));

                        } else {
                            if (!TextUtils.isEmpty(price) && mPrice != null)
                                mPrice.setText(MobonUtils.getCommaNumeric(price) + "원");
                        }

                        String logo2 = item.optString("logo2");

                        if (!TextUtils.isEmpty(logo2) && mark_img != null) {
//                            mImageModule.load(logo2).into(mark_img);
                            try {
                                ImageLoader.with(mContext).from(logo2).load(mark_img);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (mark_img != null)
                            mark_img.setVisibility(View.GONE);

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                final int default_banner_height = getResources().getDimensionPixelOffset(R.dimen.mobon_banner_height_50);
                                int layout_height = mMainLayout.getHeight();
                                if (!mBannerType.equals(MobonBannerType.BANNER_300x250) && !mBannerType.equals(MobonBannerType.BANNER_600x600)) {
                                    if (layout_height < default_banner_height) {
//                                        mImageModule.load(item.optString("img")).fit().centerCrop().into(mContentIv);
                                        //ImageLoader.with(mContext).from(item.optString("img")).load(mContentIv);

                                        String imgLink = item.optString("img");
                                        LogPrint.d("keyboardad mobon imgLink :: " + imgLink);
                                        try {
                                            ImageModule.with(mContext).asBitmap().load(item.optString("img")).into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    mContentIv.setImageBitmap(resource);
                                                    mContentIv.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                    int leftColor = getColorFromBitmap(resource, true);
                                                    int rightColor = getColorFromBitmap(resource, false);
                                                    if (mIBannerAdCallback != null) {
                                                        mIBannerAdCallback.onBannerLoaded(leftColor, rightColor);
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        mMainLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        image_layout = mMainLayout.findViewById(R.id.image_layout);
                                        if (image_layout != null && image_layout.getLayoutParams() != null) {

                                            ViewGroup.LayoutParams layoutParams = image_layout.getLayoutParams();
                                            if (layoutParams instanceof LayoutParams) {
                                                LayoutParams params = (LayoutParams)
                                                        image_layout.getLayoutParams();

                                                int layoutHeight = MobonUtils.convertDpToPx(mContext, mXmlHeight);
                                                if (layoutHeight < 1) {
                                                    layoutHeight = mBannerType.equals(MobonBannerType.BANNER_320x100) ? getResources().getDimensionPixelOffset(R.dimen.mobon_banner_height_100) : default_banner_height;
                                                }
                                                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && mBannerType.equals(MobonBannerType.BANNER_CUSTOM)) {
                                                    params.width = params.height = layoutHeight - params.topMargin;
                                                    params.setMargins(2, 2, 2, 2);

                                                    mTitle.post(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            Rect realSize = new Rect();
                                                            mTitle.getPaint().getTextBounds(mTitle.getText().toString(), 0, mTitle.getText().length(), realSize);
                                                            LinearLayout content_layout = mMainLayout.findViewById(R.id.t_contents);
                                                            if (content_layout != null && content_layout.getLayoutParams() != null) {
                                                                ViewGroup.LayoutParams layoutParams = content_layout.getLayoutParams();
                                                                if (layoutParams instanceof LayoutParams) {
                                                                    LayoutParams params3 = (LayoutParams)
                                                                            content_layout.getLayoutParams();
                                                                    params3.width = realSize.width() + params3.leftMargin + params3.rightMargin;
                                                                    content_layout.setLayoutParams(params3);
                                                                }
                                                            }
                                                        }
                                                    });

                                                } else
                                                    params.width = mContentIv.getHeight() < 1 ? layoutHeight - params.topMargin * 2 : mContentIv.getHeight();
                                                image_layout.setLayoutParams(params);
                                            }
                                        }

//                                        mImageModule.load(item.optString("img")).fit().centerCrop().into(mContentIv, new Callback() {
//                                            @Override
//                                            public void onSuccess() {
//                                                mImageModule.load(item.optString("img")).fit().centerCrop().into(mContentIv);
//                                            }
//
//                                            @Override
//                                            public void onError(Throwable t) {
//                                                mImageModule.load(item.optString("img")).fit().centerCrop().into(mContentIv);
//                                            }
//                                        });
                                        //ImageLoader.with(mContext).from(item.optString("img")).load(mContentIv);

                                        String imgLink = item.optString("img");
                                        LogPrint.d("imgLink :: " + imgLink);
                                        try {
                                            ImageModule.with(mContext).asBitmap().load(item.optString("img")).into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    mContentIv.setImageBitmap(resource);
                                                    mContentIv.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                    int leftColor = getColorFromBitmap(resource, true);
                                                    int rightColor = getColorFromBitmap(resource, false);
                                                    if (mIBannerAdCallback != null) {
                                                        mIBannerAdCallback.onBannerLoaded(leftColor, rightColor);
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        mMainLayout.setVisibility(View.VISIBLE);
                                    }
                                } else {
//                                    mImageModule.load(item.optString("img")).fit().centerCrop().into(mContentIv);
                                    //ImageLoader.with(mContext).from(item.optString("img")).load(mContentIv);
                                    try {
                                        ImageModule.with(mContext).asBitmap().load(item.optString("img")).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                mContentIv.setImageBitmap(resource);
                                                mContentIv.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                int leftColor = getColorFromBitmap(resource, true);
                                                int rightColor = getColorFromBitmap(resource, false);
                                                if (mIBannerAdCallback != null) {
                                                    mIBannerAdCallback.onBannerLoaded(leftColor, rightColor);
                                                }
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    mMainLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        });


                    }

                    mMobonMark = (ImageView) mMainLayout.findViewById(R.id.mobon_mark);
                    if (mMobonMark != null) {
                        if (!TextUtils.isEmpty(mobonInfo)) {
                            mMobonMark.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MobonUtils.getBrowserPackageName(mContext, mobonInfo, false);
                                }
                            });
                        }
                    }

                    ad_del = (RelativeLayout) mMainLayout.findViewById(R.id.ad_del);
                    if ( ad_del != null ) {
                        if ( isReward )
                            ad_del.setVisibility(View.GONE);
                        else
                            ad_del.setVisibility(View.VISIBLE);

                        ad_del.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ViewParent vp = getParent();
                                if (vp != null) {
                                    ViewParent vpp = vp.getParent();
                                    if ( vpp != null ) {
                                        ((ViewGroup) vpp).setVisibility(View.GONE);
                                        if (mIBannerAdCallback != null)
                                            mIBannerAdCallback.onCloseClicked();
                                    }
                                }
                            }
                        });
                    }
/**
                    RelativeLayout kl_del = (RelativeLayout) mMainLayout.findViewById(R.id.kl_del);
                    if (kl_del != null) {
                        kl_del.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getParent() != null)
                                    ((ViewGroup) getParent()).setVisibility(View.GONE);
                                sendNewsViewCount(2);
                                if (mIBannerAdCallback != null)
                                    mIBannerAdCallback.onCloseClicked();
                            }
                        });
                    }
**/
                    mMainLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            LogPrint.d("keyboardad landing url : " + purl + " , isReward :: " + isReward);

                            long curTime = System.currentTimeMillis();

                            if (curTime - mClickedTime < 1000) {
                                LogPrint.d("ad double click !!!");
                                return;
                            }
                            mClickedTime = curTime;
                            LogPrint.d("mobon banner clicked 2-2");

                            if (mIBannerAdCallback != null) {
                                if ( isReward )
                                    MobonUtils.setWebViewOpen(mContext, purl, false);
                                else
                                    MobonUtils.getBrowserPackageName(mContext, purl, false);
                                mIBannerAdCallback.onAdClicked();
                            }

                        }
                    });


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogPrint.d("ERROR => " + e.toString());
        }
    }

    private Messenger mServiceCallback = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceCallback = new Messenger(service);
            // connect to service
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceCallback = null;
        }
    };


    public void destroyAd() {

        mIBannerAdCallback = null;
        if (mScheduleHandler != null) {
            mScheduleHandler.removeCallbacksAndMessages(mScheduleHandler);
            mScheduleHandler = null;
        }

    }

    public MobonBannerView setInterval(int _second) {
        mInterval = _second;
        return this;
    }


    public void setAdListener(iSimpleMobonBannerCallback pMobonAdCallback) {
        mIBannerAdCallback = pMobonAdCallback;
    }

    public void setDelListener(iDelClickCallback delclickCallback) {
        mDelClickCallback = delclickCallback;
    }


    public void onDestroy() {
        if (adapterObject != null) {
            adapterObject.onDestory();
            mMediationAdView = null;
        }

        if (mMainLayout != null)
            mMainLayout.removeAllViews();

        if (mWebview != null)
            mWebview = null;
    }

    /*
    public void loadMediationAd(JSONObject list) {
        onInit();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mediationManager = new MediationManager(mContext, list, mBannerType);
                mediationManager.LoadMediation(new iMobonMediationCallback() {
                    @Override
                    public void onLoadedAdData(String data, AdapterObject adapter) {
                        try {

                            if (mMainLayout != null)
                                mMainLayout.removeAllViews();
                            if (MobonBannerView.this.getParent() != null) {
                                isParantRemove = true;
                                ((ViewGroup) MobonBannerView.this.getParent()).removeView(MobonBannerView.this);
                            }

                            if (adapter.getName().toLowerCase().equals("mbadapter") || adapter.getName().toLowerCase().equals("mbmixadapter")) {
                                final String unitId = adapter.getUnitId();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //UpdateMobonScriptUI(unitId);
                                    }
                                }, 10);
                            } else
                                loadMobonAd(data);

                            mMediationAdView = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mIBannerAdCallback != null)
                                mIBannerAdCallback.onLoadedAdInfo(false, e.getLocalizedMessage());

                            return;
                        }

                    }

                    @Override
                    public void onAdAdapter(AdapterObject adapter) {
                        adapterObject = adapter;
                        mMediationAdView = adapter.getAdView();
                        if (mMediationAdView != null) {
                            if (mMainLayout != null)
                                mMainLayout.removeAllViews();
                            if (MobonBannerView.this.getParent() != null) {
                                isParantRemove = true;
                                ((ViewGroup) MobonBannerView.this.getParent()).removeView(MobonBannerView.this);
                            }

                            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_ad_del_btn, null);
                            layout.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((RelativeLayout) getParent()).setVisibility(View.GONE);
                                }
                            });

                            LayoutParams params = new LayoutParams(MobonUtils.convertDpToPx(mContext, 17), MobonUtils.convertDpToPx(mContext, 17));
                            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            layout.setLayoutParams(params);
                            mMainLayout.addView((View) mMediationAdView);
                            mMainLayout.addView((View) layout);
                            if (mIBannerAdCallback != null)
                                mIBannerAdCallback.onLoadedAdInfo(true, "");
                            isParantRemove = false;
                        } else {
                            if (mIBannerAdCallback != null)
                                mIBannerAdCallback.onLoadedAdInfo(false, "ERROR " + adapter.getAdapterPackageName() + " load");

                        }
                    }

                    @Override
                    public void onAdClosed() {

                    }

                    @Override
                    public void onAdCancel() {

                    }

                    @Override
                    public void onAdFailedToLoad(String errorMsg) {
                        if (mIBannerAdCallback != null) {
                            mIBannerAdCallback.onLoadedAdInfo(false, MobonKey.NOFILL);
                            mIBannerAdCallback = null;
                        }

                    }

                    @Override
                    public void onAdImpression() {

                    }

                    @Override
                    public void onAdClicked() {
                        if (mIBannerAdCallback != null)
                            mIBannerAdCallback.onAdClicked();

                    }

                    @Override
                    public void onAppFinish() {

                    }
                });

            }
        });


    }*/


    private boolean isCookieInit = false;

    private void UpdateScriptUI(final String type, final String scriptCode) {

        if (mMainLayout == null) {
            if (mIBannerAdCallback != null)
                mIBannerAdCallback.onLoadedAdInfo(false, MobonKey.NOFILL);
            return;
        }
        try {

            if (MobonBannerView.this.getParent() != null) {
                isParantRemove = true;
                ((ViewGroup) MobonBannerView.this.getParent()).removeView(MobonBannerView.this);
            }

            mMainLayout.removeAllViews();

            final CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.getCookie("https://mediacategory.com");
            if (!isCookieInit)
                cookieManager.setCookie("https://mediacategory.com", "");
            isCookieInit = true;

            mMainLayout.setVisibility(View.INVISIBLE);


            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    inflate(mContext, R.layout.banner_script_layout, mMainLayout);
                    LogPrint.d("inflate 1");
                    mMainLayout.setGravity(Gravity.CENTER);
                    mWebview = mMainLayout.findViewById(R.id.webview);
                    dummy_image = mMainLayout.findViewById(R.id.dummy_image);
                    left_bg = mMainLayout.findViewById(R.id.left_bg);
                    right_bg = mMainLayout.findViewById(R.id.right_bg);

                    WebSettings settings = mWebview.getSettings();
                    settings.setJavaScriptEnabled(true);
                    settings.setBuiltInZoomControls(false);
                    settings.setJavaScriptCanOpenWindowsAutomatically(true);
                    settings.setSupportMultipleWindows(true);

                    // 웹뷰 - HTML5 창 속성 추가
                    String path = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
                    settings.setDatabaseEnabled(true);
                    settings.setDatabasePath(path);
                    settings.setDomStorageEnabled(true);
                    settings.setBlockNetworkLoads(false);
                    settings.setAllowFileAccess(false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// https 이미지.
                        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    } else
                        mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);


                    mWebview.setVerticalScrollBarEnabled(false);

                    //   mWebview.setBackgroundColor(Color.TRANSPARENT);

                    if (isExtractColor)
                        mWebview.setDrawingCacheEnabled(true);

                    ad_del = (RelativeLayout) mMainLayout.findViewById(R.id.ad_del);
                    if ( ad_del != null ) {
                        if ( isReward )
                            ad_del.setVisibility(View.GONE);
                        else
                            ad_del.setVisibility(View.VISIBLE);

                        ad_del.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ViewParent vp = getParent();
                                if (vp != null) {
                                    ViewParent vpp = vp.getParent();
                                    if ( vpp != null ) {
                                        ((ViewGroup) vpp).setVisibility(View.GONE);
                                        if (mIBannerAdCallback != null)
                                            mIBannerAdCallback.onCloseClicked();
                                    }
                                }
                            }
                        });
                    }
/**
                    RelativeLayout kl_del = (RelativeLayout) mMainLayout.findViewById(R.id.kl_del);
                    if (kl_del != null) {
                        kl_del.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getParent() != null)
                                    ((ViewGroup) getParent()).setVisibility(View.GONE);
                                sendNewsViewCount(3);
                                if (mIBannerAdCallback != null)
                                    mIBannerAdCallback.onCloseClicked();
                            }
                        });
                    }
**/
                    mWebview.setOnTouchListener(new OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            return (event.getAction() == MotionEvent.ACTION_MOVE);
                        }
                    });

                    mWebview.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                            WebView newWebView = new WebView(mContext);
                            view.addView(newWebView);
                            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                            transport.setWebView(newWebView);
                            resultMsg.sendToTarget();

                            newWebView.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                    if (url.contains("mediacategory.com"))
                                        url += "&au_id=" + SPManager.getString(mContext, MobonKey.AUID);
                                    browserIntent.setData(Uri.parse(url));
                                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    try {
                                        mContext.startActivity(browserIntent);
                                        if (mIBannerAdCallback != null)
                                            mIBannerAdCallback.onAdClicked();
                                        if(mWebview != null)
                                            mWebview.loadUrl("javascript:mixerClickFn();");
                                    } catch (ActivityNotFoundException e) {
                                        mContext.startActivity(Intent.createChooser(browserIntent, "Title"));
                                    }

                                    if (url.contains("//img.mobon.net/ad/linfo.php"))
                                        mWebview.goBack();
                                    return true;
                                }
                            });
                            return true;
                        }

                        @Override
                        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                            if ((message.contains("Uncaught SyntaxError:") || message.contains("Uncaught ReferenceError:") || message.equals("AdapterFailCallback_" + mScriptNo)) && !message.contains("wp_json")) {
                                if (mWebview == null)
                                    return;
                                else {
                                    mWebview.onPause();
                                    mWebview = null;
                                }
                                mMainLayout.removeAllViews();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean nextMediation = false;
                                        if (mediationManager != null) {
                                            nextMediation = mediationManager.next();
                                        }

                                        if (!nextMediation) {
                                            if (mIBannerAdCallback != null)
                                                mIBannerAdCallback.onLoadedAdInfo(false, MobonKey.NOFILL);

                                        }
                                    }
                                }, 1000);

                            } else if (message.equals("AdapterSuccessCallback_" + mScriptNo)) {
                                if (mWebview != null) {
                                    if (mIBannerAdCallback != null)
                                        mIBannerAdCallback.onLoadedAdInfo(true, "");
                                }
                            }

                        }
                    });


                    mWebview.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            if (url.contains("mediacategory.com"))
                                url += "&au_id=" + SPManager.getString(mContext, MobonKey.AUID);
                            browserIntent.setData(Uri.parse(url));
                            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                mContext.startActivity(browserIntent);
                                if (mIBannerAdCallback != null)
                                    mIBannerAdCallback.onAdClicked();
                                if(mWebview != null)
                                    mWebview.loadUrl("javascript:mixerClickFn();");
                                return true;

                            } catch (ActivityNotFoundException e) {
                                mContext.startActivity(Intent.createChooser(browserIntent, "Title"));
                            }
                            return super.shouldOverrideUrlLoading(view, url);
                        }

                        @Override
                        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError
                                error) {
                            //super.onReceivedSslError(view, handler, error);
                            handler.cancel();

                            LogPrint.d("!!!!!!!!!!!!!!!!!!!!!!! keyboardad mobon onReceivedSslError : ");

                            if (mWebview == null)
                                return;
                            else {
                                mWebview.onPause();
                                mWebview = null;
                            }

                            boolean nextMediation = false;

                            mMainLayout.removeAllViews();

                            if (mediationManager != null) {
                                nextMediation = mediationManager.next();
                            }

                            if (!nextMediation) {

                                if (mIBannerAdCallback != null)
                                    mIBannerAdCallback.onLoadedAdInfo(false, MobonKey.NOFILL);

                            }
                        }

                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest
                                request, WebResourceError error) {
                            super.onReceivedError(view, request, error);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (error.getErrorCode() == -1)
                                    return;
                            }

                            mMainLayout.removeAllViews();

                            if (mWebview == null)
                                return;
                            else {
                                mWebview.onPause();
                                mWebview.destroy();
                                mWebview = null;
                            }

                            view.loadUrl("about:blank");
                            boolean nextMediation = false;
                            if (mediationManager != null) {
                                // String mediationName = mediationManager.getCurMediationName();
                                nextMediation = mediationManager.next();
                            }

                            if (!nextMediation) {

                                if (mIBannerAdCallback != null)
                                    mIBannerAdCallback.onLoadedAdInfo(false, MobonKey.NOFILL);

                            }

                        }

                        @Override
                        public void onPageFinished(final WebView view, String url) {
                            //  view.setVisibility(View.VISIBLE);
                            LogPrint.d("keyboardad mobon sb onPageFinished url :: " + url);
                            if (url.contains("/servlet/auid")) {
                                if (type.equals("mbadapter"))
                                    loadMobonScript(mWebview, scriptCode);
                                else
                                    loadSSPScript(mWebview, scriptCode);


                            } else {

                                mMainLayout.setVisibility(View.VISIBLE);

                                if (MobonBannerView.this.getParent() != null) {
                                    ((ViewGroup) MobonBannerView.this.getParent()).setBackgroundColor(Color.WHITE);
                                }
                            }
                        }
                    });

                    if (Build.VERSION.SDK_INT >= 21) {
                        cookieManager.setAcceptThirdPartyCookies(mWebview, true);
                    }

                    if(type.equals("coupang")){
                        loadCoupangScript(mWebview);
                        return;
                    }

                    String mediacategoryCookie = cookieManager.getCookie("https://mediacategory.com");
                    String au_id = SPManager.getString(mContext, MobonKey.AUID);

                    if (TextUtils.isEmpty(mediacategoryCookie) || TextUtils.isEmpty(au_id) || (mediacategoryCookie != null && !mediacategoryCookie.contains(au_id) || !mediacategoryCookie.contains(au_id))) {
                        // cookieManager.removeAllCookie();
                        mMainLayout.setVisibility(View.INVISIBLE);
                        String url = "https://www.mediacategory.com/servlet/auid?adid=" + SPManager.getString(mContext, MobonKey.ADID);
                        mWebview.loadUrl(url);
                    } else {
                        //  mMainLayout.removeAllViews();

                        if (type.equals("mbadapter"))
                            loadMobonScript(mWebview, scriptCode);
                        else
                            loadSSPScript(mWebview, scriptCode);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogPrint.d("error => " + e.toString());
        }


    }

    private int drawCount;

    private void drawScriptBg(final View view) {

        if (++drawCount > 50) {
            mMainLayout.setVisibility(View.VISIBLE);
            return;
        }
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bitmap cache = view.getDrawingCache();
                    if (cache != null) {
                        Bitmap bitmap = Bitmap.createBitmap(cache);
                        int color = MobonUtils.setPalette(bitmap);
                        if (color != 0xFFFFFF) {
                            ((View) view.getParent()).setBackgroundColor(color);
                            mMainLayout.setVisibility(View.VISIBLE);
                        } else {
                            drawScriptBg(view);
                        }
                        bitmap.recycle();
                    } else {
                        drawScriptBg(view);
                    }
                }
            }, 50);


        } catch (Exception e) {
            e.printStackTrace();
            mMainLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadSSPScript(WebView wv, final String code) {

        String[] info = code.split("_");

        if (info.length < 2 || wv == null) {
            boolean nextMediation = false;
            if (mediationManager != null) {
                nextMediation = mediationManager.next();
            }

            if (!nextMediation) {
                mMainLayout.removeAllViews();
                if (mIBannerAdCallback != null)
                    mIBannerAdCallback.onLoadedAdInfo(false, MobonKey.NOFILL);

            }
            return;
        }

        mScriptNo = info[0];
        String htmlContent = "<!DOCTYPE html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Direct SDK Script</title>" +
                "</head>" +
                "<body style='margin:0px;padding:0px;'>" +
                "<script src=\"https://mixer.mobon.net/js/sspScript.min.js\" type=\"text/javascript\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "    (function(){\n" +
                "        document.write(\"<div id='sspScript_div'></div>\");\n" +
                "        const sspId = \"" + code + "\";\n" +
                "        const sspNo = \"" + info[0] + "\";\n" +
                "        const sspWidth = \"" + info[1] + "\";\n" +
                "        const sspHeight = \"" + info[2] + "\";\n" +
                "        const sspHref = window.location.href;\n" +
                "        const sspVer = \"" + MobonUtils.getAppVersion(mContext) + "\";\n" +
                "        const sspTelecom = \"SKT\";\n" +
                "        const sspGaid = \"" + MobonUtils.getAdid(mContext) + "\";\n" +
                "        sspScriptAppFn(sspNo,sspWidth,sspHeight,sspHref,sspVer,sspTelecom,sspGaid);\n" +
                "    })();\n" +
                "</script></body></html>";

        if(wv != null)
        wv.loadDataWithBaseURL("https://mediacategory.com", htmlContent, "text/html; charset=utf-8", "UTF-8", null);
        //  wv.scrollBy(0, Utils.convertDpToPx(mContext, 8));

        try {
            if ( wv == null )
                return;
            Picture picture = wv.capturePicture();
            Bitmap  b = Bitmap.createBitmap( convertDpToPx(mContext, 320),
                    convertDpToPx(mContext, 50), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas( b );
            picture.draw( c );
            if ( dummy_image != null && b != null ) {
                dummy_image.setImageBitmap(b);
                int leftColor = GetColors(b, true);
                int rightColor = GetColors(b, false);
                if ( left_bg != null && right_bg != null ) {
                    left_bg.setBackgroundColor(leftColor);
                    right_bg.setBackgroundColor(rightColor);
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mWebview != null) {
                    if (mIBannerAdCallback != null)
                        mIBannerAdCallback.onLoadedAdInfo(true, "");
                }
            }
        }, 100);
        */

    }

    private void loadMobonScript(WebView wv, final String scriptCode) {

        mScriptNo = scriptCode;

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Direct SDK Script</title>" +
                "</head>" +
                "<body style='margin:0px;padding:0px;'>" +
                "<script src=\"https://code.jquery.com/jquery-1.11.1.js\"></script>" +
                "<script src=\"https://code.jquery.com/ui/1.11.1/jquery-ui.js\"></script>" +
                "<script>" +
                "document.write(\"<div id='mobonDivBanner_477519'><iframe name='ifrad' id='mobonIframe_477519' src='https://www.mediacategory.com/servlet/adbnMobileBanner?from=\"+escape(document.referrer)+\"&s=" + scriptCode + "&iwh=320_50&bntype=37&cntad=5&cntsr=5&useAdapt=Y" + MobonUtils.getManPlusParam(mContext) + "' frameborder='0' scrolling='no' style='height:100%; width:100%;'></iframe></div>\"); " +
                "" +
                "    $(document).ready(function(){" +
                "   $('html, body').css({'overflow': 'hidden', 'height': '100%'});" +
                "        function getCookie(name) {" +
                "            var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');" +
                "            return value? value[2] : null;" +
                "        }" +
                "        var cookie = getCookie('au_id');" +
                "        var newJ =$.parseJSON(wp_json);" +
                "        $.each(newJ.client[0].data,function(i,obj){" +
                "            function purlCookie() {" +
                "                obj.purl + '?' + cookie;" +
                "            }" +
                "            purlCookie();" +
                "            i++;" +
                "        });" +
                "    });" +
                "</script>" +
                "</body>" +
                "</html>";


        wv.loadDataWithBaseURL("https://mediacategory.com", htmlContent, "text/html; charset=utf-8", "UTF-8", null);

        try {
            if ( wv == null )
                return;
            Picture picture = wv.capturePicture();
            Bitmap  b = Bitmap.createBitmap( convertDpToPx(mContext, 320),
                    convertDpToPx(mContext, 50), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas( b );
            picture.draw( c );
            if ( dummy_image != null && b != null ) {
                LogPrint.d("kksskk dummy_image not null");
                dummy_image.setImageBitmap(b);
                int leftColor = GetColors(b, true);
                int rightColor = GetColors(b, false);
                LogPrint.d("kksskk leftColor :: " + leftColor + " , rightColor :: " + rightColor);
                if ( left_bg != null && right_bg != null ) {
                    left_bg.setBackgroundColor(leftColor);
                    right_bg.setBackgroundColor(rightColor);
                }
            } else {
                LogPrint.d("kksskk dummy_image null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mIBannerAdCallback != null)
            mIBannerAdCallback.onLoadedAdInfo(true, "");

    }

    private void loadCoupangScript(WebView wv) {

        mScriptNo = "";
/**
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Direct SDK Script</title>" +
                "</head>" +
                "<body style='margin:0px;padding:0px;'>" +
                "<script src=\"https://ads-partners.coupang.com/g.js\"></script>\n" +
                "<script>" +
                "new PartnersCoupang.G({\"id\":511600,\"template\":\"carousel\",\"trackingCode\":\"AF6919774\",\"width\":\"320\",\"height\":\"50\"});" +
                "</script>" +
                "</body>" +
                "</html>";
**/
        String c_adid = SPManager.getString(mContext, MobonKey.ADID);
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Direct SDK Script</title>" +
                "</head>" +
                "<body style='margin:0px;padding:0px;'>" +
                "<script src=\"https://ads-partners.coupang.com/g.js\"></script>\n" +
                "<script>\n" +
                "\tnew PartnersCoupang.G({\"id\":511600,\"template\":\"carousel\",\"trackingCode\":\"AF6919774\",\"subId\":\"ocbdynamic\",\"width\":\"320\",\"height\":\"50\",\"deviceId\":\"" + c_adid  + "\"});\n" +
                "</script>" +
                "</body>" +
                "</html>";

        wv.loadDataWithBaseURL("https://mediacategory.com", htmlContent, "text/html; charset=utf-8", "UTF-8", null);
        // 쿠팡 다이나믹 배너 노출카운트 올림
        sendDynamicBannerViewCount();

        if (mIBannerAdCallback != null)
            mIBannerAdCallback.onLoadedAdInfo(true, "");

    }


    @Override
    public int getDescendantFocusability() {
        return super.getDescendantFocusability();
    }

    private void sendDynamicBannerViewCount() {
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.postStats("dynamic_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
            }
        });
    }

    public void bannerClick() {
        LogPrint.d("bannerClick");
        if ( mMainLayout != null ) {
            LogPrint.d("bannerClick performClick");
            mMainLayout.performClick();
        }

        if ( mWebview != null ) {
            LogPrint.d("bannerClick webview not null");
            MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, convertDpToPx(mContext, 160), convertDpToPx(mContext, 25), 0);
            MotionEvent event2 = MotionEvent.obtain(SystemClock.uptimeMillis() + 10, SystemClock.uptimeMillis() + 10, MotionEvent.ACTION_UP, 100, 100, 0);
            mWebview.dispatchTouchEvent(event);
            mWebview.dispatchTouchEvent(event2);
        }
    }

    private int getColorFromBitmap(Bitmap bm, boolean isLeft) {
        /*if ( !isReward )
            return Color.WHITE;*/
        int color = Color.WHITE;
        int bitmapWidth = bm.getWidth();
        int bitmapHeight = bm.getHeight();
        ArrayList<Integer> colorArray = new ArrayList<>();
        if ( isLeft ) {
            for ( int i = 0 ; i < 2 ; i ++ ) {
                for ( int j = 0 ; j < bitmapHeight ; j ++ ) {
                    int pixelColor = bm.getPixel(i,j);
                    colorArray.add(pixelColor);
                }
            }
        } else {
            for ( int i = bitmapWidth - 2 ; i < bitmapWidth ; i ++ ) {
                for ( int j = 0 ; j < bitmapHeight ; j ++ ) {
                    int pixelColor = bm.getPixel(i,j);
                    colorArray.add(pixelColor);
                }
            }
        }
        int max = 0;
        Set<Integer> set = new HashSet<Integer>(colorArray);
        for ( int val : set) {
            int num = Collections.frequency(colorArray, val);
            if ( max < num ) {
                max = num;
                color = val;
            }
        }
        return color;
    }
}