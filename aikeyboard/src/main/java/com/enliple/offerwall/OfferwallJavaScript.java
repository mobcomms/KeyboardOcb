package com.enliple.offerwall;

import com.enliple.keyboard.network.Url;
import com.enliple.keyboard.ui.common.LogPrint;

public class OfferwallJavaScript {
    private static final String script_base_url = "https://api.healingcash.co.kr";
    private static final String js_file_url = "https://pomission.com/attach/script/tracker.js";

    private static final String js_hybrid = "https://ocbapi.cashkeyboard.co.kr/API/OCB/offerwall/js/tracker.js";

    private static final String js_file_dev_url = "http://112.220.254.82:11000/attach/script/tracker.js";
    public static final String INSECT_MY_JS = "document.addEventListener(\"DOMContentLoaded\", function(){ " +
            "var my_awesome_script = document.createElement('script');" +
            "my_awesome_script.setAttribute('src','" + script_base_url + "/images/script/live_script.js?1');" +
            "document.head.appendChild(my_awesome_script);" +
            "});";

    public static final String getP3Javascript(String missionClass, String dataLink, String mediaId, String keyword) {
        String p3_javascript = "document.addEventListener(\"DOMContentLoaded\", function(){ " +
                "var my_awesome_script = document.createElement('script');" +
                "my_awesome_script.setAttribute('src','" + js_file_url + "');" +
                "my_awesome_script.setAttribute('id','pomission_js');" +
                "my_awesome_script.setAttribute('class','" + missionClass + "');" +
                "my_awesome_script.setAttribute('data_link','" + dataLink + "');" +
                "my_awesome_script.setAttribute('media_id','" + mediaId + "');" +
                "my_awesome_script.setAttribute('keyword','" + keyword + "');" +
                "document.head.appendChild(my_awesome_script);" +
                "});";
        LogPrint.d("p3_javascript :: " + p3_javascript);
        return p3_javascript;
    }

    public static final String getHybridJavascript(String missionClass, String dataLink, String mediaId, String keyword) {
        String h_javascript = "document.addEventListener(\"DOMContentLoaded\", function(){ " +
                "var my_awesome_script = document.createElement('script');" +
                "my_awesome_script.setAttribute('src','" + js_hybrid + "');" +
                "my_awesome_script.setAttribute('id','pomission_js');" +
                "my_awesome_script.setAttribute('class','" + missionClass + "');" +
                "my_awesome_script.setAttribute('data_link','" + dataLink + "');" +
                "my_awesome_script.setAttribute('media_id','" + mediaId + "');" +
                "my_awesome_script.setAttribute('keyword','" + keyword + "');" +
                "document.head.appendChild(my_awesome_script);" +
                "});";
        LogPrint.d("h_javascript :: " + h_javascript);
        return h_javascript;
    }

}
