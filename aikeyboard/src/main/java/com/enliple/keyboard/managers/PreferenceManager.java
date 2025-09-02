package com.enliple.keyboard.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.models.RecentEmojiModel;
import com.enliple.keyboard.models.RecentEmoticonModel;
import com.enliple.keyboard.models.RecentSearchModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PreferenceManager {

    private static PreferenceManager preferencesManager;
    private static Context mContext = null;
    private static SharedPreferences mPref = null;

    private static String PREF_KEY_RECENT_SEARCH = "PREF_KEY_RECENT_SEARCH";
    private static String PREF_KEY_RECENT_EMOJI = "PREF_KEY_RECENT_EMOJI";
    private static String PREF_KEY_RECENT_EMOTICON = "PREF_KEY_RECENT_EMOTICON";

    private static String JSON_KEY_SEARCH_WORD = "JSON_KEY_SEARCH_WORD";
    private static String JSON_KEY_DATE = "JSON_KEY_DATE";
    private static String JSON_KEY_LAST_DATE = "JSON_KEY_LAST_DATE";

    private static String JSON_KEY_UNICODE = "JSON_KEY_UNICODE";
    private static String JSON_KEY_SEQUENCE = "JSON_KEY_SEQUENCE";

    private static String JSON_KEY_EMOTICON = "JSON_KEY_EMOTICON";

    public PreferenceManager(Context context) {
        mContext = context;
        if (mContext != null)
            if (mPref == null)
                mPref = mContext.getSharedPreferences("enliplePreferenceManager", 0);
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (preferencesManager == null) {
            preferencesManager = new PreferenceManager(context);
        }
        return preferencesManager;
    }

    public void setRecentSearch(String searchWord) {

        List<RecentSearchModel> carsList = getRecentSearch();

        if (carsList.size() >= 20)
            carsList.remove(19);

        for (RecentSearchModel item : carsList)
            if (item.getSearchWord().equals(searchWord)) {
                carsList.remove(item);
                break;
            }

        SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.KOREA);
        Date currentTime = new Date();
        String sCurTime = df.format(currentTime);
        KeyboardLogPrint.w("recent searchWord :: " + searchWord + " is exist");

        RecentSearchModel result = new RecentSearchModel();
        result.setSearchWord(searchWord);
        result.setDate(sCurTime);
        result.setLastDate(System.currentTimeMillis());
        carsList.add(0, result);

        JSONArray jsonArray = new JSONArray();
        try {
            for (RecentSearchModel item : carsList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JSON_KEY_SEARCH_WORD, item.getSearchWord());
                jsonObject.put(JSON_KEY_DATE, item.getDate());
                jsonObject.put(JSON_KEY_LAST_DATE, item.getLastDate());
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPref.edit().putString(PREF_KEY_RECENT_SEARCH, jsonArray.toString()).apply();
    }

    public List<RecentSearchModel> getRecentSearch() {
        String sRecentSearch = mPref.getString(PREF_KEY_RECENT_SEARCH, null);
        ArrayList<RecentSearchModel> listRecentSearch = new ArrayList<RecentSearchModel>();

        if (sRecentSearch != null) {
            try {
                JSONArray jsonArray = new JSONArray(sRecentSearch);
                for (int i = 0 ; i < jsonArray.length() ; i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    RecentSearchModel result = new RecentSearchModel();

                    result.setSearchWord(json.getString(JSON_KEY_SEARCH_WORD));
                    result.setDate(json.getString(JSON_KEY_DATE));
                    result.setLastDate(json.getLong(JSON_KEY_LAST_DATE));
                    listRecentSearch.add(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(listRecentSearch, (lhs, rhs) -> lhs.getLastDate() > rhs.getLastDate() ? -1 : 1);

        return listRecentSearch;
    }

    public void deleteRecentSearch(String searchWord) {
        List<RecentSearchModel> carsList = getRecentSearch();

        for(RecentSearchModel item : carsList)
            if(item.getSearchWord().equals(searchWord)) {
                carsList.remove(item);
                break;
            }

        JSONArray jsonArray = new JSONArray();
        try {
            for (RecentSearchModel item : carsList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JSON_KEY_SEARCH_WORD, item.getSearchWord());
                jsonObject.put(JSON_KEY_DATE, item.getDate());
                jsonObject.put(JSON_KEY_LAST_DATE, item.getLastDate());
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPref.edit().putString(PREF_KEY_RECENT_SEARCH, jsonArray.toString()).apply();
    }

    public void setRecentEmoji(String _emoji) {

        List<RecentEmojiModel> carsList = getRecentEmoji();

        if (carsList.size() >= 70)
            carsList.remove(69);

        for (RecentEmojiModel item : carsList)
            if (item.getUnicode().equals(_emoji)) {
                carsList.remove(item);
                break;
            }

        SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.KOREA);
        Date currentTime = new Date();
        String sCurTime = df.format(currentTime);
        KeyboardLogPrint.w("recent _emoji :: " + _emoji + " is exist");

        RecentEmojiModel result = new RecentEmojiModel();
        result.setUniCode(_emoji);
//        result.setDate(sCurTime);
        result.setLastDate(System.currentTimeMillis());
        carsList.add(0, result);

        JSONArray jsonArray = new JSONArray();
        try {
            for (RecentEmojiModel item : carsList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JSON_KEY_UNICODE, item.getUnicode());
//                jsonObject.put(JSON_KEY_SEQUENCE, item.getSequence());
                jsonObject.put(JSON_KEY_LAST_DATE, item.getLastDate());
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPref.edit().putString(PREF_KEY_RECENT_EMOJI, jsonArray.toString()).apply();
    }

    public List<RecentEmojiModel> getRecentEmoji() {
        String sRecentSearch = mPref.getString(PREF_KEY_RECENT_EMOJI, null);
        ArrayList<RecentEmojiModel> listRecentSearch = new ArrayList<RecentEmojiModel>();

        if (sRecentSearch != null) {
            try {
                JSONArray jsonArray = new JSONArray(sRecentSearch);
                for (int i = 0 ; i < jsonArray.length() ; i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    RecentEmojiModel result = new RecentEmojiModel();

                    result.setUniCode(json.getString(JSON_KEY_UNICODE));
//                    result.setSequence(json.getInt(JSON_KEY_SEQUENCE));
                    result.setLastDate(json.getInt(JSON_KEY_LAST_DATE));
                    listRecentSearch.add(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(listRecentSearch, (lhs, rhs) -> lhs.getLastDate() > rhs.getLastDate() ? -1 : 1);

        return listRecentSearch;
    }

    public void setRecentEmoticon(String _emoticon) {

        List<RecentEmoticonModel> carsList = getRecentEmoticon();

        if (carsList.size() >= 35)
            carsList.remove(34);

        for (RecentEmoticonModel item : carsList)
            if (item.getText().equals(_emoticon)) {
                carsList.remove(item);
                break;
            }

        SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.KOREA);
        Date currentTime = new Date();
        String sCurTime = df.format(currentTime);
        KeyboardLogPrint.w("recent _emoticon :: " + _emoticon + " is exist");

        RecentEmoticonModel result = new RecentEmoticonModel();
        result.setText(_emoticon);
//        result.setDate(sCurTime);
        result.setLastDate(System.currentTimeMillis());
        carsList.add(0, result);

        JSONArray jsonArray = new JSONArray();
        try {
            for (RecentEmoticonModel item : carsList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JSON_KEY_EMOTICON, item.getText());
//                jsonObject.put(JSON_KEY_SEQUENCE, item.getSequence());
                jsonObject.put(JSON_KEY_LAST_DATE, item.getLastDate());
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPref.edit().putString(PREF_KEY_RECENT_EMOTICON, jsonArray.toString()).apply();
    }

    public List<RecentEmoticonModel> getRecentEmoticon() {
        String sRecentSearch = mPref.getString(PREF_KEY_RECENT_EMOTICON, null);
        ArrayList<RecentEmoticonModel> listRecentSearch = new ArrayList<RecentEmoticonModel>();

        if (sRecentSearch != null) {
            try {
                JSONArray jsonArray = new JSONArray(sRecentSearch);
                for (int i = 0 ; i < jsonArray.length() ; i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    RecentEmoticonModel result = new RecentEmoticonModel();

                    result.setText(json.getString(JSON_KEY_EMOTICON));
//                    result.setSequence(json.getInt(JSON_KEY_SEQUENCE));
                    result.setLastDate(json.getInt(JSON_KEY_LAST_DATE));
                    listRecentSearch.add(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(listRecentSearch, (lhs, rhs) -> lhs.getLastDate() > rhs.getLastDate() ? -1 : 1);

        return listRecentSearch;
    }
}
