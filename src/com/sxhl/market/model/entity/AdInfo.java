package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

@TableDescription(name = "adInfo")
public class AdInfo extends BaseModel implements AutoType, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String adId;
	private String url;
	private String gameId;
	private String packageName;
	private Integer positionIndex;
	private String sizeType;
	private Integer type;
	private String remark;

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Integer getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(Integer positionIndex) {
		this.positionIndex = positionIndex;
	}

	public String getSizeType() {
		return sizeType;
	}

	public void setSizeType(String sizeType) {
		this.sizeType = sizeType;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String toString() {
		return "AdInfo [adId=" + adId + ", url=" + url + ", gameId=" + gameId
				+ ", packageName=" + packageName + ", positionIndex="
				+ positionIndex + ", sizeType=" + sizeType + ", type=" + type
				+ ", remark=" + remark + "]";
	}

}