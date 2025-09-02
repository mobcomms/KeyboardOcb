package com.enliple.keyboard.imgmodule.manager;

import android.app.Activity;

final class DoNothingFirstFrameWaiter implements FrameWaiter {

  @Override
  public void registerSelf(Activity activity) {
    // Do nothing.
  }
}
