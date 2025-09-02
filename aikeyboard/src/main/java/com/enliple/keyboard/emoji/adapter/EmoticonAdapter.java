package com.enliple.keyboard.emoji.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.activity.SoftKeyboard;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.ThemeManager;
import com.enliple.keyboard.common.ThemeModel;
import com.enliple.keyboard.managers.PreferenceManager;

public class EmoticonAdapter extends RecyclerView.Adapter<EmoticonAdapter.ViewHolder> {

    private String[] mList;
    private long mVibrateLevel = 0;
    private Vibrator mVibrator = null;
    private RecentEmoticonAdapter recentAdapter;

    public EmoticonAdapter(Context context, String[] list, RecentEmoticonAdapter _recentAdapter) {
        mList = list;
        recentAdapter = _recentAdapter;
        mVibrateLevel = SharedPreference.getLong(context, Common.PREF_VIBRATE_LEVEL);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoticon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_keyword.setText(mList[position]);
        holder.tv_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if ( mSoundHandler != null ) {
//                    mSoundHandler.sendEmptyMessage(0);
//                }
//                KeyboardLogPrint.w("StaticEmojiAdapter click");
//
                if (mVibrateLevel > 0)
                    mVibrator.vibrate(mVibrateLevel * Common.VIBRATE_MUL);

                ((SoftKeyboard) holder.tv_keyword.getContext()).sendText(mList[position]);
                PreferenceManager.getInstance(holder.tv_keyword.getContext()).setRecentEmoticon(mList[position]);
                if (recentAdapter != null)
                    recentAdapter.setupRecentDataFromList();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_keyword;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_keyword = (TextView) itemView.findViewById(R.id.tv_keyword);
        }
    }

}
