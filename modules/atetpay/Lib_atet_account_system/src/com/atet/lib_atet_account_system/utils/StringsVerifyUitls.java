package com.atet.lib_atet_account_system.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 输入参数的格式校验
 * 
 * @author zhaominglai
 * @description 对输入的字符串进行格式的检验
 * @date 2014年6月11日
 * 
 * */
public class StringsVerifyUitls {
	
	
	//验证的类型为用户名
	public final static int TYPE_USERNAME = 0;
	
	//验证的类型为密码
	public final static int TYPE_PWD = 1;
	
	//验证的类型为中文名字
	public final static int TYPE_CNAME = 2;
	
	//验证的类型为电话号码
	public final static int TYPE_PHONE = 3;
	
	//验证的类型为Email
	public final static int TYPE_EMAIL = 4;
	
	//验证的类型为微信
	public final static int TYPE_WECHAT = 5;
	
	//验证的类型为QQ
	public final static int TYPE_QQ = 6;
	
	//验证类型为昵称
	public final static int TYPE_NICKNAME = 7;
	
	//字符为空
	public final static int STATUS_CODE_NULL = 0;
	
	//非常字符
	public final static int STAUS_CODE_INVALIDATE = 1;
	
	//格式正确
	public final static int STAUS_CODE_VALIDATE = 2;
	
	//未知的验证类型
	public final static int UNKOWN_TYPE = 3;
	
	
	/**
	 * 字符串校验方式
	 * @author zhaominglai
	 * @param type:校验的类型 str:要校验的字符串
	 * @date 2014年6月11日
	 * 
	 * */
	public static int verifyStringsFormat(int type,String str)
	{
		int result = 0;
		
		switch(type)
		{
		case TYPE_USERNAME:
			result = verifyUserName(str);
			break;
			
		case TYPE_PWD:
			result =verifyPwd(str);
			break;
			
		case TYPE_PHONE:
			result =verifyPhone(str);
			break;
			
		case TYPE_EMAIL:
			result =verifyEmail(str);
			break;
			
		case TYPE_QQ:
			result =verifyQQ(str);
			break;
			
		case TYPE_WECHAT:
			result =verifyWechat(str);
			break;
			
		case TYPE_CNAME:
			result =verifyCname(str);
			break;
			
		case TYPE_NICKNAME:
			result = verifyNickname(str);
			break;
			
		default:
			result = UNKOWN_TYPE;
		}
		
		return result;
	}
	
	private static int verifyNickname(String name) {
		// TODO Auto-generated method stub
		if (name == null || name.equals(""))
			return STATUS_CODE_NULL;
		/*
		 * 昵称1-16位,
		 */
		/*String reg = "^[A-Za-z][A-Za-z0-9_]{0,15}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(name);*/
		
		if (name.length() > 16 || name.length() < 1)
			return STAUS_CODE_INVALIDATE;
		
		return STAUS_CODE_VALIDATE;
	
	}

	public static int verifyUserName(String name)
	{
		if (name == null || name.equals(""))
			return STATUS_CODE_NULL;
		/*
		 * 用户名验证 由5-16个字母、数字或下划线组成，必须以字母开头
		 */
		String reg = "^[A-Za-z][A-Za-z0-9_]{4,24}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(name);
		
		if (!m.matches())
			return STAUS_CODE_INVALIDATE;
		
		return STAUS_CODE_VALIDATE;
	}
	
	public static int verifyPwd(String pwd)
	{
		if (pwd == null || pwd.equals(""))
			return STATUS_CODE_NULL;
		/*
		 * 密码验证 由5-16个字母或数字组成
		 */
		String reg = "^[A-Za-z0-9]{5,16}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(pwd);
		
		if (!m.matches())
			return STAUS_CODE_INVALIDATE;
		
		return STAUS_CODE_VALIDATE;
	}
	
	public static int verifyCname(String cname)
	{
		if (cname == null || cname.equals(""))
			return STATUS_CODE_NULL;
		/*
		 * 中文名字由两到四个汉字组成
		 */
		String reg = "^[\u4E00-\u9FA5]{2,4}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(cname);
		
		if (!m.matches())
			return STAUS_CODE_INVALIDATE;
		
		return STAUS_CODE_VALIDATE;
	}
	
	public static int verifyPhone(String phone)
	{
		if (phone == null || phone.equals(""))
			return STATUS_CODE_NULL;
		/*
		 * 按手机号码的规则,新增170段
		 */
		String reg = "^0?(13[0-9]|15[0-9]|18[0-9]|17[07]|14[57])[0-9]{8}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(phone);
		
		if (!m.matches())
			return STAUS_CODE_INVALIDATE;
		
		return STAUS_CODE_VALIDATE;
	}
	
	public static int verifyEmail(String email)
	{
		if (email == null || email.equals(""))
			return STATUS_CODE_NULL;
		/*
		 * 邮箱地址验证，必须包含@符号
		 */
		//String reg = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String reg = "[a-zA-Z0-9][a-zA-Z0-9._-]{0,16}[a-zA-Z0-9]@([a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9].)+[a-zA-Z0-9]+";
		
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(email);
		
		if (!m.matches())
			return STAUS_CODE_INVALIDATE;
		
		return STAUS_CODE_VALIDATE;
	}
	
	public static int verifyQQ(String qq)
	{
		if (qq == null || qq.equals(""))
			return STATUS_CODE_NULL;
		/*
		 * qq验证
		 */
		String reg = "^[1-9][0-9]{4,11}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(qq);
		
		if (!m.matches())
			return STAUS_CODE_INVALIDATE;
		
		return STAUS_CODE_VALIDATE;
	}
	
	public static int verifyWechat(String wechat)
	{
		if (wechat == null || wechat.equals(""))
			return STATUS_CODE_NULL;
		/*
		 * 微信验证
		 */
		String reg = "^[1-9][0-9]{4,11}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(wechat);
		
		if (!m.matches())
			return STAUS_CODE_INVALIDATE;
		
		return STAUS_CODE_VALIDATE;
	}
	
	


}
