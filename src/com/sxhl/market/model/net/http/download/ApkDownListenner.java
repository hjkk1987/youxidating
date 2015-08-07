package com.sxhl.market.model.net.http.download;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;

import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.MainTabActivity;
import com.sxhl.market.control.manage.activity.MyGameActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.utils.AppUtil;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.StringTool;

/**
 * 实现的主要功能：下载时的通知栏显示
 * 
 * @author: LiuQin
 * @date: 2013-5-29 下午5:20:19 
 * 修改记录：
 *     修改者:    
 *       修改时间：
 *       修改内容：
 */
public class ApkDownListenner implements DownloadListenner {
	private static final String TAG="ApkDownListennerImpl";
	private Context mContext;
	private Handler mWorketHandler;
	
	private NotificationManager mNfm;
	private Notification mDownloadingNF;
	private RemoteViews mContentView;
	
	public ApkDownListenner(Context context) {
		super();
		this.mContext = context;
		
        if(mWorketHandler!=null){
            mWorketHandler.getLooper().quit();
            mWorketHandler=null;
        }
		HandlerThread handlerThread=new HandlerThread("myHandlerThread");
		handlerThread.start();
		mWorketHandler=new MyHandler(handlerThread.getLooper());
		initDownloadingNF();
	}
	
	class MyHandler extends Handler{
		private volatile long lastUpdateTime=0;
		private volatile String titleText="";
		private volatile int downloadingCount=0;
		private volatile int currentDownPercent=0;
		private ConcurrentHashMap<String, Integer> progressMap=new ConcurrentHashMap<String, Integer>();
	
		public MyHandler(Looper looper) {
			super(looper);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			FileDownInfo fileDownInfo=(FileDownInfo)msg.obj;
			
			try{
				switch (msg.what){
				case DownloadTask.STATE_ON_DOWNLOAD_PROGRESS:
					DebugTool.info(TAG,"progress:"+fileDownInfo.getFileId());

					long currentTime=System.currentTimeMillis();
					if(currentTime-lastUpdateTime<1000){
						break;
					}
					lastUpdateTime=currentTime;

					int downLen=fileDownInfo.getDownLen();
					int fileSize=fileDownInfo.getFileSize();
					int downPercent=(int)((long)downLen*100/(long)fileSize);

					Integer lastPercent=progressMap.get(fileDownInfo.getFileId());
					if(lastPercent==null || downPercent<=lastPercent){
						break;
					}
					progressMap.put(fileDownInfo.getFileId(), downPercent);

					currentDownPercent=currentDownPercent-lastPercent+downPercent;
					int currentPercent=currentDownPercent/downloadingCount;

					updateDownloadingNfProgress(titleText,currentPercent);
					break;

				case DownloadTask.STATE_ON_DOWNLOAD_WAIT:
					downloadingCount++;
					titleText+=" "+fileDownInfo.getExtraData();
					progressMap.put(fileDownInfo.getFileId(), 0);

					updateDownloadingNfProgress(titleText,getPercent()/downloadingCount);
					break;

				case DownloadTask.STATE_ON_DOWNLOAD_FINISH:
					DebugTool.info(TAG,"finish,error,stop");
					Integer len=progressMap.remove(fileDownInfo.getFileId());
					if(len==null){
						break;
					}
					currentDownPercent-=len;
					titleText=titleText.replace(" "+fileDownInfo.getExtraData(), "");
					downloadingCount--;

					if(downloadingCount>0){
						updateDownloadingNfProgress(titleText,getPercent()/downloadingCount);
					}
					break;

				case DownloadTask.STATE_ON_DOWNLOAD_COMPLETED:
					cancelNF(mContext, R.drawable.nf_down_downloading);
					lastUpdateTime=0;
					titleText="";
					downloadingCount=0;
					currentDownPercent=0;
					progressMap.clear();
					break;
				} 
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		private int getPercent(){
			int percent=0;
			Iterator<Map.Entry<String, Integer>> it = progressMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Integer> entry=it.next();
				Integer value=entry.getValue();
				if(value!=null){
					percent+=value;
				}
			}
			return percent;
		}
	}
	
	@Override
	public void onDownloadWait(FileDownInfo fileDownInfo) {
		// TODO Auto-generated method stub
		
		mWorketHandler.removeMessages(DownloadTask.STATE_ON_DOWNLOAD_PROGRESS);
		Message.obtain(mWorketHandler, DownloadTask.STATE_ON_DOWNLOAD_WAIT, fileDownInfo).sendToTarget();
	}

	@Override
	public void onDownloadStart(FileDownInfo fileDownInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadSized(FileDownInfo fileDownInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadProgress(FileDownInfo fileDownInfo) {
		// TODO Auto-generated method stub
		Message.obtain(mWorketHandler, DownloadTask.STATE_ON_DOWNLOAD_PROGRESS, fileDownInfo).sendToTarget();
	}

	@Override
	public void onDownloadStop(FileDownInfo fileDownInfo) {
		// TODO Auto-generated method stub
		mWorketHandler.removeMessages(DownloadTask.STATE_ON_DOWNLOAD_PROGRESS);
		Message.obtain(mWorketHandler, DownloadTask.STATE_ON_DOWNLOAD_FINISH, fileDownInfo).sendToTarget();
	}

	@Override
	public void onDownloadFinish(FileDownInfo fileDownInfo) {
		// TODO Auto-generated method stub
		String gameId=fileDownInfo.getFileId();
		File appFile=new File(fileDownInfo.getLocalDir(),fileDownInfo.getLocalFilename());
	    DebugTool.info(TAG, "onDownloadFinish,id:"+gameId);
//	    DebugTool.info(TAG, "object:"+fileDownInfo.getObject());
		DebugTool.debug(TAG,"gameId:"+gameId);
		DebugTool.debug(TAG,"appPath:"+appFile.getAbsolutePath());
		apkDownedFinishHandle(mContext, appFile, gameId,(MyGameInfo)(fileDownInfo.getObject()));
		
		Message.obtain(mWorketHandler, DownloadTask.STATE_ON_DOWNLOAD_FINISH, fileDownInfo).sendToTarget();
	}

	@Override
	public void onDownloadCompleted() {
		// TODO Auto-generated method stub
		mWorketHandler.removeMessages(DownloadTask.STATE_ON_DOWNLOAD_PROGRESS);
		Message.obtain(mWorketHandler, DownloadTask.STATE_ON_DOWNLOAD_COMPLETED, null).sendToTarget();
//		ActivityIntentTool.cancelNF(mContext, R.drawable.stat_downloading_0);
	}

	@Override
	public void onDownloadError(FileDownInfo fileDownInfo, String errMsg) {
		// TODO Auto-generated method stub
		
		MyGameInfo info=(MyGameInfo)fileDownInfo.getObject();
		if(info!=null){
			info.setState(Constant.GAME_STATE_DOWNLOAD_ERROR);
			PersistentSynUtils.update(info);
			createDownladErrorNF(mContext,info);
		}
		
		Message.obtain(mWorketHandler, DownloadTask.STATE_ON_DOWNLOAD_FINISH, fileDownInfo).sendToTarget();
	}

	private void apkDownedFinishHandle(Context context,File appFile,String gameId,Object object){
	    MyGameInfo info= null;
	    if(object!=null){
	        info=(MyGameInfo)object;
	    }
		String appPath=appFile.getAbsolutePath();
		if(!StringTool.isEmpty(appPath) && !StringTool.isEmpty(gameId)){
			boolean isSuccess=false;
			if(!appPath.toLowerCase().endsWith(MyGameActivity.ZIP_EXT)){
			    //			MyGameInfo info=getMyGameInfoFromDB(mContext,gameId);
			    if(info!=null){
			        ApplicationInfo appInfo=AppUtil.getApkInfoByPath(context, appPath);
			        if(appInfo!=null){
			            info.setPackageName(appInfo.packageName);
			            info.setName(context.getPackageManager().getApplicationLabel(appInfo)+"");
			            info.setState(Constant.GAME_STATE_NOT_INSTALLED);
			            isSuccess=true;
			        } else {
			            info.setState(Constant.GAME_STATE_DOWNLOAD_ERROR);
			        }
			    }
			} else {
			    if(info!=null){
			        info.setState(Constant.GAME_STATE_NOT_INSTALLED);
			    }
			    isSuccess=true;
			}

			PersistentSynUtils.update(info);
			if(isSuccess){
				createDownladSuccessNF(context, info);
				createDownladErrorNF(context, null);
			} else {
				createDownladErrorNF(context,info);
				if(appFile!=null && appFile.exists()){
					appFile.delete();
				}
			}
		}
	}
	
	/**
	 * 方法概述： 创建成功下载通知
	 * @description：
	 * @param context
	 * @param info
	 * @throws 
	 * @author: LiuQin
	 * @date 2013-5-29 下午5:19:20
	 * 修改记录：
	 *     修改者：
	 *       修改时间：
	 *       修改内容：
	 */
	private void createDownladSuccessNF(Context context,MyGameInfo info){
	    if(MyGameActivity.sIsVisible ){
	        DebugTool.info(TAG, "crete success,return");
	        return;
	    }
	    
	    DebugTool.info(TAG, "crete success nf");
		int errCount=0;
		errCount=PersistentSynUtils.getModelListCount(MyGameInfo.class, " state="+Constant.GAME_STATE_NOT_INSTALLED);
		String tickerText=info.getName()+" "+context.getText(R.string.down_nf_tiker_success);
		String titleText=null;
		if(errCount<=1){
			titleText=tickerText;
		} else {
			titleText="\""+errCount+"\""+context.getString(R.string.down_nf_success_count);
		}

		createNF(context, R.drawable.nf_down_complete, tickerText, titleText, context.getString(R.string.down_nf_click_to_show_success), MainTabActivity.class, R.drawable.nf_down_complete, DownloadTask.STATE_ON_DOWNLOAD_FINISH);
	}
		
	/**
	 * 方法概述： 创建失败下载通知
	 * @description：
	 * @param context
	 * @param info
	 * @throws 
	 * @author: LiuQin
	 * @date 2013-5-29 下午5:19:36
	 * 修改记录：
	 *     修改者：
	 *       修改时间：
	 *       修改内容：
	 */
	private void createDownladErrorNF(Context context,MyGameInfo info){
	    if(MyGameActivity.sIsVisible){
	        DebugTool.info(TAG, "crete error,return");
	        return;
	    }
	    
	    DebugTool.info(TAG, "crete error nf");
		int errCount=0;
		errCount=PersistentSynUtils.getModelListCount(MyGameInfo.class, " state="+Constant.GAME_STATE_DOWNLOAD_ERROR);
		String tickerText=null;
		if(info!=null){
			tickerText=info.getName()+" "+context.getString(R.string.down_nf_tiker_fail);
		}
		String titleText=null;
		if(errCount<1){
			return;
		} else if(errCount==1){
			if(info!=null){
				titleText=tickerText;
			} else {
//				titleText=infos.get(0).getName()+" 下载失败";
				titleText="\""+errCount+"\""+context.getText(R.string.down_nf_fail_count);
			}
		} else if(errCount>1) {
			titleText="\""+errCount+"\""+context.getText(R.string.down_nf_fail_count);
		}

		createNF(context, R.drawable.nf_down_error, tickerText, titleText, context.getString(R.string.down_nf_click_to_show_error), MainTabActivity.class, R.drawable.nf_down_error, DownloadTask.STATE_ON_DOWNLOAD_ERROR);
	}
	
	public void initDownloadingNF(){
		mDownloadingNF=getDownloadingNF(mContext, R.drawable.nf_down_downloading, MainTabActivity.class,R.drawable.nf_down_downloading,DownloadTask.STATE_ON_DOWNLOAD_PROGRESS);
	}
	
	public Notification getDownloadingNF(Context context, int id, Class<?> startClass, int iconID,int nfType) {
		mNfm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification nf = new Notification(iconID, null, System.currentTimeMillis());
		Intent intent = new Intent();
		Bundle b=new Bundle();
		b.putInt("nf_type", nfType);
		intent.putExtra("data", b);
		intent.setClass(context, startClass);
		PendingIntent mPI = PendingIntent.getActivity(context, 0, intent, 0);
		nf.flags = Notification.FLAG_ONGOING_EVENT;
		nf.contentIntent=mPI;
		if(mContentView==null){
			mContentView = new RemoteViews(context.getPackageName(), R.layout.notification_down);
		}
		nf.contentView = mContentView;
		return nf;
	}
	
	/**
	 * 方法概述： 更新通知栏中的下载进度
	 * @description：
	 * @param titleText 通知栏标题
	 * @param progress 进度
	 * @throws 
	 * @author: LiuQin
	 * @date 2013-5-29 下午5:18:17
	 * 修改记录：
	 *     修改者：
	 *       修改时间：
	 *       修改内容：
	 */
	private void updateDownloadingNfProgress(String titleText,int progress){
	    if(progress>100 || progress<0){
	        progress=100;
	    }
	    
		mContentView.setTextViewText(R.id.nf_down_tv_title, titleText);
		mContentView.setTextViewText(R.id.nf_down_tv_percent, progress+"%");
		mContentView.setProgressBar(R.id.nf_down_progressbar, 100, progress, false);
		mNfm.notify(R.drawable.nf_down_downloading, mDownloadingNF);
	}
		
	/**
	 * 方法概述：创建通知栏
	 * @description：
	 * @param context
	 * @param id 通知栏id
	 * @param tickerText 通知时的标题
	 * @param titleText 通知栏标题
	 * @param contentText 通知栏内容
	 * @param startClass 点击时启动的Activity类
	 * @param iconID 通知栏图标资源id
	 * @param nfType 自定类型
	 * @throws 
	 * @author: LiuQin
	 * @date 2013-5-29 下午5:13:32
	 * 修改记录：
	 *     修改者：
	 *       修改时间：
	 *       修改内容：
	 */
	public static void createNF(Context context, int id,
			String tickerText, String titleText, String contentText,
			Class<?> startClass, int iconID,int nfType) {
		NotificationManager nfm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nfm.cancel(id);
		Notification nf = new Notification(iconID, tickerText,
				System.currentTimeMillis());
		Intent intent = new Intent();
		Bundle b=new Bundle();
		b.putInt("nf_type", nfType);
		intent.putExtra("data", b);
		intent.setClass(context, startClass);
		PendingIntent mPI = PendingIntent.getActivity(context, 0, intent, 0);
		nf.setLatestEventInfo(context, titleText, contentText, mPI);
		nf.flags = Notification.FLAG_AUTO_CANCEL;
		nfm.notify(id, nf);
	}

	/**
	 * 方法概述：取消通知栏
	 * @description：
	 * @param context
	 * @param id 要取消的通知栏id
	 * @throws 
	 * @author: LiuQin
	 * @date 2013-5-29 下午5:17:11
	 * 修改记录：
	 *     修改者：
	 *       修改时间：
	 *       修改内容：
	 */
	public static void cancelNF(Context context,int id){
		NotificationManager nfm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nfm.cancel(id);
	}

    public Handler getmWorketHandler() {
        return mWorketHandler;
    }
    
    public void recycle(){
        if(mWorketHandler!=null){
            mWorketHandler.getLooper().quit();
        }
        cancelNF(mContext, R.drawable.nf_down_downloading);
    }
}
