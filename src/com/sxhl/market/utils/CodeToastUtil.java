package com.sxhl.market.utils;

import android.content.Context;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.model.task.TaskResult;

public class CodeToastUtil {
	
	private static int ToastTime = 3000;
	
	/**查看是否有匹配的code
	 * false 无匹配code
	 * true 有匹配code*/
	public static boolean ToastByCode(Context context,int code){
		switch (code) {
		case TaskResult.ILLEGAL_OPERA:
			//非法操作
			Toast.makeText(context, R.string.code_sys_illegal_opera, ToastTime).show();
			return true;

		case TaskResult.SYSTEM_ERROR:
			//系统内部错误
			Toast.makeText(context, R.string.code_sys_inner_error, ToastTime).show();
			return true;
			
		case TaskResult.JSON_FORM_ERROR:
			//请求JSON格式解析错误
			Toast.makeText(context, R.string.code_sys_json_parse_error, ToastTime).show();
			return true;
			
		case TaskResult.REQUEST_DATA_ERROR:
			//请求参数错误
			Toast.makeText(context, R.string.code_sys_request_params_error, ToastTime).show();
			return true;
			
		case TaskResult.LOGIN_PARAME_ERROR:
			//登录参数错误
			Toast.makeText(context, R.string.launcher_account_code_login_param_error, ToastTime).show();
			return true;
			
		case TaskResult.USER_NAME_FORMAT_ERROR:
			//用户名格式错误
			Toast.makeText(context, R.string.launcher_account_code_username_format_error, ToastTime).show();
			return true;
			
		case TaskResult.CHINESE_NAME_FORMAT_ERROR:
			//中文名格式错误
			Toast.makeText(context, R.string.launcher_account_code_chinesename_format_error, ToastTime).show();
			return true;
			
		case TaskResult.PHONE_FORMAT_ERROR:
			//手机号码格式错误
			Toast.makeText(context, R.string.launcher_account_code_phone_format_error, ToastTime).show();
			return true;
			
		case TaskResult.EMAIL_FORMAT_ERROR:
			//邮箱格式错误
			Toast.makeText(context, R.string.launcher_account_code_email_format_error, ToastTime).show();
			return true;
			
		case TaskResult.WECHAT_FORMAT_ERROR:
			//微信号格式错误
			Toast.makeText(context, R.string.launcher_account_code_wechat_format_error, ToastTime).show();
			return true;
			
		case TaskResult.QQ_FORMAT_ERROR:
			//QQ格式错误
			Toast.makeText(context, R.string.launcher_account_code_qq_format_error, ToastTime).show();
			return true;
			
		case TaskResult.DEVICE_ID_FORMAT_ERROR:
			//设备ID格式错误
			Toast.makeText(context, R.string.launcher_account_code_deviceid_format_error, ToastTime).show();
			return true;
			
		case TaskResult.USER_PRIMARY_KEY_ERROR:
			//用户主键ID无效
			Toast.makeText(context, R.string.launcher_account_code_user_primary_key_error, ToastTime).show();
			return true;
			
		case TaskResult.OLD_PASSWORD_ERROR:
			//旧密码错误
			Toast.makeText(context, R.string.launcher_account_code_old_password_error, ToastTime).show();
			return true;
			
		case TaskResult.NEW_PASSWORD_ERROR:
			//新密码错误
			Toast.makeText(context, R.string.launcher_account_code_new_password_error, ToastTime).show();
			return true;
			
//		case TaskResult.EMAIL_NOT_MATCH_ERROR:
//			//邮箱不匹配
//			Toast.makeText(context, R.string.launcher_register_email_format_error, ToastTime).show();
//			return true;
			
		case TaskResult.NICKNAME_FORMAT_ERROR:
			//昵称格式错误
			Toast.makeText(context, R.string.launcher_account_nickname_format_error, ToastTime).show();
			return true;
			
		case TaskResult.USER_REGISTER_FAIL:
			//用户注册失败
			Toast.makeText(context, R.string.launcher_account_register_fail, ToastTime).show();
			return true;
			
		case TaskResult.USER_EXITS:
			//用户已存在
			Toast.makeText(context, R.string.launcher_account_user_exits, ToastTime).show();
			return true;
			
		case TaskResult.USER_NOT_EXITS:
			//用户不存在
			Toast.makeText(context, R.string.launcher_account_user_not_exits, ToastTime).show();
			return true;
			
		case TaskResult.USER_FROZEN:
			//用户已经被冻结
			Toast.makeText(context, R.string.launcher_account_user_frozen, ToastTime).show();
			return true;
			
		case TaskResult.USER_LOCKED:
			//用户已经被锁定
			Toast.makeText(context, R.string.launcher_account_user_locked, ToastTime).show();
			return true;
			
		case TaskResult.USER_PASSWORD_ERROR:
			//用户密码错误
			Toast.makeText(context, R.string.launcher_account_user_password_error, ToastTime).show();
			return true;
			
		case TaskResult.TWO_PASSWORD_NOT_MATCH:
			//两次输入的密码不匹配
			Toast.makeText(context, R.string.launcher_account_two_password_not_match, ToastTime).show();
			return true;
			
		case TaskResult.USER_NAME_NULL:
			Toast.makeText(context, R.string.launcher_account_user_name_null, ToastTime).show();
			//用户名为空
			return true;
			
		case TaskResult.USER_PASSWORD_NULL:
			Toast.makeText(context, R.string.launcher_account_password_null, ToastTime).show();
			//密码为空
			return true;
			
		case TaskResult.USER_EMAIL_NULL:
			//邮箱为空
			Toast.makeText(context, R.string.launcher_account_email_null, ToastTime).show();
			return true;
			
		case TaskResult.USER_NICK_NAME_NULL:
			//昵称为空
			Toast.makeText(context, R.string.launcher_account_nickname_null, ToastTime).show();
			return true;
			
		case TaskResult.DEVICE_ID_ERROR:
			//设备id错误
			Toast.makeText(context, R.string.launcher_account_deviceid_error, ToastTime).show();
			return true;
			
		case TaskResult.DEVICE_CODE_ERROR:
			//设备code错误
			Toast.makeText(context, R.string.launcher_account_device_code_error, ToastTime).show();
			return true;
			
//		case TaskResult.DEVICE_ID_NO_DATA:
//			//设备id没有符合要求的数据
//			Toast.makeText(context, R.string.code_device_id_no_required_data, ToastTime).show();
//			return true;
			
		case TaskResult.GAME_CLASSIFY_NO_DATA:
			//没有符合要求的数据(游戏分类)
//			Toast.makeText(context, R.string.code_device_id_no_required_data, ToastTime).show();
			return true;
		}
		return false;
		
	}

}
