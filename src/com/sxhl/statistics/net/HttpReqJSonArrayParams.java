package com.sxhl.statistics.net;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


import com.sxhl.market.app.Configuration;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.statistics.model.CollectGameInfo;
import com.sxhl.statistics.model.GameOnlineInfo;
import com.sxhl.statistics.model.InitInfo;


public class HttpReqJSonArrayParams {
	// 游戏专题ID
	private Integer typeId;
	// 游戏ID
	private String gameId;
	// 游戏关键字
	private String keyWord;
	// 用户ID
	private Integer userId;
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
	// 用户头像
	private String avator;
	// 绑定的电话号，用于找回密码
	private String phtone;
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
	// 设备型号标识，后台根据此数据获取改设备所属公司下的游戏专题
	private String deviceCode;
	// 图片的规格类型
	private String sizeTypeCode;
	// 图片的用途类型
	private String useTypeCode;
	// 要求的数量
	private int number;
	// 游戏的下载地址
	private String url;
	


	// 中间下载地址
	private Integer downToken;
	private String packageNames;
	// 游戏ID 获取下载地址
	private String id;
	private String atetId;
	private String deviceId;
	private Integer deviceType;
	private String chanalId;
	private Long startTime;
	private Long endTime;

	private String loginName;
	private String password;
	private String newPassword;
	private String oldPassword;
	private String confirmPassword;
	private String chineseName;
	private String phone;
	private Integer sex;
	private String birthday;
	private String address;
	private String qq;
	private String wechat;

	private String gameType;
	
	private String channelId;//渠道ID，预留字段，值设置为0
	private String productId;//终端设备的唯一标识。
	private String cpId;
	private Long enterTime;
	private Long exitTime;
	private String gameName;
	private String versionCode;
	private String packageName;
	
	private List<CollectGameInfo> Data;
	private List<InitInfo> platFormTimeInfoData;
	private List<GameOnlineInfo> gameOnlineTimeInfoData;
	private Long lastUploadTime;
	
	public void setLastUploadTime(long time)
	{
		this.lastUploadTime = time;
	}
	
	public Long getLastUploadTime()
	{
		return this.lastUploadTime;
	}
	
	
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	public void addDataArray(CollectGameInfo array)
	{
		if (Data == null)
			Data = new ArrayList<CollectGameInfo>();
		
		Data.add(array);
	}
	
	public void addDataArray(GameOnlineInfo array)
	{
		if (gameOnlineTimeInfoData == null)
			gameOnlineTimeInfoData = new ArrayList<GameOnlineInfo>();
		
		gameOnlineTimeInfoData.add(array);
	}
	
	public void addDataArray(InitInfo array)
	{
		if (platFormTimeInfoData == null)
			platFormTimeInfoData = new ArrayList<InitInfo>();
		
		platFormTimeInfoData.add(array);
	}
	
	

	public HttpReqJSonArrayParams() {

	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setCommContent(String commContent) {
		this.commContent = commContent;
	}

	public void setTypeId(Integer typeId) {
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

	public void setPhtone(String phtone) {
		this.phtone = phtone;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public String getSizeTypeCode() {
		return sizeTypeCode;
	}

	public void setSizeTypeCode(String sizeTypeCode) {
		this.sizeTypeCode = sizeTypeCode;
	}

	public String getUseTypeCode() {
		return useTypeCode;
	}

	public void setUseTypeCode(String useTypeCode) {
		this.useTypeCode = useTypeCode;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getDownToken() {
		return downToken;
	}

	public void setDownToken(Integer downToken) {
		this.downToken = downToken;
	}

	public String getPackageNames() {
		return packageNames;
	}

	public void setPackageNames(String packageNames) {
		this.packageNames = packageNames;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWechat() {
		return wechat;
	}
	

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getNickName() {
		return nickName;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return email;
	}

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}
	
	public long getStartTime()
	{
		return this.startTime;
	}
	
	public long getEndTime()
	{
		return this.endTime;
	}
	
	public void setEndTime(long endTime)
	{
		this.endTime = endTime;
	}
	
	public String getChanalId() {
		return chanalId;
	}

	public void setChanalId(String chanalId) {
		this.chanalId = chanalId;
	}

	public byte[] toJsonParam() {
		Field[] fields = this.getClass().getDeclaredFields();
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
				} 
				 else if (isLong(field)){
					sb.append((Long) value);
				}else {
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
	
	/**
	 * 判断是否为long形
	 * 
	 * @author zhaominglai
	 * @date 2014/6/21
	 * 
	 * */
	private static boolean isLong(Field item) {
		if (item.getType().getName().toString().equals("long")
				|| item.getType().getName().toString()
				.equals("java.lang.Long")) {
			return true;
		}
		return false;
	}
	
	private static boolean isList(Field item)
	{
		if (item.getType().getName().toString().equals("List<CollectGameInfo>")
				|| item.getType().getName().toString().equals("List<InitInfo>")
				|| item.getType().getName().toString()
				.equals("java.lang.List")) {
			return true;
		}
		return false;
	}
	
	public byte[] getGameCollectJson()
	{
		JSONObject object = new JSONObject();
		try {
			object.put("atetId", this.atetId);
			object.put("deviceType", this.deviceType);
			object.put("deviceId", this.deviceId);
			object.put("deviceCode", this.deviceCode);
			object.put("productId", this.productId);
			object.put("channelId", this.channelId);
			object.put("lastUploadTime", this.lastUploadTime);
			JSONArray DataArray = new JSONArray();
			
			for (int i = 0;i < this.Data.size();i++)
			{
				JSONObject obj = new JSONObject();
				
				obj.put("userId", this.Data.get(i).getUserId());
				obj.put("packageName", this.Data.get(i).getPackageName());
				obj.put("gameId", this.Data.get(i).getGameId());
				obj.put("cpId", this.Data.get(i).getCpId());
				obj.put("gameName", this.Data.get(i).getGameName());
				obj.put("gameType", this.Data.get(i).getGameType());
				obj.put("copyright", this.Data.get(i).getCopyRight());
				
				obj.put("clickCount", this.Data.get(i).getClickCount());
				obj.put("adClick", this.Data.get(i).getAdClick());
				obj.put("downCount", this.Data.get(i).getDownCount());
				obj.put("recordTime", this.Data.get(i).getRecordTime());
				
				DataArray.put(obj);
				
			}
			
			object.put("data", DataArray);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String jstring = object.toString();
		
		Log.e("json 解析",jstring);
		
		return jstring.getBytes();
		
	}
	
	
	/**
	 * 获取平台时长的json数组
	 * */
	public byte[] getPlatFormOnlineJson()
	{
		JSONObject object = new JSONObject();
		try {
			object.put("atetId", this.atetId);
			object.put("deviceId", this.deviceId);
			object.put("productId", this.productId);
			object.put("deviceCode", this.deviceCode);
			object.put("deviceType", this.deviceType);
			object.put("packageName", this.packageName);
			object.put("versionCode",this.versionCode);
			object.put("channelId", this.channelId);
			object.put("lastUploadTime", this.lastUploadTime);
			JSONArray DataArray = new JSONArray();
			
			for (int i = 0;i < this.platFormTimeInfoData.size();i++)
			{
				JSONObject obj = new JSONObject();
				
				obj.put("userId", this.platFormTimeInfoData.get(i).getUserId());
				obj.put("startTime", this.platFormTimeInfoData.get(i).getStartTime());
				obj.put("endTime", this.platFormTimeInfoData.get(i).getEndTime());
				obj.put("longTime", this.platFormTimeInfoData.get(i).getLongTime());
				
				DataArray.put(obj);
				
			}
			
			object.put("data", DataArray);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String jstring = object.toString();
		
		Log.e("json 解析",jstring);
		
		return jstring.getBytes();
		
	}
	/**
	 * 获取游戏运行时长的json数组
	 * */
	public byte[] getGameOnlineJson()
	{
		JSONObject object = new JSONObject();
		try {
			object.put("atetId", this.atetId);
			object.put("deviceId", this.deviceId);
			object.put("productId", this.productId);
			object.put("deviceCode", this.deviceCode);
			object.put("deviceType", this.deviceType);
			object.put("channelId", this.channelId);
			object.put("lastUploadTime", this.lastUploadTime);
			JSONArray DataArray = new JSONArray();
			
			for (int i = 0;i < this.gameOnlineTimeInfoData.size();i++)
			{
				JSONObject obj = new JSONObject();
				
				obj.put("userId", this.gameOnlineTimeInfoData.get(i).getUserId());
				obj.put("gameId", this.gameOnlineTimeInfoData.get(i).getGameId());
				obj.put("gameName", this.gameOnlineTimeInfoData.get(i).getGameName());
				obj.put("gameType", this.gameOnlineTimeInfoData.get(i).getGameType());
				obj.put("packageName", this.gameOnlineTimeInfoData.get(i).getPackageName());
				obj.put("versionCode", this.gameOnlineTimeInfoData.get(i).getVersionCode());
				obj.put("copyright", this.gameOnlineTimeInfoData.get(i).getCopyRight());
				obj.put("cpId", this.gameOnlineTimeInfoData.get(i).getCpId());
				obj.put("startTime", this.gameOnlineTimeInfoData.get(i).getStartTime());
				obj.put("endTime", this.gameOnlineTimeInfoData.get(i).getEndTime());
				obj.put("longTime", this.gameOnlineTimeInfoData.get(i).getLongTime());
				
				DataArray.put(obj);
				
			}
			
			object.put("data", DataArray);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String jstring = object.toString();
		
		Log.e("json 解析",jstring);
		
		return jstring.getBytes();
		
	}
	
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		return "HttpReqParams [typeId=" + typeId + ", gameId=" + gameId
				+ ", keyWord=" + keyWord + ", userId=" + userId
				+ ", commContent=" + commContent + ", plateId=" + plateId
				+ ", postId=" + postId + ", suggestType=" + suggestType
				+ ", nickName=" + nickName + ", userName=" + userName
				+ ", avator=" + avator + ", phtone=" + phtone + ", email="
				+ email + ", pageSize=" + pageSize + ", currentPage="
				+ currentPage + ", content=" + content + ", contact=" + contact
				+ ", deviceCode=" + deviceCode + ", sizeTypeCode="
				+ sizeTypeCode + ", useTypeCode=" + useTypeCode + ", number="
				+ number + ", url=" + url + ", downToken=" + downToken
				+ ", packageNames=" + packageNames + ", id=" + id
				+ ", deviceId=" + deviceId + ", loginName=" + loginName
				+ ", password=" + password + ", newPassword=" + newPassword
				+ ", oldPassword=" + oldPassword + ", confirmPassword="
				+ confirmPassword + ", chineseName=" + chineseName + ", phone="
				+ phone + ", sex=" + sex + ", birthday=" + birthday
				+ ", address=" + address + ", qq=" + qq + ", wechat=" + wechat
				+ ", gameType=" + gameType 
				+ ",startTime="+ startTime+",endTime "+endTime+"]";
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public long getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}

	public long getExitTime() {
		return exitTime;
	}

	public void setExitTime(long exitTime) {
		this.exitTime = exitTime;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAtetId() {
		return atetId;
	}

	public void setAtetId(String atetId) {
		this.atetId = atetId;
	}

	

}
