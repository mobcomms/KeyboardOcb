package com.enliple.keyboard.imgmodule.module;

import android.content.Context;
import androidx.annotation.NonNull;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.Registry;

/**
 * Registers a set of components to use when initializing ImageModule within an app when ImageModule
 * annotation processor is used.
 *
 * <p>Any number of LibraryImageModule can be contained within any library or application.
 *
 * <p>LibraryImageModule are called in no defined order. If LibraryImageModule within an
 * application conflict, {@link AppImageModule}s can use the {@link
 * com.enliple.keyboard.imgmodule.ImageModule.annotation.Excludes} annotation to selectively remove one or more of the
 * conflicting modules.
 */
@SuppressWarnings("deprecation")
public abstract class LibraryImageModule implements RegistersComponents {
  @Override
  public void registerComponents(
          @NonNull Context context, @NonNull ImageModule imageModule, @NonNull Registry registry) {
    // Default empty impl.
  }
}
