package com.enliple.keyboard.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.provider.FontRequest;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
//import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.adapter.AikbdClipAdapter;
import com.enliple.keyboard.adapter.AikbdSurpriseAdapter;
import com.enliple.keyboard.adapter.OCBBrandAdapter;
import com.enliple.keyboard.adapter.OCBSaveShoppingAdapter;
import com.enliple.keyboard.adapter.OCBShoppingAdapter;
import com.enliple.keyboard.common.AIKBD_DBHelper;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.Decompress;
import com.enliple.keyboard.common.KeyboardADModel;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.KeyboardUserIdModel;
import com.enliple.keyboard.common.MyInfo;
import com.enliple.keyboard.common.OlabangItem;
import com.enliple.keyboard.common.PointDBHelper;
import com.enliple.keyboard.common.ShoppingCommonModel;
import com.enliple.keyboard.common.ThemeManager;
import com.enliple.keyboard.common.ThemeModel;
import com.enliple.keyboard.common.UserIdDBHelper;
import com.enliple.keyboard.emoji.adapter.EmojiPagerAdapter;
import com.enliple.keyboard.emoji.adapter.EmoticonPagerAdapter;
import com.enliple.keyboard.emoji.adapter.RecentAdapter;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.request.target.CustomTarget;
import com.enliple.keyboard.imgmodule.request.transition.Transition;
import com.enliple.keyboard.managers.PreferenceManager;
import com.enliple.keyboard.mobonAD.MobonBannerType;
import com.enliple.keyboard.mobonAD.MobonBannerView;
import com.enliple.keyboard.mobonAD.MobonSimpleSDK;
import com.enliple.keyboard.mobonAD.MobonUtils;
import com.enliple.keyboard.mobonAD.iSimpleMobonBannerCallback;
import com.enliple.keyboard.models.AdChoices;
import com.enliple.keyboard.models.BoardModel;
import com.enliple.keyboard.models.BrandModel;
import com.enliple.keyboard.models.BrandTabModel;
import com.enliple.keyboard.models.ClipboardModel;
import com.enliple.keyboard.models.CriteoData;
import com.enliple.keyboard.models.JointRewardData;
import com.enliple.keyboard.models.NewsInfo;
import com.enliple.keyboard.models.OfferwallData;
import com.enliple.keyboard.models.RecentEmojiModel;
import com.enliple.keyboard.models.ShoppingData;
import com.enliple.keyboard.models.SurpriseModel;
import com.enliple.keyboard.models.TypingGameInfo;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.receiver.MyReceiver;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.offerwall.OnKeyboardSingleClickListener;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MainKeyboardView extends View implements KeyboardView.OnKeyboardActionListener {
    // This is a test Criteo Publisher ID that works for this sample app
    // In your application, update this value with your own Criteo Publisher ID
    private static final String DEFAULT_OLABANG_DIRECT_LINK = "ocbt://com.skmc.okcashbag.home_google/detail/shopping?url=https%3A%2F%2Fwww.livecm.osara.co.kr%2Fv2%2Fshopping";
    private static final int Publisher_Code = 1349;
    private static final int Media_Code = 32420;
    private static final int Section_Code = 805581;
/*
    private static final String CRITEO_PUBLISHER_ID = "B-000000";
    // Here we use test Criteo Ad Unit IDs in order to return test ads
    private static final String CRITEO_BANNER_AD_UNIT_ID = "30s6zt3ayypfyemwjvmp";*/

    private static final String DEFAULT_BRAND_LINK = "https://coupa.ng/b3kAjA";
    private static final int CONTAINER_CLIPBOARD = 0;
    private static final int CONTAINER_SURPRISE = 1;
    public static final String SPOT_POINT_KEYBOARD = "01";
    public static final String SPOT_POINT_NEWS = "02";
    public static final String SPOT_POINT_BANNER = "03";
    public static final String SPOT_POINT_TIMEDEAL = "04";
    public static final String SPOT_POINT_SEARCH = "05";
    public static final String SPOT_POINT_BRAND = "06";
    public static final String SPOT_POINT_SAVE_SHOPPING = "07";

    private static final String COUPANG_SC = "-1";

    private static final int ID_HOMESHOPPING = 7;
    private static final int ID_DOMESTIC = 1;
    private static final int ID_OVERSEA = 2;
    private static final int ID_TRAVEL = 4;
    private static final int ID_BOOK = 3;

    public static final String POINT_TYPE_USE_KEY = "1";
    public static final String POINT_TYPE_BRAND_AD = "2";
    public static final String POINT_TYPE_BANNER_AD = "3";
    public static final String POINT_TYPE_PROMOTION = "4";
    private static final int SEARCH_MAX_LIMIT = 5;
    private static final int SEARCH_TOTAL = 25;
    public static final int KEYBOARD = 0;
    public static final int TAB_EMOJI = 1;
    public static final int OLABANG = 4;
    public static final int SHOPPING = 5;
    public static final int MY = 6;
    public static final int BRAND_AD = 7;
    public static final int UTIL_COLLECTION = 8;
    public static final String POINT_CHARGE = "POINT_CHARGE";
    public static final String POINT_SAVED = "POINT_SAVED";
    public static final String SET_KEYBOARD = "SET_KEYBOARD";
    public static final String FINISH_WEBVIEW = "FINISH_WEBVIEW";
    public static final String POP_POINT = "POP_POINT";
    public static final String BACK_KEY_LISTENER = "cashpop_back_event";
    public static final int GUBUN_KEYBOARD = 0;
    public static final int GUBUN_EMOJI = 1;
    public static final int GUBUN_UTIL = 2;
    public static final int GUBUN_EMOTICON = 3;
    private static final int SOUND_0 = 0;
    private static final int SOUND_1 = 1;
    private static final int SOUND_2 = 2;
    private static final int SOUND_3 = 3;
    private static final int SOUND_4 = 4;

    private static final int ONE_HOUR = 60 * 60 * 1000;
    private static final int ONE_MIN = 60 * 1000;
    private static final int ONE_SEC = 1000;

    private static final String COUPANG_DY_AD = "1";
    private static final String COUPANG_DY_REWARD_AD = "2";
    private static final String COUPANG_DY_BRAND_AD = "3";

    private int screenWidth = 0;
    public static String mPopPoint = "";

    private AnimationDrawable ani;

    public OnClickCallbackListener mClickCallbackListener = null;
    public OnEClickCallbackListener mEClickCallbackListener = null;
    public BannerAdCallbackListener mBannerAdCallbackListener = null;
    private RelativeLayout mKeyboardLayer = null;
    private RelativeLayout mEmojiLayer = null;
    private RelativeLayout mEmoticonLayer = null;
    private RelativeLayout mUtilLayer = null;
    private LatinKeyboard ekv;
    private LatinKeyboard emoticonkv;
    private ViewPager viewPager;
    private EmojiPagerAdapter emojiPagerAdapter;
    private ViewPager emoticonViewPager;
    private EmoticonPagerAdapter emoticonPagerAdapter;
    private SoftKeyboard mSoftKeyboard;
    private Context mContext;
    private RelativeLayout.LayoutParams mRootParams;
    private RelativeLayout layout;
    private TextView mTopLine;
    private RelativeLayout mContainer;
    private RelativeLayout mADLayer;
    private RelativeLayout mMobonAdLayer;
    private RelativeLayout mMobonAdLayerC;
    private RelativeLayout ad_del;

    private RelativeLayout mSRADLayer;
    private RelativeLayout mKLADLayer;
    private RelativeLayout mEVLayer;
    private RelativeLayout mEVDel;

    private RelativeLayout mKLDel;

    private RelativeLayout rl_unsafe_check;
    private ImageView iv_unsafe_check;
    private TextView tv_unsafe_check;
    private ImageView iv_close;

    private RelativeLayout mSRDel;

    // 리워드 베너
    private RelativeLayout reward_ad_layer;
    private RelativeLayout mMobonRewardAdLayer;
    private RelativeLayout mMobonRewardAdLayerC;
    private View r_leftBg, r_rightBg;
    private RelativeLayout rest_layer;
    private RelativeLayout reward_close;
    private TextView reward_point;

    // 쿠팡 리워드 배너
    private RelativeLayout reward_coupang_layer;
    private ImageView reward_coupang_image;
    private TextView coupang_product_name;
    private View coupang_top_margin;
    private TextView coupang_price;
    private ImageView coupang_rocket;

    // joint 광고
    private RelativeLayout reward_joint_layer;
    private ImageView reward_joint_image;
    private TextView joint_product_name;
    private View joint_top_margin;
    private TextView joint_price;
    private ImageView joint_logo;
    private ImageView joint_adchoice;
    private RelativeLayout reward_joint_banner_layer;
    private ImageView joint_banner_image;
    private ImageView joint_banner_adchoice;

    // 쿠팡 dy 6%
    private RelativeLayout coupang_dy_layer;
    private ImageView dy_coupang_image;
    private TextView dy_coupang_product_name;
    private TextView dy_coupang_price;
    private ImageView dy_coupang_rocket;
    private RelativeLayout dy_coupang_close;

    // 크리테오
    private RelativeLayout criteo_layer;
    private ImageView criteo_image;
    private TextView criteo_product_name;
    private TextView criteo_price;
    private ImageView criteo_adchoice;
    private RelativeLayout criteo_close;
    private CriteoData criteoData;

    // 모비위드 layer
    private RelativeLayout mobwith_layer;
    private RelativeLayout mobwith_container;
    private WebView mobwith_webview;
    private RelativeLayout mobwith_close;
    private TextView mobwith_reward_point;
    private TextView mobwith_non_reward_badge;
    // 모비믹서
    private RelativeLayout mixer_layer;
    private RelativeLayout mixer_container;
    private WebView mixer_webview;
    private RelativeLayout mixer_close;

    private LatinKeyboardView mKeyboardView;
    private LatinKeyboardView mEKeyboardView;
    private LatinKeyboardView mEmoticonKeyboardView;
    private LatinKeyboard kv;
    private int mKeyboardHeight;
    private int mOriginKeyboardHeight;
    private int mTabPosition = 0;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private Vibrator mVibrator = null;
    private int mVolumeLevel = 0;
    private long mVibrateLevel = 0;
    private SoundPool mSoundPool = null;
    private AudioManager mAudioManager;
    private RelativeLayout mFirstTabLayer = null; // 이모지
    private RelativeLayout mTimedealTabLayer = null;// 타임딜
    private RelativeLayout mSecondTabLayer = null;//쿠팡
    private RelativeLayout mShoppingTabLayer = null;//쇼핑
    private RelativeLayout offerwall_tab = null; // offerwall tab layer
    private RelativeLayout mFourthLayer = null;// my
    private RelativeLayout mMoreLayer = null; // 설정
    private RelativeLayout game_tab;
//    private ImageView mImgChat; // chat gpt
    private ImageView mImgFirst; // 이모지
    private ImageView mImgTimedeal; // 타임딜
    private ImageView mImgSecond; // 쿠팡
    private ImageView mImgShopping; // 쇼핑
    private ImageView img_offerwall; // offerwall image
    private ImageView mImgFourth; // my
    private ImageView mImgMore; // 설정
    private TextView mTxtPoint;
    private TextView mMatchedEmoji;
    private boolean mToggleBookmark = false;
    private TextView mBrandBadge;

    private int mLimitMax = 0;
    public boolean mIsFuncPointPossible = false;
    public boolean mIsKeyPointPossible = false;
    private ThemeModel mThemeModel;
    private Drawable mCashDr;
    private Drawable mZeroCashDr;
    private String mPacName;

    private WindowManager mIconWindowManager;
    private RelativeLayout mIconView;

    MobonBannerView bannerView;
    MobonBannerView utilBannerView, utilRewardBannerView;
    MobonBannerView rewardBannerView;
    private View topLine;
    private LinearLayout shoppingTopLayer;
    private ImageView imgShoppingLogo;
    private RelativeLayout closeSearch;
    private ImageView imgShoppingClose;
    private EditText editSearch;
    private RelativeLayout clearText;

    private RelativeLayout offerwall_point_layer;
    private TextView ocb_offerwall_point;
    private RelativeLayout mOCBPointLayer;
    private TextView mOCBPointText;
    private TextView mPlusText;
    private int mOCBPoint = 0;

    private OCBShoppingAdapter adapter;
    private ArrayList<ShoppingCommonModel> shoppingCommonModel;
    private String shoppingTitle = "";
    private NestedScrollView searchScroll;
    private TextView searchNoData;
    private ConstraintLayout sc_shopping_result;
    private Drawable tabEmoji;
    private Drawable tabOlabang;
    private Drawable tabShopping;
    private Drawable tabMy;
    private Drawable tabMore;
    private Drawable tabOCBSearch;
    private Drawable tabEmojiOn;
    private Drawable tabMoreOn;
    private Drawable tabMyOn;
    private Drawable tabOlabangOn;
    private Drawable tabOCBSearchOn;
    private Drawable tabShoppingOn;
//    private Drawable tabChatGpt;
    private Drawable adDel;
    private Drawable drBrand;

    private String brandLink = DEFAULT_BRAND_LINK;
    private ViewPager brandPager;
    private OCBBrandAdapter brandAdapter;
    private OlabangItem olabangItem = null;

    private RelativeLayout banner_ad_layer;
    private ImageView bannerClose;
    private ImageView bannerImg;
    private TextView bannerTxt;

    private RelativeLayout coupang_ad_layer;
    private ImageView coupangAdImage;
    private TextView coupangAdTitle;

    private boolean isInAppKeyboard = false;
    private String cardNum;
    private RakeAPI rake = null;

    private ConstraintLayout category_layer;
    private View utilArrow;
    private RelativeLayout clip_root;
    private TextView strCollection;
    private RelativeLayout collection_second_tab;

    private RelativeLayout news_ad_layer;
    private TextView newsTxt;
    private ImageView dummyImage; // news의 talkback이 동작하는 것을 막기 위해

    public NewsInfo newsInfo;
    public TypingGameInfo typingGameInfo = new TypingGameInfo();

    private ClipboardManager clipboard;
    private String currentPageId = "";

    final int PERMISSION = 1;
    SpeechRecognizer mRecognizer;
    Intent rIntent;

    private boolean isBrandHasReward = false;

    private RelativeLayout util_ad_layer;

    // 유틸 모비위드 layer
    private WebView util_mobwith_webview;
    private RelativeLayout util_mobwith_layer; // 모비위드 전체 container
    private RelativeLayout util_mobwith_container; // 실제 webview가 붙을 layer
    private TextView util_mobwith_reward_point, util_mobwith_non_reward_badge;
    private RelativeLayout util_mobwith_close;
    // 유틸 리워드 베너
    private RelativeLayout util_mobon_reward_ad_layer_c; // util reward 전체 layout
    private RelativeLayout util_reward_ad_layer; // coupang, mobon 둘 광고 영역의 container
    private RelativeLayout util_mobon_reward_ad_layer; // mobon reward add 할 container
    private View util_r_leftBg, util_r_rightBg;
    private RelativeLayout util_rest_layer;
    private RelativeLayout util_reward_close;
    private TextView util_reward_point;

    // 유틸 joint 광고
    private RelativeLayout util_reward_joint_layer;
    private ImageView util_reward_joint_image;
    private TextView util_joint_product_name;
    private View util_joint_top_margin;
    private TextView util_joint_price;
    private ImageView util_joint_logo;
    private ImageView util_joint_adchoice;
    private RelativeLayout util_reward_joint_banner_layer;
    private ImageView util_joint_banner_image;
    private ImageView util_joint_banner_adchoice;

    // 유틸 쿠팡 리워드 배너
    private RelativeLayout util_reward_coupang_layer; // util의 쿠팡 dy container
    private ImageView util_reward_coupang_image;
    private TextView util_coupang_product_name;
    private TextView util_coupang_price;
    private ImageView util_coupang_rocket;
    private View util_coupang_top_margin;
    private String userPoint;
    private String spotPoint;
    private String todayPoint;
    private MyInfo myInfo = new MyInfo();

    private int orgImgOption = 0;

    private TextView tab_homeshopping, tab_domestic, tab_oversea, tab_travel, tab_book;
    private int selectedShoppingTab = ID_HOMESHOPPING;
    private OCBSaveShoppingAdapter saveShoppingAdapter;
    private RecyclerView shopping_recyclerview;

    private int moreAdCloseCount = 0; // more tab읭 광고 x 버튼 클릭 카운트를 올린다. x버튼 2회 클릭 후부터는 광고 호출을 그날 하루동안은 하지 않는다.

    private boolean isRewardPossible = true; // 현재 노출될 리워드 광고가 포인트 지급 가능한지 여부
    private int utilRewardCount = 0; // util의 리워드 광고 노출 여부
    private boolean isKeyboardPointClickPossible = true;
    private boolean isRewardPointClickPossible = true;
    private boolean isSetChangeRegistered = false;
    private boolean isMixerHasError = false;
    private boolean isMobWithHasError = false;
    private boolean isUtilMobWithHasError = false;
    private boolean isJointReward = true;
    private OfferwallData selectedOfferwallData = null;

    private int offerwall_badge_count = 1;

    private boolean isHybridOfferwall = true;

    private boolean isMobwithClicked = false;

    private Handler emojiPageHandler = new Handler(Looper.getMainLooper());
    private boolean isChatGptResummed = false;
    private void setChangedKeyboardHeight() {
        if ( ekv != null && mEKeyboardView != null ) {
            setKeyboardHeight(ekv, mEKeyboardView);
        }
        if ( emoticonkv != null && mEmoticonKeyboardView != null ) {
            setKeyboardHeight(emoticonkv, mEmoticonKeyboardView);
        }
        if ( kv != null && mKeyboardView != null ) {
            setKeyboardHeight(kv, mKeyboardView);
        }
    }

    private void setKeyboardHeight(LatinKeyboard keyboard, LatinKeyboardView keyboardView) {
        int keyHeightLevel = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL);
        int level = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL);

        try {
//            float fLevel = Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * keyHeightLevel) / 100));
            float fLevel = Common.GetHeightValue(keyHeightLevel);
            adjustKeyboardKeyHeight(keyboard, fLevel);
            keyboardView.setKeys();
            keyboardView.changeConfig(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeKeyboardHeight(int keyboardHeight, LatinKeyboard eKeyboard, LatinKeyboard emoKeyboard) {
        KeyboardLogPrint.e("keyboardHeight changeKeyboardHeight keyboardHeight : " + keyboardHeight);
        ekv = eKeyboard;
        if ( mEKeyboardView != null ) {
            mEKeyboardView.setKeyboard(ekv);
            mEKeyboardView.setKeys();
            mEKeyboardView.setOnKeyboardActionListener(this);
            mEKeyboardView.setPreviewEnabled(false);
            viewPager.setPadding(0, 0, 0, ekv.getHeight());
        }


        LogPrint.d("khskkim change height keyboard from pref height :: " + keyboardHeight);
        if ( mEmoticonKeyboardView != null ) {
            emoticonkv = emoKeyboard;
            mEmoticonKeyboardView.setKeyboard(emoticonkv);
            mEmoticonKeyboardView.setKeys();
            mEmoticonKeyboardView.setOnKeyboardActionListener(this);
            mEmoticonKeyboardView.setPreviewEnabled(false);
            emoticonViewPager.setPadding(0, 0, 0, emoticonkv.getHeight());
        }

        mOriginKeyboardHeight = keyboardHeight + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_line) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_padding);
        mKeyboardHeight = keyboardHeight + getResources().getDimensionPixelSize(R.dimen.aikbd_ad_height) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_line) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_padding) + getResources().getDimensionPixelSize(R.dimen.aikbd_top_height);
        KeyboardLogPrint.e("keyboardHeight changeKeyboardHeight mOriginKeyboardHeight 키보드 높이 + 윗쪽 라인 1dp 및 padding 3dp 포함 : " + mOriginKeyboardHeight);
        KeyboardLogPrint.e("keyboardHeight changeKeyboardHeight mKeyboardHeight 키보드 높이 + 광고 높이, 상단 tab 높이, 윗쪽 라인 1dp 및 padding 3dp 포함 : " + mKeyboardHeight);
        mRootParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mKeyboardHeight);
        mContainer.setLayoutParams(mRootParams);
        KeyboardLogPrint.e("keyboardHeight 키보드 + 상단 tab + 광고 영역으로 mOriginKeyboardHeight를 height를 설정");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mOriginKeyboardHeight);
        mEmojiLayer.setLayoutParams(params);
        mEmoticonLayer.setLayoutParams(params);
        mKeyboardLayer.setLayoutParams(params);
        mUtilLayer.setLayoutParams(params);
        KeyboardLogPrint.e("keyboardHeight 이모지, 이모티콘, 자판, 유틸키보드 영역높이를 mKeyboardHeight를 height를 설정");
    }

    public MainKeyboardView(Context context, Application application, LatinKeyboard lkv, LatinKeyboard elkv, LatinKeyboard emoticonlkv, int keyboardHeight, OnClickCallbackListener listener, OnEClickCallbackListener elistener, BannerAdCallbackListener bannerListener) {
        super(context);

        //rake = RakeAPI.getInstance(getContext(), Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.ENABLE);
        if (CustomAsyncTask.GUBUN_RELEASE.equals(CustomAsyncTask.gubun))
            rake = RakeAPI.getInstance(context, Common.LIVE_TOKEN, RakeAPI.Env.LIVE, RakeAPI.Logging.DISABLE);
        else
            rake = RakeAPI.getInstance(context, Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.DISABLE);
        mContext = context;
        mClickCallbackListener = listener;
        mEClickCallbackListener = elistener;
        mBannerAdCallbackListener = bannerListener;
        kv = lkv;
        ekv = elkv;
        emoticonkv = emoticonlkv;
        mSoftKeyboard = (SoftKeyboard) context;
        LogPrint.d("keyboard_height 1 :: " + keyboardHeight);
        mOriginKeyboardHeight = keyboardHeight + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_line) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_padding);
        mKeyboardHeight = keyboardHeight + getResources().getDimensionPixelSize(R.dimen.aikbd_ad_height) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_line) + getResources().getDimensionPixelSize(R.dimen.aikbd_keyboard_top_padding) + getResources().getDimensionPixelSize(R.dimen.aikbd_top_height);
        mSoftKeyboard.setSearchListener(new SoftKeyboard.SearchListener() {
            @Override
            public void onSearch() {
                if (editSearch != null) {
                    if (editSearch.hasFocus()) {
                        if (editSearch.getText().toString().length() > 0) {
                            setRake("/keyboard/search", "tap.searchbtn");
                            addView(3);
                            sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_SEARCH);
                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    task.getSearchList(editSearch.getText().toString(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                        @Override
                                        public void onResponse(boolean result, Object obj) {
                                            if (result) {
                                                try {
                                                    JSONObject object = (JSONObject) obj;
                                                    if (object != null) {
                                                        shoppingTitle = "";
                                                        int total = 0;
                                                        shoppingCommonModel = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> recomArr = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> ohsaraArr = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> trandArr = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> hotArr = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> homeArr = new ArrayList<>();
                                                        JSONObject recommendObj = object.optJSONObject("recommend");
                                                        if (recommendObj != null) {
                                                            int totalCount = recommendObj.optInt("totalCount");
                                                            total = totalCount;
                                                            JSONArray reArray = recommendObj.optJSONArray("messages");
                                                            if (reArray != null && reArray.length() > 0) {
                                                                for (int i = 0; i < reArray.length(); i++) {
                                                                    JSONObject reObj = reArray.optJSONObject(i);
                                                                    if (reObj != null) {
                                                                        String indexSeq = reObj.optString("indexSeq");
                                                                        String category = reObj.optString("category");
                                                                        JSONObject inReObj = reObj.optJSONObject("recommend");
                                                                        if (inReObj != null) {
                                                                            String type = inReObj.getString("type");
                                                                            // 20210429 오라방 제거
                                                                            if (type.equals("one_item_time") || type.equals("exhibition") || type.equals("one_item")) {
                                                                                ShoppingCommonModel model = new ShoppingCommonModel();
                                                                                model.setMainType("recommend_" + type);
                                                                                model.setStartDate(inReObj.optLong("startDate"));
                                                                                model.setEndDate(inReObj.optLong("endDate"));
                                                                                model.setExpired(inReObj.optBoolean("isExpired"));
                                                                                model.setIndexSeq(indexSeq);
                                                                                model.setCategory(category);
                                                                                model.setId(inReObj.optString("recommendId"));
                                                                                model.setType(inReObj.optString("type"));
                                                                                model.setTitle(inReObj.optString("title"));
                                                                                model.setImageUrl(inReObj.optString("imageUrl"));
                                                                                model.setPrice(inReObj.optDouble("price"));
                                                                                model.setTag(inReObj.optString("tag"));
                                                                                model.setLinkUrl(inReObj.optString("linkUrl"));
                                                                                model.setOriginalPrice(inReObj.optDouble("originalPrice"));
                                                                                model.setTotalCount(totalCount);
                                                                                recomArr.add(model);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                shoppingTitle = shoppingTitle + "추천";
                                                            }
                                                        }
                                                        JSONObject ohsaraObj = object.optJSONObject("ohsaraMarket");
                                                        if (ohsaraObj != null) {
                                                            int totalCount = ohsaraObj.optInt("totalCount");
                                                            total = total + totalCount;
                                                            JSONArray ohArray = ohsaraObj.optJSONArray("messages");
                                                            if (ohArray != null && ohArray.length() > 0) {
                                                                for (int i = 0; i < ohArray.length(); i++) {
                                                                    JSONObject ohObj = ohArray.optJSONObject(i);
                                                                    if (ohObj != null) {
                                                                        String indexSeq = ohObj.optString("indexSeq");
                                                                        String category = ohObj.optString("category");
                                                                        JSONObject inOhObj = ohObj.optJSONObject("ohsaraMarket");
                                                                        if (inOhObj != null) {
                                                                            ShoppingCommonModel model = new ShoppingCommonModel();
                                                                            model.setMainType("ohsaraMarket");
                                                                            model.setIndexSeq(indexSeq);
                                                                            model.setCategory(category);
                                                                            model.setId(inOhObj.optString("id"));
                                                                            model.setTitle(inOhObj.optString("productName"));
                                                                            model.setImageUrl(inOhObj.optString("imageUrl"));
                                                                            model.setPrice(inOhObj.optDouble("price"));
                                                                            model.setOriginalPrice(inOhObj.optDouble("originalPrice"));
                                                                            model.setDiscountText(inOhObj.optString("discountText"));
                                                                            model.setSaveText(inOhObj.optString("saveText"));
                                                                            model.setLinkUrl(inOhObj.optString("linkUrl"));
                                                                            model.setTotalCount(totalCount);
                                                                            ohsaraArr.add(model);
                                                                        }
                                                                    }
                                                                }
                                                                String str = "오사라마켓";
                                                                if (shoppingTitle.length() > 0) {
                                                                    str = "/오사라마켓";
                                                                }
                                                                shoppingTitle = shoppingTitle + str;
                                                            }
                                                        }
                                                        JSONObject trandIssueObj = object.optJSONObject("trendIssue");
                                                        if (trandIssueObj != null) {
                                                            int totalCount = trandIssueObj.optInt("totalCount");
                                                            total = total + totalCount;
                                                            JSONArray trandArray = trandIssueObj.optJSONArray("messages");
                                                            if (trandArray != null && trandArray.length() > 0) {
                                                                for (int i = 0; i < trandArray.length(); i++) {
                                                                    JSONObject trandObj = trandArray.optJSONObject(i);
                                                                    if (trandObj != null) {
                                                                        String indexSeq = trandObj.optString("indexSeq");
                                                                        String category = trandObj.optString("category");
                                                                        JSONObject inTrandObj = trandObj.optJSONObject("trendIssue");
                                                                        if (inTrandObj != null) {
                                                                            ShoppingCommonModel model = new ShoppingCommonModel();
                                                                            model.setMainType("trendIssue");
                                                                            model.setIndexSeq(indexSeq);
                                                                            model.setCategory(category);
                                                                            model.setId(inTrandObj.optString("id"));
                                                                            model.setTitle(inTrandObj.optString("title"));
                                                                            model.setImageUrl(inTrandObj.optString("imageUrl"));
                                                                            model.setShopName(inTrandObj.optString("shopName"));
                                                                            model.setPrice(inTrandObj.optDouble("price"));
                                                                            model.setLinkUrl(inTrandObj.optString("linkUrl"));
                                                                            model.setTotalCount(totalCount);
                                                                            trandArr.add(model);
                                                                        }
                                                                    }
                                                                }
                                                                String str = "트랜드";
                                                                if (shoppingTitle.length() > 0) {
                                                                    str = "/트랜드";
                                                                }
                                                                shoppingTitle = shoppingTitle + str;
                                                            }
                                                        }


                                                        JSONObject hotProductObj = object.optJSONObject("hotProduct");
                                                        if (hotProductObj != null) {
                                                            int totalCount = hotProductObj.optInt("totalCount");
                                                            total = total + totalCount;
                                                            JSONArray hotArray = hotProductObj.optJSONArray("messages");
                                                            if (hotArray != null && hotArray.length() > 0) {
                                                                for (int i = 0; i < hotArray.length(); i++) {
                                                                    JSONObject hotObj = hotArray.optJSONObject(i);
                                                                    if (hotObj != null) {
                                                                        String indexSeq = hotObj.optString("indexSeq");
                                                                        String category = hotObj.optString("category");
                                                                        JSONObject inHotObj = hotObj.optJSONObject("hotProduct");
                                                                        if (inHotObj != null) {
                                                                            ShoppingCommonModel model = new ShoppingCommonModel();
                                                                            model.setMainType("hotProduct");
                                                                            model.setIndexSeq(indexSeq);
                                                                            model.setCategory(category);
                                                                            model.setId(inHotObj.optString("id"));
                                                                            model.setTitle(inHotObj.optString("title"));
                                                                            model.setImageUrl(inHotObj.optString("imageUrl"));
                                                                            model.setPrice(inHotObj.optDouble("price"));
                                                                            model.setOriginalPrice(inHotObj.optDouble("originalPrice"));
                                                                            model.setDiscountText(inHotObj.optString("discountText"));
                                                                            model.setSaveText(inHotObj.optString("saveText"));
                                                                            model.setLinkUrl(inHotObj.optString("linkUrl"));
                                                                            model.setTotalCount(totalCount);
                                                                            hotArr.add(model);
                                                                        }
                                                                    }
                                                                }
                                                                String str = "상품";
                                                                if (shoppingTitle.length() > 0) {
                                                                    str = "/상품";
                                                                }
                                                                shoppingTitle = shoppingTitle + str;
                                                            }
                                                        }

                                                        JSONObject homeshoppingObj = object.optJSONObject("homeShopping");
                                                        if (homeshoppingObj != null) {
                                                            int totalCount = homeshoppingObj.optInt("totalCount");
                                                            total = total + totalCount;
                                                            JSONArray homeArray = homeshoppingObj.optJSONArray("messages");
                                                            if (homeArray != null && homeArray.length() > 0) {
                                                                for (int i = 0; i < homeArray.length(); i++) {
                                                                    JSONObject homeObj = homeArray.optJSONObject(i);
                                                                    if (homeObj != null) {
                                                                        String indexSeq = homeObj.optString("indexSeq");
                                                                        String category = homeObj.optString("category");
                                                                        JSONObject inHomeObj = homeObj.optJSONObject("homeShopping");
                                                                        if (inHomeObj != null) {
                                                                            ShoppingCommonModel model = new ShoppingCommonModel();
                                                                            model.setMainType("homeShopping");
                                                                            model.setIndexSeq(indexSeq);
                                                                            model.setCategory(category);
                                                                            model.setId(inHomeObj.optString("id"));
                                                                            model.setTitle(inHomeObj.optString("title"));
                                                                            model.setImageUrl(inHomeObj.optString("imageUrl"));
                                                                            model.setPrice(inHomeObj.optDouble("price"));
                                                                            model.setSaveText(inHomeObj.optString("saveRateText"));
                                                                            model.setLinkUrl(inHomeObj.optString("linkUrl"));

                                                                            model.setTotalCount(totalCount);
                                                                            homeArr.add(model);
                                                                        }
                                                                    }
                                                                }
                                                                String str = "상품";
                                                                if (shoppingTitle.length() > 0) {
                                                                    str = "/상품";
                                                                }
                                                                shoppingTitle = shoppingTitle + str;
                                                            }
                                                        }

                                                        // 기획 변경으로 이걸로 대체
                                                        shoppingTitle = "쇼핑 검색 결과";

                                                        int recomSize = recomArr.size();
                                                        int ohsaraSize = ohsaraArr.size();
                                                        int trandSize = trandArr.size();
                                                        int hotSize = hotArr.size();
                                                        int homeSize = homeArr.size();
                                                        ArrayList<ShoppingCommonModel> t_recomArr = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> t_ohsaraArr = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> t_trandArr = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> t_hotArr = new ArrayList<>();
                                                        ArrayList<ShoppingCommonModel> t_homeArr = new ArrayList<>();

                                                        if ((recomSize + ohsaraSize + trandSize + hotSize + homeSize) > SEARCH_TOTAL) {
                                                            /**
                                                             * 노출정책
                                                             * 추천, 오사라마켓, 트랜드, 핫은 각각 최대 5개씩 노출
                                                             * 만약 부족한 것이 있다면 다른 카테고리에서 추가
                                                             * EX : 추천4개, 오사라 10개, 트랜드 3개, 핫 4개 일 경우 추천4개, 오사라 9개, 트랜드 3개, 핫4개 노출
                                                             * EX : 추천 4개 오사라 5개 트랜드 6개, 핫 9개 일 경우 추천 4개, 오사라 5개, 트랜드 6개, 핫 5개 노출
                                                             */
                                                            ArrayList<ShoppingCommonModel> rArr = new ArrayList<>();
                                                            ArrayList<ShoppingCommonModel> oArr = new ArrayList<>();
                                                            ArrayList<ShoppingCommonModel> tArr = new ArrayList<>();
                                                            ArrayList<ShoppingCommonModel> hArr = new ArrayList<>();
                                                            ArrayList<ShoppingCommonModel> hoArr = new ArrayList<>();
                                                            int totalLess = 0;
                                                            // 추천이 5개보다 적으면 우선 t_recomArr에 추천을 다 담아둠.
                                                            if (recomSize < SEARCH_MAX_LIMIT) {
                                                                int less = SEARCH_MAX_LIMIT - recomSize;
                                                                totalLess = totalLess + less;
                                                                for (int i = 0; i < recomSize; i++) {
                                                                    t_recomArr.add(recomArr.get(i));
                                                                }
                                                            } else { // 추천이 5개보다 많으면 기본 노출 5개 외에 나머지 항목들을 rArr에 담아둠. 그래고 t_recomArr에는 처음 5개 담아둠
                                                                for (int i = SEARCH_MAX_LIMIT; i < recomSize; i++) {
                                                                    rArr.add(recomArr.get(i));
                                                                }

                                                                for (int i = 0; i < SEARCH_MAX_LIMIT; i++) {
                                                                    t_recomArr.add(recomArr.get(i));
                                                                }
                                                            }

                                                            if (ohsaraSize < SEARCH_MAX_LIMIT) {
                                                                int less = SEARCH_MAX_LIMIT - ohsaraSize;
                                                                totalLess = totalLess + less;
                                                                for (int i = 0; i < ohsaraSize; i++) {
                                                                    t_ohsaraArr.add(ohsaraArr.get(i));
                                                                }
                                                            } else {
                                                                for (int i = SEARCH_MAX_LIMIT; i < ohsaraSize; i++) {
                                                                    oArr.add(ohsaraArr.get(i));
                                                                }
                                                                for (int i = 0; i < SEARCH_MAX_LIMIT; i++) {
                                                                    t_ohsaraArr.add(ohsaraArr.get(i));
                                                                }
                                                            }

                                                            if (trandSize < SEARCH_MAX_LIMIT) {
                                                                int less = SEARCH_MAX_LIMIT - trandSize;
                                                                totalLess = totalLess + less;
                                                                for (int i = 0; i < trandSize; i++) {
                                                                    t_trandArr.add(trandArr.get(i));
                                                                }
                                                            } else {
                                                                for (int i = SEARCH_MAX_LIMIT; i < trandSize; i++) {
                                                                    tArr.add(trandArr.get(i));
                                                                }
                                                                for (int i = 0; i < SEARCH_MAX_LIMIT; i++) {
                                                                    t_trandArr.add(trandArr.get(i));
                                                                }
                                                            }

                                                            if (hotSize < SEARCH_MAX_LIMIT) {
                                                                int less = SEARCH_MAX_LIMIT - hotSize;
                                                                totalLess = totalLess + less;
                                                                for (int i = 0; i < hotSize; i++) {
                                                                    t_hotArr.add(hotArr.get(i));
                                                                }
                                                            } else {
                                                                for (int i = SEARCH_MAX_LIMIT; i < hotSize; i++) {
                                                                    hArr.add(hotArr.get(i));
                                                                }
                                                                for (int i = 0; i < SEARCH_MAX_LIMIT; i++) {
                                                                    t_hotArr.add(hotArr.get(i));
                                                                }
                                                            }

                                                            if (homeSize < SEARCH_MAX_LIMIT) {
                                                                int less = SEARCH_MAX_LIMIT - homeSize;
                                                                totalLess = totalLess + less;
                                                                for (int i = 0; i < homeSize; i++) {
                                                                    t_homeArr.add(homeArr.get(i));
                                                                }
                                                            } else {
                                                                for (int i = SEARCH_MAX_LIMIT; i < homeSize; i++) {
                                                                    hoArr.add(homeArr.get(i));
                                                                }
                                                                for (int i = 0; i < SEARCH_MAX_LIMIT; i++) {
                                                                    t_homeArr.add(homeArr.get(i));
                                                                }
                                                            }

                                                            // tArr에는5개보다 많든 적든 5개 이내 값들이 각각 저장되어 있고 t arr에는 남는 것들이 저장되어 있음.

                                                            //  총 부족분이 있을 경우
                                                            if (totalLess > 0) {
                                                                if (recomSize > SEARCH_MAX_LIMIT) {
                                                                    if (totalLess > recomSize - SEARCH_MAX_LIMIT) {
                                                                        for (int i = 0; i < rArr.size(); i++) {
                                                                            t_recomArr.add(rArr.get(i));
                                                                        }
                                                                        totalLess = totalLess - rArr.size();
                                                                    } else {
                                                                        for (int i = 0; i < totalLess; i++) {
                                                                            t_recomArr.add(rArr.get(i));
                                                                        }
                                                                        totalLess = totalLess - totalLess;
                                                                    }
                                                                }
                                                            }

                                                            if (totalLess > 0) {
                                                                if (ohsaraSize > SEARCH_MAX_LIMIT) {
                                                                    if (totalLess > ohsaraSize - SEARCH_MAX_LIMIT) {
                                                                        for (int i = 0; i < oArr.size(); i++) {
                                                                            t_ohsaraArr.add(oArr.get(i));
                                                                        }
                                                                        totalLess = totalLess - oArr.size();
                                                                    } else {
                                                                        for (int i = 0; i < totalLess; i++) {
                                                                            t_ohsaraArr.add(oArr.get(i));
                                                                        }
                                                                        totalLess = totalLess - totalLess;
                                                                    }
                                                                }
                                                            }

                                                            if (totalLess > 0) {
                                                                if (trandSize > SEARCH_MAX_LIMIT) {
                                                                    if (totalLess > trandSize - SEARCH_MAX_LIMIT) {
                                                                        for (int i = 0; i < tArr.size(); i++) {
                                                                            t_trandArr.add(tArr.get(i));
                                                                        }
                                                                        totalLess = totalLess - tArr.size();
                                                                    } else {
                                                                        for (int i = 0; i < totalLess; i++) {
                                                                            t_trandArr.add(tArr.get(i));
                                                                        }
                                                                        totalLess = totalLess - totalLess;
                                                                    }
                                                                }
                                                            }

                                                            if (totalLess > 0) {
                                                                if (hotSize > SEARCH_MAX_LIMIT) {
                                                                    if (totalLess > hotSize - SEARCH_MAX_LIMIT) {
                                                                        for (int i = 0; i < hArr.size(); i++) {
                                                                            t_hotArr.add(hArr.get(i));
                                                                        }
                                                                    } else {
                                                                        for (int i = 0; i < totalLess; i++) {
                                                                            t_hotArr.add(hArr.get(i));
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            if (totalLess > 0) {
                                                                if (homeSize > SEARCH_MAX_LIMIT) {
                                                                    if (totalLess > homeSize - SEARCH_MAX_LIMIT) {
                                                                        for (int i = 0; i < hoArr.size(); i++) {
                                                                            t_homeArr.add(hoArr.get(i));
                                                                        }
                                                                    } else {
                                                                        for (int i = 0; i < totalLess; i++) {
                                                                            t_homeArr.add(hoArr.get(i));
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            shoppingCommonModel.addAll(t_recomArr);
                                                            shoppingCommonModel.addAll(t_ohsaraArr);
                                                            shoppingCommonModel.addAll(t_trandArr);
                                                            shoppingCommonModel.addAll(t_hotArr);
                                                            shoppingCommonModel.addAll(t_homeArr);
                                                        } else {
                                                            shoppingCommonModel.addAll(recomArr);
                                                            shoppingCommonModel.addAll(ohsaraArr);
                                                            shoppingCommonModel.addAll(trandArr);
                                                            shoppingCommonModel.addAll(hotArr);
                                                            shoppingCommonModel.addAll(homeArr);
                                                        }

                                                        if (shoppingCommonModel != null && shoppingCommonModel.size() > 0) {
                                                            sc_shopping_result.setVisibility(View.GONE);
                                                            searchScroll.setVisibility(View.VISIBLE);
                                                            searchNoData.setVisibility(View.GONE);
                                                            adapter.setItems(shoppingCommonModel, total, shoppingTitle, editSearch.getText().toString());
                                                            setRake("/keyboard/search/results", "");
                                                        } else {
                                                            searchScroll.setVisibility(View.GONE);
                                                            searchNoData.setVisibility(View.VISIBLE);
                                                        }
                                                    } else {
                                                        searchScroll.setVisibility(View.GONE);
                                                        searchNoData.setVisibility(View.VISIBLE);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    searchScroll.setVisibility(View.GONE);
                                                    searchNoData.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                searchScroll.setVisibility(View.GONE);
                                                searchNoData.setVisibility(View.VISIBLE);
                                                try {
                                                    JSONObject object = (JSONObject) obj;
                                                    if (object != null) {
                                                        String error = object.optString(Common.NETWORK_ERROR);
                                                        String dError = object.optString(Common.NETWORK_DISCONNECT);
                                                        if (!TextUtils.isEmpty(error)) {
                                                            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            if (!TextUtils.isEmpty(dError)) {
                                                                Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
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
                            });

                        } else {
                            if (shoppingTopLayer != null && shoppingTopLayer.getVisibility() == View.VISIBLE)
                                Toast.makeText(mContext, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        LogPrint.d("main init 1");
        initialize(context);
    }

    public MainKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LogPrint.d("main init 2");
        initialize(context);
    }

    public MainKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LogPrint.d("main init 3");
        initialize(context);
    }

    /**
     * ocb 메모기능 삭제로 제외
     * public void memoVisibleSetting(boolean hasText) {
     * if (mImgThird != null && mImgThird1 != null) {
     * if (hasText) {
     * mImgThird.setVisibility(View.GONE);
     * mImgThird1.setVisibility(View.VISIBLE);
     * } else {
     * mImgThird.setVisibility(View.VISIBLE);
     * mImgThird1.setVisibility(View.GONE);
     * }
     * }
     * }
     **/

    public void setPackageName(String packageName) {
        mPacName = packageName;
    }

    public String getPackageName() {
        return mPacName;
    }

    private boolean isKeyboardApp() {
        if (!TextUtils.isEmpty(mPacName)) {
            if ("com.enliple.keyboard.ui.ckeyboard".equals(mPacName)) {
                return true;
            }
        }
        return false;
    }

    public int getKeyboardHeight() {
       return mKeyboardHeight;
    }

    private boolean isDefaultBrowserExist() {
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo != null) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (packageName == null)
                return false;
            else {
                if ("android".equals(packageName.toLowerCase()))
                    return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static Intent getDefaultBrowserIntent(Context context, String url) {
        PackageManager pm = context.getPackageManager();
        String[] browserPackages = {"com.android.browser", "com.sec.android.app.sbrowser", "com.android.chrome"};

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        for (String pkg : browserPackages) {
            intent.setPackage(pkg);
            if (intent.resolveActivity(pm) != null) {
                return intent;
            }
        }
        return intent.setPackage(null);
    }

    ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        private static final long THRESHOLD_MS = 100;
        private long lastChangedTime = 0;
        private String lastString = "";
        public void onPrimaryClipChanged() {
            try {
                if (clipboard != null && clipboard.getText() != null) {
                    String a = clipboard.getText().toString();
                    LogPrint.d("clip change :: " + a);
                    if (!TextUtils.isEmpty(a)) {
                        if (System.currentTimeMillis() - lastChangedTime < THRESHOLD_MS && Objects.equals(lastString, a)) {
                            return;
                        }
                        clipboard.removePrimaryClipChangedListener(mPrimaryClipChangedListener);
                        AIKBD_DBHelper dbHelper = new AIKBD_DBHelper(mContext);
                        dbHelper.deleteClipboard(a, new AIKBD_DBHelper.Listener() {
                            @Override
                            public void onDeleted() {
                                LogPrint.d("clip change insert");
                                lastString = a;
                                dbHelper.insertClipboard(a);
                                clipboard.addPrimaryClipChangedListener(mPrimaryClipChangedListener);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void initialize(Context context) {
        LogPrint.d("MainKeyboardView initialize");
        clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(mPrimaryClipChangedListener);

//        EmojiCompat.Config config = new BundledEmojiCompatConfig(context.getApplicationContext());
//        EmojiCompat.init(config);

        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(mContext, fontRequest);
        EmojiCompat.init(config);

        // 20231121 initilize시 시간 오래걸리는 현상으로 제거. 왜 달아놨는지 모르겠는데 추가 테스트 진행해봐야할듯.
//        try {
//            Thread.sleep(1500);
//        }catch (Exception e) {}

        if (mAudioManager == null)
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_main_keyboard, null);

        ViewCompat.setOnApplyWindowInsetsListener(layout, (v, insets) -> {
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            LogPrint.d("keyboard navigationBarHeight : " + navigationBarHeight);
            // 네비게이션 바 높이만큼 패딩 추가
//            v.setPadding(0, 0, 0, navigationBarHeight);
            navigationBarHeight = navigationBarHeight * 2;
            LogPrint.d("CustomKeyboardView navigationBarHeight: " + navigationBarHeight);
            v.setPadding(0, 0, 0, navigationBarHeight);
            // 네비게이션 바가 가려지는 문제 해결 (패딩 대신 Margin 조정)
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//            params.bottomMargin = navigationBarHeight;
//            v.setLayoutParams(params);

            return insets;
        });

        topLine = layout.findViewById(R.id.topLine);
        shoppingTopLayer = layout.findViewById(R.id.shopping_top_layer);

        shoppingTopLayer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        mContainer = (RelativeLayout) layout.findViewById(R.id.container);
        mKeyboardLayer = (RelativeLayout) layout.findViewById(R.id.only_keyboard_layer);
        mEmojiLayer = (RelativeLayout) layout.findViewById(R.id.e_keyboard_layer);
        mEmoticonLayer = (RelativeLayout) layout.findViewById(R.id.emoticon_keyboard_layer);
        mUtilLayer = (RelativeLayout) layout.findViewById(R.id.util_layer);
        mTopLine = (TextView) layout.findViewById(R.id.top_line);

        mFirstTabLayer = (RelativeLayout) layout.findViewById(R.id.first_tab);
        mTimedealTabLayer = (RelativeLayout) layout.findViewById(R.id.timedeal_tab);
        mSecondTabLayer = (RelativeLayout) layout.findViewById(R.id.second_tab);
        mShoppingTabLayer = (RelativeLayout) layout.findViewById(R.id.shopping_tab);
        offerwall_tab = (RelativeLayout) layout.findViewById(R.id.offerwall_tab);
        mFourthLayer = (RelativeLayout) layout.findViewById(R.id.fourth_tab);
        mMoreLayer = layout.findViewById(R.id.more_tab);
        game_tab = layout.findViewById(R.id.game_tab);

        mImgFirst = (ImageView) layout.findViewById(R.id.img_first);
        mImgTimedeal = (ImageView) layout.findViewById(R.id.img_timedeal);
        mImgSecond = (ImageView) layout.findViewById(R.id.img_second);
        mBrandBadge = layout.findViewById(R.id.brand_badge);
        mImgShopping = (ImageView) layout.findViewById(R.id.img_shopping);
        img_offerwall = (ImageView) layout.findViewById(R.id.img_offerwall);
        mImgFourth = (ImageView) layout.findViewById(R.id.img_fourth);
        mImgMore = layout.findViewById(R.id.img_more);
        mTxtPoint = (TextView) layout.findViewById(R.id.aikbd_txt_point);
        mMatchedEmoji = (TextView) layout.findViewById(R.id.matched_emoji);
        closeSearch = layout.findViewById(R.id.closeSearch);
        banner_ad_layer = layout.findViewById(R.id.banner_ad_layer);
        bannerClose = layout.findViewById(R.id.bannerClose);
        bannerImg = layout.findViewById(R.id.bannerImg);
        bannerTxt = layout.findViewById(R.id.bannerTxt);

        mShoppingTabLayer.setVisibility(View.GONE);
        offerwall_tab.setVisibility(View.VISIBLE);

        coupang_ad_layer = layout.findViewById(R.id.coupang_ad_layer);
        coupangAdImage = (ImageView) coupang_ad_layer.findViewById(R.id.adImage);
        coupangAdTitle = (TextView) coupang_ad_layer.findViewById(R.id.adTitle);

        imgShoppingClose = layout.findViewById(R.id.img_shopping_close);

        news_ad_layer = layout.findViewById(R.id.news_ad_layer);
        newsTxt = layout.findViewById(R.id.newsTxt);
        dummyImage = layout.findViewById(R.id.dummyImage);
        news_ad_layer.setOnClickListener(mClickListener);

        mThemeModel = ThemeManager.GetThemeModel(mContext, 10);
        if (mThemeModel != null) {
            try {
                NinePatchDrawable norNor = ThemeManager.GetNinePatch(mContext, mThemeModel.getNorBtnNorI()); // 일반키 normal
                NinePatchDrawable norPre = ThemeManager.GetNinePatch(mContext, mThemeModel.getNorBtnPreI()); // 일반키 pressed
                Drawable norBtnSelector = ThemeManager.GetImageSelector(norNor, norPre); // 일반키 selector

                NinePatchDrawable speNor = ThemeManager.GetNinePatch(mContext, mThemeModel.getSpBtnNorI()); // 특수키 normal
                NinePatchDrawable spePre = ThemeManager.GetNinePatch(mContext, mThemeModel.getSpBtnPreI()); // 특수키 pressed
                Drawable spBtnSelector = ThemeManager.GetImageSelector(speNor, spePre); // 특수키 selector
//                NinePatchDrawable bg = ThemeManager.GetNinePatch(mContext, mThemeModel.getBgImg()); // 배경이미지
                Drawable bg = ThemeManager.GetDrawableFromPath(mThemeModel.getBgImg()); // 배경이미지, 나인페치 미적용 2017.12.04
                int txtColor = Color.parseColor(mThemeModel.getKeyText()); // 키 텍스트 색상
                String strOptColor = "#919191";
                if (!TextUtils.isEmpty(mThemeModel.getKeyTextS()))
                    strOptColor = mThemeModel.getKeyTextS();

                int optTxtColor = Color.parseColor(strOptColor);

                String mUsedTheme = "theme_color_01";
                String strRoot = mContext.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator;
                File rootFile = new File(strRoot);
                File[] fileList = rootFile.listFiles();
                if (fileList.length > 0) {
                    for (int i = 0; i < fileList.length; i++) {
                        File inFile = fileList[i];
                        if (inFile.isDirectory()) {
                            mUsedTheme = inFile.getName();
                        }
                    }
                }
                LogPrint.d("mUsedTheme :: " + mUsedTheme);
//                if ( "theme_color_01".equals(mUsedTheme) || "theme_118".equals(mUsedTheme) )
//                    tabChatGpt = getResources().getDrawable(R.drawable.aikbd_chat_gpt_normal);
//                else
//                    tabChatGpt = getResources().getDrawable(R.drawable.aikbd_chat_gpt_theme);

                tabEmoji = ThemeManager.GetDrawableFromPath(mThemeModel.getTabEmoji());
                tabOlabang = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOlabang());
                //tabShopping = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShopping());
                if (!TextUtils.isEmpty(mThemeModel.getTabSaveShopping()))
                    tabShopping = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShopping());
                else {
                    String targetPath = mThemeModel.getTabEmoji();
                    if (!TextUtils.isEmpty(targetPath)) {
                        if (targetPath.contains("theme_color_01") || targetPath.contains("theme_118")) {
                            tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_118);
                        } else if (targetPath.contains("theme_119")) {
                            tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_119);
                        } else if (targetPath.contains("theme_120")) {
                            tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_120);
                        } else if (targetPath.contains("theme_115")) {
                            tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_115);
                        } else if (targetPath.contains("theme_121")) {
                            tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_121);
                        } else {
                            tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_118);
                        }
                    }
                }
                tabMy = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMy());
                tabMore = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMore());
                tabOCBSearch = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOCBSearch());

                tabEmojiOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabEmojiOn());
                tabMoreOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMoreOn());
                tabMyOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMyOn());
                tabOlabangOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOlabangOn());
                tabOCBSearchOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOCBSearchOn());
                if (!TextUtils.isEmpty(mThemeModel.getTabSaveShoppingOn()))
                    tabShoppingOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShoppingOn());
                else {
                    String targetPath = mThemeModel.getTabEmoji();
                    if (!TextUtils.isEmpty(targetPath)) {
                        if (targetPath.contains("theme_color_01") || targetPath.contains("theme_118")) {
                            tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_118);
                        } else if (targetPath.contains("theme_119")) {
                            tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_119);
                        } else if (targetPath.contains("theme_120")) {
                            tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_120);
                        } else if (targetPath.contains("theme_115")) {
                            tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_115);
                        } else if (targetPath.contains("theme_121")) {
                            tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_121);
                        } else {
                            tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_118);
                        }
                    }
                }

                adDel = ThemeManager.GetDrawableFromPath(mThemeModel.getAdDel());
                /*
                brandOff = ThemeManager.GetDrawableFromPath(mThemeModel.getBrandOff());
                brandOn = ThemeManager.GetDrawableFromPath(mThemeModel.getBrandOn());
                brandOn2 = ThemeManager.GetDrawableFromPath(mThemeModel.getBrandOn2());
                */

                drBrand = ThemeManager.GetDrawableFromPath(mThemeModel.getBrandOff());
                brandLink = DEFAULT_BRAND_LINK;
                LogPrint.d("drbrand set 1");
                AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
                BrandTabModel model = helper.getBrandUrl();
                if (model != null && !TextUtils.isEmpty(model.getImage()) && !TextUtils.isEmpty(model.getLink())) {
                    File file = new File(model.getImage());
                    if (file.exists()) {
                        LogPrint.d("drbrand set 2");
                        drBrand = ThemeManager.GetDrawableFromPath(model.getImage());
                        brandLink = model.getLink();
                    }
                }


                mCashDr = ThemeManager.GetDrawableFromPath(mThemeModel.getTabCash());
                mZeroCashDr = ThemeManager.GetDrawableFromPath(mThemeModel.getTabZeroCash());
                int tabNor = Color.parseColor(mThemeModel.getTabOff()); // 상단 tab off 색상
                int tabPre = Color.parseColor(mThemeModel.getTabOn()); // 상단 tab on 색상

                Drawable tabSelector_logo = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_first = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_second = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_third = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_fourth = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_fifth = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_more = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_chat = ThemeManager.GetColorSelector(tabNor, tabPre); // chat gpt tab selector
                mFirstTabLayer.setBackgroundDrawable(tabSelector_first);
                mTimedealTabLayer.setBackgroundDrawable(tabSelector_second);
                mSecondTabLayer.setBackgroundDrawable(tabSelector_third);
                mShoppingTabLayer.setBackgroundDrawable(tabSelector_fourth);
                offerwall_tab.setBackgroundDrawable(tabSelector_fourth);
                mFourthLayer.setBackgroundDrawable(tabSelector_fifth);
                mMoreLayer.setBackgroundDrawable(tabSelector_more);
                game_tab.setBackgroundDrawable(tabSelector_chat);
                String strTopLine = mThemeModel.getTopLine();
                int iTopColor = Color.parseColor(strTopLine);
                mTopLine.setBackgroundColor(iTopColor);
                topLine.setBackgroundColor(iTopColor);
                //mImgSecond.setImageResource(R.drawable.coupang_floating_icon);

                //mImgSecond.setBackgroundDrawable(brandOn);
                //setBrandImage(brandOn);
/**
 mImgFirst.setBackgroundDrawable(tabEmoji);
 mImgTimedeal.setBackgroundDrawable(tabShopping);
 mImgShopping.setBackgroundDrawable(tabOCBSearch);
 mImgFourth.setBackgroundDrawable(tabMy);
 mImgMore.setBackgroundDrawable(tabMore);**/
                setTabImages(KEYBOARD);
                setBrandTabIcon();
                shoppingTopLayer.setBackgroundColor(tabNor);
                //banner_ad_layer.setBackgroundColor(tabNor);

                String strColor = mThemeModel.getKeyText();
                int botStrColor = Color.parseColor(strColor);
                //bannerTxt.setTextColor(botStrColor);
                //bannerClose.setBackgroundDrawable(adDel);
                imgShoppingClose.setBackgroundDrawable(adDel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mFirstTabLayer.setOnClickListener(mClickListener);
        mTimedealTabLayer.setOnClickListener(mClickListener);
        mSecondTabLayer.setOnClickListener(mClickListener);
        mShoppingTabLayer.setOnClickListener(mClickListener);
        offerwall_tab.setOnClickListener(mClickListener);
        mFourthLayer.setOnClickListener(mClickListener);
        mMoreLayer.setOnClickListener(mClickListener);
        game_tab.setOnClickListener(mClickListener);
/**
 mFirstTabLayer.setOnLongClickListener(new OnLongClickListener() {
@Override public boolean onLongClick(View v) {
if (mSoftKeyboard != null) {
mSoftKeyboard.onPress(-226);
mSoftKeyboard.onKey(-226, null);
}
KeyboardLogPrint.w("visible setting long click mMatchedEmoji :: " + mMatchedEmoji.getVisibility());
if (mMatchedEmoji.getVisibility() == View.VISIBLE) {
KeyboardLogPrint.w("emoji visible");
viewPager.setCurrentItem(0);
} else {
KeyboardLogPrint.w("emoji gone");
viewPager.setCurrentItem(1);
RecentAdapter adapter = emojiPagerAdapter.getRecentAdapter();
if (adapter != null)
adapter.setupRecentDataFromList(false);
}

return true;
}
});**/
/**
 mThirdTabLayer.setOnLongClickListener(new OnLongClickListener() {
@Override public boolean onLongClick(View v) {
String currentTime = "0";
try {
long curTime = System.currentTimeMillis();
KeyboardLogPrint.w("curTime :: " + curTime);
currentTime = String.valueOf(curTime);
} catch (Exception e) {
e.printStackTrace();
}
String memoStr = "";
if (mSoftKeyboard != null)
memoStr = mSoftKeyboard.getEditStr();
KeyboardLogPrint.w("memoStr :: " + memoStr);
if (!TextUtils.isEmpty(memoStr)) {
KeyboardLogPrint.w("currentTime :: " + currentTime);
AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
if (helper.getMemoSize() >= MemoAdapter.MEMO_SIZE) {
Toast.makeText(mContext, mContext.getResources().getString(R.string.aikbd_memo_insert_error), Toast.LENGTH_SHORT).show();
} else {
if (helper.isMemoExist(memoStr)) {
Toast.makeText(mContext, mContext.getResources().getString(R.string.aikbd_memo_exist), Toast.LENGTH_SHORT).show();
} else {
helper.insertMemo("", memoStr, currentTime, "N");
Toast.makeText(mContext, mContext.getResources().getString(R.string.aikbd_memo_saved), Toast.LENGTH_SHORT).show();
}
}
} else {
Toast.makeText(mContext, mContext.getResources().getString(R.string.aikbd_memo_empty), Toast.LENGTH_SHORT).show();
}
return true;
}
});**/
        reward_joint_layer = layout.findViewById(R.id.reward_joint_layer);
        reward_joint_image = layout.findViewById(R.id.reward_joint_image);
        joint_product_name = layout.findViewById(R.id.joint_product_name);
        joint_top_margin = layout.findViewById(R.id.joint_top_margin);
        joint_price = layout.findViewById(R.id.joint_price);
        joint_logo = layout.findViewById(R.id.joint_logo);
        joint_adchoice = layout.findViewById(R.id.joint_adchoice);
        reward_joint_banner_layer = layout.findViewById(R.id.reward_joint_banner_layer);
        joint_banner_image = layout.findViewById(R.id.joint_banner_image);
        joint_banner_adchoice = layout.findViewById(R.id.joint_banner_adchoice);

        reward_coupang_layer = layout.findViewById(R.id.reward_coupang_layer);
        reward_coupang_image = layout.findViewById(R.id.reward_coupang_image);
        coupang_product_name = layout.findViewById(R.id.coupang_product_name);
        coupang_top_margin = layout.findViewById(R.id.coupang_top_margin);
        coupang_price = layout.findViewById(R.id.coupang_price);
        coupang_rocket = layout.findViewById(R.id.coupang_rocket);

        mixer_layer = layout.findViewById(R.id.mixer_layer);
        mixer_container = layout.findViewById(R.id.mixer_container);
        //mixer_webview = layout.findViewById(R.id.mixer_webview);

        mixer_close = layout.findViewById(R.id.mixer_close);

        mobwith_layer = layout.findViewById(R.id.mobwith_layer);
        mobwith_container = layout.findViewById(R.id.mobwith_container);
        mobwith_close = layout.findViewById(R.id.mobwith_close);
        mobwith_reward_point = layout.findViewById(R.id.mobwith_reward_point);
        mobwith_non_reward_badge = layout.findViewById(R.id.mobwith_non_reward_badge);

        // criteo
        criteo_layer = layout.findViewById(R.id.criteo_layer);
        criteo_image = layout.findViewById(R.id.criteo_image);
        criteo_product_name = layout.findViewById(R.id.criteo_product_name);
        criteo_price = layout.findViewById(R.id.criteo_price);
        criteo_adchoice = layout.findViewById(R.id.criteo_adchoice);
        criteo_close = layout.findViewById(R.id.criteo_close);

        coupang_dy_layer = layout.findViewById(R.id.coupang_dy_layer);
        dy_coupang_image = layout.findViewById(R.id.dy_coupang_image);
        dy_coupang_product_name = layout.findViewById(R.id.dy_coupang_product_name);
        dy_coupang_price = layout.findViewById(R.id.dy_coupang_price);
        dy_coupang_rocket = layout.findViewById(R.id.dy_coupang_rocket);
        dy_coupang_close = layout.findViewById(R.id.dy_coupang_close);
        reward_ad_layer = layout.findViewById(R.id.reward_ad_layer);
        mMobonRewardAdLayer = layout.findViewById(R.id.mobon_reward_ad_layer);
        mMobonRewardAdLayerC = layout.findViewById(R.id.mobon_reward_ad_layer_c);
        r_leftBg = layout.findViewById(R.id.r_leftBg);
        r_rightBg = layout.findViewById(R.id.r_rightBg);
        rest_layer = layout.findViewById(R.id.rest_layer);
        reward_close = layout.findViewById(R.id.reward_close);
        reward_point = layout.findViewById(R.id.reward_point);

        mMobonAdLayer = layout.findViewById(R.id.mobon_ad_layer);
        mMobonAdLayerC = layout.findViewById(R.id.mobon_ad_layer_c);
        ad_del = layout.findViewById(R.id.ad_del);

        mADLayer = (RelativeLayout) layout.findViewById(R.id.ad_layer);
        mKLADLayer = (RelativeLayout) layout.findViewById(R.id.kl_layer);
        mSRADLayer = (RelativeLayout) layout.findViewById(R.id.sr_layer);
        mEVLayer = (RelativeLayout) layout.findViewById(R.id.ev_layer);
        mEVDel = (RelativeLayout) layout.findViewById(R.id.ev_del);
        mKLDel = (RelativeLayout) layout.findViewById(R.id.kl_del);

        mSRDel = (RelativeLayout) layout.findViewById(R.id.sr_del);
        rl_unsafe_check = (RelativeLayout) layout.findViewById(R.id.rl_unsafe_check);
        iv_unsafe_check = (ImageView) layout.findViewById(R.id.iv_unsafe_check);
        tv_unsafe_check = (TextView) layout.findViewById(R.id.tv_unsafe_check);
        iv_close = (ImageView) layout.findViewById(R.id.iv_close);

        mKLDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setADVisible(false, "");
            }
        });

        mSRDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setADVisible(false, "");
            }
        });

        mEVDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setADVisible(false, "");
            }
        });

        setADVisible(false, ""); // 기본은 첫 시작 시 감추도록 함

        viewPager = (ViewPager) layout.findViewById(R.id.emojiKeyboard);

        emojiPagerAdapter = new EmojiPagerAdapter(context, viewPager);
        emojiPagerAdapter.setItems(mContext);
        viewPager.setAdapter(emojiPagerAdapter);
        viewPager.setVerticalScrollBarEnabled(true);
        viewPager.setScrollBarStyle(ViewPager.SCROLLBARS_INSIDE_OVERLAY);

        emoticonViewPager = (ViewPager) layout.findViewById(R.id.emoticonKeyboard);
        emoticonPagerAdapter = new EmoticonPagerAdapter(context, emoticonViewPager);
        emoticonViewPager.setAdapter(emoticonPagerAdapter);
        emoticonViewPager.setCurrentItem(1);
        emoticonViewPager.setVerticalScrollBarEnabled(true);
        emoticonViewPager.setScrollBarStyle(ViewPager.SCROLLBARS_INSIDE_OVERLAY);

        mRootParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mKeyboardHeight);
        mContainer.setLayoutParams(mRootParams);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mOriginKeyboardHeight);
        mEmojiLayer.setLayoutParams(params);
        mEmoticonLayer.setLayoutParams(params);
        mKeyboardLayer.setLayoutParams(params);
        mUtilLayer.setLayoutParams(params);
        mKeyboardView = (LatinKeyboardView) layout.findViewById(R.id.keyboard_view);
        mEKeyboardView = (LatinKeyboardView) layout.findViewById(R.id.e_keyboard_view);
        mEmoticonKeyboardView = (LatinKeyboardView) layout.findViewById(R.id.emoticon_keyboard_view);
//        selectKeyboard(true);

        selectKeyboard(GUBUN_KEYBOARD);

        if ( kv != null && ekv != null && emoticonkv != null ) {
            try {
                mKeyboardView.setKeyboard(kv);
                mKeyboardView.setKeys();
                mEKeyboardView.setKeyboard(ekv);
                mEKeyboardView.setKeys();
                mEKeyboardView.setOnKeyboardActionListener(this);
                mEKeyboardView.setPreviewEnabled(false);
                viewPager.setPadding(0, 0, 0, ekv.getHeight());

                mEmoticonKeyboardView.setKeyboard(emoticonkv);
                mEmoticonKeyboardView.setKeys();
                mEmoticonKeyboardView.setOnKeyboardActionListener(this);
                mEmoticonKeyboardView.setPreviewEnabled(false);
                emoticonViewPager.setPadding(0, 0, 0, emoticonkv.getHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setKeyboardBackground();
        initEmojiArray();
        if ( !isSetChangeRegistered ) {
            IntentFilter intentFilter = new IntentFilter();

//        intentFilter.addAction(SoftKeyboard.SET_CHANGE);
//        intentFilter.addAction(MyReceiver.VOLUME_CHANGE);
            intentFilter.addAction("THEME_CHANGE");
            intentFilter.addAction(POINT_CHARGE);
            intentFilter.addAction(SET_KEYBOARD);
            intentFilter.addAction(SoftKeyboard.CHAT_GPT_RESUME);
            intentFilter.addAction(SoftKeyboard.CHAT_GPT_PAUSE);
//        intentFilter.addAction("SOUND_CHANGE");

//        mContext.registerReceiver(mSetChange, intentFilter);
//        mContext.registerReceiver(mSetChange, new IntentFilter(MyReceiver.VOLUME_CHANGE));
//        mContext.registerReceiver(mSetChange, new IntentFilter("THEME_CHANGE"));
//        mContext.registerReceiver(mSetChange, new IntentFilter(POINT_CHARGE));
//        mContext.registerReceiver(mSetChange, new IntentFilter(SET_KEYBOARD));
//        mContext.registerReceiver(mSetChange, new IntentFilter("SOUND_CHANGE"));
//        IntentFilter filter = new IntentFilter();

            intentFilter.addAction(BACK_KEY_LISTENER);
            intentFilter.addAction(FINISH_WEBVIEW);
            intentFilter.addAction(POP_POINT);
            try {
                // target 34 대응
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.registerReceiver(mSetChange, intentFilter, Context.RECEIVER_EXPORTED);
                } else {
                    mContext.registerReceiver(mSetChange, intentFilter);
                }
//                mContext.registerReceiver(mSetChange, intentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isSetChangeRegistered = true;
        }


        LogPrint.d("regist receiver MainKeyboardView");
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mVolumeLevel = SharedPreference.getInt(context, Common.PREF_I_VOLUME_LEVEL);
        if (mVolumeLevel < 0) {
            SharedPreference.setInt(context, Common.PREF_I_VOLUME_LEVEL, Common.DEFAULT_SOUND_LEVEL);
            mVolumeLevel = Common.DEFAULT_SOUND_LEVEL;
        }

        mVibrateLevel = SharedPreference.getLong(context, Common.PREF_VIBRATE_LEVEL);
        initSound(context);

        //initBannerView();
        //mADLayer.addView(bannerView);


        imgShoppingLogo = layout.findViewById(R.id.img_shopping_logo);


        editSearch = layout.findViewById(R.id.editSearch);
        clearText = layout.findViewById(R.id.clearText);

        clearText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                editSearch.setText("");
                mSoftKeyboard.clear();
                selectKeyboard(GUBUN_KEYBOARD);
            }
        });
        editSearch.setCursorVisible(true);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    //editSearch.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    clearText.setVisibility(View.VISIBLE);
                } else {
                    //editSearch.setGravity(Gravity.CENTER);
                    clearText.setVisibility(View.GONE);
                }
            }
        });
        editSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectKeyboard(GUBUN_KEYBOARD);
            }
        });

        bannerClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRake(currentPageId, "tap.closebtn");
                banner_ad_layer.setVisibility(View.GONE);
                SharedPreference.setLong(mContext, Key.CLOSE_AD_TIME, System.currentTimeMillis());
                sendViewCount();
            }
        });

        reward_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRake(currentPageId, "tap.closebtn");
                banner_ad_layer.setVisibility(View.GONE);
                mMobonRewardAdLayerC.setVisibility(View.GONE);
                SharedPreference.setLong(mContext, Key.CLOSE_AD_TIME, System.currentTimeMillis());
                sendViewCount();
            }
        });
/**
 클립보드 복사한 내용 가져오기
 final ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
 clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
 public void onPrimaryClipChanged() {
 String a = clipboard.getText().toString();
 if ( editSearch != null ) {
 editSearch.setText(a.toString());
 }
 }
 });**/

        /**editSearch.setOnTouchListener(new OnTouchListener() {
        @Override public boolean onTouch(View view, MotionEvent motionEvent) {

        editSearch.setFocusable(true);
        editSearch.requestFocus();
        editSearch.setFocusableInTouchMode(true);
        return false;
        }
        });**/
        closeSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRake("/keyboard/search", "tap.closebtn");
                editSearch.setText("");
                mImgShopping.setBackgroundDrawable(tabOCBSearch);
                mSoftKeyboard.clear();
                mSoftKeyboard.recoverInputConnection();
                shoppingTopLayer.setVisibility(View.GONE);
                SoftKeyboard.SetImeOption(orgImgOption);
                mKeyboardView.invalidateAllKeys();

                selectKeyboard(GUBUN_KEYBOARD);
            }
        });

        editSearch.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        offerwall_point_layer = layout.findViewById(R.id.offerwall_point_layer);
        ocb_offerwall_point = layout.findViewById(R.id.ocb_offerwall_point);
        mOCBPointLayer = layout.findViewById(R.id.ocb_point_layer);
        mOCBPointText = layout.findViewById(R.id.aikbd_ocb_point);
        mPlusText = layout.findViewById(R.id.plus);
        mOCBPointLayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setRake(Common.PAGE_ID_KEYBOARD, "top_tap.earnbtn");
                int totalOCBPoint = mSoftKeyboard.getOCBPoint();
                LogPrint.d("kkkssskkkkkk click point :: " + totalOCBPoint + " , possible :: " + isKeyboardPointClickPossible);
                if (totalOCBPoint > 0 && isKeyboardPointClickPossible ) {
                    isKeyboardPointClickPossible = false;
                    CustomAsyncTask task = new CustomAsyncTask(mContext);
                    String sPoint = "" + totalOCBPoint;
                    LogPrint.d("kkkssskkkkkk click request point :: " + sPoint);
                    task.savePointV2(sPoint, POINT_TYPE_USE_KEY, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            isKeyboardPointClickPossible = true;
                            if (result) {
                                try {
                                    // 적립 후속 처리
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        LogPrint.d("kkkssskkkkkk click response :: " + object.toString());
                                        boolean rt = object.optBoolean("Result");
                                        if (rt) {
                                            int totalPoint = object.optInt("total_point");
                                            mSoftKeyboard.setTotalPoint(totalPoint);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                // api 30부터 setGravity 사용 막힘. 일반 toast로 대체(2022.09.19)
                                                setRake(Common.PAGE_ID_KEYBOARD, "toast.usereward");
                                                Toast.makeText(mContext, sPoint + "P 적립!!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                                                View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_save_toast, null);
                                                TextView tv = layout.findViewById(R.id.toastStr);
                                                tv.setText(sPoint + "P 적립!!");
                                                Toast toast = new Toast(mContext);
                                                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                toast.setDuration(Toast.LENGTH_SHORT);
                                                toast.setView(layout);
                                                toast.show();
                                                setRake(Common.PAGE_ID_KEYBOARD, "toast.usereward");
                                            }

                                            mImgFourth.setVisibility(View.VISIBLE);
                                            mOCBPointLayer.setVisibility(View.GONE);

                                            int offerwall_show = object.optInt("offerwall_show", 0);
                                            LogPrint.d("offerwall_show :: " + offerwall_show);
                                            setOfferwallBadge(offerwall_show);
                                        } else {
                                            String errstr = object.optString("errstr");
                                            if (!TextUtils.isEmpty(errstr)) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                    // api 11 이상부터 setGravity 사용하지 못함. 일괄 내장 toast 로 통일(2022.09.19)
                                                    Toast.makeText(mContext, errstr, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                                                    View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_save_toast, null);
                                                    TextView tv = layout.findViewById(R.id.toastStr);
                                                    tv.setText(errstr);
                                                    ImageView img = layout.findViewById(R.id.img);
                                                    img.setVisibility(View.GONE);
                                                    Toast toast = new Toast(mContext);
                                                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                    toast.setDuration(Toast.LENGTH_SHORT);
                                                    toast.setView(layout);
                                                    toast.show();
                                                }
                                            }
                                        }
                                    } else {

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        String error = object.optString(Common.NETWORK_ERROR);
                                        String dError = object.optString(Common.NETWORK_DISCONNECT);
                                        if (!TextUtils.isEmpty(error)) {
                                            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (!TextUtils.isEmpty(dError)) {
                                                Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });


                    /**
                     String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
                     String sPoint = "" + totalOCBPoint;
                     task.getToken(sPoint, POINT_TYPE_USE_KEY, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override public void onResponse(boolean result, Object obj) {
                    if (result) {
                    try {
                    JSONObject object = (JSONObject) obj;
                    if (object != null) {
                    boolean rt = object.optBoolean("Result");
                    if (rt) {
                    String token = object.optString("token");
                    SharedPreference.setString(mContext, Key.KEY_TOKEN, token);
                    if (!TextUtils.isEmpty(token)) {
                    String sPoint = "" + totalOCBPoint;
                    task.savePoint(sPoint, POINT_TYPE_USE_KEY, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override public void onResponse(boolean result, Object obj) {
                    if (result) {
                    try {
                    // 적립 후속 처리
                    JSONObject object = (JSONObject) obj;
                    if (object != null) {
                    boolean rt = object.optBoolean("Result");
                    if (rt) {
                    int totalPoint = object.optInt("total_point");
                    mSoftKeyboard.setTotalPoint(totalPoint);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (isInAppKeyboard) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_save_toast, null);
                    TextView tv = layout.findViewById(R.id.toastStr);
                    tv.setText(sPoint + "P 적립!!");
                    Toast toast = new Toast(mContext);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    setRake(Common.PAGE_ID_KEYBOARD, "toast.usereward");
                    } else {
                    setRake(Common.PAGE_ID_KEYBOARD, "toast.usereward");
                    Toast.makeText(mContext, sPoint + "P 적립!!", Toast.LENGTH_SHORT).show();
                    }
                    } else {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_save_toast, null);
                    TextView tv = layout.findViewById(R.id.toastStr);
                    tv.setText(sPoint + "P 적립!!");
                    Toast toast = new Toast(mContext);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    setRake(Common.PAGE_ID_KEYBOARD, "toast.usereward");
                    }

                    mImgFourth.setVisibility(View.VISIBLE);
                    mOCBPointLayer.setVisibility(View.GONE);
                    } else {
                    String errstr = object.optString("errstr");
                    if (!TextUtils.isEmpty(errstr)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Toast.makeText(mContext, errstr, Toast.LENGTH_SHORT).show();
                    if (isInAppKeyboard) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_save_toast, null);
                    TextView tv = layout.findViewById(R.id.toastStr);
                    tv.setText(errstr);
                    ImageView img = layout.findViewById(R.id.img);
                    img.setVisibility(View.GONE);
                    Toast toast = new Toast(mContext);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    } else {
                    Toast.makeText(mContext, errstr, Toast.LENGTH_SHORT).show();
                    }
                    } else {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_save_toast, null);
                    TextView tv = layout.findViewById(R.id.toastStr);
                    tv.setText(errstr);
                    ImageView img = layout.findViewById(R.id.img);
                    img.setVisibility(View.GONE);
                    Toast toast = new Toast(mContext);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    }
                    }
                    }
                    } else {

                    }
                    } catch (Exception e) {
                    e.printStackTrace();
                    }
                    } else {
                    try {
                    JSONObject object = (JSONObject) obj;
                    if (object != null) {
                    String error = object.optString(Common.NETWORK_ERROR);
                    String dError = object.optString(Common.NETWORK_DISCONNECT);
                    if ( !TextUtils.isEmpty(error) ) {
                    Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                    } else {
                    if ( !TextUtils.isEmpty(dError) ) {
                    Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
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
                    } else {
                    String errMsg = object.optString("errstr");
                    Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
                    }
                    } else {

                    }
                    } catch (Exception e) {
                    e.printStackTrace();
                    }
                    } else {
                    try {
                    JSONObject object = (JSONObject) obj;
                    if (object != null) {
                    String error = object.optString(Common.NETWORK_ERROR);
                    String dError = object.optString(Common.NETWORK_DISCONNECT);
                    if ( !TextUtils.isEmpty(error) ) {
                    Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                    } else {
                    if ( !TextUtils.isEmpty(dError) ) {
                    Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
                    }
                    }
                    }
                    } catch (Exception e) {
                    e.printStackTrace();
                    }
                    }
                    }
                    });**/
                }
            }
        });

        //getNotice();
    }

    @Override
    public void onDetachedFromWindow() {
        LogPrint.d("un regist receiver onDetachedFromWindow");
        //mContext.unregisterReceiver(mSetChange);
        super.onDetachedFromWindow();
    }

    public void unregisterReceiver() {
        if ( isSetChangeRegistered ) {
            mContext.unregisterReceiver(mSetChange);
            isSetChangeRegistered = false;
        }

    }
/*
    public void unregisterReceiver() {
        try {
            if ( mContext != null && mSetChange != null )
                mContext.unregisterReceiver(mSetChange);
            else {
                if ( mContext == null )
                    LogPrint.d("mContext null");
                if ( mSetChange == null )
                    LogPrint.d("mSetChange null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    */

    private boolean hasRecentDataFromList() {
        List<RecentEmojiModel> recentEntries = PreferenceManager.getInstance(mContext).getRecentEmoji();

        return (recentEntries != null && recentEntries.size() > 0);
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            KeyboardLogPrint.e("mClickListener call");
            int id = v.getId();
            if (id == R.id.first_tab) { // go emoji

                setRake(Common.PAGE_ID_KEYBOARD, "top_tap.emoticon");
//                soundAndVibrate();
//
//                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
//
//                Intent intent = new Intent(mContext, KeyboardAditionADActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);

                // 20200910 P(광고리스트) 기능 제거 - keko
//                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
//
//                Intent intent = new Intent(mContext, KeyboardAditionADActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);

//                if (emojiPagerAdapter.getItemsSize() == 0) {
//                    emojiPagerAdapter.setItems(mContext);
//                }

                mSoftKeyboard.completeInputConnection();
                if (mMatchedEmoji.getVisibility() == View.VISIBLE) {
                    if (mClickCallbackListener != null) {
                        String emoji = mMatchedEmoji.getText().toString();
                        mClickCallbackListener.onReceive("", emoji);
                    }
                } else {
                    if (mTabPosition == TAB_EMOJI) {
                        setTabImages(KEYBOARD);
                        selectKeyboard(GUBUN_KEYBOARD);
                    } else {
                        setTabImages(TAB_EMOJI);
                        selectKeyboard(GUBUN_EMOJI);
                    }
                }
            } else if (id == R.id.second_tab) {  // go Coupang - keko
                if (!Common.IsNetworkConnected(mContext)) {
                    Toast.makeText(mContext, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    LogPrint.d("brand visibility ;: " + mBrandBadge.getVisibility());
                    if ( mBrandBadge.getVisibility() == View.VISIBLE )
                        isBrandHasReward = true;
                    else
                        isBrandHasReward = false;

                    mSoftKeyboard.hide();

                    setRake(Common.PAGE_ID_KEYBOARD, "top_tap.brandad");
                    LogPrint.d("coupang rake called");
                    setTabImages(KEYBOARD);

                /*
                addView(6);
                CustomAsyncTask task = new CustomAsyncTask(mContext);
                task.postStats("brand_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {

                    }
                });
*/
/**
 InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
 imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);

 soundAndVibrate();
 **/
                    boolean isOn = SharedPreference.getTrueBoolean(mContext, Key.KEY_IS_BRAND_ON);
                    if (isOn)
                        SharedPreference.setBoolean(mContext, Key.KEY_IS_BRAND_ON, false);
                    LogPrint.d("coupang isBrand On :: " + isOn);

                    AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
                    BrandTabModel model = helper.getBrandUrl();
                    String brLink = "";
                    if (model != null) {
                        LogPrint.d("coupang model not null");
                        LogPrint.d("coupang model image :: " + model.getImage());
                        LogPrint.d("coupang model link :: " + model.getLink());
                    } else {
                        LogPrint.d("coupang model null");
                    }
                    if (model != null && !TextUtils.isEmpty(model.getImage()) && !TextUtils.isEmpty(model.getLink())) {
                        File file = new File(model.getImage());
                        if (file.exists()) {
                            LogPrint.d("coupang 서버에서 받은 BRAND LINK 있음");
                            brLink = model.getLink();
                        }
                    }
                    LogPrint.d("coupang brand_Link :: " + brLink);
                    if ( model != null )
                        LogPrint.d("coupang brand_icon :: " + model.getImage());
                    if ( !TextUtils.isEmpty(brLink) ) {
                        if ( isBrandHasReward )
                            sendBrandPoint();
                        LogPrint.d("coupang 서버에서 받은 BRAND LINK 로 이동. link :: " + brLink);
                        brandLink = brLink;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //intent.setData(Uri.parse("https://coupa.ng/b3kAjA"));
                        intent.setData(Uri.parse(brandLink));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);

                        sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BRAND);
                    } else {

                        String dyData = SharedPreference.getString(mContext, Key.COUPANG_DY_BRAND_DATA);
                        LogPrint.d("coupang dyData :: " + dyData);
                        try {
                            if ( !TextUtils.isEmpty(dyData) ) {
                                JSONObject oj = new JSONObject(dyData);
                                if ( oj != null ) {
                                    JSONArray ar = oj.optJSONArray("data");
                                    if ( ar != null && ar.length() > 0 ) {
                                        JSONObject inObj = ar.optJSONObject(0);
                                        if ( inObj != null ) {
                                            brLink = inObj.optString("productUrl");
                                            LogPrint.d("coupang dyData brLink :: " + brLink);
                                            removeBrandFirstArray(oj);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ( !TextUtils.isEmpty(brLink) ) {
                            LogPrint.d("coupang 저장된 dy 광고 있음. 링크 이동 :: " + brLink);
                            if ( isBrandHasReward )
                                sendBrandPoint();

                            brandLink = brLink;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            //intent.setData(Uri.parse("https://coupa.ng/b3kAjA"));
                            intent.setData(Uri.parse(brandLink));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);

                            sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BRAND);
                        } else {
                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                            task.getCoupangData(COUPANG_DY_BRAND_AD, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                @Override
                                public void onResponse(boolean result, Object obj) {
                                    String link = "";
                                    if ( result ) {
                                        try {
                                            JSONObject object = (JSONObject) obj;
                                            LogPrint.d("coupang ad obj :: " + object.toString());
                                            if ( object != null ) {
                                                JSONArray array = object.optJSONArray("data");
                                                if ( array != null && array.length() > 0 ) {
                                                    SharedPreference.setString(mContext, Key.COUPANG_DY_BRAND_DATA, object.toString());
                                                    JSONObject inObj = array.optJSONObject(0);
                                                    if ( inObj != null ) {
                                                        link = inObj.optString("productUrl");
                                                        removeBrandFirstArray(object);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (!TextUtils.isEmpty(link) ) {
                                        LogPrint.d("api로 받아온 dy 광고 있음 :: " + link);
                                        if ( isBrandHasReward)
                                            sendBrandPoint();
                                        brandLink = link;
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        //intent.setData(Uri.parse("https://coupa.ng/b3kAjA"));
                                        intent.setData(Uri.parse(brandLink));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);

                                        sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BRAND);
                                    } else {
                                        if ( isBrandHasReward )
                                            sendBrandPoint();

                                        brandLink = DEFAULT_BRAND_LINK;
                                        LogPrint.d("api로 받아온 dy 광고 없음 :: " + brandLink);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        //intent.setData(Uri.parse("https://coupa.ng/b3kAjA"));
                                        intent.setData(Uri.parse(brandLink));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);

                                        sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BRAND);
                                    }
                                }
                            });
                        }
                    }
                }
            } else if (id == R.id.shopping_tab) {
                LogPrint.d("shopping_tab clicked");
                setRake(Common.PAGE_ID_KEYBOARD, "top_tap.searchbtn");
                setRake("/keyboard/search", "");
                setTabImages(KEYBOARD);
                //soundAndVibrate();
                shoppingTopLayer.setVisibility(View.VISIBLE);
                orgImgOption = SoftKeyboard.GetImeOption();
                SoftKeyboard.SetImeOption(editSearch.getImeOptions());
                editSearch.requestFocus();
                mKeyboardView.invalidateAllKeys();
                EditorInfo info = new EditorInfo();
                InputConnection ic = editSearch.onCreateInputConnection(info);
                mSoftKeyboard.clear();
                mSoftKeyboard.setInputConnection(ic, info);

                selectKeyboard(GUBUN_KEYBOARD);

            } else if ( id == R.id.offerwall_tab ) {
                LogPrint.d("offerwall_tab clicked");
                setRake(Common.PAGE_ID_KEYBOARD, "top_tap.ppzone");
                // 2023.02.03 배포시에는 우선 기존대로 native로 적용되도록 배포 예정
                /*
                Intent intent = new Intent(mContext, KeyboardOfferwallListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
*/
                if ( isHybridOfferwall ) {
                    String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
                    try {
                        E_Cipher cp = E_Cipher.getInstance();
                        uuid = cp.Decode(mContext, uuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ( !TextUtils.isEmpty(uuid) ) {
                        Intent intent = new Intent(mContext, KeyboardHybridOfferwallActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else {
                        String encodedUrl = Uri.encode("enliplekeyboard://keyboard_offerwall_hybrid");
                        String url = "ocbt://com.skmc.okcashbag.home_google/auth/entry?type=CI&exitUrl=" + encodedUrl;
                        LogPrint.d("url::"+url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }

                } else {
                    LogPrint.d("called old list");
                    Intent intent = new Intent(mContext, KeyboardOfferwallListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }

            } else if (id == R.id.fourth_tab) {
                if (mOCBPointLayer.getVisibility() != View.VISIBLE) // 작업 위해 임시로 막음 다시 풀어야함 ( 중요 )
                    addView(2);
                else
                    setRake(Common.PAGE_ID_KEYBOARD, "top_tap.mypoint");
            } else if (id == R.id.timedeal_tab) {
                addView(5);
            } else if (id == R.id.more_tab) {
                addView(7);
/**
 mSoftKeyboard.hide();
 if (mContext != null) {
 setRake(Common.PAGE_ID_KEYBOARD, "top_tap.setting");
 setTabImages(KEYBOARD);
 Intent intent = new Intent(mContext, KeyboardSettingsActivity.class);
 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 mContext.startActivity(intent);
 } else {

 }
 **/
            } else if ( id == R.id.game_tab) {
                getGameURL();
//                String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
//                try {
//                    E_Cipher cp = E_Cipher.getInstance();
//                    uuid = cp.Decode(mContext, uuid);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if ( !TextUtils.isEmpty(uuid) ) {
//                    Intent intent = new Intent(mContext, KeyboardChatGptChatActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent);
//                } else {
//                    String encodedUrl = Uri.encode("enliplekeyboard://keyboard_chat_gpt");
//                    String url = "ocbt://com.skmc.okcashbag.home_google/auth/entry?type=CI&exitUrl=" + encodedUrl;
//                    LogPrint.d("url::"+url);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent);
//                }
//                setRake("/keyboard", "top_tap.chatbot");
                setRake("/keyboard", "top_tap.mission");
            } else if (id == R.id.news_ad_layer) {
                if (banner_ad_layer.getVisibility() != View.VISIBLE && mMobonAdLayerC.getVisibility() != View.VISIBLE
                        && mMobonRewardAdLayerC.getVisibility() != View.VISIBLE &&  coupang_dy_layer.getVisibility() != View.VISIBLE && mixer_layer.getVisibility() != View.VISIBLE && criteo_layer.getVisibility() != View.VISIBLE) {
                    if (newsInfo != null && !TextUtils.isEmpty(newsInfo.getLink())) {
                        setRake(currentPageId, "tap.news");
                        if (isDefaultBrowserExist()) {
                            LogPrint.d("news clicked default exist ");
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(newsInfo.getLink()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_NEWS);
                        } else {
                            LogPrint.d("news clicked default not exist ");
                            websiteOpen(newsInfo.getLink());
                            sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_NEWS);
                        }
                    }
                }
            }
        }
    };

//    /**
//     * fix : 무조건 on으로 노출되어야 할때는 fix가 true, 그외는 무조건 false;
//     *
//     * @param fix
//     */
//    public void setBrandIcon(boolean fix) {
//        if (fix) {
//            SharedPreference.setBoolean(mContext, Key.KEY_IS_BRAND_ON, true);
//            setBrandImage(brandOn);
//        } else {
//            setBrandImage(brandOff);
//            boolean isOn = SharedPreference.getTrueBoolean(mContext, Key.KEY_IS_BRAND_ON);
//            if (isOn) {
//                setBrandImage(brandOn);
//            } else {
//                setBrandImage(brandOff);
//            }
//        }
//        //mImgSecond.setImageResource(R.drawable.coupang_floating_icon_off);
//    }

    private void getGameURL() {
        String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            uuid = cp.Decode(mContext, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ( !TextUtils.isEmpty(uuid) ) {
            CustomAsyncTask task = new CustomAsyncTask(mContext);
            task.getGameUrl(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    String gameURL = "error";
                    if ( result ) {
                        if ( obj != null ) {
                            JSONObject object = (JSONObject) obj;
                            if ( object != null ) {
                                LogPrint.d("obj str : " + object.toString());
                                String result_code = object.optString("result_code");
                                if ( "Success".equals(result_code) ) {
                                    String url = object.optString("data");
                                    if (!TextUtils.isEmpty(url) ) {
                                        gameURL = url;
                                    }
                                }
                            }
                        }
                    }
                    LogPrint.d("response called");
                    Intent intent = new Intent(mContext, KeyboardGameZoneActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    LogPrint.d("get game url response :: " + gameURL);
                    intent.putExtra("url", gameURL);
                    mContext.startActivity(intent);
                }
            });
        } else {
            String encodedUrl = Uri.encode("enliplekeyboard://keyboard_gamezone");
            String url = "ocbt://com.skmc.okcashbag.home_google/auth/entry?type=CI&exitUrl=" + encodedUrl;
            LogPrint.d("url::"+url);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }




    }

    public void initBrandInfo() {
        AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
        helper.deleteBrandUrl();
        drBrand = ThemeManager.GetDrawableFromPath(mThemeModel.getBrandOff());
        setBrandImage(drBrand);
        // deleteBrandUrl을 했기 떄문에 클릭 시 drLink값이 다시 정해짐
    }

    public void setBrandTabIcon() {
        LogPrint.d("setBrandTabIcon");
        AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
        BrandTabModel model = helper.getBrandUrl();
        if (model != null) {
            LogPrint.d("model not null");
            LogPrint.d("model image :: " + model.getImage());
            LogPrint.d("model link :: " + model.getLink());
        } else {
            LogPrint.d("model null");
        }
        if (model != null && !TextUtils.isEmpty(model.getImage()) && !TextUtils.isEmpty(model.getLink())) {
            File file = new File(model.getImage());
            if (file.exists()) {
                LogPrint.d("drbrand set 3");
                drBrand = ThemeManager.GetDrawableFromPath(model.getImage());
                brandLink = model.getLink();
            }
        }
        setBrandImage(drBrand);
    }

    /**
     * 하루에 첫 키보드 올라올 때 한번만 호출되며 이때 more 광고 close click 횟수를 초기화 한다.
     */
    public void initAdCloseCount() {
        moreAdCloseCount = 0;
    }

    public boolean isSearchVisible() {
        if (shoppingTopLayer.getVisibility() == View.VISIBLE)
            return true;
        else
            return false;
    }

    public void savePoint() {
        UserIdDBHelper helper = new UserIdDBHelper(mContext);
        KeyboardUserIdModel model = helper.getUserInfo();
        if (model == null) {
            mFourthLayer.setClickable(true);
            return;
        }
        PointDBHelper pointHelper = new PointDBHelper(mContext);
        int savePoint = pointHelper.getSavePoint();
        if (savePoint <= 0)
            return;
        CustomAsyncTask apiAsyncTask = new CustomAsyncTask(mContext);
        int randomPointCount = pointHelper.getRandomPoint();
        apiAsyncTask.requestChargePoint(model, savePoint, randomPointCount, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean rt, Object obj) {
                if (rt) {
                    try {
                        JSONObject object = new JSONObject(obj.toString());
                        boolean result = object.optBoolean("Result");
                        boolean errStr = object.optBoolean("errstr");
                        if (result) {
                            int useablePoint = object.optInt("useablePoint");
                            setLimitMax(useablePoint);

                            PointDBHelper helper = new PointDBHelper(mContext);
                            helper.deleteSavePoint();
                            helper.insertSavePoint(0);
                            helper.deleteRandomPoint();
                            helper.insertRandomPoint(0);

                            Intent pointCharged = new Intent(POINT_SAVED);
                            mContext.sendBroadcast(pointCharged);
                        } else {
                            KeyboardLogPrint.e("charge point err :: " + errStr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void launchApp(String packageName) {
        try {
            Intent intent = new Intent();
            intent.setPackage(packageName);

            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
            Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

            if (resolveInfos.size() > 0) {
                ResolveInfo launchable = resolveInfos.get(0);
                ActivityInfo activity = launchable.activityInfo;
                ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                Intent i = new Intent(Intent.ACTION_MAIN);

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                i.setComponent(name);

                mContext.startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addView(int index) {
        KeyboardLogPrint.e("addView call");
        LayoutInflater inflater = null;
        if (mContext != null)
            inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = null;
        if (inflater != null) {
            KeyboardLogPrint.e("addView index :: " + index);
            if (index == 1) {
                layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_first_tab_layer, null);
                initFirstTab(layout);
                if (mUtilLayer != null) {
                    mUtilLayer.removeAllViews();
                    mUtilLayer.addView(layout);
                }
                selectKeyboard(GUBUN_UTIL);
            } else if (index == 2) { // MY
                boolean isTimedeal = true;
                if (olabangItem != null)
                    isTimedeal = false;
                if (mTabPosition != MY) {
                    mTabPosition = MY;

                    //layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_my_tab_new_layer, null);
                    layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_tab_my_layer, null);
                    //initPagerMyTab(layout);
                    //initNewMyTab(layout);
                    initMyTab(layout);
                    if (mUtilLayer != null) {
                        mUtilLayer.removeAllViews();
                        mUtilLayer.addView(layout);
                    }
                    setTabImages(MY);
                    selectKeyboard(GUBUN_UTIL);

                    /**
                     layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_new_my_tab_layer, null);

                     initMyTab(layout);
                     if (mUtilLayer != null) {
                     mUtilLayer.removeAllViews();
                     mUtilLayer.addView(layout);
                     }
                     setTabImages(MY);
                     selectKeyboard(GUBUN_UTIL);
                     **/
                } else {
                    setTabImages(KEYBOARD);
                    mUtilLayer.removeAllViews();
                    selectKeyboard(GUBUN_KEYBOARD);
                }
            } else if (index == 3) { // 쇼핑
                if (mUtilLayer != null) {
                    if (mTabPosition != SHOPPING) {
                        mTabPosition = SHOPPING;
                        layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_shopping_tab_layer, null);
                        initShoppingTab(layout);
                        //initThirdTab(layout);
                        if (mUtilLayer != null) {
                            mUtilLayer.removeAllViews();
                            mUtilLayer.addView(layout);
                        }
                        setTabImages(SHOPPING);
                        selectKeyboard(GUBUN_UTIL);
                    } else {
                        setTabImages(KEYBOARD);
                        mUtilLayer.removeAllViews();
                        selectKeyboard(GUBUN_KEYBOARD);
                    }
                }
            } else if (index == 6) {
                if (mTabPosition != BRAND_AD) {
                    mTabPosition = BRAND_AD;
                    layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_brand_tab_layer, null);
                    initBrandTab(layout);
                    if (mUtilLayer != null) {
                        boolean isOn = SharedPreference.getTrueBoolean(mContext, Key.KEY_IS_BRAND_ON);
                        if (isOn)
                            SharedPreference.setBoolean(mContext, Key.KEY_IS_BRAND_ON, false);
                        //mImgSecond.setImageResource(R.drawable.coupang_floating_icon_off);
                        //mImgSecond.setBackgroundDrawable(brandOff);
                        setTabImages(BRAND_AD);
                        mUtilLayer.removeAllViews();
                        mUtilLayer.addView(layout);
                    }
                    selectKeyboard(GUBUN_UTIL);
                } else {
                    mUtilLayer.removeAllViews();
                    selectKeyboard(GUBUN_KEYBOARD);
                }
            } else if (index == 4) {
                layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_fourth_tab_layer, null);
                initFourthTab(layout);
                if (mUtilLayer != null) {
                    mUtilLayer.removeAllViews();
                    mUtilLayer.addView(layout);
                }
                selectKeyboard(GUBUN_UTIL);
            } else if (index == 5) {
                if (mTabPosition != OLABANG) {
                    mTabPosition = OLABANG;
                    layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_olabang_tab_layer, null);
                    initOlabangTab(layout);
                    if (mUtilLayer != null) {
                        mUtilLayer.removeAllViews();
                        mUtilLayer.addView(layout);
                    }
                    setTabImages(OLABANG);
                    selectKeyboard(GUBUN_UTIL);

                } else {
                    setTabImages(KEYBOARD);
                    mUtilLayer.removeAllViews();
                    selectKeyboard(GUBUN_KEYBOARD);
                }
            } else if (index == 7) {
                if (mTabPosition != UTIL_COLLECTION) {
                    mTabPosition = UTIL_COLLECTION;
                    setRake("/keyboard", "top_tap.utill");
                    setRake("/keyboard/utill", "");
                    layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_util_collection_tab_layer, null);
                    initUtilCollection(layout);
                    if (mUtilLayer != null) {
                        mUtilLayer.removeAllViews();
                        mUtilLayer.addView(layout);
                    }
                    setTabImages(UTIL_COLLECTION);
                    selectKeyboard(GUBUN_UTIL);
                } else {
                    if (clip_root != null && clip_root.getVisibility() == View.VISIBLE) {
                        if (category_layer != null) {
                            clip_root.setVisibility(View.GONE);
                            category_layer.setVisibility(View.VISIBLE);
                        }

                        if (strCollection != null) {
                            strCollection.setText("적립 내역 보기");
                            strCollection.setTextColor(Color.parseColor("#868686"));
                            //strCollection.setClickable(true);
                        }

                        if (utilArrow != null)
                            utilArrow.setVisibility(View.VISIBLE);
                    } else {
                        setTabImages(KEYBOARD);
                        mUtilLayer.removeAllViews();
                        selectKeyboard(GUBUN_KEYBOARD);
                    }
                }
            } else {
                if (mUtilLayer != null) {
                    mUtilLayer.removeAllViews();
                    mUtilLayer.addView(layout);
                }

                selectKeyboard(GUBUN_UTIL);
            }
        }
    }

    private void initFirstTab(RelativeLayout layout) {
        RelativeLayout first_tab = (RelativeLayout) layout.findViewById(R.id.first_first_tab);
        first_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectKeyboard(GUBUN_KEYBOARD);
                setRake(Common.PAGE_ID_KEYBOARD, "bottom_tap.keyboard");
            }
        });
    }

    private void initFourthTab(RelativeLayout layout) {
        RelativeLayout first_tab = (RelativeLayout) layout.findViewById(R.id.fourth_first_tab);
        first_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectKeyboard(GUBUN_KEYBOARD);
            }
        });
    }


    /**
     * @param layout
     */
    private void initUtilCollection(RelativeLayout layout) {
        category_layer = layout.findViewById(R.id.category_layer);
        RelativeLayout aikbd_util_setting = layout.findViewById(R.id.aikbd_util_setting);
        RelativeLayout aikbd_util_clipboard = layout.findViewById(R.id.aikbd_util_clipboard);
        RelativeLayout aikbd_util_change_theme = layout.findViewById(R.id.aikbd_util_change_theme);
        RelativeLayout aikbd_util_qna = layout.findViewById(R.id.aikbd_util_qna);
        RelativeLayout aikbd_util_type = layout.findViewById(R.id.aikbd_util_type);
        RelativeLayout collection_first_tab = layout.findViewById(R.id.collection_first_tab);
        collection_second_tab = layout.findViewById(R.id.collection_second_tab);
        RelativeLayout voice_layer = layout.findViewById(R.id.voice_layer);
        util_ad_layer = layout.findViewById(R.id.util_ad_layer);

        util_mobon_reward_ad_layer_c = layout.findViewById(R.id.util_mobon_reward_ad_layer_c);
        util_reward_ad_layer = layout.findViewById(R.id.util_reward_ad_layer);
        util_mobon_reward_ad_layer = layout.findViewById(R.id.util_mobon_reward_ad_layer);
        util_r_leftBg = layout.findViewById(R.id.util_r_leftBg);
        util_r_rightBg = layout.findViewById(R.id.util_r_rightBg);
        util_rest_layer = layout.findViewById(R.id.util_rest_layer);
        util_reward_close = layout.findViewById(R.id.util_reward_close);
        util_reward_point = layout.findViewById(R.id.util_reward_point);
        util_reward_coupang_layer = layout.findViewById(R.id.util_reward_coupang_layer);
        util_reward_coupang_image = layout.findViewById(R.id.util_reward_coupang_image);
        util_coupang_product_name = layout.findViewById(R.id.util_coupang_product_name);
        util_coupang_top_margin = layout.findViewById(R.id.util_coupang_top_margin);
        util_coupang_price = layout.findViewById(R.id.util_coupang_price);
        util_coupang_rocket = layout.findViewById(R.id.util_coupang_rocket);


        util_reward_joint_layer = layout.findViewById(R.id.util_reward_joint_layer);
        util_reward_joint_image = layout.findViewById(R.id.util_reward_joint_image);
        util_joint_product_name = layout.findViewById(R.id.util_joint_product_name);
        util_joint_top_margin = layout.findViewById(R.id.util_joint_top_margin);
        util_joint_price = layout.findViewById(R.id.util_joint_price);
        util_joint_logo = layout.findViewById(R.id.util_joint_logo);
        util_joint_adchoice = layout.findViewById(R.id.util_joint_adchoice);
        util_reward_joint_banner_layer = layout.findViewById(R.id.util_reward_joint_banner_layer);
        util_joint_banner_image = layout.findViewById(R.id.util_joint_banner_image);
        util_joint_banner_adchoice = layout.findViewById(R.id.util_joint_banner_adchoice);

        util_mobwith_layer = layout.findViewById(R.id.util_mobwith_layer);
        util_mobwith_container = layout.findViewById(R.id.util_mobwith_container);
        util_mobwith_reward_point = layout.findViewById(R.id.util_mobwith_reward_point);
        util_mobwith_non_reward_badge = layout.findViewById(R.id.util_mobwith_non_reward_badge);
        util_mobwith_close = layout.findViewById(R.id.util_mobwith_close);

        if ( util_mobon_reward_ad_layer_c != null )
            util_mobon_reward_ad_layer_c.setVisibility(View.GONE);

        if ( util_ad_layer != null )
            util_ad_layer.setVisibility(View.GONE);

        if ( util_mobwith_layer != null )
            util_mobwith_layer.setVisibility(View.GONE);

        if ( util_mobwith_reward_point != null )
            util_mobwith_reward_point.setVisibility(View.GONE);

        if ( util_mobwith_non_reward_badge != null )
            util_mobwith_non_reward_badge.setVisibility(View.GONE);

        if (isVerticalMode() && moreAdCloseCount < 2) {
            CustomAsyncTask task = new CustomAsyncTask(mContext);
            task.isNewRewardPossible(Common.MOBWITH_RELEASE_SETTING_ZONE, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if ( result ) {
                        try {
                            JSONObject object = (JSONObject) obj;
                            if ( object != null ) {
                                boolean rt = object.optBoolean("Result");
                                if ( utilRewardBannerView != null )
                                    utilRewardBannerView.destroyAd();
                                utilRewardBannerView = null;

                                if (utilBannerView != null)
                                    utilBannerView.destroyAd();
                                utilBannerView = null;

                                if ( util_mobon_reward_ad_layer_c != null )
                                    util_mobon_reward_ad_layer_c.setVisibility(View.GONE);

                                if ( util_ad_layer != null )
                                    util_ad_layer.setVisibility(View.GONE);

                                if ( util_mobwith_webview != null && util_mobwith_container != null ) {
                                    util_mobwith_container.removeAllViews();
                                    if (util_mobwith_webview.getParent() != null)
                                        ((ViewGroup) util_mobwith_webview.getParent()).removeView(util_mobwith_webview);
                                }

                                if ( util_mobwith_layer != null )
                                    util_mobwith_layer.setVisibility(View.GONE);

                                initUtilMobwithAD(rt);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        /* 음성인식 주석처리
        String lang = "ko-KR";
        if (mSoftKeyboard != null && !mSoftKeyboard.isKoreanKeyboaard())
            lang = "en-US";
        initVoiceRecognizer(lang);
*/
        utilArrow = layout.findViewById(R.id.arrow);
        clip_root = layout.findViewById(R.id.clip_root);
        clip_root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        strCollection = layout.findViewById(R.id.strCollection);
        int screenWidth = Common.getDisplayWidth(mContext);
        int rest = screenWidth - Common.convertDpToPx(mContext, 58) - Common.convertDpToPx(mContext, 240);
        int gap = (int) (rest / 6);
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) aikbd_util_setting.getLayoutParams();
        param.setMargins(0, 0, gap, 0);
        aikbd_util_setting.setLayoutParams(param);

        param = (LinearLayout.LayoutParams) aikbd_util_clipboard.getLayoutParams();
        param.setMargins(gap, 0, gap, 0);
        aikbd_util_clipboard.setLayoutParams(param);

        param = (LinearLayout.LayoutParams) aikbd_util_type.getLayoutParams();
        param.setMargins(gap, 0, gap, 0);
        aikbd_util_type.setLayoutParams(param);

        param = (LinearLayout.LayoutParams) voice_layer.getLayoutParams();
        param.setMargins(0, 0, gap, 0);
        voice_layer.setLayoutParams(param);

        if (typingGameInfo != null && "Y".equals(typingGameInfo.getStatus())) {
            aikbd_util_type.setVisibility(View.VISIBLE);
        } else {
            aikbd_util_type.setVisibility(View.GONE);
        }

        RecyclerView clip_recyclerView = layout.findViewById(R.id.clip_recyclerView);
        RelativeLayout clip_empty_layer = layout.findViewById(R.id.clip_empty_layer);
        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        clip_recyclerView.setLayoutManager(manager);
        AikbdClipAdapter adapter = new AikbdClipAdapter(mContext, screenWidth, new AikbdClipAdapter.Listener() {
            @Override
            public void onItemClicked(ClipboardModel model) {
                if (model != null) {
                    setRake("/keyboard/clipboard", "tap.clipboardcopy");
                    mSoftKeyboard.past(model.getClipboard());
                }
            }
        });
        clip_recyclerView.setAdapter(adapter);

        collection_second_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strCollection != null && "전체 삭제".equals(strCollection.getText().toString())) {
                    setRake("/keyboard/clipboard", "bottom_tap.delete");
                    AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
                    helper.deleteClipboards();
                    strCollection.setTextColor(Color.parseColor("#cacaca"));
                    // strCollection.setClickable(false);
                    adapter.setItems(new ArrayList<ClipboardModel>());
                    clip_recyclerView.setVisibility(View.GONE);
                    clip_empty_layer.setVisibility(View.VISIBLE);
                } else {
                    String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
                    if (strCollection != null && "적립 내역 보기".equals(strCollection.getText().toString())) {
                        setRake("/keyboard/utill", "bottom_tap.viewmore");
                        if (!TextUtils.isEmpty(uuid)) {
                            try {
                                String url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=돈버는 키보드&url=https%3a%2f%2fwebview.okcashbag.com%2fv1.0%2fearnkbd%2findex.html";
                                if (CustomAsyncTask.gubun.equals(CustomAsyncTask.GUBUN_ALPHA))
                                    url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=돈버는 키보드&url=https%3a%2f%2falp-webview.okcashbag.com%2fv1.0%2fearnkbd%2findex.html";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                                mSoftKeyboard.hide();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            String message = "인증되지 않은 사용자 입니다. OK캐쉬백 앱에서 인증해주시기 바랍니다.";
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
/*
        strCollection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( "전체 삭제".equals(strCollection.getText().toString()) ) {
                    AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
                    helper.deleteClipboards();
                    strCollection.setTextColor(Color.parseColor("#cacaca"));
                    strCollection.setClickable(false);
                    adapter.setItems(new ArrayList<ClipboardModel>());
                    clip_recyclerView.setVisibility(View.GONE);
                    clip_empty_layer.setVisibility(View.VISIBLE);
                }
            }
        });
*/
        strCollection.setText("적립 내역 보기");
        strCollection.setTextColor(Color.parseColor("#868686"));
        utilArrow.setVisibility(View.VISIBLE);
        //strCollection.setClickable(true);

        aikbd_util_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSoftKeyboard.hide();
                setRake("/keyboard/utill", "tap.setting");
                if (mContext != null) {
                    setTabImages(KEYBOARD);
                    Intent intent = new Intent(mContext, KeyboardSettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else {

                }
            }
        });

        aikbd_util_clipboard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strCollection != null)
                    strCollection.setText("전체 삭제");
                if (utilArrow != null)
                    utilArrow.setVisibility(View.GONE);
                setRake("/keyboard/utill", "tap.clipboard");
                clip_root.setVisibility(View.VISIBLE);
                AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
                ArrayList<ClipboardModel> models = helper.getClipboards();
                if (models != null && models.size() > 0) {
                    //strCollection.setClickable(true);
                    strCollection.setTextColor(Color.parseColor("#868686"));
                    clip_recyclerView.setVisibility(View.VISIBLE);
                    clip_empty_layer.setVisibility(View.GONE);
                    adapter.setItems(models);
                } else {
                    //strCollection.setClickable(false);
                    strCollection.setTextColor(Color.parseColor("#cacaca"));
                    clip_recyclerView.setVisibility(View.GONE);
                    clip_empty_layer.setVisibility(View.VISIBLE);
                }
            }
        });

        aikbd_util_change_theme.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, KeyboardOCBCategoryThemeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("IS_FROM_SETTING", false);
                intent.putExtra("IS_FROM_KEYBOARD", true);
                mContext.startActivity(intent);
            }
        });

        aikbd_util_qna.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    uuid = cp.Decode(mContext, uuid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if ( !TextUtils.isEmpty(uuid) ) {
                    Intent intent = new Intent(mContext, KeyboardFAQActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else {
                    String encodedUrl = Uri.encode("enliplekeyboard://keyboard_faq");
                    String url = "ocbt://com.skmc.okcashbag.home_google/auth/entry?type=CI&exitUrl=" + encodedUrl;
                    LogPrint.d("url::"+url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });

        aikbd_util_type.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setRake("/keyboard/utill", "tap.event");
                    String url = typingGameInfo.getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        mSoftKeyboard.hide();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
/* 음성인식 주석처리
        voice_layer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopVoic음성eRecognizer();
                startVoiceRecognizer();
            }
        });
*/
        collection_first_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clip_root.getVisibility() == View.VISIBLE) {
                    setRake("/keyboard/clipboard", "bottom_tap.keyboard");
                } else {
                    setRake("/keyboard/utill", "bottom_tap.keyboard");
                }

                selectKeyboard(GUBUN_KEYBOARD);
            }
        });
    }

    /**
     * private void initSurpriseView(boolean isGoSurprise) {
     * if (clipContainer == null)
     * return;
     * <p>
     * clipContainer.setVisibility(View.VISIBLE);
     * LayoutInflater inflater = null;
     * if (mContext != null)
     * inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
     * RelativeLayout layout = null;
     * if (inflater != null) {
     * setRake("/keyboard/poppoint", "");
     * layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_suprise_layer, null);
     * if ( clipContainer != null ) {
     * containerContent = CONTAINER_SURPRISE;
     * clipContainer.removeAllViews();
     * clipContainer.addView(layout);
     * }
     * RecyclerView surprise_recyclerview = layout.findViewById(R.id.surprise_recyclerview);
     * RelativeLayout suprise_close = layout.findViewById(R.id.suprise_close);
     * RelativeLayout btn_guide = layout.findViewById(R.id.btn_guide);
     * ImageView guide_tip = layout.findViewById(R.id.guide_tip);
     * TextView guide_content = layout.findViewById(R.id.guide_content);
     * View topBg = layout.findViewById(R.id.top_bg);
     * RelativeLayout surprise_bg = layout.findViewById(R.id.surprise_bg);
     * surprise_bg.setOnClickListener(new OnClickListener() {
     *
     * @Override public void onClick(View view) {
     * if ( guide_content.getVisibility() == View.VISIBLE ) {
     * guide_tip.setVisibility(View.GONE);
     * guide_content.setVisibility(View.GONE);
     * }
     * }
     * });
     * <p>
     * guide_content.setOnTouchListener(new OnTouchListener() {
     * @Override public boolean onTouch(View view, MotionEvent motionEvent) {
     * return true;
     * }
     * });
     * <p>
     * GridLayoutManager manager = new GridLayoutManager(mContext, 3);
     * surprise_recyclerview.setLayoutManager(manager);
     * AikbdSurpriseAdapter adapter = new AikbdSurpriseAdapter(mContext, new AikbdSurpriseAdapter.Listener() {
     * @Override public void onItemClicked() {
     * if ( guide_content.getVisibility() == View.VISIBLE ) {
     * guide_tip.setVisibility(View.GONE);
     * guide_content.setVisibility(View.GONE);
     * }
     * }
     * });
     * surprise_recyclerview.setAdapter(adapter);
     * <p>
     * CustomAsyncTask task = new CustomAsyncTask(mContext);
     * task.getSurpriseList(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
     * @Override public void onResponse(boolean result, Object obj) {
     * if( result && obj != null ) {
     * try {
     * JSONObject object = (JSONObject) obj;
     * if ( object != null ) {
     * JSONArray arr = object.optJSONArray("data");
     * if ( arr != null && arr.length() > 0 ) {
     * ArrayList<SurpriseModel> modelArray = new ArrayList<>();
     * for ( int i = 0 ; i < arr.length() ; i ++ ) {
     * JSONObject in_obj = arr.optJSONObject(i);
     * if ( in_obj != null ) {
     * SurpriseModel model = new SurpriseModel();
     * model.setTitle(in_obj.optString("title"));
     * model.setPoint(in_obj.optString("point"));
     * model.setIcon(in_obj.optString("icon"));
     * modelArray.add(model);
     * }
     * }
     * adapter.setItems(modelArray);
     * }
     * }
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     * }
     * });
     * <p>
     * if (mThemeModel != null) {
     * String botColor = mThemeModel.getBotTabColor();
     * int iBotTabColor = Color.parseColor(botColor);
     * topBg.setBackgroundColor(iBotTabColor);
     * }
     * <p>
     * btn_guide.setOnClickListener(new OnClickListener() {
     * @Override public void onClick(View view) {
     * setRake("/keyboard/poppoint", "tap.info");
     * if ( guide_tip != null && guide_tip.getVisibility() == View.GONE && guide_content != null && guide_content.getVisibility() == View.GONE ) {
     * guide_tip.setVisibility(View.VISIBLE);
     * guide_content.setVisibility(View.VISIBLE);
     * } else {
     * guide_tip.setVisibility(View.GONE);
     * guide_content.setVisibility(View.GONE);
     * }
     * }
     * });
     * <p>
     * clipContainer.setOnTouchListener(new OnTouchListener() {
     * @Override public boolean onTouch(View view, MotionEvent motionEvent) {
     * if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
     * if ( guide_tip != null && guide_tip.getVisibility() == View.VISIBLE && guide_content != null && guide_content.getVisibility() == View.VISIBLE ) {
     * guide_tip.setVisibility(View.GONE);
     * guide_content.setVisibility(View.GONE);
     * }
     * }
     * return true;
     * }
     * });
     * <p>
     * suprise_close.setOnClickListener(new OnClickListener() {
     * @Override public void onClick(View view) {
     * if ( guide_content.getVisibility() == View.VISIBLE ) {
     * guide_tip.setVisibility(View.GONE);
     * guide_content.setVisibility(View.GONE);
     * } else {
     * setRake("/keyboard/poppoint", "tap.closebtn");
     * if ( isGoSurprise ) {
     * LayoutInflater inflater = null;
     * if (mContext != null)
     * inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
     * boolean isTimedeal = true;
     * if (olabangItem != null)
     * isTimedeal = false;
     * if (mTabPosition != MY) {
     * mTabPosition = MY;
     * RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_my_tab_new_layer, null);
     * initNewMyTab(layout);
     * if (mUtilLayer != null) {
     * mUtilLayer.removeAllViews();
     * mUtilLayer.addView(layout);
     * }
     * setTabImages(MY);
     * selectKeyboard(GUBUN_UTIL);
     * } else {
     * setTabImages(KEYBOARD);
     * mUtilLayer.removeAllViews();
     * selectKeyboard(GUBUN_KEYBOARD);
     * }
     * } else {
     * if ( clipContainer != null ) {
     * clipContainer.removeAllViews();
     * clipContainer.setVisibility(View.GONE);
     * }
     * if ( strCollection != null ) {
     * strCollection.setText("적립 내역 보기");
     * strCollection.setTextColor(Color.parseColor("#868686"));
     * }
     * }
     * }
     * }
     * });
     * }
     * }
     **/
    private void getClipData() {
        if (clipboard != null) {
            ClipData data = clipboard.getPrimaryClip();
            if (data != null) {
                for (int i = 0; i < data.getItemCount(); i++) {

                }
            }
        }
    }

    /**
     * private void initClipboardView(RelativeLayout collection_second_tab, TextView strCollection, int screenWidth) {
     * if ( clipContainer == null || collection_second_tab == null || strCollection == null)
     * return;
     * clipContainer.setVisibility(View.VISIBLE);
     * LayoutInflater inflater = null;
     * if (mContext != null)
     * inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
     * RelativeLayout layout = null;
     * if (inflater != null) {
     * setRake("/keyboard/clipboard", "");
     * layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_clipboard_view, null);
     * <p>
     * <p>
     * if ( clipContainer != null ) {
     * containerContent = CONTAINER_CLIPBOARD;
     * clipContainer.removeAllViews();
     * clipContainer.addView(layout);
     * }
     * }
     * <p>
     * }
     **/
    private void initMyTab(RelativeLayout layout) {
        LogPrint.d("initMyTab called");
        RelativeLayout first_tab = (RelativeLayout) layout.findViewById(R.id.my_first_tab);
        RelativeLayout mySecondTab = (RelativeLayout) layout.findViewById(R.id.my_second_tab);

        ConstraintLayout surprise_layer = layout.findViewById(R.id.surprise_layer);
        LinearLayout my_layer = layout.findViewById(R.id.my_layer);

        ConstraintLayout point_layer = layout.findViewById(R.id.point_layer);
        TextView tab_my_point = layout.findViewById(R.id.tab_my_point);
        RecyclerView surprise_recyclerview = layout.findViewById(R.id.surprise_recyclerview);
        RelativeLayout btn_guide = layout.findViewById(R.id.btn_guide);
        ImageView guide_tip = layout.findViewById(R.id.guide_tip);
        TextView guide_content = layout.findViewById(R.id.guide_content);
        ConstraintLayout guide_layer = layout.findViewById(R.id.guide_layer);
        TextView tlt_cashbag = layout.findViewById(R.id.tlt_cashbag);

        cardNum = SharedPreference.getString(mContext, Key.CARD_NUM);
        String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        String tPoint = SharedPreference.getString(mContext, Key.OCB_USER_POINT);

        View go_keyboard_save = layout.findViewById(R.id.go_keyboard_save);
        View go_ocb_save = layout.findViewById(R.id.go_ocb_save);

        TextView point = layout.findViewById(R.id.point);
        TextView ocb_unit = layout.findViewById(R.id.ocb_unit);
        TextView today_point = layout.findViewById(R.id.today_point);
        TextView tp_unit = layout.findViewById(R.id.tp_unit);
        TextView month_point = layout.findViewById(R.id.month_point);
        TextView mp_unit = layout.findViewById(R.id.mp_unit);
        ConstraintLayout copy_layer = layout.findViewById(R.id.copy_layer);
        TextView m_surprise_tlt = layout.findViewById(R.id.m_surprise_tlt);
        TextView cardFirst = layout.findViewById(R.id.card_first);
        TextView cardSecond = layout.findViewById(R.id.card_second);
        TextView cardThird = layout.findViewById(R.id.card_third);
        TextView cardFourth = layout.findViewById(R.id.card_fourth);

        if ( mSoftKeyboard != null ) {
            int width = mSoftKeyboard.getScreenWidth();
            LogPrint.d("MainKeyboardView screen width : " + width);
            int size = 22;
            int card_no_size = 14;
            if ( width < 1000 ) {
                size = 16;
                card_no_size = 12;
            }
            month_point.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            mp_unit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            today_point.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            tp_unit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            point.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            ocb_unit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

            cardFirst.setTextSize(TypedValue.COMPLEX_UNIT_DIP, card_no_size);
            cardSecond.setTextSize(TypedValue.COMPLEX_UNIT_DIP, card_no_size);
            cardThird.setTextSize(TypedValue.COMPLEX_UNIT_DIP, card_no_size);
            cardFourth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, card_no_size);
        }

        // 2023.04.25 TAB 변경
        surprise_layer.setVisibility(View.GONE);
        my_layer.setVisibility(View.VISIBLE);
        if ( guide_layer != null && guide_layer.getVisibility() == View.VISIBLE ) {
            guide_layer.setVisibility(View.GONE);
        }

        guide_layer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                guide_layer.setVisibility(View.GONE);
            }
        });

        first_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectKeyboard(GUBUN_KEYBOARD);
                // rake 임시 주석
                setRake(Common.PAGE_ID_KEYBOARD, "bottom_tap.keyboard");
            }
        });
        mySecondTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
                if (!TextUtils.isEmpty(uuid)) {
                    try {
                        String url = "ocbt://com.skmc.okcashbag.home_google/myMenu/points";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        mSoftKeyboard.hide();
                        // rake 임시 주석
                        setRake("/keyboard/mypoint", "bottom_tap.viewmore");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = "인증되지 않은 사용자 입니다. OK캐쉬백 앱에서 인증해주시기 바랍니다.";
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        surprise_recyclerview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( guide_layer != null && guide_layer.getVisibility() == View.VISIBLE ) {
                    guide_layer.setVisibility(View.GONE);
                }
            }
        });

        tab_my_point.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (my_layer.getVisibility() == View.GONE) {
                    if ( guide_layer != null && guide_layer.getVisibility() == View.VISIBLE ) {
                        guide_layer.setVisibility(View.GONE);
                    }
                    surprise_layer.setVisibility(View.GONE);
                    my_layer.setVisibility(View.VISIBLE);

                    CustomAsyncTask task = new CustomAsyncTask(mContext);
                    task.getUserInfo(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            if (result) {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        JSONObject dataObject = object.optJSONObject("data");
                                        myInfo.setMonth_point(dataObject.optString("month_point"));
                                        myInfo.setToday_point(dataObject.optString("today_point"));

                                        today_point.setText(Common.putComma(myInfo.getToday_point()));
                                        month_point.setText(Common.putComma(myInfo.getMonth_point()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        String error = object.optString(Common.NETWORK_ERROR);
                                        String dError = object.optString(Common.NETWORK_DISCONNECT);
                                        if (!TextUtils.isEmpty(error)) {
                                            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (!TextUtils.isEmpty(dError)) {
                                                Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    // rake 임시 주석
                    setRake("/keyboard/poppoint", "tap.mypointtap");
                }
            }
        });

        surprise_layer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( guide_layer != null && guide_layer.getVisibility() == View.VISIBLE ) {
                    guide_layer.setVisibility(View.GONE);
                }
            }
        });

        guide_content.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        surprise_recyclerview.setLayoutManager(manager);
        AikbdSurpriseAdapter adapter = new AikbdSurpriseAdapter(mContext, new AikbdSurpriseAdapter.Listener() {
            @Override
            public void onItemClicked() {
                if ( guide_layer != null && guide_layer.getVisibility() == View.VISIBLE ) {
                    guide_layer.setVisibility(View.GONE);
                }
            }
        });
        surprise_recyclerview.setAdapter(adapter);

//        CustomAsyncTask task = new CustomAsyncTask(mContext);
//        task.getSurpriseList(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//            @Override
//            public void onResponse(boolean result, Object obj) {
//                if (result && obj != null) {
//                    try {
//                        JSONObject object = (JSONObject) obj;
//                        if (object != null) {
//                            LogPrint.d("object surprise :: " + object.toString());
//                            JSONArray arr = object.optJSONArray("data");
//                            if (arr != null && arr.length() > 0) {
//                                ArrayList<SurpriseModel> modelArray = new ArrayList<>();
//                                for (int i = 0; i < arr.length(); i++) {
//                                    JSONObject in_obj = arr.optJSONObject(i);
//                                    if (in_obj != null) {
//                                        SurpriseModel model = new SurpriseModel();
//                                        model.setTitle(in_obj.optString("title"));
//                                        model.setPoint(in_obj.optString("point"));
//                                        model.setIcon(in_obj.optString("icon"));
//                                        if ( !in_obj.optString("title").equals("쇼핑 검색") )
//                                            modelArray.add(model);
//                                    }
//                                }
//                                adapter.setItems(modelArray);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getUserInfo(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            JSONObject dataObject = object.optJSONObject("data");
                            myInfo.setMonth_point(dataObject.optString("month_point"));
                            myInfo.setToday_point(dataObject.optString("today_point"));

                            today_point.setText(Common.putComma(myInfo.getToday_point()));
                            month_point.setText(Common.putComma(myInfo.getMonth_point()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            String error = object.optString(Common.NETWORK_ERROR);
                            String dError = object.optString(Common.NETWORK_DISCONNECT);
                            if (!TextUtils.isEmpty(error)) {
                                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                            } else {
                                if (!TextUtils.isEmpty(dError)) {
                                    Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_guide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( guide_layer != null && guide_layer.getVisibility() == View.GONE ) {
                    guide_layer.setVisibility(View.VISIBLE);
                } else {
                    guide_layer.setVisibility(View.GONE);
                }
            }
        });

        layout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if ( guide_layer != null && guide_layer.getVisibility() == View.VISIBLE ) {
                        guide_layer.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });

        // My tab
        go_keyboard_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=돈버는 키보드&url=https%3a%2f%2fwebview.okcashbag.com%2fv1.0%2fearnkbd%2findex.html";
                    if (CustomAsyncTask.gubun.equals(CustomAsyncTask.GUBUN_ALPHA))
                        url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=돈버는 키보드&url=https%3a%2f%2falp-webview.okcashbag.com%2fv1.0%2fearnkbd%2findex.html";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    // rake 임시 주석
                    setRake("/keyboard/mypoint", "tap.keyboardpoint");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        go_ocb_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
                if (!TextUtils.isEmpty(uuid)) {
                    try {
                        String url = "ocbt://com.skmc.okcashbag.home_google/myMenu/points";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        mSoftKeyboard.hide();
                        // rake 임시 주석
                        setRake("/keyboard/mypoint", "tap.totalpoint");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = "인증되지 않은 사용자 입니다. OK캐쉬백 앱에서 인증해주시기 바랍니다.";
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardFirst.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoftKeyboard != null)
                    mSoftKeyboard.setMemberNumber(cardFirst.getText().toString());
            }
        });

        cardSecond.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoftKeyboard != null)
                    mSoftKeyboard.setMemberNumber(cardSecond.getText().toString());
            }
        });

        cardThird.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoftKeyboard != null)
                    mSoftKeyboard.setMemberNumber(cardThird.getText().toString());
            }
        });

        cardFourth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoftKeyboard != null)
                    mSoftKeyboard.setMemberNumber(cardFourth.getText().toString());
            }
        });

        try {
            E_Cipher cp = E_Cipher.getInstance();
            cardNum = cp.Decode(mContext, cardNum);
            uuid = cp.Decode(mContext, uuid);
            tPoint = cp.Decode(mContext, tPoint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(tPoint) || "-1".equals(tPoint)) {
            tPoint = "-";
            point.setText(tPoint);
        } else {
            point.setText(Common.putComma(tPoint));
        }
        if (cardNum != null && cardNum.length() == 16) {
            String firstNum = cardNum.substring(0, 4);
            String secondNum = cardNum.substring(4, 8);
            String thirdNum = cardNum.substring(8, 12);
            String fourthNum = cardNum.substring(12, 16);
            setUnderLine(cardFirst, firstNum);
            setUnderLine(cardSecond, secondNum);
            setUnderLine(cardThird, thirdNum);
            setUnderLine(cardFourth, fourthNum);
        }

        copy_layer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("label", cardNum);
                if ( data != null ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            PersistableBundle extras = new PersistableBundle();
                            extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
                            data.getDescription().setExtras(extras);
                        } else {
                            PersistableBundle extras = new PersistableBundle();
                            extras.putBoolean("android.content.extra.IS_SENSITIVE", true);
                            data.getDescription().setExtras(extras);
                        }
                    }
                    manager.setPrimaryClip(data);
                    Toast.makeText(mContext, "맴버십 번호가 복사되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        m_surprise_tlt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (surprise_layer.getVisibility() == View.GONE) {
                    surprise_layer.setVisibility(View.VISIBLE);
                    my_layer.setVisibility(View.GONE);

                    CustomAsyncTask task = new CustomAsyncTask(mContext);
                    task.getSurpriseList(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            if (result && obj != null) {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        JSONArray arr = object.optJSONArray("data");
                                        if (arr != null && arr.length() > 0) {
                                            ArrayList<SurpriseModel> modelArray = new ArrayList<>();
                                            for (int i = 0; i < arr.length(); i++) {
                                                JSONObject in_obj = arr.optJSONObject(i);
                                                if (in_obj != null) {
                                                    SurpriseModel model = new SurpriseModel();
                                                    model.setTitle(in_obj.optString("title"));
                                                    model.setPoint(in_obj.optString("point"));
                                                    model.setIcon(in_obj.optString("icon"));
                                                    if ( !in_obj.optString("title").equals("쇼핑 검색") )
                                                        modelArray.add(model);
                                                }
                                            }
                                            adapter.setItems(modelArray);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
        // rake 임시 주석
        setRake(Common.PAGE_ID_KEYBOARD, "top_tap.mypoint");
        setRake("/keyboard/mypoint", "");
        setRake("/keyboard/mypoint", "tap.poppointtap");
    }

    private void initBrandTab(RelativeLayout layout) {
        String linkUrl = "";
        ViewPager pager = layout.findViewById(R.id.pager);
        RelativeLayout brandFirstTab = (RelativeLayout) layout.findViewById(R.id.brand_first_tab);
        RelativeLayout botBg = (RelativeLayout) layout.findViewById(R.id.brand_bottom_navigator);
        ImageView goKeyboardIcon = (ImageView) layout.findViewById(R.id.brand_first_img);
        TextView sec_line = (TextView) layout.findViewById(R.id.line);
        TextView strMore = (TextView) layout.findViewById(R.id.strMore);
        RelativeLayout brandSecondTab = (RelativeLayout) layout.findViewById(R.id.brand_second_tab);
        String botColor = mThemeModel.getBotTabColor();
        String strGoKeyboard = mThemeModel.getKeyboardBot();
        String lineColor = mThemeModel.getBotLine();
        String strColor = mThemeModel.getKeyText();
        int iBotColor = Color.parseColor(lineColor);
        int iBotTabColor = Color.parseColor(botColor);
        int botStrColor = Color.parseColor(strColor);
        /**botBg.setBackgroundColor(iBotTabColor);
         Drawable drGoKeyboard = ThemeManager.GetDrawableFromPath(strGoKeyboard);
         goKeyboardIcon.setBackgroundDrawable(drGoKeyboard);
         sec_line.setBackgroundColor(iBotColor);
         strMore.setTextColor(botStrColor);**/

        screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
        if (mSoftKeyboard != null) {
            screenWidth = mSoftKeyboard.getScreenWidth();
        }

        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) pager.getLayoutParams();
        int width = screenWidth - convertDpToPx(mContext, 40);
        int height = (int) ((165 * width) / 320);
        param.width = screenWidth;
        param.height = height;
        pager.setLayoutParams(param);

        brandFirstTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabImages(KEYBOARD);
                selectKeyboard(GUBUN_KEYBOARD);
            }
        });

        brandSecondTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (brandAdapter != null && brandPager != null) {
                    int position = brandPager.getCurrentItem();
                    BrandModel model = brandAdapter.getItem(position);
                    if (model != null) {
                        String moreLink = model.getMorePath();
                        try {
                            // ocb 더많은 혜택으로 이동할 경우
                            setRake("/keyboard/brandad", "bottom_tap.viewmore");
                            moreLink = "ocbt://com.skmc.okcashbag.home_google/main?tab=main";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(moreLink));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                } else {

                }
            }
        });

        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getBrandInfo(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result && obj != null) {
                    try {
                        JSONObject object = (JSONObject) obj;

                        if (object != null) {
                            int total_count = object.optInt("total_count");
                            JSONArray arr = object.optJSONArray("list_info");
                            if (arr != null && arr.length() > 0) {
                                ArrayList<BrandModel> modelArray = new ArrayList<>();
                                for (int i = 0; i < arr.length(); i++) {
                                    BrandModel model = new BrandModel();
                                    JSONObject inObj = arr.optJSONObject(i);
                                    String title = inObj.optString("title");
                                    String url = inObj.optString("url");
                                    String img_path = inObj.optString("img_path");
                                    String more_path = inObj.optString("more_path");
                                    model.setTitle(title);
                                    model.setLinkUrl(url);
                                    model.setImagePath(img_path);
                                    model.setMorePath(more_path);
                                    int iCurrent = i + 1;
                                    int total = arr.length();
                                    model.setCurrent(iCurrent);
                                    model.setTotal(total);
                                    modelArray.add(model);
                                }
                                brandPager = layout.findViewById(R.id.pager);
                                brandPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    }

                                    @Override
                                    public void onPageSelected(int position) {

                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });
                                brandAdapter = new OCBBrandAdapter(mContext, screenWidth, rake);
                                brandPager.setAdapter(brandAdapter);
                                brandAdapter.setItems(modelArray);
                                setRake("/keyboard/brandad", "");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (!result && obj != null) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            String error = object.optString(Common.NETWORK_ERROR);
                            String dError = object.optString(Common.NETWORK_DISCONNECT);
                            if (!TextUtils.isEmpty(error)) {
                                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                            } else {
                                if (!TextUtils.isEmpty(dError)) {
                                    Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
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

    private void initShoppingTab(RelativeLayout layout) {
        RelativeLayout first_tab = (RelativeLayout) layout.findViewById(R.id.shopping_first_tab);
//        LinearLayout indicator = (LinearLayout) layout.findViewById(R.id.indicator);
        RelativeLayout botBg = (RelativeLayout) layout.findViewById(R.id.shopping_bottom_navigator);
        ImageView goKeyboardIcon = (ImageView) layout.findViewById(R.id.shopping_first_img);
        TextView sec_line = (TextView) layout.findViewById(R.id.line);
        TextView strGoShopping = (TextView) layout.findViewById(R.id.strGoShopping);
        RelativeLayout shoppingSecondTab = (RelativeLayout) layout.findViewById(R.id.shopping_second_tab);
        RecyclerView shoppingRecyclerView = layout.findViewById(R.id.shoppingRecyclerView);
        searchScroll = layout.findViewById(R.id.searchScroll);
        searchNoData = layout.findViewById(R.id.searchNoData);
        sc_shopping_result = layout.findViewById(R.id.sc_shopping_result);
        String botColor = mThemeModel.getBotTabColor();
        String strGoKeyboard = mThemeModel.getKeyboardBot();
        String lineColor = mThemeModel.getBotLine();
        String strColor = mThemeModel.getKeyText();
        int iBotColor = Color.parseColor(lineColor);
        int iBotTabColor = Color.parseColor(botColor);
        int botStrColor = Color.parseColor(strColor);
        /**
         botBg.setBackgroundColor(iBotTabColor);
         Drawable drGoKeyboard = ThemeManager.GetDrawableFromPath(strGoKeyboard);
         goKeyboardIcon.setBackgroundDrawable(drGoKeyboard);
         sec_line.setBackgroundColor(iBotColor);
         strGoShopping.setTextColor(botStrColor);**/

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(RecyclerView.VERTICAL);
        shoppingRecyclerView.setLayoutManager(manager);
        adapter = new OCBShoppingAdapter(mContext, rake);
        shoppingRecyclerView.setAdapter(adapter);
        shoppingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                setRake("/keyboard/search/results", "scroll.searchresult");
            }
        });
        first_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRake(Common.PAGE_ID_KEYBOARD, "bottom_tap.keyboard");
                selectKeyboard(GUBUN_KEYBOARD);
            }
        });
        shoppingSecondTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력한 검색 키워드의 통합 검색 결과 화면으로 이동, 결과화면은 어디??
                try {
                    setRake("/keyboard/search/results", "bottom_tap.viewmore");
                    String encodedSearchWord = URLEncoder.encode(editSearch.getText().toString(), "UTF-8");
                    String url = "ocbt://com.skmc.okcashbag.home_google/searchMain?keyword=" + encodedSearchWord;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initOlabangTab(RelativeLayout layout) {
        RelativeLayout shopping_layer = layout.findViewById(R.id.shopping_layer);

        shopping_recyclerview = layout.findViewById(R.id.shopping_recyclerview);
        tab_homeshopping = layout.findViewById(R.id.tab_homeshopping);
        tab_domestic = layout.findViewById(R.id.tab_domestic);
        tab_oversea = layout.findViewById(R.id.tab_oversea);
        tab_travel = layout.findViewById(R.id.tab_travel);
        tab_book = layout.findViewById(R.id.tab_book);

        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        shopping_recyclerview.setLayoutManager(manager);
        saveShoppingAdapter = new OCBSaveShoppingAdapter(mContext, Common.getDisplayWidth(mContext), new OCBSaveShoppingAdapter.Listener() {
            @Override
            public void onItemClicked(ShoppingData model) {
                try {
                    String url = model.getLinkUrl3();
                    if (!TextUtils.isEmpty(url)) {
                        sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_SAVE_SHOPPING);
                        LogPrint.d("Uri.parse(url) :: " + Uri.parse(url));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        mSoftKeyboard.hide();
                        setRake("/keyboard/shopping", "tap.shoppingbrand");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        shopping_recyclerview.setAdapter(saveShoppingAdapter);

        tab_homeshopping.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedShoppingTab = ID_HOMESHOPPING;
                getShoppingList(selectedShoppingTab);
            }
        });

        tab_domestic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedShoppingTab = ID_DOMESTIC;
                getShoppingList(selectedShoppingTab);
            }
        });

        tab_oversea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedShoppingTab = ID_OVERSEA;
                getShoppingList(selectedShoppingTab);
            }
        });

        tab_travel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedShoppingTab = ID_TRAVEL;
                getShoppingList(selectedShoppingTab);
            }
        });

        tab_book.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedShoppingTab = ID_BOOK;
                getShoppingList(selectedShoppingTab);
            }
        });

        selectedShoppingTab = ID_HOMESHOPPING;
        getShoppingList(selectedShoppingTab);

        RelativeLayout first_tab = (RelativeLayout) layout.findViewById(R.id.olabang_first_tab);
        RelativeLayout botBg = (RelativeLayout) layout.findViewById(R.id.olabang_bottom_navigator);
        ImageView goKeyboardIcon = (ImageView) layout.findViewById(R.id.olabang_first_img);
        TextView sec_line = (TextView) layout.findViewById(R.id.line);
        TextView strGoOlabang = (TextView) layout.findViewById(R.id.strGoOlabang);
        RelativeLayout olabangSecondTab = (RelativeLayout) layout.findViewById(R.id.olabang_second_tab);
        View topBg = layout.findViewById(R.id.top_bg);

        String botColor = mThemeModel.getBotTabColor();
        String strGoKeyboard = mThemeModel.getKeyboardBot();
        String lineColor = mThemeModel.getBotLine();
        String strColor = mThemeModel.getKeyText();
        int iBotColor = Color.parseColor(lineColor);
        int iBotTabColor = Color.parseColor(botColor);
        int botStrColor = Color.parseColor(strColor);

        first_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabImages(KEYBOARD);
                selectKeyboard(GUBUN_KEYBOARD);
                setRake(Common.PAGE_ID_KEYBOARD, "bottom_tap.keyboard");
            }
        });


        // 오라방
        RelativeLayout olabang_layer = layout.findViewById(R.id.olabang_layer);
        RelativeLayout olabang_close = layout.findViewById(R.id.olabang_close);
        ImageView olabangImage = layout.findViewById(R.id.olabangImage);
        TextView saleImage = layout.findViewById(R.id.saleImage);
        TextView point = layout.findViewById(R.id.point);
        TextView time = layout.findViewById(R.id.time);
        TextView title = layout.findViewById(R.id.title);
        olabangSecondTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=쇼핑적립&url=https://webview.okcashbag.com/v1.0/shopping/newlist.html";
                    if (olabang_layer.getVisibility() == View.VISIBLE) {
                        setRake("/keyboard/olive", "bottom_tap.viewmore");
                        //url = "ocbt://com.skmc.okcashbag.home_google/detail/event?url=https%3A%2F%2Fwebview.okcashbag.com%2Fv1.0%2Folabang%2Findex.html&title=%EC%98%A4!%EB%9D%BC%EB%B0%A9";
                        // 신규 url로 대체, 2022.05.25
                        //url = "ocbt://com.skmc.okcashbag.home_google/detail/shopping?url=https://www.livecm.osara.co.kr/v2/shopping";
                        // 오라방의 json data 값으로 대체 2022.05.26
                        url = getOlabangDirectLink();
                        LogPrint.d("olabang direct link :: " + url);
                    } else if (shopping_layer.getVisibility() == View.VISIBLE) {
                        setRake("/keyboard/shopping", "bottom_tap.viewmore");
                    }

                    /**if ( olabang_layer.getVisibility() == View.VISIBLE )
                     url = o_linkUrl;**/
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /**try {
                 String url = o_linkUrl;
                 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 mContext.startActivity(intent);
                 } catch (Exception e) {
                 e.printStackTrace();
                 }**/
            }
        });

        // 현재 시간에 떠야하는 오라방 아이템이 있을 경우
        if (olabangItem != null) {
            setRake(Common.PAGE_ID_KEYBOARD, "top_tap.ohlive");
            setRake("/keyboard/olive", "");
            strGoOlabang.setText("오!라방 바로가기");
            topBg.setBackgroundColor(iBotTabColor);
            olabang_layer.setVisibility(View.VISIBLE);
            shopping_layer.setVisibility(View.GONE);
            olabang_layer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (olabangItem != null) {
                            setRake("/keyboard/olive", "tap.olivefeed");
                            String url = olabangItem.getLinkUrl();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            olabang_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventId = olabangItem.getEventId();
                    String sObject = SharedPreference.getString(mContext, Key.KEY_OCB_OLABANG_DATA);
                    try {
                        JSONObject object = new JSONObject(sObject);
                        if (object != null) {
                            JSONObject streamingsObj = object.optJSONObject("streamings");
                            JSONArray liveBoardsArr = streamingsObj.optJSONArray("liveBoards");
                            if (liveBoardsArr != null && liveBoardsArr.length() > 0) {
                                boolean isChanged = false;
                                for (int i = 0; i < liveBoardsArr.length(); i++) {
                                    JSONObject liveObj = liveBoardsArr.optJSONObject(i);
                                    String id = liveObj.optString("eventId");
                                    if (id.equals(eventId)) {
                                        isChanged = true;
                                        liveObj.put("isDel", true);
                                        liveBoardsArr.remove(i);
                                        liveBoardsArr.put(i, liveObj);
                                        break;
                                    }
                                }
                                if (isChanged) {
                                    setRake("/keyboard/olive", "tap.closebtn");
                                    streamingsObj.remove("liveBoards");
                                    streamingsObj.put("liveBoards", liveBoardsArr);
                                    object.remove("streamings");
                                    object.put("streamings", streamingsObj);
                                    SharedPreference.setString(mContext, Key.KEY_OCB_OLABANG_DATA, object.toString());
                                    topBg.setBackgroundColor(Color.parseColor("#ffffff"));
                                    olabang_layer.setVisibility(View.GONE);
                                    shopping_layer.setVisibility(View.VISIBLE);

                                    olabangItem = null;
                                    mImgFirst.setBackgroundDrawable(tabEmoji);
                                    mImgTimedeal.setBackgroundDrawable(tabShoppingOn);
                                    mImgShopping.setBackgroundDrawable(tabOCBSearch);
                                    mImgFourth.setBackgroundDrawable(tabMy);
                                    mImgMore.setBackgroundDrawable(tabMore);
                                    LogPrint.d("skkim chat olabang close");
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ImageLoader.with(mContext).from(olabangItem.getImageUrl()).transform(ImageUtils.cropCenter()).load(olabangImage);
            GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.aikbd_olabang_icon_bg);
            olabangImage.setBackground(drawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                olabangImage.setClipToOutline(true);

            title.setText(olabangItem.getTitle());

            if (TextUtils.isEmpty(olabangItem.getBenefitText())) {
                saleImage.setVisibility(View.GONE);
                saleImage.setText("");
            } else {
                String benefitText = olabangItem.getBenefitText();
                if (benefitText.endsWith("%") || benefitText.replaceAll(" ", "").equals("무료배송") || benefitText.equals("1+1") || benefitText.equals("최저가")) {
                    if (benefitText.endsWith("%")) {
                        String str = benefitText.replaceAll("%", "");
                        benefitText = str + "\n%";
                    } else if (benefitText.replaceAll(" ", "").equals("무료배송")) {
                        benefitText = "무료\n배송";
                    }
                    saleImage.setText(benefitText);
                    saleImage.setVisibility(View.VISIBLE);
                } else {
                    saleImage.setVisibility(View.GONE);
                    saleImage.setText("");
                }
            }

            long currentTime = System.currentTimeMillis();
            long st_time = olabangItem.getLiveEndDate() - currentTime;
            CountDownTimer countDownTimer = new CountDownTimer(st_time, 1000) {
                public void onTick(long millisUntilFinished) {
                    int hour = (int) (millisUntilFinished / ONE_HOUR);

                    long forMin = millisUntilFinished - (hour * ONE_HOUR);
                    int minute = (int) (forMin / ONE_MIN);

                    long forSec = millisUntilFinished - (hour * ONE_HOUR) - (minute * ONE_MIN);
                    int sec = (int) (forSec / ONE_SEC);
                    String sHour = "";
                    String sMin = "";
                    String sSec = "";
                    if (hour >= 10) sHour = "" + hour;
                    else sHour = "0" + hour;

                    if (minute >= 10) sMin = "" + minute;
                    else sMin = "0" + minute;

                    if (sec >= 10) sSec = "" + sec;
                    else sSec = "0" + sec;

                    time.setText(sHour + ":" + sMin + ":" + sSec);
                }

                public void onFinish() {

                }
            };
            countDownTimer.start();
        } else { // 오라방이 없어 타임딜이 떠야할 경우
            setRake(Common.PAGE_ID_KEYBOARD, "top_tap.shopping");
            setRake("/keyboard/shopping", "");
            strGoOlabang.setText("쇼핑적립 바로가기");
            topBg.setBackgroundColor(Color.parseColor("#ffffff"));
            olabang_layer.setVisibility(View.GONE);
            shopping_layer.setVisibility(View.VISIBLE);
        }
    }

    private void setUtilBG(final RelativeLayout layout) {
        Drawable bg = ThemeManager.GetDrawableFromPath(mThemeModel.getBgImg()); // 배경이미지, 나인페치 미적용 2017.12.04
        layout.setBackgroundDrawable(bg);
    }

    public void setKeyboardBackground() {
        if (mThemeModel == null)
            return;
        String ImageUrl = mThemeModel.getBgImg();
        File file = new File(ImageUrl);
//        NinePatchDrawable bg = ThemeManager.GetNinePatch(mContext, mThemeModel.getBgImg()); // 배경이미지
        Drawable bg = ThemeManager.GetDrawableFromPath(mThemeModel.getBgImg()); // 배경이미지, 나인페치 미적용 2017.12.04
//        Drawable bg = ThemeManager.GetNinePatch1(mContext, mThemeModel.getBgImg()); // 배경이미지
        mKeyboardLayer.setBackgroundDrawable(bg);
        mEmojiLayer.setBackgroundDrawable(bg);
        mEmoticonLayer.setBackgroundDrawable(bg);

        int alpha = mThemeModel.getBgAlpha();
        if (mKeyboardLayer.getBackground() != null)
            mKeyboardLayer.getBackground().setAlpha(alpha);

        if (mEmojiLayer.getBackground() != null)
            mEmojiLayer.getBackground().setAlpha(alpha);
        if (mEmoticonLayer.getBackground() != null)
            mEmoticonLayer.getBackground().setAlpha(alpha);
    }

    public void setEmojiPage(int index) {
        viewPager.setCurrentItem(index);
    }

    public void setEmoticonPage(int index) {
        emoticonViewPager.setCurrentItem(index);
    }

    public void selectKeyboard(int gubun) {
        KeyboardLogPrint.e("selectKeyboard MainKeyboardView gubun :: " + gubun);
        try {
            if (gubun == GUBUN_KEYBOARD) {
                mTabPosition = KEYBOARD;
                mKeyboardLayer.setVisibility(View.VISIBLE);
                mEmojiLayer.setVisibility(View.GONE);
                mEmoticonLayer.setVisibility(View.GONE);
                mUtilLayer.setVisibility(View.GONE);
                mUtilLayer.removeAllViews();
                setTabImages(KEYBOARD);
            } else if (gubun == GUBUN_EMOJI) {
                setRake("/keyboard/emoticon", "");
                mTabPosition = TAB_EMOJI;

                if (emojiPagerAdapter.getItemsSize() == 0) {
                    if ( SoftKeyboard.isKeyboardShow ) {
                        emojiPagerAdapter.setItems(mContext);
                    }
                }

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                    LogPrint.d("emoji scroll x 0");
//                    viewPager.setScrollX(0);
//                }
                emojiPageHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if ( SoftKeyboard.isKeyboardShow ) {
                            int recentCount = 0;
                            RecentAdapter adapter = emojiPagerAdapter.getRecentAdapter();
                            if (adapter != null)
                                recentCount = adapter.setupRecentDataFromList(false);
                            LogPrint.d("emoji recentCount :: " + recentCount);
                            final int currIndex = recentCount > 0 ? 0 : 1;
                            LogPrint.d("emoji set current item :: " + currIndex);
                            viewPager.setCurrentItem(currIndex);
                            mKeyboardLayer.setVisibility(View.GONE);
                            mEmojiLayer.setVisibility(View.VISIBLE);
                            mEmoticonLayer.setVisibility(View.GONE);
                            mUtilLayer.setVisibility(View.GONE);
                            mUtilLayer.removeAllViews();
                        }
                    }
                }, 100);


            } else if (gubun == GUBUN_EMOTICON) {
                mTabPosition = TAB_EMOJI;
                viewPager.setScrollX(0);

                if (hasRecentDataFromList()) {
                    LogPrint.d("recent data exist");
                    viewPager.setCurrentItem(0);
                } else {
                    LogPrint.d("recent data not exist");
                    viewPager.setCurrentItem(1);
                    RecentAdapter adapter = emojiPagerAdapter.getRecentAdapter();
                    if (adapter != null)
                        adapter.setupRecentDataFromList(false);
                }

                mKeyboardLayer.setVisibility(View.GONE);
                mEmojiLayer.setVisibility(View.GONE);
                mEmoticonLayer.setVisibility(View.VISIBLE);
                mUtilLayer.setVisibility(View.GONE);
                mUtilLayer.removeAllViews();

            } else {
                mKeyboardLayer.setVisibility(View.GONE);
                mEmojiLayer.setVisibility(View.GONE);
                mEmoticonLayer.setVisibility(View.GONE);
                mUtilLayer.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int width;
    private int height;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    public void showKeyboard() {
        if (mContainer != null) {
            KeyboardLogPrint.e("showKeyboard called");
            InputMethodManager im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.showSoftInput(mContainer, InputMethodManager.SHOW_FORCED);
        }
    }

    public void setPreviewEnabled(boolean setValue, boolean enabled) {
        if (mKeyboardView != null) {
            if (setValue)
                mKeyboardView.setPreviewEnabled(enabled);
            else
                mKeyboardView.setPreviewEnabled(false);
        }
    }

    public View getView() {
        return layout;
    }

    public void setKeyboard(Keyboard keyboard) {
        if ( mKeyboardView != null && keyboard != null ) {
            int level = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL);
            mKeyboardView.setKeyboard(keyboard);
            setKeyboardBackground();
            mKeyboardView.setKeys();
            mKeyboardView.changeConfig(level);
        }
    }

    public Keyboard getKeyboard() {
        return mKeyboardView.getKeyboard();
    }

    public void closing() {
        LogPrint.d("skkimmm keyboardview closing");
        mKeyboardView.closing();
    }

    public boolean handleBack() {
        return mKeyboardView.handleBack();
    }

    public boolean isShifted() {
        if (mKeyboardView != null)
            KeyboardLogPrint.e("mainkeyboardview isShifted :: " + mKeyboardView.isShifted());
        return mKeyboardView.isShifted();
    }

    public boolean setShifted(boolean shifted, int position) { //키보드의 shift 값 setting
        KeyboardLogPrint.e("skkimmm shift mainkeyboardview setShifted :: " + shifted + " , position :: " + position);
        mKeyboardView.redrawKeys();
        return mKeyboardView.setShifted(shifted);
    }

    public KeyboardView getKeyboardView() {
        return mKeyboardView;
    }

    public void initEmojiArray() {
        if (mMatchedEmoji != null && mImgFirst != null) {
            KeyboardLogPrint.w("visible setting initEmojiArray matchedEmoji gone, img first visible");
            mImgFirst.setVisibility(View.VISIBLE);
            mMatchedEmoji.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    @Override
    public void onPress(int primaryCode) {
        KeyboardLogPrint.w("MainKeyboardView onPress");
        soundAndVibrate();
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        KeyboardLogPrint.w("MainKeyboardView onKey");

        int index = -1;
        if (primaryCode == -2 || primaryCode == -5 || primaryCode == -6) {
            if (primaryCode == -2 || primaryCode == -6) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    viewPager.setScrollX(0);
                KeyboardLogPrint.w("visible setting onKey current page 1");
                viewPager.setCurrentItem(1);


            }

            if (mEClickCallbackListener != null) {
                mEClickCallbackListener.onReceive(primaryCode);
            }
            // emoji
        } else if (primaryCode <= -992 && primaryCode >= -1000) {
            LogPrint.d("emoji primaryCode :: " + primaryCode);
            switch (primaryCode) {
                case -992:
                    index = 0;
                    break;
                case -993:
                    index = 1;
                    break;
                case -994:
                    index = 2;
                    break;
                case -995:
                    index = 3;
                    break;
                case -996:
                    index = 4;
                    break;
                case -997:
                    index = 5;
                    break;
                case -998:
                    index = 6;
                    break;
                case -999:
                    index = 7;
                    break;
                case -1000:
                    index = 8;
                    break;
            }
            setEmojiPage(index);

            // emoticon
        } else if (primaryCode <= -1001 && primaryCode >= -1007) {
            switch (primaryCode) {
                case -1001:
                    index = 0;
                    break;
                case -1002:
                    index = 1;
                    break;
                case -1003:
                    index = 2;
                    break;
                case -1004:
                    index = 3;
                    break;
                case -1005:
                    index = 4;
                    break;
                case -1006:
                    index = 5;
                    break;
                case -1007:
                    index = 6;
                    break;
                case -1008:
                    index = 7;
                    break;
            }
            setEmoticonPage(index);
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public interface BannerAdCallbackListener {
        void onBannerCallResult(boolean result);
    }

    public interface OnClickCallbackListener {
        public void onReceive(String matchStr, String emoji);
    }

    public interface OnEClickCallbackListener {
        public void onReceive(int primaryKey);
    }

    public void initAd() {
        initAdInfo();
    }

    public static void webOpen(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            KeyboardLogPrint.e("webOpen");
            if (!"".equals(findAppIsExist(context, "com.facebook.katana"))) {
                intent.setPackage("com.facebook.katana");
                KeyboardLogPrint.e("webOpen facebook start");
                context.startActivity(intent);
            } else if (!"".equals(findAppIsExist(context, "com.android.browser"))) {
                intent.setPackage("com.android.browser");
                KeyboardLogPrint.e("webOpen browser start");
                context.startActivity(intent);
            } else if (!"".equals(findAppIsExist(context, "com.sec.android.app.sbrowser"))) {
                intent.setPackage("com.sec.android.app.sbrowser");
                KeyboardLogPrint.e("webOpen sbrowser start");
                context.startActivity(intent);
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void websiteOpen(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!"".equals(findAppIsExist(mContext, "com.sec.android.app.sbrowser"))) {
                intent.setPackage("com.sec.android.app.sbrowser");
                mContext.startActivity(intent);
            } else if (!"".equals(findAppIsExist(mContext, "com.android.chrome"))) {
                intent.setPackage("com.android.chrome");
                mContext.startActivity(intent);
            } else if (!"".equals(findAppIsExist(mContext, "com.android.browser"))) {
                intent.setPackage("com.android.browser");
                mContext.startActivity(intent);
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse(url));
                mContext.startActivity(i);
            }
        } catch (ActivityNotFoundException e1) {
            e1.printStackTrace();
            KeyboardLogPrint.e("activity not found exception");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            KeyboardLogPrint.e("exception!!!!!!!!");
        }
    }

    public static void onWebSiteOpen(Context context, final String url, String packageName) {
        try {
            if (TextUtils.isEmpty(packageName))
                packageName = "com.android.browser";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (!"".equals(findAppIsExist(context, packageName))) {
                intent.setPackage(packageName);
                context.startActivity(intent);
            } else if (!"".equals(findAppIsExist(context, "com.android.browser"))) {
                intent.setPackage("com.android.browser");
                context.startActivity(intent);
            } else if (!"".equals(findAppIsExist(context, "com.sec.android.app.sbrowser"))) {
                intent.setPackage("com.sec.android.app.sbrowser");
                context.startActivity(intent);
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } catch (ActivityNotFoundException e1) {
            e1.printStackTrace();
            KeyboardLogPrint.e("activity not found exception");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            KeyboardLogPrint.e("exception!!!!!!!!");
        }
    }

    public static String findAppIsExist(Context context, String findAppName) {
        String resultApp = "";

        PackageManager packageManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appsList = packageManager.queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < appsList.size(); i++) {
            if (appsList.get(i).activityInfo.packageName.indexOf(findAppName) != -1) {
                resultApp = appsList.get(i).activityInfo.packageName;
            }
        }

        return resultApp;
    }

    private void initSound(Context context) {
        int selected_sound = SharedPreference.getInt(context, Common.PREF_SELECTED_SOUND) < 0 ? 0 : SharedPreference.getInt(context, Common.PREF_SELECTED_SOUND);
        int resId;
        if (selected_sound == SOUND_0)
            resId = R.raw.aikbd_sound0;
        else if (selected_sound == SOUND_1)
            resId = R.raw.aikbd_sound1;

        else if (selected_sound == SOUND_2)
            resId = R.raw.aikbd_sound2;
        else if (selected_sound == SOUND_3)
            resId = R.raw.aikbd_sound3;
        else if (selected_sound == SOUND_4)
            resId = R.raw.aikbd_sound4;
        else
            resId = R.raw.aikbd_sound0;
        try {
//            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSoundPool = new SoundPool.Builder()
                        .setMaxStreams(2)
                        .build();
            } else {
                mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BroadcastReceiver mSetChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            KeyboardLogPrint.e("MainKeyboardView action :: " + action);
            if (SoftKeyboard.SET_CHANGE.equals(action)) {
                mVolumeLevel = SharedPreference.getInt(context, Common.PREF_I_VOLUME_LEVEL);
                if (mVolumeLevel < 0) {
                    SharedPreference.setInt(context, Common.PREF_I_VOLUME_LEVEL, Common.DEFAULT_SOUND_LEVEL);
                    mVolumeLevel = Common.DEFAULT_SOUND_LEVEL;
                }
                mVibrateLevel = SharedPreference.getLong(context, Common.PREF_VIBRATE_LEVEL);
                KeyboardLogPrint.w("MainKeyboardView onReceive volume :: " + mVolumeLevel);
                KeyboardLogPrint.w("MainKeyboardView onReceive vibrate :: " + mVibrateLevel);
                setChangedKeyboardHeight();
            } else if (MyReceiver.VOLUME_CHANGE.equals(action)) {
            } else if ("THEME_CHANGE".equals(action)) {
                try {
                    AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
                    String str = helper.getTheme();
                    if (!TextUtils.isEmpty(str))
                        mThemeModel = ThemeManager.GetThemeModel(str, 11);
                    if (mThemeModel != null) {
                        try {
                            KeyboardLogPrint.e("MainKeyboard THEME_CHANGE");

                            NinePatchDrawable norNor = ThemeManager.GetNinePatch(mContext, mThemeModel.getNorBtnNorI()); // 일반키 normal
                            NinePatchDrawable norPre = ThemeManager.GetNinePatch(mContext, mThemeModel.getNorBtnPreI()); // 일반키 pressed
                            Drawable norBtnSelector = ThemeManager.GetImageSelector(norNor, norPre); // 일반키 selector

                            NinePatchDrawable speNor = ThemeManager.GetNinePatch(mContext, mThemeModel.getSpBtnNorI()); // 특수키 normal
                            NinePatchDrawable spePre = ThemeManager.GetNinePatch(mContext, mThemeModel.getSpBtnPreI()); // 특수키 pressed
                            Drawable spBtnSelector = ThemeManager.GetImageSelector(speNor, spePre); // 특수키 selector
//                            NinePatchDrawable bg = ThemeManager.GetNinePatch(mContext, mThemeModel.getBgImg()); // 배경이미지
                            int txtColor = Color.parseColor(mThemeModel.getKeyText()); // 키 텍스트 색상

                            KeyboardLogPrint.e("txtColor :: " + txtColor);

                            tabEmoji = ThemeManager.GetDrawableFromPath(mThemeModel.getTabEmoji());
                            tabEmojiOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabEmojiOn());
                            tabOlabang = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOlabang());
                            //tabShopping = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShopping());
                            String mUsedTheme = "theme_color_01";
                            String strRoot = mContext.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator;
                            File rootFile = new File(strRoot);
                            File[] fileList = rootFile.listFiles();
                            if (fileList.length > 0) {
                                for (int i = 0; i < fileList.length; i++) {
                                    File inFile = fileList[i];
                                    if (inFile.isDirectory()) {
                                        mUsedTheme = inFile.getName();
                                    }
                                }
                            }
                            LogPrint.d("mUsedTheme :: " + mUsedTheme);
//                            if ( "theme_color_01".equals(mUsedTheme) || "theme_118".equals(mUsedTheme) )
//                                tabChatGpt = getResources().getDrawable(R.drawable.aikbd_chat_gpt_normal);
//                            else
//                                tabChatGpt = getResources().getDrawable(R.drawable.aikbd_chat_gpt_theme);
                            if (!TextUtils.isEmpty(mThemeModel.getTabSaveShopping()))
                                tabShopping = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShopping());
                            else {
                                String targetPath = mThemeModel.getTabEmoji();
                                if (!TextUtils.isEmpty(targetPath)) {
                                    if (targetPath.contains("theme_color_01") || targetPath.contains("theme_118")) {
                                        tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_118);
                                    } else if (targetPath.contains("theme_119")) {
                                        tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_119);
                                    } else if (targetPath.contains("theme_120")) {
                                        tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_120);
                                    } else if (targetPath.contains("theme_115")) {
                                        tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_115);
                                    } else if (targetPath.contains("theme_121")) {
                                        tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_121);
                                    } else {
                                        tabShopping = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_118);
                                    }
                                }
                            }


                            tabMy = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMy());

                            tabMore = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMore());
                            tabOCBSearch = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOCBSearch());
                            tabMoreOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMoreOn());
                            tabMyOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMyOn());
                            tabOlabangOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOlabangOn());
                            tabOCBSearchOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOCBSearchOn());
                            if (!TextUtils.isEmpty(mThemeModel.getTabSaveShoppingOn()))
                                tabShoppingOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShoppingOn());
                            else {
                                String targetPath = mThemeModel.getTabEmoji();
                                if (!TextUtils.isEmpty(targetPath)) {
                                    if (targetPath.contains("theme_color_01") || targetPath.contains("theme_118")) {
                                        tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_118);
                                    } else if (targetPath.contains("theme_119")) {
                                        tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_119);
                                    } else if (targetPath.contains("theme_120")) {
                                        tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_120);
                                    } else if (targetPath.contains("theme_115")) {
                                        tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_115);
                                    } else if (targetPath.contains("theme_121")) {
                                        tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_121);
                                    } else {
                                        tabShoppingOn = mContext.getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_on_118);
                                    }
                                }
                            }
                            adDel = ThemeManager.GetDrawableFromPath(mThemeModel.getAdDel());

                            drBrand = ThemeManager.GetDrawableFromPath(mThemeModel.getBrandOff());
                            brandLink = DEFAULT_BRAND_LINK;
                            LogPrint.d("drbrand set 4");
                            BrandTabModel model = helper.getBrandUrl();
                            if (model != null && !TextUtils.isEmpty(model.getImage()) && !TextUtils.isEmpty(model.getLink())) {
                                File file = new File(model.getImage());
                                if (file.exists()) {
                                    LogPrint.d("drbrand set 5");
                                    Drawable br = ThemeManager.GetDrawableFromPath(model.getImage());
                                    if ( br != null ) {
                                        drBrand = br;
                                        brandLink = model.getLink();
                                    }
                                }
                            }
                            mCashDr = ThemeManager.GetDrawableFromPath(mThemeModel.getTabCash());
                            mZeroCashDr = ThemeManager.GetDrawableFromPath(mThemeModel.getTabZeroCash());
                            int tabNor = Color.parseColor(mThemeModel.getTabOff()); // 상단 tab off 색상
                            int tabPre = Color.parseColor(mThemeModel.getTabOn()); // 상단 tab on 색상
                            Drawable tabSelector_logo = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                            Drawable tabSelector_first = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                            Drawable tabSelector_second = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                            Drawable tabSelector_third = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                            Drawable tabSelector_fourth = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                            Drawable tabSelector_fifth = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                            Drawable tabSelector_more = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                            Drawable tabSelector_chat = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                            mFirstTabLayer.setBackgroundDrawable(tabSelector_first);
                            mTimedealTabLayer.setBackgroundDrawable(tabSelector_second);
                            mSecondTabLayer.setBackgroundDrawable(tabSelector_third);
                            mShoppingTabLayer.setBackgroundDrawable(tabSelector_fourth);
                            offerwall_tab.setBackgroundDrawable(tabSelector_fourth);
                            mFourthLayer.setBackgroundDrawable(tabSelector_fifth);
                            mMoreLayer.setBackgroundDrawable(tabSelector_more);
                            game_tab.setBackgroundDrawable(tabSelector_chat);
                            //mImgSecond.setImageResource(R.drawable.aikbd_btn_ad_icon_on);
                            //mImgSecond.setBackgroundDrawable(brandOn);
                            //setBrandImage(brandOn);
                            /**
                             mImgFirst.setBackgroundDrawable(tabEmoji);
                             mImgTimedeal.setBackgroundDrawable(tabShopping);
                             mImgShopping.setBackgroundDrawable(tabOCBSearch);
                             mImgFourth.setBackgroundDrawable(tabMy);
                             mImgMore.setBackgroundDrawable(tabMore);**/
                            setTabImages(KEYBOARD);
                            setBrandTabIcon();
                            String strTopLine = mThemeModel.getTopLine();
                            int iTopColor = Color.parseColor(strTopLine);
                            mTopLine.setBackgroundColor(iTopColor);
                            topLine.setBackgroundColor(iTopColor);

                            shoppingTopLayer.setBackgroundColor(tabNor);
                            //banner_ad_layer.setBackgroundColor(tabNor);

                            String strColor = mThemeModel.getKeyText();
                            int botStrColor = Color.parseColor(strColor);
                            //bannerTxt.setTextColor(botStrColor);
                            //bannerClose.setBackgroundDrawable(adDel);
                            imgShoppingClose.setBackgroundDrawable(adDel);
                            setKeyboardBackground();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

//                    if (mTxtPoint != null && !TextUtils.isEmpty(mTxtPoint.getText().toString()) && !"0".equals(mTxtPoint.getText().toString()))
//                        mImgFourth.setBackgroundDrawable(mCashDr);
////                        setBackgroundDrawable(mImgFourth, mCashDr);
//                    else
//                        mImgFourth.setBackgroundDrawable(mZeroCashDr);
////                        setBackgroundDrawable(mImgFourth, mZeroCashDr);
                    setBrandTabIcon();
                    int level = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL);
                    if ( mEKeyboardView != null ) {
                        mEKeyboardView.setKeys();
                        mEKeyboardView.changeConfig(level);
                    }
                    if ( mEmoticonKeyboardView != null ) {
                        mEmoticonKeyboardView.setKeys();
                        mEmoticonKeyboardView.changeConfig(level);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (POINT_CHARGE.equals(action)) {
                KeyboardLogPrint.e("POINT_CHARGE BROADCASTRECEIVER received");
//                setPoint();
            } else if ("SOUND_CHANGE".equals(action)) {
                if (intent != null) {
                    int sound = intent.getIntExtra("change_sound", 0);
                    SharedPreference.setInt(mContext, Common.PREF_SELECTED_SOUND, sound);
                    LogPrint.d("################# get mainkeyboardview sound change position ::: " + sound);
                }
                initSound(mContext);
            } else if (SET_KEYBOARD.equals(action)) {
                if (mUtilLayer != null)
                    mUtilLayer.removeAllViews();
                selectKeyboard(GUBUN_KEYBOARD);
            } else if (FINISH_WEBVIEW.equals(action)) {

            } else if (BACK_KEY_LISTENER.equals(action)) {
                if (mIconWindowManager != null && mIconView != null) {
                    KeyboardLogPrint.e("back_key_listener");
//                    Glide.get(mContext).clearMemory();
                    mIconWindowManager.removeViewImmediate(mIconView);
                    mIconWindowManager = null;
                    mIconView = null;
                    System.gc();
                }
            } else if (POP_POINT.equals(action)) {
                try {
                    boolean result = intent.getBooleanExtra("RESULT", false);
                    String message = intent.getStringExtra("MESSAGE");
                    String unique = intent.getStringExtra("UNIQUE");
                    String popPoint = intent.getStringExtra("POP_POINT");
                    if (!TextUtils.isEmpty(popPoint))
                        mPopPoint = popPoint;
                    KeyboardLogPrint.e("mPopPoint :: " + mPopPoint);
                    if (!result && mContext != null) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            else if ( SoftKeyboard.CHAT_GPT_RESUME.equals(action) ) {
//                if ( mImgChat != null ) {
//                    isChatGptResummed = true;
//                    LogPrint.d("skkim chat resume");
//                    Drawable d = getResources().getDrawable(R.drawable.aikbd_chat_gpt_theme);
//                    mImgChat.setBackgroundDrawable(d);
//                }
//            } else if ( SoftKeyboard.CHAT_GPT_PAUSE.equals(action) ) {
//                if ( mImgChat != null ) {
//                    LogPrint.d("skkim chat pause");
//                    isChatGptResummed = false;
//                    mImgChat.setBackgroundDrawable(tabChatGpt);
//                }
//            }
        }
    };

    private ArrayList<KeyboardADModel> getADModel() {
        KeyboardLogPrint.e("getADModel");
        KeyboardADModel mModel = null;
        String json = SharedPreference.getString(mContext, Common.PREF_NEW_AD_JSON);
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject obj = new JSONObject(json);
                if (obj != null) {
                    boolean result = obj.optBoolean("Result");
                    if (!result)
                        return null;
                    int adrate = obj.optInt("adRate");
                    KeyboardLogPrint.e("new adrate :: " + adrate);
                    SharedPreference.setInt(mContext, Common.PREF_AD_RATE, adrate);

                    ArrayList<KeyboardADModel> modelArray = new ArrayList<KeyboardADModel>();
                    String gubun = obj.optString("adGubun");
                    if ("EV".equals(gubun)) {
                        KeyboardADModel model = new KeyboardADModel();
                        String img = obj.optString("img");
                        String link = obj.optString("link");
                        String pkg = obj.optString("pkg", ""); // 브라우저 오픈 시킬때 연결할 앱 페키지명

                        KeyboardLogPrint.e(" pkg :: " + pkg);

                        model.setImg(img);
                        model.setLink(link);
                        model.setGubun(gubun);
                        model.setPkg(pkg);

                        modelArray.add(model);

                        return modelArray;
                    } else {
                        JSONArray adArray = obj.getJSONArray("ad");
                        if (adArray != null && adArray.length() > 0) {
                            for (int i = 0; i < adArray.length(); i++) {
                                JSONObject adObject = adArray.getJSONObject(i);
                                String title = adObject.optString("title");
                                String content = adObject.optString("content");
                                String price = adObject.optString("price");
                                String point = adObject.optString("point");
                                String logo = adObject.optString("logo");
                                String img = adObject.optString("img");
                                String link = adObject.optString("link");


                                KeyboardLogPrint.e("new ad title :: " + title);
                                KeyboardLogPrint.e("new ad content :: " + content);
                                KeyboardLogPrint.e("new ad price :: " + price);
                                KeyboardLogPrint.e("new ad point :: " + point);
                                KeyboardLogPrint.e("new ad logo :: " + logo);
                                KeyboardLogPrint.e("new ad img :: " + img);
                                KeyboardLogPrint.e("new ad link :: " + link);

                                KeyboardADModel model = new KeyboardADModel();
                                model.setTitle(title);
                                model.setContent(content);
                                model.setPrice(price);
                                model.setPoint(point);
                                model.setLogo(logo);
                                model.setImg(img);
                                model.setLink(link);
                                model.setGubun(gubun);
                                model.setPkg("");

                                modelArray.add(model);
                            }
                            return modelArray;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void initAdInfo() {
        KeyboardLogPrint.w("keyad initAdInfo");
        SharedPreference.setString(mContext, Common.PREF_AD_JSON, "");
//        mAdModels = null;
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            KeyboardLogPrint.w("onItemClick called");
            if (view.getId() == R.id.memo_grid) {
                KeyboardLogPrint.w("memo_grid clicked");
            }
        }
    };

    private void toggleBookMark() {
        mToggleBookmark = !mToggleBookmark;
    }

    public static float DpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static int DPFromPixel(int pixel, Context context) {
        float DEFAULT_HDIP_DENSITY_SCALE = 1.5f;
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pixel / DEFAULT_HDIP_DENSITY_SCALE * scale);
    }

    private void adjustKeyboardKeyHeight(LatinKeyboard keyboard, double newKeyHeight) {
        int height = 0;
        if ( keyboard != null ) {
            for (Keyboard.Key key : keyboard.getKeys()) {
                key.height *= newKeyHeight;
                KeyboardLogPrint.e("keyboardHeight MainKeyboardView key.height : " + key.height);
                key.y *= newKeyHeight;
                KeyboardLogPrint.e("keyboardHeight MainKeyboardView key.y : " + key.y);
                height = key.height;
                KeyboardLogPrint.e("keyboardHeight MainKeyboardView height : " + height);
            }
            LogPrint.d("khskkim keyboard_height 2 mainkeyboardview :: " + height);

            keyboard.setHeight(height);
        }
    }

    private void soundAndVibrate() {
        if (mVibrateLevel > 0) {
            mVibrator.vibrate(mVibrateLevel * Common.VIBRATE_MUL);
            KeyboardLogPrint.d("vibrate called");
        }
    }

    public void setADVisible(boolean visible, String gubun) {
        if (visible) {
            mADLayer.setVisibility(View.GONE);
            if ("KL".equals(gubun)) {
                mKLADLayer.setVisibility(View.VISIBLE);
                mSRADLayer.setVisibility(View.GONE);
                mEVLayer.setVisibility(View.GONE);
            } else if ("SR".equals(gubun)) {
                mKLADLayer.setVisibility(View.GONE);
                mSRADLayer.setVisibility(View.VISIBLE);
                mEVLayer.setVisibility(View.GONE);
            } else if ("EV".equals(gubun)) {
                mKLADLayer.setVisibility(View.GONE);
                mSRADLayer.setVisibility(View.GONE);
                mEVLayer.setVisibility(View.VISIBLE);
            } else {
                mKLADLayer.setVisibility(View.GONE);
                mSRADLayer.setVisibility(View.GONE);
                mEVLayer.setVisibility(View.GONE);
            }
        } else {
            mADLayer.setVisibility(View.GONE);
            mKLADLayer.setVisibility(View.GONE);
            mSRADLayer.setVisibility(View.GONE);
            mEVLayer.setVisibility(View.GONE);
        }
    }

    public void keyboardShow() {
        //setTalkbackOff();
        setRake(Common.PAGE_ID_KEYBOARD, "");

        if ( isAnimationStart() ) {
            AlphaAnimation anim = new AlphaAnimation(1.0f,0.0f);
            anim.setDuration(500);
            anim.setStartOffset(300);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(5);
            img_offerwall.startAnimation(anim);
//            ani=(AnimationDrawable)img_offerwall.getDrawable();
//            ani.setOneShot(true);
//            ani.start();
        } else {
            img_offerwall.setBackgroundResource(R.drawable.aikbd_btn_ppzon_icon);
        }

//        if ( mContext != null ) {
//            AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
//            if ( helper.isFirstExecuteExist() ) {
//
//            } else {
//
//            }
//        }

        /*
        if (mContext != null) {
            CustomAsyncTask task = new CustomAsyncTask(mContext);
            task.getTypingGameStatus(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if (result) {
                        try {
                            JSONObject object = (JSONObject) obj;
                            if (object != null) {
                                boolean rt = object.optBoolean("Result");
                                if (rt) {
                                    String status = object.optString("status");
                                    String t_url = object.optString("url");
                                    if (typingGameInfo == null)
                                        typingGameInfo = new TypingGameInfo();
                                    typingGameInfo.setStatus(status);
                                    typingGameInfo.setUrl(t_url);
                                    if ("Y".equals(typingGameInfo.getStatus())) {
                                    } else {
                                        typingGameInfo = new TypingGameInfo();
                                    }
                                    JSONObject pObject = object.optJSONObject("point");
                                    if (pObject != null) {
                                        String tPoint = pObject.optString("today_point");
                                        String spot_point = pObject.optString("spot_point");
                                        String sum_point = pObject.optString("sum_point");
                                        myInfo.setSum_point(sum_point);
                                        myInfo.setSpot_point(spot_point);
                                    }
                                } else {
                                    typingGameInfo = new TypingGameInfo();
                                }
                            } else {
                                typingGameInfo = new TypingGameInfo();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            typingGameInfo = new TypingGameInfo();
                        }
                    } else {
                        typingGameInfo = new TypingGameInfo();
                    }
                }
            });
        }*/
    }

    public void loadUtilRewardBanner(String mobon, String coupang, String moneytree, String news) {
        if ( utilRewardBannerView != null )
            utilRewardBannerView.destroyAd();
        utilRewardBannerView = null;

        if (utilBannerView != null)
            utilBannerView.destroyAd();
        utilBannerView = null;

        if ( util_reward_coupang_layer != null )
            util_reward_coupang_layer.setVisibility(View.GONE);
        if ( util_reward_coupang_layer != null )
            util_reward_joint_layer.setVisibility(View.GONE);

        if ( !TextUtils.isEmpty(mobon) && "on".equals(mobon) ) {
            initUtilRewardBannerView(coupang, moneytree, news);
            utilRewardBannerView.loadAd();
        } else if ( !TextUtils.isEmpty(mobon) && !"on".equals(mobon) ) {
            if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                LogPrint.d("util sep test 1 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                Random rand = new Random();
                int val = rand.nextInt(3);
                LogPrint.d("util Val :: " + val);
                if ( val == 0 ) {
                    initUtilCoupangAD();
                } else if ( val == 1 ) {
                    initUtilMoneyTreeAD();
                } else
                    initUtilRewardNews();
            } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(moneytree) && "on".equals(moneytree)) {
                LogPrint.d("util sep test 2 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                Random rand = new Random();
                int val = rand.nextInt(2);
                LogPrint.d("util Val1 :: " + val);
                if ( val == 0 ) {
                    initUtilCoupangAD();
                } else {
                    initUtilMoneyTreeAD();
                }
            } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                LogPrint.d("util sep test 3 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                Random rand = new Random();
                int val = rand.nextInt(2);
                LogPrint.d("util Val1 :: " + val);
                if ( val == 0 ) {
                    initUtilCoupangAD();
                } else {
                    initUtilRewardNews();
                }
            } else if ( !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                LogPrint.d("util sep test 4 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                Random rand = new Random();
                int val = rand.nextInt(2);
                LogPrint.d("util Val1 :: " + val);
                if ( val == 0 ) {
                    initUtilMoneyTreeAD();
                } else {
                    initUtilRewardNews();
                }
            } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !"on".equals(moneytree) && !"on".equals(news)) {
                LogPrint.d("util sep test 5 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                initUtilCoupangAD();
            } else if ( !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !"on".equals(news) && !"on".equals(coupang)) {
                LogPrint.d("util sep test 6 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                initUtilMoneyTreeAD();
            } else if ( !TextUtils.isEmpty(news) && "on".equals(news) && !"on".equals(coupang) && !"on".equals(moneytree)) {
                LogPrint.d("util sep test 7 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                initUtilRewardNews();
            } else {
                LogPrint.d("util sep test 8 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
            }
        } else {
            loadUtilBanner();
        }
    }

    public void loadUtilBanner() {
        if ( utilRewardBannerView != null )
            utilRewardBannerView.destroyAd();
        utilRewardBannerView = null;

        if (utilBannerView != null)
            utilBannerView.destroyAd();
        utilBannerView = null;

        initUtilBannerView();
        if ( utilBannerView != null )
            utilBannerView.loadAd();
    }

    // 2022.12.23 mobwith 광고로 모든 광고가 대체됨.
//    public void loadMediaBanner() {
//        if (mBannerView != null) {
//            mBannerView.onDestroy();
//        }
//        mBannerView = null;
//        initMediaBannerView();
//    }

    public void init() {
        isKeyboardPointClickPossible = true;
        isRewardPointClickPossible = true;
        if (offerwall_point_layer != null ) {
            offerwall_point_layer.setVisibility(View.GONE);
        }

        if (ocb_offerwall_point != null ) {
            ocb_offerwall_point.setText("");
        }
//        getOfferwallList(false);
    }

    /**
     * 모든 광고를 모비위드 광고로 대체
     */
    public void loadMobWithAdWithoutCheck() {
        mBrandBadge.setVisibility(View.GONE);

        LogPrint.d("mobwith ad test loadMobWithAdWithoutCheck");
        if ( mobwith_reward_point != null )
            mobwith_reward_point.setVisibility(View.GONE);

        if ( mobwith_non_reward_badge != null )
            mobwith_non_reward_badge.setVisibility(View.GONE);

        if (rewardBannerView != null)
            rewardBannerView.destroyAd();
        rewardBannerView = null;

        if ( mobwith_webview != null && mobwith_container != null ) {
            mobwith_container.removeAllViews();
            if (mobwith_webview.getParent() != null)
                ((ViewGroup) mobwith_webview.getParent()).removeView(mobwith_webview);
        }

        if ( mobwith_layer != null )
            mobwith_layer.setVisibility(View.GONE);

        if ( mixer_webview != null && mixer_container != null ) {
            mixer_container.removeAllViews();
            if (mixer_webview.getParent() != null)
                ((ViewGroup) mixer_webview.getParent()).removeView(mixer_webview);
        }

        if ( r_leftBg != null ) {
            r_leftBg.setBackgroundColor(Color.WHITE);
        }

        if ( r_rightBg != null ) {
            r_rightBg.setBackgroundColor(Color.WHITE);
        }

        if ( rest_layer != null )
            rest_layer.setVisibility(View.GONE);

        if (bannerView != null)
            bannerView.destroyAd();
        bannerView = null;
        if ( reward_coupang_layer != null )
            reward_coupang_layer.setVisibility(View.GONE);
        if ( reward_joint_layer != null )
            reward_joint_layer.setVisibility(View.GONE);
        if ( reward_joint_banner_layer != null )
            reward_joint_banner_layer.setVisibility(View.GONE);
        if ( coupang_dy_layer != null )
            coupang_dy_layer.setVisibility(View.GONE);

        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);

        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);

        initMobwithAD(false);
    }
    /**
     * 2022.12.23 모든 광고를 모비위드 광고로 대체
     */
    public void loadMobWithAd() {
        LogPrint.d("mobwith ad test loadMobWithAd");
        if ( mobwith_reward_point != null )
            mobwith_reward_point.setVisibility(View.GONE);

        if ( mobwith_non_reward_badge != null )
            mobwith_non_reward_badge.setVisibility(View.GONE);

        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.isNewRewardWithBrandPossible(Common.MOBWITH_RELEASE_KEYBOARD_ZONE, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if ( object != null ) {
                            boolean rt = object.optBoolean("reward");
                            boolean brand = object.optBoolean("brand");
                            LogPrint.d("brand reward :: isNewRewardWithBrandPossible reward :: " + rt + " , brand :: " + brand);
                            // brand badge 노출 유무
                            if ( brand ) {
                                int r_point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                mBrandBadge.setText(r_point + "P");
                                mBrandBadge.setVisibility(View.VISIBLE);
                            } else
                                mBrandBadge.setVisibility(View.GONE);

                            LogPrint.d("isNewRewardPossible reward possible rt :: " + rt);
                            if (rewardBannerView != null)
                                rewardBannerView.destroyAd();
                            rewardBannerView = null;

                            if ( mobwith_webview != null && mobwith_container != null ) {
                                mobwith_container.removeAllViews();
                                if (mobwith_webview.getParent() != null)
                                    ((ViewGroup) mobwith_webview.getParent()).removeView(mobwith_webview);
                            }

                            if ( mobwith_layer != null )
                                mobwith_layer.setVisibility(View.GONE);

                            if ( mixer_webview != null && mixer_container != null ) {
                                mixer_container.removeAllViews();
                                if (mixer_webview.getParent() != null)
                                    ((ViewGroup) mixer_webview.getParent()).removeView(mixer_webview);
                            }

                            if ( r_leftBg != null ) {
                                r_leftBg.setBackgroundColor(Color.WHITE);
                            }

                            if ( r_rightBg != null ) {
                                r_rightBg.setBackgroundColor(Color.WHITE);
                            }

                            if ( rest_layer != null )
                                rest_layer.setVisibility(View.GONE);

                            if (bannerView != null)
                                bannerView.destroyAd();
                            bannerView = null;
                            if ( reward_coupang_layer != null )
                                reward_coupang_layer.setVisibility(View.GONE);
                            if ( reward_joint_layer != null )
                                reward_joint_layer.setVisibility(View.GONE);
                            if ( reward_joint_banner_layer != null )
                                reward_joint_banner_layer.setVisibility(View.GONE);
                            if ( coupang_dy_layer != null )
                                coupang_dy_layer.setVisibility(View.GONE);

                            if ( criteo_layer != null )
                                criteo_layer.setVisibility(View.GONE);

                            if ( mixer_layer != null )
                                mixer_layer.setVisibility(View.GONE);
                            LogPrint.d("After rt :: " + rt);
                            if ( rt ) {
                                int r_point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                if ( r_point <= 0 )
                                    rt = false;
                            }

                            initMobwithAD(rt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBrandBadge.setVisibility(View.GONE);
                    }
                } else
                    mBrandBadge.setVisibility(View.GONE);
            }
        });
    }

//    public void loadUtilMobWithAd() {
//        LogPrint.d("loadMobWithAd");
//        CustomAsyncTask task = new CustomAsyncTask(mContext);
//        task.isNewRewardPossible(CustomAsyncTask.ZONE_ID_UTIL, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//            @Override
//            public void onResponse(boolean result, Object obj) {
//                if ( result ) {
//                    try {
//                        JSONObject object = (JSONObject) obj;
//                        if ( object != null ) {
//                            boolean rt = object.optBoolean("Result");
//
//                            if (rewardBannerView != null)
//                                rewardBannerView.destroyAd();
//                            rewardBannerView = null;
//
//                            if ( mobwith_webview != null && mobwith_container != null ) {
//                                mobwith_container.removeAllViews();
//                                if (mobwith_webview.getParent() != null)
//                                    ((ViewGroup) mobwith_webview.getParent()).removeView(mobwith_webview);
//                            }
//
//                            if ( mobwith_layer != null )
//                                mobwith_layer.setVisibility(View.GONE);
//
//                            if ( mixer_webview != null && mixer_container != null ) {
//                                mixer_container.removeAllViews();
//                                if (mixer_webview.getParent() != null)
//                                    ((ViewGroup) mixer_webview.getParent()).removeView(mixer_webview);
//                            }
//
//                            if ( r_leftBg != null ) {
//                                r_leftBg.setBackgroundColor(Color.WHITE);
//                            }
//
//                            if ( r_rightBg != null ) {
//                                r_rightBg.setBackgroundColor(Color.WHITE);
//                            }
//
//                            if ( rest_layer != null )
//                                rest_layer.setVisibility(View.GONE);
//
//                            if (bannerView != null)
//                                bannerView.destroyAd();
//                            bannerView = null;
//                            if ( reward_coupang_layer != null )
//                                reward_coupang_layer.setVisibility(View.GONE);
//                            if ( reward_joint_layer != null )
//                                reward_joint_layer.setVisibility(View.GONE);
//                            if ( reward_joint_banner_layer != null )
//                                reward_joint_banner_layer.setVisibility(View.GONE);
//                            if ( coupang_dy_layer != null )
//                                coupang_dy_layer.setVisibility(View.GONE);
//
//                            if ( criteo_layer != null )
//                                criteo_layer.setVisibility(View.GONE);
//
//                            if ( mixer_layer != null )
//                                mixer_layer.setVisibility(View.GONE);
//
//                            initMobwithAD(rt);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    public void loadMixerBanner(int index) {
        LogPrint.d("keyboardad load mixer banner");
        if (rewardBannerView != null)
            rewardBannerView.destroyAd();
        rewardBannerView = null;

        if ( mixer_webview != null && mixer_container != null )
            mixer_container.removeView(mixer_webview);
        mixer_webview = new WebView(mContext);

        if ( r_leftBg != null ) {
            r_leftBg.setBackgroundColor(Color.WHITE);
        }

        if ( r_rightBg != null ) {
            r_rightBg.setBackgroundColor(Color.WHITE);
        }

        if ( rest_layer != null )
            rest_layer.setVisibility(View.GONE);

        if (bannerView != null)
            bannerView.destroyAd();
        bannerView = null;
        reward_coupang_layer.setVisibility(View.GONE);
        reward_joint_layer.setVisibility(View.GONE);
        reward_joint_banner_layer.setVisibility(View.GONE);
        coupang_dy_layer.setVisibility(View.GONE);

        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);

        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);
        else {
            if ( layout != null ) {
                mixer_webview = layout.findViewById(R.id.mixer_webview);
                mixer_layer.setVisibility(View.GONE);
            }
        }

        initMixerAD();
    }

    public void loadCoupangBanner() {
        KeyboardLogPrint.d("loadCoupangBanner called");
        if (bannerView != null)
            bannerView.destroyAd();
        bannerView = null;
        initBannerView(false);
        bannerView.loadCoupangAd();
    }

    public void loadNativeBanner() {
        if ( mMobonAdLayerC != null && mMobonRewardAdLayerC != null ) {
            mMobonAdLayerC.setVisibility(View.GONE);
            mMobonRewardAdLayerC.setVisibility(View.GONE);
        }

        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getNativeBannerInfo(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                boolean isBannerSuccess = false;
                if (result && obj != null) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            JSONArray arr = object.optJSONArray("items");
                            if (arr != null && arr.length() > 0) {
                                JSONObject in_obj = arr.optJSONObject(0);
                                if (in_obj != null) {
                                    String url = in_obj.optString("cUrl");
                                    String title = in_obj.optString("cText");
                                    isBannerSuccess = true;
                                    if ( bannerTxt != null ) {
                                        bannerTxt.setVisibility(View.VISIBLE);
                                        bannerTxt.setText(title);
                                    }
                                    if ( bannerImg != null )
                                        bannerImg.setVisibility(View.GONE);

                                    if ( banner_ad_layer != null ) {
                                        banner_ad_layer.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    setRake("/keyboard/bannerad", "tap.bannerad");
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mContext.startActivity(intent);
                                                    CustomAsyncTask task = new CustomAsyncTask(mContext);
                                                    task.postStats("banner_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                        @Override
                                                        public void onResponse(boolean result, Object obj) {

                                                        }
                                                    });
                                                    sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        banner_ad_layer.setVisibility(View.VISIBLE);
                                    }

                                    setRake("/keyboard/bannerad", "");
                                    CustomAsyncTask task = new CustomAsyncTask(mContext);
                                    task.postStats("banner_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                        @Override
                                        public void onResponse(boolean result, Object obj) {

                                        }
                                    });
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (mBannerAdCallbackListener != null) {
                    mBannerAdCallbackListener.onBannerCallResult(isBannerSuccess);
                }
            }
        });
    }

    public void loadCustomBanner(int index) {
        LogPrint.d("kksskk loadCustomBanner C GONE");
        mMobonAdLayerC.setVisibility(View.GONE);
        mMobonRewardAdLayerC.setVisibility(View.GONE);
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getBannerInfo("" + index, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                boolean isBannerSuccess = false;
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            boolean rt = object.optBoolean("Result");
                            if (rt) {
                                JSONObject bannerObject = object.optJSONObject("list_info");
                                if (bannerObject != null) {
                                    String title = bannerObject.optString("title");
                                    String url = bannerObject.optString("url");
                                    String type = null;
                                    try {
                                        type = bannerObject.optString("text_img_YN");
                                    } catch (Exception e) {

                                    }

                                    if (type != null && type.equalsIgnoreCase("N")) {
                                        try {
                                            String imgurl = bannerObject.optString("img_path");

                                            if (!TextUtils.isEmpty(imgurl) && !imgurl.startsWith("https")) {
                                                if (imgurl.startsWith("http")) {
                                                    imgurl = imgurl.replace("http", "https");
                                                }
                                            }
                                            if ( bannerTxt != null && bannerImg != null && !TextUtils.isEmpty(imgurl) ) {
                                                isBannerSuccess = true;
                                                bannerTxt.setVisibility(View.GONE);
                                                bannerImg.setVisibility(View.VISIBLE);
                                                ImageLoader.with(mContext).from(imgurl).load(bannerImg);
                                            }
                                        } catch (Exception e) {
                                            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(url) && bannerTxt != null && bannerImg != null) {
                                                isBannerSuccess = true;
                                                bannerTxt.setVisibility(View.VISIBLE);
                                                bannerImg.setVisibility(View.GONE);
                                                bannerTxt.setText(title);
                                            }
                                        }
                                    } else {
                                        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(url) && bannerTxt != null && bannerImg != null) {
                                            isBannerSuccess = true;
                                            bannerTxt.setVisibility(View.VISIBLE);
                                            bannerImg.setVisibility(View.GONE);
                                            bannerTxt.setText(title);
                                        }
                                    }

                                    banner_ad_layer.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                setRake(currentPageId, "tap.bannerad");
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mContext.startActivity(intent);
                                                CustomAsyncTask task = new CustomAsyncTask(mContext);
                                                task.postStats("banner_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                    @Override
                                                    public void onResponse(boolean result, Object obj) {

                                                    }
                                                });
                                                sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    banner_ad_layer.setVisibility(View.VISIBLE);
                                    setRake("/keyboard/bannerad", "");
                                    CustomAsyncTask task = new CustomAsyncTask(mContext);
                                    task.postStats("banner_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                        @Override
                                        public void onResponse(boolean result, Object obj) {

                                        }
                                    });
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (mBannerAdCallbackListener != null) {
                    mBannerAdCallbackListener.onBannerCallResult(isBannerSuccess);
                }
            }
        });
    }

    /*
    public void loadMediationBanner() {
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            JSONObject object = new JSONObject();
            object.put("name", "adfitsdk");
            object.put("unitid", "DAN-Vx0wT8ZfwKuX4Mxx");
            object.put("mediaKey", "");
            array.put(object);
            // 차후 노출 순서에 따라 array에 put 한다.
            //array.put(new JSONObject("{\"name\":\"admixersdk\",\"unitid\":\"26117793\",\"mediaKey\":\"19239320\"}"));
            //array.put(new JSONObject("{\"name\":\"criteo\",\"unitid\":\"111111\",\"mediaKey\":\"\"}"));
            obj.put("list", array);
            bannerView = null;
            initBannerView();
            if (bannerView != null) {
                bannerView.onDestroy();
                bannerView.loadMediationAd(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }*/
/*
    public void coupangBannerData(final CoupangAdPopup.AdVO advo, final Handler mHandler) {
        if (coupang_ad_layer == null) return;

        ImageLoader.with(mContext).from(advo.getImgUrl()).transform(ImageUtils.cropCenter()).load(coupangAdImage);
        String text = advo.getTitle();
        if (text != null && !text.isEmpty()) {
            text = text.replace("<b>", "<big><b>");
            text = text.replace("</b>", "</b></big>");
            coupangAdTitle.setText(Html.fromHtml(text));
        }

        coupang_ad_layer.findViewById(R.id.closeBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.setLong(mContext, Common.PREF_COUPANG_CLOSE_TIME, System.currentTimeMillis());
                mHandler.sendEmptyMessage(SoftKeyboard.HIDE_COUPANG_AD_DIALOG);
            }
        });

        coupang_ad_layer.findViewById(R.id.parentView).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advo != null) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = SoftKeyboard.OPEN_COUPANG_AD;
                    msg.obj = advo.getTargetUrl();
                    mHandler.sendMessage(msg);
                }
            }
        });
    }*/

    public void coupangBannerShowHide(boolean isShow) {
        if (coupang_ad_layer == null) return;
        if (isShow && coupang_ad_layer.getVisibility() == View.VISIBLE) return;
        if (!isShow && coupang_ad_layer.getVisibility() == View.GONE) return;
        coupang_ad_layer.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    public void goneBannerView() {
        banner_ad_layer.setVisibility(View.GONE);
    }

    public void goneCriteoADView() {
        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);
    }

    public void goneCoupangDYView() {
        coupang_dy_layer.setVisibility(View.GONE);
    }

    public void goneMixerLayer() {
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);
    }

    public void goneRewardBanner() {
        mMobonRewardAdLayerC.setVisibility(View.GONE);
    }

    public void goneADView() {
        //mMobonAdLayer.removeAllViews();
        LogPrint.d("kksskk goneADView C GONE");
        mMobonAdLayerC.setVisibility(View.GONE);
    }

    public void goneMobonADView() {
        mADLayer.setVisibility(View.GONE);
        //bannerView.setVisibility(View.GONE);
    }

    public void goneMobwithADView() {
        if ( mobwith_layer != null )
            mobwith_layer.setVisibility(View.GONE);
    }

    public void goneSearchLayout() {
        editSearch.setText("");
        shoppingTopLayer.setVisibility(View.GONE);
    }

    public void stopAllTimer() {
        if (adapter != null)
            adapter.stopTimer();
    }

    private ArrayList<BoardModel> mBoardList;
    private int mNoticeCount = 0;

    private void setBoardList(JSONObject dataItem) {
        try {
            BoardModel boardModel = new BoardModel();
            boardModel.setTitle(dataItem.optString("title"));
            boardModel.setContents(dataItem.optString("contents"));
            boardModel.setIcon_url(dataItem.optString("icon_url"));
            boardModel.setUrl(dataItem.optString("url"));
            boardModel.setBbs_tp_code(dataItem.optString("bbs_tp_code"));
            mBoardList.add(boardModel);
        } catch (NullPointerException e) {
            LogPrint.d("setNoticeArrayList e : " + e.getMessage());
        }
    }

    public void setFuncPointPossible(boolean possible) {
        KeyboardLogPrint.e("setFunctionPossible :: " + possible);
        mIsFuncPointPossible = possible;
    }

    public boolean getFuncPointPossible() {
        KeyboardLogPrint.e("getFuncPointPossible :: " + mIsFuncPointPossible);
        return mIsFuncPointPossible;
    }

    public void setKeyPointPossible(boolean possible) {
        mIsKeyPointPossible = possible;
    }

    public void setPoint() {
        // 키보드 기능에서 포인트 지급하지않도록 막음
        int m_p = 0;
        if (m_p == 0) {
            return;
        }

        if (isKeyboardApp())
            return;
        if (getFuncPointPossible()) {
            KeyboardLogPrint.e("포인트 지급해야함. db에 포인트값 1 올려 저장");
            PointDBHelper helper = new PointDBHelper(mContext);
            int point = helper.getPoint();
            if (point >= getLimitMax()) {
                KeyboardLogPrint.e("금일 포인트 추가 한도 초과 되어 더이상 포인트가 안쌓임");
                setFuncPointPossible(false);
                return;
            }

            point++;
            helper.deletePoint();
            helper.insertPoint(point);
            if (point > 0)
                mFourthLayer.setClickable(true);
            setFuncPointPossible(false);
            if (mTxtPoint != null)
                mTxtPoint.setText("" + point);

            if (mImgFourth != null) {
                if (point <= 0) {
//                    setBackgroundDrawable(mImgFourth, mZeroCashDr);
                    mImgFourth.setBackgroundDrawable(mZeroCashDr);
                } else {
                    mImgFourth.setBackgroundDrawable(mCashDr);
//                    setBackgroundDrawable(mImgFourth, mCashDr);
                }
            }
        } else {
            KeyboardLogPrint.e("이번 키보드에서는 포인트가 이미지급 되었으므로 포인트 올리지 않음");
        }
    }

    public void setLimitMax(int limit) {
        KeyboardLogPrint.e("about point setLimitMax :: limit :: " + limit);
        PointDBHelper helper = new PointDBHelper(mContext);
        helper.deleteMaxPoint();
        helper.insertMaxPoint(limit);
        mLimitMax = helper.getMaxPoint();
        KeyboardLogPrint.e("about point mLimitMax :: " + mLimitMax);
    }

    public int getLimitMax() {

        PointDBHelper helper = new PointDBHelper(mContext);
        mLimitMax = helper.getMaxPoint();

        return mLimitMax;
    }

    public static void SetDefaultTheme(final Context context) {
        KeyboardLogPrint.e("SetDefualtTheme");
        AIKBD_DBHelper helper = new AIKBD_DBHelper(context);
        if (!helper.isThemeExist()) {
            KeyboardLogPrint.e("SetDefualtTheme unzip progressed");
//            downloadAndUnzipContent();
            String rPath = context.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator;
            File rFile = new File(rPath);
            if (!rFile.exists())
                rFile.mkdirs();
            Decompress decompress = new Decompress();
            decompress.AssetUnZip(context, false, new Decompress.PostUnzip() {
                @Override
                public void unzipDone(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        KeyboardLogPrint.e("result :: " + result);
                        AIKBD_DBHelper helper = new AIKBD_DBHelper(context);
                        helper.deleteTheme();
                        helper.insertTheme(result);

                        ThemeModel themeModel = ThemeManager.GetThemeModel(result, 0);
                        double scale = ThemeManager.GetScale(context);
                        KeyboardLogPrint.e("scale :: " + scale);
                        if (scale != 1 && themeModel != null) {
                            KeyboardLogPrint.e("scale is not 1");
                            ThemeManager.ResizingSpImage(themeModel.getBackImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getSpaceImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEnterImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmojiImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmoticonImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmoticonRecent(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmoticonFirst(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmoticonSecond(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmoticonThird(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmoticonFourth(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmoticonFifth(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getEmoticonSixth(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getKeySymbol(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getKeyLang(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(themeModel.getShiftImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            if (!TextUtils.isEmpty(themeModel.getShiftImg1())) {
                                ThemeManager.ResizingSpImage(themeModel.getShiftImg1(), Bitmap.CompressFormat.PNG, 100, scale);
                            }
                            if (!TextUtils.isEmpty(themeModel.getShiftImg2())) {
                                ThemeManager.ResizingSpImage(themeModel.getShiftImg2(), Bitmap.CompressFormat.PNG, 100, scale);
                            }
                        } else {
                            KeyboardLogPrint.e("scale is 1");
                        }

                        if (themeModel != null) {
                            Intent themeUpdated = new Intent("THEME_CHANGE");
                            context.sendBroadcast(themeUpdated);
                        }
                    }
                }
            });
        }
    }

    private void setPagerIndicatorState(LinearLayout group, int size, int position) {
        if (group != null) {
            for (int i = 0; i < size; i++) {
                if (i == position) {
                    group.getChildAt(i).setSelected(true);
                } else {
                    group.getChildAt(i).setSelected(false);
                }
            }
        }
    }

    private void setPagerIndicator(LinearLayout group, int pageCount) {
        if (group.getChildCount() > 0) {
            group.removeAllViews();
        }
        for (int i = 0; i < pageCount; i++) {
            TextView dot = new TextView(mContext);
            dot.setBackgroundResource(R.drawable.aikbd_dot_selector);
            dot.setId(i);
            group.addView(dot);
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) dot.getLayoutParams();
            param.width = convertDpToPx(mContext, 6); // aikbd_dot_selector의 drawable 파일과 같이 사이즈 조정이 되어야 함.
            param.height = convertDpToPx(mContext, 6); // aikbd_dot_selector의 drawable 파일과 같이 사이즈 조정이 되어야 함.
            if (i != 0) {
                param.leftMargin = convertDpToPx(mContext, 6);
            }
            dot.setLayoutParams(param);
        }
    }

    public static int convertDpToPx(Context pContext, int pDp) {
        try {
            return ((int) (pDp * pContext.getResources().getDisplayMetrics().density));
        } catch (Exception e) {
            return 0;
        }
    }

    public void changeKeyHeight() {
        /*
        if (timeDealPager != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) timeDealPager.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            timeDealPager.setLayoutParams(layoutParams);
        }
         */
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        KeyboardLogPrint.e("MainKeyboardView event.getAction() :: " + event.getAction());
        KeyboardLogPrint.e("MainKeyboardView event.getKeyCode() :: " + event.getKeyCode());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    break;
                case KeyEvent.KEYCODE_MENU:
                    break;
                default:
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void setUnderLine(TextView view, String str) {
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
        view.setText(content);
    }

    public boolean isPointVisible() {
        boolean visible = false;
        if (mOCBPointLayer.getVisibility() == View.VISIBLE)
            visible = true;
        return visible;
    }

    private void setOCBPointLayer() {
        String sPoint;
        LogPrint.d("sPoint : " + mOCBPoint);
        if (mOCBPoint > 99) {
            sPoint = "99P";
            mPlusText.setVisibility(View.VISIBLE);
        } else {
            sPoint = mOCBPoint + "P";
            mPlusText.setVisibility(View.VISIBLE);
        }
        LogPrint.d("kkkssskkkkkk setOCBPointLayer sPoint :: " + sPoint);
        mOCBPointText.setText(sPoint);
        if (mOCBPoint > 0) {
            mOCBPointLayer.setVisibility(View.VISIBLE);
            mImgFourth.setVisibility(View.INVISIBLE);
        } else {
            mOCBPointLayer.setVisibility(View.GONE);
            mImgFourth.setVisibility(View.VISIBLE);
        }

        // for test
//        mOCBPointLayer.setVisibility(View.GONE);
//        mImgFourth.setVisibility(View.VISIBLE);

        /**
         if (mOCBPointLayer.getVisibility() != View.VISIBLE) {
         String sPoint;
         if (mOCBPoint > 99) {
         sPoint = "99P";
         mPlusText.setVisibility(View.VISIBLE);
         } else {
         sPoint = mOCBPoint + "P";
         mPlusText.setVisibility(View.GONE);
         }

         mOCBPointText.setText(sPoint);
         mOCBPointLayer.setVisibility(View.VISIBLE);
         }**/
    }

    public void setOCBPoint(int point) {
        if (point < 0)
            point = 0;
        mOCBPoint = point;
        LogPrint.d("kkkssskkkkkk setOCBPoint mOCBPoint :: " + mOCBPoint);
        setOCBPointLayer();
    }

    public void setOfferwallStatus(boolean isHybrid) {
        isHybridOfferwall = isHybrid;
    }

    public void setPointVisibie(int visibility) {
        if (visibility == View.VISIBLE)
            mImgFourth.setVisibility(View.INVISIBLE);
        else
            mImgFourth.setVisibility(View.VISIBLE);
        mOCBPointLayer.setVisibility(visibility);

        // for test
//        mImgFourth.setVisibility(View.VISIBLE);
//        mOCBPointLayer.setVisibility(View.GONE);
    }

    public String getOlabangDirectLink() {
        String strObj = SharedPreference.getString(mContext, Key.KEY_OCB_OLABANG_DATA);
        LogPrint.d("strObj :: " + strObj);
        String directLinkUrl = DEFAULT_OLABANG_DIRECT_LINK;
        try {
            JSONObject object = new JSONObject(strObj);
            JSONObject streamingsObj = object.optJSONObject("streamings");
            if (streamingsObj != null) {
                directLinkUrl = streamingsObj.optString("linkUrl");
            }
            return directLinkUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return DEFAULT_OLABANG_DIRECT_LINK;
        }
    }

    public OlabangItem getOlabangItem() {
        OlabangItem item = null;
        boolean canShow = false;
        String strObj = SharedPreference.getString(mContext, Key.KEY_OCB_OLABANG_DATA);
        LogPrint.d("strObj :: " + strObj);
        try {
            JSONObject object = new JSONObject(strObj);
            JSONObject streamingsObj = object.optJSONObject("streamings");
            if (streamingsObj != null) {
                JSONArray liveBoardsArr = streamingsObj.optJSONArray("liveBoards");
                if (liveBoardsArr != null && liveBoardsArr.length() > 0) {
                    long currentTime = System.currentTimeMillis();
                    for (int i = 0; i < liveBoardsArr.length(); i++) {
                        JSONObject liveObj = liveBoardsArr.optJSONObject(i);
                        long liveStart = liveObj.optLong("liveStartDate");
                        long liveEnd = liveObj.optLong("liveEndDate");
                        boolean isDel = liveObj.optBoolean("isDel");
                        if (!isDel) {
                            // 현재 라이브 중인 방송
                            boolean isStarted = liveStart <= currentTime;
                            boolean isNotEnded = currentTime < liveEnd;
                            if (liveStart <= currentTime && currentTime < liveEnd) {
                                // 방송의 live time이 1시간이 넘을 경우
                                if (isTimeOverOneHour(liveEnd, liveStart)) {
                                    // 방송 시작한지 1시간이 넘지 않았을 경우
                                    if (!isTimeOverOneHour(currentTime, liveStart)) {
                                        canShow = true;
                                    }
                                } else { // 현재 라이브 중인 방송이 1시간이 안되는 방송인 경우에는 무조건 노출
                                    canShow = true;
                                }
                                if (canShow) {
                                    item = new OlabangItem();
                                    String title = liveObj.optString("title");
                                    title = Html.fromHtml(title).toString();
                                    item.setEventId(liveObj.optString("eventId"));
                                    item.setTitle(title);
                                    item.setImageUrl(liveObj.optString("imageUrl"));
                                    item.setLinkUrl(liveObj.optString("linkUrl"));
                                    item.setStartDate(liveObj.optLong("startDate"));
                                    item.setEndDate(liveObj.optLong("endDate"));
                                    item.setEpisodeId(liveObj.optString("episodeId"));
                                    item.setPointText(liveObj.optString("pointText"));
                                    item.setBenefitText(liveObj.optString("benefitText"));
                                    item.setLiveType(liveObj.optString("liveType"));
                                    item.setSoldOut(liveObj.optBoolean("isSoldOut"));
                                    item.setLiveStartDate(liveObj.optLong("liveStartDate"));
                                    item.setLiveEndDate(liveObj.optLong("liveEndDate"));
                                    item.setSaleEndDate(liveObj.optLong("saleEndDate"));
                                    item.setCreateDate(liveObj.optLong("createDate"));
                                    item.setMaster(liveObj.optBoolean("isMaster"));
                                    item.setRemainTime(liveEnd - currentTime);
                                    break;
                                }
                            } else {

                            }
                        }
                    }
                }
            }
            olabangItem = item;
            if (mImgTimedeal != null) {
                if (item == null) {
                    mImgTimedeal.setBackgroundDrawable(tabShopping);
                } else {
                    mImgTimedeal.setBackgroundDrawable(tabOlabang);
                }
            }

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            olabangItem = null;
            if (mImgTimedeal != null)
                mImgTimedeal.setBackgroundDrawable(tabShopping);
            return null;
        }
    }

    private boolean isTimeOverOneHour(long end, long start) {
        boolean over = false;
        long oneHour = 60 * 60 * 1000;
        long duration = end - start;
        if (duration >= oneHour)
            over = true;
        return over;
    }

    public void setTabImages(int index) {
        stopAllTimer();
        boolean isTimedeal = true;
        if (olabangItem != null)
            isTimedeal = false;
        if (index == KEYBOARD) {
            mImgFirst.setBackgroundDrawable(tabEmoji);
            if (isTimedeal)
                mImgTimedeal.setBackgroundDrawable(tabShopping);
            else
                mImgTimedeal.setBackgroundDrawable(tabOlabang);
            mImgShopping.setBackgroundDrawable(tabOCBSearch);
            mImgFourth.setBackgroundDrawable(tabMy);
            mImgMore.setBackgroundDrawable(tabMore);
            //setBrandImage(brandOff);
        } else if (index == TAB_EMOJI) {
            mImgFirst.setBackgroundDrawable(tabEmojiOn);
            if (isTimedeal)
                mImgTimedeal.setBackgroundDrawable(tabShopping);
            else
                mImgTimedeal.setBackgroundDrawable(tabOlabang);
            mImgShopping.setBackgroundDrawable(tabOCBSearch);
            mImgFourth.setBackgroundDrawable(tabMy);
            mImgMore.setBackgroundDrawable(tabMore);
            //setBrandImage(brandOff);
        } else if (index == OLABANG) {
            mImgFirst.setBackgroundDrawable(tabEmoji);
            if (isTimedeal)
                mImgTimedeal.setBackgroundDrawable(tabShoppingOn);
            else
                mImgTimedeal.setBackgroundDrawable(tabOlabangOn);
            mImgShopping.setBackgroundDrawable(tabOCBSearch);
            mImgFourth.setBackgroundDrawable(tabMy);
            mImgMore.setBackgroundDrawable(tabMore);
            //setBrandImage(brandOff);
        } else if (index == SHOPPING) {
            mImgFirst.setBackgroundDrawable(tabEmoji);
            if (isTimedeal)
                mImgTimedeal.setBackgroundDrawable(tabShopping);
            else
                mImgTimedeal.setBackgroundDrawable(tabOlabang);
            mImgShopping.setBackgroundDrawable(tabOCBSearchOn);
            mImgFourth.setBackgroundDrawable(tabMy);
            mImgMore.setBackgroundDrawable(tabMore);
            //setBrandImage(brandOff);
        } else if (index == MY) {
            mImgFirst.setBackgroundDrawable(tabEmoji);
            if (isTimedeal)
                mImgTimedeal.setBackgroundDrawable(tabShopping);
            else
                mImgTimedeal.setBackgroundDrawable(tabOlabang);
            mImgShopping.setBackgroundDrawable(tabOCBSearch);
            mImgFourth.setBackgroundDrawable(tabMyOn);
            mImgMore.setBackgroundDrawable(tabMore);
            //setBrandImage(brandOff);
        } else if (index == BRAND_AD) {
            mImgFirst.setBackgroundDrawable(tabEmoji);
            if (isTimedeal)
                mImgTimedeal.setBackgroundDrawable(tabShopping);
            else
                mImgTimedeal.setBackgroundDrawable(tabOlabang);
            mImgShopping.setBackgroundDrawable(tabOCBSearch);
            mImgFourth.setBackgroundDrawable(tabMy);
            mImgMore.setBackgroundDrawable(tabMore);
            //setBrandImage(brandOn2);
        } else if (index == UTIL_COLLECTION) {
            mImgFirst.setBackgroundDrawable(tabEmoji);
            if (isTimedeal)
                mImgTimedeal.setBackgroundDrawable(tabShopping);
            else
                mImgTimedeal.setBackgroundDrawable(tabOlabang);
            mImgShopping.setBackgroundDrawable(tabOCBSearch);
            mImgFourth.setBackgroundDrawable(tabMy);
            mImgMore.setBackgroundDrawable(tabMoreOn);
            //setBrandImage(brandOff);
        } else {
            mImgFirst.setBackgroundDrawable(tabEmoji);
            if (isTimedeal)
                mImgTimedeal.setBackgroundDrawable(tabShopping);
            else
                mImgTimedeal.setBackgroundDrawable(tabOlabang);
            mImgShopping.setBackgroundDrawable(tabOCBSearch);
            mImgFourth.setBackgroundDrawable(tabMy);
            mImgMore.setBackgroundDrawable(tabMore);
            //setBrandImage(brandOff);
        }
        LogPrint.d("skkim chat setTabImage");
//        if ( !isChatGptResummed )
//            mImgChat.setBackgroundDrawable(tabChatGpt);
        setBrandImage(drBrand);
    }

    public void showSettingsAlert(final Context c) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅은 매체에서 구현필요.\n 매체구현단");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                c.startActivity(intent);
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void initUtilMobwithAD(boolean rewardPossible) {
        LogPrint.d("initUtilMobwithAD");
        if ( mContext == null )
            return;
        LogPrint.d("initUtilMobwithAD 1");
        isUtilMobWithHasError = false;
        util_mobwith_webview = null;
        util_mobwith_webview = new WebView(mContext);

        WebSettings settings = util_mobwith_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

        String path = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(path);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkLoads(false);
        settings.setAllowFileAccess(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// https 이미지.
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            util_mobwith_webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else
            util_mobwith_webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        util_mobwith_webview.setVerticalScrollBarEnabled(false);
        util_mobwith_webview.setDrawingCacheEnabled(true);

        util_mobwith_layer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        util_mobwith_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(mContext);
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        LogPrint.d("newWebView url :: " + url);
                        try {
                            int r_point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                            if ( rewardPossible && r_point > 0 ) {
                                sendNewRewardPoint(Common.MOBWITH_RELEASE_SETTING_ZONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
                            LogPrint.d("util mobwith click");
                            mContext.startActivity(browserIntent);

                            if(util_mobwith_webview != null) {
                                LogPrint.d("util mobwith click 1");
                                util_mobwith_webview.loadUrl("javascript:mixerClickFn();");
                            }
                            LogPrint.d("util isRewardPossible :: " + rewardPossible);

                        } catch (ActivityNotFoundException e) {
                            mContext.startActivity(Intent.createChooser(browserIntent, "Title"));
                        }

                        setRake("/keyboard/utill", "tap.utillbannerad");

                        if (url.contains("//img.mobon.net/ad/linfo.php"))
                            util_mobwith_webview.goBack();
                        return true;
                    }
                });
                return true;
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                LogPrint.d("util mobwith keyboardad console message :: " + message);
                if ((message.contains("Uncaught SyntaxError:") || message.contains("Uncaught ReferenceError:") || message.contains("AdapterFailCallback")) && !message.contains("wp_json")) {
                    LogPrint.d("util mobwith keyboardad contain error");
                    isUtilMobWithHasError = true;
                    if (util_mobwith_webview == null) {
                        LogPrint.d("util mobwith keyboardad null");
                        return;
                    } else {
                        LogPrint.d("util mobwith keyboardad not null");
                        util_mobwith_webview.onPause();
                    }
                    // 모비위드 실패 시 로직 후처리
                    if ( util_mobon_reward_ad_layer_c != null )
                        util_mobon_reward_ad_layer_c.setVisibility(View.GONE);

                    if ( util_ad_layer != null )
                        util_ad_layer.setVisibility(View.GONE);

                    if ( util_mobwith_layer != null )
                        util_mobwith_layer.setVisibility(View.GONE);

                } else if (message.contains("AdapterSuccessCallback")) {
                    LogPrint.d("util mobwith keyboardad AdapterSuccessCallback");
                    if (util_mobwith_webview != null) {
                        // 믹서 로드 성공
                        isUtilMobWithHasError = false;
                    }
                } else {
                    LogPrint.d("util mobwith keyboardad else console error :: " + message);
                }
            }
        });

        util_mobwith_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogPrint.d("util mobwith keyboardad shouldOverrideUrlLoading 1 :: " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.cancel();
                LogPrint.d("util mobwith keyboardad onReceivedSslError");

                isUtilMobWithHasError = true;

                if (util_mobwith_webview == null) {
                    LogPrint.d("util mobwith keyboardad webview null ");
                    return;
                }

                // 모비위드 실패 시 로직 후처리
                if ( util_mobon_reward_ad_layer_c != null )
                    util_mobon_reward_ad_layer_c.setVisibility(View.GONE);

                if ( util_ad_layer != null )
                    util_ad_layer.setVisibility(View.GONE);

                if ( util_mobwith_layer != null )
                    util_mobwith_layer.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest
                    request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isUtilMobWithHasError = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    LogPrint.d("util mobwith keyboardad onReceivedError :: " + error.toString() + " , desc :: " + error.getDescription() + " , code :: " + error.getErrorCode());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (error.getErrorCode() == -1)
                        return;
                }

                if (util_mobwith_webview == null) {
                    LogPrint.d("keyboardad util mobwith webview null 1");
                    return;
                }
                // 모비위드 실패 시 로직 후처리
                if ( util_mobon_reward_ad_layer_c != null )
                    util_mobon_reward_ad_layer_c.setVisibility(View.GONE);

                if ( util_ad_layer != null )
                    util_ad_layer.setVisibility(View.GONE);

                if ( util_mobwith_layer != null )
                    util_mobwith_layer.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                String targetUrl = Common.GetMobwithURL(mContext, Common.MOBWITH_RELEASE_SETTING_ZONE, true);
                if (targetUrl.contains(url)) {
                    // 모비위드 실패 시 로직 후처리
                    if ( util_mobon_reward_ad_layer_c != null )
                        util_mobon_reward_ad_layer_c.setVisibility(View.GONE);

                    if ( util_ad_layer != null )
                        util_ad_layer.setVisibility(View.GONE);

                    if ( util_mobwith_layer != null )
                        util_mobwith_layer.setVisibility(View.GONE);

                    if ( util_mobwith_webview != null && util_mobwith_container != null && util_mobwith_layer != null && !isUtilMobWithHasError ) {
                        LogPrint.d("util mobwith keyboardad mobwith_layer not null");
                        util_mobwith_layer.setVisibility(View.VISIBLE);
                        util_mobwith_container.removeAllViews();
                        if ( (ViewGroup) util_mobwith_webview.getParent() != null)
                            ((ViewGroup) util_mobwith_webview.getParent()).removeView(util_mobwith_webview);
                        util_mobwith_webview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        util_mobwith_container.addView(util_mobwith_webview);
                        LogPrint.d("rewardPossible 1 :: " + rewardPossible);
                        if ( rewardPossible ) {
                            int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                            if ( point > 0 ) {
                                util_mobwith_reward_point.setText(point + "P");
                                util_mobwith_reward_point.setVisibility(View.VISIBLE);
                                util_mobwith_non_reward_badge.setVisibility(View.GONE);
                            } else {
                                util_mobwith_reward_point.setVisibility(View.GONE);
                                util_mobwith_non_reward_badge.setVisibility(View.VISIBLE);
                            }
                        } else {
                            util_mobwith_reward_point.setVisibility(View.GONE);
                            util_mobwith_non_reward_badge.setVisibility(View.VISIBLE);
                        }

                        int baseWidth = Common.convertDpToPx(mContext, 411);
                        int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                        if (mSoftKeyboard != null) {
                            screenWidth = mSoftKeyboard.getScreenWidth();
                        }

                        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) util_mobwith_container.getLayoutParams();
                        if ( screenWidth < baseWidth) {
                            LogPrint.d("left ~~~");
                            param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                        } else {
                            LogPrint.d("right ~~~");
                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        }
                        util_mobwith_container.setLayoutParams(param);

                    }

                    util_mobwith_close.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setRake(currentPageId, "tap.closebtn");
                            util_mobwith_layer.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        String url = Common.GetMobwithURL(mContext, Common.MOBWITH_RELEASE_SETTING_ZONE, true);

        LogPrint.d("keyboardad util mobwith url :: " + url);
        util_mobwith_webview.loadUrl(url);
    }

    private void initMobwithAD(boolean rewardPossible) {
        LogPrint.d("initMobwithAD");
        if ( mContext == null )
            return;
        LogPrint.d("initMobwithAD 1");
        isMobWithHasError = false;

        mobwith_webview = new WebView(mContext);
        WebSettings settings = mobwith_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

        String path = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(path);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkLoads(false);
        settings.setAllowFileAccess(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// https 이미지.
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mobwith_webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else
            mobwith_webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mobwith_webview.setVerticalScrollBarEnabled(false);
        mobwith_webview.setDrawingCacheEnabled(true);

        mobwith_layer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

                                                                                                                                                                                                                        mobwith_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                try {
                    WebView newWebView = new WebView(mContext);
                    view.addView(newWebView);
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(newWebView);
                    resultMsg.sendToTarget();

                    newWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            LogPrint.d("mobwith webWebView url :: " + url);
                            String alli_1 = "aliexpress.com";
                            String alli_2 = "campaign.aliexpress.com";
                            String alli_3 = "m.aliexpress.com";
                            boolean isPassPossible = true;
                            if ( url != null ) {
                                if ( url.contains(alli_1) || url.contains(alli_2) || url.contains(alli_3) )
                                    if ( !mSoftKeyboard.isKeyboardShow )
                                        isPassPossible = false;
                            }

                            if ( isPassPossible ) {
                                try {
                                    int r_point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                    if ( rewardPossible && r_point > 0 ) {
                                        sendNewRewardPoint(Common.MOBWITH_RELEASE_KEYBOARD_ZONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                try {
                                    mContext.startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    if(mobwith_webview != null) {
                                        mobwith_webview.loadUrl("javascript:mixerClickFn();");
                                    }
                                } catch (Exception e) {
                                    try {
                                        if ( mContext != null )
                                            mContext.startActivity(Intent.createChooser(intent, "Title"));
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                }

                                sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);

                                if (url.contains("//img.mobon.net/ad/linfo.php"))
                                    mobwith_webview.goBack();
                            }
                            return true;
                        }
                    });
                } catch(Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                LogPrint.d("mobwith keyboardad console message :: " + message);
                if ((message.contains("Uncaught SyntaxError:") || message.contains("Uncaught ReferenceError:") || message.contains("AdapterFailCallback")) && !message.contains("wp_json")) {
                    LogPrint.d("mobwith keyboardad contain error");
                    isMobWithHasError = true;
                    if (mobwith_webview == null) {
                        LogPrint.d("mobwith keyboardad null");
                        return;
                    } else {
                        LogPrint.d("mobwith keyboardad not null");
                        mobwith_webview.onPause();
                    }
                    // 모비위드 실패 시 로직 후처리
                    if ( criteo_layer != null )
                        criteo_layer.setVisibility(View.GONE);
                    if ( coupang_dy_layer != null )
                        coupang_dy_layer.setVisibility(View.GONE);
                    if ( mMobonRewardAdLayerC != null )
                        mMobonRewardAdLayerC.setVisibility(View.GONE);
                    if ( reward_coupang_layer != null )
                        reward_coupang_layer.setVisibility(View.GONE);
                    if ( reward_joint_layer != null )
                        reward_joint_layer.setVisibility(View.GONE);
                    if ( reward_joint_banner_layer != null )
                        reward_joint_banner_layer.setVisibility(View.GONE);
                    if ( mMobonRewardAdLayer != null )
                        mMobonRewardAdLayer.setVisibility(View.GONE);

                    if ( mixer_layer != null ) {
                        mixer_layer.setVisibility(View.GONE);
                    }

                    if ( mobwith_layer != null ) {
                        mobwith_layer.setVisibility(View.GONE);
                    }

                } else if (message.contains("AdapterSuccessCallback")) {
                    LogPrint.d("mobwith keyboardad AdapterSuccessCallback");
                    if (mobwith_webview != null) {
                        // 믹서 로드 성공
                        isMobWithHasError = false;
                    }
                } else {
                    LogPrint.d("mobwith keyboardad else console error :: " + message);
                }
            }
        });

        mobwith_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogPrint.d("mobwith keyboardad shouldOverrideUrlLoading 1 :: " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.cancel();
                LogPrint.d("mobwith keyboardad onReceivedSslError");

                isMobWithHasError = true;

                if (mobwith_webview == null) {
                    LogPrint.d("mobwith keyboardad webview null ");
                    return;
                }

                // 모비위드 실패 시 로직 후처리
                if ( criteo_layer != null )
                    criteo_layer.setVisibility(View.GONE);
                if ( coupang_dy_layer != null )
                    coupang_dy_layer.setVisibility(View.GONE);
                if ( mMobonRewardAdLayerC != null )
                    mMobonRewardAdLayerC.setVisibility(View.GONE);
                if ( reward_coupang_layer != null )
                    reward_coupang_layer.setVisibility(View.GONE);
                if ( reward_joint_layer != null )
                    reward_joint_layer.setVisibility(View.GONE);
                if ( reward_joint_banner_layer != null )
                    reward_joint_banner_layer.setVisibility(View.GONE);
                if ( mMobonRewardAdLayer != null )
                    mMobonRewardAdLayer.setVisibility(View.GONE);
                if ( mixer_layer != null ) {
                    mixer_layer.setVisibility(View.GONE);
                }
                if ( mobwith_layer != null ) {
                    mobwith_layer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest
                    request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isMobWithHasError = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    LogPrint.d("mobwith keyboardad onReceivedError :: " + error.toString() + " , desc :: " + error.getDescription() + " , code :: " + error.getErrorCode());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (error.getErrorCode() == -1)
                        return;
                }

                if (mobwith_webview == null) {
                    LogPrint.d("keyboardad mixer webview null 1");
                    return;
                }
                // 모비위드 실패 시 로직 후처리
                view.loadUrl("about:blank");
                if ( criteo_layer != null )
                    criteo_layer.setVisibility(View.GONE);
                if ( coupang_dy_layer != null )
                    coupang_dy_layer.setVisibility(View.GONE);
                if ( mMobonRewardAdLayerC != null )
                    mMobonRewardAdLayerC.setVisibility(View.GONE);
                if ( reward_coupang_layer != null )
                    reward_coupang_layer.setVisibility(View.GONE);
                if ( reward_joint_layer != null )
                    reward_joint_layer.setVisibility(View.GONE);
                if ( reward_joint_banner_layer != null )
                    reward_joint_banner_layer.setVisibility(View.GONE);
                if ( mMobonRewardAdLayer != null )
                    mMobonRewardAdLayer.setVisibility(View.GONE);
                if ( mixer_layer != null ) {
                    mixer_layer.setVisibility(View.GONE);
                }
                if ( mobwith_layer != null ) {
                    mobwith_layer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                LogPrint.d("mobwith onPageFinished");
                String targetUrl = Common.GetMobwithURL(mContext, Common.MOBWITH_RELEASE_KEYBOARD_ZONE, true);
                if (targetUrl.contains(url)) {
                    if ( criteo_layer != null )
                        criteo_layer.setVisibility(View.GONE);
                    if ( coupang_dy_layer != null )
                        coupang_dy_layer.setVisibility(View.GONE);
                    if ( mMobonRewardAdLayerC != null )
                        mMobonRewardAdLayerC.setVisibility(View.GONE);
                    if ( reward_coupang_layer != null )
                        reward_coupang_layer.setVisibility(View.GONE);
                    if ( reward_joint_layer != null )
                        reward_joint_layer.setVisibility(View.GONE);
                    if ( reward_joint_banner_layer != null )
                        reward_joint_banner_layer.setVisibility(View.GONE);
                    if ( mMobonRewardAdLayer != null )
                        mMobonRewardAdLayer.setVisibility(View.GONE);
                    if ( mixer_layer != null ) {
                        mixer_layer.setVisibility(View.GONE);
                    }

                    if ( mobwith_webview != null && mobwith_container != null && mobwith_layer != null && !isMobWithHasError ) {
                        LogPrint.d("mobwith keyboardad mobwith_layer not null");
                        mobwith_layer.setVisibility(View.VISIBLE);
                        mobwith_container.removeAllViews();
                        if ( (ViewGroup) mobwith_webview.getParent() != null)
                            ((ViewGroup) mobwith_webview.getParent()).removeView(mobwith_webview);
                        mobwith_webview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        mobwith_container.addView(mobwith_webview);
                        if ( rewardPossible ) {
                            int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                            if ( point > 0 ) {
                                mobwith_reward_point.setText(point + "P");
                                mobwith_reward_point.setVisibility(View.VISIBLE);
                                mobwith_non_reward_badge.setVisibility(View.GONE);
                            } else {
                                mobwith_reward_point.setVisibility(View.GONE);
                                mobwith_non_reward_badge.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mobwith_reward_point.setVisibility(View.GONE);
                            mobwith_non_reward_badge.setVisibility(View.VISIBLE);
                        }

                        int baseWidth = Common.convertDpToPx(mContext, 411);
                        int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                        if (mSoftKeyboard != null) {
                            screenWidth = mSoftKeyboard.getScreenWidth();
                        }

                        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mobwith_container.getLayoutParams();
                        if ( screenWidth < baseWidth) {
                            LogPrint.d("left ~~~");
                            param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                        } else {
                            LogPrint.d("right ~~~");
                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        }
                        mobwith_container.setLayoutParams(param);
                    }

                    mobwith_close.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setRake(currentPageId, "tap.closebtn");
                            mobwith_layer.setVisibility(View.GONE);
                            sendViewCount();
                        }
                    });
                }
            }
        });
        String url = Common.GetMobwithURL(mContext, Common.MOBWITH_RELEASE_KEYBOARD_ZONE, true);

        LogPrint.d("keyboardad mobwith url :: " + url);
        mobwith_webview.loadUrl(url);
    }

    private void initMixerAD() {
        isMixerHasError = false;
        /*
        if (mixer_webview != null) {
            mixer_webview.stopLoading();
        } else {
            mixer_webview = layout.findViewById(R.id.mixer_webview);
        }
         */
        WebSettings settings = mixer_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

        // 웹뷰 - HTML5 창 속성 추가
        String path = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(path);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkLoads(false);
        settings.setAllowFileAccess(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// https 이미지.
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mixer_webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else
            mixer_webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);


        mixer_webview.setVerticalScrollBarEnabled(false);

        //   mixer_webview.setBackgroundColor(Color.TRANSPARENT);

        mixer_webview.setDrawingCacheEnabled(true);

        mixer_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(mContext);
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
//                        browserIntent.setData(Uri.parse(url));
//                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            LogPrint.d("mixer click");
                            mContext.startActivity(browserIntent);
                            /*
                            if(mixer_webview != null)
                                mixer_webview.loadUrl("javascript:sdkMixerClick();");
*/
                            if(mixer_webview != null) {
                                LogPrint.d("mixer click 1");
                                mixer_webview.loadUrl("javascript:mixerClickFn();");
                            }


                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
//                            mContext.startActivity(Intent.createChooser(browserIntent, "Title"));
                        }

                        if (url.contains("//img.mobon.net/ad/linfo.php"))
                            mixer_webview.goBack();
                        return true;
                    }
                });
                return true;
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                LogPrint.d("keyboardad console message :: " + message);
                if ((message.contains("Uncaught SyntaxError:") || message.contains("Uncaught ReferenceError:") || message.contains("AdapterFailCallback")) && !message.contains("wp_json")) {
                    LogPrint.d("keyboardad mixer contain error");
                    if (mixer_webview == null) {
                        LogPrint.d("keyboardad mixer_webview null");
                        return;
                    } else {
                        LogPrint.d("keyboardad mixer_webview not null");
                        mixer_webview.onPause();
                    }
                    // 믹서 실패 시 로직 후처리
                    criteo_layer.setVisibility(View.GONE);
                    coupang_dy_layer.setVisibility(View.GONE);
                    mMobonRewardAdLayerC.setVisibility(View.GONE);
                    reward_coupang_layer.setVisibility(View.GONE);
                    reward_joint_layer.setVisibility(View.GONE);
                    reward_joint_banner_layer.setVisibility(View.GONE);
                    mMobonRewardAdLayer.setVisibility(View.GONE);
                    if ( mixer_layer != null ) {
                        isMixerHasError = true;
                        mixer_layer.setVisibility(View.GONE);
                    }
                } else if (message.contains("AdapterSuccessCallback")) {
                    LogPrint.d("keyboardad mixer AdapterSuccessCallback");
                    if (mixer_webview != null) {
                        // 믹서 로드 성공
                        isMixerHasError = false;
                    }
                } else {
                    LogPrint.d("keyboardad mixer else console error :: " + message);
                }
            }
        });

        mixer_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogPrint.d("keyboardad mixer shouldOverrideUrlLoading 1 :: " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.cancel();
                LogPrint.d("keyboardad mixer onReceivedSslError");

                if (mixer_webview == null) {
                    LogPrint.d("keyboardad mixer webview null ");
                    isMixerHasError = true;
                    return;
                } else {
                    //mixer_webview.onPause();
                }
                // 믹서 실패 시 로직 후처리
                criteo_layer.setVisibility(View.GONE);
                coupang_dy_layer.setVisibility(View.GONE);
                mMobonRewardAdLayerC.setVisibility(View.GONE);
                reward_coupang_layer.setVisibility(View.GONE);
                reward_joint_layer.setVisibility(View.GONE);
                reward_joint_banner_layer.setVisibility(View.GONE);
                mMobonRewardAdLayer.setVisibility(View.GONE);
                if ( mixer_layer != null ) {
                    isMixerHasError = true;
                    mixer_layer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest
                    request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    LogPrint.d("keyboardad mixer onReceivedError :: " + error.toString() + " , desc :: " + error.getDescription() + " , code :: " + error.getErrorCode());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (error.getErrorCode() == -1)
                        return;
                }

                if (mixer_webview == null) {
                    LogPrint.d("keyboardad mixer webview null 1");
                    isMixerHasError = true;
                    return;
                } else {
                    //mixer_webview.onPause();
                }
                // 믹서 실패 시 로직 후처리
                view.loadUrl("about:blank");
                if ( criteo_layer != null )
                    criteo_layer.setVisibility(View.GONE);
                coupang_dy_layer.setVisibility(View.GONE);
                mMobonRewardAdLayerC.setVisibility(View.GONE);
                reward_coupang_layer.setVisibility(View.GONE);
                reward_joint_layer.setVisibility(View.GONE);
                reward_joint_banner_layer.setVisibility(View.GONE);
                mMobonRewardAdLayer.setVisibility(View.GONE);
                if ( mixer_layer != null ) {
                    isMixerHasError = true;
                    mixer_layer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                //  view.setVisibility(View.VISIBLE);
                String targetUrl = "https://mixer.mobon.net/script?sspNo=639&w=320&h=50&ver=5.3.0&carrier=SKTelecom&ifa=" + MobonUtils.getAdid(mContext) + "&requestType=API";

                if (targetUrl.contains(url)) {
                    if ( criteo_layer != null )
                        criteo_layer.setVisibility(View.GONE);
                    coupang_dy_layer.setVisibility(View.GONE);
                    mMobonRewardAdLayerC.setVisibility(View.GONE);
                    reward_coupang_layer.setVisibility(View.GONE);
                    reward_joint_layer.setVisibility(View.GONE);
                    reward_joint_banner_layer.setVisibility(View.GONE);
                    mMobonRewardAdLayer.setVisibility(View.GONE);
                    if ( mixer_layer != null && !isMixerHasError ) {
                        LogPrint.d("keyboardad mixer_layer not null");
                        mixer_layer.setVisibility(View.VISIBLE);
                        mixer_container.removeAllViews();
                        if ( (ViewGroup) mixer_webview.getParent() != null)
                            ((ViewGroup) mixer_webview.getParent()).removeView(mixer_webview);
                        mixer_container.addView(mixer_webview);
                        //mixer_webview.setVisibility(View.VISIBLE);
                    } else {

                    }

                    mixer_close.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setRake(currentPageId, "tap.closebtn");
                            mixer_layer.setVisibility(View.GONE);
                            sendViewCount();
                        }
                    });
                } else {

                }
                /*
                if (url.contains("/servlet/auid")) {
                    if (type.equals("mbadapter"))
                        loadMobonScript(mixer_webview, scriptCode);
                    else
                        loadSSPScript(mixer_webview, scriptCode);


                } else {

                    mMainLayout.setVisibility(View.VISIBLE);

                    if (MobonBannerView.this.getParent() != null) {
                        ((ViewGroup) MobonBannerView.this.getParent()).setBackgroundColor(Color.WHITE);
                    }
                }*/
            }
        });
        //String url = Url.DOMAIN + Url.OCB_AD_MIXER + "&ifa=" + MobonUtils.getAdid(mContext) + "&carrier=SKTelecom&ver=5.1.0&w=320&h=50";
        String url = "https://mixer.mobon.net/script?sspNo=639&w=320&h=50&ver=5.3.0&carrier=SKTelecom&ifa=" + MobonUtils.getAdid(mContext) + "&requestType=API";

        LogPrint.d("keyboardad mixer url :: " + url);
        mixer_webview.loadUrl(url);
    }

    // 2022.12.23 mobwith 광고로 모든 광고 대체됨
//    public void initMediaBannerView() {
//        mMobonAdLayer.setBackgroundColor(Color.TRANSPARENT);
//        AdData adData = new AdData();
//        String appName = "OkCashbag";
//        try {
//            appName = (String) mContext.getPackageManager().getApplicationLabel(mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES));
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        adData.major("testbanner", AdConfig.API_BANNER, Publisher_Code, Media_Code, Section_Code, "http://www.storeurl.com", mContext.getPackageName(), appName, 320, 50);
//        adData.minor("0", "40", "mezzo", "geonjin.mun@cj.net");
//        adData.isPermission(AdConfig.NOT_USED, AdConfig.NOT_USED);
//        mBannerView = new AdManView(mContext);
//        mBannerView.setData(adData, new AdListener() {
//            @Override
//            public void onAdSuccessCode(Object v, String id, final String type, final String status, final String jsonDataString) {
//                Navimanager.getInstance().onAdSuccessCode(type, status, jsonDataString, new Handler() {
//                    @Override
//                    public void dispatchMessage(Message msg) {
//                        try {
//                            String log = String.valueOf(msg.obj);
//                            LogPrint.d("mbanner onAdSuccessCode msg :: " + log);
//                            Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
//                            ((Activity) mContext).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (AdResponseCode.Status.SUCCESS.equals(status)) {
//                                        LogPrint.d("kksskk dispatchMessage C VISIBLE");
//                                        mMobonAdLayerC.setVisibility(View.VISIBLE);
//                                        mMobonAdLayer.removeAllViews();
//                                        mBannerView.addBannerView(mMobonAdLayer);
//                                    }
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onAdFailCode(Object v, String id, String type, String status, String jsonDataString) {
//                mBannerView.onDestroy();
//                Navimanager.getInstance().onAdFailCode(type, status, jsonDataString, new Handler() {
//                    @Override
//                    public void dispatchMessage(Message msg) {
//                        String log = String.valueOf(msg.obj);
//                        LogPrint.d("mbanner onAdFailCode msg :: " + log);
//                        Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
//                        loadBanner(false);
//                    }
//                });
//            }
//
//            @Override
//            public void onAdErrorCode(Object v, String id, String type, String status, String failingUrl) {
//                mBannerView.onDestroy();
//                Navimanager.getInstance().onAdErrorCode(type, status, failingUrl, new Handler() {
//                    @Override
//                    public void dispatchMessage(Message msg) {
//                        String log = String.valueOf(msg.obj);
//                        LogPrint.d("mbanner onAdErrorCode msg :: " + log);
//                        Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
//                        loadBanner(false);
//                    }
//                });
//            }
//
//            @Override
//            public void onAdEvent(Object v, String id, String type, String status, String jsonDataString) {
//                LogPrint.d("mbanner onAdEvent type :: " + type + " , status :: " + status + " , dataString :: " + jsonDataString);
//                if (AdEvent.Type.CLICK.equals(type)) {
//                    mBannerView.onDestroy();
//                } else if (AdEvent.Type.CLOSE.equals(type)) {
//                    mBannerView.onDestroy();
//                } else if (AdEvent.Type.IMP.equals(type)) {
//
//                }
//                Navimanager.getInstance().onAdEvent(type, status, jsonDataString, new Handler() {
//                    @Override
//                    public void dispatchMessage(Message msg) {
//                        String log = String.valueOf(msg.obj);
//                        Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onPermissionSetting(Object v, String id) {
//                showSettingsAlert(mContext);
//            }
//        });
//        mBannerView.request(new Handler());
//    }

    boolean isRun;

    public void initUtilRewardBannerView(String coupang, String moneytree, String news) {
        String bannerUnitId = "670190"; // reword의 unit id
        //bannerUnitId = "551812"; // 테스트 위해 모비온 광고 대신 넣음
        new MobonSimpleSDK(mContext, "okaycashbag"); // reward의 mediaCode 신규 발급받아 적용해야함.
        utilRewardBannerView = new MobonBannerView(mContext, MobonBannerType.BANNER_320x50, isRun, isRewardPossible).setExtractColor(false).setBannerUnitId(bannerUnitId);
        LogPrint.d("initBannerView");
        util_mobon_reward_ad_layer.setBackgroundColor(Color.TRANSPARENT);

        utilRewardBannerView.setAdListener(new iSimpleMobonBannerCallback() {
            @Override
            public void onLoadedAdInfo(boolean result, String errorStr) {
                util_ad_layer.setVisibility(View.GONE);
                if (result) {
                    LogPrint.d("1 onLoadedAdInfo true");
                    //visibleMobonADView();
                    if (utilRewardBannerView != null) {
                        int baseWidth = Common.convertDpToPx(mContext, 411);
                        int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                        if (mSoftKeyboard != null) {
                            screenWidth = mSoftKeyboard.getScreenWidth();
                        }

                        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) util_reward_ad_layer.getLayoutParams();
                        int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                        if ( isRewardPossible ) {
                            if ( point <= 0 ) {
                                util_reward_point.setVisibility(View.GONE);
                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                            } else {
                                LogPrint.d("2 baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                util_reward_point.setVisibility(View.VISIBLE);
                                if ( screenWidth < baseWidth) {
                                    LogPrint.d("left ~~~");
                                    param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                } else {
                                    LogPrint.d("right ~~~");
                                    param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                }
                            }
                        } else {
                            LogPrint.d("1 baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                            util_reward_point.setVisibility(View.GONE);
                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        }

                        util_reward_ad_layer.setLayoutParams(param);

                        util_mobon_reward_ad_layer_c.setVisibility(View.VISIBLE);
                        util_mobon_reward_ad_layer.setVisibility(View.VISIBLE);
                        util_reward_coupang_layer.setVisibility(View.GONE);
                        util_reward_joint_layer.setVisibility(View.GONE);
                        util_reward_joint_banner_layer.setVisibility(View.GONE);
                        util_mobon_reward_ad_layer.removeAllViews();
                        util_mobon_reward_ad_layer.addView(utilRewardBannerView);
                        LogPrint.d(" call reward_mobon_eprs");
                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                        task.postStats("reward_mobon_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                            @Override
                            public void onResponse(boolean result, Object obj) {

                            }
                        });
                    }
                } else {
                    LogPrint.d("1 onLoadedAdInfo false. reason :: " + errorStr);
                    if ( utilRewardBannerView != null ) {
                        utilRewardBannerView.onDestroy();
                    }

                    if (isJointReward) {
                        if ( "on".equals(news) ) {
                            Random rand = new Random();
                            int val = rand.nextInt(3);
                            if ( val == 0 ) {
                                initUtilRewardNews();
                            } else {
                                initUtilJointReward();
                            }
                        } else {
                            initUtilJointReward();
                        }
                    } else {
                        if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                            LogPrint.d("util sep test 1 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            Random rand = new Random();
                            int val = rand.nextInt(3);
                            LogPrint.d("util Val :: " + val);
                            if ( val == 0 ) {
                                initUtilCoupangAD();
                            } else if ( val == 1 ) {
                                initUtilMoneyTreeAD();
                            } else
                                initUtilRewardNews();
                        } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(moneytree) && "on".equals(moneytree)) {
                            LogPrint.d("util sep test 2 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            Random rand = new Random();
                            int val = rand.nextInt(2);
                            LogPrint.d("util Val1 :: " + val);
                            if ( val == 0 ) {
                                initUtilCoupangAD();
                            } else {
                                initUtilMoneyTreeAD();
                            }
                        } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                            LogPrint.d("util sep test 3 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            Random rand = new Random();
                            int val = rand.nextInt(2);
                            LogPrint.d("util Val1 :: " + val);
                            if ( val == 0 ) {
                                initUtilCoupangAD();
                            } else {
                                initUtilRewardNews();
                            }
                        } else if ( !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                            LogPrint.d("util sep test 4 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            Random rand = new Random();
                            int val = rand.nextInt(2);
                            LogPrint.d("util Val1 :: " + val);
                            if ( val == 0 ) {
                                initUtilMoneyTreeAD();
                            } else {
                                initUtilRewardNews();
                            }
                        } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !"on".equals(moneytree) && !"on".equals(news)) {
                            LogPrint.d("util sep test 5 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            initUtilCoupangAD();
                        } else if ( !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !"on".equals(news) && !"on".equals(coupang)) {
                            LogPrint.d("util sep test 6 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            initUtilMoneyTreeAD();
                        } else if ( !TextUtils.isEmpty(news) && "on".equals(news) && !"on".equals(coupang) && !"on".equals(moneytree)) {
                            LogPrint.d("util sep test 7 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            initUtilRewardNews();
                        } else {
                            LogPrint.d("util sep test 8 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                        }
                    }
                }
            }

            @Override
            public void onAdClicked() {
                KeyboardLogPrint.d("1 click ads");
                // reward banner에도 깜짝 포인트 제공하는가?
                // 9월 epic 리워드 광고에 깜짝 포인트 제공하지 않도록 수정
                //sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                LogPrint.d(" call reward_mobon_click");
                CustomAsyncTask task = new CustomAsyncTask(mContext);
                task.postStats("reward_mobon_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {

                    }
                });
            }

            @Override
            public void onCloseClicked() {
                if (moreAdCloseCount < 2)
                    moreAdCloseCount++;
            }

            @Override
            public void onBannerLoaded(int leftColor, int rightColor) {
                LogPrint.d("1 onBannerLoaded leftColor :: " + leftColor + " , rightColor :: " + rightColor);
                util_r_leftBg.setVisibility(View.VISIBLE);
                util_r_rightBg.setVisibility(View.VISIBLE);
                util_r_leftBg.setBackgroundColor(leftColor);
                util_r_rightBg.setBackgroundColor(rightColor);

                int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                util_reward_point.setText(point+"P");
                util_rest_layer.setVisibility(View.VISIBLE);
            }
        });

    }

    public void initRewardBannerView(String coupang, String moneytree, String news) {

        String bannerUnitId = "670190"; // reword의 unit id
        //bannerUnitId = "551812"; // 테스트 위해 모비온 광고 대신 넣음
        new MobonSimpleSDK(mContext, "okaycashbag"); // reward의 mediaCode 신규 발급받아 적용해야함.
        rewardBannerView = new MobonBannerView(mContext, MobonBannerType.BANNER_320x50, isRun, isRewardPossible).setExtractColor(false).setBannerUnitId(bannerUnitId);
        isRun = true;
        LogPrint.d("initBannerView");
        mMobonRewardAdLayer.setBackgroundColor(Color.TRANSPARENT);

        rewardBannerView.setAdListener(new iSimpleMobonBannerCallback() {
            @Override
            public void onLoadedAdInfo(boolean result, String errorStr) {
                LogPrint.d("kksskk reward onLoadedAdInfo C GONE");
                if ( criteo_layer != null )
                    criteo_layer.setVisibility(View.GONE);
                if ( mixer_layer != null )
                    mixer_layer.setVisibility(View.GONE);
                coupang_dy_layer.setVisibility(View.GONE);
                banner_ad_layer.setVisibility(View.GONE);
                mMobonAdLayerC.setVisibility(View.GONE);
                if (result) {
                    LogPrint.d("onLoadedAdInfo true");
                    //visibleMobonADView();
                    if (rewardBannerView != null) {
                        int baseWidth = Common.convertDpToPx(mContext, 411);
                        int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                        if (mSoftKeyboard != null) {
                            screenWidth = mSoftKeyboard.getScreenWidth();
                        }

                        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) reward_ad_layer.getLayoutParams();
                        int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                        if ( isRewardPossible ) {
                            LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                            if ( point <= 0 ) {
                                reward_point.setVisibility(View.GONE);
                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                            } else {
                                reward_point.setVisibility(View.VISIBLE);
                                if ( screenWidth < baseWidth) {
                                    LogPrint.d("left ~~~");
                                    param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                } else {
                                    LogPrint.d("right ~~~");
                                    param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                }
                            }
                        } else {
                            LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                            reward_point.setVisibility(View.GONE);
                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        }

                        reward_ad_layer.setLayoutParams(param);

                        mMobonRewardAdLayerC.setVisibility(View.VISIBLE);
                        mMobonRewardAdLayer.removeAllViews();
                        mMobonRewardAdLayer.addView(rewardBannerView);
                        // 모비온 리워드 광고 노출
                        LogPrint.d(" call reward_mobon_eprs");
                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                        task.postStats("reward_mobon_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                            @Override
                            public void onResponse(boolean result, Object obj) {

                            }
                        });
                    }
                } else {
                    LogPrint.d("onLoadedAdInfo false. reason :: " + errorStr);
                    if ( rewardBannerView != null ) {
                        rewardBannerView.onDestroy();
                    }
                    if (isJointReward ) {
                        if ( "on".equals(news) ) {
                            Random rand = new Random();
                            int val = rand.nextInt(3);
                            if ( val == 0 ) {
                                initRewardNews();
                            } else {
                                initJointReward();
                            }
                        } else {
                            initJointReward();
                        }
                    } else {
                        if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                            LogPrint.d("sep test 1 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            Random rand = new Random();
                            int val = rand.nextInt(3);
                            LogPrint.d("Val :: " + val);
                            if ( val == 0 ) {
                                initCoupangAD();
                            } else if ( val == 1 ) {
                                initMoneyTreeAD();
                            } else
                                initRewardNews();
                        } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(moneytree) && "on".equals(moneytree)) {
                            LogPrint.d("sep test 2 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            Random rand = new Random();
                            int val = rand.nextInt(2);
                            LogPrint.d("Val1 :: " + val);
                            if ( val == 0 ) {
                                initCoupangAD();
                            } else {
                                initMoneyTreeAD();
                            }
                        } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                            LogPrint.d("sep test 3 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            Random rand = new Random();
                            int val = rand.nextInt(2);
                            LogPrint.d("Val1 :: " + val);
                            if ( val == 0 ) {
                                initCoupangAD();
                            } else {
                                initRewardNews();
                            }
                        } else if ( !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !TextUtils.isEmpty(news) && "on".equals(news)) {
                            LogPrint.d("sep test 4 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            Random rand = new Random();
                            int val = rand.nextInt(2);
                            LogPrint.d("Val1 :: " + val);
                            if ( val == 0 ) {
                                initMoneyTreeAD();
                            } else {
                                initRewardNews();
                            }
                        } else if ( !TextUtils.isEmpty(coupang) && "on".equals(coupang) && !"on".equals(moneytree) && !"on".equals(news)) {
                            LogPrint.d("sep test 5 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            initCoupangAD();
                        } else if ( !TextUtils.isEmpty(moneytree) && "on".equals(moneytree) && !"on".equals(news) && !"on".equals(coupang)) {
                            LogPrint.d("sep test 6 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            initMoneyTreeAD();
                        } else if ( !TextUtils.isEmpty(news) && "on".equals(news) && !"on".equals(coupang) && !"on".equals(moneytree)) {
                            LogPrint.d("sep test 7 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                            initRewardNews();
                        } else {
                            LogPrint.d("sep test 8 coupang :: " + coupang + " moneytree :: " + moneytree + " news :: " + news);
                        }
                    }
                }
            }

            @Override
            public void onAdClicked() {
                KeyboardLogPrint.d("click ads");
                // reward banner에도 깜짝 포인트 제공하는가?
                // 9월 epic 리워드 클릭 시 깜짝 포인트 지급하지 않음.
                //sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);

                // 모비온 리워드 광고 클릭
                LogPrint.d(" call reward_mobon_click");
                CustomAsyncTask task = new CustomAsyncTask(mContext);
                task.postStats("reward_mobon_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {

                    }
                });
            }

            @Override
            public void onCloseClicked() {
                LogPrint.d("reward mobon banner close");
                sendViewCount();
            }

            @Override
            public void onBannerLoaded(int leftColor, int rightColor) {
                LogPrint.d("onBannerLoaded leftColor :: " + leftColor + " , rightColor :: " + rightColor);
                r_leftBg.setVisibility(View.VISIBLE);
                r_rightBg.setVisibility(View.VISIBLE);
                r_leftBg.setBackgroundColor(leftColor);
                r_rightBg.setBackgroundColor(rightColor);

                int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                reward_point.setText(point+"P");
                rest_layer.setVisibility(View.VISIBLE);
            }
        });

    }

    public void initBannerView(boolean isFromMixer) {
//        String bannerUnitId = "524092";
        String bannerUnitId = "551812"; // 모비믹서 포함 신규 id
        new MobonSimpleSDK(mContext, "okaycashbag");
        bannerView = new MobonBannerView(mContext, MobonBannerType.BANNER_CUSTOM, isRun, false).setExtractColor(false).setBannerUnitId(bannerUnitId);
        isRun = true;
        LogPrint.d("keyboardad mobon initBannerView");
        mMobonAdLayer.setBackgroundColor(Color.TRANSPARENT);

        bannerView.setAdListener(new iSimpleMobonBannerCallback() {
            @Override
            public void onLoadedAdInfo(boolean result, String errorStr) {
                if ( criteo_layer != null )
                    criteo_layer.setVisibility(View.GONE);
                if ( mixer_layer != null )
                    mixer_layer.setVisibility(View.GONE);
                coupang_dy_layer.setVisibility(View.GONE);
                banner_ad_layer.setVisibility(View.GONE);
                mMobonRewardAdLayerC.setVisibility(View.GONE);

                if (result) {
                    LogPrint.d("keyboardad mobon onLoadedAdInfo true");
                    //visibleMobonADView();
                    if (bannerView != null) {
                        LogPrint.d("keyboardad mobon banner C visible");
                        mMobonAdLayerC.setVisibility(View.VISIBLE);
                        mMobonAdLayer.removeAllViews();
                        //if (mMobonAdLayer.getChildCount() <= 0) {
                        mMobonAdLayer.addView(bannerView);
                        LogPrint.d("keyboardad mobon after addView");
                        ad_del.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LogPrint.d("keyboardad mobon banner onClick C GONE");
                                mMobonAdLayerC.setVisibility(View.GONE);
                                sendViewCount();
                            }
                        });
/**
                        badge_layer.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if ( bannerView != null ) {
                                    KeyboardLogPrint.d("banner performClick");
                                    bannerView.bannerClick();
                                } else {
                                    KeyboardLogPrint.d("banner performClick bannerView null");
                                }
                            }
                        });

                        ad_del.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mMobonAdLayer.setVisibility(View.GONE);
                                sendViewCount();
                            }
                        });

                        if ( mSoftKeyboard != null ) {
                            if ( mSoftKeyboard.isBannerPointPossible() ) {
                                int point = SharedPreference.getInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                point_badge.setVisibility(View.VISIBLE);
                                btn_go_ad.setVisibility(View.GONE);
                                point_badge.setText(point + "P");
                            } else {
                                point_badge.setVisibility(View.GONE);
                                btn_go_ad.setVisibility(View.VISIBLE);
                            }
                        }**/
                        //}
                    }
                } else {
                    LogPrint.d("keyboardad mobon banner C false onLoadedAdInfo GONE");
                    mMobonAdLayerC.setVisibility(View.GONE);
                    bannerView.onDestroy();
                    loadMixerBanner(1);
                    //sendViewCount();
                }
            }

            @Override
            public void onAdClicked() {
                KeyboardLogPrint.d("keyboardad mobon click ads");
                sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                /**
                // 포인트 베지 떠 있는 상태에서 광고 클릭 시
                if ( point_badge.getVisibility() == View.VISIBLE ) {
                    CustomAsyncTask task = new CustomAsyncTask(mContext);
                    task.sendBannerPoint(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            Toast.makeText(mContext, "포인트 적립이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }**/
            }

            @Override
            public void onCloseClicked() {
                LogPrint.d("keyboardad mobon banner close");
                sendViewCount();
            }

            @Override
            public void onBannerLoaded(int leftColor, int rightColor) {
                LogPrint.d("keyboardad mobon onBannerLoaded leftColor :: " + leftColor + " , rightColor :: " + rightColor);
            }
        });
    }

    public void initUtilBannerView() {
        if (util_ad_layer == null)
            return;
        if ( util_mobon_reward_ad_layer_c != null )
            util_mobon_reward_ad_layer_c.setVisibility(View.GONE);

        String bannerUnitId = "582723"; // 모비믹서 포함 신규 id
        new MobonSimpleSDK(mContext, "okaycashbag");
        utilBannerView = new MobonBannerView(mContext, MobonBannerType.BANNER_CUSTOM, isRun, false).setExtractColor(false).setBannerUnitId(bannerUnitId);
        isRun = true;
        LogPrint.d("initUtilBannerView");
        util_ad_layer.setBackgroundColor(Color.TRANSPARENT);

        utilBannerView.setAdListener(new iSimpleMobonBannerCallback() {
            @Override
            public void onLoadedAdInfo(boolean result, String errorStr) {
                if (result) {
                    LogPrint.d("onLoadedAdInfo true");
                    if (utilBannerView != null) {
                        util_ad_layer.setVisibility(View.VISIBLE);
                        util_ad_layer.removeAllViews();
                        util_ad_layer.addView(utilBannerView);
                    }
                } else {
                    LogPrint.d("onLoadedAdInfo false");
                    util_ad_layer.setVisibility(View.GONE);
                    utilBannerView.onDestroy();
                }
            }

            @Override
            public void onAdClicked() {
                KeyboardLogPrint.d("click ads");
                setRake("/keyboard/utill", "tap.utillbannerad");
            }

            @Override
            public void onCloseClicked() {
                if (moreAdCloseCount < 2)
                    moreAdCloseCount++;
            }

            @Override
            public void onBannerLoaded(int leftBg, int rightBg) {

            }
        });
    }

    public void setIsInAppKeyboard(boolean val) {
        this.isInAppKeyboard = val;
    }

    public String getBotColor() {
        if (mThemeModel != null)
            return mThemeModel.getBotTabColor();
        else return null;
    }

    public void setRake(String page_id, String action_id) {
        new Thread() {
            public void run() {
                currentPageId = page_id;
                String track_id = SharedPreference.getString(mContext, Key.KEY_OCB_TRACK_ID);
                String device_id = SharedPreference.getString(mContext, Key.KEY_OCB_DEVICE_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    track_id = cp.Decode(mContext, track_id);
                    device_id = cp.Decode(mContext, device_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String time = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
                String session_id = time + "_" + device_id;
                OCBLogSentinelShuttle shuttle = new OCBLogSentinelShuttle();
                shuttle.page_id(page_id).action_id(action_id).keyboard_log_yn("yes").session_id(session_id).mbr_id(track_id);
                rake.track(shuttle.toJSONObject());
            }
        }.start();
    }

    private void setBrandImage(Drawable dr) {
        if (mImgSecond != null) {
            if (dr != null) {
                LogPrint.d("mImgSecond, dr not null");
                // 하루 첫 키보드 올라온 이후 brand icon red로 변환 되는데 다른 tab 누를 경우에도 계속 red 유지하기 위해 추가 코드
                /*
                if ( SharedPreference.getTrueBoolean(mContext, Key.KEY_IS_BRAND_ON) )
                    dr = brandOn;
                 */
                mImgSecond.setBackground(dr);
                if (mImgSecond.getBackground() == null) {
                    LogPrint.d("mImgSecond.getBackground() null");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mImgSecond.setBackground(ContextCompat.getDrawable(mContext, R.drawable.aikbd_coupang_floating_icon_small));
                    } else {
                        mImgSecond.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.aikbd_coupang_floating_icon_small));
                    }
                    brandLink = DEFAULT_BRAND_LINK;
                }
            } else {
                LogPrint.d("dr null");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mImgSecond.setBackground(ContextCompat.getDrawable(mContext, R.drawable.aikbd_coupang_floating_icon_small));
                } else {
                    mImgSecond.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.aikbd_coupang_floating_icon_small));
                }
                brandLink = DEFAULT_BRAND_LINK;
            }
        } else
            LogPrint.d("mImgSecond null");
    }

    public void showSurpriseToast(boolean isInAppKeyboard, Context context, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // api 11 이상부터 setGravity 사용하지 못함. 일괄 내장 toast 로 통일(2022.09.19)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            /**
            if (isInAppKeyboard) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_surprise_toast, null);
                TextView tv = layout.findViewById(R.id.toastStr);
                tv.setText(message);
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }**/
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_surprise_toast, null);
            TextView tv = layout.findViewById(R.id.toastStr);
            tv.setText(message);
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        setRake(currentPageId, "toast.popreward");
    }

    private void sendNewRewardPoint(String zone_id) {
        int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
        if ( point <= 0 )
            return;
        if ( isRewardPointClickPossible ) {
            LogPrint.d("sendNewRewardPoint zone_id :: " + zone_id );
            isRewardPointClickPossible = false;
            CustomAsyncTask task = new CustomAsyncTask(mContext);
            task.newSendPoint(zone_id, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if ( result ) {
                        try {
                            JSONObject object = (JSONObject) obj;
                            LogPrint.d("sendNewRewardPoint object :: " + object.toString() );
                            if ( object != null ) {
                                boolean rt = object.optBoolean("Result");
                                if ( rt ) {
                                    String message = object.optString("message");
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }, 500);
                                    setRake(currentPageId, "toast.popreward");
                                    //showSurpriseToast(isInAppKeyboard, mContext, message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void sendRewardPoint(String sc) {
        int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
        if ( point <= 0 )
            return;
        if ( isRewardPointClickPossible ) {
            LogPrint.d("sendRewardPoint sc :: " + sc );
            isRewardPointClickPossible = false;
            CustomAsyncTask task = new CustomAsyncTask(mContext);
            task.sendRewardPoint(sc, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if ( result ) {
                        try {
                            JSONObject object = (JSONObject) obj;
                            LogPrint.d("sendRewardPoint object :: " + object.toString() );
                            if ( object != null ) {
                                boolean rt = object.optBoolean("Result");
                                if ( rt ) {
                                    String message = object.optString("message");
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }, 500);
                                    setRake(currentPageId, "toast.popreward");
                                    //showSurpriseToast(isInAppKeyboard, mContext, message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void sendSurprise(boolean isInAppKeyboard, Context context, String type) {
        CustomAsyncTask task1 = new CustomAsyncTask(context);
        task1.sendSpotPoint(type, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                LogPrint.d("sendSurprise result :: " + result);
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            LogPrint.d("sendSurprise obj :: " + object.toString());
                            boolean rt = object.optBoolean("Result");
                            if (rt) {
                                String message = object.optString("message");
                                showSurpriseToast(isInAppKeyboard, context, message);
                            } else {
                            }
                        } else {
                            LogPrint.d("sendSurprise object null");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    LogPrint.d("sendSurprise object null");
                }
            }
        });
    }

    public boolean isNewsExist() {
        if (newsInfo != null && !TextUtils.isEmpty(newsInfo.getTitle()))
            return true;
        else
            return false;
    }

    private void setNewsWithoutNetwork(JSONObject object, NewsCallbackListener listener) {
        LogPrint.d("setNewsWithoutNetwork");
        if ( object != null ) {
            LogPrint.d("setNewsWithoutNetwork object not null");
            JSONArray array = object.optJSONArray("newsList");
            if ( array != null && array.length() > 0 ) {
                LogPrint.d("setNewsWithoutNetwork array size :: " + array.length());
                JSONObject in_obj = array.optJSONObject(0);

                dummyImage.setFocusable(true);

                String news_title = in_obj.optString("news_title");
                String link_url = in_obj.optString("link_url");
                String news_img_url = in_obj.optString("news_img_url");
                String object_id = in_obj.optString("object_id");
                String site_name = in_obj.optString("site_name");
                String root_domain = in_obj.optString("root_domain");
                String addedTitle = news_title;

                newsInfo = new NewsInfo();
                newsInfo.setTitle(addedTitle);
                newsInfo.setLink(link_url);
                newsInfo.setImage(news_img_url);
                newsInfo.setObjectId(object_id);
                newsInfo.setSiteName(site_name);
                newsInfo.setRootDomain(root_domain);
                if ( !addedTitle.startsWith("[")) {
                    addedTitle = "[뉴스] " + addedTitle;
                }
                int start = 0;
                int end = addedTitle.indexOf("]") + 1;
                SpannableString spannableString = new SpannableString(addedTitle);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#868686")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                LogPrint.d("spannable news :: " + spannableString);
                newsTxt.setText(spannableString);
                dummyImage.setContentDescription("\u00A0");

                removeNewsFirstArray(object);
            }
        }
        if ( listener != null )
            listener.onNewsReceived();
    }

    private void setNewsWithNetwork(String statiFlag, NewsCallbackListener listener) {
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getArrayNews(statiFlag, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            boolean rt = object.optBoolean("Result");
                            if (rt) {
                                JSONArray array = object.optJSONArray("newsList");
                                if ( array != null && array.length() > 0 ) {
                                    SharedPreference.setString(mContext, Key.NEWS_JSON_DATA, object.toString());
                                    JSONObject in_obj = array.optJSONObject(0);
                                    if (in_obj != null) {
                                        dummyImage.setFocusable(true);

                                        String news_title = in_obj.optString("news_title");
                                        String link_url = in_obj.optString("link_url");
                                        String news_img_url = in_obj.optString("news_img_url");
                                        String object_id = in_obj.optString("object_id");
                                        String site_name = in_obj.optString("site_name");
                                        String root_domain = in_obj.optString("root_domain");
                                        String addedTitle = news_title;

                                        newsInfo = new NewsInfo();
                                        newsInfo.setTitle(addedTitle);
                                        newsInfo.setLink(link_url);
                                        newsInfo.setImage(news_img_url);
                                        newsInfo.setObjectId(object_id);
                                        newsInfo.setSiteName(site_name);
                                        newsInfo.setRootDomain(root_domain);
                                        // 5월 epic update 한 버전에 기존 받아놓았던 데이터가 있을 경우
                                        // 기존데이터에는 [뉴스]가 없는 경우이므로 이 경우에는 강제적으로 [뉴스] 붙여줘야함.
                                        if ( !addedTitle.startsWith("[")) {
                                            addedTitle = "[뉴스] " + addedTitle;
                                        }
                                        int start = 0;
                                        int end = addedTitle.indexOf("]") + 1;

                                        SpannableString spannableString = new SpannableString(addedTitle);
                                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#868686")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        LogPrint.d("spannable news :: " + spannableString);
                                        newsTxt.setText(spannableString);
                                        dummyImage.setContentDescription("\u00A0");

                                        removeNewsFirstArray(object);

                                        if ( "Y".equals(statiFlag) )
                                            sendViewCount();
                                    }
                                }
                            }
                        }
                        if ( listener != null )
                            listener.onNewsReceived();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if ( listener != null )
                            listener.onNewsReceived();
                    }
                } else {
                    if ( listener != null )
                        listener.onNewsReceived();
                }
            }
        });
    }

    private boolean removeNewsFirstArray(JSONObject object) {
        try {
            JSONObject tempObj = object;
            JSONArray tempArr = tempObj.optJSONArray("newsList");
            if ( tempArr != null && tempArr.length() > 0 ) {
                tempArr.remove(0);
                tempObj.remove("newsList");
                tempObj.put("newsList", tempArr);
                LogPrint.d("news tempObj :: " + tempObj.toString());
                SharedPreference.setString(mContext, Key.NEWS_JSON_DATA, tempObj.toString());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setNews(String statiFlag, NewsCallbackListener listener) {
        //setTalkbackOff();
        // 2022.11.15 뉴스 데이터를 array로 받아와 저장해서 사용하는 방식으로 변경함.
        String newsData = SharedPreference.getString(mContext, Key.NEWS_JSON_DATA);
        LogPrint.d("newsData :: " + newsData);
        try {
            if ( !TextUtils.isEmpty(newsData) ) {
                LogPrint.d("newsData not empty");
                JSONObject oj = new JSONObject(newsData);
                if ( oj != null ) {
                    JSONArray ar = oj.optJSONArray("newsList");
                    if ( ar != null && ar.length() > 0 ) {
                        LogPrint.d("ar not null length :: " + ar.length());
                        setNewsWithoutNetwork(oj, listener);
                        if ("Y".equals(statiFlag))
                            sendViewCount();
                    } else {
                        LogPrint.d("ar null or length less zero");
                        setNewsWithNetwork(statiFlag, listener);
                    }
                } else {
                    LogPrint.d("oj null");
                    setNewsWithNetwork(statiFlag, listener);
                }
            } else {
                LogPrint.d("news data empty");
                setNewsWithNetwork(statiFlag, listener);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setNewsWithNetwork(statiFlag, listener);
        }
    }

    private void sendViewCount() {
        String root_domain = "";
        if ( newsInfo != null )
            root_domain = newsInfo.getRootDomain();
        else
            LogPrint.d("newsInfo null");
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.sendViewCount(root_domain, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {

            }
        });
    }
/**
    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(mContext, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                Intent it = new Intent(mContext, STTPermissionActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(it);
            }
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < matches.size(); i++) {
                mSoftKeyboard.past(matches.get(i));
            }
            if (mRecognizer != null) {
                mRecognizer.stopListening();
                mRecognizer.cancel();
                mRecognizer.destroy();
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };**/

    private void selectShoppingTab(int selectedId) {
        if (selectedId == ID_HOMESHOPPING) {
            tab_homeshopping.setTypeface(null, Typeface.BOLD);
            tab_domestic.setTypeface(null, Typeface.NORMAL);
            tab_oversea.setTypeface(null, Typeface.NORMAL);
            tab_travel.setTypeface(null, Typeface.NORMAL);
            tab_book.setTypeface(null, Typeface.NORMAL);

            tab_homeshopping.setTextColor(Color.parseColor("#000000"));
            tab_domestic.setTextColor(Color.parseColor("#bfbfbf"));
            tab_oversea.setTextColor(Color.parseColor("#bfbfbf"));
            tab_travel.setTextColor(Color.parseColor("#bfbfbf"));
            tab_book.setTextColor(Color.parseColor("#bfbfbf"));
        } else if (selectedId == ID_DOMESTIC) {
            tab_homeshopping.setTypeface(null, Typeface.NORMAL);
            tab_domestic.setTypeface(null, Typeface.BOLD);
            tab_oversea.setTypeface(null, Typeface.NORMAL);
            tab_travel.setTypeface(null, Typeface.NORMAL);
            tab_book.setTypeface(null, Typeface.NORMAL);

            tab_homeshopping.setTextColor(Color.parseColor("#bfbfbf"));
            tab_domestic.setTextColor(Color.parseColor("#000000"));
            tab_oversea.setTextColor(Color.parseColor("#bfbfbf"));
            tab_travel.setTextColor(Color.parseColor("#bfbfbf"));
            tab_book.setTextColor(Color.parseColor("#bfbfbf"));
        } else if (selectedId == ID_OVERSEA) {
            tab_homeshopping.setTypeface(null, Typeface.NORMAL);
            tab_domestic.setTypeface(null, Typeface.NORMAL);
            tab_oversea.setTypeface(null, Typeface.BOLD);
            tab_travel.setTypeface(null, Typeface.NORMAL);
            tab_book.setTypeface(null, Typeface.NORMAL);

            tab_homeshopping.setTextColor(Color.parseColor("#bfbfbf"));
            tab_domestic.setTextColor(Color.parseColor("#bfbfbf"));
            tab_oversea.setTextColor(Color.parseColor("#000000"));
            tab_travel.setTextColor(Color.parseColor("#bfbfbf"));
            tab_book.setTextColor(Color.parseColor("#bfbfbf"));
        } else if (selectedId == ID_TRAVEL) {
            tab_homeshopping.setTypeface(null, Typeface.NORMAL);
            tab_domestic.setTypeface(null, Typeface.NORMAL);
            tab_oversea.setTypeface(null, Typeface.NORMAL);
            tab_travel.setTypeface(null, Typeface.BOLD);
            tab_book.setTypeface(null, Typeface.NORMAL);

            tab_homeshopping.setTextColor(Color.parseColor("#bfbfbf"));
            tab_domestic.setTextColor(Color.parseColor("#bfbfbf"));
            tab_oversea.setTextColor(Color.parseColor("#bfbfbf"));
            tab_travel.setTextColor(Color.parseColor("#000000"));
            tab_book.setTextColor(Color.parseColor("#bfbfbf"));
        } else if (selectedId == ID_BOOK) {
            tab_homeshopping.setTypeface(null, Typeface.NORMAL);
            tab_domestic.setTypeface(null, Typeface.NORMAL);
            tab_oversea.setTypeface(null, Typeface.NORMAL);
            tab_travel.setTypeface(null, Typeface.NORMAL);
            tab_book.setTypeface(null, Typeface.BOLD);

            tab_homeshopping.setTextColor(Color.parseColor("#bfbfbf"));
            tab_domestic.setTextColor(Color.parseColor("#bfbfbf"));
            tab_oversea.setTextColor(Color.parseColor("#bfbfbf"));
            tab_travel.setTextColor(Color.parseColor("#bfbfbf"));
            tab_book.setTextColor(Color.parseColor("#000000"));
        }
    }
/**
    private void initVoiceRecognizer(String lang) {
        rIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        rIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        rIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
    }

    private void startVoiceRecognizer() {
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        mRecognizer.setRecognitionListener(recognitionListener);
        mRecognizer.startListening(rIntent);
    }

    public void stopVoiceRecognizer() {
        if (mRecognizer != null) {
            mRecognizer.stopListening();
            mRecognizer.cancel();
            mRecognizer.destroy();
        }
    }
**/
    private void getShoppingList(int index) {
        setRake("/keyboard/shopping", "tap.shoppingtap");
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getShoppingList("" + index, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    if (obj != null) {
                        try {
                            if ((obj instanceof JSONArray)) {
                                JSONArray arr = (JSONArray) obj;
                                if (arr != null && arr.length() > 0) {
                                    ArrayList<ShoppingData> array = new ArrayList<>();
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject inObj = arr.optJSONObject(i);
                                        if (inObj != null) {
                                            ShoppingData data = new ShoppingData();
                                            data.setLinkUrl3(inObj.optString("linkUrl3"));
                                            data.setImgUrl(inObj.optString("imgUrl"));
                                            data.setLinkType(inObj.optString("linkType"));
                                            data.setTotalSaveRate(inObj.optString("totalSaveRate"));
                                            array.add(data);
                                        }
                                    }
                                    if (shopping_recyclerview != null)
                                        shopping_recyclerview.scrollToPosition(0);
                                    selectShoppingTab(index);
                                    saveShoppingAdapter.setItems(array);
                                }
                            } else {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        String error = object.optString(Common.NETWORK_ERROR);
                                        String dError = object.optString(Common.NETWORK_DISCONNECT);
                                        if (!TextUtils.isEmpty(error)) {
                                            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (!TextUtils.isEmpty(dError)) {
                                                Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }




                            /*
                            JSONObject object = (JSONObject) obj;
                            if ( object != null ) {
                                JSONObject  resultData = object.optJSONObject("resultData");
                                if ( resultData != null ) {
                                    String arrName = "affiList";
                                    if ( index == ID_TRAVEL )
                                        arrName = "trevelList";
                                    JSONArray arr = resultData.optJSONArray(arrName);
                                    if ( arr != null && arr.length() > 0 ) {
                                        ArrayList<ShoppingData> array = new ArrayList<>();
                                        for ( int i = 0 ; i < arr.length() ; i ++ ) {
                                            JSONObject inObj = arr.optJSONObject(i);
                                            if ( inObj != null ) {
                                                String url = inObj.optString("linkUrl3");
                                                if ( url != null && url.startsWith("ocbt://")) {
                                                    ShoppingData data = new ShoppingData();
                                                    data.setLinkUrl3(url);
                                                    data.setImgUrl(inObj.optString("imgUrl"));
                                                    data.setMallName(inObj.optString("mallName"));
                                                    data.setTotalSaveRate(inObj.optString("totalSaveRate"));
                                                    array.add(data);
                                                }
                                            }
                                        }

                                        selectShoppingTab(index);
                                        saveShoppingAdapter.setItems(array);
                                    }
                                }
                            }

                             */
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            String error = object.optString(Common.NETWORK_ERROR);
                            String dError = object.optString(Common.NETWORK_DISCONNECT);
                            if (!TextUtils.isEmpty(error)) {
                                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                            } else {
                                if (!TextUtils.isEmpty(dError)) {
                                    Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
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

    private boolean isVerticalMode() {
        if (mContext == null)
            return true;
        WindowManager windowService = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int currentRatation = windowService.getDefaultDisplay().getRotation();
        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation)
            return false;
        else
            return true;
    }

    private void initRecommandThemeLayer(RelativeLayout layer) {
        if (mContext == null || layer == null)
            return;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_recommand_theme_tab_layer, null);
    }

    private void initMyLayer(RelativeLayout layer) {
        if (mContext == null || layer == null)
            return;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_page_my_tab_layer, null);
        cardNum = SharedPreference.getString(mContext, Key.CARD_NUM);
        String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        String tPoint = SharedPreference.getString(mContext, Key.OCB_USER_POINT);

        View go_keyboard_save = layout.findViewById(R.id.go_keyboard_save);
        View go_ocb_save = layout.findViewById(R.id.go_ocb_save);
        ConstraintLayout point_layer = layout.findViewById(R.id.point_layer);
        TextView tlt_cashbag = layout.findViewById(R.id.tlt_cashbag);
        TextView point = layout.findViewById(R.id.point);
        TextView today_point = layout.findViewById(R.id.today_point);
        TextView month_point = layout.findViewById(R.id.month_point);
        ConstraintLayout copy_layer = layout.findViewById(R.id.copy_layer);
        TextView surprise_tlt = layout.findViewById(R.id.surprise_tlt);

        today_point.setText(Common.putComma(myInfo.getToday_point()));
        month_point.setText(Common.putComma(myInfo.getMonth_point()));

        go_keyboard_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setRake("/keyboard/mypoint", "tap.keyboardpoint");
                    String url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=돈버는 키보드&url=https%3a%2f%2fwebview.okcashbag.com%2fv1.0%2fearnkbd%2findex.html";
                    if (CustomAsyncTask.gubun.equals(CustomAsyncTask.GUBUN_ALPHA))
                        url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=돈버는 키보드&url=https%3a%2f%2falp-webview.okcashbag.com%2fv1.0%2fearnkbd%2findex.html";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        go_ocb_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
                if (!TextUtils.isEmpty(uuid)) {
                    try {
                        setRake("/keyboard/mypoint", "tap.totalpoint");
                        String url = "ocbt://com.skmc.okcashbag.home_google/myMenu/points";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        mSoftKeyboard.hide();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = "인증되지 않은 사용자 입니다. OK캐쉬백 앱에서 인증해주시기 바랍니다.";
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        surprise_tlt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initSurpriseLayer(layer);
            }
        });

        copy_layer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("label", cardNum);

                if ( data != null ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            PersistableBundle extras = new PersistableBundle();
                            extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
                            data.getDescription().setExtras(extras);
                        } else {
                            PersistableBundle extras = new PersistableBundle();
                            extras.putBoolean("android.content.extra.IS_SENSITIVE", true);
                            data.getDescription().setExtras(extras);
                        }
                    }

                    manager.setPrimaryClip(data);
                    Toast.makeText(mContext, "맴버십 번호가 복사되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        TextView cardFirst = layout.findViewById(R.id.card_first);
        TextView cardSecond = layout.findViewById(R.id.card_second);
        TextView cardThird = layout.findViewById(R.id.card_third);
        TextView cardFourth = layout.findViewById(R.id.card_fourth);

        cardFirst.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoftKeyboard != null)
                    mSoftKeyboard.setMemberNumber(cardFirst.getText().toString());
            }
        });

        cardSecond.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoftKeyboard != null)
                    mSoftKeyboard.setMemberNumber(cardSecond.getText().toString());
            }
        });

        cardThird.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoftKeyboard != null)
                    mSoftKeyboard.setMemberNumber(cardThird.getText().toString());
            }
        });

        cardFourth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSoftKeyboard != null)
                    mSoftKeyboard.setMemberNumber(cardFourth.getText().toString());
            }
        });

        try {
            E_Cipher cp = E_Cipher.getInstance();
            cardNum = cp.Decode(mContext, cardNum);
            uuid = cp.Decode(mContext, uuid);
            tPoint = cp.Decode(mContext, tPoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(tPoint) || "-1".equals(tPoint)) {
            tPoint = "-";
            point.setText(tPoint);
        } else {
            point.setText(Common.putComma(tPoint));
        }
        if (cardNum != null && cardNum.length() == 16) {
            String firstNum = cardNum.substring(0, 4);
            String secondNum = cardNum.substring(4, 8);
            String thirdNum = cardNum.substring(8, 12);
            String fourthNum = cardNum.substring(12, 16);
            setUnderLine(cardFirst, firstNum);
            setUnderLine(cardSecond, secondNum);
            setUnderLine(cardThird, thirdNum);
            setUnderLine(cardFourth, fourthNum);
        }


        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getUserInfo(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            JSONObject dataObject = object.optJSONObject("data");
                            myInfo.setMonth_point(dataObject.optString("month_point"));
                            myInfo.setToday_point(dataObject.optString("today_point"));

                            today_point.setText(Common.putComma(myInfo.getToday_point()));
                            month_point.setText(Common.putComma(myInfo.getMonth_point()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            String error = object.optString(Common.NETWORK_ERROR);
                            String dError = object.optString(Common.NETWORK_DISCONNECT);
                            if (!TextUtils.isEmpty(error)) {
                                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                            } else {
                                if (!TextUtils.isEmpty(dError)) {
                                    Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        layer.removeAllViews();
        layer.addView(layout);
    }

    private void initSurpriseLayer(RelativeLayout layer) {
        if (mContext == null || layer == null)
            return;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.aikbd_page_surprise_layer, null);

        TextView tab_my_point = layout.findViewById(R.id.tab_my_point);
        RecyclerView surprise_recyclerview = layout.findViewById(R.id.surprise_recyclerview);
        RelativeLayout btn_guide = layout.findViewById(R.id.btn_guide);
        ImageView guide_tip = layout.findViewById(R.id.guide_tip);
        ConstraintLayout guide_layer = layout.findViewById(R.id.guide_layer);
        TextView guide_content = layout.findViewById(R.id.guide_content);
        View topBg = layout.findViewById(R.id.top_bg);
        RelativeLayout surprise_bg = layout.findViewById(R.id.surprise_bg);

        guide_layer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                guide_layer.setVisibility(View.GONE);
            }
        });

        tab_my_point.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initMyLayer(layer);
                setRake("/keyboard/poppoint", "tap.mypointtap");
            }
        });

        surprise_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (guide_layer.getVisibility() == View.VISIBLE) {
                    guide_layer.setVisibility(View.GONE);
                }
            }
        });

        guide_content.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        surprise_recyclerview.setLayoutManager(manager);
        AikbdSurpriseAdapter adapter = new AikbdSurpriseAdapter(mContext, new AikbdSurpriseAdapter.Listener() {
            @Override
            public void onItemClicked() {
                if (guide_layer.getVisibility() == View.VISIBLE) {
                    guide_layer.setVisibility(View.GONE);
                }
            }
        });
        surprise_recyclerview.setAdapter(adapter);

        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getSurpriseList(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result && obj != null) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            JSONArray arr = object.optJSONArray("data");
                            if (arr != null && arr.length() > 0) {
                                ArrayList<SurpriseModel> modelArray = new ArrayList<>();
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject in_obj = arr.optJSONObject(i);
                                    if (in_obj != null) {
                                        SurpriseModel model = new SurpriseModel();
                                        model.setTitle(in_obj.optString("title"));
                                        model.setPoint(in_obj.optString("point"));
                                        model.setIcon(in_obj.optString("icon"));
                                        if ( !in_obj.optString("title").equals("쇼핑 검색") )
                                            modelArray.add(model);
                                    }
                                }
                                adapter.setItems(modelArray);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        btn_guide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (guide_layer != null && guide_layer.getVisibility() == View.GONE) {
                    guide_layer.setVisibility(View.VISIBLE);
                } else {
                    guide_layer.setVisibility(View.GONE);
                }
            }
        });

        layout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (guide_layer != null && guide_layer.getVisibility() == View.VISIBLE) {
                        guide_layer.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });

        layer.removeAllViews();
        layer.addView(layout);

        setRake("/keyboard/mypoint", "tap.poppointtap");
    }

    public void setEKeyboardHeightResize() {
        int height = SharedPreference.getInt(mContext, Common.PREF_SPACE_KEY_HEIGHT);
        /*
        LogPrint.d("keyboard_height setEKeyboardHeightResize height :: " + height + " , mOriginKeyboardHeight :: " + mOriginKeyboardHeight + " , mKeyboardHeight :: " + mKeyboardHeight);
        LogPrint.d("keyboard_height mContainer height :: " + mContainer.getHeight());
        LogPrint.d("keyboard_height mEmojiLayer height :: " + mEmojiLayer.getHeight());
        LogPrint.d("keyboard_height mEmoticonLayer height :: " + mEmoticonLayer.getHeight());
        LogPrint.d("keyboard_height mKeyboardLayer height :: " + mKeyboardLayer.getHeight());
        LogPrint.d("keyboard_height mUtilLayer height :: " + mUtilLayer.getHeight());
*/

        if (height > 0) {
            if (viewPager != null && mEKeyboardView != null) {
                viewPager.setPadding(0, 0, 0, height + Common.convertDpToPx(mContext, 4));
                ViewGroup.LayoutParams eParam = mEKeyboardView.getLayoutParams();
                eParam.height = height - Common.convertDpToPx(mContext, 4);
                mEKeyboardView.setLayoutParams(eParam);
            }

            if (emoticonViewPager != null && mEmoticonKeyboardView != null) {
                emoticonViewPager.setPadding(0, 0, 0, height + Common.convertDpToPx(mContext, 4));
                ViewGroup.LayoutParams etParam = mEmoticonKeyboardView.getLayoutParams();
                etParam.height = height - Common.convertDpToPx(mContext, 4);
                mEmoticonKeyboardView.setLayoutParams(etParam);
            }
        }
    }

    interface NewsCallbackListener {
        void onNewsReceived();
    }

    private void initCriteoAD() {
        LogPrint.d("kksskk criteo ad called");
        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);
        coupang_dy_layer.setVisibility(View.GONE);
        banner_ad_layer.setVisibility(View.GONE);
        mMobonAdLayerC.setVisibility(View.GONE);

        criteoData = new CriteoData();

        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getCriteoAd(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    boolean isDataSuccess = false;
                    try {
                        JSONObject object = (JSONObject) obj;
                        if ( object != null ) {
                            KeyboardLogPrint.d("criteo ad info :: " + object.toString());
                            String image_url = object.optString("image_url");
                            String click_url = object.optString("click_url");
                            String title = object.optString("title");
                            String description = object.optString("description");
                            String price = object.optString("price");
                            String cta = object.optString("cta");

                            criteoData.setImage_url(image_url);
                            criteoData.setClick_url(click_url);
                            criteoData.setTitle(title);
                            criteoData.setDescription(description);
                            criteoData.setPrice(price);
                            criteoData.setCta(cta);

                            JSONObject adchoiceObject = object.optJSONObject("adchoices");
                            if ( adchoiceObject != null ) {
                                String adchoice_image_url = adchoiceObject.optString("image_url");
                                String adchoice_click_url = adchoiceObject.optString("click_url");
                                criteoData.setAdchoice_click_url(adchoice_click_url);
                                criteoData.setAdchoice_image_url(adchoice_image_url);
                            }

                            ImageLoader.with(mContext).from(image_url).load(criteo_image);
                            ImageLoader.with(mContext).from(criteoData.getAdchoice_image_url()).load(criteo_adchoice);
                            criteo_product_name.setText(title);
                            criteo_price.setText(price);

                            if ( criteo_layer != null )
                                criteo_layer.setVisibility(View.VISIBLE);
                            coupang_dy_layer.setVisibility(View.GONE);
                            mMobonRewardAdLayerC.setVisibility(View.GONE);
                            reward_coupang_layer.setVisibility(View.GONE);
                            reward_joint_layer.setVisibility(View.GONE);
                            reward_joint_banner_layer.setVisibility(View.GONE);
                            mMobonRewardAdLayer.setVisibility(View.GONE);
                            if ( mixer_layer != null )
                                mixer_layer.setVisibility(View.GONE);

                            criteo_layer.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(click_url));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intent);
                                }
                            });

                            criteo_adchoice.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(criteoData.getAdchoice_click_url()));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intent);
                                }
                            });

                            criteo_close.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    setRake(currentPageId, "tap.closebtn");
                                    criteo_layer.setVisibility(View.GONE);
                                    sendViewCount();
                                }
                            });
                            isDataSuccess = true;
                        }
                        if ( !isDataSuccess ) {
                            loadMixerBanner(2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadMixerBanner(3);
                    }
                } else {
                    loadMixerBanner(4);
                }
            }
        });
    }

    private void initCoupang_DY_AD() {
        LogPrint.d("kksskk initCoupang_DY_AD C GONE");
        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);
        coupang_dy_layer.setVisibility(View.GONE);
        banner_ad_layer.setVisibility(View.GONE);
        mMobonAdLayerC.setVisibility(View.GONE);

        String dyData = SharedPreference.getString(mContext, Key.COUPANG_DY_DATA);
        try {
            if ( !TextUtils.isEmpty(dyData) ) {
                JSONObject oj = new JSONObject(dyData);
                if ( oj != null ) {
                    JSONArray ar = oj.optJSONArray("data");
                    if ( ar != null && ar.length() > 0 ) {
                        initDY_CoupangWithoutNetwork(oj);
                    } else {
                        initDY_CoupangWithNetwork();
                    }
                } else {
                    initDY_CoupangWithNetwork();
                }
            } else {
                initDY_CoupangWithNetwork();
            }

        } catch (Exception e) {
            e.printStackTrace();
            initDY_CoupangWithNetwork();
        }
    }

    private void initUtilMoneyTreeAD() {
        util_ad_layer.setVisibility(View.GONE);

        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getMoneyTreeAD(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        boolean isDataSuccess = false;
                        JSONObject object = (JSONObject) obj;
                        LogPrint.d("moneytree ad obj :: " + object.toString());
                        if ( object != null ) {
                            String productImage = object.optString("image");
                            String productUrl = object.optString("link") + "&uniq_key=" + MobonUtils.getAdid(mContext);
                            String productName = object.optString("product_name");
                            String productPrice = object.optString("price");
                            LogPrint.d("productImage :: " + productImage);
                            LogPrint.d("productUrl :: " + productUrl);
                            LogPrint.d("productName :: " + productName);
                            LogPrint.d("productPrice :: " + productPrice);
                            if ( !TextUtils.isEmpty(productImage) && !TextUtils.isEmpty(productUrl) && !TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productPrice)) {
                                int baseWidth = Common.convertDpToPx(mContext, 411);
                                int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                                if (mSoftKeyboard != null) {
                                    screenWidth = mSoftKeyboard.getScreenWidth();
                                }
                                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) util_reward_ad_layer.getLayoutParams();
                                LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                if ( isRewardPossible ) {
                                    if ( point <= 0 ) {
                                        util_reward_point.setVisibility(View.GONE);
                                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                    } else {
                                        util_reward_point.setVisibility(View.VISIBLE);
                                        if ( screenWidth < baseWidth) {
                                            LogPrint.d("left ~~~");
                                            param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                        } else {
                                            LogPrint.d("right ~~~");
                                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                        }
                                    }
                                } else {
                                    util_reward_point.setVisibility(View.GONE);
                                    param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                }
                                util_reward_ad_layer.setLayoutParams(param);

                                ImageLoader.with(mContext).from(productImage).load(util_reward_coupang_image);
                                util_coupang_product_name.setText(productName);
                                util_coupang_price.setText(Common.putComma(productPrice) + "원");
                                util_coupang_rocket.setVisibility(View.GONE);

                                util_r_leftBg.setVisibility(View.VISIBLE);
                                util_r_rightBg.setVisibility(View.VISIBLE);
                                util_r_leftBg.setBackgroundColor(Color.WHITE);
                                util_r_rightBg.setBackgroundColor(Color.WHITE);

                                util_reward_point.setText(point+"P");
                                util_rest_layer.setVisibility(View.VISIBLE);

                                util_ad_layer.setVisibility(View.GONE);
                                util_mobon_reward_ad_layer_c.setVisibility(View.VISIBLE);
                                util_reward_ad_layer.setVisibility(View.VISIBLE);
                                util_mobon_reward_ad_layer.setVisibility(View.GONE);
                                util_reward_coupang_layer.setVisibility(View.VISIBLE);

                                util_mobon_reward_ad_layer_c.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // 9월 epic 리워드 광고 클릭 시 깜짝포인트 제공하지 않도록 수정
                                        //sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                        sendRewardPoint(COUPANG_SC);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(productUrl));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                        LogPrint.d(" call reward_thezoom_click");
                                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                                        task.postStats("reward_thezoom_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                            @Override
                                            public void onResponse(boolean result, Object obj) {

                                            }
                                        });
                                    }
                                });
                                isDataSuccess = true;
                                LogPrint.d(" call reward_thezoom_eprs");
                                CustomAsyncTask task = new CustomAsyncTask(mContext);
                                task.postStats("reward_thezoom_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                    @Override
                                    public void onResponse(boolean result, Object obj) {

                                    }
                                });
                            }
                        }
                        if ( !isDataSuccess ) {
                            sendViewCount();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendViewCount();
                    }
                } else {
                    sendViewCount();
                }
            }
        });
    }

    private void initUtilRewardNews() {
        util_ad_layer.setVisibility(View.GONE);

        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getRewardNews("Y", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            boolean rt = object.optBoolean("Result");
                            if (rt) {
                                JSONObject in_obj = object.optJSONObject("newsList");
                                if (in_obj != null) {
                                    String news_title = in_obj.optString("news_title");
                                    String link_url = in_obj.optString("link_url");
                                    String news_img_url = in_obj.optString("news_img_url");
                                    String object_id = in_obj.optString("object_id");
                                    String site_name = in_obj.optString("site_name");
                                    String root_domain = in_obj.optString("root_domain");

                                    int baseWidth = Common.convertDpToPx(mContext, 411);
                                    int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                                    if (mSoftKeyboard != null) {
                                        screenWidth = mSoftKeyboard.getScreenWidth();
                                    }
                                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) util_reward_ad_layer.getLayoutParams();
                                    LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                    int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                    if ( isRewardPossible ) {
                                        if ( point <= 0 ) {
                                            util_reward_point.setVisibility(View.GONE);
                                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                        } else {
                                            util_reward_point.setVisibility(View.VISIBLE);
                                            if ( screenWidth < baseWidth) {
                                                LogPrint.d("left ~~~");
                                                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                            } else {
                                                LogPrint.d("right ~~~");
                                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                            }
                                        }
                                    } else {
                                        util_reward_point.setVisibility(View.GONE);
                                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                    }

                                    util_reward_ad_layer.setLayoutParams(param);
                                    util_reward_coupang_image.setVisibility(View.GONE);

                                    String addedTitle = "[포인트 뉴스] " + news_title;

                                    SpannableString spannableString = new SpannableString(addedTitle);
                                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#aa23e9")), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    LogPrint.d("spannable news :: " + spannableString);
                                    util_coupang_top_margin.setVisibility(View.GONE);
                                    util_coupang_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13.0f);
                                    util_coupang_product_name.setText(spannableString);
                                    util_coupang_price.setVisibility(View.GONE);

                                    util_coupang_rocket.setVisibility(View.GONE);

                                    util_r_leftBg.setVisibility(View.VISIBLE);
                                    util_r_rightBg.setVisibility(View.VISIBLE);
                                    util_r_leftBg.setBackgroundColor(Color.WHITE);
                                    util_r_rightBg.setBackgroundColor(Color.WHITE);

                                    util_reward_point.setText(point+"P");
                                    util_rest_layer.setVisibility(View.VISIBLE);

                                    util_ad_layer.setVisibility(View.GONE);
                                    util_mobon_reward_ad_layer_c.setVisibility(View.VISIBLE);
                                    util_reward_ad_layer.setVisibility(View.VISIBLE);
                                    util_mobon_reward_ad_layer.setVisibility(View.GONE);
                                    util_reward_coupang_layer.setVisibility(View.VISIBLE);

                                    util_mobon_reward_ad_layer_c.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                                            // sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                            LogPrint.d(" call reward_news_click");
                                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                                            task.postStats("reward_news_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                @Override
                                                public void onResponse(boolean result, Object obj) {

                                                }
                                            });

                                            Intent intent = new Intent(mContext, RewardAdWebViewActivity.class);
                                            intent.putExtra("reward_link", link_url);
                                            intent.putExtra("sc", "-1");
                                            intent.putExtra("isNews", true);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.startActivity(intent);
                                        }
                                    });

                                    task.postStats("reward_news_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                        @Override
                                        public void onResponse(boolean result, Object obj) {

                                        }
                                    });
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

    private void initUtilCoupangAD() {
        util_ad_layer.setVisibility(View.GONE);

        String dyData = SharedPreference.getString(mContext, Key.COUPANG_DY_REWARD_DATA);
        try {
            if ( !TextUtils.isEmpty(dyData) ) {
                JSONObject oj = new JSONObject(dyData);
                if ( oj != null ) {
                    JSONArray ar = oj.optJSONArray("data");
                    if ( ar != null && ar.length() > 0 ) {
                        initUtilCoupangWithoutNetwork(oj);
                    } else {
                        initUtilCoupangWithNetwork();
                    }
                } else {
                    initUtilCoupangWithNetwork();
                }
            } else {
                initUtilCoupangWithNetwork();
            }

        } catch (Exception e) {
            e.printStackTrace();
            initUtilCoupangWithNetwork();
        }
    }

    private void initMoneyTreeAD() {
        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);
        coupang_dy_layer.setVisibility(View.GONE);
        banner_ad_layer.setVisibility(View.GONE);
        mMobonAdLayerC.setVisibility(View.GONE);
        LogPrint.d("kksskk initMoneyTreeAD C GONE");
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getMoneyTreeAD(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        boolean isDataSuccess = false;
                        JSONObject object = (JSONObject) obj;
                        if ( object != null ) {
                            String productImage = object.optString("image");
                            String productUrl = object.optString("link") + "&uniq_key=" + MobonUtils.getAdid(mContext);
                            String productName = object.optString("product_name");
                            String productPrice = object.optString("price");
                            LogPrint.d("productImage :: " + productImage);
                            LogPrint.d("productUrl :: " + productUrl);
                            LogPrint.d("productName :: " + productName);
                            LogPrint.d("productPrice :: " + productPrice);

                            if ( !TextUtils.isEmpty(productImage) && !TextUtils.isEmpty(productUrl) && !TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productPrice)) {
                                int baseWidth = Common.convertDpToPx(mContext, 411);
                                int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                                if (mSoftKeyboard != null) {
                                    screenWidth = mSoftKeyboard.getScreenWidth();
                                }
                                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) reward_ad_layer.getLayoutParams();
                                LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                if ( isRewardPossible ) {
                                    if ( point <= 0 ) {
                                        reward_point.setVisibility(View.GONE);
                                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                    } else {
                                        reward_point.setVisibility(View.VISIBLE);
                                        if ( screenWidth < baseWidth) {
                                            LogPrint.d("left ~~~");
                                            param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                        } else {
                                            LogPrint.d("right ~~~");
                                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                        }
                                    }
                                } else {
                                    reward_point.setVisibility(View.GONE);
                                    param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                }
                                reward_ad_layer.setLayoutParams(param);
                                reward_coupang_image.setVisibility(View.VISIBLE);
                                ImageLoader.with(mContext).from(productImage).load(reward_coupang_image);
                                coupang_top_margin.setVisibility(View.VISIBLE);
                                coupang_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f);
                                coupang_product_name.setText(productName);
                                coupang_price.setVisibility(View.VISIBLE);
                                coupang_price.setText(Common.putComma(productPrice) + "원");
                                coupang_rocket.setVisibility(View.GONE);

                                r_leftBg.setVisibility(View.VISIBLE);
                                r_rightBg.setVisibility(View.VISIBLE);
                                r_leftBg.setBackgroundColor(Color.WHITE);
                                r_rightBg.setBackgroundColor(Color.WHITE);

                                reward_point.setText(point+"P");
                                rest_layer.setVisibility(View.VISIBLE);

                                mMobonRewardAdLayerC.setVisibility(View.VISIBLE);
                                reward_coupang_layer.setVisibility(View.VISIBLE);
                                mMobonRewardAdLayer.setVisibility(View.GONE);

                                mMobonRewardAdLayerC.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // 9월 epic 리워드 광고 클릭 시 깜짝포인트 제공하지 않도록 수정
                                        //sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                        sendRewardPoint(COUPANG_SC);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(productUrl));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                        LogPrint.d(" call reward_thezoom_click");
                                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                                        task.postStats("reward_thezoom_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                            @Override
                                            public void onResponse(boolean result, Object obj) {

                                            }
                                        });
                                    }
                                });
                                isDataSuccess = true;
                                LogPrint.d(" call reward_thezoom_eprs");
                                CustomAsyncTask task = new CustomAsyncTask(mContext);
                                task.postStats("reward_thezoom_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                    @Override
                                    public void onResponse(boolean result, Object obj) {

                                    }
                                });
                            }
                        }
                        if ( !isDataSuccess ) {
                            sendViewCount();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendViewCount();
                    }
                } else {
                    sendViewCount();
                }
            }
        });
    }

    private void initRewardNews() {
        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);
        coupang_dy_layer.setVisibility(View.GONE);
        banner_ad_layer.setVisibility(View.GONE);
        mMobonAdLayerC.setVisibility(View.GONE);
        LogPrint.d("kksskk initRewardNews C GONE");
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getRewardNews("Y", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            boolean rt = object.optBoolean("Result");
                            if (rt) {
                                JSONObject in_obj = object.optJSONObject("newsList");
                                if (in_obj != null) {

                                    dummyImage.setFocusable(true);

                                    String news_title = in_obj.optString("news_title");
                                    String link_url = in_obj.optString("link_url");
                                    String news_img_url = in_obj.optString("news_img_url");
                                    String object_id = in_obj.optString("object_id");
                                    String site_name = in_obj.optString("site_name");
                                    String root_domain = in_obj.optString("root_domain");

                                    int baseWidth = Common.convertDpToPx(mContext, 411);
                                    int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                                    if (mSoftKeyboard != null) {
                                        screenWidth = mSoftKeyboard.getScreenWidth();
                                    }
                                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) reward_ad_layer.getLayoutParams();
                                    LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                    int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                    if ( isRewardPossible ) {
                                        if ( point <= 0 ) {
                                            reward_point.setVisibility(View.GONE);
                                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                        } else {
                                            reward_point.setVisibility(View.VISIBLE);
                                            if ( screenWidth < baseWidth) {
                                                LogPrint.d("left ~~~");
                                                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                            } else {
                                                LogPrint.d("right ~~~");
                                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                            }
                                        }
                                    } else {
                                        reward_point.setVisibility(View.GONE);
                                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                    }

                                    reward_ad_layer.setLayoutParams(param);
                                    reward_joint_image.setVisibility(View.GONE);

                                    String addedTitle = "[포인트 뉴스] " + news_title;

                                    SpannableString spannableString = new SpannableString(addedTitle);
                                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#aa23e9")), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    LogPrint.d("spannable news :: " + spannableString);
                                    joint_top_margin.setVisibility(View.GONE);
                                    joint_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13.0f);
                                    joint_product_name.setText(spannableString);
                                    joint_price.setVisibility(View.GONE);

                                    joint_logo.setVisibility(View.GONE);
                                    joint_adchoice.setVisibility(View.GONE);

                                    r_leftBg.setVisibility(View.VISIBLE);
                                    r_rightBg.setVisibility(View.VISIBLE);
                                    r_leftBg.setBackgroundColor(Color.WHITE);
                                    r_rightBg.setBackgroundColor(Color.WHITE);

                                    reward_point.setText(point+"P");
                                    rest_layer.setVisibility(View.VISIBLE);

                                    mMobonRewardAdLayerC.setVisibility(View.VISIBLE);
                                    reward_joint_layer.setVisibility(View.VISIBLE);
                                    mMobonRewardAdLayer.setVisibility(View.GONE);

                                    mMobonRewardAdLayerC.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                                            // sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                            LogPrint.d(" call reward_news_click");
                                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                                            task.postStats("reward_news_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                @Override
                                                public void onResponse(boolean result, Object obj) {

                                                }
                                            });

                                            Intent intent = new Intent(mContext, RewardAdWebViewActivity.class);
                                            intent.putExtra("reward_link", link_url);
                                            intent.putExtra("sc", "-1");
                                            intent.putExtra("isNews", true);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.startActivity(intent);
                                        }
                                    });

                                    task.postStats("reward_news_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                        @Override
                                        public void onResponse(boolean result, Object obj) {

                                        }
                                    });


                                    dummyImage.setContentDescription("\u00A0");

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

    private void initUtilJointReward() {
        LogPrint.d("initUtilJointReward");
        util_ad_layer.setVisibility(View.GONE);
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getJointRewardAD(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        boolean isDataSuccess = false;
                        JointRewardData rewardData = null;
                        if ( object != null ) {
                            LogPrint.d("util joint reward object :: " + object.toString());
                            boolean rt = object.optBoolean("Result");
                            if ( rt ) {
                                JSONObject adObject = object.optJSONObject("adData");
                                if ( adObject != null ) {
                                    rewardData = new JointRewardData();
                                    String productId = adObject.optString("productId");
                                    String image = adObject.optString("image");
                                    String title = adObject.optString("title");
                                    String description = adObject.optString("description");
                                    String price = adObject.optString("price");
                                    String click_url = adObject.optString("click_url");
                                    String logo_url = adObject.optString("logo_url");
                                    String kind = adObject.optString("kind");
                                    String type = adObject.optString("type");
                                    String click_key_name = adObject.optString("click_key_name");
                                    String eprs_key_name = adObject.optString("eprs_key_name");
                                    rewardData.setProductId(productId);
                                    rewardData.setImage(image);
                                    rewardData.setTitle(title);
                                    rewardData.setDescription(description);
                                    rewardData.setPrice(price);
                                    rewardData.setClick_url(click_url);
                                    rewardData.setLogo_url(logo_url);
                                    rewardData.setKind(kind);
                                    rewardData.setType(type);
                                    rewardData.setClick_key_name(click_key_name);
                                    rewardData.setEprs_key_name(eprs_key_name);
                                    JSONObject adchoicesObj = adObject.optJSONObject("adchoices");
                                    if ( adchoicesObj != null ) {
                                        AdChoices adChoices = null;
                                        String choice_img = adchoicesObj.optString("image_url");
                                        String choice_click = adchoicesObj.optString("click_url");
                                        if ( !TextUtils.isEmpty(choice_img) && !TextUtils.isEmpty(choice_click) ) {
                                            adChoices = new AdChoices();
                                            adChoices.setImage_url(choice_img);
                                            adChoices.setClick_url(choice_click);
                                            rewardData.setAdChoices(adChoices);
                                        }
                                    }

                                    final JointRewardData fRewardData = rewardData;

                                    // 여기에서 data를 이용한 ui 그리기
                                    int baseWidth = Common.convertDpToPx(mContext, 411);
                                    int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                                    if (mSoftKeyboard != null) {
                                        screenWidth = mSoftKeyboard.getScreenWidth();
                                    }
                                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) util_reward_ad_layer.getLayoutParams();
                                    LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                    int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                    if ( isRewardPossible ) {
                                        if ( point <= 0 ) {
                                            util_reward_point.setVisibility(View.GONE);
                                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                        } else {
                                            util_reward_point.setVisibility(View.VISIBLE);
                                            if ( screenWidth < baseWidth) {
                                                LogPrint.d("left ~~~");
                                                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                            } else {
                                                LogPrint.d("right ~~~");
                                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                            }
                                        }
                                    } else {
                                        util_reward_point.setVisibility(View.GONE);
                                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                    }

                                    util_reward_ad_layer.setLayoutParams(param);

                                    if ( "banner".equals(type) ) {
                                        try {
                                            ImageModule.with(mContext).asBitmap().load(rewardData.getImage()).into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    util_joint_banner_image.setImageBitmap(resource);
                                                    util_joint_banner_image.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                    int leftColor = Common.GetColors(resource, true);
                                                    int rightColor = Common.GetColors(resource, false);
                                                    LogPrint.d("leftColor :: " + leftColor + " , rightColor :: " + rightColor);
                                                    if ( util_r_leftBg != null && util_r_rightBg != null ) {
                                                        util_r_leftBg.setVisibility(View.VISIBLE);
                                                        util_r_rightBg.setVisibility(View.VISIBLE);
                                                        util_r_leftBg.setBackgroundColor(leftColor);
                                                        util_r_rightBg.setBackgroundColor(rightColor);

                                                        util_reward_point.setText(point+"P");
                                                        util_rest_layer.setVisibility(View.VISIBLE);

                                                        final AdChoices a_choices = fRewardData.getAdChoices();

                                                        if ( a_choices != null ) {
                                                            ImageLoader.with(mContext).from(a_choices.getImage_url()).load(util_joint_banner_adchoice);
                                                            util_joint_banner_adchoice.setVisibility(View.VISIBLE);

                                                            util_joint_banner_adchoice.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                    intent.setData(Uri.parse(a_choices.getClick_url()));
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    mContext.startActivity(intent);
                                                                }
                                                            });
                                                        } else {
                                                            joint_banner_adchoice.setVisibility(View.GONE);
                                                        }

                                                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                                                        task.postStats(fRewardData.getEprs_key_name(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                            @Override
                                                            public void onResponse(boolean result, Object obj) {

                                                            }
                                                        });

                                                        util_ad_layer.setVisibility(View.GONE);
                                                        util_mobon_reward_ad_layer_c.setVisibility(View.VISIBLE);
                                                        util_reward_ad_layer.setVisibility(View.VISIBLE);
                                                        util_mobon_reward_ad_layer.setVisibility(View.GONE);
                                                        util_reward_coupang_layer.setVisibility(View.GONE);
                                                        util_reward_joint_layer.setVisibility(View.GONE);
                                                        util_reward_joint_banner_layer.setVisibility(View.VISIBLE);

                                                        util_mobon_reward_ad_layer_c.setOnClickListener(new OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                                                                // sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                                if ( isRewardPossible ) {
                                                                    sendRewardPoint(COUPANG_SC);
                                                                }
                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                intent.setData(Uri.parse(fRewardData.getClick_url()));
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                mContext.startActivity(intent);
                                                                LogPrint.d(" call reward_banner_joint_click");
                                                                CustomAsyncTask task = new CustomAsyncTask(mContext);
                                                                task.postStats(fRewardData.getClick_key_name(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                                    @Override
                                                                    public void onResponse(boolean result, Object obj) {

                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        util_reward_joint_image.setVisibility(View.VISIBLE);
                                        ImageLoader.with(mContext).from(rewardData.getImage()).load(util_reward_joint_image);
                                        util_joint_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f);
                                        util_joint_product_name.setText(rewardData.getTitle());

                                        if ( !TextUtils.isEmpty(price) && !TextUtils.isEmpty(logo_url) ) {
                                            util_joint_top_margin.setVisibility(View.VISIBLE);
                                            util_joint_logo.setVisibility(View.VISIBLE);
                                            util_joint_price.setVisibility(View.VISIBLE);
                                            util_joint_price.setText(price);
                                            ImageLoader.with(mContext).from(rewardData.getLogo_url()).load(util_joint_logo);
                                        } else if ( !TextUtils.isEmpty(price) && TextUtils.isEmpty(logo_url) ) {
                                            util_joint_top_margin.setVisibility(View.VISIBLE);
                                            util_joint_logo.setVisibility(View.INVISIBLE);
                                            util_joint_price.setVisibility(View.VISIBLE);
                                            util_joint_price.setText(price);
                                        } else if ( TextUtils.isEmpty(price) && !TextUtils.isEmpty(logo_url) ) {
                                            util_joint_top_margin.setVisibility(View.VISIBLE);
                                            util_joint_logo.setVisibility(View.VISIBLE);
                                            util_joint_price.setVisibility(View.INVISIBLE);
                                            util_joint_price.setText("");
                                            ImageLoader.with(mContext).from(rewardData.getLogo_url()).load(util_joint_logo);
                                        } else {
                                            util_joint_top_margin.setVisibility(View.GONE);
                                            util_joint_logo.setVisibility(View.GONE);
                                            util_joint_price.setVisibility(View.GONE);
                                            util_joint_price.setText("");
                                        }

                                        util_r_leftBg.setVisibility(View.VISIBLE);
                                        util_r_rightBg.setVisibility(View.VISIBLE);
                                        util_r_leftBg.setBackgroundColor(Color.WHITE);
                                        util_r_rightBg.setBackgroundColor(Color.WHITE);

                                        util_reward_point.setText(point+"P");
                                        util_rest_layer.setVisibility(View.VISIBLE);

                                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                                        task.postStats(fRewardData.getEprs_key_name(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                            @Override
                                            public void onResponse(boolean result, Object obj) {

                                            }
                                        });

                                        util_ad_layer.setVisibility(View.GONE);
                                        util_mobon_reward_ad_layer_c.setVisibility(View.VISIBLE);
                                        util_reward_ad_layer.setVisibility(View.VISIBLE);
                                        util_mobon_reward_ad_layer.setVisibility(View.GONE);
                                        util_reward_coupang_layer.setVisibility(View.GONE);
                                        util_reward_joint_layer.setVisibility(View.VISIBLE);
                                        util_reward_joint_banner_layer.setVisibility(View.GONE);

                                        final AdChoices a_choices = fRewardData.getAdChoices();

                                        if ( a_choices != null ) {
                                            ImageLoader.with(mContext).from(a_choices.getImage_url()).load(util_joint_adchoice);
                                            util_joint_adchoice.setVisibility(View.VISIBLE);

                                            util_joint_adchoice.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(a_choices.getClick_url()));
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mContext.startActivity(intent);
                                                }
                                            });
                                        } else {
                                            util_joint_adchoice.setVisibility(View.GONE);
                                        }

                                        util_mobon_reward_ad_layer_c.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                                                // sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                if ( isRewardPossible ) {
                                                    sendRewardPoint(COUPANG_SC);
                                                }
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(fRewardData.getClick_url()));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mContext.startActivity(intent);
                                                LogPrint.d(" call reward_joint_click");
                                                CustomAsyncTask task = new CustomAsyncTask(mContext);
                                                task.postStats(fRewardData.getClick_key_name(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                    @Override
                                                    public void onResponse(boolean result, Object obj) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                    isDataSuccess = true;
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

    private void initJointReward() {
        LogPrint.d("initJointReward");
        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);
        coupang_dy_layer.setVisibility(View.GONE);
        banner_ad_layer.setVisibility(View.GONE);
        mMobonAdLayerC.setVisibility(View.GONE);

        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getJointRewardAD(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        boolean isDataSuccess = false;
                        JointRewardData rewardData = null;
                        if ( object != null ) {
                            LogPrint.d("joint reward object :: " + object.toString());
                            boolean rt = object.optBoolean("Result");
                            if ( rt ) {
                                JSONObject adObject = object.optJSONObject("adData");
                                if ( adObject != null ) {
                                    rewardData = new JointRewardData();
                                    String productId = adObject.optString("productId");
                                    String image = adObject.optString("image");
                                    String title = adObject.optString("title");
                                    String description = adObject.optString("description");
                                    String price = adObject.optString("price");
                                    String click_url = adObject.optString("click_url");
                                    String logo_url = adObject.optString("logo_url");
                                    String kind = adObject.optString("kind");
                                    String type = adObject.optString("type");
                                    String click_key_name = adObject.optString("click_key_name");
                                    String eprs_key_name = adObject.optString("eprs_key_name");
                                    rewardData.setProductId(productId);
                                    rewardData.setImage(image);
                                    rewardData.setTitle(title);
                                    rewardData.setDescription(description);
                                    rewardData.setPrice(price);
                                    rewardData.setClick_url(click_url);
                                    rewardData.setLogo_url(logo_url);
                                    rewardData.setKind(kind);
                                    rewardData.setType(type);
                                    rewardData.setClick_key_name(click_key_name);
                                    rewardData.setEprs_key_name(eprs_key_name);
                                    JSONObject adchoicesObj = adObject.optJSONObject("adchoices");
                                    if ( adchoicesObj != null ) {
                                        AdChoices adChoices = null;
                                        String choice_img = adchoicesObj.optString("image_url");
                                        String choice_click = adchoicesObj.optString("click_url");
                                        if ( !TextUtils.isEmpty(choice_img) && !TextUtils.isEmpty(choice_click) ) {
                                            adChoices = new AdChoices();
                                            adChoices.setImage_url(choice_img);
                                            adChoices.setClick_url(choice_click);
                                            rewardData.setAdChoices(adChoices);
                                        }
                                    }

                                    final JointRewardData fRewardData = rewardData;

                                    // 여기에서 data를 이용한 ui 그리기
                                    int baseWidth = Common.convertDpToPx(mContext, 411);
                                    int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                                    if (mSoftKeyboard != null) {
                                        screenWidth = mSoftKeyboard.getScreenWidth();
                                    }
                                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) reward_ad_layer.getLayoutParams();
                                    LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                    int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                    if ( isRewardPossible ) {
                                        if ( point <= 0 ) {
                                            reward_point.setVisibility(View.GONE);
                                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                        } else {
                                            reward_point.setVisibility(View.VISIBLE);
                                            if ( screenWidth < baseWidth) {
                                                LogPrint.d("left ~~~");
                                                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                            } else {
                                                LogPrint.d("right ~~~");
                                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                            }
                                        }
                                    } else {
                                        reward_point.setVisibility(View.GONE);
                                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                    }

                                    reward_ad_layer.setLayoutParams(param);

                                    if ( "banner".equals(type) ) {
                                        try {
                                            ImageModule.with(mContext).asBitmap().load(rewardData.getImage()).into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    joint_banner_image.setImageBitmap(resource);
                                                    joint_banner_image.setBackgroundColor(Color.parseColor("#00ffffff"));
                                                    int leftColor = Common.GetColors(resource, true);
                                                    int rightColor = Common.GetColors(resource, false);
                                                    LogPrint.d("leftColor :: " + leftColor + " , rightColor :: " + rightColor);
                                                    if ( r_leftBg != null && r_rightBg != null ) {
                                                        r_leftBg.setVisibility(View.VISIBLE);
                                                        r_rightBg.setVisibility(View.VISIBLE);
                                                        r_leftBg.setBackgroundColor(leftColor);
                                                        r_rightBg.setBackgroundColor(rightColor);

                                                        reward_point.setText(point+"P");
                                                        rest_layer.setVisibility(View.VISIBLE);

                                                        final AdChoices a_choices = fRewardData.getAdChoices();

                                                        if ( a_choices != null ) {
                                                            ImageLoader.with(mContext).from(a_choices.getImage_url()).load(joint_banner_adchoice);
                                                            joint_banner_adchoice.setVisibility(View.VISIBLE);

                                                            joint_banner_adchoice.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                    intent.setData(Uri.parse(a_choices.getClick_url()));
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    mContext.startActivity(intent);
                                                                }
                                                            });
                                                        } else {
                                                            joint_banner_adchoice.setVisibility(View.GONE);
                                                        }

                                                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                                                        task.postStats(fRewardData.getEprs_key_name(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                            @Override
                                                            public void onResponse(boolean result, Object obj) {

                                                            }
                                                        });

                                                        mMobonRewardAdLayerC.setVisibility(View.VISIBLE);
                                                        reward_coupang_layer.setVisibility(View.GONE);
                                                        reward_joint_layer.setVisibility(View.GONE);
                                                        reward_joint_banner_layer.setVisibility(View.VISIBLE);
                                                        mMobonRewardAdLayer.setVisibility(View.GONE);

                                                        mMobonRewardAdLayerC.setOnClickListener(new OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                                                                // sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                                if ( isRewardPossible ) {
                                                                    sendRewardPoint(COUPANG_SC);
                                                                }
                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                intent.setData(Uri.parse(fRewardData.getClick_url()));
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                mContext.startActivity(intent);
                                                                LogPrint.d(" call reward_banner_joint_click");
                                                                CustomAsyncTask task = new CustomAsyncTask(mContext);
                                                                task.postStats(fRewardData.getClick_key_name(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                                    @Override
                                                                    public void onResponse(boolean result, Object obj) {

                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        reward_joint_image.setVisibility(View.VISIBLE);
                                        ImageLoader.with(mContext).from(rewardData.getImage()).load(reward_joint_image);
                                        joint_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f);
                                        joint_product_name.setText(rewardData.getTitle());

                                        if ( !TextUtils.isEmpty(price) && !TextUtils.isEmpty(logo_url) ) {
                                            joint_top_margin.setVisibility(View.VISIBLE);
                                            joint_logo.setVisibility(View.VISIBLE);
                                            joint_price.setVisibility(View.VISIBLE);
                                            joint_price.setText(price);
                                            ImageLoader.with(mContext).from(rewardData.getLogo_url()).load(joint_logo);
                                        } else if ( !TextUtils.isEmpty(price) && TextUtils.isEmpty(logo_url) ) {
                                            joint_top_margin.setVisibility(View.VISIBLE);
                                            joint_logo.setVisibility(View.INVISIBLE);
                                            joint_price.setVisibility(View.VISIBLE);
                                            joint_price.setText(price);
                                        } else if ( TextUtils.isEmpty(price) && !TextUtils.isEmpty(logo_url) ) {
                                            joint_top_margin.setVisibility(View.VISIBLE);
                                            joint_logo.setVisibility(View.VISIBLE);
                                            joint_price.setVisibility(View.INVISIBLE);
                                            joint_price.setText("");
                                            ImageLoader.with(mContext).from(rewardData.getLogo_url()).load(joint_logo);
                                        } else {
                                            joint_top_margin.setVisibility(View.GONE);
                                            joint_logo.setVisibility(View.GONE);
                                            joint_price.setVisibility(View.GONE);
                                            joint_price.setText("");
                                        }

                                        r_leftBg.setVisibility(View.VISIBLE);
                                        r_rightBg.setVisibility(View.VISIBLE);
                                        r_leftBg.setBackgroundColor(Color.WHITE);
                                        r_rightBg.setBackgroundColor(Color.WHITE);

                                        reward_point.setText(point+"P");
                                        rest_layer.setVisibility(View.VISIBLE);

                                        CustomAsyncTask task = new CustomAsyncTask(mContext);
                                        task.postStats(fRewardData.getEprs_key_name(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                            @Override
                                            public void onResponse(boolean result, Object obj) {

                                            }
                                        });

                                        mMobonRewardAdLayerC.setVisibility(View.VISIBLE);
                                        reward_coupang_layer.setVisibility(View.GONE);
                                        reward_joint_layer.setVisibility(View.VISIBLE);
                                        reward_joint_banner_layer.setVisibility(View.GONE);
                                        mMobonRewardAdLayer.setVisibility(View.GONE);

                                        final AdChoices a_choices = fRewardData.getAdChoices();

                                        if ( a_choices != null ) {
                                            ImageLoader.with(mContext).from(a_choices.getImage_url()).load(joint_adchoice);
                                            joint_adchoice.setVisibility(View.VISIBLE);

                                            joint_adchoice.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(a_choices.getClick_url()));
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mContext.startActivity(intent);
                                                }
                                            });
                                        } else {
                                            joint_adchoice.setVisibility(View.GONE);
                                        }

                                        mMobonRewardAdLayerC.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                                                // sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                                if ( isRewardPossible ) {
                                                    sendRewardPoint(COUPANG_SC);
                                                }
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(fRewardData.getClick_url()));
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mContext.startActivity(intent);
                                                LogPrint.d(" call reward_joint_click");
                                                CustomAsyncTask task = new CustomAsyncTask(mContext);
                                                task.postStats(fRewardData.getClick_key_name(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                    @Override
                                                    public void onResponse(boolean result, Object obj) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                    isDataSuccess = true;
                                }
                            }
                        }
                        if ( !isDataSuccess ) {
                            sendViewCount();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendViewCount();
                    }
                } else {
                    sendViewCount();
                }
            }
        });
    }

    private void initCoupangAD() {
        if ( criteo_layer != null )
            criteo_layer.setVisibility(View.GONE);
        if ( mixer_layer != null )
            mixer_layer.setVisibility(View.GONE);
        coupang_dy_layer.setVisibility(View.GONE);
        banner_ad_layer.setVisibility(View.GONE);
        mMobonAdLayerC.setVisibility(View.GONE);
        LogPrint.d("kksskk initCoupangAD C GONE");
        String dyData = SharedPreference.getString(mContext, Key.COUPANG_DY_REWARD_DATA);
        try {
            if ( !TextUtils.isEmpty(dyData) ) {
                JSONObject oj = new JSONObject(dyData);
                if ( oj != null ) {
                    JSONArray ar = oj.optJSONArray("data");
                    if ( ar != null && ar.length() > 0 ) {
                        initCoupangWithoutNetwork(oj);
                    } else {
                        initCoupangWithNetwork();
                    }
                } else {
                    initCoupangWithNetwork();
                }
            } else {
                initCoupangWithNetwork();
            }

        } catch (Exception e) {
            e.printStackTrace();
            initCoupangWithNetwork();
        }


    }

    private boolean removeFirstArray(JSONObject object) {
        try {
            JSONObject tempObj = object;
            JSONArray tempArr = tempObj.optJSONArray("data");
            if ( tempArr != null && tempArr.length() > 0 ) {
                tempArr.remove(0);
                tempObj.remove("data");
                tempObj.put("data", tempArr);
                LogPrint.d("tempObj :: " + tempObj.toString());
                SharedPreference.setString(mContext, Key.COUPANG_DY_DATA, tempObj.toString());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeBrandFirstArray(JSONObject object) {
        try {
            JSONObject tempObj = object;
            JSONArray tempArr = tempObj.optJSONArray("data");
            if ( tempArr != null && tempArr.length() > 0 ) {
                tempArr.remove(0);
                tempObj.remove("data");
                tempObj.put("data", tempArr);
                LogPrint.d("tempObj :: " + tempObj.toString());
                SharedPreference.setString(mContext, Key.COUPANG_DY_BRAND_DATA, tempObj.toString());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeRewardFirstArray(JSONObject object) {
        try {
            JSONObject tempObj = object;
            JSONArray tempArr = tempObj.optJSONArray("data");
            if ( tempArr != null && tempArr.length() > 0 ) {
                tempArr.remove(0);
                tempObj.remove("data");
                tempObj.put("data", tempArr);
                LogPrint.d("tempObj :: " + tempObj.toString());
                SharedPreference.setString(mContext, Key.COUPANG_DY_REWARD_DATA, tempObj.toString());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void initDY_CoupangWithoutNetwork(JSONObject object) {
        boolean isDataSuccess = false;
        if ( object != null ) {
            JSONArray array = object.optJSONArray("data");
            if ( array != null && array.length() > 0 ) {
                SharedPreference.setString(mContext, Key.COUPANG_DY_DATA, object.toString());
                JSONObject inObj = array.optJSONObject(0);
                if ( inObj != null ) {
                    String productName = inObj.optString("productName");
                    String productPrice = inObj.optString("productPrice");
                    String productImage = inObj.optString("productImage");
                    String productUrl = inObj.optString("productUrl");
                    boolean isRocket = inObj.optBoolean("isRocket");
                    boolean isFreeShipping = inObj.optBoolean("isFreeShipping");

                    ImageLoader.with(mContext).from(productImage).load(dy_coupang_image);
                    dy_coupang_product_name.setText(productName);
                    dy_coupang_price.setText(Common.putComma(productPrice) + "원");

                    dy_coupang_rocket.setVisibility(View.VISIBLE);
                    if ( isRocket )
                        dy_coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_rocket);
                    else
                        dy_coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_logo);

                    coupang_dy_layer.setVisibility(View.VISIBLE);
                    mMobonRewardAdLayerC.setVisibility(View.GONE);
                    reward_coupang_layer.setVisibility(View.GONE);
                    reward_joint_layer.setVisibility(View.GONE);
                    reward_joint_banner_layer.setVisibility(View.GONE);
                    mMobonRewardAdLayer.setVisibility(View.GONE);
                    if ( mixer_layer != null )
                        mixer_layer.setVisibility(View.GONE);
                    if ( criteo_layer != null )
                        criteo_layer.setVisibility(View.GONE);

                    coupang_dy_layer.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(productUrl));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    });

                    dy_coupang_close.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            setRake(currentPageId, "tap.closebtn");
                            coupang_dy_layer.setVisibility(View.GONE);
                            sendViewCount();
                        }
                    });
                    isDataSuccess = true;
                }
            }
        }
        if ( !isDataSuccess ) {
            sendViewCount();
        } else {
            removeFirstArray(object);
        }
    }

    private void initDY_CoupangWithNetwork() {
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getCoupangData(COUPANG_DY_AD, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        boolean isDataSuccess = false;
                        JSONObject object = (JSONObject) obj;
                        LogPrint.d("coupang ad obj :: " + object.toString());
                        if ( object != null ) {
                            JSONArray array = object.optJSONArray("data");
                            if ( array != null && array.length() > 0 ) {
                                SharedPreference.setString(mContext, Key.COUPANG_DY_DATA, object.toString());
                                JSONObject inObj = array.optJSONObject(0);
                                if ( inObj != null ) {
                                    String productName = inObj.optString("productName");
                                    String productPrice = inObj.optString("productPrice");
                                    String productImage = inObj.optString("productImage");
                                    String productUrl = inObj.optString("productUrl");
                                    boolean isRocket = inObj.optBoolean("isRocket");
                                    boolean isFreeShipping = inObj.optBoolean("isFreeShipping");

                                    ImageLoader.with(mContext).from(productImage).load(dy_coupang_image);
                                    dy_coupang_product_name.setText(productName);
                                    dy_coupang_price.setText(Common.putComma(productPrice) + "원");

                                    dy_coupang_rocket.setVisibility(View.VISIBLE);
                                    if ( isRocket )
                                        dy_coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_rocket);
                                    else
                                        dy_coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_logo);

                                    coupang_dy_layer.setVisibility(View.VISIBLE);
                                    mMobonRewardAdLayerC.setVisibility(View.GONE);
                                    reward_coupang_layer.setVisibility(View.GONE);
                                    reward_joint_layer.setVisibility(View.GONE);
                                    reward_joint_banner_layer.setVisibility(View.GONE);
                                    mMobonRewardAdLayer.setVisibility(View.GONE);
                                    if ( mixer_layer != null )
                                        mixer_layer.setVisibility(View.GONE);
                                    if ( criteo_layer != null )
                                        criteo_layer.setVisibility(View.GONE);

                                    coupang_dy_layer.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(productUrl));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.startActivity(intent);
                                        }
                                    });

                                    dy_coupang_close.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            setRake(currentPageId, "tap.closebtn");
                                            coupang_dy_layer.setVisibility(View.GONE);
                                            sendViewCount();
                                        }
                                    });
                                    isDataSuccess = true;
                                }
                            }
                        }
                        if ( !isDataSuccess ) {
                            sendViewCount();
                        } else {
                            removeFirstArray(object);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendViewCount();
                    }
                } else {
                    sendViewCount();
                }
            }
        });
    }

    private void initUtilCoupangWithoutNetwork(JSONObject object) {
        boolean isDataSuccess = false;
        if ( object != null ) {
            JSONArray array = object.optJSONArray("data");
            if ( array != null && array.length() > 0 ) {
                SharedPreference.setString(mContext, Key.COUPANG_DY_REWARD_DATA, object.toString());
                JSONObject inObj = array.optJSONObject(0);
                if ( inObj != null ) {
                    String productName = inObj.optString("productName");
                    String productPrice = inObj.optString("productPrice");
                    String productImage = inObj.optString("productImage");
                    String productUrl = inObj.optString("productUrl");
                    boolean isRocket = inObj.optBoolean("isRocket");
                    boolean isFreeShipping = inObj.optBoolean("isFreeShipping");

                    int baseWidth = Common.convertDpToPx(mContext, 411);
                    int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                    if (mSoftKeyboard != null) {
                        screenWidth = mSoftKeyboard.getScreenWidth();
                    }
                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) util_reward_ad_layer.getLayoutParams();
                    LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                    int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                    if ( isRewardPossible ) {
                        if ( point <= 0 ) {
                            util_reward_point.setVisibility(View.GONE);
                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        } else {
                            util_reward_point.setVisibility(View.VISIBLE);
                            if ( screenWidth < baseWidth) {
                                LogPrint.d("left ~~~");
                                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                            } else {
                                LogPrint.d("right ~~~");
                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                            }
                        }
                    } else {
                        util_reward_point.setVisibility(View.GONE);
                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    }

                    util_reward_ad_layer.setLayoutParams(param);
                    util_reward_coupang_image.setVisibility(View.VISIBLE);
                    ImageLoader.with(mContext).from(productImage).load(util_reward_coupang_image);
                    util_coupang_top_margin.setVisibility(View.VISIBLE);
                    util_coupang_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f);
                    util_coupang_product_name.setText(productName);
                    util_coupang_price.setVisibility(View.VISIBLE);
                    util_coupang_price.setText(Common.putComma(productPrice) + "원");

                    util_coupang_rocket.setVisibility(View.VISIBLE);
                    if ( isRocket )
                        util_coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_rocket);
                    else
                        util_coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_logo);

                    util_r_leftBg.setVisibility(View.VISIBLE);
                    util_r_rightBg.setVisibility(View.VISIBLE);
                    util_r_leftBg.setBackgroundColor(Color.WHITE);
                    util_r_rightBg.setBackgroundColor(Color.WHITE);

                    util_reward_point.setText(point+"P");
                    util_rest_layer.setVisibility(View.VISIBLE);

                    util_ad_layer.setVisibility(View.GONE);
                    util_mobon_reward_ad_layer_c.setVisibility(View.VISIBLE);
                    util_reward_ad_layer.setVisibility(View.VISIBLE);
                    util_mobon_reward_ad_layer.setVisibility(View.GONE);
                    util_reward_coupang_layer.setVisibility(View.VISIBLE);

                    util_mobon_reward_ad_layer_c.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                            //sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                            if ( isRewardPossible )
                                sendRewardPoint(COUPANG_SC);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(productUrl));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            LogPrint.d(" call reward_coupang_click");
                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                            task.postStats("reward_coupang_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                @Override
                                public void onResponse(boolean result, Object obj) {

                                }
                            });
                        }
                    });
                    isDataSuccess = true;
                }
            }
        }
        if ( !isDataSuccess ) {
            sendViewCount();
        } else {
            removeRewardFirstArray(object);
            LogPrint.d(" call reward_coupang_eprs");
            CustomAsyncTask task = new CustomAsyncTask(mContext);
            task.postStats("reward_coupang_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {

                }
            });
        }
    }

    private void initUtilCoupangWithNetwork() {
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getCoupangData(COUPANG_DY_REWARD_AD, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        boolean isDataSuccess = false;
                        JSONObject object = (JSONObject) obj;
                        LogPrint.d("coupang ad obj :: " + object.toString());
                        if ( object != null ) {
                            JSONArray array = object.optJSONArray("data");
                            if ( array != null && array.length() > 0 ) {
                                SharedPreference.setString(mContext, Key.COUPANG_DY_REWARD_DATA, object.toString());
                                JSONObject inObj = array.optJSONObject(0);
                                if ( inObj != null ) {
                                    String productName = inObj.optString("productName");
                                    String productPrice = inObj.optString("productPrice");
                                    String productImage = inObj.optString("productImage");
                                    String productUrl = inObj.optString("productUrl");
                                    boolean isRocket = inObj.optBoolean("isRocket");
                                    boolean isFreeShipping = inObj.optBoolean("isFreeShipping");

                                    int baseWidth = Common.convertDpToPx(mContext, 411);
                                    int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                                    if (mSoftKeyboard != null) {
                                        screenWidth = mSoftKeyboard.getScreenWidth();
                                    }
                                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) util_reward_ad_layer.getLayoutParams();
                                    LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                    int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                    if ( isRewardPossible ) {
                                        if ( point <= 0 ) {
                                            util_reward_point.setVisibility(View.GONE);
                                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                        } else {
                                            util_reward_point.setVisibility(View.VISIBLE);
                                            if ( screenWidth < baseWidth) {
                                                LogPrint.d("left ~~~");
                                                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                            } else {
                                                LogPrint.d("right ~~~");
                                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                            }
                                        }
                                    } else {
                                        util_reward_point.setVisibility(View.GONE);
                                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                    }
                                    util_reward_ad_layer.setLayoutParams(param);
                                    util_reward_coupang_image.setVisibility(View.VISIBLE);
                                    ImageLoader.with(mContext).from(productImage).load(util_reward_coupang_image);
                                    util_coupang_top_margin.setVisibility(View.VISIBLE);
                                    util_coupang_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f);
                                    util_coupang_product_name.setText(productName);
                                    util_coupang_price.setVisibility(View.VISIBLE);
                                    util_coupang_price.setText(Common.putComma(productPrice) + "원");

                                    util_coupang_rocket.setVisibility(View.VISIBLE);
                                    if ( isRocket )
                                        util_coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_rocket);
                                    else
                                        util_coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_logo);

                                    util_r_leftBg.setVisibility(View.VISIBLE);
                                    util_r_rightBg.setVisibility(View.VISIBLE);
                                    util_r_leftBg.setBackgroundColor(Color.WHITE);
                                    util_r_rightBg.setBackgroundColor(Color.WHITE);

                                    util_reward_point.setText(point+"P");
                                    util_rest_layer.setVisibility(View.VISIBLE);

                                    util_ad_layer.setVisibility(View.GONE);
                                    util_mobon_reward_ad_layer_c.setVisibility(View.VISIBLE);
                                    util_reward_ad_layer.setVisibility(View.VISIBLE);
                                    util_mobon_reward_ad_layer.setVisibility(View.GONE);
                                    util_reward_coupang_layer.setVisibility(View.VISIBLE);

                                    util_mobon_reward_ad_layer_c.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                                            //sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                            sendRewardPoint(COUPANG_SC);
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(productUrl));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.startActivity(intent);
                                            LogPrint.d(" call reward_coupang_click");
                                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                                            task.postStats("reward_coupang_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                @Override
                                                public void onResponse(boolean result, Object obj) {

                                                }
                                            });
                                        }
                                    });
                                    isDataSuccess = true;
                                }
                            }
                        }
                        if ( !isDataSuccess ) {
                            sendViewCount();
                        } else {
                            removeRewardFirstArray(object);
                            LogPrint.d(" call reward_coupang_eprs");
                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                            task.postStats("reward_coupang_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                @Override
                                public void onResponse(boolean result, Object obj) {

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendViewCount();
                    }
                } else {
                    sendViewCount();
                }
            }
        });
    }

    private void initCoupangWithoutNetwork(JSONObject object) {
        boolean isDataSuccess = false;
        if ( object != null ) {
            JSONArray array = object.optJSONArray("data");
            if ( array != null && array.length() > 0 ) {
                SharedPreference.setString(mContext, Key.COUPANG_DY_REWARD_DATA, object.toString());
                JSONObject inObj = array.optJSONObject(0);
                if ( inObj != null ) {
                    String productName = inObj.optString("productName");
                    String productPrice = inObj.optString("productPrice");
                    String productImage = inObj.optString("productImage");
                    String productUrl = inObj.optString("productUrl");
                    boolean isRocket = inObj.optBoolean("isRocket");
                    boolean isFreeShipping = inObj.optBoolean("isFreeShipping");

                    int baseWidth = Common.convertDpToPx(mContext, 411);
                    int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                    if (mSoftKeyboard != null) {
                        screenWidth = mSoftKeyboard.getScreenWidth();
                    }
                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) reward_ad_layer.getLayoutParams();
                    LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                    int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                    if ( isRewardPossible ) {
                        if ( point <= 0 ) {
                            reward_point.setVisibility(View.GONE);
                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        } else {
                            reward_point.setVisibility(View.VISIBLE);
                            if ( screenWidth < baseWidth) {
                                LogPrint.d("left ~~~");
                                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                            } else {
                                LogPrint.d("right ~~~");
                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                            }
                        }
                    } else {
                        reward_point.setVisibility(View.GONE);
                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    }

                    reward_ad_layer.setLayoutParams(param);
                    reward_coupang_image.setVisibility(View.VISIBLE);
                    ImageLoader.with(mContext).from(productImage).load(reward_coupang_image);
                    coupang_top_margin.setVisibility(View.VISIBLE);
                    coupang_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f);
                    coupang_product_name.setText(productName);
                    coupang_price.setVisibility(View.VISIBLE);
                    coupang_price.setText(Common.putComma(productPrice) + "원");

                    coupang_rocket.setVisibility(View.VISIBLE);
                    if ( isRocket )
                        coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_rocket);
                    else
                        coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_logo);

                    r_leftBg.setVisibility(View.VISIBLE);
                    r_rightBg.setVisibility(View.VISIBLE);
                    r_leftBg.setBackgroundColor(Color.WHITE);
                    r_rightBg.setBackgroundColor(Color.WHITE);

                    reward_point.setText(point+"P");
                    rest_layer.setVisibility(View.VISIBLE);

                    mMobonRewardAdLayerC.setVisibility(View.VISIBLE);
                    reward_coupang_layer.setVisibility(View.VISIBLE);
                    mMobonRewardAdLayer.setVisibility(View.GONE);

                    mMobonRewardAdLayerC.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                            // sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                            if ( isRewardPossible ) {
                                sendRewardPoint(COUPANG_SC);
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(productUrl));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            LogPrint.d(" call reward_coupang_click");
                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                            task.postStats("reward_coupang_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                @Override
                                public void onResponse(boolean result, Object obj) {

                                }
                            });
                        }
                    });
                    isDataSuccess = true;
                }
            }
        }
        if ( !isDataSuccess ) {
            sendViewCount();
        } else {
            removeRewardFirstArray(object);
            LogPrint.d(" call reward_coupang_eprs");
            CustomAsyncTask task = new CustomAsyncTask(mContext);
            task.postStats("reward_coupang_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {

                }
            });
        }
    }

    private void initCoupangWithNetwork() {
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getCoupangData(COUPANG_DY_REWARD_AD, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        boolean isDataSuccess = false;
                        JSONObject object = (JSONObject) obj;
                        LogPrint.d("coupang ad obj 111 :: " + object.toString());
                        if ( object != null ) {
                            JSONArray array = object.optJSONArray("data");
                            if ( array != null && array.length() > 0 ) {
                                SharedPreference.setString(mContext, Key.COUPANG_DY_REWARD_DATA, object.toString());
                                JSONObject inObj = array.optJSONObject(0);
                                if ( inObj != null ) {
                                    String productName = inObj.optString("productName");
                                    String productPrice = inObj.optString("productPrice");
                                    String productImage = inObj.optString("productImage");
                                    String productUrl = inObj.optString("productUrl");
                                    boolean isRocket = inObj.optBoolean("isRocket");
                                    boolean isFreeShipping = inObj.optBoolean("isFreeShipping");

                                    int baseWidth = Common.convertDpToPx(mContext, 411);
                                    int screenWidth = SharedPreference.getInt(mContext, Key.KEY_SCREEN_WIDTH);
                                    if (mSoftKeyboard != null) {
                                        screenWidth = mSoftKeyboard.getScreenWidth();
                                    }
                                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) reward_ad_layer.getLayoutParams();
                                    LogPrint.d("baseWidth :: " + baseWidth + " , screenWidth :: " + screenWidth);
                                    int point = SharedPreference.getZeroInt(mContext, Key.KEY_OCB_AD_BANNER_POINT);
                                    if ( isRewardPossible ) {
                                        if ( point <= 0 ) {
                                            reward_point.setVisibility(View.GONE);
                                            param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                        } else {
                                            reward_point.setVisibility(View.VISIBLE);
                                            if ( screenWidth < baseWidth) {
                                                LogPrint.d("left ~~~");
                                                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                            } else {
                                                LogPrint.d("right ~~~");
                                                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                            }
                                        }
                                    } else {
                                        reward_point.setVisibility(View.GONE);
                                        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                    }
                                    reward_ad_layer.setLayoutParams(param);
                                    reward_coupang_image.setVisibility(View.VISIBLE);
                                    ImageLoader.with(mContext).from(productImage).load(reward_coupang_image);
                                    coupang_top_margin.setVisibility(View.VISIBLE);
                                    coupang_product_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f);
                                    coupang_product_name.setText(productName);
                                    coupang_price.setVisibility(View.VISIBLE);
                                    coupang_price.setText(Common.putComma(productPrice) + "원");

                                    coupang_rocket.setVisibility(View.VISIBLE);
                                    if ( isRocket )
                                        coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_rocket);
                                    else
                                        coupang_rocket.setBackgroundResource(R.drawable.ocb_cp_logo);

                                    r_leftBg.setVisibility(View.VISIBLE);
                                    r_rightBg.setVisibility(View.VISIBLE);
                                    r_leftBg.setBackgroundColor(Color.WHITE);
                                    r_rightBg.setBackgroundColor(Color.WHITE);

                                    reward_point.setText(point+"P");
                                    rest_layer.setVisibility(View.VISIBLE);

                                    mMobonRewardAdLayerC.setVisibility(View.VISIBLE);
                                    reward_coupang_layer.setVisibility(View.VISIBLE);
                                    mMobonRewardAdLayer.setVisibility(View.GONE);

                                    mMobonRewardAdLayerC.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // 9월 epic 리워드 광고에 깜짝 포인트 지급하지 않도록 수정
                                            //sendSurprise(isInAppKeyboard, mContext, SPOT_POINT_BANNER);
                                            sendRewardPoint(COUPANG_SC);
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(productUrl));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.startActivity(intent);
                                            LogPrint.d(" call reward_coupang_click");
                                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                                            task.postStats("reward_coupang_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                                @Override
                                                public void onResponse(boolean result, Object obj) {

                                                }
                                            });
                                        }
                                    });
                                    isDataSuccess = true;
                                }
                            }
                        }
                        if ( !isDataSuccess ) {
                            sendViewCount();
                        } else {
                            removeRewardFirstArray(object);
                            LogPrint.d(" call reward_coupang_eprs");
                            CustomAsyncTask task = new CustomAsyncTask(mContext);
                            task.postStats("reward_coupang_eprs", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                @Override
                                public void onResponse(boolean result, Object obj) {

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendViewCount();
                    }
                } else {
                    sendViewCount();
                }
            }
        });
    }

    private boolean isUtilRewardVisible() {
        try {
            String obj = SharedPreference.getString(mContext, Key.KEY_OCB_AD_FREQUENCY);
            if (!TextUtils.isEmpty(obj)) {
                JSONObject object = new JSONObject(obj);
                String rewardFrequency = object.optString("reward");
                if (TextUtils.isEmpty(rewardFrequency))
                    rewardFrequency = "-1";
                String[] rewardArr = rewardFrequency.split(",");
                if ( rewardArr != null && rewardArr.length > 0 ) {
                    for (int i = 0; i < rewardArr.length; i++) {
                        if (utilRewardCount == Integer.valueOf(rewardArr[i])) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void getOfferwallList(boolean isRetry) {
        if ( ocb_offerwall_point == null || offerwall_point_layer == null )
            return;
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.getOfferwallList("","", "1", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
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
                                                String missionClass = missionObj.optString("mission_class");
                                                data.setScreenshot(missionObj.optString("screenshot"));
                                                data.setMission_class(missionClass);
                                                data.setReg_date(missionObj.optString("reg_date"));
                                                data.setMedia_point(missionObj.optDouble("media_point"));
                                                data.setDaily_participation(missionObj.optInt("daily_participation"));
                                                data.setAdver_url(missionObj.optString("adver_url"));
                                                data.setIntro_img(missionObj.optString("intro_img"));
                                                data.setMission_id(missionObj.optString("mission_id"));
                                                data.setDaily_participation_cnt(missionObj.optInt("daily_participation_cnt"));
                                                data.setUser_point(missionObj.optDouble("user_point"));
                                                data.setCheck_url(missionObj.optString("check_url"));
                                                data.setUser_point(missionObj.optDouble("user_point"));
                                                int seq = missionObj.optInt("mission_seq");
                                                data.setMission_seq(seq);
                                                data.setShop_name(missionObj.optString("adver_name"));
                                                data.setKeyword(missionObj.optString("keyword"));
                                                data.setThumb_img(missionObj.optString("thumb_img"));
                                                data.setTarget_name(missionObj.optString("target_name"));

                                                if ( missionClass.toLowerCase().equals("p1") || missionClass.toLowerCase().equals("p2") || missionClass.toLowerCase().equals("p3") ) {
                                                    array.add(data);
                                                }
                                            }
                                        }
                                        if (array.size() > 0 ) {
                                            selectedOfferwallData = array.get(0);

                                            String sOfferwallPoint = "+" + selectedOfferwallData.getUser_point();
                                            try {
                                                double dPoint = selectedOfferwallData.getUser_point();
                                                int iPoint = (int)dPoint;
                                                sOfferwallPoint = "+" + iPoint;
                                            } catch (Exception e ) {
                                                e.printStackTrace();
                                            }
                                            ocb_offerwall_point.setText(sOfferwallPoint);
                                            offerwall_point_layer.setVisibility(View.VISIBLE);
                                            offerwall_point_layer.setOnClickListener(new OnKeyboardSingleClickListener() {
                                                @Override
                                                protected void onSingleClick(View v) {
                                                    LogPrint.d("data not null offerwallClicked false url :: " + selectedOfferwallData.getAdver_url());
                                                    String mission_class = selectedOfferwallData.getMission_class();
                                                    if ( !TextUtils.isEmpty(mission_class)) {
                                                        if ( mission_class.toLowerCase().equals("p1") ) {
                                                            String screenshot = selectedOfferwallData.getScreenshot();
                                                            if ( TextUtils.isEmpty(screenshot) ) {
                                                                offerwallParticipationCheck(selectedOfferwallData, false, mission_class);
                                                            } else {
                                                                if ( "N".equals(screenshot) ) {
                                                                    offerwallMissionClick(selectedOfferwallData, false);
                                                                } else {
                                                                    offerwallParticipationCheck(selectedOfferwallData, false, mission_class);
                                                                }
                                                            }
                                                        } else if ( mission_class.toLowerCase().equals("p2") || mission_class.toLowerCase().equals("p3") ) {
                                                            offerwallParticipationCheck(selectedOfferwallData, false, mission_class);
                                                        } else {
                                                            Toast.makeText(mContext, mContext.getString(R.string.aikbd_offerwall_update), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    if ( com.enliple.keyboard.ui.common.Common.IsFormissionTokenError(mContext, rt) ) {
                                        if ( !isRetry ) {
                                            getListWithToken(true);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        LogPrint.d("obj null");
                    }
                }
            }
        });
    }

    private void offerwallParticipationCheck(OfferwallData data, boolean isRetry, String mission_class) {
        CustomAsyncTask task = new CustomAsyncTask(mContext);
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
                                LogPrint.d("mission_class :: " + mission_class);
                                if ( mission_class.toLowerCase().equals("p1") ) {
                                    Intent intent = new Intent(mContext, KeyboardOfferwallWebView1Activity.class);
                                    intent.putExtra("landingUrl", landingUrl);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intent);
                                } else if ( mission_class.toLowerCase().equals("p2") || mission_class.toLowerCase().equals("p3")){
                                    Intent intent = new Intent(mContext, KeyboardOfferwallWebView23Activity.class);
                                    intent.putExtra("landingUrl", landingUrl);
                                    intent.putExtra("intent_mission", data);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intent);
                                }
                                /*
                                String landingUrl = object.optString("landing_url");
                                LogPrint.d("landingUrl :: " + landingUrl);
                                Intent intent = new Intent(mContext, KeyboardOfferwallWebView1Activity.class);
                                intent.putExtra("landingUrl", landingUrl);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                                 */
                            } else {
                                if ( com.enliple.keyboard.ui.common.Common.IsFormissionTokenError(mContext, rt) ) {
                                    if ( !isRetry ) {
                                        CustomAsyncTask inTask = new CustomAsyncTask(mContext);
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
                                                                SharedPreference.setString(mContext, Key.KEY_FORMISSION_TOKEN, tk);
                                                                offerwallParticipationCheck(data, true, mission_class);
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }
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

    private void offerwallMissionClick(OfferwallData data, boolean isRetry) {
        LogPrint.d("offerwallMissionClick");
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.offerwallClick(data.getMission_seq(), data.getMission_id(), new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if ( object != null ) {
                            int rt = object.optInt("result");
                            if ( rt == 0 ) {
                                Intent intent = new Intent(mContext, KeyboardOfferwallGuideActivity.class);
                                intent.putExtra("intent_mission", data);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            } else {
                                if ( com.enliple.keyboard.ui.common.Common.IsFormissionTokenError(mContext, rt) ) {
                                    if ( !isRetry ) {
                                        CustomAsyncTask inTask = new CustomAsyncTask(mContext);
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
                                                                SharedPreference.setString(mContext, Key.KEY_FORMISSION_TOKEN, tk);
                                                                offerwallMissionClick(data, true);
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }
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

    private void getListWithToken(boolean isRetry) {
        CustomAsyncTask task = new CustomAsyncTask(mContext);
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
                                SharedPreference.setString(mContext, Key.KEY_FORMISSION_TOKEN, tk);
                                getOfferwallList(isRetry);
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

/*
    public void setTalkbackOff() {

        if ( newsTxt != null ) {
            newsTxt.setEnabled(false);
            newsTxt.setContentDescription(" ");
            newsTxt.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                @Override
                public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {

                    // Let the default implementation populate the info.
                    super.onInitializeAccessibilityNodeInfo(host, info);

                    // Override this particular property
                    info.setEnabled(false);
                }
            });

        }
    }*/

    private void setOfferwallBadge(int offerwall_show) {
        if ( offerwall_badge_count == offerwall_show ) {
            getOfferwallList(false);
            offerwall_badge_count = 1;
            LogPrint.d("offerwall_badge_count == offerwall_show");
        } else {
            if ( offerwall_badge_count < offerwall_show ) {
                LogPrint.d("offerwall_badge_count < offerwall_show");
                offerwall_badge_count ++;
            } else {
                LogPrint.d("offerwall_badge_count > offerwall_show");
                // 혹시 문제가 있어 offerwall badge count가 offerwall_show 값보다 크게 되면 초기화 해준다.
                offerwall_badge_count = 1;
            }
        }
    }

    private void sendBrandPoint() {
        LogPrint.d("coupang sendBrandPoint brand reward ::  mBrandBadge.getVisibility() :: " + mBrandBadge.getVisibility());
        CustomAsyncTask task = new CustomAsyncTask(mContext);
        task.sendBrandPoint(Common.BRAND_ZONE, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        LogPrint.d("coupang brand reward :: sendNewRewardPoint object :: " + object.toString() );
                        if ( object != null ) {
                            boolean rt = object.optBoolean("Result");
                            if ( rt ) {
                                String message = object.optString("message");
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                    }
                                }, 500);
                                setRake(currentPageId, "toast.popreward");
                                //showSurpriseToast(isInAppKeyboard, mContext, message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean isAnimationStart() {
        //boolean firstOfferwall = SharedPreference.getBoolean(mContext, Common.PREF_OFFERWALL_FIRST);
        //boolean secondOfferwall = SharedPreference.getBoolean(mContext, Common.PREF_OFFERWALL_FIRST);
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        LogPrint.d("hour :: " + + hour);
        // 시간이 오전 9시에서 오전 10시 사이인지 확인합니다.
        if (hour >= 9 && hour < 10) {
            boolean firstOfferwall = SharedPreference.getBoolean(mContext, Common.PREF_OFFERWALL_FIRST);
            LogPrint.d("firstOfferwall :: " + firstOfferwall);
            if ( !firstOfferwall ) {
                SharedPreference.setBoolean(mContext, Common.PREF_OFFERWALL_FIRST, true);
                return true;
            }
        }

        if ( hour >= 16 && hour < 18 ) {
            boolean secondOfferwall = SharedPreference.getBoolean(mContext, Common.PREF_OFFERWALL_SECOND);
            LogPrint.d("secondOfferwall :: " + secondOfferwall);
            if ( !secondOfferwall ) {
                SharedPreference.setBoolean(mContext, Common.PREF_OFFERWALL_SECOND, true);
                return true;
            }
        }
        return false;
    }

    public boolean isGameZoneVisible() {
        return game_tab.getVisibility() == View.VISIBLE ? true : false;
    }

    public void gameZoneVisible(String use_YN) {
        LogPrint.d("gameZoneVisible use_YN :: " + use_YN);
        if ("N".equals(use_YN)) {
            if (game_tab.getVisibility() == View.VISIBLE) {
                game_tab.setVisibility(View.GONE);
            }
        } else {
            if (game_tab.getVisibility() == View.GONE) {
                game_tab.setVisibility(View.VISIBLE);
            }
        }
    }
}
