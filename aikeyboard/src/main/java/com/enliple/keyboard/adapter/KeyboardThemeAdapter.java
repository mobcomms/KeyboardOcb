package com.enliple.keyboard.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.ThemeListInfo;
import com.enliple.keyboard.imageloader.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-11-15.
 */

public class KeyboardThemeAdapter extends BaseAdapter {
    private ArrayList<ThemeListInfo> mItemList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mSize;
    private int mImageWidth;
    private int mImageHeight;
    private String mUsedName = null;
    public KeyboardThemeAdapter(Context context, float size) {
        mContext = context;
        mSize = (int) size;
        mImageWidth = (int)(size - convertDpToPixel(12, mContext));
        mImageHeight = (int)(mImageWidth / 1.44);
        KeyboardLogPrint.e("mSize :: " + mSize);
        KeyboardLogPrint.e("mImageWidth :: " + mImageWidth);
        KeyboardLogPrint.e("mImageHeight :: " + mImageHeight);
        mItemList = new ArrayList();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setItemList(ArrayList<ThemeListInfo> resultList) {
        if (mItemList != null) {
            mItemList = new ArrayList();
            mItemList.addAll(resultList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItemList != null ? mItemList.size() : 0;
    }

    @Override
    public ThemeListInfo getItem(int i) {
        if (mItemList != null) {
            return mItemList.get(i);
        }
        return null;
    }

    public void setUseFileName(String name) {
        KeyboardLogPrint.e("unzipFolderName setUseFileName :: " + name);
        mUsedName = name;
    }

    public String getUseFileName() {
        if ( mUsedName != null )
            return mUsedName;
        else
            return "";
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int p, View v, ViewGroup vg) {
        ViewHolder vh = null;
        ThemeListInfo model = mItemList.get(p);
        try {
            if (v == null) {
                vh = new ViewHolder();
                v = mInflater.inflate(R.layout.aikbd_theme_list_item, null);
                vh.root = (RelativeLayout) v.findViewById(R.id.root);
                vh.image_layer = (RelativeLayout) v.findViewById(R.id.image_layer);
                vh.image = (ImageView) v.findViewById(R.id.image);
                vh.name = (TextView) v.findViewById(R.id.name);
                vh.check = (TextView) v.findViewById(R.id.check_img);
                vh.used = (TextView) v.findViewById(R.id.used_img);
                v.setTag(vh);
            } else {
                vh = (ViewHolder) v.getTag();
            }

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(mImageWidth, mImageHeight);
            param.addRule(RelativeLayout.CENTER_HORIZONTAL);

            RelativeLayout.LayoutParams layer_params = new RelativeLayout.LayoutParams(mSize, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layer_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            vh.image_layer.setLayoutParams(layer_params);
            vh.image.setLayoutParams(param);
            vh.check.setLayoutParams(param);
            vh.used.setLayoutParams(param);
            if ( model.getUnZipFileName().equals(mUsedName) ) {
                vh.used.setVisibility(View.VISIBLE);
//                vh.check.setVisibility(View.GONE);
            } else {
                vh.used.setVisibility(View.GONE);
            }

            ImageLoader.with(mContext).from(model.getImage()).load(vh.image);
//            Glide.with(mContext).load(model.getImage()).into(vh.image);

            vh.name.setText(model.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    private class ViewHolder {
        RelativeLayout root;
        RelativeLayout image_layer;
        ImageView image;
        TextView name;
        TextView check;
        TextView used;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}