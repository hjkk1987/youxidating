package com.sxhl.market.model.entity;

public class VersionInfo {
	private String downloadURL;
	private Integer versionCode;
	private String versionName;
	private String remark;
	private Integer minVersion;
	private Integer code;

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public Integer getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(Integer versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getMinVersion() {
		return minVersion;
	}

	public void setMinVersion(Integer minVersion) {
		this.minVersion = minVersion;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "VersionInfo [downloadURL=" + downloadURL + ", versionCode="
				+ versionCode + ", versionName=" + versionName + ", remark="
				+ remark + ", minVersion=" + minVersion + ", code=" + code
				+ "]";
	}

}
