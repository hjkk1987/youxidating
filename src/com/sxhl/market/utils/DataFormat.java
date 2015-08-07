package com.sxhl.market.utils;

public class DataFormat {

	/**
	 * @Title
	 * @Description
	 * @param 
	 * @return
	 * @throws
	 */
	public static String dataFormat(String string){
		String dataString=null;
		int data =Integer.parseInt(string);
		if(data<1024){
			dataString=String.valueOf(data)+"KB";
		}else{
			long dataLong=data/1024;
			dataString=String.valueOf(dataLong)+"MB";
		}
		return dataString;
	}

}
