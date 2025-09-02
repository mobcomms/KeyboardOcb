package com.enliple.keyboard.imgmodule.load.engine;

/**
 * An exception indicating that code outside of ImageModule threw an unexpected exception.
 *
 * <p>This is useful to allow us to distinguish developer errors on the part of users of ImageModule from
 * developer errors on the part of developers of ImageModule itself.
 */
final class CallbackException extends RuntimeException {
  private static final long serialVersionUID = -7530898992688511851L;

  CallbackException(Throwable cause) {
    super("Unexpected exception thrown by non-ImageModule code", cause);
  }
}
