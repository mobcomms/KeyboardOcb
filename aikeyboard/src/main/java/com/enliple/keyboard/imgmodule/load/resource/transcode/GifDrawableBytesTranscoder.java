package com.enliple.keyboard.imgmodule.load.resource.transcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.enliple.keyboard.imgmodule.load.Options;
import com.enliple.keyboard.imgmodule.load.engine.Resource;
import com.enliple.keyboard.imgmodule.load.resource.bytes.BytesResource;
import com.enliple.keyboard.imgmodule.load.resource.gif.GifDrawable;
import com.enliple.keyboard.imgmodule.util.ByteBufferUtil;
import java.nio.ByteBuffer;

/**
 * An {@link com.enliple.keyboard.imgmodule.load.resource.transcode.ResourceTranscoder} that converts {@link
 * com.enliple.keyboard.imgmodule.load.resource.gif.GifDrawable} into bytes by obtaining the original bytes of
 * the GIF from the {@link com.enliple.keyboard.imgmodule.load.resource.gif.GifDrawable}.
 */
public class GifDrawableBytesTranscoder implements ResourceTranscoder<GifDrawable, byte[]> {
  @Nullable
  @Override
  public Resource<byte[]> transcode(
      @NonNull Resource<GifDrawable> toTranscode, @NonNull Options options) {
    GifDrawable gifData = toTranscode.get();
    ByteBuffer byteBuffer = gifData.getBuffer();
    return new BytesResource(ByteBufferUtil.toBytes(byteBuffer));
  }
}
