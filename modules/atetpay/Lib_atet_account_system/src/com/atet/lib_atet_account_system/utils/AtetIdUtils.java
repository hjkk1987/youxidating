package com.atet.lib_atet_account_system.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 获取设备信息，统计工具类
 * 
 * 
 * @author zhaominglai
 * @date 2014/6/25
 * 
 * */
public class AtetIdUtils {

	/**
	 * 用于统计任务的时间
	 * */
	public static long STATISTICS_TIME_DEVICE = 0;
	public static long STATISTICS_TIME_GAME = 0;

	/**
	 * 用于更新统计的时间，在STATISTICS_TIME_DEVICE的基础上每次加5分钟，
	 * 
	 * */
	public static synchronized long updateStatisticTimeDevice() {
		if (STATISTICS_TIME_DEVICE == 0) {
			STATISTICS_TIME_DEVICE = getServerTime();
			System.out.println("获取网络时间。" + STATISTICS_TIME_DEVICE);
		}

		STATISTICS_TIME_DEVICE += 5 * 60 * 1000;
		return STATISTICS_TIME_DEVICE;
	}

	/**
	 * 用于更新统计的时间，在STATISTICS_TIME_DEVICE的基础上每次加5分钟，
	 * 
	 * */
	public static synchronized long updateStatisticTimeGAME() {
		if (STATISTICS_TIME_GAME == 0)
			STATISTICS_TIME_GAME = getServerTime();

		STATISTICS_TIME_GAME += 5 * 60 * 1000;
		return STATISTICS_TIME_GAME;
	}

	/*	*//**
	 * 获取userId
	 * 
	 * @author zhaominglai
	 * */
	/*
	 * public static int getUserId(Context context) { // TODO Auto-generated
	 * method stub SharedPreferences sp =
	 * context.getSharedPreferences(Constant.LOGIN_USER_SP,
	 * Context.MODE_PRIVATE); return sp.getInt(Constant.LOGIN_USER_ID, 0); }
	 */

	/**
	 * 获取channelId
	 * */
	public static String getChannelId(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"deviceInfo", context.MODE_WORLD_READABLE);
		return preferences.getString("DEVICE_CHANNEL_ID", "1");
	}

	/**
	 * 获取atetId
	 * */
	public static String getAtetId(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"deviceInfo", Context.MODE_PRIVATE);
		return preferences.getString("DEVICE_ATET_ID", "1");
	}

	/**
	 * 保存atetId
	 * */
	public static void saveAtetId(Context context, String atetId) {
		SharedPreferences preferences = context.getSharedPreferences(
				"deviceInfo", Context.MODE_PRIVATE);
		preferences.edit().putString("DEVICE_ATET_ID", atetId).commit();
	}

	/**
	 * 获取gpu信息
	 * */
	public static String getGpuInfo(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("gpuInfo",
				context.MODE_WORLD_READABLE);
		return preferences.getString("gpu_render", "no");
	}

	/**
	 * 保存gpu信息
	 * */
	public static void setGpuInfo(Context context, String gpuInfo) {
		SharedPreferences preferences = context.getSharedPreferences("gpuInfo",
				context.MODE_WORLD_READABLE);
		preferences.edit().putString("gpu_render", gpuInfo);
	}

	public static int getDeviceType(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"deviceInfo", context.MODE_WORLD_READABLE);
		return preferences.getInt("DEVICE_TYPE", 1);// tv类型，这个版本为过渡版，所以在这里固定方法
	}

	/**
	 * 生成deviceId， deviceId由productId和deviceCode经md5加密成16位的字符串
	 * 
	 * 
	 * @modify yingdangchao
	 * @description 更改获取deviceId未服务器返回的deviceId
	 * */
	public static String getDeviceId(Context context, ContentResolver resolver) {
		SharedPreferences preferences = context.getSharedPreferences(
				"deviceInfo", context.MODE_WORLD_READABLE);
		return preferences.getString("DEVICE_DEVICE_ID", "");
		// return BaseApplication.mDeviceId;

		/*
		 * StringBuffer sb = new StringBuffer(); sb.append(getProductId(context,
		 * resolver)); sb.append(getDeviceCode()); String deviceId = null; try {
		 * deviceId = toMD5(sb.toString().getBytes()); } catch (Exception e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * return deviceId;
		 */
	}

	public static boolean isHadUpdateHardWareInfo(Context context) {
		String IS_UPDATED = "isUpdated";
		SharedPreferences bootSP = context.getSharedPreferences("BOOT_INFO",
				context.MODE_PRIVATE);
		boolean isUpdate = bootSP.getBoolean(IS_UPDATED, false);
		return isUpdate;
	}

	/** 获取设备的productId */
	public static String getProductId(Context context, ContentResolver resolver) {

		return Utils.getDNumber(context, resolver);
	}

	public static String getDeviceCode() {
		return Utils.getClientType(null);
	}

	/**
	 * 从服务器上获取时间
	 * 
	 * */
	public static long getServerTime() {
		/*
		 * HttpReqParams param = new HttpReqParams();
		 * 
		 * TaskResult<TimeInfo> timeResult =
		 * HttpApi.getObject(StatisticsUrlConstant
		 * .HTTP_GET_SEVER_TIME,StatisticsUrlConstant
		 * .HTTP_GET_SEVER_TIME2,StatisticsUrlConstant.HTTP_GET_SEVER_TIME3,
		 * TimeInfo.class, param.toJsonParam());
		 * 
		 * if (timeResult.getCode() == 0 && timeResult.getData() != null) {
		 * Log.e
		 * ("getserver time ",getDateToString(timeResult.getData().getTime()));
		 * return timeResult.getData().getTime(); }
		 */
		return System.currentTimeMillis();
	}

	public static long getTime() {

		// 优先获取网络时间，网络时间获取失败后才获取本地时间
		/*
		 * long timeValue = getServerTime();
		 * 
		 * 
		 * 
		 * return timeValue == 0 ? System.currentTimeMillis() : timeValue;
		 */

		return System.currentTimeMillis();
	}

	/* 时间戳转换成字符窜 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateToString(long time) {
		Date d = new Date(time);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss ");
		return sf.format(d);
	}

	/**
	 * 获取应用名称
	 * 
	 * */
	public static String getGameName(Context context, String packageName) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
			return "";
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/* *//**
	 * 获取gameType
	 * 
	 * */
	/*
	 * public static String getGameType(Context context,String packageName) {
	 * List<SearchGameInfo> gameList1 =
	 * PersistentSynUtils.getModelList(SearchGameInfo.class,
	 * " packageName like \"" + packageName + "\""); List<GameInfo> gameList2 =
	 * PersistentSynUtils.getModelList(GameInfo.class, " packageName = \"" +
	 * packageName + "\""); List<ADInfo> gameList3 =
	 * PersistentSynUtils.getModelList(ADInfo.class, " packageName = \"" +
	 * packageName + "\"");
	 * 
	 * if (gameList1.size() > 0) return gameList1.get(0).getTypeName();
	 * 
	 * if (gameList2.size() > 0) return gameList2.get(0).getTypeName();
	 * 
	 * if (gameList3.size() > 0) return gameList3.get(0).getTypeName();
	 * 
	 * return "0"; }
	 */

	/**
	 * 获取设备分辨率
	 * */
	public static String getResolution(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;

		return screenWidth + "*" + screenHeight;
	}

	public static int getDpi(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();

		return dm.densityDpi;
	}

	/** 获取系统sdk版本号 */
	public static String getSDKVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getPlatformVersion(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info.versionName;
	}

	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	/**
	 * 返回当前时间，以秒计算
	 * 
	 * @author zhaominglai
	 * @date 2014/9/12
	 * */
	public static long getSecondTime() {
		return System.currentTimeMillis() / 1000;
	}

	public static String getBlueToothMac(Context context) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			return "no_bluetooth";
		}
		return adapter.getAddress();
	}

	public static String getCpuName() {

		if (android.os.Build.HARDWARE != null)
			return android.os.Build.HARDWARE;

		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);
			for (int i = 0; i < array.length; i++) {
			}
			return array[1];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "没有";
	}

	/**
	 * 获得SD卡总大小
	 * 
	 * @return
	 */
	public static String getSDTotalSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得机身内存总大小
	 * 
	 * @return
	 */
	public static String getRomTotalSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	public static String[] getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" };
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return cpuInfo;
	}

	public static String getTotalMemory(Context context) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
		} catch (IOException e) {
		}
		// return Formatter.formatFileSize(context, initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
		System.out.println("总运存--->>>" + initial_memory / (1024 * 1024));
		return Formatter.formatFileSize(context, initial_memory);
	}

}
