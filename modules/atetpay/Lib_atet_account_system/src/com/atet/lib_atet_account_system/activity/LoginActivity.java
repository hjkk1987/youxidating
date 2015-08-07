package com.atet.lib_atet_account_system.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atet.lib_atet_account_system.ATETUser;
import com.atet.lib_atet_account_system.R;
import com.atet.lib_atet_account_system.http.callbacks.LoginCallback;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;

/**
 * 用户登陆，示例Demo，在导出的JAR包中不会包含此类文件
 * 
 * @author zhaominglai
 * @date 2014/8/2
 * 
 * */
public class LoginActivity extends Activity implements LoginCallback {

	private EditText mEtUser, mEtPwd;
	private Button mBtnLogin, mBtnReg, mBtnFind, mBtnDevInfo, mBtnCancle;
	ATETUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();

		// user.login("frank09", "19900909dj", 1,this);
		// user.register("test", "z250811261", "1234567", "frankzhao@163.com",
		// "18665998093", this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (user == null)
			user = new ATETUser(getApplicationContext());
	}

	private void initViews() {
		// TODO Auto-generated method stub

		mEtPwd = (EditText) findViewById(R.id.et_pwd);

		mEtUser = (EditText) findViewById(R.id.et_username);

		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// mBtnLogin.setEnabled(false);
				user.login(mEtUser.getText().toString(), mEtPwd.getText()
						.toString(), LoginActivity.this);
			}
		});

		mBtnReg = (Button) findViewById(R.id.btn_reg);

		mBtnReg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);

				startActivity(intent);

			}
		});

		mBtnFind = (Button) findViewById(R.id.btn_find);

		mBtnFind.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						FindPwdActivity.class);

				startActivity(intent);

			}
		});

		mBtnDevInfo = (Button) findViewById(R.id.btn_deviceinfo);
		mBtnDevInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RetrieveDevInfoActivity.class);

				startActivity(intent);
			}
		});

		mBtnCancle = (Button) findViewById(R.id.btn_cancle);
		mBtnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				user.cancleLogin();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void loginSuccessed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(LoginActivity.this, "登陆成功", 1).show();
			}
		});
	}

	@Override
	public void loginError() {
		// TODO Auto-generated method stub

		/*
		 * if (count == 1) { user.login(mEtUser.getText().toString(),
		 * mEtPwd.getText().toString(), LoginActivity.this,2);
		 * Toast.makeText(LoginActivity.this, "第一次登陆失败", 1).show(); } else
		 */
		Toast.makeText(LoginActivity.this, "操作失败，请重复提交或者稍后再试。", 1).show();
	}

	@Override
	public void userIsNotExist() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(LoginActivity.this, "用户名不存在。", 1).show();
			}
		});
	}

	@Override
	public void InvailedUserPwd() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(LoginActivity.this, "登陆密码错误。", 1).show();
			}
		});
	}

	@Override
	public void loginFailed(final int backCode) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(LoginActivity.this, "登陆失败，：。" + backCode, 1)
						.show();
			}
		});
	}

	@Override
	public void netIsNotAvailable() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(LoginActivity.this, "当前网络不可用，请检查网络连接。", 1)
						.show();
			}
		});
	}

	@Override
	public void invailedLoginNameParam() {
		// TODO Auto-generated method stub

		mBtnLogin.setEnabled(true);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(LoginActivity.this, "输入的用户名格式不正确。", 1).show();
			}
		});
	}

	@Override
	public void invailedPwdParam() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(LoginActivity.this, "输入的密码格式不正确。", 1).show();
			}
		});
	}

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE type) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (user != null) {
			user.releaseResource();
		}
	}
}
