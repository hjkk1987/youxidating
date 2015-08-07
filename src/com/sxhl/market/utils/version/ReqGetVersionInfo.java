package com.sxhl.market.utils.version;

import java.io.Serializable;

/**
 * @ClassName: AppInfo
 * @Description: 应用信息实体
 * @author: Liuqin
 * @date 2013-1-7 上午9:29:32
 * 
 */
public class ReqGetVersionInfo {
    private String deviceCode;
    private String appId;
    
    public String getDeviceCode() {
        return deviceCode;
    }
    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    @Override
    public String toString() {
        return "ReqGetVersionInfo [deviceCode=" + deviceCode + ", appId="
                + appId + "]";
    }
}
