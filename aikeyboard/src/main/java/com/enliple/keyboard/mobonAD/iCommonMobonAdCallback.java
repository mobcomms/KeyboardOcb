package com.enliple.keyboard.mobonAD;

import org.json.JSONObject;

/**
 * @author 김현준
 * @since 2015-03-23
 * <p>
 * - 모비온 광고에 대한 콜백 인터페이스 정의 추가!
 */
public interface iCommonMobonAdCallback {
    void onLoadedMobonAdData(final boolean result, final JSONObject data, final String errorStr);
}
