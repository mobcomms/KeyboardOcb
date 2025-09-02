package com.enliple.keyboard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

public class KeyboardFAQActivity extends AppCompatActivity {
    private final static int FILE_CHOOSER_REQ = 0;
    private String faq_url = "https://ocbapi.cashkeyboard.co.kr/API/OCB/inquiry/index.php";
    private String link;
    private WebView webview;
    private RelativeLayout white;
    private ImageView ivLoading;
    private ValueCallback mFilePathCallback;
    private boolean isWebPageError = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_faq);
        View root_layout = findViewById(R.id.root_layout);
        Common.SetInset(root_layout);
        String id = SharedPreference.getString(KeyboardFAQActivity.this, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(KeyboardFAQActivity.this, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ( TextUtils.isEmpty(id) )
            finish();
        link = faq_url + "?uuid=" + id;
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        white = findViewById(R.id.white);
        ivLoading = findViewById(R.id.ivLoading);
        webview = findViewById(R.id.webview);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setBackgroundResource(R.drawable.aikbd_white_bg);
        webview.addJavascriptInterface(new FAQBridge(), "HybridApp");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogPrint.d("webview client onPageStarted url :: " + url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogPrint.d("kskk webview  url :: " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    LogPrint.d("error :: " + error.getErrorCode());
                }
                if ( error != null ) {
                    isWebPageError = true;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogPrint.d("errorCode :: " + errorCode);
                isWebPageError = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogPrint.d("webview client onPageFinished url :: " + url);
                if ( url.startsWith(faq_url) ) {
                    white.setVisibility(View.GONE);
                }

                if ( isWebPageError ) {
                    white.setVisibility(View.VISIBLE);
                    Toast.makeText(KeyboardFAQActivity.this, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                /* 파일 업로드 */
                if (mFilePathCallback != null) {
                    LogPrint.d("upload mFilePathCallback not null");
                    //파일을 한번 오픈했으면 mFilePathCallback 를 초기화를 해줘야함
                    // -- 그렇지 않으면 다시 파일 오픈 시 열리지 않는 경우 발생
                    mFilePathCallback.onReceiveValue(null);
                    mFilePathCallback = null;
                }
                mFilePathCallback = filePathCallback;

                //권한이 있으면 처리
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");  //모든 contentType 파일 표시
                startActivityForResult(intent, 0);
                return true;
            }
        });
        webview.clearHistory();                                            // 히스토리 초기화 (?)
        webview.clearCache(true);                                          // 웹뷰 캐시 삭제

        WebSettings webSettings = webview.getSettings();
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
        webview.loadUrl(link);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogPrint.d("onNewIntent called KeyboardFAQActivity");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogPrint.d("***** upload onActivityResult() - requestCode : " + requestCode);
        LogPrint.d("***** upload onActivityResult() - resultCode : " + resultCode);
        LogPrint.d("***** upload onActivityResult() - data : " + data);
        /* 파일 선택 완료 후 처리 */
        switch (requestCode) {
            case FILE_CHOOSER_REQ:
                //fileChooser 로 파일 선택 후 onActivityResult 에서 결과를 받아 처리함
                if (resultCode == RESULT_OK) {
                    //파일 선택 완료 했을 경우
                    if (mFilePathCallback != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            LogPrint.d("upload onActivityResult onReceiveValue :: " + data.getData().toString());
                            mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                        } else {
                            LogPrint.d("upload onActivityResult onReceiveValue 1 :: " + data.getData().toString());
                            mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
                        }
                    }
                    mFilePathCallback = null;
                } else {
                    //cancel 했을 경우
                    if (mFilePathCallback != null) {
                        mFilePathCallback.onReceiveValue(null);
                        mFilePathCallback = null;
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class FAQBridge {
        @JavascriptInterface
        public void showMessage(String message) {
            Toast.makeText(KeyboardFAQActivity.this, message, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
