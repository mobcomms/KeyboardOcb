package com.enliple.keyboard.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.enliple.keyboard.activity.KeyboardSelectActivity;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.R;
import com.enliple.keyboard.ad.Listener;
import com.enliple.keyboard.mobonAD.MobonKey;
import com.enliple.keyboard.mobonAD.MobonUtils;
import com.enliple.keyboard.mobonAD.manager.SPManager;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by Administrator on 2017-01-19.
 */

public class Common {
    public static int GAME_STATUS_YES = 0;
    public static int GAME_STATUS_NO = 1;
    public static int GAME_STATUS_NONE = -1;
    public static final String LISTENER_TYPE_ERROR = "LISTENER_TYPE_ERROR";
    public static final String LISTENER_TYPE_STATE = "LISTENER_TYPE_STATE";
    public static final String LISTENER_TYPE_POINT = "LISTENER_TYPE_POINT";
    public static final String ERROR_AD_LOAD ="ERROR_AD_LOAD";
    public static final String ERROR_NETWORK_CONDITION ="ERROR_NETWORK_CONDITION";
    public static final String STATE_AD_LOADING ="STATE_AD_LOADING";
    public static final String STATE_AD_LOADED ="STATE_AD_LOADED";
    public static final String STATE_AD_RELOADING ="STATE_AD_RELOADING";
    public static final String STATE_AD_MOVE ="STATE_AD_MOVE";
    public static final String STATE_AD_MOVE_FAIL ="STATE_AD_MOVE_FAIL";
    public static final String STATE_AD_CLOSE = "STATE_AD_CLOSE";
    public static final String MOBWITH_RELEASE_CHAT_GPT = "10882901";
    public static final String MOBWITH_RELEASE_KEYBOARD_ZONE  = "10881730";
    public static final String MOBWITH_RELEASE_SETTING_ZONE   = "10881738";
    public static final String MOBWITH_RELEASE_OFFERWALL_ZONE = "10881742";
    public static final String BRAND_ZONE = "brand";
    public static final String MOBWITH_DEV_KEYBOARD_ZONE  = "1763";
    public static final String MOBWITH_DEV_SETTING_ZONE   = "1765";
    public static final String MOBWITH_DEV_OFFERWALL_ZONE = "1766";

    public static final String DEV_TOKEN = "f954dacc28599ea87510cd16334e8b9dbcd2dc";
    public static final String LIVE_TOKEN = "c9d2715b7801a3432b319def768d558390ebbd";
    public static final String PAGE_ID_KEYBOARD = "/keyboard";
    public static final String json = "{\"timeDeals\":[{\"id\":\"101191\",\"linkUrl\":\"ocbt:\\/\\/com.skmc.okcashbag.home_google\\/detail\\/eventNoTitle?url=https%3A%2F%2Fohsara.okcashbag.com%2Fm%2F%3Focb-view%3Df\",\"startDate\":1619276400000,\"endDate\":1619362740000,\"feeds\":[{\"id\":\"24995\",\"title\":\"*최저가* 더블스윗 달~콤한 수제 마카롱 8구\",\"description\":\"최종 혜택가 10,430원, 오늘만 OK캐쉬백 30%적립, 500개 선착순 판매\",\"imageInfo\":\"https:\\/\\/pocimg-c.okcashbag.com\\/upload\\/shopping\\/20210423131440_147152477.png\",\"linkUrl\":\"ocbt:\\/\\/com.skmc.okcashbag.home_google\\/detail\\/eventNoTitle?url=https%3a%2f%2fohsara.okcashbag.com%2fm%2fproduct.html%3Fbranduid%3d3545747%26ocb-view%3df%26mission_id%3D24995&eventId=24995\",\"startDate\":1619276400000,\"endDate\":1619362740000,\"benefitAmount\":4470,\"salePrice\":14900,\"originPrice\":27900,\"timeDealStartDate\":1619276400000,\"timeDealEndDate\":1619362740000,\"immdSaveYn\":\"Y\",\"immdSavePoint\":1,\"soldOutYn\":\"false\"}]},{\"id\":\"101214\",\"startDate\":1619276400000,\"endDate\":1619362740000,\"feeds\":[{\"id\":\"25107\",\"title\":\"V컬러링 지금 가입하면, 2개월 무료 이용!\",\"description\":\"보이는 컬러링, SKT V컬러링\",\"imageInfo\":\"https:\\/\\/pocimg-c.okcashbag.com\\/upload\\/shopping\\/20210423132402_068318146.png\",\"linkUrl\":\"ocbt:\\/\\/com.skmc.okcashbag.home_google\\/detail\\/event?url=https%3A%2F%2Femp.okcashbag.com%2Fevent%2Fapp%2FSKTVAS%2FeventIntro.do%3FkeyEvent%3D2al0gs99X24gh104%26mission_id%3D25107&title=%EC%9D%B4%EB%B2%A4%ED%8A%B8%20%ED%99%95%EC%9D%B8%ED%95%98%EA%B8%B0&eventId=25107\",\"startDate\":1619276400000,\"endDate\":1619362740000,\"benefitAmount\":1750,\"salePrice\":1750,\"originPrice\":6600,\"timeDealStartDate\":1619276400000,\"timeDealEndDate\":1619362740000,\"immdSaveYn\":\"Y\",\"immdSavePoint\":1,\"soldOutYn\":\"false\"}]},{\"id\":\"101190\",\"linkUrl\":\"ocbt:\\/\\/com.skmc.okcashbag.home_google\\/detail\\/eventNoTitle?url=https%3A%2F%2Fohsara.okcashbag.com%2Fm%2F%3Focb-view%3Df\",\"startDate\":1619276400000,\"endDate\":1619362740000,\"feeds\":[{\"id\":\"24993\",\"title\":\"*최저가* 대웅생명과학 KF94 마스크 100매 + 사은품\",\"description\":\"최종 혜택가 27,930원, 오늘만 OK캐쉬백 30%적립, 500개 선착순 판매\",\"imageInfo\":\"https:\\/\\/pocimg-c.okcashbag.com\\/upload\\/shopping\\/20210423131129_910037177.png\",\"linkUrl\":\"ocbt:\\/\\/com.skmc.okcashbag.home_google\\/detail\\/eventNoTitle?url=https%3a%2f%2fohsara.okcashbag.com%2fm%2fproduct.html%3Fbranduid%3d3545304%26ocb-view%3df%26mission_id%3D24993&eventId=24993\",\"startDate\":1619276400000,\"endDate\":1619362740000,\"benefitAmount\":11970,\"salePrice\":39900,\"originPrice\":120000,\"timeDealStartDate\":1619276400000,\"timeDealEndDate\":1619362740000,\"immdSaveYn\":\"Y\",\"immdSavePoint\":1,\"soldOutYn\":\"false\"}]}]}";
    public static final String OCB_PARTNER_CODE = "01";
    public static final String BUILD_APP = "캐시 키보드";
    public static final String BUILD_SDK_WHOWHO = "후후";
    public static final String BUILD_SDK_OKCASHBAG = "OK Cashbag";
    public static final String BUILD_SDK_MANGO = "망고쇼핑";
//    public static final String BUILD_SDK_OLIVEYOUNG = "SDK_OLIVEYOUNG";
    public static final String BUILD_SDK_ETC = "SDK_ETC";

    public static final String TARGET_PACKAGENAME = "com.skmc.okcashbag.home_google";
    //public static final String TARGET_PACKAGENAME = "com.enliple.chaewon";
    public static final int AD_LEVEL_DEFAULT =3;
    public static final int AD_RATE_DEFAULT = 5;
    public static final String SERVICE_CODE = "01"; // 각각의 캐시키보드 파생버전들을 구분하기 위한 값 live check 시 전달. ( 캐시키보드2는 01로 할당함
//    public static final float DEFAULT_SOUND = 0.2f;
//    public static final int BASIS_MEDIA_VOLUME = 12;
//    public static final int DEFAULT_SOUND_LEVEL = 1;
    public static final int AUDIO_TYPE = AudioManager.STREAM_MUSIC;
//public static final int AUDIO_TYPE = AudioManager.STREAM_SYSTEM;
    public static final int VIBRATE_MUL = 3;
public static final int DEFAULT_SOUND_LEVEL = 1; // 초기 default sound를 0으로 setting 하도록 변경 2017.08.25 (from 대표님 지시사항) -> 2017.11.08 한경수 실장님 지시로 default sound 를 0 -> 5로 변경
    public static final int DEFAULT_VIBRATE_LEVEL = 1; // 초기 default vibrate가 50이었으나 대표님 지시사항으로 전반적으로 vibrate level을 1/2로 줄임  2017.08.25
    public static final int DEFAULT_KEYBOARD_SIZE = 15; // OCB 요청으로 변경
    public static final String MEDIA_C3 = "c3tvalapp";
//    public static final int VOLUME_LEVEL = 400;
//public static final int VOLUME_LEVEL = 200;
    public static final int RECENT_SEARCH_LIMIT = 20;
//    public static final boolean EMOJI_ENABLED = true;
    public static final String PREF_KEYBOARD_MODE = "keyboard_mode";
    public static final String PREF_IS_KOREAN_KEYBOARD = "is_korean_keyboard";
    public static final String PREF_EXT_KEYBOARD = "ext_keyboard";
//    public static final String PREF_VIBRATE_SETTING = "vibrate_setting";
//    public static final String PREF_SOUND_SETTING = "sound_setting";
    public static final String PREF_SELECTED_SOUND = "selected_sound";

    public static final String PREF_KEYBOARD_HEIGHT = "keyboard_height";
    public static final String PREF_PREVIEW_SETTING = "preview_setting";
    public static final String PREF_QWERTY_NUM_SETTING = "qwerty_num_setting";
    public static final String PREF_NEWS_SETTING = "pref_news_setting";
    public static final String PREF_NEXT_TIME = "pref_next_time";
//    public static final String PREF_VOLUME_LEVEL = "volume_level";
public static final String PREF_I_VOLUME_LEVEL = "i_volume_level";
    public static final String PREF_VIBRATE_LEVEL = "vibrate_level";
    public static final String PREF_KEYWORD_VERSION = "keyword_version";
    public static final String PREF_KEYBOARD_NAME = "pref_keyboard_name";
    public static final String PREF_APP_ICON = "pref_app_icon";
    public static final String PREF_APP_BACKGROUND = "pref_app_background";
    public static final String PREF_APP_END_BACKGROUND = "pref_app_end_background";
    public static final String PREF_APP_PACKAGE = "pref_app_package";
    public static final String PREF_AD_VIEW_TIME = "pref_ad_view_time";
    public static final String PREF_AD_PUSH = "pref_ad_push";
    public static final String PREF_AD_LEVEL = "pref_ad_level";
    public static final String PREF_COUPANG_CLOSE_TIME = "coupang_close_time";
    public static final String PREF_SEARCH_AD_SETTING = "pref_search_ad_setting";

    public static final int MODE_CHUNJIIN = 0;
    public static final int MODE_CHUNJIIN_PLUS = 1;
    public static final int MODE_QUERTY = 2;
    public static final int MODE_NARA = 3;
    public static final int MODE_DAN = 4;
    public static final String OCB_UUID = "aikbd_ocb_uuid";

//    public static final String CHANGE_ICON_SET_KEY = SoftKeyboard.getStaticApplicationContext().getString(R.string.setting_change_icon_set_key);
//    public static final String CHANGE_ICON_SET_VALUE_GOOGLE = SoftKeyboard.getStaticApplicationContext() .getString(R.string.setting_change_icon_set_value_google);
//    public static final String CHANGE_ICON_SET_VALUE_DEFAULT = CHANGE_ICON_SET_VALUE_GOOGLE;
//    public static final String CHANGE_ICON_SET_VALUE_APPLE = SoftKeyboard.getStaticApplicationContext()
//            .getString(R.string.setting_change_icon_set_value_apple);

    public static final String PREF_KEYBOARD_BG_ALPHA = "keyboard_bg_alpha";
    public static final String PREF_KEYBOARD_BUTTON_ALPHA = "keyboard_btn_alpha";
    public static final String PREF_KEYBOARD_DARK_BUTTON_ALPHA = "keyboard_dark_btn_alpha";
    public static final String PREF_KEYBOARD_BUTTON_COLOR = "keyboard_btn_color";
    public static final String PREF_KEYBOARD_SBUTTON_COLOR = "keyboard_sbtn_color";
    public static final String PREF_KEYBOARD_TXT_COLOR= "keyboard_txt_color";
    public static final String PREF_KEYBOARD_SIZE_LEVEL = "keyboard_size_level";
    public static final String PREF_KEYBOARD_BG_DRAWABLE = "keyboard_bg_drawable";
    public static final String PREF_KEYBOARD_BG_GALLERY = "keyboard_bg_gallery";
    public static final String PREF_KEYBOARD_BG_URL = "keyboard_bg_url";
    public static final String PREF_MAX_TEXTURE_SIZE = "max_texture_size";
    public static final String PREF_MATCHED_EMOJI_DATE = "matched_emoji_date";
    public static final String PREF_HAS_SETTING = "keyboard_has_setting";
    public static final String PREF_AUID = "pref_auid";
    public static final String PREF_KEYWORD_CNT = "pref_keyword_cnt";
    public static final String PREF_SAVED_KEYWORD = "pref_saved_keyword";
    public static final String NETWORK_ERROR = "network_error";
    public static final String NETWORK_DISCONNECT = "network_disconnect";
    public static final String NETWORK_DISCONNECTED = "network_disconnected";
    public static final String PREF_AD_POSSIBLE = "pref_ad_possible";
    public static final int EMOJI_SIZE = 38;
    public static final double DEFAULT_KEY_HEIGHT = 0.88;
    public static final String MEDIA_VOLUME_LEVEL = "media_volume_level";
    public static final String META_DATA_MEDIA_CODE = "keyboard_media_key";
    public static final String META_DATA_S_VALUE = "keyboard_s_key";
    public static final String META_DATA_U_VALUE = "keyboard_u_key";
    public static final String PREF_AD_JSON = "pref_ad_json";
    public static final String PREF_NEW_AD_JSON = "pref_new_ad_json";
    public static final String PREF_FIRST_INSTALL_TIME = "first_install_time";
    public static final String PREF_KEYBOARD_THEME_INDEX = "pref_keyboard_theme_index";
    public static final String PREF_THEME_NORMAL_BTN_COLOR = "pref_theme_normal_btn_color";
    public static final String PREF_THEME_SPECIAL_BTN_COLOR = "pref_theme_special_btn_color";
    public static final String PREF_THEME_TXT_COLOR = "pref_theme_txt_color";
    public static final String PREF_THEME_TXT_COLOR_S = "pref_theme_txt_color_s";
    public static final String PREF_THEME_SP_ICON_COLOR = "pref_theme_sp_icon_color";
    public static final String PREF_BTN_SELECTOR = "pref_btn_selector";
    public static final String PREF_DARK_BTN_SELECTOR = "pref_dark_btn_selector";
    public static final String PREF_RAND_CNT = "pref_rand_cnt";
    public static final String PREF_DATE_POINT = "pref_keyboard_date_point";
    public static final String PREF_AD_RATE = "pref_ad_rate";
    public static final String PREF_POP_LIST = "pref_poplist";
    public static final String PREF_POP_SHOW = "pref_popshow";

    public static final String PREF_OFFERWALL_FIRST = "offerwall_first_badge";
    public static final String PREF_OFFERWALL_SECOND = "offerwall_second_badge";

    public static final String PREF_SPACE_KEY_HEIGHT = "pref_space_key_height";

    public static final int NUMBER_AD_NOTICE_BLANK = 10;

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

    public static String incodeCheckedNew(String word)
    {
        String returnWord = "";
        try
        {
            try
            {
                String charSet[] = { "utf-8", "euc-kr", "8859_1", "ksc5601", "x-windows-949", "iso-8859-1" };
                for (int i = 0; i < charSet.length; i++)
                {
                    for (int j = 0; j < charSet.length; j++)
                    {
                        returnWord = new String(word.getBytes(charSet[i]), charSet[j]);
                        if (returnWord.contains("¿") || returnWord.contains("¢") || returnWord.contains("�") || returnWord.contains("Ã") || returnWord.contains("帮") || returnWord.contains("«"))
                        {
                            KeyboardLogPrint.d(charSet[i] + " to " + charSet[j] + " = " + returnWord);
                        }
                        else
                        {
                            KeyboardLogPrint.d("returnWord >>>>>>>>>>>>>>>>>>>> " + returnWord);
                            return returnWord;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
            }
            System.out.println("returnWord : " + returnWord);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return word;
    }

    /**
     * 한글 인코딩 체크 및 깨졌을시 디코딩 처리
     *
     * @param word
     * @return
     */
    public static String incodeChecked(String word)
    {
        String returnWord = "";
        try
        {
            try
            {
                String charSet[] = { "utf-8", "euc-kr", "8859_1", "ksc5601", "x-windows-949", "iso-8859-1" };
                for (int i = 0; i < charSet.length; i++)
                {
                    for (int j = 0; j < charSet.length; j++)
                    {
                        returnWord = new String(word.getBytes(charSet[i]), charSet[j]);
                        if (! Patterns.checkNumEngHangulUnicode(returnWord)) // 디코딩 결과가 깨진 문자열일 때.. (숫자, 영어, 한글, 한글 초성이 아닌 경우)
                        //if (returnWord.contains("??") || returnWord.contains("占") || returnWord.contains("¿") || returnWord.contains("¢") || returnWord.contains("�") || returnWord.contains("Ã") || returnWord.contains("帮") || returnWord.contains("«"))
                        {
                            KeyboardLogPrint.d(charSet[i] + " to " + charSet[j] + " = " + returnWord);
                        }
                        else
                        {
                            KeyboardLogPrint.d("returnWord >>>>>>>>>>>>>>>>>>>> " + returnWord);
                            return returnWord;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
            }
            KeyboardLogPrint.d("returnWord : " + returnWord);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // 파라미터로 받은 문자 대신 빈 공백이나 Null 을 줘서 DB에 들어가지 않도록 한다.
        // 어차피 깨진 문자열은 아무리 날려봐야 안나오니까....
        return ""/*word*/;
    }

    // 외부 앱 호출
    public static boolean callApp(Context context, String url)
    {
        Intent intent = null;

        try
        {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            KeyboardLogPrint.d("intent url     +++" + url);
            KeyboardLogPrint.d("intent getScheme+++" + intent.getScheme());
            KeyboardLogPrint.d("intent getData +++" + intent.getDataString());
            KeyboardLogPrint.d("intent getData +++" + intent.getData().toString());
        }
        catch (URISyntaxException ex)
        {
            KeyboardLogPrint.d("Bad URI " + url + ":" + ex.getMessage());
            return false;
        }
        try
        {
            boolean retval = true;

            if (url.startsWith("intent"))
            {

                if (context.getPackageManager().resolveActivity(intent, 0) == null)
                {
                    String packagename = intent.getPackage();
                    if (packagename != null)
                    {
                        Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                        retval = true;
                    }
                }
                else
                {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setComponent(null);

                    try
                    {
                        if (((Activity) context).startActivityIfNeeded(intent, -1))
                        {
                            retval = true;
                        }
                    }
                    catch (ActivityNotFoundException ex)
                    {
                        retval = false;
                    }
                }
            }
            else
            {

                boolean bKakaoTalk = false;
                boolean bKakaoStory = false;

                // 설치된 패키지 확인
                PackageManager pm = context.getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
                for (final ResolveInfo app : activityList)
                {
                    if ((app.activityInfo.name).contains("com.kakao.talk"))
                    {
                        bKakaoTalk = true;
                        break;
                    }
                    if ((app.activityInfo.name).contains("com.kakao.story"))
                    {
                        bKakaoStory = true;
                        break;
                    }
                }

                // 해당 앱이 없을때 마켓으로 연결
                if (url.startsWith("kakaolink://") && !bKakaoTalk)
                {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("market://details?id=" + "com.kakao.talk"));
                    ((Activity) context).startActivityForResult(intent1, 0);
                    retval = true;
                }
                else if (url.startsWith("storylink://") && !bKakaoStory)
                {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("market://details?id=" + "com.kakao.story"));
                    ((Activity) context).startActivityForResult(intent1, 0);
                    retval = true;
                }
                else
                {
                    Uri uri = Uri.parse(url);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    ((Activity) context).startActivityForResult(intent, 0);
                    retval = true;
                }

            }
            return retval;

        }
        catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static String getDate()
    {
        String date = "";
        try
        {
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
            date = formater.format(new Date());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return date;
    }

    public static float getVolume(String tag, int mediaVolume, int level)
    {
        KeyboardLogPrint.w("getVolume tag :: " + tag);
        KeyboardLogPrint.w("getVolume mediaVolume :: " + mediaVolume);
        KeyboardLogPrint.w("getVolume level :: " + level);
        float rVolume = 0;
        float startVal = 0;
        if ( mediaVolume == 0 )
            return 0;
        if ( 0 < mediaVolume && mediaVolume < 7 )
        {
            startVal = 0.1f;
            for ( int i = 0 ; i < mediaVolume ; i ++ )
                startVal = startVal - 0.002f;
            rVolume = startVal * level;
        }
        else
        {
            startVal = 0.08f;
            for ( int i = 7 ; i < mediaVolume ; i ++ )
                startVal = startVal - 0.005f;
            rVolume = startVal * level;
        }

        if (mediaVolume > 15 ) { // 펜텍 등 일부 폰에서 media 값이 다름
            rVolume = 0.1f * level;
        }
//        rVolume = startVal * level;
        KeyboardLogPrint.e("level :: " + level);
        KeyboardLogPrint.e("mediaVolume :: " + mediaVolume);
        KeyboardLogPrint.e("startVal :: " + startVal);
        KeyboardLogPrint.e("rVolume :: " + rVolume);
        KeyboardLogPrint.e("tag :: " + tag);
        return rVolume;
    }

    public static int getStreamLevel(Context context) {
        AudioManager mManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volume = mManager.getStreamVolume(AUDIO_TYPE );
        KeyboardLogPrint.e("getStreamLevel volume :: " + volume);
        return volume;
    }

    public static int getMaximumTextureSize() {
        int maximumTextureSize = 0;
        try {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            int[] version = new int[2];
            egl.eglInitialize(display, version);

            int[] totalConfigurations = new int[1];
            egl.eglGetConfigs(display, null, 0, totalConfigurations);

            EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
            egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

            int[] textureSize = new int[1];

            for (int i = 0; i < totalConfigurations[0]; i++) {
                egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

                if (maximumTextureSize < textureSize[0]) {
                    maximumTextureSize = textureSize[0];
                }
            }
            egl.eglTerminate(display);
        } catch (Exception e) {
            return 0;
        }
        return maximumTextureSize;
    }

    public static void onWebSiteOpen(final Context context, final String url) {
        KeyboardLogPrint.d("Call onWebSiteOpen");

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!"".equals(Common.findAppIsExist(context, "com.android.browser")))
                intent.setPackage("com.android.browser");
            else if (!"".equals(Common.findAppIsExist(context, "com.sec.android.app.sbrowser")))
                intent.setPackage("com.sec.android.app.sbrowser");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
    public static Bitmap GenerateBarcode(String number, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = number;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();

        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }**/

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static int convertpxTodp(Context pContext, int pPx) {
        try {
            return ((int) (pPx / pContext.getResources().getDisplayMetrics().density));
        } catch (Exception e) {
            LogPrint.e("convertPxToDp() Exception! : " + e.getLocalizedMessage());
            return 0;
        }
    }

    public static int convertDpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int convertDpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String putCommaWithoutDot(String value) {
        try {
            if ( value == null || value.isEmpty() ) {
                return "0";
            } else if ( value.indexOf(".") >= 0 ) {
                double valueNum = Double.parseDouble(value);
                if ( valueNum == 0.0 )
                    return "0";
                else
                    return new DecimalFormat("###,##0").format(valueNum);
            } else {
                long valueNum = Long.parseLong(value);
                if ( valueNum == 0L) {
                    return "0";
                } else {
                    return new DecimalFormat("###,##0").format(valueNum);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String putComma(String value) {
        try {
            if ( value == null || value.isEmpty() ) {
                return "0";
            } else if ( value.indexOf(".") >= 0 ) {
                double valueNum = Double.parseDouble(value);
                if ( valueNum == 0.0 )
                    return "0";
                else
                    return new DecimalFormat("###,##0.00").format(valueNum);
            } else {
                long valueNum = Long.parseLong(value);
                if ( valueNum == 0L) {
                    return "0";
                } else {
                    return new DecimalFormat("###,##0").format(valueNum);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
/*
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static int getDisplayWidth(Context context) {
        int width = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if ( Build.VERSION.SDK_INT > 12 ) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        } else {
            width = display.getWidth();
        }
        return width;
    }

 */

    public static int getDisplayWidth(Context context) {
        int width = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            width = windowMetrics.getBounds().width() - insets.left - insets.right;
            LogPrint.d("getDisplayWidth version code over 30 width :: " + width);
            return width;
        } else {
            Display display = wm.getDefaultDisplay();
            if ( Build.VERSION.SDK_INT > 12 ) {
                Point size = new Point();
                display.getSize(size);
                width = size.x;
            } else {
                width = display.getWidth();
            }
            LogPrint.d("getDisplayWidth version code less 30 width :: " + width);
            return width;
        }
    }

    public static boolean IsNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if ( nw != null ) {
                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                if ( actNw != null ) {
                    if ( actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ) {
                        LogPrint.d("isNetworkConnected 2 true");
                        return true;
                    } else if ( actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ) {
                        LogPrint.d("isNetworkConnected 1 true");
                        return true;
                    } else {
                        LogPrint.d("isNetworkConnected 3 false");
                        return false;
                    }
                } else {
                    LogPrint.d("isNetworkConnected 2 false");
                    return false;
                }
            } else {
                LogPrint.d("isNetworkConnected 1 false");
                return false;
            }
        } else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if ( info != null ) {
                boolean isConnected = info.isConnected();
                LogPrint.d("isNetworkConnected 1111 " + isConnected);
                return isConnected;
            } else {
                LogPrint.d("isNetworkConnected 4 false");
                return false;
            }
        }
    }

    public static long GetTimeGap(String time) {
        long gap = 0;
        try {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String strTodayDate = format.format(date);

            date = new Date();
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String strCurrentDateWithTime = format.format(date);

            String alarmTime = strTodayDate + " " + time;

            Date cDate = format.parse(strCurrentDateWithTime);
            Date aDate = format.parse(alarmTime);
            LogPrint.d("cDate string :: " + cDate.toString());
            LogPrint.d("aDate string :: " + aDate.toString());
            long l_cDate = cDate.getTime();
            long c_aDate = aDate.getTime();
            gap = c_aDate - l_cDate;
            // gap이 0 보다 작다는 것은 이미 시간이 지난 것이기 때문에 다음날 해당 시간 알림이 울리도록 함.
            if ( gap < 0 ) {
                gap = gap + (long)(1000 * 60 * 60 * 24);
            }
            return gap;
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Bitmap GetRoundedTopLeftCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        final Rect topRightRect = new Rect(bitmap.getWidth()/2, 0, bitmap.getWidth(), bitmap.getHeight()/2);
        final Rect bottomRect = new Rect(0, bitmap.getHeight()/2, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        // Fill in upper right corner
        canvas.drawRect(topRightRect, paint);
        // Fill in bottom corners
        canvas.drawRect(bottomRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    // notification news에서 사용하려고 만든 것. 현재는 사용하지 않음. 2022.12.29
    public static Bitmap GetRoundedBottomLeftCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        final Rect topRightRect = new Rect(bitmap.getWidth()/2, 0, bitmap.getWidth(), bitmap.getHeight()/2);
        final Rect bottomRect = new Rect(0, bitmap.getHeight()/2, bitmap.getWidth(), bitmap.getHeight());

        final Rect topRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
        final Rect bottomRightRect = new Rect(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        // Fill in upper right corner
        canvas.drawRect(topRect, paint);
        // Fill in bottom corners
        canvas.drawRect(bottomRightRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap GetCenterCropBitmap(Bitmap bitmap) {
        if ( bitmap == null )
            return null;
        if ( bitmap.getWidth() >= bitmap.getHeight() ) {
            return Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight());
        } else {
            return Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());
        }
    }


    public static Bitmap GetCenterCroppedBitmap(Bitmap bitmap) {
        if (bitmap == null )
            return null;
        int value = 0;
        if (bitmap.getHeight() <= bitmap.getWidth()) {
            value = bitmap.getHeight();
        } else {
            value = bitmap.getWidth();
        }
        return Bitmap.createBitmap(bitmap, 0, 0, value, value);
    }

    public static int GetColors(Bitmap bm, boolean isLeft) {
        int color = Color.WHITE;
        int bitmapWidth = bm.getWidth();
        int bitmapHeight = bm.getHeight();
        ArrayList<Integer> colorArray = new ArrayList<>();
        if ( isLeft ) {
            for ( int i = 0 ; i < 2 ; i ++ ) {
                for ( int j = 0 ; j < bitmapHeight ; j ++ ) {
                    int pixelColor = bm.getPixel(i,j);
                    colorArray.add(pixelColor);
                }
            }
        } else {
            for ( int i = bitmapWidth - 2 ; i < bitmapWidth ; i ++ ) {
                for ( int j = 0 ; j < bitmapHeight ; j ++ ) {
                    int pixelColor = bm.getPixel(i,j);
                    colorArray.add(pixelColor);
                }
            }
        }
        int max = 0;
        Set<Integer> set = new HashSet<Integer>(colorArray);
        for ( int val : set) {
            int num = Collections.frequency(colorArray, val);
            if ( max < num ) {
                max = num;
                color = val;
            }
        }
        return color;
    }

    public static String GetMobwithURL(Context context, String zone, boolean isRelease) {
        if ( TextUtils.isEmpty(zone) || context == null )
            return "";
        String url = "";
        String adid = MobonUtils.getAdid(context);
        String auid = SPManager.getString(context, MobonKey.AUID);
        if (TextUtils.isEmpty(auid))
            auid = "";

        boolean isUnderFourteen = SharedPreference.getBoolean(context, Key.KEY_IS_UNDER_FOURTEEN);
        if ( isUnderFourteen ) {
            auid = "";
            adid = "";
        }

        if ( isRelease ) {
            url = "https://www.mobwithad.com/api/v1/banner/app/ocbKeyboard?zone=" + zone + "&count=1&w=320&h=50&adid=" + adid + "&auid=" + auid;
        } else {
            url = "https://dev.mobwithad.com/api/v1/banner/app/ocbKeyboard?zone=" + zone + "&count=1&w=320&h=50&adid=" + adid + "&auid=" + auid;
        }
        LogPrint.d("mobwith url :: " + url);
        return url;
    }

    public static int GetKeyboardKind(Context context) {
        if ( context == null )
            return -1;
        LogPrint.d("keyboard_kind GetKeyboardkind");
        AIKBD_DBHelper helper = new AIKBD_DBHelper(context);
        int kind = helper.getKeyboardkind();
        if ( kind >= 0 ) {
            return kind;
        } else {
            int prefValue = SharedPreference.getInt(context, Common.PREF_KEYBOARD_MODE);
            LogPrint.d("keyboard_kind prefValue :: " + prefValue);
            helper.insertKeyboardKind(prefValue);
            LogPrint.d("keyboard_kind after save :: " + helper.getKeyboardkind());
            return helper.getKeyboardkind();
        }
    }

    public static void SetKeyboardKind(Context context, int kind) {
        if ( context == null )
            return;
        LogPrint.d("keyboard_kind SetKeyboardKind");
        AIKBD_DBHelper helper = new AIKBD_DBHelper(context);
        helper.insertKeyboardKind(kind);
    }

    public static float GetHeightValue(int level) {
        float val = 1.2f;
        try {
            val = Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * level) / 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    public static void GetGameStatus(Context context, Listener.OnGameStatusListener listener) {
        int game_status = SharedPreference.getInt(context, Key.KEY_GAME_STATUS);
        LogPrint.d("GetGameStatus status : " + game_status);
        if (Common.GAME_STATUS_NONE == game_status) {
            CustomAsyncTask task1 = new CustomAsyncTask(context);
            task1.getJointFinish("", "", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if (result) {
                        try {
                            JSONObject object = (JSONObject) obj;
                            if (object != null) {
                                JSONObject game_obj = object.optJSONObject("gamezone");
                                if (game_obj != null) {
                                    boolean game_result = game_obj.optBoolean("Result");
                                    String game_YN = game_obj.optString("use_YN");
                                    if (game_result) {
                                        int status = Common.GAME_STATUS_NO;
                                        if ("Y".equals(game_YN)) {
                                            status = Common.GAME_STATUS_YES;
                                        }
                                        LogPrint.d("GetGameStatus received status : " + status);
                                        SharedPreference.setInt(context, Key.KEY_GAME_STATUS, status);
                                        if (listener != null) {
                                            LogPrint.d("GetGameStatus received game_YN : " + game_YN);
                                            listener.received(game_YN);
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (listener != null) {
                                listener.received("N");
                            }
                        }
                        if (listener != null) {
                            listener.received("N");
                        }
                    } else {
                        if (listener != null) {
                            listener.received("N");
                        }
                    }
                }
            });
        } else {
            String game_YN = "N";
            if (game_status == Common.GAME_STATUS_YES)
                game_YN = "Y";
            if (listener != null) {
                listener.received(game_YN);
            }
        }
    }

    public static void SetInset(View rootView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                androidx.core.graphics.Insets bars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                                | WindowInsetsCompat.Type.displayCutout()
                                | WindowInsetsCompat.Type.ime()
                );
                ViewCompat.setPaddingRelative(v,
                        bars.left,
                        bars.top,
                        bars.right,
                        bars.bottom
                );
                return WindowInsetsCompat.CONSUMED;
            }
        });
    }

//    public static void SetInset(View rootView) {
//        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
//            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
//            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
//            LogPrint.d("activity navigationBarHeight : " + navigationBarHeight);
//            // 상태바/네비게이션바 높이만큼 패딩 추가
//            v.setPadding(0, statusBarHeight, 0, navigationBarHeight);
//
//            return insets;
//        });
////
////        // 네비게이션 바 아이콘 색상 조정 (필요할 경우)
////        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(rootView);
////        if (controller != null) {
////            controller.setAppearanceLightStatusBars(true);  // 상태바 글자 검은색
////            controller.setAppearanceLightNavigationBars(true); // 네비게이션 바 글자 검은색
////        }
//    }

}