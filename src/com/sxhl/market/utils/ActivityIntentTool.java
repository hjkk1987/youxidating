package com.sxhl.market.utils;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.sxhl.market.R;
import com.sxhl.market.model.net.http.download.DownloadTask;
import com.sxhl.market.receiver.BReceiver;
/** 
 * @author  
 * time：2012-8-12 下午5:19:56 
 * description: activity工具类
 */
public class ActivityIntentTool {
	
	/** 是否播放页面切换动画*/
	private static boolean isStartAnimation = true;
	
	/**
	 * Activity 结束跳转
	 * */
	public static void gotoActivity(Context context, Class<?> cls) {
		Intent intent = new Intent(context, cls);
		context.startActivity(intent);
		((Activity) context).finish();
	}
	
	/**
	 * Activity 不结束跳转
	 * */
	public static void gotoActivityNotFinish(Context context, Class<?> cls) {
		Intent intent = new Intent(context, cls);
		context.startActivity(intent);
	}  
 
	/** 
	 * 启动Activity
	 * @param context 当前上下文
	 * @param to 要启动的Activity
	 * @param flag 
	 *    Intent.FLAG_ACTIVITY_CLEAR_TOP 清空所有栈顶
	 *    Intent.FLAG_ACTIVITY_SINGLE_TOP 如果当前Activity运行在栈顶，就不启动Activity
	 */
	public static void gotoActivity ( Context context ,Class < ? extends Activity > to, int flag ) {
		Intent intent = new Intent ( context , to );
		intent.setFlags ( flag);
		
		context.startActivity(intent); 
	}
	
	
	/**
	 * Get IntentData FilePath
	 * */
	public static String getIntentDataFilePath(Activity activity, Uri intentDataUrl) {
		
		if(null == intentDataUrl) return null;
		String filePath = null;
		if ("content".equals(intentDataUrl.getScheme())) {
			Cursor cursor = activity.managedQuery(intentDataUrl,
					null, null, null, null);
			cursor.moveToFirst();
			filePath = cursor.getString(1);
		} else {
			filePath = intentDataUrl.getPath();
		}
		
		return filePath;
	}
	
	/**
	 * Get IntentData FileName
	 * */
	public static String getIntentDataFileName(Activity activity, Uri intentDataUrl) {
		
		if(null == intentDataUrl) return null;
		String fileName = null;
		if ("content".equals(intentDataUrl.getScheme())) {
			Cursor cursor = activity.managedQuery(intentDataUrl,
					null, null, null, null);
			cursor.moveToFirst();
			fileName = cursor.getString(3);
		} else {
			fileName = intentDataUrl.getLastPathSegment();
		}
		
		return fileName;
	}
	
	/**
     * 启动activity进入退出效果跳转动画
     * @param context
     */
	public static void startZoomInOutAnimation(Context context){
		if(isStartAnimation){
			AnimationTool.doActivityChangeStyle(context, AnimationTool.STYLE_ZOOMIN_ZOOMOUT);
		}
	}
	
	/**
	 * Create Activity Shortcuts
	 * */
	@SuppressWarnings("unused")
	private static void addWindos(Context context, String activityName) {
		
	    Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
	    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
	    // 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加 
	    intent.putExtra("duplicate", false);
	    Intent intent2 = new Intent(Intent.ACTION_MAIN);
	    intent2.addCategory(Intent.CATEGORY_LAUNCHER);
	    // 删除的应用程序的ComponentName，即应用程序包名+activity的名字 
	    intent2.setComponent(new ComponentName(context.getPackageName(), context.getPackageName() + "." + activityName));
	    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
	    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context,
	    R.drawable.ic_launcher));
	    context.sendBroadcast(intent); 
	}

}
