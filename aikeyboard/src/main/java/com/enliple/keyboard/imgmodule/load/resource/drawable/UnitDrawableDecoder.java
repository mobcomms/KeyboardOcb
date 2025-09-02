package com.enliple.keyboard.imgmodule.load.resource.drawable;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.enliple.keyboard.imgmodule.load.Options;
import com.enliple.keyboard.imgmodule.load.ResourceDecoder;
import com.enliple.keyboard.imgmodule.load.engine.Resource;

/** Passes through a {@link Drawable} as a {@link Drawable} based {@link Resource}. */
public class UnitDrawableDecoder implements ResourceDecoder<Drawable, Drawable> {
  @Override
  public boolean handles(@NonNull Drawable source, @NonNull Options options) {
    return true;
  }

  @Nullable
  @Override
  public Resource<Drawable> decode(
      @NonNull Drawable source, int width, int height, @NonNull Options options) {
    return NonOwnedDrawableResource.newInstance(source);
  }
}
