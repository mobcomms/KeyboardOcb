package com.enliple.keyboard.common;

/**
 * Created by Administrator on 2017-11-15.
 */

public class ThemeModel {
    private boolean mDownTheme;
    private String mTabBookmark;
    private String mTabCash;
    private String mBgImg;
    private String mBgOriginImg;
    private String mEnterImg;
    private String mEmojiImg;
    private String mEmoticonImg;
    private String mNorBtnPreI;
    private String mNorBtnNorI;
    private String mTabLogo;
    private String mTabMemo;
    private String mTabMemoPlus;
    private String mTabCashbackLogo;
    private String mTabShopping;
    private String mTabOlabang;
    private String mTabMy;
    private String mTabMore;
    private String mTabOCBSearch;
    private String mTabMoreOn;
    private String mTabMyOn;
    private String mTabOlabangOn;
    private String mTabOCBSearchOn;
    private String mTabShoppingOn;
    private String mKeySearchEnter;
    private String mShiftImg;
    private String mShiftImg1;
    private String mShiftImg2;
    private String mKeySymbol;
    private String mSpaceImg;
    private String mSpBtnPreI;
    private String mSpBtnNorI;
    private String mKeyLang;
    private String mKeyMemoPlus; // 메모 안쪽 + 이미지
    private String mFavBotOff;
    private String mFavBotOn;
    private String mFavMemoOff;
    private String mFavMemoOn;
    private String mDelBot;
    private String mKeyboardBot;
    private String mGoMemo;
    private String mTopLine;
    private String mBotLine;
    private String mKeyText;
    private String mKeyTextS = "";
    private String mTabOff;
    private String mTabOn;
    private String mFavText;
    private int mBgAlpha;
    private String mTabZeroCash;
    private String mTabEmoji;
    private String mTabEmojiOn;
    private int mNorAlpha;
    private int mSpAlpha;
    private int mNorBtnColor;
    private int mSpBtnColor;
    private String mBgColor;
    private String mBotTabColor;
    private String mTopIconColor;
//    private String mTopBg;
//    private String mTopBgSelect;
    private String mSpBtnPreC;
    private String mSpBtnNorC;
    private String mNorBtnPreC;
    private String mNorBtnNorC;
    private String mSpImgColor;
    private String mBackImg;
//    private String mLangImg;
    private String mFuncImageColor;
    private String mBrandOff;
    private String mBrandOn;
    private String mBrandOn2;
    private String mKeyPreview;

    private String mTabSaveShopping;
    private String mTabSaveShoppingOn;

    public void setTabSaveShopping(String tabSaveShopping) {
        this.mTabSaveShopping = tabSaveShopping;
    }

    public String getTabSaveShopping() {
        return this.mTabSaveShopping;
    }

    public void setTabSaveShoppingOn(String tabSaveShoppingOn) {
        this.mTabSaveShoppingOn = tabSaveShoppingOn;
    }

    public String getTabSaveShoppingOn() {
        return this.mTabSaveShoppingOn;
    }

    public String getBrandOn2() { return mBrandOn2; }

    public void setBrandOn2(String brandOn) {
        this.mBrandOn2 = brandOn;
    }

    public String getBrandOn() { return mBrandOn; }

    public void setBrandOn(String brandOn) {
        this.mBrandOn = brandOn;
    }

    public String getBrandOff() { return mBrandOff; }

    public void setBrandOff(String brandOff) {
        this.mBrandOff = brandOff;
    }

    public String getAdDel() {
        return mAdDel;
    }

    public void setAdDel(String mAdDel) {
        this.mAdDel = mAdDel;
    }

    private String mAdDel;



    public String getTabEmojiOn() { return mTabEmojiOn; }

    public void setTabEmojiOn(String tabEmojiOn) {
        this.mTabEmojiOn = tabEmojiOn;
    }

    public String getTabMore() { return mTabMore; }

    public void setTabMore(String tabMore) {
        this.mTabMore = tabMore;
    }

    public String getTabOCBSearch() { return mTabOCBSearch; }

    public void setTabOCBSearch(String tabOCBSearch) {
        this.mTabOCBSearch = tabOCBSearch;
    }

    public String getTabMoreOn() { return mTabMoreOn; }

    public void setTabMoreOn(String tabMoreOn) {
        this.mTabMoreOn = tabMoreOn;
    }

    public String getTabMyOn() { return mTabMyOn; }

    public void setTabMyOn(String tabMyOn) {
        this.mTabMyOn = tabMyOn;
    }

    public String getTabOlabangOn() { return mTabOlabangOn; }

    public void setTabOlabangOn(String tabOlabangOn) {
        this.mTabOlabangOn = tabOlabangOn;
    }

    public String getTabOCBSearchOn() { return mTabOCBSearchOn; }

    public void setTabOCBSearchOn(String tabOCBSearchOn) {
        this.mTabOCBSearchOn = tabOCBSearchOn;
    }

    public String getTabShoppingOn() { return mTabShoppingOn; }

    public void setTabShoppingOn(String tabShoppingOn) {
        this.mTabShoppingOn = tabShoppingOn;
    }

    public String getTabCashbackLogo() {
        return mTabCashbackLogo;
    }

    public void setTabCashbackLogo(String mTabCashbackLogo) {
        this.mTabCashbackLogo = mTabCashbackLogo;
    }

    public String getTabShopping() {
        return mTabShopping;
    }

    public void setTabShopping(String mTabShopping) {
        this.mTabShopping = mTabShopping;
    }

    public String getTabOlabang() {
        return mTabOlabang;
    }

    public void setTabOlabang(String mTabOlabang) {
        this.mTabOlabang = mTabOlabang;
    }

    public String getTabMy() {
        return mTabMy;
    }

    public void setTabMy(String mTabMy) {
        this.mTabMy = mTabMy;
    }

    private String mEmoticonRecent;
    private String mEmoticonFirst;
    private String mEmoticonSecond;
    private String mEmoticonThird;
    private String mEmoticonFourth;
    private String mEmoticonFifth;
    private String mEmoticonSixth;


    public void setEmoticonRecent(String val) { mEmoticonRecent = val; }
    public String getEmoticonRecent() { return mEmoticonRecent; }

    public void setEmoticonFirst(String val) { mEmoticonFirst = val; }
    public String getEmoticonFirst() { return mEmoticonFirst; }

    public void setEmoticonSecond(String val) { mEmoticonSecond = val; }
    public String getEmoticonSecond() { return mEmoticonSecond; }

    public void setEmoticonThird(String val) { mEmoticonThird = val; }
    public String getEmoticonThird() { return mEmoticonThird; }

    public void setEmoticonFourth(String val) { mEmoticonFourth = val; }
    public String getEmoticonFourth() { return mEmoticonFourth; }

    public void setEmoticonFifth(String val) { mEmoticonFifth = val; }
    public String getEmoticonFifth() { return mEmoticonFifth; }

    public void setEmoticonSixth(String val) { mEmoticonSixth = val; }
    public String getEmoticonSixth() { return mEmoticonSixth; }

    public void setBgOriginImg(String val) { mBgOriginImg = val; }
    public String getBgOriginImg() { return mBgOriginImg; }

    public void setBotTabColor(String val) { mBotTabColor = val; }

    public void setNorBtnColor(int val) { mNorBtnColor = val; }

    public void setSpBtnColor(int val) { mSpBtnColor = val; }

    public void setNorAlpha(int val) {
        mNorAlpha = val;
    }

    public void setSpAlpha(int val) {
        mSpAlpha = val;
    }

    public void setEmojiImg(String val) {
        mEmojiImg = val;
    }

    public void setEmoticonImg(String val) {
        mEmoticonImg = val;
    }

    public void setTabZeroCash(String val) {
        mTabZeroCash = val;
    };

    public void setFavText(String val) {
        mFavText = val;
    }

    public void setKeyText(String val) {
        mKeyText = val;
    }

    public void setKeyTextS(String val) {
        mKeyTextS = val;
    }

    public void setDelBot(String val) {
        mDelBot = val;
    }

    public void setKeyboardBot(String val) {
        mKeyboardBot = val;
    }

    public void setGoMemo(String val) {
        mGoMemo = val;
    }

    public void setFavBotOff(String val) {
        mFavBotOff = val;
    }

    public void setFavBotOn(String val) {
        mFavBotOn = val;
    }

    public void setFavMemoOff(String val) {
        mFavMemoOff = val;
    }

    public void setFavMemoOn(String val) {
        mFavMemoOn = val;
    }

    public void setKeyMemoPlus(String val) {
        mKeyMemoPlus = val;
    }

    public void setKeyLang(String val) {
        mKeyLang = val;
    }

    public void setDownTheme(boolean val) { mDownTheme = val; } //

    public void setBgImg(String val) {
        mBgImg = val;
    }

    public void setBgColor(String val) {
        mBgColor = val;
    }

    public void setBgAlpha(int val) { mBgAlpha = val; }

    public void setTabLogo(String val) { mTabLogo = val; } //

    public void setTabMemo(String val) {
        mTabMemo = val;
    } //

    public void setTabMemoPlus(String val) {
        mTabMemoPlus = val;
    }

    public void setTabEmoji(String val) {
        mTabEmoji = val;
    }

    public void setTabBookMark(String val) {
        mTabBookmark = val;
    } //

    public void setTabCash(String val) {
        mTabCash = val;
    } //

    public void setTopIconColor(String val) {
        mTopIconColor = val;
    }

    public void setSpImgColor(String val) {
        mSpImgColor = val;
    }

    public String getBotTabColor() { return mBotTabColor; }

    public int getNorBtnColor() { return mNorBtnColor; }

    public int getSpBtnColor() { return mSpBtnColor; }

    public int getNorAlpha() {
        return mNorAlpha;
    }

    public int getSpAlpha() {
        return mSpAlpha;
    }

    public String getEmojiImg() {
        return mEmojiImg;
    }

    public String getEmoticonImg() {
        return mEmoticonImg;
    }

    public String getTabZeroCash() {
        return mTabZeroCash;
    }

    public String getFavText() {
        return mFavText;
    }

    public String getKeyText() {
        return mKeyText;
    }

    public String getKeyTextS() {
        return mKeyTextS;
    }

    public String getDelBot() {
        return mDelBot;
    }

    public String getKeyboardBot() {
        return mKeyboardBot;
    }

    public String getGoMemo() {
        return mGoMemo;
    }

    public String getFavBotOff() {
        return mFavBotOff;
    }

    public String getFavBotOn() {
        return mFavBotOn;
    }

    public String getFavMemoOff() {
        return mFavMemoOff;
    }

    public String getFavMemoOn() {
        return mFavMemoOn;
    }

    public String getKeyMemoPlus() {
        return mKeyMemoPlus;
    }

    public String getKeyLang() {
        return mKeyLang;
    }

    public boolean getDownTheme() { return mDownTheme; }

    public String getBgImg() {
        return mBgImg;
    }

    public String getBgColor() {
        return mBgColor;
    }

    public int getBgAlpha() { return mBgAlpha; }

    public String getTabLogo() {
        return mTabLogo;
    }

    public String getTabEmoji() { return mTabEmoji; }

    public String getTabMemo() {
        return mTabMemo;
    }

    public String getTabMemoPlus() {
        return mTabMemoPlus;
    }

    public String getTabBookMark() {
        return mTabBookmark;
    }

    public String getTabCash() {
        return mTabCash;
    }

    public String getTopIconColor() {
        return mTopIconColor;
    }

    public String getSpImgColor() {
        return mSpImgColor;
    }

    public void setTabOff(String val) {
        mTabOff = val;
    }

    public void setTabOn(String val) {
        mTabOn = val;
    }

    public void setSpBtnPreC(String val) {
        mSpBtnPreC = val;
    }

    public void setSpBtnNorC(String val) {
        mSpBtnNorC = val;
    }

    public void setSpBtnPreI(String val) {
        mSpBtnPreI = val;
    }

    public void setSpBtnNorI(String val) {
        mSpBtnNorI = val;
    }

    public void setNorBtnPreC(String val) {
        mNorBtnPreC = val;
    }

    public void setNorBtnNorC(String val) {
        mNorBtnNorC = val;
    }

    public void setNorBtnPreI(String val) {
        mNorBtnPreI = val;
    }

    public void setNorBtnNorI(String val) {
        mNorBtnNorI = val;
    }

    public String getTabOff() {
        return mTabOff;
    }

    public String getTabOn() {
        return mTabOn;
    }

    public String getSpBtnPreC() {
        return mSpBtnPreC;
    }

    public String getSpBtnNorC() {
        return mSpBtnNorC;
    }

    public String getSpBtnPreI() {
        return mSpBtnPreI;
    }

    public String getSpBtnNorI() {
        return mSpBtnNorI;
    }

    public String getNorBtnPreC() {
        return mNorBtnPreC;
    }

    public String getNorBtnNorC() {
        return mNorBtnNorC;
    }

    public String getNorBtnPreI() {
        return mNorBtnPreI;
    }

    public String getNorBtnNorI() {
        return mNorBtnNorI;
    }


    public void setSpaceImg(String val) {
        mSpaceImg = val;
    }

    public void setKeySearchEnter(String val) {
        mKeySearchEnter = val;
    } //

    public void setEnterImg(String val) {
        mEnterImg = val;
    }

    public void setShiftImg(String val) {
        mShiftImg = val;
    }

    public void setShiftImg1(String val) {
        mShiftImg1 = val;
    }

    public void setShiftImg2(String val) {
        mShiftImg2 = val;
    }

    public void setBackImg(String val) {
        mBackImg = val;
    }

    public void setKeySymbol(String val) {
        mKeySymbol = val;
    }

    public void setFuncImageColor(String val) {
        mFuncImageColor = val;
    }

    public void setTopLine(String val) {
        mTopLine = val;
    }

    public void setBotLine(String val) {
        mBotLine = val;
    }

    public String getSpaceImg() {
        return mSpaceImg;
    }

    public String getKeySearchEnter() {
        return mKeySearchEnter;
    }

    public String getEnterImg() {
        return mEnterImg;
    }

    public String getShiftImg() {
        return mShiftImg;
    }

    public String getShiftImg1() {
        return mShiftImg1;
    }

    public String getShiftImg2() {
        return mShiftImg2;
    }

    public String getBackImg() {
        return mBackImg;
    }

    public String getKeySymbol() {
        return mKeySymbol;
    }

    public String getFuncImageColor() {
        return mFuncImageColor;
    }

    public String getTopLine() {
        return mTopLine;
    }

    public String getBotLine() {
        return mBotLine;
    }

    public String getKeyPreview() {
        return mKeyPreview;
    }

    public void setKeyPreview(String mKeyPreview) {
        this.mKeyPreview = mKeyPreview;
    }
}
