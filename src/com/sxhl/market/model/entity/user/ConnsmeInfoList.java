package com.sxhl.market.model.entity.user;

import java.util.ArrayList;

import com.sxhl.market.model.entity.AutoType;

public class ConnsmeInfoList implements AutoType{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ConsmeInfo> list;
	private int code;
	
	public ArrayList<ConsmeInfo> getList() {
		return list;
	}
	public void setList(ArrayList<ConsmeInfo> list) {
		this.list = list;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "ConnsmeInfoList [consmeInfoList=" + list + ", code="
				+ code + "]";
	}
	
}
