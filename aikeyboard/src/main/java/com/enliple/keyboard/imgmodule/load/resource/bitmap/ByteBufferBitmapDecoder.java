package com.enliple.keyboard.imgmodule.load.resource.bitmap;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import com.enliple.keyboard.imgmodule.load.Options;
import com.enliple.keyboard.imgmodule.load.ResourceDecoder;
import com.enliple.keyboard.imgmodule.load.engine.Resource;
import java.io.IOException;
import java.nio.ByteBuffer;

/** Decodes {@link android.graphics.Bitmap Bitmaps} from {@link java.nio.ByteBuffer ByteBuffers}. */
public class ByteBufferBitmapDecoder implements ResourceDecoder<ByteBuffer, Bitmap> {
  private final Downsampler downsampler;

  public ByteBufferBitmapDecoder(Downsampler downsampler) {
    this.downsampler = downsampler;
  }

  @Override
  public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
    return downsampler.handles(source);
  }

  @Override
  public Resource<Bitmap> decode(
      @NonNull ByteBuffer source, int width, int height, @NonNull Options options)
      throws IOException {
    return downsampler.decode(source, width, height, options);
  }
}
