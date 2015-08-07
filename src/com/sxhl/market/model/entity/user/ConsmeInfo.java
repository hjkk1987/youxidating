package com.sxhl.market.model.entity.user;

import com.sxhl.market.model.entity.AutoType;

public class ConsmeInfo implements AutoType{
	private int count;
	private String type;
	private String gname;
	private long time;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "ConsmeInfo [count=" + count + ", type=" + type + ", gname="
				+ gname + ", time=" + time + "]";
	}
	
}
