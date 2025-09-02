package com.enliple.keyboard.imgmodule.module;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses {@link ImageModule} references out of the AndroidManifest file.
 */
// Used only in javadoc.
@SuppressWarnings("deprecation")
@Deprecated
public final class ManifestParser {
  private static final String TAG = "ManifestParser";
  private static final String IMAGE_MODULE_VALUE = "ImageModule";

  private final Context context;

  public ManifestParser(Context context) {
    this.context = context;
  }

  @SuppressWarnings("deprecation")
  public List<ImageModule> parse() {
    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(TAG, "Loading Image modules");
    }
    List<ImageModule> modules = new ArrayList<>();
    try {
      ApplicationInfo appInfo =
          context
              .getPackageManager()
              .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      if (appInfo.metaData == null) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
          Log.d(TAG, "Got null app info metadata");
        }
        return modules;
      }
      if (Log.isLoggable(TAG, Log.VERBOSE)) {
        Log.v(TAG, "Got app info metadata: " + appInfo.metaData);
      }
      for (String key : appInfo.metaData.keySet()) {
        if (IMAGE_MODULE_VALUE.equals(appInfo.metaData.get(key))) {
          modules.add(parseModule(key));
          if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Loaded Image module: " + key);
          }
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException("Unable to find metadata to parse ImageModule", e);
    }
    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(TAG, "Finished loading ImageModule");
    }

    return modules;
  }

  @SuppressWarnings("deprecation")
  private static ImageModule parseModule(String className) {
    Class<?> clazz;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Unable to find ImageModule implementation", e);
    }

    Object module = null;
    try {
      module = clazz.getDeclaredConstructor().newInstance();
      // These can't be combined until API minimum is 19.
    } catch (InstantiationException e) {
      throwInstantiateImageModuleException(clazz, e);
    } catch (IllegalAccessException e) {
      throwInstantiateImageModuleException(clazz, e);
    } catch (NoSuchMethodException e) {
      throwInstantiateImageModuleException(clazz, e);
    } catch (InvocationTargetException e) {
      throwInstantiateImageModuleException(clazz, e);
    }

    if (!(module instanceof ImageModule)) {
      throw new RuntimeException("Expected instanceof ImageModule, but found: " + module);
    }
    return (ImageModule) module;
  }

  private static void throwInstantiateImageModuleException(Class<?> clazz, Exception e) {
    throw new RuntimeException("Unable to instantiate ImageModule implementation for " + clazz, e);
  }
}
