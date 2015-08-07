package com.sxhl.market.utils;

/**
 * @author fxl
 * time: 2012-9-11 上午11:17:50
 * description: webView页面JS字体大小显示工具类
 */
public class FontSizeJSTool {
	
	/** 缩放尺寸间距*/
	public static final int ZOOM = 3;
	public static final int SMALL_SIZE = 15;
	public static final int MIDDLE_SIZE = SMALL_SIZE + ZOOM;
	public static final int BIG_SIZE = MIDDLE_SIZE + ZOOM;
	
	private static FontSizeJSTool instance;
	
	private int fontSize = MIDDLE_SIZE;
	
	private FontSizeJSTool() {
    }
    
	public static FontSizeJSTool getInstance(){
		if(instance==null){
			instance=new FontSizeJSTool();
		}
		return instance;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		if(fontSize > BIG_SIZE){
			this.fontSize = SMALL_SIZE;
		}else{
			this.fontSize = fontSize;
		}
	}
}
