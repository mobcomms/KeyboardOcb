package com.enliple.offerwall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.enliple.keyboard.ui.common.LogPrint;

import java.net.URISyntaxException;
import java.util.List;

public class OfferwallWebViewClient extends WebViewClient {
    private Context context;
    private ProgressBar progressBar;
    private WebViewClientCallback callback = null;
    private String prevFinishUrl = "";

    public OfferwallWebViewClient(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    public void setCallback(WebViewClientCallback callback){
        this.callback = callback;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        LogPrint.d("web onPageFinished url :: " + url);
        if ( url.contains("about:blank") )
            return;

        if ( callback != null ) {
            callback.onPageFinished(view, prevFinishUrl, url);
            prevFinishUrl = url;
        }
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);


    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogPrint.d("web onPageStarted url :: " + url);
        if ( callback != null ) {
            callback.onPageStarted(view, url, favicon);
            callback.setWebViewBackAndForward(view.canGoBack(), view.canGoForward());
        }

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogPrint.d("web shouldOverrideUrlLoading 4 :: " + url);
        if ( callback != null )
            callback.shouldOverrideUrlLoading(view, url);

        if ( url.startsWith("http://") || url.startsWith("https://") ) {
            //view.loadUrl(url);
            //return true;
            return super.shouldOverrideUrlLoading(view, url);
        } else {
            try {
                boolean flag = OfferwallUtils.callApp((Activity)context, url.toString());
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
    public void onReceivedError(WebView view, WebResourceRequest
            request, WebResourceError error) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LogPrint.d("offerwall error received error description :: " + error.getDescription());
        }
        super.onReceivedError(view, request, error);
        if ( callback != null ) {
            callback.onReceivedError("", error);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view,errorCode, description, failingUrl);
        if ( callback != null ) {
            callback.onReceivedError("", null);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("")
                    .setMessage("이 사이트의 보안 인증서는 신뢰할 수 없습니다. \\n 계속하시겠습니까?")
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

    public void showAlert (String message, String positiveButton,
                           DialogInterface.OnClickListener positiveListener, String negativeButton, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
               .setPositiveButton(positiveButton, positiveListener)
               .setNegativeButton(negativeButton, negativeListener)
               .show();
    }

    public interface WebViewClientCallback {
        void setWebViewBackAndForward(boolean isGoBack, boolean isGoForward);
        void onPageStarted(WebView view, String url, Bitmap favicon);
        void onPageFinished(WebView view, String prevUrl, String url);
        void onReceivedError(String url, WebResourceError error);
        boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);
        boolean shouldOverrideUrlLoading(WebView view, String url);
    }
}
