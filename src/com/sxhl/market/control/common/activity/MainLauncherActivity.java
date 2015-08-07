package com.sxhl.market.control.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.model.entity.DeviceInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskManager;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.utils.CodeToastUtil;
import com.sxhl.market.utils.NetUtil;
import com.sxhl.market.view.CommonCallbackDialog;
import com.sxhl.market.view.CommonCallbackDialog.MyDialogListener;
import com.sxhl.market.view.costom.GameDetailAndComments;
import com.sxhl.statistics.utils.DemoGLSurfaceView;

/**
 * @author yindangchao
 * @date 2014/10/21 10:52
 * @discription  主入口activity
 */
@SuppressLint("NewApi")
public class MainLauncherActivity extends Activity {

	private TaskManager taskManager = new TaskManager(this);
	boolean isAlreadyGetDeviceId = false;
	SharedPreferences preferences;
	CommonCallbackDialog mDialog;
	AlertDialog hintDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display mDisplay = getWindowManager().getDefaultDisplay();
		int W = mDisplay.getWidth();
		int H = mDisplay.getHeight();
		if (W > 0 && H > 0 && W <= 450 && H <= 750) {
			hintDialog = new AlertDialog.Builder(this)
					.setTitle(getString(R.string.kindly_reminder))
					.setMessage(getString(R.string.not_support_display))
					.setCancelable(false)
					.setNegativeButton(
							getString(R.string.not_support_display_goback),
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									hintDialog.dismiss();
									MainLauncherActivity.this.finish();
								}
							}).create();
			hintDialog.setCanceledOnTouchOutside(false);
			hintDialog.show();
			return;
		}
		if (!getSharedPreferences("BOOT_INFO", MODE_PRIVATE).getBoolean(
				"isUpdated", false)) {
			GLSurfaceView gl = new DemoGLSurfaceView(this);
			LinearLayout lin = new LinearLayout(this);
			lin.addView(gl);
			gl.setLayoutParams(new android.widget.LinearLayout.LayoutParams(2,
					2, 0));
			setContentView(lin);

		}

		preferences = getSharedPreferences(Constant.DEVICE_INFO_SP, MODE_APPEND);
		getDeviceInfoFromDatabase();
		/*setContentView(R.layout.test_view);
		GameDetailAndComments item = (GameDetailAndComments)findViewById(R.id.item);
		item.getGameDescriptionName().setText("愤怒的消极 ");
		item.getTvDescription().setText("客户端健康度好空间斯科拉答上来；第三大矿机反抗三风口浪尖个");*/
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 从本地获取deviceid，若是取不到则从网络获取
	 */
	private void getDeviceInfoFromDatabase() {

		isAlreadyGetDeviceId = preferences.getBoolean(
				Constant.IS_ALREADY_GET_DEVICE_ID, false);
		if (isAlreadyGetDeviceId) {
			DeviceInfo deviceInfo = new DeviceInfo();
			deviceInfo.setChannelId(preferences.getString(
					Constant.DEVICE_CHANNEL_ID, "0"));
			deviceInfo.setDeviceId(preferences.getString(
					Constant.DEVICE_DEVICE_ID, ""));
			deviceInfo.setType(preferences.getInt(Constant.DEVICE_TYPE, 1));
			BaseApplication.deviceInfo = deviceInfo;
			Intent in = new Intent(this, MainTabActivity.class);
			in.putExtra("launcher", 2);
			startActivity(in);
			finish();
		} else {
			// Toast.makeText(this, "初始化程序", 2 * 1000).show();
			loadDeviceIdTask();

		}

	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 异步网络获取deviceid
	 */
	AsynTaskListener<DeviceInfo> getDeviceIdTaskListener = new AsynTaskListener<DeviceInfo>() {

		@Override
		public boolean preExecute(BaseTask<DeviceInfo> task, Integer taskKey) {
			return true;
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<DeviceInfo> result) {
			if (CodeToastUtil.ToastByCode(BaseApplication.m_appContext,
					result.getCode())) {
			}
			if (result.getCode() == TaskResult.DEVICE_ID_NO_DATA) {
				// 无匹配数据
				// Toast.makeText(MainLauncherActivity.this,
				// getString(R.string.get_deviceid_nodata), 2000).show();
				showQuitDialog();
			}
			if (result.getCode() == TaskResult.FAILED) {
				// Toast.makeText(MainLauncherActivity.this,
				// getString(R.string.get_deviceid_fail), 2000).show();
				showQuitDialog();
			} else if (result.getCode() == TaskResult.OK) {
				// 成功获取数据
				// Toast.makeText(MainLauncherActivity.this,
				// getString(R.string.get_deviceid_success), 2000).show();
				final DeviceInfo deviceInfo = result.getData();
				new Thread(new Runnable() {

					@Override
					public void run() {
						saveToSp(deviceInfo);
						// PersistentSynUtils.addModel(deviceInfo);// 将数据插入数据库
						BaseApplication.deviceInfo = deviceInfo;
					}
				}).start();
				// if
				// (DeviceStatisticsUtils.fetchAtetId(MainLauncherActivity.this))
				// {
				Intent in = new Intent(MainLauncherActivity.this,
						MainTabActivity.class);
				in.putExtra("launcher", 2);
				startActivity(in);
				MainLauncherActivity.this.finish();
				// }

			}

		}

		public TaskResult<DeviceInfo> doTaskInBackground(Integer taskKey) {
			HttpReqParams params = new HttpReqParams();
			params.setDeviceCode(BaseApplication.mDeviceCode);
			// params.setDeviceCode("demo_phone_market");
			params.setProductId(BaseApplication.mDeviceId);
			params.setChannelId("0");
			params.setType(2);

			// return HttpApi.getObject(UrlConstant.HTTP_GET_DEVICE_ID,
			// DeviceInfo.class, params.toJsonParam());
			return HttpApi.getObject(UrlConstant.HTTP_GET_DEVICE_ID1,
					UrlConstant.HTTP_GET_DEVICE_ID2,
					UrlConstant.HTTP_GET_DEVICE_ID3, DeviceInfo.class,
					params.toJsonParam());
		}
	};

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 启动 获取设备id的异步进程
	 * */
	public void loadDeviceIdTask() {
		if (NetUtil.isNetworkAvailable(this, true)) {
			// 网络可用
			taskManager.cancelTask(Constant.TASKKEY_GET_DEVICE_ID);
			taskManager.startTask(getDeviceIdTaskListener,
					Constant.TASKKEY_GET_DEVICE_ID);
		} else {
			// 网络 不可用
			// showDialog();
			Toast.makeText(this, getString(R.string.network_out_of_link), 2000)
					.show();
			showQuitDialog();
		}
	}

	// @Override
	// protected void onDestroy() {
	// // TODO Auto-generated method stub
	// taskManager.cancelTask(Constant.TASKKEY_GET_DEVICE_ID);
	// super.onDestroy();
	// }

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 需要处理
			return true;// 屏蔽back键(不响应back键)
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 将deviceinfo保存到本地sp文件中
	 */
	private void saveToSp(DeviceInfo deviceInfo) {
		try {
			Editor editor = preferences.edit();
			if (!(deviceInfo.getChannelId() == null || "".equals(deviceInfo
					.getChannelId()))) {
				editor.putString(Constant.DEVICE_CHANNEL_ID,
						deviceInfo.getChannelId());
			}
			if (!(deviceInfo.getDeviceId() == null || "".equals(deviceInfo
					.getDeviceId()))) {
				editor.putString(Constant.DEVICE_DEVICE_ID,
						deviceInfo.getDeviceId());
			}
			if (!(deviceInfo.getType() == null || "".equals(deviceInfo
					.getType()))) {
				editor.putInt(Constant.DEVICE_TYPE, deviceInfo.getType());
			}
			editor.putBoolean(Constant.IS_ALREADY_GET_DEVICE_ID, true);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 获取失败提示退出对话框
	 */
	private void showQuitDialog() {
		// if (this.isDestroyed()) {
		// return;
		// }
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
							MainLauncherActivity.this.finish();
							break;
						case R.id.btnSure:
							// Toast.makeText(BaseExitActivity.this, "退出成功",
							// Toast.LENGTH_SHORT).show();
							mDialog.dismiss();
							loadDeviceIdTask();
						}
					}
				}, getString(R.string.connect_failed),
				getString(R.string.is_reconnect));
		LayoutInflater inf = getLayoutInflater();
		View v = inf.inflate(R.layout.layout_self_tip_dialog, null);
		Button btn = (Button) v.findViewById(R.id.btnCancle);
		btn.setText("退出");
		mDialog.setContentView(v);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}

}
