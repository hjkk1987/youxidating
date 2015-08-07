package com.sxhl.market.utils;

import android.util.Log;

import com.sxhl.market.app.Configuration;
/**
 * @author wsd
 * @Description:调试工具类
 * @date 2012-12-3 下午1:52:27
 */
public class DebugTool {
	
	/**
	 * 打印调试信息.
	 * 
	 * @param msg
	 */
	public static void debug(String msg) {
		if (Configuration.IS_DEBUG_ENABLE) {
			Log.d(Configuration.DEBUG_TAG, msg);
		}
	}

	/**
	 * 打印调试信息.
	 * 
	 * @param msg
	 */
	public static void debug(String tag,String msg) {
		if (Configuration.IS_DEBUG_ENABLE) {
			Log.d(tag, msg);
		}
	}
	/**
	 * 打印警告信息.
	 * 
	 * @param msg
	 */
	public static void warn(String msg) {
		if (Configuration.IS_DEBUG_ENABLE) {
			Log.w(Configuration.DEBUG_TAG, msg);
		}
	}
	/**
	 * 打印警告信息.
	 * 
	 * @param msg
	 */
	public static void warn(String tag,String msg) {
		if (Configuration.IS_DEBUG_ENABLE) {
			Log.w(tag, msg);
		}
	}
	/**
	 * 打印提示信息.
	 * 
	 * @param msg
	 */
	public static void info(String msg) {
		if (Configuration.IS_DEBUG_ENABLE) {
			Log.i(Configuration.DEBUG_TAG, msg);
		}
	}
	/**
	 * 打印提示信息.
	 * 
	 * @param msg
	 */
	public static void info(String tag,String msg) {
		if (Configuration.IS_DEBUG_ENABLE) {
			Log.i(tag, msg);
		}
	}
	/**
	 * 打印错误信息.
	 * 
	 * @param msg
	 */
	public static void error(String msg, Exception e) {
		if (Configuration.IS_DEBUG_ENABLE) {
		}
	}
	/**
	 * 打印错误信息.
	 * 
	 * @param msg
	 */
	public static void error(String tag,String msg, Exception e) {
		if (Configuration.IS_DEBUG_ENABLE) {
			Log.e(tag, msg, e);
		}
	}
}
