package com.sxhl.market.model.exception;

import com.sxhl.market.app.ExceptionHandler;

/**
 * 
 * @author Jinfu zhou<br/>
 * time: 2013-05-02 19:52 pm<br/>
 * description: 描述服务器json中的code错误异常<br/>
 * 例如系统返回码(json)：<hr/>
 * {
 * 		data:{...},   // 其他的数据
 * 		code:2	    // 2:表示客户端完成所有请求数据没有出现异常，但服务器系统内部出现错误
 * }
 * 
 * @see ExceptionHandler
 */
public class ServerLogicalException extends Exception {
	private static final long serialVersionUID = -5121005644467766593L;
	private int code;
	
	/**
	 * 系统返回码
		(0-100)
		<br/><b>0:操作成功</b>
	 */
	public static final int CODE_SYS_SUCCESS = 0;
	/**
	 * 系统返回码
		(0-100)
	 * <br/><b>1:非法操作</b>
	 */
	public static final int CODE_SYS_INVALID_OPTION = 1;
	/**
	 * 系统返回码
		(0-100)
	 * <br/><b>2:系统内部错误</b>
	 */
	public static final int CODE_SYS_INNER_ERROR = 2;
	/**
	 * 系统返回码
		(0-100)
	 * <br/><b>3:请求json格式解析错误</b>
	 */
	public static final int CODE_SYS_JSON_PARSE_ERROR = 3;
	/**
	 * 系统返回码
		(0-100)
	 * <br/><b>4:获取请求参数错误</b>
	 */
	public static final int CODE_SYS_REQUEST_PARAMS_ERROR = 4;
	/**
	 * 系统返回码
		(0-100)
	 * <br/><b>5:令牌无效</b>
	 */
	public static final int CODE_SYS_INVALID_TOKEN = 5;
	//=================================================
	
	/**
	 * token返回码
		(101-200)
	 * <br/><b>101:创建令牌错误</b>
	 */
	public static final int CODE_TOKEN_CREATE_ERROR = 101;
	//===================================================
	
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>201:用户ID为空</b>
	 */
	public static final int CODE_SIGN_ID_EMPTY = 201;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>202:用户密码为空</b>
	 */
	public static final int CODE_SIGN_PWD_EMPTY = 202;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>203:IMEI为空</b>
	 */
	public static final int CODE_SIGN_IMEI_EMPTY = 203;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>204:程序签名为空</b>
	 */
	public static final int CODE_SIGN_SIGNATURE_EMPTY = 204;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>205:程序签名密码为空</b>
	 */
	public static final int CODE_SIGN_SIGNATURE_PWD_EMPTY = 205;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>206:用户已存在</b>
	 */
	public static final int CODE_SIGN_USER_EXIST = 206;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>207:用户不存在</b>
	 */
	public static final int CODE_SIGN_USER_NOT_EXIST = 207;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>208:用户密码错误</b>
	 */
	public static final int CODE_SIGN_USER_PWD_ERROR = 208;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>209:获取密钥失败</b>
	 */
	public static final int CODE_SIGN_GET_PKEY_ERROR = 209;
	/**
	 * 注册/登录返回码
		(201-300)
	 * <br/><b>210:程序签名验证不通过</b>
	 */
	public static final int CODE_SIGN_SIGNATURE_INVALID = 210;
	//========================================================
	/**
	 * 扣费请求返回码
		(301-400)
	 * <br/><b>301:用户ID或密码为空</b>
	 */
	public static final int CODE_PAY_ID_OR_PWD_EMPTY = 301;
	/**
	 * 扣费请求返回码
		(301-400)
	 * <br/><b>302:用户不存在</b>
	 */
	public static final int CODE_PAY_USER_NOT_EXIST = 302;
	/**
	 * 扣费请求返回码
		(301-400)
	 * <br/><b>303:用户密码错误</b>
	 */
	public static final int CODE_PAY_USER_PWD_ERROR = 303;
	/**
	 * 扣费请求返回码
		(301-400)
	 * <br/><b>304:余额不足</b>
	 */
	public static final int CODE_PAY_NOT_ENOUGH_MONEY = 304;
	/**
	 * 扣费请求返回码
		(301-400)
	 * <br/><b>305:数值被篡改</b>
	 */
	public static final int CODE_PAY_CONFLICT = 305;
	//================================================
	/**
	 * 充值请求返回码
		(401-500)
	 * <br/><b>401:用户不存在</b>
	 */
	public static final int CODE_PREPAID_USER_NOT_EXIST = 401;
	/**
	 * 充值请求返回码
		(401-500)
	 * <br/><b>402:用户账户不存在</b>
	 */
	public static final int CODE_PREPAID_ACCOUNT_NOT_EXIST = 402;
	/**
	 * 充值请求返回码
		(401-500)
	 * <br/><b>403:第三方充值失败</b>
	 */
	public static final int CODE_PREPAID_3TD_PART_ERROR = 403;
	/**
	 * 充值请求返回码
		(401-500)
	 * <br/><b>404:数值被篡改</b>
	 */
	public static final int CODE_PREPAID_CONFLICT = 404;
	//=======================================================
	/**
	 * 订单请求返回码
		(501-600)
	 * <br/><b>501:没有与订单号对应的记录</b>
	 */
	public static final int CODE_ORDER_NOT_EXIST_BY_ORDERID = 501;
	/**
	 * 订单请求返回码
		(501-600)
	 * <br/><b>502:没有与用户ID对应的订单</b>
	 */
	public static final int CODE_ORDER_NOT_EXIST_BY_USERID = 502;
	
	/* 
	 * 以下的都是游戏系统返回码
	 * 规则：
	 * 1.游戏系统返回码中系统返回码同支付系统（0-100）定义和支付一致
	 * 2.游戏系统返回码其他定义码从1000开始，为了和支付相区别
	 */
	/**
	 * 游戏返回码
	 * <br/><b>1101:没有符合要求的数据</b>
	 */
	public static final int CODE_GAME_NO_REQUIRED_DATA = 1101;
	/**
	 * 游戏返回码
	 * <br/><b>1102:该游戏ID不存在</b>
	 */
	public static final int CODE_GAME_ID_NOT_EXIST = 1102;
	
	/*
	 * 以下都是广场的状态码
	 * 1401-1500
	 */
	/**
	 * 发布帖子失败
	 */
	public static final int CODE_SQUARE_POST_FAIL = 1401;
	
	/**
	 * 顶帖子失败
	 */
	public static final int CODE_SQUARE_UPPOST_FAIL = 1402;
	/**
	 * 回复帖子失败
	 */
	public static final int CODE_SQUARE_REPLY_POST_FAIL = 1403;
	/**
	 * 查询帖子详情失败
	 */
	public static final int CODE_SQUARE_QUERY_POST_FAIL = 1404;
	
	
	/*
	 * 评论模块接口错误返回码(1301-1400)
	 */
	/**
	 * 回复评论失败
	 */
	public static final int CODE_COMMENT_REPLY_FAIL = 1302;
	/*
	 * 用户模块接口错误返回码(1201-1300)
	 */
	/**
	 * 保存用户反馈意见失败
	 */
	public static final int CODE_USER_FEEDBACK_FAIL = 1201;
	/**
	 * 修改用户信息失败
	 */
	public static final int CODE_USER_MODIFY_FAIL = 1202;
	/**
	 * 查询用户信息失败
	 */
	public static final int CODE_USER_QUERY_FAIL = 1203;
	/**
	 * 插入用户信息失败
	 */
	public static final int CODE_USER_INSERT_FAIL = 1204;
	
	public ServerLogicalException(int code){
		super();
		this.code = code;
	}
	
	public ServerLogicalException(int code, String msg){
		super(msg);
		this.code = code;
	}
	
	public ServerLogicalException(int code, String msg, Throwable ex){
		super(msg, ex);
		this.code = code;
	}
	
	public ServerLogicalException(int code, Throwable ex){
		super(ex);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
