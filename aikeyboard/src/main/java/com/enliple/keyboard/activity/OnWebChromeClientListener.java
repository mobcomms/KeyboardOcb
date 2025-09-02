package com.enliple.keyboard.activity;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebView;

public interface OnWebChromeClientListener
{
	public void uploadMessage(ValueCallback<Uri> uploadMsg);

	public void webChromeClientPageFinished(WebView view, String url);
	
	public void onProgressChanged(WebView view, int newProgress);
}
