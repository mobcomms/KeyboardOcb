package com.enliple.keyboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;
import com.enliple.keyboard.models.ClipboardModel;
import com.enliple.keyboard.models.SurpriseModel;

import java.util.ArrayList;

public class AikbdSurpriseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public ArrayList<SurpriseModel> items;
    private Context context;
    private Listener listener;

    public interface Listener {
        void onItemClicked();
    }
    public AikbdSurpriseAdapter(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(ArrayList<SurpriseModel> its) {
        items = new ArrayList<>();
        items.addAll(its);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aikbd_surprise_item, parent, false);
        return new MainViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bindMainViewHolder((MainViewHolder) holder, position);
    }

    private void bindMainViewHolder(MainViewHolder holder, int position) {
        SurpriseModel value = items.get(position);
        if ( value != null ) {
            holder.title.setText(value.getTitle());
            try {
                ImageLoader.with(context).from(value.getIcon()).load(holder.image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ( "0".equals(value.getPoint())) {
                holder.badge.setVisibility(View.INVISIBLE);
            } else {
                holder.badge.setVisibility(View.VISIBLE);
                holder.badge.setText(value.getPoint() + "P");
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ( listener != null )
                        listener.onItemClicked();
                }
            });
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, badge;
        public MainViewHolder(Context context, View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            badge = itemView.findViewById(R.id.badge);
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
