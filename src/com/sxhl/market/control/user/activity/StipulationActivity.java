package com.sxhl.market.control.user.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sxhl.market.R;
import com.sxhl.market.control.common.activity.BaseActivity;

public class StipulationActivity extends BaseActivity {
	WebView view = null;
	View proBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_register_des);
		view = (WebView) findViewById(R.id.myWebView);
		proBar = this.findViewById(R.id.proContainer);
		view.loadUrl("http://service.521bama.com:7878/file/userStatement/saigame.html");
		view.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				proBar.setVisibility(View.GONE);
				view.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				proBar.setVisibility(View.VISIBLE);
				view.setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK) && view.canGoBack()) {

			view.goBack();

			return true;

		}

		return super.onKeyDown(keyCode, event);

	}

}
