package com.enliple.keyboard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.models.OfferwallData;
import com.enliple.keyboard.models.OfferwallParticipationReq;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.network.Url;
import com.enliple.keyboard.ui.common.Common;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.offerwall.OfferwallLoadingFragment;
import com.enliple.offerwall.OfferwallUtils;
import com.enliple.offerwall.OfferwallWebChromeClient;
import com.enliple.offerwall.OfferwallWebViewClient;
import com.enliple.offerwall.OnKeyboardSingleClickListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class KeyboardOfferwallWebViewActivity extends AppCompatActivity {
    private ConstraintLayout root;
    private OfferwallLoadingFragment loadingFragment;
    private TextView btn_back;
    private ProgressBar progressBar;
    private WebView webView;
    private FrameLayout popupLayout;
    private ConstraintLayout btn_get_reward;
    private TextView txt_get_reward;
    private OfferwallData data;
    private String insect_js = "";
    private OfferwallWebViewClient offerwallWebViewClient;
    private OfferwallWebChromeClient offerwallWebChromeClient;
    private ArrayList<WebView> childWebViewList = new ArrayList<>();
    public static Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_offerwall_webview);
        LogPrint.d("KeyboardOfferwallWebViewActivity onCreate");
        Intent intent = getIntent();
        if ( intent != null ) {
            data = (OfferwallData)intent.getSerializableExtra("intent_mission");
            if ( data == null )
                finish();
        } else {
            finish();
        }
        initViews();
    }

    public void onBackPressed() {
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
        insect_js = "document.addEventListener(\"DOMContentLoaded\", function(){ " +
                "var my_awesome_script = document.createElement('script');" +
                "my_awesome_script.setAttribute('src','" + Url.OFFERWALL_FILE_PATH + data.getMission_class() + "/tracker.js');" +
                "document.head.appendChild(my_awesome_script);" +
                "});";
        LogPrint.d("insect_js :: " + insect_js);

        root = findViewById(R.id.root);
        btn_back = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        popupLayout = findViewById(R.id.popupLayout);
        btn_get_reward = findViewById(R.id.btn_get_reward);
        txt_get_reward = findViewById(R.id.txt_get_reward);

        loadingFragment = OfferwallLoadingFragment.getInstance();
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "knowhow");

        offerwallWebViewClient = new OfferwallWebViewClient(KeyboardOfferwallWebViewActivity.this, progressBar);
        offerwallWebChromeClient = new OfferwallWebChromeClient(childWebViewList, webView, progressBar, root, popupLayout);
        webView.setWebViewClient(offerwallWebViewClient);
        webView.setWebChromeClient(offerwallWebChromeClient);
        setWebViewClientCallback();
        setWebChromeClientCallback();

        getInit(webView);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        webView.loadUrl(data.getAdver_url());

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

                String mediaUserKey = SharedPreference.getString(KeyboardOfferwallWebViewActivity.this, Key.KEY_OCB_USER_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    mediaUserKey = cp.Decode(KeyboardOfferwallWebViewActivity.this, mediaUserKey);
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
                    LogPrint.d("isGuideVisible isVisible 7 :: " + isVisible);
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
                        btn_get_reward.setEnabled(false);
                        txt_get_reward.setTextColor(Color.parseColor("#8f8f8f"));
                        btn_get_reward.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    }

                    view.evaluateJavascript(insect_js, new ValueCallback<String>() {                        @Override
                        public void onReceiveValue(String value) {
                            LogPrint.d("value :: " + value);
                        }
                    });
                }

                @Override
                public void onPageFinished(WebView view, String prevUrl, String url) {

                }

                @Override
                public void onReceivedError(String url, WebResourceError error) {

                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
                        btn_get_reward.setEnabled(false);
                        txt_get_reward.setTextColor(Color.parseColor("#8f8f8f"));
                        btn_get_reward.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    }
                    view.evaluateJavascript(insect_js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            LogPrint.d("value 1 :: " + value);
                        }
                    });
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("http://") || url.startsWith("https://")){
                        view.loadUrl(url);
                        return true;
                    } else{
                        try {
                            boolean flag = OfferwallUtils.callApp(KeyboardOfferwallWebViewActivity.this, url);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardOfferwallWebViewActivity.this);
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
            });
        }
    }

    private void offerwallParticipation(OfferwallParticipationReq req, boolean isRetry) {
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOfferwallWebViewActivity.this);
        task.offerwallParticipation(req, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if ( object != null ) {
                            int rt = object.optInt("result");
                            if ( rt == 0 ) {
                                getSupportFragmentManager().beginTransaction().remove(loadingFragment).commit();
                                // ocb server에 성공 api 호출 후 해당 api 성공 시 아래 수행
                                Toast.makeText(KeyboardOfferwallWebViewActivity.this, data.getUser_point() + "P를 적립하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(KeyboardOfferwallListActivity.OFFERWALL_JOIN);
                                sendBroadcast(intent);
                            } else {
                                if (Common.IsFormissionTokenError(KeyboardOfferwallWebViewActivity.this, rt) ) {
                                    if ( !isRetry ) {
                                        CustomAsyncTask inTask = new CustomAsyncTask(KeyboardOfferwallWebViewActivity.this);
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
                                                                offerwallParticipation(req, true);
                                                            } else {
                                                                // 토큰 획득 실패 후처리
                                                                Toast.makeText(KeyboardOfferwallWebViewActivity.this, getString(R.string.aikbd_offerwall_fail_toast_message), Toast.LENGTH_SHORT).show();
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
                    btn_get_reward.setEnabled(true);
                    txt_get_reward.setTextColor(Color.parseColor("#ffffff"));
                    btn_get_reward.setBackgroundColor(Color.parseColor("#fe0955"));
                    btn_get_reward.invalidate();
                    txt_get_reward.invalidate();
                }
            }
        });
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
            SharedPreference.setString(KeyboardOfferwallWebViewActivity.this, Key.KEY_N_ID, id);
        }
    }
}
