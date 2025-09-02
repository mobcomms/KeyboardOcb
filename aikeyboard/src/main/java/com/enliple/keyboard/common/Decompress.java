package com.enliple.keyboard.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress {
    private static final int BUFFER_SIZE = 1024 * 10;
    private static final String TAG = "Decompress";
    private PostUnzip _callback;

    public void AssetUnZip(Context context, boolean isDefault, PostUnzip callback) {
        AssetManager am = context.getResources().getAssets() ;
        InputStream is = null ;
        FileOutputStream fos = null ;
        byte buf[] = new byte[1024] ;
        String zipFileName = "";
        String unzipFileName = "";
        try {
            if ( isDefault ) {
                zipFileName = "Default.zip";
                unzipFileName = "Default";
            } else {
                zipFileName = "theme_color_01.zip";
                unzipFileName = "theme_color_01";
            }

            is = am.open(zipFileName) ;
//            fos = new FileOutputStream(getFilesDir() + "file.txt") ;
            fos = new FileOutputStream(context.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + zipFileName);
            while (is.read(buf) > 0) {
                fos.write(buf) ;
            }

            fos.close() ;
            is.close() ;

//            File dFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + zipFileName);
//            File uzFile = new File(context.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + unzipFileName);
//            KeyboardLogPrint.e("aaa dFile aaa  ::: " + dFile.exists());
//            KeyboardLogPrint.e("aaa uzFile aaa  ::: " + uzFile.exists());
//            if ( uzFile.exists() )
//                uzFile.delete();
//            KeyboardLogPrint.e("dFile path :: " + context.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + zipFileName);
            unzip(context.getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + zipFileName, context.getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + unzipFileName + File.separator, isDefault, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unzip(String zipFile, String location, boolean isDefault, PostUnzip callback) {
        try {
            FileInputStream fin = new FileInputStream(zipFile);
            unzip(fin, zipFile, location, isDefault, callback);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void unzip(InputStream stream, String zipFile, String destination, boolean isDefault, PostUnzip callback) {
        KeyboardLogPrint.d("skkim!! unzip call");
        dirChecker(destination, "");
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            _callback = callback;
            ZipInputStream zin = new ZipInputStream(stream);
            ZipEntry ze = null;
            JSONObject object = new JSONObject();
            while ((ze = zin.getNextEntry()) != null) {
                KeyboardLogPrint.d("skkim!! Unzipping " + ze.getName());

                if (ze.isDirectory()) {
                    dirChecker(destination, ze.getName());
                } else {
                    File f = new File(destination, ze.getName());
                    String destDirCanonicalPath = (new File(destination)).getCanonicalPath();
                    String outputFileCanonicalPath = f.getCanonicalPath();
                    KeyboardLogPrint.d("skkim!! destDirCanonicalPath    :: " + destDirCanonicalPath);
                    KeyboardLogPrint.d("skkim!! outputFileCanonicalPath :: " + outputFileCanonicalPath);
                    if (!outputFileCanonicalPath.startsWith(destDirCanonicalPath)) {
                        throw new Exception(String.format("Found Zip Path Traversal Vulnerability with %s", outputFileCanonicalPath));
                    } else {
                    if ( f.exists() )
                        f.delete();
                    if (!f.exists()) {
                        FileOutputStream fout = new FileOutputStream(destination + ze.getName());
                        int count;
                        while ((count = zin.read(buffer)) != -1) {
                            fout.write(buffer, 0, count);
//                            object = makeJSON(object, ze.getName(), destination);
                            if ( isDefault )
                                object = makeAssetJSON(object, ze.getName(), destination);
                            else
                                object = makeJSON(object, ze.getName(), destination);
                        }
                        zin.closeEntry();
                        fout.close();

                        KeyboardLogPrint.e("skkim !! json result :: " + object.toString());
                    }
                    }
//                    File f = new File(destination + ze.getName());
//                    KeyboardLogPrint.e("destination :: " + destination + ze.getName());
//                    if ( f.exists() )
//                        f.delete();
//                    if (!f.exists()) {
//                        FileOutputStream fout = new FileOutputStream(destination + ze.getName());
//                        int count;
//                        while ((count = zin.read(buffer)) != -1) {
//                            fout.write(buffer, 0, count);
////                            object = makeJSON(object, ze.getName(), destination);
//                            if ( isDefault )
//                                object = makeAssetJSON(object, ze.getName(), destination);
//                            else
//                                object = makeJSON(object, ze.getName(), destination);
//                        }
//                        zin.closeEntry();
//                        fout.close();
//
//                        KeyboardLogPrint.e("json result :: " + object.toString());
//                    }
                }

            }
            zin.close();

            if ( _callback != null ) {

                _callback.unzipDone(object.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject makeAssetJSON(JSONObject object, String fileName, String destination) {
        try {
            String tag_fileName = null;
            tag_fileName = fileName.replaceAll("Default/", "");

            if ( ThemeManager.KEY_DEL.equals(tag_fileName) ) {
                object.put("key_del", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT.equals(tag_fileName) ) {
                object.put("shift_img", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT_1.equals(tag_fileName) ) {
                object.put("shift_img_1", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT_2.equals(tag_fileName) ) {
                object.put("shift_img_2", destination + fileName);
            } else if ( ThemeManager.KEY_ENTER.equals(tag_fileName) ) {
                object.put("enter_img", destination + fileName);
            } else if ( ThemeManager.KEY_EMOJI.equals(tag_fileName) ) {
                object.put("emoji_img", destination + fileName);
            } else if ( ThemeManager.KEY_EMOTICON.equals(tag_fileName) ) {
                object.put("emoticon_img", destination + fileName);
            } else if ( ThemeManager.KEY_NOR_OFF.equals(tag_fileName) ) {
                object.put("nor_btn_nor_i", destination + fileName);
            } else if ( ThemeManager.KEY_NOR_ON.equals(tag_fileName) ) {
                object.put("nor_btn_pre_i", destination + fileName);
            } else if ( ThemeManager.KEY_SPACE.equals(tag_fileName) ) {
                object.put("space_img", destination + fileName);
            } else if ( ThemeManager.KEY_SPE_OFF.equals(tag_fileName) ) {
                object.put("sp_btn_nor_i", destination + fileName);
            } else if ( ThemeManager.KEY_SPE_ON.equals(tag_fileName) ) {
                object.put("sp_btn_pre_i", destination + fileName);
            } else if ( ThemeManager.BG.equals(tag_fileName) ) {
                object.put("bgimg", destination + fileName);
            } else if ( ThemeManager.BG_ORIGIN.equals(tag_fileName) ) {
                object.put("bgoriginimg", destination + fileName);
            } else if ( ThemeManager.TAB_BOOKMARK.equals(tag_fileName) ) {
                object.put("tab_bookmark", destination + fileName);
            } else if ( ThemeManager.TAB_CASH.equals(tag_fileName) ) {
                object.put("tab_cash", destination + fileName);
            } else if ( ThemeManager.TAB_ZERO_CASH.equals(tag_fileName) ) {
                object.put("tab_zero_cash", destination + fileName);
            } else if ( ThemeManager.KEY_SEARCH_ENTER.equals(tag_fileName) ) {
                object.put("key_search_enter", destination + fileName);
            } else if ( ThemeManager.TAB_LOGO.equals(tag_fileName) ) {
                object.put("tab_logo", destination + fileName);
            } else if ( ThemeManager.TAB_MEMO.equals(tag_fileName) ) {
                object.put("tab_memo", destination + fileName);
            } else if ( ThemeManager.TAB_MEMO_PLUS.equals(tag_fileName) ) {
                object.put("tab_memo_plus", destination + fileName);
            } else if ( ThemeManager.KEY_SYMBOL.equals(tag_fileName) ) {
                object.put("key_symbol", destination + fileName);
            } else if ( ThemeManager.KEY_LANG.equals(tag_fileName) ) {
                object.put("key_lang", destination + fileName);
            } else if ( ThemeManager.KEY_MEMO_PLUS.equals(tag_fileName) ) {
                object.put("key_memo_plus", destination + fileName);
            } else if ( ThemeManager.FAV_BOT_OFF.equals(tag_fileName) ) {
                object.put("fav_bot_off", destination + fileName);
            } else if ( ThemeManager.FAV_BOT_ON.equals(tag_fileName) ) {
                object.put("fav_bot_on", destination + fileName);
            } else if ( ThemeManager.FAV_MEMO_OFF.equals(tag_fileName) ) {
                object.put("fav_memo_off", destination + fileName);
            } else if ( ThemeManager.FAV_MEMO_ON.equals(tag_fileName) ) {
                object.put("fav_memo_on", destination + fileName);
            } else if ( ThemeManager.DEL_BOT.equals(tag_fileName) ) {
                object.put("del_bot", destination + fileName);
            } else if ( ThemeManager.KEYBOARD_BOT.equals(tag_fileName) ) {
                object.put("keyboard_bot", destination + fileName);
            } else if ( ThemeManager.GO_MEMO.equals(tag_fileName) ) {
                object.put("go_memo", destination + fileName);
            } else if ( ThemeManager.TAB_EMOJI.equals(tag_fileName) ) {
                object.put("tab_emoji", destination + fileName);
            } else if ( ThemeManager.TAB_EMOJI_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_EMOJI_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OK_CASHBACK_LOGO.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_OK_CASHBACK, destination + fileName);
            } else if ( ThemeManager.TAB_OLABANG.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_ORABANG, destination + fileName);
            } else if ( ThemeManager.TAB_SHOPPING.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_SHOPPING, destination + fileName);
            } else if ( ThemeManager.TAB_MY.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_MY, destination + fileName);
            } else if ( ThemeManager.TAB_MORE.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_MORE, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SEARCH.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SEARCH, destination + fileName);
            } else if ( ThemeManager.TAB_MORE_ON.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_MORE_ON, destination + fileName);
            } else if ( ThemeManager.TAB_MY_ON.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_MY_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OLABANG_ON.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_ORABANG_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SEARCH_ON.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SEARCH_ON, destination + fileName);
            } else if ( ThemeManager.TAB_SHOPPING_ON.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_SHOPPING_ON, destination + fileName);
            } else if ( ThemeManager.THEME_COLOR.equals(tag_fileName) ) {
                object = parseColor(object, fileName, destination);
            } else if ( ThemeManager.IMG_AD_DEL.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_AD_DEL, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_OFF.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_OFF, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_ON.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_ON, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_ON_2.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_ON_2, destination + fileName);
            } else if ( ThemeManager.IMG_KEY_PREVIEW.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_PREVIEW, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SAVE_SHOPPING.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SAVE_SHOPPING, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SAVE_SHOPPING_ON.equals(tag_fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SAVE_SHOPPING_ON, destination + fileName);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject makeJSON(JSONObject object, String fileName, String destination) {
        try {
            LogPrint.e("fileName :: " + fileName);
            if ( ThemeManager.KEY_DEL.equals(fileName) ) {
                object.put("key_del", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT.equals(fileName) ) {
                object.put("shift_img", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT_1.equals(fileName) ) {
                object.put("shift_img_1", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT_2.equals(fileName) ) {
                object.put("shift_img_2", destination + fileName);
            } else if ( ThemeManager.KEY_ENTER.equals(fileName) ) {
                object.put("enter_img", destination + fileName);
            } else if ( ThemeManager.KEY_EMOJI.equals(fileName) ) {
                object.put("emoji_img", destination + fileName);
            } else if ( ThemeManager.KEY_EMOTICON.equals(fileName) ) {
                object.put("emoticon_img", destination + fileName);
            } else if ( ThemeManager.KEY_NOR_OFF.equals(fileName) ) {
                object.put("nor_btn_nor_i", destination + fileName);
            } else if ( ThemeManager.KEY_NOR_ON.equals(fileName) ) {
                object.put("nor_btn_pre_i", destination + fileName);
            } else if ( ThemeManager.KEY_SPACE.equals(fileName) ) {
                object.put("space_img", destination + fileName);
            } else if ( ThemeManager.KEY_SPE_OFF.equals(fileName) ) {
                object.put("sp_btn_nor_i", destination + fileName);
            } else if ( ThemeManager.KEY_SPE_ON.equals(fileName) ) {
                object.put("sp_btn_pre_i", destination + fileName);
            } else if ( ThemeManager.BG.equals(fileName) ) {
                object.put("bgimg", destination + fileName);
            } else if ( ThemeManager.BG_ORIGIN.equals(fileName) ) {
                object.put("bgoriginimg", destination + fileName);
            } else if ( ThemeManager.TAB_BOOKMARK.equals(fileName) ) {
                object.put("tab_bookmark", destination + fileName);
            } else if ( ThemeManager.TAB_CASH.equals(fileName) ) {
                object.put("tab_cash", destination + fileName);
            } else if ( ThemeManager.TAB_ZERO_CASH.equals(fileName) ) {
                object.put("tab_zero_cash", destination + fileName);
            } else if ( ThemeManager.KEY_SEARCH_ENTER.equals(fileName) ) {
                object.put("key_search_enter", destination + fileName);
            } else if ( ThemeManager.TAB_LOGO.equals(fileName) ) {
                object.put("tab_logo", destination + fileName);
            } else if ( ThemeManager.TAB_MEMO.equals(fileName) ) {
                object.put("tab_memo", destination + fileName);
            } else if ( ThemeManager.TAB_MEMO_PLUS.equals(fileName) ) {
                object.put("tab_memo_plus", destination + fileName);
            } else if ( ThemeManager.KEY_SYMBOL.equals(fileName) ) {
                object.put("key_symbol", destination + fileName);
            } else if ( ThemeManager.KEY_LANG.equals(fileName) ) {
                object.put("key_lang", destination + fileName);
            } else if ( ThemeManager.KEY_MEMO_PLUS.equals(fileName) ) {
                object.put("key_memo_plus", destination + fileName);
            } else if ( ThemeManager.FAV_BOT_OFF.equals(fileName) ) {
                object.put("fav_bot_off", destination + fileName);
            } else if ( ThemeManager.FAV_BOT_ON.equals(fileName) ) {
                object.put("fav_bot_on", destination + fileName);
            } else if ( ThemeManager.FAV_MEMO_OFF.equals(fileName) ) {
                object.put("fav_memo_off", destination + fileName);
            } else if ( ThemeManager.FAV_MEMO_ON.equals(fileName) ) {
                object.put("fav_memo_on", destination + fileName);
            } else if ( ThemeManager.DEL_BOT.equals(fileName) ) {
                object.put("del_bot", destination + fileName);
            } else if ( ThemeManager.KEYBOARD_BOT.equals(fileName) ) {
                object.put("keyboard_bot", destination + fileName);
            } else if ( ThemeManager.GO_MEMO.equals(fileName) ) {
                object.put("go_memo", destination + fileName);
            } else if ( ThemeManager.TAB_EMOJI.equals(fileName) ) {
                object.put("tab_emoji", destination + fileName);
            } else if ( ThemeManager.TAB_EMOJI_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_EMOJI_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OK_CASHBACK_LOGO.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OK_CASHBACK, destination + fileName);
            } else if ( ThemeManager.TAB_OLABANG.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_ORABANG, destination + fileName);
            } else if ( ThemeManager.TAB_SHOPPING.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_SHOPPING, destination + fileName);
            } else if ( ThemeManager.TAB_MY.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_MY, destination + fileName);
            } else if ( ThemeManager.TAB_MORE.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_MORE, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SEARCH.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SEARCH, destination + fileName);
            } else if ( ThemeManager.TAB_MORE_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_MORE_ON, destination + fileName);
            } else if ( ThemeManager.TAB_MY_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_MY_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OLABANG_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_ORABANG_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SEARCH_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SEARCH_ON, destination + fileName);
            } else if ( ThemeManager.TAB_SHOPPING_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_SHOPPING_ON, destination + fileName);
            } else if ( ThemeManager.THEME_COLOR.equals(fileName) ) {
                object = parseColor(object, fileName, destination);
            } else if ( ThemeManager.IMG_AD_DEL.equals(fileName) ) {
                object.put(ThemeManager.KEY_AD_DEL, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_OFF.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_OFF, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_ON.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_ON, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_ON_2.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_ON_2, destination + fileName);
            } else if ( ThemeManager.IMG_KEY_PREVIEW.equals(fileName) ) {
                object.put(ThemeManager.KEY_PREVIEW, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SAVE_SHOPPING.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SAVE_SHOPPING, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SAVE_SHOPPING_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SAVE_SHOPPING_ON, destination + fileName);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String ReadTextFile(String fileName, String destination ) {
        String text = null;
        try {
            File file = new File(destination + fileName);
            FileInputStream fis = new FileInputStream(file);
            Reader in = new InputStreamReader(fis);
            int size = fis.available();
            char[] buffer = new char[size];
            in.read(buffer);
            in.close();

            text = new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return text;
    }

    private JSONObject parseColor(JSONObject object, String fileName, String destination) {
        try {
            String colorStr = ReadTextFile(fileName, destination);
            KeyboardLogPrint.e("colorStr :: " + colorStr);
            if ( !TextUtils.isEmpty(colorStr) ) {
                JSONObject obj = new JSONObject(colorStr);
                object.put("top_line", obj.optString("top_line"));
                object.put("bot_line", obj.optString("bot_line"));
                object.put("key_text", obj.optString("key_text"));
                object.put("key_text_s", obj.optString("key_text_s"));
                object.put("tab_off", obj.optString("tab_off"));
                object.put("tab_on", obj.optString("tab_on"));
                object.put("fav_text", obj.optString("fav_text"));
                object.put("down_theme", obj.optString("down_theme"));
                object.put("sp_key_alpha", obj.optString("sp_key_alpha"));
                object.put("nor_key_alpha", obj.optString("nor_key_alpha"));
                object.put("bg_alpha", obj.optString("bg_alpha"));
                object.put("nor_btn_color", obj.optInt("nor_btn_color"));
                object.put("sp_btn_color", obj.optInt("sp_btn_color"));
                object.put("bot_tab_color", obj.optString("bot_tab_color"));
            }

            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void dirChecker(String destination, String dir) {
        File f = new File(destination + dir);

        if (!f.isDirectory()) {
            boolean success = f.mkdirs();
            if (!success) {
                KeyboardLogPrint.w("Failed to create folder " + f.getName());
            }
        }
    }

    public static interface PostUnzip {
        void unzipDone(String result);
    }
}