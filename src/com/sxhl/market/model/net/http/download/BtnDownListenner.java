package com.sxhl.market.model.net.http.download;

import java.io.File;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.manage.activity.MyGameActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.DownloadGameInfo;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.utils.AppUtil;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.DeviceTool;
import com.sxhl.market.utils.ZipUtils;
import com.sxhl.market.view.CommonCallbackDialog;
import com.sxhl.market.view.CommonCallbackDialog.MyDialogListener;
import com.sxhl.market.view.CommonWaitDialog;
import com.sxhl.statistics.model.CollectGameInfo;
import com.sxhl.statistics.utils.GameCollectHelper;

/**
 * 实现的主要功能：button按钮的下载实现
 * 
 * @author: LiuQin
 * @date: 2013-5-29 下午5:23:20 修改记录： 修改者: 修改时间： 修改内容：
 */
public class BtnDownListenner {
	private static final String TAG = "DownloadBtnListenner";
	private static final int PKG_INSTALLED_REMOVED = 5;
	private Context mContext;
	private DownloadTask mDownTask;
	private Button mDownloadBtn;
	private GameInfo mGameInfo;
	private MyGameInfo mMyGameInfo;
	private boolean mIsDownloading = false;
	private CommonWaitDialog mProDialog;
	private boolean mIsUnzipping = false;
	private CommonCallbackDialog mDialog;

	public BtnDownListenner(Context context) {
		super();
		this.mContext = context;
	}

	/**
	 * 方法概述：
	 * 
	 * @description：
	 * @param btn
	 *            绑定的Button
	 * @param gameInfo
	 *            游戏信息
	 * @throws
	 * @author: LiuQin
	 * @date 2013-5-29 下午5:24:31 修改记录： 修改者： 修改时间： 修改内容：
	 */
	public void listen(Button btn, GameInfo gameInfo) {
		this.mDownloadBtn = btn;
		this.mGameInfo = gameInfo;
		initDownButton(mDownloadBtn);
		mDownloadBtn.setOnClickListener(downListener);
	}

	/**
	 * 方法概述：回收资源
	 * 
	 * @description：
	 * @throws
	 * @author: LiuQin
	 * @date 2013-5-29 下午5:26:42 修改记录： 修改者： 修改时间： 修改内容：
	 */
	public void recycle() {
		try {
			if (downloadReceiver != null) {
				mDownTask.unregBrocastReceiver(downloadReceiver);
				downloadReceiver = null;
			}
			closeDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: initDownButton
	 * @Description: 初始化下载按钮
	 * @throws
	 */
	private void initDownButton(Button downloadBtn) {
		mDownTask = DownloadTask.getInstance(mContext);
		setupDownButton(downloadBtn);
		if (downloadReceiver == null) {
			downloadReceiver = new DownloadReceiver();
			mDownTask.regBrocastReceiver(downloadReceiver);
		}
	}

	// private void initSearchOp(Button searchTip, GameInfo gameInfo) {
	// Integer ret = 0;
	// int installVersionCode = AppUtil.getInstalledAppVersionCode(mContext,
	// gameInfo.getPackageName());
	// if (installVersionCode == -1) {
	// searchTip.setText(R.string.down_btn_not_download_only);
	// ret = Constant.GAME_STATE_NOT_DOWNLOAD;
	// } else if (installVersionCode > gameInfo.getVersion()) {
	// searchTip.setText(R.string.down_btn_update);
	// ret = Constant.GAME_STATE_UPDATE;
	// } else {
	// searchTip.setText(R.string.down_btn_installed);
	// ret = Constant.GAME_STATE_INSTALLED;
	// }
	// }

	/**
	 * @Title: setupDownButton
	 * @Description: 设置下载按钮
	 * @throws
	 */
	private void setupDownButton(Button downloadBtn) {
		String id = mGameInfo.getGameId() + "";

		// for test
		// mMyGameInfo=new
		// MyGameInfo("1001","com.k","炸弹人","http://img.wdjimg.com/mms/icon/v1/9/86/6ee6228ef39b9da16bc9852be0834869_78_78.png","http://apps.wandoujia.com/apps/jp.hudson.android.bombermandojo/download","/sdcard/mydown","test1001.apk",0);
		// id=mMyGameInfo.getGameId();

		// 从我的游戏列表读取数据
		// Group<MyGameInfo> infos =
		// PersistentSynUtils.getModelList(MyGameInfo.class, " gameId='" + id +
		// "'");
		DebugTool
				.error("MyTest", "package:" + mGameInfo.getPackageName(), null);
		Group<MyGameInfo> infos = PersistentSynUtils.getModelList(
				MyGameInfo.class, " packageName='" + mGameInfo.getPackageName()
						+ "'");
		if (infos != null && infos.size() > 0) {
			mMyGameInfo = infos.get(0);
		}
		boolean isSet = false;
		downloadBtn.setText(R.string.down_btn_not_download);
		if (mMyGameInfo != null) {
			int state = mMyGameInfo.getState() & 0xff;
			if (state == Constant.GAME_STATE_NOT_INSTALLED) {
				// 已下载
				downloadBtn.setText(R.string.down_btn_not_install);
				isSet = true;
			} else if (state == Constant.GAME_STATE_INSTALLED
					|| state == Constant.GAME_STATE_NEED_UPDATE) {
				// 安装
				downloadBtn.setText(R.string.down_btn_installed);
				isSet = true;
			}
		}

		if (!isSet) {
			if (mDownTask.isDownloading(id)) {
				// 正在下载中
				int percent = mDownTask.getDownloadingPercentage(mGameInfo
						.getGameId() + "");
				if (percent >= 0) {
					downloadBtn.setText(percent + "%");
				} else {
					downloadBtn.setText(R.string.down_btn_wait_to_start);
				}
				mIsDownloading = true;
			}
		}
		if (mMyGameInfo == null) {
			mMyGameInfo = MyGameActivity.getMyGameInfoFromGameInfo(mContext,
					mGameInfo);
			if (AppUtil
					.isAlreadyInstall(mContext, mMyGameInfo.getPackageName())) {
				mMyGameInfo.setState(Constant.GAME_STATE_INSTALLED);
				downloadBtn.setText(R.string.down_btn_installed);
			}
		}
	}

	private OnClickListener downListener = new OnClickListener() {
		@Override 
		public void onClick(View v) {
			onDownButtonClick(mDownloadBtn);
		}
	};

	public void onDownButtonClick(Button downloadBtn) {
		if (mMyGameInfo == null) {
			Toast.makeText(mContext, R.string.manage_down_data_format_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		int state = mMyGameInfo.getState() & 0xff;
		if (state == Constant.GAME_STATE_NOT_DOWNLOAD
				|| state == Constant.GAME_STATE_DOWNLOAD_ERROR) {
			// 点击下载
			DownloadGameInfo downloadGame = new DownloadGameInfo();
			downloadGame.setGameId(mGameInfo.getGameId());
			downloadGame.setGameName(mGameInfo.getGameName());
			downloadGame.setPackageName(mGameInfo.getPackageName());
			downloadGame.setCpId(mGameInfo.getCpId());
			downloadGame.setTypeId(mGameInfo.getTypeId());
			PersistentSynUtils.addModel(downloadGame);

			notDownloadHanlde(downloadBtn);
		} else if (state == Constant.GAME_STATE_NOT_INSTALLED) {
			// 点击安装
			File apkFile = new File(mMyGameInfo.getLocalDir(),
					mMyGameInfo.getLocalFilename());
			if (apkFile.exists()) {
				// 文件已经下载，直接安装
				// AppUtil.installApkByPath(mContext,
				// apkFile.getAbsolutePath());
				unZipToInstall(apkFile);
			} else {
				// 文件已丢失
				showReDownloadDialog(
						downloadBtn,
						mContext,
						mMyGameInfo,
						mMyGameInfo.getName()
								+ mContext
										.getText(R.string.down_msg_file_already_delete));
			}
		} else if (state == Constant.GAME_STATE_INSTALLED
				|| state == Constant.GAME_STATE_NEED_UPDATE) {
			// 点击运行
			if (!AppUtil.startAppByPkgName(mContext,
					mMyGameInfo.getPackageName())) {
				String downUrl = mMyGameInfo.getDownUrl();
				if (downUrl == null || downUrl.equals("")) {
					// 如果不是从本市场下载的应用
					Toast.makeText(mContext, R.string.manage_warn_delete_only,
							Toast.LENGTH_SHORT).show();
				} else {
					// 游戏已卸载
					File zipFile = new File(mMyGameInfo.getLocalDir(),
							mMyGameInfo.getLocalFilename());
					if (zipFile != null && zipFile.exists()) {
						// 提示重新安装
						showReInstallDialog(downloadBtn, mContext, mMyGameInfo);
					} else {
						// 提示重新下载
						showReDownloadDialog(
								downloadBtn,
								mContext,
								mMyGameInfo,
								mMyGameInfo.getName()
										+ mContext
												.getText(R.string.down_msg_already_uninstall));
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void startDownload(Button downloadBtn) {
		downloadBtn.setText(R.string.down_btn_wait_to_start);
		// 加入下载列表
		FileDownInfo fileDownInfo = new FileDownInfo(mMyGameInfo.getGameId(),
				mMyGameInfo.getDownUrl(), mMyGameInfo.getLocalDir(),
				mMyGameInfo.getLocalFilename());
		fileDownInfo.setExtraData(mMyGameInfo.getName());
		fileDownInfo.setObject(mMyGameInfo);
		mDownTask.download(fileDownInfo);
	}

	private DownloadReceiver downloadReceiver = null;

	// 下载信息Receiver
	class DownloadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			String action = intent.getAction();
			if (action.equals(DownloadTask.ACTION_ON_APP_INSTALLED)) {
				Bundle b = intent.getBundleExtra("data");
				int opType = b.getInt("opType");
				MyGameInfo myGameInfo = (MyGameInfo) b
						.getSerializable("myGameInfo");
				if (myGameInfo != null) {
					mHandler.obtainMessage(PKG_INSTALLED_REMOVED, opType, 0,
							myGameInfo).sendToTarget();
				}
				// String packageName = b.getString("packageName");
				// DebugTool.debug(TAG, "bundle packageName:" + packageName);
				// mHandler.obtainMessage(PKG_INSTALLED_REMOVED, opType, 0,
				// packageName).sendToTarget();
				return;
			}

			Message msg = null;
			FileDownInfo fileDownInfo = (FileDownInfo) intent
					.getSerializableExtra(DownloadTask.FILE_DOWN_INFO_KEY);
			if (fileDownInfo == null
					|| mMyGameInfo == null
					|| !fileDownInfo.getFileId()
							.equals(mMyGameInfo.getGameId())) {
				return;
			}
			if (action.equals(DownloadTask.ACTION_ON_DOWNLOAD_PROGRESS)) {
				// 监听下载进度
				msg = mHandler.obtainMessage(
						DownloadTask.STATE_ON_DOWNLOAD_PROGRESS, fileDownInfo);
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
						DownloadTask.STATE_ON_DOWNLOAD_ERROR, fileDownInfo);
			} else if (action.equals(DownloadTask.ACTION_ON_DOWNLOAD_STOP)) {
				// 下载停止
				msg = mHandler.obtainMessage(
						DownloadTask.STATE_ON_DOWNLOAD_STOP,
						fileDownInfo.getFileId());
			} else {
				return;
			}
			mHandler.sendMessage(msg);
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			FileDownInfo fileDownInfo;

			switch (msg.what) {

			case DownloadTask.STATE_ON_DOWNLOAD_WAIT:
				mIsDownloading = false;
				mDownloadBtn.setText(R.string.down_btn_wait_to_start);
				break;

			case DownloadTask.STATE_ON_DOWNLOAD_START:
				mIsDownloading = true;
				mDownloadBtn.setText("0%");
				break;

			case DownloadTask.STATE_ON_DOWNLOAD_STOP:
				mIsDownloading = false;
				mDownloadBtn.setText(R.string.down_btn_pause);
				break;

			case DownloadTask.STATE_ON_DOWNLOAD_PROGRESS:
				// 更新进度
				if (!mIsDownloading) {
					return;
				}
				fileDownInfo = (FileDownInfo) msg.obj;
				if (fileDownInfo != null) {
					int percentage = (int) ((long) fileDownInfo.getDownLen() * 100 / (long) fileDownInfo
							.getFileSize());
					mDownloadBtn
							.setText(((percentage > 100 || percentage < 0) ? 100
									: percentage)
									+ "%");
				}
				break;

			case DownloadTask.STATE_ON_DOWNLOAD_FINISH:
				mIsDownloading = false;
				fileDownInfo = (FileDownInfo) msg.obj;
				MyGameInfo info = (MyGameInfo) fileDownInfo.getObject();
				if (info.getState() == Constant.GAME_STATE_NOT_INSTALLED) {
					mDownloadBtn.setText(R.string.down_btn_not_install);
					mMyGameInfo.setState(Constant.GAME_STATE_NOT_INSTALLED);
				} else {
					mDownloadBtn.setText(R.string.down_btn_download_fail);
					mMyGameInfo.setState(Constant.GAME_STATE_DOWNLOAD_ERROR);
				}
				break;

			case DownloadTask.STATE_ON_DOWNLOAD_ERROR:
				mIsDownloading = false;
				mDownloadBtn.setText(R.string.down_btn_download_fail);
				mMyGameInfo.setState(Constant.GAME_STATE_DOWNLOAD_ERROR);
				break;

			case PKG_INSTALLED_REMOVED:
				final MyGameInfo myGameInfoOp = (MyGameInfo) msg.obj;
				String pkgName = myGameInfoOp.getPackageName();
				// String pkgName = (String) msg.obj;
				if (mMyGameInfo.getPackageName().equals(pkgName)) {
					if (msg.arg1 == DownloadTask.OP_INSTALL) {
						mMyGameInfo.setState(Constant.GAME_STATE_INSTALLED);
						mMyGameInfo.setLaunchAct(myGameInfoOp.getLaunchAct());
						mDownloadBtn.setText(R.string.down_btn_installed);
					} else if (msg.arg1 == DownloadTask.OP_UNINSTALL) {
						mMyGameInfo.setState(Constant.GAME_STATE_NOT_DOWNLOAD);
						mDownloadBtn.setText(R.string.down_btn_not_download);
					}
				}
				break;
			default:
				break;
			}
		}
	};

	private void unZipToInstall(final File apkFile) {
		if (apkFile.getName().toLowerCase().endsWith(MyGameActivity.ZIP_EXT)) {
			if (mIsUnzipping) {
				return;
			}
			mIsUnzipping = true;
			final String unApkPath = MyGameActivity
					.getLocalUnApkPath(mMyGameInfo);
			DebugTool.debug(TAG, "decompress...");
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					unZipApk(apkFile.getAbsolutePath(),
							Constant.GAME_ZIP_DATA_LOCAL_DIR, unApkPath);
				}
			}).start();
		} else {
			// 文件已经下载，直接安装
			AppUtil.installApkByPath(mContext, apkFile.getAbsolutePath());
		}
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
									mProDialog = new CommonWaitDialog(mContext);
									mProDialog.setCanceledOnTouchOutside(false);
									// mProDialog.setTitle(mContext.getText(R.string.manage_zip_uncompressing));

									// mProDialog.setIcon(0);
//									mProDialog.setIndeterminateDrawable(null);
									// SpannableString ss1 = new
									// SpannableString(
									// mContext.getText(R.string.manage_zip_uncompressing));
									// ss1.setSpan(new RelativeSizeSpan(0.8f),
									// 0,
									// ss1.length(), 0);
									// ss1.setSpan(new ForegroundColorSpan(
									// 0xFFC9C9C9), 0, ss1.length(), 0);
									mProDialog.setTitle(mContext.getText(R.string.manage_zip_uncompressing)+"");

									mMsg = mContext
											.getText(R.string.manage_zip_progress);
									mProDialog.setMessage(mMsg + "0%");
									mProDialog
											.setOnCancelListener(new DialogInterface.OnCancelListener() {
												@Override
												public void onCancel(
														DialogInterface dialog) {
													// TODO Auto-generated
													// method
													// stub
													DebugTool.debug(TAG,
															"cancel unzip");
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
													zip.terminal();
												}
											});
									mProDialog.show();
								} catch (Exception e) {
									// TODO: handle exception
									DebugTool.error(TAG, e);
									isCancel = true;
									zip.terminal();
									Toast.makeText(mContext,
											R.string.manage_zip_init_error,
											Toast.LENGTH_SHORT).show();
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
								&& DeviceTool.getCallState(mContext) == TelephonyManager.CALL_STATE_IDLE) {
							AppUtil.installApkByPath(mContext, apkDestPath);
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
									Toast.makeText(mContext, errId,
											Toast.LENGTH_SHORT).show();
								}
							}
						});
						mIsUnzipping = false;
					}
				});
	}

	public void closeDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
		if (mProDialog != null && mProDialog.isShowing()) {
			mProDialog.dismiss();
			mProDialog = null;
		}
	}

	public void closeUnzipDialogIfFinish() {
		if (!mIsUnzipping && mProDialog != null && mProDialog.isShowing()) {
			mProDialog.dismiss();
			mProDialog = null;
		}
	}

	private void showReInstallDialog(final Button downloadBtn, Context context,
			final MyGameInfo myGameInfo) {
		closeDialog();
		mDialog = new CommonCallbackDialog(context, R.style.TipDialog);
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
					downloadBtn.setText(R.string.down_btn_not_install);

					mIsUnzipping = true;
					final File apkFile = new File(myGameInfo.getLocalDir(),
							myGameInfo.getLocalFilename());
					final String unApkPath = MyGameActivity
							.getLocalUnApkPath(myGameInfo);
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
		mDialog.setTitle(context.getString(R.string.down_title_tip));
		mDialog.setMessage(myGameInfo.getName()
				+ context.getText(R.string.down_msg_already_uninstall));
		mDialog.setCancle(context.getString(R.string.cancle1));
		mDialog.setSure(context.getString(R.string.down_btn_reinstall));
		mDialog.show();
	}

	private void showReDownloadDialog(final Button downloadBtn,
			Context context, final MyGameInfo myGameInfo, final String msg) {
		closeDialog();
		mDialog = new CommonCallbackDialog(context, R.style.TipDialog);
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
					downloadBtn.setText(R.string.down_btn_not_download);
					notDownloadHanlde(downloadBtn);
					closeDialog();
					break;
				}
			}
		});
		mDialog.setTitle(context.getString(R.string.down_title_tip));
		mDialog.setMessage(msg);
		mDialog.setCancle(context.getString(R.string.cancle1));
		mDialog.setSure(context.getString(R.string.down_btn_redownload));
		mDialog.show();
	}

	private void notDownloadHanlde(Button downloadBtn) {
		if (!mDownTask.isDownloading(mMyGameInfo.getGameId())) {
			if (mMyGameInfo.getState() == Constant.GAME_STATE_DOWNLOAD_ERROR) {
				mMyGameInfo.setState(Constant.GAME_STATE_NOT_DOWNLOAD);
				PersistentSynUtils.update(mMyGameInfo);
			}

			if (MyGameActivity.addToMyGameList(mContext, mGameInfo)) {
				downloadBtn.setText(R.string.down_btn_wait_to_start);
			}
		} else {
			// 下载中，暂停
			mDownTask.setDownloadStop(mMyGameInfo.getGameId());
		}
	}
}
