package com.enliple.keyboard.imgmodule.load.data;

import androidx.annotation.NonNull;
import java.io.IOException;

/**
 * Responsible for rewinding a stream like data types.
 *
 * @param <T> The stream like data type that can be rewound.
 */
public interface DataRewinder<T> {

  /**
   * A factory interface for producing individual {@link
   * com.enliple.keyboard.imgmodule.load.data.DataRewinder}s.
   *
   * @param <T> The type of data that the {@link com.enliple.keyboard.imgmodule.load.data.DataRewinder} will
   *     wrap.
   */
  interface Factory<T> {
    /** Returns a new {@link com.enliple.keyboard.imgmodule.load.data.DataRewinder} wrapping the given data. */
    @NonNull
    DataRewinder<T> build(@NonNull T data);

    /**
     * Returns the class of data this factory can produce {@link
     * com.enliple.keyboard.imgmodule.load.data.DataRewinder}s for.
     */
    @NonNull
    Class<T> getDataClass();
  }

  /**
   * Rewinds the wrapped data back to the beginning and returns the re-wound data (or a wrapper for
   * the re-wound data).
   *
   * @return An object pointing to the wrapped data.
   */
  @NonNull
  T rewindAndGet() throws IOException;

  /**
   * Called when this rewinder is no longer needed and can be cleaned up.
   *
   * <p>The underlying data may still be in use and should not be closed or invalidated.
   */
  void cleanup();
}
