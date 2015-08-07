package com.sxhl.market.model.entity;

import java.io.Serializable;

import com.sxhl.market.model.database.BaseModel;
import com.sxhl.market.model.database.TableDescription;

@TableDescription(name="collectionInfo")
public class CollectionInfo extends BaseModel implements AutoType,Serializable {
	private static final long serialVersionUID = 1L;
	/**
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/
	private String gameId;//软件id
	private String name;//软件名称
	private String packageName;//软件包名
	private String iconMax;// (大图)
	private String iconMin;// (小图)
	private String iconMiddle;// (中图)
	private String starLevel;// (游戏星级)
	private int downCounts;// (下载次数)
	private String size;// (游戏大小)
	private String resume;// (简要描述)
	private String description;// (详细描述)
	private String downAddress;// (下载地址)
	private String imgs;// (保存游戏详细信息图片地址，多图以“，”分开)
	private int typeId;//类型id，1史诗推荐 ，2精品游戏，3游戏专题
	private double version;//版本号

	public CollectionInfo() {
		super();
	}

	public CollectionInfo(String gameId, String name, String packageName,
			String iconMax, String iconMin, String iconMiddle,
			String starLevel, int downCounts, String size, String resume,
			String description, String downAddress,String imgs, int typeId,
			double version) {
		super();
		this.gameId = gameId;
		this.name = name;
		this.packageName = packageName;
		this.iconMax = iconMax;
		this.iconMin = iconMin;
		this.iconMiddle = iconMiddle;
		this.starLevel = starLevel;
		this.downCounts = downCounts;
		this.size = size;
		this.resume = resume;
		this.description = description;
		this.downAddress = downAddress;
		this.imgs = imgs;
		this.typeId = typeId;
		this.version = version;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getIconMax() {
		return iconMax;
	}

	public void setIconMax(String iconMax) {
		this.iconMax = iconMax;
	}

	public String getIconMin() {
		return iconMin;
	}

	public void setIconMin(String iconMin) {
		this.iconMin = iconMin;
	}

	public String getIconMiddle() {
		return iconMiddle;
	}

	public void setIconMiddle(String iconMiddle) {
		this.iconMiddle = iconMiddle;
	}

	public String getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(String starLevel) {
		this.starLevel = starLevel;
	}

	public int getDownCounts() {
		return downCounts;
	}

	public void setDownCounts(int downCounts) {
		this.downCounts = downCounts;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDownAddress() {
		return downAddress;
	}

	public void setDownAddress(String downAddress) {
		this.downAddress = downAddress;
	}

	
	public String getImgs() {
	
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}


	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	

	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "GameInfo [gameId=" + gameId + ", name=" + name
				+ ", packageName=" + packageName + ", iconMax=" + iconMax
				+ ", iconMin=" + iconMin + ", iconMiddle=" + iconMiddle
				+ ", starLevel=" + starLevel + ", downCounts=" + downCounts
				+ ", size=" + size + ", resume=" + resume + ", description="
				+ description + ", downAddress=" + downAddress + ", imgList="
				+ imgs + ", typeId=" + typeId + ", version=" + version + "]";
	}
}
