package com.enliple.keyboard.emoji.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class KeyboardSinglePageView {

    private Context context;
    private BaseAdapter adapter;

    public KeyboardSinglePageView(Context context, BaseAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    public View getView() {

        final GridView emojiGrid = new GridView(context);

        emojiGrid.setColumnWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
        emojiGrid.setNumColumns(GridView.AUTO_FIT);
        emojiGrid.setVerticalScrollBarEnabled(false);
        emojiGrid.setHorizontalScrollBarEnabled(false);
        emojiGrid.setAdapter(adapter);
        return emojiGrid;
    }

    public void notifyDataSetChanged()
    {
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
}