package com.atet.lib_atet_account_system.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

//import com.tcl.deviceinfo.TDeviceInfo;

public class Utils {
	private static final String TAG = "Utils";
	private static final boolean LOGI = true;

	public static int DEVICE_TYPE = DeviceType.DEVICE_TYPE_GAMEBOX;

	public static class DeviceType {
		public static final int DEVICE_TYPE_GAMEBOX = 1;
		public static final int DEVICE_TYPE_TCL = 2;
		public static final int DEVICE_TYPE_JIUZHOU = 3;

	}

	public static String getClientType(ContentResolver resolver) {

		String mRet = "";

		if (mRet == null || mRet.equals("")) {
			mRet = android.os.Build.MODEL;
		}
		if (mRet != null) {
			mRet = mRet.replaceAll(" ", "");
		}

		return mRet;
	}

	/**
	 * 获取DNumber
	 */
	public static String getDNumber(Context context, ContentResolver resolver) {

		SharedPreferences proSp = context.getSharedPreferences("ProductInfo",
				Activity.MODE_APPEND);
		if (proSp.getBoolean("IS_ALREADY_GET_PRODUCTID", false)) {
			return proSp.getString("ProductID", "");
		}
		String mRet = "";
		/**
		 * 手机都能获取imei号，所以优先获取imei作为硬件设备的唯一标识符
		 * 
		 * */
		if (mRet == null || mRet.equals("")) {
			TelephonyManager TelephonyMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			mRet = TelephonyMgr.getDeviceId();
		}
		if (mRet == null || mRet.equals("")) {
			// 取wifi的物理地址
			mRet = MacAdressUtil.getWifiMacAddress(context);
		}

		if (mRet == null || mRet.equals("")) {
			// 获取以太网的物理地址
			mRet = MacAdressUtil.getMacAddress();
		}

		if (mRet == null || mRet.equals("")) {
			// 获取蓝牙的物理地址
			mRet = MacAdressUtil.getBluetoothMacAdress();
		}

		if (mRet == null || mRet.equals("")) {
			return "";
		}
		if (mRet != null && !mRet.equals("")) {
			mRet = mRet.replaceAll(" ", "");
			proSp.edit().putBoolean("IS_ALREADY_GET_PRODUCTID", true)
					.putString("ProductID", mRet).commit();
		}

		return mRet;
	}
}
