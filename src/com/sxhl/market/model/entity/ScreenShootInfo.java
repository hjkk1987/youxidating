package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

@TableDescription(name = "screenShootInfo")
public class ScreenShootInfo extends BaseModel implements AutoType,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String photoUrl;// 游戏详情的图片或者视频的下载地址
	private Integer type;// 图片为1；视频为2；
	private String gameId;
	private String gameTypeId;

	public String getGameTypeId() {
		return gameTypeId;
	}

	public void setGameTypeId(String gameTypeId) {
		this.gameTypeId = gameTypeId;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	@Override
	public String toString() {
		return "ScreenShootInfo [photoUrl=" + photoUrl + ", type=" + type
				+ ", gameId=" + gameId + "]";
	}

}
