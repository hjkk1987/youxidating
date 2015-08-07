package com.sxhl.market.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhl.market.R;

public class CommonWaitDialog extends AlertDialog {
	private String title;
	private String message;
	private TextView tvMessage;
	private TextView tvTitle;

	public void setTitle(String title) {
		this.title = title;
		if (tvTitle!=null) {
			tvTitle.setText(title);
		}
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		if (tvMessage!=null) {
			tvMessage.setText(message);
		}
		
	}

	protected CommonWaitDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public CommonWaitDialog(Context context) {
		super(context);
		// setGravity();
		// TODO Auto-generated constructor stub
	}

	public CommonWaitDialog(Context context, String title, String message) {
		super(context);
		this.title = title;
		this.message = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View layout = initView();
		getWindow().setContentView(layout);

	}

	private View initView() {
		// TODO Auto-generated method stub
		View layout = getLayoutInflater().inflate(R.layout.custom_wait_dialog,
				null);
		tvMessage = (TextView) layout.findViewById(R.id.common_wait_text);
		tvTitle = (TextView) layout.findViewById(R.id.common_wait_title);
		ImageView imgProgress = (ImageView) layout
				.findViewById(R.id.common_wait_dia_pro_img);
		imgProgress.setBackgroundResource(R.anim.wait_dia_frame_anim);
		AnimationDrawable animationDrawable = (AnimationDrawable) imgProgress
				.getBackground();
		animationDrawable.start();
		tvMessage.setText(message);
		tvTitle.setText(title);
		return layout;
	}
}
