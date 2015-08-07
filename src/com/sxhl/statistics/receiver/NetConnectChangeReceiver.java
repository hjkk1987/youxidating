package com.sxhl.statistics.receiver;

import com.sxhl.statistics.services.GameCollectService;
import com.sxhl.statistics.services.UploadGameOnlineService;
import com.sxhl.statistics.services.UploadPlatFormTimeInfoService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.sax.StartElementListener;
import android.util.Log;
import android.widget.Toast;



/**
 * 监听网络的变化，有网络的时候及时上传信息。
 * 
 * @author zhaominglai
 * @date 2014/9/23 17:04
 * 
 * */
public class NetConnectChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 String action = intent.getAction();
         if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            // Log.d("mark", "网络状态已经改变");
             ConnectivityManager connectivityManager = (ConnectivityManager)      
                                      context.getSystemService(Context.CONNECTIVITY_SERVICE);
             
             NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();  
             if(netInfo != null && netInfo.isAvailable()) {
                 String name = netInfo.getTypeName();
                 
                 Intent gameCollectIntent = new Intent(context,GameCollectService.class);
                 context.startService(gameCollectIntent);
                 Intent platformUpload = new Intent(context,UploadPlatFormTimeInfoService.class);
                 context.startService(platformUpload);
                 Intent gameonlineUpload = new Intent(context,UploadGameOnlineService.class);
                 context.startService(gameonlineUpload);
                 Toast.makeText(context, "当前网络名称：" + name, 1).show();
                 //Log.d("mark", "当前网络名称：" + name);
             } else {
                 //Log.d("mark", "没有可用网络");
             }
         }
     }
}
