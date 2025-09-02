package com.enliple.keyboard.imgmodule.module;

import android.content.Context;
import androidx.annotation.NonNull;
import com.enliple.keyboard.imgmodule.ImageBuilder;

/**
 * Defines a set of dependencies and options to use when initializing ImageModule within an application.
 *
 * <p>There can be at most one {@link AppImageModule} in an application. Only Applications can
 * include a {@link AppImageModule}. Libraries must use {@link LibraryImageModule}.
 *
 * <p>Classes that extend {@link AppImageModule} must be annotated with {@link
 * com.enliple.keyboard.imgmodule.ImageModule} to be processed correctly.
 *
 * <p>Classes that extend {@link AppImageModule} can optionally be annotated with {@link
 * com.enliple.keyboard.imgmodule.ImageModule} to optionally exclude one or more {@link
 * LibraryImageModule} and/or {@link ImageModule} classes.
 *
 * <p>Once an application has migrated itself and all libraries it depends on to use ImageModule
 * annotation processor, {@link AppImageModule} implementations should override {@link
 * #isManifestParsingEnabled()} and return {@code false}.
 */
// Used only in javadoc.
@SuppressWarnings("deprecation")
public abstract class AppImageModule extends LibraryImageModule implements AppliesOptions {
  /**
   * Returns {@code true} if ImageModule should check the AndroidManifest for {@link ImageModule}s.
   *
   * <p>Implementations should return {@code false} after they and their dependencies have migrated
   * to ImageModule annotation processor.
   *
   * <p>Returns {@code true} by default.
   */
  public boolean isManifestParsingEnabled() {
    return true;
  }

  @Override
  public void applyOptions(@NonNull Context context, @NonNull ImageBuilder builder) {
    // Default empty impl.
  }
}
