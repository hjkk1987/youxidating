package com.atet.lib_atet_account_system;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.atet.lib_atet_account_system.http.FindPwdHttpParams;
import com.atet.lib_atet_account_system.http.GetDeviceInfoHttpParams;
import com.atet.lib_atet_account_system.http.LoginHttpParams;
import com.atet.lib_atet_account_system.http.RegisterHttpParams;
import com.atet.lib_atet_account_system.http.callbacks.FindPwdCallback;
import com.atet.lib_atet_account_system.http.callbacks.GetAtetIdCallback;
import com.atet.lib_atet_account_system.http.callbacks.GetDeviceInfoCallback;
import com.atet.lib_atet_account_system.http.callbacks.LoginCallback;
import com.atet.lib_atet_account_system.http.callbacks.RegisterCallback;
import com.atet.lib_atet_account_system.http.request.GsonRequest;
import com.atet.lib_atet_account_system.model.AtetIdInfo;
import com.atet.lib_atet_account_system.model.DeviceInfo;
import com.atet.lib_atet_account_system.model.DeviceRespInfo;
import com.atet.lib_atet_account_system.model.FindPwdRespInfo;
import com.atet.lib_atet_account_system.model.LoginRespInfo;
import com.atet.lib_atet_account_system.model.UserInfo;
import com.atet.lib_atet_account_system.params.Constant;
import com.atet.lib_atet_account_system.params.Constant.EMPTY_INPUT_TYPE;
import com.atet.lib_atet_account_system.utils.AESCryptoUtils;
import com.atet.lib_atet_account_system.utils.FindPwdHelper;
import com.atet.lib_atet_account_system.utils.GetAtetIdInfoHelper;
import com.atet.lib_atet_account_system.utils.GetDeviceInfoHelper;
import com.atet.lib_atet_account_system.utils.LoginHelper;
import com.atet.lib_atet_account_system.utils.NetUtil;
import com.atet.lib_atet_account_system.utils.RegisterHelper;
import com.atet.lib_atet_account_system.utils.StringsVerifyUitls;
import com.atet.lib_atet_account_system.utils.Utils;

/**
 * 用户系统中的核心类，用来执行登陆，注册，找回密码等各项基本的任务
 * @author zhaominglai
 * @date 2014/7/22
 * 
 * */
public class ATETUser implements IATETUser, GetDeviceInfoCallback,
		GetAtetIdCallback {
	// 登陆时的Post参数对象
	LoginHttpParams mLoginParams;
	// 注册时的Post参数对象
	RegisterHttpParams mRegParams;
	// 找回密码时的Post参数对象
	FindPwdHttpParams mFindParams;

	GetDeviceInfoHttpParams mDevParmas;
	// 登陆操作的辅助类对象
	LoginHelper mLoginHelper;
	// 注册操作的辅助类对象
	RegisterHelper mRegisterHelper;
	// 找回密码的辅助类对象
	FindPwdHelper mFindPwdHelper;

	GetDeviceInfoHelper mGetDevInfoHelper;

	GetAtetIdInfoHelper mGetAtetIdInfoHelper;

	// 所有用户系统相关的网络请求的队列
	static RequestQueue mQueue;
	SharedPreferences mUserSp;
	SharedPreferences mDeviceInfoSP;

	LoginCallback tmpLogin;
	RegisterCallback tmpReg;
	FindPwdCallback tmpFind;

	private int DEVICE_TYPE = -1;
	private String DEVICE_ID = null;
	private String DEVICE_CODE = null;
	private String CHANNEL_ID = null;
	private String PRODUCT_ID = null;
	private String ATET_ID = null;
	private String LOGIN_TAG = "login";
	private String LOGIN_RETRY_TAG = "login1";
	private String LOGIN_RETRY1_TAG = "login2";

	private String REG_TAG = "reg";
	private String REG_RETRY_TAG = "reg1";
	private String REG_RETRY1_TAG = "reg2";

	private String FIND_TAG = "find";
	private String FIND_RETRY_TAG = "find1";
	private String FIND_RETRY1_TAG = "find2";

	String mUserId;

	// public static ATETUser mUser;

	Context mContext;
	/** 密码，用于保存在本地 */
	private String mPwd;

	public ATETUser(Context context) {
		// TODO Auto-generatdsed constructor stub
		mContext = context;
		PRODUCT_ID = Utils.getDNumber(context, context.getContentResolver());
		DEVICE_CODE = Utils.getClientType(context.getContentResolver());
		if (ATET_ID != null) {
			Log.e("ATETID", ATET_ID);
		} else {
			Log.e("ATETID", "nill");
		}

		if (!initDeviceInfo(context)) {
			retreiveDeviceInfo(this);
		}
		if (!isInitAtetId(context)) {
			getAtetIdFromNet(this);
		}
	}

	/**
	 * 初始化设备信息
	 * 
	 * @return true 代表本地存在有效的信息 false 代表本地无DeviceInfo信息
	 * */
	private boolean initDeviceInfo(Context context) {
		if (DEVICE_ID == null) {
			DeviceInfo dev = getDeviceInfoFromLocal(context);
			CHANNEL_ID = dev.getChannelId();
			DEVICE_ID = dev.getDeviceId();
			DEVICE_TYPE = dev.getDeviceType();

			if (DEVICE_ID == null)
				return false;
		}

		return true;
	}

	private boolean isInitAtetId(Context context) {
		if (ATET_ID == null) {
			ATET_ID = getAtetIdFromLocal(context);
			if (ATET_ID == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查设备网络是否可用
	 * 
	 * @author zhaominglai
	 * @date 2014/8/5
	 * */
	public boolean checkNetAvailable() {
		if (mContext == null)
			Log.e("debug context", "null");
		return NetUtil.isNetworkAvailable(this.mContext, true);
	}

	/**
	 * 登陆操作函数
	 * 
	 * @param loginName
	 *            用户名
	 * @param pwd
	 *            密码
	 * @param deviceType
	 *            设备类型
	 * @param loginCallBack
	 *            登陆界面的回调对象
	 * */
	@Override
	public boolean login(String loginName, String pwd,
			LoginCallback loginCallBack) {
		// TODO Auto-generated method stub

		/** 如果当前网络不可用，就返回 */
		if (!checkNetAvailable()) {
			loginCallBack.netIsNotAvailable();
			return false;
		}
		// 如果输入的参数格式不正确，则返回
		if (!verifyLoginParams(loginName, pwd, loginCallBack)) {
			return false;
		}

		mLoginHelper = new LoginHelper();
		mLoginParams = mLoginHelper.getLoginParams(loginName, pwd, DEVICE_TYPE);

		try {
			mPwd = AESCryptoUtils.encrypt(Constant.AES_SEED, pwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);

		GsonRequest<LoginRespInfo> loginRequest = mLoginHelper.getLoginRequest(
				mLoginParams, loginCallBack, this, Constant.FIRST_CONNECT_TRY);
		loginRequest.setTag(LOGIN_TAG);
		mQueue.add(loginRequest);

		tmpLogin = loginCallBack;
		return false;
	}

	/**
	 * 登陆操作函数,第二次尝试
	 * 
	 * @param loginName
	 *            用户名
	 * @param pwd
	 *            密码
	 * @param deviceType
	 *            设备类型
	 * @param loginCallBack
	 *            登陆界面的回调对象
	 * */

	public boolean retryLogin() {
		// TODO Auto-generated method stub

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);

		System.out.println("retry login");

		if (tmpLogin != null) {
			GsonRequest<LoginRespInfo> loginRequest1 = mLoginHelper
					.getLoginRequest(mLoginParams, tmpLogin, this,
							Constant.SECOND_CONNECT_TRY);
			loginRequest1.setTag(LOGIN_TAG);
			mQueue.add(loginRequest1);
		}
		return false;
	}

	/**
	 * 登陆操作函数,第三次尝试
	 * 
	 * @param loginName
	 *            用户名
	 * @param pwd
	 *            密码
	 * @param deviceType
	 *            设备类型
	 * @param loginCallBack
	 *            登陆界面的回调对象
	 * */

	public boolean retryLogin1() {
		// TODO Auto-generated method stub

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);

		System.out.println("retry login2");

		if (tmpLogin != null) {
			GsonRequest<LoginRespInfo> loginRequest2 = mLoginHelper
					.getLoginRequest(mLoginParams, tmpLogin, this,
							Constant.THIRD_CONNECT_TRY);
			loginRequest2.setTag(LOGIN_TAG);
			mQueue.add(loginRequest2);
		}
		return false;
	}

	/**
	 * @param context
	 */

	// private String getAtetId() {
	// // TODO Auto-generated method stub
	// return null;
	// }

	public void cancleLogin() {
		if (mQueue != null) {
			mQueue.cancelAll(LOGIN_TAG);
		}
	}

	public void cancleRegister() {
		if (mQueue != null) {
			mQueue.cancelAll(this);
		}
	}

	public void cancleFindPwd() {
		if (mQueue != null) {
			mQueue.cancelAll(this);
		}
	}

	@Override
	public boolean register(String atetId, String nickName, String loginName,
			String pwd, String rePwd, String email, String phone,
			RegisterCallback registerCallback) {
		// TODO Auto-generated method stub

		/** 如果当前网络不可用，就返回 */
		if (!checkNetAvailable()) {
			registerCallback.netIsNotAvailable();
			return false;
		}

		if (!verifyRegParams(nickName, loginName, pwd, rePwd, email, phone,
				registerCallback))
			return false;

		System.out.println("reg ");

		mRegisterHelper = new RegisterHelper();

		// if (ATET_ID==null) {
		// ATET_ID = "00000";
		// }
		if (atetId == null || atetId.equals("")) {
			atetId = ATET_ID;
		}
		mRegParams = mRegisterHelper.getRegisterParams(atetId, DEVICE_ID,
				DEVICE_CODE, PRODUCT_ID, CHANNEL_ID, nickName, loginName, pwd,
				email, phone);

		try {
			mPwd = AESCryptoUtils.encrypt(Constant.AES_SEED, pwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);

		GsonRequest<LoginRespInfo> registerRequest = mRegisterHelper
				.getRegRequest(mRegParams, registerCallback, this,
						Constant.FIRST_CONNECT_TRY);
		registerRequest.setTag(this);
		mQueue.add(registerRequest);

		tmpReg = registerCallback;
		return false;

	}

	/** 向网络发送请求参数之前，先校验参数的合法性 */
	private boolean verifyRegParams(String nickName, String loginName,
			String pwd, String rePwd, String email, String phone,
			RegisterCallback registerCallback) {
		// TODO Auto-generated method stub

		int nickResultCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_NICKNAME, nickName);

		if (nickResultCode == StringsVerifyUitls.STATUS_CODE_NULL) {
			registerCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_NICKNAME);
			return false;
		} else if (nickResultCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			registerCallback.nickNameInputInvailed();
			return false;
		}

		int userResultCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_USERNAME, loginName);

		if (userResultCode == StringsVerifyUitls.STATUS_CODE_NULL) {
			registerCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_USERNAME);
			return false;
		} else if (userResultCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			registerCallback.userInputInvailed();
			return false;
		}

		int emailCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_EMAIL, email);

		if (emailCode == StringsVerifyUitls.STATUS_CODE_NULL) {
			registerCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_EMAIL);
			return false;
		} else if (emailCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			registerCallback.emailInputInvailed();
			return false;
		}

		int pwdCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_PWD, pwd);

		if (pwdCode == StringsVerifyUitls.STATUS_CODE_NULL) {
			registerCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_PWD);
			return false;
		} else if (pwdCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			registerCallback.pwdInputInvailed();
			return false;
		}

		// 如果输入的密码与确认密码不一致的情况
		if (rePwd.equals("")) {
			registerCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_REPWD);
			return false;
		} else if (!pwd.equals(rePwd)) {
			registerCallback.twoPwdIsNotSame();
			return false;
		}

		int phoneCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_PHONE, phone);

//		if (phoneCode == StringsVerifyUitls.STATUS_CODE_NULL) {
//			registerCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_PHONE);
//			return false;
//		} else 
			if (phoneCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			registerCallback.phoneInputInvailed();
			return false;
		}

		return true;
	}

	public boolean retryRegister() {
		// TODO Auto-generated method stub

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);

		System.out.println("retry reg ");
		GsonRequest<LoginRespInfo> registerRequest = mRegisterHelper
				.getRegRequest(mRegParams, tmpReg, this,
						Constant.SECOND_CONNECT_TRY);
		registerRequest.setTag(this);
		mQueue.add(registerRequest);

		// mQueue.add(mRegisterHelper.getRegRequest(mRegParams,
		// tmpReg,this,Constant.SECOND_CONNECT_TRY));
		return false;

	}

	public boolean retryRegister1() {
		// TODO Auto-generated method stub

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);

		System.out.println("retry reg1 ");

		GsonRequest<LoginRespInfo> registerRequest = mRegisterHelper
				.getRegRequest(mRegParams, tmpReg, this,
						Constant.THIRD_CONNECT_TRY);
		registerRequest.setTag(this);
		mQueue.add(registerRequest);
		return false;

	}

	/**
	 * 找回密码功能
	 * 
	 * @author zhaominglai
	 * @param userName
	 *            用户名
	 * @param email
	 *            邮箱
	 * @param findCallback
	 *            操作结果的回调对象
	 * 
	 * */
	@Override
	public boolean findPassword(String userName, String email,
			FindPwdCallback findCallback) {
		// TODO Auto-generated method stub

		/** 如果当前网络不可用，就返回 */
		if (!checkNetAvailable()) {
			findCallback.netIsNotAvailable();
			return false;
		}

		/** 如果输入的参数秒不正确，就返回。 */
		if (!verifyFindPwdParams(userName, email, findCallback)) {
			return false;
		}
		// 创建找回密码的辅助类对象
		mFindPwdHelper = new FindPwdHelper();
		mFindParams = mFindPwdHelper.getFindPwdParams(userName, email);

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);

		// 将找回密码的网络请求加入到Volley中的网络队列当中
		GsonRequest<FindPwdRespInfo> findRequest = mFindPwdHelper
				.getFindPwdRequest(mFindParams, findCallback, this,
						Constant.FIRST_CONNECT_TRY);
		findRequest.setTag(this);
		mQueue.add(findRequest);
		tmpFind = findCallback;
		return true;
	}

	private void retryFindPwd() {
		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);
		System.out.println("retry find");
		// 将找回密码的网络请求加入到Volley中的网络队列当中
		GsonRequest<FindPwdRespInfo> findRequest = mFindPwdHelper
				.getFindPwdRequest(mFindParams, tmpFind, this,
						Constant.SECOND_CONNECT_TRY);
		findRequest.setTag(this);
		mQueue.add(findRequest);
	}

	private void retryFindPwd1() {
		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);
		System.out.println("retry find1");
		// 将找回密码的网络请求加入到Volley中的网络队列当中
		GsonRequest<FindPwdRespInfo> findRequest = mFindPwdHelper
				.getFindPwdRequest(mFindParams, tmpFind, this,
						Constant.THIRD_CONNECT_TRY);
		findRequest.setTag(this);
		mQueue.add(findRequest);
	}

	/**
	 * 检查找回密码模块的输入参数格式是否正确
	 * 
	 * @author zhaominglai
	 * @date 2014/8/5
	 * **/
	private boolean verifyFindPwdParams(String userName, String email,
			FindPwdCallback findCallback) {
		int userResultCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_USERNAME, userName);

		if (userResultCode == StringsVerifyUitls.STATUS_CODE_NULL) {
			findCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_USERNAME);
			return false;
		} else if (userResultCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			findCallback.userInputInvailed();
			return false;
		}

		int emailResultCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_EMAIL, email);

		if (emailResultCode == StringsVerifyUitls.STATUS_CODE_NULL) {
			findCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_EMAIL);
			return false;
		} else if (emailResultCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			findCallback.emailInputInvailed();
			return false;
		}

		return true;
	}

	/**
	 * 检查登录模块模块的输入参数格式是否正确
	 * 
	 * @author zhaominglai
	 * @date 2014/8/5
	 * **/
	private boolean verifyLoginParams(String userName, String pwd,
			LoginCallback loginCallback) {
		int userResultCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_USERNAME, userName);

		if (userResultCode == StringsVerifyUitls.STATUS_CODE_NULL) {
			loginCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_USERNAME);
			return false;
		} else if (userResultCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			loginCallback.invailedLoginNameParam();
			return false;
		}

		int pwdResultCode = StringsVerifyUitls.verifyStringsFormat(
				StringsVerifyUitls.TYPE_PWD, pwd);

		if (pwdResultCode == StringsVerifyUitls.STATUS_CODE_NULL) {
			loginCallback.someInputIsEmpte(EMPTY_INPUT_TYPE.EMPTY_PWD);
			return false;
		} else if (pwdResultCode == StringsVerifyUitls.STAUS_CODE_INVALIDATE) {
			/** 调用回调函数 */
			loginCallback.invailedPwdParam();
			return false;
		}

		return true;
	}

	/**
	 * 初始化用户信息
	 * 
	 * */
	public static void init(String channelId, String deviceId, String deviceCode) {
		/*
		 * if (!checkLocaleDeveceInfo()) getDeviceInfo();
		 */
	}

	/**
	 * 向服务器获取atetid
	 * */
	private void getAtetIdFromNet(GetAtetIdCallback callback) {
		// TODO Auto-generated method stub
		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);
		if (mGetAtetIdInfoHelper == null)
			mGetAtetIdInfoHelper = new GetAtetIdInfoHelper();
		mQueue.add(mGetAtetIdInfoHelper.getAtetIdInfoRequest(
				mGetAtetIdInfoHelper.getAtetIdInfoParams(mContext), callback));

		// .getDevInfoRequest(mGetDevInfoHelper
		// .getDevInfoParams(Constant.DEVICE_CHANNEL_ID, PRODUCT_ID,
		// DEVICE_CODE, DEVICE_TYPE), callBack));
	}

	/**
	 * 向服务器获取DeviceInfo
	 * */
	public void retreiveDeviceInfo(GetDeviceInfoCallback callBack) {
		if (mQueue == null)
			mQueue = Volley.newRequestQueue(mContext);

		if (mGetDevInfoHelper == null)
			mGetDevInfoHelper = new GetDeviceInfoHelper();

		DEVICE_TYPE = Constant.DEVICE_TYPE;

		mQueue.add(mGetDevInfoHelper.getDevInfoRequest(mGetDevInfoHelper
				.getDevInfoParams(Constant.DEVICE_CHANNEL_ID, PRODUCT_ID,
						DEVICE_CODE, DEVICE_TYPE), callBack));
	}

	/**
	 * 向后台请求设备信息成功时的回调
	 * 
	 * */
	@Override
	public void getInfoSuccessed(DeviceRespInfo respData) {
		// TODO Auto-generated method stub

		setDeviceInfo(mContext, respData);
	}

	@Override
	public void getInfoError() {
		// TODO Auto-generated method stub
		Log.e("deviceIdError", "请求deviceId失败");
	}

	@Override
	public void getInfoFailed(int backCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserId() {
		// TODO Auto-generated method stub

		/*
		 * if (mUser == null) { mUserSp.getString(Constant.SP_USER_ID, ""); }
		 */
		return null;
	}

	/**
	 * 设置服务器后台反馈的用户信息
	 * 
	 * */
	@Override
	public void setUserInfo(LoginRespInfo userInfo) {
		// TODO Auto-generated method stub

		if (mUserSp == null)
			mUserSp = mContext.getSharedPreferences(Constant.SP_USER,
					Context.MODE_PRIVATE);

		mUserSp.edit().putInt(Constant.SP_USER_ID, userInfo.getUserId())
				.putString(Constant.SP_USER_NAME, userInfo.getLoginName())
				.putString(Constant.SP_USER_NICKNAME, userInfo.getNickName())
				.putString(Constant.SP_USER_PWD, mPwd)
				.putBoolean(Constant.LOGIN_USER_ALREADY_LOGIN, true)
				.putString(Constant.SP_USER_ICON, userInfo.getIcon())
				.putString(Constant.SP_USER_EMAIL, userInfo.getEmail())
				.putString(Constant.SP_USER_PHONE, userInfo.getPhone())
				.commit();

	}

	@Override
	public void netIsNotAvailable() {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取登陆用户的信息
	 * 
	 * */
	public UserInfo getUserInfo() {
		if (mUserSp == null)
			mUserSp = mContext.getSharedPreferences(Constant.SP_USER,
					Context.MODE_PRIVATE);

		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(mUserSp.getInt(Constant.SP_USER_ID, -1));
		userInfo.setUserName(mUserSp.getString(Constant.SP_USER_NAME, null));
		userInfo.setNickName(mUserSp.getString(Constant.SP_USER_NICKNAME, null));
		userInfo.setPwd(mUserSp.getString(Constant.SP_USER_PWD, ""));
		userInfo.setIcon(mUserSp.getString(Constant.SP_USER_ICON, null));
		userInfo.setEmail(mUserSp.getString(Constant.SP_USER_EMAIL, null));
		userInfo.setPhone(mUserSp.getString(Constant.SP_USER_PHONE, null));
		return userInfo;
	}

	/**
	 * 注销登陆账户的信息
	 * 
	 * */
	public void clearUserInfo() {
		if (mUserSp == null && mContext != null)
			mUserSp = mContext.getSharedPreferences(Constant.SP_USER,
					Context.MODE_PRIVATE);

		mUserSp.edit().clear().commit();

	}

	/**
	 * 释放资源
	 * 
	 * */
	public void releaseResource() {
		/*
		 * if (mContext != null) { mContext = null; }
		 */
		if (mQueue != null) {
			mQueue = null;

			tmpFind = null;
			tmpReg = null;
			tmpLogin = null;
		}

		/*
		 * if (mUser != null) { mUser = null; }
		 */

	}

	/**
	 * 从本地获取DeviceInfo
	 * 
	 * @author zhaominglai
	 * @date 2014/8/12
	 * 
	 * */
	public DeviceInfo getDeviceInfoFromLocal(Context context) {
		DeviceInfo dev = new DeviceInfo();

		SharedPreferences mSP = context.getSharedPreferences(
				Constant.SP_DEVICE, Context.MODE_PRIVATE);

		dev.setChannelId(mSP.getString(Constant.SP_CHANNEL_ID, null));
		dev.setDeviceId(mSP.getString(Constant.SP_DEVICE_ID, null));
		dev.setDeviceType(mSP.getInt(Constant.SP_DEVICE_TYPE, -1));

		return dev;

	}

	private String getAtetIdFromLocal(Context context) {
		// TODO Auto-generated method stub

		SharedPreferences mSP = context.getSharedPreferences(
				Constant.SP_DEVICE, Context.MODE_PRIVATE);
		return mSP.getString(Constant.SP_ATET_ID, null);
	}

	/**
	 * 将后台获取到的设备信息保存到本地
	 * */
	public void setDeviceInfo(Context context, DeviceRespInfo respData) {
		DEVICE_ID = respData.getDeviceId();
		DEVICE_TYPE = respData.getType();
		CHANNEL_ID = respData.getChannelId();
		SharedPreferences mSP = context.getSharedPreferences(
				Constant.SP_DEVICE, Context.MODE_PRIVATE);
		mSP.edit().putString(Constant.SP_CHANNEL_ID, CHANNEL_ID)
				.putString(Constant.SP_DEVICE_ID, DEVICE_ID)
				.putInt(Constant.SP_DEVICE_TYPE, DEVICE_TYPE).commit();
	}

	@Override
	public void retryConnect(Constant.RETRY_TYPE type) {
		// TODO Auto-generated method stub
		switch (type) {
		case TYPE_LOGIN:
			retryLogin();
			break;

		case TYPE_REG:
			retryRegister();
			break;

		case TYPE_FINDPWD:
			retryFindPwd();
			break;

		case TYPE_LOGIN1:
			retryLogin1();
			break;

		case TYPE_REG1:
			retryRegister1();
			break;

		case TYPE_FINDPWD1:
			retryFindPwd1();
			break;

		default:
			break;
		}
	}

	@Override
	public void someInputIsEmpte(EMPTY_INPUT_TYPE type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAtetIdSuccessed(AtetIdInfo respData) {
		// TODO Auto-generated method stub
		ATET_ID = respData.getAtetId();
		SharedPreferences sp = mContext.getSharedPreferences(
				Constant.SP_DEVICE, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(Constant.SP_ATET_ID, ATET_ID).commit();
	}

	@Override
	public void getAtetIdError() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAtetIdFailed(int backCode) {
		// TODO Auto-generated method stub
		Log.e("reponse", "错误代码" + backCode);
	}
}
