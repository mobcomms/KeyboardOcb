package com.enliple.keyboard.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.NewThemeListInfo;
import com.enliple.keyboard.common.Util;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import java.io.File;
import java.util.ArrayList;

public class AikbdThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<NewThemeListInfo> items;
    private Context context;
    public MainViewHolder holder;
    private int imageWidth, imageHeight, bWidth, bHeight;
    private String mUsedName = null;
    private ThemeClickListener listener;

    public interface ThemeClickListener {
        void onThemeClicked(NewThemeListInfo info, int position);
    }

    public void setThemeClickListener(ThemeClickListener lsn) {
        listener = lsn;
    }

    public AikbdThemeAdapter(Context context, int width) {
        this.context = context;
        int unitWidth = width - Common.convertDpToPx(context, 80);
        imageWidth = (int)(unitWidth / 2);
        imageHeight = (int)((imageWidth * 102) / 140);

        bWidth = imageWidth - Common.convertDpToPx(context, 10);
        bHeight = imageHeight - Common.convertDpToPx(context, 12);
    }

    public void setItems(ArrayList<NewThemeListInfo> its) {
        if ( items == null )
            items = new ArrayList<>();
        this.items.clear();
        this.items.addAll(its);
        notifyDataSetChanged();
    }

    public void setUseFileName(String name) {
        mUsedName = name;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aikbd_theme_item, parent, false);
        holder = new MainViewHolder(context, view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bindMainViewHolder((MainViewHolder) holder, position);
    }

    private void bindMainViewHolder(MainViewHolder holder, int position) {
        NewThemeListInfo value = items.get(position);
        ConstraintLayout.LayoutParams iParams = (ConstraintLayout.LayoutParams) holder.image.getLayoutParams();
        iParams.width = imageWidth;
        iParams.height = imageHeight;
        holder.image.setLayoutParams(iParams);

        ConstraintLayout.LayoutParams bParams = (ConstraintLayout.LayoutParams) holder.background.getLayoutParams();
        bParams.width = imageWidth;
        bParams.height = imageHeight;
        holder.background.setLayoutParams(bParams);

        holder.name.setText(value.getName());
        if ( value != null )
            LogPrint.d("theme image path :: " + value.getImage());
        if ( !TextUtils.isEmpty(value.getName()) && !"null".equals(value.getImage()) ) {
            LogPrint.d("image path exist");
            holder.image.setVisibility(View.VISIBLE);
            ImageLoader.with(context).from(value.getImage()).noStorageCache().load(holder.image);
            if ( value.getUnZipFileName().equals(mUsedName) ) {
                holder.background.setVisibility(View.VISIBLE);
            } else {
                holder.background.setVisibility(View.GONE);
            }
        } else {
            LogPrint.d("image path not exist");
            holder.image.setVisibility(View.INVISIBLE);
            holder.background.setVisibility(View.GONE);
        }

        if ( "Y".equals(value.getIsNew()) )
            holder.newBadge.setVisibility(View.VISIBLE);
        else
            holder.newBadge.setVisibility(View.GONE);

        if ( "Y".equals(value.getIsPopular()) )
            holder.popularBadge.setVisibility(View.VISIBLE);
        else
            holder.popularBadge.setVisibility(View.GONE);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( listener != null )
                    listener.onThemeClicked(value, position);
            }
        });
    }

    public NewThemeListInfo getItem(int position) {
        if ( items != null ) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if ( items != null && items.size() > 0 )
            return items.size();
        else
            return 0;
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout background;
        private ImageView image;
        private TextView name;
        private View root;
        private TextView newBadge, popularBadge;
//        private RelativeLayout card_container, used_img;
//        private ImageView image;
//        private TextView name;
//        private TextView download;
//        private CardView cardView;
        public MainViewHolder(Context context, View itemView) {
            super(itemView);
            root = itemView;
            background = itemView.findViewById(R.id.background);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.theme_name);
            newBadge = itemView.findViewById(R.id.newBadge);
            popularBadge = itemView.findViewById(R.id.popularBadge);
        }
    }
}
