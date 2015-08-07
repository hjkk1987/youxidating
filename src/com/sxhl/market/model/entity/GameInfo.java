package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

/**
 * 
 * @ClassName: GameInfo
 * @Description: 软性信息实体类
 * @author 孔德升
 * @date 2012-12-5 下午2:05:55
 */
@TableDescription(name = "gameInfo")
public class GameInfo extends BaseModel implements AutoType, Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private String gameId;// 软件id
	private String gameName;// 软件名称
	private String cpId;// 渠道id
	private String file;// 游戏文件的下载地址
	private String maxPhoto;// (大图)
	private String middlePhoto;// (中图)
	private String minPhoto;// (小图)
	private String erectPhoto;// 游戏竖图的URL
	private Integer fitAge;// 游戏适合年龄
	private double startLevel; // 游戏星际
	private Integer gameDownCount;// 下载次数
	private Integer gameSize;// (游戏大小)
	private Integer gamePrice;// 游戏价格
	private String remark;// (简要描述)
	private String appendixZip;// 游戏的附加信息（如启动游戏之前显示的图片等）的下载地址
	private Long createTime;
	private Long updateTime;
	private String packageName;// 软件包名
	private String versionName;// 版本名
	private Integer versionCode;// 版本号
	private Integer handleType;// 手柄类型
	private Integer isGood;// 是否属于精品游戏，0代表是精品游戏，1代表不是精品游戏
	private double shortNo;// 游戏排序号，值越大排越前
	// private String imgs;//详情图片的json封装
	private Group<ScreenShootInfo> imgs;// 详情图片json的封装

	private String typeId;// 分类id
	private String typeName;// 分类的名字

	// private Group<DownloadAddressInfo> downloadInfo; // 封装下载地址的json字符串；

	private String midDownAdress;// 中间下载地址
	private Integer downToken;// 下载方式；1：从本地下载；2：从网络获取真实下载地址

	// private Group<DownloadAddressInfo> data; // 封装下载地址的json字符串；

	private Integer gameType; // 0代表代理游戏，1代表第三方游戏
	
	private int controllability;

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getMaxPhoto() {
		return maxPhoto;
	}

	public void setMaxPhoto(String maxPhoto) {
		this.maxPhoto = maxPhoto;
	}

	public String getMiddlePhoto() {
		return middlePhoto;
	}

	public void setMiddlePhoto(String middlePhoto) {
		this.middlePhoto = middlePhoto;
	}

	public String getMinPhoto() {
		return minPhoto;
	}

	public void setMinPhoto(String minPhoto) {
		this.minPhoto = minPhoto;
	}

	public String getErectPhoto() {
		return erectPhoto;
	}

	public void setErectPhoto(String erectPhoto) {
		this.erectPhoto = erectPhoto;
	}

	public Integer getFitAge() {
		return fitAge;
	}

	public void setFitAge(Integer fitAge) {
		this.fitAge = fitAge;
	}

	public double getStartLevel() {
		return startLevel;
	}

	public void setStartLevel(double startLevel) {
		this.startLevel = startLevel;
	}

	public Integer getGameDownCount() {
		return gameDownCount;
	}

	public void setGameDownCount(Integer gameDownCount) {
		this.gameDownCount = gameDownCount;
	}

	public Integer getGameSize() {
		return gameSize;
	}

	public void setGameSize(Integer gameSize) {
		this.gameSize = gameSize;
	}

	public Integer getGamePrice() {
		return gamePrice;
	}

	public void setGamePrice(Integer gamePrice) {
		this.gamePrice = gamePrice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAppendixZip() {
		return appendixZip;
	}

	public void setAppendixZip(String appendixZip) {
		this.appendixZip = appendixZip;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Integer getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(Integer versionCode) {
		this.versionCode = versionCode;
	}

	public Integer getHandleType() {
		return handleType;
	}

	public void setHandleType(Integer handleType) {
		this.handleType = handleType;
	}

	public Integer getIsGood() {
		return isGood;
	}

	public void setIsGood(Integer isGood) {
		this.isGood = isGood;
	}

	public Double getShortNo() {
		return shortNo;
	}

	public void setShortNo(Double shortNo) {
		this.shortNo = shortNo;
	}

	public Group<ScreenShootInfo> getImgs() {
		return imgs;
	}

	public void setImgs(Group<ScreenShootInfo> imgs) {
		this.imgs = imgs;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	// public Group<DownloadAddressInfo> getDownloadInfo() {
	// return downloadInfo;
	// }
	//
	// public void setDownloadInfo(Group<DownloadAddressInfo> downloadInfo) {
	// this.downloadInfo = downloadInfo;
	// }

	public String getMidDownAdress() {
		return midDownAdress;
	}

	public void setMidDownAdress(String midDownAdress) {
		this.midDownAdress = midDownAdress;
	}

	public Integer getDownToken() {
		return downToken;
	}

	public void setDownToken(Integer downToken) {
		this.downToken = downToken;
	}

	//
	// public Group<DownloadAddressInfo> getData() {
	// return data;
	// }
	//
	// public void setData(Group<DownloadAddressInfo> data) {
	// this.data = data;
	// }

	public Integer getGameType() {
		return gameType;
	}

	public void setGameType(Integer gameType) {
		this.gameType = gameType;
	}
	
	public int getControllability() {
		return controllability;
	}

	public void setControllability(int controllability) {
		this.controllability = controllability;
	}

	@Override
	public String toString() {
		return "GameInfo [gameId=" + gameId + ", gameName=" + gameName
				+ ", cpId=" + cpId + ", file=" + file + ", maxPhoto="
				+ maxPhoto + ", middlePhoto=" + middlePhoto + ", minPhoto="
				+ minPhoto + ", erectPhoto=" + erectPhoto + ", fitAge="
				+ fitAge + ", startLevel=" + startLevel + ", gameDownCount="
				+ gameDownCount + ", gameSize=" + gameSize + ", gamePrice="
				+ gamePrice + ", remark=" + remark + ", appendixZip="
				+ appendixZip + ", createTime=" + createTime + ", updateTime="
				+ updateTime + ", packageName=" + packageName
				+ ", versionName=" + versionName + ", versionCode="
				+ versionCode + ", handleType=" + handleType + ", isGood="
				+ isGood + ", shortNo=" + shortNo + ", imgs=" + imgs
				+ ", typeId=" + typeId + ", typeName=" + typeName
				// + ", downloadInfo=" + downloadInfo + ", midDownAdress="
				// + midDownAdress + ", downToken=" + downToken + ", data=" +
				// data
				+ ", gameType=" + gameType
				+ ", controllability=" + controllability + 
				"]";
	}

}
