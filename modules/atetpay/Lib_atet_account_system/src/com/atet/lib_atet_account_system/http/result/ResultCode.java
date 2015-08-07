package com.atet.lib_atet_account_system.http.result;


/**
 * 用户系统，从服务器返回的编码。
 * 
 * @author zhaominglai
 * @date 2014/7/31
 * 
 * */
public class ResultCode {
	
	/**操作成功*/
	public final static int OK = 0;

	/**非法操作*/
	public final static int INVALID_OP = 1;
	
	/**系统内部错误*/
	public final static int INTERVERL_SYS_ERR = 2;
	
	/**请求json格式解析错误*/
	public final static int PARSE_JSON_ERR = 3;
	
	/**获取请求参数错误*/
	public final static int RETRIVE_PARAMS_ERR = 4;
	
	/**用户名不存在*/
	public final static int USER_IS_NOT_EXIST = 1101;
	
	/**用户被冻结*/
	public final static int USER_IS_FREEZE = 1102;
	
	/**用户被锁定*/
	public final static int USER_IS_LOCKED = 1103;
	
	/**登录密码错误*/
	public final static int LOGIN_PWD_IS_WRONG = 1104;
	
	/**参数不匹配，比如密码和邮箱*/
	public final static int PARAMS_IS_NOT_MATCH = 1100;
	
	/**注册失败*/
	public final static int REG_FAILD = 1201;
	
	/**用户名已存在*/
	public final static int REG_USER_ALREADY_EXIST = 1202;
	
	/**设备ID错误*/
	public final static int INVAILID_DEVICEID = 1203;
	
	/**设备COde错误*/
	public final static int INVAILD_DEVICECODE = 1104;

	
}
