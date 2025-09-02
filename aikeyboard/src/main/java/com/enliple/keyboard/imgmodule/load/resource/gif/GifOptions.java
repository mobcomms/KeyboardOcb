package com.enliple.keyboard.imgmodule.load.resource.gif;

import com.enliple.keyboard.imgmodule.load.DecodeFormat;
import com.enliple.keyboard.imgmodule.load.Option;
import com.enliple.keyboard.imgmodule.load.Options;
import com.enliple.keyboard.imgmodule.load.ResourceDecoder;

/** Options related to decoding GIFs. */
public final class GifOptions {

  /**
   * Indicates the {@link com.enliple.keyboard.imgmodule.load.DecodeFormat} that will be used in conjunction
   * with the particular GIF to determine the {@link android.graphics.Bitmap.Config} to use when
   * decoding frames of GIFs.
   */
  public static final Option<DecodeFormat> DECODE_FORMAT =
      Option.memory(
          "com.enliple.keyboard.imgmodule.load.resource.gif.GifOptions.DecodeFormat", DecodeFormat.DEFAULT);

  /**
   * If set to {@code true}, disables the GIF {@link com.enliple.keyboard.imgmodule.load.ResourceDecoder}s
   * ({@link ResourceDecoder#handles(Object, Options)} will return {@code false}). Defaults to
   * {@code false}.
   */
  public static final Option<Boolean> DISABLE_ANIMATION =
      Option.memory("com.enliple.keyboard.imgmodule.load.resource.gif.GifOptions.DisableAnimation", false);

  private GifOptions() {
    // Utility class.
  }
}
