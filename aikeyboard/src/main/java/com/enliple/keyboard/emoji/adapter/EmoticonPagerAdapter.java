package com.enliple.keyboard.emoji.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.enliple.keyboard.common.ThemeModel;
import com.google.android.customflexbox.FlexboxLayoutManager;

import java.util.ArrayList;

import static com.enliple.keyboard.emoji.constants.EmoticonTexts.transEmoticonTexts1;
import static com.enliple.keyboard.emoji.constants.EmoticonTexts.transEmoticonTexts2;
import static com.enliple.keyboard.emoji.constants.EmoticonTexts.transEmoticonTexts3;
import static com.enliple.keyboard.emoji.constants.EmoticonTexts.transEmoticonTexts4;
import static com.enliple.keyboard.emoji.constants.EmoticonTexts.transEmoticonTexts5;
import static com.enliple.keyboard.emoji.constants.EmoticonTexts.transEmoticonTexts6;

public class EmoticonPagerAdapter extends PagerAdapter {

    private ViewPager pager;
    private ArrayList<View> pages;
    private ThemeModel themeModel;
    public RecentEmoticonAdapter mRecentAdapter;
    public EmoticonPagerAdapter(Context context, ViewPager pager) {
        super();

        // OkCashBack_SDK
        this.pager = pager;
        this.themeModel = themeModel;
        this.pages = new ArrayList<View>();

        mRecentAdapter = new RecentEmoticonAdapter(context);

        RecyclerView recycler1 = new RecyclerView(context);
        recycler1.setLayoutManager(new FlexboxLayoutManager(context));
        recycler1.setAdapter(mRecentAdapter);
        pages.add(recycler1);

        RecyclerView recycler2 = new RecyclerView(context);
        recycler2.setLayoutManager(new FlexboxLayoutManager(context));
        recycler2.setAdapter(new EmoticonAdapter(context, transEmoticonTexts1, mRecentAdapter));
        pages.add(recycler2);

        RecyclerView recycler3 = new RecyclerView(context);
        recycler3.setLayoutManager(new FlexboxLayoutManager(context));
        recycler3.setAdapter(new EmoticonAdapter(context, transEmoticonTexts2, mRecentAdapter));
        pages.add(recycler3);

        RecyclerView recycler4 = new RecyclerView(context);
        recycler4.setLayoutManager(new FlexboxLayoutManager(context));
        recycler4.setAdapter(new EmoticonAdapter(context, transEmoticonTexts3, mRecentAdapter));
        pages.add(recycler4);

        RecyclerView recycler5 = new RecyclerView(context);
        recycler5.setLayoutManager(new FlexboxLayoutManager(context));
        recycler5.setAdapter(new EmoticonAdapter(context, transEmoticonTexts4, mRecentAdapter));
        pages.add(recycler5);

        RecyclerView recycler6 = new RecyclerView(context);
        recycler6.setLayoutManager(new FlexboxLayoutManager(context));
        recycler6.setAdapter(new EmoticonAdapter(context, transEmoticonTexts5, mRecentAdapter));
        pages.add(recycler6);

        RecyclerView recycler7 = new RecyclerView(context);
        recycler7.setLayoutManager(new FlexboxLayoutManager(context));
        recycler7.setAdapter(new EmoticonAdapter(context, transEmoticonTexts6, mRecentAdapter));
        pages.add(recycler7);

    }

    public RecentEmoticonAdapter getRecentAdapter() {
        if (mRecentAdapter != null )
            return mRecentAdapter;
        else
            return null;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
//        pager.addView(pages.get(position), position, keyboardHeight);
        pager.addView(pages.get(position));
        return pages.get(position);
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        pager.removeView(pages.get(position));
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
