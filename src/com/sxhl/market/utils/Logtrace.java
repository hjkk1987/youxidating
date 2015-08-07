package com.sxhl.market.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.sxhl.market.app.Configuration;

/**
 * @author wsd
 * @Description:调试工具类
 * @date 2012-12-3 下午1:52:27
 */
public class Logtrace {
    private static final String logDir = "/sdcard/sxhl/logtrace/";
    private static final String logFileName = "market.log";
    private static File fd;
    
    static {
        if(Configuration.IS_DEBUG_ENABLE){
           init(false); 
           trace("Logtrace","Logtrace init====================================================");
        }
    }

    /**
     * 打印调试信息.
     * 
     * @param msg
     */
    public static void debug(String msg) {
        if (Configuration.IS_DEBUG_ENABLE) {
            Log.d(Configuration.DEBUG_TAG, msg);
        }
    }

    /**
     * 打印调试信息.
     * 
     * @param msg
     */
    public static void debug(String tag, String msg) {
        if (Configuration.IS_DEBUG_ENABLE) {
            Log.d(tag, msg);
            trace(tag, msg);
        }
    }

    /**
     * 打印警告信息.
     * 
     * @param msg
     */
    public static void warn(String msg) {
        if (Configuration.IS_DEBUG_ENABLE) {
            Log.w(Configuration.DEBUG_TAG, msg);
        }
    }

    /**
     * 打印警告信息.
     * 
     * @param msg
     */
    public static void warn(String tag, String msg) {
        if (Configuration.IS_DEBUG_ENABLE) {
            Log.w(tag, msg);
            trace(tag, msg);
        }
    }

    /**
     * 打印提示信息.
     * 
     * @param msg
     */
    public static void info(String msg) {
        if (Configuration.IS_DEBUG_ENABLE) {
            Log.i(Configuration.DEBUG_TAG, msg);
        }
    }

    /**
     * 打印提示信息.
     * 
     * @param msg
     */
    public static void info(String tag, String msg) {
        if (Configuration.IS_DEBUG_ENABLE) {
            Log.i(tag, msg);
            trace(tag, msg);
        }
    }

    /**
     * 打印错误信息.
     * 
     * @param msg
     */
    public static void error(String msg, Exception e) {
        if (Configuration.IS_DEBUG_ENABLE) {
            Log.e(Configuration.DEBUG_TAG,"**ERROR** "+ msg, e);
        }
    }

    /**
     * 打印错误信息.
     * 
     * @param msg
     */
    public static void error(String tag, String msg, Exception e) {
        if (Configuration.IS_DEBUG_ENABLE) {
            Log.e(tag, msg, e);
            trace(tag, msg);
        }
    }

    public static void init(boolean isReset) {
        if (!Configuration.IS_DEBUG_ENABLE) {
            return;
        }
        
        File dir = new File(logDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        fd = new File(dir, logFileName);
        if (isReset && fd.exists()) {
            fd.delete();
        }
        if (!fd.exists()) {
            try {
                fd.createNewFile();
            } catch (IOException e) {
                fd = null;
            }
        }
    }

    public static void trace(String tagName, String msg) {
        if(fd==null){
            return;
        }
        try {
            BufferedWriter outBuf = new BufferedWriter(new FileWriter(fd, true));
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String time = fmt.format(date);
            StringBuffer sb = new StringBuffer();
            sb.append(time);
            sb.append("     ");
            if(tagName!=null){
                sb.append("["+tagName+"]");
                sb.append("     ");
            }
            sb.append(msg);
            sb.append("\n");
            outBuf.append(sb);
            outBuf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
