package com.sxhl.market.model.net.http.download;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.util.Log;

import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.DownloadGameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.net.http.Response;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.statistics.model.CollectGameInfo;
import com.sxhl.statistics.utils.GameCollectHelper;

public class Report {
	private static final String TAG = "REPORT";
	private static final int REPORT_MAX_RETRY_TIME = 1;
	// 线程池中的活动线程数量
	private static final int FIX_THREADS = 2;

	private final Object queueLock = new Object();
	private ThreadPoolExecutor executor = null;
	private volatile Map<String, Object> queue = new HashMap<String, Object>();
	private static Report INSTANSE = null;

	private Report() {
		if (executor == null) {
			executor = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(FIX_THREADS);
		}
	}

	public synchronized static Report getInstance() {
		if (INSTANSE == null) {
			INSTANSE = new Report();
		}
		return INSTANSE;
	}

	private boolean putInQueue(String gameId) {
		synchronized (queueLock) {
			if (queue.containsKey(gameId)) {
				return false;
			}
			queue.put(gameId, null);
			return true;
		}
	}

	private void removeFromQueue(String gameId) {
		synchronized (queueLock) {
			queue.remove(gameId);
		}
	}

	public void reportToServer(String gameId) {
		if (gameId == null || gameId.length() < 1) {
			return;
		}
		if (!putInQueue(gameId)) {
			return;
		}
		executor.execute(new ReportRunnable(gameId));
	}

	private class ReportRunnable implements Runnable {
		private String gameId;

		public ReportRunnable(String gameId) {
			super();
			this.gameId = gameId;
		}

		public void run() {
			try {
				addToStatistics(gameId);
				confirmSuccessRequest(gameId);
				removeFromQueue(gameId);
				if (queue.size() <= 0) {
					DebugTool.debug(TAG, "===report end");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void addToStatistics(String gameId) {
		// TODO Auto-generated method stub
		Group<DownloadGameInfo> mGameInfos = PersistentSynUtils.getModelList(
				DownloadGameInfo.class, " gameId = \"" + gameId + "\"");
		DownloadGameInfo mGameInfo = null;
		if (mGameInfos != null && mGameInfos.size() > 0) {
			mGameInfo = mGameInfos.get(0);
		} else {
			return;
		}
		CollectGameInfo collectGame = new CollectGameInfo();
		if (BaseApplication.m_loginUser != null
				&& BaseApplication.m_loginUser.getUserId() != null) {
			collectGame.setUserId(Integer.parseInt(BaseApplication.m_loginUser
					.getUserId()));

		} else {
			collectGame.setUserId(0);
		}
		collectGame.setGameId(mGameInfo.getGameId());
		collectGame.setGameName(mGameInfo.getGameName());
		collectGame.setPackageName(mGameInfo.getPackageName());

		collectGame.setCopyRight(1);
		collectGame.setCpId(mGameInfo.getCpId());
		collectGame.setAdClick(0);
		collectGame.setClickCount(0);
		collectGame.setDownCount(1);
		collectGame.setRecordTime(System.currentTimeMillis());
		int mTypeId;
		try {
			mTypeId = Integer.parseInt(mGameInfo.getTypeId());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			mTypeId = 3;
		}
		if (mTypeId == 1) {
			collectGame.setGameType("史诗推荐");
		} else if (mTypeId == 2) {

			collectGame.setGameType("精品游戏");
		} else {
			collectGame.setGameType("专题游戏");

		}
		GameCollectHelper.addGameCollectInfo(collectGame, 3);
		PersistentSynUtils.execDeleteData(DownloadGameInfo.class,
				" where gameId = \"" + mGameInfo.getGameId() + "\"");
	}

	private boolean confirmSuccessRequest(final String gameId) {
		boolean result = false;
		for (int i = 0; i < REPORT_MAX_RETRY_TIME && !result; i++) {
			result = confirmSuccess(gameId);
			if (result) {
				DebugTool.debug(TAG, "gameId=" + gameId + " report success");
				return true;
			}
			DebugTool.error(TAG, "gameId=" + gameId
					+ " error to confirmSuccess,current retry time:" + (i + 1),
					null);
		}
		return result;
	}

	private boolean confirmSuccess(final String gameId) {
		try {
			// String postData="{gameId:\""+gameId+"\"}";
			// DebugTool.info(TAG, "postData:"+postData);
			HttpReqParams pam = new HttpReqParams();
			pam.setGameId(gameId);
			pam.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			if (BaseApplication.m_loginUser != null
					&& BaseApplication.m_loginUser.getUserId() != null) {
				pam.setUserId(BaseApplication.m_loginUser.getUserId());
			} else if (BaseApplication.m_loginUser == null
					|| BaseApplication.m_loginUser.getUserId() == null) {
				pam.setUserId("0");
			}
			// Response response = HttpApi.getHttpPost1(
			// UrlConstant.HTTP_GAME_DOWNLOAD_COUNT, pam.toJsonParam());
			Response response = HttpApi.getHttpPost1(
					UrlConstant.HTTP_GAME_DOWNLOAD_COUNT1,
					UrlConstant.HTTP_GAME_DOWNLOAD_COUNT2,
					UrlConstant.HTTP_GAME_DOWNLOAD_COUNT3, pam.toJsonParam());
			if (response != null) {
				String responseStr = response.asString();
				Log.e("response", responseStr);
				DebugTool.info(TAG, "response string:" + responseStr);
				if (responseStr != null && responseStr.indexOf("code\":0") > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void recycle() {
		try {
			INSTANSE = null;
			if (executor != null) {
				executor.shutdown();
				executor = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
