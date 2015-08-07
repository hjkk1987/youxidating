package com.sxhl.market.model.entity;

import com.sxhl.market.model.database.TableDescription;
import com.sxhl.market.model.entity.user.XPayUserInfo;

@TableDescription(name = "userInfo")
public class UserInfo extends XPayUserInfo implements AutoType {
	// 用户id
	private String userId;
	// 用户名
	private String userName;
	// 昵称
	private String nickName;
	// 积分
	private int points;
	// 用户等级
	private int level;
	// 头像路径
	private String avator;
	// 绑定的手机号码，用于找回密码
	private String phone;
	// 绑定的手机号码，用于找回密码
	private String email;
	// 用户头像
	private String icon;

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getNickName() {
		return nickName;
	}

	public int getPoints() {
		return points;
	}

	public int getLevel() {
		return level;
	}

	public String getAvator() {
		return avator;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setAvator(String avator) {
		this.avator = avator;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", userName=" + userName
				+ ", nickName=" + nickName + ", points=" + points + ", level="
				+ level + ", avator=" + avator + ", phone=" + phone
				+ ", email=" + email + ", getBalance()=" + getBalance()
				+ ", getSign()=" + getSign() + ", getSpass()=" + getSpass()
				+ ", getPkey()=" + getPkey() + ", getCode()=" + getCode()
				+ ", getUid()=" + getUid() + ", getImei()=" + getImei()
				+ ", getPass()=" + getPass() + ", getId()=" + getId()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
