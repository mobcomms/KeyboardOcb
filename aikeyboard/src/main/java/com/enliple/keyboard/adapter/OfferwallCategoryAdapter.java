package com.enliple.keyboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.models.OfferwallCategoryData;

import java.util.ArrayList;
import java.util.List;

public class OfferwallCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<OfferwallCategoryData> items;
    public Listener listener;

    public interface Listener {
        void onCategoryClicked(OfferwallCategoryData data);
    }

    public OfferwallCategoryAdapter(Context context, ArrayList<OfferwallCategoryData> items, Listener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }


    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.offerwall_category_item, parent, false);
        return new OfferwallCategoryViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        bindCalViewHolder((OfferwallCategoryViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }


    private void bindCalViewHolder(OfferwallCategoryViewHolder holder, int position) {
        OfferwallCategoryData item = items.get(position);

        holder.c_text.setText(item.getClass_name());

        if ( item.isSelected() ) {
            holder.c_text.setTypeface(holder.c_text.getTypeface(), Typeface.BOLD);
            holder.c_text.setTextColor(Color.parseColor("#000000"));
            holder.c_line.setVisibility(View.VISIBLE);
        } else {
            holder.c_text.setTypeface(holder.c_text.getTypeface(), Typeface.NORMAL);
            holder.c_text.setTextColor(Color.parseColor("#666666"));
            holder.c_line.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( listener != null && items != null && items.size() > 0 ) {
                    for ( int i = 0 ; i < items.size() ; i ++ ) {
                        OfferwallCategoryData data = items.get(i);
                        if ( i == position ) {
                            data.setSelected(true);
                        } else {
                            data.setSelected(false);
                        }
                        items.set(i, data);
                    }
                    notifyDataSetChanged();

                    listener.onCategoryClicked(item);
                }
            }
        });
    }

    public class OfferwallCategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView c_text;
        public View c_line;
        public OfferwallCategoryViewHolder(Context context, View itemView) {
            super(itemView);
            c_text = itemView.findViewById(R.id.c_text);
            c_line = itemView.findViewById(R.id.c_line);
        }
    }
}
