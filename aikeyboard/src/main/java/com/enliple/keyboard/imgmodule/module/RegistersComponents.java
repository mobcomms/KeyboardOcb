package com.enliple.keyboard.imgmodule.module;

import android.content.Context;
import androidx.annotation.NonNull;

import com.enliple.keyboard.imgmodule.Registry;

/** An internal interface, to be removed when {@link ImageModule}s are removed. */
// Used only in javadocs.
@SuppressWarnings("deprecation")
@Deprecated
interface RegistersComponents {

  /**
   * Lazily register components immediately after the ImageModule singleton is created but before any
   * requests can be started.
   *
   * <p>This method will be called once and only once per implementation.
   *
   * @param context An Application {@link android.content.Context}.
   * @param imageModule The ImageModule singleton that is in the process of being initialized.
   * @param registry An {@link Registry} to use to register components.
   */
  void registerComponents(
          @NonNull Context context, @NonNull com.enliple.keyboard.imgmodule.ImageModule imageModule, @NonNull Registry registry);
}
