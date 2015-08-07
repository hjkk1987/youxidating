package com.atet.lib_atet_account_system.http;

public class FindPwdHttpParams extends HttpParams{
	
	
	private String email;
	
	
	private String loginName;


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getLoginName() {
		return loginName;
	}


	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}


	@Override
	public String toString() {
		return "FindPwdHttpParams {email=" + email + ", loginName=" + loginName
				+ "}";
	}
	

}
