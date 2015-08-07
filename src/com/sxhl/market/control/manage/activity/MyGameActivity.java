package com.sxhl.market.control.manage.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseExitActivity;
import com.sxhl.market.control.game.activity.CommDetailActivity;
import com.sxhl.market.control.manage.adapter.CollectionAdapter;
import com.sxhl.market.control.manage.adapter.CollectionAdapter.CollectionButtonClickInterface;
import com.sxhl.market.control.manage.adapter.MyGameAdapter;
import com.sxhl.market.control.manage.adapter.MyGameViewpagerAdapter;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.CollectionInfo;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.net.http.download.ApkDownListenner;
import com.sxhl.market.model.net.http.download.DownloadTask;
import com.sxhl.market.model.net.http.download.FileDownInfo;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.receiver.BReceiver;
import com.sxhl.market.utils.AppUtil;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.DeviceTool;
import com.sxhl.market.utils.NetUtil;
import com.sxhl.market.utils.ZipUtils;
import com.sxhl.market.view.CommonCallbackDialog;
import com.sxhl.market.view.CommonCallbackDialog.MyDialogListener;
import com.sxhl.market.view.CommonWaitDialog;

/**
 * 实现的主要功能：游戏管理
 * 
 * @author: LiuQin
 * @date: 2013-5-22 上午11:56:05 修改记录： 修改者: 修改时间： 修改内容：
 */
public class MyGameActivity extends BaseExitActivity implements Runnable {
	private static final String TAG = "MyGameActivity";
	private static final boolean IS_ONLY_ZIP = false;
	public static final String ZIP_EXT = ".zip";
	private static final int HANDLER_INIT = 0;
	private static final int HANDLER_SHOW_COLLECTION = 2;
	// 更新了游戏
	private static final int HANDLER_GAME_UPDATED_SUCCESS = 7;
	// 更新游戏失败
	private static final int HANDLER_GAME_UPDATED_FAILED = 8;
	// 开始获取最新游戏信息
	private static final int HANDLER_GAME_START_UPDATE = 9;
	// 点击下载
	private static final int HANDLER_ON_ITEM_CLICK_TO_DOWN = 3;
	// 系统更新了应用
	private static final int HANDLER_PKG_INSTALLED_REMOVED = 5;

	private RadioGroup mRadioGroup;
	// 我的游戏选项卡
	private RadioButton mRadioBtnMyGame;
	// 收藏选项卡
	private RadioButton mRadioBtnCollection;
	// 我的游戏列表
	private GridView mMyGameGv;
	// 收藏游戏列表
	private ListView mCollectionLv;
	private CollectionAdapter mCollectionAdapter;
	private Group<CollectionInfo> mCollectionInfos = new Group<CollectionInfo>();
	private MyGameAdapter mMyGameAdapter;
	private Group<MyGameInfo> mMyGameInfos = new Group<MyGameInfo>();
	private DownloadTask mDownTask;
	private CommonCallbackDialog mDialog;
	private CommonWaitDialog mProDialog;
	private boolean mIsUnzipping = false;
	private Handler mWorketHandler;
	private LinearLayout mLinearLayoutProgress;
	private ProgressBar mProBarLoading;
	private TextView mTvLoadingTip;
	private boolean mIsIniting = false;
	public static volatile boolean sIsVisible = false;
	private CommonCallbackDialog mUpdateDialog;
	private ViewPager myGameViewpager;
	private MyGameInfo myCurrentUpdateGameInfo;
	private TextView myGameEmptyView, myCollectionEmptyView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.manage_activity_layout_mygame);
		initViews();
		setHeadTitle(getString(R.string.manage_head_title));
		bindHeadRightButton(R.drawable.manage_add_app);
		mIsIniting = true;
		new Thread(this).start();
	}

	private void initViews() {
		myGameEmptyView = new TextView(this);
		myCollectionEmptyView = new TextView(this);
		myGameEmptyView.setText("亲，您还未下载游戏");
		myCollectionEmptyView.setText("还没有收藏哦！");
		mMyGameGv = new GridView(this);
		mMyGameGv.setNumColumns(4);
		mCollectionLv = new ListView(this);
		mCollectionLv.setCacheColorHint(getResources().getColor(
				android.R.color.transparent));
		mCollectionLv.setSelector(getResources().getDrawable(
				R.drawable.list_view_bg_transparent));
		mRadioGroup = (RadioGroup) findViewById(R.id.manage_radioGroup);
		mRadioBtnMyGame = (RadioButton) findViewById(R.id.manage_radioBtn_mygame);
		mRadioBtnCollection = (RadioButton) findViewById(R.id.manage_radioBtn_collection);
		myGameViewpager = (ViewPager) findViewById(R.id.vPager_mygame);
		List<View> viewList = new ArrayList<View>();
		LinearLayout linMygame = new LinearLayout(this);
		LinearLayout linMyCollection = new LinearLayout(this);
		linMygame.addView(myGameEmptyView);
		linMygame.addView(mMyGameGv);
		myGameEmptyView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		myGameEmptyView.setGravity(Gravity.CENTER);
		myGameEmptyView.setVisibility(View.GONE);
		mMyGameGv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		linMyCollection.addView(myCollectionEmptyView);
		linMyCollection.addView(mCollectionLv);
		myCollectionEmptyView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		myCollectionEmptyView.setGravity(Gravity.CENTER);
		myCollectionEmptyView.setVisibility(View.GONE);
		mCollectionLv.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		viewList.add(linMygame);
		viewList.add(linMyCollection);
		PagerAdapter mPagerAdapter = new MyGameViewpagerAdapter(viewList);
		myGameViewpager.setAdapter(mPagerAdapter);
		myGameViewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if (arg0 == 0) {
					mRadioBtnMyGame.setChecked(true);
				} else if (arg0 == 1) {
					mRadioBtnCollection.setChecked(true);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		mRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup rGroup,
							int checkedId) {
						if (mIsIniting) {
							return;
						}
						selectRadioBtn();
					}
				});

		mMyGameGv.setOnItemClickListener(myGameOnItemClickListener);
		mMyGameGv.setOnItemLongClickListener(myGameOnItemLongClickListener);
		mMyGameAdapter = new MyGameAdapter(MyGameActivity.this);
		mMyGameGv.setAdapter(mMyGameAdapter);

		mCollectionLv.setDivider(getResources().getDrawable(
				R.drawable.listview_divider));
		mCollectionAdapter = new CollectionAdapter(MyGameActivity.this,
				collectionItemClickInterface);
		mCollectionLv.setAdapter(mCollectionAdapter);

		mLinearLayoutProgress = (LinearLayout) findViewById(R.id.manage_linearlayout_progress);
		mProBarLoading = (ProgressBar) findViewById(R.id.manage_proBar_loading);
		mTvLoadingTip = (TextView) findViewById(R.id.manage_tv_loading_tip);

		mDownTask = DownloadTask.getInstance(this);
		mWorketHandler = ((ApkDownListenner) mDownTask.getListenner())
				.getmWorketHandler();
		mDownTask.regBrocastReceiver(downloadReceiver);
	}

	private void cancelNotification() {
		ApkDownListenner.cancelNF(this, R.drawable.nf_down_complete);
		ApkDownListenner.cancelNF(this, R.drawable.nf_down_error);
	}

	private void selectRadioBtn() {
		if (mRadioBtnMyGame.isChecked()) {
			myGameViewpager.setCurrentItem(0);
			MyGameActivity.sIsVisible = true;
			cancelNotification();
		} else {
			myGameViewpager.setCurrentItem(1);
			MyGameActivity.sIsVisible = false;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		DebugTool.debug(TAG, "onStart");

		// mDownTask.regBrocastReceiver(downloadReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		DebugTool.debug(TAG, "onResume");
		Group<MyGameInfo> lastGames = PersistentSynUtils.getModelList(
				MyGameInfo.class, "id>0");
		if (lastGames != null && lastGames.size() > 0) {
			myGameEmptyView.setVisibility(View.GONE);
			this.mMyGameInfos = lastGames;
			mMyGameAdapter.setGroup(this.mMyGameInfos);
			mMyGameAdapter.notifyDataSetChanged();
		} else {
			myGameEmptyView.setVisibility(View.VISIBLE);
		}

		Group<CollectionInfo> collectionInfos = PersistentSynUtils
				.getModelList(CollectionInfo.class, " id>0 ");
		if (collectionInfos != null && collectionInfos.size() > 0) {
			myCollectionEmptyView.setVisibility(View.GONE);
			this.mCollectionInfos = collectionInfos;
			mCollectionAdapter.setGroup(this.mCollectionInfos);
			mCollectionAdapter.notifyDataSetChanged();
			mRadioBtnCollection.setText(getString(
					R.string.manage_tab_collection_title,
					collectionInfos.size()));
		} else {
			myCollectionEmptyView.setVisibility(View.VISIBLE);
		}

		Intent intent = getIntent();
		if (intent != null) {
			Bundle b = intent.getBundleExtra("data");
			if (b != null) {
				int nfType = b.getInt("nf_type");
				if (nfType != 0) {
					// 点击通知栏进来的
					mRadioBtnMyGame.setChecked(true);
				}
			}
		}

		if (mRadioBtnMyGame.isChecked()) {
			MyGameActivity.sIsVisible = true;
			cancelNotification();
		}

		closeUnzipDialogIfFinish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyGameActivity.sIsVisible = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DebugTool.debug(TAG, "onDestroy");

		mDownTask.unregBrocastReceiver(downloadReceiver);
		closeDialog();
		BReceiver.uninstallPackageName = "";
	}

	@Override
	public void run() {
		// 从我的游戏列表读取数据
		Group<MyGameInfo> infos = PersistentSynUtils.getModelList(
				MyGameInfo.class, " id>0");
		if (infos != null && infos.size() > 0) {
			mMyGameInfos = infos;
		}

		// for test
		addTestData();

		// 获取收藏数量
		Group<CollectionInfo> collectionInfos = PersistentSynUtils
				.getModelList(CollectionInfo.class, "id>0 ");
		if (collectionInfos != null && collectionInfos.size() > 0) {
			mCollectionInfos = collectionInfos;
		}

		mHandler.sendEmptyMessage(HANDLER_INIT);
	}

	/**
	 * kds 处理点击事件
	 */
	private CollectionButtonClickInterface collectionItemClickInterface = new CollectionButtonClickInterface() {
		@Override
		public void softDownLoadOrInstall(final CollectionInfo gameInfo) {
			// 收藏中点击下载
			myGameEmptyView.setVisibility(View.GONE);
			GameInfo info = new GameInfo();
			info.setGameId(gameInfo.getGameId());
			info.setPackageName(gameInfo.getPackageName());
			info.setGameName(gameInfo.getName());
			info.setMinPhoto(gameInfo.getIconMin());
			info.setFile(gameInfo.getDownAddress());
			info.setGameSize(Integer.parseInt(gameInfo.getSize()));
			info.setVersionCode((int) gameInfo.getVersion());
			if (addToMyGameList(MyGameActivity.this, info)) {
				PersistentSynUtils.delete(gameInfo);

				mCollectionInfos.remove(gameInfo);
				mHandler.obtainMessage(HANDLER_SHOW_COLLECTION).sendToTarget();
			}
		}

		@Override
		public void softDelete(CollectionInfo object) {
			// 收藏中点击删除
			PersistentSynUtils.delete(object);
			mCollectionInfos.remove(object);
			mHandler.obtainMessage(HANDLER_SHOW_COLLECTION).sendToTarget();
		}

		@Override
		public void jtoGameDetail(CollectionInfo gameInfo) {
			// TODO Auto-generated method stub
			if (gameInfo != null) {
				GameInfo info = makeGameInfo(gameInfo);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable(CommDetailActivity.KEY_GAMEINFO, info);
				mBundle.putInt(CommDetailActivity.KEY_GAMEINFO_TYPEID,
						Integer.parseInt(info.getTypeId()));
				jumpToActivity(MyGameActivity.this, CommDetailActivity.class,
						mBundle, false);
			}

		}
	};

	private OnItemLongClickListener myGameOnItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			showDeleteDialog(position);
			return true;
		}
	};

	private void showDeleteDialog(final int index) {
		closeDialog();
		final MyGameInfo myGameInfo = mMyGameInfos.get(index);
		final int state = myGameInfo.getState();
		mDialog = new CommonCallbackDialog(this, R.style.TipDialog);
		mDialog.setListener(new MyDialogListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				switch (view.getId()) {
				case R.id.btnCancle:
					closeDialog();
					break;
				case R.id.btnSure:
					boolean apkInstalled = false;
					// if(state==Constant.GAME_STATE_INSTALLED ||
					// state==Constant.GAME_STATE_INSTALLED_USER){
					if (state == Constant.GAME_STATE_INSTALLED
							|| state == Constant.GAME_STATE_NEED_UPDATE) {
						PackageInfo packageInfo = AppUtil.getPkgInfoByName(
								MyGameActivity.this,
								myGameInfo.getPackageName());
						if (packageInfo != null) {
							apkInstalled = true;
							BReceiver.uninstallPackageName = myGameInfo
									.getPackageName();
							AppUtil.uninstallApk(MyGameActivity.this,
									myGameInfo.getPackageName());
						}
					}
					if (!apkInstalled) {
						mMyGameInfos.remove(index);
						mMyGameAdapter.notifyDataSetChanged();
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PersistentSynUtils.delete(myGameInfo);

								sendBroadcast(new Intent(
										"com.atet.tvmarket.installnewgame"));
								if (state == Constant.GAME_STATE_NOT_INSTALLED
										|| state == Constant.GAME_STATE_INSTALLED) {
									File apkFile = new File(myGameInfo
											.getLocalDir(), myGameInfo
											.getLocalFilename());
									if (apkFile.exists()) {
										apkFile.delete();
									}

									if (myGameInfo.getLocalFilename()
											.toLowerCase()
											.endsWith(MyGameActivity.ZIP_EXT)) {
										// 删除解压出来的apk文件
										File file = new File(MyGameActivity
												.getLocalUnApkPath(myGameInfo));
										if (file != null && file.exists()) {
											file.delete();
										}
									}
								} else if (state == Constant.GAME_STATE_INSTALLED_SYSTEM
										|| state == Constant.GAME_STATE_INSTALLED_USER) {
								} else {
									mDownTask.cancelDownload(new FileDownInfo(
											myGameInfo.getGameId(), null,
											myGameInfo.getLocalDir(),
											myGameInfo.getLocalFilename()));
								}

								Intent in = new Intent(
										DownloadTask.ACTION_ON_APP_INSTALLED);
								Bundle bundle = new Bundle();
								bundle.putInt("opType",
										DownloadTask.OP_UNINSTALL);
								// bundle.putString("packageName",
								// myGameInfo.getPackageName());
								// bundle.putString("gameId",
								// myGameInfo.getGameId());
								bundle.putSerializable("myGameInfo", myGameInfo);
								in.putExtra("data", bundle);
								MyGameActivity.this.sendBroadcast(in);
							}
						}).start();
					}
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Group<MyGameInfo> lastestGames = PersistentSynUtils
									.getModelList(MyGameInfo.class, "id>0");
							if (lastestGames == null
									|| lastestGames.size() == 0) {
								myGameEmptyView.setVisibility(View.VISIBLE);
							}
						}
					}, 200);

					closeDialog();
					break;
				}
			}
		});
		if ((state & 0xf00) != 0) {
			mDialog.setTitle(getText(R.string.manage_uninstall_app) + "");
		} else {
			mDialog.setTitle(getText(R.string.manage_uninstall_game) + "");
		}
		mDialog.setMessage(myGameInfo.getName());
		mDialog.show();
	}

	private OnItemClickListener myGameOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MyGameInfo myGameInfo = mMyGameInfos.get(position);
			int state = myGameInfo.getState() & 0xff;
			if (state == Constant.GAME_STATE_NOT_DOWNLOAD
					|| state == Constant.GAME_STATE_DOWNLOAD_ERROR) {
				// 未下载
				notDownloadHanlde(myGameInfo);
			} else if (state == Constant.GAME_STATE_NOT_INSTALLED) {
				// 未安装
				final File apkFile = new File(myGameInfo.getLocalDir(),
						myGameInfo.getLocalFilename());
				if (apkFile.exists()) {
					if (apkFile.getName().toLowerCase().endsWith(ZIP_EXT)) {
						if (mIsUnzipping) {
							return;
						}
						mIsUnzipping = true;
						final String unApkPath = getLocalUnApkPath(myGameInfo);
						DebugTool.debug(TAG, "decompress...");
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								unZipApk(apkFile.getAbsolutePath(),
										Constant.GAME_ZIP_DATA_LOCAL_DIR,
										unApkPath);
							}
						}).start();
					} else {
						// apk文件已经下载，直接安装
						AppUtil.installApkByPath(MyGameActivity.this,
								apkFile.getAbsolutePath());
					}
				} else {
					// 安装文件已丢失
					showReDownloadDialog(position, myGameInfo.getName()
							+ getText(R.string.down_msg_file_already_delete));
				}
			} else if (state == Constant.GAME_STATE_INSTALLED) {
				// 已安装
				// if(!AppUtil.startAppByPkgName(MyGameActivity.this,
				// mMyGameInfos.get(position).getPackageName())){
				if (!AppUtil.startAppByActName(MyGameActivity.this,
						myGameInfo.getPackageName(), myGameInfo.getLaunchAct())) {
					String downUrl = myGameInfo.getDownUrl();
					if (downUrl == null || downUrl.equals("")) {
						Toast.makeText(MyGameActivity.this,
								R.string.manage_warn_app_delete,
								Toast.LENGTH_SHORT).show();
					} else {
						// 游戏已卸载
						File zipFile = new File(myGameInfo.getLocalDir(),
								myGameInfo.getLocalFilename());
						if (zipFile != null && zipFile.exists()) {
							// 提示重新安装
							showReInstallDialog(position);
						} else {
							// 提示重新下载
							showReDownloadDialog(
									position,
									myGameInfo.getName()
											+ getText(R.string.down_msg_already_uninstall));
							// Toast.makeText(MyGameActivity.this,
							// R.string.manage_warn_delete,
							// Toast.LENGTH_SHORT).show();
						}
					}
				}
			} else if (state == Constant.GAME_STATE_NEED_UPDATE) {
				showGameUpdateDialog(myGameInfo);
			}
		}
	};

	/**
	 * @param myGameInfo
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 现实更新游戏对话框
	 */
	private void showGameUpdateDialog(final MyGameInfo myGameInfo) {
		// TODO Auto-generated method stub
		mUpdateDialog = new CommonCallbackDialog(MyGameActivity.this,
				R.style.TipDialog, new MyDialogListener() {
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						switch (view.getId()) {
						case R.id.btnCancle:
							mUpdateDialog.dismiss();
							break;
						case R.id.btnSure:
							mUpdateDialog.dismiss();
							handleUpdate(myGameInfo);
						}
					}
				}, getString(R.string.manager_hint_update),
				getString(R.string.manager_game_is_updae));
		LayoutInflater inf = getLayoutInflater();
		View v = inf.inflate(R.layout.layout_self_tip_dialog, null);
		mUpdateDialog.setContentView(v);
		mUpdateDialog.setCancelable(false);
		mUpdateDialog.setCanceledOnTouchOutside(false);
		mUpdateDialog.show();
	}

	/**
	 * @param myGameInfo
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 执行更新游戏操作
	 */
	private void handleUpdate(final MyGameInfo myGameInfo) {
		// TODO Auto-generated method stub
		if (!NetUtil.isEnableDownload(MyGameActivity.this, true)) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg_start_update = mHandler.obtainMessage(
						HANDLER_GAME_START_UPDATE, myGameInfo.getGameId());
				mHandler.sendMessage(msg_start_update);
				// 获取最新游戏详情
				GameInfo gameInfo = getLatestMyGameInfo(myGameInfo);
				if (gameInfo == null) {
					Message msg_update_failed = mHandler.obtainMessage(
							HANDLER_GAME_UPDATED_FAILED, myGameInfo.getGameId());
					mHandler.sendMessage(msg_update_failed);
					return;
				}
				myGameInfo.setDownUrl(gameInfo.getFile());
				myGameInfo.setIconUrl(gameInfo.getMinPhoto());
				myGameInfo.setState(Constant.GAME_STATE_NOT_DOWNLOAD);
				SharedPreferences sp = getSharedPreferences(
						Constant.NEW_VERSION_INFO_SP, MODE_PRIVATE);
				Editor editor = sp.edit();
				int newVersionCode = sp.getInt(myGameInfo.getGameId(), 0);
				editor.remove(myGameInfo.getGameId()).commit();
				myGameInfo.setVersionCode(newVersionCode);
				myGameInfo.setLocalDir(Constant.GAME_DOWNLOAD_LOCAL_DIR);
				if (MyGameActivity.IS_ONLY_ZIP
						|| gameInfo.getFile().toLowerCase().endsWith(ZIP_EXT)) {
					myGameInfo.setLocalFilename(getLocalZipName(gameInfo));
				} else {
					myGameInfo.setLocalFilename(getLocalApkName(gameInfo));
				}

				Message msg_update_success = mHandler.obtainMessage(
						HANDLER_GAME_UPDATED_SUCCESS, myGameInfo);
				mHandler.sendMessage(msg_update_success);
				PersistentSynUtils.update(myGameInfo);
			}
		}).start();

	}

	/**
	 * @param myGameInfo
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 获取最新游戏详情
	 */
	private GameInfo getLatestMyGameInfo(MyGameInfo myGameInfo) {
		// TODO Auto-generated method stub
		HttpReqParams params = new HttpReqParams();
		params.setDeviceId(BaseApplication.deviceInfo.getDeviceId());
		params.setGameId(myGameInfo.getGameId());
		TaskResult<Group<GameInfo>> resultGameInfos = HttpApi.getList(
				UrlConstant.HTTP_GAME_DETAIL1, UrlConstant.HTTP_GAME_DETAIL2,
				UrlConstant.HTTP_GAME_DETAIL3, GameInfo.class,
				params.toJsonParam());
		if (resultGameInfos != null
				&& resultGameInfos.getCode() == TaskResult.OK
				&& resultGameInfos.getData() != null) {
			return resultGameInfos.getData().get(0);
		} else {
			return null;
		}
	}

	/**
	 * @param myGameInfo
	 * @author yindangchao
	 * @date 2014/11/14 15:20
	 * @discription 更新游戏下载
	 */
	private void upgradeDownloadHandle(MyGameInfo myGameInfo) {
		// TODO Auto-generated method stub

		File targetFile = new File(myGameInfo.getLocalDir(),
				myGameInfo.getLocalFilename());
		if (targetFile.exists() && targetFile.length() > 0) {
			targetFile.delete();
		}
		notDownloadHanlde(myGameInfo);
	}

	private void startDownload(DownloadTask downTask, MyGameInfo myGameInfo) {
		FileDownInfo fileDownInfo = new FileDownInfo(myGameInfo.getGameId(),
				myGameInfo.getDownUrl(), myGameInfo.getLocalDir(),
				myGameInfo.getLocalFilename());
		fileDownInfo.setExtraData(myGameInfo.getName());
		fileDownInfo.setObject(myGameInfo);
		downTask.download(fileDownInfo);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			FileDownInfo fileDownInfo;
			MyGameInfo myGameInfo;

			try {
				switch (msg.what) {
				case HANDLER_INIT:
					if (mMyGameInfos == null || mMyGameInfos.size() == 0) {
						myGameEmptyView.setVisibility(View.VISIBLE);
					} else {
						myGameEmptyView.setVisibility(View.GONE);
						mMyGameAdapter.setGroup(mMyGameInfos);
						mMyGameAdapter.notifyDataSetChanged();
					}
					if (mCollectionInfos == null
							|| mCollectionInfos.size() == 0) {
						myCollectionEmptyView.setVisibility(View.VISIBLE);
					} else {
						myCollectionEmptyView.setVisibility(View.GONE);
						mRadioBtnCollection.setText(getString(
								R.string.manage_tab_collection_title,
								mCollectionInfos.size()));
						mCollectionAdapter.setGroup(mCollectionInfos);
						mCollectionAdapter.notifyDataSetChanged();
					}

					mLinearLayoutProgress.setVisibility(View.GONE);
					myGameViewpager.setVisibility(View.VISIBLE);
					mIsIniting = false;
					break;
				case HANDLER_SHOW_COLLECTION:

					mCollectionAdapter.setGroup(mCollectionInfos);
					mCollectionAdapter.notifyDataSetChanged();
					mRadioBtnCollection.setText(getString(
							R.string.manage_tab_collection_title,
							mCollectionInfos.size()));

					Group<CollectionInfo> dcollectionInfos = PersistentSynUtils
							.getModelList(CollectionInfo.class, " id>0 ");
					if (dcollectionInfos == null
							|| dcollectionInfos.size() == 0) {
						myCollectionEmptyView.setVisibility(View.VISIBLE);
					}
					break;

				case HANDLER_ON_ITEM_CLICK_TO_DOWN:
					tipBarHandle((String) msg.obj,
							HANDLER_ON_ITEM_CLICK_TO_DOWN, 0);
					break;

				case DownloadTask.STATE_ON_DOWNLOAD_START: {
					tipBarHandle((String) msg.obj,
							DownloadTask.STATE_ON_DOWNLOAD_START, 0);
					break;
				}

				case DownloadTask.STATE_ON_DOWNLOAD_STOP:
					tipBarHandle((String) msg.obj,
							DownloadTask.STATE_ON_DOWNLOAD_STOP, 0);
					break;

				case DownloadTask.STATE_ON_DOWNLOAD_PROGRESS: {
					// 更新进度
					tipBarHandle((String) msg.obj,
							DownloadTask.STATE_ON_DOWNLOAD_PROGRESS, msg.arg1);
					break;
				}

				case DownloadTask.STATE_ON_DOWNLOAD_FINISH:
					fileDownInfo = (FileDownInfo) msg.obj;
					String fileId = fileDownInfo.getFileId();
					if (fileId != null) {
						MyGameInfo info = (MyGameInfo) fileDownInfo.getObject();
						myGameInfo = getMyGameInfoById(fileId);
						if (myGameInfo != null) {
							myGameInfo.setState(info.getState());
							myGameInfo.setPackageName(info.getPackageName());
							myGameInfo.setName(info.getName());
							mMyGameAdapter.notifyDataSetChanged();
						}

						if (info.getState() == Constant.GAME_STATE_NOT_INSTALLED) {
							tipBarHandle(fileId,
									DownloadTask.STATE_ON_DOWNLOAD_FINISH, 0);
						} else {
							tipBarHandle(fileId,
									DownloadTask.STATE_ON_DOWNLOAD_ERROR, 0);
						}
					}
					break;

				case DownloadTask.STATE_ON_DOWNLOAD_ERROR:
					myGameInfo = getMyGameInfoById((String) msg.obj);
					if (myGameInfo != null) {
						myGameInfo.setState(Constant.GAME_STATE_DOWNLOAD_ERROR);
					}

					tipBarHandle((String) msg.obj,
							DownloadTask.STATE_ON_DOWNLOAD_ERROR, 0);
					break;

				case DownloadTask.STATE_ON_DOWNLOAD_WAIT:
					FileDownInfo fileDownInfo2 = (FileDownInfo) msg.obj;
					myGameInfo = getMyGameInfoById(fileDownInfo2.getFileId());
					if (myGameInfo == null) {
						myGameInfo = (MyGameInfo) fileDownInfo2.getObject();
						mMyGameInfos.add(myGameInfo);
						mMyGameAdapter.notifyDataSetChanged();
					} else {
						if (myGameInfo.getState() != Constant.GAME_STATE_NOT_DOWNLOAD) {
							myGameInfo
									.setState(Constant.GAME_STATE_NOT_DOWNLOAD);
						}
						tipBarHandle(fileDownInfo2.getFileId(),
								DownloadTask.STATE_ON_DOWNLOAD_WAIT, 0);
					}
					break;
				case HANDLER_GAME_UPDATED_SUCCESS:
					mMyGameAdapter.notifyDataSetChanged();
					MyGameInfo myGameInfo1 = (MyGameInfo) msg.obj;
					upgradeDownloadHandle(myGameInfo1);
					break;
				case HANDLER_GAME_UPDATED_FAILED:
					tipBarHandle((String) msg.obj, HANDLER_GAME_UPDATED_FAILED,
							0);
					break;
				case HANDLER_GAME_START_UPDATE:
					tipBarHandle((String) msg.obj, HANDLER_GAME_START_UPDATE, 0);
					break;
				case HANDLER_PKG_INSTALLED_REMOVED:
					// String pkgName=(String)msg.obj;
					// DebugTool.debug(TAG,"pkg installed_removed:"+pkgName);
					// String gameId=(String)msg.obj;
					final MyGameInfo myGameInfoOp = (MyGameInfo) msg.obj;
					String pkgName = myGameInfoOp.getPackageName();
					for (int i = 0; i < mMyGameInfos.size(); i++) {
						myGameInfo = mMyGameInfos.get(i);
						if (myGameInfo.getPackageName().equals(pkgName)) {
							// if(myGameInfo.getGameId().equals(gameId)){
							if (msg.arg1 == DownloadTask.OP_INSTALL) {
								myGameInfo
										.setState(Constant.GAME_STATE_INSTALLED);
								myGameInfo.setLaunchAct(myGameInfoOp
										.getLaunchAct());
							} else if (msg.arg1 == DownloadTask.OP_UNINSTALL) {
								mMyGameInfos.remove(i);
								mMyGameAdapter.notifyDataSetChanged();
							}
							break;
						}
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void tipBarHandle(String gameId, int type, int msgArg1) {
		LinearLayout tipBar = (LinearLayout) mMyGameGv.findViewWithTag(gameId);
		if (tipBar == null) {
			return;
		}
		ImageView stateImg = (ImageView) tipBar.getTag(R.id.manage_iv_state);
		TextView tvProgress = (TextView) tipBar.getTag(R.id.manage_tv_progress);

		switch (type) {
		case DownloadTask.STATE_ON_DOWNLOAD_PROGRESS:
			// if(!tvProgress.isShown()){
			// tvProgress.setVisibility(View.VISIBLE);
			// }
			tvProgress.setText(msgArg1 + "%");
			break;

		case HANDLER_ON_ITEM_CLICK_TO_DOWN:
			stateImg.setImageResource(R.drawable.down_ing);
			tvProgress.setVisibility(View.GONE);
			tvProgress.setText("0%");
			break;

		case DownloadTask.STATE_ON_DOWNLOAD_START:
			stateImg.setImageResource(R.drawable.down_ing);
			tvProgress.setText("0%");
			tvProgress.setVisibility(View.VISIBLE);
			break;

		case DownloadTask.STATE_ON_DOWNLOAD_STOP:
			stateImg.setImageResource(R.drawable.down_pause);
			tvProgress.setVisibility(View.GONE);
			break;

		case DownloadTask.STATE_ON_DOWNLOAD_FINISH:
			tipBar.setVisibility(View.INVISIBLE);
			break;

		case DownloadTask.STATE_ON_DOWNLOAD_ERROR:
			stateImg.setImageResource(R.drawable.down_error);
			tvProgress.setVisibility(View.GONE);
			break;

		case DownloadTask.STATE_ON_DOWNLOAD_WAIT:
			stateImg.setImageResource(R.drawable.down_ing);
			tvProgress.setVisibility(View.GONE);
			// tvProgress.setText("0%");
			tipBar.setVisibility(View.VISIBLE);
			break;
		case HANDLER_GAME_START_UPDATE:
			stateImg.setImageResource(R.drawable.down_ing);
			tvProgress.setVisibility(View.GONE);
			tipBar.setVisibility(View.VISIBLE);
			break;
		case HANDLER_GAME_UPDATED_FAILED:
			tipBar.setVisibility(View.INVISIBLE);
			Toast.makeText(MyGameActivity.this, "获取最新下载地址失败，请检查网络连接", 1000)
					.show();
			break;
		}
	}

	// 下载信息Receiver
	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			String action = intent.getAction();
			FileDownInfo fileDownInfo = (FileDownInfo) intent
					.getSerializableExtra(DownloadTask.FILE_DOWN_INFO_KEY);
			Message msg = null;
			if (action.equals(DownloadTask.ACTION_ON_DOWNLOAD_PROGRESS)) {
				// 监听下载进度
				int percentage = (int) ((long) fileDownInfo.getDownLen() * 100 / (long) fileDownInfo
						.getFileSize());
				// DebugTool.info(TAG,
				// "percent:"+percentage+" downLen:"+fileDownInfo.getDownLen());
				if (percentage > 100 || percentage < 0) {
					percentage = 100;
				}
				msg = mHandler.obtainMessage(
						DownloadTask.STATE_ON_DOWNLOAD_PROGRESS, percentage, 0,
						fileDownInfo.getFileId());
			} else if (action.equals(DownloadTask.ACTION_ON_DOWNLOAD_START)) {
				// 下载开始
				msg = mHandler.obtainMessage(
						DownloadTask.STATE_ON_DOWNLOAD_START,
						fileDownInfo.getFileId());
			} else if (action.equals(DownloadTask.ACTION_ON_DOWNLOAD_FINISH)) {
				// 下载完成
				msg = mHandler.obtainMessage(
						DownloadTask.STATE_ON_DOWNLOAD_FINISH, fileDownInfo);
			} else if (action.equals(DownloadTask.ACTION_ON_DOWNLOAD_ERROR)) {
				// 下载发生错误
				msg = mHandler.obtainMessage(
						DownloadTask.STATE_ON_DOWNLOAD_ERROR,
						fileDownInfo.getFileId());
			} else if (action.equals(DownloadTask.ACTION_ON_DOWNLOAD_STOP)) {
				// 下载停止
				msg = mHandler.obtainMessage(
						DownloadTask.STATE_ON_DOWNLOAD_STOP,
						fileDownInfo.getFileId());
			} else if (action.equals(DownloadTask.ACTION_ON_DOWNLOAD_WAIT)) {
				msg = mHandler.obtainMessage(
						DownloadTask.STATE_ON_DOWNLOAD_WAIT, fileDownInfo);
			} else if (action.equals(DownloadTask.ACTION_ON_APP_INSTALLED)) {
				Bundle b = intent.getBundleExtra("data");
				int opType = b.getInt("opType");
				MyGameInfo myGameInfo = (MyGameInfo) b
						.getSerializable("myGameInfo");
				if (myGameInfo != null) {
					msg = mHandler.obtainMessage(HANDLER_PKG_INSTALLED_REMOVED,
							opType, 0, myGameInfo);
				} else {
					return;
				}
				// String packageName=b.getString("packageName");
				// String gameId=b.getString("gameId");
				// DebugTool.debug(TAG, "bundle packageName:"+packageName);
				// msg=mHandler.obtainMessage(HANDLER_PKG_INSTALLED_REMOVED,opType,0,packageName);
				// msg=mHandler.obtainMessage(HANDLER_PKG_INSTALLED_REMOVED,opType,0,gameId);
			} else {
				return;
			}
			mHandler.sendMessage(msg);
		}
	};

	private MyGameInfo getMyGameInfoById(String id) {
		MyGameInfo gameInfo = null;
		for (int i = 0; i < mMyGameInfos.size(); i++) {
			gameInfo = mMyGameInfos.get(i);
			if (gameInfo.getGameId().equals(id)) {
				return gameInfo;
			}
		}
		return null;
	}

	private void unZipApk(String zipfilePath, String destDir,
			final String apkDestPath) {
		final ZipUtils zip = new ZipUtils();
		zip.unZipApk(zipfilePath, destDir, apkDestPath,
				new ZipUtils.OnZipListener() {
					private CharSequence mMsg;
					private boolean isCancel = false;

					@Override
					public void onStart() {
						DebugTool.debug(TAG, "zip onStart()");
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									closeDialog();
									mProDialog = new CommonWaitDialog(
											MyGameActivity.this);
									mProDialog.setCanceledOnTouchOutside(false);
									// mProDialog.setTitle(MyGameActivity.this.getText(R.string.manage_zip_uncompressing));
									// mProDialog.setIcon(0);
									// mProDialog.setIndeterminateDrawable(null);
									// SpannableString ss1 = new
									// SpannableString(
									// MyGameActivity.this
									// .getText(R.string.manage_zip_uncompressing));
									// ss1.setSpan(new RelativeSizeSpan(0.8f),
									// 0,
									// ss1.length(), 0);
									// ss1.setSpan(new ForegroundColorSpan(
									// 0xFFC9C9C9), 0, ss1.length(), 0);
									mProDialog
											.setTitle(getIdToString(R.string.manage_zip_uncompressing));

									mMsg = MyGameActivity.this
											.getText(R.string.manage_zip_progress);
									mProDialog.setMessage(mMsg + "0%");
									mProDialog
											.setOnCancelListener(new DialogInterface.OnCancelListener() {
												@Override
												public void onCancel(
														DialogInterface dialog) {
													// TODO Auto-generated
													// method stub
													DebugTool.debug(TAG,
															"cancel unzip");
													mIsUnzipping = false;
													isCancel = true;
													zip.terminal();
												}
											});
									mProDialog
											.setOnDismissListener(new DialogInterface.OnDismissListener() {
												@Override
												public void onDismiss(
														DialogInterface dialog) {
													// TODO Auto-generated
													// method stub
													DebugTool.debug(TAG,
															"cancel unzip");
													isCancel = true;
													mIsUnzipping = false;
													zip.terminal();
												}
											});
									mProDialog.show();
								} catch (Exception e) {
									// TODO: handle exception
									DebugTool.error(TAG, e);
									isCancel = true;
									zip.terminal();
									showToastMsg(MyGameActivity.this
											.getString(R.string.manage_zip_init_error));
								}
							}
						});
					}

					@Override
					public void onProcess(final int progress) {
						// TODO Auto-generated method stub
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (mProDialog != null) {
									mProDialog.setMessage(mMsg + "" + progress
											+ "%");
								}
							}
						});
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						DebugTool.debug(TAG, "zip onFinish()");
						if (!isCancel
								&& DeviceTool.getCallState(MyGameActivity.this) == TelephonyManager.CALL_STATE_IDLE) {
							AppUtil.installApkByPath(MyGameActivity.this,
									apkDestPath);
						}
						closeDialog();
						mIsUnzipping = false;
					}

					@Override
					public void onError(Exception e, final int errId) {
						// TODO Auto-generated method stub
						DebugTool.debug(TAG, "zip onError()");
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								closeDialog();
								if (errId > 0) {
									showToastMsg(MyGameActivity.this
											.getString(errId));
								}
							}
						});
						mIsUnzipping = false;
					}
				});
	}

	private void closeDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		if (mProDialog != null && mProDialog.isShowing()) {
			mProDialog.dismiss();
			mProDialog = null;
		}
	}

	private void closeUnzipDialogIfFinish() {
		if (!mIsUnzipping && mProDialog != null && mProDialog.isShowing()) {
			mProDialog.dismiss();
			mProDialog = null;
		}
	}

	private void showReInstallDialog(final int index) {
		closeDialog();
		final MyGameInfo myGameInfo = mMyGameInfos.get(index);
		mDialog = new CommonCallbackDialog(this, R.style.TipDialog);
		mDialog.setListener(new MyDialogListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				switch (view.getId()) {
				case R.id.btnCancle:
					closeDialog();
					break;
				case R.id.btnSure:
					// 重新安装
					if (mIsUnzipping) {
						return;
					}
					myGameInfo.setState(Constant.GAME_STATE_NOT_INSTALLED);
					PersistentSynUtils.update(myGameInfo);
					// 界面没变化，无需更新
					// mMyGameAdapter.notifyDataSetChanged();

					mIsUnzipping = true;
					final File apkFile = new File(myGameInfo.getLocalDir(),
							myGameInfo.getLocalFilename());
					final String unApkPath = getLocalUnApkPath(myGameInfo);
					DebugTool.debug(TAG, "decompress...");
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							unZipApk(apkFile.getAbsolutePath(),
									Constant.GAME_ZIP_DATA_LOCAL_DIR, unApkPath);
						}
					}).start();

					closeDialog();
					break;
				}
			}
		});
		mDialog.setTitle(getString(R.string.down_title_tip));
		mDialog.setMessage(myGameInfo.getName()
				+ getText(R.string.down_msg_already_uninstall));
		mDialog.setCancle(getString(R.string.cancle1));
		mDialog.setSure(getString(R.string.down_btn_reinstall));
		mDialog.show();
	}

	private void showReDownloadDialog(final int index, final String msg) {
		closeDialog();
		final MyGameInfo myGameInfo = mMyGameInfos.get(index);
		mDialog = new CommonCallbackDialog(this, R.style.TipDialog);
		mDialog.setListener(new MyDialogListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				switch (view.getId()) {
				case R.id.btnCancle:
					// 取消
					closeDialog();
					break;
				case R.id.btnSure:
					// 重新下载
					myGameInfo.setState(Constant.GAME_STATE_NOT_DOWNLOAD);
					PersistentSynUtils.update(myGameInfo);
					mMyGameAdapter.notifyDataSetChanged();
					notDownloadHanlde(myGameInfo);
					closeDialog();
					break;
				}
			}
		});
		mDialog.setTitle(getString(R.string.down_title_tip));
		mDialog.setMessage(msg);
		mDialog.setCancle(getString(R.string.cancle1));
		mDialog.setSure(getString(R.string.down_btn_redownload));
		mDialog.show();
	}

	private void notDownloadHanlde(MyGameInfo myGameInfo) {
		if (!mDownTask.isDownloading(myGameInfo.getGameId())) {
			if (!NetUtil.isEnableDownload(MyGameActivity.this, true)) {
				return;
			}

			// 加入下载列表
			if (myGameInfo.getState() == Constant.GAME_STATE_DOWNLOAD_ERROR) {
				myGameInfo.setState(Constant.GAME_STATE_NOT_DOWNLOAD);
				PersistentSynUtils.update(myGameInfo);
			}
			DebugTool.info(
					TAG,
					myGameInfo.getName() + " downUrl:"
							+ myGameInfo.getDownUrl());
			startDownload(mDownTask, myGameInfo);
		} else {
			// 下载中，暂停
			mDownTask.setDownloadStop(myGameInfo.getGameId());
		}
	}

	/**
	 * 方法概述：添加到我的游戏列表并开始下载
	 * 
	 * @description：
	 * @param context
	 * @param gameInfo
	 * @throws
	 * @author: LiuQin
	 * @date 2013-5-22 下午1:36:13 修改记录： 修改者： 修改时间： 修改内容：
	 */
	public static boolean addToMyGameList(Context context, GameInfo gameInfo) {
		if (!NetUtil.isEnableDownload(context, true)) {
			return false;
		}

		MyGameInfo myGameInfo = getMyGameInfoFromGameInfo(context, gameInfo);
		if (myGameInfo == null) {
			Toast.makeText(context, R.string.manage_down_data_format_error,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		DownloadTask downTask = DownloadTask.getInstance(context);
		boolean isNeedToDownload = true;
		boolean isINMyGame = false;
		// Group<MyGameInfo>
		// infos=PersistentSynUtils.getModelList(MyGameInfo.class,
		// " gameId='"+gameInfo.getGameId()+"'");
		Group<MyGameInfo> infos = PersistentSynUtils.getModelList(
				MyGameInfo.class, " packageName='" + gameInfo.getPackageName()
						+ "'");
		if (infos == null || infos.size() < 1) {
			isINMyGame = false;
			long leftSize = DeviceTool.getSdAvailableSpace();
			long gameSize = 0;
			try {
				gameSize = gameInfo.getGameSize();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			DebugTool.info(TAG, "gameSize:" + gameSize + " leftSpace:"
					+ leftSize);
			if (gameSize > 0 && leftSize >= 0 && gameSize > leftSize) {
				Toast.makeText(context, R.string.manage_down_error_no_space,
						Toast.LENGTH_SHORT).show();
				return false;
			}

			long id = PersistentSynUtils.addModel(myGameInfo);
			myGameInfo.setId(id + "");
		} else {
			isINMyGame = true;
			if (infos.get(0).getState() == Constant.GAME_STATE_INSTALLED
					|| infos.get(0).getState() == Constant.GAME_STATE_NOT_INSTALLED
					|| infos.get(0).getState() == Constant.GAME_STATE_NEED_UPDATE) {
				Toast.makeText(
						context,
						myGameInfo.getName()
								+ context
										.getString(R.string.manage_down_installed),
						Toast.LENGTH_SHORT).show();
				isNeedToDownload = false;
			}
			myGameInfo = infos.get(0);
		}
		if (isNeedToDownload) {
			if (!downTask.isDownloading(myGameInfo.getGameId())) {
				// 加入下载列表
				FileDownInfo fileDownInfo = new FileDownInfo(
						myGameInfo.getGameId(), myGameInfo.getDownUrl(),
						myGameInfo.getLocalDir(), myGameInfo.getLocalFilename());
				fileDownInfo.setExtraData(myGameInfo.getName());
				fileDownInfo.setObject(myGameInfo);
				downTask.download(fileDownInfo);
				if (!isINMyGame) {
					Toast.makeText(
							context,
							myGameInfo.getName()
									+ context
											.getString(R.string.manage_down_addto_list),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							context,
							myGameInfo.getName()
									+ context
											.getString(R.string.manage_down_is_in_list),
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(
						context,
						myGameInfo.getName()
								+ context
										.getString(R.string.manage_down_downloading),
						Toast.LENGTH_SHORT).show();
			}
		}
		return true;
	}

	public static MyGameInfo getMyGameInfoFromGameInfo(Context context,
			GameInfo gameInfo) {
		MyGameInfo myGameInfo = null;
		String gameId = gameInfo.getGameId() + "";
		String packageName = gameInfo.getPackageName();
		String name = gameInfo.getGameName();
		String iconUrl = gameInfo.getMinPhoto();
		String downUrl = gameInfo.getFile();
		String localDir = Constant.GAME_DOWNLOAD_LOCAL_DIR;
		int versionCode = gameInfo.getVersionCode();
		String localFileName;
		if (MyGameActivity.IS_ONLY_ZIP
				|| downUrl.toLowerCase().endsWith(ZIP_EXT)) {
			localFileName = getLocalZipName(gameInfo);
		} else {
			localFileName = getLocalApkName(gameInfo);
		}
		if (!gameId.equals("") || downUrl.startsWith("http://")
				|| downUrl.startsWith("https://")) {
			myGameInfo = new MyGameInfo(gameId, packageName, name, iconUrl,
					downUrl, localDir, localFileName,
					Constant.GAME_STATE_NOT_DOWNLOAD, versionCode, "");
		}
		return myGameInfo;
	}

	public static String getLocalApkName(GameInfo info) {
		return info.getPackageName() + "_" + info.getGameId() + "_v"
				+ info.getVersionCode() + ".apk";
	}

	public static String getLocalApkName(MyGameInfo info) {
		return info.getPackageName() + "_" + info.getGameId() + "_v"
				+ info.getVersionCode() + ".apk";
	}

	public static String getLocalZipName(GameInfo info) {
		return info.getPackageName() + "_" + info.getGameId() + "_v"
				+ info.getVersionCode() + ZIP_EXT;
	}

	public static String getLocalZipName(MyGameInfo info) {
		return info.getPackageName() + "_" + info.getGameId() + "_v"
				+ info.getVersionCode() + ZIP_EXT;
	}

	public static String getLocalApkPath(GameInfo info) {
		return new File(Constant.GAME_DOWNLOAD_LOCAL_DIR, getLocalApkName(info))
				.getAbsolutePath();
	}

	public static String getLocalApkPath(MyGameInfo info) {
		return new File(info.getLocalDir(), info.getLocalFilename())
				.getAbsolutePath();
	}

	public static String getLocalUnApkPath(MyGameInfo info) {
		return new File(info.getLocalDir(), getLocalApkName(info))
				.getAbsolutePath();
	}

	protected void bindHeadRightButton(Integer iconDrawableResId) {
		ImageButton btnAddApp = (ImageButton) findViewById(R.id.btnHeadRight);
		btnAddApp.setOnClickListener(mOnAddAppClickListener);
		btnAddApp.setImageResource(iconDrawableResId);
		// 按钮的图片的id绑定到当前的控件，用于当点击公共的头布局进行区分
		btnAddApp.setTag(iconDrawableResId);
		btnAddApp.setVisibility(View.VISIBLE);
	}

	private OnClickListener mOnAddAppClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MyGameActivity.this,
					AddAppActivity.class);
			startActivity(intent);
		}
	};

	// for test
	// ////////////////////////////////////////////////////////
	private void addTestData() {
		// if(mMyGameInfos.size()==0){
		// MyGameInfo info;
		//
		// info=new
		// MyGameInfo("0001","com.k","拳皇","http://img.wdjimg.com/mms/icon/v1/9/15/0e5559997a51e0c52f4053dfe6aad159_78_78.png","http://apps.wandoujia.com/apps/com.ggee.vividruntime.gg_1403/download",Constant.GAME_DOWNLOAD_LOCAL_DIR,"test1.apk",0,0,"");
		// mMyGameInfos.add(info);
		//
		// info=new
		// MyGameInfo("0002","com.tiger.arcade.dino","恐龙快打","http://img.wdjimg.com/mms/icon/v1/8/5f/1db12aef26b7d25acffbee258679a5f8_78_78.png","http://apps.wandoujia.com/apps/com.tiger.arcade.dino/download",Constant.GAME_DOWNLOAD_LOCAL_DIR,"dino.zip",1,0,"");
		// DebugTool.info(TAG, "dino state:"+info.getState());
		// mMyGameInfos.add(info);
		//
		// info=new
		// MyGameInfo("0003","com.k","三国志","http://img.wdjimg.com/mms/icon/v1/0/7b/95ade768ee6317127435b2be2c8f77b0_78_78.png","http://apps.wandoujia.com/apps/com.tiger.arcade.wof/download",Constant.GAME_DOWNLOAD_LOCAL_DIR,"test3.apk",0,0,"");
		// mMyGameInfos.add(info);
		//
		// info=new
		// MyGameInfo("0004","com.k","超级玛丽","http://img.wdjimg.com/mms/icon/v1/5/a0/31a7de74431f79557ee38e1cec470a05_78_78.png","http://apps.wandoujia.com/apps/com.androidemu.harvemario.swnosigns/download",Constant.GAME_DOWNLOAD_LOCAL_DIR,"test4.apk",0,0,"");
		// mMyGameInfos.add(info);
		//
		// for(int i=0;i<8;i++){
		// info=new MyGameInfo("91", "com.android.settings", "设置", "", "", "",
		// "", 2,0,"");
		// mMyGameInfos.add(info);
		// info=new MyGameInfo("92", "com.android.music", "音乐", "", "", "", "",
		// 2,0,"");
		// mMyGameInfos.add(info);
		// info=new MyGameInfo("93", "com.android.email", "Email", "", "", "",
		// "", 2,0,"");
		// mMyGameInfos.add(info);
		// }
		//
		// long id;
		// for (MyGameInfo g: mMyGameInfos) {
		// id=PersistentSynUtils.addModel(g);
		// g.setId(id+"");
		// }
		//
		// // infos=PersistentSynUtils.getModelList(MyGameInfo.class, " id>0");
		// // if(infos!=null && infos.size()>0){
		// // mMyGameInfos=infos;
		// // }
		// }
	}

	private GameInfo makeGameInfo(CollectionInfo info) {
		GameInfo gInfo = new GameInfo();
		if (info == null) {
			return gInfo;
		}
		// gInfo.setImgs(info.getImgs());
		gInfo.setGameId(info.getGameId());
		gInfo.setGameName(info.getName());
		gInfo.setMaxPhoto(info.getIconMax());
		gInfo.setMiddlePhoto(info.getIconMiddle());
		gInfo.setMinPhoto(info.getIconMin());
		gInfo.setStartLevel(Double.parseDouble(info.getStarLevel()));
		gInfo.setGameDownCount(info.getDownCounts());
		gInfo.setGameSize(Integer.parseInt(info.getSize()));
		gInfo.setPackageName(info.getPackageName());
		gInfo.setMidDownAdress(info.getDownAddress());
		gInfo.setTypeId(info.getTypeId() + "");
		gInfo.setVersionCode(Integer.valueOf((int) info.getVersion()));
		gInfo.setRemark(info.getDescription());
		return gInfo;
	}
	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// // TODO Auto-generated method stub
	// menu.clear();
	// MenuItem menuitem = menu.add(0, 88, 0, "清除数据");
	// menuitem.setIcon(android.R.drawable.ic_menu_delete);
	//
	// menuitem = menu.add(0, 89, 0, "检查更新");
	// menuitem.setIcon(android.R.drawable.ic_menu_rotate);
	// return true;
	// }
	//
	// //for test
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // TODO Auto-generated method stub
	// if(item.getItemId()==88){
	// PersistentSynUtils.execDeleteData(MyGameInfo.class, " where id>0");
	// } else if(item.getItemId()==89){
	// // VersionManager vm=new VersionManager(this);
	// // vm.checkVersion(false);
	//
	// Report.getInstance().reportToServer("313");
	// }
	// return true;
	// }
	// ////////////////////////////////////////////////////////

}
