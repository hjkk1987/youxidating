package com.sxhl.market.utils;

import android.content.Context;
import android.widget.Toast;

import com.sxhl.market.app.BaseApplication;

/**
 * @author fxl
 * time: 2012-9-5 下午5:59:24
 * description:屏幕单位换算工具类
 */
public class DisplayTool {

	private static final float density = BaseApplication.m_appContext
			.getResources().getDisplayMetrics().density;

	private static final float scaledDensity = BaseApplication.m_appContext
			.getResources().getDisplayMetrics().scaledDensity;

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(float pxValue) {
		return (int) (pxValue / density + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(float dipValue) {
		return (int) (dipValue * density + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(float pxValue) {
		return (int) (pxValue / scaledDensity + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @return
	 */
	public static int sp2px(float spValue) {
		return (int) (spValue * scaledDensity + 0.5f);
	}
	
	/**
	 * 显示"LENGTH_SHORT" Toast信息
	 */
	public static void showShortToast(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示"LENGTH_LONG" Toast信息
	 */
	public static void showLongToast(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

}
