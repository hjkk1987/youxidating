package com.atet.lib_atet_account_system.http.callbacks;

import com.atet.lib_atet_account_system.params.Constant;


/**
 * 最基本的回调接口
 * 
 * @author zhaominglai
 * @date 2014年8月4日
 * 
 * */
public interface BaseCallback {
	
	/**
	 * 当前网络不可用时的回调
	 * */
	public void netIsNotAvailable();
	
	/**
	 * 当输入的参数有空的情况
	 * 
	 * */
	public void someInputIsEmpte(Constant.EMPTY_INPUT_TYPE type);
	
}
