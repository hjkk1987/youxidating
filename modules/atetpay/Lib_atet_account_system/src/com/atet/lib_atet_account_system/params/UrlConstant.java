package com.atet.lib_atet_account_system.params;

public class UrlConstant {
	
	/**登录用的IP地址*/
	public static final String URL_LOGIN1 = IPPortConstant.IP_ACCOUNT_CONTEXT1 +"/myuser/login.do";
	public static final String URL_LOGIN2 = IPPortConstant.IP_ACCOUNT_CONTEXT2 +"/myuser/login.do";
	public static final String URL_LOGIN3 = IPPortConstant.IP_ACCOUNT_CONTEXT3 +"/myuser/login.do";
	
	/**查找密码IP地址*/
	public static final String URL_FINDPWD1 = IPPortConstant.IP_ACCOUNT_CONTEXT1 +"/myuser/findpass.do";
	public static final String URL_FINDPWD2 = IPPortConstant.IP_ACCOUNT_CONTEXT2 +"/myuser/findpass.do";
	public static final String URL_FINDPWD3 = IPPortConstant.IP_ACCOUNT_CONTEXT3 +"/myuser/findpass.do";
	
	/**注册的IP地址*/
	public static final String URL_REG1 = IPPortConstant.IP_ACCOUNT_CONTEXT1 +"/myuser/regist.do";
	public static final String URL_REG2 = IPPortConstant.IP_ACCOUNT_CONTEXT2 +"/myuser/regist.do";
	public static final String URL_REG3 = IPPortConstant.IP_ACCOUNT_CONTEXT3 +"/myuser/regist.do";
	//public static final String URL_LOGIN = "http://10.1.1.62:8080/myuser/login.do";
	/**获取DeviceInf地址*/
	public static final String URL_DEVINFO = IPPortConstant.BASE_GAMEBOX_SEVER +"/atetinterface/deviceid.do";
//atetId地址
	public static final String URL_ATETID = "http://61.145.164.119:25001/mycollect/load.do";
}
