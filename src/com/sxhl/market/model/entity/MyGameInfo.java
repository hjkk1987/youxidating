package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

/** 
 * @ClassName: MyGameInfo 
 * @Description: 我的游戏列表中的实体类
 * @author: Liuqin
 * @date 2012-12-14 下午1:44:57 
 *  
 */  
@TableDescription(name="myGameInfo")
public class MyGameInfo extends BaseModel implements AutoType,Serializable{

	/**
	* @Fields serialVersionUID :
	*/
	private static final long serialVersionUID = 1L;
	
	//应用ID
	private String gameId;
	//应用包名
	private String packageName;
	//启动的activity
	private String launchAct;
	//应用名称
	private String name;
	//版本号
	private int versionCode;
	//应用图标url
	private String iconUrl;
	//下载url
	private String downUrl;
	//本地保存目录
	private String localDir;
	//本地保存文件名
	private String localFilename;
	//状态：1已下载但未安装，2已安装，0未下载
	private int state;
	
	private String id;
	
	public MyGameInfo() {
		super();
	}
	
	public MyGameInfo(String gameId, String packageName, String name,
			String iconUrl, String downUrl, String localDir,
			String localFilename, int state, int versionCode, String launchAct) {
		super();
		this.gameId = gameId;
		this.packageName = packageName;
		this.name = name;
		this.iconUrl = iconUrl;
		this.downUrl = downUrl;
		this.localDir = localDir;
		this.localFilename = localFilename;
		this.state = state;
		this.versionCode = versionCode;
		this.launchAct = launchAct;
	}

	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	public String getLocalDir() {
		return localDir;
	}
	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}
	public String getLocalFilename() {
		return localFilename;
	}
	public void setLocalFilename(String localFilename) {
		this.localFilename = localFilename;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}



	public int getVersionCode() {
		return versionCode;
	}



	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}



	public String getLaunchAct() {
		return launchAct;
	}



	public void setLaunchAct(String launchAct) {
		this.launchAct = launchAct;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    @Override
    public String toString() {
        return "MyGameInfo [gameId=" + gameId + ", packageName=" + packageName
                + ", launchAct=" + launchAct + ", name=" + name
                + ", versionCode=" + versionCode + ", iconUrl=" + iconUrl
                + ", downUrl=" + downUrl + ", localDir=" + localDir
                + ", localFilename=" + localFilename + ", state=" + state
                + ", id=" + id + "]";
    }
}
