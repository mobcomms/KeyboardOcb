package com.enliple.keyboard.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

public class RewardAdWebViewActivity extends Activity {
    private static final int DEFAULT_COUNT = 5;
    private WebView keyboard_news_webview;
    private TextView btn_back;
    private String link = null;
    private String seq = null;
    private String point = null;
    private ProgressBar count_progressbar;
    private TextView count_time;
    private CountDownTimer countDownTimer;
    private int count = 0;
    private boolean isExtScheme = false;
    private String scForPoint = "";
    private boolean pointRequired = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_webview_keyboard);
        LogPrint.d("webview activity onCreate");
        View root_layout = findViewById(R.id.root_layout);
        Common.SetInset(root_layout);
        boolean isNews = false;
        Intent intent = getIntent();
        if ( intent != null ) {
            link = intent.getStringExtra("reward_link");
            point = SharedPreference.getZeroInt(RewardAdWebViewActivity.this, Key.KEY_OCB_AD_BANNER_POINT) + "";
            count = SharedPreference.getInt(RewardAdWebViewActivity.this, Key.KEY_REWARD_COUNT);
            scForPoint = intent.getStringExtra("sc");
            isNews = intent.getBooleanExtra("isNews", false);
        }
        LogPrint.d("kskk  setWebViewOpen url 2 :: " + link + " , count :: " + count + " , isNews :: " + isNews);
        if (TextUtils.isEmpty(link) )
            finish();

        TextView tlt = (TextView)findViewById(R.id.main_title);
        if ( isNews )
            tlt.setText("뉴스");
        else
            tlt.setText("광고");
        
        keyboard_news_webview = findViewById(R.id.keyboard_news_webview);
        btn_back = findViewById(R.id.btn_back);

        count_progressbar = findViewById(R.id.count_progressbar);
        count_time = findViewById(R.id.count_time);

        keyboard_news_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogPrint.d("kskk webview  url :: " + url);
                boolean overrideUrlLoading = false;
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    LogPrint.d("kskk url contain http or https");
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    LogPrint.d("kskk url not contain http or https");
                    try {
                        if ( countDownTimer != null ) {
                            isExtScheme = true;
                            countDownTimer.onFinish();
                        }
                        //startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));
                        boolean flag = callApp(RewardAdWebViewActivity.this, url.toString());
                        if (!flag) {
                            Intent intent = Intent.parseUri(url.toString(), Intent.URI_INTENT_SCHEME);
                            String fallBackUrl = "";
                            if ( intent != null ) {
                                fallBackUrl = intent.getStringExtra("browser_fallback_url");
                            }
                            LogPrint.d("fallBackUrl :: " + fallBackUrl);
                            if(!TextUtils.isEmpty(fallBackUrl)) {
                                view.loadUrl(fallBackUrl);
                                flag = false;
                            } else
                                flag = true;
                        }
                        return flag;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        keyboard_news_webview.setWebChromeClient(new WebChromeClient());
        keyboard_news_webview.clearHistory();                                            // 히스토리 초기화 (?)
        keyboard_news_webview.clearCache(true);                                          // 웹뷰 캐시 삭제

        WebSettings webSettings = keyboard_news_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadWithOverviewMode(true);
        // Javascript 엔진 사용
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);                // 웹뷰 캐쉬 제거
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            webSettings.setTextZoom(100);
        keyboard_news_webview.loadUrl(link);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        startTimer(count);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogPrint.d("kskk onPause isExtScheme :: " + isExtScheme);
        if ( !isExtScheme ) {
            if ( countDownTimer != null )
                countDownTimer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogPrint.d("kskk onDestroy");
        if ( !isExtScheme ) {
            if ( countDownTimer != null )
                countDownTimer.cancel();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogPrint.d("webview activity onNewIntent");
    };

    private void startTimer(int second) {
        count_progressbar.setMax(second * 1000);
        count_progressbar.setProgress(second * 1000);
        count_time.setText(second + "");
        countDownTimer = new CountDownTimer(second * 1000, 100) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                count_progressbar.setProgress((int)leftTimeInMilliseconds);
                count_time.setText(seconds + "");
                LogPrint.d("onTick called");
                // format the textview to show the easily readable format

            }
            @Override
            public void onFinish() {
                count_progressbar.setVisibility(View.GONE);
                count_time.setVisibility(View.GONE);
                LogPrint.d("kskk onFinish isExtScheme :: " + isExtScheme + " , scForPoint :: " + scForPoint + " , pointRequired :: " + pointRequired);
                if ( !pointRequired ) {
                    CustomAsyncTask task = new CustomAsyncTask(RewardAdWebViewActivity.this);
                    task.sendRewardPoint(scForPoint, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            LogPrint.d("kskk result :: " + result);
                            if ( result ) {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    LogPrint.d("kskk sendRewardPoint object 1 :: " + object.toString() );
                                    if ( object != null ) {
                                        boolean rt = object.optBoolean("Result");
                                        if ( rt ) {
                                            String message = object.optString("message");
                                            /*
                                            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                            View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_surprise_toast, null);
                                            TextView tv = layout.findViewById(R.id.toastStr);
                                            tv.setText(message);
                                            Toast toast = new Toast(RewardAdWebViewActivity.this);
                                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                                            toast.setDuration(Toast.LENGTH_SHORT);
                                            toast.setView(layout);
                                            toast.show();*/
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                            //setRake(currentPageId, "toast.popreward");
                                            if ( isExtScheme ) {
                                                if ( countDownTimer != null )
                                                    countDownTimer.cancel();
                                                finish();
                                            }
                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                pointRequired = true;

/*
                if ( !TextUtils.isEmpty(scForPoint) ) {
                    CustomAsyncTask task = new CustomAsyncTask(RewardAdWebViewActivity.this);
                    task.saveNotiNewsPoint(seq, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            Toast.makeText(RewardAdWebViewActivity.this, point + "포인트가 적립되었습니다.", Toast.LENGTH_SHORT).show();
                            if ( isExtScheme )
                                finish();
                        }
                    });
                }

 */
            }
        }.start();
    }


    private boolean callApp(Activity activity, String url) {
        Intent intent = null;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            LogPrint.d("intent getScheme     +++===> " + intent.getScheme());
            LogPrint.d("intent getDataString +++===> " + intent.getDataString());
        } catch (URISyntaxException ex) {
            LogPrint.d("Bad URI " + url + ":" + ex.getMessage());
            return false;
        }
        return callAppResult(intent, activity, url);
    }

    private boolean callAppResult(Intent intent, Activity activity, String url) {
        try {
            boolean retval = false;

            if (url.startsWith("intent")) {
                if (activity.getPackageManager().resolveActivity(intent, 0) == null) {
                    String packagename = intent.getPackage();
                    if (packagename != null) {
                        Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        retval = true;
                    }
                }
                else {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setComponent(null);
                    try {
                        if (activity.startActivityIfNeeded(intent, -1)) {
                            retval = true;
                        }

                    } catch (ActivityNotFoundException ex) {
                        retval = false;
                    }
                }
            } else {
                boolean bKakaoTalk = false;
                boolean bKakaoStory = false;

                // 설치된 패키지 확인
                PackageManager pm = activity.getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
                for ( int i = 0 ; i < activityList.size() ; i ++ ) {
                    ResolveInfo app =  activityList.get(i);
                    if (app.activityInfo.name.contains("com.kakao.talk")) {
                        bKakaoTalk = true;
                        break;
                    }
                    if (app.activityInfo.name.contains("com.kakao.story")) {
                        bKakaoStory = true;
                        break;
                    }
                }

                // 해당 앱이 없을때 마켓으로 연결
                if ((url.startsWith("kakaolink://") || (url.startsWith("kakaotalk://")) )&& !bKakaoTalk) {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("market://details?id=" + "com.kakao.talk"));
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivityForResult(intent1, 0);
                    retval = true;
                } else if (url.startsWith("storylink://") && !bKakaoStory) {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("market://details?id=" + "com.kakao.story"));
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivityForResult(intent1, 0);
                    retval = true;
                } else {
                    Uri uri = Uri.parse(url);
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(uri);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivityForResult(intent1, 0);
                    retval = true;
                }
            }
            return retval;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
