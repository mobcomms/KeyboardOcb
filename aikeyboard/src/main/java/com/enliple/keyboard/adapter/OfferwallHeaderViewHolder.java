package com.enliple.keyboard.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;

public class OfferwallHeaderViewHolder extends RecyclerView.ViewHolder {
    public static int VIEW_TYPE = R.layout.aikbd_offerwall_header;
    public TextView txt_possible_point;
    public ConstraintLayout btn_all;
    public ConstraintLayout btn_join;
    public TextView text_all;
    public TextView text_join;
    public View line_all;
    public View line_join;

    public OfferwallHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_possible_point = itemView.findViewById(R.id.txt_possible_point);
        btn_all = itemView.findViewById(R.id.btn_all);
        btn_join = itemView.findViewById(R.id.btn_join);
        text_all = itemView.findViewById(R.id.text_all);
        text_join = itemView.findViewById(R.id.text_join);
        line_all = itemView.findViewById(R.id.line_all);
        line_join = itemView.findViewById(R.id.line_join);
    }
}
