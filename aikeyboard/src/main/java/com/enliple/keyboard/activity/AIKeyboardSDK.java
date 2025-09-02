package com.enliple.keyboard.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.AIKBD_DBHelper;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.Decompress;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.KeyboardUserIdModel;
import com.enliple.keyboard.common.PointDBHelper;
import com.enliple.keyboard.common.ThemeManager;
import com.enliple.keyboard.common.ThemeModel;
import com.enliple.keyboard.common.UserIdDBHelper;
import com.enliple.keyboard.common.Util;
import com.enliple.keyboard.network.CustomAsyncTask;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by shoppul-pc1 on 2017-05-17.
 */

public class AIKeyboardSDK {

//    String url = "http://api.cashkeyboard.co.kr/images/theme/theme_01.zip";
//    String zipfileName = "theme_01.zip";
//    String unzipFolderName = "theme_01";

    public static final int USER_ID = 0;
    public static final int GUBUN = 1;
    public static final int DEVICE_ID = 2;
    private static AIKeyboardSDK mInstance = null;
    private static Context mContext;

    public AIKeyboardSDK(Context context) {
        mContext = context;
        setCheckSettingCode();
    }


    public static synchronized AIKeyboardSDK init(Context context) {
        if (context == null) {
            KeyboardLogPrint.d("context == null");
            return null;
        }

        if (mInstance == null) {
            mInstance = new AIKeyboardSDK(context);
        }

        return mInstance;
    }

    public static void SetDefaultTheme() {
        AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
        if (!helper.isThemeExist()) {
//            downloadAndUnzipContent();
            String rPath = mContext.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator;
            File rFile = new File(rPath);
            if (!rFile.exists())
                rFile.mkdirs();
            Decompress decompress = new Decompress();
            decompress.AssetUnZip(mContext, false, new Decompress.PostUnzip() {
                @Override
                public void unzipDone(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
                        helper.deleteTheme();
                        helper.insertTheme(result);

                        ThemeModel mThemeModel = ThemeManager.GetThemeModel(result, 0);
                        double scale = ThemeManager.GetScale(mContext);
                        if (scale != 1 && mThemeModel != null) {
                            ThemeManager.ResizingSpImage(mThemeModel.getBackImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getSpaceImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEnterImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmojiImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmoticonImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmoticonRecent(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmoticonFirst(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmoticonSecond(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmoticonThird(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmoticonFourth(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmoticonFifth(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getEmoticonSixth(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getKeySymbol(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getKeyLang(), Bitmap.CompressFormat.PNG, 100, scale);
                            ThemeManager.ResizingSpImage(mThemeModel.getShiftImg(), Bitmap.CompressFormat.PNG, 100, scale);
                            if (!TextUtils.isEmpty(mThemeModel.getShiftImg1())) {
                                ThemeManager.ResizingSpImage(mThemeModel.getShiftImg1(), Bitmap.CompressFormat.PNG, 100, scale);
                            }
                            if (!TextUtils.isEmpty(mThemeModel.getShiftImg2())) {
                                ThemeManager.ResizingSpImage(mThemeModel.getShiftImg2(), Bitmap.CompressFormat.PNG, 100, scale);
                            }
                        } else {

                        }
                    }
                }
            });
        }
    }

    public static void setPop(boolean set) {
        SharedPreference.setBoolean(mContext, Common.PREF_POP_SHOW, set);
    }

    public static void setDebug(boolean val) {
        KeyboardLogPrint.setDebugMode(val);
    }

    public static void GoSetting(String hideHeader) {
        Intent intent = new Intent(mContext, KeyboardSettingsActivity.class);
        intent.putExtra("HIDE_HEADER", hideHeader);
        mContext.startActivity(intent);
    }

    public static void setUserOut() {
        UserIdDBHelper helper = new UserIdDBHelper(mContext);
        helper.deleteUserInfo();
        PointDBHelper pHelper = new PointDBHelper(mContext);
        pHelper.deletePoint();
    }

    public static void SetMaxPoint(String userId, String gubun, String deviceId) {
        KeyboardUserIdModel model = new KeyboardUserIdModel();
        model.setUserId(userId);
        model.setDeviceId(deviceId);
        model.setGubun(gubun);

        CustomAsyncTask apiAsyncTask = new CustomAsyncTask(mContext);
        apiAsyncTask.requestMaxPoint(model, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean rt, Object obj) {
                if (rt) {
                    try {
                        JSONObject object = new JSONObject(obj.toString());
                        boolean result = object.optBoolean("Result");
                        String errStr = object.optString("errstr");
                        String result_day = object.optString("ResultDay");
                        KeyboardLogPrint.e("result_day :: " + result_day);

                        if (result) {
                            int point = object.optInt("useablePoint");
                            KeyboardLogPrint.e("SetMaxPoint about point max point result true, point :: " + point);
                            String today = Common.getDate();
                            if (result_day != null && result_day.equals(today)) {
                                PointDBHelper helper = new PointDBHelper(mContext);
                                helper.deleteMaxPoint();
                                helper.insertMaxPoint(point);
                                SharedPreference.setString(mContext, Common.PREF_DATE_POINT, Common.getDate());
                                KeyboardLogPrint.e("after set max point helper.getMaxPoint() :: " + helper.getMaxPoint());
                            }
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });

    }

    public static ArrayList<String> GetUserId() {
        ArrayList<String> array = new ArrayList<String>();
        UserIdDBHelper helper = new UserIdDBHelper(mContext);
        KeyboardUserIdModel model = helper.getUserInfo();
        if ( model != null ) {
            String userId = model.getUserId();
            String gubun = model.getGubun();
            String deviceId = model.getDeviceId();
            KeyboardLogPrint.e("GetUserId userId :: " + userId);
            KeyboardLogPrint.e("GetUserId gubun :: " + gubun);
            KeyboardLogPrint.e("GetUserId deviceId :: " + deviceId);
            array.add(USER_ID, userId);
            array.add(GUBUN, gubun);
            array.add(DEVICE_ID, deviceId);
            return array;
        } else {
            return null;
        }
    }

    public static void SetUserId(String userId, String gubun, String deviceId) {
        KeyboardLogPrint.e("SetUserId");
        KeyboardUserIdModel model = new KeyboardUserIdModel();
        model.setUserId(userId);
        model.setDeviceId(deviceId);
        model.setGubun(gubun);

        UserIdDBHelper helper = new UserIdDBHelper(mContext);
        helper.deleteUserInfo();
        helper.insertUserInfo(model);
    }

    public static void SetADPushStatus(boolean pushStatus) {
        KeyboardLogPrint.e("SetADPushStatus val :: " + pushStatus);
        SharedPreference.setBoolean(mContext, Common.PREF_AD_PUSH, pushStatus);
    }

    public static void RunKeyboardSetting(String keyboardName, String iconName, String bg, String end_bg, String userId, String gubun, String deviceId) {
        mContext.startActivity(new Intent(mContext, KeyboardSelectActivity.class));
/**
        if (TextUtils.isEmpty(keyboardName) || TextUtils.isEmpty(iconName) || TextUtils.isEmpty(bg) || TextUtils.isEmpty(end_bg)) {
            Toast.makeText(mContext, "키보드 사용에 필요한 필수값을 입력하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (mInstance == null)
                System.out.println("First AIKeyboardSDK init function call !!!!!!!!!!!!!!!! ");
            else {
                mContext.startActivity(new Intent(mContext, KeyboardSelectActivity.class));
                SharedPreference.setString(mContext, Common.PREF_KEYBOARD_NAME, keyboardName);
                SharedPreference.setString(mContext, Common.PREF_APP_ICON, iconName);
                SharedPreference.setString(mContext, Common.PREF_APP_BACKGROUND, bg);
                SharedPreference.setString(mContext, Common.PREF_APP_END_BACKGROUND, end_bg);
                SharedPreference.setString(mContext, Common.PREF_APP_PACKAGE, mContext.getPackageName());
                KeyboardUserIdModel model = new KeyboardUserIdModel();
                model.setUserId(userId);
                model.setDeviceId(deviceId);
                model.setGubun(gubun);

                UserIdDBHelper helper = new UserIdDBHelper(mContext);
                helper.deleteUserInfo();
                helper.insertUserInfo(model);
            }
        }**/
    }

    public static void RunKeyboardSetting(String keyboardName, String adid) {

        if (TextUtils.isEmpty(keyboardName)) {
            Toast.makeText(mContext, "키보드 사용에 필요한 필수값을 입력하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (mInstance == null)
                System.out.println("First AIKeyboardSDK init function call !!!!!!!!!!!!!!!! ");
            else {
                mContext.startActivity(new Intent(mContext, KeyboardSelectActivity.class));
                SharedPreference.setString(mContext, Common.PREF_KEYBOARD_NAME, keyboardName);
                SharedPreference.setString(mContext, Common.PREF_APP_PACKAGE, mContext.getPackageName());
            }
        }
    }

    public static void RunKeyboardSetting(String keyboardName, String iconName, String bg, String end_bg, boolean isCallApp) {

        if (TextUtils.isEmpty(keyboardName) || TextUtils.isEmpty(iconName) || TextUtils.isEmpty(bg) || TextUtils.isEmpty(end_bg)) {
            Toast.makeText(mContext, "키보드 사용에 필요한 필수값을 입력하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (mInstance == null)
                System.out.println("First AIKeyboardSDK init function call !!!!!!!!!!!!!!!! ");
            else {
                Intent intent = new Intent(mContext, KeyboardSelectActivity.class);
                if (isCallApp)
                    intent.putExtra("CALL_APP", isCallApp);
                mContext.startActivity(intent);
                SharedPreference.setString(mContext, Common.PREF_KEYBOARD_NAME, keyboardName);
                SharedPreference.setString(mContext, Common.PREF_APP_ICON, iconName);
                SharedPreference.setString(mContext, Common.PREF_APP_BACKGROUND, bg);
                SharedPreference.setString(mContext, Common.PREF_APP_END_BACKGROUND, end_bg);
                SharedPreference.setString(mContext, Common.PREF_APP_PACKAGE, mContext.getPackageName());
            }
        }
    }

    public static void RunKeyboardSetting(String keyboardName, boolean isCallApp) {

        if (TextUtils.isEmpty(keyboardName)) {
            Toast.makeText(mContext, "키보드 사용에 필요한 필수값을 입력하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (mInstance == null)
                System.out.println("First AIKeyboardSDK init function call !!!!!!!!!!!!!!!! ");
            else {
                Intent intent = new Intent(mContext, KeyboardSelectActivity.class);
                if (isCallApp)
                    intent.putExtra("CALL_APP", isCallApp);
                mContext.startActivity(intent);

                SharedPreference.setString(mContext, Common.PREF_KEYBOARD_NAME, keyboardName);
                SharedPreference.setString(mContext, Common.PREF_APP_PACKAGE, mContext.getPackageName());
            }
        }
    }

    private void setCheckSettingCode() {
        String mediaCode = Util.getMetaData(mContext, Common.META_DATA_MEDIA_CODE);
        String s_value = Util.getMetaData(mContext, Common.META_DATA_S_VALUE);
        String u_value = Util.getMetaData(mContext, Common.META_DATA_U_VALUE);

        KeyboardLogPrint.d("Meta data code ::: " + mediaCode);
        KeyboardLogPrint.d("Meta data s ::: " + s_value);
        KeyboardLogPrint.d("Meta data u ::: " + u_value);

        if (TextUtils.isEmpty(mediaCode) || TextUtils.isEmpty(s_value) || TextUtils.isEmpty(u_value)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setTitle("MetaData 오류")        // 제목 설정
                    .setMessage("MetaData의 값이 잘못되었습니다.")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기
        } else {
            SharedPreference.setString(mContext, Common.META_DATA_MEDIA_CODE, mediaCode);
            SharedPreference.setString(mContext, Common.META_DATA_S_VALUE, s_value);
            SharedPreference.setString(mContext, Common.META_DATA_U_VALUE, u_value);
        }
    }
}

//package com.enliple.keyboard.activity;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.text.TextUtils;
//import android.widget.Toast;
//
//import com.enliple.keyboard.common.Common;
//import com.enliple.keyboard.common.KeyboardLogPrint;
//import com.enliple.keyboard.common.Util;
//
///**
// * Created by shoppul-pc1 on 2017-05-17.
// */
//
//public class AIKeyboardSDK {
//    private static AIKeyboardSDK mInstance = null;
//    private static Context mContext;
//
//    public AIKeyboardSDK(Context context) {
//        mContext = context;
//        setCheckSettingCode();
//
//    }
//
//
//
//    public static synchronized AIKeyboardSDK init(Context context) {
//        if (context == null) {
//            KeyboardLogPrint.d("context == null");
//            return null;
//        }
//
//        if (mInstance == null) {
//            mInstance = new AIKeyboardSDK(context);
//        }
//
//
//
//        return mInstance;
//    }
//
//    public static void setDebug(boolean val)
//    {
//        KeyboardLogPrint.setDebugMode(val);
//    }
//
//    public static void RunKeyboardSetting(String keyboardName, String iconName, String bg, String end_bg, String adid) {
//
//        if ( TextUtils.isEmpty(keyboardName) || TextUtils.isEmpty(iconName) || TextUtils.isEmpty(bg) || TextUtils.isEmpty(end_bg) ||  )
//        {
//            Toast.makeText(mContext, "키보드 사용에 필요한 필수값을 입력하시기 바랍니다.", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            if ( mInstance == null )
//                System.out.println("First AIKeyboardSDK init function call !!!!!!!!!!!!!!!! ");
//            else
//            {
//                mContext.startActivity(new Intent(mContext, Keyboard_Main_Activity.class));
//                SharedPreference.setString(mContext, Common.PREF_KEYBOARD_NAME , keyboardName);
//                SharedPreference.setString(mContext, Common.PREF_APP_ICON, iconName);
//                SharedPreference.setString(mContext, Common.PREF_APP_BACKGROUND, bg);
//                SharedPreference.setString(mContext, Common.PREF_APP_END_BACKGROUND, end_bg);
//                SharedPreference.setString(mContext, Common.PREF_APP_PACKAGE, mContext.getPackageName());
//                SharedPreference.setString(mContext, Common.PREF_ADID, adid);
//            }
//        }
//    }
//
//
//    public static void RunKeyboardSetting(String keyboardName, String adid) {
//
//        if ( TextUtils.isEmpty(keyboardName) ||  )
//        {
//            Toast.makeText(mContext, "키보드 사용에 필요한 필수값을 입력하시기 바랍니다.", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            if ( mInstance == null )
//                System.out.println("First AIKeyboardSDK init function call !!!!!!!!!!!!!!!! ");
//            else
//            {
//                mContext.startActivity(new Intent(mContext, Keyboard_Main_Activity.class));
//                SharedPreference.setString(mContext, Common.PREF_KEYBOARD_NAME , keyboardName);
//                SharedPreference.setString(mContext, Common.PREF_APP_PACKAGE, mContext.getPackageName());
//                SharedPreference.setString(mContext, Common.PREF_ADID, adid);
//            }
//        }
//    }
//
//    private void setCheckSettingCode() {
//        String mediaCode = Util.getMetaData(mContext, Common.META_DATA_MEDIA_CODE);
//        String s_value = Util.getMetaData(mContext, Common.META_DATA_S_VALUE);
//        String u_value = Util.getMetaData(mContext, Common.META_DATA_U_VALUE);
//
//        KeyboardLogPrint.d("Meta data code ::: " + mediaCode);
//        KeyboardLogPrint.d("Meta data s ::: " + s_value);
//        KeyboardLogPrint.d("Meta data u ::: " + u_value);
//
//        if (TextUtils.isEmpty(mediaCode) || TextUtils.isEmpty(s_value) || TextUtils.isEmpty(u_value)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//            builder.setTitle("MetaData 오류")        // 제목 설정
//                    .setMessage("MetaData의 값이 잘못되었습니다.")        // 메세지 설정
//                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
//                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                        // 확인 버튼 클릭시 설정
//                        public void onClick(DialogInterface dialog, int whichButton) {
//
//                        }
//                    });
//            AlertDialog dialog = builder.create();    // 알림창 객체 생성
//            dialog.show();    // 알림창 띄우기
//        } else {
//            SharedPreference.setString(mContext, Common.META_DATA_MEDIA_CODE, mediaCode);
//            SharedPreference.setString(mContext, Common.META_DATA_S_VALUE, s_value);
//            SharedPreference.setString(mContext, Common.META_DATA_U_VALUE, u_value);
//        }
//    }
//}
