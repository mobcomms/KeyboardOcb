package com.enliple.keyboard.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.models.OfferwallCategoryData;

import java.util.ArrayList;

public class OfferwallCategoryHeaderViewHolder extends RecyclerView.ViewHolder {
    public static int VIEW_TYPE = R.layout.aikbd_category_offerwall_header;
    public TextView txt_possible_point;
    public RecyclerView c_recyclerview;
    public OfferwallCategoryAdapter adapter;
    public ArrayList<OfferwallCategoryData> categoryArray;
    public Listener listener;
    public interface Listener {
        void onCategoryClicked(OfferwallCategoryData data);
    }
    public OfferwallCategoryHeaderViewHolder(@NonNull View itemView, ArrayList<OfferwallCategoryData> categoryArray, Listener listener) {
        super(itemView);
        this.listener = listener;
        this.categoryArray = categoryArray;
        c_recyclerview = itemView.findViewById(R.id.c_recyclerview);
        txt_possible_point = itemView.findViewById(R.id.txt_possible_point);
        adapter = new OfferwallCategoryAdapter(itemView.getContext(), categoryArray, new OfferwallCategoryAdapter.Listener() {
            @Override
            public void onCategoryClicked(OfferwallCategoryData data) {
                if  (listener != null )
                    listener.onCategoryClicked(data);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(itemView.getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        c_recyclerview.setLayoutManager(manager);

        c_recyclerview.setAdapter(adapter);

    }
}
