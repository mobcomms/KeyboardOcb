package com.enliple.keyboard;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.activity.KeyboardHybridOfferwallActivity;
import com.enliple.keyboard.activity.KeyboardSettingsActivity;
import com.enliple.keyboard.activity.SoftKeyboard;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardUserIdModel;
import com.enliple.keyboard.common.UserIdDBHelper;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.ckeyboard.IntroActivity;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.keyboard.ui.common.SharedPreference;

import org.json.JSONObject;

public class CKeyboard {
    // 후후에서 호출하는 함수. 무료유저인지 여부.
    public static void setFreeUser(Context context, boolean isFree) {
        SharedPreference.setBoolean(context, Key.KEY_USER_FREE, isFree);
    }

    public static void GoSettings(Context context) {
        Intent intent = new Intent(context, KeyboardSettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void updateUserInfo(Context context, String uuid, long user_point, String card_number) {
        LogPrint.d("get uuid 1 :: " + uuid);
        if ( uuid == null || TextUtils.isEmpty(uuid) ) {
            LogPrint.d("updateUserInfo uuid is empty");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
        } else {
            LogPrint.d("skkim updateUserInfo uuid not null");
            String savedUuid = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
            String savedCardNum = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.CARD_NUM);
            String savedUserPoint = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.OCB_USER_POINT);

            E_Cipher cp = E_Cipher.getInstance();
            try {
                savedUuid = cp.Decode(context, savedUuid);
                savedCardNum = cp.Decode(context, savedCardNum);
                savedUserPoint = cp.Decode(context, savedUserPoint);
                LogPrint.d("skkim updateUserInfo uuid :: " + uuid);
                LogPrint.d("skkim updateUserInfo user_point :: " + user_point);
                LogPrint.d("skkim updateUserInfo card_number :: " + card_number);
                LogPrint.d("skkim updateUserInfo savedUuid :: " + savedUuid);
                LogPrint.d("skkim updateUserInfo savedCardNum :: " + savedCardNum);
                LogPrint.d("skkim updateUserInfo savedUserPoint :: " + savedUserPoint);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean isApiCallPossible = false;
            String updatedCardNo = "";
            long updatedUserPoint = -1;

            if ( TextUtils.isEmpty(savedUuid) ) {
                isApiCallPossible = true;
            } else {
                if ( !TextUtils.isEmpty(uuid) && !savedUuid.equals(uuid) )
                    isApiCallPossible  = true;
            }

            if ( TextUtils.isEmpty(savedCardNum) ) {
                updatedCardNo = card_number;
            } else {
                if ( !TextUtils.isEmpty(card_number) && !savedCardNum.equals(card_number) )
                    updatedCardNo = card_number;
            }

            String s_point = user_point + "";
            if ( TextUtils.isEmpty(savedUserPoint) ) {
                updatedUserPoint = user_point;
            } else {
                if ( !TextUtils.isEmpty(s_point) && !savedUserPoint.equals(s_point) )
                    updatedUserPoint = user_point;
            }

            LogPrint.d("skkim updateUserInfo isApiCallPossible :: " + isApiCallPossible);
            LogPrint.d("skkim updateUserInfo updatedCardNo :: " + updatedCardNo);
            LogPrint.d("skkim updateUserInfo updatedUserPoint :: " + updatedUserPoint);
            if ( isApiCallPossible ) {
                CustomAsyncTask task = new CustomAsyncTask(context);
                task.updateUserInfo(uuid, user_point, card_number, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {
                        boolean isSuccess =  false;
                        int errorCode = -1;
                        if ( result ) {
                            JSONObject object = (JSONObject) obj;
                            if (object != null ) {
                                boolean rt = object.optBoolean("Result");
                                errorCode = object.optInt("errcode");
                                if ( rt )
                                    isSuccess = true;
                            } else {

                            }

                            if (SoftKeyboard.isKeyboardShow )
                                Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                            if ( !isSuccess ) {
                                LogPrint.d("skkim updateUserInfo errorCode :: " + errorCode);
                                if ( errorCode == 99 ) {
                                    LogPrint.d("skkim updateUserInfo init user info");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
                                } else {
                                    if (SoftKeyboard.isKeyboardShow )
                                        Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                LogPrint.d("skkim updateUserInfo success ");
                                E_Cipher cp = E_Cipher.getInstance();
                                String sec = uuid + card_number;
                                LogPrint.d("skkim updateUserInfo sec :: " + sec);
                                try {
                                    sec = sec.substring(0, 16);
                                    LogPrint.d("skkim updateUserInfo set all infos sec after  :: " + sec);
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
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
                                    LogPrint.d("skkim updateUserInfo error :: " + error);
                                    LogPrint.d("skkim updateUserInfo dError :: " + dError);
                                    if ( !TextUtils.isEmpty(error) ) {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if ( !TextUtils.isEmpty(dError) ) {
                                            Toast.makeText(context, dError, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else { // uuid가 바뀌지 않아 api 통신을 하지 않아도 됨.
                if ( !TextUtils.isEmpty(updatedCardNo) ) { // uuid는 안바꼈는데 카드 번호가 바뀜. 그렇게 되면 암호화 키 sec 값이 바뀌어야하므로 바뀐 sec값을 바탕으로 다시 값들을 세팅해줘야한다.
                    cp = E_Cipher.getInstance();
                    String sec = uuid + card_number;
                    LogPrint.d("skkim updateUserInfo uuid not change, card number changed sec :: " + sec);
                    try {
                        sec = sec.substring(0, 16);
                        LogPrint.d("skkim updateUserInfo after uuid not change, card number changed sec :: " + sec);
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { // uuid, card number, sec 값이 바뀌지 않았으므로 업데이트가 필요한 값들만 갱신한다.
                    LogPrint.d("skkim updateUserInfo uuid, card number not changed");
                    cp = E_Cipher.getInstance();
                    LogPrint.d("skkim updateUserInfo saved sec :: " + com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_SEC));
                    try {
                        if ( updatedUserPoint != -1 )
                            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        if ( uuid == null || TextUtils.isEmpty(uuid) ) {
//            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
//            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
//            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
//        } else {
//            String savedUuid = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
//            String savedCardNum = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.CARD_NUM);
//            String savedUserPoint = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.OCB_USER_POINT);
//            E_Cipher cp = E_Cipher.getInstance();
//            try {
//                savedUuid = cp.Decode(context, savedUuid);
//                savedCardNum = cp.Decode(context, savedCardNum);
//                savedUserPoint = cp.Decode(context, savedUserPoint);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            boolean isUpdatePossible = true;
//            if ( !TextUtils.isEmpty(savedUuid) && !TextUtils.isEmpty(uuid) && !TextUtils.isEmpty(card_number) && !TextUtils.isEmpty(savedCardNum)) {
//                if ( savedUuid.equals(uuid) && savedCardNum.equals(card_number) ) {
//                    isUpdatePossible = false;
//                }
//            }
//            if ( isUpdatePossible ) {
//                CustomAsyncTask task = new CustomAsyncTask(context);
//                task.updateUserInfo(uuid, user_point, card_number, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//                    @Override
//                    public void onResponse(boolean result, Object obj) {
//                        boolean isSuccess =  false;
//                        if ( result ) {
//                            JSONObject object = (JSONObject) obj;
//                            if (object != null ) {
//                                boolean rt = object.optBoolean("Result");
//                                if ( rt )
//                                    isSuccess = true;
//                            }
//                        }
//                        if (SoftKeyboard.isKeyboardShow )
//                            Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
//                        if ( !isSuccess ) {
//                            if (SoftKeyboard.isKeyboardShow )
//                                Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            LogPrint.d("get uuid 2 :: " + uuid);
//                            E_Cipher cp = E_Cipher.getInstance();
//                            String sec = uuid + card_number;
//                            LogPrint.d("skkim net sec :: " + sec);
//                            try {
//                                sec = sec.substring(0, 16);
//                                com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
//                                com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
//                                com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
//                                com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//            } else {
//                if ( !savedUserPoint.equals(user_point + "") ) {
//                    String sec = savedUuid + savedCardNum;
//                    LogPrint.d("skkim not net sec :: " + sec);
//                    try {
//                        sec = sec.substring(0, 16);
//                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }

    public static void updateUserInfo(Context context, String uuid, long user_point, String card_number, String track_id, String device_id) {
        if ( uuid == null || TextUtils.isEmpty(uuid) ) {
            LogPrint.d("updateUserInfo uuid is empty");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
        } else {
            LogPrint.d("skkim updateUserInfo uuid not null");
            String savedUuid = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
            String savedCardNum = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.CARD_NUM);
            String savedUserPoint = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.OCB_USER_POINT);
            String savedTrackId = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_TRACK_ID);
            String savedDeviceId = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_DEVICE_ID);

            E_Cipher cp = E_Cipher.getInstance();
            try {
                savedUuid = cp.Decode(context, savedUuid);
                savedCardNum = cp.Decode(context, savedCardNum);
                savedUserPoint = cp.Decode(context, savedUserPoint);
                savedTrackId = cp.Decode(context, savedTrackId);
                savedDeviceId = cp.Decode(context, savedDeviceId);
                LogPrint.d("skkim updateUserInfo uuid :: " + uuid);
                LogPrint.d("skkim updateUserInfo user_point :: " + user_point);
                LogPrint.d("skkim updateUserInfo card_number :: " + card_number);
                LogPrint.d("skkim updateUserInfo track_id :: " + track_id);
                LogPrint.d("skkim updateUserInfo device_id :: " + device_id);
                LogPrint.d("skkim updateUserInfo savedUuid :: " + savedUuid);
                LogPrint.d("skkim updateUserInfo savedCardNum :: " + savedCardNum);
                LogPrint.d("skkim updateUserInfo savedUserPoint :: " + savedUserPoint);
                LogPrint.d("skkim updateUserInfo savedTrackId :: " + savedTrackId);
                LogPrint.d("skkim updateUserInfo savedDeviceId :: " + savedDeviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean isApiCallPossible = false;
            String updatedCardNo = "";
            long updatedUserPoint = -1;
            String updatedTrackId = "";
            String updatedDeviceId = "";

            if ( TextUtils.isEmpty(savedUuid) ) {
                isApiCallPossible = true;
            } else {
                if ( !TextUtils.isEmpty(uuid) && !savedUuid.equals(uuid) )
                    isApiCallPossible  = true;
            }

            if ( TextUtils.isEmpty(savedCardNum) ) {
                updatedCardNo = card_number;
            } else {
                if ( !TextUtils.isEmpty(card_number) && !savedCardNum.equals(card_number) )
                    updatedCardNo = card_number;
            }

            String s_point = user_point + "";
            if ( TextUtils.isEmpty(savedUserPoint) ) {
                updatedUserPoint = user_point;
            } else {
                if ( !TextUtils.isEmpty(s_point) && !savedUserPoint.equals(s_point) )
                    updatedUserPoint = user_point;
            }

            if ( TextUtils.isEmpty(savedTrackId) ) {
                updatedTrackId = track_id;
            } else {
                if ( !TextUtils.isEmpty(track_id) && !savedTrackId.equals(track_id) )
                    updatedTrackId = track_id;
            }

            if ( TextUtils.isEmpty(savedDeviceId) ) {
                updatedDeviceId = device_id;
            } else {
                if ( !TextUtils.isEmpty(device_id) && !savedDeviceId.equals(device_id) )
                    updatedDeviceId = device_id;
            }
            LogPrint.d("skkim updateUserInfo isApiCallPossible :: " + isApiCallPossible);
            LogPrint.d("skkim updateUserInfo updatedCardNo :: " + updatedCardNo);
            LogPrint.d("skkim updateUserInfo updatedUserPoint :: " + updatedUserPoint);
            LogPrint.d("skkim updateUserInfo updatedTrackId :: " + updatedTrackId);
            LogPrint.d("skkim updateUserInfo updatedDeviceId :: " + updatedDeviceId);
            if ( isApiCallPossible ) {
                CustomAsyncTask task = new CustomAsyncTask(context);
                task.updateUserInfo(uuid, user_point, card_number, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {
                        boolean isSuccess =  false;
                        int errorCode = -1;
                        if ( result ) {
                            JSONObject object = (JSONObject) obj;
                            if (object != null ) {
                                boolean rt = object.optBoolean("Result");
                                errorCode = object.optInt("errcode");
                                if ( rt )
                                    isSuccess = true;
                            } else {

                            }

                            if (SoftKeyboard.isKeyboardShow )
                                Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                            if ( !isSuccess ) {
                                LogPrint.d("skkim updateUserInfo errorCode :: " + errorCode);
                                if ( errorCode == 99 ) {
                                    LogPrint.d("skkim updateUserInfo init user info");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
                                } else {
                                    if (SoftKeyboard.isKeyboardShow )
                                        Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                LogPrint.d("skkim updateUserInfo success ");
                                E_Cipher cp = E_Cipher.getInstance();
                                String sec = uuid + card_number;
                                LogPrint.d("skkim updateUserInfo sec :: " + sec);
                                try {
                                    sec = sec.substring(0, 16);
                                    LogPrint.d("skkim updateUserInfo set all infos sec after  :: " + sec);
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_TRACK_ID, cp.Encode(context, track_id));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_DEVICE_ID, cp.Encode(context, device_id));
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
                                    LogPrint.d("skkim updateUserInfo error :: " + error);
                                    LogPrint.d("skkim updateUserInfo dError :: " + dError);
                                    if ( !TextUtils.isEmpty(error) ) {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if ( !TextUtils.isEmpty(dError) ) {
                                            Toast.makeText(context, dError, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else { // uuid가 바뀌지 않아 api 통신을 하지 않아도 됨.
                if ( !TextUtils.isEmpty(updatedCardNo) ) { // uuid는 안바꼈는데 카드 번호가 바뀜. 그렇게 되면 암호화 키 sec 값이 바뀌어야하므로 바뀐 sec값을 바탕으로 다시 값들을 세팅해줘야한다.
                    cp = E_Cipher.getInstance();
                    String sec = uuid + card_number;
                    LogPrint.d("skkim updateUserInfo uuid not change, card number changed sec :: " + sec);
                    try {
                        sec = sec.substring(0, 16);
                        LogPrint.d("skkim updateUserInfo after uuid not change, card number changed sec :: " + sec);
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_TRACK_ID, cp.Encode(context, track_id));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_DEVICE_ID, cp.Encode(context, device_id));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { // uuid, card number, sec 값이 바뀌지 않았으므로 업데이트가 필요한 값들만 갱신한다.
                    LogPrint.d("skkim updateUserInfo uuid, card number not changed");
                    cp = E_Cipher.getInstance();
                    LogPrint.d("skkim updateUserInfo saved sec :: " + com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_SEC));
                    try {
                        if ( updatedUserPoint != -1 )
                            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                        if ( !TextUtils.isEmpty(updatedTrackId) )
                            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_TRACK_ID, cp.Encode(context, track_id));
                        if ( !TextUtils.isEmpty(updatedDeviceId) )
                            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_DEVICE_ID, cp.Encode(context, device_id));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void updateUserInfo(Context context, String uuid, long user_point, String card_number, boolean isUnderFourteen) {
        LogPrint.d("get uuid 1 :: " + uuid);
        SharedPreference.setBoolean(context, Key.KEY_IS_UNDER_FOURTEEN, isUnderFourteen);
        if ( uuid == null || TextUtils.isEmpty(uuid) ) {
            LogPrint.d("updateUserInfo uuid is empty");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
        } else {
            LogPrint.d("skkim updateUserInfo uuid not null");
            String savedUuid = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
            String savedCardNum = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.CARD_NUM);
            String savedUserPoint = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.OCB_USER_POINT);

            E_Cipher cp = E_Cipher.getInstance();
            try {
                savedUuid = cp.Decode(context, savedUuid);
                savedCardNum = cp.Decode(context, savedCardNum);
                savedUserPoint = cp.Decode(context, savedUserPoint);
                LogPrint.d("skkim updateUserInfo uuid :: " + uuid);
                LogPrint.d("skkim updateUserInfo user_point :: " + user_point);
                LogPrint.d("skkim updateUserInfo card_number :: " + card_number);
                LogPrint.d("skkim updateUserInfo savedUuid :: " + savedUuid);
                LogPrint.d("skkim updateUserInfo savedCardNum :: " + savedCardNum);
                LogPrint.d("skkim updateUserInfo savedUserPoint :: " + savedUserPoint);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean isApiCallPossible = false;
            String updatedCardNo = "";
            long updatedUserPoint = -1;

            if ( TextUtils.isEmpty(savedUuid) ) {
                isApiCallPossible = true;
            } else {
                if ( !TextUtils.isEmpty(uuid) && !savedUuid.equals(uuid) )
                    isApiCallPossible  = true;
            }

            if ( TextUtils.isEmpty(savedCardNum) ) {
                updatedCardNo = card_number;
            } else {
                if ( !TextUtils.isEmpty(card_number) && !savedCardNum.equals(card_number) )
                    updatedCardNo = card_number;
            }

            String s_point = user_point + "";
            if ( TextUtils.isEmpty(savedUserPoint) ) {
                updatedUserPoint = user_point;
            } else {
                if ( !TextUtils.isEmpty(s_point) && !savedUserPoint.equals(s_point) )
                    updatedUserPoint = user_point;
            }

            LogPrint.d("skkim updateUserInfo isApiCallPossible :: " + isApiCallPossible);
            LogPrint.d("skkim updateUserInfo updatedCardNo :: " + updatedCardNo);
            LogPrint.d("skkim updateUserInfo updatedUserPoint :: " + updatedUserPoint);
            if ( isApiCallPossible ) {
                CustomAsyncTask task = new CustomAsyncTask(context);
                task.updateUserInfo(uuid, user_point, card_number, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {
                        boolean isSuccess =  false;
                        int errorCode = -1;
                        if ( result ) {
                            JSONObject object = (JSONObject) obj;
                            if (object != null ) {
                                boolean rt = object.optBoolean("Result");
                                errorCode = object.optInt("errcode");
                                if ( rt )
                                    isSuccess = true;
                            } else {

                            }

                            if (SoftKeyboard.isKeyboardShow )
                                Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                            if ( !isSuccess ) {
                                LogPrint.d("skkim updateUserInfo errorCode :: " + errorCode);
                                if ( errorCode == 99 ) {
                                    LogPrint.d("skkim updateUserInfo init user info");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
                                } else {
                                    if (SoftKeyboard.isKeyboardShow )
                                        Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                LogPrint.d("skkim updateUserInfo success ");
                                E_Cipher cp = E_Cipher.getInstance();
                                String sec = uuid + card_number;
                                LogPrint.d("skkim updateUserInfo sec :: " + sec);
                                try {
                                    sec = sec.substring(0, 16);
                                    LogPrint.d("skkim updateUserInfo set all infos sec after  :: " + sec);
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
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
                                    LogPrint.d("skkim updateUserInfo error :: " + error);
                                    LogPrint.d("skkim updateUserInfo dError :: " + dError);
                                    if ( !TextUtils.isEmpty(error) ) {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if ( !TextUtils.isEmpty(dError) ) {
                                            Toast.makeText(context, dError, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else { // uuid가 바뀌지 않아 api 통신을 하지 않아도 됨.
                if ( !TextUtils.isEmpty(updatedCardNo) ) { // uuid는 안바꼈는데 카드 번호가 바뀜. 그렇게 되면 암호화 키 sec 값이 바뀌어야하므로 바뀐 sec값을 바탕으로 다시 값들을 세팅해줘야한다.
                    cp = E_Cipher.getInstance();
                    String sec = uuid + card_number;
                    LogPrint.d("skkim updateUserInfo uuid not change, card number changed sec :: " + sec);
                    try {
                        sec = sec.substring(0, 16);
                        LogPrint.d("skkim updateUserInfo after uuid not change, card number changed sec :: " + sec);
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { // uuid, card number, sec 값이 바뀌지 않았으므로 업데이트가 필요한 값들만 갱신한다.
                    LogPrint.d("skkim updateUserInfo uuid, card number not changed");
                    cp = E_Cipher.getInstance();
                    LogPrint.d("skkim updateUserInfo saved sec :: " + com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_SEC));
                    try {
                        if ( updatedUserPoint != -1 )
                            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        if ( uuid == null || TextUtils.isEmpty(uuid) ) {
//            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
//            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
//            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
//        } else {
//            String savedUuid = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
//            String savedCardNum = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.CARD_NUM);
//            String savedUserPoint = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.OCB_USER_POINT);
//            E_Cipher cp = E_Cipher.getInstance();
//            try {
//                savedUuid = cp.Decode(context, savedUuid);
//                savedCardNum = cp.Decode(context, savedCardNum);
//                savedUserPoint = cp.Decode(context, savedUserPoint);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            boolean isUpdatePossible = true;
//            if ( !TextUtils.isEmpty(savedUuid) && !TextUtils.isEmpty(uuid) && !TextUtils.isEmpty(card_number) && !TextUtils.isEmpty(savedCardNum)) {
//                if ( savedUuid.equals(uuid) && savedCardNum.equals(card_number) ) {
//                    isUpdatePossible = false;
//                }
//            }
//            if ( isUpdatePossible ) {
//                CustomAsyncTask task = new CustomAsyncTask(context);
//                task.updateUserInfo(uuid, user_point, card_number, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
//                    @Override
//                    public void onResponse(boolean result, Object obj) {
//                        boolean isSuccess =  false;
//                        if ( result ) {
//                            JSONObject object = (JSONObject) obj;
//                            if (object != null ) {
//                                boolean rt = object.optBoolean("Result");
//                                if ( rt )
//                                    isSuccess = true;
//                            }
//                        }
//                        if (SoftKeyboard.isKeyboardShow )
//                            Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
//                        if ( !isSuccess ) {
//                            if (SoftKeyboard.isKeyboardShow )
//                                Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            LogPrint.d("get uuid 2 :: " + uuid);
//                            E_Cipher cp = E_Cipher.getInstance();
//                            String sec = uuid + card_number;
//                            LogPrint.d("skkim net sec :: " + sec);
//                            try {
//                                sec = sec.substring(0, 16);
//                                com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
//                                com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
//                                com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
//                                com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//            } else {
//                if ( !savedUserPoint.equals(user_point + "") ) {
//                    String sec = savedUuid + savedCardNum;
//                    LogPrint.d("skkim not net sec :: " + sec);
//                    try {
//                        sec = sec.substring(0, 16);
//                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }

    public static void updateUserInfo(Context context, String uuid, long user_point, String card_number, String track_id, String device_id, boolean isUnderFourteen) {
        SharedPreference.setBoolean(context, Key.KEY_IS_UNDER_FOURTEEN, isUnderFourteen);
        if ( uuid == null || TextUtils.isEmpty(uuid) ) {
            LogPrint.d("updateUserInfo uuid is empty");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
        } else {
            LogPrint.d("skkim updateUserInfo uuid not null");
            String savedUuid = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
            String savedCardNum = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.CARD_NUM);
            String savedUserPoint = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.OCB_USER_POINT);
            String savedTrackId = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_TRACK_ID);
            String savedDeviceId = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_DEVICE_ID);

            E_Cipher cp = E_Cipher.getInstance();
            try {
                savedUuid = cp.Decode(context, savedUuid);
                savedCardNum = cp.Decode(context, savedCardNum);
                savedUserPoint = cp.Decode(context, savedUserPoint);
                savedTrackId = cp.Decode(context, savedTrackId);
                savedDeviceId = cp.Decode(context, savedDeviceId);
                LogPrint.d("skkim updateUserInfo uuid :: " + uuid);
                LogPrint.d("skkim updateUserInfo user_point :: " + user_point);
                LogPrint.d("skkim updateUserInfo card_number :: " + card_number);
                LogPrint.d("skkim updateUserInfo track_id :: " + track_id);
                LogPrint.d("skkim updateUserInfo device_id :: " + device_id);
                LogPrint.d("skkim updateUserInfo savedUuid :: " + savedUuid);
                LogPrint.d("skkim updateUserInfo savedCardNum :: " + savedCardNum);
                LogPrint.d("skkim updateUserInfo savedUserPoint :: " + savedUserPoint);
                LogPrint.d("skkim updateUserInfo savedTrackId :: " + savedTrackId);
                LogPrint.d("skkim updateUserInfo savedDeviceId :: " + savedDeviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean isApiCallPossible = false;
            String updatedCardNo = "";
            long updatedUserPoint = -1;
            String updatedTrackId = "";
            String updatedDeviceId = "";

            if ( TextUtils.isEmpty(savedUuid) ) {
                isApiCallPossible = true;
            } else {
                if ( !TextUtils.isEmpty(uuid) && !savedUuid.equals(uuid) )
                    isApiCallPossible  = true;
            }

            if ( TextUtils.isEmpty(savedCardNum) ) {
                updatedCardNo = card_number;
            } else {
                if ( !TextUtils.isEmpty(card_number) && !savedCardNum.equals(card_number) )
                    updatedCardNo = card_number;
            }

            String s_point = user_point + "";
            if ( TextUtils.isEmpty(savedUserPoint) ) {
                updatedUserPoint = user_point;
            } else {
                if ( !TextUtils.isEmpty(s_point) && !savedUserPoint.equals(s_point) )
                    updatedUserPoint = user_point;
            }

            if ( TextUtils.isEmpty(savedTrackId) ) {
                updatedTrackId = track_id;
            } else {
                if ( !TextUtils.isEmpty(track_id) && !savedTrackId.equals(track_id) )
                    updatedTrackId = track_id;
            }

            if ( TextUtils.isEmpty(savedDeviceId) ) {
                updatedDeviceId = device_id;
            } else {
                if ( !TextUtils.isEmpty(device_id) && !savedDeviceId.equals(device_id) )
                    updatedDeviceId = device_id;
            }
            LogPrint.d("skkim updateUserInfo isApiCallPossible :: " + isApiCallPossible);
            LogPrint.d("skkim updateUserInfo updatedCardNo :: " + updatedCardNo);
            LogPrint.d("skkim updateUserInfo updatedUserPoint :: " + updatedUserPoint);
            LogPrint.d("skkim updateUserInfo updatedTrackId :: " + updatedTrackId);
            LogPrint.d("skkim updateUserInfo updatedDeviceId :: " + updatedDeviceId);
            if ( isApiCallPossible ) {
                CustomAsyncTask task = new CustomAsyncTask(context);
                task.updateUserInfo(uuid, user_point, card_number, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {
                        boolean isSuccess =  false;
                        int errorCode = -1;
                        if ( result ) {
                            JSONObject object = (JSONObject) obj;
                            if (object != null ) {
                                boolean rt = object.optBoolean("Result");
                                errorCode = object.optInt("errcode");
                                if ( rt )
                                    isSuccess = true;
                            } else {

                            }

                            if (SoftKeyboard.isKeyboardShow )
                                Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                            if ( !isSuccess ) {
                                LogPrint.d("skkim updateUserInfo errorCode :: " + errorCode);
                                if ( errorCode == 99 ) {
                                    LogPrint.d("skkim updateUserInfo init user info");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, "");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, "");
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, "0");
                                } else {
                                    if (SoftKeyboard.isKeyboardShow )
                                        Toast.makeText(context, "연동에 실패하였습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                LogPrint.d("skkim updateUserInfo success ");
                                E_Cipher cp = E_Cipher.getInstance();
                                String sec = uuid + card_number;
                                LogPrint.d("skkim updateUserInfo sec :: " + sec);
                                try {
                                    sec = sec.substring(0, 16);
                                    LogPrint.d("skkim updateUserInfo set all infos sec after  :: " + sec);
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_TRACK_ID, cp.Encode(context, track_id));
                                    com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_DEVICE_ID, cp.Encode(context, device_id));
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
                                    LogPrint.d("skkim updateUserInfo error :: " + error);
                                    LogPrint.d("skkim updateUserInfo dError :: " + dError);
                                    if ( !TextUtils.isEmpty(error) ) {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if ( !TextUtils.isEmpty(dError) ) {
                                            Toast.makeText(context, dError, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else { // uuid가 바뀌지 않아 api 통신을 하지 않아도 됨.
                if ( !TextUtils.isEmpty(updatedCardNo) ) { // uuid는 안바꼈는데 카드 번호가 바뀜. 그렇게 되면 암호화 키 sec 값이 바뀌어야하므로 바뀐 sec값을 바탕으로 다시 값들을 세팅해줘야한다.
                    cp = E_Cipher.getInstance();
                    String sec = uuid + card_number;
                    LogPrint.d("skkim updateUserInfo uuid not change, card number changed sec :: " + sec);
                    try {
                        sec = sec.substring(0, 16);
                        LogPrint.d("skkim updateUserInfo after uuid not change, card number changed sec :: " + sec);
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_SEC, sec);
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_USER_ID, cp.Encode(context, uuid));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.CARD_NUM, cp.Encode(context, card_number));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_TRACK_ID, cp.Encode(context, track_id));
                        com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_DEVICE_ID, cp.Encode(context, device_id));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { // uuid, card number, sec 값이 바뀌지 않았으므로 업데이트가 필요한 값들만 갱신한다.
                    LogPrint.d("skkim updateUserInfo uuid, card number not changed");
                    cp = E_Cipher.getInstance();
                    LogPrint.d("skkim updateUserInfo saved sec :: " + com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_SEC));
                    try {
                        if ( updatedUserPoint != -1 )
                            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.OCB_USER_POINT, cp.Encode(context, user_point + ""));
                        if ( !TextUtils.isEmpty(updatedTrackId) )
                            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_TRACK_ID, cp.Encode(context, track_id));
                        if ( !TextUtils.isEmpty(updatedDeviceId) )
                            com.enliple.keyboard.activity.SharedPreference.setString(context, Key.KEY_OCB_DEVICE_ID, cp.Encode(context, device_id));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean isSelectedCKeyboard(Context context) {
        String currentKeyboard = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        String keyboardName = context.getPackageName() + "/com.enliple.keyboard.activity.SoftKeyboard";
        LogPrint.e("KeyboardLoadingActivity currentKeyboard :: " + currentKeyboard);
        LogPrint.e("KeyboardLoadingActivity keyboardName :: " + keyboardName);
        return keyboardName.equals(currentKeyboard);
    }

    // 외부업체에서 키보드 선택 호출.
    public static void showInputMethodPicker(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();
    }

//    public static set() {
//
//    }
//    public static get() {
//
//    }

    public static String GetUuid(Context context) {
        String id = com.enliple.keyboard.activity.SharedPreference.getString(context, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(context, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static boolean CallOfferwall(Context context, String message) {
        if ( isSelectedCKeyboard(context) && !TextUtils.isEmpty(GetUuid(context)) ) {
            Intent intent = new Intent(context, KeyboardHybridOfferwallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } else {
            if ( !isSelectedCKeyboard(context) && !TextUtils.isEmpty(GetUuid(context)) ) {
                context.startActivity(new Intent(context, IntroActivity.class));
                return true;
            } else {
                if ( !TextUtils.isEmpty(message) )
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }
}
