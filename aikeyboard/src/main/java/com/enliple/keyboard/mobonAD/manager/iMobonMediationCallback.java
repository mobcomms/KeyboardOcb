package com.enliple.keyboard.mobonAD.manager;

/**
 * - 모비온 미디에이션 콜백
 */
public interface iMobonMediationCallback {

    /**
     * 타사 미디에이션 실패하고
     * mobon json data가 있다면 mobon 광고 데이터 json 리턴.
     */
    void onLoadedAdData(final String data,final AdapterObject adapter);

    void onAdAdapter(final AdapterObject adapter);

    void onAdClosed();

    void onAdCancel();

    void onAdFailedToLoad(String errorMsg);

    void onAdImpression();

    void onAdClicked();

    void onAppFinish();
}
