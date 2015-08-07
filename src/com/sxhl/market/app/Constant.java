package com.sxhl.market.app;

import android.os.Environment;
import android.view.KeyEvent;

/**
 * @author time：2012-8-6 下午6:44:10 description: 系统常量
 */
public class Constant {
	/** 程序包名 **/
	public static final String Market_Package = "com.sxhl.market";
	/** 应用ID **/
	public static final String APP_ID = "A1A0A0000";
	/** 用于存放上次检测游戏版本时间sp */
	public static final String LAST_CHECK_GAME_UPDATE_TIME = "lastCheckGameUpdateTime";
	/** 用于存放游戏版本的sp */
	public static final String NEW_VERSION_INFO_SP = "newVersionInfoSp";
	/** 用于存放设备信息的SP */
	public static final String DEVICE_INFO_SP = "deviceInfo";
	/** 用于是否有新礼包信息的SP */
	public static final String SP_HAS_NEW_GIFT="hasNewGiftSp";
	/** 用于是否有新礼包信息的SP */
	public static final String SP_VALUE_HAS_NEW_GIFT ="hasNewGiftSpValue";

	public static final String GIFTINTENTKEY = "GIFTINTENTKEY";
	public static int ExtraData = 2;
	/** 用户找回密码**/
	public static int TASK_FIND_PASSWORD = 6001;
	
	/** 用于存放设备的chanelId */
	public static final String DEVICE_CHANNEL_ID = "DEVICE_CHANNEL_ID";
	/** 用于存放设备的deviceId */
	public static final String DEVICE_DEVICE_ID = "DEVICE_DEVICE_ID";
	/** 用于存放设备的type */
	public static final String DEVICE_TYPE = "DEVICE_TYPE";
	/** 用于判断是否成功获取设备id */
	public static final String IS_ALREADY_GET_DEVICE_ID = "IS_ALREADY_GET_DEVICE_ID";

	/** 无网络连接 */
	public static final int NETWORK_TYPE_NONE = -1;

	/** 通过WIFI连接网络 */
	public static final int NETWORK_TYPE_NET = 0;

	/** 通过GPRS连接网络 */
	public static final int NETWORK_TYPE_WAP = 1;

	/** 数据库的版本 **/
	public static final int DATABASE_VERSION = 22;

	/** 数据库的名称 **/
	public static final String DATABASE_NAME = "123box.db";

	/** 响应异常 */
	public static final String HTTP_RESPONSE_EXCEPTION = "响应异常";

	/** 登陆成功的提示 **/
	public static final String MESSAGE_LOGIN_SUCCESS = "登陆成功";

	/** 登陆失败 的提示 **/
	public static final String MESSAGE_LOGIN_FAIL = "登陆失败";

	/** 注销成功提示 **/
	public static final String MESSAGE_LOGOUT_SUCCESS = "注销成功";

	/** 注销失败提示 **/
	public static final String MESSAGE_LOGOUT_FAIL = "注销失败";

	/** 列表获取异常 **/
	public static final String MESSAGE_LIST_FAIL = "获取列表失败";

	/** 列表获取成功 **/
	public static final String MESSAGE_LIST_SUCCESS = "获取列表成功";

	/** 评论成功 **/
	public static final String MESSAGE_COMMENT_SUCCESS = "评论成功";

	/** 评论失败 **/
	public static final String MESSAGE_COMMENT_FAIL = "评论失败";

	/** 提交意见成功 **/
	public static final String MESSAGE_FEEDBACK_SUCCESS = "提交意见成功";

	/** 提交意见失败 **/
	public static final String MESSAGE_FEEDBACK_FAIL = "提交意见失败";

	/** 文件进度更新 */
	public static final int ACTION_PROGRESS_UPDATE = 100;

	/** 文件下载上传状态_开始 */
	public static final int STATE_START_CODE = 101;
	public static final String STATE_START_MES = "文件开始下载...";

	/** 文件下载上传状态_暂停 */
	public static final int STATE_PAUSE_CODE = 102;
	public static final String STATE_PAUSE_MES = "暂停...";
	/** 文件下载上传状态_取消 */
	public static final int STATE_STOP_CODE = 103;
	public static final String STATE_STOP_MES = "停止...";

	/** 文件下载上传状态_结束 */
	public static final int STATE_DONE_CODE = 104;
	public static final String STATE_DONE_MES = "完毕...";

	/** 文件下载上传状态_无任务 */
	public static final int STATE_NOTASK_CODE = 105;

	/** 文件长度KEY **/
	public static final String KEY_FILE_LENGTH = "filelength";

	/** 文件已经下载或者是上传的长度KEY **/
	public static final String KEY_FINSHED_LENGTH = "filefinishedlength";

	/** Adapter的适配类型： 图标 内容 》 */
	public static final int ADAPTER_ICON_CONTEXT = 106;

	/** Adapter的适配类型： 索引 内容 》 */
	public static final int ADAPTER_INDEX_CONTEXT = 107;

	/** 文件上传标识 **/
	public static final int ACTION_DOWNLOAD = 108;
	/** 文件下载的标识 **/
	public static final int ACTION_UPLOAD = 109;

	/** Adapter的适配类型： 索引 内容 类型》 */
	public static final int ADAPTER_INDEX_CONTEXT_TYPE = 114;

	public static final String INTENT_ACTION_LOGGED_OUT = "com.sxhl.market.APP.INTENT_ACTION_LOGGED_OUT";
	/**
	 * 游戏模块 获取游戏专题类别taskKey
	 */
	public static final int TASKKEY_GAME_TYPELIST = 1000;

	/**
	 * 回复帖子评论列表 获取帖子回复列表taskKey
	 */
	public static final int TASKKEY_REPLY_COMMLIST = 1001;

	/**
	 * 游戏的评论列表taskKey
	 */
	public static final int TASKKEY_COMMEN_LIST = 1002;
	/**
	 * 游戏的回复taskKey
	 */
	public static final int TASKKEY_REPLY_COMMENT = 1007;

	/**
	 * 热门游戏gridView的taskKey
	 */
	public static final int TASKKEY_HOTGAME_LIST = 1003;
	/**
	 * 游戏的史诗列表taskKey
	 */
	public static final int TASKKEY_RECOMMED_LIST = 1004;
	/**
	 * 游戏的主题taskKey
	 */
	public static final int TASKKEY_GAME_TOPIC_LIST = 1005;

	/**
	 * 根据包名获取游戏版本信息taskKey
	 */
	public static final int TASKKEY_NEEDUPDATEGAME = 1006;

	/**
	 * 根据所有包名获取游戏版本信息taskKey
	 */
	public static final int TASKKEY_AllNEEDUPDATE = 1010;

	/**
	 * 
	 */
	public static final int USER_UPDATE = 1023;
	public static final int USER_UPDATE_PWD = 1024;
	public static final int USER_UPDATE_PWD_RETRY = 1025;
	public static final int USER_UPDATE_PWD_RETRY1 = 1026;

	/**
	 * 广场模块 XXXX的taskKey
	 */
	public static final int TASKKEY_SQUARE_ = 2000;

	/**
	 * 广场帖子模块 XXXX的taskKey
	 */
	public static final int TASKKEY_SQUARE_LIST = 2001;
	public static final int TASKKEY_SQUARE_UPPOST = 2002;
	public static final int TASKKEY_SQUARE_ONMORE = 2003;
	public static final int TASKKEY_SQUARE_ONREFRESH = 2004;
	public static final int TASKKEY_SQUARE_POSTCOMMENT = 2005;
	public static final int TASKKEY_SQUARE_SENDARTICLE = 2006;
	public static final int TASKKEY_SQUARE_FILEUPLOAD = 2007;
	public static final int TASKKEY_SQUARE_COMMENT_MORE = 2008;
	public static final int TASKKEY_SQUARE_COMMENT_REFRESH = 2009;
	/*** 帖子详情 **/
	public static final int TASKKEY_SQUARE_DETAIL = 4003;
	/**
	 * 用户模块 XXXX的taskKey
	 */
	public static final int TASKKEY_USER_ = 3000;
	/**
	 * 用户注册taskKey
	 */
	public static final int TASKKEY_REGISTER = 3001;
	/**
	 * 用户登录
	 */
	public static final int TASKKEY_LOGIN = 3002;
	/**
	 * 用户注销
	 */
	public static final int TASKKEY_LOGOUT = 3003;
	/**
	 * 用户消费列表获取
	 */
	public static final int TASKKEY_USER_CONSUME_LIST = 3004;
	/**
	 * 获取密钥task
	 */
	public static final int TASKKEY_TOKEN = 3005;
	/**
	 * 获取详细消费列表
	 */
	public static final int CONSUMERE_LIST = 3006;
	/**
	 * 充值请求
	 */
	public static final int TASKKEY_RECHARGE = 3007;

	/**
	 * 一件反馈
	 */
	public static final int TASKKEY_FEEDBACK = 3008;

	/**
	 * 横屏请求广告数据
	 */
	public static final int TASKKEY_BILL = 3009;

	/**
	 * 横屏精选游戏数据
	 */
	public static final int TASKKEY_CHOSEN = 4000;

	/*** 横屏游戏详情 ***/
	public static final int TASKKEY_LAND_DETAIL = 4001;
	/** 横屏游戏详情评论 ***/
	public static final int TASKKEY_LAND_COMMENT = 4002;
	/** 获取设备id **/
	public static final int TASKKEY_GET_DEVICE_ID = 4022;

	/** 竖屏游戏详情 **/
	public static final int TASKKEY_GAME_DETAIL = 4005;

	/** 获取下载次数和星级 **/
	public static final int TASKKEY_GAME_COUNT_STAR = 4006;

	/** 礼包相关 **/
	public static final int TASKKEY_GIFTCENTER_MYGIFT = 5001;
	public static final int TASKKEY_GIFT_MYGIFT = 5002;
	public static final int TASKKEY_GIFT_ALLGIFT = 5003;

	public static final String KEY_GAMEINFO = "KEY_GAMEINFO";
	// Handler What加载数据完毕
	public static final int WHAT_DID_LOAD_DATA = 10;
	// Handler What更新数据完毕
	public static final int WHAT_DID_REFRESH = 11;
	// Handler What更多数据完毕
	public static final int WHAT_DID_MORE = 12;
	// 加载热门游戏
	public static final int LOAD_HOT_GAME = 13;
	// 加载专题列表游戏
	public static final int LOAD_GAME_TOPIC = 14;
	// 史诗推荐加载项
	public static final int LOAD_RECOMMEND_DATA = 15;
	// 史诗推荐上下拉加载项
	public static final int LOAD_RECOMMEND_UP_DATA = 16;
	// 应用id
	public static final String APPID = "21";

	// 我的游戏状态
	public static final int GAME_STATE_NOT_DOWNLOAD = 0;
	public static final int GAME_STATE_NOT_INSTALLED = 1;
	public static final int GAME_STATE_INSTALLED = 2;
	public static final int GAME_STATE_NEED_UPDATE = 6;
	// public static final int GAME_STATE_DOWNLOADING=3;
	public static final int GAME_STATE_DOWNLOAD_ERROR = 4;
	// public static final int GAME_STATE_UPDATE=5;
	public static final int GAME_STATE_INSTALLED_SYSTEM = 0x100 | GAME_STATE_INSTALLED;
	public static final int GAME_STATE_INSTALLED_USER = 0x200 | GAME_STATE_INSTALLED;
	public static String SDCARD_ROOT = Environment.getExternalStorageState()
			.equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment
			.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";
	// 游戏下载保存目录
	public static String GAME_DOWNLOAD_LOCAL_DIR = SDCARD_ROOT
			+ "/sxhl/market/";
	// 游戏数据包存放目录
	public static String GAME_ZIP_DATA_LOCAL_DIR = SDCARD_ROOT + "/";
	public static final String TABLE_NAME_COLLECTION = "collectionInfo";

	public static final int ACTIVITY_STATE_INIT = 0;
	public static final int ACTIVITY_STATE_VISIBLE = 1;
	public static final int ACTIVITY_STATE_INVISIBLE = 2;
	public static final int ACTIVITY_STATE_DESTROY = 3;

	public static final int KEY_CODE_SWITCH_ORIENTATION = 142;
	// public static final int KEY_CODE_SWITCH_ORIENTATION=KeyEvent.KEYCODE_K;
	// public static final int KEY_CODE_CLICK=KeyEvent.KEYCODE_J;
	public static final int KEY_CODE_CLICK = KeyEvent.KEYCODE_A;
	public static final int SWITCH_MARKET_ORIENTATION_INTERVAL = 1200;

	// // 升级相关信息
	// public static final String GAMEPAD_PACKAGE_NAME = "com.atet.tvgamepad";
	// public static final String MARKET_PACKAGE_NAME = "com.atet.tcllauncher";
	// public static final String SETTING_PACKAGE_NAME = "com.atet.tclsettings";
	// public static final String GAMEPAD_APP_ID = "10101";
	// public static final String MARKET_APP_ID = "10102";
	// public static final String SETTING_APP_ID = "10103";
	// // public static final int UPDATE_INTERVAL = 24 * 3600 * 1000;
	// public static final int UPDATE_INTERVAL = 12 * 3600 * 1000;
	//
	// // assets\ATETGamepad.apk应用的版本号，如果不存在该文件，设为-100
	// public static final int GAMEPAD_SETTING_VERSION_CODE = 64;
	// // assets\ATETSettings.apk应用的版本号，如果不存在该文件，设为-100 海尔版
	// public static final int SETTINGS_VERSION_CODE = 1000000;
	//
	public static int DIALOG_MESSAGE_FONT_SIZE = 18;
	public static int DIALOG_BACKGROUND_ALPHA = 180;
	//

	public static int TASK_LOGIN = 1;
	public static int TASK_REGISTER = 2;
	public static int TASK_MODIFY_INFORMATION = 3;
	public static int TASK_MODIFY_PASSWORD = 6;
}
