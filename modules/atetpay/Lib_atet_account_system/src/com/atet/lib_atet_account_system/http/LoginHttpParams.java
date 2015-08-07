package com.atet.lib_atet_account_system.http;

public class LoginHttpParams extends HttpParams{
	

	private int userId;
	
	private String email;
	
	private String phone;
	
	private String loginName;
	
	private String password;
	
	private int deviceType;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		return "LoginHttpParams {userId=" + userId + ", email=" + email
				+ ", phone=" + phone + ", loginName=" + loginName
				+ ", password=" + password + ", deviceType=" + deviceType + "}";
	}
	
	

}
