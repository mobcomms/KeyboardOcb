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
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.common.CustomFont;
import com.enliple.keyboard.mobonAD.MobonUtils;
import com.enliple.keyboard.models.OfferwallParticipationReq;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Common;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.offerwall.OfferwallLoadingFragment;
import com.enliple.offerwall.OfferwallUtils;
import com.enliple.offerwall.OfferwallWebChromeClient;
import com.enliple.offerwall.OfferwallWebViewClient;
import com.enliple.offerwall.OnKeyboardSingleClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static android.os.ext.SdkExtensions.getExtensionVersion;

public class KeyboardHybridOfferwallActivity extends AppCompatActivity {
    private final static int FILE_CHOOSER_REQ = 0;
    private ConstraintLayout root, web_parent;
    private TextView btn_back;
    private RelativeLayout white;
    private ImageView ivLoading;
    private OfferwallLoadingFragment loadingFragment;
    private ProgressBar progressBar;
    private WebView webView;
    private FrameLayout popupLayout;
    private ConstraintLayout btn_get_reward;
    private TextView txt_get_reward;
    private TextView count_time;
    private TextView txt_guide;
    private TextView main_title;
    private RelativeLayout timer_layer;
    private CountDownTimer countDownTimer;
    private String insect_js = "";
    private OfferwallWebViewClient offerwallWebViewClient;
    private OfferwallWebChromeClient offerwallWebChromeClient;
    private ArrayList<WebView> childWebViewList = new ArrayList<>();
    public static Activity mActivity;
    private ValueCallback mFilePathCallback;
    private boolean isWebPageError = false;
    private String jsString = null;

//    ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new PickVisualMedia(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri result) {
//                    int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
//                    getContentResolver().takePersistableUriPermission(result, flag);
//
//                    if ( mFilePathCallback != null ) {
//                        LogPrint.d("onActivityResult uri :: " + result.toString());
//                        mFilePathCallback.onReceiveValue(new Uri[]{result});
//                    }
//                    mFilePathCallback = null;
//                }
//            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_offerwall_hybrid);
        LogPrint.d("KeyboardHybridOfferwallActivity onCreate");
        initViews();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogPrint.d("onNewIntent called KeyboardHybridOfferwallActivity");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void onBackPressed() {
        LogPrint.d("childWebViewList.size :: " + childWebViewList.size());
        if (childWebViewList.size() > 0) {
            int lastIdx = childWebViewList.size() - 1;
            LogPrint.d("lastIdx :: " + lastIdx);
            WebView wv = childWebViewList.get(lastIdx);

            if (wv.canGoBack()) {
                LogPrint.d("back 1");
                wv.goBack();
            } else {
                LogPrint.d("back 2");
                wv.loadUrl("javascript:window.close()");
            }
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                LogPrint.d("back 4");
                finish();
            }
        }
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
                        /*
                        temp_image.setVisibility(View.VISIBLE);
                        Uri uri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            temp_image.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
*/

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

    private void initViews() {
        /*
        temp_image = findViewById(R.id.temp_image);
        temp_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp_image.setVisibility(View.GONE);
            }
        });
        */
        mActivity = this;

        web_parent = findViewById(R.id.web_parent);
        root = findViewById(R.id.root);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        popupLayout = findViewById(R.id.popupLayout);
        btn_get_reward = findViewById(R.id.btn_get_reward);
        txt_get_reward = findViewById(R.id.txt_get_reward);
        count_time = findViewById(R.id.count_time);
        timer_layer = findViewById(R.id.timer_layer);
        txt_guide = findViewById(R.id.txt_guide);
        main_title = findViewById(R.id.main_title);
        btn_back = findViewById(R.id.btn_back);
        white = findViewById(R.id.white);
        ivLoading = findViewById(R.id.ivLoading);

        ConstraintLayout root = findViewById(R.id.root);
        com.enliple.keyboard.common.Common.SetInset(root);

        CustomFont.SetFont(getApplicationContext(), main_title, CustomFont.FONT_TYPE_BOLD);

//        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(ivLoading);
//        ImageModule.with(KeyboardHybridOfferwallActivity.this).load(R.raw.loading_red).into(imageViewTarget);


        loadingFragment = OfferwallLoadingFragment.getInstance();
        webView.addJavascriptInterface(new PomissionBridge(), "HybridApp");

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setBackgroundResource(R.drawable.aikbd_white_bg);

        offerwallWebViewClient = new OfferwallWebViewClient(KeyboardHybridOfferwallActivity.this, progressBar);
        offerwallWebChromeClient = new OfferwallWebChromeClient(childWebViewList, webView, progressBar, web_parent, popupLayout);
        webView.setWebViewClient(offerwallWebViewClient);
        webView.setWebChromeClient(offerwallWebChromeClient);
        setWebViewClientCallback();
        setWebChromeClientCallback();

        getInit(webView);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        String adid = MobonUtils.getAdid(KeyboardHybridOfferwallActivity.this);
        LogPrint.d("adid 111111 :: " + adid);
        if ( TextUtils.isEmpty(adid) ) {
            MobonUtils.getADID(KeyboardHybridOfferwallActivity.this);



            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    String adid = MobonUtils.getAdid(KeyboardHybridOfferwallActivity.this);
                    LogPrint.d("adid 111111_1 :: " + adid);
                    String id = SharedPreference.getString(KeyboardHybridOfferwallActivity.this, Key.KEY_OCB_USER_ID);
                    try {
                        E_Cipher cp = E_Cipher.getInstance();
                        id = cp.Decode(KeyboardHybridOfferwallActivity.this, id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    boolean isUnderFourteen = SharedPreference.getBoolean(KeyboardHybridOfferwallActivity.this, Key.KEY_IS_UNDER_FOURTEEN);
//                    LogPrint.d("isUnderFourteen :: " + isUnderFourteen);
//                    if ( isUnderFourteen )
//                        adid = "";

                    String landingUrl = "https://ocbapi.cashkeyboard.co.kr/API/OCB/offerwall/index.php?ad_id=" + adid
                            + "&user_key=" + id + "&top_yn=v1" + "&server_type=" + CustomAsyncTask.gubun;

                    LogPrint.d("landingUrl 11112222 :: " + landingUrl);

                    webView.loadUrl(landingUrl);
                }
            }, 200);
        } else {
            String id = SharedPreference.getString(KeyboardHybridOfferwallActivity.this, Key.KEY_OCB_USER_ID);
            try {
                E_Cipher cp = E_Cipher.getInstance();
                id = cp.Decode(KeyboardHybridOfferwallActivity.this, id);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            boolean isUnderFourteen = SharedPreference.getBoolean(KeyboardHybridOfferwallActivity.this, Key.KEY_IS_UNDER_FOURTEEN);
//            LogPrint.d("isUnderFourteen :: " + isUnderFourteen);
//            if ( isUnderFourteen )
//                adid = "";

            String landingUrl = "https://ocbapi.cashkeyboard.co.kr/API/OCB/offerwall/index.php?ad_id=" + adid
                    + "&user_key=" + id + "&top_yn=v1" + "&server_type=" + CustomAsyncTask.gubun;

            LogPrint.d("landingUrl 1212 :: " + landingUrl);

            LogPrint.d("hybrid landingUrl :: " + landingUrl);

            webView.loadUrl(landingUrl);
        }


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_get_reward.setOnClickListener(new OnKeyboardSingleClickListener() {
            @Override
            protected void onSingleClick(View v) {
                if (webView != null && !TextUtils.isEmpty(jsString)) {
                    try {
                        JSONObject object = new JSONObject(jsString);
                        if ( object != null ) {
                            int mission_seq = object.optInt("mission_seq");
                            String mission_id = object.optString("mission_id");
                            String point = object.optString("user_point");
                            requestSendPoint(mission_seq, mission_id, point);
                            /**
                            String mediaUserKey = SharedPreference.getString(KeyboardHybridOfferwallActivity.this, Key.KEY_OCB_USER_ID);
                            try {
                                E_Cipher cp = E_Cipher.getInstance();
                                mediaUserKey = cp.Decode(KeyboardHybridOfferwallActivity.this, mediaUserKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String adid = MobonUtils.getAdid(KeyboardHybridOfferwallActivity.this);

                            String sendPomission = "javascript:send_pomission(" +mission_seq +
                                    ", \"" + mission_id + "\", \"" + mediaUserKey + "\", \"" + adid + "\");";
                            LogPrint.d("sendPomission :: " + sendPomission);
                            webView.loadUrl(sendPomission);
**/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (!com.enliple.keyboard.common.Common.IsNetworkConnected(KeyboardHybridOfferwallActivity.this)) {
            Toast.makeText(KeyboardHybridOfferwallActivity.this, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
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
        offerwallWebChromeClient.setCallback(new OfferwallWebChromeClient.WebChromeClientCallback() {
            @Override
            public void onChromePageStarted(WebView webView, String url) {
                LogPrint.d("chrome onChromePageStarted v url :: " + url);
                setEvaluate(webView);
            }

            @Override
            public void onChromePageFinished(WebView webView, String url) {
                LogPrint.d("chrome onChromePageFinished v url :: " + url);
            }

            @Override
            public void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                LogPrint.d("***** upload onShowFileChooser()");
                //Callback 초기화
                //return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);

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

//                    pickMedia.launch(new PickVisualMediaRequest.Builder()
//                            .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
//                            .build());



//                if ( flag%2 == 0 ) {
//                    LogPrint.d("upload flag 1 called");
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("*/*");  //모든 contentType 파일 표시
//                    startActivityForResult(intent, 0);
//                } else {
//                    LogPrint.d("upload flag 2 called");
//                    Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
//                    startActivityForResult(intent, 0);
//                }
//                flag ++;


            }

            @Override
            public void js_load(String js) {
                try {
                    if (!TextUtils.isEmpty(js)) {
                        LogPrint.d("js :: " + js);

                        JSONObject jsonObject = new JSONObject(js);
                        if (jsonObject != null) {
                            jsString = js;
                            StringBuilder builder = new StringBuilder();
                            builder.append("document.addEventListener(\"DOMContentLoaded\", function(){ ");
                            builder.append("var my_awesome_script = document.createElement('script');");
                            builder.append("my_awesome_script.setAttribute('id','pomission_js');");
                            Iterator<String> iter = jsonObject.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                String value = jsonObject.optString(key);
                                LogPrint.d("key :: " + key);
                                LogPrint.d("value :: " + value);
                                String scriptStr = "my_awesome_script.setAttribute('" + key + "','" + value + "');";
                                LogPrint.d("scriptStr :: " + scriptStr);
                                LogPrint.d(" ************* ");
                                builder.append(scriptStr);
                            }
                            builder.append("document.head.appendChild(my_awesome_script);");
                            builder.append("});");
                            insect_js = builder.toString();
                            LogPrint.d("strJavaScript :: " + insect_js);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void historyBack() {
                finish();
                //ActivityCompat.finishAffinity(KeyboardHybridOfferwallActivity.this);
            }

            @Override
            public void buttonActivation(String isActive) {
                LogPrint.d("buttonActivation 1 :: " + isActive);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean active = false;
                        if ("Y".equals(isActive))
                            active = true;
                        if (btn_get_reward != null)
                            btn_get_reward.setEnabled(active);
                        if (active) {
                            txt_get_reward.setTextColor(Color.parseColor("#ffffff"));
                            btn_get_reward.setBackgroundColor(Color.parseColor("#2f368e"));
                        } else {
                            txt_get_reward.setTextColor(Color.parseColor("#8f8f8f"));
                            btn_get_reward.setBackgroundColor(Color.parseColor("#dbdbdb"));
                        }
                    }
                });
            }

            @Override
            public void getProductGuide(String json) {
                LogPrint.d("getProductGuide json 3 :: " + json);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!TextUtils.isEmpty(json)) {
                                LogPrint.d("json :: " + json);
                                JSONObject jsonObject = new JSONObject(json);
                                if (jsonObject != null) {
                                    String title = jsonObject.optString("full");
                                    SpannableString spannableString = new SpannableString(title);
                                    JSONArray arr = jsonObject.optJSONArray("data");
                                    if (arr != null && arr.length() > 0) {
                                        for (int i = 0; i < arr.length(); i++) {
                                            JSONObject in_obj = arr.optJSONObject(i);
                                            GuideData g_data = new GuideData();
                                            String name = in_obj.optString("name");
                                            String color = in_obj.optString("color");
                                            int ad_startIndex = title.indexOf(name) - 1;
                                            int ad_endIndex = ad_startIndex + name.length() + 2;
                                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), ad_startIndex, ad_endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannableString.setSpan(new AbsoluteSizeSpan(20, true), ad_startIndex, ad_endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannableString.setSpan(new StyleSpan(Typeface.BOLD), ad_startIndex, ad_endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        }
                                        txt_guide.setText(spannableString);

                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void isGuideVisible(String isVisible) {
                LogPrint.d("isGuideVisible isVisible 6 :: " + isVisible);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("Y".equals(isVisible)) {
                            LogPrint.d("6 visible");
                            txt_guide.setVisibility(View.VISIBLE);
                        } else {
                            LogPrint.d("6 gone");
                            txt_guide.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void buttonVisibility(String isVisible) {
                LogPrint.d("buttonVisibility 1 :: " + isVisible);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("Y".equals(isVisible))
                            btn_get_reward.setVisibility(View.VISIBLE);
                        else
                            btn_get_reward.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void countdownStart(int count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (countDownTimer != null)
                            countDownTimer = null;

                        countDownTimer = new CountDownTimer(count * 1000, 100) {
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
                                //completeMission();
                            }
                        }.start();

                        if (timer_layer != null)
                            timer_layer.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void countdownCancel() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelTimer();
                    }
                });
            }

            @Override
            public void sendPoint(int mission_seq, String mission_id, String point) {
                requestSendPoint(mission_seq, mission_id, point);
            }
        });
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        }*/
    }

    private void setWebViewClientCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            offerwallWebViewClient.setCallback(new OfferwallWebViewClient.WebViewClientCallback() {
                @Override
                public void setWebViewBackAndForward(boolean isGoBack, boolean isGoForward) {

                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    LogPrint.d("webview client onPageStarted url :: " + url);
                    if (url.startsWith("https://ocbapi.cashkeyboard.co.kr/API/OCB/offerwall/index.php"))
                        main_title.setText("Plus Point Zone");
                    else
                        main_title.setText("미션 참여");

                    if (btn_get_reward != null && txt_get_reward != null) {
                        LogPrint.d("buttonEnabled false 1");
                        btn_get_reward.setEnabled(false);
                        txt_get_reward.setTextColor(Color.parseColor("#8f8f8f"));
                        btn_get_reward.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    }

                    setEvaluate(view);
                }

                @Override
                public void onPageFinished(WebView view, String prevUrl, String url) {
                    LogPrint.d("webview client onPageFinished url :: " + url);
                    if ( url.startsWith("https://ocbapi.cashkeyboard.co.kr/API/OCB/offerwall/index.php") ) {
                        white.setVisibility(View.GONE);
                    }
                    LogPrint.d("skkim isWebPageError 1 :: " + isWebPageError);
                    if ( isWebPageError ) {
                        white.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onReceivedError(String url, WebResourceError error) {
                    if ( error != null ) {
                        int code = error.getErrorCode();
                        LogPrint.d("error :::::: " + error.getDescription() + " , err_code :: " + error.getErrorCode());
                        isWebPageError = true;
                    }
                    isWebPageError = true;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    LogPrint.d("shouldOverrideUrlLoading :: ");
                    return false;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    LogPrint.d("shouldOverrideUrlLoading :: url :: " + url);
                    return false;
                }
            });

        } else {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    LogPrint.d("setWebViewClientCallback onPageStarted1");
                    if (btn_get_reward != null && txt_get_reward != null) {
                        LogPrint.d("buttonEnabled 2 ");
                        btn_get_reward.setEnabled(false);
                        txt_get_reward.setTextColor(Color.parseColor("#8f8f8f"));
                        btn_get_reward.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    }
                    setEvaluate(view);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if ( url.startsWith("https://ocbapi.cashkeyboard.co.kr/API/OCB/offerwall/index.php") ) {
                        white.setVisibility(View.GONE);
                    }
                    LogPrint.d("skkim isWebPageError :: " + isWebPageError);
                    if ( isWebPageError ) {
                        white.setVisibility(View.VISIBLE);
//                        Toast.makeText(KeyboardHybridOfferwallActivity.this, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    LogPrint.d("webview client shouldOverrideUrlLoading url :: " + url);
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        view.loadUrl(url);
                        return true;
                    } else {
                        try {
                            boolean flag = OfferwallUtils.callApp(KeyboardHybridOfferwallActivity.this, url);
                            if (!flag) {
                                Intent intent = Intent.parseUri(url.toString(), Intent.URI_INTENT_SCHEME);
                                String fallBackUrl = "";
                                if (intent != null) {
                                    fallBackUrl = intent.getStringExtra("browser_fallback_url");
                                }
                                LogPrint.d("fallBackUrl :: " + fallBackUrl);
                                if (!TextUtils.isEmpty(fallBackUrl)) {
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
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    LogPrint.d("skkim onReceivedError description isWebPageError turn true :: " + description);
                    isWebPageError = true;
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardHybridOfferwallActivity.this);
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

    private void completeMission() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (btn_get_reward != null && txt_get_reward != null) {
                    LogPrint.d("buttonEnabled 1 true");
                    btn_get_reward.setEnabled(true);
                    txt_get_reward.setTextColor(Color.parseColor("#ffffff"));
                    btn_get_reward.setBackgroundColor(Color.parseColor("#2f368e"));
                    btn_get_reward.invalidate();
                    txt_get_reward.invalidate();
                }
            }
        });
    }

    private void cancelTimer() {
        if (countDownTimer != null)
            countDownTimer.cancel();

        if (timer_layer != null)
            timer_layer.setVisibility(View.GONE);
    }

    private void requestSendPoint(int i_mission_seq, String missionId, String point) {
        getSupportFragmentManager().beginTransaction().replace(R.id.popupLayout, loadingFragment).commit();
        if (i_mission_seq > 0) {
            String mediaUserKey = SharedPreference.getString(KeyboardHybridOfferwallActivity.this, Key.KEY_OCB_USER_ID);
            try {
                E_Cipher cp = E_Cipher.getInstance();
                mediaUserKey = cp.Decode(KeyboardHybridOfferwallActivity.this, mediaUserKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String mediaUserPhone = "";
            String mediaUserEmail = "";
            OfferwallParticipationReq req = new OfferwallParticipationReq();
            req.setMission_seq(i_mission_seq);
            req.setMission_id(missionId);
            req.setMedia_user_key(mediaUserKey);
            req.setMedia_user_phone(mediaUserPhone);
            req.setMedia_user_email(mediaUserEmail);

            offerwallParticipation(req, point, false);
        }
    }

    private void offerwallParticipation(OfferwallParticipationReq req, String point, boolean isRetry) {
        CustomAsyncTask task = new CustomAsyncTask(KeyboardHybridOfferwallActivity.this);
        task.offerwallParticipation(req, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                LogPrint.d("offerwallParticipation responnse result :: " + result);
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            LogPrint.d("offerwallParticipation responnse object :: " + object.toString());
                            int rt = object.optInt("result");
                            getSupportFragmentManager().beginTransaction().remove(loadingFragment).commit();
                            if (rt == 0) {
                                btn_get_reward.setEnabled(false);
                                btn_get_reward.setVisibility(View.GONE);
                                LogPrint.d("button enabled false visible gone");
                                // ocb server에 성공 api 호출 후 해당 api 성공 시 아래 수행
                                Toast.makeText(KeyboardHybridOfferwallActivity.this, point + "P를 적립하였습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (Common.IsFormissionTokenError(KeyboardHybridOfferwallActivity.this, rt)) {
                                    if (!isRetry) {
                                        CustomAsyncTask inTask = new CustomAsyncTask(KeyboardHybridOfferwallActivity.this);
                                        inTask.getOfferwallToken(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                            @Override
                                            public void onResponse(boolean result, Object obj) {
                                                if (result) {
                                                    try {
                                                        JSONObject object = (JSONObject) obj;
                                                        if (object != null) {
                                                            int rt = object.optInt("result");
                                                            if (rt == 0) {
                                                                String tk = object.optString("token");
                                                                LogPrint.d("received token :: " + tk);
                                                                SharedPreference.setString(KeyboardHybridOfferwallActivity.this, Key.KEY_FORMISSION_TOKEN, tk);
                                                                offerwallParticipation(req, point, true);
                                                            } else {
                                                                // 토큰 획득 실패 후처리
                                                                Toast.makeText(KeyboardHybridOfferwallActivity.this, getString(R.string.aikbd_offerwall_fail_toast_message), Toast.LENGTH_SHORT).show();
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
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(KeyboardHybridOfferwallActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else {
                            LogPrint.d("offerwallParticipation responnse object null");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private class PomissionBridge {
        @JavascriptInterface
        public void showMessage(String message) {
            LogPrint.d("skkim showMessage message :: " + message);
            Toast.makeText(KeyboardHybridOfferwallActivity.this, message, Toast.LENGTH_SHORT).show();
        }


        @JavascriptInterface
        public boolean isAppInstalled(String packageName) {
            LogPrint.d("isAllInstalled packageName :: " + packageName);
            boolean isInstalled = false;
            if (!TextUtils.isEmpty(packageName))
                isInstalled = MobonUtils.isPackageInstalled(packageName, KeyboardHybridOfferwallActivity.this);
            LogPrint.d("isAllInstalled result :: " + isInstalled);
            return isInstalled;
        }

        @JavascriptInterface
        public void js_load(String js) {
            try {
                if (!TextUtils.isEmpty(js)) {
                    LogPrint.d("js :: " + js);

                    JSONObject jsonObject = new JSONObject(js);
                    if (jsonObject != null) {
                        jsString = js;
                        StringBuilder builder = new StringBuilder();
                        builder.append("document.addEventListener(\"DOMContentLoaded\", function(){ ");
                        builder.append("var my_awesome_script = document.createElement('script');");
                        builder.append("my_awesome_script.setAttribute('id','pomission_js');");
                        Iterator<String> iter = jsonObject.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            String value = jsonObject.optString(key);
                            LogPrint.d("key :: " + key);
                            LogPrint.d("value :: " + value);
                            String scriptStr = "my_awesome_script.setAttribute('" + key + "','" + value + "');";
                            LogPrint.d("scriptStr :: " + scriptStr);
                            LogPrint.d(" ************* ");
                            builder.append(scriptStr);
                        }
                        builder.append("document.head.appendChild(my_awesome_script);");
                        builder.append("});");
                        insect_js = builder.toString();
                        LogPrint.d("strJavaScript :: " + insect_js);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void historyBack() {
            LogPrint.d("historyBack called");
            finish();
            //ActivityCompat.finishAffinity(KeyboardHybridOfferwallActivity.this);
        }

        // 버튼 활성화 유무 호출
        @JavascriptInterface
        public void buttonActivation(String isActive) {
            LogPrint.d("buttonActivation isActive :: " + isActive);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean active = false;
                    if ("Y".equals(isActive))
                        active = true;
                    if (btn_get_reward != null)
                        btn_get_reward.setEnabled(active);
                    if (active) {
                        txt_get_reward.setTextColor(Color.parseColor("#ffffff"));
                        btn_get_reward.setBackgroundColor(Color.parseColor("#2f368e"));
                    } else {
                        txt_get_reward.setTextColor(Color.parseColor("#8f8f8f"));
                        btn_get_reward.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    }
                }
            });
        }

        @JavascriptInterface
        public void getProductGuide(String json) {
            LogPrint.d("getProductGuide json 2 :: " + json);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!TextUtils.isEmpty(json)) {
                            LogPrint.d("json :: " + json);
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject != null) {
                                String title = jsonObject.optString("full");
                                SpannableString spannableString = new SpannableString(title);
                                JSONArray arr = jsonObject.optJSONArray("data");
                                if (arr != null && arr.length() > 0) {
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject in_obj = arr.optJSONObject(i);
                                        GuideData g_data = new GuideData();
                                        String name = in_obj.optString("name");
                                        String color = in_obj.optString("color");
                                        int ad_startIndex = title.indexOf(name) - 1;
                                        int ad_endIndex = ad_startIndex + name.length() + 2;
                                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), ad_startIndex, ad_endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannableString.setSpan(new AbsoluteSizeSpan(20, true), ad_startIndex, ad_endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), ad_startIndex, ad_endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                    txt_guide.setText(spannableString);

                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @JavascriptInterface
        public void isGuideVisible(String isVisible) {
            LogPrint.d("isGuideVisible isVisible 5 :: " + isVisible);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ("Y".equals(isVisible))
                        txt_guide.setVisibility(View.VISIBLE);
                    else
                        txt_guide.setVisibility(View.GONE);
                }
            });
        }

        // 버튼 노출 유무 호출
        @JavascriptInterface
        public void buttonVisibility(String isVisible) {
            LogPrint.d("buttonVisibility isVisible 2 :: " + isVisible);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ("Y".equals(isVisible))
                        btn_get_reward.setVisibility(View.VISIBLE);
                    else
                        btn_get_reward.setVisibility(View.GONE);
                }
            });
        }

        // 타이머 시작 시 호출
        @JavascriptInterface
        public void countdownStart(String ct) {
            LogPrint.d("countdownStart count 1 :: " + ct);
            int cnt = -1;
            try {
                cnt = Integer.valueOf(ct);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cnt >= 0) {
                final int count = cnt;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (countDownTimer != null)
                            countDownTimer = null;

                        countDownTimer = new CountDownTimer(count * 1000, 100) {
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
                                //completeMission();
                            }
                        }.start();

                        if (timer_layer != null)
                            timer_layer.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        // 타이머 취소 시 호출
        @JavascriptInterface
        public void countdownCancel() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelTimer();
                }
            });
        }

        // 즉시 적립 시 호출
        @JavascriptInterface
        public void sendPoint(int mission_seq, String mission_id, String point) {
            requestSendPoint(mission_seq, mission_id, point);
        }

    }

    private void setEvaluate(WebView wv) {
        LogPrint.d("insect_js :: " + insect_js);
        wv.evaluateJavascript(insect_js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                LogPrint.d("value :: " + value);
            }
        });
    }

    private class GuideData {
        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        private String str;
        private String color;

    }

    /*
    public class MyJavaScriptInterface {
        @JavascriptInterface
        public void completedMission() {
            LogPrint.d("Mission Complete!!!");
            completeMission();
        }

        @JavascriptInterface
        public void sendNaverId(String id) {
            LogPrint.d("n_Id : " + id);
            SharedPreference.setString(KeyboardHybridOfferwallActivity.this, Key.KEY_N_ID, id);
        }
    }
     */

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean isPhotoPickerAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return getExtensionVersion(Build.VERSION_CODES.R) >= 2;
        } else
            return false;
    }
}