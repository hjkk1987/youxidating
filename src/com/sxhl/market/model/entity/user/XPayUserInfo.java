package com.sxhl.market.model.entity.user;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.entity.AutoType;

public class XPayUserInfo extends BaseModel implements AutoType {
	private String uid;
	private String imei;
	private String pass;
	private String pkey;
	private Integer code;
	private String sign;
	private String spass;
	private Long balance;
	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSpass() {
		return spass;
	}

	public void setSpass(String spass) {
		this.spass = spass;
	}

	public String getPkey() {
		return pkey;
	}

	public void setPkey(String pkey) {
		this.pkey = pkey;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public XPayUserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public XPayUserInfo(String uid, String imei, String pass,String sign,String spass){
		this.uid = uid;
		this.imei = imei;
		this.pass = pass;
		this.sign = sign;
		this.spass = spass;
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

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
}
