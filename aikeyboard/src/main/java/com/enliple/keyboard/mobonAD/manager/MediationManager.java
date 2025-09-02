package com.enliple.keyboard.mobonAD.manager;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.enliple.keyboard.mobonAD.MobonBannerType;
import com.enliple.keyboard.mobonAD.MobonKey;
import com.enliple.keyboard.mobonAD.MobonSimpleSDK;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yikim on 2018-02-13.
 */

public class MediationManager {
    private final Context mContext;
    private String AD_TYPE;

    private ArrayList<AdapterObject> mAdapterList;
    private boolean isLoadShow;
    private AdapterObject mAapter;
    private int mAdapterIndex = 0;
    private iMobonMediationCallback mCallback;

    public MediationManager(Context _context, JSONObject adData, String adType) {
        this.mContext = _context;
        AD_TYPE = adType;
        init(adData);
    }

    public void init(JSONObject obj) {
        mAdapterIndex = 0;
        if (mAdapterList == null)
            mAdapterList = new ArrayList<>();

        try {
            JSONArray array = obj.optJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject medi = array.getJSONObject(i);
                String adapterName = medi.optString("name");
                String unitId = medi.optString("unitid");
                String mediaKey = medi.optString("mediaKey");

                boolean isCreated = false;
                for (AdapterObject adapter : mAdapterList) {
                    if (adapter.getUnitId().equals(unitId) && adapter.getMediaKey().equals(mediaKey)) {
                        isCreated = true;
                        if (adapterName.equals("mobon"))
                            adapter.setAdData(medi.optString("data"));
                        break;
                    }
                }

                if (IntegrationHelper.verifiedAdapter(adapterName) && !TextUtils.isEmpty(unitId) && !TextUtils.isEmpty(AD_TYPE) && !isCreated) {

                    if (MobonKey.INTERSTITIAL_TYPE.FULL.toString().equals(AD_TYPE))
                        AD_TYPE = MobonBannerType.INTERSTITIAL;
                    else if (MobonKey.INTERSTITIAL_TYPE.NORMAL.toString().equals(AD_TYPE))
                        AD_TYPE = MobonBannerType.INTERSTITIAL_POPUP;

                    mAdapterList.add(new AdapterObject(IntegrationHelper.getAdapaterName(adapterName), mediaKey, unitId, AD_TYPE, medi.optString("data"), false, true));
                    LogPrint.d("AdapterList add : " + adapterName + " : " + unitId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean LoadMediation(final iMobonMediationCallback callback) {
        mCallback = callback;
        if (mAdapterList.size() > mAdapterIndex) {
            mAapter = mAdapterList.get(mAdapterIndex++);
            if (mAapter.getName().toLowerCase().equals("mobon")) {
                callback.onLoadedAdData(mAapter.getAdData(), mAapter);
                return true;
            } else if (mAapter.getName().toLowerCase().equals("appbacon")) {
                String data = null;
                for (AdapterObject obj : mAdapterList) {
                    if (obj.getName().toLowerCase().equals("mobon"))
                        data = obj.getAdData();
                }
                callback.onLoadedAdData(data, mAapter);
                return true;
            } else if (mAapter.getName().toLowerCase().equals("mbadapter") || mAapter.getName().toLowerCase().equals("mbmixadapter")) {
                String data = null;
                for (AdapterObject obj : mAdapterList) {
                    if (obj.getName().toLowerCase().equals("mobon"))
                        data = obj.getAdData();
                }
                callback.onLoadedAdData(data, mAapter);
                return true;
            }

            if (!mAapter.isCreated()) {
                mAapter.onCreate(mContext, mAapter.getAdapterPackageName());

                if (mAapter.getName().toLowerCase().contains("criteo")) {
                    Application app = MobonSimpleSDK.get(mContext).getApplication();
                    if (app != null)
                        mAapter.setApplication(app);
                    else {
                        LogPrint.d(mAapter.getName() + " Adapter Application NULL!!!!");
                        if (mAdapterList.size() > 0)
                            LoadMediation(callback);
                        else {
                            callback.onAdFailedToLoad(mAapter.getName() + " Adapter Application NULL!!!!");
                        }

                        return true;
                    }

                }
                mAapter.setLog(LogPrint.debug);
            }

            if (!mAapter.init(mAapter.getName())) {
                if (mAdapterList.size() > 0)
                    LoadMediation(callback);
                else
                    callback.onAdFailedToLoad(mAapter.getName() + " init() error");
                return true;
            }
            mAapter.setAdListner(new MediationAdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    if (isLoadShow) {
                        isLoadShow = false;
                        mAapter.close();
                    }
                    LogPrint.d(mAapter.getName() + " onAdClosed");
                    callback.onAdClosed();
                    //  mAdapterList.onDestory();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    LogPrint.d(mAapter.getName() + " ADLoaded");
//                    if (isLoadShow) {
//                        isLoadShow = false;
//                        adapter.show();
//                    }
                    if (!(mAapter.getName().equalsIgnoreCase("admixer") && !MobonBannerType.ENDING.equals(AD_TYPE) && !MobonBannerType.INTERSTITIAL.equals(AD_TYPE) && !MobonBannerType.INTERSTITIAL_POPUP.equals(AD_TYPE) && !MobonBannerType.MEDIATION_ADFIT_SMALL.equals(AD_TYPE)))
                        callback.onAdAdapter(mAapter);
                }

                @Override
                public void onAdCancel() {
                    super.onAdCancel();

                    if (isLoadShow) {
                        isLoadShow = false;
                        mAapter.close();
                    }
                    LogPrint.d(mAapter.getName() + " onAdCancel");
                    callback.onAdCancel();
                }

                @Override
                public void onAdFailedToLoad(String errorMsg) {
                    super.onAdFailedToLoad(errorMsg);
                    //  mAdapterList.onDestory();
                    LogPrint.d(mAapter.getName() + "  AD FailLoad: " + errorMsg);

                    //   mAapter = null;

                    if (mAdapterList.size() > 0)
                        LoadMediation(callback);
                    else
                        callback.onAdFailedToLoad(errorMsg);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();

                    LogPrint.d(mAapter.getName() + "  ADClicked");
                    callback.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    LogPrint.d(mAapter.getName() + "  onAdImpression");
                    callback.onAdImpression();
                }

                @Override
                public void onAppFinish() {
                    super.onAppFinish();
                    callback.onAppFinish();
                }
            });

            mAapter.load();

            if (mAapter.getName().equalsIgnoreCase("admixer") && !MobonBannerType.ENDING.equals(AD_TYPE) && !MobonBannerType.INTERSTITIAL.equals(AD_TYPE) && !MobonBannerType.INTERSTITIAL_POPUP.equals(AD_TYPE) && !MobonBannerType.MEDIATION_ADFIT_SMALL.equals(AD_TYPE))
                callback.onAdAdapter(mAapter);

        } else
            callback.onAdFailedToLoad(MobonKey.NOFILL);
        return false;
    }

    public boolean next() {
        if (mCallback != null) {
            LoadMediation(mCallback);
            return true;
        } else
            return false;
    }

}
