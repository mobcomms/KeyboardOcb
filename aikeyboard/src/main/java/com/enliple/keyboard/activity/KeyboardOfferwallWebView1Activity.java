package com.enliple.keyboard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.enliple.keyboard.R;
import com.enliple.keyboard.ui.common.LogPrint;

import java.net.URISyntaxException;
import java.util.List;


public class KeyboardOfferwallWebView1Activity extends AppCompatActivity {
    private final static int FILE_CHOOSER_REQ = 0;
    private WebView webView;
    private TextView btn_back;
    private String landingUrl = "";
    private ValueCallback mFilePathCallback;
    public static Activity mActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_offerwall_webview_1);

        LogPrint.d("KeyboardOfferwallWebViewActivity onCreate");
        Intent intent = getIntent();
        if ( intent != null ) {
            landingUrl = intent.getStringExtra("landingUrl");
            LogPrint.d("landingUrl :: " + landingUrl);
            if (TextUtils.isEmpty(landingUrl) )
                finish();
        } else {
            finish();
        }
        LogPrint.d("KeyboardOfferwallWebView1Activity landingUrl :: " + landingUrl);
        initViews();
    }

    public void onBackPressed() {
        if ( webView != null ) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void initViews() {
        mActivity = this;
        btn_back = findViewById(R.id.btn_back);
        webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogPrint.d("kskk webview  url :: " + url);
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    LogPrint.d("kskk url contain http or https");
                    if ( url.startsWith("http://") )
                        url = url.replaceAll("http://" , "https://");
                    LogPrint.d("after url :: " + url);
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    try {
                        boolean flag = callApp(KeyboardOfferwallWebView1Activity.this, url.toString());
                        LogPrint.d("flag :: " + flag);
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
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogPrint.d("onPageStarted :: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogPrint.d("onPageFinished :: " + url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardOfferwallWebView1Activity.this);
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
            public void onReceivedError(WebView view, WebResourceRequest
                    request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    LogPrint.d("onReceivedError :: " + error.toString() + " , error code :: " + error.getErrorCode()
                    + " , error description :: " + error.getDescription().toString());
                    if (error.getErrorCode() == -1)
                        return;
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                LogPrint.d("onConsoleMessage :: " + message + " , lineNumber :: " + lineNumber + " , sourceID :: " + sourceID);
            }

            /* Android 5.0 이상 카메라 - input type="file" 태그를 선택했을 때 반응 처리 */
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                LogPrint.d("***** onShowFileChooser()");
                //Callback 초기화
                //return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);

                /* 파일 업로드 */
                if (mFilePathCallback != null) {
                    //파일을 한번 오픈했으면 mFilePathCallback 를 초기화를 해줘야함
                    // -- 그렇지 않으면 다시 파일 오픈 시 열리지 않는 경우 발생
                    mFilePathCallback.onReceiveValue(null);
                    mFilePathCallback = null;
                }
                mFilePathCallback = filePathCallback;

                //권한 체크
//            if(권한 여부) {
                /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ) {
                    LogPrint.d("over 13 image upload request");
                    Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                    intent.setType("image/*"); // 이미지 파일 타입 지정
                    startActivityForResult(intent, 0);
                } else {
                    LogPrint.d("under 13 image upload request");
                    Intent intent = new Intent(Intent.ACTION_PICK); // 갤러리 지정
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false); // 사진 다중 선택 지정
                    intent.setType("image/*"); // 이미지 파일 타입 지정
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // 미디어 스토어 사용 [이미지]
                    startActivityForResult(intent, 0); // 응답 코드 0 삽입
                }*/
                //권한이 있으면 처리
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
                    Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                    intent.setType("image/*");
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");  //모든 contentType 파일 표시
                    startActivityForResult(intent, 0);
                }

//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("*/*");  //모든 contentType 파일 표시
//                startActivityForResult(intent, 0);

//            } else {

                //권한이 없으면 처리

//            }

                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result){
                LogPrint.d("onJsAlert message :: " + message + " , url :: " + url);
                final JsResult finalRes = result;
                if ( !KeyboardOfferwallWebView1Activity.this.isFinishing() ) {
                    new AlertDialog.Builder(KeyboardOfferwallWebView1Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finalRes.confirm();
                                            Intent intent = new Intent(KeyboardOfferwallListActivity.OFFERWALL_JOIN);
                                            sendBroadcast(intent);
                                            finish();
                                        }
                                    })
                            .create()
                            .show();
                }
                return true;
            }
        });

        getInit(webView);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        webView.loadUrl(landingUrl);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }*/
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
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
                        try {
                            Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            retval = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            retval = false;
                        }
                    }
                } else {
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
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return retval;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogPrint.d("***** onActivityResult() - requestCode : "+requestCode);
        LogPrint.d("***** onActivityResult() - resultCode : "+resultCode);
        LogPrint.d("***** onActivityResult() - data : "+data);
        /* 파일 선택 완료 후 처리 */
        switch(requestCode) {
            case FILE_CHOOSER_REQ:
                //fileChooser 로 파일 선택 후 onActivityResult 에서 결과를 받아 처리함
                if(resultCode == RESULT_OK) {
                    //파일 선택 완료 했을 경우
                    if ( mFilePathCallback != null ) {
                        LogPrint.d("upload filePath callback not null");
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            LogPrint.d("upload parseResult data :: " + data.getData().toString());
                            mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                        }else{
                            LogPrint.d("upload parseResult data under lollipop :: " + data.getData().toString());
                            mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
                        }
                    } else {
                        LogPrint.d("upload filePath callback null");
                    }
                    mFilePathCallback = null;
                } else {
                    LogPrint.d("upload result not ok");
                    //cancel 했을 경우
                    if(mFilePathCallback != null) {
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
}
