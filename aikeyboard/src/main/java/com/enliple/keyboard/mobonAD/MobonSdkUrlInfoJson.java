package com.enliple.keyboard.mobonAD;

import android.text.TextUtils;

import org.json.JSONObject;

public class MobonSdkUrlInfoJson {
    public String ending_s;
    public String intro_s;
    public String banner_s;
    public String bacon_s;
    public String json1_s;
    public String json2_s;
    public String json3_s;
    public String json4_s;
    public String mcover_s;
    public int mcover_fq;
    public String bntype;
    public String domain;
    public String rfUrl;
    public String logView;
    public int auid_fq;
    public String cross_browser;

    public MobonSdkUrlInfoJson(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            ending_s = obj.optString("ending");
            intro_s = obj.optString("intro");
            banner_s = obj.optString("banner");
            bacon_s = obj.optString("bacon");
            json1_s = TextUtils.isEmpty(obj.optString("json1")) ? obj.optString("json_s1") : obj.optString("json1");
            json2_s = TextUtils.isEmpty(obj.optString("json2")) ? obj.optString("json_s2") : obj.optString("json2");
            json3_s = TextUtils.isEmpty(obj.optString("json3")) ? obj.optString("json_s3") : obj.optString("json3");
            json4_s = TextUtils.isEmpty(obj.optString("json4")) ? obj.optString("json_s4") : obj.optString("json4");
            mcover_s = obj.optString("mcover");
            mcover_fq = obj.optInt("fq_mcover");
            auid_fq = obj.optInt("fq_auid") < 1 ? 7 : obj.optInt("fq_auid");
            bntype = obj.optString("bntype");
            domain = obj.optString("domain");
            rfUrl = obj.optString("rfUrl");
            logView = obj.optString("log_view");
            cross_browser = TextUtils.isEmpty(obj.optString("cross_browser")) ? "n" : obj.optString("cross_browser");
        } catch (Exception e) {

        }
    }
}
