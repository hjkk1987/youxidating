package com.sxhl.statistics.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.NetUtil;
import com.sxhl.market.utils.Utils;
import com.sxhl.statistics.bases.StatisticsConstant;
import com.sxhl.statistics.bases.StatisticsUrlConstant;
import com.sxhl.statistics.model.InstalledInfo;
import com.sxhl.statistics.model.TimeInfo;
import com.sxhl.statistics.net.StatisticsTaskResult;

/**
 * 获取设备信息，统计工具类
 * 
 * 
 * @author zhaominglai
 * @date 2014/6/25
 * 
 * */
public class DeviceStatisticsUtils {

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
	 * 获取cpId
	 * 
	 * */
	public static String getCpId(String packageName) {
		List<GameInfo> cpResult = PersistentSynUtils.getModelList(
				GameInfo.class, " packageName = \"" + packageName + "\"");
		if (cpResult.size() > 0)
			return cpResult.get(0).getCpId();
		return "3";
	}

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
				context.MODE_PRIVATE);
		return preferences.getString("gpu_render", null);
	}

	/**
	 * 保存gpu信息
	 * */
	public static void setGpuInfo(Context context, String gpuInfo) {
		SharedPreferences preferences = context.getSharedPreferences("gpuInfo",
				context.MODE_PRIVATE);
		preferences.edit().putString("gpu_render", gpuInfo);
	}

	/**
	 * 去服务器获取atetId
	 * 
	 * @author zhaominglai
	 * @date 2014/9/30
	 * */
	public static synchronized boolean fetchAtetId(Context context) {
		SharedPreferences bootSP = context.getSharedPreferences("BOOT_INFO",
				Context.MODE_PRIVATE);

		int isFirstUpload = 0;
		// 如果存在atetId则不再进行操作
		if (bootSP.getBoolean("hasAtetId", false)) {
			return true;
		}

		// 判断之前有没有上传过装机量.0表示没有,1表示有.
		if (bootSP.getBoolean("isUpdated", false)) {
			isFirstUpload = 1;
		}

		String channelId = DeviceStatisticsUtils.getChannelId(context);

		// 如果没有网络就返回
		if (!NetUtil.isNetworkAvailable(context, true)) {
			DebugTool.info("UpdateHardwareServer", "向网络发送装机量信息,无网络失败。");
			// 生成操作日志到本地
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "统计装机量服务";
				StringBuilder sb = new StringBuilder();
				sb.append("上传设备信息失败！无有效的网络连接。");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(context, name, msg);
			}
			return false;
		}

		// 如果不存在有效的channelId号则取消本次操作，等下一次再进行上传
		if (channelId.equals("0") || channelId.equals("1") || channelId == null) {
			// 去服务器获取channelId deviceId信息，并在下一次启动的时候再来读取。
			DeviceInfoHelper.getDeviceInfoFromNet(
					context,
					"0",
					DeviceStatisticsUtils.getDeviceCode(),
					DeviceStatisticsUtils.getProductId(context,
							context.getContentResolver()));
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "统计装机量服务";
				StringBuilder sb = new StringBuilder();
				sb.append("当前无有效的channelId，再尝试去服务器获取。");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(context, name, msg);
			}
			return false;
		}

		HttpReqParams params = new HttpReqParams();

		InstalledInfo info = new InstalledInfo();

		info.setDeviceCode(DeviceStatisticsUtils.getDeviceCode());
		info.setDeviceId(DeviceStatisticsUtils.getDeviceId(context,
				context.getContentResolver()));
		info.setDeviceType(DeviceStatisticsUtils.getDeviceType(context));
		info.setChannelId(DeviceStatisticsUtils.getChannelId(context));
		info.setBlueToothMac(DeviceStatisticsUtils.getBlueToothMac(context));
		info.setProductId(DeviceStatisticsUtils.getProductId(context,
				context.getContentResolver()));
		info.setCpu(DeviceStatisticsUtils.getCpuName());
		if (DeviceStatisticsUtils.getGpuInfo(context) != null) {
			info.setGpu(DeviceStatisticsUtils.getGpuInfo(context));
		} else {
			info.setGpu("nogpuinfo");

		}
		info.setRam(DeviceStatisticsUtils.getTotalMemory(context));
		info.setResolution(DeviceStatisticsUtils.getResolution(context));
		info.setRom(DeviceStatisticsUtils.getRomTotalSize(context));
		info.setSdkVersion(DeviceStatisticsUtils.getSDKVersion());
		info.setSdCard(DeviceStatisticsUtils.getSDTotalSize(context));
		info.setPackageName(context.getPackageName());
		info.setVersionCode(DeviceStatisticsUtils.getPlatformVersion(context));
		info.setDpi(DeviceStatisticsUtils.getDpi(context));
		info.setInstallTime(System.currentTimeMillis());
		info.setIsFirstUpload(isFirstUpload);

		DebugTool.info("UpdateHardwareServer", "向网络发送装机量信息");
		params.setDeviceId(info.getDeviceId());
		params.setDeviceCode(info.getDeviceCode());
		params.setDeviceType(info.getDeviceType());
		params.setProductId(info.getProductId());
		params.setPackageName(info.getPackageName());
		params.setChannelId(info.getChannelId());
		params.setCpu(info.getCpu());
		params.setBlueToothMac(info.getBlueToothMac());
		params.setGpu(info.getGpu());
		params.setRom(info.getRom());
		params.setRam(info.getRam());
		params.setResolution(info.getResolution());
		params.setSdCard(info.getSdCard());
		params.setSdkVersion(info.getSdkVersion());
		params.setVersionCode(info.getVersionCode());
		params.setDpi(info.getDpi());
		params.setInstallTime(info.getInstallTime());
		params.setIsFirstUpload(isFirstUpload);

		TaskResult<InstalledInfo> result = HttpApi.getObject(
				StatisticsUrlConstant.HTTP_INSTALLED_INFOS1,
				StatisticsUrlConstant.HTTP_INSTALLED_INFOS2,
				StatisticsUrlConstant.HTTP_INSTALLED_INFOS3,
				InstalledInfo.class, params.toJsonParam());

		DebugTool.debug("UpdateHardwareServer",
				"UpLoadInstalledInfosTask  result.getcode=" + result.getCode()
						+ "  result.getdata=" + result.getData());

		if (result.getCode() == TaskResult.OK && result.getData() != null) {

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "统计装机量服务";
				StringBuilder sb = new StringBuilder();
				sb.append("上传设备信息成功");
				sb.append("\r\n" + info.toString() + "\r\n");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(context, name, msg);
			}

			bootSP = context.getSharedPreferences("BOOT_INFO",
					Context.MODE_PRIVATE);
			bootSP.edit().putBoolean("isUpdated", true)
					.putBoolean("hasAtetId", true).commit();
			DeviceStatisticsUtils.saveAtetId(context, result.getData()
					.getAtetId());

			return true;
		} else if (result.getCode() == TaskResult.FAILED) {

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "统计装机量服务";
				StringBuilder sb = new StringBuilder();
				sb.append("上传设备信息失败！服务器返回状态标志为失败！");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(context, name, msg);
			}

			return false;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_REQUSET_PARAM_ERR) {

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "统计装机量服务";
				StringBuilder sb = new StringBuilder();
				sb.append("上传设备信息失败！请求的参数发生错误");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(context, name, msg);
			}

			return false;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_SYSERR) {
			// 没有符合要求的数据

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "统计装机量服务";
				StringBuilder sb = new StringBuilder();
				sb.append("上传设备信息失败！服务器系统内部发生错误");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(context, name, msg);
			}
			return false;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_INVALIDATE_OP) {
			// 没有符合要求的数据

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "统计装机量服务";
				StringBuilder sb = new StringBuilder();
				sb.append("上传设备信息失败！非法操作");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(context, name, msg);
			}
			return false;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_REQUSET_JSON_ERR) {
			// 没有符合要求的数据

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "统计装机量服务";
				StringBuilder sb = new StringBuilder();
				sb.append("上传设备信息失败！服务器系统解析请求的json数据出错");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(context, name, msg);
			}
			return false;
		}

		return false;
	}

	public static int getDeviceType(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"deviceInfo", context.MODE_WORLD_READABLE);
		return preferences.getInt("DEVICE_TYPE", 2);// 2为手机类型
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
		return preferences.getString("DEVICE_DEVICE_ID",
				BaseApplication.mDeviceId);
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
		return BaseApplication.mDeviceCode;
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
			return "no";
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/**
	 * 获取gameType
	 * 
	 * */
	public static String getGameType(Context context, String packageName) {
		List<GameInfo> gameList2 = PersistentSynUtils.getModelList(
				GameInfo.class, " packageName = \"" + packageName + "\"");

		if (gameList2 != null && gameList2.size() > 0) {
			GameInfo mGameInfo = gameList2.get(0);
			int typeId = 0;
			try {
				typeId = Integer.parseInt(mGameInfo.getTypeId());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				typeId = 3;
			}

			if (typeId == 1) {
				return new String("史诗推荐");
			} else if (typeId == 2) {
				return new String("精品游戏");
			} else {
				return new String("专题游戏");
			}

		} else {
			List<MyGameInfo> gameList3 = PersistentSynUtils.getModelList(
					MyGameInfo.class, " packageName = \"" + packageName + "\"");
			if (gameList3 != null && gameList3.size() > 0) {
				return "史诗推荐";
			}
		}
		// return gameList2.get(0).getTypeName();

		return "专题游戏";
	}

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

	public static String toMD5(byte[] bytes) throws Exception {
		MessageDigest algorithm = MessageDigest.getInstance("MD5");

		algorithm.reset();

		algorithm.update(bytes);

		return toHexString(algorithm.digest(), "");
	}

	private static String toHexString(byte[] bytes, String separator) {

		StringBuilder hexString = new StringBuilder();

		for (byte b : bytes) {
			String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex).append(separator);
		}

		return hexString.toString();
	}

	/**
	 * 判断两个时间戳是不是同一天
	 * */
	public static boolean ifTwoTimeStampIsSameDay(long timeOld, long timeCurrent) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String old = sdf.format(timeOld);
		String cur = sdf.format(timeCurrent);

		return old.equals(cur) ? true : false;
	}

	/**
	 * 判断两个时间戳是不是相隔1天
	 * */
	public static boolean ifTwoTimeStampBetweenOneDay(long timeOld,
			long timeCurrent) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String old = sdf.format(timeOld);
		String cur = sdf.format(timeCurrent);

		long yesterDay = getTimeToday(old);
		long today = getTimeToday(cur);
		long timeValue = today - yesterDay;

		return timeValue == 24 * 60 * 60 * 1000 ? true : false;
	}

	/**
	 * 将时间戳转换成今天的日期
	 * 
	 * */
	public static String timeToDay(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(timeStamp);

		return date;
	}

	/**
	 * 获取游戏采集接口，上次上传成功的时间。
	 * */
	public static long getLastUploadTimeCollect(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				StatisticsConstant.SP_UPLOAD_TIMEINFOS, Context.MODE_PRIVATE);

		return sp.getLong(StatisticsConstant.SP_LASTUPLOAD_GAMECOLLECT, 0);
	}

	/**
	 * 获取userId
	 * 
	 * @author zhaominglai
	 * */
	public static int getUserId(Context context) {
		// TODO Auto-generated method stub
		SharedPreferences sp = context.getSharedPreferences("account",
				Context.MODE_PRIVATE);
		return sp.getInt("LOGIN_USER_ID", 0);
	}

	/**
	 * 设置游戏采集接口上传成功的时间
	 * */
	public static void setLastUploadTimeCollect(Context context, long time) {
		Log.e("collectGame", "" + time);
		SharedPreferences sp = context.getSharedPreferences(
				StatisticsConstant.SP_UPLOAD_TIMEINFOS, Context.MODE_PRIVATE);
		sp.edit().putLong(StatisticsConstant.SP_LASTUPLOAD_GAMECOLLECT, time)
				.commit();

	}

	/**
	 * 获取游戏平台在线时长信息，上次上传成功的时间。
	 * */
	public static long getLastUploadTimePlatForm(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				StatisticsConstant.SP_UPLOAD_TIMEINFOS, Context.MODE_PRIVATE);

		return sp.getLong(StatisticsConstant.SP_LASTUPLOAD_PLATFORM, 0);
	}

	/**
	 * 设置游戏平台在线时长信息上传成功的时间
	 * */
	public static void setLastUploadTimePlatForm(Context context, long time) {
		Log.e("platFormGame", "" + time);
		SharedPreferences sp = context.getSharedPreferences(
				StatisticsConstant.SP_UPLOAD_TIMEINFOS, Context.MODE_PRIVATE);
		sp.edit().putLong(StatisticsConstant.SP_LASTUPLOAD_PLATFORM, time)
				.commit();

	}

	/**
	 * 获取游戏在线时长信息，上次上传成功的时间。
	 * */
	public static long getLastUploadTimeGameOnline(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				StatisticsConstant.SP_UPLOAD_TIMEINFOS, Context.MODE_PRIVATE);

		return sp.getLong(StatisticsConstant.SP_LASTUPLOAD_GAMEONLINE, 0);
	}

	/**
	 * 设置游戏在线时长信息上传成功的时间
	 * */
	public static void setLastUploadTimeGameOnline(Context context, long time) {
		Log.e("gameOnline", "" + time);
		SharedPreferences sp = context.getSharedPreferences(
				StatisticsConstant.SP_UPLOAD_TIMEINFOS, Context.MODE_PRIVATE);
		sp.edit().putLong(StatisticsConstant.SP_LASTUPLOAD_GAMEONLINE, time)
				.commit();

	}

	/**
	 * 获取gameId号
	 * */
	public static String getGameId(String packageName) {
		// TODO Auto-generated method stub

		List<GameInfo> listPlatFormGame = PersistentSynUtils.getModelList(
				GameInfo.class, " packageName = " + "\"" + packageName + "\"");

		if (listPlatFormGame.size() > 0)
			return listPlatFormGame.get(0).getGameId();

		return "1";
	}

	/**
	 * 获取游戏版权信息
	 * */
	public static Integer getCopyRight(String packageName) {
		// TODO Auto-generated method stub

		List<MyGameInfo> listPlatFormGame1 = PersistentSynUtils
				.getModelList(MyGameInfo.class, " packageName = " + "\""
						+ packageName + "\"");

		if (listPlatFormGame1.size() > 0)
			return StatisticsConstant.GAME_COPYRIGHT_PLATFORM;

		List<GameInfo> listPlatFormGame = PersistentSynUtils.getModelList(
				GameInfo.class, " packageName = " + "\"" + packageName + "\"");

		if (listPlatFormGame.size() > 0)
			return StatisticsConstant.GAME_COPYRIGHT_PLATFORM;

		/*
		 * List<GameInfo> listThirdGame =
		 * PersistentSynUtils.getModelList(SearchGameInfo.class,
		 * " packageName = "+"\""+packageName + "\"");
		 * 
		 * if (listThirdGame.size() > 0) return
		 * StatisticsConstant.GAME_COPYRIGHT_THIRDPARTY;
		 */

		return StatisticsConstant.GAME_COPYRIGHT_OTHER;
	}

	public static String getVersionCode(String packageName) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getVersionName(Context context, String packageName) {
		// TODO Auto-generated method stub

		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					packageName, 0);

			return pinfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "1";
	}

	/**
	 * 获取当天的凌晨的时间戳
	 * */
	public static long getTimeToday(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date.getTime();
	}

	/**
	 * 检测网络连接的类型。
	 * 
	 * @return 0没有网络连接　1　wifi网络　　2其他网络连接如GPRS等。
	 * 
	 * **/
	public static int checkNetworkType(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

		if (netInfo == null || !netInfo.isAvailable()) {
			// 没有网络连接。
			return StatisticsConstant.NET_TYPE_NULL;
		}

		if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return StatisticsConstant.NET_TYPE_WIFI;
		}

		return StatisticsConstant.NET_TYPE_OTHER;
	}

	/**
	 * 根据包名查询是不是atet平台下面的游戏.
	 * 
	 * */

	public static boolean isAtetGame(String packageName) {
		List<GameInfo> listPlatFormGame = PersistentSynUtils.getModelList(
				GameInfo.class, " packageName = " + "\"" + packageName + "\"");

		List<MyGameInfo> listPlatFormGame1 = PersistentSynUtils
				.getModelList(MyGameInfo.class, " packageName = " + "\""
						+ packageName + "\"");

		if (listPlatFormGame.size() > 0) {

			return true;
		}

		if (listPlatFormGame1.size() > 0) {
			for (MyGameInfo myGameInfo : listPlatFormGame1) {
				if (myGameInfo.getState() != Constant.GAME_STATE_INSTALLED_SYSTEM
						&& myGameInfo.getState() != Constant.GAME_STATE_INSTALLED_USER) {
					return true;
				}
			}
			return false;
		}

		return false;
	}

}
