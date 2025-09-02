package com.enliple.keyboard.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.adapter.KeyboardChatGPTAdapter;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.mobonAD.MobonKey;
import com.enliple.keyboard.mobonAD.MobonUtils;
import com.enliple.keyboard.mobonAD.manager.SPManager;
import com.enliple.keyboard.models.ChatGPTModel;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.AikbdAdLoadingLayer;
import com.enliple.keyboard.ui.CustomAdView;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.KeyboardCallback;
import com.enliple.keyboard.ui.common.LogPrint;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class KeyboardChatGptChatActivity extends Activity {
    private static final String MOBON_DOMAIN = "https://www.mediacategory.com";
    public static final String AD_CAULY = "cauly";
    public static final String AD_MOBON = "mobon";
    public static final String AD_COUPANG = "coupang";
    public static final String AD_NO_AD = "noAD";
    private static final int AD_INIT = -1;
    private static final int AD_SUCCESS = 1;
    private static final int AD_FAIL = 2;
    public static String MEDIA_KEY = "44288150";
    public static String ADUNIT_ID_INTERSTITIAL_BANNER = "114178938";

    public static Activity mActivity = null;
    private RecyclerView chat_recyclerview;
    private ConstraintLayout bot_layer;
    private EditText chat_edit;
    private TextView img_send;
    private RelativeLayout btn_send;
    private TextView btn_close;
    private TextView ticket_count;
    private RelativeLayout get_ticket_layer;
    private TextView btn_get_ticket;
    private ConstraintLayout root;
    private KeyboardChatGPTAdapter adapter;
    private boolean requestPossible = false;
    private int ticketCount = 0;
    private SimpleDateFormat format = new SimpleDateFormat("a HH:mm", Locale.KOREA);
    private NestedScrollView header_layer;
    private ConstraintLayout total_content;
    private NestedScrollView scroll;
    private View bot_empty_layer;
    // ad
    private RelativeLayout container_banner;
    private TextView ad_close;
    private CardView webview_layer;
    private WebView ad_webview;
    private ImageView ad_image_test;
    private AikbdAdLoadingLayer ad_loading_layer;
    private CustomAdView custom_ad_view;
    private TextView bot_str;
    //    private boolean isMobWithHasError = false;
    private int ad_status = AD_INIT;
    private String jsonArrayData = "";

    private RakeAPI rake;
    private String rake_page_key = "/keyboard/chatbot";
    private String clickAdLink = "";
    private String clickLogoLink = "";
    private long keyboardStatusChagneTime = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogPrint.d("chat activity onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aikbd_activity_chat_gpt);
        initViews();

        setRake("");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogPrint.d("onResume");
        mActivity = this;
        registReceiver();
        sendBroadcast(new Intent(SoftKeyboard.CHAT_GPT_RESUME));
    }

    public void onBackPressed() {
        if (container_banner.getVisibility() != View.VISIBLE) {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogPrint.d("onPause");
        mActivity = null;
        unregistReceiver();
        sendBroadcast(new Intent(SoftKeyboard.CHAT_GPT_PAUSE));

        LogPrint.d("skkim chat gpt onPause mActivity null");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void registReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("KEYBOARD_STATUS");
        // target 34 대응
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(receiver, intentFilter);
        }
//        registerReceiver(receiver, intentFilter);
    }

    private void unregistReceiver() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        } else
            LogPrint.d("myReceiver null");
/*
        if ( mMainKeyboardView != null ) {
            mMainKeyboardView.unregisterReceiver();
        }
 */
    }

    private void initViews() {
        if (CustomAsyncTask.GUBUN_RELEASE.equals(CustomAsyncTask.gubun))
            rake = RakeAPI.getInstance(KeyboardChatGptChatActivity.this, Common.LIVE_TOKEN, RakeAPI.Env.LIVE, RakeAPI.Logging.DISABLE);
        else
            rake = RakeAPI.getInstance(KeyboardChatGptChatActivity.this, Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.DISABLE);
        setKeyboardEvent();
        header_layer = findViewById(R.id.header_layer);
        container_banner = findViewById(R.id.container_banner);
        ad_close = findViewById(R.id.close_ad);
        webview_layer = findViewById(R.id.webview_layer);
        bot_str = findViewById(R.id.bot_str);
        chat_recyclerview = findViewById(R.id.chat_recyclerview);
        bot_layer = findViewById(R.id.bot_layer);
        chat_edit = findViewById(R.id.chat_edit);
        img_send = findViewById(R.id.img_send);
        btn_send = findViewById(R.id.btn_send);
        btn_close = findViewById(R.id.btn_close);
        ticket_count = findViewById(R.id.ticket_count);
        btn_get_ticket = findViewById(R.id.btn_get_ticket);
        get_ticket_layer = findViewById(R.id.get_ticket_layer);
        root = findViewById(R.id.root);
        bot_empty_layer = findViewById(R.id.bot_empty_layer);
        total_content = findViewById(R.id.total_content);
        scroll = findViewById(R.id.scroll);

        container_banner.setVisibility(View.GONE);

        img_send.setBackgroundResource(R.drawable.aikbd_btn_chat_send_disable);

        chat_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chat_edit != null && chat_edit.getText().toString().length() == 0)
                    img_send.setBackgroundResource(R.drawable.aikbd_btn_chat_send_disable);
                else {
                    if (chat_edit != null && chat_edit.getText().toString().length() > 0) {
                        if (requestPossible) {
                            img_send.setBackgroundResource(R.drawable.aikbd_btn_chat_send_enable);
                        } else {
                            img_send.setBackgroundResource(R.drawable.aikbd_btn_chat_send_disable);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_send.setOnClickListener(clickListener);
        btn_close.setOnClickListener(clickListener);
        btn_get_ticket.setOnClickListener(clickListener);
        LinearLayoutManager manager = new LinearLayoutManager(KeyboardChatGptChatActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        chat_recyclerview.setLayoutManager(manager);

        adapter = new KeyboardChatGPTAdapter(KeyboardChatGptChatActivity.this, new KeyboardChatGPTAdapter.Listener() {
            @Override
            public void onListUpdated() {
                LogPrint.d("onListUpdated");
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        chat_recyclerview.scrollToPosition(adapter.getItemCount() - 1);
//                    }
//                }, 100);
                scroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 100);
            }
        });
        chat_recyclerview.setAdapter(adapter);

        chat_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
        task.getGptTickerCount(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            LogPrint.d("chat list :: " + object.toString());
                            boolean rt = object.optBoolean("Result");
                            LogPrint.d("skkim chat gpt rt :: " + rt);
                            if (rt) {
                                ticketCount = object.optInt("use_count");
                                if (ticketCount <= 0) {
                                    ticket_count.setTextColor(Color.parseColor("#868686"));
                                } else {
                                    ticket_count.setTextColor(Color.parseColor("#000000"));
                                }
                                ticket_count.setText("" + ticketCount);

                                JSONArray array = object.optJSONArray("last_answer");
                                if (array != null && array.length() > 0) {
                                    LogPrint.d("skkim chat gpt array over zero ticket count :: " + ticketCount);
                                    jsonArrayData = object.toString();
                                    if (!TextUtils.isEmpty(jsonArrayData)) {
                                        if (array != null && array.length() > 0) {
                                            header_layer.setVisibility(View.GONE);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject inObj = array.optJSONObject(i);
                                                if (inObj != null) {
                                                    String question = inObj.optString("question");
                                                    String answer = inObj.optString("answer");
                                                    String type = inObj.optString("type", "Y");
                                                    ChatGPTModel q_model = new ChatGPTModel();
                                                    q_model.setContent(question);
                                                    q_model.setType(KeyboardChatGPTAdapter.TYPE_QUESTION);
                                                    q_model.setAnswerType("Y");
                                                    adapter.addItem(q_model, false);

                                                    ChatGPTModel a_model = new ChatGPTModel();
                                                    a_model.setContent(answer);
                                                    a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
                                                    a_model.setLoading(false);
                                                    a_model.setAnswerType(type);
                                                    adapter.addItem(a_model, false);
                                                }
                                            }
                                        }
                                        scroll.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                LogPrint.d("skkim chat gpt scroll down");
                                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                                            }
                                        }, 200);
                                    } else {
                                        header_layer.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                LogPrint.d("skkim chat gpt header scroll down");
                                                header_layer.fullScroll(ScrollView.FOCUS_DOWN);
                                            }
                                        }, 200);
                                    }

                                    if (ticketCount > 0) {
                                        requestPossible = true;
                                    } else
                                        requestPossible = false;
                                    LogPrint.d("skkim chat gpt requestPossible :: " + requestPossible);
                                    if (requestPossible) {
                                        get_ticket_layer.setVisibility(View.GONE);
                                        bot_layer.setVisibility(View.VISIBLE);
                                    } else {
                                        get_ticket_layer.setVisibility(View.VISIBLE);
                                        bot_layer.setVisibility(View.GONE);

                                        header_layer.setVisibility(View.GONE);
                                        scroll.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                                            }
                                        }, 100);
                                    }
                                } else {
                                    LogPrint.d("skkim chat gpt arr less zero :: ");
                                    header_layer.setVisibility(View.VISIBLE);
                                    if (ticketCount > 0) {
                                        requestPossible = true;
                                    } else
                                        requestPossible = false;

                                    if (requestPossible) {
                                        get_ticket_layer.setVisibility(View.GONE);
                                        bot_layer.setVisibility(View.VISIBLE);

                                        scroll.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                                            }
                                        }, 100);

                                    } else {
                                        get_ticket_layer.setVisibility(View.VISIBLE);
                                        bot_layer.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                LogPrint.d("skkim chat gpt rt false");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    LogPrint.d("result  false");
                    JSONObject object = (JSONObject) obj;
                    if ( object != null ) {
                        try {
                            String network_str = object.optString(Common.NETWORK_DISCONNECT);
                            if ( !TextUtils.isEmpty(network_str) )
                                Toast.makeText(KeyboardChatGptChatActivity.this, network_str, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void sendChat() {
        if (chat_edit != null && !TextUtils.isEmpty(chat_edit.getText().toString())) {
            LogPrint.d("gpt requestPossible");
            if (requestPossible) {
                if (header_layer != null && header_layer.getVisibility() == View.VISIBLE)
                    header_layer.setVisibility(View.GONE);
                String question = chat_edit.getText().toString();
                chat_edit.setText("");
                requestPossible = false;
                ChatGPTModel q_model = new ChatGPTModel();
                q_model.setContent(question);
                q_model.setType(KeyboardChatGPTAdapter.TYPE_QUESTION);
                q_model.setAnswerType("Y");
                adapter.addItem(q_model, true);

                ChatGPTModel a_model = new ChatGPTModel();
                a_model.setContent("");
                a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
                a_model.setLoading(true);
                a_model.setAnswerType("Y");
                adapter.addItem(a_model, true);



//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        chat_recyclerview.scrollToPosition(adapter.getItemCount() - 1);
//                    }
//                }, 200);

                scroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scroll.fullScroll(ScrollView.FOCUS_DOWN);

                    }
                }, 200);

                CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
                task.sendChat(question, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {
                        LogPrint.d("skkim chat gpt result :: " + result);
                        if (result) {
                            try {
                                JSONObject object = (JSONObject) obj;
                                if (object != null) {
                                    LogPrint.d("skkim chat gpt response :: " + object.toString());
                                    boolean rt = object.optBoolean("Result");
                                    if (rt) {
                                        String content = object.optString("answer_text");
                                        String type = object.optString("type", "Y");
                                        ChatGPTModel a_model = new ChatGPTModel();
                                        a_model.setContent(content);
                                        a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
                                        a_model.setLoading(false);
                                        a_model.setAnswerType(type);
                                        adapter.setItem(a_model);

//                                        chat_edit.setText("");

                                        ticketCount = object.optInt("use_count");
                                        LogPrint.d("ticket count :: " + ticketCount);
                                        if (ticketCount <= 0) {
                                            ticket_count.setTextColor(Color.parseColor("#000000"));
                                        } else {
                                            ticket_count.setTextColor(Color.parseColor("#868686"));
                                        }
                                        ticket_count.setText("" + ticketCount);
                                        if (ticketCount > 0)
                                            requestPossible = true;
                                        else
                                            requestPossible = false;

                                        if (requestPossible) {
                                            bot_layer.setVisibility(View.VISIBLE);
                                            get_ticket_layer.setVisibility(View.GONE);
                                        } else {
                                            bot_layer.setVisibility(View.GONE);
                                            get_ticket_layer.setVisibility(View.VISIBLE);
                                        }
                                        setRake("get.answer");
                                    } else {
                                        chat_edit.setText(question);

                                        ChatGPTModel a_model = new ChatGPTModel();
                                        a_model.setContent("");
                                        a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
                                        a_model.setLoading(false);
                                        a_model.setAnswerType("N");
                                        adapter.setItem(a_model);
                                        String errStr = object.optString("errstr");
                                        Toast.makeText(KeyboardChatGptChatActivity.this, errStr, Toast.LENGTH_SHORT).show();
                                        setRake("get.error");
                                    }
                                    scroll.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            scroll.fullScroll(ScrollView.FOCUS_DOWN);
                                        }
                                    }, 200);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (obj != null) {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        LogPrint.d("chat false obj :: " + object.toString());
                                    } else {
                                        LogPrint.d("chat false obj null :: ");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private String getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        return format.format(date);
    }

    private void chargeGptTicket(int index) {
        LogPrint.d("chargeGetTicket index :: " + index);
        CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
        task.chargeGptTicket(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            boolean rt = object.optBoolean("Result");
                            if (rt) {
                                ticketCount = object.optInt("use_count");
                                if (ticketCount <= 0) {
                                    ticket_count.setTextColor(Color.parseColor("#000000"));
                                } else {
                                    ticket_count.setTextColor(Color.parseColor("#868686"));
                                }
                                ticket_count.setText("" + ticketCount);
                                String toastMessage = "ChatGPT 이용권 " + ticketCount + "장 획득!";
                                Toast.makeText(KeyboardChatGptChatActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                                if (ticketCount > 0)
                                    requestPossible = true;
                                else
                                    requestPossible = false;

                                if (requestPossible) {
                                    bot_layer.setVisibility(View.VISIBLE);
                                    get_ticket_layer.setVisibility(View.GONE);
                                } else {
                                    bot_layer.setVisibility(View.GONE);
                                    get_ticket_layer.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.btn_send) {
                sendChat();
                setRake("tap.sendbtn");
            } else if (id == R.id.btn_close) {
                setRake("top_tap.closebtn");
                finish();
            } else if (id == R.id.btn_get_ticket) {
                // 광고 보러가기 후 callback 성공 넘어오면 아래 함수 호출
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0);
                    }
                }, 100);
                showCaulyAd();
                setRake("tap.getTicket");
            }
        }
    };

    private void showCaulyAd() {
        clickAdLink = "";
        clickLogoLink = "";
        if (ad_webview != null && webview_layer != null) {
            webview_layer.removeAllViews();
            if (ad_webview.getParent() != null)
                ((ViewGroup) ad_webview.getParent()).removeView(ad_webview);
        }

        ad_loading_layer = new AikbdAdLoadingLayer(KeyboardChatGptChatActivity.this);
        ad_loading_layer.setLoadingImage();
        custom_ad_view = new CustomAdView(KeyboardChatGptChatActivity.this, new CustomAdView.ClickListener() {
            @Override
            public void onAdClick() {
                LogPrint.d("onAdClick");
                if (!TextUtils.isEmpty(clickAdLink)) {
                    chargeGptTicket(0);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickAdLink));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    container_banner.setVisibility(View.GONE);

                    try {
                        if (clickAdLink != null)
                            LogPrint.d("Cauly chat ad click startActivity url :: " + clickAdLink.toString());
                        startActivity(intent);
                    } catch (Exception e) {
                        if (clickAdLink != null)
                            LogPrint.d("Cauly chat ad activity not found startActivity url :: " + clickAdLink.toString());
                        startActivity(Intent.createChooser(intent, "Title"));
                    }
                }
            }

            @Override
            public void onLogoClick() {
                if (!TextUtils.isEmpty(clickLogoLink)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickLogoLink));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        if (clickLogoLink != null)
                            LogPrint.d("Cauly chat ad click startActivity url :: " + clickLogoLink.toString());
                        startActivity(intent);
                    } catch (Exception e) {
                        if (clickLogoLink != null)
                            LogPrint.d("Cauly chat ad activity not found startActivity url :: " + clickLogoLink.toString());
                        startActivity(Intent.createChooser(intent, "Title"));
                    }
                }
            }

            @Override
            public void onImageSetted(boolean isSet) {
                if (isSet) {
                    webview_layer.removeAllViews();
                    if (bot_str != null)
                        bot_str.setVisibility(View.VISIBLE);

                    webview_layer.addView(custom_ad_view);

                    ad_loading_layer.setVisibility(View.GONE);
                }
            }
        });

        int targetWidth = getScreenWidth() - Common.convertDpToPx(KeyboardChatGptChatActivity.this, 110);
        int targetHeight = targetWidth * 1230 / 720;
        CardView.LayoutParams params = new CardView.LayoutParams(targetWidth, targetHeight);
        ad_loading_layer.setLayoutParams(params);
        webview_layer.addView(ad_loading_layer);
        bot_str.setVisibility(View.VISIBLE);
        container_banner.setVisibility(View.VISIBLE);

        if (ad_close != null)
            ad_close.setVisibility(View.GONE);

//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//            countDownTimer = null;
//        }

        ad_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ad_webview != null && webview_layer != null) {
                    webview_layer.removeAllViews();
                    if (ad_webview.getParent() != null)
                        ((ViewGroup) ad_webview.getParent()).removeView(ad_webview);
                }

                container_banner.setVisibility(View.GONE);
                setRake("tap.closebtn");
            }
        });

        CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
        task.getChatGptAd(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;

                        if (object != null) {
                            LogPrint.d("Cauly object :: " + object.toString());
                            String type = object.optString("type");
                            if (AD_CAULY.equals(type)) {
                                String link = object.optString("link");
                                LogPrint.d("Cauly link :: " + link);
                                setCaulyAd(link);
                            } else if (AD_MOBON.equals(type)) {
                                boolean mobonAd = setMobonAd(object);
                                if (!mobonAd) {
                                    LogPrint.d("mobonAd false");
                                    setNoAd(10);
                                } else {
                                    setRake("ad_display");
                                }
                            } else if (AD_COUPANG.equals(type)) {
                                boolean coupangAd = setCoupangAd(object);
                                if (!coupangAd) {
                                    LogPrint.d("coupangAd false");
                                    setNoAd(11);
                                } else {
                                    setRake("ad_display");
                                }
                            } else if (AD_NO_AD.equals(type)) {
                                setNoAd(12);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    LogPrint.d("Cauly api call response false");
                    setNoAd(0);
                }
                ad_close.setClickable(false);
                ad_close.setVisibility(View.VISIBLE);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(ad_close, "alpha", 0f, 1f);
                fadeIn.setDuration(500);
                fadeIn.start();

                fadeIn.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        ad_close.setClickable(true);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
            }
        });
    }

    public int getScreenWidth() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        int realScreenWidth = 0;
        if (Build.VERSION.SDK_INT >= 17) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
                android.graphics.Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                realScreenWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
                LogPrint.d("realScreenWidth 2 :: " + realScreenWidth);
            } else {
                DisplayMetrics realMetrics = new DisplayMetrics();
                display.getRealMetrics(realMetrics);
                realScreenWidth = realMetrics.widthPixels;
                LogPrint.d("realScreenWidth 1 :: " + realScreenWidth);
            }
        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realScreenWidth = (Integer) mGetRawW.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realScreenWidth = display.getWidth();
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realScreenWidth = display.getWidth();
        }
        return realScreenWidth;
    }

    private String getAdUrl(Context context) {
        String adid = MobonUtils.getAdid(context);
        String auid = SPManager.getString(context, MobonKey.AUID);
        if (TextUtils.isEmpty(auid))
            auid = "";
        return "https://www.mobwithad.com/api/v1/banner/app/ocbKeyboard?zone=" + Common.MOBWITH_RELEASE_CHAT_GPT +
                "&count=1&w=300&h=600&adid=" + adid + "&auid=" + auid;
    }

    private void setNoAd(int index) {
        LogPrint.d("setNoAd index :: " + index);
        ad_status = AD_FAIL;
        if (ad_webview != null && webview_layer != null) {
            webview_layer.removeAllViews();
            if (ad_webview.getParent() != null)
                ((ViewGroup) ad_webview.getParent()).removeView(ad_webview);
        }
        ad_loading_layer = new AikbdAdLoadingLayer(KeyboardChatGptChatActivity.this);
        ad_loading_layer.setAdFailImage();
        int targetWidth = getScreenWidth() - Common.convertDpToPx(KeyboardChatGptChatActivity.this, 110);
        int targetHeight = targetWidth * 1230 / 720;
        CardView.LayoutParams params = new CardView.LayoutParams(targetWidth, targetHeight);
        ad_loading_layer.setLayoutParams(params);
        webview_layer.addView(ad_loading_layer);

        if (ad_close != null) {
            ad_close.setClickable(true);
            ad_close.setVisibility(View.VISIBLE);
        }
        if (bot_str != null)
            bot_str.setVisibility(View.GONE);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogPrint.d("skkim chat gpt action :: " + action);
            if ("KEYBOARD_STATUS".equals(action)) {
                long callTime = System.currentTimeMillis();
                if (callTime - keyboardStatusChagneTime <= 200)
                    return;
                keyboardStatusChagneTime = callTime;
                boolean isKeyboardShow = intent.getBooleanExtra("isShow", false);
                int keyboardHeight = intent.getIntExtra("keyboardHeight", 0);
                LogPrint.d("skkim chat gpt isKeyboardShow :: " + isKeyboardShow);
                int t_keyboardHeight = SharedPreference.getInt(KeyboardChatGptChatActivity.this, Common.PREF_KEYBOARD_HEIGHT);
                LogPrint.d("skkim chat gpt t_keyboardHeight :: " + t_keyboardHeight);
                if ( bot_empty_layer != null ) {
                    LogPrint.d("skkim chat gpt bot_empty_layer.getHeight :: " + bot_empty_layer.getHeight());
                    if (bot_empty_layer.getHeight() < 100) {
                        ViewGroup.LayoutParams params = bot_empty_layer.getLayoutParams();
                        params.height = keyboardHeight;
                        bot_empty_layer.setLayoutParams(params);
                    }
                }


                if (isKeyboardShow) {
                    setRake("tap.textinput");
//                    if ( bot_empty_layer.getVisibility() != View.VISIBLE ) {
//                        bot_empty_layer.setVisibility(View.VISIBLE);
//                        LogPrint.d("skkim chat gpt bot_empty_layer visible");
//                    }
//                    setFocus();
                } else {
                    if ( bot_empty_layer != null ) {
                        if (bot_empty_layer.getVisibility() != View.GONE) {
                            LogPrint.d("skkim chat gpt bot_empty_layer gone");
                            bot_empty_layer.setVisibility(View.GONE);
                        }
                    }
                }
//                ConstraintLayout.LayoutParams param = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
                LogPrint.d("skkim chat gpt keyboardHeight :: " + keyboardHeight);
                if ( scroll != null && chat_edit != null) {
                    scroll.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isKeyboardShow)
                                scroll.scrollBy(0, keyboardHeight);
                            chat_edit.post(new Runnable() {
                                @Override
                                public void run() {
                                    LogPrint.d("requestFocus");
//                                chat_edit.clearFocus();
                                    chat_edit.requestFocus();
                                }
                            });
//                        else
//                            scroll.scrollBy(0, -keyboardHeight);
                        }
                    }, 100);
                }
            }
        }
    };

    private int getKeyboardHeight() {
        int keyboardViewHeight = SharedPreference.getInt(KeyboardChatGptChatActivity.this, Common.PREF_KEYBOARD_HEIGHT);
        int height = keyboardViewHeight + getResources().getDimensionPixelSize(R.dimen.aikbd_ad_height) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_line) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_padding) + getResources().getDimensionPixelSize(R.dimen.aikbd_top_height);

        return height;
    }

    public void setRake(String action_id) {
        new Thread() {
            public void run() {
                String track_id = SharedPreference.getString(KeyboardChatGptChatActivity.this, Key.KEY_OCB_TRACK_ID);
                String device_id = SharedPreference.getString(KeyboardChatGptChatActivity.this, Key.KEY_OCB_DEVICE_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    track_id = cp.Decode(KeyboardChatGptChatActivity.this, track_id);
                    device_id = cp.Decode(KeyboardChatGptChatActivity.this, device_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String time = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
                String session_id = time + "_" + device_id;
                OCBLogSentinelShuttle shuttle = new OCBLogSentinelShuttle();
                shuttle.page_id(rake_page_key).action_id(action_id).keyboard_log_yn("yes").session_id(session_id).mbr_id(track_id);
                rake.track(shuttle.toJSONObject());
            }
        }.start();
    }

    private boolean setCoupangAd(JSONObject object) {
        boolean isAdExist = false;
        if (object != null) {
            JSONArray array = object.optJSONArray("data");
            if (array != null && array.length() > 0) {
                JSONObject dataObj = array.optJSONObject(0);
                if (dataObj != null) {
                    String imagePath = dataObj.optString("productImage");
                    clickAdLink = dataObj.optString("productUrl");
                    boolean isRocket = dataObj.optBoolean("isRocket");
                    LogPrint.d("coupang imagePath :: " + imagePath);
                    isAdExist = true;
                    custom_ad_view.setAd(AD_COUPANG, isRocket, imagePath, "", 0, 0, getScreenWidth());
                }
            }
        }
        return isAdExist;
    }

    private boolean setMobonAd(JSONObject object) {
        boolean isAdExist = false;
        String imagePath = "";
        String logoPath = "";
        if (object != null) {
            JSONArray array = object.optJSONArray("client");
            if (array != null && array.length() > 0) {
                JSONObject inObj = array.optJSONObject(0);
                if (inObj != null) {
                    logoPath = inObj.optString("mobonLogo");
                    if (logoPath.startsWith("//"))
                        logoPath = "https:" + logoPath;
                    clickLogoLink = inObj.optString("mobonInfo");
                    if (clickLogoLink.startsWith("//"))
                        clickLogoLink = "https:" + clickLogoLink;

                    JSONArray inArr = inObj.optJSONArray("data");
                    if (inArr != null && inArr.length() > 0) {

                        JSONObject dataObj = inArr.optJSONObject(0);
                        if (dataObj != null) {
                            String purl = dataObj.optString("purl");
                            String img_first = dataObj.optString("mimg_720_1230");
                            String img_second = dataObj.optString("mimg_800_1500");
                            String img_third = dataObj.optString("mimg_250_250");
                            String img_default = dataObj.optString("img");
                            int base_width = 0;
                            int base_height = 0;
                            String default_mobon_image_url = "https://img.mobon.net";
                            if (!TextUtils.isEmpty(img_first) && !default_mobon_image_url.equals(img_first)) {
                                imagePath = img_first;
                                base_width = 720;
                                base_height = 1230;
                            } else {
                                if (!TextUtils.isEmpty(img_second) && !default_mobon_image_url.equals(img_second)) {
                                    imagePath = img_second;
                                    base_width = 800;
                                    base_height = 1500;
                                } else {
                                    if (!TextUtils.isEmpty(img_third) && !default_mobon_image_url.equals(img_third)) {
                                        imagePath = img_third;
                                        base_width = 250;
                                        base_height = 250;
                                    } else {
                                        if (!TextUtils.isEmpty(img_default)) {
                                            if (!"https://img.mobon.net".equals(img_default)) {
                                                imagePath = img_default;
                                                String removeExtention = imagePath.substring(0, imagePath.lastIndexOf("."));
                                                LogPrint.d("skkim removeExtention :: " + removeExtention);
                                                String size = removeExtention.substring(removeExtention.lastIndexOf("-") + 1);
                                                LogPrint.d("skkim size :: " + size);
                                                String[] sizeArr = size.split("x");
                                                LogPrint.d("sizeArr :: " + sizeArr.length);
                                                if (sizeArr != null && sizeArr.length == 2) {
                                                    String t_width = sizeArr[0];
                                                    String t_height = sizeArr[1];
                                                    if (!TextUtils.isEmpty(t_width) && !TextUtils.isEmpty(t_height)) {
                                                        try {
                                                            base_width = Integer.valueOf(t_width);
                                                            base_height = Integer.valueOf(t_height);
                                                            LogPrint.d("skkim base_width :: " + base_width);
                                                            LogPrint.d("skkim base_height :: " + base_height);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (!TextUtils.isEmpty(purl) && !TextUtils.isEmpty(imagePath) && !imagePath.startsWith("http://")) {
                                LogPrint.d("imagePath :: " + imagePath);
                                isAdExist = true;
                                clickAdLink = MOBON_DOMAIN + purl;
                                custom_ad_view.setAd(AD_MOBON, false, imagePath, logoPath, base_width, base_height, getScreenWidth());
                            } else {
                                isAdExist = false;
                            }
                        }
                    }
                }
            }
        }
        return isAdExist;
    }

    private void setCaulyAd(String link) {
        if (!TextUtils.isEmpty(link)) {
            ad_status = AD_SUCCESS;
            ad_webview = new WebView(KeyboardChatGptChatActivity.this);
            ad_webview.clearCache(true);

            ad_webview.setOverScrollMode(View.OVER_SCROLL_NEVER);

            WebSettings settings = ad_webview.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(false);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setSupportMultipleWindows(true);

            String path = getDir("database", Context.MODE_PRIVATE).getPath();
            settings.setDatabaseEnabled(false);
//                                settings.setDatabasePath(path);
            settings.setDomStorageEnabled(false);
            settings.setBlockNetworkLoads(false);
            settings.setAllowFileAccess(false);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// https 이미지.
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ad_webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else
                ad_webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            ad_webview.setVerticalScrollBarEnabled(false);
            ad_webview.setDrawingCacheEnabled(false);

            container_banner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            ad_webview.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    try {
                        WebView newWebView = new WebView(KeyboardChatGptChatActivity.this);
                        view.addView(newWebView);
                        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                        transport.setWebView(newWebView);
                        resultMsg.sendToTarget();

                        newWebView.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                LogPrint.d("Cauly ad click:: " + url);
                                chargeGptTicket(1);

                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                container_banner.setVisibility(View.GONE);

                                try {
                                    if (url != null)
                                        LogPrint.d("Cauly chat ad click startActivity url :: " + url.toString());
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
//                                    if (url != null)
//                                        LogPrint.d("Cauly chat ad activity not found startActivity url :: " + url.toString());
//                                    startActivity(Intent.createChooser(intent, "Title"));
                                }
                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }

                @Override
                public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                    LogPrint.d("Cauly chat ad console message :: " + message);
                    if (message.contains("Uncaught SyntaxError:") || message.contains("Uncaught ReferenceError:")) {
                        LogPrint.d("Cauly chat ad  contain error");

                        setNoAd(4);

                        if (ad_webview == null) {
                            LogPrint.d("Cauly chat ad  null");
                            return;
                        } else {
                            LogPrint.d("Cauly chat ad  not null");
                            ad_webview.onPause();
                        }
                    }
                }
            });

            ad_webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    LogPrint.d("Cauly chat ad shouldOverrideUrlLoading 1 :: " + url);
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    //super.onReceivedSslError(view, handler, error);
                    handler.cancel();
                    LogPrint.d("Cauly chat ad onReceivedSslError");

                    setNoAd(3);

                    if (ad_webview == null) {
                        LogPrint.d("Cauly chat ad webview null ");
                        return;
                    }
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest
                        request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    LogPrint.d("Cauly onReceivedError");

                    setNoAd(2);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        LogPrint.d("Cauly chat ad onReceivedError :: " + error.toString() + " , desc :: " + error.getDescription() + " , code :: " + error.getErrorCode());
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (error.getErrorCode() == -1)
                            return;
                    }

                    if (ad_webview == null) {
                        LogPrint.d("Cauly chat ad  webview null 1");
                        return;
                    }
                    // 모비위드 실패 시 로직 후처리
                    view.loadUrl("about:blank");
                }

                @Override
                public void onPageFinished(final WebView view, String url) {
                    LogPrint.d("Cauly chat ad   onPageFinished ad_status :: " + ad_status + " , url :: " + url);
                    if (link.contains(url)) {
                        if (ad_webview != null && webview_layer != null && container_banner != null && ad_status == AD_SUCCESS) {
                            LogPrint.d("Cauly chat ad  mobwith_layer not null");
                            int targetWidth = getScreenWidth() - Common.convertDpToPx(KeyboardChatGptChatActivity.this, 110);
                            int targetHeight = targetWidth * 1230 / 720;
                            LogPrint.d("Cauly chat ad targetWidth :: " + targetWidth + " , targetHeight :: " + targetHeight);

                            webview_layer.removeAllViews();
                            if ((ViewGroup) ad_webview.getParent() != null)
                                ((ViewGroup) ad_webview.getParent()).removeView(ad_webview);
                            if (bot_str != null)
                                bot_str.setVisibility(View.VISIBLE);
                            ad_webview.setLayoutParams(new ViewGroup.LayoutParams(targetWidth, targetHeight));

                            webview_layer.addView(ad_webview);

                            ad_loading_layer.setVisibility(View.GONE);

                            ad_close.setClickable(false);
                            ad_close.setVisibility(View.VISIBLE);
                            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(ad_close, "alpha", 0f, 1f);
                            fadeIn.setDuration(500);
                            fadeIn.start();

                            fadeIn.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(@NonNull Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(@NonNull Animator animator) {
                                    ad_close.setClickable(true);
                                }

                                @Override
                                public void onAnimationCancel(@NonNull Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(@NonNull Animator animator) {

                                }
                            });

                            setRake("ad_display");
                        }
                    }
                }
            });
            ad_webview.loadUrl(link);
        } else {
            LogPrint.d("Cauly api call response linke empty");
            setNoAd(1);
        }
    }

    private void setFocus() {
        chat_edit.post(new Runnable() {
            @Override
            public void run() {
                chat_edit.requestFocus();
            }
        });
    }

    private void setKeyboardEvent() {
        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        KeyboardCallback softKeyboard = new KeyboardCallback((ConstraintLayout) findViewById(R.id.root), controlManager);
        softKeyboard.setKeyboardCallbackCallback(new KeyboardCallback.KeyboardCallbackChanged() {
            @Override
            public void onKeyboardCallbackHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        LogPrint.d("skkim chat gpt isKeyboardShow new :: false");
                    }
                });
            }

            @Override
            public void onKeyboardCallbackShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        LogPrint.d("skkim chat gpt isKeyboardShow new :: true");
                    }
                });
            }
        });
    }

    private void showStopResponseDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardChatGptChatActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(KeyboardChatGptChatActivity.this).inflate(R.layout.aikbd_chat_gpt_dialog, viewGroup, false);
        Button aikbd_dialog_cancel = dialogView.findViewById(R.id.aikbd_dialog_cancel);
        Button aikbd_dialog_ok = dialogView.findViewById(R.id.aikbd_dialog_ok);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        aikbd_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이용권 차감 후 아래 로직 수행
                alertDialog.dismiss();
                KeyboardChatGptChatActivity.super.onBackPressed();
            }
        });
    }
}


//public class KeyboardChatGptChatActivity extends Activity {
//    private static final String MOBON_DOMAIN = "https://www.mediacategory.com";
//    public static final String AD_CAULY = "cauly";
//    public static final String AD_MOBON = "mobon";
//    public static final String AD_COUPANG = "coupang";
//    public static final String AD_NO_AD = "noAD";
//    private static final int AD_INIT = -1;
//    private static final int AD_SUCCESS = 1;
//    private static final int AD_FAIL = 2;
//    public static String MEDIA_KEY = "44288150";
//    public static String ADUNIT_ID_INTERSTITIAL_BANNER = "114178938";
//
//    public static Activity mActivity = null;
//    private RecyclerView chat_recyclerview;
//    private ConstraintLayout bot_layer;
//    private EditText chat_edit;
//    private TextView img_send;
//    private RelativeLayout btn_send;
//    private TextView btn_close;
//    private TextView ticket_count;
//    private RelativeLayout get_ticket_layer;
//    private TextView btn_get_ticket;
//    private ConstraintLayout root;
//    private KeyboardChatGPTAdapter adapter;
//    private boolean requestPossible = false;
//    private int ticketCount = 0;
//    private SimpleDateFormat format = new SimpleDateFormat("a HH:mm", Locale.KOREA);
//    private NestedScrollView header_layer;
//    private ConstraintLayout total_content;
//    private NestedScrollView scroll;
//    private View bot_empty_layer;
//    // ad
//    private RelativeLayout container_banner;
//    private TextView ad_close;
//    private CardView webview_layer;
//    private WebView ad_webview;
//    private ImageView ad_image_test;
//    private AikbdAdLoadingLayer ad_loading_layer;
//    private CustomAdView custom_ad_view;
//    private TextView bot_str;
//    //    private boolean isMobWithHasError = false;
//    private int ad_status = AD_INIT;
//    private CountDownTimer countDownTimer;
//    private String jsonArrayData = "";
//
//    private RakeAPI rake;
//    private String rake_page_key = "/keyboard/chatbot";
//    private String clickAdLink = "";
//    private String clickLogoLink = "";
//    private long keyboardStatusChagneTime = 0;
//    private boolean isSendChatProgressing = false;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        LogPrint.d("chat activity onCreate");
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.aikbd_activity_chat_gpt);
//        initViews();
//
//        setRake("");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        LogPrint.d("onResume");
//        mActivity = this;
//        registReceiver();
//        sendBroadcast(new Intent(SoftKeyboard.CHAT_GPT_RESUME));
//    }
//
//    public void onBackPressed() {
//        if (container_banner.getVisibility() != View.VISIBLE) {
//            if ( isSendChatProgressing ) {
//                showStopResponseDialog();
//            } else {
//                super.onBackPressed();
//            }
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        LogPrint.d("onPause");
//        mActivity = null;
//        unregistReceiver();
//        sendBroadcast(new Intent(SoftKeyboard.CHAT_GPT_PAUSE));
//
//        LogPrint.d("skkim chat gpt onPause mActivity null");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    private void registReceiver() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("KEYBOARD_STATUS");
//        registerReceiver(receiver, intentFilter);
//    }
//
//    private void unregistReceiver() {
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//        } else
//            LogPrint.d("myReceiver null");
///*
//        if ( mMainKeyboardView != null ) {
//            mMainKeyboardView.unregisterReceiver();
//        }
//
// */
//    }
//
//    private void initViews() {
//        if (CustomAsyncTask.GUBUN_RELEASE.equals(CustomAsyncTask.gubun))
//            rake = RakeAPI.getInstance(KeyboardChatGptChatActivity.this, Common.LIVE_TOKEN, RakeAPI.Env.LIVE, RakeAPI.Logging.DISABLE);
//        else
//            rake = RakeAPI.getInstance(KeyboardChatGptChatActivity.this, Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.DISABLE);
//        setKeyboardEvent();
//        header_layer = findViewById(R.id.header_layer);
//        container_banner = findViewById(R.id.container_banner);
//        ad_close = findViewById(R.id.close_ad);
//        webview_layer = findViewById(R.id.webview_layer);
//        bot_str = findViewById(R.id.bot_str);
//        chat_recyclerview = findViewById(R.id.chat_recyclerview);
//        bot_layer = findViewById(R.id.bot_layer);
//        chat_edit = findViewById(R.id.chat_edit);
//        img_send = findViewById(R.id.img_send);
//        btn_send = findViewById(R.id.btn_send);
//        btn_close = findViewById(R.id.btn_close);
//        ticket_count = findViewById(R.id.ticket_count);
//        btn_get_ticket = findViewById(R.id.btn_get_ticket);
//        get_ticket_layer = findViewById(R.id.get_ticket_layer);
//        root = findViewById(R.id.root);
//        bot_empty_layer = findViewById(R.id.bot_empty_layer);
//        total_content = findViewById(R.id.total_content);
//        scroll = findViewById(R.id.scroll);
//
//        container_banner.setVisibility(View.GONE);
//
//        img_send.setBackgroundResource(R.drawable.aikbd_btn_chat_send_disable);
//
//        chat_edit.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (chat_edit != null && chat_edit.getText().toString().length() == 0)
//                    img_send.setBackgroundResource(R.drawable.aikbd_btn_chat_send_disable);
//                else {
//                    if (chat_edit != null && chat_edit.getText().toString().length() > 0) {
//                        if (requestPossible) {
//                            img_send.setBackgroundResource(R.drawable.aikbd_btn_chat_send_enable);
//                        } else {
//                            img_send.setBackgroundResource(R.drawable.aikbd_btn_chat_send_disable);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        btn_send.setOnClickListener(clickListener);
//        btn_close.setOnClickListener(clickListener);
//        btn_get_ticket.setOnClickListener(clickListener);
//        LinearLayoutManager manager = new LinearLayoutManager(KeyboardChatGptChatActivity.this);
//        manager.setOrientation(RecyclerView.VERTICAL);
//        chat_recyclerview.setLayoutManager(manager);
//
//        adapter = new KeyboardChatGPTAdapter(KeyboardChatGptChatActivity.this, new KeyboardChatGPTAdapter.Listener() {
//            @Override
//            public void onListUpdated() {
//                LogPrint.d("onListUpdated");
////                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        chat_recyclerview.scrollToPosition(adapter.getItemCount() - 1);
////                    }
////                }, 100);
//                scroll.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                    }
//                }, 100);
//            }
//        });
//        chat_recyclerview.setAdapter(adapter);
//
//        chat_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
//
//        CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
//        task.getGptTickerCount(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//            @Override
//            public void onResponse(boolean result, Object obj) {
//                if (result) {
//                    try {
//                        JSONObject object = (JSONObject) obj;
//                        if (object != null) {
//                            LogPrint.d("chat list :: " + object.toString());
//                            boolean rt = object.optBoolean("Result");
//                            LogPrint.d("skkim chat gpt rt :: " + rt);
//                            if (rt) {
//                                ticketCount = object.optInt("use_count");
//                                if (ticketCount <= 0) {
//                                    ticket_count.setTextColor(Color.parseColor("#868686"));
//                                } else {
//                                    ticket_count.setTextColor(Color.parseColor("#000000"));
//                                }
//                                ticket_count.setText("" + ticketCount);
//
//                                JSONArray array = object.optJSONArray("last_answer");
//                                if (array != null && array.length() > 0) {
//                                    LogPrint.d("skkim chat gpt array over zero ticket count :: " + ticketCount);
//                                    jsonArrayData = object.toString();
//                                    if (!TextUtils.isEmpty(jsonArrayData)) {
//                                        if (array != null && array.length() > 0) {
//                                            header_layer.setVisibility(View.GONE);
//                                            for (int i = 0; i < array.length(); i++) {
//                                                JSONObject inObj = array.optJSONObject(i);
//                                                if (inObj != null) {
//                                                    String question = inObj.optString("question");
//                                                    String answer = inObj.optString("answer");
//                                                    String type = inObj.optString("type", "Y");
//                                                    ChatGPTModel q_model = new ChatGPTModel();
//                                                    q_model.setContent(question);
//                                                    q_model.setType(KeyboardChatGPTAdapter.TYPE_QUESTION);
//                                                    q_model.setAnswerType("Y");
//                                                    adapter.addItem(q_model, false);
//
//                                                    ChatGPTModel a_model = new ChatGPTModel();
//                                                    a_model.setContent(answer);
//                                                    a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
//                                                    a_model.setLoading(false);
//                                                    a_model.setAnswerType(type);
//                                                    adapter.addItem(a_model, false);
//                                                }
//                                            }
//                                        }
//                                        scroll.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                LogPrint.d("skkim chat gpt scroll down");
//                                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                                            }
//                                        }, 200);
//                                    } else {
//                                        header_layer.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                LogPrint.d("skkim chat gpt header scroll down");
//                                                header_layer.fullScroll(ScrollView.FOCUS_DOWN);
//                                            }
//                                        }, 200);
//                                    }
//
//                                    if (ticketCount > 0) {
//                                        requestPossible = true;
//                                    } else
//                                        requestPossible = false;
//                                    LogPrint.d("skkim chat gpt requestPossible :: " + requestPossible);
//                                    if (requestPossible) {
//                                        get_ticket_layer.setVisibility(View.GONE);
//                                        bot_layer.setVisibility(View.VISIBLE);
//                                    } else {
//                                        get_ticket_layer.setVisibility(View.VISIBLE);
//                                        bot_layer.setVisibility(View.GONE);
//
//                                        header_layer.setVisibility(View.GONE);
//                                        scroll.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                                            }
//                                        }, 100);
//                                    }
//                                } else {
//                                    LogPrint.d("skkim chat gpt arr less zero :: ");
//                                    header_layer.setVisibility(View.VISIBLE);
//                                    if (ticketCount > 0) {
//                                        requestPossible = true;
//                                    } else
//                                        requestPossible = false;
//
//                                    if (requestPossible) {
//                                        get_ticket_layer.setVisibility(View.GONE);
//                                        bot_layer.setVisibility(View.VISIBLE);
//
//                                        scroll.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                                            }
//                                        }, 100);
//
//                                    } else {
//                                        get_ticket_layer.setVisibility(View.VISIBLE);
//                                        bot_layer.setVisibility(View.GONE);
//                                    }
//                                }
//                            } else {
//                                LogPrint.d("skkim chat gpt rt false");
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//
//                }
//            }
//        });
//    }
//
//    private void sendChatNew() {
//        if (chat_edit != null && !TextUtils.isEmpty(chat_edit.getText().toString())) {
//            LogPrint.d("gpt requestPossible");
//            if (requestPossible) {
//                if (header_layer != null && header_layer.getVisibility() == View.VISIBLE)
//                    header_layer.setVisibility(View.GONE);
//                String question = chat_edit.getText().toString();
//                chat_edit.setText("");
//                requestPossible = false;
//                ChatGPTModel q_model = new ChatGPTModel();
//                q_model.setContent(question);
//                q_model.setType(KeyboardChatGPTAdapter.TYPE_QUESTION);
//                q_model.setAnswerType("Y");
//                adapter.addItem(q_model, true);
//
//                ChatGPTModel a_model = new ChatGPTModel();
//                a_model.setContent("");
//                a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
//                a_model.setLoading(true);
//                a_model.setAnswerType("Y");
//                adapter.addItem(a_model, true);
//
//
//
////                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        chat_recyclerview.scrollToPosition(adapter.getItemCount() - 1);
////                    }
////                }, 200);
//
//                scroll.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                        chat_edit.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                LogPrint.d("requestFocus");
////                                chat_edit.clearFocus();
////                                chat_edit.requestFocus();
//                            }
//                        });
//                    }
//                }, 200);
//                isSendChatProgressing = true;
//                String added_question = question + " 핵심만 간결하게 최대 100자 이내로";
//                CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
//                task.sendChatNew(question, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//                    @Override
//                    public void onResponse(boolean result, Object obj) {
//                        LogPrint.d("skkim chat gpt result :: " + result);
//                        if (result) {
//                            isSendChatProgressing = false;
//                            try {
//                                boolean isAnswerReceived = false;
//                                JSONObject object = (JSONObject) obj;
//                                if (object != null) {
//                                    JSONArray arr = object.optJSONArray("choices");
//                                    if ( arr != null && arr.length() > 0 ) {
//                                        JSONObject d_obj = arr.optJSONObject(0);
//                                        if ( d_obj != null ) {
//                                            JSONObject message_obj = d_obj.optJSONObject("message");
//                                            if ( message_obj != null ) {
//                                                String answer = message_obj.optString("content");
//                                                if ( !TextUtils.isEmpty(answer) ) {
//                                                    String type = object.optString("type", "Y");
//                                                    ChatGPTModel a_model = new ChatGPTModel();
//                                                    a_model.setContent(answer);
//                                                    a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
//                                                    a_model.setLoading(false);
//                                                    a_model.setAnswerType(type);
//                                                    adapter.setItem(a_model);
//
////                                        chat_edit.setText("");
//
//                                                    ticketCount = object.optInt("use_count");
//                                                    LogPrint.d("ticket count :: " + ticketCount);
//                                                    if (ticketCount <= 0) {
//                                                        ticket_count.setTextColor(Color.parseColor("#000000"));
//                                                    } else {
//                                                        ticket_count.setTextColor(Color.parseColor("#868686"));
//                                                    }
//                                                    ticket_count.setText("" + ticketCount);
//                                                    if (ticketCount > 0)
//                                                        requestPossible = true;
//                                                    else
//                                                        requestPossible = false;
//
//                                                    if (requestPossible) {
//                                                        bot_layer.setVisibility(View.VISIBLE);
//                                                        get_ticket_layer.setVisibility(View.GONE);
//                                                    } else {
//                                                        bot_layer.setVisibility(View.GONE);
//                                                        get_ticket_layer.setVisibility(View.VISIBLE);
//                                                    }
//                                                    isAnswerReceived = true;
//                                                    setRake("get.answer");
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    if ( !isAnswerReceived ) {
//                                        chat_edit.setText(question);
//
//                                        ChatGPTModel a_model = new ChatGPTModel();
//                                        a_model.setContent("답변을 찾을 수 없습니다.");
//                                        a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
//                                        a_model.setLoading(false);
//                                        a_model.setAnswerType("N");
//                                        adapter.setItem(a_model);
//                                        String errStr = "답변을 찾을 수 없습니다.";
//                                        Toast.makeText(KeyboardChatGptChatActivity.this, errStr, Toast.LENGTH_SHORT).show();
//                                        setRake("get.error");
//                                    }
//
//                                    scroll.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                                        }
//                                    }, 200);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            if (obj != null) {
//                                try {
//                                    JSONObject object = (JSONObject) obj;
//                                    if (object != null) {
//                                        LogPrint.d("chat false obj :: " + object.toString());
//                                    } else {
//                                        LogPrint.d("chat false obj null :: ");
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        }
//    }
//
//
//    private void sendChat() {
//        if (chat_edit != null && !TextUtils.isEmpty(chat_edit.getText().toString())) {
//            LogPrint.d("gpt requestPossible");
//            if (requestPossible) {
//                if (header_layer != null && header_layer.getVisibility() == View.VISIBLE)
//                    header_layer.setVisibility(View.GONE);
//                String question = chat_edit.getText().toString();
//                chat_edit.setText("");
//                requestPossible = false;
//                ChatGPTModel q_model = new ChatGPTModel();
//                q_model.setContent(question);
//                q_model.setType(KeyboardChatGPTAdapter.TYPE_QUESTION);
//                q_model.setAnswerType("Y");
//                adapter.addItem(q_model, true);
//
//                ChatGPTModel a_model = new ChatGPTModel();
//                a_model.setContent("");
//                a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
//                a_model.setLoading(true);
//                a_model.setAnswerType("Y");
//                adapter.addItem(a_model, true);
//
//
//
////                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        chat_recyclerview.scrollToPosition(adapter.getItemCount() - 1);
////                    }
////                }, 200);
//
//                scroll.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                        chat_edit.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                LogPrint.d("requestFocus");
////                                chat_edit.clearFocus();
////                                chat_edit.requestFocus();
//                            }
//                        });
//                    }
//                }, 200);
//
//                CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
//                task.sendChat(question, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//                    @Override
//                    public void onResponse(boolean result, Object obj) {
//                        LogPrint.d("skkim chat gpt result :: " + result);
//                        if (result) {
//                            try {
//                                JSONObject object = (JSONObject) obj;
//                                if (object != null) {
//                                    LogPrint.d("skkim chat gpt response :: " + object.toString());
//                                    boolean rt = object.optBoolean("Result");
//                                    if (rt) {
//                                        String content = object.optString("answer_text");
//                                        String type = object.optString("type", "Y");
//                                        ChatGPTModel a_model = new ChatGPTModel();
//                                        a_model.setContent(content);
//                                        a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
//                                        a_model.setLoading(false);
//                                        a_model.setAnswerType(type);
//                                        adapter.setItem(a_model);
//
////                                        chat_edit.setText("");
//
//                                        ticketCount = object.optInt("use_count");
//                                        LogPrint.d("ticket count :: " + ticketCount);
//                                        if (ticketCount <= 0) {
//                                            ticket_count.setTextColor(Color.parseColor("#000000"));
//                                        } else {
//                                            ticket_count.setTextColor(Color.parseColor("#868686"));
//                                        }
//                                        ticket_count.setText("" + ticketCount);
//                                        if (ticketCount > 0)
//                                            requestPossible = true;
//                                        else
//                                            requestPossible = false;
//
//                                        if (requestPossible) {
//                                            bot_layer.setVisibility(View.VISIBLE);
//                                            get_ticket_layer.setVisibility(View.GONE);
//                                        } else {
//                                            bot_layer.setVisibility(View.GONE);
//                                            get_ticket_layer.setVisibility(View.VISIBLE);
//                                        }
//                                        setRake("get.answer");
//                                    } else {
//                                        chat_edit.setText(question);
//
//                                        ChatGPTModel a_model = new ChatGPTModel();
//                                        a_model.setContent("");
//                                        a_model.setType(KeyboardChatGPTAdapter.TYPE_ANSWER);
//                                        a_model.setLoading(false);
//                                        a_model.setAnswerType("N");
//                                        adapter.setItem(a_model);
//                                        String errStr = object.optString("errstr");
//                                        Toast.makeText(KeyboardChatGptChatActivity.this, errStr, Toast.LENGTH_SHORT).show();
//                                        setRake("get.error");
//                                    }
//                                    scroll.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                                        }
//                                    }, 200);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            if (obj != null) {
//                                try {
//                                    JSONObject object = (JSONObject) obj;
//                                    if (object != null) {
//                                        LogPrint.d("chat false obj :: " + object.toString());
//                                    } else {
//                                        LogPrint.d("chat false obj null :: ");
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        }
//    }
//
//    private String getCurrentTime() {
//        long currentTime = System.currentTimeMillis();
//        Date date = new Date(currentTime);
//        return format.format(date);
//    }
//
//    private void chargeGptTicket(int index) {
//        LogPrint.d("chargeGetTicket index :: " + index);
//        CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
//        task.chargeGptTicket(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//            @Override
//            public void onResponse(boolean result, Object obj) {
//                if (result) {
//                    try {
//                        JSONObject object = (JSONObject) obj;
//                        if (object != null) {
//                            boolean rt = object.optBoolean("Result");
//                            if (rt) {
//                                ticketCount = object.optInt("use_count");
//                                if (ticketCount <= 0) {
//                                    ticket_count.setTextColor(Color.parseColor("#000000"));
//                                } else {
//                                    ticket_count.setTextColor(Color.parseColor("#868686"));
//                                }
//                                ticket_count.setText("" + ticketCount);
//                                String toastMessage = "ChatGPT 이용권 " + ticketCount + "장 획득!";
//                                Toast.makeText(KeyboardChatGptChatActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
//                                if (ticketCount > 0)
//                                    requestPossible = true;
//                                else
//                                    requestPossible = false;
//
//                                if (requestPossible) {
//                                    bot_layer.setVisibility(View.VISIBLE);
//                                    get_ticket_layer.setVisibility(View.GONE);
//                                } else {
//                                    bot_layer.setVisibility(View.GONE);
//                                    get_ticket_layer.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    private View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            int id = view.getId();
//            if (id == R.id.btn_send) {
//                sendChat();
//                setRake("tap.sendbtn");
//            } else if (id == R.id.btn_close) {
//                setRake("top_tap.closebtn");
//                finish();
//            } else if (id == R.id.btn_get_ticket) {
//                // 광고 보러가기 후 callback 성공 넘어오면 아래 함수 호출
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        inputMethodManager.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0);
//                    }
//                }, 100);
//                showCaulyAd();
//                setRake("tap.getTicket");
//            }
//        }
//    };
//
//    private void showCaulyAd() {
//        clickAdLink = "";
//        clickLogoLink = "";
//        if (ad_webview != null && webview_layer != null) {
//            webview_layer.removeAllViews();
//            if (ad_webview.getParent() != null)
//                ((ViewGroup) ad_webview.getParent()).removeView(ad_webview);
//        }
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (countDownTimer != null) {
//                    countDownTimer.cancel();
//                    countDownTimer = null;
//                }
//
//                countDownTimer = new CountDownTimer(2000, 1000) {
//                    @Override
//                    public void onTick(long leftTimeInMilliseconds) {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        ad_close.setClickable(false);
//                        ad_close.setVisibility(View.VISIBLE);
//                        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(ad_close, "alpha", 0f, 1f);
//                        fadeIn.setDuration(500);
//                        fadeIn.start();
//
//                        fadeIn.addListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(@NonNull Animator animator) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(@NonNull Animator animator) {
//                                ad_close.setClickable(true);
//                            }
//
//                            @Override
//                            public void onAnimationCancel(@NonNull Animator animator) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(@NonNull Animator animator) {
//
//                            }
//                        });
//                    }
//                }.start();
//            }
//        });
//
//        ad_loading_layer = new AikbdAdLoadingLayer(KeyboardChatGptChatActivity.this);
//        ad_loading_layer.setLoadingImage();
//        custom_ad_view = new CustomAdView(KeyboardChatGptChatActivity.this, new CustomAdView.ClickListener() {
//            @Override
//            public void onAdClick() {
//                LogPrint.d("onAdClick");
//                if (!TextUtils.isEmpty(clickAdLink)) {
//                    chargeGptTicket(0);
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickAdLink));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                    container_banner.setVisibility(View.GONE);
//
//                    try {
//                        if (clickAdLink != null)
//                            LogPrint.d("Cauly chat ad click startActivity url :: " + clickAdLink.toString());
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        if (clickAdLink != null)
//                            LogPrint.d("Cauly chat ad activity not found startActivity url :: " + clickAdLink.toString());
//                        startActivity(Intent.createChooser(intent, "Title"));
//                    }
//                }
//            }
//
//            @Override
//            public void onLogoClick() {
//                if (!TextUtils.isEmpty(clickLogoLink)) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickLogoLink));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                    try {
//                        if (clickLogoLink != null)
//                            LogPrint.d("Cauly chat ad click startActivity url :: " + clickLogoLink.toString());
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        if (clickLogoLink != null)
//                            LogPrint.d("Cauly chat ad activity not found startActivity url :: " + clickLogoLink.toString());
//                        startActivity(Intent.createChooser(intent, "Title"));
//                    }
//                }
//            }
//
//            @Override
//            public void onImageSetted(boolean isSet) {
//                if (isSet) {
//                    webview_layer.removeAllViews();
//                    if (bot_str != null)
//                        bot_str.setVisibility(View.VISIBLE);
//
//                    webview_layer.addView(custom_ad_view);
//
//                    ad_loading_layer.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        int targetWidth = getScreenWidth() - Common.convertDpToPx(KeyboardChatGptChatActivity.this, 110);
//        int targetHeight = targetWidth * 1230 / 720;
//        CardView.LayoutParams params = new CardView.LayoutParams(targetWidth, targetHeight);
//        ad_loading_layer.setLayoutParams(params);
//        webview_layer.addView(ad_loading_layer);
//        bot_str.setVisibility(View.VISIBLE);
//        container_banner.setVisibility(View.VISIBLE);
//
//        if (ad_close != null)
//            ad_close.setVisibility(View.GONE);
//
////        if (countDownTimer != null) {
////            countDownTimer.cancel();
////            countDownTimer = null;
////        }
//
//        ad_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ad_webview != null && webview_layer != null) {
//                    webview_layer.removeAllViews();
//                    if (ad_webview.getParent() != null)
//                        ((ViewGroup) ad_webview.getParent()).removeView(ad_webview);
//                }
//
//                if (countDownTimer != null) {
//                    countDownTimer.cancel();
//                    countDownTimer = null;
//                }
//
//                container_banner.setVisibility(View.GONE);
//                setRake("tap.closebtn");
//            }
//        });
//
//        CustomAsyncTask task = new CustomAsyncTask(KeyboardChatGptChatActivity.this);
//        task.getChatGptAd(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//            @Override
//            public void onResponse(boolean result, Object obj) {
//                if (result) {
//                    try {
//                        JSONObject object = (JSONObject) obj;
//
//                        if (object != null) {
//                            LogPrint.d("Cauly object :: " + object.toString());
//                            String type = object.optString("type");
//                            if (AD_CAULY.equals(type)) {
//                                String link = object.optString("link");
//                                LogPrint.d("Cauly link :: " + link);
//                                setCaulyAd(link);
//                            } else if (AD_MOBON.equals(type)) {
//                                boolean mobonAd = setMobonAd(object);
//                                if (!mobonAd) {
//                                    LogPrint.d("mobonAd false");
//                                    setNoAd(10);
//                                } else {
//                                    setRake("ad_display");
//                                }
//                            } else if (AD_COUPANG.equals(type)) {
//                                boolean coupangAd = setCoupangAd(object);
//                                if (!coupangAd) {
//                                    LogPrint.d("coupangAd false");
//                                    setNoAd(11);
//                                } else {
//                                    setRake("ad_display");
//                                }
//                            } else if (AD_NO_AD.equals(type)) {
//                                setNoAd(12);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    LogPrint.d("Cauly api call response false");
//                    setNoAd(0);
//                }
//            }
//        });
//    }
//
//    public int getScreenWidth() {
//        Display display = getWindow().getWindowManager().getDefaultDisplay();
//        int realScreenWidth = 0;
//        if (Build.VERSION.SDK_INT >= 17) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//                WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
//                android.graphics.Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
//                realScreenWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
//                LogPrint.d("realScreenWidth 2 :: " + realScreenWidth);
//            } else {
//                DisplayMetrics realMetrics = new DisplayMetrics();
//                display.getRealMetrics(realMetrics);
//                realScreenWidth = realMetrics.widthPixels;
//                LogPrint.d("realScreenWidth 1 :: " + realScreenWidth);
//            }
//        } else if (Build.VERSION.SDK_INT >= 14) {
//            //reflection for this weird in-between time
//            try {
//                Method mGetRawW = Display.class.getMethod("getRawWidth");
//                realScreenWidth = (Integer) mGetRawW.invoke(display);
//            } catch (Exception e) {
//                //this may not be 100% accurate, but it's all we've got
//                realScreenWidth = display.getWidth();
//            }
//
//        } else {
//            //This should be close, as lower API devices should not have window navigation bars
//            realScreenWidth = display.getWidth();
//        }
//        return realScreenWidth;
//    }
//
//    private String getAdUrl(Context context) {
//        String adid = MobonUtils.getAdid(context);
//        String auid = SPManager.getString(context, MobonKey.AUID);
//        if (TextUtils.isEmpty(auid))
//            auid = "";
//        return "https://www.mobwithad.com/api/v1/banner/app/ocbKeyboard?zone=" + Common.MOBWITH_RELEASE_CHAT_GPT +
//                "&count=1&w=300&h=600&adid=" + adid + "&auid=" + auid;
//    }
//
//    private void setNoAd(int index) {
//        LogPrint.d("setNoAd index :: " + index);
//        ad_status = AD_FAIL;
//        if (ad_webview != null && webview_layer != null) {
//            webview_layer.removeAllViews();
//            if (ad_webview.getParent() != null)
//                ((ViewGroup) ad_webview.getParent()).removeView(ad_webview);
//        }
//        ad_loading_layer = new AikbdAdLoadingLayer(KeyboardChatGptChatActivity.this);
//        ad_loading_layer.setAdFailImage();
//        int targetWidth = getScreenWidth() - Common.convertDpToPx(KeyboardChatGptChatActivity.this, 110);
//        int targetHeight = targetWidth * 1230 / 720;
//        CardView.LayoutParams params = new CardView.LayoutParams(targetWidth, targetHeight);
//        ad_loading_layer.setLayoutParams(params);
//        webview_layer.addView(ad_loading_layer);
//
//        if (ad_close != null) {
//            ad_close.setClickable(true);
//            ad_close.setVisibility(View.VISIBLE);
//        }
//        if (bot_str != null)
//            bot_str.setVisibility(View.GONE);
//    }
//
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            LogPrint.d("skkim chat gpt action :: " + action);
//            if ("KEYBOARD_STATUS".equals(action)) {
//                long callTime = System.currentTimeMillis();
//                if (callTime - keyboardStatusChagneTime <= 200)
//                    return;
//                keyboardStatusChagneTime = callTime;
//                boolean isKeyboardShow = intent.getBooleanExtra("isShow", false);
//                int keyboardHeight = intent.getIntExtra("keyboardHeight", 0);
//                LogPrint.d("skkim chat gpt isKeyboardShow :: " + isKeyboardShow);
//                LogPrint.d("skkim chat gpt bot_empty_layer.getHeight :: " + bot_empty_layer.getHeight());
//                int t_keyboardHeight = SharedPreference.getInt(KeyboardChatGptChatActivity.this, Common.PREF_KEYBOARD_HEIGHT);
//                LogPrint.d("skkim chat gpt t_keyboardHeight :: " + t_keyboardHeight);
//                if (bot_empty_layer.getHeight() < 100) {
//                    ViewGroup.LayoutParams params = bot_empty_layer.getLayoutParams();
//                    params.height = keyboardHeight;
//                    bot_empty_layer.setLayoutParams(params);
//                }
//
//                if (isKeyboardShow) {
//                    setRake("tap.textinput");
////                    if ( bot_empty_layer.getVisibility() != View.VISIBLE ) {
////                        bot_empty_layer.setVisibility(View.VISIBLE);
////                        LogPrint.d("skkim chat gpt bot_empty_layer visible");
////                    }
////                    setFocus();
//                } else {
//                    if (bot_empty_layer.getVisibility() != View.GONE) {
//                        LogPrint.d("skkim chat gpt bot_empty_layer gone");
//                        bot_empty_layer.setVisibility(View.GONE);
//                    }
//                }
////                ConstraintLayout.LayoutParams param = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
//                LogPrint.d("skkim chat gpt keyboardHeight :: " + keyboardHeight);
//                scroll.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (isKeyboardShow)
//                            scroll.scrollBy(0, keyboardHeight);
////                        else
////                            scroll.scrollBy(0, -keyboardHeight);
//                    }
//                }, 100);
//            }
//        }
//    };
//
//    private int getKeyboardHeight() {
//        int keyboardViewHeight = SharedPreference.getInt(KeyboardChatGptChatActivity.this, Common.PREF_KEYBOARD_HEIGHT);
//        int height = keyboardViewHeight + getResources().getDimensionPixelSize(R.dimen.aikbd_ad_height) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_line) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_padding) + getResources().getDimensionPixelSize(R.dimen.aikbd_top_height);
//
//        return height;
//    }
//
//    public void setRake(String action_id) {
//        new Thread() {
//            public void run() {
//                String track_id = SharedPreference.getString(KeyboardChatGptChatActivity.this, Key.KEY_OCB_TRACK_ID);
//                String device_id = SharedPreference.getString(KeyboardChatGptChatActivity.this, Key.KEY_OCB_DEVICE_ID);
//                try {
//                    E_Cipher cp = E_Cipher.getInstance();
//                    track_id = cp.Decode(KeyboardChatGptChatActivity.this, track_id);
//                    device_id = cp.Decode(KeyboardChatGptChatActivity.this, device_id);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                String time = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
//                String session_id = time + "_" + device_id;
//                OCBLogSentinelShuttle shuttle = new OCBLogSentinelShuttle();
//                shuttle.page_id(rake_page_key).action_id(action_id).keyboard_log_yn("yes").session_id(session_id).mbr_id(track_id);
//                rake.track(shuttle.toJSONObject());
//            }
//        }.start();
//    }
//
//    private boolean setCoupangAd(JSONObject object) {
//        boolean isAdExist = false;
//        if (object != null) {
//            JSONArray array = object.optJSONArray("data");
//            if (array != null && array.length() > 0) {
//                JSONObject dataObj = array.optJSONObject(0);
//                if (dataObj != null) {
//                    String imagePath = dataObj.optString("productImage");
//                    clickAdLink = dataObj.optString("productUrl");
//                    boolean isRocket = dataObj.optBoolean("isRocket");
//                    LogPrint.d("coupang imagePath :: " + imagePath);
//                    isAdExist = true;
//                    custom_ad_view.setAd(AD_COUPANG, isRocket, imagePath, "", 0, 0, getScreenWidth());
//                }
//            }
//        }
//        return isAdExist;
//    }
//
//    private boolean setMobonAd(JSONObject object) {
//        boolean isAdExist = false;
//        String imagePath = "";
//        String logoPath = "";
//        if (object != null) {
//            JSONArray array = object.optJSONArray("client");
//            if (array != null && array.length() > 0) {
//                JSONObject inObj = array.optJSONObject(0);
//                if (inObj != null) {
//                    logoPath = inObj.optString("mobonLogo");
//                    if (logoPath.startsWith("//"))
//                        logoPath = "https:" + logoPath;
//                    clickLogoLink = inObj.optString("mobonInfo");
//                    if (clickLogoLink.startsWith("//"))
//                        clickLogoLink = "https:" + clickLogoLink;
//
//                    JSONArray inArr = inObj.optJSONArray("data");
//                    if (inArr != null && inArr.length() > 0) {
//
//                        JSONObject dataObj = inArr.optJSONObject(0);
//                        if (dataObj != null) {
//                            String purl = dataObj.optString("purl");
//                            String img_first = dataObj.optString("mimg_720_1230");
//                            String img_second = dataObj.optString("mimg_800_1500");
//                            String img_third = dataObj.optString("mimg_250_250");
//                            String img_default = dataObj.optString("img");
//                            int base_width = 0;
//                            int base_height = 0;
//                            String default_mobon_image_url = "https://img.mobon.net";
//                            if (!TextUtils.isEmpty(img_first) && !default_mobon_image_url.equals(img_first)) {
//                                imagePath = img_first;
//                                base_width = 720;
//                                base_height = 1230;
//                            } else {
//                                if (!TextUtils.isEmpty(img_second) && !default_mobon_image_url.equals(img_second)) {
//                                    imagePath = img_second;
//                                    base_width = 800;
//                                    base_height = 1500;
//                                } else {
//                                    if (!TextUtils.isEmpty(img_third) && !default_mobon_image_url.equals(img_third)) {
//                                        imagePath = img_third;
//                                        base_width = 250;
//                                        base_height = 250;
//                                    } else {
//                                        if (!TextUtils.isEmpty(img_default)) {
//                                            if (!"https://img.mobon.net".equals(img_default)) {
//                                                imagePath = img_default;
//                                                String removeExtention = imagePath.substring(0, imagePath.lastIndexOf("."));
//                                                LogPrint.d("skkim removeExtention :: " + removeExtention);
//                                                String size = removeExtention.substring(removeExtention.lastIndexOf("-") + 1);
//                                                LogPrint.d("skkim size :: " + size);
//                                                String[] sizeArr = size.split("x");
//                                                LogPrint.d("sizeArr :: " + sizeArr.length);
//                                                if (sizeArr != null && sizeArr.length == 2) {
//                                                    String t_width = sizeArr[0];
//                                                    String t_height = sizeArr[1];
//                                                    if (!TextUtils.isEmpty(t_width) && !TextUtils.isEmpty(t_height)) {
//                                                        try {
//                                                            base_width = Integer.valueOf(t_width);
//                                                            base_height = Integer.valueOf(t_height);
//                                                            LogPrint.d("skkim base_width :: " + base_width);
//                                                            LogPrint.d("skkim base_height :: " + base_height);
//                                                        } catch (Exception e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            if (!TextUtils.isEmpty(purl) && !TextUtils.isEmpty(imagePath) && !imagePath.startsWith("http://")) {
//                                LogPrint.d("imagePath :: " + imagePath);
//                                isAdExist = true;
//                                clickAdLink = MOBON_DOMAIN + purl;
//                                custom_ad_view.setAd(AD_MOBON, false, imagePath, logoPath, base_width, base_height, getScreenWidth());
//                            } else {
//                                isAdExist = false;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return isAdExist;
//    }
//
//    private void setCaulyAd(String link) {
//        if (!TextUtils.isEmpty(link)) {
//            ad_status = AD_SUCCESS;
//            ad_webview = new WebView(KeyboardChatGptChatActivity.this);
//            ad_webview.clearCache(true);
//
//            ad_webview.setOverScrollMode(View.OVER_SCROLL_NEVER);
//
//            WebSettings settings = ad_webview.getSettings();
//            settings.setJavaScriptEnabled(true);
//            settings.setBuiltInZoomControls(false);
//            settings.setJavaScriptCanOpenWindowsAutomatically(true);
//            settings.setSupportMultipleWindows(true);
//
//            String path = getDir("database", Context.MODE_PRIVATE).getPath();
//            settings.setDatabaseEnabled(false);
////                                settings.setDatabasePath(path);
//            settings.setDomStorageEnabled(false);
//            settings.setBlockNetworkLoads(false);
//            settings.setAllowFileAccess(false);
//            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// https 이미지.
//                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                ad_webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            } else
//                ad_webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//
//            ad_webview.setVerticalScrollBarEnabled(false);
//            ad_webview.setDrawingCacheEnabled(false);
//
//            container_banner.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    return true;
//                }
//            });
//
//            ad_webview.setWebChromeClient(new WebChromeClient() {
//                @Override
//                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
//                    try {
//                        WebView newWebView = new WebView(KeyboardChatGptChatActivity.this);
//                        view.addView(newWebView);
//                        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//                        transport.setWebView(newWebView);
//                        resultMsg.sendToTarget();
//
//                        newWebView.setWebViewClient(new WebViewClient() {
//                            @Override
//                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                                LogPrint.d("Cauly ad click:: " + url);
//                                chargeGptTicket(1);
//
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                container_banner.setVisibility(View.GONE);
//
//                                try {
//                                    if (url != null)
//                                        LogPrint.d("Cauly chat ad click startActivity url :: " + url.toString());
//                                    startActivity(intent);
//                                } catch (Exception e) {
//                                    if (url != null)
//                                        LogPrint.d("Cauly chat ad activity not found startActivity url :: " + url.toString());
//                                    startActivity(Intent.createChooser(intent, "Title"));
//                                }
//                                return true;
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//
//                @Override
//                public void onConsoleMessage(String message, int lineNumber, String sourceID) {
//                    LogPrint.d("Cauly chat ad console message :: " + message);
//                    if (message.contains("Uncaught SyntaxError:") || message.contains("Uncaught ReferenceError:")) {
//                        LogPrint.d("Cauly chat ad  contain error");
//
//                        setNoAd(4);
//
//                        if (ad_webview == null) {
//                            LogPrint.d("Cauly chat ad  null");
//                            return;
//                        } else {
//                            LogPrint.d("Cauly chat ad  not null");
//                            ad_webview.onPause();
//                        }
//                    }
//                }
//            });
//
//            ad_webview.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    LogPrint.d("Cauly chat ad shouldOverrideUrlLoading 1 :: " + url);
//                    return super.shouldOverrideUrlLoading(view, url);
//                }
//
//                @Override
//                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                    //super.onReceivedSslError(view, handler, error);
//                    handler.cancel();
//                    LogPrint.d("Cauly chat ad onReceivedSslError");
//
//                    setNoAd(3);
//
//                    if (ad_webview == null) {
//                        LogPrint.d("Cauly chat ad webview null ");
//                        return;
//                    }
//                }
//
//                @Override
//                public void onReceivedError(WebView view, WebResourceRequest
//                        request, WebResourceError error) {
//                    super.onReceivedError(view, request, error);
//                    LogPrint.d("Cauly onReceivedError");
//
//                    setNoAd(2);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        LogPrint.d("Cauly chat ad onReceivedError :: " + error.toString() + " , desc :: " + error.getDescription() + " , code :: " + error.getErrorCode());
//                    }
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (error.getErrorCode() == -1)
//                            return;
//                    }
//
//                    if (ad_webview == null) {
//                        LogPrint.d("Cauly chat ad  webview null 1");
//                        return;
//                    }
//                    // 모비위드 실패 시 로직 후처리
//                    view.loadUrl("about:blank");
//                }
//
//                @Override
//                public void onPageFinished(final WebView view, String url) {
//                    LogPrint.d("Cauly chat ad   onPageFinished ad_status :: " + ad_status + " , url :: " + url);
//                    if (link.contains(url)) {
//                        if (ad_webview != null && webview_layer != null && container_banner != null && ad_status == AD_SUCCESS) {
//                            LogPrint.d("Cauly chat ad  mobwith_layer not null");
//                            int targetWidth = getScreenWidth() - Common.convertDpToPx(KeyboardChatGptChatActivity.this, 110);
//                            int targetHeight = targetWidth * 1230 / 720;
//                            LogPrint.d("Cauly chat ad targetWidth :: " + targetWidth + " , targetHeight :: " + targetHeight);
//
//                            webview_layer.removeAllViews();
//                            if ((ViewGroup) ad_webview.getParent() != null)
//                                ((ViewGroup) ad_webview.getParent()).removeView(ad_webview);
//                            if (bot_str != null)
//                                bot_str.setVisibility(View.VISIBLE);
//                            ad_webview.setLayoutParams(new ViewGroup.LayoutParams(targetWidth, targetHeight));
//
//                            webview_layer.addView(ad_webview);
//
//                            ad_loading_layer.setVisibility(View.GONE);
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (countDownTimer != null) {
//                                        countDownTimer.cancel();
//                                        countDownTimer = null;
//                                    }
//
//                                    countDownTimer = new CountDownTimer(3500, 1000) {
//                                        @Override
//                                        public void onTick(long leftTimeInMilliseconds) {
//                                        }
//
//                                        @Override
//                                        public void onFinish() {
//                                            ad_close.setClickable(false);
//                                            ad_close.setVisibility(View.VISIBLE);
//                                            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(ad_close, "alpha", 0f, 1f);
//                                            fadeIn.setDuration(500);
//                                            fadeIn.start();
//
//                                            fadeIn.addListener(new Animator.AnimatorListener() {
//                                                @Override
//                                                public void onAnimationStart(@NonNull Animator animator) {
//
//                                                }
//
//                                                @Override
//                                                public void onAnimationEnd(@NonNull Animator animator) {
//                                                    ad_close.setClickable(true);
//                                                }
//
//                                                @Override
//                                                public void onAnimationCancel(@NonNull Animator animator) {
//
//                                                }
//
//                                                @Override
//                                                public void onAnimationRepeat(@NonNull Animator animator) {
//
//                                                }
//                                            });
//                                        }
//                                    }.start();
//                                }
//                            });
//
//                            setRake("ad_display");
//                        }
//                    }
//                }
//            });
//            ad_webview.loadUrl(link);
//        } else {
//            LogPrint.d("Cauly api call response linke empty");
//            setNoAd(1);
//        }
//    }
//
//    private void setFocus() {
//        chat_edit.post(new Runnable() {
//            @Override
//            public void run() {
//                chat_edit.requestFocus();
//            }
//        });
//    }
//
//    private void setKeyboardEvent() {
//        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
//        KeyboardCallback softKeyboard = new KeyboardCallback((ConstraintLayout) findViewById(R.id.root), controlManager);
//        softKeyboard.setKeyboardCallbackCallback(new KeyboardCallback.KeyboardCallbackChanged() {
//            @Override
//            public void onKeyboardCallbackHide() {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogPrint.d("skkim chat gpt isKeyboardShow new :: false");
//                    }
//                });
//            }
//
//            @Override
//            public void onKeyboardCallbackShow() {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogPrint.d("skkim chat gpt isKeyboardShow new :: true");
//                    }
//                });
//            }
//        });
//    }
//
//    private void showStopResponseDialog() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardChatGptChatActivity.this, R.style.CustomAlertDialog);
//        ViewGroup viewGroup = findViewById(android.R.id.content);
//        View dialogView = LayoutInflater.from(KeyboardChatGptChatActivity.this).inflate(R.layout.aikbd_chat_gpt_dialog, viewGroup, false);
//        Button aikbd_dialog_cancel = dialogView.findViewById(R.id.aikbd_dialog_cancel);
//        Button aikbd_dialog_ok = dialogView.findViewById(R.id.aikbd_dialog_ok);
//        builder.setView(dialogView);
//        final AlertDialog alertDialog = builder.create();
//        aikbd_dialog_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 이용권 차감 후 아래 로직 수행
//                alertDialog.dismiss();
//                KeyboardChatGptChatActivity.super.onBackPressed();
//            }
//        });
//    }
//}
