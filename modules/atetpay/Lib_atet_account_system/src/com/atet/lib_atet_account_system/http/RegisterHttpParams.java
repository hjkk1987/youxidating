package com.atet.lib_atet_account_system.http;

public class RegisterHttpParams extends HttpParams {

	private String deviceId;

	private String deviceCode;

	private String nickName;

	private String chineseName;

	private int sex;

	private String qq;

	private String wechat;

	private String birthday;

	private String address;

	private String email;

	private String phone;

	private String loginName;

	private String password;

	private int type;
	private String productId;
	private String channelId;
	private String atetId;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public void setAtetId(String atetId) {
		this.atetId = atetId;
	}

	public String getAtetId() {
		return atetId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductId() {
		return productId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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


	@Override
	public String toString() {
		return "RegisterHttpParams {deviceId=" + deviceId + ", deviceCode="
				+ deviceCode + ", nickName=" + nickName + ", chineseName="
				+ chineseName + ", sex=" + sex + ", qq=" + qq + ", wechat="
				+ wechat + ", birthday=" + birthday + ", address=" + address
				+ ", email=" + email + ", phone=" + phone + ", loginName="
				+ loginName + ", password=" + password + ", deviceType="
				+ type + "}";
	}

}
