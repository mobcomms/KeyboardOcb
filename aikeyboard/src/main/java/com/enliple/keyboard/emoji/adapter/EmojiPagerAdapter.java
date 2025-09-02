package com.enliple.keyboard.emoji.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.enliple.keyboard.emoji.constants.EmojiTexts;
import com.enliple.keyboard.emoji.view.KeyboardSinglePageView;
import com.enliple.keyboard.ui.common.LogPrint;

import java.util.ArrayList;

public class EmojiPagerAdapter extends PagerAdapter {

    private final String[] TITLES = {"recent","Smiley", "Animals", "Foods", "Activities", "Travels", "Objects", "Symbols", "Flags" };

    private ViewPager pager;
    private ArrayList<View> pages;
    public RecentAdapter mRecentAdapter;
    public EmojiPagerAdapter(Context context, ViewPager pager) {
        super();
        LogPrint.d("emoji EmojiPagerAdapter create");
        // OkCashBack_SDK
        this.pager = pager;
        this.pages = new ArrayList<View>();

        mRecentAdapter = new RecentAdapter(context);
    }

    public RecentAdapter getRecentAdapter() {
        if (mRecentAdapter != null ) {
            LogPrint.d("emoji getRecentAdapter not null");
            return mRecentAdapter;
        } else {
            LogPrint.d("emoji getRecentAdapter null");
            return null;
        }
    }

    public void setItems(Context context) {
        LogPrint.d("emoji setItems");
        pages.clear();
        pages = new ArrayList<View>();
        mRecentAdapter = new RecentAdapter(context);

        pages.add(new KeyboardSinglePageView(context, mRecentAdapter).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.smileysEmojis,mRecentAdapter)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.animalsEmojis,mRecentAdapter)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.foodsEmojis,mRecentAdapter)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.activitiesEmojis,mRecentAdapter)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.travelsEmojis,mRecentAdapter)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.objectsEmojis,mRecentAdapter)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.symbolsEmojis,mRecentAdapter)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.flagsEmojis,mRecentAdapter)).getView());

        notifyDataSetChanged();
    }

    public int getItemsSize(){
        if(pages != null){
            LogPrint.d("emoji getItemsSize pages not null size :: " + pages.size());
            return pages.size();
        }else{
            LogPrint.d("emoji getItemsSize pages null");
            return 0;
        }
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        if (position < 0 || position >= pages.size()) {
            LogPrint.e("emoji instantiateItem error: position=" + position + ", pages size=" + pages.size());
            return new View(container.getContext());
        }

        pager.addView(pages.get(position));
        return pages.get(position);
    }

//    @Override
//    public View instantiateItem(ViewGroup container, int position) {
//        LogPrint.d("emoji instantiateItem position :: " + position + "page size :: " + pages.size());
////        pager.addView(pages.get(position), position, keyboardHeight);
//        pager.addView(pages.get(position));
//        return pages.get(position);
//    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        LogPrint.d("emoji destroyItem position :: " + position);
        pager.removeView(pages.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

//    @Override
//    public int getCount() {
//        return TITLES.length;
//    }

    @Override
    public int getCount() {
        return pages == null ? 0 : pages.size();  // TITLES.length 쓰지 말고 실제 페이지 수
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
