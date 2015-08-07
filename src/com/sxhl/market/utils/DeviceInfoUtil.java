package com.sxhl.market.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sxhl.market.model.entity.DeviceInfo;
import com.sxhl.statistics.utils.DeviceStatisticsUtils;

/**
 * 
 * @ClassName: DeviceInfoUtil
 * @Description:TODO(用来取1、设备型号 2、本机设备productId 3、服务器返回的deviceId
 *                             4、服务器返回的channelId)
 * @author wenfuqiang
 * @date: 2014-8-21 上午11:22:12
 */
public class DeviceInfoUtil {

	/**
	 * 
	 * @Title: getDeviceId
	 * @Description: TODO(获取设备的产品id)
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String getProductId(Context mContext, ContentResolver resolver) {

		return Utils.getDNumber(mContext, resolver);
	}

	/**
	 * 
	 * @Title: getDeviceId
	 * @Description: TODO(设备从服务器返回的设备id)
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String getDeviceId(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"deviceInfo", mContext.MODE_WORLD_READABLE);
		return preferences.getString("DEVICE_DEVICE_ID", "");
	}

	/**
	 * 
	 * @Title: getDeviceCode
	 * @Description: TODO(获取本机的设备的设备型号)
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String getDeviceCode(ContentResolver resolver) {
		return Utils.getClientType(resolver);
	}

	/**
	 * 
	 * @Title: getChannelId
	 * @Description: TODO(服务器返回的channelId)
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String getChannelId(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"deviceInfo", mContext.MODE_WORLD_READABLE);
		return preferences.getString("DEVICE_CHANNEL_ID", "0");
	}

	/**
	 * 
	 * @Title: getDeviceType
	 * @Description: TODO(获取设备的类型)
	 * @param: @param mContext
	 * @param: @return int: 1为TV,2为手机,3为平板
	 * @return: int
	 * @throws
	 */
	public static int getDeviceType(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"deviceInfo", mContext.MODE_WORLD_READABLE);
		return preferences.getInt("DEVICE_TYPE", 1);
	}
	
	
	
	
	/**
	 * 
	 * @Title: saveDeviceInfoToSP
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param: @param mContext 上下文
	 * @param: @param deviceInfo 设备的信息
	 * @return: void
	 * @throws
	 */
	public static void saveDeviceInfoToSP(Context mContext,
			DeviceInfo deviceInfo) {
		SharedPreferences preferences = mContext.getSharedPreferences(
				"deviceInfo", mContext.MODE_WORLD_READABLE);
		Editor editor = preferences.edit();
		try {
			editor.putBoolean("LOGIN_IS_REGISTER", true);
			if (!(deviceInfo.getChannelId() == null || "".equals(deviceInfo
					.getChannelId()))) {
				editor.putString("DEVICE_CHANNEL_ID", deviceInfo.getChannelId());
			}
			if (!(deviceInfo.getDeviceId() == null || "".equals(deviceInfo
					.getDeviceId()))) {
				editor.putString("DEVICE_DEVICE_ID", deviceInfo.getDeviceId());
			}
			if (!(deviceInfo.getType() == null || "".equals(deviceInfo
					.getType()))) {
				editor.putInt("DEVICE_TYPE", deviceInfo.getType());
			}
			editor.putBoolean("IS_ALREADY_GET_DEVICE_ID", true);
			editor.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			preferences = null;
			editor = null;
		}
	}
	
	public static boolean fetchAtetId(Context context){
	    return DeviceStatisticsUtils.fetchAtetId(context);
	}
}
