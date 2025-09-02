package com.enliple.keyboard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.load.engine.DiskCacheStrategy;
import com.enliple.keyboard.imgmodule.request.target.CustomTarget;
import com.enliple.keyboard.imgmodule.request.transition.Transition;
import com.enliple.keyboard.imgmodule.signature.ObjectKey;
import com.enliple.keyboard.models.AdChoices;
import com.enliple.keyboard.models.OfferwallData;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.network.Url;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.offerwall.OnKeyboardSingleClickListener;

public class KeyboardOfferwallGuideActivity extends AppCompatActivity {
    private TextView guide_btn_back;
    private ImageView guide_image;
    private TextView btn_join_mission;
    private OfferwallData data;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_offerwall_guide);

        Intent intent = getIntent();
        if ( intent != null ) {
            data = (OfferwallData)intent.getSerializableExtra("intent_mission");
            if ( data == null )
                finish();
        } else {
            finish();
        }

        guide_btn_back = findViewById(R.id.guide_btn_back);
        guide_image = findViewById(R.id.guide_image);
        btn_join_mission = findViewById(R.id.btn_join_mission);

        String guide_image_path = Url.OFFERWALL_FILE_PATH + data.getMission_class() + "/guide.png";
        LogPrint.d("guide_image_path :: " + guide_image_path);

        ImageModule.with(KeyboardOfferwallGuideActivity.this)
                .asBitmap().load(guide_image_path).diskCacheStrategy(DiskCacheStrategy.NONE).
                signature(new ObjectKey(System.currentTimeMillis())).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                guide_image.setImageBitmap(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

        guide_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_join_mission.setOnClickListener(new OnKeyboardSingleClickListener() {
            @Override
            protected void onSingleClick(View v) {
                Intent intent = new Intent(KeyboardOfferwallGuideActivity.this, KeyboardOfferwallWebViewActivity.class);
                intent.putExtra("intent_mission", data);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
