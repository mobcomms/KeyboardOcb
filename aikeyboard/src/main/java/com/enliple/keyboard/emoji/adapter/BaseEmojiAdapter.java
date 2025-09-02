package com.enliple.keyboard.emoji.adapter;

import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.emoji.text.EmojiCompat;

import com.enliple.keyboard.activity.SoftKeyboard;
import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;

import java.util.ArrayList;

public abstract class BaseEmojiAdapter extends BaseAdapter {

    protected SoftKeyboard mSoftKeyBoard;
    protected ArrayList<String> emojiTexts;
    protected int MaxRecentData = 35;

    public BaseEmojiAdapter(SoftKeyboard emojiKeyboardService ) {
        this.mSoftKeyBoard = emojiKeyboardService;
    }

    @Override
    public int getCount() {
        return emojiTexts.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TextView textView;
        if (convertView == null) {
            textView = new TextView(mSoftKeyBoard);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.0f);
            textView.setTextColor(Color.BLACK);
        } else {
            textView = (TextView) convertView;
        }

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            EmojiCompat compat = EmojiCompat.get();
            textView.setText(compat.process(emojiTexts.get(position)));
        }else {
            textView.setText(Html.fromHtml(emojiTexts.get(position)));
        }

        textView.setBackgroundResource(R.drawable.aikbd_btn_background);

        return textView;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
