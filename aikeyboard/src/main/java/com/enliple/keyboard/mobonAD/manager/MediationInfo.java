package com.enliple.keyboard.mobonAD.manager;

import android.text.TextUtils;

/**
 * Created by yikim on 2018-02-13.
 */

public class MediationInfo {
    public String pId;
    public int ad_Type;
    public String company;

    public MediationInfo(String _id, String _company) {
        this.pId = _id;
        this.company = _company;

        if (!TextUtils.isEmpty(pId))
            pId = pId.trim();
    }
}
