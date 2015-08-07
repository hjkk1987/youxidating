package com.sxhl.market.control.manage.activity;

import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.common.activity.MainTabActivity;
import com.sxhl.market.control.manage.adapter.AddAppAdapter;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.model.net.http.download.DownloadTask;
import com.sxhl.market.model.net.http.download.FileDownInfo;
import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.view.CommonCallbackDialog;
import com.sxhl.market.view.CommonCallbackDialog.MyDialogListener;

public class AddAppActivity extends BaseActivity {
	private GridView mAppGridView;
	private LinearLayout mLinearLayoutProgress;
	private List<ResolveInfo> mShareAppInfos;
	private AddAppAdapter mAdapter;
	private PackageManager mPm;
	private CommonCallbackDialog mDialog;
	private Toast mToast;
	private int mActState = Constant.ACTIVITY_STATE_INIT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.manage_activity_layout_add_app);
		setHeadTitle(getString(R.string.manage_head_add_app_title));
		goBack();
		init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		closeDialog();
		mActState = Constant.ACTIVITY_STATE_DESTROY;
	}

	private void init() {
		mPm = getPackageManager();
		initViews();
		getInstalledApps();
	}

	private void initViews() {
		mAppGridView = (GridView) findViewById(R.id.manage_app_gv);
		mLinearLayoutProgress = (LinearLayout) findViewById(R.id.manage_linearlayout_progress);
	}

	private void getInstalledApps() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mShareAppInfos = queryAppInfo();
				Group<MyGameInfo> infos = PersistentSynUtils.getModelList(
						MyGameInfo.class, " id>0");

				if (infos != null) {
					final int addedSize = infos.size();
					StringBuffer app = new StringBuffer();
					StringBuffer pkg = new StringBuffer();
					app.append("|");
					pkg.append("|" + "com.sxhl.market" + "|");
					for (int i = 0; i < addedSize; i++) {
						app.append(infos.get(i).getLaunchAct() + "|");
						pkg.append(infos.get(i).getPackageName() + "|");
					}

					String addedString = app.toString();
					String pkgString = pkg.toString();
					String packageName;
					ResolveInfo resolveInfo;
					for (int i = 0; i < mShareAppInfos.size(); i++) {
						resolveInfo = mShareAppInfos.get(i);
						packageName = resolveInfo.activityInfo.packageName;
						if (packageName.startsWith("com.android.")) {
							mShareAppInfos.remove(i--);
						} else if (addedString.contains("|"
								+ resolveInfo.activityInfo.name + "|")) {
							mShareAppInfos.remove(i--);
						} else {
							if (!packageName.startsWith("com.android.")
									&& pkgString.contains("|" + packageName
											+ "|")) {
								mShareAppInfos.remove(i--);
							}
						}
						if (mActState == Constant.ACTIVITY_STATE_DESTROY) {
							return;
						}
					}
				}

				if (mActState == Constant.ACTIVITY_STATE_DESTROY) {
					return;
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							mLinearLayoutProgress.setVisibility(View.GONE);
							mAppGridView.setVisibility(View.VISIBLE);
							mAdapter = new AddAppAdapter(AddAppActivity.this);
							mAppGridView.setAdapter(mAdapter);
							mAppGridView
									.setOnItemClickListener(mOnItemClickListenner);
							mAdapter.updateDataSet(mShareAppInfos);

							if (mShareAppInfos.size() <= 0) {
								showToast(getString(R.string.manage_no_app_to_add));
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				});

			}
		}).start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
		}
	};

	private OnItemClickListener mOnItemClickListenner = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			showAddDialog(position);
		}
	};

	public List<ResolveInfo> queryAppInfo() {
		PackageManager pm = this.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_INTENT_FILTERS);
		// 调用系统排序 ， 根据name排序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		return resolveInfos;
	}

	private void closeDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	private void showAddDialog(final int position) {
		closeDialog();

		DebugTool.info(mShareAppInfos.get(position).activityInfo.name);
		final ResolveInfo appInfo = mShareAppInfos.get(position);
		final String appName = appInfo.activityInfo.loadLabel(mPm) + "";

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
					MyGameInfo myGameInfo = new MyGameInfo();
					myGameInfo.setPackageName(appInfo.activityInfo.packageName);
					myGameInfo.setLaunchAct(appInfo.activityInfo.name);
					myGameInfo.setName(appName);
					int flag = appInfo.activityInfo.applicationInfo.flags;
					if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0
							|| (flag & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
						myGameInfo
								.setState(Constant.GAME_STATE_INSTALLED_SYSTEM);
					} else {
						myGameInfo.setState(Constant.GAME_STATE_INSTALLED_USER);
					}
					long id = PersistentSynUtils.addModel(myGameInfo);
					if (id != -1) {
						myGameInfo.setId(id + "");
						myGameInfo.setGameId((-id) + "");
						PersistentSynUtils.update(myGameInfo);

						mShareAppInfos.remove(position);
						mAdapter.notifyDataSetChanged();

						FileDownInfo fileDownInfo = new FileDownInfo();
						fileDownInfo.setFileId(myGameInfo.getGameId());
						fileDownInfo.setObject(myGameInfo);
						Intent downIntent = new Intent(
								DownloadTask.ACTION_ON_DOWNLOAD_WAIT);
						downIntent.putExtra(DownloadTask.FILE_DOWN_INFO_KEY,
								fileDownInfo);
						sendBroadcast(downIntent);

						showToast(appName + " "
								+ getText(R.string.manage_add_success));
					} else {
						showToast(appName + " "
								+ getText(R.string.manage_add_fail));
					}

					closeDialog();
					break;
				}
			}
		});
		mDialog.setTitle(getText(R.string.manage_add_this_app) + "");
		mDialog.setMessage(appName);
		mDialog.show();
	}

	private void showToast(String msg) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(AddAppActivity.this, msg, Toast.LENGTH_SHORT);
		mToast.show();
	}
}
