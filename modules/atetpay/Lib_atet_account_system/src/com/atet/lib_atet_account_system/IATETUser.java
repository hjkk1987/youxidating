package com.atet.lib_atet_account_system;

import com.atet.lib_atet_account_system.http.callbacks.FindPwdCallback;
import com.atet.lib_atet_account_system.http.callbacks.LoginCallback;
import com.atet.lib_atet_account_system.http.callbacks.RegisterCallback;
import com.atet.lib_atet_account_system.model.LoginRespInfo;
import com.atet.lib_atet_account_system.params.Constant;

/**
 * ATET用户系统所需要的接口规范
 * 
 * @author zhaominglai
 * @date 2014/7/30
 * 
 * */
public interface IATETUser {

	/** 登录 */
	public boolean login(String loginName, String pwd,
			LoginCallback loginCallBack);

	/** 注册 */
	boolean register(String atetId, String nickName, String loginName,
			String pwd, String rePwd, String email, String phone,
			RegisterCallback registerCallback);

	/** 找回密码 */
	public boolean findPassword(String userName, String email,
			FindPwdCallback findCallback);

	/** 获取用户的UserId */
	public String getUserId();

	/** 设置用户信息 */
	public void setUserInfo(LoginRespInfo userInfo);

	public void retryConnect(Constant.RETRY_TYPE type);

}
