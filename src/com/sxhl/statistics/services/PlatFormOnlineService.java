package com.sxhl.statistics.services;

import java.util.List;

import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.statistics.model.InitInfo;
import com.sxhl.statistics.utils.DeviceStatisticsUtils;
import com.sxhl.statistics.utils.PackageInfoUtils;
import com.sxhl.statistics.utils.StatisticsRecordTestUtils;

import android.app.IntentService;
import android.content.Intent;

public class PlatFormOnlineService extends IntentService {

	private static String TAG = "PlatFormOnlineService";
	
	private static InitInfo mLastAppInfo;
	
	private static String mLastPackageName;
	
	private static Long mPlatFormTimeStep;
	
	public PlatFormOnlineService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public PlatFormOnlineService()
	{
		this(TAG);
	}

	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
			runPlatFormStatistic();
	}

	/**
	 * 平台运行在线时长情况统计
	 * 
	 * */
	private void runPlatFormStatistic() {
		// TODO Auto-generated method stub
		
		String runningPackageName = PackageInfoUtils.getRunningPackageName(this);
		String platFormName = getPackageName();
		
		if(mLastPackageName == null)
		{
			/**
			 * 设备开机后，第一次运行该服务，此时lastPackageName为空
			 * */
			mLastPackageName = runningPackageName;
			//如果平台正在运行，则向数据库中新增1条记录
			if(mLastPackageName.equals(platFormName))
			{
				
				addInfoWhenAppBoot(platFormName);
				return;
			}
			
			
		}
		else if (mLastPackageName.equals(runningPackageName) && mLastPackageName.equals(platFormName))
		{
			/**
			 * 上一次检测到正在运行的应用也是游戏平台，当前检测到的应用也是游戏平台，这种情况就统一当作游戏平台连续运行。
			 * 
			 * 游戏连续运行就需要更新数据库中的记录，而不是新增记录。
			 * 
			 * 更新的信息主要是开始时间、运行时长、结束时间。
			 * 
			 * 开始时间要用结束时间-运行时长来动态更新
			 * */
			
			if(mLastAppInfo != null)
			{
				mPlatFormTimeStep++;
				
				long curTime = System.currentTimeMillis();
				long duration = mPlatFormTimeStep*30;
				
				/**
				 * 如果不是开始时间与结束时间不是同一天，则分割时间
				 * */
				if(DeviceStatisticsUtils.ifTwoTimeStampBetweenOneDay(mLastAppInfo.getStartTime(), curTime))
				{
					long endTime = DeviceStatisticsUtils.getTimeToday(DeviceStatisticsUtils.timeToDay(curTime))- 1000;
					long durantion1 = (curTime - endTime)/1000;
					mLastAppInfo.setEndTime(endTime);
					mLastAppInfo.setLongTime(duration - durantion1);
					mLastAppInfo.setStartTime(curTime - duration*1000);
					
					if (checkDatabaseIfCanUpdate(mLastAppInfo.getId()))
					{
						/**
						 * 如果数据库中可以更新当前记录，则执行更新动作
						 * */
						PersistentSynUtils.update(mLastAppInfo);
						updateInfoLog(curTime - duration*1000, duration, endTime);
						addInfoAtOneDayFirst(platFormName,endTime+1000);
						return;
					}
					else
					{
						addInfoWhenAppBoot(platFormName);
					}
				}
				
					
				mLastAppInfo.setEndTime(curTime);
				mLastAppInfo.setLongTime(duration);
				mLastAppInfo.setStartTime(curTime - duration*1000);
				
				if (checkDatabaseIfCanUpdate(mLastAppInfo.getId()))
				{
					/**
					 * 如果数据库中可以更新当前记录，则执行更新动作
					 * */
					PersistentSynUtils.update(mLastAppInfo);
					
					updateInfoLog(curTime - duration*1000, duration, curTime);
				}
				else
				{
					/**
					 * 如果数据库中不能更新记录，则新建记录
					 * 
					 * */
					addInfoWhenAppBoot(platFormName);
				}
			}
		}
		else if(!mLastPackageName.equals(runningPackageName))
		{
			/**
			 * 上一次检测到的应用不是当前运行的应用。
			 * 
			 * 1、上次是其他应用，这次是平台。
			 * 2、上次是平台，这次是其他应用　。
			 * 
			 * */
			
			//当前平台正在运行
			if(runningPackageName.equals(platFormName))
			{
				addInfoWhenAppBoot(platFormName);
				return;
			}
			
			mLastPackageName = runningPackageName;
			
		}
	}

	private void addInfoWhenAppBoot(String packageName) {
		// TODO Auto-generated method stub
		long curruntTime = System.currentTimeMillis();
		InitInfo info = new InitInfo();
		checkDataBaseIsOverwrite();
		
		mPlatFormTimeStep = 0L;
		info.setStartTime(curruntTime);
		info.setLongTime(mPlatFormTimeStep);
		info.setEndTime(curruntTime);
		info.setUserId(DeviceStatisticsUtils.getUserId(this));
		
		long id = PersistentSynUtils.addModel(info);
		
		if (id != -1)
		{
			mLastAppInfo = info;
			mLastAppInfo.setId(id+"");
			mLastPackageName = packageName;
			
			addNewInfoLog(curruntTime, curruntTime);
		}
	}
	
	/**
	 * 对于平台跨天运行，将时间分割
	 * 开始时间设置为当天的凌晨
	 * 
	 * */
	private void addInfoAtOneDayFirst(String packageName,long startTime) {
		// TODO Auto-generated method stub
		long curruntTime = System.currentTimeMillis();
		InitInfo info = new InitInfo();
		
		checkDataBaseIsOverwrite();
		
		mPlatFormTimeStep = 0L;
		info.setStartTime(startTime);
		info.setLongTime((curruntTime-startTime)/1000);
		info.setEndTime(curruntTime);
		info.setUserId(DeviceStatisticsUtils.getUserId(this));
		
		long id = PersistentSynUtils.addModel(info);
		
		if (id != -1)
		{
			mLastAppInfo = info;
			mLastAppInfo.setId(id+"");
			mLastPackageName = packageName;
			
			addNewInfoLog(startTime, curruntTime);
		}
	}
	
	private void checkDataBaseIsOverwrite()
	{
		List<InitInfo> recordList = PersistentSynUtils.getModelList(InitInfo.class," id > 0");
		
		if (recordList != null && recordList.size() > 100)
		{
			StatisticsRecordTestUtils.databaseOverwriteLog(this, "平台在线统计服务");
			PersistentSynUtils.delete(recordList.get(0));
		}
				
	}
	
	
	/**
	 * 
	 * 检测是否可以向数据库中更新记录。
	 * 一般情况出现在上传服务成功后将数据库中的记录清空了。
	 * 
	 * */
	private boolean checkDatabaseIfCanUpdate(String id)
	{
		List<InitInfo> recordList = PersistentSynUtils.getModelList(InitInfo.class, " id = "+ id);
		
		return recordList.size() > 0 ? true:false;
	}
	
	
	private void addNewInfoLog(long startTime,long endTime)
	{
		StringBuilder sb = new StringBuilder();
		if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
		{
			String name = "平台运行时长统计服务";
			sb.append("\r\n 新增游戏平台运行记录  ");
			sb.append(" 开始时间："+DeviceStatisticsUtils.getDateToString(startTime));
			sb.append(" 结束时间："+DeviceStatisticsUtils.getDateToString(endTime));
			sb.append("\r\n");
			String msg = sb.toString();
			sb = null;
			StatisticsRecordTestUtils.newLog(this,name, msg);
		}
	}
	
	private void updateInfoLog(long startTime,long longTime,long endTime)
	{
		StringBuilder sb = new StringBuilder();
		if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
		{
			String name = "平台运行时长统计服务";
			sb.append("\r\n 游戏平台连续运行  ");
			sb.append(" 开始时间："+DeviceStatisticsUtils.getDateToString(startTime));
			sb.append(" 运行时长："+longTime + "秒");
			sb.append(" 结束时间："+DeviceStatisticsUtils.getDateToString(endTime));
			sb.append("\r\n");
			String msg = sb.toString();
			sb = null;
			StatisticsRecordTestUtils.newLog(this,name, msg);
		}
	}
	
}
