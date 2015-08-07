package com.atet.lib_atet_account_system.utils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atet.lib_atet_account_system.http.GetDeviceInfoHttpParams;
import com.atet.lib_atet_account_system.http.callbacks.GetDeviceInfoCallback;
import com.atet.lib_atet_account_system.http.request.GsonRequest;
import com.atet.lib_atet_account_system.http.result.ResultCode;
import com.atet.lib_atet_account_system.model.DeviceRespInfo;
import com.atet.lib_atet_account_system.params.UrlConstant;

/**
 * 获取设备信息辅助类
 * 
 * @author zhaominglai
 * @date 2014/7/31
 * 
 * */
public class GetDeviceInfoHelper {

	/** 获取设备信息参数类 */
	public GetDeviceInfoHttpParams mGetDevInfoParams;

	/** 获取设备信息的Request类请求 */
	public GsonRequest<DeviceRespInfo> mLoginRequest;

	/**
	 * 获取请求参数类
	 * 
	 * @author zhaominglai
	 * @param channelId
	 *            渠道ID号
	 * @param productId
	 *            原始的设备ID号
	 * @param deviceCode
	 *            设备类型代号
	 * */
	public GetDeviceInfoHttpParams getDevInfoParams(String channelId,
			String productId, String deviceCode, int deviceType) {
		if (mGetDevInfoParams == null)
			mGetDevInfoParams = new GetDeviceInfoHttpParams();

		mGetDevInfoParams.setChannelId(channelId);
		mGetDevInfoParams.setDeviceCode(deviceCode);
		mGetDevInfoParams.setProductId(productId);
		mGetDevInfoParams.setType(deviceType);
		return mGetDevInfoParams;
	}

	/**
	 * 获取设备信息的网络请求
	 * 
	 * @param mGetDevInfoParams
	 *            网络请求参数类
	 * @param callBack
	 *            获取设备信息的回调对象
	 * */
	public GsonRequest<DeviceRespInfo> getDevInfoRequest(
			GetDeviceInfoHttpParams mGetDevInfoParams,
			final GetDeviceInfoCallback callBack) {
		if (mLoginRequest == null)
			mLoginRequest = new GsonRequest<DeviceRespInfo>(
					DeviceRespInfo.class,
					new Response.Listener<DeviceRespInfo>() {

						@Override
						public void onResponse(DeviceRespInfo response) {
							// TODO Auto-generated method stub
							DeviceRespInfo respData = response;

							checkGetDeviceInfoResult(respData.getCode(),
									callBack, respData);

							System.out.println(respData.toString());
						}
					}, UrlConstant.URL_DEVINFO, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							callBack.getInfoError();
						}
					}, mGetDevInfoParams);

		return mLoginRequest;
	}

	/** 处理后台返回过来的结果信息 */
	private void checkGetDeviceInfoResult(int code,
			GetDeviceInfoCallback callBack, DeviceRespInfo respData) {
		switch (code) {
		case ResultCode.OK:
			/** 获取设备信息成功 */
			callBack.getInfoSuccessed(respData);
			break;

		default:
			/** 获取设备信息失败 */
			callBack.getInfoFailed(code);
			break;
		}

	}

}
