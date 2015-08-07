package com.atet.lib_atet_account_system.http.callbacks;


/**
 * 登录操作的各种回调函数，代表了登录之后返回的各种状况的处理，比如登录成功，登录失败，
 * 
 * @author zhaominglai
 * @date 2014年7月31日
 * 
 * */
public interface LoginCallback extends BaseCallback{
	
	/**登录成功时的回调*/
	public abstract void loginSuccessed();
	
	/**登录发生错误*/
	public abstract void loginError();
	
	/**用户不存在*/
	public abstract void userIsNotExist();
	
	/**用户密码错误*/
	public abstract void InvailedUserPwd();
	
	/**登录失败，从服务器返回了错误代码*/
	public abstract void loginFailed(int backCode);

	/**输入的用户名格式不正确*/
	public abstract void invailedLoginNameParam();
	
	/**输入的密码格式不正确*/
	public abstract void invailedPwdParam();
}
