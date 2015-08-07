package com.sxhl.market.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.sxhl.market.app.Configuration;
/** 
 * @author  
 * time：2012-8-6 下午6:16:29 
 * description: 
 */
public class StringTool {
	/*
	 *指定字符串进行base64编码
	 *@prama src 
	 *return base64String 
	 */
//	public static String base64Encode(String src)
//	{
//		return Base64.encodeToString(src.getBytes(), Base64.DEFAULT);
//	}
	
	/**
	 * 判断字符串是否为空.    
	 * 
	 * @param src
	 * @return
	 */
	public static boolean isEmpty(String src) {
		return src == null || "".equals(src.trim()) || "null".equalsIgnoreCase(src);
	}

	/**
     * 验证输入的邮箱格式是否符合
     * 
     * @param email
     * @return 是否合法
     */
	public static boolean emailFormat(String email) {
        boolean tag = true;
        final String pattern1 = "\\w+@(\\w+\\.){1,3}\\w+";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }
	
	/**
	 * String是否有特殊字符
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static boolean StringSpecial(String str) throws PatternSyntaxException {      
		boolean tag = true;
		final String regEx = "[^a-zA-Z0-9]";
		final Pattern pattern = Pattern.compile(regEx);
		final Matcher mat = pattern.matcher(str);
		if (mat.find()) {
			tag = false;
		}
		return tag;
    }
	
	/**
	 * 反转义
	 * @param url
	 * @return
	 */
	public static String unescapeUnicode(String url) {
		if (isEmpty(url)) {
			return "";
		}
		String result = url;
		String[][] unescape = new String[][]{
				new String[]{"\\\\", ""},
				new String[]{"\\\"", "\""},
				new String[]{"\\'", "'"},
		};
		for (String[] item : unescape) {
			result = result.replaceAll(item[0], item[1]);
		}
		
		return result;
	}
	
	/**
	 * 将字符串解析成double类型
	 * 
	 * @param value
	 * @return
	 */
	public static double convertStringToDouble(String value) {
		double rlt = 0;
		try {
			rlt = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			// ignore.
		}
		return rlt;
	}
	
	/**
	 * 转换生日格式为 yyyy-MM-dd.
	 * 
	 * @param birthday
	 * @return
	 */
	public static String replaceBirthday(String birthday) {
		if (isEmpty(birthday)) {
			return "";
		}
		
		return birthday.replaceAll("(\\d{4}-\\d{1,2}-\\d{1,2}).*", "$1");
	}
	
	/**
	 * 下拉列表null值处理
	 * 
	 * @param value 需要处理的值
	 * @return 处理后的值
	 * */
	public static String transformNullValue(String value) {
		return isEmpty(value) ? "请选择" : value; 
	}
	
	/**
	 * 字符串大写转换小写处理
	 * 
	 * @param str 需要处理的值
	 * @return 处理后的值
	 * */
	public static String changeAa(String str) {
    	
    	StringBuffer sb = new StringBuffer();
    	for(int i=0; i<str.length(); i++) {
    		
    		char c = str.charAt(i); 
    		if(c >= 'A' && c <= 'Z') c = (char)(c - 'A' + 'a'); 
    		sb.append(c);
    	}
    	
    	return sb.toString();
    }
	
	/**
	 * 将字符串转换为整形
	 * 
	 * @param value
	 * @return
	 */
	public static int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * 得到中英文混合字符串长度.
	 * 
	 * @param src
	 * @param endcoding
	 * @return
	 */
	public static int getChineseLength(String src, String endcoding) {
		// 定义返回的字符串长度
		int len = 0;
		try {
			int j = 0;
			// 按照指定编码得到byte[]
			byte[] byteValue = src.getBytes(endcoding);
			while (true) {
				short tmpst = (short) (byteValue[j] & 0xF0);
				if (tmpst >= 0xB0) {
					if (tmpst < 0xC0) {
						j += 2;
						len += 2;
					} else if ((tmpst == 0xC0) || (tmpst == 0xD0)) {
						j += 2;
						len += 2;
					} else if (tmpst == 0xE0) {
						j += 3;
						len += 2;
					} else if (tmpst == 0xF0) {
						short tmpst0 = (short) (((short) byteValue[j]) & 0x0F);
						if (tmpst0 == 0) {
							j += 4;
							len += 2;
						} else if ((tmpst0 > 0) && (tmpst0 < 12)) {
							j += 5;
							len += 2;
						} else if (tmpst0 > 11) {
							j += 6;
							len += 2;
						}
					}
				} else {
					j += 1;
					len += 1;
				}
				if (j > byteValue.length - 1) {
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			return 0;
		}
		return len;
}
	
	/**
	 * 得到UTF-8编码的中英文混合字符串长度.
	 * 
	 * @param src
	 * @return
	 */
	public static int getChineseLength(String src) {
		return getChineseLength(src, "UTF-8");
	}
	
	public static void main(String[] args) {
		System.out.println(getChineseLength("你好abc"));
	}
	
	/**
	 * 移除字符串数组中的第一个元素.
	 * 
	 * @param original
	 * @return
	 */
	public static String[] removeFirstItem(String[] original) {
        if (original == null || original.length == 0) {
        	return new String[]{};
        }
		int newLength = original.length - 1;
        String[] copy = new String[newLength];
        System.arraycopy(original, 1, copy, 0, newLength);
        return copy;
	}
	
	
	/** 
	 * 截取字符串 截取到小数点后两位
	 * @param str
	 * @return
	 */
	public static String getSubString ( String str ) {
		if ( str != null ) {
			String [ ] values = str.split ( "\\." );
			if ( values [ 1 ] != null && values [ 1 ].length () > 2 ) {
				str = values [ 0 ] + values [ 1 ].substring ( 0 , 1 );
			}
		}
		return str;
	}
	
	
	
	/** 
	 * 截取日期字符串
	 * @param str
	 * @return
	 */
	public static String getSubDateString ( String str,int start,int end ) {
		if(str != null && str.length () >= (end-start) ){
			str = str.substring ( start , end);
			DebugTool.info ( " 截取日期字符串"+str );
		}
		return str;
	}
	/**
	 * 数字字符串转换成数字,KB转换成MB,保留两位小数
	 */
	public static float StringToFloat(String size){
		try {
			DebugTool.debug(Configuration.DEBUG_TAG,"SIZE==="+size);
			float sizeKB=Float.parseFloat(size);
			//BigDecimal b = new BigDecimal(sizeMB); 
			//float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue(); 
			//float b = (float)(Math.round(sizeKB/(1024*1024)));
			float b=sizeKB/(1024*1024);
			  int   scale  =   2;//设置位数  
			  int   roundingMode  =  4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.  
			  BigDecimal   bd  =   new  BigDecimal((double)b);  
			  bd   =  bd.setScale(scale,roundingMode);  
			  b   =  bd.floatValue(); 
			return b;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return 1;
		}
	}
	/**
	 * 格式化文件大小
	 */
	public static String formatFileSize(double size){
		try {
			double b=size/(1024*1024);
			DecimalFormat a = new DecimalFormat("##.##");
			
			return a.format(b);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return "1";
		}
	}
}
