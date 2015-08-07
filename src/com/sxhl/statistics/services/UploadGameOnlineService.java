package com.sxhl.statistics.services;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.text.format.Formatter;

import com.sxhl.market.model.database.PersistentUtils;
import com.sxhl.statistics.model.GameOnlineInfo;
import com.sxhl.statistics.model.InitInfo;
import com.sxhl.statistics.bases.StatisticsConstant;
import com.sxhl.statistics.bases.StatisticsUrlConstant;
import com.sxhl.statistics.net.HttpReqJSonArrayParams;
import com.sxhl.statistics.net.StatisticsTaskResult;
import com.sxhl.statistics.utils.DeviceStatisticsUtils;
import com.sxhl.statistics.utils.StatisticsRecordTestUtils;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.NetUtil;


/**
 * 
 * 上传游戏运行时长信息的服务
 * 
 * @author zhaominglai
 * @date 2014/9/18
 * 
 * **/
public class UploadGameOnlineService extends IntentService {
	
	private static String TAG = "UploadGameOnlineService";

	public UploadGameOnlineService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public UploadGameOnlineService()
	{
		this(TAG);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
			uploadInfos();
	}

	/**
	 * 上传本地数据库中的平台时长信息
	 * 
	 * 1、先判断有没有网络，无网络不进行操作。
	 * 2、判断本地有没有要上传的记录，如果没有记录则不需要上传。
	 * **/
	private void uploadInfos() {
		// TODO Auto-generated method stub
		
		/**
		 * 
		 * 先判断有没有网络，如果没有网络，此次操作取消。
		 * */
		
		if(!NetUtil.isNetworkAvailable(this, true))
		{
			noInternetLog("游戏运行时长服务");
			return;
		}
		
		/**
		 * 手机端的统计只在wifi的情况下上传.
		 * */
		if (DeviceStatisticsUtils.checkNetworkType(this) != StatisticsConstant.NET_TYPE_WIFI){
			noInternetLog("游戏运行时长服务-----有效网络不是wifi,不进行数据上传");
			return;
		}
		
		/**
		 * 如果没有获取到atetId，则向服务器请求一次，如果还是没有的话就返回。等待下一次的上传。因为没有atetId的话，不上传统计数据。
		 * 
		 * **/
		if(DeviceStatisticsUtils.getAtetId(this).equals("1")){
			boolean hasAtetId = DeviceStatisticsUtils.fetchAtetId(this);
			
			if(!hasAtetId){
				return;
			}
		}
		
		/**
		 * 
		 * 判断本地有没有记录，如果没有记录的话则返回。
		 * */
		
		List<GameOnlineInfo> list = PersistentSynUtils.getModelList(GameOnlineInfo.class, " id > 0");
		
		if (list.size()== 0)
		{
			return;
		}
		
		HttpReqJSonArrayParams params = new HttpReqJSonArrayParams();
		params.setAtetId(DeviceStatisticsUtils.getAtetId(this));
		params.setDeviceId(DeviceStatisticsUtils.getDeviceId(this, this.getContentResolver()));
		params.setProductId(DeviceStatisticsUtils.getProductId(this,this.getContentResolver()));
		params.setDeviceCode(DeviceStatisticsUtils.getDeviceCode());
		params.setDeviceType(DeviceStatisticsUtils.getDeviceType(this));
		params.setChannelId(DeviceStatisticsUtils.getChannelId(this));
		params.setLastUploadTime(DeviceStatisticsUtils.getLastUploadTimeGameOnline(this));
		
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n  上传的参数");
		sb.append(" atetId: "+params.getAtetId());
		sb.append(" deviceId: "+params.getDeviceId());
		sb.append(" productId: "+params.getProductId());
		sb.append(" deviceCode: "+params.getDeviceCode());
		sb.append(" deviceType: "+params.getDeviceType());
		sb.append(" channelId: "+params.getChannelId());
		sb.append(" lastUploadTime: "+params.getLastUploadTime());
		sb.append("\r\n");
		sb.append(" 当前设备收集游戏平台运行时长记录数为   " + list.size());
		
		for (int i = 0; i < list.size();i++)
		{
			GameOnlineInfo info = list.get(i);
			params.addDataArray(info);
			
			sb.append("\r\n");
			sb.append(" 游戏名称:"+info.getGameName());
			sb.append(" 程序包名:"+info.getPackageName());
			sb.append(" gameId:"+info.getGameId());
			sb.append(" gameType:"+info.getGameType());
			sb.append(" versionCode:"+info.getVersionCode());
			sb.append(" copyright:"+info.getCopyRight());
			sb.append(" cpId:"+info.getCpId());
			sb.append(" userId:"+info.getUserId());
			sb.append(" 开始时间:"+info.getStartTime());
			sb.append(" 运行时长:"+info.getLongTime());
			sb.append(" 结束时间:"+info.getEndTime());
			sb.append("\r\n");
			
		}
		
		
		byte[] param = params.getGameOnlineJson();
		TaskResult<GameOnlineInfo> result = HttpApi.getObject(StatisticsUrlConstant.HTTP_GAMEONLINE_INFOS1,
				StatisticsUrlConstant.HTTP_GAMEONLINE_INFOS2,StatisticsUrlConstant.HTTP_GAMEONLINE_INFOS3,GameOnlineInfo.class, param);
		
		DebugTool
		.debug(TAG,
				"gameonline  result.getcode="
						+ result.getCode() + "  result.getdata="
						+ result.getData());
		if (result.getCode() == TaskResult.OK) {
			//上传成功后,则删除这条信息
			PersistentSynUtils.execDeleteData(GameOnlineInfo.class, " where id > 0");
			
			DeviceStatisticsUtils.setLastUploadTimeGameOnline(this, System.currentTimeMillis());
			
			
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
			{
				String name = "游戏运行时长服务";
				sb.append("\r\n 数据大小约："+Formatter.formatFileSize(this, param.length));
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this,name, msg);
			}

			return;
		}else if (result.getCode() == TaskResult.FAILED) {
			
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
			{
				String name = "游戏运行时长服务";
				sb = new StringBuilder();
				sb.append("上传设备信息失败！服务器返回状态标志为失败！");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this,name, msg);
			}
			
			return;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_REQUSET_PARAM_ERR) {
			
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
			{
				String name = "游戏运行时长服务";
				sb.append("上传设备信息失败！请求的参数发生错误");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this,name, msg);
			}
			
			return;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_SYSERR) {
			// 没有符合要求的数据
			
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
			{
				String name = "游戏运行时长服务";
				sb.append("上传设备信息失败！服务器系统内部发生错误");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this,name, msg);
			}
			return;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_INVALIDATE_OP) {
			// 没有符合要求的数据
			
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
			{
				String name = "游戏运行时长服务";
				sb.append("上传设备信息失败！非法操作");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this,name, msg);
			}
			return;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_REQUSET_JSON_ERR) {
			// 没有符合要求的数据
			
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
			{
				String name = "游戏运行时长服务";
				sb.append("上传设备信息失败！服务器系统解析请求的json数据出错");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this,name, msg);
			}
			return;
		}
		
	}
	
	private void noInternetLog(String tag)
	{
		StringBuilder sb = new StringBuilder();
		if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
		{
			String name = tag;
			sb.append("\r\n 无网络，服务上传失败  ");
			sb.append("\r\n");
			String msg = sb.toString();
			sb = null;
			StatisticsRecordTestUtils.newLog(this,name, msg);
		}
	}

}
