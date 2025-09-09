package com.enliple.keyboard.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017-11-14.
 */

public class ThemeManager {
    public static final int INDEX_BACK = 0;
    public static final int INDEX_SPACE = 1;
    public static final int INDEX_ENTER = 2;
    public static final int INDEX_ENTER_SEARCH = 3;
    public static final int INDEX_EMOJI = 4;
    public static final int INDEX_SYMBOL = 5;
    public static final int INDEX_LANG = 6;
    public static final int INDEX_SHIFT = 7;

    public static final String BRAND_ICON_ON_2 = "aikbd_btn_ad_icon_on_2.png";
    public static final String BRAND_ICON_ON = "aikbd_btn_ad_icon_on.png";
    public static final String BRAND_ICON_OFF = "aikbd_btn_ad_icon.png";
    public static final String KEY_DEL = "aikbd_btn_keyboard_delete.png";
    public static final String KEY_SHIFT = "aikbd_btn_keyboard_shift.png";
    public static final String KEY_SHIFT_1 = "aikbd_btn_keyboard_shift_1.png";
    public static final String KEY_SHIFT_2 = "aikbd_btn_keyboard_shift_2.png";
    public static final String KEY_ENTER = "aikbd_btn_keyboard_enter.png";
    public static final String KEY_EMOJI = "aikbd_btn_keyboard_icon.png";
    public static final String KEY_EMOTICON = "aikbd_btn_keyboard_icon_1.png";
    public static final String KEY_NOR_OFF = "aikbd_btn_keyboard_key_off.9.png";
    public static final String KEY_NOR_ON = "aikbd_btn_keyboard_key_on.9.png";
    public static final String KEY_SPACE = "aikbd_btn_keyboard_space.png";
    public static final String KEY_SPE_OFF = "aikbd_btn_keyboard_specialkey_off.9.png";
    public static final String KEY_SPE_ON = "aikbd_btn_keyboard_specialkey_on.9.png";
//    public static final String BG = "aikbd_img_keyboard_bg.9.png";
    public static final String BG = "aikbd_img_keyboard_bg.png"; // 나인페치 미적용 2017.12.04
    public static final String BG_ORIGIN = "aikbd_img_keyboard_bg_origin.png";
    public static final String THEME_COLOR = "theme_color.txt";
    public static final String TAB_BOOKMARK = "aikbd_btn_keyboard_bookmark.png";
    public static final String TAB_CASH = "aikbd_btn_keyboard_cash.png";
    public static final String TAB_ZERO_CASH = "aikbd_btn_keyboard_cash_off.png";
    public static final String KEY_SEARCH_ENTER = "aikbd_btn_keyboard_search.png";
    public static final String TAB_LOGO = "aikbd_btn_keyboard_setting.png";
    public static final String TAB_MEMO = "aikbd_btn_keyboard_memo.png";
    public static final String TAB_MEMO_PLUS = "aikbd_btn_keyboard_memo_on.png";
    public static final String KEY_SYMBOL = "aikbd_btn_keyboard_sign.png";
    public static final String KEY_LANG = "aikbd_btn_keyboard_text.png";
    public static final String KEY_MEMO_PLUS = "aikbd_btn_memo_addition.png";
    public static final String FAV_BOT_OFF = "aikbd_btn_memo_bookmark_off.png";
    public static final String FAV_BOT_ON = "aikbd_btn_memo_bookmark_on.png";
    public static final String FAV_MEMO_OFF = "aikbd_btn_memo_bookmark_s_off.png";
    public static final String FAV_MEMO_ON = "aikbd_btn_memo_bookmark_s_on.png";
    public static final String DEL_BOT = "aikbd_btn_memo_delete_s.png";
    public static final String KEYBOARD_BOT = "aikbd_btn_memo_keyboard.png";
    public static final String GO_MEMO = "aikbd_btn_memo_modify.png";
    public static final String TAB_EMOJI = "aikbd_btn_keyboard_emoticon_ocb.png";
    public static final String TAB_EMOJI_ON = "aikbd_btn_keyboard_emoticon_on_ocb.png"; // us


    public static final String TAB_OK_CASHBACK_LOGO = "aikbd_btn_cashback_icon.png";
    public static final String TAB_OLABANG = "aikbd_btn_orabang_icon.png";
    public static final String TAB_SHOPPING = "aikbd_btn_shopping_icon.png";
    public static final String TAB_MY = "aikbd_btn_my_icon.png";

    public static final String TAB_MORE = "aikbd_btn_more_icon.png";
    public static final String TAB_OCB_SEARCH = "aikbd_btn_search_icon.png";
    public static final String TAB_MORE_ON = "aikbd_btn_more_icon_on.png";
    public static final String TAB_MY_ON = "aikbd_btn_my_icon_on.png";
    public static final String TAB_OLABANG_ON = "aikbd_btn_orabang_icon_on.png";
    public static final String TAB_OCB_SEARCH_ON = "aikbd_btn_search_icon_on.png";
    public static final String TAB_SHOPPING_ON = "aikbd_btn_shopping_icon_on.png";

    public static final String TAB_OCB_SAVE_SHOPPING = "aikbd_btn_save_shopping_icon.png";
    public static final String TAB_OCB_SAVE_SHOPPING_ON = "aikbd_btn_save_shopping_icon_on.png";

    public static final String BTN_EMOTICON = "aikbd_btn_keyboard_icon_1.png";
    public static final String IMG_RECENT_EMOTICON = "aikbd_btn_emoticon_latest.png";
    public static final String IMG_FIRST_EMOTICON = "aikbd_btn_emoticon_1.png";
    public static final String IMG_SECOND_EMOTICON = "aikbd_btn_emoticon_2.png";
    public static final String IMG_THIRD_EMOTICON = "aikbd_btn_emoticon_3.png";
    public static final String IMG_FOURTH_EMOTICON = "aikbd_btn_emoticon_4.png";
    public static final String IMG_FIFTH_EMOTICON = "aikbd_btn_emoticon_5.png";
    public static final String IMG_SIXTH_EMOTICON = "aikbd_btn_emoticon_6.png";

    public static final String IMG_AD_DEL = "kb_bar_close_icon.png";

    public static final String IMG_KEY_PREVIEW = "aikbd_btn_keyboard_preview.png";

    public static final String KEY_TAB_BOOKMARK = "tab_bookmark";
    public static final String KEY_TAB_CASH = "tab_cash";
    public static final String KEY_TAB_ZERO_CASH = "tab_zero_cash";
    public static final String KEY_BGIMG = "bgimg";
    public static final String KEY_BG_ORIGINIMG = "bgoriginimg";
    public static final String KEY_ENTER_IMG = "enter_img";
    public static final String KEY_EMOJI_IMG = "emoji_img";
    public static final String KEY_EMOJI_IMG_ON = "emoji_img_on";
    public static final String KEY_EMOTICON_IMG = "emoticon_img";
    public static final String KEY_EMOTICON_RECENT = "emoticon_recent";
    public static final String KEY_EMOTICON_FIRST = "emoticon_first";
    public static final String KEY_EMOTICON_SECOND = "emoticon_second";
    public static final String KEY_EMOTICON_THIRD = "emoticon_third";
    public static final String KEY_EMOTICON_FOURTH = "emoticon_fourth";
    public static final String KEY_EMOTICON_FIFTH = "emoticon_fifth";
    public static final String KEY_EMOTICON_SIXTH = "emoticon_sixth"; //us

    public static final String KEY_AD_DEL = "kb_bar_close_icon";

    public static final String KEY_NOR_BTN_NOR_I = "nor_btn_nor_i";
    public static final String KEY_NOR_BTN_PRE_I = "nor_btn_pre_i";
    public static final String KEY_TAB_LOGO = "tab_logo";
    public static final String KEY_TAB_MEMO = "tab_memo";
    public static final String KEY_TAB_MEMO_PLUS = "tab_memo_plus";
    public static final String KEY_KEY_SEARCH_ENTER = "key_search_enter";
    public static final String KEY_SHIFT_IMG = "shift_img";
    public static final String KEY_SHIFT_IMG_1 = "shift_img_1";
    public static final String KEY_SHIFT_IMG_2 = "shift_img_2";
    public static final String KEY_KEY_SYMBOL = "key_symbol";
    public static final String KEY_SPACE_IMG = "space_img";
    public static final String KEY_KEY_DEL = "key_del";
    public static final String KEY_SP_BTN_NOR_I = "sp_btn_nor_i";
    public static final String KEY_SP_BTN_PRE_I = "sp_btn_pre_i";
    public static final String KEY_KEY_LANG = "key_lang";
    public static final String KEY_KEY_MEMO_PLUS = "key_memo_plus";
    public static final String KEY_FAV_BOT_OFF = "fav_bot_off";
    public static final String KEY_FAV_BOT_ON = "fav_bot_on";
    public static final String KEY_FAV_MEMO_OFF = "fav_memo_off";
    public static final String KEY_FAV_MEMO_ON = "fav_memo_on";
    public static final String KEY_DEL_BOT = "del_bot";
    public static final String KEY_KEYBOARD_BOT = "keyboard_bot";
    public static final String KEY_GO_MEMO = "go_memo";
    public static final String KEY_TOP_LINE = "top_line";
    public static final String KEY_BOT_LINE = "bot_line";
    public static final String KEY_KEY_TEXT = "key_text";
    public static final String KEY_KEY_TEXT_S = "key_text_s";
    public static final String KEY_TAB_OFF = "tab_off";
    public static final String KEY_TAB_ON = "tab_on";
    public static final String KEY_FAV_TEXT = "fav_text";
    public static final String KEY_DOWN_THEME = "down_theme";
    public static final String KEY_SP_KEY_ALPHA = "sp_key_alpha";
    public static final String KEY_NOR_KEY_ALPHA = "nor_key_alpha";
    public static final String KEY_BG_ALPHA = "bg_alpha";
    public static final String KEY_TAB_EMOJI = "tab_emoji";
    public static final String KEY_TAB_EMOJI_ON = "tab_emoji_on";
    public static final String KEY_TAB_OK_CASHBACK = "tab_ok_cashback";
    public static final String KEY_TAB_ORABANG = "tab_olabang";
    public static final String KEY_TAB_SHOPPING = "tab_shopping";
    public static final String KEY_TAB_MY = "tab_my";
    public static final String KEY_NOR_BTN_COLOR = "nor_btn_color";
    public static final String KEY_SP_BTN_COLOR = "sp_btn_color";
    public static final String KEY_BOT_TAB_COLOR = "bot_tab_color";

    public static final String KEY_TAB_MORE = "tab_more";
    public static final String KEY_TAB_OCB_SEARCH = "tab_ocb_search";
    public static final String KEY_TAB_MORE_ON = "tab_more_on";
    public static final String KEY_TAB_MY_ON = "tab_my_on";
    public static final String KEY_TAB_ORABANG_ON = "tab_olabang_on";
    public static final String KEY_TAB_OCB_SEARCH_ON = "tab_ocb_search_on";
    public static final String KEY_TAB_SHOPPING_ON = "tab_shopping_on";
    public static final String KEY_ICON_BRAND_OFF = "tab_brand_off";
    public static final String KEY_ICON_BRAND_ON = "tab_brand_on";
    public static final String KEY_ICON_BRAND_ON_2 = "tab_brand_on_2";

    public static final String KEY_TAB_OCB_SAVE_SHOPPING = "tab_ocb_save_shopping";
    public static final String KEY_TAB_OCB_SAVE_SHOPPING_ON = "tab_ocb_save_shopping_on";

    public static final String KEY_PREVIEW = "keyboard_preview";

    private static OnSaveFinishCallbackListener mCallbackListener;

    public static void changeJSONObject(Context context, String key_name, String key_value) {
        AIKBD_DBHelper helper = new AIKBD_DBHelper(context);
        String str = helper.getTheme();
        if ( !TextUtils.isEmpty(str) ) {
            try {
                KeyboardLogPrint.e("BEFORE :: " + str);
                JSONObject object = new JSONObject(str);
                object.remove(key_name);
                object.put(key_name, key_value);

                helper.deleteTheme();
                helper.insertTheme(object.toString());

                KeyboardLogPrint.e("AFTER :: " + helper.getTheme());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ThemeModel GetThemeModel(Context context, int index) {
        ThemeModel model = null;
        AIKBD_DBHelper helper = new AIKBD_DBHelper(context);
        String str = helper.getTheme();
        if (!TextUtils.isEmpty(str)) {
            model = GetThemeModel(str, index);
        }
        return model;
    }

    public static ThemeModel GetThemeModel(String jsonStr, int index) {
        KeyboardLogPrint.e("index :: " + index);
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                if (obj != null) {
                    ThemeModel model = new ThemeModel();
                    boolean gubun = obj.optBoolean(KEY_DOWN_THEME);
                    if (gubun) {
                        KeyboardLogPrint.e("gubun true");
                        model.setTabBookMark(obj.optString(KEY_TAB_BOOKMARK));
                        model.setTabCash(obj.optString(KEY_TAB_CASH));
                        model.setTabZeroCash(obj.optString(KEY_TAB_ZERO_CASH));
                        model.setBgImg(obj.optString(KEY_BGIMG));
                        model.setBgOriginImg(obj.optString(KEY_BG_ORIGINIMG, ""));
                        model.setEnterImg(obj.optString(KEY_ENTER_IMG));
                        model.setEmojiImg(obj.optString(KEY_EMOJI_IMG));
                        model.setEmoticonImg(obj.optString(KEY_EMOTICON_IMG));
                        model.setEmoticonRecent(obj.optString(KEY_EMOTICON_RECENT));
                        model.setEmoticonFirst(obj.optString(KEY_EMOTICON_FIRST));
                        model.setEmoticonSecond(obj.optString(KEY_EMOTICON_SECOND));
                        model.setEmoticonThird(obj.optString(KEY_EMOTICON_THIRD));
                        model.setEmoticonFourth(obj.optString(KEY_EMOTICON_FOURTH));
                        model.setEmoticonFifth(obj.optString(KEY_EMOTICON_FIFTH));
                        model.setEmoticonSixth(obj.optString(KEY_EMOTICON_SIXTH));
                        model.setNorBtnNorI(obj.optString(KEY_NOR_BTN_NOR_I));
                        model.setNorBtnPreI(obj.optString(KEY_NOR_BTN_PRE_I));
                        model.setTabLogo(obj.optString(KEY_TAB_LOGO));
                        model.setTabMemo(obj.optString(KEY_TAB_MEMO));
                        model.setTabMemoPlus(obj.optString(KEY_TAB_MEMO_PLUS));
                        model.setKeySearchEnter(obj.optString(KEY_KEY_SEARCH_ENTER));
                        model.setShiftImg(obj.optString(KEY_SHIFT_IMG));
                        model.setShiftImg1(obj.optString(KEY_SHIFT_IMG_1));
                        model.setShiftImg2(obj.optString(KEY_SHIFT_IMG_2));
                        model.setKeySymbol(obj.optString(KEY_KEY_SYMBOL));
                        model.setSpaceImg(obj.optString(KEY_SPACE_IMG));
                        model.setSpBtnNorI(obj.optString(KEY_SP_BTN_NOR_I));
                        model.setSpBtnPreI(obj.optString(KEY_SP_BTN_PRE_I));
                        model.setKeyLang(obj.optString(KEY_KEY_LANG));
                        model.setKeyMemoPlus(obj.optString(KEY_KEY_MEMO_PLUS));
                        model.setFavBotOff(obj.optString(KEY_FAV_BOT_OFF));
                        model.setFavBotOn(obj.optString(KEY_FAV_BOT_ON));
                        model.setFavMemoOff(obj.optString(KEY_FAV_MEMO_OFF));
                        model.setFavMemoOn(obj.optString(KEY_FAV_MEMO_ON));
                        model.setDelBot(obj.optString(KEY_DEL_BOT));
                        model.setKeyboardBot(obj.optString(KEY_KEYBOARD_BOT));
                        model.setGoMemo(obj.optString(KEY_GO_MEMO));
                        model.setTopLine(obj.optString(KEY_TOP_LINE));
                        model.setBotLine(obj.optString(KEY_BOT_LINE));
                        model.setKeyText(obj.optString(KEY_KEY_TEXT));
                        model.setKeyTextS(obj.optString(KEY_KEY_TEXT_S));
                        model.setTabOff(obj.optString(KEY_TAB_OFF));
                        model.setTabOn(obj.optString(KEY_TAB_ON));
                        model.setFavText(obj.optString(KEY_FAV_TEXT));
                        model.setSpAlpha(obj.optInt(KEY_SP_KEY_ALPHA));
                        model.setNorAlpha(obj.optInt(KEY_NOR_KEY_ALPHA));
                        model.setBgAlpha(obj.optInt(KEY_BG_ALPHA));
                        model.setTabEmoji(obj.optString(KEY_TAB_EMOJI));
                        model.setTabEmojiOn(obj.optString(KEY_TAB_EMOJI_ON));
                        model.setBackImg(obj.optString(KEY_KEY_DEL));
                        model.setNorBtnColor(obj.optInt(KEY_NOR_BTN_COLOR));
                        model.setSpBtnColor(obj.optInt(KEY_SP_BTN_COLOR));
                        model.setBotTabColor(obj.optString(KEY_BOT_TAB_COLOR));
                        model.setTabCashbackLogo(obj.optString(KEY_TAB_OK_CASHBACK));
                        model.setTabOlabang(obj.optString(KEY_TAB_ORABANG));
                        model.setTabShopping(obj.optString(KEY_TAB_SHOPPING));
                        model.setTabMy(obj.optString(KEY_TAB_MY));
                        model.setTabMore(obj.optString(KEY_TAB_MORE));
                        model.setTabOCBSearch(obj.optString(KEY_TAB_OCB_SEARCH));
                        model.setTabMoreOn(obj.optString(KEY_TAB_MORE_ON));
                        model.setTabMyOn(obj.optString(KEY_TAB_MY_ON));
                        model.setTabOlabangOn(obj.optString(KEY_TAB_ORABANG_ON));
                        model.setTabOCBSearchOn(obj.optString(KEY_TAB_OCB_SEARCH_ON));
                        model.setTabShoppingOn(obj.optString(KEY_TAB_SHOPPING_ON));
                        model.setAdDel(obj.optString(KEY_AD_DEL));
                        model.setBrandOff(obj.optString(KEY_ICON_BRAND_OFF));
                        model.setBrandOn(obj.optString(KEY_ICON_BRAND_ON));
                        model.setBrandOn2(obj.optString(KEY_ICON_BRAND_ON_2));
                        model.setKeyPreview(obj.optString(KEY_PREVIEW));
                        model.setTabSaveShopping(obj.optString(KEY_TAB_OCB_SAVE_SHOPPING));
                        model.setTabSaveShoppingOn(obj.optString(KEY_TAB_OCB_SAVE_SHOPPING_ON));
                        return model;
                    } else {
                        KeyboardLogPrint.e("gubun false");
                        return null;
                    }
                } else {
                    KeyboardLogPrint.e("obj null");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            KeyboardLogPrint.e("jsonStr null");
            return null;
        }
    }

/*    public static NinePatchDrawable GetNinePatch(Context context, String name) {
        NinePatchDrawable dr = null;
        try {
            InputStream stream = new FileInputStream(name);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            dr = NinePatchBitmapFactory.createNinePatchDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dr;
    }*/

    // new FileInputStream로 inputstream을 열어놓고 close 시키는 부분이 없어 메모리 누수 발생 위험
    public static NinePatchDrawable GetNinePatch(Context context, String name) {
        NinePatchDrawable dr = null;
        InputStream stream = null;
        try {
            stream = new FileInputStream(name);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            dr = NinePatchBitmapFactory.createNinePatchDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {}
            }
        }
        return dr;
    }

    public static Drawable GetNinePatch1(Context context, String name) {
        Bitmap bitmap = BitmapFactory.decodeFile(name);
        byte[] chunk = bitmap.getNinePatchChunk();
        if(NinePatch.isNinePatchChunk(chunk)) {
            KeyboardLogPrint.w("nine patch 0");
            return new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
        } else {
            KeyboardLogPrint.w("nine patch x");
            return new BitmapDrawable(bitmap);
        }
    }

    public static void ResizingSpImage(String path, Bitmap.CompressFormat format, int quality, double scale) {
        if ( !TextUtils.isEmpty(path)) {
            // 2023.05.08 memory leak 처리
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
            int bitmapWidth = (int)(bitmap.getWidth() * scale);
            int bitmapHeight = (int)(bitmap.getHeight() * scale);
            KeyboardLogPrint.e("before bitmap width :: " + bitmap.getWidth());
            KeyboardLogPrint.e("before bitmap height :: " + bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
            KeyboardLogPrint.e("after bitmap width :: " + bitmap.getWidth());
            KeyboardLogPrint.e("after bitmap height :: " + bitmap.getHeight());

            File imageFile = new File(path);


            FileOutputStream fos = null;
            try {
                imageFile.delete();
                fos = new FileOutputStream(imageFile);

                bitmap.compress(format,quality,fos);

                fos.close();
            }
            catch (IOException e) {
                KeyboardLogPrint.e(e.getMessage());
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } else {

        }
    }

    public static Bitmap GetBitmapFromPath(String path) {
        // 2023.05.08 memory leak 처리
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
//        Bitmap bm = BitmapFactory.decodeFile(path);
        return bm;
    }

    public static Drawable GetDrawableFromPath(String path) {
        try {
            Drawable dr = null;
            if (!TextUtils.isEmpty(path)) {
                dr = Drawable.createFromPath(path);
            }
            return dr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Drawable GetImageSelector(Drawable nor, Drawable pre) {
        Drawable dr = null;
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pre);
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, nor);
        return stateListDrawable;
    }

    public static Drawable GetColorSelector(int nor_color, int pre_color) {
        ColorDrawable nor = new ColorDrawable(nor_color);
        ColorDrawable pre = new ColorDrawable(pre_color);

        return GetImageSelector(nor, pre);
    }

    public static Drawable GetNorSelector(Context context, ThemeModel model) {
        NinePatchDrawable norNor = ThemeManager.GetNinePatch(context, model.getNorBtnNorI()); // 일반키 normal
        NinePatchDrawable norPre = ThemeManager.GetNinePatch(context, model.getNorBtnPreI()); // 일반키 pressed
        Drawable norBtnSelector = ThemeManager.GetImageSelector(norNor, norPre); // 일반키 selector
        return norBtnSelector;
    }

    public static Drawable GetSpSelector(Context context, ThemeModel model) {
        NinePatchDrawable speNor = ThemeManager.GetNinePatch(context, model.getSpBtnNorI()); // 특수키 normal
        NinePatchDrawable spePre = ThemeManager.GetNinePatch(context, model.getSpBtnPreI()); // 특수키 pressed
        Drawable spBtnSelector = ThemeManager.GetImageSelector(speNor, spePre); // 특수키 selector
        return spBtnSelector;
    }

    public static boolean saveBitmapToFile(File dir, String fileName, Bitmap bm, Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(format,quality,fos);

            fos.close();

            return true;
        }
        catch (IOException e) {
            KeyboardLogPrint.e(e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean saveBitmapToFile(File dir, String fileName, Bitmap bm, Bitmap.CompressFormat format, int quality, OnSaveFinishCallbackListener listener) {
        mCallbackListener = listener;
        File imageFile = new File(dir,fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(format,quality,fos);

            fos.close();
            if ( mCallbackListener != null ) {
                KeyboardLogPrint.e("callback not null");
                mCallbackListener.onResponse(true);
            } else {
                KeyboardLogPrint.e("callback null");
            }
            return true;
        }
        catch (IOException e) {
            KeyboardLogPrint.e(e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if ( mCallbackListener != null ) {
                mCallbackListener.onResponse(false);
            }
        }
        return false;
    }

    public static boolean BgDrawableToFile(Drawable drawable, String dir, String fileName) {
        File dFile = new File(dir);
        Bitmap bm = drawableToBitmap(drawable);
        boolean result = saveBitmapToFile(dFile, fileName, bm, Bitmap.CompressFormat.PNG, 100);
        return result;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        return bitmap;
    }



    public static void deleteAllFiles(File dir, String except) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    File temp = new File(dir, children[i]);
                    if (temp.isDirectory()) {
                        deleteAllFiles(temp, except);
                    }
                    else
                    {
                        boolean b = temp.delete();
                        if (b == false)
                        {
                        }
                    }
                }

            }
            dir.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllFiles(String path, String except) {
        try {
            File file = new File(path);
            File[] tempFile = file.listFiles();
            if (tempFile.length > 0) {
                for (int i = 0; i < tempFile.length; i++) {
                    if (tempFile[i].isFile()) {
                        if ( !tempFile[i].getAbsolutePath().contains(except) ) {
                            KeyboardLogPrint.e("delete file :: " + tempFile[i].getAbsolutePath());
                            tempFile[i].delete();
                        } else {
                            KeyboardLogPrint.i("not delete file :: " + tempFile[i].getAbsolutePath());
                        }
                    } else {
                        deleteAllFiles(tempFile[i].getPath(), except);
                    }
                    if ( !tempFile[i].getAbsolutePath().contains(except) ) {
                        KeyboardLogPrint.e("delete file :: " + tempFile[i].getAbsolutePath());
                        tempFile[i].delete();
                    } else {
                        KeyboardLogPrint.i("not delete file :: " + tempFile[i].getAbsolutePath());
                    }
                }
                new File(path).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DeleteFile(Context context, String path, String except) {
        try {
            File file = new File(path);
            File[] tempFile = file.listFiles();
            if (tempFile.length > 0) {
                for (int i = 0; i < tempFile.length; i++) {
                    if (tempFile[i].isFile()) {
                        if ( !tempFile[i].getAbsolutePath().contains(except) ) {
                            tempFile[i].delete();
                        } else {
                        }
                    } else {
                        DeleteFile(context, tempFile[i].getPath(), except);
                    }
                    if ( !tempFile[i].getAbsolutePath().contains(except) ) {
                        tempFile[i].delete();
                    } else {
                    }
                }
                new File(path).delete();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int GetScaleLevel(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mgr.getDefaultDisplay().getMetrics(metrics);

        int density = metrics.densityDpi;
        KeyboardLogPrint.d("GetScale densityDPI = " + density);
        if ( density <= 120 ) { // ldpi
            KeyboardLogPrint.e("GetScale 1");
            return 0;
        } else if ( 120 < density && density <= 160 ) { // mdpi
            KeyboardLogPrint.e("GetScale 2");
            return 0;
        } else if ( 160 < density && density <= 240 ) { // hdpi
            KeyboardLogPrint.e("GetScale 3");
            return 0;
        } else if ( 240 < density && density <= 320 ) { // xhdpi
            KeyboardLogPrint.e("GetScale 4");
            return 0;
        } else if ( 320 < density && density <= 480 ) { // xxhdpi
            KeyboardLogPrint.e("GetScale 5");
            return 1;
        } else { // xxxhdpi
            KeyboardLogPrint.e("GetScale 6");
            return 2;
        }
    }

    public static double GetScale(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mgr.getDefaultDisplay().getMetrics(metrics);

        int density = metrics.densityDpi;
        KeyboardLogPrint.d("GetScale densityDPI = " + density);
        if ( density <= 120 ) { // ldpi
            KeyboardLogPrint.e("GetScale 1");
            return 0.37;
        } else if ( 120 < density && density <= 160 ) { // mdpi
            KeyboardLogPrint.e("GetScale 2");
            return 0.5;
        } else if ( 160 < density && density <= 240 ) { // hdpi
            KeyboardLogPrint.e("GetScale 3");
            return 0.66;
        } else if ( 240 < density && density <= 320 ) { // xhdpi
            KeyboardLogPrint.e("GetScale 4");
            return 0.75;
        } else if ( 320 < density && density <= 480 ) { // xxhdpi
            KeyboardLogPrint.e("GetScale 5");
            return 1;
        } else { // xxxhdpi
            KeyboardLogPrint.e("GetScale 6");
            return 1.5;
        }
    }

    public interface OnSaveFinishCallbackListener {
        public void onResponse(boolean result);
    }
}
