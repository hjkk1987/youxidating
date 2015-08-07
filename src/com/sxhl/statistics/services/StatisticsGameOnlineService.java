package com.sxhl.statistics.services;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.statistics.model.GameOnlineInfo;
import com.sxhl.statistics.model.InitInfo;
import com.sxhl.statistics.utils.DeviceStatisticsUtils;
import com.sxhl.statistics.utils.PackageInfoUtils;
import com.sxhl.statistics.utils.StatisticsRecordTestUtils;


/**
 * 用于统计游戏在线时长的服务，每30秒钟为一次
 * 
 * 
 * 在手机端中,游戏统计只用来统计本地数据库中存在的游戏.
 * 
 * @author zhaominglai
 * @date 2014/10/21
 * 
 * */
public class StatisticsGameOnlineService extends IntentService {

	private static String TAG = "StatisticsGameOnlineService";
	
	private static GameOnlineInfo mLastAppInfo;
	
	private static String mLastPackageName;
	
	private static Long mAppTimeStep;
	
	private static List<String> mHomeList;
	
	public StatisticsGameOnlineService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public StatisticsGameOnlineService()
	{
		this(TAG);
	}

	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
			gameStatistics();
			//runAppStatistic();
	}
	
	/**
	 * 游戏统计服务
	 * 
	 * @author zhaominglai
	 * @date 2014/10/22
	 * */
	private void gameStatistics(){
		//获取正在运行的APP包名
		String runningPackageName = PackageInfoUtils.getRunningPackageName(this);
		
		//如果是atet平台下的游戏就进行统计，否则不统计
		if (DeviceStatisticsUtils.isAtetGame(runningPackageName)) {
			//如果这一次运行的游戏和上一次不同或者是mLastPackageName为null，则算成新游戏开始运行
			if (mLastPackageName == null || !mLastPackageName.equals(runningPackageName)) {
				addInfoWhenAppBoot(runningPackageName);
				System.out.println("---------------------新建游戏记录-----"+runningPackageName);
			} else if (mLastPackageName != null && mLastPackageName.equals(runningPackageName)) {
				//游戏连续运行的情况
				if (mLastAppInfo != null) {
					mAppTimeStep++;
					
					long curTime = System.currentTimeMillis();
					long duration = mAppTimeStep*30;
					
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
						if (checkDatabaseIfCanUpdate(mLastAppInfo.getId())){
							/**
							 * 如果数据库中可以更新当前记录，则执行更新动作
							 * */
							PersistentSynUtils.update(mLastAppInfo);
						}
						else
						{
							/**
							 * 如果数据库中不能更新记录，则新建记录
							 * 
							 * */
							addInfoWhenAppBoot(runningPackageName);
						}
						
						addInfoAtOneDayFirst(runningPackageName,endTime + 1000);
						return;
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
						System.out.println("---------------------更新游戏记录-----"+runningPackageName);
						updateInfoLog(runningPackageName, mLastAppInfo.getGameName(), curTime - duration*1000, duration, curTime);
					}
					else
					{
						/**
						 * 如果数据库中不能更新记录，则新建记录
						 * 
						 * */
						addInfoWhenAppBoot(runningPackageName);
						System.out.println("---------------------更新游戏记录不成功，所以新建-----"+runningPackageName);
					}
				}
				
			}
		} else {
			mLastPackageName = runningPackageName;
			mAppTimeStep = 0L;
			System.out.println("---------------------不是atet平台游戏，不统计-----"+runningPackageName);
			return;
		}
		
	}

	/**
	 * 统计游戏的在线时长
	 * 
	 * */
	private void runAppStatistic() {
		// TODO Auto-generated method stub
		
		/**
		 * 本服务只统计非平台、非桌面类的游戏或者应用。
		 * 
		 * 1、不统计平台本身
		 * 2、不统计桌面内应用
		 * 
		 * */
		
		String runningPackageName = PackageInfoUtils.getRunningPackageName(this);
		String platFormName = getPackageName();
		
		if(mLastPackageName == null){
			/**
			 * 设备开机后，第一次运行该服务，此时lastPackageName为空
			 * */
			mLastPackageName = runningPackageName;
			
			//如果不是平台或者桌面类的应用在运行，则新增一条记录
			//如果是平台游戏就统计,其他应用不统计
			if(DeviceStatisticsUtils.isAtetGame(runningPackageName)){
				addInfoWhenAppBoot(runningPackageName);
				Log.e(TAG,"sisuation 1");
				return;
			}
			Log.e(TAG,"sisuation 2");
			
			
		}else{
			//服务不是开机第一次启动的情况,这个时候mLastPackageName有上一次运行的记录.
			//判断当前游戏是不是需要统计
			if(DeviceStatisticsUtils.isAtetGame(runningPackageName)){
				
				//如果上一次的游戏与这一次的游戏相同,则表明游戏连续运行,游戏连续运行就需要更新记录.
				if (mLastPackageName.equals(runningPackageName)) {
					
					if(mLastAppInfo != null)
					{
						mAppTimeStep++;
						
						long curTime = System.currentTimeMillis();
						long duration = mAppTimeStep*30;
						
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
							if (checkDatabaseIfCanUpdate(mLastAppInfo.getId())){
								/**
								 * 如果数据库中可以更新当前记录，则执行更新动作
								 * */
								PersistentSynUtils.update(mLastAppInfo);
							}
							else
							{
								/**
								 * 如果数据库中不能更新记录，则新建记录
								 * 
								 * */
								addInfoWhenAppBoot(runningPackageName);
							}
							
							addInfoAtOneDayFirst(runningPackageName,endTime + 1000);
							return;
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
							updateInfoLog(runningPackageName, mLastAppInfo.getGameName(), curTime - duration*1000, duration, curTime);
						}
						else
						{
							/**
							 * 如果数据库中不能更新记录，则新建记录
							 * 
							 * */
							addInfoWhenAppBoot(runningPackageName);
						}
						Log.e(TAG,"sisuation 4");
					}
				}else{
					
					//如果上一次是其他应用,而当前的游戏为有效的游戏则新建记录.
					addInfoWhenAppBoot(runningPackageName);
					mLastPackageName = runningPackageName;
					
				}
			}else {
				//如果当前游戏不需要统计,则将当前的程序名称保存下来作为下一次的比较.
				mLastPackageName=runningPackageName;
				mAppTimeStep = 0L;
			}
		}
		
		/*if(mLastPackageName == null){
			*//**
			 * 设备开机后，第一次运行该服务，此时lastPackageName为空
			 * *//*
			mLastPackageName = runningPackageName;
			
			//如果不是平台或者桌面类的应用在运行，则新增一条记录
			//如果是平台游戏就统计,其他应用不统计
			if(DeviceStatisticsUtils.isAtetGame(runningPackageName)){
				addInfoWhenAppBoot(runningPackageName);
				Log.e(TAG,"sisuation 1");
				return;
			}
			Log.e(TAG,"sisuation 2");
			
			
		}
		else if (runningPackageName.equals(platFormName)||isHomeApp(runningPackageName))
		{
			*//**
			 * 如果当前运行的应用是桌面应用或者是平台本身，则将mAppTimeStep置０
			 * *//*
			mLastPackageName = runningPackageName;
			mAppTimeStep = 0L;
			
			Log.e(TAG,"sisuation 3");
			return;
		}
		else if (mLastPackageName.equals(runningPackageName) && !mLastPackageName.equals(platFormName) && !isHomeApp(mLastPackageName))
		{
			*//**
			 * 游戏连续运行的情况
			 * 
			 * 游戏连续运行就需要更新数据库中的记录，而不是新增记录。
			 * 
			 * 更新的信息主要是开始时间、运行时长、结束时间。
			 * 
			 * 开始时间要用结束时间-运行时长来动态更新
			 * *//*
			
			if(mLastAppInfo != null)
			{
				mAppTimeStep++;
				
				long curTime = System.currentTimeMillis();
				long duration = mAppTimeStep*30;
				
				*//**
				 * 如果不是开始时间与结束时间不是同一天，则分割时间
				 * *//*
				if(DeviceStatisticsUtils.ifTwoTimeStampBetweenOneDay(mLastAppInfo.getStartTime(), curTime))
				{
					long endTime = DeviceStatisticsUtils.getTimeToday(DeviceStatisticsUtils.timeToDay(curTime))- 1000;
					long durantion1 = (curTime - endTime)/1000;
					mLastAppInfo.setEndTime(endTime);
					mLastAppInfo.setLongTime(duration - durantion1);
					mLastAppInfo.setStartTime(curTime - duration*1000);
					if (checkDatabaseIfCanUpdate(mLastAppInfo.getId())){
						*//**
						 * 如果数据库中可以更新当前记录，则执行更新动作
						 * *//*
						PersistentSynUtils.update(mLastAppInfo);
					}
					else
					{
						*//**
						 * 如果数据库中不能更新记录，则新建记录
						 * 
						 * *//*
						addInfoWhenAppBoot(runningPackageName);
					}
					
					addInfoAtOneDayFirst(runningPackageName,endTime + 1000);
					return;
				}
				
				mLastAppInfo.setEndTime(curTime);
				mLastAppInfo.setLongTime(duration);
				mLastAppInfo.setStartTime(curTime - duration*1000);
				
				if (checkDatabaseIfCanUpdate(mLastAppInfo.getId()))
				{
					*//**
					 * 如果数据库中可以更新当前记录，则执行更新动作
					 * *//*
					PersistentSynUtils.update(mLastAppInfo);
					updateInfoLog(runningPackageName, mLastAppInfo.getGameName(), curTime - duration*1000, duration, curTime);
				}
				else
				{
					*//**
					 * 如果数据库中不能更新记录，则新建记录
					 * 
					 * *//*
					addInfoWhenAppBoot(runningPackageName);
				}
				Log.e(TAG,"sisuation 4");
			}
		}
		else if(!mLastPackageName.equals(runningPackageName))
		{
			*//**
			 * 上一次检测到的应用不是当前运行的应用。
			 * 
			 * 1、上次是其他应用，这次是平台。
			 * 2、上次是平台，这次是其他应用　。
			 * 
			 * *//*
			
			
				addInfoWhenAppBoot(runningPackageName);
				mLastPackageName = runningPackageName;
				Log.e(TAG,"sisuation 5");
				return;
			
		}*/
	}

	private void addInfoWhenAppBoot(String packageName) {
		// TODO Auto-generated method stub
		long curruntTime = System.currentTimeMillis();
		GameOnlineInfo info = new GameOnlineInfo();
		checkDataBaseIsOverwrite();
		mAppTimeStep = 0L;
		info.setUserId(DeviceStatisticsUtils.getUserId(this));
		info.setGameId(DeviceStatisticsUtils.getGameId(packageName));
		info.setGameType(DeviceStatisticsUtils.getGameType(this, packageName));
		info.setGameName(DeviceStatisticsUtils.getGameName(this,packageName));
		info.setVersionCode(DeviceStatisticsUtils.getVersionName(this, packageName));
		info.setPackageName(packageName);
		info.setCopyRight(DeviceStatisticsUtils.getCopyRight(packageName));
		info.setCpId(DeviceStatisticsUtils.getCpId(packageName));
		info.setStartTime(curruntTime);
		info.setLongTime(mAppTimeStep);
		info.setEndTime(curruntTime);
		
		long id = PersistentSynUtils.addModel(info);
		
		if (id != -1)
		{
			mLastAppInfo = info;
			mLastAppInfo.setId(id+"");
			mLastPackageName = packageName;
			addNewInfoLog(packageName, info.getGameName(), curruntTime, curruntTime);
		}
	}
	
	
	private void addInfoAtOneDayFirst(String packageName,long startTime) {
		// TODO Auto-generated method stub
		long curruntTime = System.currentTimeMillis();
		GameOnlineInfo info = new GameOnlineInfo();
		checkDataBaseIsOverwrite();
		mAppTimeStep = 0L;
		info.setUserId(DeviceStatisticsUtils.getUserId(this));
		info.setGameId(DeviceStatisticsUtils.getGameId(packageName));
		info.setGameType(DeviceStatisticsUtils.getGameType(this, packageName));
		info.setGameName(DeviceStatisticsUtils.getGameName(this,packageName));
		info.setVersionCode(DeviceStatisticsUtils.getVersionName(this, packageName));
		info.setPackageName(packageName);
		info.setCopyRight(DeviceStatisticsUtils.getCopyRight(packageName));
		info.setCpId(DeviceStatisticsUtils.getCpId(packageName));
		info.setStartTime(startTime);
		info.setLongTime((curruntTime-startTime)/1000);
		info.setEndTime(curruntTime);
		
		long id = PersistentSynUtils.addModel(info);
		
		if (id != -1)
		{
			mLastAppInfo = info;
			mLastAppInfo.setId(id+"");
			mLastPackageName = packageName;
			
			addNewInfoLog(packageName, info.getGameName(), startTime, curruntTime);
		}
	}
	
	/**
	 * 判断指定的程序是不是桌面类APP
	 * */
	private boolean isHomeApp(String packageName)
	{
		
		if(mHomeList == null || mHomeList.size() == 0)
		{
			mHomeList = getHomes();
		}
		
		if(mHomeList != null && mHomeList.size() > 0)
		{
			return mHomeList.contains(packageName);
		}
		
		return false;
	}
	
	/** 
	 * 获得属于桌面的应用的应用包名称 
	 * @return 返回包含所有包名的字符串列表 
	 */  
	private List<String> getHomes() {  
	    List<String> names = new ArrayList<String>();  
	    PackageManager packageManager = this.getPackageManager();  
	    //属性  
	    Intent intent = new Intent(Intent.ACTION_MAIN);  
	    intent.addCategory(Intent.CATEGORY_HOME);  
	    List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,  
	            PackageManager.MATCH_DEFAULT_ONLY);  
	    for(ResolveInfo ri : resolveInfo){  
	        names.add(ri.activityInfo.packageName);  
	        System.out.println(ri.activityInfo.packageName);  
	    }  
	    return names;  
	}  
	
	private void checkDataBaseIsOverwrite()
	{
		List<GameOnlineInfo> recordList = PersistentSynUtils.getModelList(GameOnlineInfo.class," id > 0");
		
		if (recordList != null && recordList.size() > 100)
		{
			StatisticsRecordTestUtils.databaseOverwriteLog(this, "游戏在线统计服务");
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
		List<GameOnlineInfo> recordList = PersistentSynUtils.getModelList(GameOnlineInfo.class, " id = "+ id);
		
		return recordList.size() > 0 ? true:false;
	}
	
	private void addNewInfoLog(String packageName,String gameName,long startTime,long endTime)
	{
		StringBuilder sb = new StringBuilder();
		if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
		{
			String name = "游戏运行时长统计服务";
			sb.append("\r\n 新增游戏运行记录  ");
			sb.append(" 游戏包名："+packageName);
			sb.append(" 应用名称："+gameName);
			sb.append(" 开始时间："+DeviceStatisticsUtils.getDateToString(startTime));
			sb.append(" 结束时间："+DeviceStatisticsUtils.getDateToString(endTime));
			sb.append("\r\n");
			String msg = sb.toString();
			sb = null;
			StatisticsRecordTestUtils.newLog(this,name, msg);
		}
	}
	
	private void updateInfoLog(String packageName,String gameName,long startTime,long longTime,long endTime)
	{
		StringBuilder sb = new StringBuilder();
		if (StatisticsRecordTestUtils.ACCOUNT_DEBUG == true)
		{
			String name = "游戏运行时长统计服务";
			sb.append("\r\n 游戏连续运行  ");
			sb.append(" 游戏包名："+packageName);
			sb.append(" 应用名称："+gameName);
			sb.append(" 开始时间："+DeviceStatisticsUtils.getDateToString(startTime));
			sb.append(" 运行时长："+longTime + "秒");
			sb.append(" 结束时间："+DeviceStatisticsUtils.getDateToString(endTime));
			sb.append("\r\n");
			String msg = sb.toString();
			sb = null;
			StatisticsRecordTestUtils.newLog(this,name, msg);
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
