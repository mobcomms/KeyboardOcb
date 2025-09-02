package com.enliple.keyboard.imgmodule.load.resource.file;

import androidx.annotation.NonNull;
import com.enliple.keyboard.imgmodule.load.Options;
import com.enliple.keyboard.imgmodule.load.ResourceDecoder;
import com.enliple.keyboard.imgmodule.load.engine.Resource;
import java.io.File;

/**
 * A simple {@link com.enliple.keyboard.imgmodule.load.ResourceDecoder} that creates resource for a given {@link
 * java.io.File}.
 */
public class FileDecoder implements ResourceDecoder<File, File> {

  @Override
  public boolean handles(@NonNull File source, @NonNull Options options) {
    return true;
  }

  @Override
  public Resource<File> decode(
      @NonNull File source, int width, int height, @NonNull Options options) {
    return new FileResource(source);
  }
}
