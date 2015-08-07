package com.sxhl.market.control.user.activity;

import java.io.File;
import java.io.FileInputStream;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sxhl.market.R;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.model.net.http.download.DownloadTask;
import com.sxhl.market.utils.NetUtil;
import com.sxhl.market.utils.StringTool;
import com.sxhl.market.view.CommonCallbackDialog;
import com.sxhl.market.view.CommonCallbackDialog.MyDialogListener;

public class SettingActivity extends BaseActivity {
	private static final int DIALOG = 1;
	private CheckBox chkWifi;
	private boolean isWifiChecked;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	RelativeLayout layout;
	// 自定义Dialog，用于注销的提示
	private CommonCallbackDialog mDialog;
	private TextView mTitle, mMessage;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);
		setHeadTitle("设置");
		goBack();

		preferences = getSharedPreferences("marketApp", MODE_PRIVATE);
		editor = preferences.edit();

		setWifiState();
		setClearBufferedState();
		initChkDelZip();
	}

	private void setWifiState() {
		chkWifi = (CheckBox) findViewById(R.id.chkWifi);
		chkWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					isWifiChecked = true;
				} else {
					isWifiChecked = false;

					if (!NetUtil.isWifiOpen(SettingActivity.this)) {
						// 禁止非wifi环境下载,如果有游戏正在下载，stop
						DownloadTask downTask = DownloadTask
								.getInstance(getApplicationContext());
						if (downTask.getDownloadingFileIds().size() > 0) {
							downTask.setAllDownloadStop();
						}
					}
				}
				editor.putBoolean("wifi", isWifiChecked);
				editor.commit();
			}
		});
		isWifiChecked = preferences.getBoolean("wifi", false);
		Log.i("info", "进入到界面时的 wifi的选中状态为：" + isWifiChecked);
		if (isWifiChecked) {
			chkWifi.setChecked(true);
		} else {
			chkWifi.setChecked(false);
		}
	}

	private void setClearBufferedState() {
		layout = (RelativeLayout) findViewById(R.id.layoutClearBuffer);
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
	}

	private void showDialog() {
		mDialog = new CommonCallbackDialog(this, R.style.TipDialog,
				new MyDialogListener() {
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						switch (view.getId()) {
						case R.id.btnCancle:
							mDialog.dismiss();
							break;
						case R.id.btnSure:
							clearCache();
							mDialog.dismiss();
							break;
						}
					}
				}, getIdToString(R.string.user_clear_cache_dialog_title),
				getIdToString(R.string.user_clear_cache_dialog_content));
		mDialog.setContentView(R.layout.layout_self_tip_dialog);
		mDialog.show();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				Bundle b = msg.getData();
				double size = b.getDouble("size");
				showToastMsg(getIdToString(R.string.user_clear_cache_success));
			} else if (msg.what == 0) {
				showToastMsg(getIdToString(R.string.user_clear_cache_fail));
			} else if (msg.what == 2) {
				showToastMsg(getIdToString(R.string.user_clear_cache_null));
			}

		}

	};

	private void clearCache() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
//					File cache = ImageCache
//							.getExternalCacheDir(SettingActivity.this);
//					if (cache != null) {
						// FileInputStream fis;
						// fis = new FileInputStream(cache);
						// long size = fis.available(); //这就是文件大小
						// fis.close();
//						DiskLruCache.deleteContents(cache);
						ImageLoader.getInstance().stop();
						ImageLoader.getInstance().clearDiskCache();

						Message msg = new Message();
						Bundle b = new Bundle();// 存放数据
						b.putDouble("size", 0);
						msg.setData(b);
						msg.what = 1;
						mHandler.sendMessage(msg);
//					} else {
//						mHandler.sendEmptyMessage(2);
//					}
				} catch (Exception e) {
					mHandler.sendEmptyMessage(0);
				}

			}
		}).start();
	}

	private void initChkDelZip() {
		CheckBox chkDelZip = (CheckBox) findViewById(R.id.chkDelZip);
		chkDelZip.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				editor.putBoolean("isDelZip", isChecked);
				editor.commit();
			}
		});

		boolean isDelZip = preferences.getBoolean("isDelZip", false);
		if (isDelZip) {
			chkDelZip.setChecked(true);
		} else {
			chkDelZip.setChecked(false);
		}
	}
}
