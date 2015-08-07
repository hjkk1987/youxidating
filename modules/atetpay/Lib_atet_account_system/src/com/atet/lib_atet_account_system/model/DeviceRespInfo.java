package com.atet.lib_atet_account_system.model;

import com.atet.lib_atet_account_system.http.result.Result;

public class DeviceRespInfo extends Result {

	private String deviceId;

	private int type;

	private String channelId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getType() {
		return type;
	}

	public void setDeviceType(int type) {
		this.type = type;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Override
	public String toString() {
		return "DeviceRespInfo {deviceId=" + deviceId + ", deviceType="
				+ type + ", channelId=" + channelId + "}";
	}

}
