package com.atet.lib_atet_account_system.http.callbacks;


/**
 * 找回密码功能模块的回调函数接口
 * 
 * @author zhaominglai
 * @date 2014年8月4日
 * 
 * */
public interface FindPwdCallback extends BaseCallback{
	
	/**操作成功时的回调*/
	public abstract void findSuccessed();
	
	/**操作发生错误*/
	public abstract void findError();
	
	/**用户不存在*/
	public abstract void userIsNotExist();
	
	/**输入的邮箱地址不戒备匹配*/
	public abstract void emailIsNotMatch();
	
	/**登录失败，从服务器返回了错误代码*/
	public abstract void findFailed(int backCode);
	
	/**输入的用户名格式不正确*/
	public abstract void userInputInvailed();
	
	/**输入的邮箱格式不正确*/
	public abstract void emailInputInvailed();
	
}
