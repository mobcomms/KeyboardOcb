package com.enliple.keyboard.emoji.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.activity.SoftKeyboard;
import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.managers.PreferenceManager;
import com.enliple.keyboard.models.RecentEmojiModel;
import com.enliple.keyboard.receiver.MyReceiver;
import com.enliple.keyboard.ui.common.LogPrint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-04-14.
 */

public class RecentAdapter extends BaseEmojiAdapter {
    private static final int SOUND_0 = 0;
    private static final int SOUND_1 = 1;
    private static final int SOUND_2 = 2;
    private static final int SOUND_3 = 3;
    private static final int SOUND_4 = 4;
    private Vibrator mVibrator = null;
    //    private float mVolumeLevel = 0;
    private int mVolumeLevel = 0;
    private long mVibrateLevel = 0;
    private int mSoundId = 0;
    private SoundPool mSoundPool = null;
    private AudioManager mAudioManager;
    private int mAudioMode = 0;
    private int mMediaVolume;
    private Context mContext;
    private int mStreamId = 0;
    private boolean mIsMatchedEmoji = false;
    private String mKwd = "";
    private Handler mSoundHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (mAudioManager != null)
                    mAudioMode = mAudioManager.getRingerMode();
                try {
                    if (mVolumeLevel > 0 && mAudioMode == AudioManager.RINGER_MODE_NORMAL && mSoundPool != null) {
                        float vol = Common.getVolume("RecentAdapter", mMediaVolume, mVolumeLevel);
                        if ( mSoundPool != null ) {
                            mSoundPool.stop(mSoundId);
                            mStreamId = mSoundPool.play(mSoundId, vol, vol, 0, 0, 1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == 1) {
                if (mSoundPool != null) {
                    mSoundPool.stop(mStreamId);
                }
            }
        }
    };

    public RecentAdapter(Context context) {
        super((SoftKeyboard) context);
        KeyboardLogPrint.w("RecentAdapter creator");
//        context.registerReceiver(mSetChange, new IntentFilter(SoftKeyboard.SET_CHANGE));
//        context.registerReceiver(mSetChange, new IntentFilter(MyReceiver.VOLUME_CHANGE));
//        context.registerReceiver(mSetChange, new IntentFilter("SOUND_CHANGE"));
        mContext = context;
        if (mAudioManager == null)
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioMode = mAudioManager.getRingerMode();
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mVolumeLevel = SharedPreference.getInt(context, Common.PREF_I_VOLUME_LEVEL);
        if (mVolumeLevel < 0) {
            SharedPreference.setInt(context, Common.PREF_I_VOLUME_LEVEL, Common.DEFAULT_SOUND_LEVEL);
            mVolumeLevel = Common.DEFAULT_SOUND_LEVEL;
        }

        mMediaVolume = Common.getStreamLevel(context);
//        mMediaVolume = SharedPreference.getInt(context, Common.MEDIA_VOLUME_LEVEL);
//        if ( mMediaVolume < 0 )
//        {
//            mMediaVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC );
//            SharedPreference.setInt(context, Common.MEDIA_VOLUME_LEVEL, mMediaVolume);
//        }

        mVibrateLevel = SharedPreference.getLong(context, Common.PREF_VIBRATE_LEVEL);
        initSound(context);
        setupRecentDataFromList(false);
    }

    public int setupRecentDataFromList(boolean isMatched) {
        KeyboardLogPrint.e("setupRecentDataFromList");
        List<RecentEmojiModel> recentEntries = PreferenceManager.getInstance(mContext).getRecentEmoji();

        if (recentEntries != null) {
            emojiTexts = new ArrayList<String>();
            mIsMatchedEmoji = isMatched;
            mKwd = "";
            for (RecentEmojiModel _data : recentEntries) {
                emojiTexts.add(_data.getUnicode());
                KeyboardLogPrint.e("setupRecentDataFromList in for");
            }
            notifyDataSetChanged();
        }
        if ( emojiTexts != null ) {
            return emojiTexts.size();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View textView = (TextView) super.getView(position, convertView, parent);
        KeyboardLogPrint.w("getView RecentAdapter");
        textView.setSoundEffectsEnabled(false);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSoundHandler != null) {
                    mSoundHandler.sendEmptyMessage(0);
                }
                if (mVibrateLevel > 0)
                    mVibrator.vibrate(mVibrateLevel * Common.VIBRATE_MUL);
                KeyboardLogPrint.w("RecentAdapter click mKwd :: " + mKwd);

                mSoftKeyBoard.sendText(emojiTexts.get(position));
                PreferenceManager.getInstance(mContext).setRecentEmoji(emojiTexts.get(position));

            }
        });
        return textView;

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private void initSound(Context context) {
        int selected_sound = SharedPreference.getInt(context, Common.PREF_SELECTED_SOUND) < 0 ? 0 : SharedPreference.getInt(context, Common.PREF_SELECTED_SOUND);
        LogPrint.d("################# get RecentAdapter sound change position ::: " + selected_sound);
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

            mSoundId = mSoundPool.load(context, resId, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private BroadcastReceiver mSetChange = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (SoftKeyboard.SET_CHANGE.equals(action)) {
//                mVolumeLevel = SharedPreference.getInt(context, Common.PREF_I_VOLUME_LEVEL);
//                if (mVolumeLevel < 0) {
//                    SharedPreference.setInt(context, Common.PREF_I_VOLUME_LEVEL, Common.DEFAULT_SOUND_LEVEL);
//                    mVolumeLevel = Common.DEFAULT_SOUND_LEVEL;
//                }
//                mVibrateLevel = SharedPreference.getLong(context, Common.PREF_VIBRATE_LEVEL);
//                KeyboardLogPrint.w("RecentAdapter onReceive volume :: " + mVolumeLevel);
//                KeyboardLogPrint.w("RecentAdapter onReceive vibrate :: " + mVibrateLevel);
//            } else if (MyReceiver.VOLUME_CHANGE.equals(action)) {
////                mMediaVolume = SharedPreference.getInt(context, Common.MEDIA_VOLUME_LEVEL);
//                mMediaVolume = Common.getStreamLevel(context);
//            } else if ("SOUND_CHANGE".equals(action)) {
//                if ( intent != null ) {
//                    int sound = intent.getIntExtra("change_sound", 0);
//                    SharedPreference.setInt(mContext, Common.PREF_SELECTED_SOUND, sound);
//                }
//                initSound(mContext);
//            }
//        }
//    };

    private ArrayList<String> reorderedArray(String kwd, ArrayList<String> array) {
        ArrayList<String> returnArray = new ArrayList<String>();
        ArrayList<String> tArray = array;
        String matchedUnicode = "";
        KeyboardLogPrint.w("kwd :: " + kwd);

        for (int i = 0; i < tArray.size(); i++) {
            if (kwd.equals(tArray.get(i))) {
                KeyboardLogPrint.w("array.get(i) 1 :: " + tArray.get(i));
                matchedUnicode = tArray.get(i);
                KeyboardLogPrint.w("matchedUnicode :: " + matchedUnicode);
                tArray.remove(i);
            } else {
                KeyboardLogPrint.e("array.get(i) 2 :: " + tArray.get(i));
            }
        }
        returnArray.add(matchedUnicode);
        returnArray.addAll(tArray);
        return returnArray;
    }
}
