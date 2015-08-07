package com.sxhl.market.control.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atet.lib_atet_account_system.ATETUser;
import com.atet.lib_atet_account_system.http.callbacks.RegisterCallback;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;
import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.preferences.PreferencesHelper;
import com.sxhl.market.utils.AESCryptoUtils;
import com.sxhl.market.utils.NetUtil;
import com.sxhl.statistics.utils.DeviceStatisticsUtils;

public class NewRegisterActivity extends BaseActivity implements
		RegisterCallback {
	private Context mContext;
	private ATETUser user;
	private EditText mUsernameEt;
	private EditText mPwdEt;
	private EditText mNickNameEt;
	private EditText mEmailEt;
	private EditText mPhoneNumEt;
	private CheckBox mPwdVisibleCb;
	private CheckBox mReadClause;
	private Button mRegisterEnsureBtn;
	private String atetId = null;
	private TextView tvUserAgreement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mContext = this;
		goBack();
		setHeadTitle(getString(R.string.usr_main_register));
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (user == null) {
			user = new ATETUser(mContext);
		}
		super.onResume();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mReadClause = (CheckBox) findViewById(R.id.chkBoxClause);
		mUsernameEt = (EditText) findViewById(R.id.et_username);
		mNickNameEt = (EditText) findViewById(R.id.et_nickname);
		mEmailEt = (EditText) findViewById(R.id.et_mail);
		mPhoneNumEt = (EditText) findViewById(R.id.et_phone);
		mPwdEt = (EditText) findViewById(R.id.et_psd);
		mPwdVisibleCb = (CheckBox) findViewById(R.id.cbPwdVisible);
		mRegisterEnsureBtn = (Button) findViewById(R.id.btn_regist);
		tvUserAgreement = (TextView) findViewById(R.id.tvLinkTo);
		tvUserAgreement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jumpToActivity(StipulationActivity.class, false);
			}
		});
		if (mPwdVisibleCb == null) {
		}
		mPwdVisibleCb
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mPwdEt.setTransformationMethod(isChecked ? HideReturnsTransformationMethod
								.getInstance() : PasswordTransformationMethod
								.getInstance());
					}
				});

		mRegisterEnsureBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!NetUtil.isNetworkAvailable(mContext, true)) {
					showToastMsg(getString(R.string.com_network_not_found_exception));
					return;
				}
				if (!mReadClause.isChecked()) {
					showToastMsg(getString(R.string.read_items_first));
					return;
				}

				if (mUsernameEt.getText().toString().equals("")) {
					showToastMsg(getString(R.string.username_not_null));
					return;
				}

				if (mNickNameEt.getText().toString().equals("")) {
					showToastMsg(getString(R.string.nickname_not_null));
					return;
				}
				if (mEmailEt.getText().toString().equals("")) {
					showToastMsg(getString(R.string.email_not_null));
					return;
				}
				if (mPhoneNumEt.getText().toString().equals("")) {
					showToastMsg(getString(R.string.phone_number_not_null));
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
						getString(R.string.registing), Constant.TASK_REGISTER,
						user);
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						atetId = getSharedPreferences("deviceInfo",
								Context.MODE_PRIVATE).getString(
								"DEVICE_ATET_ID", null);
						if (atetId == null) {
							if (DeviceStatisticsUtils.fetchAtetId(mContext)) {
								atetId = getSharedPreferences("deviceInfo",
										Context.MODE_PRIVATE).getString(
										"DEVICE_ATET_ID", null);
							}

						}
						if (atetId == null) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									stopProgressBar();
									showToastMsg("注册失败，atetId为空");
								}
							});

							return;
						}
						runOnUiThread(new Runnable() {
							public void run() {
								user.register(atetId, mNickNameEt.getText()
										.toString(), mUsernameEt.getText()
										.toString(), mPwdEt.getText()
										.toString(), mPwdEt.getText()
										.toString(), mEmailEt.getText()
										.toString(), mPhoneNumEt.getText()
										.toString(), NewRegisterActivity.this);
							}
						});
					}
				}).start();

			}
		});
	}

	/* 
	 * 	注册成功回调
	 */
	public void regSuccessed() {
		// TODO Auto-generated method stub

		UserInfo userInfo = new UserInfo();
		PreferencesHelper preferencesHelper = new PreferencesHelper(mContext);
		com.atet.lib_atet_account_system.model.UserInfo userResult = user
				.getUserInfo();
		userInfo.setNickName(userResult.getNickName());
		userInfo.setUserId(userResult.getUserId() + "");
		userInfo.setUserName(userResult.getUserName());
		userInfo.setUid(userResult.getUserName());
		userInfo.setEmail(userResult.getEmail());
		userInfo.setPhone(userResult.getPhone());

		String pwd = null;
		try {
			pwd = AESCryptoUtils.encrypt("ATET", mPwdEt.getText().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String pwd = EncryptUtils.encryptAES(mPwdEt.getText().toString(),
		// "ATET");
		/*
		 * preferencesHelper.setValue("upwd", pwd);
		 * preferencesHelper.setValue("uid", userResult.getUserName());
		 * userInfo.setUid(mUsernameEt.getText().toString());
		 */
		preferencesHelper.setValue("LOGIN_PASSWORD", pwd);
		preferencesHelper.setValue("LOGIN_USER_NAME", userResult.getUserName());
		preferencesHelper.setIntValue("LOGIN_USER_ID", userResult.getUserId());
		preferencesHelper.setValue("uid", userResult.getUserName());
		userInfo.setUid(mUsernameEt.getText().toString());

		PersistentSynUtils.execDeleteData(UserInfo.class, " where id >0");
		PersistentSynUtils.addModel(userInfo);
		BaseApplication.setLoginUser(userInfo);

		stopProgressBar();
		Toast.makeText(mContext,
				getText(R.string.launcher_account_reg_successed), 1 * 1000)
				.show();
		if (getIntent().getIntExtra(Constant.GIFTINTENTKEY, 0) == Constant.ExtraData) {
			Intent in = new Intent(this, GiftPackageActivity.class);
			startActivity(in);
			finish();
		} else {
			finish();
		}

	}

	@Override
	public void regError() {
		// TODO Auto-generated method stub

		stopProgressBar();
		Toast.makeText(mContext, getText(R.string.launcher_account_reg_error),
				1).show();
	}

	@Override
	public void userIsExist() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext, getText(R.string.launcher_account_user_exits),
				1).show();
	}

	@Override
	public void regFailed(int backCode) {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(
				mContext,
				getText(R.string.launcher_account_reg_failed).toString()
						+ backCode, 1).show();
	}

	@Override
	public void userInputInvailed() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(
				mContext,
				getText(R.string.launcher_account_user_name_all_number)
						.toString(), 1).show();
	}

	@Override
	public void pwdInputInvailed() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext, getText(R.string.password_format_error), 1)
				.show();
	}

	@Override
	public void emailInputInvailed() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext, getText(R.string.email_format_error), 1)
				.show();
	}

	@Override
	public void phoneInputInvailed() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext, getText(R.string.phone_number_format_error), 1)
				.show();
	}

	@Override
	public void chineseInputInvailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void qqInputInvailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void nickNameInputInvailed() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext,
				getText(R.string.nickname_cannot_longger_than_sixteen), 1)
				.show();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
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
	public void twoPwdIsNotSame() {
		// TODO Auto-generated method stub
		stopProgressBar();
		Toast.makeText(mContext, getText(R.string.two_pwd_not_same),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE arg0) {
		// TODO Auto-generated method stub

	}

}
