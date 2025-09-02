package com.enliple.keyboard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.models.ClipboardModel;
import com.enliple.keyboard.models.ShoppingData;

import java.util.ArrayList;

public class OCBSaveShoppingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public ArrayList<ShoppingData> items;
    private Context context;
    private Listener listener;
    private int imageWidth, imageHeight;

    public interface Listener {
        void onItemClicked(ShoppingData model);
    }

    public OCBSaveShoppingAdapter(Context context, int screenWidth, Listener listener) {
        this.context = context;
        imageWidth = (screenWidth - Common.convertDpToPx(context, 140)) / 3;
        imageHeight = (36 * imageWidth) / 72;
        this.listener = listener;
    }

    public void setItems(ArrayList<ShoppingData> its) {
        items = new ArrayList<>();
        items.addAll(its);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aikbd_save_shopping_item, parent, false);
        return new MainViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bindMainViewHolder((MainViewHolder) holder, position);
    }

    private void bindMainViewHolder(MainViewHolder holder, int position) {
        ShoppingData value = items.get(position);
        if ( value != null ) {
            ViewGroup.LayoutParams rParam = holder.image.getLayoutParams();
            rParam.width = imageWidth;
            rParam.height = imageHeight;
            holder.image.setLayoutParams(rParam);
            try {
                ImageLoader.with(context).from(value.getImgUrl()).load(holder.image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.rate.setText(value.getTotalSaveRate() + "%");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ( listener != null ) {
                        listener.onItemClicked(value);
                    }
                }
            });
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        RelativeLayout image_layer;
        TextView rate;
        public MainViewHolder(Context context, View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            image_layer = itemView.findViewById(R.id.image_layer);
            rate = itemView.findViewById(R.id.rate);
        }
    }

    @Override
    public int getItemCount() {
        if ( items != null && items.size() > 0 )
            return items.size();
        else
            return 0;
    }
}
