package com.enliple.keyboard.mobonAD.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.enliple.keyboard.mobonAD.MobonBannerType;
import com.enliple.keyboard.mobonAD.MobonKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yikim on 2018-02-09.
 */

public class AdapterObject {
    private String mName;
    private List<String> mPermissions;
    private List<String> mActivities;
    private ArrayList<Pair<String, String>> mExternalLibraries;
    private List<String> mBroadcastReceivers;
    private String mSdkName;
    private String mAdapterPackageName;
    private boolean mIsAdapter;
    private Map<String, Integer> mPermissionToMaxSdkVersion;
    private Map<String, Integer> mPermissionToMinSdkVersion;
    private boolean mIsVerified;
    private Object instance;
    private Object mAdView;
    private View.OnClickListener mAdListner;
    private boolean isAdLoaded;
    private boolean isShowed;
    private String mMediaKey;
    private String mUnitId;
    private int mAdType;
    private String mAdData; // json ad data

    public AdapterObject(String name, boolean isAdapter) {
        this.mName = name;
        //this.mActivities = activities;
        this.mIsAdapter = isAdapter;
        if (isAdapter) {
            if (name.toLowerCase().equals("criteo"))
                this.mAdapterPackageName = "com.criteo.publisher.CriteoAdapter";
            else
                this.mAdapterPackageName = "com.mobon." + name.toLowerCase() + "_sdk." + name + "Adapter";
        }
        this.isAdLoaded = false;
    }

    public AdapterObject(String name, String mediaKey, String unitId, String AdType, String adData, boolean isTestMode, boolean isAdapter) {

        this.mName = name;
        if (TextUtils.equals(name, "mobon") || TextUtils.equals(name, "mbadapter") || TextUtils.equals(name, "mbmixadapter")) {
            mAdData = adData;
            mUnitId = unitId;
        } else {
            this.mIsAdapter = isAdapter;
            if (isAdapter) {
                if (name.toLowerCase().equals("criteo"))
                    this.mAdapterPackageName = "com.criteo.publisher.CriteoAdapter";
                else
                    this.mAdapterPackageName = "com.mobon." + name.toLowerCase() + "_sdk." + name + "Adapter";
            }

            this.isAdLoaded = false;
            mMediaKey = mediaKey;
            mUnitId = unitId;
            if (TextUtils.equals(AdType, MobonBannerType.BANNER_320x50))
                mAdType = MediationAdSize.BANNER_320_50;
            else if (TextUtils.equals(AdType, MobonBannerType.BANNER_320x100))
                mAdType = MediationAdSize.BANNER_320_100;
            else if (TextUtils.equals(AdType, MobonBannerType.BANNER_300x250))
                mAdType = MediationAdSize.BANNER_300_250;
            else if (TextUtils.equals(AdType, MobonBannerType.ENDING))
                mAdType = MediationAdSize.ENDING;
            else if (TextUtils.equals(AdType, MobonBannerType.INTERSTITIAL) || TextUtils.equals(AdType, MobonKey.INTERSTITIAL_TYPE.FULL.toString()))
                mAdType = MediationAdSize.INTERSTITIAL;
            else if (TextUtils.equals(AdType, MobonBannerType.INTERSTITIAL_POPUP) || TextUtils.equals(AdType, MobonBannerType.MEDIATION_ADFIT_SMALL))
                mAdType = MediationAdSize.INTERSTITIAL_POPUP;
            else
                mAdType = MediationAdSize.BANNER_320_50;
        }
        if (isTestMode)
            setTestMode(isTestMode);
    }

    public String getName() {
        return this.mName;
    }

    void setName(String name) {
        this.mName = name;
    }

    List<String> getPermissions() {
        return this.mPermissions;
    }

    void setPermissions(List<String> permissions) {
        this.mPermissions = permissions;
    }

    List<String> getActivities() {
        return this.mActivities;
    }

    void setActivities(List<String> activities) {
        this.mActivities = activities;
    }

    ArrayList<Pair<String, String>> getExternalLibraries() {
        return this.mExternalLibraries;
    }

    void setExternalLibraries(ArrayList<Pair<String, String>> externalLibraries) {
        this.mExternalLibraries = externalLibraries;
    }

    public String getAdData() {
        return mAdData;
    }

    public void setAdData(String _data) {
        mAdData = _data;
    }

    String getSdkName() {
        return this.mSdkName;
    }

    void setSdkName(String sdkName) {
        this.mSdkName = sdkName;
    }

    public String getAdapterPackageName() {
        return this.mAdapterPackageName;
    }

    boolean isAdapter() {
        return this.mIsAdapter;
    }

    public void setIsVerified(boolean _is) {
        mIsVerified = _is;
    }

    public boolean isVerified() {
        return mIsVerified;
    }

    public String getUnitId() {
        return mUnitId == null ? "" : mUnitId;
    }

    public String getMediaKey() {
        return mMediaKey == null ? "" : mMediaKey;
    }

    List<String> getBroadcastReceivers() {
        return this.mBroadcastReceivers;
    }

    void setBroadcastReceivers(List<String> broadcastReceivers) {
        this.mBroadcastReceivers = broadcastReceivers;
    }

    public Map<String, Integer> getPermissionToMaxSdkVersion() {
        return this.mPermissionToMaxSdkVersion;
    }

    public void setPermissionToMaxSdkVersion(Map<String, Integer> permissionToMaxSdkVersion) {
        this.mPermissionToMaxSdkVersion = permissionToMaxSdkVersion;
    }

    public Map<String, Integer> getPermissionToMinSdkVersion() {
        return this.mPermissionToMinSdkVersion;
    }

    public void setPermissionToMinSdkVersion(Map<String, Integer> permissionToMinSdkVersion) {
        this.mPermissionToMinSdkVersion = permissionToMinSdkVersion;
    }

    public void onCreate(Context context, String className) {
        try {
            if (mName.toLowerCase().contains("admixer")) {
                instance = Reflection.instantiateClassWithConstructor(
                        className, Object.class,
                        new Class[]{Activity.class},
                        new Object[]{context});
            } else
                instance = Reflection.instantiateClassWithConstructor(
                        className, Object.class,
                        new Class[]{Context.class},
                        new Object[]{context});

        } catch (Exception e) {
            System.out.println("AdapterObject oncrete() : " + e.getMessage());
        }
    }

    public boolean setApplication(Application application) {
        if (instance != null) {
            try {
                new Reflection.MethodBuilder(instance, "setApplication")
                        .addParam(Application.class, application)
                        .execute();

            } catch (Exception e) {
                System.out.println("AdapterObject setApplication() : " + e.getCause());
                e.printStackTrace();
                return false;
            }
        } else
            return false;

        return true;
    }


    public boolean init(final String _adapterName) {
        if (instance != null) {
            try {
                if (_adapterName.toLowerCase().equalsIgnoreCase("admixer") || _adapterName.toLowerCase().equalsIgnoreCase("criteo") || _adapterName.toLowerCase().equalsIgnoreCase("perpl")) {
                    new Reflection.MethodBuilder(instance, "init")
                            .addParam(String.class, mMediaKey)
                            .addParam(String.class, mUnitId)
                            .addParam(int.class, mAdType)
                            .execute();
                } else {
                    new Reflection.MethodBuilder(instance, "init")
                            .addParam(String.class, mUnitId)
                            .addParam(int.class, mAdType)
                            .execute();
                }
            } catch (Exception e) {
                System.out.println("AdapterObject init() : " + e.getCause());
                e.printStackTrace();
                return false;
            }
        } else
            return false;

        return true;
    }

    public void init(String unit_id, int AdSize, int width, int height) {
        if (instance != null) {
            try {
                new Reflection.MethodBuilder(instance, "init")
                        .addParam(String.class, unit_id)
                        .addParam(int.class, AdSize)
                        .execute();


            } catch (Exception e) {
                System.out.println("AdapterObject init() : " + e.getMessage());
            }
        }
    }

    public void setLog(boolean is) {
        if (instance != null) {
            try {
                new Reflection.MethodBuilder(instance, "setLog")
                        .addParam(boolean.class, is)
                        .execute();


            } catch (Exception e) {
                System.out.println("AdapterObject setLog() : " + e.getMessage());
            }
        }
    }

    public void getVersion() {
        if (instance != null) {
            try {
                new Reflection.MethodBuilder(instance, "getVersion")
                        .execute();


            } catch (Exception e) {
                System.out.println("AdapterObject getVersion() : " + e.getMessage());
            }
        }
    }

    public void setAdListner(final MediationAdListener _listner) {
        if (instance != null) {
            try {
                mAdListner = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag() != null) {
                            try {
                                JSONObject obj = new JSONObject(v.getTag().toString());
                                int code = obj.optInt("code");
                                String errMsg = obj.optString("msg");

                                switch (code) {
                                    case MediationAdCode.AD_LISTENER_CODE_ERROR:
                                        isAdLoaded = false;
                                        _listner.onAdFailedToLoad(errMsg);
                                        break;

                                    case MediationAdCode.AD_LISTENER_CODE_AD_LOAD:
                                        isAdLoaded = true;
                                        _listner.onAdLoaded();
                                        break;
                                    case MediationAdCode.AD_LISTENER_CODE_AD_CLICK:
                                        _listner.onAdClicked();
                                        break;
                                    case MediationAdCode.AD_LISTENER_CODE_IMPRESSION:
                                        _listner.onAdImpression();
                                        break;
                                    case MediationAdCode.AD_LISTENER_CODE_AD_LEFT:

                                        break;
                                    case MediationAdCode.AD_LISTENER_CODE_AD_CLOSE:
                                        isAdLoaded = false;
                                        isShowed = false;
                                        _listner.onAdClosed();
                                        break;
                                    case MediationAdCode.AD_LISTENER_CODE_AD_CANCEL:
                                        isAdLoaded = false;
                                        isShowed = false;
                                        _listner.onAdCancel();
                                        break;
                                    case MediationAdCode.AD_LISTENER_CODE_AD_DISPLAYED:
                                        isAdLoaded = false;
                                        _listner.onAdDisplayed();
                                        break;
                                    case MediationAdCode.AD_LISTENER_CODE_AD_DISMISSED:
                                        isAdLoaded = false;
                                        isShowed = false;
                                        _listner.onAdDismissed();
                                        break;
                                    case MediationAdCode.AD_LISTENER_CODE_FINISH_CLICK:
                                        isAdLoaded = false;
                                        _listner.onAppFinish();
                                        break;
                                    default:
                                        isAdLoaded = false;
                                        _listner.onAdFailedToLoad("wrong data.. check mobon sdk version");
                                        break;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };
                new Reflection.MethodBuilder(instance, "setAdListener")
                        .addParam(View.OnClickListener.class, mAdListner)
                        .execute();
            } catch (Exception e) {
                System.out.println("AdapterObject setAdListner() : " + e.getMessage());
            }


        }
    }

    public Object getAdView() {
        try {
            if (mAdType == MediationAdSize.BANNER_320_50 || mAdType == MediationAdSize.BANNER_320_100 || mAdType == MediationAdSize.BANNER_300_250)
                mAdView = new Reflection.MethodBuilder(instance, "getBannerView")
                        .execute();
            else if (mAdType == MediationAdSize.NATIVE)
                mAdView = new Reflection.MethodBuilder(instance, "getNativeView")
                        .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mAdView;
    }

    public Object getNativeView() {
        if (instance != null) {
            try {
                mAdView = new Reflection.MethodBuilder(instance, "getNativeView")
                        .execute();
            } catch (Exception e) {
                System.out.println("AdapterObject getNativeView() : " + e.getMessage());
            }
        }
        return mAdView;
    }

    public void setTestMode(boolean isTest) {
        if (instance != null) {
            try {
                new Reflection.MethodBuilder(instance, "setTestMode")
                        .addParam(boolean.class, isTest)
                        .execute();
            } catch (Exception e) {
                System.out.println("AdapterObject setTestMode() : " + e.getMessage());
            }
        }
    }

    public void close() {
        if (instance != null) {
            try {
                new Reflection.MethodBuilder(instance, "close")
                        .execute();
            } catch (Exception e) {
                System.out.println("AdapterObject setTestMode() : " + e.getMessage());
            }
        }
    }

    public void load() {
        isShowed = false;
        if (instance != null) {
            try {
                new Reflection.MethodBuilder(instance, "loadAd")
                        .execute();
            } catch (Exception e) {
                System.out.println("AdapterObject load() : " + e.getMessage());
            }
        }
    }

    public boolean show() {
        if (instance != null) {
            try {
                return (boolean) new Reflection.MethodBuilder(instance, "show")
                        .execute();
            } catch (Exception e) {
                System.out.println("AdapterObject show() : " + e.getMessage());
            }
        }
        return false;
    }

    public boolean isAdLoad() {
        if (instance != null) {
            try {
                return (boolean) new Reflection.MethodBuilder(instance, "isLoaded")
                        .execute();
            } catch (Exception e) {
                System.out.println("AdapterObject show() : " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean isShow() {
        return isShowed;
    }

    public void onDestory() {
        if (instance != null) {
            try {
                mAdListner = null;
                new Reflection.MethodBuilder(instance, "destroy")
                        .execute();
                mAdView = null;
                instance = null;
            } catch (Exception e) {
                System.out.println("AdapterObject onDestory() : " + e.getMessage());
            }
        }
    }

    public boolean isCreated() {
        return instance != null;
    }
}
