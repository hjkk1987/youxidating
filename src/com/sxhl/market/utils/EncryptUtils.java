package com.sxhl.market.utils;

/**
 * 用于加密、解密的工具类，此类使用AES算法进行加密和解密，加密和解密时在公钥中加入盐值
 * @author xieyonggui
 * @since jdk1.6
 * @version 1.0
 */
public class EncryptUtils 
{
	/**
     * 加密
     * @param content 需要加密的内容
     * @param key 加密是采用的public key
     * @return 加密后的密文
     */
    public static String encryptAES(String content, String key)
    {  
    	return encrypt(content, key,false);
    }  
    
    /**
     * 解密
     * @param cryptograph 需要解密的密文
     * @param key 加密时采用的public key
     * @return 解密后的内容文本
     */
    public static String decryptAES(String cryptograph, String key)
    {  
    	return decrypt(cryptograph, key,false);
    }  
    
    /**
     * @Title: decodeToken
     * @Description: 解密令牌 
     * @param token 令牌密文
     * @param time 时间戳
     * @return 解密后的令牌(null解密出错)
     * @throws
     */
    public static String decodeToken(String token,String time){
    	return decrypt(token, time, false);
    }

    /**
     * @Title: encodePassword
     * @Description: 用户密码加密 
     * @param pwd 密码明文
     * @param token 令牌明文
     * @return
     * @throws
     */
    public static String encodePassword(String pwd,String token,boolean isKey){
    	String pass=mencrypt(pwd, false);
    	if(pass==null){
    		return null;
    	}
    	pass=mencrypt(token+pass,isKey);
    	return pass;
    }
    
	static {
		System.loadLibrary("crypt");
		init();
	}
	public static native boolean init();
	public static native String encrypt(String code, String key, boolean isKey);
	public static native String decrypt(String code, String key,boolean isKey);
	//存放code和key
	public static native boolean kdecrypt(String code, String key);
	//code用md5加密
	public static native String mencrypt(String code,boolean isKey);
}
