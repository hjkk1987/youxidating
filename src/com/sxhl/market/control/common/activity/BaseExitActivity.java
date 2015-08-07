package com.sxhl.market.control.common.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;

import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.model.net.http.download.DownloadTask;
import com.sxhl.market.preferences.Preferences;
import com.sxhl.market.view.CommonCallbackDialog;
import com.sxhl.market.view.CommonCallbackDialog.MyDialogListener;

/**
 * @ClassName: BaseExitActivity.java
 * @Description: TODO
 * @author 吴绍东
 * @date 2013-2-20 下午3:07:36
 */
public class BaseExitActivity extends BaseActivity {
	int INTENT_ACTION_EXIT = 4001;
	// 自定义Dialog，用于注销的提示
	private CommonCallbackDialog mDialog;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == INTENT_ACTION_EXIT) {
				// Toast.makeText(getApplicationContext(), R.string.exit_tip,
				// Toast.LENGTH_SHORT).show();
				showExitDialog();
			}
		}
	};
	int count = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 监听返回按键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// showQuit();
			showExitDialog();
			return true;
		}
		return false;
	}

	public void showQuit() {// 退出确认
		new Thread() {
			public void run() {
				Looper.prepare();
				if (count < 1) {
					count++;
					Message message = new Message();
					message.what = INTENT_ACTION_EXIT;
					mHandler.sendMessage(message);
					SystemClock.sleep(2000);
					count--;
				} else {
					quitSysComplete();
				}
			}
		}.start();
	}

	public void quitSysComplete() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Preferences.setGameRecommendState(prefs.edit(), 0);
		Preferences.setGameHotState(prefs.edit(), 0);
		Preferences.setGameTopicsState(prefs.edit(), 0);
		sendBroadcast(new Intent(Constant.INTENT_ACTION_LOGGED_OUT));
		super.finish();
		// System.exit(0);

		// 如果有游戏正在下载，全部取消
		DownloadTask.getInstance(getApplicationContext()).recycle();
		taskManager.cancelAllTasks();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	protected void showExitDialog() {
		mDialog = new CommonCallbackDialog(this, R.style.TipDialog,
				new MyDialogListener() {
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						switch (view.getId()) {
						case R.id.btnCancle:
							// Toast.makeText(BaseExitActivity.this, "您取消了此次退出",
							// Toast.LENGTH_SHORT).show();
							mDialog.dismiss();
							break;
						case R.id.btnSure:
							// Toast.makeText(BaseExitActivity.this, "退出成功",
							// Toast.LENGTH_SHORT).show();
							quitSysComplete();

							mDialog.dismiss();
							break;
						}
					}
				}, getString(R.string.game_self_dialog_title),
				getString(R.string.game_self_dialog_msg));
		mDialog.setContentView(R.layout.layout_self_tip_dialog);
		mDialog.show();
	}
}
