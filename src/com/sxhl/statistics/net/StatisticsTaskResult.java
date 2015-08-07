package com.sxhl.statistics.net;

/**
 * 统计接口的状态返回值
 * 
 * @author zhaominglai
 * @date 2014/7/13
 * 
 * */
public class StatisticsTaskResult {
	
	/**以下为数据统计服务专用*/
	/**请求数据成功*/
	public static final int STATISTICS_OK = 0;
	
	/**非法操作*/
	public static final int STATISTICS_INVALIDATE_OP = 1;
	
	/**系统内部错误*/
	public static final int STATISTICS_SYSERR = 2;
	
	/**请求json数据解析发生错误*/
	public static final int STATISTICS_REQUSET_JSON_ERR = 3;
	
	/**请求参数错误*/
	public static final int STATISTICS_REQUSET_PARAM_ERR = 4;

}
