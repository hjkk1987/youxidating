package com.atet.lib_atet_account_system.model;

import java.util.Date;

import com.atet.lib_atet_account_system.http.result.Result;

public class LoginRespInfo extends Result {

	private int userId;

	private String loginName;

	private String chineseName;

	private String nickName;

	private String email;

	private String phone;

	private int age;

	private int sex;

	private String birthday;

	private String address;

	private String qq;

	private String wechat;

	private Date createTime;

	private Date updateTime;
	private String icon;

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "LoginRespInfo {userId=" + userId + ", loginName=" + loginName
				+ ", chineseName=" + chineseName + ", nickName=" + nickName
				+ ", email=" + email + ", phone=" + phone + ", age=" + age
				+ ", sex=" + sex + ", birthday=" + birthday + ", address="
				+ address + ", qq=" + qq + ", wechat=" + wechat
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ",icon = " + icon + ", toString()=" + super.toString() + "}";
	}

}
