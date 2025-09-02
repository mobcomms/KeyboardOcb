package com.enliple.keyboard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.ad.EnWebBannerView;
import com.enliple.keyboard.ad.Listener;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.webview.ENKWebView;

import org.json.JSONException;
import org.json.JSONObject;

public class KeyboardGameZoneActivity extends AppCompatActivity implements ENKWebView.Listener{
    private final static int FILE_CHOOSER_REQ = 0;
    private String url = "";
    private String id = "error";

    ENKWebView enkWebView;
    private EnWebBannerView banner_view;
    private ValueCallback mFilePathCallback;
    private OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            LogPrint.d("back pressed");
            if ( banner_view.getVisibility() == View.VISIBLE )
                return;
            enkWebView.sendScript("backpress",null);
            if (!Common.IsNetworkConnected(KeyboardGameZoneActivity.this) || "error".equals(url) || TextUtils.isEmpty(id)) {
                finish();
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_gamezone);
        this.getOnBackPressedDispatcher().addCallback(this, backPressedCallback);

        enkWebView = (ENKWebView) findViewById(R.id.tampWebview);
        enkWebView.setBackgroundColor(Color.TRANSPARENT);
        enkWebView.setBackgroundResource(R.drawable.aikbd_white_bg);
        View root_layout = findViewById(R.id.root_layout);
        Common.SetInset(root_layout);

        Intent secondIntent = getIntent();
        if ( secondIntent != null )
            url = secondIntent.getStringExtra("url");
        LogPrint.d("url :: " + url);
        if (TextUtils.isEmpty(url) ) {
            CustomAsyncTask task = new CustomAsyncTask(KeyboardGameZoneActivity.this);
            task.getGameUrl(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    LogPrint.d("game result ::: " + result);
                    String gameURL = "error";
                    if ( result ) {
                        if ( obj != null ) {
                            JSONObject object = (JSONObject) obj;
                            if ( object != null ) {
                                LogPrint.d("game obj str : " + object.toString());
                                String result_code = object.optString("result_code");
                                if ( "Success".equals(result_code) ) {
                                    String url = object.optString("data");
                                    LogPrint.d("data url : " + url);
                                    if (!TextUtils.isEmpty(url) ) {
                                        gameURL = url;
                                    }
                                }
                            }
                        }
                    }
                    url = gameURL;
                    checkUrl(gameURL);
                }
            });
        } else {
            checkUrl(url);
        }
    }

    private void checkUrl(String url) {
        LogPrint.d("checkUrl url : " + url);
        banner_view = findViewById(R.id.banner_view);
        banner_view.onResume(adViewListener);


        String id = SharedPreference.getString(KeyboardGameZoneActivity.this, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(KeyboardGameZoneActivity.this, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("id :: " + id);
        boolean networkState = Common.IsNetworkConnected(KeyboardGameZoneActivity.this);
        LogPrint.d("networkState :: " + networkState);
        if (!networkState || "error".equals(url) || TextUtils.isEmpty(url) || TextUtils.isEmpty(id)) {
            String toastMessage = !networkState ? "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요." : "서버와의 통신에 실패했습니다. 잠시 후 다시 시도해주세요.";
            Toast.makeText(KeyboardGameZoneActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            RelativeLayout top_layer = (RelativeLayout) findViewById(R.id.top_layer);
            enkWebView.setVisibility(View.GONE);
            top_layer.setVisibility(View.VISIBLE);
            TextView btn_back = (TextView) findViewById(R.id.btn_back);

            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            return;
        }
        init();
    }

    public void init() {

        enkWebView.setBackgroundColor(Color.TRANSPARENT);

        enkWebView.setListener(this, this);
        enkWebView.setWebContentsDebuggingEnabled(true);
        LogPrint.d("game zone url :: " + url);

        enkWebView.loadMainURL(url);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        LogPrint.w( " onPageStarted : " + url);

    }

    @Override
    public void onPageFinished(String url) {
        LogPrint.w( " onPageFinished : " + url);

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        LogPrint.w( " onPageError : " + url);

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    @Override
    public void shouldInterceptRequest(String url) {

    }
    @Override
    public boolean onJsConfirm(String msg, JsResult result) {
        return false;
    }

    @Override
    public void showAd(String data) {
        LogPrint.d("showAd data :: " + data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ( banner_view != null ) {
                    banner_view.adLoad(data);
                    banner_view.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //    @Override
//    public void onBackPressed() {
//             enkWebView.sendScript("backpress",null);
//            if (!Common.isConnected(KeyboardGameZoneActivity.this) || url == "error" || TextUtils.isEmpty(id)) {
//                finish();
//            }
//
//    }
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* 파일 선택 완료 후 처리 */
        switch (requestCode) {
            case FILE_CHOOSER_REQ:
                //fileChooser 로 파일 선택 후 onActivityResult 에서 결과를 받아 처리함
                if (resultCode == RESULT_OK) {
                    //파일 선택 완료 했을 경우
                    if (mFilePathCallback != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                        } else {
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

    private Listener.OnBannerViewListener adViewListener = new Listener.OnBannerViewListener() {
        @Override
        public void onBannerViewError(String errorCode, String detail) {
            LogPrint.e("ErrorCode :" + errorCode + " Detail : " +detail );
        }

        @Override
        public void onBannerViewState(String bannerViewState, String detail) {
            LogPrint.e("Sate :" + bannerViewState + " Detail : " + detail );
            if ( Common.STATE_AD_MOVE.equals(bannerViewState) ) {
                JSONObject senddata = new JSONObject();
                try {
                    String[] arr = detail.split("/");
                    if ( arr != null && arr.length == 2 ) {
                        detail = arr[0];

                    }

                    LogPrint.d("Detail :: " + detail);
                    senddata.put("gamename", detail);
                    enkWebView.sendScript("complateAd",senddata);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void onBannerPoint(String targetId, int point) {
            LogPrint.e("PointSaved targetId :" + targetId + " point : " + point );

        }
    };

}
