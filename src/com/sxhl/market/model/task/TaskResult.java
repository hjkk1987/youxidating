package com.sxhl.market.model.task;

import android.util.Log;

/**
 * @author time：2012-8-7 下午4:34:03 description: 任务执行结果
 */
public class TaskResult<T> {
	/** 任务执行成功(true) */
	public static final int OK = 0;

	/** 用户不存在 */
	public static final int USER_NOT_EXITS = 1101;
	/** 用户已经被冻结 */
	public static final int USER_FROZEN = 1102;
	/** 用户已经被锁定 */
	public static final int USER_LOCKED = 1103;
	/** 账号密码错误 */
	public static final int USER_PASSWORD_ERROR = 1104;

	/** 没有符合要求的数据(游戏分类) */
	public static final int GAME_CLASSIFY_NO_DATA = 1601;

	/** 查询软件版本没有符合要求的数据 */
	public static final int UPDATE_VERSION_NO_INFO = 1301;

	/** 非法操作 */
	public static final int ILLEGAL_OPERA = 1;
	/** 系统内部错误 */
	public static final int SYSTEM_ERROR = 2;
	/** 请求JSON格式解析错误 */
	public static final int JSON_FORM_ERROR = 3;
	/** 请求的参数错误 */
	public static final int REQUEST_DATA_ERROR = 4;
	/** 没有符合要求的数据 */
	public static final int NULL_DATA = 1101;
	/** 游戏的id不存在 */
	public static final int NULL_ID = 1102;
	/** 新增接口没有符合要求的数据 */
	public static final int THIRD_NULL_DATA = 1401;

	/** 登陆参数错误 */
	public static final int LOGIN_PARAME_ERROR = 1001;
	/** 用户名格式错误 */
	public static final int USER_NAME_FORMAT_ERROR = 1002;
	/** 密码格式错误 */
	public static final int PASSWORD_FORMAT_ERROR = 1003;
	/** 中文名格式错误 */
	public static final int CHINESE_NAME_FORMAT_ERROR = 1004;
	/** 手机号码格式错误 */
	public static final int PHONE_FORMAT_ERROR = 1005;
	/** 邮箱格式错误 */
	public static final int EMAIL_FORMAT_ERROR = 1006;
	/** 微信号格式错误 */
	public static final int WECHAT_FORMAT_ERROR = 1007;
	/** QQ号格式错误 */
	public static final int QQ_FORMAT_ERROR = 1008;
	/** 设备ID格式错误 */
	public static final int DEVICE_ID_FORMAT_ERROR = 1009;
	/** 用户主键ID无效 */
	public static final int USER_PRIMARY_KEY_ERROR = 1010;
	/** 旧密码错误 */
	public static final int OLD_PASSWORD_ERROR = 1011;
	/** 邮箱不匹配错误 */
	public static final int EMAIL_NOT_MATCH_ERROR = 1100;
	/** 昵称格式错误 */
	public static final int NICKNAME_FORMAT_ERROR = 1012;
	/** 新密码错误 */
	public static final int NEW_PASSWORD_ERROR = 1013;
	/** 两次输入的密码不一致 */
	public static final int TWO_PASSWORD_NOT_MATCH = 1014;

	/** 用户名为空 */
	public static final int USER_NAME_NULL = 1080;
	/** 密码为空 */
	public static final int USER_PASSWORD_NULL = 1081;
	/** 邮箱为空 */
	public static final int USER_EMAIL_NULL = 1082;
	/** 昵称为空 */
	public static final int USER_NICK_NAME_NULL = 1083;

	/** 用户注册失败 */
	public static final int USER_REGISTER_FAIL = 1201;
	/** 用户已经存在 */
	public static final int USER_EXITS = 1202;
	/** 设备ID错误 */
	public static final int DEVICE_ID_ERROR = 1203;
	/** 设备Code错误 */
	public static final int DEVICE_CODE_ERROR = 1204;
	/** 设备id没有符合要求的数据 */
	public static final int DEVICE_ID_NO_DATA = 1501;

	/** 已到达末页 */
	public static final int ENDPAGE = 11;
	/** 任务执行失败(false) */
	public static final int FAILED = -1;
	/** 任务执行成功, 但返回结果为空 */
	public static final int EMPTY = -2;
	/** 任务执行抛出错误 */
	public static final int ERROR = -3;

	private Long startTime;
	private Long endTime;
	private Long createTime;
	private Long currentTime;
	private Long updateTime;
	private Long lastUpdateTime;

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setCurrentTime(Long currentTime) {
		this.currentTime = currentTime;
	}

	public Long getCurrentTime() {
		return currentTime;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	private int totalCount;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/** 任务执行完成后的结果码 */
	private int code = FAILED;

	/** 返回的数据 */
	private T data = null;

	/** 服务器返回的提示信息(当返回值为 'OK' or 'FAILED', 从该字段取信息) */
	private String msg = "";

	/** 当任务执行抛出异常时, 此属性不为空 */
	private Exception exception = null;

	/**
	 * @return 任务执行完成后的结果码
	 */
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return 任务返回的数据
	 */
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	/**
	 * @return 当任务执行抛出异常时, 该方法返回结果不为空.
	 */
	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
