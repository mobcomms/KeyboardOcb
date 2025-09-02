package com.enliple.keyboard.imgmodule.request;

import com.enliple.keyboard.imgmodule.load.DataSource;
import com.enliple.keyboard.imgmodule.load.engine.ImageModuleException;
import com.enliple.keyboard.imgmodule.load.engine.Resource;

/**
 * A callback that listens for when a resource load completes successfully or fails due to an
 * exception.
 */
public interface ResourceCallback {

  /**
   * Called when a resource is successfully loaded.
   *
   * @param resource The loaded resource.
   */
  void onResourceReady(
      Resource<?> resource, DataSource dataSource, boolean isLoadedFromAlternateCacheKey);

  /**
   * Called when a resource fails to load successfully.
   *
   * @param e a non-null {@link ImageModuleException}.
   */
  void onLoadFailed(ImageModuleException e);

  /** Returns the lock to use when notifying individual requests. */
  Object getLock();
}
