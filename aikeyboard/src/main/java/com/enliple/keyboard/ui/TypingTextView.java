package com.enliple.keyboard.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;

public class TypingTextView extends AppCompatTextView {
    private CharSequence text;
    private int textLength, count, duration;
    private TypingEndListener listener;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (count != textLength) {
                count++;
                setText(text.subSequence(0, count));
                postDelayed(this, duration);
            } else {
                if ( listener != null )
                    listener.typingEnd();
                removeCallbacks(this);
            }
        }
    };

    public interface TypingEndListener {
        void typingEnd();
    }

    public void addTypingEndListener(TypingEndListener listener) {
        this.listener = listener;
    }

    public TypingTextView(Context context) {
        super(context);
    }

    public TypingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param text     Text to be displayed.
     * @param duration Animation duration in milliseconds.
     */
    public void typeText(@NonNull CharSequence text, int duration) {
        startTyping(text, duration);
    }

    /**
     * @param resourceId Text to be displayed using a string resource identifier.
     * @param duration   Animation duration in milliseconds.
     */
    public void typeText(@StringRes int resourceId, int duration) {
        startTyping(getResources().getString(resourceId), duration);
    }

    private void startTyping(@NonNull CharSequence text, int duration) {
        count = 0;
        this.duration = duration;
        this.text = text;
        if (TextUtils.isEmpty(text)) {
            return;
        }
        textLength = TextUtils.getTrimmedLength(text);
        if (textLength == 0) {
            return;
        }
        postDelayed(runnable, duration);
    }
}
