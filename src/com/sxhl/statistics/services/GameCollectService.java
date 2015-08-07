package com.sxhl.statistics.services;

import java.util.List;

import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.task.TaskManager;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.statistics.bases.StatisticsConstant;
import com.sxhl.statistics.bases.StatisticsUrlConstant;
import com.sxhl.statistics.model.CollectGameInfo;
import com.sxhl.statistics.model.GameOnlineInfo;
import com.sxhl.statistics.net.HttpReqJSonArrayParams;
import com.sxhl.statistics.net.StatisticsTaskResult;
import com.sxhl.statistics.utils.DeviceStatisticsUtils;
import com.sxhl.statistics.utils.StatisticsHelper;
import com.sxhl.statistics.utils.StatisticsRecordTestUtils;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

/**
 * 游戏采集接口服务，用于向服务器上传游戏的点击量与下载量信息
 * 
 * 
 * 1、有网络的情况下就上传。
 * 2、有WIFI的情况下上传所有的信息。如果在非wifi的网络情况下就只上传有下载量的记录信息。
 * 
 * @author zhaominglai
 * @date 2014/6/25
 * 
 * */
public class GameCollectService extends IntentService {

	private String TAG = "GameConllectServer";
	protected TaskManager taskManager;

	public GameCollectService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public GameCollectService() {
		this("GameConllectService");
	}

	/** 向服务器上传信息
	 * 
	 *  在wifi的条件下上传所有的记录，在其他网络的情况下只上传有下载量的记录。
	 *  因为下载量的数据要小于点击量。
	 *  
	 *  
	 *  */
	public void UpLoadGameCollectInfoTask(int netType) {
		
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
		
		//Log.e(TAG, "向网络发送gameCollect信息");
		HttpReqJSonArrayParams params = new HttpReqJSonArrayParams();
		params.setAtetId(DeviceStatisticsUtils.getAtetId(this));
		params.setDeviceId(DeviceStatisticsUtils.getDeviceId(this,
				this.getContentResolver()));
		params.setProductId(DeviceStatisticsUtils.getProductId(this,
				this.getContentResolver()));
		params.setDeviceCode(DeviceStatisticsUtils.getDeviceCode());
		params.setDeviceType(DeviceStatisticsUtils.getDeviceType(this));
		params.setChannelId(DeviceStatisticsUtils.getChannelId(this));
		params.setLastUploadTime(DeviceStatisticsUtils
				.getLastUploadTimeCollect(this));

		/*
		 * SharedPreferences sp = getSharedPreferences(Constant.LOGIN_USER_SP,
		 * MODE_PRIVATE);
		 */
		
		List<CollectGameInfo> lists= null;
		
		if(netType == StatisticsConstant.NET_TYPE_OTHER){
			
			 lists = PersistentSynUtils.getModelList(
					CollectGameInfo.class, " downCount > 0");
		}
		else
		{

			lists = PersistentSynUtils.getModelList(
				CollectGameInfo.class, " id > 0");
		
		}
		
		if (lists.size() == 0) {
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "游戏信息采集服务";
				StringBuilder sb = new StringBuilder();
				sb.append(" 当前设备需要收集的游戏信息记录数为   0");

				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this, name, msg);
			}
			return;
		}

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
		sb.append(" 当前设备收集的游戏信息记录数为   " + lists.size());

		for (int i = 0; i < lists.size(); i++) {
			CollectGameInfo info = lists.get(i);
			params.addDataArray(info);

			sb.append("  游戏名称  ：" + info.getGameName());
			int downCount = (Integer) (info.getDownCount() == null ? "无数据"
					: info.getDownCount());
			
			// downCount = downCount == null ? "无数据" : downCount;

			sb.append("  下载量  ：" + downCount);
			sb.append("  点击量  ：" + info.getClickCount());
			sb.append("  广告点击量  ：" + info.getAdClick());

		}

		byte[] param = params.getGameCollectJson();
		TaskResult<CollectGameInfo> result = HttpApi.getObject(
				StatisticsUrlConstant.HTTP_GAMECOLLECT_INFOS1,
				StatisticsUrlConstant.HTTP_GAMECOLLECT_INFOS2,
				StatisticsUrlConstant.HTTP_GAMECOLLECT_INFOS3,
				CollectGameInfo.class, param);

		DebugTool.info(TAG,
				"CollcetInfosAsynTaskListener  result.getcode="
						+ result.getCode() + "  result.getdata="
						+ result.getData());
		if (result.getCode() == TaskResult.OK) {
			DebugTool.info("gameCollect infos upload successed.");
			
			
			if(netType == StatisticsConstant.NET_TYPE_WIFI){
				
				PersistentSynUtils.execDeleteData(CollectGameInfo.class,
						" where id > 0");
			}else if(netType == StatisticsConstant.NET_TYPE_OTHER){
				
				PersistentSynUtils.execDeleteData(CollectGameInfo.class,
						" where downCount > 0");
			}
			DeviceStatisticsUtils.setLastUploadTimeCollect(this,
					System.currentTimeMillis());
			Log.e("collectGame",
					"dsffa"
							+ DeviceStatisticsUtils
									.getLastUploadTimeCollect(this));
			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {

				String name = "游戏信息采集服务";
				
				sb.append("\r\n 数据大小约："+Formatter.formatFileSize(this, param.length));
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this, name, msg);
			}

		} else if (result.getCode() == TaskResult.FAILED) {

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "游戏信息采集服务";
				sb = new StringBuilder();
				sb.append("上传游戏采集信息失败！ 服务器返回状态标志为失败！");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this, name, msg);
			}

		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_REQUSET_PARAM_ERR) {

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "游戏信息采集服务";

				sb.append("上传游戏采集信息失败！请求的参数发生错误");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this, name, msg);
			}

			return;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_SYSERR) {
			// 没有符合要求的数据

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "游戏信息采集服务";

				sb.append("上传游戏采集信息失败！服务器系统内部发生错误");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this, name, msg);
			}
			return;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_INVALIDATE_OP) {
			// 没有符合要求的数据

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "游戏信息采集服务";

				sb.append("上传游戏采集信息失败！非法操作");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this, name, msg);
			}
			return;
		} else if (result.getCode() == StatisticsTaskResult.STATISTICS_REQUSET_JSON_ERR) {
			// 没有符合要求的数据

			if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true) {
				String name = "游戏信息采集服务";

				sb.append("上传游戏采集信息失败！服务器系统解析请求的json数据出错");
				String msg = sb.toString();
				sb = null;
				StatisticsRecordTestUtils.newLog(this, name, msg);
			}
			return;
		}

	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub

		/*
		 * if (ALLGameControllerService.IS_RUNNING == false) return;
		 */
		/*
		 * if (StatisticsHelper.IS_RUNNING == false) return;
		 */
		
		int netType = checkNetworkType(this);
		
		if (netType == StatisticsConstant.NET_TYPE_NULL)
		{
			return;
		}
		
		UpLoadGameCollectInfoTask(netType);

	}
	
	
	/**
	 * 检测网络连接的类型。
	 * 
	 * @return 0没有网络连接　1　wifi网络　　2其他网络连接如GPRS等。
	 * 
	 * **/
	private int checkNetworkType(Context context){
		
		ConnectivityManager connectivityManager = (ConnectivityManager)      
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo(); 
		
		if(netInfo == null || !netInfo.isAvailable())
		{
			//没有网络连接。
			return StatisticsConstant.NET_TYPE_NULL;
		}
		
		if(netInfo.getType() == ConnectivityManager.TYPE_WIFI)
		{
			return StatisticsConstant.NET_TYPE_WIFI;
		}
		
		return StatisticsConstant.NET_TYPE_OTHER;
	}

}
