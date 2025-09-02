package com.enliple.keyboard.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.enliple.keyboard.activity.SharedPreference;

/**
 * Created by Administrator on 2017-03-28.
 */

public class Sound
{
    MediaPlayer mPlayer = null;

    public Sound(Context context, int id)
    {
        mPlayer = MediaPlayer.create(context, id);
    }

    public void play()
    {
        mPlayer.seekTo(0);
        mPlayer.start();
    }

    public void release()
    {
        if ( mPlayer != null )
        {
            mPlayer.reset();
            mPlayer.release();
        }
    }

    private static int mDefStreamId = -1;
    public static void soundPlay(Context context,
                                 AudioManager mAudioManager,
                                 String tag,
                                 final SoundPool mSoundPool,
                                 final int soundResId){

        int mMediaVolume = SharedPreference.getInt(context, Common.MEDIA_VOLUME_LEVEL);
        if ( mMediaVolume < 0 )
        {
            mMediaVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            KeyboardLogPrint.e("soundPlay mMediaVolume less zero :: "  + mMediaVolume );
            SharedPreference.setInt(context, Common.MEDIA_VOLUME_LEVEL, mMediaVolume);
        } else {
            KeyboardLogPrint.e("soundPlay mMediaVolume over zero :: "  + mMediaVolume );
        }

        int mCalcVolume = 0;
        int volLevel = SharedPreference.getInt(context, Common.PREF_I_VOLUME_LEVEL);
        if ( volLevel < 0 )
        {

            SharedPreference.setInt(context, Common.PREF_I_VOLUME_LEVEL, Common.DEFAULT_SOUND_LEVEL);
            mCalcVolume = Common.DEFAULT_SOUND_LEVEL;
            KeyboardLogPrint.e("soundPlay volLevel less zero mCalcVolume :: "  +  mCalcVolume);
        } else {
            mCalcVolume = volLevel;
            KeyboardLogPrint.e("soundPlay volLevel over zero mCalcVolume :: "  +  mCalcVolume);
        }

        final float fLevel = Common.getVolume(tag, mMediaVolume, mCalcVolume);
        KeyboardLogPrint.e("soundPlay fLevel :: "  +  fLevel);
        try {
            KeyboardLogPrint.e("soundPlay mDefStreamId :: "  +  mDefStreamId);
            if (mDefStreamId != -1) {
                mSoundPool.stop(mDefStreamId);
            }
            mDefStreamId = mSoundPool.play(soundResId, fLevel, fLevel, 0, 0, 1);
            KeyboardLogPrint.e("soundPlay after mDefStreamId :: "  +  mDefStreamId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
