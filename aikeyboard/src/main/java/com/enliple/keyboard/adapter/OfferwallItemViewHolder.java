package com.enliple.keyboard.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;

public class OfferwallItemViewHolder extends RecyclerView.ViewHolder {
    public static int VIEW_TYPE = R.layout.aikbd_offerwall_item;

    public ImageView offerwall_image;
    public TextView offerwall_point;
    public TextView offerwall_title;
    public TextView offerwall_desc;
    public TextView offerwall_join_count;
    public View bot_layer;

    public OfferwallItemViewHolder(@NonNull View itemView) {
        super(itemView);
        offerwall_image = itemView.findViewById(R.id.offerwall_image);
        offerwall_point = itemView.findViewById(R.id.offerwall_point);
        offerwall_title = itemView.findViewById(R.id.offerwall_title);
        offerwall_desc = itemView.findViewById(R.id.offerwall_desc);
        offerwall_join_count = itemView.findViewById(R.id.offerwall_join_count);
        bot_layer = itemView.findViewById(R.id.bot_layer);
    }
}
