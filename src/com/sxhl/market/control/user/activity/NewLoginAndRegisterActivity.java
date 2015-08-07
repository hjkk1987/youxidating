package com.sxhl.market.control.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atet.lib_atet_account_system.ATETUser;
import com.atet.lib_atet_account_system.http.callbacks.LoginCallback;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;
import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.preferences.PreferencesHelper;
import com.sxhl.market.utils.AESCryptoUtils;

public class NewLoginAndRegisterActivity extends BaseActivity implements
		OnClickListener, LoginCallback {
	private EditText etUserName, etUserPsd;
	private Button btnLogin;
	private TextView tvForgetPsd, tvRegNow;
	private ATETUser user;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mContext = this;
		initView();
		goBack();
		setHeadTitle(getIdToString(R.string.usr_main_login));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (user == null) {
			user = new ATETUser(mContext);
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		etUserName = (EditText) findViewById(R.id.et_username);
		etUserPsd = (EditText) findViewById(R.id.et_psd);
		btnLogin = (Button) findViewById(R.id.btn_login);
		tvForgetPsd = (TextView) findViewById(R.id.tv_forget_psd);
		tvRegNow = (TextView) findViewById(R.id.tv_registe_now);
		btnLogin.setOnClickListener(this);
		tvForgetPsd.setOnClickListener(this);
		tvRegNow.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_login:
			if (etUserName.getText().toString().equals("")) {
				showToastMsg(getString(R.string.username_not_null));
				return;
			}
			if (etUserPsd.getText().toString().equals("")) {
				showToastMsg(getString(R.string.password_not_null));
				return;
			}
			if (etUserName.getText().toString().length() < 5) {
				showToastMsg(getString(R.string.username_cannot_less_than_three));
				return;
			}
			if (etUserName.getText().toString().length() > 16) {
				showToastMsg(getString(R.string.username_cannot_longger_than_sixteen));
				return;
			}

			if (etUserPsd.getText().toString().length() < 5) {
				showToastMsg(getString(R.string.password_cannot_longger_than_eight));
				return;
			}
			if (etUserPsd.getText().toString().length() > 16) {
				showToastMsg(getString(R.string.password_cannot_longger_than_sixteen));
				return;
			}
			startProgressBar(getString(R.string.tip),
					getString(R.string.logining), Constant.TASK_LOGIN, user);
			user.login(etUserName.getText().toString(), etUserPsd.getText()
					.toString(), this);
			break;
		case R.id.tv_forget_psd:
			startActivity(new Intent(mContext, FindPsdActivity.class));
			break;
		case R.id.tv_registe_now:
			if (getIntent().getIntExtra(Constant.GIFTINTENTKEY, 0) == Constant.ExtraData) {
				Intent in = new Intent(this, NewRegisterActivity.class);
				in.putExtra(Constant.GIFTINTENTKEY, Constant.ExtraData);
				startActivity(in);
			}else {
				startActivity(new Intent(mContext, NewRegisterActivity.class));
			}
		
			finish();
			break;
		default:
			break;
		}
	}

	/* 
	 * 登录成功回调
	 */
	public void loginSuccessed() {
		// TODO Auto-generated method stub
		// if (isProDialogCancled()) {
		// return;
		// }
		if (user == null) {
			return;
		}
		UserInfo userInfo = new UserInfo();
		PreferencesHelper preferencesHelper = new PreferencesHelper(mContext);
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

			pwd = AESCryptoUtils
					.encrypt("ATET", etUserPsd.getText().toString());
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
		userInfo.setUid(etUserName.getText().toString());

		PersistentSynUtils.execDeleteData(UserInfo.class, " where id >0");
		PersistentSynUtils.addModel(userInfo);
		BaseApplication.setLoginUser(userInfo);
		Toast.makeText(mContext,
				getText(R.string.launcher_account_login_successed), 1).show();
		stopProgressBar();

		if (getIntent().getIntExtra(Constant.GIFTINTENTKEY, 0) == Constant.ExtraData) {
			Intent in = new Intent(this, GiftPackageActivity.class);
			startActivity(in);
		}

		finish();
	}

	@Override
	public void loginError() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext,
				getText(R.string.launcher_account_login_error), 1).show();
	}

	@Override
	public void userIsNotExist() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext,
				getText(R.string.launcher_account_user_not_exits), 1).show();
	}

	@Override
	public void InvailedUserPwd() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext,
				getText(R.string.launcher_account_user_password_error), 1)
				.show();
	}

	@Override
	public void loginFailed(final int backCode) {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(
				mContext,
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
		Toast.makeText(mContext,
				getText(R.string.com_network_not_found_exception),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void invailedLoginNameParam() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(
				mContext,
				getText(R.string.launcher_account_user_name_all_number)
						.toString(), 1).show();
	}

	@Override
	public void invailedPwdParam() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(
				mContext,
				getText(R.string.launcher_account_password_error_format)
						.toString(), 1).show();
	}

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE arg0) {
		// TODO Auto-generated method stub

	}
}
