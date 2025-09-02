package com.enliple.keyboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.OlabangItem;
import com.enliple.keyboard.common.TimeDealModel;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;
import com.enliple.keyboard.models.BrandModel;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Key;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OCBBrandAdapter extends PagerAdapter  {
    private Context context = null;
    private int width;
    private ArrayList<BrandModel> items = new ArrayList<>();
    RakeAPI rake;
    public OCBBrandAdapter(Context context, int screenWidth, RakeAPI rk) {
        this.context = context;
        width = screenWidth;
        rake = rk;
    }

    public void setItems(List<BrandModel> its) {
        if (this.items != null && items.size() > 0 ) {
            this.items.clear();
            items = new ArrayList<>();
        }
        this.items.addAll(its);
        notifyDataSetChanged();
    }

    public int getItemSize() {
        if ( this.items != null ) {
            return items.size();
        } else {
            return 0;
        }
    }

    public BrandModel getItem(int position) {
        if ( items != null && items.size() > 0 ) {
            return items.get(position);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;

        if ( context != null ) {
            BrandModel item = items.get(position);
            if ( item != null ) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.aikbd_brand_item, container, false);
                View root = view;
                ImageView image = view.findViewById(R.id.image);
                ImageView badge = view.findViewById(R.id.badge);
                CardView card = view.findViewById(R.id.card);
                TextView current = view.findViewById(R.id.aikbd_current);
                TextView total = view.findViewById(R.id.aikbd_total);
                String sCurrent = item.getCurrent() + "";
                String sTotal = "/" + item.getTotal();
                current.setText(sCurrent);
                total.setText(sTotal);
                int wdt = width - Common.convertDpToPx(context, 40);
                int ht = (int)((165 * wdt) / 320);
                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) card.getLayoutParams();
                param.width = wdt;
                param.height = ht;
                param.addRule(RelativeLayout.CENTER_HORIZONTAL);
                card.setLayoutParams(param);
                card.setRadius(Common.convertDpToPx(context, 15));
                try {
                    ImageLoader.with(context).from(item.getImagePath()).transform(ImageUtils.fitCenter()).noStorageCache().noMemoryCache().load(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String time = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
                            OCBLogSentinelShuttle shuttle = new OCBLogSentinelShuttle();
                            String track_id = SharedPreference.getString(context, Key.KEY_OCB_TRACK_ID);
                            String device_id = SharedPreference.getString(context, Key.KEY_OCB_DEVICE_ID);
                            try {
                                E_Cipher cp = E_Cipher.getInstance();
                                track_id = cp.Decode(context, track_id);
                                device_id = cp.Decode(context, device_id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String session_id = time + "_" + device_id;
                            shuttle.page_id("/keyboard/brandad").action_id("tap.brandad").session_id(session_id).mbr_id(track_id);
                            rake.track(shuttle.toJSONObject());
                            String url = item.getLinkUrl();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                            CustomAsyncTask task = new CustomAsyncTask(context);
                            task.postStats("brand_click", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                @Override
                                public void onResponse(boolean result, Object obj) {

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (items != null && items.size() > 0) {
            return items.size();
        } else {
            return 0;
        }
    }
}
