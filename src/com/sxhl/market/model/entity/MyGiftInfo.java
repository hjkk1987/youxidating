package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;
import com.sxhl.market.utils.DateUtil;

/**
 * @author yindangchao
 * @date 2014/11/14 15:20
 * @discription 我的礼包实体类
 */
@TableDescription(name = "myGiftInfo")
public class MyGiftInfo extends BaseModel implements AutoType, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userId;
	private String gameId;
	private String giftPackageid;
	private String name;
	private int number;
	private String useMethod;
	private String content;
	private String icon;
	private String giftCode;
	private Long receiveTime = 0l;

	public String getFormatReceiveTime() {
		return DateUtil.formatDate(this.receiveTime);
	}

	public Long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setGiftCode(String giftCode) {
		this.giftCode = giftCode;
	}

	public String getGiftCode() {
		return giftCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGiftPackageid() {
		return giftPackageid;
	}

	public void setGiftPackageid(String giftPackageid) {
		this.giftPackageid = giftPackageid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getUseMethod() {
		return useMethod;
	}

	public void setUseMethod(String useMethod) {
		this.useMethod = useMethod;
	}

}
