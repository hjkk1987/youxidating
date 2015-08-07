package com.sxhl.market.QRtools.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ResActivity extends Activity {
	String str;
	Handler handler;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		str = getIntent().getStringExtra("result");
		TextView tv = new TextView(this);
		tv.setText(str);
		setContentView(tv);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!str.equals("sfsfs")) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler.sendEmptyMessage(0);
				}
			}
		}).start();

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Toast.makeText(ResActivity.this, "初始化支付失败", 2000).show();
				finish();
			};
		};
	}
}
