package com.sxhl.market.app;

import android.os.Environment;

/**
 * @author time：2012-8-6 下午6:02:16 description:
 */
public class Configuration {

	// 是否输出LogCat调试日志
	public static final boolean IS_DEBUG_ENABLE = true;

	// 是否使用调试服务器
	public static final boolean IS_HTTP_DEBUG_ENABLE = false;

	// LogCat的TAG标签
	public static final String DEBUG_TAG = "MARKET_PRODUCT";

	// 保存的本地文件的默认文件夹
	public static String FILE_CATCHE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/eacanMobile/";

	// 线程池大小
	public static final int MAX_THREAD_POOL_SIZE = 5;

}
