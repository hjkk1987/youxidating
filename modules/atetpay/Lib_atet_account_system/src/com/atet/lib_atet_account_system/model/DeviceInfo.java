package com.atet.lib_atet_account_system.model;

/**
 * 设备信息实体类
 * 
 * @author zhaominglai
 * @date 2014/8/12
 * */
public class DeviceInfo {
	
	private int deviceType;
	
	private String deviceCode;
	
	private String deviceId;
	
	private String channelId;

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Override
	public String toString() {
		return "DeviceInfo {deviceType=" + deviceType + ", deviceCode="
				+ deviceCode + ", deviceId=" + deviceId + ", channelId="
				+ channelId + "}";
	}
	
	
	
}
