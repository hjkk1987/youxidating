package com.sxhl.market.control.user.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sxhl.market.R;
import com.sxhl.market.app.Configuration;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.utils.DebugTool;

public abstract class BaseImplActivity extends BaseActivity {
	private int mLayoutResId;
	private CharSequence mTitle;
	private boolean mBackGone;
	// private static ProgressDialog mDlgProgress;

	// 测试用，测试调用的次数
	int stopProssDialogCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stopProssDialogCount = 0;

		// 1.inflate
		mLayoutResId = getLayoutId();
		if (mLayoutResId > 0) {
			setContentView(mLayoutResId);
		} else {
			// ingore
		}

		// 2.Bind back button and title
		mBackGone = isBackGone();
		bindBack();
		mTitle = getHeadTitle();
		if (!TextUtils.isEmpty(mTitle)) {
			setHeadTitle();
		}

		// 3.Initialize all view's instances
		initView();

		// 4.Bind other view's listeners
		bindViewListener();
	}

	protected abstract int getLayoutId();

	protected abstract void initView();

	protected abstract void bindViewListener();

	protected abstract CharSequence getHeadTitle();

	protected boolean isBackGone() {
		// Subclasses can override this method
		// to make sure the visibility of back button
		// someone can make the button gone for this
		// method return true
		return false;
	}

	private void setHeadTitle() {
		TextView title = (TextView) findViewById(R.id.tvHeadTitle);
		title.setText(mTitle);
	}

	private void bindBack() {
		View back = findViewById(R.id.btnBack);
		View lineHeadVertical = findViewById(R.id.lineHeadVertical);
		lineHeadVertical.setVisibility(View.GONE);
		final int visible = back.getVisibility();
		// Make sure the back button is visible
		if (!mBackGone) {
			if (visible != View.VISIBLE) {
				back.setVisibility(View.VISIBLE);
			}
		} else {
			back.setVisibility(View.GONE);
		}

		// Bind listener
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Just finish self
				finish();
			}
		});
	}

/*<<<<<<< .mine
	public void startProgressBar(String title, String message) {
		if (mDlgProgress == null) {
			mDlgProgress = ProgressDialog.show(this, title, message);
			mDlgProgress.setCancelable(true);// 按返回键可以取消
			mDlgProgress.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (event.getRepeatCount() == 0
							&& keyCode == KeyEvent.KEYCODE_BACK) {
						// taskManager.cancelTask(Constant.TASKKEY_LOGIN);
					//	ATETUser.mQueue.cancelAll("request_login");
						if (mDlgProgress != null) {
							mDlgProgress.dismiss();
							mDlgProgress = null;
						}
=======*/
	// public void startProgressBar(String title, String message, final int
	// action) {
	// if (mDlgProgress == null) {
	// mDlgProgress = ProgressDialog.show(this, title, message);
	// mDlgProgress.setCancelable(true);// 按返回键可以取消
	//
	// } else {
	// mDlgProgress.show();
	// }
	//
	// }
	//
	// public void stopProgressBar() {
	// if (mDlgProgress != null) {
	//
	// mDlgProgress.dismiss();
	// mDlgProgress = null;
	// }
	// }
	//
	// public boolean isProDialogCancled() {
	// return (mDlgProgress == null);
	// }


	/**
	 * 
	 * @Title: toJson
	 * @Description: 将Token转换为字节
	 * @param @param token
	 * @param @return
	 * @return byte[]
	 * @throws
	 */
	public byte[] toJson(Object obj) {
		Gson gson = new Gson();
		String tokenGson = gson.toJson(obj);
		DebugTool.debug(Configuration.DEBUG_TAG, "发送请求的gson：" + tokenGson);
		return tokenGson.getBytes();
	}

}
