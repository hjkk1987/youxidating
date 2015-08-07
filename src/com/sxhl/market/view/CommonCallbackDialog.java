package com.sxhl.market.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.utils.DebugTool;

public class CommonCallbackDialog extends Dialog implements OnClickListener {

	private Context mContext;
	private Button mCancle;
	private Button mSure;
	private TextView mTitle, mMessage;
	private MyDialogListener listener;
	private String title, message;
	private String sure, cancle;

	public interface MyDialogListener {
		public void onClick(View view);
	}

	public CommonCallbackDialog(Context context) {
		super(context);
		mContext = context;
		setGravity();
	}

	private void setGravity() {
		Window window = this.getWindow();
		window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);

	}

	public CommonCallbackDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		setGravity();
	}

	public CommonCallbackDialog(Context context, int theme,
			MyDialogListener listener, String title, String message) {
		super(context, theme);
		this.listener = listener;
		this.title = title;
		this.message = message;
		setGravity();
	}

	public CommonCallbackDialog(Context context, int theme,
			MyDialogListener listener, String title, String message,
			String sure, String cancle) {
		super(context, theme);
		this.listener = listener;
		this.title = title;
		this.message = message;
		this.sure = sure;
		this.cancle = cancle;
		setGravity();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_self_tip_dialog);
		initViews();
	}

	private void initViews() {
		mTitle = (TextView) findViewById(R.id.tvTitle);
		mTitle.setText(title);
		mMessage = (TextView) findViewById(R.id.tvMessage);
		if (message != null) {
			mMessage.setText(message);
		}
		mCancle = (Button) findViewById(R.id.btnCancle);
		if (cancle != null) {
			mCancle.setText(cancle);
		}
		mSure = (Button) findViewById(R.id.btnSure);
		if (sure != null) {
			mSure.setText(sure);
		}
		mCancle.setOnClickListener(this);
		mSure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		listener.onClick(v);
	}

	public void setMessageVisible(boolean isVisible) {
		if (!isVisible) {
			mMessage.setVisibility(View.GONE);
		}
	}

	public MyDialogListener getListener() {
		return listener;
	}

	public void setListener(MyDialogListener listener) {
		this.listener = listener;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSure() {
		return sure;
	}

	public void setSure(String sure) {
		this.sure = sure;
	}

	public String getCancle() {
		return cancle;
	}

	public void setCancle(String cancle) {
		this.cancle = cancle;
	}

	/**
	 * 手柄单击
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getRepeatCount() > 0) {
			return true;
		}
		DebugTool.info("CommonCallbackDialog", "onKeyDown keyCode:" + keyCode);
		if (keyCode == Constant.KEY_CODE_CLICK) {
			try {
				MotionEvent e = MotionEvent.obtain(0, 0,
						MotionEvent.ACTION_DOWN, 0, 0, 0);
				View view = getCurrentFocus();
				if (view != null) {
					view.onTouchEvent(e);
				}
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		DebugTool.info("CommonCallbackDialog", "onKeyUp keyCode:" + keyCode);
		if (keyCode == Constant.KEY_CODE_CLICK) {
			try {
				View view = getCurrentFocus();
				if (view != null) {
					MotionEvent e = MotionEvent.obtain(0, 0,
							MotionEvent.ACTION_UP, 0, 0, 0);
					view.onTouchEvent(e);
				}
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return super.onKeyUp(keyCode, event);
	}

}
