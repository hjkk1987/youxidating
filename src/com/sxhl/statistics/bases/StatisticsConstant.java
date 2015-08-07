package com.sxhl.statistics.bases;


/**
 * 统计服务中的常量类，用于保存服务中需要的常量信息
 * 
 * @author zhaominglai
 * @date 2014/7/13
 * 
 * */
public class StatisticsConstant {
	
	/**自身的包名*/
	public static String PACKAGE_SELF_NAME = "com.sxhl.market";
	
	/**平台游戏*/
	public final static int GAME_COPYRIGHT_PLATFORM = 1;
	
	/**第三方游戏*/
	public final static int GAME_COPYRIGHT_THIRDPARTY = 2;
	
	/**其它游戏*/
	public final static int GAME_COPYRIGHT_OTHER = 3;
	
	/**点击量*/
	public final static int GAME_UPDATE_CLICK = 1;
	/**广告点击量*/
	public final static int GAME_UPDATE_ADCLICK = 2;
	
	/**下载量*/
	public final static int GAME_UPDATE_DOWNCOUNT = 3;
	
	/**sharedPreference文件名，用于记录统计服务的上传时间*/
	public final static String SP_UPLOAD_TIMEINFOS ="upload_timeinfos";
	
	/**上一次游戏采集接口上传成功的时间*/
	public final static String SP_LASTUPLOAD_GAMECOLLECT ="lastUpload_collect";
	
	/**上一次游戏在线时长接口上传成功时间*/
	public final static String SP_LASTUPLOAD_GAMEONLINE ="lastUpload_gameonline";
	
	/**上一次平台在线时长接口上传成功时间*/
	public final static String SP_LASTUPLOAD_PLATFORM ="lastUpload_platform";
	
	/**设备信息相关的参数表*/
	public final static String SP_STATISTICS_DEVINFO ="statistics_devinfos";
	
	/**原始的硬件设备型号*/
	public final static String SP_STATISTICS_PRODUCTID ="statistics_productId";
	
	public final static int NET_TYPE_NULL = 0;
	
	public final static int NET_TYPE_WIFI = 1;
	
	public final static int NET_TYPE_OTHER = 2;
	
}
