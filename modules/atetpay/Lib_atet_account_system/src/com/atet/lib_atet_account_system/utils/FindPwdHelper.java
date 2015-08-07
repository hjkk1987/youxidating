package com.atet.lib_atet_account_system.utils;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.atet.lib_atet_account_system.IATETUser;
import com.atet.lib_atet_account_system.http.FindPwdHttpParams;
import com.atet.lib_atet_account_system.http.callbacks.FindPwdCallback;
import com.atet.lib_atet_account_system.http.request.GsonRequest;
import com.atet.lib_atet_account_system.http.result.ResultCode;
import com.atet.lib_atet_account_system.model.FindPwdRespInfo;
import com.atet.lib_atet_account_system.params.Constant;
import com.atet.lib_atet_account_system.params.Constant.RETRY_TYPE;
import com.atet.lib_atet_account_system.params.UrlConstant;


/**
 * 找回密码功能辅助类
 * 
 * @author zhaominglai
 * @date 2014/8/4
 * 
 * */
public class FindPwdHelper {
	//找回密码的网络请求参数对象
	public  FindPwdHttpParams mFindParams;
	
	//找回密码的网络请求对象
	public  GsonRequest<FindPwdRespInfo> mFindRequest;
	
	/**
	 * 获取找回密码的参数对象
	 * 
	 * @author zhaominglai
	 * @param loginName 用户名
	 * @param email 邮箱地址
	 * @return FindPwdHttpParams  找回密码的网络请求参数对象
	 * */
	public  FindPwdHttpParams getFindPwdParams(String loginName,String email)
	{
		
		if (mFindParams == null)
			mFindParams = new FindPwdHttpParams();
		
		mFindParams.setLoginName(loginName);
		mFindParams.setEmail(email);
		
		return mFindParams;
	}

	/**
	 * 获取找回密码的网络请求
	 * 
	 * @param findParams 找回密码的参数对象
	 * @param callBack 找回密码界面的回调对象
	 * 
	 * */
	public  GsonRequest<FindPwdRespInfo> getFindPwdRequest(FindPwdHttpParams findParams,final FindPwdCallback callBack,final IATETUser user,final int count)
	{
		String urlFindPwd = UrlConstant.URL_FINDPWD1;
		
		if (count == Constant.SECOND_CONNECT_TRY)
			urlFindPwd = UrlConstant.URL_FINDPWD2;
		else if(count == Constant.THIRD_CONNECT_TRY)
			urlFindPwd = UrlConstant.URL_FINDPWD3;
		
		
		/*if (mFindRequest == null)*/
			mFindRequest = new GsonRequest<FindPwdRespInfo>(FindPwdRespInfo.class, new Response.Listener<FindPwdRespInfo>() {
				//操作成功时的回调
				@Override
				public void onResponse(FindPwdRespInfo response) {
					// TODO Auto-generated method stub
					FindPwdRespInfo respData = response;
					
					checkResult(respData.getCode(), callBack);
					
					System.out.println(respData.toString());
				}
			},urlFindPwd,new Response.ErrorListener() {
				//操作异常时的回调函数
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					if (count == Constant.FIRST_CONNECT_TRY)
						user.retryConnect(RETRY_TYPE.TYPE_FINDPWD);
					else if (count == Constant.SECOND_CONNECT_TRY)
						user.retryConnect(RETRY_TYPE.TYPE_FINDPWD1);
					else
						callBack.findError();
				}
			}, findParams);
		
		return mFindRequest;
	}
	
	
	/**
	 * 检查从本次操作的结果
	 * 
	 * @author zhaominglai
	 * @date 2014/8/5
	 * 
	 * */
	private  void checkResult(int code,FindPwdCallback callBack)
	{
		switch(code)
		{
		case ResultCode.OK:
			//找回密码成功
			callBack.findSuccessed();
			break;
			
		case ResultCode.USER_IS_NOT_EXIST:
			//后台验证用户名不存在
			callBack.userIsNotExist();
			break;
			
		case ResultCode.PARAMS_IS_NOT_MATCH:
			//后台验证邮箱地址不匹配
			callBack.emailIsNotMatch();
			break;
			
		default:
			//其它失败的情况
			callBack.findFailed(code);
			break;
		}
		
	}
	
	
}
