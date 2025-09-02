package com.enliple.keyboard.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.enliple.keyboard.R;

public class AikbdSelectDialog extends Dialog {
    private Context context;
    private Button aikbd_dialog_cancel, aikbd_dialog_ok;
    private Listener listener;
    public interface Listener {
        void cancel();
        void ok();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aikbd_select_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        aikbd_dialog_cancel = findViewById(R.id.aikbd_dialog_cancel);
        aikbd_dialog_ok = findViewById(R.id.aikbd_dialog_ok);
        aikbd_dialog_cancel.setOnClickListener(clickListener);
        aikbd_dialog_ok.setOnClickListener(clickListener);
        super.onCreate(savedInstanceState);
    }

    public AikbdSelectDialog(Context context, Listener lsn) {
        super(context);
        this.context = context;
        this.listener = lsn;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v.getId() == R.id.aikbd_dialog_cancel ) {
                if (listener != null)
                    listener.cancel();
                dismiss();
            } else if ( v.getId() == R.id.aikbd_dialog_ok ) {
                if (listener != null)
                    listener.ok();
                dismiss();
            }
        }
    };

}