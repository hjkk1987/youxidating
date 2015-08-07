package com.sxhl.market.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;

/**
 * @ClassName: AppUtil
 * @Description:
 * @author: Liuqin
 * @date 2013-1-29 上午10:07:39
 * 
 */
public class AppUtil {
	/**
	 * @Title: startAppByPkgName
	 * @Description: 由package name启动应用
	 * @param context
	 * @param pkgName
	 * @return
	 * @throws
	 */
	public static boolean startAppByPkgName(Context context, String pkgName) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent();
		intent = packageManager.getLaunchIntentForPackage(pkgName);
		if (intent == null) {
			System.out.println("APP not found!");
			return false;
		}
		context.startActivity(intent);
		return true;
	}
	
	public static boolean startAppByActName(Context context,String pkgName,String actName){
		try{
			Intent intent = new Intent();   
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");
			intent.setComponent(new ComponentName(pkgName, actName));
			intent.setFlags(0x10200000);
			context.startActivity(intent);   	
		} catch (ActivityNotFoundException e) {
		   return false; 
		} catch (Exception e){
		    e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @Title: getApkInfoByPath
	 * @Description: 由路径得到app信息
	 * @param context
	 * @param absPath
	 * @return
	 * @throws
	 */
	public static ApplicationInfo getApkInfoByPath(Context context,
			String absPath) {
		ApplicationInfo appInfo = null;
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,
				PackageManager.GET_ACTIVITIES);
		if (pkgInfo != null) {
			appInfo = pkgInfo.applicationInfo;
			/* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
			appInfo.sourceDir = absPath;
			appInfo.publicSourceDir = absPath;
		}
		return appInfo;
	}

	/**
	 * @Title: getPkgInfoByName
	 * @Description: 由应用包名得到应用信息
	 * @param context
	 * @param pkgName
	 * @return
	 * @throws
	 */
	public static PackageInfo getPkgInfoByName(Context context, String pkgName) {
		PackageInfo pkgInfo = null;
		PackageManager pm = context.getPackageManager();
		try {
			// 0代表是获取版本信息
			pkgInfo = pm.getPackageInfo(pkgName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pkgInfo;
	}
	
	public static PackageInfo getPkgInfoByPath(Context context,String absPath){
		PackageInfo pkgInfo = null;
		PackageManager pm = context.getPackageManager();
		pkgInfo=pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
		return pkgInfo;
	}

	/**
	 * @Title: installApkByPath
	 * @Description: 由apk路径直接跳到安装界面
	 * @param context
	 * @param absPath
	 * @throws
	 */
	public static void installApkByPath(Context context, String absPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + absPath), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public static void uninstallApk(Context context,String packageName){
		String uriString = "package:"+packageName; 
		Uri uninstallUrl = Uri.parse(uriString); 
		Intent intent = new Intent(Intent.ACTION_DELETE, uninstallUrl); 
		context.startActivity(intent); 
	}
	
    public static List<ResolveInfo> queryAppInfo(Context context,String packageName) {  
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象  
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);  
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
        mainIntent.setPackage(packageName);
        // 通过查询，获得所有ResolveInfo对象.  
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, PackageManager.GET_INTENT_FILTERS);  
        return resolveInfos;
    }
    
    public static int getInstalledAppVersionCode(Context context,String pkgName){
		int versionCode=-1;
		PackageInfo pkgInfo=AppUtil.getPkgInfoByName(context, pkgName);
		if(pkgInfo!=null){
			versionCode=pkgInfo.versionCode;
		}
		return versionCode;
	}
    
    /**
	 * @Title: isAlreadyInstall
	 * @Description: 由package name判断程序是否已经安装应用
	 * @param context
	 * @param pkgName
	 * @return
	 * @throws
	 */
	public static boolean isAlreadyInstall(Context context, String pkgName) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
		// 从pinfo中将包名字逐一取出，压入pName list中
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(pkgName);
	}

}
