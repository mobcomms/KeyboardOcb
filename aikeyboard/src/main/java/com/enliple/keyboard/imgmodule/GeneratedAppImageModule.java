package com.enliple.keyboard.imgmodule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.enliple.keyboard.imgmodule.manager.RequestManagerRetriever;
import com.enliple.keyboard.imgmodule.module.AppImageModule;
import java.util.Set;

/**
 * Allows {@link AppImageModule}s to exclude {@link com.enliple.keyboard.imgmodule.annotation.ImageModule}s to
 * ease the migration from {@link com.enliple.keyboard.imgmodule.ImageModule.annotation.ImageModule}s to ImageModule annotation
 * processing system and optionally provides a {@link
 * com.enliple.keyboard.imgmodule.manager.RequestManagerRetriever.RequestManagerFactory} impl.
 */
abstract class GeneratedAppImageModule extends AppImageModule {
  /** This method can be removed when manifest parsing is no longer supported. */
  @NonNull
  abstract Set<Class<?>> getExcludedModuleClasses();

  @Nullable
  RequestManagerRetriever.RequestManagerFactory getRequestManagerFactory() {
    return null;
  }
}
