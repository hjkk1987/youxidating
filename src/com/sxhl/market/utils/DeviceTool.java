package com.sxhl.market.utils;

import java.io.File;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sxhl.market.app.Constant;

/**
 * @author time：2012-8-6 下午6:42:58 description:
 */
public class DeviceTool {
	/**
	 * 判断是否已连接到网络.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检查网络接连类型.
	 * 
	 * @param context
	 * @return SysConstants.NETWORK_TYPE_NONE: 无网络连接;
	 *         SysConstants.NETWORK_TYPE_WIFI: 通过WIFI连接网络;
	 *         SysConstants.NETWORK_TYPE_WAP: 通过GPRS连接网络.
	 */
	public static int checkNetWorkType(Context context) {
		if (isAirplaneModeOn(context)) {
			return Constant.NETWORK_TYPE_NONE;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState() == NetworkInfo.State.CONNECTED) {
			return Constant.NETWORK_TYPE_NET;
		}

		if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState() == NetworkInfo.State.CONNECTED) {
			String type = connectivityManager.getActiveNetworkInfo()
					.getExtraInfo();
			if ("wap".equalsIgnoreCase(type.substring(type.length() - 3))) {
				return Constant.NETWORK_TYPE_WAP;
			} else {
				return Constant.NETWORK_TYPE_NET;
			}
		}

		return Constant.NETWORK_TYPE_NONE;
	}

	/**
	 * 判断手机是否处于飞行模式.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAirplaneModeOn(Context context) {
		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}

	/**
	 * 获取设备序列号.
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String deviceId = null;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		if (deviceId == null || "".equals(deviceId)) {
			deviceId = "0000000000";
		}
		return deviceId;
	}

	/**
	 * 判断手机SDCard是否已安装并可读写.
	 * 
	 * @return
	 */
	public static boolean isSDCardUsable() {
		return Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState());
	}

	/**
	 * 获取指定的SDCard中图片缓存目录.
	 * 
	 * @param defaultImageFolderName
	 * @return
	 */
	public static File getImgCacheDir(String defaultImageFolderName) {
		if (isSDCardUsable()) {
			File dir = new File(Environment.getExternalStorageDirectory(),
					defaultImageFolderName);
			if (!dir.exists())
				dir.mkdirs();

			return dir;
		}
		return null;
	}

	/**
	 * 隐藏某焦点控件弹出的软件键盘.
	 * 
	 * @param context
	 * @param view
	 */
	public static void hideSoftKeyboardFromView(Context context, View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		IBinder binder = view.getWindowToken();
		inputMethodManager.hideSoftInputFromWindow(binder, 0);
	}
	
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equalsIgnoreCase(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            DebugTool.debug("sd dir:" + sdDir.getAbsolutePath());
        } else {
            sdDir = new File("/mnt/sdcard/");
            if (!sdDir.exists() || !sdDir.canRead() || !sdDir.canWrite()) {
                DebugTool.error("SD_PATH", "sd card not usable", null);
                return "";
            }
        }
        return sdDir.getAbsolutePath();
    }
    
    public static long getSdAvailableSpace(){
        long availableSpace=-1;;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equalsIgnoreCase(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            File file = new File(sdcard);
            StatFs statFs = new StatFs(file.getPath());
            availableSpace= (long) (statFs.getBlockSize()*((long)statFs.getAvailableBlocks()));
        }
        return availableSpace;
    }
    
    public static int getCallState(Context context){
        try{
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);  
            return tm.getCallState();
        } catch (Exception e){
            e.printStackTrace();
        }
        return TelephonyManager.CALL_STATE_IDLE;
    }
}
