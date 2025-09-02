package com.enliple.keyboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.enliple.httpmodule.internal.http2.Header;
import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.models.ChatGPTModel;
import com.enliple.keyboard.ui.TypingTextView;
import com.enliple.keyboard.ui.common.LogPrint;

import java.util.ArrayList;
import java.util.logging.Handler;

public class KeyboardChatGPTAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TYPE_QUESTION = "Q";
    public static final String TYPE_ANSWER = "A";
    private Context context;
    private int maxContentWidth;
    private boolean isTyping = false;
    private ArrayList<ChatGPTModel> items = new ArrayList<ChatGPTModel>();
    private Listener listener;
    public interface Listener {
        void onListUpdated();
    }

    public KeyboardChatGPTAdapter(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
        maxContentWidth = Common.getDisplayWidth(context) - Common.convertDpToPx(context, 120);

        setHasStableIds(true);
    }

    public void addItem(ChatGPTModel model, boolean isTyping) {
        if ( items != null ) {
            this.isTyping = isTyping;
            items.add(model);
            notifyItemInserted(items.size());
//            notifyDataSetChanged();
        }
    }

    public void setItem(ChatGPTModel model) {
        if ( items != null ) {
            items.set(items.size() - 1, model);
            notifyItemChanged(items.size() - 1);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aikbd_chat_gpt_item, parent, false);
        return new MainViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bindMainViewHolder((MainViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if ( items != null && items.size() > 0 )
            return items.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    private void bindMainViewHolder(MainViewHolder holder, int position) {
        ChatGPTModel value = items.get(position);
        LogPrint.d("position :: " + position);
        if ( value != null ) {
            holder.question.setMaxWidth(maxContentWidth);
            holder.answer.setMaxWidth(maxContentWidth);
            if ( TYPE_QUESTION.equals(value.getType()) ) {
                holder.answer_layer.setVisibility(View.GONE);
                holder.question_layer.setVisibility(View.VISIBLE);
                holder.question.setText(value.getContent());

                holder.question.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return true;
                    }
                });
            } else {
                holder.question_layer.setVisibility(View.GONE);
                holder.answer_layer.setVisibility(View.VISIBLE);
                LogPrint.d("position :: " + position + " , " + items.size() );
                if ( value.isLoading() ) {
                    holder.answer.setVisibility(View.GONE);
                    holder.loading_layer.setVisibility(View.VISIBLE);
                    ImageModule.with(context).load(R.raw.aikbd_chat_gpt_loading).into(holder.img_loading);
                } else {
                    holder.answer.setVisibility(View.VISIBLE);
                    holder.loading_layer.setVisibility(View.GONE);
                    if ("Y".equals(value.getAnswerType()) ) {
                        holder.answer.setTextColor(Color.parseColor("#000000"));
                    } else {
                        holder.answer.setTextColor(Color.parseColor("#fe0955"));
                    }
                    holder.answer.setText(value.getContent());
//                    if ( position != items.size() - 1 )
//                        holder.answer.setText(value.getContent());
//                    else {
//                        if ( isTyping) {
//                            holder.answer.typeText(value.getContent(), 40);
//                            holder.answer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                                @Override
//                                public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                                    LogPrint.d("skkim textview onLayoutChange oldBottom :: " + oldBottom + " , bottom :: " + bottom);
//                                    if ( oldBottom < bottom ) {
//                                        if ( listener != null ) {
//                                            listener.onListUpdated();
//                                        }
//                                    }
//                                }
//                            });
//                            isTyping = false;
//                        } else {
//                            holder.answer.setText(value.getContent());
//                        }
//                    }
                }
            }
        }
    }
    public class MainViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout question_layer, answer_layer;
        TextView question;
        TypingTextView answer;
        RelativeLayout loading_layer;
        ImageView img_loading;
        public MainViewHolder(Context context, View itemView) {
            super(itemView);
            question_layer = itemView.findViewById(R.id.question_layer);
            answer_layer = itemView.findViewById(R.id.answer_layer);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            loading_layer = itemView.findViewById(R.id.loading_layer);
            img_loading = itemView.findViewById(R.id.img_loading);
        }
    }
}
