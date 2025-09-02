package com.enliple.keyboard.receiver;

/**
 * Created by Administrator on 2017-06-15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;

public class MyReceiver extends BroadcastReceiver {
    public static final String VOLUME_CHANGE = "volume_change";
    private AudioManager mManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        KeyboardLogPrint.w("receiver volume change");
        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            mManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int volume = mManager.getStreamVolume(AudioManager.STREAM_MUSIC );
            SharedPreference.setInt(context, Common.MEDIA_VOLUME_LEVEL, volume);
            Intent volumeChange = new Intent(VOLUME_CHANGE);
            context.sendBroadcast(volumeChange);
        } else if ( intent.getAction().equals("android.intent.action.PACKAGE_CHANGED")) {
            KeyboardLogPrint.e("Cashkeyboard Package changed");
//            AIKeyboardSDK.init(context).SetDefaultTheme();
        }
    }
}