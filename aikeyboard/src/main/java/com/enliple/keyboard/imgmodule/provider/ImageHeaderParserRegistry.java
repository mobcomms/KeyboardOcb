package com.enliple.keyboard.imgmodule.provider;

import androidx.annotation.NonNull;
import com.enliple.keyboard.imgmodule.load.ImageHeaderParser;
import java.util.ArrayList;
import java.util.List;

/** Contains an unordered list of {@link ImageHeaderParser}s capable of parsing image headers. */
public final class ImageHeaderParserRegistry {
  private final List<ImageHeaderParser> parsers = new ArrayList<>();

  @NonNull
  public synchronized List<ImageHeaderParser> getParsers() {
    return parsers;
  }

  public synchronized void add(@NonNull ImageHeaderParser parser) {
    parsers.add(parser);
  }
}
