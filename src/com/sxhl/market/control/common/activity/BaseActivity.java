package com.sxhl.market.control.common.activity;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.HttpHostConnectException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atet.lib_atet_account_system.ATETUser;
import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.game.activity.SearchActivity;
import com.sxhl.market.model.exception.HttpResponseException;
import com.sxhl.market.model.task.TaskManager;
import com.sxhl.market.utils.AnimationTool;
import com.sxhl.market.utils.asynCache.ImageFetcher;
import com.sxhl.market.view.CommonWaitDialog;

/**
 * @ClassName: BaseActivity.java
 * @Description: Activity的基类，用于控制Activity之间的跳转 Toast信息显示等公共功能，全局退出等
 * @author 吴绍东
 * @date 2012-12-10 下午4:47:29
 */
@SuppressLint("NewApi")
public class BaseActivity extends FragmentActivity {
	private static final String IMAGE_CACHE_DIR = "images";
	// 任务管理器
	protected TaskManager taskManager = new TaskManager(this);
	// Activity跳转是否加动画
	private boolean isSartAnimation = false;
	protected ImageFetcher mImageFetcher;
	// private static ProgressDialog mDlgProgress;
	private static CommonWaitDialog mWaitingDialog;

	// 全局退出广播
	private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	/**
	 * @author wsd
	 * @Description:启动
	 * @date 2013-3-1 下午8:02:01
	 */
	// public void startProgressBar(String title, String message,
	// final int action, final ATETUser user) {
	// if (mDlgProgress == null) {
	// mDlgProgress = ProgressDialog.show(this, title, message);
	// mDlgProgress.setCancelable(true);
	// mDlgProgress.setOnKeyListener(new OnKeyListener() {
	// @Override
	// public boolean onKey(DialogInterface dialog, int keyCode,
	// KeyEvent event) {
	// if (event.getRepeatCount() == 0
	// && keyCode == KeyEvent.KEYCODE_BACK) {
	// // taskManager.cancelTask(Constant.TASKKEY_LOGIN);
	// // ATETUser.mQueue.cancelAll("request_login");
	// if (action == Constant.TASK_LOGIN) {
	// user.cancleLogin();
	// } else if (action == Constant.TASK_REGISTER) {
	// user.cancleRegister();
	// } else if (action == Constant.TASK_MODIFY_INFORMATION) {
	// taskManager.cancelTask(Constant.USER_UPDATE);
	// } else if (action == Constant.TASK_MODIFY_PASSWORD) {
	// taskManager.cancelTask(Constant.USER_UPDATE_PWD);
	// }
	//
	// if (mDlgProgress != null) {
	// mDlgProgress.dismiss();
	// mDlgProgress = null;
	// }
	// return true;
	// }
	// return false;
	// }
	// });
	// } else {
	// mDlgProgress.show();
	// }
	// }
	//
	// public void stopProgressBar() {
	// try {
	// if (mDlgProgress != null) {
	// mDlgProgress.dismiss();
	// mDlgProgress = null;
	// }
	// } catch (Exception e) {
	// // do nothing
	// }
	// }
	//
	// public boolean isProDialogCancled() {
	// return (mDlgProgress == null);
	// }

	public void startProgressBar(String title, String message,
			final int action, final ATETUser user) {
		mWaitingDialog = new CommonWaitDialog(this, title, message);
		mWaitingDialog.setCanceledOnTouchOutside(false);
		mWaitingDialog.show();
		mWaitingDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub

				if (event.getRepeatCount() == 0
						&& keyCode == KeyEvent.KEYCODE_BACK) {
					// taskManager.cancelTask(Constant.TASKKEY_LOGIN);
					// ATETUser.mQueue.cancelAll("request_login");
					if (action == Constant.TASK_LOGIN) {
						user.cancleLogin();
					} else if (action == Constant.TASK_REGISTER) {
						user.cancleRegister();
					} else if (action == Constant.TASK_MODIFY_INFORMATION) {
						taskManager.cancelTask(Constant.USER_UPDATE);
					} else if (action == Constant.TASK_MODIFY_PASSWORD) {
						taskManager.cancelTask(Constant.USER_UPDATE_PWD);
					}

					if (mWaitingDialog != null) {
						mWaitingDialog.dismiss();
						mWaitingDialog = null;
					}
					return true;
				}
				return false;

			}
		});

	}

	public void stopProgressBar() {
		try {
			if (mWaitingDialog != null) {
				mWaitingDialog.dismiss();
				mWaitingDialog = null;
			}
		} catch (Exception e) {
			// do nothing
		}
	}

	public boolean isProDialogCancled() {
		return (mWaitingDialog == null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 这个是设置没有标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		registerReceiver(mLoggedOutReceiver, new IntentFilter(
				Constant.INTENT_ACTION_LOGGED_OUT));

		mImageFetcher = new ImageFetcher();
	}

	public ImageFetcher getmImageFetcher() {
		return mImageFetcher;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mLoggedOutReceiver);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		stopProgressBar();
	}

	/**
	 * @author wsd
	 * @Description:
	 * @param from
	 *            上下文
	 * @param to
	 *            跳到哪个Activity
	 * @param isFinish
	 *            是否把自身finish掉
	 * @date 2012-12-10 下午4:52:35
	 */
	protected void jumpToActivity(Context from, Class<? extends Activity> to,
			Boolean isFinish) {
		Intent intent = new Intent(from, to);
		startActivity(intent);
		startAnimate(from);
		if (isFinish) {
			finish();
		}
	}

	/**
	 * @author wsd
	 * @Description:
	 * @param from
	 *            上下文
	 * @param to
	 *            跳到哪个Activity
	 * @param mBundle
	 *            用来传递参数
	 * @param isFinish
	 *            是否把自身finish掉
	 * @date 2012-12-10 下午4:52:35
	 */
	protected void jumpToActivity(Context from, Class<? extends Activity> to,
			Bundle mBundle, Boolean isFinish) {
		Intent intent = new Intent(from, to);
		intent.putExtras(mBundle);
		startActivity(intent);
		startAnimate(from);
		if (isFinish) {
			finish();
		}
	}

	protected void jumpToActivity(Class<? extends Activity> to, int id,
			Boolean isFinish) {
		Intent intent = new Intent(this, to);
		intent.putExtra("typeId", id);
		startActivity(intent);
		startAnimate(this);
		if (isFinish) {
			finish();
		}
	}

	/**
	 * @author wsd
	 * @Description:
	 * @param to
	 *            跳到哪个Activity
	 * @param isFinish
	 *            是否把自身finish掉
	 * @date 2012-12-10 下午4:52:35
	 */
	protected void jumpToActivity(Class<? extends Activity> to, Boolean isFinish) {
		Intent intent = new Intent(this, to);
		startActivity(intent);
		startAnimate(this);
		if (isFinish) {
			finish();
		}
	}

	/**
	 * @author wsd
	 * @Description:显示提示信息
	 * @param context
	 *            上下文
	 * @param tipsInfo
	 *            提示信息
	 * @date 2012-12-10 下午4:55:23
	 */
	protected void showToastMsg(Context context, String tipsInfo) {
		Toast.makeText(context, tipsInfo, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @author wsd
	 * @Description:显示提示信息
	 * @param tipsInfo
	 *            提示信息
	 * @date 2012-12-10 下午4:55:23
	 */
	protected void showToastMsg(String tipsInfo) {
		Toast.makeText(BaseActivity.this, tipsInfo, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @author wsd
	 * @Description:显示提示信息
	 * @param context
	 *            上下文
	 * @date 2012-12-10 下午4:55:23
	 */
	public void startAnimate(Context context) {
		if (isSartAnimation) {
			// 启动动画
			AnimationTool.doActivityChangeStyle(context,
					AnimationTool.STYLE_ZOOMIN_ZOOMOUT);
		}
	}

	/**
	 * @author wsd
	 * @Description:返回上级菜单
	 * @date 2012-12-14 下午8:05:38
	 */
	protected void goBack() {
		ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(mOnClickListener);
		btnBack.setVisibility(View.VISIBLE);
	}

	/**
	 * @author wsd
	 * @Description:绑定header右边按钮的图标
	 * @date 2012-12-14 下午8:36:27
	 */
	protected void bindHeadRightButton(Integer iconDrawableResId) {
		ImageButton btnHeadRight = (ImageButton) findViewById(R.id.btnHeadRight);
		btnHeadRight.setOnClickListener(mOnClickListener);
		btnHeadRight.setImageResource(iconDrawableResId);
		// 按钮的图片的id绑定到当前的控件，用于当点击公共的头布局进行区分
		btnHeadRight.setTag(iconDrawableResId);
		btnHeadRight.setVisibility(View.VISIBLE);
		setHeadVerticalLineVisible(true);
	}

	/**
	 * @author wsd
	 * @Description:绑定header右边按钮的图标
	 * @date 2012-12-14 下午8:36:27
	 */
	int typeid = 0;

	protected void bindHeadRightButton(Integer iconDrawableResId, int typeId) {
		typeid = typeId;
		ImageButton btnHeadRight = (ImageButton) findViewById(R.id.btnHeadRight);
		btnHeadRight.setOnClickListener(mOnClickListener);
		btnHeadRight.setImageResource(iconDrawableResId);
		// 按钮的图片的id绑定到当前的控件，用于当点击公共的头布局进行区分
		btnHeadRight.setTag(iconDrawableResId);
		btnHeadRight.setVisibility(View.VISIBLE);
		setHeadVerticalLineVisible(true);
	}

	/**
	 * @author wsd
	 * @Description:设置头部标题名称
	 * @date 2012-12-15 上午9:38:53
	 */
	protected void setHeadTitle(String title) {
		TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
		tvHeadTitle.setText(title);
		setHeadVerticalLineVisible(false);
	}

	protected void setHeadVerticalLineVisible(boolean flag) {
		// View line=findViewById(R.id.lineHeadVertical);
		// if(flag){
		// line.setVisibility(View.VISIBLE);
		// }else{
		// line.setVisibility(View.GONE);
		// }
	}

	// 处理公共按钮点击事件
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnHeadRight:
				// 获取按钮的点击id
				Integer tag = (Integer) v.getTag();
				if (tag == null) {
					break;
				}
				if (tag == R.drawable.common_head_search) {
					// 跳转到搜索Activity,不关闭当前Activity
					jumpToActivity(SearchActivity.class, false);

				} else if (tag == R.drawable.send_article) {
					// jumpToActivity(SendArticleActivity.class, typeid, false);
				} else if (tag == R.drawable.article_commit) {
					showToastMsg(getIdToString(R.string.proluance_success));
				}
				break;

			default:
				break;
			}
		}
	};

	public String getIdToString(int id) {
		return getResources().getString(id);
	}

	/**
	 * @author fcs
	 * @Description:判断是否为网络类型异常
	 * @date 2013-6-7 下午7:47:31
	 */
	public boolean isNetWorkException(Exception e) {
		if (e != null) {
			if (e instanceof HttpHostConnectException
					|| e instanceof UnknownHostException
					|| e instanceof SocketTimeoutException
					|| e instanceof HttpResponseException
					|| e instanceof NoHttpResponseException) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// if (keyCode == Constant.KEY_CODE_SWITCH_ORIENTATION
		// && !LandMainTabActivity.sIsVisible) {
		// long currentTime = System.currentTimeMillis();
		// if (currentTime - LandMainTabActivity.sLastSwitchOrientationTime <
		// Constant.SWITCH_MARKET_ORIENTATION_INTERVAL) {
		// return true;
		// }
		// LandMainTabActivity.sLastSwitchOrientationTime = currentTime;
		// // 启动横屏
		// DebugTool.info("BaseActivity", "startMarketLand");
		// Intent intent = new Intent(this, LandMainTabActivity.class);
		// startActivity(intent);
		// return true;
		// }
		return super.onKeyUp(keyCode, event);
	}
}
