package com.enliple.keyboard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.models.OfferwallData;
import com.enliple.keyboard.models.OfferwallParticipationReq;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Common;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.offerwall.OfferwallJavaScript;
import com.enliple.offerwall.OfferwallLoadingFragment;
import com.enliple.offerwall.OfferwallUtils;
import com.enliple.offerwall.OfferwallWebChromeClient;
import com.enliple.offerwall.OfferwallWebViewClient;
import com.enliple.offerwall.OnKeyboardSingleClickListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class KeyboardOfferwallWebView23Activity extends AppCompatActivity {
    private ConstraintLayout root;
    private OfferwallLoadingFragment loadingFragment;
    private TextView btn_back;
    private TextView main_title;
    private TextView txt_guide;
    private ProgressBar progressBar;
    private WebView webView;
    private FrameLayout popupLayout;
    private ConstraintLayout btn_get_reward;
    private TextView txt_get_reward;
    private TextView count_time;
    private RelativeLayout timer_layer;
    private CountDownTimer countDownTimer;
    private OfferwallData data;
    private String insect_js = "";
    private OfferwallWebViewClient offerwallWebViewClient;
    private OfferwallWebChromeClient offerwallWebChromeClient;
    private ArrayList<WebView> childWebViewList = new ArrayList<>();
    private String countDownUrl = null;
    private String landingUrl = "";
    private boolean buttonEnabled = false;
    public static Activity mActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_offerwall_webview);

        LogPrint.d("KeyboardOfferwallWebView23Activity onCreate");
        Intent intent = getIntent();
        if ( intent != null ) {
            data = (OfferwallData)intent.getSerializableExtra("intent_mission");
            landingUrl = intent.getStringExtra("landingUrl");
            if ( data == null )
                finish();
        } else {
            finish();
        }
        initViews();
    }

    public void onPause() {
        super.onPause();
        cancelTimer();
    }

    public void onBackPressed() {
        cancelTimer();
        timer_layer.setVisibility(View.GONE);
        if (childWebViewList.size() > 0) {
            int lastIdx = childWebViewList.size() - 1;
            WebView wv = childWebViewList.get(lastIdx);
            if (wv.canGoBack()) {
                wv.goBack();
            } else {
                wv.loadUrl("javascript:window.close()");
            }
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }
    }

    private void initViews() {
        mActivity = this;
        txt_get_reward = findViewById(R.id.txt_get_reward);
        count_time = findViewById(R.id.count_time);
        timer_layer = findViewById(R.id.timer_layer);
        root = findViewById(R.id.root);
        btn_back = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        popupLayout = findViewById(R.id.popupLayout);
        btn_get_reward = findViewById(R.id.btn_get_reward);
        main_title = findViewById(R.id.main_title);
        txt_guide = findViewById(R.id.txt_guide);

        String keyword = data.getAdver_name();
        if ( "p3".equals(data.getMission_class().toLowerCase()) ) {
            keyword = data.getTarget_name();
            String title = "스토어 리스트에서 " + data.getAdver_name() + "의 " + data.getTarget_name() + "상품을 찾으세요";
            int ad_startIndex = title.indexOf(data.getAdver_name()) - 1;
            int ad_endIndex = ad_startIndex + data.getAdver_name().length() + 2;
            int t_startIndex = title.indexOf(data.getTarget_name()) - 1;
            int t_endIndex = t_startIndex + data.getTarget_name().length() + 1;
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ffee00")), ad_startIndex, ad_endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(20, true), ad_startIndex, ad_endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(20, true), t_startIndex, t_endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), ad_startIndex, ad_endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), t_startIndex, t_endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_guide.setText(spannableString);

        } else if ( "p2".equals(data.getMission_class().toLowerCase()) ) {
            String title = "플레이스 리스트에서 " + data.getAdver_name() + " 업체를 찾으세요";
            int startIndex = title.indexOf(data.getAdver_name());
            int endIndex = startIndex + data.getAdver_name().length();
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ffee00")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(20, true), startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_guide.setText(spannableString);
        }

        insect_js = OfferwallJavaScript.getP3Javascript(data.getMission_class(), data.getCheck_url(),
                CustomAsyncTask.FORMISSION_MEDIA_ID, keyword);
        LogPrint.d("insect_js :: " + insect_js);


        loadingFragment = OfferwallLoadingFragment.getInstance();
        webView.addJavascriptInterface(new KeyboardOfferwallWebView23Activity.MyJavaScriptInterface(), "knowhow");

        offerwallWebViewClient = new OfferwallWebViewClient(KeyboardOfferwallWebView23Activity.this, progressBar);
        offerwallWebChromeClient = new OfferwallWebChromeClient(childWebViewList, webView, progressBar, root, popupLayout);
        webView.setWebViewClient(offerwallWebViewClient);
        webView.setWebChromeClient(offerwallWebChromeClient);
        setWebViewClientCallback();
        setWebChromeClientCallback();

        getInit(webView);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        webView.loadUrl(landingUrl);

        String mission_classs = data.getMission_class();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_get_reward.setOnClickListener(new OnKeyboardSingleClickListener() {
            @Override
            protected void onSingleClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.popupLayout, loadingFragment).commit();

                String mediaUserKey = SharedPreference.getString(KeyboardOfferwallWebView23Activity.this, Key.KEY_OCB_USER_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    mediaUserKey = cp.Decode(KeyboardOfferwallWebView23Activity.this, mediaUserKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String mediaUserPhone = "";
                String mediaUserEmail = "";
                OfferwallParticipationReq req = new OfferwallParticipationReq();
                req.setMission_seq(data.getMission_seq());
                req.setMission_id(data.getMission_id());
                req.setMedia_user_key(mediaUserKey);
                req.setMedia_user_phone(mediaUserPhone);
                req.setMedia_user_email(mediaUserEmail);

                offerwallParticipation(req, false);

            }
        });
    }

    private void getInit(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);        // javascript 에 의한 윈도우창 열기
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
    }

    private void setWebChromeClientCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            offerwallWebChromeClient.setCallback(new OfferwallWebChromeClient.WebChromeClientCallback() {
                @Override
                public void onChromePageStarted(WebView webView, String url) {

                }

                @Override
                public void onChromePageFinished(WebView webView, String url) {
                    LogPrint.d("onChromePageFinished called data.getCheck_url() :: " + data.getCheck_url());
                    LogPrint.d("onChromePageFinished called path                :: " + Uri.parse(url).getPath());
                    if ( !buttonEnabled ) {
                        if (countDownUrl == null) {
                            if ( url != null && data != null && data.getCheck_url() != null ) {
                                String path = Uri.parse(url).getPath();
                                LogPrint.d("path  1 :: " + path);
                                LogPrint.d("check 1 :: " + data.getCheck_url());
                                if (data.getCheck_url().contains(path)) {
                                    LogPrint.d("onPageFinished start PROGRESS");
                                    countDownUrl = url;
                                    startProgress();
                                    timer_layer.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            if ( !url.equals(countDownUrl) ) {
                                cancelTimer();
                                countDownUrl = null;
                                timer_layer.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                }

                @Override
                public void js_load(String js) {

                }

                @Override
                public void historyBack() {

                }

                @Override
                public void buttonActivation(String isActive) {

                }

                @Override
                public void getProductGuide(String json) {

                }

                @Override
                public void isGuideVisible(String isVisible) {
                    LogPrint.d("isGuideVisible isVisible 3 :: " + isVisible);
                }

                @Override
                public void buttonVisibility(String isVisible) {

                }

                @Override
                public void countdownStart(int count) {

                }

                @Override
                public void countdownCancel() {

                }

                @Override
                public void sendPoint(int mission_seq, String mission_id, String point) {

                }
            });
        }
    }

    private void setWebViewClientCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            offerwallWebViewClient.setCallback(new OfferwallWebViewClient.WebViewClientCallback() {
                @Override
                public void setWebViewBackAndForward(boolean isGoBack, boolean isGoForward) {

                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    LogPrint.d("setWebViewClientCallback onPageStarted");
                    if ( btn_get_reward != null && txt_get_reward != null ) {
                        btn_get_reward.setEnabled(buttonEnabled);
                        if ( buttonEnabled ) {
                            txt_get_reward.setTextColor(Color.parseColor("#ffffff"));
                            btn_get_reward.setBackgroundColor(Color.parseColor("#2f368e"));
                        } else {
                            txt_get_reward.setTextColor(Color.parseColor("#8f8f8f"));
                            btn_get_reward.setBackgroundColor(Color.parseColor("#dbdbdb"));
                        }
                    }

                    if ( !url.equals(countDownUrl) ) {
                        //카운트 다운 중에 url 이 변경된 경우
                        cancelTimer();
                        countDownUrl = null;
                    }

                    btn_get_reward.setEnabled(buttonEnabled);
                    /*
                    view.evaluateJavascript(insect_js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            LogPrint.d("value :: " + value);
                        }
                    });*/
                }

                @Override
                public void onPageFinished(WebView view, String prevUrl, String url) {
                    LogPrint.d("onPageFinished ");
                    if ( !buttonEnabled ) {
                        if (countDownUrl == null) {
                            if ( url != null && data != null && data.getCheck_url() != null ) {
                                String path = Uri.parse(url).getPath();
                                LogPrint.d("path  1 :: " + path);
                                LogPrint.d("check 1 :: " + data.getCheck_url());
                                if (data.getCheck_url().contains(path)) {
                                    LogPrint.d("onPageFinished start PROGRESS");
                                    countDownUrl = url;
                                    startProgress();
                                    timer_layer.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            if ( !url.equals(countDownUrl) ) {
                                cancelTimer();
                                countDownUrl = null;
                                timer_layer.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onReceivedError(String url, WebResourceError error) {

                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    LogPrint.d("shouldOverrideUrlLoading 3 url :: " + request.getUrl());
                    return false;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    LogPrint.d("shouldOverrideUrlLoading 1 url :: " + url);
                    return false;
                }
            });

        } else{
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    LogPrint.d("setWebViewClientCallback onPageStarted1");
                    if ( btn_get_reward != null && txt_get_reward != null ) {
                        btn_get_reward.setEnabled(buttonEnabled);
                        txt_get_reward.setTextColor(Color.parseColor("#8f8f8f"));
                        btn_get_reward.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    }

                    if ( !url.equals(countDownUrl) ) {
                        //카운트 다운 중에 url 이 변경된 경우
                        cancelTimer();
                        countDownUrl = null;
                    }

                    btn_get_reward.setEnabled(buttonEnabled);

                    view.evaluateJavascript(insect_js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            LogPrint.d("value :: " + value);
                        }
                    });
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    LogPrint.d("shouldOverrideUrlLoading 2 url :: " + url);
                    if (url.startsWith("http://") || url.startsWith("https://")){
                        if ( url.startsWith("http://") )
                            url = url.replaceAll("http://" , "https://");
                        view.loadUrl(url);
                        return true;
                    } else{
                        try {
                            boolean flag = OfferwallUtils.callApp(KeyboardOfferwallWebView23Activity.this, url);
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

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardOfferwallWebView23Activity.this);
                        builder.setTitle("")
                                .setMessage("이 사이트의 보안 인증서는 신뢰할 수 없습니다. \\n 계속하시겠습니까?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        if (handler != null)
                                            handler.proceed();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (view != null)
                                            view.goBack();
                                    }
                                })
                                .create().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    LogPrint.d("onPageFinished 1 ");
                    if (countDownUrl == null) {
                        if ( url != null && data != null && data.getCheck_url() != null ) {
                            String path = Uri.parse(url).getPath();
                            LogPrint.d("path  :: " + path);
                            LogPrint.d("check :: " + data.getCheck_url());
                            if (data.getCheck_url().contains(path)) {
                                countDownUrl = url;
                                startProgress();
                                timer_layer.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if ( !url.equals(countDownUrl) ) {
                            cancelTimer();
                            countDownUrl = null;
                            timer_layer.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void offerwallParticipation(OfferwallParticipationReq req, boolean isRetry) {
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOfferwallWebView23Activity.this);
        task.offerwallParticipation(req, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if ( object != null ) {
                            int rt = object.optInt("result");
                            if ( rt == 0 ) {
                                btn_get_reward.setEnabled(false);
                                btn_get_reward.setVisibility(View.GONE);
                                getSupportFragmentManager().beginTransaction().remove(loadingFragment).commit();
                                // ocb server에 성공 api 호출 후 해당 api 성공 시 아래 수행
                                int i_point = (int) data.getUser_point();
                                Toast.makeText(KeyboardOfferwallWebView23Activity.this, i_point + "P를 적립하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(KeyboardOfferwallListActivity.OFFERWALL_JOIN);
                                sendBroadcast(intent);
                            } else {
                                if (Common.IsFormissionTokenError(KeyboardOfferwallWebView23Activity.this, rt) ) {
                                    if ( !isRetry ) {
                                        CustomAsyncTask inTask = new CustomAsyncTask(KeyboardOfferwallWebView23Activity.this);
                                        inTask.getOfferwallToken(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                            @Override
                                            public void onResponse(boolean result, Object obj) {
                                                if ( result ) {
                                                    try {
                                                        JSONObject object = (JSONObject) obj;
                                                        if ( object != null ) {
                                                            int rt = object.optInt("result");
                                                            if ( rt == 0 ) {
                                                                String tk = object.optString("token");
                                                                LogPrint.d("received token :: " + tk);
                                                                SharedPreference.setString(KeyboardOfferwallWebView23Activity.this, Key.KEY_FORMISSION_TOKEN, tk);
                                                                offerwallParticipation(req, true);
                                                            } else {
                                                                // 토큰 획득 실패 후처리
                                                                Toast.makeText(KeyboardOfferwallWebView23Activity.this, getString(R.string.aikbd_offerwall_fail_toast_message), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    // 실패 후처리
                                    String msg = object.optString("msg");
                                    if ( !TextUtils.isEmpty(msg) ) {
                                        Toast.makeText(KeyboardOfferwallWebView23Activity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void completeMission() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ( btn_get_reward != null && txt_get_reward != null ) {
                    buttonEnabled = true;
                    btn_get_reward.setEnabled(buttonEnabled);
                    txt_get_reward.setTextColor(Color.parseColor("#ffffff"));
                    btn_get_reward.setBackgroundColor(Color.parseColor("#2f368e"));
                    btn_get_reward.invalidate();
                    txt_get_reward.invalidate();
                }
            }
        });
    }

    private void cancelTimer() {
        if ( countDownTimer != null )
            countDownTimer.cancel();
    }

    private void startProgress() {

        int second = data.getCheck_time();
        /*
        String sec = second + "초";
        SpannableString spannableString = new SpannableString(second + "초");
        spannableString.setSpan(new StyleSpan(Typeface.NORMAL), sec.length() - 1, sec.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        count_time.setText(spannableString);
         */
        countDownTimer = new CountDownTimer(second * 1000, 100) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                LogPrint.d("onTick seconds :: " + seconds);
                String second = seconds + "초";
                SpannableString spannableString = new SpannableString(second);
                spannableString.setSpan(new StyleSpan(Typeface.NORMAL), second.length() - 1, second.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                count_time.setText(spannableString);

                // format the textview to show the easily readable format

            }
            @Override
            public void onFinish() {
                timer_layer.setVisibility(View.GONE);
                completeMission();
            }
        }.start();
    }

    public class MyJavaScriptInterface {
        @JavascriptInterface
        public void completedMission() {
            LogPrint.d("Mission Complete!!!");
            completeMission();
        }

        @JavascriptInterface
        public void sendNaverId(String id) {
            LogPrint.d("n_Id : " + id);
            SharedPreference.setString(KeyboardOfferwallWebView23Activity.this, Key.KEY_N_ID, id);
        }
    }
}
