package com.sxhl.market.receiver;

import java.io.File;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.MainTabActivity;
import com.sxhl.market.control.manage.activity.MyGameActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.model.net.http.download.DownloadTask;
import com.sxhl.market.utils.AppUtil;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.DeviceTool;
import com.sxhl.market.utils.StringTool;

public class BReceiver extends BroadcastReceiver {
	public static final String TAG = "RECEIVER";
	public static String uninstallPackageName="";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			DebugTool.debug(TAG, "receiver action:" + action);
			
//			if(action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REPLACED)){
			if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
				DebugTool.debug(TAG, "receiver:app installed");
        		final String packageName = intent.getData().getSchemeSpecificPart();
				appHandle(context, packageName, DownloadTask.OP_INSTALL);
			} else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
				DebugTool.debug(TAG, "receiver:app removed");
        		final String packageName = intent.getData().getSchemeSpecificPart();
				appHandle(context, packageName, DownloadTask.OP_UNINSTALL);
			} else if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
			    DebugTool.debug(TAG, "receiver: media mounted action");
			    initSdMounted();
			} else if(action.equals("com.sxhl.market.launcher_main")){
			    Intent in=new Intent();
			    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 
			       in.setClass(context, MainTabActivity.class);
		
			   context.startActivity(in); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void appHandle(final Context context,final String packageName,final int opType){
	    if(StringTool.isEmpty(packageName)){
	       return; 
	    }
	    DebugTool.debug(TAG, "receiver:packageName -- "+packageName);
	    
	    new Thread(new Runnable() {
	        @Override
	        public void run() {
	            // TODO Auto-generated method stub
	            try {
	                Group<MyGameInfo> myGameInfos=PersistentSynUtils.getModelList(MyGameInfo.class, " packageName='"+packageName+"'");
	                if(myGameInfos!=null && myGameInfos.size()>0){
	                    MyGameInfo myGameInfo=myGameInfos.get(0);

	                    if(opType==DownloadTask.OP_INSTALL){
	                        DebugTool.info(TAG, "receiver:OP_INSTALL");
	                        myGameInfo.setState(Constant.GAME_STATE_INSTALLED);
	                        List<ResolveInfo> resolveInfos=AppUtil.queryAppInfo(context, packageName); 
	                        if(resolveInfos!=null && resolveInfos.size()>0){
	                            myGameInfo.setLaunchAct(resolveInfos.get(0).activityInfo.name);
	                            DebugTool.debug(TAG,"launch act name:"+myGameInfo.getLaunchAct());
	                        }
	                        PersistentSynUtils.update(myGameInfo);

	                        Intent in=new Intent(DownloadTask.ACTION_ON_APP_INSTALLED);
	                        Bundle bundle=new Bundle();
	                        bundle.putInt("opType", DownloadTask.OP_INSTALL);
//	                        bundle.putString("packageName", packageName);
//	                        bundle.putString("gameId", myGameInfo.getGameId());
	                        bundle.putSerializable("myGameInfo", myGameInfo);
	                        in.putExtra("data", bundle);
	                        context.sendBroadcast(in);

	                    } else if(opType==DownloadTask.OP_UNINSTALL){
	                        if(!uninstallPackageName.equals(packageName)){
	                            return;
	                        }
	                        DebugTool.info(TAG, "receiver:OP_UNINSTALL");
	                        PersistentSynUtils.delete(myGameInfo);
	                        Intent in=new Intent(DownloadTask.ACTION_ON_APP_INSTALLED);
	                        Bundle bundle=new Bundle();
	                        bundle.putInt("opType", DownloadTask.OP_UNINSTALL);
//	                        bundle.putString("packageName", packageName);
//	                        bundle.putString("gameId", myGameInfo.getGameId());
	                        bundle.putSerializable("myGameInfo", myGameInfo);
	                        in.putExtra("data", bundle);
	                        context.sendBroadcast(in);
	                    } else {
	                        return;
	                    }
	                    
	                    if(myGameInfo.getLocalFilename().toLowerCase().endsWith(MyGameActivity.ZIP_EXT)){
	                        //删除解压出来的apk文件
	                        File file=new File(MyGameActivity.getLocalUnApkPath(myGameInfo));
	                        if(file!=null && file.exists()){
	                            file.delete();
	                        }
	                    }

	                    boolean isDelZip=true;
	                    if(opType==DownloadTask.OP_INSTALL){
	                        SharedPreferences preferences=context.getSharedPreferences("marketApp", Context.MODE_PRIVATE);
	                        isDelZip=preferences.getBoolean("isDelZip",false);
	                    }
	                    if(isDelZip){
	                        //delete the zip file
	                        File apkFile=new File(myGameInfo.getLocalDir(),myGameInfo.getLocalFilename());
	                        if(apkFile!=null && apkFile.exists()){
	                            apkFile.delete();
	                        }
	                    }
	                }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
	        }
	    }).start();
	}
	
    private void initSdMounted() {
        String sdRoot = DeviceTool.getSDPath();
        if(StringTool.isEmpty(sdRoot)){
            return;
        }
        if(Constant.SDCARD_ROOT.equals(sdRoot)){
            return;
        }
        Constant.SDCARD_ROOT=sdRoot;
        //游戏下载保存目录
        Constant.GAME_DOWNLOAD_LOCAL_DIR = Constant.SDCARD_ROOT+"/sxhl/market/";
        //游戏数据包存放目录
		Constant.GAME_ZIP_DATA_LOCAL_DIR = Constant.SDCARD_ROOT + "/";
	}
}