package com.sxhl.statistics.model;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;
import com.sxhl.market.model.entity.AutoType;



@TableDescription(name="TimeInfo")
public class TimeInfo extends BaseModel implements AutoType, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long time;// 设备在服务器数据库中对应的ID
	private Integer code;// 平台类型,1为TV,2为手机

	
	

	@Override
	public String toString() {
		return "DeviceInfo [time=" + time + ", code=" + code + "]";
	}




	public Long getTime() {
		return time;
	}




	public void setTime(Long time) {
		this.time = time;
	}




	public Integer getCode() {
		return code;
	}




	public void setCode(Integer code) {
		this.code = code;
	}

}
