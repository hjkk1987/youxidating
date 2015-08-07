package com.atet.lib_atet_account_system.http;

public class AtetIdInfoHttpParams extends HttpParams {
	private String deviceId;
	private String productId;
	private String deviceCode;
	private Integer deviceType;
	private String resolution;
	private String ram;
	private String rom;
	private String sdCard;
	private String packageName;
	private String cpu;
	private String gpu;
	private String blueToothMac;
	private String versionCode;
	private String sdkVersion;
	private Integer dpi;
	private String channelId;
	private Long installTime;
	private int isFirstUpload;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
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

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
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

	public Integer getDpi() {
		return dpi;
	}

	public void setDpi(Integer dpi) {
		this.dpi = dpi;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public Long getInstallTime() {
		return installTime;
	}

	public void setInstallTime(Long installTime) {
		this.installTime = installTime;
	}

	public int getIsFirstUpload() {
		return isFirstUpload;
	}

	public void setIsFirstUpload(int isFirstUpload) {
		this.isFirstUpload = isFirstUpload;
	}

}
