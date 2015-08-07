package com.sxhl.statistics.model;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;
import com.sxhl.market.model.entity.AutoType;




@TableDescription(name = "CollectGameInfo")
public class CollectGameInfo extends BaseModel implements AutoType, Serializable {

	private static final long serialVersionUID = 1L;
	private Integer userId;//用户账号
	private String gameId;
	private String packageName;//游戏包名
	private String gameName;//游戏名称
	private String gameType;//游戏类型

	private String cpId;//CP
	private Integer clickCount; // 广告图片的简要描述
	private Integer downCount;
	private Integer adClick;
	
	
	private Integer copyright;//版本标志，1、平台游戏　2、第三方游戏　3、其他应用
	private Long recordTime;//记录时间
	private String recordDate;//记录的日期
	




	@Override
	public String toString() {
		return "InitInfo {userId:" + userId + ", packageName:" + packageName + ", cpId:"
				+", gameName:" + gameName
				+", gameType:" + gameType
				+ ",cpId "+cpId
				+",adClick"+adClick
				+ ", clickCount:" + clickCount + ",downCount:"+downCount+"}";
	}
	
	
	public String toJsonString() {
		
		return "{" +"\"userId\":"+"\""+userId+"\""
				+",\"packageName\":"+"\""+packageName+"\""
				+",\"gameName\":"+"\""+gameName+"\""		
				+",\"gameType\":"+"\""+gameType+"\""		
				+",\"cpId\":"+"\""+cpId+"\""
				+"\""
				+",\"clickCount\":"+"\""+clickCount+"\""
				+",\"downCount\":"+"\""+downCount+"\""
				+ "}";
	}


	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getCpId() {
		return cpId;
	}


	public void setCpId(String cpId) {
		this.cpId = cpId;
	}


	public Integer getClickCount() {
		return clickCount;
	}


	public void setClickCount(Integer clickCount) {
		this.clickCount = clickCount;
	}


	public Integer getDownCount() {
		return downCount;
	}


	public void setDownCount(Integer downCount) {
		this.downCount = downCount;
	}


	public String getGameName() {
		return gameName;
	}


	public void setGameName(String gameName) {
		this.gameName = gameName;
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


	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public Integer getCopyRight() {
		return copyright;
	}


	public void setCopyRight(Integer copyRight) {
		this.copyright = copyRight;
	}


	public Long getRecordTime() {
		return recordTime;
	}


	public void setRecordTime(Long recordTime) {
		this.recordTime = recordTime;
	}


	public Integer getAdClick() {
		return adClick;
	}


	public void setAdClick(Integer adClick) {
		this.adClick = adClick;
	}


	public String getRecordDate() {
		return recordDate;
	}


	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}
	
	
}
