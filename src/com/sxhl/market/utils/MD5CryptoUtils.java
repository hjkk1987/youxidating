package com.sxhl.market.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * MD5加密算法工具类
 * 
 * @author zhaominglai
 * @date 2014.6.12
 * 
 * 
 * */
public class MD5CryptoUtils {
	
	
	/**
	 * MD5加密算法 
	 * 
	 * @param bytes字节数组，如果是String类型，则应该调用getbyte()方法，如"123456".getbyte();
	 * @return 加密后的16进制形式的字符串
	 * 
	 * */
	public static String toMD5(byte[] bytes) throws Exception
	{
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		
		algorithm.reset();
		
		algorithm.update(bytes);
		
		return toHexString(algorithm.digest(),"");
	}
	
	private static String toHexString(byte[] bytes,String separator){
		
		StringBuilder hexString = new StringBuilder();
		
		for (byte b:bytes)
		{
			String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1)
			{
				hexString.append('0');
			}
			hexString.append(hex).append(separator);
		}
		
		return hexString.toString();
	}
}
