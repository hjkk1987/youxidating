package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

@TableDescription(name = "gameType")
public class GameType extends BaseModel implements AutoType, Serializable {
	private static final long serialVersionUID = 1L;
	// Id
	private String typeId;
	// 专题图标
	private String icon;
	// 专题名称
	private String name;
	// 专题描述
	private String remark;
	private Integer orderNum;
	private Long createTime;

	private Long updateTime;
	
	private int games;

	public GameType() {
		super();
	}

	@Override
	public String toString() {
		return "GameType [typeId=" + typeId + ", icon=" + icon + ", name="
				+ name + ", remark=" + remark + ", orderNum=" + orderNum
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", games=" + games + "]";
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public int getGames() {
		return games;
	}

	public void setGames(int games) {
		this.games = games;
	}
}
