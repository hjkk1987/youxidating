package com.atet.lib_atet_account_system.http.result;

/**
 * 用于标志后台返回的数据的基类
 * 
 * @author zhaominglai
 * @date 2014/7/22
 * */
public class Result {
	//状态码
	private int code;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}



	@Override
	public String toString() {
		return "Result {code=" + code + "}";
	}
	
	
}
