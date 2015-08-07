package com.sxhl.market.model.entity.user;

import com.sxhl.market.model.entity.AutoType;

public class LogoutRequest implements AutoType{
	private String uid; // the user ID
	private String imei; // the device imei
	private int code; // error code
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public LogoutRequest(String uid, String imei){
		this.uid = uid;
		this.imei = imei;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}
}
