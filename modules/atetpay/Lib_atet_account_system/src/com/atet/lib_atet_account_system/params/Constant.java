package com.atet.lib_atet_account_system.params;

/**
 * 用户登录系统中所需要的常量信息
 * 
 * @author zhaominglai
 * @date 2014年7月30日
 * 
 * */
public class Constant {

	/** 用于保存用户信息的sharedPreference情况表明 */
	// public static final String SP_USER = "sharedprf_user";
	public static final String SP_USER = "account";

	/** 用户名 */
	// public static final String SP_USER_NAME = "userName";
	public static final String SP_USER_NAME = "LOGIN_USER_NAME";

	public static final String SP_USER_NICKNAME = "nickName";

	/** 用户密码 */
	// public static final String SP_USER_PWD = "password";
	public static final String SP_USER_PWD = "LOGIN_PASSWORD";

	/** 用户头像 **/
	public static final String SP_USER_ICON = "LOGIN_USER_ICON";
	/** 用户头像 **/
	public static final String SP_USER_EMAIL = "LOGIN_USER_EMAIL";
	/** 用户头像 **/
	public static final String SP_USER_PHONE = "LOGIN_USER_PHONE";
	/** 用户登录状态 */
	public static final String SP_USER_STATUS = "status";

	/** AES加密算法所需加密种子 */
	public static final String AES_SEED = "ATET";

	/** 用于存放用户是否已经登录 */
	public static final String LOGIN_USER_ALREADY_LOGIN = "LOGIN_USER_ALREADY_LOGIN";

	/** 用户ID */
	// public static final String SP_USER_ID ="userId";
	public static final String SP_USER_ID = "LOGIN_USER_ID";

	public static enum USER_STATUS {
		ONLINE, OFFLINE
	};

	public static final String SP_DEVICE = "deviceInfo";

	public static final String SP_DEVICE_ID = "DEVICE_DEVICE_ID";

	// public static final String SP_DEVICE_CODE="deviceCode";

	public static final String SP_CHANNEL_ID = "DEVICE_CHANNEL_ID";

	public static final String SP_DEVICE_TYPE = "DEVICE_TYPE";
	public static final String SP_ATET_ID = "DEVICE_ATET_ID";

	/** 设备类型，1为电视，2为手机 */
	public static final int DEVICE_TYPE = 2;

	/** 渠道ID号 */
	public static final String DEVICE_CHANNEL_ID = "0";

	public static final String DEFAULT_DEVICE_ID = "20140701212944327101";
	public static final String DEFAULT_DEVICE_CODE = "T2";

	/** 第一次连接服务器 */
	public static final int FIRST_CONNECT_TRY = 1;

	public static final int SECOND_CONNECT_TRY = 2;

	public static final int THIRD_CONNECT_TRY = 3;

	public static enum RETRY_TYPE {
		TYPE_LOGIN, TYPE_REG, TYPE_FINDPWD, TYPE_LOGIN1, TYPE_REG1, TYPE_FINDPWD1
	}

	public static enum EMPTY_INPUT_TYPE {
		EMPTY_USERNAME, EMPTY_PWD, EMPTY_REPWD, EMPTY_EMAIL, EMPTY_PHONE, EMPTY_QQ, EMPTY_WECHAT, EMPTY_NICKNAME
	}

}
