package com.atet.lib_atet_account_system.http.callbacks;

import com.atet.lib_atet_account_system.model.DeviceRespInfo;


/**
 * 向云端获取设备信息的回调函数
 * 
 * @author zhaominglai
 * @date 2014年7月31日
 * 
 * */
public interface GetDeviceInfoCallback extends BaseCallback{
	
	/**获取设备信息成功*/
	public abstract void getInfoSuccessed(DeviceRespInfo respData);
	
	/**获取设备信息错误*/
	public abstract void getInfoError();
	
	/**获取设备信息失败，从服务器返回了错误代码*/
	public abstract void getInfoFailed(int backCode);

}
