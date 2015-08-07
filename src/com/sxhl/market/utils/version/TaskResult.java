package com.sxhl.market.utils.version;
/** 
 * @author  
 * time：2012-8-7 下午4:34:03 
 * description: 任务执行结果
 */
public class TaskResult<T> {
	/** 任务执行成功(true) */
	public static final int OK = 1;
	/** 已到达末页 */
	public static final int ENDPAGE = 11;
	/** 任务执行失败(false) */
	public static final int FAILED = -1;
	/** 任务执行成功, 但返回结果为空 */
	public static final int EMPTY = -2;
	/** 任务执行抛出错误 */
	public static final int ERROR = 2;
	
	
	/** 任务执行完成后的结果码 */
	private int code = 0;
	
	/** 返回的数据 */
	private T data = null;
	
	/** 服务器返回的提示信息(当返回值为 'OK' or 'FAILED', 从该字段取信息) */
	private String msg = "";
	
	/** 服务器返回的错误信息(当返回值为 'ERROR', 从该字段取信息) */
	private String errorMsg = "";
	
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

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
