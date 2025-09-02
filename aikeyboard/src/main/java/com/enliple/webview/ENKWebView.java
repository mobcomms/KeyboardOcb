package com.enliple.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

public class ENKWebView extends WebView {


    static String TAG = "ENKWebView";
    Context context;

    WebSettings webSettings;
    WebViewClient mCustomWebViewClient;

    protected Listener mListener;

    protected String mLanguageIso3;


    boolean isFirstOpen = false;


    /**
     * File upload callback for platform versions prior to Android 5.0
     */
    public ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * File upload callback for Android 5.0+
     */
    public ValueCallback<Uri[]> mFileUploadCallbackSecond;

    public ValueCallback<Uri[]> mFilePathCallback = null;

    protected static final int REQUEST_CODE_FILE_PICKER = 51426;
    protected static final int REQUEST_CODE_CAMERA_PICKER = 51427;
    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;
    protected int mRequestCodeCameraPicker = REQUEST_CODE_CAMERA_PICKER;
    protected String mUploadableFileTypes = "*/*";
    protected static final String CHARSET_DEFAULT = "UTF-8";

    public String LOCAL_KEY_TOKEN = "token";

    protected WeakReference<Activity> mActivity;
//    protected WeakReference<Fragment> mFragment;

    private Activity currentActivity;
    public interface Listener {
        void onPageStarted(String url, Bitmap favicon);

        void onPageFinished(String url);

        void onPageError(int errorCode, String description, String failingUrl);

        void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent);

        void onExternalPageRequest(String url);

        void shouldInterceptRequest(String url);
        boolean onJsConfirm(String msg, JsResult result);

        void showAd(String data);
    }

    public void setListener(final Activity activity, final Listener listener) {
        setListener(activity, listener, REQUEST_CODE_FILE_PICKER);
    }

    public void setListener(final Activity activity, final Listener listener, final int requestCodeFilePicker) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
            currentActivity = activity;
        } else {
            mActivity = null;
        }

        setListener(listener, requestCodeFilePicker);
    }

    protected void setListener(final Listener listener, final int requestCodeFilePicker) {
        mListener = listener;
        mRequestCodeFilePicker = requestCodeFilePicker;
    }

    public ENKWebView(Context context) {
        this(context, null);
    }

    public ENKWebView(Context context, AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("webViewStyle", "attr", "android"));
    }

    public ENKWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        webSettings = this.getSettings(); // 웹뷰에서 webSettings를 사용할 수 있도록 함.
        webSettings.setJavaScriptEnabled(true); //웹뷰에서 javascript를 사용하도록 설정
        webSettings.setSupportMultipleWindows(true);//                                               `
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //멀티윈도우 띄우는 것
        webSettings.setAllowFileAccess(true); //파일 엑세스
        webSettings.setLoadWithOverviewMode(true); // 메타태그
        webSettings.setUseWideViewPort(true); //화면 사이즈 맞추기
        webSettings.setSupportZoom(false); // 화면 줌 사용 여부
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 브라우저 캐시 사용 재정의 value : LOAD_DEFAULT, LOAD_NORMAL, LOAD_CACHE_ELSE_NETWORK, LOAD_NO_CACHE, or LOAD_CACHE_ONLY
        webSettings.setDefaultFixedFontSize(14); //기본 고정 글꼴 크기, value : 1~72 사이의 숫자
        webSettings.setTextZoom(100);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        setAllowAccessFromFileUrls(webSettings, true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        clearCache(true);
        clearHistory();
        clearCookies(context);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mCustomWebViewClient = new WebViewClientClass();

//        webView.setListener(this, this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.setWebContentsDebuggingEnabled(true);
        }

        this.addJavascriptInterface(new AndroidBridge(), "Native");
//        this.setWebChromeClient(new WebChromeClient());
        this.setWebViewClient(mCustomWebViewClient);

        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(currentActivity);
                view.addView(newWebView);
                WebViewTransport transport = (WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
                            currentActivity.startActivity(browserIntent);

                        } catch (ActivityNotFoundException e) {
//                            startActivity(Intent.createChooser(browserIntent, "Title"));

                        }

                        return true;
                    }
                });
                return true;
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                LogPrint.d("console message :: " + message);

            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadMainURL(String url) {
        LogPrint.d("game url : " + url);
        loadUrl(url); //연결할 url


    }
    public void sendScript(String event,JSONObject data )  {
        try{
            JSONObject sendData = new JSONObject();
            sendData.put("event", event);
            sendData.put("data", data == null ? "" : data);
            LogPrint.d("sendScript :: " + sendData.toString());
//
            post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript:recvNative('" + sendData.toString() + "')");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            LogPrint.e( "onPageFinished : " + url);

            if (!isFirstOpen) {

                isFirstOpen = true;

                try {
                    String id = SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
                    try {
                        E_Cipher cp = E_Cipher.getInstance();
                        id = cp.Decode(context, id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LogPrint.d("init id :: " + id);
                    JSONObject senddata = new JSONObject();
                    senddata.put("userKey", id);
                    senddata.put("companyCode", "okcashbag");

                    sendScript("init",senddata);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LogPrint.e( "onPageFinished isFirstOpen : else" );

            }
        }

        @Override
        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {

            if (handler != null) {

//                handler.proceed();

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("보안 인증서에 문제가 있습니다.");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (handler != null) {

                            handler.proceed();
                        } else {
                            if (mCustomWebViewClient != null) {

                                mCustomWebViewClient.onReceivedSslError(view, handler, error);
                            } else {

//                                super.onReceivedSslError(view, handler, error);
                            }
                        }

//                    if (handler != null)
//                        handler.proceed();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                //                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onWindowVisibilityChanged(int visibility) {

        if (visibility != View.GONE) {
            super.onWindowVisibilityChanged(visibility);
        } else {
            super.onWindowVisibilityChanged(View.VISIBLE);
        }
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("NewApi")
    protected static void setAllowAccessFromFileUrls(final WebSettings webSettings,
                                                     final boolean allowed) {
        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(allowed);
            webSettings.setAllowUniversalAccessFromFileURLs(allowed);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public class AndroidBridge {

        @JavascriptInterface
        public void RecvMOAEvent(String data) {
            try {
                JSONObject webData = new JSONObject(data);
                LogPrint.e( "WebView RecvMOAEvent : " + data);

                LogPrint.e( "WebView Data : " + webData);
                String event;
                event = webData.getString("event");
                LogPrint.e( "WebView Data event : " + event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void onAppEvent(String data) {
            try {
                JSONObject webData = new JSONObject(data);
                String event, p_data;
                event = webData.getString("event");
                p_data = webData.getString("data");

                String result;


                switch (event) {
                    case "exit":
                        currentActivity.finish();
                        break;
                    case "showAD":
                        // 광고뷰 노출
                        mListener.showAd(p_data);
//                        String url = webData.getString("data");
//
//                        currentActivity.finish();
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        currentActivity.startActivity(intent);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();

            cookieSyncMngr.sync();
        }
    }

}
