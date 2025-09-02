package com.enliple.keyboard.imgmodule.load.resource.file;

import com.enliple.keyboard.imgmodule.load.resource.SimpleResource;
import java.io.File;

/** A simple {@link com.enliple.keyboard.imgmodule.load.engine.Resource} that wraps a {@link File}. */
// Public API.
@SuppressWarnings("WeakerAccess")
public class FileResource extends SimpleResource<File> {
  public FileResource(File file) {
    super(file);
  }
}
