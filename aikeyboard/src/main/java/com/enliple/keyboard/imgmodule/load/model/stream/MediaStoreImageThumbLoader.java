package com.enliple.keyboard.imgmodule.load.model.stream;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.enliple.keyboard.imgmodule.load.Options;
import com.enliple.keyboard.imgmodule.load.data.mediastore.MediaStoreUtil;
import com.enliple.keyboard.imgmodule.load.data.mediastore.ThumbFetcher;
import com.enliple.keyboard.imgmodule.load.model.ModelLoader;
import com.enliple.keyboard.imgmodule.load.model.ModelLoaderFactory;
import com.enliple.keyboard.imgmodule.load.model.MultiModelLoaderFactory;
import com.enliple.keyboard.imgmodule.signature.ObjectKey;
import java.io.InputStream;

/**
 * Loads {@link InputStream}s from media store image {@link Uri}s that point to pre-generated
 * thumbnails for those {@link Uri}s in the media store.
 */
public class MediaStoreImageThumbLoader implements ModelLoader<Uri, InputStream> {
  private final Context context;

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public MediaStoreImageThumbLoader(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override
  public LoadData<InputStream> buildLoadData(
      @NonNull Uri model, int width, int height, @NonNull Options options) {
    if (MediaStoreUtil.isThumbnailSize(width, height)) {
      return new LoadData<>(new ObjectKey(model), ThumbFetcher.buildImageFetcher(context, model));
    } else {
      return null;
    }
  }

  @Override
  public boolean handles(@NonNull Uri model) {
    return MediaStoreUtil.isMediaStoreImageUri(model);
  }

  /** Factory that loads {@link InputStream}s from media store image {@link Uri}s. */
  public static class Factory implements ModelLoaderFactory<Uri, InputStream> {

    private final Context context;

    public Factory(Context context) {
      this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new MediaStoreImageThumbLoader(context);
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}
