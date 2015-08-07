package com.sxhl.market.control.user.activity;

import java.io.File;
import java.lang.reflect.Method;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseExitActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.UploadInfo;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.model.exception.HttpResponseException;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.net.http.Response;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.preferences.PreferencesHelper;
import com.sxhl.market.utils.AESCryptoUtils;
import com.sxhl.market.utils.BitmapUtils;
import com.sxhl.market.utils.ImageTools;
import com.sxhl.market.utils.MD5CryptoUtils;
import com.sxhl.market.utils.asynCache.ImageFetcher;
import com.sxhl.market.view.CircularImageView;
import com.sxhl.market.view.CommonCallbackDialog;
import com.sxhl.market.view.CommonCallbackDialog.MyDialogListener;
import com.sxhl.market.view.MyAvatarDialog;

public class ChangeLoginActivity extends BaseExitActivity {
	// 注销按钮
	private Button mLogout;
	// 可编辑按钮
	private Button mEditable;
	// 不可被编辑按钮
	private Button mUnEditable;
	// 用户头像
	private CircularImageView mUserAvatar;
	// // 积分
	// private TextView mPoints;
	// // 用户等级
	// private TextView mLevel;
	// // 用户ID
	// private TextView mId;
	// 用户昵称编辑框
	private EditText mEtNickName;
	// 用户昵称tip
	private TextView mTvNickName;
	private Intent mIntent;
	// 设置按钮
	private View mUnRelSetting;
	// 用户设置的布局
	private RelativeLayout mRelSetting;
	// 在线支付布局
	private RelativeLayout mRelPayOnLine;
	// 修改密码布局
	private RelativeLayout mChangePassword;
	// 用户登录布局
	private RelativeLayout mLogin;

	private View mLogonPart;
	private View mUnlogonPart;
	// 自定义Dialog，用于注销的提示
	private CommonCallbackDialog mDialog;
	private MyAvatarDialog mAvatarDialog;
	private Uri photoUri;
	// 标识登录状态下，是否处在编辑信息状态
	private boolean isEditState = false;
	private static final int TAKE_BIG_PICTURE = 1;
	private static final int CROP_BIG_PICTURE = 3;
	private static final int CHOOSE_BIG_PICTURE = 5;
	private File tempFile;
	private Bitmap mBitmap;
	private RelativeLayout rel_layout_gift;
	private RelativeLayout rel_login_layout_gift;
	private ImageView img_newGiftHintPoint, img_loginNewGiftHintPoint;

	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// intent.putExtra("outputX", outputX);
		// intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_user_center);
		initView();
		setHeadTitle(getString(R.string.title_user_center));
		bindClickListener();
	}	

	@Override
	protected void onResume() {
		super.onResume();
		BaseApplication.setLoginUser(BaseApplication.getUserFromSp());
		updateViewSates();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void updateViewSates() {
		if (BaseApplication.getLoginUser() == null) {
			mLogonPart.setVisibility(View.GONE);
			mUnlogonPart.setVisibility(View.VISIBLE);
		} else {
			mLogonPart.setVisibility(View.VISIBLE);
			mUnlogonPart.setVisibility(View.GONE);
			initLoginState();
		}
	}

	private void initLoginState() {
		UserInfo userInfo = BaseApplication.getLoginUser();
		if (isEditState) {
			// 如果编辑状态下，头像是可以点击的，昵称可以编辑
			mUserAvatar.setEnabled(true);
			if (userInfo.getAvator() == null || userInfo.getAvator().equals("")) {
				mUserAvatar.setImageResource(R.drawable.login_default_icon);
			}
			mTvNickName.setText(String.format(
					getString(R.string.user_label_nickname), ""));
			mEtNickName.setVisibility(View.VISIBLE);
			mTvNickName.setVisibility(View.INVISIBLE);

			String etext = mEtNickName.getText().toString().trim();
			if ("".equals(etext)) {
				mEtNickName.setText(userInfo.getNickName());

			}

			mEditable.setVisibility(View.GONE);
			mUnEditable.setVisibility(View.VISIBLE);
			// 光标在输入框的最后显示
			CharSequence text = mEtNickName.getText();
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());
			}
		} else {
			mUserAvatar.setEnabled(false);
			// 非编辑状态使用网络方法
			if (userInfo.getAvator() == null || userInfo.getAvator().equals("")) {
				mUserAvatar.setImageResource(R.drawable.ico_default_avavtor);
			} else {
				mImageFetcher.loadImage(userInfo.getAvator(), mUserAvatar, 10);
			}
			mTvNickName.setText(String.format(
					getString(R.string.user_label_nickname),
					userInfo.getNickName()));
			mEtNickName.setVisibility(View.INVISIBLE);
			mTvNickName.setVisibility(View.VISIBLE);
			mEditable.setVisibility(View.VISIBLE);
			mUnEditable.setVisibility(View.GONE);
		}
		// mPoints.setText(String.format(getString(R.string.user_label_points),
		// userInfo.getPoints()));
		// mLevel.setText(String.format(getString(R.string.user_label_level),
		// userInfo.getLevel()));
		// mId.setText(String.format(getString(R.string.user_label_id),
		// userInfo.getUserName()));
		// mImageFetcher.loadImage(userInfo.getAvator(), mUserAvatar, 10);
	}

	private void changePhoto() {
		if (tempFile != null && tempFile.length() > 0) {
			if (mBitmap != null && !mBitmap.isRecycled()) {
				mBitmap.recycle();
			}
			mBitmap = ImageFetcher.decodeSampledBitmapFromFile(
					tempFile.getAbsolutePath(), 100, 100);
			if (mBitmap != null) {
				mUserAvatar.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(
						mBitmap, 10));
			}
		}
	}

	/**
	 * 
	 * @Title: initLoginView
	 * @Description: 初始化界面
	 * @param
	 * @return void
	 * @throws
	 */
	private void initView() {
		img_newGiftHintPoint = (ImageView) findViewById(R.id.user_center_gift_new_img);
		img_loginNewGiftHintPoint = (ImageView) findViewById(R.id.user_login_center_gift_new_img);
		mLogonPart = findViewById(R.id.user_logon_part);
		mUnlogonPart = findViewById(R.id.user_unlogon_part);
		boolean hasNewGift = getSharedPreferences(Constant.SP_HAS_NEW_GIFT,
				MODE_PRIVATE).getBoolean(Constant.SP_VALUE_HAS_NEW_GIFT, false);
		if (hasNewGift) {
			img_newGiftHintPoint.setVisibility(View.VISIBLE);
			img_loginNewGiftHintPoint.setVisibility(View.VISIBLE);
		} else {
			img_newGiftHintPoint.setVisibility(View.GONE);
			img_loginNewGiftHintPoint.setVisibility(View.GONE);
		}

		mLogout = (Button) findViewById(R.id.btnLogout);
		mRelSetting = (RelativeLayout) findViewById(R.id.rel_lay_setting);
		mRelPayOnLine = (RelativeLayout) findViewById(R.id.rel_lay_payonline);
		rel_layout_gift = (RelativeLayout) findViewById(R.id.rel_lay_gift);
		rel_login_layout_gift = (RelativeLayout) findViewById(R.id.rel_lay_logined_gift);

		mUserAvatar = (CircularImageView) findViewById(R.id.user_imgBtn_avator);
		mEtNickName = (EditText) findViewById(R.id.user_et_nickname);
		mTvNickName = (TextView) findViewById(R.id.user_tv_nickname);
		// mPoints = (TextView) findViewById(R.id.user_tv_points);
		// mLevel = (TextView) findViewById(R.id.user_tv_level2);
		// mId = (TextView) findViewById(R.id.user_tv_id);
		mEditable = (Button) findViewById(R.id.user_btn_enable_edit);
		mUnEditable = (Button) findViewById(R.id.user_btn_disable_edit);

		mUnRelSetting = (RelativeLayout) findViewById(R.id.rel_lay_setting_un);
		mLogin = (RelativeLayout) findViewById(R.id.rel_lay_login_un);
		mChangePassword = (RelativeLayout) findViewById(R.id.rel_lay_change_password);
		// mEtNickName.set
		mEtNickName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					mEtNickName.setSelection(mEtNickName.getText().toString()
							.length());
				} else {
				}
			}
		});

		if (android.os.Build.VERSION.SDK_INT > 10) {
			try {
				Class<EditText> cls = EditText.class;
				Method setSoftInputShownOnFocus;
				setSoftInputShownOnFocus = cls.getMethod(
						"setSoftInputShownOnFocus", boolean.class);
				setSoftInputShownOnFocus.setAccessible(true);
				setSoftInputShownOnFocus.invoke(mEtNickName, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void bindClickListener() {
		ClickListener listener = new ClickListener();
		rel_layout_gift.setOnClickListener(listener);
		rel_login_layout_gift.setOnClickListener(listener);
		mLogout.setOnClickListener(listener);
		mRelSetting.setOnClickListener(listener);
		mRelPayOnLine.setOnClickListener(listener);
		mLogin.setOnClickListener(listener);
		mUnRelSetting.setOnClickListener(listener);
		mChangePassword.setOnClickListener(listener);

		// 绑定是否可编辑用户信息按钮点击事件
		mEditable.setOnClickListener(listener);
		mUnEditable.setOnClickListener(listener);

		mUserAvatar.setOnClickListener(listener);
	}

	/**
	 * 
	 * @ClassName: ButtonClickListener
	 * @author A18ccms a18ccms_gmail_com
	 * @date 2012-12-20 下午1:59:02
	 * 
	 */
	private class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rel_lay_setting:
				mIntent = new Intent(ChangeLoginActivity.this,
						SettingActivity.class);
				startActivity(mIntent);
				break;
			case R.id.rel_lay_payonline:
				// Intent intent = new Intent(ChangeLoginActivity.this,
				// OnlinePayActivity.class);
				// startActivity(intent);
				showToastMsg(getString(R.string.user_payonline_tip));
				break;
			case R.id.btnLogout:
				showDialog();
				break;
			case R.id.rel_lay_setting_un:
				mIntent = new Intent(ChangeLoginActivity.this,
						SettingActivity.class);
				startActivity(mIntent);
				break;
			case R.id.rel_lay_login_un:
				mIntent = new Intent(ChangeLoginActivity.this,
						NewLoginAndRegisterActivity.class);
				startActivity(mIntent);
				break;
			case R.id.rel_lay_gift:
				Toast.makeText(ChangeLoginActivity.this,
						getIdToString(R.string.gift_login_first), 2 * 1000)
						.show();
				img_newGiftHintPoint.setVisibility(View.GONE);
				img_loginNewGiftHintPoint.setVisibility(View.GONE);
				mIntent = new Intent(ChangeLoginActivity.this,
						NewLoginAndRegisterActivity.class);
				mIntent.putExtra(Constant.GIFTINTENTKEY, Constant.ExtraData);
				startActivity(mIntent);

				break;
			case R.id.rel_lay_logined_gift:
				img_newGiftHintPoint.setVisibility(View.GONE);
				img_loginNewGiftHintPoint.setVisibility(View.GONE);
				mIntent = new Intent(ChangeLoginActivity.this,
						GiftPackageActivity.class);
				startActivity(mIntent);

				break;
			case R.id.user_imgBtn_avator:
				showAvatarDialog();
				break;
			case R.id.user_btn_enable_edit:// 当前可以被编辑

				// InputMethodManager imm = (InputMethodManager)
				// ChangeLoginActivity.this
				// .getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				isEditState = true;
				initLoginState();
				break;

			case R.id.user_btn_disable_edit: // 确定编辑后的信息，当前不可编辑
				submitUserInfo();
				break;
			case R.id.rel_lay_change_password:
				startActivity(new Intent(ChangeLoginActivity.this,
						ChangePasswordActivity.class));
				break;
			}
		}
	}

	private void submitUserInfo() {
		UserInfo info = BaseApplication.getLoginUser();
		if (mEtNickName.getText().toString().equals(info.getNickName())
				&& tempFile == null) {
			isEditState = false;
			initLoginState();
			return;
		}
		if (mEtNickName.getText().toString().trim().length() == 0) {
			showToastMsg(getString(R.string.user_nickname_empty));
			return;
		}
		if (mEtNickName.getText().toString().length() > 16) {
			showToastMsg(getString(R.string.user_nickname_longger_than_16));
			return;
		}

		hideSoftInput();
		updateUserInfo();
	}

	/**
	 * @author wsd
	 * @Description:隐藏软键盘
	 * @date 2013-5-29 上午10:57:00
	 */
	private void hideSoftInput() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 
	 * @Title: showDialog
	 * @Description: 注销时弹出的对话框
	 * @param
	 * @return void
	 * @throws
	 */
	private void showDialog() {
		mDialog = new CommonCallbackDialog(this, R.style.TipDialog,
				new MyDialogListener() {
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						switch (view.getId()) {
						case R.id.btnCancle:
							// Toast.makeText(ChargeLoginActivity.this,
							// "您取消了此次注销", Toast.LENGTH_SHORT).show();
							mDialog.dismiss();
							break;
						case R.id.btnSure:
							// Toast.makeText(ChargeLoginActivity.this,
							// "您注销了本次登录", Toast.LENGTH_SHORT).show();
							BaseApplication.setLoginUser(null);
							// PreferencesHelper preferencesHelper = new
							// PreferencesHelper(
							// ChangeLoginActivity.this);
							// preferencesHelper.setValue("uid", null);
							removeSpUserInfo();
							isEditState = false;
							mEtNickName.setText("");
							updateViewSates();
							tempFile = null;
							mDialog.dismiss();
							break;
						}
					}

				}, getString(R.string.tip),
				getString(R.string.user_sure_logout));
		mDialog.setContentView(R.layout.layout_self_tip_dialog);
		mDialog.show();
	}

	private void removeSpUserInfo() {
		// TODO Auto-generated method stub
		PreferencesHelper preferencesHelper = new PreferencesHelper(
				ChangeLoginActivity.this);
		preferencesHelper.remove("uid");
		preferencesHelper.remove("LOGIN_USER_EMAIL");
		preferencesHelper.remove("nickName");
		preferencesHelper.remove("LOGIN_USER_ICON");
		preferencesHelper.remove("LOGIN_USER_ALREADY_LOGIN");
		preferencesHelper.remove("LOGIN_PASSWORD");
		preferencesHelper.remove("LOGIN_USER_ID");
		preferencesHelper.remove("LOGIN_USER_NAME");
		preferencesHelper.remove("LOGIN_USER_PHONE");
	}

	/**
	 * 
	 * @Title: showAvatarDialog
	 * @Description: 修改头像时，弹出的对话框
	 * @param
	 * @return void
	 * @throws
	 */
	private void showAvatarDialog() {
		mAvatarDialog = new MyAvatarDialog(
				this,
				R.style.TipDialog,
				new MyAvatarDialog.MyAvatarDialogListener() {
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						Intent intent = null;
						// 删除上一次截图的临时文件
						@SuppressWarnings("deprecation")
						SharedPreferences sharedPreferences = getSharedPreferences(
								"temp", Context.MODE_WORLD_WRITEABLE);
						ImageTools.deletePhotoAtPathAndName(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath(), sharedPreferences
								.getString("tempName", ""));

						// 保存本次截图临时文件名字
						String fileName = String.valueOf(System
								.currentTimeMillis()) + ".jpg";
						Editor editor = sharedPreferences.edit();
						editor.putString("tempName", fileName);
						editor.commit();
						if (ImageTools.checkSDCardAvailable()) {
							tempFile = new File(Environment
									.getExternalStorageDirectory(), fileName);
							photoUri = Uri.fromFile(tempFile);
						} else {
							showToastMsg(getString(R.string.user_photo_no_sdcard));
							mAvatarDialog.dismiss();
							return;
						}
						switch (view.getId()) {
						case R.id.btnCamera:
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// action
																					// is
																					// capture
							intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
							startActivityForResult(intent, TAKE_BIG_PICTURE);
							mAvatarDialog.dismiss();
							break;
						case R.id.btnPhoto:
							intent = new Intent(Intent.ACTION_GET_CONTENT, null);
							intent.setType("image/*");
							intent.putExtra("crop", "true");
							intent.putExtra("aspectX", 1);
							intent.putExtra("aspectY", 1);
							// intent.putExtra("outputX", 300);
							// intent.putExtra("outputY", 300);
							intent.putExtra("scale", true);
							intent.putExtra("return-data", false);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
							intent.putExtra("outputFormat",
									Bitmap.CompressFormat.JPEG.toString());
							intent.putExtra("noFaceDetection", false); // no
																		// face
																		// detection
							startActivityForResult(intent, CHOOSE_BIG_PICTURE);
							mAvatarDialog.dismiss();
							break;
						}
					}
				}, getString(R.string.tip),
				getString(R.string.user_choose_photo_type));
		mAvatarDialog.setContentView(R.layout.layout_avatar_dialog);
		mAvatarDialog.show();
	}

	// 向服务器提交更新结果
	private void updateUserInfo() {

		taskManager.cancelTask(Constant.USER_UPDATE);
		taskManager.startTask(userInfoListener, Constant.USER_UPDATE);
	}

	String filePath = "";
	AsynTaskListener<UserInfo> userInfoListener = new AsynTaskListener<UserInfo>() {
		@Override
		public boolean preExecute(BaseTask<UserInfo> task, Integer taskKey) {
			startProgressBar(getString(R.string.tip),
					getString(R.string.submiting),
					Constant.TASK_MODIFY_INFORMATION, null);
			return true;
		}

		@Override
		public TaskResult<UserInfo> doTaskInBackground(Integer taskKey) {
			PreferencesHelper preferencesHelper = new PreferencesHelper(
					ChangeLoginActivity.this);
			// //如果没传图片
			if (tempFile == null || tempFile.length() == 0) {
				UserInfo userInfo = BaseApplication.getLoginUser();
				HttpReqParams params = new HttpReqParams();
				params.setUserId(userInfo.getUserId());
				params.setLoginName(userInfo.getUserName());
				params.setNickName(mEtNickName.getText() + "");
				params.setEmail(userInfo.getEmail());
				params.setPhone(userInfo.getPhone());

				String pwd = preferencesHelper.getValue("LOGIN_PASSWORD");
				String dPwd = null;
				try {

					dPwd = AESCryptoUtils.decrypt("ATET", pwd);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					params.setOldPassword(MD5CryptoUtils.toMD5(dPwd.getBytes()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				params.setNewPassword("");
				params.setConfirmPassword("");

				// params.setAvator(filePath);
				params.setIcon(filePath);
				return HttpApi.getObject(UrlConstant.HTTP_USER_MODIFY1,
						UrlConstant.HTTP_USER_MODIFY2,
						UrlConstant.HTTP_USER_MODIFY3, UserInfo.class,
						params.toJsonParam());
			}
			// 如果上传了图片
			final TaskResult<UploadInfo> uploadResult = HttpApi.uploadImage(
					UrlConstant.HTTP_COMMON_PUPLOAD1,
					UrlConstant.HTTP_COMMON_PUPLOAD2,
					UrlConstant.HTTP_COMMON_PUPLOAD3, tempFile,
					UploadInfo.class, ChangeLoginActivity.this);
			if (uploadResult == null || uploadResult.getData() == null) {
				return new TaskResult<UserInfo>();

			}
			uploadResult.setCode(uploadResult.getData().getCode());
			// .getAbsolutePath()

			if (uploadResult.getCode() == TaskResult.OK) {

				UserInfo userInfo = BaseApplication.getLoginUser();
				filePath = uploadResult.getData().getAddress();
				String fileName = filePath
						.substring(filePath.lastIndexOf("/") + 1);
				HttpReqParams params = new HttpReqParams();
				params.setUserId(userInfo.getUserId());
				params.setLoginName(userInfo.getUserName());
				params.setNickName(mEtNickName.getText() + "");
				params.setEmail(userInfo.getEmail());
				params.setPhone(userInfo.getPhone());

				String pwd = preferencesHelper.getValue("LOGIN_PASSWORD");
				String dPwd = null;
				try {
					dPwd = AESCryptoUtils.decrypt("ATET", pwd);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					params.setOldPassword(MD5CryptoUtils.toMD5(dPwd.getBytes()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				params.setNewPassword("");
				params.setConfirmPassword("");

				// params.setAvator(filePath);
				params.setIcon(fileName);
				// return HttpApi.getObject(UrlConstant.HTTP_USER_MODIFY1,
				// UserInfo.class, params.toJsonParam());
				return HttpApi.getObject(UrlConstant.HTTP_USER_MODIFY1,
						UrlConstant.HTTP_USER_MODIFY2,
						UrlConstant.HTTP_USER_MODIFY3, UserInfo.class,
						params.toJsonParam());
			}
			TaskResult<UserInfo> mResult = new TaskResult<UserInfo>();
			mResult.setCode(uploadResult.getCode());
			return mResult;
		}

		@Override
		public void onResult(Integer taskKey, TaskResult<UserInfo> result) {
			TaskResult<UserInfo> info = result;

			if (info.getCode() == TaskResult.OK) {
				UserInfo userInfo = BaseApplication.getLoginUser();
				// 如果有传图片
				if (tempFile != null && tempFile.length() > 0) {
					userInfo.setAvator(filePath);
				}
				userInfo.setNickName(mEtNickName.getText() + "");
				if (userInfo != null) {
					PersistentSynUtils.execDeleteData(UserInfo.class,
							" where id >0");
					PersistentSynUtils.addModel(userInfo);
					BaseApplication.setLoginUser(userInfo);
					showToastMsg(getString(R.string.user_modify_success));
				}
				isEditState = false;
			}
			initLoginState();
			stopProgressBar();
			if (tempFile != null && tempFile.length() > 0) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						UserInfo inf = BaseApplication.getLoginUser();
						HttpReqParams param = new HttpReqParams();
						param.setUserId(inf.getUserId());
						param.setAvator(inf.getAvator());
						param.setNickName(inf.getNickName());
						Response res = null;
						try {
							res = HttpApi.getHttpPost1(
									UrlConstant.HTTP_CHANGE_COMMENT_ICON1,
									UrlConstant.HTTP_CHANGE_COMMENT_ICON2,
									UrlConstant.HTTP_CHANGE_COMMENT_ICON3,
									param.toJsonParam());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							Log.e("response", res.asString());
						} catch (HttpResponseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (ChangeLoginActivity.this.getCurrentFocus() != null) {
				if (ChangeLoginActivity.this.getCurrentFocus().getWindowToken() != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(ChangeLoginActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {// result is not correct
			tempFile = null;
			return;
		} else {
			switch (requestCode) {
			case TAKE_BIG_PICTURE:
				// TODO sent to crop
				cropImageUri(photoUri, 300, 300, CROP_BIG_PICTURE);
				break;
			case CROP_BIG_PICTURE:// from crop_big_picture
				changePhoto();
				break;
			case CHOOSE_BIG_PICTURE:
				changePhoto();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 监听返回按键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isEditState) {
				showSaveStateDialog();
			} else {
				showExitDialog();
			}
			return true;
		}
		return false;
	}

	private void showSaveStateDialog() {
		mDialog = new CommonCallbackDialog(this, R.style.TipDialog,
				new MyDialogListener() {
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						switch (view.getId()) {
						case R.id.btnCancle:
							isEditState = false;
							tempFile = null;
							mEtNickName.setText("");
							initLoginState();
							mDialog.hide();
							break;
						case R.id.btnSure:
							submitUserInfo();
							mDialog.hide();
							break;
						}
					}
				}, getString(R.string.tip), getString(R.string.user_save_info));
		mDialog.setContentView(R.layout.layout_self_tip_dialog);
		mDialog.show();
	}
}