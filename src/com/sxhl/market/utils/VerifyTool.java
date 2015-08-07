package com.sxhl.market.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/** 
 * @author  
 * time：2012-8-6 下午7:47:10 
 * description: 字符串校验工具
 */
public class VerifyTool {
	
	/*
	 * 邮箱验证
	 * @parma email 需要验证得邮箱
	 */
	public static boolean isEmail(String email)
	{
		return Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$").matcher(email).matches();
	}
	/**
     * 昵称验证
     * @param 需要验证的昵称
     * */
    public static boolean isNickName(String nickName) {
        
        return StringTool.getChineseLength(nickName) >= 4
                && StringTool.getChineseLength(nickName) <= 16
                && Pattern.compile("[\\u4e00-\\u9fa5\\w]*").matcher(nickName).matches();
    }

    /**
     * 验证密码格式, 只支持英文和数字.
     * 
     * @param pwd
     * @return
     */
    public static boolean verifyPasswordFormat(String pwd) {
        return Pattern.compile("[a-zA-Z0-9]*").matcher(pwd).matches();
    }
    
    /**
     * 验证手机号是否正确;
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles){ 
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$"); 
        Matcher m = p.matcher(mobiles); 
        return m.matches(); 
    } 
    
       
       
    /**
     * 验证IP和端口的格式是否有误
     * @param ip   访问服务器 IP 地址
     * @param port 访问服务器端口
     * @return 校验ip和端口 return -1 ip 或者 port 为空. -2 ip格式错误. -3 port为数字 0 通过
     * */
    public static final int verificateIPAndPort(String ip, String port) {

        if (null == ip || "".equals(ip) || null == port || "".equals(port)) {
            return -1;
        }

        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            return -3;
        }

        Pattern pattern = Pattern
                .compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        Matcher matcher = pattern.matcher(ip.trim());
        if (matcher.matches()) {
            return 0;
        } else {
            return -2;
        }

    }
}
