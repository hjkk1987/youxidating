package com.atet.lib_atet_account_system.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atet.lib_atet_account_system.ATETUser;
import com.atet.lib_atet_account_system.R;
import com.atet.lib_atet_account_system.http.callbacks.FindPwdCallback;
import com.atet.lib_atet_account_system.http.callbacks.GetDeviceInfoCallback;
import com.atet.lib_atet_account_system.model.DeviceRespInfo;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;
import com.atet.lib_atet_account_system.utils.Utils;

/**
 * 用户找回密码功能 ，示例Demo，在导出的JAR包中不会包含此类文件
 * 
 * @author zhaominglai
 * @date 2014/8/2
 * 
 * */
public class RetrieveDevInfoActivity extends Activity  implements GetDeviceInfoCallback{
	
	private TextView tvDeviceId,tvChannelId,tvDeviceType;
	private String DEFAULT_DEVICEID = "";
	private String DEFAULT_DEVICECODE = "";
	private String DEFAULT_CHANNELID = "";
	private int DEFAULT_DEVICETYPE = 2;
	
	ATETUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_deviceinfo);
		
		initViews();
		
		if(user == null)
			user = new ATETUser(getApplicationContext());
		
		user.retreiveDeviceInfo(this);
		
		DEFAULT_DEVICEID = Utils.getDNumber(this, this.getContentResolver());
		DEFAULT_DEVICECODE = Utils.getClientType(this.getContentResolver());
		
		Log.e("origin deviceId :",DEFAULT_DEVICEID);
		Log.e("deviceCode :",DEFAULT_DEVICECODE);
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
		
		tvDeviceId = (TextView) findViewById(R.id.tv_deviceId);
		tvChannelId = (TextView) findViewById(R.id.tv_channelId);
		tvDeviceType = (TextView) findViewById(R.id.tv_devicetype);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void netIsNotAvailable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getInfoSuccessed(DeviceRespInfo respData) {
		// TODO Auto-generated method stub
		
		tvDeviceId.setText(respData.getDeviceId());
		tvDeviceType.setText(respData.getType()+"");
		tvChannelId.setText(respData.getChannelId());
	}

	@Override
	public void getInfoError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getInfoFailed(int backCode) {
		// TODO Auto-generated method stub
		Toast.makeText(RetrieveDevInfoActivity.this, " failed :"+backCode, 1).show();
	}

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE type) {
		// TODO Auto-generated method stub
		
	}


	

}
