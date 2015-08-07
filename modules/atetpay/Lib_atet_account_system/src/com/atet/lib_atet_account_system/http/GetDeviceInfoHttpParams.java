package com.atet.lib_atet_account_system.http;

/**
 * 用于向网络获取系统设备信息
 * 
 * @author zhaominglai
 * @date 2014/7/31
 * 
 * */
public class GetDeviceInfoHttpParams extends HttpParams {

	private String channelId;

	private String deviceCode;

	private String productId;

	private int deviceType;
	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		return "GetDeviceInfoHttpParams {channelId=" + channelId
				+ ", deviceCode=" + deviceCode + ", productId=" + productId
				+ "}";
	}

}
