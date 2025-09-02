package com.enliple.keyboard.ad.webview;

import android.webkit.JavascriptInterface;

public class Bridge {
    public static class BannerBridge {
        public static interface Listener {
            void onAppEvent(String jsonData);
        }
        private Listener listener;

        public void setListener(Listener listener) {
            this.listener = listener;
        }


        @JavascriptInterface
        public void onAppEvent(String jsonData) {
            if ( listener != null )
                listener.onAppEvent(jsonData);
        }
    }

    public static class InquiryBridge {
        public static interface Listener {
            void showMessage(String message);
            void termsExpired();
        }
        private Listener listener;

        public void setListener(Listener listener) {
            this.listener = listener;
        }


        @JavascriptInterface
        public void showMessage(String message) {
            if ( listener != null ) {
                listener.showMessage(message);
            }
        }

        @JavascriptInterface
        public void termsExpired() {
            if ( listener != null ) {
                listener.termsExpired();
            }
        }
    }

    public static class PomissionBridge {
        public static interface Listener {
            void showMessage(String message);
            void historyBack();
            void js_load(String js);
        }
        private Listener listener;

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        @JavascriptInterface
        public void showMessage(String message) {
            if ( listener != null ) {
                listener.showMessage(message);
            }
        }

        @JavascriptInterface
        public void historyBack() {
            if ( listener != null ) {
                listener.historyBack();
            }
        }

        @JavascriptInterface
        public void js_load(String js) {
            if ( listener != null ) {
                listener.js_load(js);
            }
        }
    }
}
