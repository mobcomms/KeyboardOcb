package com.enliple.keyboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.AIKBD_DBHelper;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.KeyboardUserIdModel;
import com.enliple.keyboard.common.PopADModel;
import com.enliple.keyboard.common.ProgressWheel;
import com.enliple.keyboard.common.UserIdDBHelper;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.network.Url;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018-04-26.
 */

public class KeyboardAditionADActivity extends Activity {


    private RelativeLayout mADTTLLayer, mNPLayer, mTopLayer;
    private TextView mNPTitle = null;
    private TextView mPopTitle = null;
    private TextView mPopSep = null;
    private TextView mPopPrice = null;
    private RelativeLayout mPagerLayer = null;
    private RelativeLayout mADPointBackground = null;
    private VerticalPager mPager = null;
    private TextView mKeyboardPoint = null;
    private TextView mAdPoint = null;
    private TextView mAdPointTitle = null;
    private View mPagerView = null;
    private RelativeLayout mTopArrow = null;
    private RelativeLayout mBottomArrow = null;
    private RelativeLayout mPopDelBtn = null;
    private ProgressWheel mProgress;
    private int mTouchDownX = 0;
    private int mTouchDownY = 0;
    private int mTouchUpX = 0;
    private int mTouchUpY = 0;

    private String mClickedGubun = "";
    private String mClickedTitle = "";
    private String mClickedUniqueCode = "";
    private int mPagerIndex = 0;
    private ArrayList<PopADModel> mPopArray;
    private PopADModel mClickedModel;
    private static Timer mTimer;
    private int mCounter;
    private String mStrMessage;
    private String mPopPoint;
    private boolean mTimerRun = false;
    private boolean mDelPressed = false;
    private int mSize = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if ( !TextUtils.isEmpty(mStrMessage))
                    Toast.makeText(KeyboardAditionADActivity.this, mStrMessage, Toast.LENGTH_SHORT).show();
            } else if ( msg.what == 1 ) {
                try {
                    mClickedModel.setPoint("0");
                    mPopArray.remove(mPagerIndex);
                    mPopArray.add(mPagerIndex, mClickedModel);
                    String btnStr = getResources().getString(R.string.aikbd_save_complete);
                    mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg03);
                    mAdPointTitle.setText(btnStr);
                    mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_pop_gray));
                    mAdPoint.setVisibility(View.GONE);
                    if ( !TextUtils.isEmpty(mStrMessage))
                        Toast.makeText(KeyboardAditionADActivity.this, mStrMessage, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ( msg.what == 101 ) {
                UserIdDBHelper helper = new UserIdDBHelper(KeyboardAditionADActivity.this);
                KeyboardUserIdModel userInfoModel = helper.getUserInfo();
                String userId = userInfoModel.getUserId();
                String gubun = userInfoModel.getGubun();
                String deviceId = userInfoModel.getDeviceId();
                String url = "";
                if ( KeyboardLogPrint.debug )
                    url = Url.TEST_DOMAIN + Url.URL_AD_POINT;
                else
                    url = Url.DOMAIN + Url.URL_AD_POINT;

                KeyboardLogPrint.e("link userId :: " + userId + " and user gubun :: " + gubun);

                String totalUrl = url + mClickedModel.getLink() + "&service_code=01&user_id=" + userId + "&gubun=" + gubun;
                KeyboardLogPrint.e("totalUrl :::: " + totalUrl);

                CustomAsyncTask task = new CustomAsyncTask(KeyboardAditionADActivity.this);
                task.connectAdPoint(totalUrl, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean rt, Object obj) {

                        stopTimer();
                        if ( rt ) {

                            try {
                                JSONObject object = (JSONObject) obj;
                                if ( object != null ) {

                                    boolean result = object.optBoolean("Result");
                                    String message = object.optString("errstr");
                                    mPopPoint = object.optString("user_point");

                                    if (result ) {
                                        mStrMessage = "포인트 지급이 완료되었습니다.";
                                        mHandler.sendEmptyMessage(1);
                                    } else {
                                        mStrMessage = message;
                                        mHandler.sendEmptyMessage(0);
                                    }
                                } else {

                                }
                            } catch (Exception e) {

                                e.printStackTrace();
                                mStrMessage = "데이터가 올바르지 않습니다.";
                                mHandler.sendEmptyMessage(0);
                            }
                        } else {

                        }
                    }
                });
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.aikbd_activity_addition);

        initViews();
    }

    public void onResume() {
        super.onResume();
        KeyboardLogPrint.d("onResume stopTimer");
        stopTimer();
    }

    public void onPause() {
        super.onPause();
        KeyboardLogPrint.e("onPause ::::: ");
        if ( !mTimerRun ) {
            KeyboardLogPrint.d("onPause mTimerRun false stopTimer");
            stopTimer();
        } else {
            KeyboardLogPrint.d("onPause mTimerRun true mTimerRun flag change false");
            mTimerRun = false;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        KeyboardLogPrint.e("KeyboardAditionADActivity onDestroy");
    }

    public void onBackPressed() {

    }

    private void initViews() {
        KeyboardLogPrint.d("initViews");
        RelativeLayout root = (RelativeLayout) findViewById(R.id.main_layer);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                finish();
            }
        });

        mTopLayer = (RelativeLayout) findViewById(R.id.top_layer);
        mTopLayer.setVisibility(View.GONE);
        mProgress = (ProgressWheel) findViewById(R.id.progress);
        mProgress.setVisibility(View.VISIBLE);
        mTopArrow = (RelativeLayout) findViewById(R.id.top_arrow_layer);
        mBottomArrow = (RelativeLayout) findViewById(R.id.bottom_arrow_layer);
        mPopDelBtn = (RelativeLayout) findViewById(R.id.btn_del_layer);
        mAdPointTitle = (TextView) findViewById(R.id.ad_point_title);
        mADPointBackground = (RelativeLayout) findViewById(R.id.ad_point_layer);
        mKeyboardPoint = (TextView) findViewById(R.id.keyboard_point);
        mKeyboardPoint.setIncludeFontPadding(false);
        KeyboardLogPrint.e("init mPopPoint : " + mPopPoint);
        mKeyboardPoint.setText("");
        mAdPoint = (TextView) findViewById(R.id.ad_point);

        mNPLayer = (RelativeLayout) findViewById(R.id.newposting_ttl_layer);
        mADTTLLayer = (RelativeLayout) findViewById(R.id.ad_ttl_layer);
        mNPTitle = (TextView) findViewById(R.id.np_title);
        mPopTitle = (TextView) findViewById(R.id.title);
        mPopSep = (TextView) findViewById(R.id.sep);
        mPopPrice = (TextView) findViewById(R.id.price);

        mPagerLayer = (RelativeLayout) findViewById(R.id.pager_layer);
        mPager = (VerticalPager) findViewById(R.id.pager);


//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int s_width = displayMetrics.widthPixels;
//        int height = (int)(s_width - convertDpToPx(50));
//        ViewGroup.LayoutParams params = mPagerLayer.getLayoutParams();
//        params.height = height;
//        mPagerLayer.setLayoutParams(params);

        mADPointBackground.setOnClickListener(mClickListener);
        mPopDelBtn.setOnClickListener(mClickListener);
        mTopArrow.setOnTouchListener(mTouchListener);
        mBottomArrow.setOnTouchListener(mTouchListener);

        mPager.setOnTouchListener(new VerticalPager.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardLogPrint.e("mClickedGubun : " + mClickedGubun);
                KeyboardLogPrint.e("mPagerIndex : " + mPagerIndex);
                if (mPopArray == null) {
                    return true;
                }
                mClickedModel = mPopArray.get(mPagerIndex);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTouchDownX = (int) event.getRawX();
                    mTouchDownY = (int) event.getRawY();
                    KeyboardLogPrint.e("mTouchDownX : " + mTouchDownX);
                    KeyboardLogPrint.e("mTouchDownY : " + mTouchDownY);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL
                        || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mTouchUpX = (int) event.getRawX();
                    mTouchUpY = (int) event.getRawY();

                    KeyboardLogPrint.e("mTouchUpX : " + mTouchUpX);
                    KeyboardLogPrint.e("mTouchUpY : " + mTouchUpY);

                    int gapX = getDifference(mTouchDownX, mTouchUpX);
                    int gapY = getDifference(mTouchDownY, mTouchUpY);

                    KeyboardLogPrint.e("gapX : " + gapX);
                    KeyboardLogPrint.e("gapY : " + gapY);

                    if (gapX < 10 && gapY < 10) {
                        if (mPopArray.size() > 0) {
                            mClickedModel = mPopArray.get(mPagerIndex);

                            final String clickedPoint = mClickedModel.getPoint();
                            KeyboardLogPrint.e("clickedPoint : " + clickedPoint);

                            if ("0".equals(clickedPoint) || clickedPoint == null || "null".equals(clickedPoint)) {
                                String link = mClickedModel.getLink();
                                String title = mClickedModel.getTitle();
                                String image = mClickedModel.getLogo();
                                String point = mClickedModel.getPoint();
                                String unique = mClickedModel.getPCode() + "_" + mClickedModel.getSiteCode();

                                UserIdDBHelper helper = new UserIdDBHelper(KeyboardAditionADActivity.this);
                                KeyboardUserIdModel userInfoModel = helper.getUserInfo();
                                String userId = userInfoModel.getUserId();
                                String gubun = userInfoModel.getGubun();
                                String deviceId = userInfoModel.getDeviceId();
                                String url = "";
                                if (KeyboardLogPrint.debug)
                                    url = Url.TEST_DOMAIN + Url.URL_AD_CLICK;
                                else
                                    url = Url.DOMAIN + Url.URL_AD_CLICK;

                                KeyboardLogPrint.e("link userId :: " + userId + " and user gubun :: " + gubun);

                                String totalUrl = url + link + "&service_code=01&user_id=" + userId + "&gubun=" + gubun;

//                                onPopWebViewOpen(title, link, totalUrl, image, point, userId, gubun, deviceId, unique);
                                onWebSiteOpen(totalUrl);
                            } else {
                                String link = mClickedModel.getLink();
                                String title = mClickedModel.getTitle();
                                String image = mClickedModel.getLogo();
                                String point = mClickedModel.getPoint();
                                String unique = mClickedModel.getPCode() + "_" + mClickedModel.getSiteCode();
                                UserIdDBHelper helper = new UserIdDBHelper(KeyboardAditionADActivity.this);
                                KeyboardUserIdModel userInfoModel = helper.getUserInfo();
                                String userId = userInfoModel.getUserId();
                                String gubun = userInfoModel.getGubun();
                                String deviceId = userInfoModel.getDeviceId();
                                String url = "";
                                if (KeyboardLogPrint.debug)
                                    url = Url.TEST_DOMAIN + Url.URL_AD_CLICK;
                                else
                                    url = Url.DOMAIN + Url.URL_AD_CLICK;

                                KeyboardLogPrint.e("link userId :: " + userId + " and user gubun :: " + gubun);

                                String totalUrl = url + link + "&service_code=01&user_id=" + userId + "&gubun=" + gubun;

//                                onPopWebViewOpen(title, link, totalUrl, image, point, userId, gubun, deviceId, unique);
                                onWebSiteOpen(totalUrl);
                            }

                            mClickedGubun = mClickedModel.getGubun();
                            mClickedTitle = mClickedModel.getTitle();
                            mClickedUniqueCode = mClickedModel.getPCode() + "_" + mClickedModel.getSiteCode();

                            KeyboardLogPrint.e("touchListener mClickedGubun :: " + mClickedGubun);
                            KeyboardLogPrint.e("touchListener mClickedTitle :: " + mClickedTitle);
                            KeyboardLogPrint.e("touchListener mClickedUniqueCode :: " + mClickedUniqueCode);
                        }
                    }
                }
                return false;
            }
        });

        mPager.addOnPageChangedListener(new VerticalPager.OnVerticalPageChangeListener() {
            @Override
            public void onVerticalPageChanged(int newPageIndex) {
                mPagerIndex = newPageIndex;
                PopADModel model = mPopArray.get(mPagerIndex);
                String adPoint = model.getPoint();

                mADPointBackground.setVisibility(View.VISIBLE);

                String gubun = model.getGubun();
                if ("NEWPOSTING".equals(gubun) || "NEWSPIC".equals(gubun)) {
                    mADTTLLayer.setVisibility(View.INVISIBLE);
                    mNPLayer.setVisibility(View.VISIBLE);

                    mNPTitle.setText(model.getTitle());
                } else {
                    mADTTLLayer.setVisibility(View.VISIBLE);
                    mNPLayer.setVisibility(View.INVISIBLE);

                    if ("KL".equals(gubun) || "KP".equals(gubun)) {
                        String title = model.getTitle();
                        String content = model.getContent();

                        mPopSep.setVisibility(View.VISIBLE);
                        mPopPrice.setVisibility(View.VISIBLE);
                        mPopTitle.setVisibility(View.VISIBLE);
                        mPopTitle.setText(title);
                        mPopPrice.setText(content);
                    } else if ("SR".equals(gubun)) {
                        if (TextUtils.isEmpty(model.getPrice())) {
                            mPopSep.setVisibility(View.GONE);
                            mPopPrice.setVisibility(View.GONE);
                            mPopTitle.setVisibility(View.VISIBLE);
                            mPopTitle.setText(model.getTitle());
                        } else {
                            if ("0".equals(model.getPrice()) || "null".equals(model.getPrice())) {
                                mPopSep.setVisibility(View.GONE);
                                mPopPrice.setVisibility(View.GONE);
                                mPopTitle.setVisibility(View.VISIBLE);
                                mPopTitle.setText(model.getTitle());
                            } else {
                                mPopSep.setVisibility(View.VISIBLE);
                                mPopPrice.setVisibility(View.VISIBLE);
                                mPopTitle.setVisibility(View.VISIBLE);
                                mPopTitle.setText(model.getTitle());
                                mPopPrice.setText(model.getPrice() + "원");
                            }
                        }
                    } else {
                        if (TextUtils.isEmpty(model.getPrice())) {
                            mPopSep.setVisibility(View.GONE);
                            mPopPrice.setVisibility(View.GONE);
                            mPopTitle.setVisibility(View.VISIBLE);
                            mPopTitle.setText(model.getTitle());
                        } else {
                            if ("0".equals(model.getPrice()) || "null".equals(model.getPrice())) {
                                mPopSep.setVisibility(View.GONE);
                                mPopPrice.setVisibility(View.GONE);
                                mPopTitle.setVisibility(View.VISIBLE);
                                mPopTitle.setText(model.getTitle());
                            } else {
                                mPopSep.setVisibility(View.VISIBLE);
                                mPopPrice.setVisibility(View.VISIBLE);
                                mPopTitle.setVisibility(View.VISIBLE);
                                mPopTitle.setText(model.getTitle());
                                mPopPrice.setText(model.getPrice() + "원");
                            }
                        }
                    }
                }

                if ("0".equals(adPoint) || "".equals(adPoint) || "null".equals(adPoint)) {
                    String btnStr;
                    if ("NEWPOSTING".equals(model.getGubun()) || "NEWSPIC".equals(model.getGubun())) {
                        if ("NOSAVE".equals(model.getPointGubun())) {
                            btnStr = getResources().getString(R.string.aikbd_go_to_see);
                            mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg02);
                            mAdPointTitle.setText(btnStr);
                            mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_yellow));
                        } else {
                            btnStr = getResources().getString(R.string.aikbd_save_complete);
                            mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg03);
                            mAdPointTitle.setText(btnStr);
                            mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_pop_gray));
                        }
                    } else {
                        if ("NOSAVE".equals(model.getPointGubun())) {
                            btnStr = getResources().getString(R.string.aikbd_go_to_see);
                            mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg02);
                            mAdPointTitle.setText(btnStr);
                            mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_yellow));
                        } else {
                            btnStr = getResources().getString(R.string.aikbd_save_complete);
                            mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg03);
                            mAdPointTitle.setText(btnStr);
                            mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_pop_gray));
                        }
                    }
                    mAdPoint.setVisibility(View.GONE);
                } else {
                    mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg01);
                    mAdPointTitle.setText("적립받기");
                    mAdPoint.setVisibility(View.VISIBLE);
                    mAdPoint.setText("+" + adPoint);
                    mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_dark_gray1));
                    mAdPoint.setTextColor(getResources().getColor(R.color.aikbd_dark_gray1));
                }
            }
        });

        mPager.addOnScrollListener(new VerticalPager.OnScrollListener() {
            @Override
            public void onViewScrollFinished(int currentPage) {
            }

            @Override
            public void onScroll(int scrollX) {
            }
        });

//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int width = dm.widthPixels;
//        KeyboardLogPrint.d("width :::: " + width);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPagerLayer.getLayoutParams();
//        KeyboardLogPrint.d("before width :: " + params.width);
//        int padding = (int) (convertDpToPx(50));
//        mSize = width - padding;
//        params.width = mSize;
//        params.height = mSize;
//        KeyboardLogPrint.d("height :::: " + (width - padding));
//        mPagerLayer.setLayoutParams(params);
//
//        RelativeLayout.LayoutParams p_params = (RelativeLayout.LayoutParams) mPager.getLayoutParams();
//        p_params.width = mSize;
//        p_params.height = mSize;
//        mPager.setLayoutParams(p_params);

//        setDataToPager();
        connectAd();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KeyboardLogPrint.e("clilck call");
            if (v.getId() == R.id.ad_point_layer) {
                KeyboardLogPrint.e("ad_point_layer clicked");
                if (mPopArray.size() > 0) {
                    mClickedModel = mPopArray.get(mPagerIndex);

                    final String clickedPoint = mClickedModel.getPoint();
                    KeyboardLogPrint.e("clickedPoint : " + clickedPoint);

                    if ("0".equals(clickedPoint) || clickedPoint == null || "null".equals(clickedPoint)) {

                        String link = mClickedModel.getLink();
                        String title = mClickedModel.getTitle();
                        String image = mClickedModel.getLogo();
                        String point = mClickedModel.getPoint();
                        String unique = mClickedModel.getPCode() + "_" + mClickedModel.getSiteCode();
                        UserIdDBHelper helper = new UserIdDBHelper(KeyboardAditionADActivity.this);
                        KeyboardUserIdModel userInfoModel = helper.getUserInfo();
                        String userId = userInfoModel.getUserId();
                        String gubun = userInfoModel.getGubun();
                        String deviceId = userInfoModel.getDeviceId();
                        String url = "";
                        if (KeyboardLogPrint.debug)
                            url = Url.TEST_DOMAIN + Url.URL_AD_CLICK;
                        else
                            url = Url.DOMAIN + Url.URL_AD_CLICK;

                        KeyboardLogPrint.e("link userId :: " + userId + " and user gubun :: " + gubun);

                        String totalUrl = url + link + "&service_code=01&user_id=" + userId + "&gubun=" + gubun;

//                        onPopWebViewOpen(title, link, totalUrl, image, point, userId, gubun, deviceId, unique);
                        onWebSiteOpen(totalUrl);
                    } else {
                        String link = mClickedModel.getLink();
                        String title = mClickedModel.getTitle();
                        String image = mClickedModel.getLogo();
                        String point = mClickedModel.getPoint();
                        String unique = mClickedModel.getPCode() + "_" + mClickedModel.getSiteCode();
                        UserIdDBHelper helper = new UserIdDBHelper(KeyboardAditionADActivity.this);
                        KeyboardUserIdModel userInfoModel = helper.getUserInfo();
                        String userId = userInfoModel.getUserId();
                        String gubun = userInfoModel.getGubun();
                        String deviceId = userInfoModel.getDeviceId();
                        String url = "";
                        if (KeyboardLogPrint.debug)
                            url = Url.TEST_DOMAIN + Url.URL_AD_CLICK;
                        else
                            url = Url.DOMAIN + Url.URL_AD_CLICK;

                        KeyboardLogPrint.e("link userId :: " + userId + " and user gubun :: " + gubun);

                        String totalUrl = url + link + "&service_code=01&user_id=" + userId + "&gubun=" + gubun;

//                        onPopWebViewOpen(title, link, totalUrl, image, point, userId, gubun, deviceId, unique);
                        onWebSiteOpen(totalUrl);
                    }

                    mClickedGubun = mClickedModel.getGubun();
                    mClickedTitle = mClickedModel.getTitle();
                    mClickedUniqueCode = mClickedModel.getPCode() + "_" + mClickedModel.getSiteCode(); // CAULY 광고 클릭 했을 경우 사용
                } else {
                    KeyboardLogPrint.e("AD SIZE IS ZERO :: " + mPopArray.size());
                }
            } else if (v.getId() == R.id.btn_del_layer) {
                try {
//                    Intent intent = KeyboardAditionADActivity.this.getPackageManager().getLaunchIntentForPackage("com.enliple.keyboard.ui.ckeyboard");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    KeyboardAditionADActivity.this.startActivity(intent);
                    KeyboardLogPrint.i("btn_del_layer pressed");
                    mDelPressed = true;
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == R.id.top_arrow_layer) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mPager.scrollUp();
                }
            } else if (v.getId() == R.id.bottom_arrow_layer) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mPager.scrollDown();
                }
            }
            return true;
        }
    };

//    public static float DpToPixel(float dp, Context context) {
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        float px = dp * (metrics.densityDpi / 160f);
//        return px;
//    }

    private int convertDpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int getDifference(int a, int b) {
        int difference = 0;
        if (a > b) {
            difference = a - b;
        } else {
            difference = b - a;
        }

        return difference;
    }


    private void connectAd() {
        mPopArray = new ArrayList<PopADModel>();
        KeyboardLogPrint.e("connectAd");
        UserIdDBHelper helper = new UserIdDBHelper(KeyboardAditionADActivity.this);
        AIKBD_DBHelper d_helper = new AIKBD_DBHelper(KeyboardAditionADActivity.this);
        KeyboardUserIdModel model = helper.getUserInfo();
        String keyword = d_helper.getKwd();
        KeyboardLogPrint.e("connectAd keyword :: " + keyword);
        String appVersion = getAppVersion();
        if (model != null) {
            CustomAsyncTask task = new CustomAsyncTask(KeyboardAditionADActivity.this);
            task.connectPopAD(model, keyword, appVersion, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean rt, Object obj) {
                    if (rt) {
                        try {
                            JSONObject object = (JSONObject) obj;
                            if (object != null) {
                                boolean result = object.optBoolean("Result");
                                String errstr= object.optString("errstr");
                                mPopPoint = object.optString("user_point");
                                mKeyboardPoint.setText("" + mPopPoint);
                                if (result) {
                                    try {
                                        JSONArray jsonArray = object.optJSONArray("list_info");
                                        if (jsonArray != null && jsonArray.length() > 0) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                PopADModel model = new PopADModel();
                                                JSONObject rowObject = jsonArray.getJSONObject(i);
                                                String point = rowObject.optString("point", "0");
                                                String title = rowObject.optString("title", "");
                                                String content = rowObject.optString("content", "");
                                                String link = rowObject.optString("link", "");
                                                String image = rowObject.optString("image", "");
                                                String pcode = rowObject.optString("pcode", "");
                                                String sitecode = rowObject.optString("sitecode", "");
                                                String price = rowObject.optString("price", "");
                                                String ad_type = rowObject.optString("ad_type", "");
                                                String sequence = rowObject.optString("number", "");
                                                String pointGubun = rowObject.optString("pointgubun", "");
                                                String gubun = rowObject.optString("gubun", "");
                                                String logo = rowObject.optString("logo", "");
                                                KeyboardLogPrint.e("got image :: " + image);
                                                model.setPoint(point);
                                                model.setTitle(title);
                                                model.setContent(content);
                                                model.setImage(image);
                                                model.setLink(link);
                                                model.setPCode(pcode);
                                                model.setSiteCode(sitecode);
                                                model.setPrice(price);
                                                model.setAdType(ad_type);
                                                model.setPointGubun(pointGubun);
                                                model.setGubun(gubun);
                                                model.setLogo(logo);

                                                mPopArray.add(model);
                                            }
                                            setDataToPager();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mProgress.setVisibility(View.GONE);
                                        Toast.makeText(KeyboardAditionADActivity.this, "데이터 수신에 실패하였습니다." , Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(KeyboardAditionADActivity.this, errstr, Toast.LENGTH_SHORT).show();
                                    mProgress.setVisibility(View.GONE);
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mProgress.setVisibility(View.GONE);
                            Toast.makeText(KeyboardAditionADActivity.this, "데이터 수신에 실패하였습니다." , Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        mProgress.setVisibility(View.GONE);
                        Toast.makeText(KeyboardAditionADActivity.this, "데이터 수신에 실패하였습니다." , Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }

    private void setDataToPager() {

        if (mPopArray == null) {
            KeyboardLogPrint.i("setDataToPager mPopArray == null");
            finish();
            return;
        }

        int adSize = mPopArray.size();

        if (adSize > 0) {
            mPager.setLastPage(adSize - 1);

            // 기존 mModelArray == null 에 대한 처리가 없음 그래서 mModelArray == null 일 경우에도 팝업이 뜨고 거기서 클릭 발생시 exception 발생.
            // 2017.08.24 이에 관련 예외처리 함
            if (mPopArray == null) {
                KeyboardLogPrint.i("setDataToPager mPopArray == null 1 ");
                finish();
                return;
            }

            Collections.shuffle(mPopArray);

            mPagerIndex = 0;

            mProgress.setVisibility(View.GONE);
            mTopLayer.setVisibility(View.VISIBLE);

            DisplayMetrics dm = getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            KeyboardLogPrint.d("width :::: " + width);
            MyRelativeLayout.LayoutParams params = (MyRelativeLayout.LayoutParams) mTopLayer.getLayoutParams();
            KeyboardLogPrint.d("before width :: " + params.width);
            int padding = (int) (convertDpToPx(50));
            mSize = width - padding;
            params.width = mSize;
            params.height = MyRelativeLayout.LayoutParams.WRAP_CONTENT;
            params.addRule(MyRelativeLayout.CENTER_IN_PARENT);
            KeyboardLogPrint.d("height :::: " + (width - padding));
            mTopLayer.setLayoutParams(params);

            RelativeLayout.LayoutParams p_params_l = (RelativeLayout.LayoutParams) mPagerLayer.getLayoutParams();
            p_params_l.width = mSize;
            p_params_l.height = mSize;
            mPagerLayer.setLayoutParams(p_params_l);

            RelativeLayout.LayoutParams p_params = (RelativeLayout.LayoutParams) mPager.getLayoutParams();
            p_params.width = mSize;
            p_params.height = mSize;
            mPager.setLayoutParams(p_params);

            if (!"".equals(mClickedGubun) && !"".equals(mClickedTitle)) {
                ArrayList<PopADModel> containModel = new ArrayList<PopADModel>();
                ArrayList<PopADModel> restModel = new ArrayList<PopADModel>();

                for (int j = 0; j < adSize; j++) {
                    PopADModel model = mPopArray.get(j);

                    if (mClickedGubun.equals(model.getGubun()) && mClickedTitle.equals(model.getTitle()) && mClickedUniqueCode.equals(model.getPCode() + "_" + model.getSiteCode())) {
                        containModel.add(model);
                    } else {
                        restModel.add(model);
                    }
                }

                mClickedGubun = "";
                mClickedTitle = "";
                mClickedUniqueCode = "";

                mPopArray = new ArrayList<PopADModel>();

                if (containModel.size() > 0) {
                    for (int l = 0; l < containModel.size(); l++) {
                        mPopArray.add(containModel.get(l));
                    }
                }

                KeyboardLogPrint.e("contain array size :: " + containModel.size());

                if (restModel.size() > 0) {
                    for (int k = 0; k < restModel.size(); k++) {
                        mPopArray.add(restModel.get(k));
                    }
                }
            } else {
                mClickedGubun = "";
                mClickedTitle = "";
                mClickedUniqueCode = "";
            }

            KeyboardLogPrint.e(" +++ mPopArray.size : " + mPopArray.size());
            for (int i = 0; i < mPopArray.size(); i++) {
                final PopADModel model = mPopArray.get(i);
                String gubun = model.getGubun();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mPagerView = inflater.inflate(R.layout.aikbd_popupcash_item, null);

                final ImageView pagerImage = (ImageView) mPagerView.findViewById(R.id.mango_plus_image);
                final TextView gubunText = (TextView) mPagerView.findViewById(R.id.mango_ad_gubun);

                if (KeyboardLogPrint.debug) {
                    gubunText.setVisibility(View.VISIBLE);
                    gubunText.setText(gubun);
                } else {
                    gubunText.setVisibility(View.GONE);
                }

                String imagePath = model.getImage();
                KeyboardLogPrint.e("pager imagePath : " + imagePath);
//                Glide.with(KeyboardAditionADActivity.this)
//                        .load(imagePath)
//                        .asBitmap()
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                pagerImage.setImageBitmap(resource);
//                                pagerImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            }
//
//                            @Override
//                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                super.onLoadFailed(e, errorDrawable);
//                                KeyboardLogPrint.w("onLoadFailed title :: " + model.getTitle());
//                            }
//                        });

                mPager.addView(mPagerView);
            }

            String adPoint = mPopArray.get(0).getPoint();

            String gubun = mPopArray.get(0).getGubun();

            if ("NEWPOSTING".equals(gubun) || "NEWSPIC".equals(gubun)) {
                mADTTLLayer.setVisibility(View.INVISIBLE);
                mNPLayer.setVisibility(View.VISIBLE);

                mNPTitle.setText(mPopArray.get(0).getTitle());
            } else {
                mADTTLLayer.setVisibility(View.VISIBLE);
                mNPLayer.setVisibility(View.INVISIBLE);

                if ("KL".equals(gubun) || "KP".equals(gubun)) {
                    String title = mPopArray.get(0).getTitle();
                    String content = mPopArray.get(0).getContent();

                    mPopSep.setVisibility(View.VISIBLE);
                    mPopPrice.setVisibility(View.VISIBLE);
                    mPopTitle.setVisibility(View.VISIBLE);
                    mPopTitle.setText(title);
                    mPopPrice.setText(content);
                } else if ("SR".equals(gubun)) {
                    if (TextUtils.isEmpty(mPopArray.get(0).getPrice())) {
                        mPopSep.setVisibility(View.GONE);
                        mPopPrice.setVisibility(View.GONE);
                        mPopTitle.setVisibility(View.VISIBLE);
                        mPopTitle.setText(mPopArray.get(0).getTitle());
                    } else {
                        if ("0".equals(mPopArray.get(0).getPrice()) || "null".equals(mPopArray.get(0).getPrice())) {
                            mPopSep.setVisibility(View.GONE);
                            mPopPrice.setVisibility(View.GONE);
                            mPopTitle.setVisibility(View.VISIBLE);
                            mPopTitle.setText(mPopArray.get(0).getTitle());
                        } else {
                            mPopSep.setVisibility(View.VISIBLE);
                            mPopPrice.setVisibility(View.VISIBLE);
                            mPopTitle.setVisibility(View.VISIBLE);
                            mPopTitle.setText(mPopArray.get(0).getTitle());
                            mPopPrice.setText(mPopArray.get(0).getPrice() + "원");
                        }
                    }
                } else {
                    if (TextUtils.isEmpty(mPopArray.get(0).getPrice())) {
                        mPopSep.setVisibility(View.GONE);
                        mPopPrice.setVisibility(View.GONE);
                        mPopTitle.setVisibility(View.VISIBLE);
                        mPopTitle.setText(mPopArray.get(0).getTitle());
                    } else {
                        if ("0".equals(mPopArray.get(0).getPrice()) || "null".equals(mPopArray.get(0).getPrice())) {
                            mPopSep.setVisibility(View.GONE);
                            mPopPrice.setVisibility(View.GONE);
                            mPopTitle.setVisibility(View.VISIBLE);
                            mPopTitle.setText(mPopArray.get(0).getTitle());
                        } else {
                            mPopSep.setVisibility(View.VISIBLE);
                            mPopPrice.setVisibility(View.VISIBLE);
                            mPopTitle.setVisibility(View.VISIBLE);
                            mPopTitle.setText(mPopArray.get(0).getTitle());
                            mPopPrice.setText(mPopArray.get(0).getPrice() + "원");
                        }
                    }
                }
            }

            if ("0".equals(adPoint) || "".equals(adPoint) || "null".equals(adPoint)) {
                String btnStr;
                if ("NEWPOSTING".equals(mPopArray.get(0).getGubun()) || "NEWSPIC".equals(mPopArray.get(0).getGubun())) {
                    if ("NOSAVE".equals(mPopArray.get(0).getPointGubun())) {
                        btnStr = getResources().getString(R.string.aikbd_go_to_see);
                        mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg02);
                        mAdPointTitle.setText(btnStr);
                        mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_yellow));
                    } else {
                        btnStr = getResources().getString(R.string.aikbd_save_complete);
                        mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg03);
                        mAdPointTitle.setText(btnStr);
                        mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_pop_gray));
                    }
                } else {
                    if ("NOSAVE".equals(mPopArray.get(0).getPointGubun())) {
                        btnStr = getResources().getString(R.string.aikbd_go_to_see);
                        mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg02);
                        mAdPointTitle.setText(btnStr);
                        mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_yellow));
                    } else {
                        btnStr = getResources().getString(R.string.aikbd_save_complete);
                        mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg03);
                        mAdPointTitle.setText(btnStr);
                        mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_pop_gray));
                    }
                }
                mAdPoint.setVisibility(View.GONE);
            } else {
                mADPointBackground.setBackgroundResource(R.drawable.aikbd_img_pop_pointbg01);
                mAdPointTitle.setText("적립받기");
                mAdPoint.setVisibility(View.VISIBLE);
                mAdPoint.setText("+" + adPoint);
                mAdPointTitle.setTextColor(getResources().getColor(R.color.aikbd_dark_gray1));
                mAdPoint.setTextColor(getResources().getColor(R.color.aikbd_dark_gray1));
            }
        } else {
            KeyboardLogPrint.i("setDataToPager ad size less 0 ");
            finish();
        }
    }

    private String getAppVersion() {
        String version = "";
        try {
            PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = i.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    private void onWebSiteOpen(String url) {

        if ( mClickedModel != null ) {
            String point = mClickedModel.getPoint();
            if ( !"0".equals(point) ) {
                startTimer();
            }
            Common.onWebSiteOpen(KeyboardAditionADActivity.this, url);
        }
    }

    private class ForegroundTask extends TimerTask {
        @Override
        public void run() {

            KeyboardLogPrint.e("forground task for point pay");
            if ( mCounter == 5 ) {
                KeyboardLogPrint.e("forground task counter five");
                // server에 포인트 지급 후 timer cancel
                KeyboardLogPrint.e("mCounter :: " + mCounter);
                if ( mClickedModel == null )
                    return;

                mHandler.sendEmptyMessage(101);

//                UserIdDBHelper helper = new UserIdDBHelper(KeyboardAditionADActivity.this);
//                KeyboardUserIdModel userInfoModel = helper.getUserInfo();
//                String userId = userInfoModel.getUserId();
//                String gubun = userInfoModel.getGubun();
//                String deviceId = userInfoModel.getDeviceId();
//                String url = "";
//                if ( KeyboardLogPrint.debug )
//                    url = Url.TEST_DOMAIN + Url.URL_AD_POINT;
//                else
//                    url = Url.DOMAIN + Url.URL_AD_POINT;
//
//                KeyboardLogPrint.e("link userId :: " + userId + " and user gubun :: " + gubun);
//
//                String totalUrl = url + mClickedModel.getLink() + "&service_code=01&user_id=" + userId + "&gubun=" + gubun;
//                KeyboardLogPrint.e("totalUrl :::: " + totalUrl);
//                Log.d("KEY_T", "before ad point :: " + totalUrl);
//                CustomAsyncTask task = new CustomAsyncTask(KeyboardAditionADActivity.this);
//                task.connectAdPoint(totalUrl, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//                    @Override
//                    public void onResponse(boolean rt, Object obj) {
//                        Log.d("KEY_T", "point responsed");
//                        stopTimer();
//                        if ( rt ) {
//                            Log.d("KEY_T", "point responsed 000");
//                            try {
//                                JSONObject object = (JSONObject) obj;
//                                if ( object != null ) {
//
//                                    boolean result = object.optBoolean("Result");
//                                    String message = object.optString("errstr");
//                                    mPopPoint = object.optString("user_point");
//                                    Log.d("KEY_T", "point responsed  result :: " + result);
//                                    if (result ) {
//                                        mStrMessage = "포인트 지급이 완료되었습니다.";
//                                        mHandler.sendEmptyMessage(1);
//                                    } else {
//                                        mStrMessage = message;
//                                        mHandler.sendEmptyMessage(0);
//                                    }
//                                } else {
//                                    Log.d("KEY_T", "point responsed 0");
//                                }
//                            } catch (Exception e) {
//                                Log.d("KEY_T", "point responsed 00");
//                                e.printStackTrace();
//                                mStrMessage = "데이터가 올바르지 않습니다.";
//                                mHandler.sendEmptyMessage(0);
//                            }
//                        } else {
//                            Log.d("KEY_T", "point responsed 0000");
//                        }
//                    }
//                });
            } else if (mCounter < 5 ) {
                KeyboardLogPrint.e("forground task counter less five");
                int remainTime = 5 - mCounter;
//                mStrMessage = remainTime + "초 후 포인트가 지급됩니다.";
                if ( mCounter%2 == 1 )
                    mStrMessage = "포인트 적립요청중입니다. 잠시 기다려 주시기 바랍니다.";
                else
                    mStrMessage = "";
                mHandler.sendEmptyMessage(0);
            } else {
                KeyboardLogPrint.e("forground task counter else");
            }
            mCounter ++;
        }
    }

    private void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimerRun = true;
        mCounter = 0;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new ForegroundTask(), 0, 900);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mCounter = 0;
    }
}
