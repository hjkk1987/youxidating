package com.sxhl.market.model.entity.user;

import com.sxhl.market.model.entity.AutoType;

public class TokenReq implements AutoType {
	/**
	 * The timestamp of client for token requesting
	 */
	private long time;
	private String token;
	private Integer code;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public TokenReq(){
		this.time = System.currentTimeMillis();
	}
	
	public TokenReq(long time){
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
