package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

@TableDescription(name="downloadAddressInfo")
public class DownloadAddressInfo extends BaseModel implements AutoType,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String gameURLId;// 游戏名称
	private String url;// 游戏的下载地址
	private Integer downToken;// 百度网盘中间下载地址
	private String remark;// 描述
	private String logoUrl;// logo图片地址
	private String gameId;// 游戏id

	public String getGameURLId() {
		return gameURLId;
	}

	public void setGameURLId(String gameURLId) {
		this.gameURLId = gameURLId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getDownToken() {
		return downToken;
	}

	public void setDownToken(Integer downToken) {
		this.downToken = downToken;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	@Override
	public String toString() {
		return "DownloadAddressInfo [gameURLId=" + gameURLId + ", url=" + url
				+ ", downToken=" + downToken + ", remark=" + remark
				+ ", logoUrl=" + logoUrl + ", gameId=" + gameId + "]";
	}

}
