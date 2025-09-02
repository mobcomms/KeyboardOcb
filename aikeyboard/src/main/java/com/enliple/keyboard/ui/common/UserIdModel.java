package com.enliple.keyboard.ui.common;

/**
 * Created by Administrator on 2017-10-17.
 */

public class UserIdModel {
    private String mUserId = "";
    private String mGubun = "";
    private String mDeviceId = "";

    public void setDeviceId(String deviceId) { mDeviceId = deviceId; };

    public void setUserId(String userId){
        mUserId = userId;
    }

    public void setGubun(String gubun) {
        mGubun = gubun;
    }

    public String getDeviceId() { return mDeviceId; }

    public String getUserId() {
        return mUserId;
    }

    public String getGubun() {
        return mGubun;
    }
}
