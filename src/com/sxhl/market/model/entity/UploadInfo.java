package com.sxhl.market.model.entity;

import java.io.Serializable;

public class UploadInfo implements AutoType, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadInfo() {
		super();
	}

	private String address;
	private int code;

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public UploadInfo(String address) {
		super();
		this.address = address;
	}

	@Override
	public String toString() {
		return "UploadInfo [address=" + address + "]";
	}

}
