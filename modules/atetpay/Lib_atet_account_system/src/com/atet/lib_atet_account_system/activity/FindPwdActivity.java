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
import com.atet.lib_atet_account_system.http.callbacks.FindPwdCallback;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;

/**
 * 用户找回密码功能 ，示例Demo，在导出的JAR包中不会包含此类文件
 * 
 * @author zhaominglai
 * @date 2014/8/2
 * 
 * */
public class FindPwdActivity extends Activity implements FindPwdCallback{
	
	private EditText mEtUser,mEtEmail;
	private Button mBtnConfirm,mBtnCancle;
	ATETUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_find_pwd);
		
		initViews();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(user == null)
			user = new ATETUser(getApplicationContext());
	}

	private void initViews() {
		// TODO Auto-generated method stub
		
		mEtUser = (EditText) findViewById(R.id.et_find_username);
		mEtEmail = (EditText) findViewById(R.id.et_email);
		
		
		mBtnConfirm = (Button) findViewById(R.id.btn_find_ok);
		mBtnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				user.findPassword(mEtUser.getText().toString(), mEtEmail.getText().toString(),FindPwdActivity.this);
			}
		});
		
		mBtnCancle = (Button) findViewById(R.id.btn_cancle);
		mBtnCancle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				user.cancleFindPwd();
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
	public void findSuccessed() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Toast.makeText(getApplicationContext(), "找回密码成功", 1).show();
			}
		});
	}

	@Override
	public void findError() {
		// TODO Auto-generated method stub
		
			//user.findPassword(mEtUser.getText().toString(), mEtEmail.getText().toString(),FindPwdActivity.this);
				Toast.makeText(getApplicationContext(), "操作未成功。", 1).show();
	}

	@Override
	public void userIsNotExist() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Toast.makeText(getApplicationContext(), "用户不存在。", 1).show();
			}
		});
	}

	@Override
	public void emailIsNotMatch() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Toast.makeText(getApplicationContext(), "邮箱不匹配", 1).show();
			}
		});
	}

	@Override
	public void findFailed(final int backCode) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Toast.makeText(getApplicationContext(), "失败： "+backCode, 1).show();
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
				
				Toast.makeText(getApplicationContext(), "输入的用户名格式不正确。", 1).show();
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
				
				Toast.makeText(getApplicationContext(), "输入的邮箱地址不正确", 1).show();
			}
		});
	}

	@Override
	public void netIsNotAvailable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE type) {
		// TODO Auto-generated method stub
		
	}


}
