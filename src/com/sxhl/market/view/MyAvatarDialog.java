package com.sxhl.market.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.sxhl.market.R;

public class MyAvatarDialog extends Dialog implements OnClickListener{

	private Context mContext;
	private Button mCamera;
	private Button mPhoto; 
	private TextView mTitle,mMessage;
	private MyAvatarDialogListener listener;
	private String title,message;
	public interface MyAvatarDialogListener{
		public void onClick(View view);
	}
	
	public MyAvatarDialog(Context context) {
		super(context);
		mContext=context;
		setGravity();
	}

	public MyAvatarDialog(Context context,int theme){
		super(context,theme);
		mContext=context;
		setGravity();
	}
    
	public MyAvatarDialog(Context context,int theme,MyAvatarDialogListener listener,String title,String message){
		super(context,theme);
		this.listener=listener;
		this.title=title;
		this.message=message;
		setGravity();
	}
	
	private void setGravity() {
		Window window = this.getWindow();
		window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_avatar_dialog);
		initViews();
	}
	private void initViews(){
		mTitle=(TextView) findViewById(R.id.tvTitle);
		mTitle.setText(title);
		mMessage=(TextView) findViewById(R.id.tvMessage);
		mMessage.setText(message);
		mCamera=(Button) findViewById(R.id.btnCamera);
		mPhoto=(Button) findViewById(R.id.btnPhoto);
		mCamera.setOnClickListener(this);
		mPhoto.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		listener.onClick(v);
	}
}
