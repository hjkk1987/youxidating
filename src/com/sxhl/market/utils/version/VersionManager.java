package com.sxhl.market.utils.version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.MainTabActivity;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.utils.NetUtil;
import com.sxhl.market.view.CommonWaitDialog;

/**
 * @ClassName: VersionManager
 * @Description: 版本检查更新类
 * @author: Liuqin
 * @date 2012-12-18 下午2:43:23
 * 
 */
public class VersionManager {
	private static final String TAG = "VersionManager";
	// 获取新版本信息
	private static final int HANDLER_GET_LATEST_VERSION = 11;
	private static final int HANDLER_DOWNLOAD_PROGRESS = 12;
	private static final int HANDLER_DOWNLOAD_FINISH = 13;
	private static final int HANDLER_DOWNLOAD_ERROR = 14;
	private static final int HANDLER_DOWNLOAD_SIZE = 15;
	private static final int HANDLER_DOWNLOAD_STOP = 16;
	private static final int CODE_NO_RESPONDING_DATA = 1301;
	// 本地保存目录
	private String localDir = "/sdcard/sxhl/";
	// 本地保存文件名
	private static String localFileName = "update.apk";

	private Context context;
	// 最新版本信息
	private RespGetVersionInfo latestVerInfo;
	// 当前版本信息
	private PackageInfo currentVerInfo;
	// 进度条
	// private ProgressBar downloadBar;
	// 进度值
	// private TextView progressTv;
	// 对话框
	// private AlertDialog updateDialog;
	// 进度条是否设置了最大值
	// private boolean isSetMax = false;
	// 是否用户自行更新
	private boolean isSilent = true;
	// 是否取消更新
	private volatile boolean isCancel = false;
	private int fileSize;
	private Object lock = new Object();
	// private String verstionUrl;
	PreferencesHelper helper;
	private String softwareId;
	private String deviceCode;

	private String pkgName;

	private final String SP_KEY_FILE_SIZE = "updateFileSize";
	private final String SP_KEY_DOWNLOADED_SIZE = "updateDownSize";
	private final String SP_KEY_IS_WAIT_TO_NEXT_TIME = "isWaitToNextTime";
	private final String SP_KEY_LAST_CHECK_TIME = "lastCheckTime";
	private final String SP_KEY_VERSION_INFO_CACHE = "verionInfoCache";
	private final String PKGNAME_MARKET = "com.sxhl.market";
	private final String PKGNAME_GAMEPAD = "com.atet.tvgamepad";
	private static VersionUpdateCallback versionUpdateCallback;

	private AlertDialog isUpdateDialog;
	private CommonWaitDialog updateingDialog;
	private String downProgressStr;

	public interface VersionUpdateCallback {
		public void updateSuccess();

		public void updateFail();
	};

	public VersionManager(Context context, String deviceCode) {
		super();
		this.context = context;
		this.deviceCode = deviceCode;
		DebugTool.debug(TAG, "deviceCode=" + deviceCode);
		localDir = context.getFilesDir().getAbsolutePath() + "/";
		helper = new PreferencesHelper(context);
	}

	public void enableDebug(boolean flag) {
		Configuration.IS_DEBUG_ENABLE = flag;
	}

	/**
	 * 检查新版本
	 * 
	 * @param pkgName
	 *            检查的应用包名
	 * @param softId
	 *            应用在服务器中的id
	 * @param updateInterval
	 *            升级间隔时间
	 * @param isSilent
	 *            是否用户手动检查 true为自动，false为手动
	 * @throws
	 */
	public void checkVersion(String pkgName, String softId,
			long updateInterval, boolean isSilent) {
		long lastHintUpdateTime = context.getSharedPreferences("UpGradeTime",
				Activity.MODE_APPEND).getLong("lastHintUpdateTime", 0);
		long now = System.currentTimeMillis();
		// if (now - lastHintUpdateTime <= 24 * 60 * 60 * 1000) {
		// return;
		// }
		context.getSharedPreferences("UpGradeTime", Activity.MODE_APPEND)
				.edit().putLong("lastHintUpdateTime", now).commit();
		if (!NetUtil.isNetworkAvailable(context, true)) {
			// Toast.makeText(context,
			// context.getString(R.string.network_out_of_link), 2000)
			// .show();
			return;
		}

		if (pkgName == null || pkgName.length() <= 0 || softId == null
				|| softId.length() <= 0) {
			DebugTool.warn(TAG, "[checkVersion] pkgName or softId null");
			return;
		}
		this.pkgName = pkgName;
		this.softwareId = softId;
		this.latestVerInfo = null;
		this.currentVerInfo = null;
		currentVerInfo = getCurrentVersionInfo(pkgName);

		// 从服务器获取新版本信息
		getLatestVersion();

	}

	/**
	 * @Title: getLatestVersion
	 * @Description: 获取服务器最新版本信息
	 * @throws
	 */

	private void getLatestVersion() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DebugTool.debug("getLatestVersion()----------------");
					if (currentVerInfo == null) {
						currentVerInfo = getCurrentVersionInfo(pkgName);
					}
					if (currentVerInfo == null) {
						handler.sendEmptyMessage(HANDLER_GET_LATEST_VERSION);
						return;
					}
					localFileName = currentVerInfo.packageName + ".apk";
					httpGetVersionInfo(context);
					handler.sendEmptyMessage(HANDLER_GET_LATEST_VERSION);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	public void recycle() {
		try {
			isCancel = true;
			if (updateingDialog != null && updateingDialog.isShowing()) {
				updateingDialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * @Title: compareVersion
	 * @Description: 比较版本信息
	 * @throws
	 */
	private void compareVersion() {
		DebugTool.debug(TAG, "compareVersion()------------------");
		if (currentVerInfo == null) {
			currentVerInfo = getCurrentVersionInfo(pkgName);
		}
		DebugTool.debug(TAG, "currentVerInfo=" + currentVerInfo);
		if (latestVerInfo == null || currentVerInfo == null) {
			// 获取版本信息失败
			DebugTool.debug("error to get version");
			// Toast.makeText(context, R.string.update_fail_to_get_version_info,
			// Toast.LENGTH_SHORT).show();
			return;
		}
		if (latestVerInfo.getVersionCode() <= currentVerInfo.versionCode) {
			// 无需更新
			// Toast.makeText(context, R.string.update_already_new_version,
			// Toast.LENGTH_SHORT).show();
			File apkFile = new File(localDir, localFileName);
			if (apkFile.exists()) {
				DebugTool.debug("delete the apk file:"
						+ apkFile.getAbsolutePath());
				apkFile.delete();
			}
		} else {
			try {
				View layout = LayoutInflater.from(context).inflate(
						R.layout.dialog_is_update_market, null);
				TextView tvTitle = (TextView) layout
						.findViewById(R.id.tv_update_title);
				TextView tvMessage = (TextView) layout
						.findViewById(R.id.tv_update_content);
				Button btnSure = (Button) layout.findViewById(R.id.btn_sure);
				Button btnCancel = (Button) layout
						.findViewById(R.id.btn_cancel);
				tvTitle.setText(R.string.update_version_found_title);
				tvMessage.setText(latestVerInfo.getRemark());
				btnSure.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						updateVersion();
						isUpdateDialog.dismiss();

					}
				});
				btnCancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						isUpdateDialog.dismiss();
					}
				});
				isUpdateDialog = new AlertDialog.Builder(context).create();
				isUpdateDialog.setOnKeyListener(mOnDialogKeyListener);
				isUpdateDialog.show();
				isUpdateDialog.getWindow().setContentView(layout);

				// 有新版本
				// final AlertDialog.Builder builder = new AlertDialog.Builder(
				// context);
				// if (pkgName.equals(PKGNAME_MARKET)) {
				//
				// builder.setTitle(R.string.update_version_found_title);
				// }
				// builder.setMessage(latestVerInfo.getRemark());
				// builder.setPositiveButton(R.string.update_ok,
				// new OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				// updateVersion();
				// }
				// });
				// int negativeStr;
				//
				// negativeStr = R.string.update_cancel;
				// builder.setCancelable(true);
				//
				// builder.setNegativeButton(negativeStr, new OnClickListener()
				// {
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				//
				// }
				// });
				// builder.setOnKeyListener(mOnDialogKeyListener);
				// AlertDialog alertDialog = builder.create();
				// alertDialog.show();
				// AppUtil.setDialogAlpha(alertDialog,
				// Constant.DIALOG_BACKGROUND_ALPHA);
			} catch (Exception e) {
				DebugTool.error("error", e);
			}
		}
	}

	/**
	 * @Title: updateVersion
	 * @Description: 更新版本
	 * @throws
	 */
	private void updateVersion() {
		DebugTool.debug(TAG, "localDir:" + localDir);

		File apkFile = new File(localDir, localFileName);
		if (apkFile.exists()) {
			DebugTool.info(TAG, "[updateVersion] apk exist");
			String absPath = apkFile.getAbsolutePath();
			PackageInfo info = getPkgInfoByPath(context, absPath);
			if (info != null) {
				DebugTool.debug(TAG, "pkg code:" + info.versionCode
						+ " apk code:" + latestVerInfo.getVersionCode());
				if (info.applicationInfo.packageName.equals(pkgName)
						&& info.versionCode == latestVerInfo.getVersionCode()) {
					try {
						// 直接跳到安装界面
						// installApkByPath(context, absPath);
						installApk(context, pkgName, absPath);
					} catch (Exception e) {

						Toast.makeText(context, R.string.update_install_error,
								Toast.LENGTH_SHORT).show();
						apkFile.delete();
					}

					return;
				}
			} else {

				DebugTool.info(TAG, "[updateVersion] pkgInfo null");
				apkFile.delete();
			}
		}

		File dir = new File(localDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			boolean isExistIfCancel = false;

			final boolean isExistIfCancelFinal = isExistIfCancel;
			int btnStr = isExistIfCancelFinal ? R.string.update_cancel_update_and_exit
					: R.string.update_cancel_update;
			updateingDialog = new CommonWaitDialog(context);
			updateingDialog.setTitle(context
					.getString(R.string.update_updating));
			downProgressStr = context.getString(R.string.manage_zip_progress);
			updateingDialog.setMessage(downProgressStr + "0%");
			updateingDialog.setCanceledOnTouchOutside(false);
			updateingDialog.setOnKeyListener(mOnDialogKeyListener);
			updateingDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					isCancel = true;
				}
			});
			updateingDialog.show();

			// updateDialog = new AlertDialog.Builder(context)
			// .setTitle(R.string.update_updating)
			// .setView(addLayout())
			// .setCancelable(!isExistIfCancelFinal)
			// .setNegativeButton(btnStr, new OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// isCancel = true;
			//
			// }
			// })
			// .setOnCancelListener(
			// new DialogInterface.OnCancelListener() {
			// @Override
			// public void onCancel(DialogInterface dialog) {
			// // TODO Auto-generated method stub
			// isCancel = true;
			// }
			// }).create();
			// updateDialog.setCanceledOnTouchOutside(false);
			// updateDialog.setOnKeyListener(mOnDialogKeyListener);
			// updateDialog.show();
			// AppUtil.setDialogAlpha(updateDialog,
			// Constant.DIALOG_BACKGROUND_ALPHA);
			DebugTool.debug(TAG, "updateDialog     addLayout()");
			// 开始下载文件
			String url = latestVerInfo.getDownloadURL();
			DebugTool.debug(TAG, "isCancel=" + isCancel);
			downloadFile(url, apkFile);
		} catch (Exception e) {
		}
	}

	/**
	 * @Title: getCurrentVersionInfo
	 * @Description: 获取当前版本信息
	 * @return
	 * @throws
	 */
	private PackageInfo getCurrentVersionInfo(String pkgName) {
		PackageManager pm = context.getPackageManager();
		PackageInfo packInfo = null;
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			DebugTool.debug(TAG, "pkgName=" + pkgName);
			packInfo = pm.getPackageInfo(pkgName, 0);
			return packInfo;
		} catch (NameNotFoundException e) {
			packInfo = new PackageInfo();
			packInfo.packageName = pkgName.toString();
			packInfo.versionCode = -1;
			packInfo.versionName = "0";
		} catch (Exception e) {
		}
		return packInfo;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_GET_LATEST_VERSION:
				// 比较新版本与当前版本
				compareVersion();
				break;
			case HANDLER_DOWNLOAD_SIZE:
				// downloadBar.setMax(fileSize);
				DebugTool.debug(TAG, " HANDLER_DOWNLOAD_SIZE");
				break;
			case HANDLER_DOWNLOAD_PROGRESS:
				// 更新进度
				int downLen = msg.arg1;
				// progressTv.setText(((long) downLen * 100) / fileSize + "%");
				updateingDialog.setMessage(downProgressStr
						+ ((long) downLen * 100) / fileSize + "%");
				// downloadBar.setProgress(downLen);
				DebugTool.debug(TAG, "HANDLER_DOWNLOAD_PROGRESS");
				break;
			case HANDLER_DOWNLOAD_FINISH:
				// 下载完成,转到安装界面
				updateingDialog.dismiss();
				DebugTool.debug(TAG, "HANDLER_DOWNLOAD_FINISH");
				// installApkByPath(context, new File(localDir,
				// localFileName).getAbsolutePath());
				installApk(context, pkgName,
						new File(localDir, localFileName).getAbsolutePath());
				break;
			case HANDLER_DOWNLOAD_STOP:
				DebugTool.debug(TAG, "HANDLER_DOWNLOAD_STOP");
				if (updateingDialog.isShowing()) {
					updateingDialog.dismiss();
					DebugTool.debug(TAG, "updateDialog.dismiss()");
				}
				break;
			case HANDLER_DOWNLOAD_ERROR:
				// 下载发生错误
				DebugTool.debug(TAG, "HANDLER_DOWNLOAD_ERROR");
				String errMsg = (String) msg.obj;
				if (errMsg == null)
				// && errMsg.length() <= 0)
				{
					errMsg = context.getString(R.string.update_failed);
				}
				Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
				if (updateingDialog != null && updateingDialog.isShowing()) {
					updateingDialog.dismiss();
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * @Title: getApkInfoByPath
	 * @Description: 由路径得到app信息
	 * @param context
	 * @param absPath
	 * @return
	 * @throws
	 */
	public static ApplicationInfo getApkInfoByPath(Context context,
			String absPath) {
		ApplicationInfo appInfo = null;
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,
				PackageManager.GET_ACTIVITIES);
		if (pkgInfo != null) {
			appInfo = pkgInfo.applicationInfo;
			/* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
			appInfo.sourceDir = absPath;
			appInfo.publicSourceDir = absPath;
		}
		return appInfo;
	}

	/**
	 * @Title: getPkgInfoByName
	 * @Description: 由应用包名得到应用信息
	 * @param context
	 * @param pkgName
	 * @return
	 * @throws
	 */
	public static PackageInfo getPkgInfoByName(Context context, String pkgName) {
		PackageInfo pkgInfo = null;
		PackageManager pm = context.getPackageManager();
		try {
			// 0代表是获取版本信息
			pkgInfo = pm.getPackageInfo(pkgName, 0);
		} catch (Exception e) {
		}
		return pkgInfo;
	}

	public PackageInfo getPkgInfoByPath(Context context, String absPath) {
		PackageInfo pkgInfo = null;
		try {
			PackageManager pm = context.getPackageManager();
			pkgInfo = pm.getPackageArchiveInfo(absPath,
					PackageManager.GET_ACTIVITIES);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return pkgInfo;
	}

	/**
	 * @Title: installApkByPath
	 * @Description: 由apk路径直接跳到安装界面
	 * @param context
	 * @param absPath
	 * @throws
	 */
	public static void installApkByPath(Context context, String absPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + absPath),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	private void downloadFile(final String url, final File saveFile) {
		new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				InputStream inStream = null;
				FileOutputStream fos = null;
				HttpResponse response = null;
				int offset = 0;
				int sum = 0;
				final int N = 3;

				for (int i = 0; i < N; i++) {
					try {
						boolean isWifiOpen = NetUtil.isWifiOpen(context);
						response = httpGetResponse(url, !isWifiOpen, sum,
								fileSize);
						if (fileSize <= 0) {
							fileSize = httpGetFileLength(response);
							if (fileSize > 0) {
								handler.sendEmptyMessage(HANDLER_DOWNLOAD_SIZE);
							}
						}
						if (fileSize <= 0) {
							if (i + 1 >= N) {
								handler.sendEmptyMessage(HANDLER_DOWNLOAD_ERROR);
								return;
							} else {
								continue;
							}
						}
						inStream = httpGetInputStream(response);

						fos = context.openFileOutput(localFileName,
								Context.MODE_WORLD_READABLE);
						// fos = new FileOutputStream(saveFile);
						byte[] buffer = new byte[1024];

						while ((offset = inStream.read(buffer, 0, 1024)) != -1) {
							if (isCancel) {
								// 暂停退出
								handler.obtainMessage(HANDLER_DOWNLOAD_STOP)
										.sendToTarget();
								return;
							}
							fos.write(buffer, 0, offset);
							sum += offset;
							handler.obtainMessage(HANDLER_DOWNLOAD_PROGRESS,
									sum, 0).sendToTarget();
						}
						if (sum == fileSize) {
							handler.sendEmptyMessage(HANDLER_DOWNLOAD_FINISH);
						} else {
							handler.obtainMessage(
									HANDLER_DOWNLOAD_ERROR,
									context.getText(R.string.update_downloaded_file_error))
									.sendToTarget();
						}
						return;
					} catch (Exception e) {
						if (isCancel) {
							return;
						}
						e.printStackTrace();
						if (i + 1 >= N) {
							handler.obtainMessage(

									HANDLER_DOWNLOAD_ERROR,
									context.getText(R.string.update_downloading_error))
									.sendToTarget();
						} else {
							continue;
						}
					} finally {
						try {
							if (fos != null) {
								fos.close();
							}
							if (inStream != null) {
								inStream.close();
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}).start();
	}

	public InputStream httpGetInputStream(HttpResponse response)
			throws Exception {
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_PARTIAL_CONTENT
				|| statusCode == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return response.getEntity().getContent();
			} else {
				throw new Exception("Error to getEntity()");
			}
		} else {
			throw new Exception("status code is not 200 -- "
					+ response.getStatusLine().getStatusCode());
		}
	}

	@SuppressWarnings("deprecation")
	public HttpResponse httpGetResponse(String url, boolean isNotWifi,
			int startPos, int fileSize) throws Exception {
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, 60000);
		HttpConnectionParams.setSoTimeout(params, 60000);

		HttpClient httpclient = new DefaultHttpClient(params);
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		if (isNotWifi) {
			String host = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			if (host != null && port != -1) {
				HttpHost httpHost = new HttpHost(host, port);
				httpclient.getParams().setParameter(
						ConnRouteParams.DEFAULT_PROXY, httpHost);
			}
		}

		HttpGet get = new HttpGet(url);
		get.setHeader("User-Agent", "Mozilla/5.0 ( compatible ) ");
		get.setHeader("Charset", "UTF-8");
		get.setHeader("Accept-Language", "zh-CN");
		if (fileSize > 0 && fileSize > startPos) {
			int endPos = fileSize - 1;
			get.setHeader("Range", "bytes=" + startPos + "-" + endPos);
		}
		get.setHeader("Accept", "*/*");
		get.setHeader("Referer", url);
		get.setHeader("Connection", "close");
		return httpclient.execute(get);
	}

	public int httpGetFileLength(String url, boolean isNotWifi)
			throws Exception {
		HttpResponse response = httpGetResponse(url, isNotWifi, 0, 0);
		return httpGetFileLength(response);
	}

	public int httpGetFileLength(HttpResponse response) throws Exception {
		Header[] headers = response.getHeaders("Content-Length");
		return Integer.parseInt(headers[0].getValue());
	}

	public boolean isSilent() {
		synchronized (lock) {
			return isSilent;
		}
	}

	// public RelativeLayout addLayout() {
	// RelativeLayout relativeLayout = new RelativeLayout(context);
	// TextView textView = new TextView(context);
	// textView.setId(1);
	// textView.setTextSize(15);
	// textView.setTextColor(Color.WHITE);
	// textView.setText(R.string.update_downloading_msg);
	// RelativeLayout.LayoutParams layoutParams1 = new
	// RelativeLayout.LayoutParams(
	// ViewGroup.LayoutParams.WRAP_CONTENT,
	// ViewGroup.LayoutParams.WRAP_CONTENT);
	// layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	// layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
	// RelativeLayout.TRUE);
	// layoutParams1.setMargins(10, 10, 0, 5);
	// relativeLayout.addView(textView, layoutParams1);
	//
	// progressTv = new TextView(context);
	// progressTv.setId(2);
	// progressTv.setTextSize(15);
	// progressTv.setTextColor(Color.WHITE);
	// progressTv.setText("0%");
	// RelativeLayout.LayoutParams layoutParams2 = new
	// RelativeLayout.LayoutParams(
	// ViewGroup.LayoutParams.WRAP_CONTENT,
	// ViewGroup.LayoutParams.WRAP_CONTENT);
	// layoutParams2.addRule(RelativeLayout.RIGHT_OF, 1);
	// layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL,
	// RelativeLayout.TRUE);
	// layoutParams2.setMargins(10, 10, 0, 0);
	// relativeLayout.addView(progressTv, layoutParams2);
	// LayoutInflater inflater = LayoutInflater.from(context);
	// downloadBar = (ProgressBar) inflater.inflate(
	// R.layout.dialog_porgressbar, null);
	// downloadBar.setId(3);
	// downloadBar.setMax(100);
	// downloadBar.setProgress(0);
	// // downloadBar.setMinimumHeight(20);
	// // BeanUtil.setFieldValue(downloadBar, "mOnlyIndeterminate", new
	// // Boolean(
	// // false));
	// // downloadBar.setIndeterminate(false);
	// //
	// downloadBar.setProgressDrawable(BaseApplication.context.getResources().getDrawable(
	// // android.R.drawable.progress_horizontal));
	// // downloadBar.setIndeterminateDrawable(context.getResources()
	// // .getDrawable(
	// // android.R.drawable.progress_indeterminate_horizontal));
	// RelativeLayout.LayoutParams layoutParams3 = new
	// RelativeLayout.LayoutParams(
	// 250, LayoutParams.WRAP_CONTENT);
	// layoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL,
	// RelativeLayout.TRUE);
	// layoutParams3.addRule(RelativeLayout.BELOW, 1);
	// layoutParams3.setMargins(10, 5, 0, 2);
	// relativeLayout.addView(downloadBar, layoutParams3);
	// return relativeLayout;
	// }

	private boolean httpGetVersionInfo(Context context) {

		int retryTimes = 3;
		HttpReqParams param = new HttpReqParams();
		param.setAppId(softwareId);
		param.setDeviceId(deviceCode);
		while (retryTimes-- > 0) {

			// latestVerInfo = HttpApi.getObject(
			// UrlConstant.HTTP_GAME_GET_VERSION,
			// RespGetVersionInfo.class, param.toJsonParam()).getData();
			latestVerInfo = HttpApi.getObject(
					UrlConstant.HTTP_GAME_GET_VERSION1,
					UrlConstant.HTTP_GAME_GET_VERSION2,
					UrlConstant.HTTP_GAME_GET_VERSION3,
					RespGetVersionInfo.class, param.toJsonParam()).getData();
			if (latestVerInfo != null) {

				if (latestVerInfo.getCode() == 0) {
					return true;
				} else if (latestVerInfo.getCode() == CODE_NO_RESPONDING_DATA) {
					latestVerInfo = new RespGetVersionInfo();
					latestVerInfo.setVersionCode(-1);
					return true;
				} else {
					return false;
				}
			}

		}

		return false;
	}

	class PackageInstallObserver extends IPackageInstallObserver.Stub {
		public void packageInstalled(String packageName, final int returnCode) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (proDialog != null && proDialog.isShowing()) {
						proDialog.dismiss();
						proDialog = null;
					}
					// if (isCancel) {
					// return;
					// }
					if (returnCode == PackageManager.INSTALL_SUCCEEDED) {
						helper.setValue(SP_KEY_IS_WAIT_TO_NEXT_TIME
								+ softwareId, "false");
						helper.setValue(SP_KEY_VERSION_INFO_CACHE + softwareId,
								"");
						if (pkgName.equals(PKGNAME_GAMEPAD)) {
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									// toggleGamepadFromRemote(context, true);
								}
							}, 1200);
						}
						success(versionUpdateCallback);
						Toast.makeText(context, R.string.update_success,
								Toast.LENGTH_SHORT).show();
					} else {
						fail(versionUpdateCallback);
						Toast.makeText(context, R.string.update_failed,
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

		private String name;
		private ProgressDialog proDialog;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ProgressDialog getProDialog() {
			return proDialog;
		}

		public void setProDialog(ProgressDialog proDialog) {
			this.proDialog = proDialog;
		}
	}

	public void installApk2(final Context context, final String filePath,
			final String packageName, final String name,
			final ProgressDialog proDialog) {
		Uri mPackageURI = Uri.fromFile(new File(filePath));
		int installFlags = 0;
		PackageManager mPm = context.getPackageManager();
		try {
			PackageInfo pi = mPm.getPackageInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			if (pi != null) {
				installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
			}
		} catch (NameNotFoundException e) {
		} catch (Exception e) {
		}
		if ((installFlags & PackageManager.INSTALL_REPLACE_EXISTING) != 0) {
			DebugTool.warn(TAG, "[installApk2] Replacing package:"
					+ packageName);
		}

		PackageInstallObserver observer = new PackageInstallObserver();
		observer.setName(name);
		observer.setProDialog(proDialog);
		try {
			mPm.installPackage(mPackageURI, observer, installFlags, packageName);

		} catch (SecurityException e2) {
			installApkByPath(context, filePath);
		} catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
			Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT)
					.show();
			if (proDialog != null && proDialog.isShowing()) {
				proDialog.dismiss();
			}
		}
	}

	public void installApk(Context context, String pkgName, String absPath) {
		ProgressDialog mProDialog = null;
		if (pkgName != null && !pkgName.equals(context.getPackageName())) {
			mProDialog = new ProgressDialog(context);
			mProDialog.setCanceledOnTouchOutside(false);
			mProDialog.setCancelable(false);
			mProDialog.setIndeterminateDrawable(null);
			// SpannableString ss1 = new SpannableString("正在升级，请稍候...");
			// ss1.setSpan(new RelativeSizeSpan(0.8f), 0, ss1.length(), 0);
			// ss1.setSpan(new ForegroundColorSpan(0xFFC9C9C9), 0, ss1.length(),
			// 0);
			mProDialog.setMessage(context.getText(R.string.update_installing));
			mProDialog.show();
			// AppUtil.setProDialogFontSize(mProDialog,
			// Constant.DIALOG_MESSAGE_FONT_SIZE);
			// AppUtil.setDialogAlpha(mProDialog,
			// Constant.DIALOG_BACKGROUND_ALPHA);
		} else {
			try {
				Intent intent = new Intent(
						"com.atet.tclsettings.INSTALL_LAUNCHER_DIALOG");
				context.startService(intent);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		installApk2(context, absPath, pkgName, "", mProDialog);
	}

	private DialogInterface.OnKeyListener mOnDialogKeyListener = new DialogInterface.OnKeyListener() {
		@Override
		public boolean onKey(DialogInterface dialogInterface, int keyCode,
				KeyEvent event) {
			// TODO Auto-generated method stub
			View focusView = ((Dialog) dialogInterface).getCurrentFocus();
			if (focusView == null) {
				return false;
			}

			// if (GamepadTool.isButtonB(keyCode)) {
			// Dialog dialog = (Dialog) dialogInterface;
			// if (event.getAction() == KeyEvent.ACTION_UP && dialog != null
			// && dialog.isShowing()) {
			// dialog.dismiss();
			// }
			// return true;
			// } else if (GamepadTool.isButtonA(keyCode)) {
			// KeyEvent keyevent = new KeyEvent(event.getAction(),
			// KeyEvent.KEYCODE_ENTER);
			// focusView.dispatchKeyEvent(keyevent);
			// return true;
			// }
			// if (GamepadTool.isButtonXY(keyCode)) {
			// return true;
			// }
			return false;
		}
	};

	private static void success(VersionUpdateCallback callback) {
		if (callback != null) {
			callback.updateSuccess();
		}
	}

	private static void fail(VersionUpdateCallback callback) {
		if (callback != null) {
			callback.updateFail();
		}
	}
}
