package com.sxhl.market.control.user.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.atet.lib_atet_account_system.ATETUser;
import com.atet.lib_atet_account_system.http.callbacks.FindPwdCallback;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;
import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.utils.NetUtil;

public class FindPsdActivity extends BaseActivity implements FindPwdCallback {
	private EditText etUserName, etEmail;
	private Button btnFindPsd;
	private ATETUser user;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		goBack();
		setHeadTitle(getString(R.string.user_find_password));
		mContext = this;
		user = new ATETUser(this);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		etUserName = (EditText) findViewById(R.id.et_username);
		etEmail = (EditText) findViewById(R.id.et_mail);
		btnFindPsd = (Button) findViewById(R.id.btn_find_password);

		btnFindPsd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!NetUtil.isNetworkAvailable(mContext, true)) {
					showToastMsg(getString(R.string.com_network_not_found_exception));
					return;
				}
				if (etUserName.getText().toString().equals("")) {
					showToastMsg(getString(R.string.username_not_null));
					return;
				}
				if (etEmail.getText().toString().equals("")) {
					showToastMsg(getString(R.string.email_not_null));
					return;
				}
				if (etUserName.getText().toString().length() < 5) {
					showToastMsg(getString(R.string.username_cannot_less_than_three));
					return;
				}
				startProgressBar(getString(R.string.tip),
						getString(R.string.psdfinding),
						Constant.TASK_FIND_PASSWORD, user);

				user.findPassword(etUserName.getText().toString(), etEmail
						.getText().toString(), FindPsdActivity.this);
			}
		});
	}

	@Override
	public void netIsNotAvailable() {
		// TODO Auto-generated method stub
		stopProgressBar();
		showToastMsg(getString(R.string.com_network_not_found_exception));
	}

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void emailInputInvailed() {
		// TODO Auto-generated method stub
		stopProgressBar();
		showToastMsg(getString(R.string.email_format_error));
	}

	@Override
	public void emailIsNotMatch() {
		// TODO Auto-generated method stub
		stopProgressBar();
		showToastMsg(getString(R.string.email_not_match));
	}

	@Override
	public void findError() {
		// TODO Auto-generated method stub
		stopProgressBar();
		showToastMsg(getString(R.string.server_is_not_available));
	}

	@Override
	public void findFailed(int arg0) {
		// TODO Auto-generated method stub
		stopProgressBar();
		showToastMsg(getString(R.string.server_is_not_available));
	}

	@Override
	public void findSuccessed() {
		// TODO Auto-generated method stub
		stopProgressBar();
		showToastMsg(getString(R.string.email_modify_psd_success));
	}

	@Override
	public void userInputInvailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void userIsNotExist() {
		// TODO Auto-generated method stub
		stopProgressBar();
		showToastMsg(getString(R.string.launcher_account_user_not_exits));

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		if (user != null) {
			user.releaseResource();
			user = null;
		}
	}

}