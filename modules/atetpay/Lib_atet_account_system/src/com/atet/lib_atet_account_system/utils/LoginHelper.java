package com.atet.lib_atet_account_system.utils;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atet.lib_atet_account_system.IATETUser;
import com.atet.lib_atet_account_system.http.LoginHttpParams;
import com.atet.lib_atet_account_system.http.callbacks.LoginCallback;
import com.atet.lib_atet_account_system.http.request.GsonRequest;
import com.atet.lib_atet_account_system.http.result.ResultCode;
import com.atet.lib_atet_account_system.model.LoginRespInfo;
import com.atet.lib_atet_account_system.params.Constant;
import com.atet.lib_atet_account_system.params.Constant.RETRY_TYPE;
import com.atet.lib_atet_account_system.params.UrlConstant;


/**
 * 登陆操作的辅助类
 * 
 * @author zhaominglai
 * @date 2014/8/11
 * 
 * */
public class LoginHelper {
	
	public  LoginHttpParams mLoginParams;
	
	public  GsonRequest<LoginRespInfo> mLoginRequest;
	
	/**获取登陆参数对象
	 * @param loginName 用户名
	 * @param pwd 密码
	 * @param deviceType 设备类型
	 * 
	 * */
	public LoginHttpParams getLoginParams(String loginName,String pwd,int deviceType)
	{
		if (mLoginParams == null)
			mLoginParams = new LoginHttpParams();
		
		mLoginParams.setLoginName(loginName);
		mLoginParams.setPassword(cipherPwd(pwd));
		mLoginParams.setDeviceType(deviceType);
		
		return mLoginParams;
	}

	/**对密码进行加密操作*/
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
	 * 获取GsonRequest<LoginRespInfo>也就是登陆时的Request
	 * 
	 * @author zhaominglai
	 * @param LoginParams　登陆参数对象
	 * @param callBack 登陆操作的回调对象
	 * @param user ATET对象
	 * */
	public  GsonRequest<LoginRespInfo> getLoginRequest(LoginHttpParams LoginParams,final LoginCallback callBack,final IATETUser user,final int count)
	{
		/**默认第一个路径登陆进去*/
		String	urlLogin = UrlConstant.URL_LOGIN1;
		
		/**如果第一个IP域名无法解析导致不成功，进行第二次网络访问的时候则用第二个路径*/
		if (count == Constant.SECOND_CONNECT_TRY)
			urlLogin = UrlConstant.URL_LOGIN2;
		else if (count == Constant.THIRD_CONNECT_TRY)
			urlLogin = UrlConstant.URL_LOGIN3;
		
		System.out.println("login url:"+urlLogin);
		
		/*if (mLoginRequest == null)*/
			mLoginRequest = new GsonRequest<LoginRespInfo>(LoginRespInfo.class, new Response.Listener<LoginRespInfo>() {
				/**连接成功时的回调函数*/
				@Override
				public void onResponse(LoginRespInfo response) {
					// TODO Auto-generated method stub
					LoginRespInfo respData = response;
					//检测返回的数据
					checkLoginResult(respData.getCode(), callBack,user,respData);
					
					System.out.println(respData.toString());
				}
			}, urlLogin,new Response.ErrorListener() {
				/**连接失败时的回调函数*/
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					//调用callBack的loginError()回调函数，通知用户
					if (count == Constant.FIRST_CONNECT_TRY)
						user.retryConnect(RETRY_TYPE.TYPE_LOGIN);
					else if (count == Constant.SECOND_CONNECT_TRY)
						user.retryConnect(RETRY_TYPE.TYPE_LOGIN1);
					else
					{
						callBack.loginError();
						
					}
				}
			}, LoginParams);
		//mLoginRequest.setShouldCache(false);
		
		return mLoginRequest;
	}
	
	/**
	 * 检测后台返回的数据信息
	 * 
	 * @param code 后台返回的状态码
	 * @param callBack　登陆界面的回调对象
	 * @param user ATETUser回调对象
	 * @param userInfo　从返回的数据中解析出来的LoginRespInfo对象
	 * */
	private  void checkLoginResult(int code,LoginCallback callBack,IATETUser user,LoginRespInfo userInfo)
	{
		switch(code)
		{
		case ResultCode.OK:
			//保存登陆成功后的用户信息，并调用登陆界面的loginSuccessed()回调函数
			user.setUserInfo(userInfo);
			callBack.loginSuccessed();
			break;
		
		case ResultCode.USER_IS_NOT_EXIST:
			//用户名不存在的情况
			callBack.userIsNotExist();
			break;
			
		case ResultCode.LOGIN_PWD_IS_WRONG:
			//登陆密码错误的情况
			callBack.InvailedUserPwd();
			break;
			
		default:
			//其他操作失败的情况
			callBack.loginFailed(code);
			break;
		}
		
	}
	
	
	
	
}
