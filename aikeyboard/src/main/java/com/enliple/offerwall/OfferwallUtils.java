package com.enliple.offerwall;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.enliple.keyboard.ui.common.LogPrint;

import java.net.URISyntaxException;
import java.util.List;

public class OfferwallUtils {
    public static boolean callApp(Activity activity, String url) {
        Intent intent = null;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            LogPrint.d("intent getScheme     +++===> " + intent.getScheme());
            LogPrint.d("intent getDataString +++===> " + intent.getDataString());
        } catch (Exception ex) {
            LogPrint.d("Bad URI " + url + ":" + ex.getMessage());
            return false;
        }
        return callAppResult(intent, activity, url);
    }

    private static boolean callAppResult(Intent intent, Activity activity, String url) {
        try {
            LogPrint.d("callAppResult url :: " + url);
            boolean retval = false;

            if (url.startsWith("intent")) {
                if (activity.getPackageManager().resolveActivity(intent, 0) == null) {
                    String packagename = intent.getPackage();
                    if (packagename != null) {
                        try {
                            Uri uri = Uri.parse(intent.getDataString());

                            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            return true;
                        } catch (ActivityNotFoundException e) {
                            if (intent == null) return false;
                            try {
                                String packageName = intent.getPackage();
                                if (packageName != null) {
                                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                                    return true;
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            return false;
                        }
//                        Uri uri = Uri.parse("market://search?q=pname:" + packagename);
//                        intent = new Intent(Intent.ACTION_VIEW, uri);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        activity.startActivity(intent);
//                        retval = true;
                    }
                }
                else {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setComponent(null);
                    try {
                        if (activity.startActivityIfNeeded(intent, -1)) {
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
                PackageManager pm = activity.getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
                for ( int i = 0 ; i < activityList.size() ; i ++ ) {
                    ResolveInfo app =  activityList.get(i);
                    if (app.activityInfo.name.contains("com.kakao.talk")) {
                        bKakaoTalk = true;
                        break;
                    }
                    if (app.activityInfo.name.contains("com.kakao.story")) {
                        bKakaoStory = true;
                        break;
                    }
                }
                try {
                    // 해당 앱이 없을때 마켓으로 연결
                    if ((url.startsWith("kakaolink://") || (url.startsWith("kakaotalk://")) )&& !bKakaoTalk) {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.setData(Uri.parse("market://details?id=" + "com.kakao.talk"));
                        activity.startActivityForResult(intent1, 0);
                        retval = true;
                    } else if (url.startsWith("storylink://") && !bKakaoStory) {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.setData(Uri.parse("market://details?id=" + "com.kakao.story"));
                        activity.startActivityForResult(intent1, 0);
                        retval = true;
                    } else {
                        Uri uri = Uri.parse(url);
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.setData(uri);
                        activity.startActivityForResult(intent1, 0);
                        retval = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return retval;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
