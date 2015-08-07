package com.sxhl.market.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.service.HandleService;

/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 接收系统安装和卸载的广播
 */
public class InstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		PackageManager manager = context.getPackageManager();

		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			// Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG)
			// .show();
			BaseApplication.isUnistalledMap.put(packageName, false);
		}
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			// Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG)
			// .show();
			Intent intenta = new Intent(context, HandleService.class);
			intenta.putExtra("packageName", packageName);
			context.startService(intenta);
			BaseApplication.isUnistalledMap.put(packageName, true);
		}
	}

}
