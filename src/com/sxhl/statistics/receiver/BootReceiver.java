package com.sxhl.statistics.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.sxhl.market.utils.DebugTool;
import com.sxhl.statistics.utils.StatisticsHelper;



/**
 * 开机广播监听器，用于启动开机要求的统计服务
 * 
 * @author zhaominglai
 * @date 2014/7/13
 * 
 * */
public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "BootReceiver";
	private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
	

	public void onReceive(Context context, Intent intent) {
		if (BOOT_ACTION.equals(intent.getAction())) {
			DebugTool.info(TAG, "receive a bootreceiver");
			
			//启动需要开机就运行的服务
			StatisticsHelper.getInstance(context).startBootServices();
						
		}
	}
}