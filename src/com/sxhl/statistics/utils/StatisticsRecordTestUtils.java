package com.sxhl.statistics.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.content.Context;


/**
 * 测试用。功能是将后台统计的操作以文本的形式记录到本地，方便测试人员查看。正式版本中将去掉
 * 
 * */
public class StatisticsRecordTestUtils {
	
	public static String FILE_NAME = "record.txt";
	
	public static boolean ACCOUNT_DEBUG = true;
	
	
	public static void appendRecord(final Context context,final String content)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				write(context,content);
			}
		}).start();
	}
	
	public synchronized static void write(Context context,String content)
	{
		try {
			FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
			
			PrintStream ps = new PrintStream(fos);
			ps.println(content + "\n\r");
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void newLog(Context context,String serviceName,String msg)
	{
		StringBuilder recordString = new StringBuilder();	
		recordString.append(DeviceStatisticsUtils.getDateToString(DeviceStatisticsUtils.getTime()));
		recordString.append("[ " + serviceName + " ] " + msg);
	
		appendRecord(context, recordString.toString());
		
		recordString = null;
	}
	
	public synchronized static void noInternetLog(Context context,String serviceName)
	{
		if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("上传设备信息失败！无有效的网络连接。\r\n");
			String msg = sb.toString();
			sb = null;
			StatisticsRecordTestUtils.newLog(context,serviceName, msg);
		}
	}
	
	public synchronized static void databaseOverwriteLog(Context context,String serviceName)
	{
		if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("本地数据库中记录超过100条，将删除最早的1条记录。。\r\n");
			String msg = sb.toString();
			sb = null;
			StatisticsRecordTestUtils.newLog(context,serviceName, msg);
		}
	}

}
