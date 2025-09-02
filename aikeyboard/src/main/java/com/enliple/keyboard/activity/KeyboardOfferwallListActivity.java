package com.enliple.keyboard.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.adapter.OfferwallAdapter;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.mobonAD.MobonBannerType;
import com.enliple.keyboard.mobonAD.MobonBannerView;
import com.enliple.keyboard.mobonAD.MobonSimpleSDK;
import com.enliple.keyboard.mobonAD.MobonUtils;
import com.enliple.keyboard.mobonAD.iSimpleMobonBannerCallback;
import com.enliple.keyboard.models.OfferwallCategoryData;
import com.enliple.keyboard.models.OfferwallData;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Common;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class KeyboardOfferwallListActivity extends Activity {
    public static String OFFERWALL_JOIN = "AIKBD_OFFERWALL_jOIN";
    private String lastMissionSeq = "";
    private boolean isMixerHasError = false;
    private NestedScrollView scroll_view;
    private RecyclerView recyclerView;
    private TextView btn_back;
    private RelativeLayout empty_layer;
    private RelativeLayout offerwall_ad_container;
    private RelativeLayout offerwall_ad_layer;
    private RelativeLayout mixer_layer;
    private WebView mixer_webview;
    private RelativeLayout ad_del;
    private MobonBannerView bannerView;
    private OfferwallAdapter adapter;
    private boolean offerwallClicked = false;
    private boolean isDataReceiving = false;
    private boolean isRun;
    public static Activity mActivity;
    private OfferwallCategoryData categoryData = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_keyboard_offerwall_list);
        init();
        LogPrint.d("KeyboardOfferwallListActivity called");
        //getList(false, true);
        //loadBanner(); // 2022.12.20 간헐적 crash 이슈로 우선 잠금.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( offerwall_receiver != null )
            unregisterReceiver(offerwall_receiver);
        mActivity = null;
    }

    private void init() {
        mActivity = this;
        scroll_view = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.offerwall_list);
        btn_back = findViewById(R.id.btn_back);
        empty_layer = findViewById(R.id.empty_layer);
        offerwall_ad_layer = findViewById(R.id.offerwall_ad_layer);
        mixer_layer = findViewById(R.id.mixer_layer);
        mixer_webview = findViewById(R.id.mixer_webview);
        offerwall_ad_container = findViewById(R.id.offerwall_ad_container);
        ad_del = findViewById(R.id.ad_del);

        LinearLayoutManager manager = new LinearLayoutManager(KeyboardOfferwallListActivity.this);
        recyclerView.setLayoutManager(manager);

        getCategory(false);

        scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                LogPrint.d("onScrollChange called");
                if ( scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() ) {
                    if ( !isDataReceiving ) {
                        getList(false, false);
                    }
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OFFERWALL_JOIN);
        // target 34 대응
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(offerwall_receiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(offerwall_receiver, intentFilter);
        }
//        registerReceiver(offerwall_receiver, intentFilter);
    }

    private void offerwallParticipationCheck(OfferwallData data, boolean isRetry, String mission_class) {
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOfferwallListActivity.this);
        task.offerwallParticipationCheck(data.getMission_seq(), data.getMission_id(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if ( object != null ) {
                            LogPrint.d("object str :: " + object.toString());
                            int rt = object.optInt("result");
                            if ( rt == 0 ) {
                                String landingUrl = object.optString("landing_url");
                                LogPrint.d("landingUrl :: " + landingUrl);
                                if ( !TextUtils.isEmpty(landingUrl) ) {
                                    if ( mission_class.toLowerCase().equals("p1") ) {
                                        Intent intent = new Intent(KeyboardOfferwallListActivity.this, KeyboardOfferwallWebView1Activity.class);
                                        intent.putExtra("landingUrl", landingUrl);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(KeyboardOfferwallListActivity.this, KeyboardOfferwallWebView23Activity.class);
                                        intent.putExtra("landingUrl", landingUrl);
                                        intent.putExtra("intent_mission", data);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                    }
                                } else {
                                    String msg = object.optString("msg");
                                    if ( !TextUtils.isEmpty(msg) ) {
                                        Toast.makeText(KeyboardOfferwallListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                offerwallClicked = false;
                            } else {
                                if ( Common.IsFormissionTokenError(KeyboardOfferwallListActivity.this, rt) ) {
                                    if ( !isRetry ) {
                                        CustomAsyncTask inTask = new CustomAsyncTask(KeyboardOfferwallListActivity.this);
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
                                                                SharedPreference.setString(KeyboardOfferwallListActivity.this, Key.KEY_FORMISSION_TOKEN, tk);
                                                                offerwallParticipationCheck(data, true, mission_class);
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                offerwallClicked = false;
                                            }
                                        });
                                    } else
                                        offerwallClicked = false;
                                } else {
                                    String msg = object.optString("msg");
                                    if ( !TextUtils.isEmpty(msg) ) {
                                        Toast.makeText(KeyboardOfferwallListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                    offerwallClicked = false;
                                }

                            }
                        } else
                            offerwallClicked = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        offerwallClicked = false;
                    }
                } else {
                    offerwallClicked = false;
                }
            }
        });
    }

    private void offerwallMissionClick(OfferwallData data, boolean isRetry) {
        LogPrint.d("offerwallMissionClick");
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOfferwallListActivity.this);
        task.offerwallClick(data.getMission_seq(), data.getMission_id(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if ( object != null ) {
                            int rt = object.optInt("result");
                            if ( rt == 0 ) {
                                Intent intent = new Intent(KeyboardOfferwallListActivity.this, KeyboardOfferwallGuideActivity.class);
                                intent.putExtra("intent_mission", data);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                offerwallClicked = false;
                            } else {
                                if ( Common.IsFormissionTokenError(KeyboardOfferwallListActivity.this, rt) ) {
                                    if ( !isRetry ) {
                                        CustomAsyncTask inTask = new CustomAsyncTask(KeyboardOfferwallListActivity.this);
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
                                                                SharedPreference.setString(KeyboardOfferwallListActivity.this, Key.KEY_FORMISSION_TOKEN, tk);
                                                                offerwallMissionClick(data, true);
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                offerwallClicked = false;
                                            }
                                        });
                                    } else
                                        offerwallClicked = false;
                                } else
                                    offerwallClicked = false;
                            }
                        } else
                            offerwallClicked = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        offerwallClicked = false;
                    }
                } else {
                    offerwallClicked = false;
                }
            }
        });
    }

    private void getListWithToken(boolean isRetry, boolean isFirstList) {
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOfferwallListActivity.this);
        task.getOfferwallToken(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
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
                                SharedPreference.setString(KeyboardOfferwallListActivity.this, Key.KEY_FORMISSION_TOKEN, tk);
                                getList(isRetry, isFirstList);
                            } else {
                                LogPrint.e("token api error :: " + rt + " , message :: " + object.optString("msg"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getCategoryWithToken(boolean isRetry) {
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOfferwallListActivity.this);
        task.getOfferwallToken(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
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
                                SharedPreference.setString(KeyboardOfferwallListActivity.this, Key.KEY_FORMISSION_TOKEN, tk);
                                getCategory(isRetry);
                            } else {
                                LogPrint.e("token api error :: " + rt + " , message :: " + object.optString("msg"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getCategory(boolean isRetry) {
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOfferwallListActivity.this);
        task.getOfferwallCategory(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    if ( obj != null ) {
                        try {
                            JSONObject object = (JSONObject) obj;
                            if ( object != null ) {
                                LogPrint.d("object category :: " + object.toString());
                                int rt = object.optInt("result");
                                if ( rt == 0 ) {
                                    ArrayList<OfferwallCategoryData> categoryArray = new ArrayList<>();
                                    OfferwallCategoryData data = new OfferwallCategoryData();
                                    data.setClass_name("전체");
                                    data.setMission_class("");
                                    data.setSelected(true);
                                    categoryArray.add(data);

                                    JSONArray array = object.optJSONArray("mission");
                                    if ( array != null && array.length() > 0 ) {
                                        for ( int i = 0 ; i < array.length() ; i ++ ) {
                                            JSONObject inObj = array.optJSONObject(i);
                                            if ( inObj != null ) {
                                                data = new OfferwallCategoryData();
                                                data.setMission_class(inObj.optString("mission_class"));
                                                data.setClass_name(inObj.optString("class_name"));
                                                data.setSelected(false);
                                                categoryArray.add(data);
                                            }
                                        }
                                    }

                                    adapter = new OfferwallAdapter(KeyboardOfferwallListActivity.this, categoryArray, new OfferwallAdapter.Listener() {
                                        @Override
                                        public void onItemClicked(OfferwallData data) {
                                            if ( data != null ) {
                                                if ( !offerwallClicked ) {
                                                    offerwallClicked = true;
                                                    LogPrint.d("data not null offerwallClicked false url :: " + data.getAdver_url());
                                                    String mission_class = data.getMission_class();
                                                    if ( !TextUtils.isEmpty(mission_class)) {
                                                        if ( mission_class.toLowerCase().equals("p1") ) {
                                                            String screenshot = data.getScreenshot();
                                                            if ( TextUtils.isEmpty(screenshot) ) {
                                                                offerwallParticipationCheck(data, false, mission_class);
                                                            } else {
                                                                if ( "N".equals(screenshot) ) {
                                                                    offerwallMissionClick(data, false);
                                                                } else {
                                                                    offerwallParticipationCheck(data, false, mission_class);
                                                                }
                                                            }
                                                        } else if ( mission_class.toLowerCase().equals("p2") || mission_class.toLowerCase().equals("p3") ) {
                                                            offerwallParticipationCheck(data, false, mission_class);
                                                        } else {
                                                            Toast.makeText(KeyboardOfferwallListActivity.this, getString(R.string.aikbd_offerwall_update), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                } else {
                                                    LogPrint.d("data not null offerwallClicked true");
                                                }
                                            } else {
                                                LogPrint.d("data null");
                                            }
                                        }

                                        @Override
                                        public void onCategoryClilcked(OfferwallCategoryData data) {
                                            categoryData = data;
                                            lastMissionSeq = "";
                                            getList(false, true);
                /*
                if ( adapter != null ) {
                    adapter.removeItems();
                }
                lastMissionSeq = "";
                getList(false);

                 */
                                        }
                                    });
                                    recyclerView.setAdapter(adapter);
                                    getList(false, true);

                                } else {
                                    if ( Common.IsFormissionTokenError(KeyboardOfferwallListActivity.this, rt) ) {
                                        if ( !isRetry ) {
                                            getCategoryWithToken(true);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void getList(boolean isRetry, boolean isFirstList) {
        if ( isDataReceiving )
            return;

        String mission_class = "";
        if ( categoryData != null )
            mission_class = categoryData.getMission_class();

        isDataReceiving = true;
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOfferwallListActivity.this);
        task.getOfferwallList(lastMissionSeq, mission_class, CustomAsyncTask.FORMISSION_PAGE_COUNT_20 + "", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                LogPrint.d("result :: " + result);
                if ( result ) {
                    if ( obj != null ) {
                        LogPrint.d("obj is :: " + obj.toString());
                        try {
                            JSONObject object = (JSONObject) obj;
                            if ( object != null ) {
                                int rt = object.optInt("result");
                                if ( rt == 0 ) {
                                    double total_user_point = object.optDouble("total_user_point", 0);

                                    JSONArray arr = object.optJSONArray("mission");
                                    if ( arr != null && arr.length() > 0 ) {
                                        ArrayList<OfferwallData> array = new ArrayList<>();

                                        for ( int i = 0 ; i < arr.length() ; i ++ ) {
                                            JSONObject missionObj = arr.optJSONObject(i);
                                            if ( missionObj != null ) {
                                                OfferwallData data = new OfferwallData();
                                                data.setScreenshot(missionObj.optString("screenshot"));
                                                data.setMission_class(missionObj.optString("mission_class"));
                                                data.setReg_date(missionObj.optString("reg_date"));
                                                data.setMedia_point(missionObj.optDouble("media_point"));
                                                data.setDaily_participation(missionObj.optInt("daily_participation"));
                                                data.setAdver_url(missionObj.optString("adver_url"));
                                                data.setIntro_img(missionObj.optString("intro_img"));
                                                data.setMission_id(missionObj.optString("mission_id"));
                                                data.setDaily_participation_cnt(missionObj.optInt("daily_participation_cnt"));
                                                data.setUser_point(missionObj.optDouble("user_point"));
                                                data.setCheck_time(missionObj.optInt("check_time"));
                                                data.setCheck_url(missionObj.optString("check_url"));
                                                data.setTarget_name(missionObj.optString("target_name"));
                                                int seq = missionObj.optInt("mission_seq");
                                                lastMissionSeq = seq + "";
                                                data.setMission_seq(seq);
                                                data.setShop_name(missionObj.optString("adver_name"));
                                                data.setKeyword(missionObj.optString("keyword"));
                                                data.setThumb_img(missionObj.optString("thumb_img"));
                                                array.add(data);
                                            }
                                        }
                                        /*
                                        ArrayList<String> testJSONArr = new ArrayList<>();
                                        testJSONArr.add(Test.TEST_P3_OBJECT);
                                        testJSONArr.add(Test.TEST_P3_OBJECT1);
                                        testJSONArr.add(Test.TEST_P3_OBJECT2);
                                        testJSONArr.add(Test.TEST_P3_OBJECT3);
                                        testJSONArr.add(Test.TEST_P3_OBJECT4);

                                        for (int i = 0 ; i < testJSONArr.size() ; i ++ ) {
                                            JSONObject t_obj = new JSONObject(testJSONArr.get(i));
                                            OfferwallData data = new OfferwallData();
                                            data.setScreenshot(t_obj.optString("screenshot"));
                                            data.setMission_class(t_obj.optString("mission_class"));
                                            data.setReg_date(t_obj.optString("reg_date"));
                                            data.setMedia_point(t_obj.optDouble("media_point"));
                                            data.setDaily_participation(t_obj.optInt("daily_participation"));
                                            data.setAdver_url(t_obj.optString("adver_url"));
                                            data.setIntro_img(t_obj.optString("intro_img"));
                                            data.setMission_id(t_obj.optString("mission_id"));
                                            data.setDaily_participation_cnt(t_obj.optInt("daily_participation_cnt"));
                                            data.setCheck_time(t_obj.optInt("check_time"));
                                            data.setCheck_url(t_obj.optString("check_url"));
                                            data.setUser_point(t_obj.optDouble("user_point"));
                                            int seq = t_obj.optInt("mission_seq");
                                            lastMissionSeq = seq + "";
                                            data.setMission_seq(seq);
                                            data.setShop_name(t_obj.optString("adver_name"));
                                            data.setKeyword(t_obj.optString("keyword"));
                                            data.setThumb_img(t_obj.optString("thumb_img"));
                                            data.setTarget_name(t_obj.optString("target_name"));
                                            array.add(data);
                                        }*/

                                        LogPrint.d("pomission array.size :: " + array.size());
                                        if ( adapter != null ) {
                                            if ( isFirstList ) {
                                                if  (empty_layer != null ) {
                                                    if ( array.size() <= 0 ) {
                                                        adapter.setItems(new ArrayList<OfferwallData>(), total_user_point);
                                                        empty_layer.setVisibility(View.VISIBLE);
                                                    } else {
                                                        empty_layer.setVisibility(View.GONE);
                                                        adapter.setItems(array, total_user_point);
                                                    }
                                                }
                                            } else {
                                                adapter.addItems(array, total_user_point);
                                            }
                                        }
                                        /*
                                        if ( adapter != null ) {
                                            LogPrint.d("adapter item count :: " + adapter.getItemCount());
                                            if ( adapter.getItemCount() > 1 ) {
                                                LogPrint.d("pomission adapter not null item count over 1");
                                                adapter.addItems(array, total_user_point);
                                            } else {
                                                LogPrint.d("pomission adapter not null item count less 1 array size :: " + array.size());
                                                if ( array.size() <= 0 ) {
                                                    empty_layer.setVisibility(View.VISIBLE);
                                                } else {
                                                    empty_layer.setVisibility(View.GONE);
                                                    adapter.setItems(array, total_user_point);
                                                }
                                            }
                                        } else
                                            LogPrint.d("pomission adapter null"); */
                                    } else {
                                        LogPrint.d("pomission array length zero lastMissionSeq :: " + lastMissionSeq);
                                        if ( adapter != null ) {
                                            if ( isFirstList ) {
                                                adapter.setItems(new ArrayList<OfferwallData>(), total_user_point);
                                                empty_layer.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                } else {
                                    if ( Common.IsFormissionTokenError(KeyboardOfferwallListActivity.this, rt) ) {
                                        if ( !isRetry ) {
                                            getListWithToken(true, isFirstList);
                                        }
                                    }
                                }
                            } else {
                                LogPrint.d("pomission object null");
                            }
                        } catch (Exception e) {
                            LogPrint.d("pomission exception");
                            e.printStackTrace();
                        }
                    } else {
                        LogPrint.d("pomission obj null");
                    }
                }
                isDataReceiving = false;
            }
        });
    }

    public void loadMixerBanner() {
        LogPrint.d("loadMixerBanner");
        if ( offerwall_ad_container != null )
            offerwall_ad_container.setVisibility(View.GONE);
        if ( offerwall_ad_layer != null ) {
            offerwall_ad_layer.removeAllViews();
            offerwall_ad_layer.setVisibility(View.GONE);
        }
        if (bannerView != null)
            bannerView.destroyAd();
        bannerView = null;
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);

        initMixerAD();
    }

    private void initMixerAD() {
        isMixerHasError = false;
        WebSettings settings = mixer_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        // 웹뷰 - HTML5 창 속성 추가
        String path = getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(path);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkLoads(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// https 이미지.
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mixer_webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else
            mixer_webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mixer_webview.setVerticalScrollBarEnabled(false);
        mixer_webview.setDrawingCacheEnabled(true);

        mixer_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(KeyboardOfferwallListActivity.this);
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
                            startActivity(browserIntent);
                            if(mixer_webview != null)
                                mixer_webview.loadUrl("javascript:mixerClickFn();");
                        } catch (ActivityNotFoundException e) {
//                            startActivity(Intent.createChooser(browserIntent, "Title"));

                        }
                        //loadBanner();
                        if (url.contains("//img.mobon.net/ad/linfo.php"))
                            mixer_webview.goBack();
                        return true;
                    }
                });
                return true;
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                LogPrint.d("console message :: " + message);
                if ((message.contains("Uncaught SyntaxError:") || message.contains("Uncaught ReferenceError:") || message.contains("AdapterFailCallback") || message.contains("Uncaught TypeError:")) && !message.contains("wp_json")) {
                    LogPrint.d("mixer contain error");
                    if (mixer_webview == null) {
                        LogPrint.d("mixer_webview null");
                        return;
                    } else {
                        LogPrint.d("mixer_webview not null");
                        mixer_webview.onPause();
                    }
                    // 믹서 실패 시 로직 후처리
                    if ( offerwall_ad_container != null ) {
                        offerwall_ad_container.setVisibility(View.GONE);
                    }
                    if ( offerwall_ad_layer != null ) {
                        offerwall_ad_layer.setVisibility(View.GONE);
                    }
                    if ( mixer_layer != null ) {
                        isMixerHasError = true;
                        mixer_layer.setVisibility(View.GONE);
                    }
                } else if (message.contains("AdapterSuccessCallback")) {
                    LogPrint.d("mixer AdapterSuccessCallback");
                    if (mixer_webview != null) {
                        // 믹서 로드 성공
                        isMixerHasError = false;
                    }
                } else {
                    LogPrint.d("mixer else console error :: " + message);
                }
            }
        });

        mixer_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogPrint.d("mixer shouldOverrideUrlLoading :: " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.cancel();
                LogPrint.d("mixer onReceivedSslError");

                if (mixer_webview == null)
                    return;
                else {
                    mixer_webview.onPause();
                }
                // 믹서 실패 시 로직 후처리
                if ( offerwall_ad_container != null ) {
                    offerwall_ad_container.setVisibility(View.GONE);
                }
                if ( offerwall_ad_layer != null ) {
                    offerwall_ad_layer.setVisibility(View.GONE);
                }
                if ( mixer_layer != null ) {
                    isMixerHasError = true;
                    mixer_layer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest
                    request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LogPrint.d("mixer onReceivedError :: " + error.toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (error.getErrorCode() == -1)
                        return;
                }

                if (mixer_webview == null)
                    return;
                else {
                    mixer_webview.onPause();
                }
                // 믹서 실패 시 로직 후처리
                view.loadUrl("about:blank");
                if ( offerwall_ad_container != null ) {
                    offerwall_ad_container.setVisibility(View.GONE);
                }
                if ( offerwall_ad_layer != null ) {
                    offerwall_ad_layer.setVisibility(View.GONE);
                }
                if ( mixer_layer != null ) {
                    isMixerHasError = true;
                    mixer_layer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                //  view.setVisibility(View.VISIBLE);
                String targetUrl = "https://mixer.mobon.net/script?sspNo=639&w=320&h=50&ver=5.3.0&carrier=SKTelecom&ifa=" + MobonUtils.getAdid(KeyboardOfferwallListActivity.this) + "&requestType=API";

                LogPrint.d("mixer isMixerHasError :: " + isMixerHasError);
                if ( !isMixerHasError ) {
                    if (targetUrl.contains(url)) {
                        if ( offerwall_ad_container != null ) {
                            offerwall_ad_container.setVisibility(View.VISIBLE);
                        }
                        if ( offerwall_ad_layer != null ) {
                            offerwall_ad_layer.setVisibility(View.GONE);
                        }
                        if ( mixer_layer != null && !isMixerHasError ) {
                            LogPrint.d("mixer_layer not null");
                            mixer_layer.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if ( offerwall_ad_container != null ) {
                        offerwall_ad_container.setVisibility(View.GONE);
                    }
                    if ( offerwall_ad_layer != null ) {
                        offerwall_ad_layer.setVisibility(View.GONE);
                    }
                    if ( mixer_layer != null) {
                        LogPrint.d("mixer_layer not null");
                        mixer_layer.setVisibility(View.GONE);
                    }
                }
            }
        });
        String url = "https://mixer.mobon.net/script?sspNo=639&w=320&h=50&ver=5.3.0&carrier=SKTelecom&ifa=" + MobonUtils.getAdid(KeyboardOfferwallListActivity.this) + "&requestType=API";
        LogPrint.d("mixer url :: " + url);
        mixer_webview.loadUrl(url);
    }

    private void loadBanner() {
        if ( offerwall_ad_container != null )
            offerwall_ad_container.setVisibility(View.GONE);
        if ( offerwall_ad_layer != null ) {
            offerwall_ad_layer.removeAllViews();
            offerwall_ad_layer.setVisibility(View.GONE);
        }
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);

        if (bannerView != null)
            bannerView.destroyAd();
        bannerView = null;
        initBannerView();
        bannerView.loadAd();
    }

    public void initBannerView() {
        String bannerUnitId = "551812"; // 모비온 배너와 동일한 id 사용 from. 임병규 이사님
        new MobonSimpleSDK(KeyboardOfferwallListActivity.this, "okaycashbag");
        bannerView = new MobonBannerView(KeyboardOfferwallListActivity.this, MobonBannerType.BANNER_CUSTOM, isRun, false).setExtractColor(false).setBannerUnitId(bannerUnitId);
        isRun = true;
        LogPrint.d("kksskk initBannerView");
        offerwall_ad_layer.setBackgroundColor(Color.TRANSPARENT);

        bannerView.setAdListener(new iSimpleMobonBannerCallback() {
            @Override
            public void onLoadedAdInfo(boolean result, String errorStr) {
                offerwall_ad_container.setVisibility(View.GONE);
                if (result) {
                    LogPrint.d("kksskk onLoadedAdInfo true");
                    if (bannerView != null) {
                        LogPrint.d("kksskk banner C visible");
                        offerwall_ad_container.setVisibility(View.VISIBLE);
                        offerwall_ad_layer.setVisibility(View.VISIBLE);
                        mixer_layer.setVisibility(View.GONE);

                        if ( bannerView != null && (ViewGroup)bannerView.getParent() != null ) {
                            ((ViewGroup)bannerView.getParent()).removeAllViews();
                        }

                        offerwall_ad_layer.removeAllViews();
                        offerwall_ad_layer.addView(bannerView);
                        LogPrint.d("kksskk after addView");
                        ad_del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LogPrint.d("kksskk banner onClick C GONE");
                                offerwall_ad_container.setVisibility(View.GONE);
                            }
                        });
                    }
                } else {
                    LogPrint.d("kksskk banner C false onLoadedAdInfo GONE");
                    offerwall_ad_container.setVisibility(View.GONE);
                    bannerView.onDestroy();

                    loadMixerBanner();
                }
            }

            @Override
            public void onAdClicked() {
                KeyboardLogPrint.d("click ads");
                loadBanner();
            }

            @Override
            public void onCloseClicked() {

            }

            @Override
            public void onBannerLoaded(int leftColor, int rightColor) {
                LogPrint.d("kksskk onBannerLoaded leftColor :: " + leftColor + " , rightColor :: " + rightColor);
            }
        });
    }

    private BroadcastReceiver offerwall_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogPrint.d("offerwall receiver action :: " + action);
            if (OFFERWALL_JOIN.equals(action)) {
                if ( adapter != null ) {
                    adapter.removeItems();
                    lastMissionSeq = "";
                    getList(false, true);
                }
            }
        }
    };
}
