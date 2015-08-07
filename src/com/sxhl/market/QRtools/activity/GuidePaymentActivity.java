package com.sxhl.market.QRtools.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.sxhl.market.QRtools.activity.util.Res;
import com.sxhl.market.control.common.activity.BaseExitActivity;

public class GuidePaymentActivity extends BaseExitActivity {

	private Button sure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(Res.layout(GuidePaymentActivity.this,
				"aipay_scan_pay_guide"));
		setHeadTitle("二维码扫描");
		// int a = R.layout.aipay_scan_pay_guide;
		sure = (Button) findViewById(Res.id(GuidePaymentActivity.this, "sure"));
		sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(GuidePaymentActivity.this,
						CaptureActivity.class);
				startActivity(intent);
			}
		});
	}

}
