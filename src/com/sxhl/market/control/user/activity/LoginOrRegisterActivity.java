package com.sxhl.market.control.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.activity.BaseActivity;

public class LoginOrRegisterActivity extends BaseActivity {
	// 登录按钮
	private Button mLogin;
	// 注册按钮
	private Button mRegister;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login_or_register);
		setHeadTitle(getString(R.string.title_regist_logon));
		goBack();
		initView();
		bindButtonListener();
	}

	protected void initView() {
		mLogin = (Button) findViewById(R.id.btnLogin);
		mRegister = (Button) findViewById(R.id.btnRegister);
	}

	/**
	 * 
	 * @Title: bindButtonListener
	 * @Description: 给按钮绑定监听器
	 * @param
	 * @return void
	 * @throws
	 */
	protected void bindButtonListener() {
		ButtonClickListener listener = new ButtonClickListener();
		mLogin.setOnClickListener(listener);
		mRegister.setOnClickListener(listener);
	}

	/**
	 * 
	 * @Title: unBindButtonListener
	 * @Description: 按钮解绑定
	 * @param
	 * @return void
	 * @throws
	 */
	protected void unBindButtonListener() {
		mLogin.setOnClickListener(null);
		mRegister.setOnClickListener(null);
		mLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(LoginOrRegisterActivity.this,
						R.string.read_items_first, Toast.LENGTH_LONG).show();
			}
		});
		mRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(LoginOrRegisterActivity.this,
						R.string.read_items_first, Toast.LENGTH_LONG).show();
			}
		});
	}

	private class ButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnLogin:
				// 跳转到登录界面
				if (getIntent().getIntExtra(Constant.GIFTINTENTKEY, 0) == Constant.ExtraData) {
					Intent i = new Intent(LoginOrRegisterActivity.this,
							LoginActivity.class);
					i.putExtra(Constant.GIFTINTENTKEY, Constant.ExtraData);
					startActivity(i);
					LoginOrRegisterActivity.this.finish();
				} else {
					jumpToActivity(LoginOrRegisterActivity.this,
							LoginActivity.class, true);
				}
				break;
			case R.id.btnRegister:
				if (getIntent().getIntExtra(Constant.GIFTINTENTKEY, 0) == Constant.ExtraData) {
					Intent i = new Intent(LoginOrRegisterActivity.this,
							RegisterActivity.class);
					i.putExtra(Constant.GIFTINTENTKEY, Constant.ExtraData);
					startActivity(i);
					LoginOrRegisterActivity.this.finish();
				} else {
					jumpToActivity(LoginOrRegisterActivity.this,
							RegisterActivity.class, true);
				}

				break;
			}
		}
	}

	protected void showToastMsg(String tipsInfo) {
		Toast.makeText(LoginOrRegisterActivity.this, tipsInfo,
				Toast.LENGTH_SHORT).show();
	}

}
