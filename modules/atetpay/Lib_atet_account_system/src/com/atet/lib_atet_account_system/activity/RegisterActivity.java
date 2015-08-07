package com.atet.lib_atet_account_system.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.atet.lib_atet_account_system.ATETUser;
import com.atet.lib_atet_account_system.R;
import com.atet.lib_atet_account_system.http.callbacks.RegisterCallback;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;

/**
 * 用户注册，示例Demo
 * 
 * @author zhaominglai
 * @date 2014/8/2
 * 
 * */
public class RegisterActivity extends Activity implements RegisterCallback {

	private EditText mEtUser, mEtPwd, mEtNickName, mEtEmail, mEtPhone;
	private Button mBtnReg, mBtnCancle;
	ATETUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_register);

		initViews();

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

		mEtNickName = (EditText) findViewById(R.id.et_nickName);

		mEtEmail = (EditText) findViewById(R.id.et_mail);

		mEtPhone = (EditText) findViewById(R.id.et_mobile);

		mBtnReg = (Button) findViewById(R.id.btn_register);
		mBtnReg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// user.login(mEtUser.getText().toString(),
				// mEtPwd.getText().toString(), 1, RegisterActivity.this);

				user.register(null, mEtNickName.getText().toString(), mEtUser
						.getText().toString(), mEtPwd.getText().toString(),
						mEtPwd.getText().toString(), mEtEmail.getText()
								.toString(), mEtPhone.getText().toString(),
						RegisterActivity.this);
			}
		});

		mBtnCancle = (Button) findViewById(R.id.btn_cancle);
		mBtnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				user.cancleRegister();
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
	public void regSuccessed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "注册成功", 1).show();
			}
		});
	}

	@Override
	public void regError() {
		// TODO Auto-generated method stub

		// user.register(mEtNickName.getText().toString(),
		// mEtUser.getText().toString(), mEtPwd.getText().toString(),
		// mEtPwd.getText().toString(), mEtEmail.getText().toString(),
		// mEtPhone.getText().toString(), RegisterActivity.this);
		Toast.makeText(RegisterActivity.this, "注册失败，发生异常", 1).show();

	}

	@Override
	public void userIsExist() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "用户名已经存在", 1).show();
			}
		});

	}

	@Override
	public void regFailed(int backCode) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "注册失败", 1).show();
			}
		});
	}

	@Override
	public void userInputInvailed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "用户名格式不正确或则为空", 1).show();
			}
		});

	}

	@Override
	public void pwdInputInvailed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "密码格式不正确或则为空", 1).show();
			}
		});
	}

	@Override
	public void emailInputInvailed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "邮箱格式不正确或则为空", 1).show();
			}
		});
	}

	@Override
	public void phoneInputInvailed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "手机格式不正确或则为空", 1).show();
			}
		});
	}

	@Override
	public void chineseInputInvailed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "中文名称格式不正确或则为空", 1)
						.show();
			}
		});
	}

	@Override
	public void qqInputInvailed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "qq格式不正确或则为空", 1).show();
			}
		});
	}

	@Override
	public void nickNameInputInvailed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "用户昵称格式不正确或则为空", 1)
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
				Toast.makeText(RegisterActivity.this, "当前网络不可用，请检查网络连接。", 1)
						.show();
			}
		});
	}

	@Override
	public void twoPwdIsNotSame() {
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

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE type) {
		// TODO Auto-generated method stub

	}

}
