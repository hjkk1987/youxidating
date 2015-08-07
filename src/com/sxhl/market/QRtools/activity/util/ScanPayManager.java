package com.sxhl.market.QRtools.activity.util;

import android.content.Context;
import android.content.Intent;

import com.sxhl.market.QRtools.activity.GuidePaymentActivity;

public class ScanPayManager {
	
	public static void scanPay(Context context) {
		Intent intent = new Intent(context,GuidePaymentActivity.class);
		context.startActivity(intent);
	}

}
