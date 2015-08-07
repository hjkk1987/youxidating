package com.sxhl.statistics.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


/**
 * 
 * 用于获取程序包信息的辅助类
 * 
 * 
 * @author zhaominglai
 * 
 * */
public class PackageInfoUtils{
	
	
	
	/**
	 * 获取程序的名称，而非包名。例如浏览器的包名为com.android.browser，而名称为浏览器
	 * 
	 * */
	public static String getApplicationLable(Context context,String packageName)
	{
		PackageManager mPM = context.getPackageManager();
		try {
			return mPM.getApplicationLabel(mPM.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "noname";
	}
	
	/**
	 * 获取当前正在运行的Activity的程序包名。
	 * 
	 * */
	public static String getRunningPackageName(Context context)
	{
		ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		return mAM.getRunningTasks(1).get(0).topActivity.getPackageName();
	}

}
