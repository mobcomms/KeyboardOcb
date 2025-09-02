package com.enliple.keyboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.ShoppingCommonModel;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;
import com.enliple.keyboard.models.CategoryData;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OCBThemeCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<CategoryData> items = new ArrayList<>();
    private int selectedPosition = 0;
    private OnClickListener listener;
    public interface OnClickListener {
        void onCategoryClicked(CategoryData data);
    }

    public OCBThemeCategoryAdapter(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aikbd_theme_category_item, parent, false);
        return new CategoryViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        bindCategoryHolder((CategoryViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    private void bindCategoryHolder(CategoryViewHolder holder, int position) {
        CategoryData item = items.get(position);
        if ( item.isSelected() ) {
            holder.name.setTextColor(Color.parseColor("#fe0956"));
            holder.name.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.name.setTextColor(Color.parseColor("#000000"));
            holder.name.setTypeface(Typeface.DEFAULT);
        }
        holder.name.setText(item.getCategoryName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogPrint.d("before selectedPosition :: " + selectedPosition);
                CategoryData pData = items.get(selectedPosition);
                pData.setSelected(false);
                items.set(selectedPosition, pData);

                selectedPosition = position;
                LogPrint.d("after selectedPosition :: " + selectedPosition);
                CategoryData cData = items.get(selectedPosition);
                cData.setSelected(true);
                items.set(selectedPosition, cData);
                notifyDataSetChanged();

                if ( listener != null )
                    listener.onCategoryClicked(item);
            }
        });
    }

    public void setItems(ArrayList<CategoryData> its) {
        if ( items == null ) {
            items = new ArrayList<>();
        }

        items.addAll(its);
        notifyDataSetChanged();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CategoryViewHolder(Context context, View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);

        }
    }
 }
