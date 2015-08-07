package com.sxhl.statistics.model;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;
import com.sxhl.market.model.entity.AutoType;




@TableDescription(name = "InitInfo")
public class InitInfo extends BaseModel implements AutoType, Serializable {

	private static final long serialVersionUID = 1L;
	private Integer userId;
	private Long startTime; // 平台开始时间
	private Long endTime;//平台结束时间
	private Long longTime;//平台的运行时长
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
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
	
	@Override
	public String toString() {
		return "InitInfo {userId=" + userId + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", longTime=" + longTime + "}";
	}
}
