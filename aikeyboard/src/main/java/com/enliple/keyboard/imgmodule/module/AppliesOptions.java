package com.enliple.keyboard.imgmodule.module;

import android.content.Context;
import androidx.annotation.NonNull;
import com.enliple.keyboard.imgmodule.ImageBuilder;

/** An internal interface, to be removed when {@link ImageModule}s are removed. */
@Deprecated
interface AppliesOptions {
  /**
   * Lazily apply options to a {@link ImageBuilder} immediately before the ImageModule
   * singleton is created.
   *
   * <p>This method will be called once and only once per implementation.
   *
   * @param context An Application {@link android.content.Context}.
   * @param builder The {@link ImageBuilder} that will be used to create ImageModule.
   */
  void applyOptions(@NonNull Context context, @NonNull ImageBuilder builder);
}
