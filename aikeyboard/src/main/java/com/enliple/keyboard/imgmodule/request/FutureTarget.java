package com.enliple.keyboard.imgmodule.request;

import com.enliple.keyboard.imgmodule.request.target.Target;
import java.util.concurrent.Future;

/**
 * An interface for an object that is both a {@link com.enliple.keyboard.imgmodule.request.target.Target} and a
 * {@link java.util.concurrent.Future}. For example:
 *
 *
 * <p>Note - {@link #get()} and {@link #get(long, java.util.concurrent.TimeUnit)} must be called off
 * of the main thread or they will block forever.
 *
 * @param <R> The type of resource this FutureTarget will retrieve.
 */
public interface FutureTarget<R> extends Future<R>, Target<R> {}
