package com.sxhl.market.control.user.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.preferences.PreferencesHelper;
import com.sxhl.market.utils.AESCryptoUtils;
import com.sxhl.market.utils.MD5CryptoUtils;

public class ChangePasswordActivity extends BaseActivity {

	private EditText et_oldPassword;
	private EditText et_newPassword;
	private EditText et_surePassword;
	private Button btn_ok;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		setHeadTitle(getIdToString(R.string.user_change_password));
		goBack();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		et_oldPassword = (EditText) findViewById(R.id.et_user_old_psd);
		et_newPassword = (EditText) findViewById(R.id.et_user_new_psd);
		et_surePassword = (EditText) findViewById(R.id.et_sure_psd);
		btn_ok = (Button) findViewById(R.id.btn_changepassword);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!checkInputParams())
					return;

				updateUserInfo();

				// startProgressBar(getString(R.string.tip),
				// getString(R.string.modifying), 6,null);
			}
		});
	}

	private boolean checkInputParams() {
		if (et_oldPassword.getText().toString().equals("")) {
			showToastMsg(getString(R.string.password_not_null));
			return false;
		}
		if (et_newPassword.getText().toString().equals("")) {
			showToastMsg(getString(R.string.newpassword_cannot_null));
			return false;
		}
		if (et_surePassword.getText().toString().equals("")) {
			showToastMsg(getString(R.string.surepassword_cannot_null));
			return false;
		}
		if (et_oldPassword.getText().toString().length() < 5) {
			showToastMsg(getString(R.string.password_cannot_longger_than_eight));
			return false;
		}
		if ((et_oldPassword.getText().toString().length() > 16)) {
			showToastMsg(getString(R.string.password_cannot_longger_than_sixteen));
			return false;
		}

		if (et_newPassword.getText().toString().length() < 5) {
			showToastMsg(getString(R.string.newpassword_cannot_longger_than_eight));
			return false;
		}
		if ((et_newPassword.getText().toString().length() > 16)) {
			showToastMsg(getString(R.string.newpassword_cannot_longger_than_sixteen));
			return false;
		}
		if (et_surePassword.getText().toString().length() < 5) {
			showToastMsg(getString(R.string.surepassword_cannot_longger_than_eight));
			return false;
		}
		if ((et_surePassword.getText().toString().length() > 16)) {
			showToastMsg(getString(R.string.surepassword_cannot_longger_than_sixteen));
			return false;
		}

		if (!et_surePassword.getText().toString()
				.equals(et_newPassword.getText().toString())) {
			showToastMsg(getString(R.string.two_pwd_is_not_the_same));
			return false;
		}

		return true;
	}

	private String chiperPwd(String text) {
		try {
			return AESCryptoUtils.encrypt(
					com.atet.lib_atet_account_system.params.Constant.AES_SEED,
					text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private String md5String(String text) {
		try {
			return MD5CryptoUtils.toMD5(text.getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	// 向服务器提交更新结果
	private void updateUserInfo() {

		taskManager.cancelTask(Constant.USER_UPDATE_PWD);
		taskManager.startTask(userInfoListener, Constant.USER_UPDATE_PWD);
	}

	private void updateUserInfoRetry() {
		taskManager.cancelTask(Constant.USER_UPDATE_PWD_RETRY);
		taskManager.startTask(userInfoListener, Constant.USER_UPDATE_PWD_RETRY);
	}

	private void updateUserInfoRetry1() {
		taskManager.cancelTask(Constant.USER_UPDATE_PWD_RETRY1);
		taskManager
				.startTask(userInfoListener, Constant.USER_UPDATE_PWD_RETRY1);
	}

	String filePath = "";
	AsynTaskListener<UserInfo> userInfoListener = new AsynTaskListener<UserInfo>() {
		int mTaskKey;

		@Override
		public boolean preExecute(BaseTask<UserInfo> task, Integer taskKey) {
			startProgressBar(getString(R.string.tip),
					getString(R.string.submiting),
					Constant.TASK_MODIFY_PASSWORD, null);

			this.mTaskKey = taskKey;
			return true;
		}

		@Override
		public TaskResult<UserInfo> doTaskInBackground(Integer taskKey) {
			PreferencesHelper preferencesHelper = new PreferencesHelper(
					ChangePasswordActivity.this);

			UserInfo userInfo = BaseApplication.getLoginUser();
			HttpReqParams params = new HttpReqParams();
			params.setUserId(userInfo.getUserId());
			params.setLoginName(userInfo.getUserName());
			params.setNickName(userInfo.getNickName());
			params.setEmail(userInfo.getEmail());
			params.setPhone(userInfo.getPhone());
			params.setOldPassword(md5String(et_oldPassword.getText().toString()));

			params.setNewPassword(md5String(et_newPassword.getText().toString()));
			params.setConfirmPassword(md5String(et_surePassword.getText()
					.toString()));

			// params.setAvator(filePath);
			filePath = userInfo.getAvator();
			String fileName = "";
			if (filePath != null && !filePath.equals("")) {
				fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
				params.setIcon(fileName);
			}

			String url = UrlConstant.HTTP_USER_MODIFY1;

			if (taskKey == Constant.USER_UPDATE_PWD_RETRY)
				url = UrlConstant.HTTP_USER_MODIFY2;
			else if (taskKey == Constant.USER_UPDATE_PWD_RETRY1)
				url = UrlConstant.HTTP_USER_MODIFY3;

			return HttpApi.getObject(url, UserInfo.class, params.toJsonParam());
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<UserInfo> result) {
			TaskResult<UserInfo> info = result;
			int resultCode = info.getCode();
			UserInfo userInfo = info.getData();
			if (resultCode == TaskResult.OK) {
				PreferencesHelper preferencesHelper = new PreferencesHelper(
						ChangePasswordActivity.this);
				preferencesHelper.setValue("LOGIN_PASSWORD",
						chiperPwd(et_newPassword.getText().toString()));

				showToastMsg(getString(R.string.user_modify_pwd_success));
				finish();
			} else if (resultCode == TaskResult.FAILED) {
				if (taskKey == Constant.USER_UPDATE_PWD) {
					updateUserInfoRetry();
					return;
				} else if (taskKey == Constant.USER_UPDATE_PWD_RETRY) {
					updateUserInfoRetry1();
					return;
				}
			} else if (resultCode == TaskResult.OLD_PASSWORD_ERROR) {
				showToastMsg(getString(R.string.user_old_pwd_error));
			} else if (resultCode == TaskResult.NEW_PASSWORD_ERROR) {
				showToastMsg(getString(R.string.user_new_pwd_perform_error));
			}
			stopProgressBar();
		}
	};
}
