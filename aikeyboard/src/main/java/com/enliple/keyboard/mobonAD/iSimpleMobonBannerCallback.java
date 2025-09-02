package com.enliple.keyboard.mobonAD;

/**
 * - 모비온 배너 콜백
 */
public interface iSimpleMobonBannerCallback {

    /**
     * 광고 데이터 로딩 성공에 대한 콜백 인터페이스
     */
    void onLoadedAdInfo(final boolean result, final String errorStr);

    void onAdClicked();

    void onCloseClicked();

    void onBannerLoaded(int leftBg, int rightBg);
}
