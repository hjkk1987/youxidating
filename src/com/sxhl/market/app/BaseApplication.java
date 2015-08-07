package com.sxhl.market.app;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sxhl.market.model.database.DBAccess;
import com.sxhl.market.model.entity.DeviceInfo;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.model.net.http.HttpClient;
import com.sxhl.market.preferences.PreferencesHelper;
import com.sxhl.market.utils.DeviceTool;
import com.sxhl.market.utils.StringTool;
import com.sxhl.market.utils.Utils;
import com.sxhl.market.utils.asynCache.ImageWorker;
import com.sxhl.statistics.utils.DeviceStatisticsUtils;

public class BaseApplication extends Application {
	/** ApplicationContext实例 **/
	public static Context m_appContext = null;

	/** 已登录用户信息 */
	public static UserInfo m_loginUser = null;

	/** 新客户端版本url */
	public static String versionUrl = null;

	/** 网络请求 */
	public static HttpClient m_httpClient = null;

	/** 异常处理 */
	public static ExceptionHandler m_exceptionHandler = null;

	/** 线程池 */
	private static ExecutorService m_executor = null;

	/** 全局对象 */
	public static Object m_object = null;
	/** 数据库访问 */
	public static DBAccess m_sqlAccess = null;

	/** 上行view */
	public static LinearLayout m_uploadView = null;

	/** 下行view */
	public static LinearLayout m_downView = null;

	public static boolean installFlag = true;

	private static SharedPreferences preferences;

	// 设备标识或品牌
	public static String mDeviceCode;
	// 设备唯一标识
	public static String mDeviceId;
	// 服务器返回设备信息
	public static DeviceInfo deviceInfo;
	public static boolean isGetDeviceId = false;

	public static float sScreenWZoom;// 宽度的缩放比例
	public static float sScreenHZoom;// 高度的缩放比例
	// 屏的宽度
	public static int mScreenWidth;
	// 屏幕的高度
	public static int mScreenHeight;
	/** 登录标记 */
	private static boolean isLogin;
	public static String gpuInfo;
	public static String atetId;
	public static HashMap<String, Boolean> isUnistalledMap = new HashMap<String, Boolean>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		m_appContext = getApplicationContext();
		m_exceptionHandler = new ExceptionHandler(m_appContext);

		mDeviceCode = Utils.getClientType(null);
		mDeviceId = getDeviceId();
		getDeviceInfo();

		// 如果用户登陆过，并且没有注销，则帮组用户自动登录
		PreferencesHelper prefsHelper = new PreferencesHelper(this);
		if (!StringTool.isEmpty(prefsHelper.getValue("LOGIN_USER_NAME"))) {
			// // 从数据库中获取用户信息（sw）
			// Group<UserInfo> userInfos = PersistentSynUtils.getModelList(
			// UserInfo.class, " id > 0");
			// if (null != userInfos && userInfos.size() > 0) {
			setLoginUser(getUserFromSp());
			// }
		}

		new MediaCardStateBroadcastReceiver().register();

		// initAtetPay();

		ImageWorker.clearOrderImageCache(this);
		initImageLoader(getApplicationContext());
	}

	public static UserInfo getLoginUser() {
		return m_loginUser;
	}

	public static void setLoginUser(UserInfo loginUser) {
		BaseApplication.m_loginUser = loginUser;
		if (loginUser == null) {
			return;
		}
		SharedPreferences accountSp = m_appContext.getSharedPreferences(
				"account", MODE_PRIVATE);
		Editor edit = accountSp.edit();
		edit.putString("uid", loginUser.getUserName());
		edit.putString("LOGIN_USER_NAME", loginUser.getUserName());
		edit.putString("LOGIN_USER_EMAIL", loginUser.getEmail());
		edit.putString("LOGIN_USER_ICON", loginUser.getAvator());
		edit.putString("LOGIN_USER_PHONE", loginUser.getPhone());
		try {
			int userId = Integer.parseInt(loginUser.getUserId());
			edit.putInt("LOGIN_USER_ID", userId);
		} catch (NumberFormatException e) {
			edit.putInt("LOGIN_USER_ID", 0);
		}
		edit.putString("nickName", loginUser.getNickName());
		edit.commit();
	}

	public static UserInfo getUserFromSp() {
		// TODO Auto-generated method stub
		SharedPreferences accountSp = m_appContext.getSharedPreferences(
				"account", MODE_PRIVATE);
		String uid = accountSp.getString("uid", null);
		String userName = accountSp.getString("LOGIN_USER_NAME", null);
		String userEmail = accountSp.getString("LOGIN_USER_EMAIL", null);
		String userIcon = accountSp.getString("LOGIN_USER_ICON", null);
		String userPhone = accountSp.getString("LOGIN_USER_PHONE", null);
		int userId = accountSp.getInt("LOGIN_USER_ID", 0);
		String nickName = accountSp.getString("nickName", null);
		boolean isAlreadyLogin = accountSp.getBoolean(
				"LOGIN_USER_ALREADY_LOGIN", false);
		if (isAlreadyLogin) {
			UserInfo userInfo = new UserInfo();
			userInfo.setNickName(nickName);
			userInfo.setUserId(userId + "");
			userInfo.setUserName(userName);
			userInfo.setUid(uid);
			userInfo.setAvator(userIcon);
			userInfo.setEmail(userEmail);
			userInfo.setPhone(userPhone);
			return userInfo;
		}
		return null;
	}

	/** 程序异常,显示提示信息 */
	public static void handleException(Exception e) {
		m_exceptionHandler.handle(e);
	}

	/**
	 * BaseApplication.getExecutorService().execute(Runnable);
	 * 
	 * @return 多线程执行器
	 */
	public static ExecutorService getExecutorService() {
		if (m_executor == null) {
			m_executor = Executors
					.newFixedThreadPool(Configuration.MAX_THREAD_POOL_SIZE);
		}
		return m_executor;
	}

	/**
	 * @author wsd
	 * @Description:获取数据库
	 * @date 2012-12-12 下午12:52:36
	 */
	public static DBAccess getSqlInstance() {
		if (m_sqlAccess == null) {
			m_sqlAccess = DBAccess.getInstance(m_appContext);
		}
		return m_sqlAccess;
	}

	public static String getGpuInfo() {
		return gpuInfo;
	}

	public static void setGpuInfo(String gpuInfo) {
		BaseApplication.gpuInfo = gpuInfo;
		DeviceStatisticsUtils.setGpuInfo(m_appContext, gpuInfo);
	}

	/**
	 * 获取网络请求管理器.
	 * 
	 * @return
	 */
	public static HttpClient getHttpClient() {
		if (m_httpClient == null) {
			m_httpClient = new HttpClient(m_appContext);
		}
		return m_httpClient;
	}

	/**
	 * 设置HttpClient
	 * 
	 * @param httpClient
	 */
	public static void setHttpClient(HttpClient httpClient) {
		BaseApplication.m_httpClient = httpClient;
	}

	@Override
	public void onLowMemory() {

		/** 当系统内存不足时清理一些图片缓存 */
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {

		/* 关闭网络连接 */
		m_httpClient.shutdown();

		/* 关闭数据库连接 */
		// mDBAccess.close ();
		super.onTerminate();
		Log.w("MediaCardStateBroadcastReceiver", "onTerminate");
		// releaseAtetPay();
	}

	public static boolean isLogin() {
		return isLogin;
	}

	public static void setLogin(boolean isLogin) {
		BaseApplication.isLogin = isLogin;
	}

	private String getDeviceId() {
		String deviceId = Utils.getDNumber(this, null);
		return deviceId;
	}

	/** 从数据库中获取deviceInfo的信息 */
	private void getDeviceInfo() {
		preferences = m_appContext.getSharedPreferences(
				Constant.DEVICE_INFO_SP, Context.MODE_WORLD_READABLE);
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setChannelId(preferences.getString(
				Constant.DEVICE_CHANNEL_ID, "0"));
		deviceInfo.setDeviceId(preferences.getString(Constant.DEVICE_DEVICE_ID,
				""));
		deviceInfo.setType(preferences.getInt(Constant.DEVICE_TYPE, 2));
		BaseApplication.deviceInfo = deviceInfo;
	}

	private class MediaCardStateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
				initSdMounted();
			}
		}

		public void register() {
			try {
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
				intentFilter.addDataScheme("file");
				registerReceiver(this, intentFilter);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		private void initSdMounted() {
			String sdRoot = DeviceTool.getSDPath();
			if (StringTool.isEmpty(sdRoot)) {
				return;
			}
			if (Constant.SDCARD_ROOT.equals(sdRoot)) {
				return;
			}
			Constant.SDCARD_ROOT = sdRoot;
			// 游戏下载保存目录
			Constant.GAME_DOWNLOAD_LOCAL_DIR = Constant.SDCARD_ROOT
					+ "/sxhl/market/";
			// 游戏数据包存放目录
			Constant.GAME_ZIP_DATA_LOCAL_DIR = Constant.SDCARD_ROOT + "/";
		}
	}

	// private RequestQueue mQueue;
	// public RequestQueue getRequestQueue(){
	// return mQueue;
	// }
	// private void initAtetPay(){
	// File diskCacheDir = new File(getCacheDir(), "netroid");
	// int diskCacheSize = 50 * 1024 * 1024; // 50MB
	// mQueue = Netroid.newRequestQueue(getApplicationContext(), new
	// DiskCache(diskCacheDir, diskCacheSize));
	// }
	// private void releaseAtetPay(){
	// mQueue.stop();
	// }

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(100 * 1024 * 1024) // 100 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
