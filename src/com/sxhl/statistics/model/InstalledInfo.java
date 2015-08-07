package com.sxhl.statistics.model;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;
import com.sxhl.market.model.entity.AutoType;



/**
 * 用来 统计装机量的信息
 * 
 * @author zhaominglai
 * @date 2014/7/9
 * 
 * */
@TableDescription(name = "InstalledInfo")
public class InstalledInfo extends BaseModel implements AutoType, Serializable {

	private static final long serialVersionUID = 1L;
	private String deviceId; // 设备ID号
	private String deviceCode; // 设备CODE
	private Integer deviceType; // 设备类型
	private String atetId;//atet平台的设备唯一标识ID
	private String productId;
	private String channelId;//渠道ID
	private String resolution;
	private String ram;
	private String rom;
	private String sdCard;
	private String cpu;
	private String gpu;
	private String blueToothMac;
	private String packageName;
	private String versionCode;
	private String sdkVersion;
	private int dpi;
	private Long installTime;
	private int isFirstUpload;
	
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


	public int getDeviceType() {
		return deviceType;
	}


	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}


	public String getChannelId() {
		return channelId;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public String getProductId() {
		return productId;
	}


	public void setProductId(String productId) {
		this.productId = productId;
	}


	public String getResolution() {
		return resolution;
	}


	public void setResolution(String resolution) {
		this.resolution = resolution;
	}


	public String getRam() {
		return ram;
	}


	public void setRam(String ram) {
		this.ram = ram;
	}


	public String getRom() {
		return rom;
	}


	public void setRom(String rom) {
		this.rom = rom;
	}


	public String getSdCard() {
		return sdCard;
	}


	public void setSdCard(String sdCard) {
		this.sdCard = sdCard;
	}


	public String getCpu() {
		return cpu;
	}


	public void setCpu(String cpu) {
		this.cpu = cpu;
	}


	public String getGpu() {
		return gpu;
	}


	public void setGpu(String gpu) {
		this.gpu = gpu;
	}


	public String getBlueToothMac() {
		return blueToothMac;
	}


	public void setBlueToothMac(String blueToothMac) {
		this.blueToothMac = blueToothMac;
	}


	public String getVersionCode() {
		return versionCode;
	}


	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}


	public String getSdkVersion() {
		return sdkVersion;
	}


	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}


	public int getDpi() {
		return dpi;
	}


	public void setDpi(int dpi) {
		this.dpi = dpi;
	}


	public Long getInstallTime() {
		return installTime;
	}


	public void setInstallTime(Long installTime) {
		this.installTime = installTime;
	}


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public String getAtetId() {
		return atetId;
	}


	public void setAtetId(String atetId) {
		this.atetId = atetId;
	}

	public int getIsFirstUpload() {
		return isFirstUpload;
	}


	public void setIsFirstUpload(int isFirstUpload) {
		this.isFirstUpload = isFirstUpload;
	}


	@Override
	public String toString() {
		return "InstalledInfo {deviceId=" + deviceId + ", deviceCode="
				+ deviceCode + ", deviceType=" + deviceType + ", atetId="
				+ atetId + ", productId=" + productId + ", channelId="
				+ channelId + ", resolution=" + resolution + ", ram=" + ram
				+ ", rom=" + rom + ", sdCard=" + sdCard + ", cpu=" + cpu
				+ ", gpu=" + gpu + ", blueToothMac=" + blueToothMac
				+ ", packageName=" + packageName + ", versionCode="
				+ versionCode + ", sdkVersion=" + sdkVersion + ", dpi=" + dpi
				+ ", installTime=" + installTime + ", isFirstUpload="
				+ isFirstUpload + "}";
	}
}
