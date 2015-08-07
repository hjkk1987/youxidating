package com.sxhl.market.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sxhl.market.R;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.user.activity.LoginActivity;
import com.sxhl.market.control.user.activity.RegisterActivity;
import com.sxhl.market.control.user.activity.StipulationActivity;

public class MyDialog extends Dialog implements OnClickListener{
	private Context mContext;
	//注册按钮
	private Button mRegister;
	//登录按钮
	private Button mLogin;
	private BaseActivity mBaseActivity;
	
	public MyDialog(Context context) {
		super(context);
		mContext = context;
		mBaseActivity=(BaseActivity)context;
	}

	public MyDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		mBaseActivity=(BaseActivity)context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_tip_dialog);
		initViews();
	}

	private void initViews(){
		mRegister=(Button)findViewById(R.id.btnRegister);
		mLogin=(Button)findViewById(R.id.btnLogin);
		mRegister.setOnClickListener(this);
		mLogin.setOnClickListener(this);
	}
		
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnRegister:
			mBaseActivity.startActivity(new Intent(mBaseActivity, RegisterActivity.class));
			dismiss();
			break;
		case R.id.btnLogin:
			mBaseActivity.startActivity(new Intent(mBaseActivity, LoginActivity.class));
			dismiss();
			break;
		case R.id.tvLinkTo:
			Intent intent=new Intent(mContext,StipulationActivity.class);
			((Activity)mContext).startActivity(intent);//将上下文强转为activity
			break;
		}	
	}
}
