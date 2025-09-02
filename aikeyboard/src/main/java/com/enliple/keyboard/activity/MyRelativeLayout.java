package com.enliple.keyboard.activity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.enliple.keyboard.common.KeyboardLogPrint;

public class MyRelativeLayout extends RelativeLayout
{
    private Context mContext = null;
    public MyRelativeLayout(Context context)
    {
        super(context, null, 0);
        KeyboardLogPrint.e("creater");
        mContext = context;
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        KeyboardLogPrint.e("creater 1");
        mContext = context;
    }

    public MyRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        KeyboardLogPrint.e("creater 2");
        mContext = context;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        KeyboardLogPrint.e("RelativeLayout event.getAction() :: " + event.getAction());
        KeyboardLogPrint.e("RelativeLayout event.getKeyCode() :: " + event.getKeyCode());
        if (event.getAction() == KeyEvent.ACTION_DOWN ) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Intent subPopupFinishIntent = new Intent(MainKeyboardView.BACK_KEY_LISTENER);
                    mContext.sendBroadcast(subPopupFinishIntent);
                    break;
                case KeyEvent.KEYCODE_MENU:
                    Intent menuIntent = new Intent("MENU_KEY_LISTENER");
                    mContext.sendBroadcast(menuIntent);
                    break;
                default:
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public interface MyKeyEventCallbackListener
    {
        void onKeyEvent(KeyEvent event);
    }
}