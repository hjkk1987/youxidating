package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

/**
 * @author yindangchao
 * @date 2014/11/14 15:20
 * @discription 礼包实体类
 */
@TableDescription(name = "giftInfo")
public class GiftInfo extends BaseModel implements AutoType, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String gameid;
	private String giftPackageid;
	private String name;
	private Long startTime;
	private Long endTime;
	private int number;
	private String useMethod;
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setGameid(String gameid) {
		this.gameid = gameid;
	}

	public String getGameid() {
		return gameid;
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
