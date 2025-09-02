package com.enliple.keyboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.models.ClipboardModel;

import java.util.ArrayList;

public class AikbdClipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public ArrayList<ClipboardModel> items;
    private Context context;
    private Listener listener;
    private int width, height;

    public interface Listener {
        void onItemClicked(ClipboardModel model);
    }

    public AikbdClipAdapter(Context context, int screenWidth, Listener listener) {
        this.context = context;
        width = (screenWidth - Common.convertDpToPx(context, 30)) / 3;
        height = (85 * width) / 110;
        this.listener = listener;
    }

    public void setItems(ArrayList<ClipboardModel> its) {
        items = new ArrayList<>();
        items.addAll(its);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aikbd_clipboard_item, parent, false);
        return new MainViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bindMainViewHolder((MainViewHolder) holder, position);
    }

    private void bindMainViewHolder(MainViewHolder holder, int position) {
        ClipboardModel value = items.get(position);
        if ( value != null ) {
            ViewGroup.LayoutParams param = holder.clip_str.getLayoutParams();
            param.width = width;
            param.height = height;
            holder.clip_str.setLayoutParams(param);
            holder.clip_str.setText(value.getClipboard());

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
        TextView clip_str;
        public MainViewHolder(Context context, View itemView) {
            super(itemView);
            clip_str = itemView.findViewById(R.id.clip_str);
        }
    }

    @Override
    public int getItemCount() {
        if ( items != null && items.size() > 0 )
            return items.size();
        else
            return 0;
    }

    public ArrayList<ClipboardModel> getSelectedItems() {
        if ( items != null && items.size() > 0 ) {
            ArrayList<ClipboardModel> arr = new ArrayList<>();
            for ( int i = 0 ; i < items.size() ; i ++ ) {
                if ( items.get(i).isDeleteShow() ) {
                    arr.add(items.get(i));
                }
            }
            return arr;
        }
        return null;
    }
}
