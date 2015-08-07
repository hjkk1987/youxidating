package com.atet.lib_atet_account_system.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



/**
 * 
 * Android中 基于AES的加密算法工具类
 * 
 * @author zhaominglai
 * @date 2014年6月11日
 * 
 * 
 * */
public class AESCryptoUtils {
	
	
	/**
	 * 加密方法
	 * 
	 * @author zhaominglai
	 * @param seed 加密种子
	 * @param cleartext  要加密的正文
	 * @date 2014.6.11
	 * @return 加密后的16进制字符串
	 * 
	 * */
	public static String encrypt(String seed,String cleartext) throws Exception
	{
		byte[] rawKey = getRawKey(seed.getBytes());
		
		byte[] result = encrypt(rawKey,cleartext.getBytes());
		
		return toHex(result);
	}
	
	/**
	 * 解密方法
	 * 
	 * @author zhaominglai
	 * @param seed 加密种子
	 * @param cleartext  要解密的正文
	 * @date 2014.6.11
	 * @return 解密后的字符串
	 * 
	 * */
	public static String decrypt(String seed,String encrypted) throws Exception
	{
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(rawKey,enc);
		
		return new String(result);
	}
	
	private static byte[] getRawKey(byte[] seed) throws Exception 
	{
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = null;
		if (android.os.Build.VERSION.SDK_INT >=  17) {  
	         sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");  
	       } else {  
	         sr = SecureRandom.getInstance("SHA1PRNG");  
	       }   
		
		sr.setSeed(seed);
		kgen.init(128, sr);
		
		SecretKey skey = kgen.generateKey();
		
		byte[] raw = skey.getEncoded();
		
		return raw;
		
	}
	
	private static byte[] encrypt(byte[] raw,byte[] clear) throws Exception
	{
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		
		byte[] encrypted = cipher.doFinal(clear);
		
		return encrypted;
	}
	
	private static byte[] decrypt(byte[] raw,byte[] encrypted) throws Exception
	{
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		
		byte[] decrypted = cipher.doFinal(encrypted);
		
		return decrypted;
	}
	
	public static String toHex(String txt){
		return toHex(txt.getBytes());
	}
	
	public static String toHex(byte[] buf){
		if (buf == null)  
			return "";
		
		StringBuffer result = new StringBuffer(2*buf.length);
		
		for (int i = 0; i < buf.length;i++)
		{
			appendHex(result,buf[i]);
		}
		
		return result.toString();
	}
	
	private final static String HEX = "0123456789ABCDEF";
	
	private static void appendHex(StringBuffer sb,byte b)
	{
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}
	
	private static String fromHex(String hex)
	{
		return new String(toByte(hex));
	}
	
	private static byte[] toByte(String hexString)
	{
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		
		for (int i = 0;i < len;i++)
		{
			result[i] = Integer.valueOf(hexString.substring(2*i,2*i+2),16).byteValue();
		}
		
		return result;
	}

}
