package com.atet.lib_atet_account_system.utils;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atet.lib_atet_account_system.http.AtetIdInfoHttpParams;
import com.atet.lib_atet_account_system.http.callbacks.GetAtetIdCallback;
import com.atet.lib_atet_account_system.http.request.GsonRequest;
import com.atet.lib_atet_account_system.http.result.ResultCode;
import com.atet.lib_atet_account_system.model.AtetIdInfo;
import com.atet.lib_atet_account_system.params.UrlConstant;

public class GetAtetIdInfoHelper {
	public AtetIdInfoHttpParams mAtetIdInfoParams;

	public GsonRequest<AtetIdInfo> mAtetIdRequest;

	public AtetIdInfoHttpParams getAtetIdInfoParams(Context context) {
		if (mAtetIdInfoParams == null)
			mAtetIdInfoParams = new AtetIdInfoHttpParams();

		mAtetIdInfoParams.setDeviceCode(AtetIdUtils.getDeviceCode());
		mAtetIdInfoParams.setDeviceId(AtetIdUtils.getDeviceId(context,
				context.getContentResolver()));
		mAtetIdInfoParams.setDeviceType(AtetIdUtils.getDeviceType(context));
		mAtetIdInfoParams.setChannelId(AtetIdUtils.getChannelId(context));
		mAtetIdInfoParams.setBlueToothMac(AtetIdUtils.getBlueToothMac(context));
		mAtetIdInfoParams.setProductId(AtetIdUtils.getProductId(context,
				context.getContentResolver()));
		mAtetIdInfoParams.setCpu(AtetIdUtils.getCpuName());
		mAtetIdInfoParams.setGpu("unknown gpu");
		mAtetIdInfoParams.setRam(AtetIdUtils.getTotalMemory(context));
		mAtetIdInfoParams.setResolution(AtetIdUtils.getResolution(context));
		mAtetIdInfoParams.setRom(AtetIdUtils.getRomTotalSize(context));
		mAtetIdInfoParams.setSdkVersion(AtetIdUtils.getSDKVersion());
		mAtetIdInfoParams.setSdCard(AtetIdUtils.getSDTotalSize(context));
		mAtetIdInfoParams.setPackageName(context.getPackageName());
		mAtetIdInfoParams.setVersionCode(AtetIdUtils
				.getPlatformVersion(context));
		mAtetIdInfoParams.setDpi(AtetIdUtils.getDpi(context));
		mAtetIdInfoParams.setInstallTime(System.currentTimeMillis());
		if (AtetIdUtils.isHadUpdateHardWareInfo(context)) {
			mAtetIdInfoParams.setIsFirstUpload(1);
		} else {
			mAtetIdInfoParams.setIsFirstUpload(0);
		}

		return mAtetIdInfoParams;
	}

	/**

	 * */
	public GsonRequest<AtetIdInfo> getAtetIdInfoRequest(
			AtetIdInfoHttpParams mAtetIdInfoParams,
			final GetAtetIdCallback callBack) {
		if (mAtetIdRequest == null)
			mAtetIdRequest = new GsonRequest<AtetIdInfo>(AtetIdInfo.class,
					new Response.Listener<AtetIdInfo>() {

						@Override
						public void onResponse(AtetIdInfo response) {
							// TODO Auto-generated method stub
							AtetIdInfo respData = response;

							checkGetDeviceInfoResult(respData.getCode(),
									callBack, respData);

							System.out.println(respData.toString());
						}
					}, UrlConstant.URL_ATETID, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							callBack.getAtetIdError();
						}
					}, mAtetIdInfoParams);

		return mAtetIdRequest;
	}

	/** 处理后台返回过来的结果信息 */
	private void checkGetDeviceInfoResult(int code, GetAtetIdCallback callBack,
			AtetIdInfo respData) {
		switch (code) {
		case ResultCode.OK:
			/** 获取设备信息成功 */
			callBack.getAtetIdSuccessed(respData);
			break;

		default:
			/** 获取设备信息失败 */
			callBack.getAtetIdFailed(code);
			break;
		}

	}
}
