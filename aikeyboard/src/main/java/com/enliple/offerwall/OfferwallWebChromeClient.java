package com.enliple.offerwall;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.enliple.keyboard.activity.KeyboardHybridOfferwallActivity;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class OfferwallWebChromeClient extends WebChromeClient {
    private ArrayList<WebView> mChildWebViewList;
    private ConstraintLayout mLayout;
    private FrameLayout mPopupLayout;
    private WebChromeClientCallback callback;
    private WebView mMainWebView;
    private WebView childWebView;
    private ProgressBar mProgressBar;

    boolean isOpen = false;

    public OfferwallWebChromeClient(ArrayList<WebView> childWebViewList, WebView mainWebView, ProgressBar progressBar, ConstraintLayout layout,
                                    FrameLayout popupLayout, WebChromeClientCallback callback) {
        this.mChildWebViewList = childWebViewList;
        this.mMainWebView = mainWebView;
        this.mProgressBar = progressBar;
        this.mLayout = layout;
        this.mPopupLayout = popupLayout;

        this.callback = callback;

    }

    public OfferwallWebChromeClient(ArrayList<WebView> childWebViewList, WebView mainWebView, ProgressBar progressBar, ConstraintLayout layout,
                                    FrameLayout popupLayout) {
        this.mChildWebViewList = childWebViewList;
        this.mMainWebView = mainWebView;
        this.mProgressBar = progressBar;
        this.mLayout = layout;
        this.mPopupLayout = popupLayout;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        LogPrint.d("onCreateWindow view url :: " + view.getUrl());
        String vUrl = view.getUrl();

        String targetUrl = "https://ocbapi.cashkeyboard.co.kr/API/OCB/offerwall/index.php";
        String outUrl = "https://www.pomission.com/bridge/";
        if ( vUrl.contains(targetUrl) || vUrl.contains(outUrl)) {
            LogPrint.d("new open");
            // 광고 클릭 시
            WebView newWebView = new WebView(view.getContext());
            view.addView(newWebView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    LogPrint.d("new open newWebView url :: " + url);

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(url));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        LogPrint.d("util mobwith click");
                        view.getContext().startActivity(browserIntent);
/*
                        String loadAd = "javascript:loadAd();";
                        mMainWebView.loadUrl(loadAd);
*/
                        if(mMainWebView != null) {
                            LogPrint.d("offerwall ad click 1");
                            mMainWebView.loadUrl("javascript:mixerClickFn();");
                        }

                    } catch (ActivityNotFoundException e) {
                        view.getContext().startActivity(Intent.createChooser(browserIntent, "Title"));
                    }

                    if (url.contains("//img.mobon.net/ad/linfo.php"))
                        mMainWebView.goBack();
                    return true;
                }
            });
            return true;
        } else {
            LogPrint.d("not ad url");
            childWebView = new WebView(view.getContext());
            getInit(childWebView);
            childWebView.setLayoutParams(view.getLayoutParams());
            childWebView.setWebChromeClient(this);
            // hybrid 작업 시 bridge 추가
            childWebView.addJavascriptInterface(new PomissionBridge(), "HybridApp");
            childWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("")
                                .setMessage("이 사이트의 보안 인증서는 신뢰할 수 없습니다. \\n 계속하시겠습니까?")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        if ( handler != null )
                                            handler.proceed();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if ( view != null )
                                            view.goBack();
                                    }
                                })
                                .create().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override

                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    LogPrint.d("chrome onPageStarted url :: " + url);
                    try {
                        if (!isOpen) {
                            if ( mPopupLayout != null )
                                mPopupLayout.setVisibility(View.VISIBLE);
                            isOpen = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.onPageStarted(view, url, favicon);

                    if ( callback != null )
                        callback.onChromePageStarted(view, url);
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(final WebView view, String url) {
                    super.onPageFinished(view, url);
                    LogPrint.d("chrome onPageFinished url :: " + url);
                    try {
                        if ( callback != null )
                            callback.onChromePageFinished(view, url);
                        mProgressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    try {
                        LogPrint.d("chrome shouldOverrideUrlLoading url :: " + url);
                        if ( url == null )
                            return false;
                        if ((url.startsWith("http://") || url.startsWith("https://"))
                                && (url.contains("Oauth2ClientCallback/kakao") || url.contains("Oauth2ClientCallback/naver"))
                        ) {
                            mMainWebView.loadUrl(url);
                            closeChild();
                            closePopup();
                            return true;
                        } else if (url.startsWith("http://") || url.startsWith("https://")) {
                            super.shouldOverrideUrlLoading(view, url);
                        } else
                            return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return false;
                }
            });

            // 화면에 추가하기
            if ( childWebView != null ) {
                if ( mLayout != null )
                    mLayout.addView(childWebView);
                if ( mChildWebViewList != null )
                    mChildWebViewList.add(childWebView);
            }
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(childWebView);
            resultMsg.sendToTarget();

            return true;
        }
    }

    public void setCallback(WebChromeClientCallback callback) {
        this.callback = callback;
    }

    // 창을 닫을 경우 팝업 웹뷰를 사라지게 함
    private void closeChild() {
        try {
            isOpen = false;
            if (mPopupLayout != null)
                mPopupLayout.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 팝업 버튼 닫기
    private void closePopup() {
        if (childWebView != null)
            childWebView.loadUrl("javascript:window.close()");
    }

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
        LogPrint.d("onCloseWindow called");
        if ( mLayout != null ) {
            LogPrint.d("mLayout remove window");
            mLayout.removeView(window);
        }
        if ( mChildWebViewList != null && window != null ) {
            LogPrint.d("mChildWebViewList remove window");
            mChildWebViewList.remove(window);
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress >= 100) {
            mProgressBar.setVisibility(View.GONE);

        } else {
            mProgressBar.setProgress(newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        try{
            if (view != null && message != null){
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if ( result != null )
                                        result.confirm();
                                }
                            })
                            .create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        try{
            if (view != null && message != null){
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if ( result != null )
                                        result.confirm();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if ( result != null )
                                        result.cancel();
                                }
                            })
                            .create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     WebChromeClient.FileChooserParams fileChooserParams) {
        try {
            if ( callback != null)
                callback.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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

    public interface WebChromeClientCallback {
        void onChromePageStarted(WebView webView, String url);
        void onChromePageFinished(WebView webView, String url);
        void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams);

        void js_load(String js);
        void historyBack();
        void buttonActivation(String isActive);
        void getProductGuide(String json);
        void isGuideVisible(String isVisible);
        void buttonVisibility(String isVisible);
        void countdownStart(int count);
        void countdownCancel();
        void sendPoint(int mission_seq, String mission_id, String point);

    }
    
    private class PomissionBridge {
        @JavascriptInterface
        public void js_load(String js) {
            if ( callback != null )
                callback.js_load(js);
        }

        @JavascriptInterface
        public void historyBack() {
            LogPrint.d("historyBack called");
            if ( callback != null )
                callback.historyBack();
        }

        // 버튼 활성화 유무 호출
        @JavascriptInterface
        public void buttonActivation(String isActive) {
            LogPrint.d("buttonActivation isActive :: " + isActive);
            if ( callback != null )
                callback.buttonActivation(isActive);
        }

        @JavascriptInterface
        public void getProductGuide(String json) {
            LogPrint.d("getProductGuide json 1 :: " + json);
            if ( callback != null )
                callback.getProductGuide(json);
        }

        @JavascriptInterface
        public void isGuideVisible(String isVisible) {
            LogPrint.d("isGuideVisible isVisible 1 :: " + isVisible);
            if ( callback != null )
                callback.isGuideVisible(isVisible);
        }

        // 버튼 노출 유무 호출
        @JavascriptInterface
        public void buttonVisibility(String isVisible) {
            LogPrint.d("buttonVisibility isVisible 1 :: " + isVisible);
            if ( callback != null )
                callback.buttonVisibility(isVisible);
        }

        // 타이머 시작 시 호출
        @JavascriptInterface
        public void countdownStart(String ct) {
            LogPrint.d("countdownStart count 2 :: " + ct);
            int cnt = -1;
            try {
                cnt = Integer.valueOf(ct);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ( callback != null )
                callback.countdownStart(cnt);
        }

        // 타이머 취소 시 호출
        @JavascriptInterface
        public void countdownCancel() {
            if ( callback != null )
                callback.countdownCancel();
        }

        // 즉시 적립 시 호출
        @JavascriptInterface
        public void sendPoint(int mission_seq, String mission_id, String point) {
            if ( callback != null )
                callback.sendPoint(mission_seq, mission_id, point);
        }
    }
}
