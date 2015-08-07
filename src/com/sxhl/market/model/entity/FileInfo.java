package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;

public class FileInfo extends BaseModel implements AutoType, Serializable {

	public FileInfo() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8682061435975145312L;
	private String fileObj;
	public String getFileObj() {
		return fileObj;
	}
	public void setFileObj(String fileObj) {
		this.fileObj = fileObj;
	}
	@Override
	public String toString() {
		return "FileInfo [fileObj=" + fileObj + "]";
	}

}
