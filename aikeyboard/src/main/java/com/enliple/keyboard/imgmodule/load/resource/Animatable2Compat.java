package com.enliple.keyboard.imgmodule.load.resource;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static android.os.Build.VERSION_CODES.M;

public interface Animatable2Compat extends Animatable {

    /**
     * Adds a callback to listen to the animation events.
     *
     * @param callback Callback to add.
     */
    void registerAnimationCallback(@NonNull AnimationCallback callback);

    /**
     * Removes the specified animation callback.
     *
     * @param callback Callback to remove.
     * @return {@code false} if callback didn't exist in the call back list, or {@code true} if
     *         callback has been removed successfully.
     */
    boolean unregisterAnimationCallback(@NonNull AnimationCallback callback);

    /**
     * Removes all existing animation callbacks.
     */
    void clearAnimationCallbacks();

    /**
     * Abstract class for animation callback. Used to notify animation events.
     */
    abstract class AnimationCallback {
        /**
         * Called when the animation starts.
         *
         * @param drawable The drawable started the animation.
         */
        public void onAnimationStart(Drawable drawable) {};
        /**
         * Called when the animation ends.
         *
         * @param drawable The drawable finished the animation.
         */
        public void onAnimationEnd(Drawable drawable) {};

        // Only when passing this Animatable2Compat.AnimationCallback to a frameworks' AVD, we need
        // to bridge this compat version callback with the frameworks' callback.
        Animatable2.AnimationCallback mPlatformCallback;

        @RequiresApi(M)
        Animatable2.AnimationCallback getPlatformCallback() {
            if (mPlatformCallback == null) {
                mPlatformCallback = new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationStart(Drawable drawable) {
                        AnimationCallback.this.onAnimationStart(drawable);
                    }

                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        AnimationCallback.this.onAnimationEnd(drawable);
                    }
                };
            }
            return mPlatformCallback;
        }
    }
}
