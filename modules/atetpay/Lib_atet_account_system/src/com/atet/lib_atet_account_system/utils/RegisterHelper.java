package com.atet.lib_atet_account_system.utils;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atet.lib_atet_account_system.IATETUser;
import com.atet.lib_atet_account_system.http.RegisterHttpParams;
import com.atet.lib_atet_account_system.http.callbacks.RegisterCallback;
import com.atet.lib_atet_account_system.http.request.GsonRequest;
import com.atet.lib_atet_account_system.http.result.ResultCode;
import com.atet.lib_atet_account_system.model.LoginRespInfo;
import com.atet.lib_atet_account_system.params.Constant;
import com.atet.lib_atet_account_system.params.Constant.RETRY_TYPE;
import com.atet.lib_atet_account_system.params.UrlConstant;

/**
 * 用户注册操作的辅助类
 * 
 * @author zhaominglai
 * @date 2014/7/22
 * */
public class RegisterHelper {
	// 注册操作所需的参数对象
	public RegisterHttpParams mLoginParams;
	// 注册的网络请求对象
	public GsonRequest<LoginRespInfo> mRegisterRequest;

	/**
	 * 获取注册所需要的网络参数对象
	 * 
	 * @param deviceId
	 *            　设备ID号
	 * @param deviceCode
	 *            设备Code
	 * @param loginName
	 *            用户名
	 * @param pwd
	 *            用户密码
	 * @param email
	 *            邮箱
	 * @param phone
	 *            手机号码
	 * 
	 * @return RegisterHttpParmas　注册参数信息对象
	 * 
	 * */
	public RegisterHttpParams getRegisterParams(String atetId, String deviceId,
			String deviceCode, String productId, String channelId,
			String nickName, String loginName, String pwd, String email,
			String phone) {
		if (mLoginParams == null) {
			mLoginParams = new RegisterHttpParams();
		}
		mLoginParams.setAtetId(atetId);
		mLoginParams.setLoginName(loginName);
		mLoginParams.setPassword(cipherPwd(pwd));
		mLoginParams.setDeviceCode(deviceCode);
		mLoginParams.setDeviceId(deviceId);
		mLoginParams.setChannelId(channelId);
		mLoginParams.setProductId(productId);
		mLoginParams.setEmail(email);
		mLoginParams.setPhone(phone);
		mLoginParams.setNickName(nickName);

		return mLoginParams;
	}

	// 对密码进行加密
	private String cipherPwd(String pwd) {
		// TODO Auto-generated method stub
		try {
			return MD5CryptoUtils.toMD5(pwd.getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取用于注册的网络请求
	 * 
	 * @author zhaominglai
	 * @param LoginParams
	 *            注册用的登陆参数对象
	 * @param registerCallback
	 *            注册页面的回调对象
	 * @param user
	 *            ATETUser对象
	 * */
	public GsonRequest<LoginRespInfo> getRegRequest(
			RegisterHttpParams LoginParams,
			final RegisterCallback registerCallback, final IATETUser user,
			final int count) {
		String urlReg = UrlConstant.URL_REG1;

		if (count == Constant.SECOND_CONNECT_TRY)
			urlReg = UrlConstant.URL_REG2;
		else if (count == Constant.THIRD_CONNECT_TRY)
			urlReg = UrlConstant.URL_REG3;
		/* if (mRegisterRequest == null) */
		mRegisterRequest = new GsonRequest<LoginRespInfo>(LoginRespInfo.class,
				new Response.Listener<LoginRespInfo>() {
					// 注册成功时的回调
					@Override
					public void onResponse(LoginRespInfo response) {
						// TODO Auto-generated method stub
						LoginRespInfo respData = response;
						// 处理服务器端返回的数据
						handleResult(respData.getCode(), respData,
								registerCallback, user);

						Log.e("response", respData.toString());
					}
				}, urlReg, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if (count == Constant.FIRST_CONNECT_TRY)
							user.retryConnect(RETRY_TYPE.TYPE_REG);
						else if (count == Constant.SECOND_CONNECT_TRY)
							user.retryConnect(RETRY_TYPE.TYPE_REG1);
						else
							registerCallback.regError();
					}
				}, LoginParams);

		return mRegisterRequest;
	}

	/**
	 * 处理服务器返回的数据信息
	 * 
	 * */
	private void handleResult(int code, LoginRespInfo respData,
			RegisterCallback callBack, final IATETUser user) {
		switch (code) {
		case ResultCode.OK:
			// 如果注册成功，则将助用户信息保存到本地
			user.setUserInfo(respData);
			callBack.regSuccessed();
			break;

		case ResultCode.REG_USER_ALREADY_EXIST:
			// 用户名已经存在的情况
			callBack.userIsExist();
			break;

		default:
			// 注册失败
			callBack.regFailed(code);
			break;
		}
	}

}
