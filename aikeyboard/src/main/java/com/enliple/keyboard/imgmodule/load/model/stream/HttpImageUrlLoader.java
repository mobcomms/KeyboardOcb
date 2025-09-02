package com.enliple.keyboard.imgmodule.load.model.stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.enliple.keyboard.imgmodule.load.Option;
import com.enliple.keyboard.imgmodule.load.Options;
import com.enliple.keyboard.imgmodule.load.data.HttpUrlFetcher;
import com.enliple.keyboard.imgmodule.load.model.ImageUrl;
import com.enliple.keyboard.imgmodule.load.model.ModelCache;
import com.enliple.keyboard.imgmodule.load.model.ModelLoader;
import com.enliple.keyboard.imgmodule.load.model.ModelLoaderFactory;
import com.enliple.keyboard.imgmodule.load.model.MultiModelLoaderFactory;
import java.io.InputStream;

/**
 * An {@link com.enliple.keyboard.imgmodule.load.model.ModelLoader} for translating {@link
 * ImageUrl} (http/https URLS) into {@link java.io.InputStream} data.
 */
// Public API.
@SuppressWarnings("WeakerAccess")
public class HttpImageUrlLoader implements ModelLoader<ImageUrl, InputStream> {
  /**
   * An integer option that is used to determine the maximum connect and read timeout durations (in
   * milliseconds) for network connections.
   *
   * <p>Defaults to 2500ms.
   */
  public static final Option<Integer> TIMEOUT =
      Option.memory("com.enliple.keyboard.imgmodule.load.model.stream.HttpUrlLoader.Timeout", 2500);

  @Nullable private final ModelCache<ImageUrl, ImageUrl> modelCache;

  public HttpImageUrlLoader() {
    this(null);
  }

  public HttpImageUrlLoader(@Nullable ModelCache<ImageUrl, ImageUrl> modelCache) {
    this.modelCache = modelCache;
  }

  @Override
  public LoadData<InputStream> buildLoadData(
          @NonNull ImageUrl model, int width, int height, @NonNull Options options) {
    // GlideUrls memoize parsed URLs so caching them saves a few object instantiations and time
    // spent parsing urls.
    ImageUrl url = model;
    if (modelCache != null) {
      url = modelCache.get(model, 0, 0);
      if (url == null) {
        modelCache.put(model, 0, 0, model);
        url = model;
      }
    }
    int timeout = options.get(TIMEOUT);
    return new LoadData<>(url, new HttpUrlFetcher(url, timeout));
  }

  @Override
  public boolean handles(@NonNull ImageUrl model) {
    return true;
  }

  /** The default factory for {@link HttpImageUrlLoader}s. */
  public static class Factory implements ModelLoaderFactory<ImageUrl, InputStream> {
    private final ModelCache<ImageUrl, ImageUrl> modelCache = new ModelCache<>(500);

    @NonNull
    @Override
    public ModelLoader<ImageUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new HttpImageUrlLoader(modelCache);
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}
