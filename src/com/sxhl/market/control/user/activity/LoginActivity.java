package com.sxhl.market.control.user.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atet.lib_atet_account_system.ATETUser;
import com.atet.lib_atet_account_system.http.callbacks.LoginCallback;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;
import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.preferences.PreferencesHelper;
import com.sxhl.market.utils.AESCryptoUtils;

/**
 * 
 * @ClassName: LoginActivity
 * @Description: 用户登录
 * @author Zjf
 * @date 2013-5-3 上午10:01:35
 */
public class LoginActivity extends BaseImplActivity implements LoginCallback {
	private EditText mUsernameEt;
	private EditText mPwdEt;
	private Button mLoginBtn;
	ATETUser user;
	Handler handler;

	@Override
	protected int getLayoutId() {
		return R.layout.user_activity_login;
	}

	@Override
	protected void initView() {
		mUsernameEt = (EditText) findViewById(R.id.etUsername);
		mPwdEt = (EditText) findViewById(R.id.etPwd);
		mLoginBtn = (Button) findViewById(R.id.btnLogin);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (user == null)
			user = new ATETUser(getApplicationContext());
	}

	@Override
	protected void bindViewListener() {
		mLoginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mUsernameEt.getText().toString().equals("")) {
					showToastMsg(getString(R.string.username_not_null));
					return;
				}
				if (mPwdEt.getText().toString().equals("")) {
					showToastMsg(getString(R.string.password_not_null));
					return;
				}
				if (mUsernameEt.getText().toString().length() < 5) {
					showToastMsg(getString(R.string.username_cannot_less_than_three));
					return;
				}
				if (mUsernameEt.getText().toString().length() > 16) {
					showToastMsg(getString(R.string.username_cannot_longger_than_sixteen));
					return;
				}

				if (mPwdEt.getText().toString().length() < 5) {
					showToastMsg(getString(R.string.password_cannot_longger_than_eight));
					return;
				}
				if (mPwdEt.getText().toString().length() > 16) {
					showToastMsg(getString(R.string.password_cannot_longger_than_sixteen));
					return;
				}
				startProgressBar(getString(R.string.tip),
						getString(R.string.logining), Constant.TASK_LOGIN, user);
				// WaitDialog.showDialog(LoginActivity.this, null);

				/*
				 * user.login(mUsernameEt.getText().toString(), mPwdEt.getText()
				 * .toString(),LoginActivity.this);
				 */
				/*
				 * new TokenTask(LoginActivity.this).startGetToken(taskManager,
				 * tokenListener);
				 */
				//
				// startProgressBar(getString(R.string.tip),
				// getString(R.string.logining));

				user.login(mUsernameEt.getText().toString(), mPwdEt.getText()
						.toString(), LoginActivity.this);
				/*
				 * new TokenTask(LoginActivity.this).startGetToken(taskManager,
				 * tokenListener);
				 */
			}
		});
	}

	@Override
	protected CharSequence getHeadTitle() {
		return getString(R.string.title_logon);
	}

	@Override
	public void loginSuccessed() {
		// TODO Auto-generated method stub
		// if (isProDialogCancled()) {
		// return;
		// }
		if (user == null) {
			return;
		}
		UserInfo userInfo = new UserInfo();
		PreferencesHelper preferencesHelper = new PreferencesHelper(
				LoginActivity.this);
		com.atet.lib_atet_account_system.model.UserInfo userResult = user
				.getUserInfo();

		userInfo.setNickName(userResult.getNickName());
		userInfo.setUserId(userResult.getUserId() + "");
		userInfo.setUserName(userResult.getUserName());
		userInfo.setUid(userResult.getUserName());
		userInfo.setAvator(userResult.getIcon());
		userInfo.setEmail(userResult.getEmail());
		userInfo.setPhone(userResult.getPhone());

		String pwd = null;
		try {

			pwd = AESCryptoUtils.encrypt("ATET", mPwdEt.getText().toString());
			// Toast.makeText(LoginActivity.this, pwd, 1).show();
			/*
			 * ======= pwd = AESCryptoUtils.encrypt("atet",
			 * mPwdEt.getText().toString()); //
			 * Toast.makeText(LoginActivity.this, pwd, 1).show(); >>>>>>> .r3594
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String pwd = EncryptUtils.encryptAES(mPwdEt.getText().toString(),
		// "ATET");
		preferencesHelper.setValue("LOGIN_PASSWORD", pwd);
		preferencesHelper.setValue("LOGIN_USER_NAME", userResult.getUserName());
		preferencesHelper.setIntValue("LOGIN_USER_ID", userResult.getUserId());
		preferencesHelper.setValue("uid", userResult.getUserName());
		userInfo.setUid(mUsernameEt.getText().toString());

		PersistentSynUtils.execDeleteData(UserInfo.class, " where id >0");
		PersistentSynUtils.addModel(userInfo);
		BaseApplication.setLoginUser(userInfo);
		Toast.makeText(LoginActivity.this,
				getText(R.string.launcher_account_login_successed), 1).show();
		stopProgressBar();

		if (getIntent().getIntExtra(Constant.GIFTINTENTKEY, 0) == Constant.ExtraData) {
			Intent in = new Intent(this, GiftPackageActivity.class);
			in.putExtra(Constant.GIFTINTENTKEY, Constant.ExtraData);
			startActivity(in);
		}

		finish();
	}

	@Override
	public void loginError() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(LoginActivity.this,
				getText(R.string.launcher_account_login_error), 1).show();
	}

	@Override
	public void userIsNotExist() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(LoginActivity.this,
				getText(R.string.launcher_account_user_not_exits), 1).show();
	}

	@Override
	public void InvailedUserPwd() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(LoginActivity.this,
				getText(R.string.launcher_account_user_password_error), 1)
				.show();
	}

	@Override
	public void loginFailed(final int backCode) {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(
				LoginActivity.this,
				getText(R.string.launcher_account_login_failed).toString()
						+ backCode, 1).show();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		if (user != null) {
			user.releaseResource();
			user = null;
		}
	}

	@Override
	public void netIsNotAvailable() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(LoginActivity.this,
				getText(R.string.com_network_not_found_exception),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void invailedLoginNameParam() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(
				LoginActivity.this,
				getText(R.string.launcher_account_user_name_all_number)
						.toString(), 1).show();
	}

	@Override
	public void invailedPwdParam() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(
				LoginActivity.this,
				getText(R.string.launcher_account_password_error_format)
						.toString(), 1).show();
	}

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE arg0) {
		// TODO Auto-generated method stub

	}

}
