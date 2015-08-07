package com.sxhl.statistics.model;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;
import com.sxhl.market.model.entity.AutoType;




@TableDescription(name = "GameOnlineInfo")
public class GameOnlineInfo extends BaseModel implements AutoType, Serializable {

	private static final long serialVersionUID = 1L;
	private Integer userId;//用户账号
	private String packageName;//包名
	private String gameType;//游戏类型
	private String gameId;
	private String gameName;
	private String versionCode;//应用的版本号
	private String cpId;//CP
	private Integer copyright;
	private Long startTime; // 
	private Long endTime;
	private Long longTime;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public String getCpId() {
		return cpId;
	}
	public void setCpId(String cpId) {
		this.cpId = cpId;
	}
	public Integer getCopyRight() {
		return copyright;
	}
	public void setCopyRight(Integer copyRight) {
		this.copyright = copyRight;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public Long getLongTime() {
		return longTime;
	}
	public void setLongTime(Long longTime) {
		this.longTime = longTime;
	}

	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	@Override
	public String toString() {
		return "GameOnlineInfo {userId=" + userId + ", packageName="
				+ packageName + ", gameType="
				+ gameType + ", gameId=" + gameId + ", gameName=" + gameName
				+ ", versionCode=" + versionCode + ", cpId=" + cpId
				+ ", copyRight=" + copyright + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", longTime=" + longTime + "}";
	}
}
