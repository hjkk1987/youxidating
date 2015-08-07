package com.sxhl.market.model.entity.user;

import com.sxhl.market.model.entity.AutoType;

public class RechargeRequest implements AutoType{
	private String uid; // user id
	private int sum; // bonus
	private String checksum; // MD5 decode the (pkey + sum)
	
	private String checkuid; // MD5 decode the (pkey + uid)
	
	private int balance;
	
	private int code;
	
	public RechargeRequest(String uid, int sum, String checksum, String checkuid){
		this.uid = uid;
		this.sum = sum;
		this.checksum = checksum;
		this.checkuid = checkuid;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public String getCheckuid() {
		return checkuid;
	}

	public void setCheckuid(String checkuid) {
		this.checkuid = checkuid;
	}
}
