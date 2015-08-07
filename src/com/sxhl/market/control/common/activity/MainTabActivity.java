package com.sxhl.market.control.common.activity;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.QRtools.activity.GuidePaymentActivity;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.game.activity.GameZoneActivity;
import com.sxhl.market.control.manage.activity.MyGameActivity;
import com.sxhl.market.control.user.activity.ChangeLoginActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.GiftGameInfo;
import com.sxhl.market.model.entity.GiftInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.model.entity.MyGiftInfo;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.model.exception.HttpResponseException;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.net.http.download.ApkDownListenner;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskManager;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.AppUtil;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.NetUtil;
import com.sxhl.market.utils.version.VersionManager;
import com.sxhl.market.view.MyRadioButton;
import com.sxhl.statistics.utils.StatisticsHelper;

/**
 * @modify yindangchao
 * 
 */
@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity implements
		android.widget.CompoundButton.OnCheckedChangeListener {

	private TabHost mainTabHost;
	private Intent mGameZoneIntent;
	private Intent mSquareIntent;
	private Intent mGameManagerIntent;
	private Intent mUserCentreIntent;
	// private Intent mEIntent;
	private MyRadioButton manageRadioButton;
	private RadioButton gameRadioButton;
	private RadioButton qrCodeRadioButton;
	private MyRadioButton userCenterRadioButton;
	private final static String TAG = "MainTabActivity";

	public final static String SP_GIFT_MINTIME = "SP_GIFT_MINTIME";
	public final static String SP_VALUE_GIFT_MINTIME = "GIFT_MINTIME";

	private TaskManager taskManager = new TaskManager(this);
	private int needUpdateGamesNumFromNet;
	// private Bitmap bit;
	ApkDownListenner apkDownListenner;
	UserInfo user;

	// private boolean isQrLauncherCame;
	/** Called when the activity is first created. */
	private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mLoggedOutReceiver);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showGuider();
		// 这个是设置没有标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintabs);
		registerReceiver(mLoggedOutReceiver, new IntentFilter(
				Constant.INTENT_ACTION_LOGGED_OUT));
		// 设置显式意图
		this.mGameZoneIntent = new Intent(this, GameZoneActivity.class);
		this.mSquareIntent = new Intent(this, GuidePaymentActivity.class);
		this.mGameManagerIntent = new Intent(this, MyGameActivity.class);
		this.mUserCentreIntent = new Intent(this, ChangeLoginActivity.class);

		if (!NetUtil.isNetworkAvailable(this, true)) {
			Toast.makeText(this, getString(R.string.network_out_of_link), 2000)
					.show();
		}

		StatisticsHelper.getInstance(getApplicationContext()).startServices();

		// BaseApplication.deviceInfo.setDeviceId("demo01");
		// getDeviceInfoFromDatabase();
		// getDeviceID();
		// this.mUserCentreIntent = new
		// Intent(this,UserCenterUnLoginActivity.class);
		// 给radio_button们注册监听
		gameRadioButton = (RadioButton) findViewById(R.id.radio_but_game_zone);
		gameRadioButton.setOnCheckedChangeListener(this);
		qrCodeRadioButton = (RadioButton) findViewById(R.id.radio_but_square);
		qrCodeRadioButton.setOnCheckedChangeListener(this);
		// ((RadioButton)
		// findViewById(R.id.radio_but_manage)).setOnCheckedChangeListener(this);
		// ((RadioButton)
		// findViewById(R.id.radio_but_user_center)).setOnCheckedChangeListener(this);
		manageRadioButton = (MyRadioButton) findViewById(R.id.radio_but_manage);
		manageRadioButton.setOnCheckedChangeListener(this);

		userCenterRadioButton = (MyRadioButton) findViewById(R.id.radio_but_user_center);
		userCenterRadioButton.setOnCheckedChangeListener(this);
		setupIntent();
		// 掌上游 com.sxhl.market ==========21
		// VersionManager vm = new VersionManager(this, 21, 24 * 60 * 60 *
		// 1000);
		// vm.checkVersion(true);
		VersionManager vm = new VersionManager(this,
				BaseApplication.deviceInfo.getDeviceId());
		vm.checkVersion(Constant.Market_Package, Constant.APP_ID,
				12 * 3600 * 1000, true);
		checkMyGamesVersion();

		if (isTimeToCheckGameUpgrade()) {
			startAllAppNeedUpdateAsyn();
		}

		startAllGiftUpdateAsyn();
		startUserGiftUpdateAsyn();

		//
		// taskManager.cancelTask(Constant.TASKKEY_GAME_COUNT_STAR);
		// taskManager.startTask(getVerdionListener,
		// Constant.TASKKEY_GAME_COUNT_STAR);
		// BaseApplication.deviceInfo.setDeviceId("20140815141910224111");
		if (getIntent().getIntExtra("launcher", 0) == 5) {
			if (manageRadioButton != null) {
				manageRadioButton.setChecked(true);
			}
		}
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 获取服务器最新时间，用于删除数据库过期礼包
	 */
	private void checkServerTime() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			public void run() {
				JSONObject jsonSring;
				try {
					jsonSring = new JSONObject(HttpApi.getHttpPost1(
							UrlConstant.HTTP_GET_SEVER_TIME,
							UrlConstant.HTTP_GET_SEVER_TIME2,
							UrlConstant.HTTP_GET_SEVER_TIME3, null).asString());
					if (jsonSring.getInt("code") == 0) {

						Long currentTime = jsonSring.getLong("time");
						deleteGiftOverdueByServerTime(currentTime);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 检测是否有游戏在平台外升级
	 */
	private void checkMyGamesVersion() {
		// TODO Auto-generated method stub
		Group<MyGameInfo> myGameInfos = getInstalledGames();
		for (MyGameInfo myGameInfo : myGameInfos) {
			int localVersionCode = myGameInfo.getVersionCode();
			int realVersion = getRealVersionCode(myGameInfo.getPackageName());
			String newLauncherActivity = getLastestLauncherActivity(myGameInfo
					.getPackageName());
			if (localVersionCode < realVersion) {
				myGameInfo.setVersionCode(realVersion);
				if (newLauncherActivity != null
						&& !newLauncherActivity.equals(myGameInfo
								.getLaunchAct())) {
					myGameInfo.setLaunchAct(newLauncherActivity);
				}
				PersistentSynUtils.update(myGameInfo);
			}
		}
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 判断离上次检查游戏是否需要升级是否获取24小时
	 */
	private boolean isTimeToCheckGameUpgrade() {
		// TODO Auto-generated method stub
		long now = System.currentTimeMillis();
		long lastCheckTime = getSharedPreferences(
				Constant.LAST_CHECK_GAME_UPDATE_TIME, MODE_PRIVATE).getLong(
				"lastCheckTime", 0);
		// return ((now - lastCheckTime) >= 20 * 60 * 1000);
		return true;
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 获取所有已安装app的包名
	 */
	private String getAllAppsPkgName() {
		// TODO Auto-generated method stub
		String str_myGamePkgs = getPkgNamesOfNotInstalledFromMygames();

		StringBuffer sb = new StringBuffer("");
		PackageManager pm = this.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_INTENT_FILTERS);
		// 调用系统排序 ， 根据name排序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		for (ResolveInfo resInfo : resolveInfos) {
			String pkgName = resInfo.activityInfo.packageName;
			if (!pkgName.startsWith("com.android.")
					&& !str_myGamePkgs.contains(pkgName)) {
				sb.append(pkgName + ",");
			}
		}
		String pkgs = sb.toString();
		int len = pkgs.length();
		pkgs = pkgs.substring(0, len - 1);
		return pkgs;
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			/* 根据点击的按钮显示对应标签所封装的View */
			switch (buttonView.getId()) {
			case R.id.radio_but_game_zone:
				DebugTool.debug("点击了第一个TAB");
				this.mainTabHost.setCurrentTabByTag("A_TAB");
				break;
			case R.id.radio_but_square:
				this.mainTabHost.setCurrentTabByTag("B_TAB");
				break;
			case R.id.radio_but_manage:
				this.mainTabHost.setCurrentTabByTag("C_TAB");
				manageRadioButton.hidRemindPoint();
				break;
			case R.id.radio_but_user_center:
				this.mainTabHost.setCurrentTabByTag("D_TAB");
				userCenterRadioButton.hidRemindPoint();
				break;
			}
		}
	}

	private void setupIntent() {
		// 调用TabActivity的getTabHost()方法获取TabHost对象
		this.mainTabHost = getTabHost();
		TabHost localTabHost = this.mainTabHost;

		// addTab(TabHost.tabSpec tabSpec):添加选项卡,下面表示添加了5个选项卡
		// 第一个Tab是开始显示的View
		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_game_zone,
				this.mGameZoneIntent));
		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_qrcode,
				this.mSquareIntent));
		localTabHost.addTab(buildTabSpec("C_TAB", R.string.main_manage_game,
				this.mGameManagerIntent));
		localTabHost.addTab(buildTabSpec("D_TAB", R.string.main_user_center,
				this.mUserCentreIntent));
		if (getIntent().getIntExtra("launcher", 0) == 1) {
			qrCodeRadioButton.setChecked(true);
		}
	}

	/**
	 * @param tag
	 *            为所建选项卡的标签
	 * @param resLabel
	 *            选项卡标签
	 * @param resIcon
	 *            选项卡项的图标
	 * @param content
	 *            标签页view的内容
	 * @return 设置好数据的TabSpec
	 */
	private TabHost.TabSpec buildTabSpec(String tag, int resLabel,
			final Intent content) {
		return this.mainTabHost.newTabSpec(tag)// 添加一个标签为tag的选项卡，
				.setIndicator(getString(resLabel)) // 第一个参数表示选项卡的
				.setContent(content); // 选项卡标签页的内容
	}

	/**
	 * Linqin
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		if (getIntent().getIntExtra("launcher", 0) == 1) {
			if (qrCodeRadioButton != null) {
				qrCodeRadioButton.setChecked(true);
				// isQrLauncherCame = true;
			}

		} else if (getIntent().getIntExtra("launcher", 0) == 5) {
			if (manageRadioButton != null) {
				manageRadioButton.setChecked(true);
			}
		}
	}

	/**
	 * Liuqin
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("info", "MainTabActivity onResume");

		Intent intent = getIntent();
		if (intent != null) {
			Bundle b = intent.getBundleExtra("data");
			if (b != null) {
				int nfType = b.getInt("nf_type");
				if (nfType != 0) {
					mGameManagerIntent.putExtra("data", b);
					// 直接跳转到管理页面
					if (manageRadioButton.isChecked()) {
						userCenterRadioButton.setChecked(true);
					}
					manageRadioButton.setChecked(true);
					mGameManagerIntent.removeExtra("data");

					b.clear();
					intent.removeExtra("data");
					setIntent(intent);
				}
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return super.onKeyUp(keyCode, event);
	}

	private void startGamesNeedsUpdateAsyn() {
		taskManager.cancelTask(Constant.TASKKEY_NEEDUPDATEGAME);
		taskManager.startTask(mGamesNeedsUpdateAsynListener,
				Constant.TASKKEY_NEEDUPDATEGAME);
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 启动检查游戏更新线程
	 */
	private void startAllAppNeedUpdateAsyn() {
		// TODO Auto-generated method stub
		taskManager.cancelTask(Constant.TASKKEY_AllNEEDUPDATE);
		taskManager.startTask(allAppsUpdateAsynListener,
				Constant.TASKKEY_AllNEEDUPDATE);
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 启动获取最新礼包数据线程
	 */
	private void startAllGiftUpdateAsyn() {
		taskManager.cancelTask(Constant.TASKKEY_GIFT_ALLGIFT);
		taskManager.startTask(giftAsynTaskListener,
				Constant.TASKKEY_GIFT_ALLGIFT);
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 启动获取用户已领取礼包线程
	 */
	private void startUserGiftUpdateAsyn() {
		// TODO Auto-generated method stub
		taskManager.cancelTask(Constant.TASKKEY_GIFT_MYGIFT);
		taskManager.startTask(myGiftAsynTaskListener,
				Constant.TASKKEY_GIFT_MYGIFT);
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription  已安装游戏包名用“，”隔开
	 */
	private String getPkgNamesOfInstalledFromMygames() {
		Group<MyGameInfo> gameInfos = getInstalledGames();
		if (gameInfos == null || gameInfos.size() == 0) {
			return "";
		}
		String pkgsStr = "";
		int gameInfoSize = gameInfos.size();
		for (int i = 0; i < gameInfoSize; i++) {
			pkgsStr = pkgsStr + gameInfos.get(i).getPackageName() + ",";
		}
		int len = pkgsStr.length();
		pkgsStr = pkgsStr.substring(0, len - 1);
		return pkgsStr;
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription  非已安装游戏包名用“，”隔开
	 */
	private String getPkgNamesOfNotInstalledFromMygames() {
		Group<MyGameInfo> gameInfos = PersistentSynUtils.getModelList(
				MyGameInfo.class, " state != " + Constant.GAME_STATE_INSTALLED
						+ ";");
		if (gameInfos == null || gameInfos.size() == 0) {
			return "";
		}
		String pkgsStr = "";
		int gameInfoSize = gameInfos.size();
		for (int i = 0; i < gameInfoSize; i++) {
			pkgsStr = pkgsStr + gameInfos.get(i).getPackageName() + ",";
		}
		int len = pkgsStr.length();
		pkgsStr = pkgsStr.substring(0, len - 1);
		return pkgsStr;
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 获取本地已安装游戏信息
	 */
	private Group<MyGameInfo> getInstalledGames() {
		// TODO Auto-generated method stub
		Group<MyGameInfo> gameInfos = PersistentSynUtils.getModelList(
				MyGameInfo.class, " state = " + Constant.GAME_STATE_INSTALLED
						+ ";");
		return gameInfos;
	}

	/**
	 * @param gameId
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription  已安装游戏的版本号
	 */
	private int getVersionCodeFromLocal(String gameId) {
		// TODO Auto-generated method stub
		int versionCode = 0;
		Group<MyGameInfo> gameInfos = getInstalledGames();
		for (MyGameInfo myGameInfo : gameInfos) {
			if (myGameInfo.getGameId().equals(gameId)) {
				versionCode = myGameInfo.getVersionCode();
			}
		}
		return versionCode;
	}

	/**
	 * @param pkgName
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription  通過packagemanager获取版本号
	 */
	private int getRealVersionCode(String pkgName) {
		// TODO Auto-generated method stub
		int realVersionCode = 0;

		PackageInfo pkgInfo = null;
		PackageManager pm = getPackageManager();
		try {
			// 0代表是获取版本信息
			DebugTool.info(TAG, "pkgInfo:" + pkgName);
			pkgInfo = pm.getPackageInfo(pkgName, 0);

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DebugTool.info(TAG, "pkgInfo:NameNotFoundException");
		}
		if (pkgInfo != null) {
			realVersionCode = pkgInfo.versionCode;
		}
		return realVersionCode;
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 获取packagemanager里的游戏的启动activity
	 */
	private String getLastestLauncherActivity(String pkgName) {
		// TODO Auto-generated method stub
		String newLauncherActivity = null;
		List<ResolveInfo> resolveInfos = null;
		// 0代表是获取版本信息
		try {
			DebugTool.info(TAG, "pkgInfo:" + pkgName);
			resolveInfos = AppUtil.queryAppInfo(this, pkgName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (resolveInfos != null && resolveInfos.size() > 0) {
			newLauncherActivity = resolveInfos.get(0).activityInfo.name;
		}
		return newLauncherActivity;
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 将需要升级的游戏数据库状态改为GAME_STATE_NEED_UPDATE
	 */
	private void updateGameState(String gamePackagName) {
		String sql = "update myGameInfo set state="
				+ Constant.GAME_STATE_NEED_UPDATE + " where packageName=" + "'"
				+ gamePackagName + "';";
		DebugTool.info(TAG, "sql语句：" + sql);
		PersistentSynUtils.execSQL(sql);
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 通知栏提示需要升级的游戏数量
	 */
	private void notifyUpgrade(int upgradeGaneNumber) {
		// TODO Auto-generated method stub

		SharedPreferences lastCheckTimeSp = getSharedPreferences(
				Constant.LAST_CHECK_GAME_UPDATE_TIME, MODE_PRIVATE);
		lastCheckTimeSp.edit()
				.putLong("lastCheckTime", System.currentTimeMillis()).commit();
		if (upgradeGaneNumber <= 0) {
			return;
		}
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notice = new Notification();
		notice.icon = R.drawable.ic_launcher;
		notice.flags = Notification.FLAG_AUTO_CANCEL;
		Intent i = new Intent(this, MainTabActivity.class);
		i.putExtra("launcher", 5);
		PendingIntent pdIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, i, 0);
		notice.setLatestEventInfo(this, getString(R.string.game_updata_is_have)
				+ upgradeGaneNumber + getString(R.string.game_need_updates),
				getString(R.string.game_update_goto_detail), pdIntent);
		nm.notify(R.drawable.web_off, notice);
	}

	/**
	 * @param packageName
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription  通过包名查找数据库里的gameid
	 * 
	 */
	private String getLocalGameIdByPackageName(String packageName) {
		// TODO Auto-generated method stub
		String localGameId = "";
		Group<MyGameInfo> myGames = PersistentSynUtils.getModelList(
				MyGameInfo.class, " packageName='" + packageName + "'");
		if (myGames != null && myGames.size() != 0) {
			localGameId = myGames.get(0).getGameId();
		}
		DebugTool.info(TAG, "本地id ： " + localGameId);
		return localGameId;
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 获取本地需要升级的游戏的数量
	 */
	private int getLocalNeedUpdateStateGameNumber() {
		Group<MyGameInfo> gameInfos = PersistentSynUtils.getModelList(
				MyGameInfo.class, " state = " + Constant.GAME_STATE_NEED_UPDATE
						+ ";");
		if (gameInfos == null || gameInfos.size() == 0) {
			return 0;
		}
		return gameInfos.size();
	}

	private AsynTaskListener<Group<GameInfo>> mGamesNeedsUpdateAsynListener = new AsynTaskListener<Group<GameInfo>>() {

		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<Group<GameInfo>> result) {
			// TODO Auto-generated method stub
			needUpdateGamesNumFromNet = 0;
			needUpdateGamesNumFromNet += getLocalNeedUpdateStateGameNumber();
			// 当网络正常时
			if (result.getCode() == TaskResult.OK) {
				// 当能从后台得到的数据不为null时
				if (result.getData() != null) {
					// 当前需要升级的游戏列表
					Group<GameInfo> needUpdateGames = result.getData();
					int needUpdateGamesSize = needUpdateGames.size();

					if (0 == needUpdateGamesSize) {
						return;
					}

					SharedPreferences sp = getSharedPreferences(
							Constant.NEW_VERSION_INFO_SP, MODE_PRIVATE);
					Editor editor = sp.edit();
					// 比较每一个需要更新的游戏，看是否需要升级
					for (int i = 0; i < needUpdateGamesSize; i++) {
						GameInfo gameInfo = needUpdateGames.get(i);
						// 后台服务器获取的游戏信息的gameId
						String gameIdFromNet = gameInfo.getGameId();
						String gamePackageName = gameInfo.getPackageName();
						String localGameId = getLocalGameIdByPackageName(gamePackageName);
						if (gameIdFromNet.equals(localGameId)) {
							// 后台服务器获取的游戏信息的versionCode
							int versionCodeFromNet = gameInfo.getVersionCode();
							DebugTool.info(TAG, "versionCodeFromNet："
									+ versionCodeFromNet + ";gameid="
									+ gameIdFromNet);
							// 根据gameId来获取本地已安装游戏versionCode
							int versionCodeFromLocal = getVersionCodeFromLocal(gameIdFromNet);
							DebugTool.info(TAG, "versionCodeFromLocal："
									+ versionCodeFromLocal + ";gameid="
									+ gameIdFromNet);
							// 如果versioncode为0，则获取真实的versioncode
							if (0 == versionCodeFromLocal) {
								versionCodeFromLocal = getRealVersionCode(gameInfo
										.getPackageName());
							}
							// 比较versionCode来判断是否需要升级
							if (versionCodeFromNet > versionCodeFromLocal) {
								// 更新gameId游戏的state
								DebugTool.info(TAG,
										"此游戏需要更新：" + gameInfo.getPackageName());
								// 当检测到需要更新的时候，就修改状态state
								updateGameState(gamePackageName);
								// 同时将新版本信息保存到SP中
								editor.putInt(gameInfo.getGameId(),
										versionCodeFromNet);
								needUpdateGamesNumFromNet++;
							}
						}

					}
					editor.commit();

				}
			}
			notifyUpgrade(needUpdateGamesNumFromNet);
			if (needUpdateGamesNumFromNet > 0) {
				if (manageRadioButton != null && !manageRadioButton.isChecked()) {
					manageRadioButton.showRemindPoint();
				}

			}
		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {
			// TODO Auto-generated method stub
			HttpReqParams params = new HttpReqParams();
			String pkgNamesStr = getPkgNamesOfInstalledFromMygames();
			DebugTool.info(TAG, "已安装游戏的包名为：" + pkgNamesStr);
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			params.setPackageNames(pkgNamesStr);
			return HttpApi.getList(UrlConstant.HTTP_GET_GAMEINFO_INSTALLED1,
					UrlConstant.HTTP_GET_GAMEINFO_INSTALLED2,
					UrlConstant.HTTP_GET_GAMEINFO_INSTALLED3, GameInfo.class,
					params.toJsonParam());
		}
	};

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 显示开机图片
	 */
	@SuppressWarnings("unused")
	private void showGuider() {
		final ImageView iv = new ImageView(this);
		iv.setScaleType(ScaleType.FIT_XY);
		// iv.setImageResource(R.drawable.start_festival_default);
		// bit = BitmapFactory
		// .decodeResource(getResources(), R.drawable.startimg2);
		// iv.setImageBitmap(bit);
		iv.setImageResource(R.drawable.startimg2);

		final WindowManager wm = getWindowManager();
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		wmParams.format = 1;
		wmParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
		wmParams.width = display.getWidth();
		wmParams.height = display.getHeight() - getStatusBarHeight(this);
		try {
			if (wm == null) {
				DebugTool.info(TAG, "wm is null");
			} else {
				DebugTool.info(TAG, "wm is ok");
			}
			wm.addView(iv, wmParams);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			DebugTool.info(TAG, "not addview exception");
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					if (wm == null) {
						DebugTool.info(TAG, "wm is null");
					} else {
						DebugTool.info(TAG, "wm is ok");
					}
					wm.removeView(iv);
					// bit.recycle();
					// bit = null;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					DebugTool.info(TAG, "not addview exception");
				}
			}
		}, 4000);
	}

	/**
	 * @param packageName
	 * @return 根据包名获取程序名
	 */
	private String getProgramNameByPackageName(String packageName) {
		PackageManager pm = getPackageManager();
		String name = null;
		try {
			name = pm.getApplicationLabel(
					pm.getApplicationInfo(packageName,
							PackageManager.GET_META_DATA)).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}

	private AsynTaskListener<Group<GameInfo>> allAppsUpdateAsynListener = new AsynTaskListener<Group<GameInfo>>() {

		@Override
		public boolean preExecute(BaseTask<Group<GameInfo>> task,
				Integer taskKey) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<Group<GameInfo>> result) {
			// TODO Auto-generated method stub
			// 当网络正常时
			needUpdateGamesNumFromNet = 0;
			needUpdateGamesNumFromNet += getLocalNeedUpdateStateGameNumber();
			if (result.getCode() == TaskResult.OK) {
				// 当能从后台得到的数据不为null时
				if (result.getData() != null) {
					// 当前需要升级的游戏列表
					Group<GameInfo> needUpdateGames = result.getData();
					int needUpdateGamesSize = needUpdateGames.size();

					if (needUpdateGamesSize > 0) {
						SharedPreferences sp = getSharedPreferences(
								Constant.NEW_VERSION_INFO_SP, MODE_PRIVATE);
						Editor editor = sp.edit();
						// 比较每一个需要更新的游戏，看是否需要升级
						for (int i = 0; i < needUpdateGamesSize; i++) {
							GameInfo gameInfo = needUpdateGames.get(i);
							// 后台服务器获取的游戏信息的gameId
							String gameIdFromNet = gameInfo.getGameId();
							String gamePackageName = gameInfo.getPackageName();
							String localGameId = getLocalGameIdByPackageName(gamePackageName);
							if (localGameId.equals("")) {
								int localRealVersionCode = getRealVersionCode(gamePackageName);
								int versionInNet = gameInfo.getVersionCode();
								if (localRealVersionCode < versionInNet) {
									MyGameInfo myGame = new MyGameInfo();
									myGame.setGameId(gameIdFromNet);
									myGame.setPackageName(gamePackageName);
									myGame.setName(getProgramNameByPackageName(gamePackageName));
									myGame.setVersionCode(localRealVersionCode);
									myGame.setLaunchAct(getLastestLauncherActivity(gamePackageName));
									myGame.setState(Constant.GAME_STATE_NEED_UPDATE);
									PersistentSynUtils.addModel(myGame);
									editor.putInt(myGame.getGameId(),
											versionInNet);
									needUpdateGamesNumFromNet++;
								}

							} else if (gameIdFromNet.equals(localGameId)) {
								// 后台服务器获取的游戏信息的versionCode
								int versionCodeFromNet = gameInfo
										.getVersionCode();
								DebugTool.info(TAG, "versionCodeFromNet："
										+ versionCodeFromNet + ";gameid="
										+ gameIdFromNet);
								// 根据gameId来获取本地已安装游戏versionCode
								int versionCodeFromLocal = getVersionCodeFromLocal(gameIdFromNet);
								DebugTool.info(TAG, "versionCodeFromLocal："
										+ versionCodeFromLocal + ";gameid="
										+ gameIdFromNet);
								// 如果versioncode为0，则获取真实的versioncode
								if (0 == versionCodeFromLocal) {
									versionCodeFromLocal = getRealVersionCode(gameInfo
											.getPackageName());
								}
								// 比较versionCode来判断是否需要升级
								if (versionCodeFromNet > versionCodeFromLocal) {
									// 当检测到需要更新的时候，就修改状态state
									updateGameState(gamePackageName);
									// 同时将新版本信息保存到SP中
									editor.putInt(gameInfo.getGameId(),
											versionCodeFromNet);
									needUpdateGamesNumFromNet++;
								}
							}

						}
						editor.commit();

					}

				}
			}
			notifyUpgrade(needUpdateGamesNumFromNet);
			if (needUpdateGamesNumFromNet > 0) {
				if (manageRadioButton != null && !manageRadioButton.isChecked()) {
					manageRadioButton.showRemindPoint();
				}

			}
		}

		@Override
		public TaskResult<Group<GameInfo>> doTaskInBackground(Integer taskKey) {
			// TODO Auto-generated method stub
			HttpReqParams params = new HttpReqParams();
			String pkgNamesStr = getAllAppsPkgName();
			DebugTool.info(TAG, "已安装游戏的包名为：" + pkgNamesStr);
			params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			params.setPackageNames(pkgNamesStr);
			return HttpApi.getList(UrlConstant.HTTP_GET_GAMEINFO_INSTALLED1,
					UrlConstant.HTTP_GET_GAMEINFO_INSTALLED2,
					UrlConstant.HTTP_GET_GAMEINFO_INSTALLED3, GameInfo.class,
					params.toJsonParam());
		}
	};

	/**
	 * @param myGifts
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 将服务器获取到的已领取礼包保存到数据库
	 */
	private void saveTodatabase(final Group<MyGiftInfo> myGifts) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PersistentSynUtils.execDeleteData(MyGiftInfo.class,
						" where userId='" + user.getUserId() + "'");
				for (MyGiftInfo myGift : myGifts) {
					if (myGift.getReceiveTime() != 0
							&& myGift.getUseMethod() != null) {
						myGift.setUserId(user.getUserId());
						PersistentSynUtils.addModel(myGift);
					}

				}
			}
		}).start();
	}

	AsynTaskListener<Group<MyGiftInfo>> myGiftAsynTaskListener = new AsynTaskListener<Group<MyGiftInfo>>() {

		@Override
		public boolean preExecute(BaseTask<Group<MyGiftInfo>> task,
				Integer taskKey) {
			// TODO Auto-generated method stub
			user = BaseApplication.getLoginUser();
			if (user == null || user.getUserId() == null) {
				return false;
			}
			return true;
		}

		@Override
		public void onResult(Integer taskKey,
				TaskResult<Group<MyGiftInfo>> result) {
			// TODO Auto-generated method stub
			if (result.getCode() == 0 && result.getData() != null
					&& result.getData().size() > 0) {
				Group<MyGiftInfo> myGifts = result.getData();
				saveTodatabase(myGifts);
			}
		}

		@Override
		public TaskResult<Group<MyGiftInfo>> doTaskInBackground(Integer taskKey) {
			// TODO Auto-generated method stub
			HttpReqParams param = new HttpReqParams();
			param.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			param.setUserId(user.getUserId());
			return HttpApi.getList(UrlConstant.HTTP_GET_MYGIFT1,
					UrlConstant.HTTP_GET_MYGIFT2, UrlConstant.HTTP_GET_MYGIFT3,
					MyGiftInfo.class, param.toJsonParam());
		}
	};

	AsynTaskListener<Group<GiftGameInfo>> giftAsynTaskListener = new AsynTaskListener<Group<GiftGameInfo>>() {

		@Override
		public boolean preExecute(BaseTask<Group<GiftGameInfo>> task,
				Integer taskKey) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onResult(Integer taskKey,
				TaskResult<Group<GiftGameInfo>> result) {
			// TODO Auto-generated method stub
			if (result.getCode() == 0 && result.getData() != null
					&& result.getData().size() > 0) {
				getSharedPreferences(SP_GIFT_MINTIME, MODE_PRIVATE)
						.edit()
						.putLong(SP_VALUE_GIFT_MINTIME,
								System.currentTimeMillis()).commit();
				// 红点提示
				userCenterRadioButton.showRemindPoint();
				getSharedPreferences(Constant.SP_HAS_NEW_GIFT, MODE_PRIVATE)
						.edit()
						.putBoolean(Constant.SP_VALUE_HAS_NEW_GIFT, true)
						.commit();
				Group<GiftGameInfo> giftGames = result.getData();
				saveToDatabasesIfNotExist(giftGames, result.getCurrentTime());
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					checkServerTime();
				}
			}, 1500);

		}

		@Override
		public TaskResult<Group<GiftGameInfo>> doTaskInBackground(
				Integer taskKey) {
			// TODO Auto-generated method stub
			HttpReqParams param = new HttpReqParams();
			param.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
			SharedPreferences sp_minTime = getSharedPreferences(
					SP_GIFT_MINTIME, MODE_PRIVATE);
			Long minTime = sp_minTime.getLong(SP_VALUE_GIFT_MINTIME, 0);
			if(minTime!=0){
				minTime = minTime-86400000;
			}
			param.setMinTime(minTime);
			return HttpApi.getList(UrlConstant.HTTP_GET_GIFT1,
					UrlConstant.HTTP_GET_GIFT2, UrlConstant.HTTP_GET_GIFT3,
					GiftGameInfo.class, param.toJsonParam());
		}
	};

	/**
	 * @param serverTime
	 *  @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 根据服务器时间清楚过期礼包
	 */
	private void deleteGiftOverdueByServerTime(Long serverTime) {
		// TODO Auto-generated method stub
		PersistentSynUtils.execDeleteData(GiftInfo.class,
				" where endTime>0 and endTime<" + serverTime);
		PersistentSynUtils.execDeleteData(GiftInfo.class, " where useMethod='"
				+ "'");
		Group<GiftInfo> giftInfos = PersistentSynUtils.getModelList(
				GiftInfo.class, " id>0");
		Group<GiftGameInfo> giftGames = PersistentSynUtils.getModelList(
				GiftGameInfo.class, " id>0");
		for (GiftGameInfo giftGame : giftGames) {
			int giftNum = getGiftNums(giftGame, giftInfos);
			if (giftNum > 0) {
				giftGame.setGiftNum(giftNum);
				PersistentSynUtils.update(giftGame);
			} else {
				PersistentSynUtils.delete(giftGame);
			}
		}
	}

	/**
	 * @param giftGame
	 * @param giftInfos
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription  获取单个游戏下的礼包数量
	 */
	private int getGiftNums(GiftGameInfo giftGame, Group<GiftInfo> giftInfos) {
		int num = 0;
		for (GiftInfo giftInfo : giftInfos) {
			if (giftInfo.getGameid().equals(giftGame.getGameid())) {
				num++;
			}
		}
		return num;
	}

	/**
	 * @param giftGames
	 * @param serverTime
	 *  @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 将获取到的礼包和带礼包的游戏存入数据库
	 */
	private void saveToDatabasesIfNotExist(final Group<GiftGameInfo> giftGames,
			final Long serverTime) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				for (GiftGameInfo giftGame : giftGames) {
					String gameid = giftGame.getGameid();
					Group<GiftInfo> giftInfos = giftGame.getGiftData();
					for (GiftInfo giftInfo : giftInfos) {
						giftInfo.setGameid(gameid);
						Group<GiftInfo> gifts = PersistentSynUtils
								.getModelList(
										GiftInfo.class,
										" giftPackageid='"
												+ giftInfo.getGiftPackageid()
												+ "'");
						if (gifts == null || gifts.size() == 0) {
							PersistentSynUtils.addModel(giftInfo);
						}

					}
					Group<GiftGameInfo> games = PersistentSynUtils
							.getModelList(GiftGameInfo.class, " gameid='"
									+ gameid + "'");
					if (games == null || games.size() == 0) {
						//giftGame.setGiftNum(giftGame.getGiftData().size());
						PersistentSynUtils.addModel(giftGame);
					}
				}

			}
		}).start();
	}

	/**
	 * @param context
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 通知栏高度
	 */
	private static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

}