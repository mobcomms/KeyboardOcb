package com.enliple.keyboard.mobonAD.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;


import com.enliple.keyboard.ui.common.LogPrint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yikim on 2018-02-09.
 */

public class IntegrationHelper {
    private static final String TAG = "IntegrationHelper";
    private static final String[] SDK_COMPATIBILITY_VERSION_ARR = new String[]{"4.0"};
    public static HashMap<String, Boolean> verifiedAdapters;
    private static final String ADFIT = "Adfit";
    private static final String MOPUB = "Mopub";
    private static final String MOBON = "mobon";
    private static final String ADMIXER = "AdMixer";
    private static final String CRITEO = "Criteo";
    private static final String APPBACON = "appbacon";
    private static final String PERPL = "Perpl";
    private static final String MOBONSCRIPT = "mbadapter";
    private static final String SSPSCRIPT = "mbmixadapter";

    public IntegrationHelper() {
    }

    public static void validateIntegration(Context context) {

        List<String> generalPermissions = Arrays.asList("android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE");

        final AdapterObject adfitAdapter = new AdapterObject(ADFIT, true);
        final AdapterObject admixerAdapter = new AdapterObject(ADMIXER, true);
        final AdapterObject criteoAdapter = new AdapterObject(CRITEO, true);
        final AdapterObject appbacon = new AdapterObject(APPBACON, true);
        final AdapterObject perplAdapter = new AdapterObject(PERPL, true);
        final AdapterObject mobonScirptAdapter = new AdapterObject(MOBONSCRIPT, true);
        final AdapterObject sspScirptAdapter = new AdapterObject(SSPSCRIPT, true);

        ArrayList<AdapterObject> adapters = new ArrayList<AdapterObject>() {
            {
                this.add(adfitAdapter);
                this.add(admixerAdapter);
                this.add(criteoAdapter);
                this.add(appbacon);
                this.add(perplAdapter);
                this.add(mobonScirptAdapter);
                this.add(sspScirptAdapter);
            }
        };

        verifiedAdapters = new HashMap<String, Boolean>();
        Iterator iter = adapters.iterator();

        while (iter.hasNext()) {
            AdapterObject adapterObject = (AdapterObject) iter.next();
            boolean verified = true;
            LogPrint.d("IntegrationHelper" + "--------------- " + adapterObject.getName() + " --------------");
            if (adapterObject.isAdapter() && !validateAdapter(adapterObject)) {
                verified = false;
            }

            if (verified) {
                if (adapterObject.getSdkName() != null && !validateSdk(adapterObject.getSdkName())) {
                    verified = false;
                }

//                if(adapterObject.getPermissions() != null && !validatePermissions(activity, adapterObject)) {
//                    verified = false;
//                }
//
//                if(adapterObject.getActivities() != null && !validateActivities(activity, adapterObject.getActivities())) {
//                    verified = false;
//                }
//
//                if(adapterObject.getExternalLibraries() != null && !validateExternalLibraries(adapterObject.getExternalLibraries())) {
//                    verified = false;
//                }
//
//                if(adapterObject.getBroadcastReceivers() != null && !validateBroadcastReceivers(activity, adapterObject.getBroadcastReceivers())) {
//                    verified = false;
//                }
            }

            if (verified) {
                LogPrint.d("IntegrationHelper>>>> " + adapterObject.getName() + " - VERIFIED");
            } else {
                LogPrint.d("IntegrationHelper>>>> " + adapterObject.getName() + " - NOT VERIFIED");
            }

            verifiedAdapters.put(adapterObject.getName(), verified);
            adapterObject.setIsVerified(verified);
        }

        //validateGooglePlayServices(activity);
    }

    public static boolean verifiedAdapter(String sdkName) {

        if (TextUtils.isEmpty(sdkName))
            return false;

        if (sdkName.toLowerCase().equals(ADFIT.toLowerCase()) || sdkName.toLowerCase().contains(ADFIT.toLowerCase()))
            sdkName = ADFIT;
        else if (sdkName.toLowerCase().equals(ADMIXER.toLowerCase()) || sdkName.toLowerCase().contains(ADMIXER.toLowerCase()))
            sdkName = ADMIXER;
        else if (sdkName.toLowerCase().equals(CRITEO.toLowerCase()) || sdkName.toLowerCase().contains(CRITEO.toLowerCase()))
            sdkName = CRITEO;
        else if (sdkName.toLowerCase().equals(PERPL.toLowerCase()) || sdkName.toLowerCase().contains(PERPL.toLowerCase()))
            sdkName = PERPL;
        else if (sdkName.toLowerCase().equals(MOBON.toLowerCase())) {
            sdkName = MOBON;
            return true;
        }else if (sdkName.toLowerCase().equals(MOBONSCRIPT.toLowerCase())) {
            sdkName = MOBONSCRIPT;
            return true;
        } else if (sdkName.toLowerCase().equals(SSPSCRIPT.toLowerCase())) {
            sdkName = SSPSCRIPT;
            return true;
        }


        Object obj = verifiedAdapters.get(sdkName);

        if (obj != null)
            return (boolean) obj;

        return false;
    }

    public static String getAdapaterName(String sdkName) {

        if (sdkName.toLowerCase().equals(ADFIT.toLowerCase()) || sdkName.toLowerCase().contains(ADFIT.toLowerCase()))
            sdkName = ADFIT;
        else if (sdkName.toLowerCase().equals(ADMIXER.toLowerCase()) || sdkName.toLowerCase().contains(ADMIXER.toLowerCase()))
            sdkName = ADMIXER;
        else if (sdkName.toLowerCase().equals(CRITEO.toLowerCase()) || sdkName.toLowerCase().contains(CRITEO.toLowerCase()))
            sdkName = CRITEO;
        else if (sdkName.toLowerCase().equals(PERPL.toLowerCase()) || sdkName.toLowerCase().contains(PERPL.toLowerCase()))
            sdkName = PERPL;
        else if (sdkName.toLowerCase().equals(APPBACON.toLowerCase()) || sdkName.toLowerCase().contains(APPBACON.toLowerCase()))
            sdkName = APPBACON;
        else if (sdkName.toLowerCase().equals(MOBONSCRIPT.toLowerCase()))
            sdkName = MOBONSCRIPT;
        else if (sdkName.toLowerCase().equals(SSPSCRIPT.toLowerCase()))
            sdkName = SSPSCRIPT;

        return sdkName;
    }

    public static void getAdapterVersion(Context context) {
        for (Map.Entry<String, Boolean> elem : verifiedAdapters.entrySet()) {
            if (elem.getValue()) {
                AdapterObject adapter = new AdapterObject(elem.getKey(), true);
                adapter.onCreate(context, adapter.getAdapterPackageName());
                adapter.getVersion();
            }
        }
    }

//    private static void validateGooglePlayServices(final Activity activity) {
//        String mGooglePlayServicesMetaData = "com.google.android.gms.version";
//        String mGooglePlayServices = "Google Play Services";
//        Thread thread = new Thread() {
//            public void run() {
//                try {
//                    Log.w("IntegrationHelper", "--------------- Google Play Services --------------");
//                    PackageManager packageManager = activity.getPackageManager();
//                    ApplicationInfo ai = packageManager.getApplicationInfo(activity.getPackageName(), 128);
//                    Bundle bundle = ai.metaData;
//                    boolean exists = bundle.containsKey("com.google.android.gms.version");
//                    if(exists) {
//                        IntegrationHelper.validationMessageIsPresent("Google Play Services", true);
//                        String gaid = activitygetAdvertiserId(activity);
//                        if(!TextUtils.isEmpty(gaid)) {
//                            Log.i("IntegrationHelper", "GAID is: " + gaid + " (use this for test devices)");
//                        }
//                    } else {
//                        IntegrationHelper.validationMessageIsPresent("Google Play Services", false);
//                    }
//                } catch (Exception var6) {
//                    IntegrationHelper.validationMessageIsPresent("Google Play Services", false);
//                }
//
//            }
//        };
//        thread.start();
//    }

    private static boolean validateAdapter(AdapterObject adapter) {
        boolean result = false;
        try {
            if (adapter.getName().toLowerCase().equals(APPBACON.toLowerCase()) || adapter.getName().toLowerCase().equals(MOBONSCRIPT.toLowerCase()) || adapter.getName().toLowerCase().equals(SSPSCRIPT.toLowerCase()))
                return true;

            Class localClass = Class.forName(adapter.getAdapterPackageName());
            result = true;
        } catch (ClassNotFoundException var8) {
            validationMessageIsPresent("Adapter", false);
        }

        if (result) {
            validationMessageIsVerified("Adapter", true);
        }

        return result;
    }

    private static boolean validateSdk(String sdkName) {
        boolean result = false;

        try {
            Class localClass = Class.forName(sdkName);
            result = true;
            validationMessageIsPresent("SDK", true);
        } catch (ClassNotFoundException var3) {
            validationMessageIsPresent("SDK", false);
        }

        return result;
    }

    private static boolean validateActivities(Activity activity, List<String> activities) {
        boolean result = true;
        PackageManager packageManager = activity.getPackageManager();
        Iterator var4 = activities.iterator();

        while (var4.hasNext()) {
            String act = (String) var4.next();

            try {
                Class localClass = Class.forName(act);
                Intent intent = new Intent(activity, localClass);
                List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.size() > 0) {
                    validationMessageIsPresent(act, true);
                } else {
                    result = false;
                    validationMessageIsPresent(act, false);
                }
            } catch (ClassNotFoundException var9) {
                result = false;
                validationMessageIsPresent(act, false);
            }
        }

        return result;
    }

    private static boolean validatePermissions(Activity activity, AdapterObject adapterObject) {
        List<String> permissions = adapterObject.getPermissions();
        Map<String, Integer> permissionsToMaxSdkVersionMap = adapterObject.getPermissionToMaxSdkVersion();
        Map<String, Integer> permissionsToMinSdkVersionMap = adapterObject.getPermissionToMinSdkVersion();
        int currentSdkVersion = Build.VERSION.SDK_INT;
        boolean result = true;
        PackageManager packageManager = activity.getPackageManager();
        Iterator var8 = permissions.iterator();

        while (true) {
            String permission;
            int minSdkVersion;
            do {
                do {
                    if (!var8.hasNext()) {
                        return result;
                    }

                    permission = (String) var8.next();
                    if (permissionsToMaxSdkVersionMap == null || !permissionsToMaxSdkVersionMap.containsKey(permission)) {
                        break;
                    }

                    minSdkVersion = permissionsToMaxSdkVersionMap.get(permission).intValue();
                } while (minSdkVersion < currentSdkVersion);

                if (permissionsToMinSdkVersionMap == null || !permissionsToMinSdkVersionMap.containsKey(permission)) {
                    break;
                }

                minSdkVersion = permissionsToMinSdkVersionMap.get(permission).intValue();
            } while (minSdkVersion > currentSdkVersion);

            minSdkVersion = packageManager.checkPermission(permission, activity.getPackageName());
            if (minSdkVersion == 0) {
                validationMessageIsPresent(permission, true);
            } else {
                result = false;
                validationMessageIsPresent(permission, false);
            }
        }
    }

    private static boolean validateExternalLibraries(ArrayList<Pair<String, String>> externalLibraries) {
        boolean result = true;
        Iterator var2 = externalLibraries.iterator();

        while (var2.hasNext()) {
            Pair externalLibrary = (Pair) var2.next();

            try {
                Class localClass = Class.forName((String) externalLibrary.first);
                validationMessageIsPresent((String) externalLibrary.second, true);
            } catch (ClassNotFoundException var5) {
                result = false;
                validationMessageIsPresent((String) externalLibrary.second, false);
            }
        }

        return result;
    }

    private static boolean validateBroadcastReceivers(Activity activity, List<String> broadcastReceivers) {
        boolean result = true;
        PackageManager packageManager = activity.getPackageManager();
        Iterator var4 = broadcastReceivers.iterator();

        while (var4.hasNext()) {
            String broadcastReceiver = (String) var4.next();

            try {
                Class localClass = Class.forName(broadcastReceiver);
                Intent intent = new Intent(activity, localClass);
                List<ResolveInfo> list = packageManager.queryBroadcastReceivers(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.size() > 0) {
                    validationMessageIsPresent(broadcastReceiver, true);
                } else {
                    result = false;
                    validationMessageIsPresent(broadcastReceiver, false);
                }
            } catch (ClassNotFoundException var9) {
                result = false;
                validationMessageIsPresent(broadcastReceiver, false);
            }
        }

        return result;
    }

    private static void validationMessageIsPresent(String paramToValidate, boolean successful) {
        if (successful) {
            LogPrint.d("IntegrationHelper " + paramToValidate + " - VERIFIED");
        } else {
            LogPrint.d("IntegrationHelper " + paramToValidate + " - MISSING");
        }

    }

    private static void validationMessageIsVerified(String paramToValidate, boolean successful) {
        if (successful) {
            LogPrint.d("IntegrationHelper " + paramToValidate + " - VERIFIED");
        } else {
            LogPrint.d("IntegrationHelper " + paramToValidate + " - NOT VERIFIED");
        }

    }
}
