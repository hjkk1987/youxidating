package com.sxhl.market.utils.version;

import java.io.Serializable;

import com.sxhl.market.model.entity.AutoType;

/**
 * @ClassName: AppInfo
 * @Description: 应用信息实体
 * @author: Liuqin
 * @date 2013-1-7 上午9:29:32
 * 
 */
public class RespGetVersionInfo implements Serializable, AutoType {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	// 版本号
	private int versionCode;
	// 版本名
	private String versionName;
	// 描述内容
	private String remark;
	// 低于此版本的必须升级
	private int minVersion;
	private String downloadURL;
	private int code;

	public RespGetVersionInfo() {
		super();
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
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

	public int getMinVersion() {
		return minVersion;
	}

	public void setMinVersion(int minVersion) {
		this.minVersion = minVersion;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	@Override
	public String toString() {
		return "RespGetVersionInfo [versionCode=" + versionCode
				+ ", versionName=" + versionName + ", remark=" + remark
				+ ", minVersion=" + minVersion + ", downloadURL=" + downloadURL
				+ ", code=" + code + "]";
	}

}
