package com.enliple.keyboard.imgmodule.load.resource.bitmap;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.load.engine.bitmap_recycle.BitmapPool;

/**
 * An {@link com.enliple.keyboard.imgmodule.load.ResourceDecoder} that can decode a thumbnail frame {@link
 * android.graphics.Bitmap} from a {@link android.os.ParcelFileDescriptor} containing a video.
 *
 * @see android.media.MediaMetadataRetriever
 * @deprecated Use {@link VideoDecoder#parcel(BitmapPool)} instead. This class may be removed and
 *     {@link VideoDecoder} may become final in a future version of ImageModule.
 */
@Deprecated
public class VideoBitmapDecoder extends VideoDecoder<ParcelFileDescriptor> {

  @SuppressWarnings("unused")
  public VideoBitmapDecoder(Context context) {
    this(ImageModule.get(context).getBitmapPool());
  }

  // Public API
  @SuppressWarnings("WeakerAccess")
  public VideoBitmapDecoder(BitmapPool bitmapPool) {
    super(bitmapPool, new ParcelFileDescriptorInitializer());
  }
}
