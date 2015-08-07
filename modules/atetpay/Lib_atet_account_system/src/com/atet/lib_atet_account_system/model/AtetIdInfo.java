package com.atet.lib_atet_account_system.model;

import com.atet.lib_atet_account_system.http.result.Result;

public class AtetIdInfo extends Result {
	private String atetId;

	public String getAtetId() {
		return atetId;
	}

	public void setAtetId(String atetId) {
		this.atetId = atetId;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "atetId : " + atetId;
	}
}
