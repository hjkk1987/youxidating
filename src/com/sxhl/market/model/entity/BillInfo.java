package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

@TableDescription(name="billInfo")
public class BillInfo extends BaseModel implements AutoType,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url;
	private String gameId;
	private String gameName;
	private String maxPhoto;
	private String middlePhoto;
	private String minPhoto;
	private Integer gameLevel;
	private Integer gameDownCount;
	private Integer gameSize;
	private Integer gamePrice;
	private String briedDesc;
	private Long gameUpdateTime;
	private String packageName;
	private String downAddress;
	private Integer typeId;
	private Double version;
	public void setUrl(String url) {
		this.url = url;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public void setMaxPhoto(String maxPhoto) {
		this.maxPhoto = maxPhoto;
	}
	public void setMiddlePhoto(String middlePhoto) {
		this.middlePhoto = middlePhoto;
	}
	public void setMinPhoto(String minPhoto) {
		this.minPhoto = minPhoto;
	}
	public void setGameLevel(Integer gameLevel) {
		this.gameLevel = gameLevel;
	}
	public void setGameDownCount(Integer gameDownCount) {
		this.gameDownCount = gameDownCount;
	}
	public void setGameSize(Integer gameSize) {
		this.gameSize = gameSize;
	}
	public void setGamePrice(Integer gamePrice) {
		this.gamePrice = gamePrice;
	}
	public void setBriedDesc(String briedDesc) {
		this.briedDesc = briedDesc;
	}
	public void setGameUpdateTime(Long gameUpdateTime) {
		this.gameUpdateTime = gameUpdateTime;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public void setDownAddress(String downAddress) {
		this.downAddress = downAddress;
	}
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	public void setVersion(Double version) {
		this.version = version;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getUrl() {
		return url;
	}
	public String getGameId() {
		return gameId;
	}
	public String getGameName() {
		return gameName;
	}
	public String getMaxPhoto() {
		return maxPhoto;
	}
	public String getMiddlePhoto() {
		return middlePhoto;
	}
	public String getMinPhoto() {
		return minPhoto;
	}
	public Integer getGameLevel() {
		return gameLevel;
	}
	public Integer getGameDownCount() {
		return gameDownCount;
	}
	public Integer getGameSize() {
		return gameSize;
	}
	public Integer getGamePrice() {
		return gamePrice;
	}
	public String getBriedDesc() {
		return briedDesc;
	}
	public Long getGameUpdateTime() {
		return gameUpdateTime;
	}
	public String getPackageName() {
		return packageName;
	}
	public String getDownAddress() {
		return downAddress;
	}
	public Integer getTypeId() {
		return typeId;
	}
	public Double getVersion() {
		return version;
	}
	@Override
	public String toString() {
		return "BillInfo [url=" + url + ", gameId=" + gameId + ", gameName="
				+ gameName + ", maxPhoto=" + maxPhoto + ", middlePhoto="
				+ middlePhoto + ", minPhoto=" + minPhoto + ", gameLevel="
				+ gameLevel + ", gameDownCount=" + gameDownCount
				+ ", gameSize=" + gameSize + ", gamePrice=" + gamePrice
				+ ", briedDesc=" + briedDesc + ", gameUpdateTime="
				+ gameUpdateTime + ", packageName=" + packageName
				+ ", downAddress=" + downAddress + ", typeId=" + typeId
				+ ", version=" + version + "]";
	}
	
	
}
