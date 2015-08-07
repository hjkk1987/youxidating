package com.sxhl.market.model.net.http;

import java.lang.reflect.Field;

import android.util.Log;

import com.sxhl.market.app.Configuration;
import com.sxhl.market.utils.DebugTool;

public class HttpReqParams {
	// 设备类型
	private Integer type;

	private String atetId;
	// 型号标识
	private String deviceCode;
	// 产品唯一标识
	private String productId;
	// 频道ID
	private String channelId;
	// 服务器上的设备id

	private String packageName;

	// 游戏专题ID
	private String typeId;
	// 游戏ID
	private String gameId;
	// 游戏关键字
	private String keyWord;
	// 广告模板id
	private String modelId;
	// 用户ID
	private String userId;
	// 游戏评论的内容
	private String commContent;
	// 广场板块ID
	private Integer plateId;
	// 广场帖子ID
	private Integer postId;
	// 意见反馈的内容
	private Integer suggestType;
	// 昵称
	private String nickName;
	// 用户名
	private String userName;

	private String loginName;
	// 用户头像
	private String avator;

	private String oldPassword;

	private String newPassword;

	private String confirmPassword;

	private String icon;

	// 绑定的电话号，用于找回密码
	private String phone;
	// 用户邮箱地址
	private String email;

	// 请求列表数据每页显示的数量
	private Integer pageSize;
	// 请求列表数据的页码
	private Integer currentPage;
	// 帖子回复
	private String content;
	// 意见反馈中反馈联系人的联系方式
	private String contact;
	// 应用id
	private String appId;
	// 模糊查询关键字
	private String searchStr;
	// 评分
	private Integer score;
	// 图片二进制流
	private String binaryImg;

	private Integer isFirstUpload;

	private String giftPackageid;

	public String getGiftPackageid() {
		return giftPackageid;
	}

	public void setGiftPackageid(String giftPackageid) {
		this.giftPackageid = giftPackageid;
	}

	public void setBinaryImg(String binaryImg) {
		this.binaryImg = binaryImg;
	}

	// 中间下载地址
	private Integer downToken;
	private String packageNames;
	// 游戏ID 获取下载地址
	private String id;
	private String deviceId;
	private Integer deviceType;
	private Long startTime;
	private Long endTime;
	private Long duration;
	private String sdCard;
	private String blueToothMac;
	private String cpu;
	private String gpu;
	private String ram;
	private String rom;
	private String versionCode;
	private String sdkVersion;
	private String resolution;
	private Integer dpi;
	private Long installTime;

	private Long minTime;

	public void setMinTime(Long minTime) {
		this.minTime = minTime;
	}

	public Long getMinTime() {
		return minTime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSearchStr(String searchStr) {
		this.searchStr = searchStr;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppId() {
		return appId;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public HttpReqParams() {

	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setCommContent(String commContent) {
		this.commContent = commContent;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId + "";
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public void setPlateId(Integer plateId) {
		this.plateId = plateId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public void setSuggestType(Integer suggestType) {
		this.suggestType = suggestType;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAvator(String avator) {
		this.avator = avator;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public byte[] toJsonParam() {
		Field[] fields = this.getClass().getDeclaredFields();
		Log.i("sheng", "" + fields.length);
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		final int length = fields.length;
		for (int i = 0; i < length; i++) {
			Field field = fields[i];

			Object value = null;
			try {
				value = field.get(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			if (value != null) {
				// 把属性名先拼接进去
				sb.append(field.getName()).append(":");
				if (isInteger(field)) {
					sb.append((Integer) value);
				} else if (isLong(field)) {
					sb.append((Long) value);
				} else {
					sb.append("\"").append((String) value).append("\"");
				}
				// 多个属性之间用“，”号来连接
				sb.append(",");
			}
		}
		// 删除最后一个逗号
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("}");
		if (Configuration.IS_DEBUG_ENABLE) {
			DebugTool.debug(Configuration.DEBUG_TAG, "请求参数为：" + sb.toString());
		}
		Log.e("request", sb.toString());
		return sb.toString().getBytes();
	}

	/**
	 * 判断是否为整形
	 * 
	 * @param item
	 * @return
	 */
	private static boolean isInteger(Field item) {
		if (item.getType().getName().toString().equals("int")
				|| item.getType().getName().toString()
						.equals("java.lang.Integer")) {
			return true;
		}
		return false;
	}

	private static boolean isLong(Field item) {
		if (item.getType().getName().toString().equals("long")
				|| item.getType().getName().toString().equals("java.lang.Long")) {
			return true;
		}
		return false;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public String getPackageNames() {
		return packageNames;
	}

	public void setPackageNames(String packageNames) {
		this.packageNames = packageNames;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getSdCard() {
		return sdCard;
	}

	public void setSdCard(String sdCard) {
		this.sdCard = sdCard;
	}

	public String getBlueToothMac() {
		return blueToothMac;
	}

	public void setBlueToothMac(String blueToothMac) {
		this.blueToothMac = blueToothMac;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getGpu() {
		return gpu;
	}

	public void setGpu(String gpu) {
		this.gpu = gpu;
	}

	public String getRam() {
		return ram;
	}

	public void setRam(String ram) {
		this.ram = ram;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public Integer getDpi() {
		return dpi;
	}

	public void setDpi(Integer dpi) {
		this.dpi = dpi;
	}

	public Long getInstallTime() {
		return installTime;
	}

	public void setInstallTime(Long installTime) {
		this.installTime = installTime;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public String getProductId() {
		return productId;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getRom() {
		return rom;
	}

	public void setRom(String rom) {
		this.rom = rom;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getAtetId() {
		return atetId;
	}

	public void setAtetId(String atetId) {
		this.atetId = atetId;
	}

	public Integer getIsFirstUpload() {
		return isFirstUpload;
	}

	public void setIsFirstUpload(Integer isFirstUpload) {
		this.isFirstUpload = isFirstUpload;
	}

	@Override
	public String toString() {
		return "HttpReqParams {type=" + type + ", atetId=" + atetId
				+ ", deviceCode=" + deviceCode + ", productId=" + productId
				+ ", channelId=" + channelId + ", packageName=" + packageName
				+ ", typeId=" + typeId + ", gameId=" + gameId + ", keyWord="
				+ keyWord + ", modelId=" + modelId + ", userId=" + userId
				+ ", commContent=" + commContent + ", plateId=" + plateId
				+ ", postId=" + postId + ", suggestType=" + suggestType
				+ ", nickName=" + nickName + ", userName=" + userName
				+ ", loginName=" + loginName + ", avator=" + avator
				+ ", oldPassword=" + oldPassword + ", newPassword="
				+ newPassword + ", confirmPassword=" + confirmPassword
				+ ", icon=" + icon + ", phone=" + phone + ", email=" + email
				+ ", pageSize=" + pageSize + ", currentPage=" + currentPage
				+ ", content=" + content + ", contact=" + contact + ", appId="
				+ appId + ", searchStr=" + searchStr + ", score=" + score
				+ ", binaryImg=" + binaryImg + ", isFirstUpload="
				+ isFirstUpload + ", downToken=" + downToken
				+ ", packageNames=" + packageNames + ", id=" + id
				+ ", deviceId=" + deviceId + ", deviceType=" + deviceType
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", duration=" + duration + ", sdCard=" + sdCard
				+ ", blueToothMac=" + blueToothMac + ", cpu=" + cpu + ", gpu="
				+ gpu + ", ram=" + ram + ", rom=" + rom + ", versionCode="
				+ versionCode + ", sdkVersion=" + sdkVersion + ", resolution="
				+ resolution + ", dpi=" + dpi + ", installTime=" + installTime
				+ ",minTime="+minTime+"}";
	}

}
