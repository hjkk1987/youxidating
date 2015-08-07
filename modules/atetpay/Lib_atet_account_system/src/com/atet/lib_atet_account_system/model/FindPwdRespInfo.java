package com.atet.lib_atet_account_system.model;

import java.util.Date;

import com.atet.lib_atet_account_system.http.result.Result;

public class FindPwdRespInfo extends Result{
	
	private int temPassword;

	public int getTemPassword() {
		return temPassword;
	}

	public void setTemPassword(int temPassword) {
		this.temPassword = temPassword;
	}

	@Override
	public String toString() {
		return "FindPwdRespInfo {temPassword=" + temPassword + "}";
	}
	
}
