package com.enliple.keyboard.ui.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.enliple.keyboard.activity.KeyboardOfferwallListActivity;
import com.enliple.keyboard.activity.SharedPreference;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017-09-22.
 */

public class Common {
    public static final String SERVICE_CODE = "01"; // 각각의 캐시키보드 파생버전들을 구분하기 위한 값 live check 시 전달. ( 캐시키보드2는 01로 할당함, aikeyboard에도 있음
    public static final String KEY_TOKEN = "fcm_token";
    public static final String KEY_PROVIDER_FLAG = "key_provider_flag";
    public static final String KEY_GOOGLE_ACCOUNT = "key_google_account";
    public static final String GUBUN_NOTI = "01";
    public static final String GUBUN_QA = "02";
    public static final String GUBUN_FAQ = "03";
    public static final String GRADE_ROOKIE = "01";
    public static final String GRADE_PRO = "02";
    public static final String GRADE_MASTER = "03";
    public static final String GRADE_HERO = "04";
    public static final String GRADE_LEGEND = "05";

    public static final String API_GUBUN_JOIN = "01";
    public static final String API_GUBUN_EDIT = "02";
    public static final String NETWORK_ERROR = "network_error";
    public static final String GUBUN_OWN = "01";
    public static final String GUBUN_NAVER = "02";
    public static final String GUBUN_KAKAO = "03";
    public static final String GUBUN_FACEBOOK = "04";

    public static boolean isSoftkeyYn(Context context) {

        boolean useSoftNavigation;
        int id = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            useSoftNavigation = context.getResources().getBoolean(id);
        } else {
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
            useSoftNavigation = (!(hasBackKey && hasHomeKey));
        }
        return useSoftNavigation;
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
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

    public interface OnCallbackAdidListener {
        public void onCallbackADID(String adid);
    }

    // 외부 앱 호출
    public static boolean callApp(Context context, String url) {
        Intent intent = null;

        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException ex) {
            return false;
        }
        try {
            boolean retval = true;

            if (url.startsWith("intent")) {

                if (context.getPackageManager().resolveActivity(intent, 0) == null) {
                    String packagename = intent.getPackage();
                    if (packagename != null) {
                        Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                        retval = true;
                    }
                } else {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setComponent(null);

                    try {
                        if (((Activity) context).startActivityIfNeeded(intent, -1)) {
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
                PackageManager pm = context.getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
                for (final ResolveInfo app : activityList) {
                    if ((app.activityInfo.name).contains("com.kakao.talk")) {
                        bKakaoTalk = true;
                        break;
                    }
                    if ((app.activityInfo.name).contains("com.kakao.story")) {
                        bKakaoStory = true;
                        break;
                    }
                }

                // 해당 앱이 없을때 마켓으로 연결
                if (url.startsWith("kakaolink://") && !bKakaoTalk) {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("market://details?id=" + "com.kakao.talk"));
                    ((Activity) context).startActivityForResult(intent1, 0);
                    retval = true;
                } else if (url.startsWith("storylink://") && !bKakaoStory) {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("market://details?id=" + "com.kakao.story"));
                    ((Activity) context).startActivityForResult(intent1, 0);
                    retval = true;
                } else {
                    Uri uri = Uri.parse(url);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    ((Activity) context).startActivityForResult(intent, 0);
                    retval = true;
                }

            }
            return retval;

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String incodeCheckedNew(String word) {
        String returnWord = "";
        try {
            try {
                String charSet[] = {"utf-8", "euc-kr", "8859_1", "ksc5601", "x-windows-949", "iso-8859-1"};
                for (int i = 0; i < charSet.length; i++) {
                    for (int j = 0; j < charSet.length; j++) {
                        returnWord = new String(word.getBytes(charSet[i]), charSet[j]);
                        if (returnWord.contains("¿") || returnWord.contains("¢") || returnWord.contains("�") || returnWord.contains("Ã") || returnWord.contains("帮") || returnWord.contains("«")) {
                            LogPrint.d(charSet[i] + " to " + charSet[j] + " = " + returnWord);
                        } else {
                            LogPrint.d("returnWord >>>>>>>>>>>>>>>>>>>> " + returnWord);
                            return returnWord;
                        }
                    }
                }
            } catch (Exception ex) {
            }
            System.out.println("returnWord : " + returnWord);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return word;
    }

    public static String ToNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    public static void onWebSiteOpen(final Context context, final String url) {
        LogPrint.d("Call onWebSiteOpen");

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

    public static boolean IsFormissionTokenError(Context context, int errCode) {
        String tk = SharedPreference.getString(context, Key.KEY_FORMISSION_TOKEN);
        if ( TextUtils.isEmpty(tk) )
            return true;
        else {
            if ( errCode == Key.FORMISSION_ERR_TOKEN || errCode == Key.FORMISSION_ERR_EXPIRED_TOKEN || errCode == Key.FORMISSION_ERR_TOKEN_FORMAT
                    || errCode == Key.FORMISSION_ERR_TOKEN_AUTH || errCode == Key.FORMISSION_ERR_TOKEN_FORM || errCode == Key.FORMISSION_ERR_EMPTY_TOKEN )
                return true;
            else
                return false;
        }
    }
}
