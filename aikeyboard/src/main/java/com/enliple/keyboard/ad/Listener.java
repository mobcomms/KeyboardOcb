package com.enliple.keyboard.ad;

public class Listener {
    public interface OnGameStatusListener {
        void received(String status);
    }
    public interface OnBannerViewListener {
        // errorCode
        // :ERROR_AD_MOVE
        // :ERROR_AD_LOAD
        // :ERROR_NETWORK_CONDITION
        void onBannerViewError(String errorCode,String detail);

        // bannerViewState
        // :STATE_AD_LOADING
        // :STATE_AD_LOADED
        // :STATE_AD_RELOADED
        // :STATE_AD_CLOSE
        // :STATE_AD_MOVE
        void onBannerViewState(String bannerViewState,String detail);

        // Point 적립 callback
        void onBannerPoint(String targetId,int point);
    }

    public interface OnEnWebBannerListener {
        void adClicked();
        void adMoved(String url);
        void adClickError(Error error, String urlm, String errorCode);
        void adClose();
        void adLoading();
        void adLoadSuccess();
        void adConsole(String message);
        void adLoadFail(Error error, String errorCode);
    }
}
