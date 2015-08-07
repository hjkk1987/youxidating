package com.sxhl.statistics.utils;

import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.model.entity.DeviceInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.DeviceInfoUtil;

import android.content.Context;
import android.view.View;




/**
 * 获取device信息的辅助类，主要是对channelId、productId的获取。
 * 
 * @author zhaominglai
 * @date 2014/09/25
 * */
public class DeviceInfoHelper {

	
	/**
	 * 从网络上获取设备信息
	 * 
	 * */
	public static void getDeviceInfoFromNet(Context context,String channelId,String deviceCode,String productId)
	{
		
		HttpReqParams params = new HttpReqParams();
		params.setChannelId(channelId);
		params.setDeviceCode(deviceCode);
		params.setProductId(productId);
		
		TaskResult<DeviceInfo> result = HttpApi.getObject(UrlConstant.HTTP_GET_DEVICE_ID,UrlConstant.HTTP_GET_DEVICE_ID2,UrlConstant.HTTP_GET_DEVICE_ID3,
				DeviceInfo.class, params.toJsonParam());
		
		if (result.getCode() == TaskResult.DEVICE_ID_NO_DATA) {
			// 无匹配数据
		}
		if (result.getCode() == TaskResult.FAILED) {
			
		} else if (result.getCode() == TaskResult.OK) {
			// 成功获取数据
            DeviceInfo deviceInfo = result.getData();
	
			DeviceInfoUtil.saveDeviceInfoToSP(context, deviceInfo);
			// PersistentSynUtils.addModel(deviceInfo);// 将数据插入数据库
			BaseApplication.deviceInfo = deviceInfo;
			BaseApplication.isGetDeviceId = true;
		}
	}
}
