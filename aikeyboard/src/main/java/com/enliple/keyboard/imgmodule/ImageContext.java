package com.enliple.keyboard.imgmodule;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.ImageView;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.enliple.keyboard.imgmodule.ImageModule.RequestOptionsFactory;
import com.enliple.keyboard.imgmodule.load.engine.Engine;
import com.enliple.keyboard.imgmodule.load.engine.bitmap_recycle.ArrayPool;
import com.enliple.keyboard.imgmodule.request.RequestListener;
import com.enliple.keyboard.imgmodule.request.RequestOptions;
import com.enliple.keyboard.imgmodule.request.target.ImageViewTargetFactory;
import com.enliple.keyboard.imgmodule.request.target.ViewTarget;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Global context for all loads in ImageModule containing and exposing the various registries and classes
 * required to load resources.
 */
@SuppressWarnings("PMD.DataClass")
public class ImageContext extends ContextWrapper {
  @VisibleForTesting
  static final TransitionOptions<?, ?> DEFAULT_TRANSITION_OPTIONS =
      new GenericTransitionOptions<>();

  private final ArrayPool arrayPool;
  private final Registry registry;
  private final ImageViewTargetFactory imageViewTargetFactory;
  private final RequestOptionsFactory defaultRequestOptionsFactory;
  private final List<RequestListener<Object>> defaultRequestListeners;
  private final Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions;
  private final Engine engine;
  private final ImageExperiments experiments;
  private final int logLevel;

  @Nullable
  @GuardedBy("this")
  private RequestOptions defaultRequestOptions;

  public ImageContext(
      @NonNull Context context,
      @NonNull ArrayPool arrayPool,
      @NonNull Registry registry,
      @NonNull ImageViewTargetFactory imageViewTargetFactory,
      @NonNull RequestOptionsFactory defaultRequestOptionsFactory,
      @NonNull Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions,
      @NonNull List<RequestListener<Object>> defaultRequestListeners,
      @NonNull Engine engine,
      @NonNull ImageExperiments experiments,
      int logLevel) {
    super(context.getApplicationContext());
    this.arrayPool = arrayPool;
    this.registry = registry;
    this.imageViewTargetFactory = imageViewTargetFactory;
    this.defaultRequestOptionsFactory = defaultRequestOptionsFactory;
    this.defaultRequestListeners = defaultRequestListeners;
    this.defaultTransitionOptions = defaultTransitionOptions;
    this.engine = engine;
    this.experiments = experiments;
    this.logLevel = logLevel;
  }

  public List<RequestListener<Object>> getDefaultRequestListeners() {
    return defaultRequestListeners;
  }

  public synchronized RequestOptions getDefaultRequestOptions() {
    if (defaultRequestOptions == null) {
      defaultRequestOptions = defaultRequestOptionsFactory.build().lock();
    }

    return defaultRequestOptions;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public <T> TransitionOptions<?, T> getDefaultTransitionOptions(@NonNull Class<T> transcodeClass) {
    TransitionOptions<?, ?> result = defaultTransitionOptions.get(transcodeClass);
    if (result == null) {
      for (Entry<Class<?>, TransitionOptions<?, ?>> value : defaultTransitionOptions.entrySet()) {
        if (value.getKey().isAssignableFrom(transcodeClass)) {
          result = value.getValue();
        }
      }
    }
    if (result == null) {
      result = DEFAULT_TRANSITION_OPTIONS;
    }
    return (TransitionOptions<?, T>) result;
  }

  @NonNull
  public <X> ViewTarget<ImageView, X> buildImageViewTarget(
      @NonNull ImageView imageView, @NonNull Class<X> transcodeClass) {
    return imageViewTargetFactory.buildTarget(imageView, transcodeClass);
  }

  @NonNull
  public Engine getEngine() {
    return engine;
  }

  @NonNull
  public Registry getRegistry() {
    return registry;
  }

  public int getLogLevel() {
    return logLevel;
  }

  @NonNull
  public ArrayPool getArrayPool() {
    return arrayPool;
  }

  public ImageExperiments getExperiments() {
    return experiments;
  }
}
