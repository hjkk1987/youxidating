package com.sxhl.market.utils;

import com.sxhl.market.app.BaseApplication;

public class ScreenZoomUtils {
    /**
     * 
     * @author fcs
     * @Description:以1280为标准，对其他屏幕的一个缩放值
     * @date 2013-6-6 下午5:19:10
     */
    public static float getScreenWZoom_1280(int mScreenWidth){
    	return ((float)mScreenWidth)/1280;
    }
    
    /**
     * 
     * @author fcs
     * @Description:以720为标准，对其他屏幕的一个缩放值
     * @date 2013-6-6 下午5:26:49
     */
    public static float getSceennHZoom_720(int mScreenHeight){
    	return ((float)mScreenHeight)/720;
    }
    /**
     * 
     * @author fcs
     * @Description:获取转换后的值
     * @date 2013-6-6 下午8:16:24
     */
    public static int transformZoomW_1208(int w){
    	return (int)(w*BaseApplication.sScreenWZoom);
    }
    /**
     * 
     * @author fcs
     * @Description:获取转换后的值
     * @date 2013-6-6 下午8:16:42
     */
    public static int transformZoomH_720(int h){
    	return (int)(h*BaseApplication.sScreenHZoom);
    }
}
