package com.enliple.keyboard.ad;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.ad.webview.Bridge;
import com.enliple.keyboard.ad.webview.SDKWebView;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.mobonAD.MobonUtils;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class EnWebBannerView extends RelativeLayout implements View.OnClickListener {
    private String ad_url = "";
    private Context context;
    private TextView banner_click;
    private LinearLayout en_web_banner_layout;
    private RelativeLayout en_web_banner_root;
    private Button banner_close;
    private SDKWebView ad_webview;
    RelativeLayout point_loading_view;
    private CardView ad_container;


    // webview onPageFinished 체크를 위한 플래그. webview load 시 초기값은 false, onPageFinished 호출 이후 true
    // : webview url 로드 시 연결 실패의 경우 os 에서 재시도를 호출하기때문에 onPageFinished 가 두번 호출 됨.
    private boolean onPageFinishedCalled = false;

    // webview load 성공 플래그 값. 기본 값은 true, webview 의 onReceivedError, onReceivedSslError 상황일때 false
    private boolean isLoadSuccess = true;

    // TODO : attr 옵션값 설정
    // webview 재 로드 time (min)
    // : 광고 로딩 시간 때문에 미리 로드를 하고 있음.
    // : 광고가 10분간 유효하기 때문에 timer 를 통해 재로드 를 하고 있음.
    private int refreshTime = 10;

    //ad view 를 호출한 key(고객사에서 전달해 주는 값으로 적립 요청 시 서버에 ad_name 으로 기록 됨
    private String requestKey = "0";

    //재 로딩 Timer
    private Timer reLoadTimer = null;

    // Ad Page 기본 Url
    private String adUrl = "https://www.mobwithad.com/api/v1/banner/app/ocbKeyboard?zone={0}&count=1&w=300&h=250&adid={1}";

    Listener.OnBannerViewListener listener = null;

    //ad page 에서 전달해주는 광고의 ID
    public String webCallbackDataAdId;

    //ad page 에서 전달해주는 광고의 적립 포인트
    public int webCallbackDataAdPoint;

    //webview load 시점의 userKey
    // : 고객사의 로그인, 로그아웃 시점에 재 로드를 명시적으로 호출 안하기 때문에, load 시점의 userKey 와 show 시점의 userkey 가 틀릴 수 있음.
    // : show 시점에 userkey 가 틀릴경우 view 에서 재 로드를 진행.
    public String adLoadedUserKey = "";

    //광고 클릭시 이동 url
    private String moveUrl = "";
    private Handler loadingHandler;
    private String targetId;
    private String zoneId;
    private long bannerClickTime = 0;
    private Runnable loadingRunnable = new Runnable() {
        @Override
        public void run() {
            if ( point_loading_view != null )
                point_loading_view.setVisibility(View.VISIBLE);
        }
    };

    public EnWebBannerView(@NonNull Context context){
        super(context);
        this.context = context;
        initView();
    }
    public EnWebBannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }
    public EnWebBannerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    public void setRequestKey(String ad_name){
        this.requestKey = ad_name;
    }

    public void onResume(Listener.OnBannerViewListener listener) {
        this.listener = listener;
        //adLoad();
    }

    //ReLoadTimer 삭제.
    public void onStop(){
        listener = null;
        if(reLoadTimer != null){
            reLoadTimer.cancel();
            reLoadTimer = null;
        }
    }

    public void adViewShow(String requestKey){
        this.requestKey = requestKey;
        String loginUserKey = SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            loginUserKey = cp.Decode(context, loginUserKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("loginUserKey : " + loginUserKey);
        // adLoadedUserKey 와 현재 저장된 user를 비교 해서 틀리면 재 로드
        if(adLoadedUserKey.equals(loginUserKey) == false){
            adLoad();
        }

        this.setVisibility(View.VISIBLE);
    }


    public void openCoupangAd(){
        //ad page가 로드된 이후 쿠팡 광고로 변경 스크립트.
        ad_webview.loadUrl("javascript:postMessage('executeCoupangFunc', '*');");
    }

    public void setListener(Listener.OnBannerViewListener listener ) {
        this.listener = listener;
    }

    public void initView(){
        listener = new Listener.OnBannerViewListener() {
            @Override
            public void onBannerViewError(String errorCode, String detail) {

            }

            @Override
            public void onBannerViewState(String bannerViewState, String detail) {

            }

            @Override
            public void onBannerPoint(String targetId, int point) {

            }
        };

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.web_banner_view, this, false);
        addView(v);
        en_web_banner_root = findViewById(R.id.en_web_banner_root);
        point_loading_view = findViewById(R.id.point_loading_view);
        ad_webview = findViewById(R.id.ad_web);
        en_web_banner_layout = findViewById(R.id.en_web_banner_layout);

        banner_close = findViewById(R.id.banner_close);
        banner_click = findViewById(R.id.banner_click);

        banner_close.setOnClickListener(this);
        banner_click.setOnClickListener(this);

        ad_container = findViewById(R.id.ad_container);
        int width = Common.convertDpToPx(context, 250);
        int height = 250 * width / 300;
//        Log.d("TAG", "width : " + width + " , height : " + height);
        ViewGroup.LayoutParams params = ad_container.getLayoutParams();
        params.width = width;
        params.height = height;
        ad_container.setLayoutParams(params);
        ad_container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        en_web_banner_root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        setBridge();
    }

    public void adWebBannerClose(int delayMillis){
        // 닫기 버튼 클릭 시 다시 재로드 하도록 수정하고 닫기 버튼 클릭 callback 전달
        sendBannerViewListener(Common.LISTENER_TYPE_STATE, Common.STATE_AD_CLOSE, "AD CLOSED", "");
        adLoad();
        this.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View target) {
        int targetId = target.getId();
        if(targetId == R.id.banner_close){
            adWebBannerClose(10);
        } else if( targetId == R.id.banner_click){
            adTouch();
        }
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    //광고 보기 클릭 시 webview 클릭 처리
    private void adTouch() {
        // bannerClickTime 이 0 즉 해당 bannerview의 첫 클릭이거나
        // bannerClickTime + 2000 보다 현재시간이 클 경우. 즉 마지막 클릭이 일어난 후 2초 이상이 지난 시점이거나
        if ( bannerClickTime == 0 || System.currentTimeMillis() > bannerClickTime + 2000L ) {
            LogPrint.d("banner click time ok");
            bannerClickTime = System.currentTimeMillis();
            Long downTime = SystemClock.uptimeMillis();
            Long eventTime = SystemClock.uptimeMillis();
            MotionEvent motionEvent1 = MotionEvent.obtain( downTime, eventTime +10, MotionEvent.ACTION_DOWN, ad_container.getX()+ad_container.getWidth()/2, ad_container.getY()+ad_container.getWidth()/2, 0 );
            MotionEvent motionEvent3 = MotionEvent.obtain(downTime + 10, eventTime + 10, MotionEvent.ACTION_UP, ad_container.getX()+10, ad_container.getY()+10, 0);
            ad_container.dispatchTouchEvent(MotionEvent.obtain(motionEvent1));
            ad_container.dispatchTouchEvent(MotionEvent.obtain(motionEvent3));
        } else {
            LogPrint.d("banner click time not ok");
        }
    }

    // Listener 를 보내기전에 통신 상태를 체크하여 네크워크 연결이 안되어있을시 해당 Listener 로 반환.
    private void sendBannerViewListener(String listenerType,String code,String detail, String responseCode){
        if(listener == null){
            return;
        }
        //listener에 보내기전에 네트워크를 체크한다.
        if(Common.IsNetworkConnected(context) == false){
            listener.onBannerViewError(Common.ERROR_NETWORK_CONDITION,code);
            return;
        }

        switch (listenerType){
            case Common.LISTENER_TYPE_ERROR:
                listener.onBannerViewError(code,detail);
                break;
            case Common.LISTENER_TYPE_STATE:
                listener.onBannerViewState(code,detail);
                break;
            case Common.LISTENER_TYPE_POINT:
                adWebBannerClose(100);
                break;
        }
    }

    public void adClosed() {
        sendBannerViewListener(Common.LISTENER_TYPE_STATE, Common.STATE_AD_CLOSE, "AD CLOSED", "");
    }

    public void adLoad(String targetId) {
        this.targetId = targetId;
        try {
            String[] datas = targetId.split("/");
            if ( datas != null && datas.length == 2 ) {
                zoneId = datas[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adLoad();
    }

    // ad page 로드.
    public void adLoad(){
        LogPrint.d("adLoad");
        point_loading_view.setVisibility(View.GONE);
        webCallbackDataAdId = "";
        webCallbackDataAdPoint = 0;
        onPageFinishedCalled = false;

        boolean isUnderFourteen = SharedPreference.getBoolean(context, Key.KEY_IS_UNDER_FOURTEEN);

        String ad_id = MobonUtils.getAdid(context);
        if ( isUnderFourteen )
            ad_id = "";
        String userKey = SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            userKey = cp.Decode(context, userKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        adUrl = adUrl.replace("{0}",zoneId).replace("{1}", ad_id);
        LogPrint.d("adUrl :: " + adUrl);
        adLoadedUserKey = userKey;

        if(reLoadTimer != null){
            sendBannerViewListener(Common.LISTENER_TYPE_STATE, Common.STATE_AD_RELOADING,adUrl, "");
            reLoadTimer.cancel();
        } else {
            sendBannerViewListener(Common.LISTENER_TYPE_STATE, Common.STATE_AD_LOADING,adUrl, "");
        }
        ad_webview.clearCache(true);

        ad_webview.setWebChromeClient(webChromeClient);
        ad_webview.setWebViewClient(webViewClient);

        ad_webview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        ad_webview.loadUrl(adUrl);

        reLoadTimer = new Timer();
        reLoadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        adLoad();
                    }
                });
            }
        }, refreshTime * 60000);
    }

    private WebChromeClient webChromeClient = new WebChromeClient(){
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            //try {
            WebView newWebView = new WebView(context);
            view.addView(newWebView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    boolean isAvailableUrl = true;
                    moveUrl = url;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(intent);
                    } catch (Exception e) {
                        isAvailableUrl = false;
                    }
                    LogPrint.d("isAvailableUrl :: " + isAvailableUrl);
                    if(isAvailableUrl){
                        adWebBannerClose(10);
                        sendBannerViewListener(Common.LISTENER_TYPE_STATE, Common.STATE_AD_MOVE,targetId, "");
                    } else {
                        sendBannerViewListener(Common.LISTENER_TYPE_STATE, Common.STATE_AD_MOVE_FAIL,targetId, "");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adWebBannerClose(10);
                            }
                        }, 500);
                    }
                    return true;
                }
            });
            return true;
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            LogPrint.d("mobwith console message :: " + message);
            if ((message.contains("Uncaught SyntaxError:") || message.contains("Uncaught ReferenceError:") || message.contains("no ad") || message.contains("AdapterFailCallback")) && !message.contains("wp_json")) {
                //ad_webview.loadUrl("javascript:postMessage('executeCoupangFunc', '*');");
                moveUrl = "";
            }
        }
    };

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //LogPrint.d(" onPageStarted");
            moveUrl = url;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //LogPrint.d(" shouldOverrideUrlLoading");

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.cancel();
            //LogPrint.d("mobwith onReceivedSslError");
            isLoadSuccess = false;
            sendBannerViewListener(Common.LISTENER_TYPE_ERROR, Common.ERROR_AD_LOAD,"onReceivedSslError", "");
            moveUrl = "";
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest
                request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            isLoadSuccess = false;
            //TODO 인터넷 연결 실패시 두번 호출되는지 확인
            sendBannerViewListener(Common.LISTENER_TYPE_ERROR, Common.ERROR_AD_LOAD,"onReceivedError", "");
            moveUrl = "";
        }

        @Override
        public void onPageFinished(final WebView view, String url) {

            if (!onPageFinishedCalled) {
                onPageFinishedCalled = true;

                if(isLoadSuccess){
                    sendBannerViewListener(Common.LISTENER_TYPE_STATE, Common.STATE_AD_LOADED,targetId, "");
                }
            }
        }
    };
    private Handler handler = new Handler(Looper.getMainLooper());

    private void setBridge() {
        Bridge.BannerBridge bridge = new Bridge.BannerBridge();
        bridge.setListener(new Bridge.BannerBridge.Listener() {
            @Override
            public void onAppEvent(String jsonData) {
                // ad page 쪽에서 전달 받는 함수는 1개.
                LogPrint.d("banner click jsonData :: " + jsonData);
                // 메시지 처리를 수행합니다. Gson class 추가할 수 없어 json parsing 하는 방식으로 변경함.
                try {
                    if ( !TextUtils.isEmpty(jsonData) ) {
                        JSONObject object = new JSONObject(jsonData);
                        if ( object != null ) {
                            JSONObject dataObject = object.optJSONObject("data");
                            if ( dataObject != null ) {
                                int width = 250;
                                int height = 250;
                                webCallbackDataAdId = dataObject.optString("ad_id");
                                webCallbackDataAdPoint = dataObject.optInt("point");
                                if ( dataObject.has("width") ) {
                                    int h_width = dataObject.optInt("width");
                                    if ( h_width > 0 ) {
                                        width = h_width;
                                    }
                                }
                                if ( dataObject.has("height") ) {
                                    int h_height = dataObject.optInt("height");
                                    if ( h_height > 0 ) {
                                        height = h_height;
                                    }
                                }
                                int newWidthInPxWidth = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        width,
                                        getResources().getDisplayMetrics()
                                );
                                int newWidthInPxHeight = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        height,
                                        getResources().getDisplayMetrics()
                                );
                                ViewGroup.LayoutParams layoutParams = ad_container.getLayoutParams();
                                layoutParams.width = newWidthInPxWidth;
                                layoutParams.height = newWidthInPxHeight;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ad_container.setLayoutParams(layoutParams);

                                            }
                                        });
                                    }
                                }).start();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                Gson gson = new Gson();
                try{
                    AppEventModel appEventModel = gson.fromJson(jsonData,AppEventModel.class);
                    webCallbackDataAdId = appEventModel.data.ad_id;
                    webCallbackDataAdPoint = appEventModel.data.point ;
                    ViewGroup.LayoutParams layoutParams = ad_container.getLayoutParams();

                    if (appEventModel.data.width == 0 || appEventModel.data.height == 0){
                        appEventModel.data.width = 250;
                        appEventModel.data.height = 250;

                    }

                    if(appEventModel.data.width != 250 || appEventModel.data.height != 250){
                        int newWidthInPxWidth = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                appEventModel.data.width,
                                getResources().getDisplayMetrics()
                        );
                        int newWidthInPxHeight = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                appEventModel.data.height,
                                getResources().getDisplayMetrics()
                        );
                        layoutParams.width = newWidthInPxWidth;
                        layoutParams.height = newWidthInPxHeight;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ad_container.setLayoutParams(layoutParams);

                                    }
                                });
                            }
                        }).start();
                    }

                    //banner_click.setText("광고 보고 " + appEventModel.data.point + "P 받기");
                }catch (Exception e){

                }
*/
                LogPrint.d("JavascriptInterface" + jsonData);
            }
        });
        ad_webview.addJavascriptInterface(bridge, "Native");
    }

    public class AppEventModel
    {
        public String event;
        public AppEventDataModel data;
    }
    public  class AppEventDataModel
    {
        public String ad_id;
        public int point;
        public int width;
        public int height;

    }
}
