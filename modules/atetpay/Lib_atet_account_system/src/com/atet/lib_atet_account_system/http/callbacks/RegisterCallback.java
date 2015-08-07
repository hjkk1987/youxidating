package com.atet.lib_atet_account_system.http.callbacks;


/**
 * 用户注册的回调函数接口
 * 
 * @author zhaominglai
 * @date 2014年7月31日
 * 
 * */
public interface RegisterCallback extends BaseCallback{
	
	/**注册成功时的回调*/
	public abstract void regSuccessed();
	
	/**注册发生错误*/
	public abstract void regError();
	
	/**用户已经存在*/
	public abstract void userIsExist();
	
	
	/**登录失败，从服务器返回了错误代码*/
	public abstract void regFailed(int backCode);
	
	/**输入的用户名格式不正确*/
	public abstract void userInputInvailed();
	
	/**输入的密码格式不正确*/
	public abstract void pwdInputInvailed();
	
	/**输入的邮箱格式不正确*/
	public abstract void emailInputInvailed();
	
	/**输入的手机格式不正确*/
	public abstract void phoneInputInvailed();
	
	/**输入的中文名字格式不正确*/
	public abstract void chineseInputInvailed();
	
	/**输入的qq格式不正确*/
	public abstract void qqInputInvailed();
	
	/**输入的昵称格式不正确*/
	public abstract void nickNameInputInvailed();
	
	/**两次输入的密码不一致*/
	public abstract void twoPwdIsNotSame();

	
}
