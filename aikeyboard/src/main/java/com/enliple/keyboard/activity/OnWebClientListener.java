package com.enliple.keyboard.activity;

import android.graphics.Bitmap;
import android.webkit.WebView;

public interface OnWebClientListener
{
	public void onPageStarted(WebView view, String url, Bitmap favicon);

	public void onPageFinished(WebView view, String url);

	public boolean shouldOverrideUrlLoading(WebView view, String url);
}
