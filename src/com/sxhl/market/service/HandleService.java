package com.sxhl.market.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGameInfo;

/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 处理安装或者升级游戏
 */
public class HandleService extends IntentService {

	public HandleService() {
		super("noname");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String packageName = intent.getStringExtra("packageName");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (BaseApplication.isUnistalledMap.get(packageName)) {
			MyGameInfo myGameInfo = null;
			Group<MyGameInfo> group = PersistentSynUtils.getModelList(
					MyGameInfo.class, "packageName='" + packageName + "'");
			if (group != null && group.size() > 0) {
				myGameInfo = group.get(0);
				if (myGameInfo.getState() == Constant.GAME_STATE_INSTALLED
						|| myGameInfo.getState() == Constant.GAME_STATE_INSTALLED_USER) {
					PersistentSynUtils.delete(myGameInfo);
				}
			}
		} else {
		}
	}
}
