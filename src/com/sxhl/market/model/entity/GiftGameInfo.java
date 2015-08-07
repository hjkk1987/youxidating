package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

/**
 * @author yindangchao
 * @date 2014/11/14 15:20
 * @discription 有礼包的游戏实体类
 */
@TableDescription(name = "giftGameInfo")
public class GiftGameInfo extends BaseModel implements AutoType, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String gameName;
	private String maxPhoto;
	private String minPhoto;
	private Integer sortNum;
	private Group<GiftInfo> giftData;
	private String gameid;
	private int giftNum = 0;

	public void setGiftNum(int giftNum) {
		this.giftNum = giftNum;
	}

	public int getGiftNum() {
		return giftNum;
	}

	public void setGameid(String gameid) {
		this.gameid = gameid;
	}

	public String getGameid() {
		return gameid;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getMaxPhoto() {
		return maxPhoto;
	}

	public void setMaxPhoto(String maxPhoto) {
		this.maxPhoto = maxPhoto;
	}

	public String getMinPhoto() {
		return minPhoto;
	}

	public void setMinPhoto(String minPhoto) {
		this.minPhoto = minPhoto;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public Group<GiftInfo> getGiftData() {
		return giftData;
	}

	public void setGiftData(Group<GiftInfo> giftData) {
		this.giftData = giftData;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
