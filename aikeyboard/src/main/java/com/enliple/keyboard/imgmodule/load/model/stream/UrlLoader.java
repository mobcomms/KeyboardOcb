package com.enliple.keyboard.imgmodule.load.model.stream;

import androidx.annotation.NonNull;
import com.enliple.keyboard.imgmodule.load.Options;
import com.enliple.keyboard.imgmodule.load.model.ImageUrl;
import com.enliple.keyboard.imgmodule.load.model.ModelLoader;
import com.enliple.keyboard.imgmodule.load.model.ModelLoaderFactory;
import com.enliple.keyboard.imgmodule.load.model.MultiModelLoaderFactory;
import java.io.InputStream;
import java.net.URL;

/**
 * A wrapper class that translates {@link java.net.URL} objects into {@link
 * ImageUrl} objects and then uses the wrapped {@link
 * com.enliple.keyboard.imgmodule.load.model.ModelLoader} for {@link ImageUrl}s to
 * load the data.
 */
public class UrlLoader implements ModelLoader<URL, InputStream> {
  private final ModelLoader<ImageUrl, InputStream> imageUrlLoader;

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public UrlLoader(ModelLoader<ImageUrl, InputStream> imageUrlLoader) {
    this.imageUrlLoader = imageUrlLoader;
  }

  @Override
  public LoadData<InputStream> buildLoadData(
      @NonNull URL model, int width, int height, @NonNull Options options) {
    return imageUrlLoader.buildLoadData(new ImageUrl(model), width, height, options);
  }

  @Override
  public boolean handles(@NonNull URL model) {
    return true;
  }

  /** Factory for loading {@link InputStream}s from {@link URL}s. */
  public static class StreamFactory implements ModelLoaderFactory<URL, InputStream> {

    @NonNull
    @Override
    public ModelLoader<URL, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new UrlLoader(multiFactory.build(ImageUrl.class, InputStream.class));
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}
