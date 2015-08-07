package com.atet.lib_atet_account_system.utils;

import java.net.URI;
import java.net.URL;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;



/**
 * @ClassName: NetUtil
 * @Description:
 * @author: Liuqin
 * @date 2013-3-1 下午2:19:36
 * 
 */
public class NetUtil {

	private static final String TAG = "NetUtil";

	private NetUtil() {
	}

	public static boolean isWifiOpen(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifinfo = conManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifinfo != null) {
			State wifi = wifinfo.getState();
			
			if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
				return true;
			}
		}

		try {
			State state = conManager.getNetworkInfo(
					ConnectivityManager.TYPE_ETHERNET).getState();
			if (State.CONNECTED == state || (State.CONNECTING == state)) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	/**
	 * 
	 * @Title: isNetworkAvailable
	 * @Description: TODO
	 * @param @param context
	 * @param @param isAllowConnecting
	 * @param @return
	 * @return boolean true表示网络正常 false表示网络异常
	 * @throws
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static boolean isNetworkAvailable(Context context,
			boolean isAllowConnecting) {
		// 获得网络系统连接服务
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// 获得WIFI状态
		State state;
		
		/*NetworkInfo info = manager.getActiveNetworkInfo();
		
		if (info == null)
			return false;
		
		state = info.getState();*/
		
		
		try {
		    state = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		    if (State.CONNECTED == state || (isAllowConnecting && State.CONNECTING == state)) {
		        return true;
		    }
		} catch (Exception e) {
		    // TODO: handle exception
		}

		// 获取MOBILE状态
		try {
		    state = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		    if (State.CONNECTED == state || (isAllowConnecting && State.CONNECTING == state)) {
		        return true;
		    }    
        } catch (Exception e) {
            // TODO: handle exception
        }

		try {
			state = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
					.getState();
			if (State.CONNECTED == state || (isAllowConnecting && State.CONNECTING == state)) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
	}



	/**
	 * 
	 * @Title: checkNetWorkStatus
	 * @Description: TODO 检查当前的网络是否可用
	 * @param @param context
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean checkNetWorkStatus(Context context) {
		boolean state;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) {
			state = true;
		} else {
			state = false;
		}
		return state;
	}
}
