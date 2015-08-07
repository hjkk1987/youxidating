package com.sxhl.market.app;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.conn.HttpHostConnectException;

import android.content.Context;
import android.widget.Toast;

import com.sxhl.market.R;
import com.sxhl.market.model.exception.HttpRequestException;
import com.sxhl.market.model.exception.HttpResponseException;
import com.sxhl.market.model.exception.IllegalUrlException;
import com.sxhl.market.model.exception.ImageDownloadException;
import com.sxhl.market.model.exception.ImageFileNotFoundException;
import com.sxhl.market.model.exception.NetWorkNotFoundException;
import com.sxhl.market.model.exception.SDCardNotFoundException;
import com.sxhl.market.model.exception.ServerLogicalException;
import com.sxhl.market.model.exception.XmlParseException;
import com.sxhl.market.utils.DebugTool;

/**
 * @author time：2012-8-7 下午3:18:48 description:
 */
public class ExceptionHandler {
	/** 依附的Context */
	private Context context = null;

	/**
	 * 构建ExceptionHandler实例
	 * 
	 * @param context
	 *            依附的Context
	 */
	public ExceptionHandler(Context context) {
		this.context = context;
	}

	/**
	 * 处理异常.
	 * 
	 * @param e
	 *            异常实例
	 */
	public void handle(Exception e) {
		if (e == null) {
			return;
		}
		DebugTool.error(e.toString(), e);

		// 执行HTTP请求出错
		if (e instanceof HttpRequestException) {
			showToast(R.string.com_http_request_exception);
		}
		// HTTP响应数据格式有误
		else if (e instanceof HttpResponseException) {
			showToast(R.string.com_http_response_exception);
		}
		// 非法参数异常
		else if (e instanceof IllegalArgumentException) {
			showToast(R.string.com_illegal_argument_exception);
		}
		// 请求的图片URL格式不正确
		else if (e instanceof IllegalUrlException) {
			showToast(R.string.com_illegal_url_exception);
		}
		// 下载图片时出错
		else if (e instanceof ImageDownloadException) {
			showToast(R.string.com_image_download_exception);
		}
		// 访问的本地图片文件不存在
		else if (e instanceof ImageFileNotFoundException) {
			showToast(R.string.com_image_file_not_found_exception);
		}
		// 无网络连接异常
		else if (e instanceof NetWorkNotFoundException) {
			showToast(R.string.com_network_not_found_exception);
		}
		// SDCard不可用异常
		else if (e instanceof SDCardNotFoundException) {
			showToast(R.string.com_sdcard_not_found_exception);
		}
		// XML解析异常
		else if (e instanceof XmlParseException) {
			showToast(R.string.com_xml_parse_exception);
		} else if (e instanceof ServerLogicalException) {
			final ServerLogicalException ex = (ServerLogicalException) e;
			final int code = ex.getCode();
			handleServerLogical(code);
			// 连接服务器失败异常
		} else if (e instanceof HttpHostConnectException) {
			showToast(R.string.com_net_fail_exception);
		} else if (e instanceof UnknownHostException) {
			showToast(R.string.com_net_fail_exception);
		} else if (e instanceof SocketTimeoutException) {
			showToast(R.string.commom_net_prompt);
		}
		// 其他异常
		else {
			showToast(R.string.com_default_prompt_msg);
		}
	}

	private void handleServerLogical(int code) {
		final int msgId;
		switch (code) {
		// sys codes
		case ServerLogicalException.CODE_SYS_SUCCESS:
			msgId = R.string.code_sys_success;
			break;
		case ServerLogicalException.CODE_SYS_INVALID_OPTION:
			msgId = R.string.code_sys_invalid_option;
			break;
		case ServerLogicalException.CODE_SYS_INNER_ERROR:
			msgId = R.string.code_sys_inner_error;
			break;
		case ServerLogicalException.CODE_SYS_JSON_PARSE_ERROR:
			msgId = R.string.code_sys_json_parse_error;
			break;
		case ServerLogicalException.CODE_SYS_REQUEST_PARAMS_ERROR:
			msgId = R.string.code_sys_request_params_error;
			break;
		case ServerLogicalException.CODE_SYS_INVALID_TOKEN:
			msgId = R.string.code_sys_invalid_token;
			break;

		// token
		case ServerLogicalException.CODE_TOKEN_CREATE_ERROR:
			msgId = R.string.code_token_create_error;
			break;

		// sign in
		case ServerLogicalException.CODE_SIGN_ID_EMPTY:
			msgId = R.string.code_sign_id_empty;
			break;
		case ServerLogicalException.CODE_SIGN_PWD_EMPTY:
			msgId = R.string.code_sign_pwd_empty;
			break;
		case ServerLogicalException.CODE_SIGN_IMEI_EMPTY:
			msgId = R.string.code_sign_imei_empty;
			break;
		case ServerLogicalException.CODE_SIGN_SIGNATURE_EMPTY:
			msgId = R.string.code_sign_signature_empty;
			break;
		case ServerLogicalException.CODE_SIGN_SIGNATURE_PWD_EMPTY:
			msgId = R.string.code_sign_signature_pwd_empty;
			break;
		case ServerLogicalException.CODE_SIGN_USER_EXIST:
			msgId = R.string.code_sign_user_exist;
			break;
		case ServerLogicalException.CODE_SIGN_USER_NOT_EXIST:
			msgId = R.string.code_sign_user_not_exist;
			break;
		case ServerLogicalException.CODE_SIGN_USER_PWD_ERROR:
			msgId = R.string.code_sign_user_pwd_error;
			break;
		case ServerLogicalException.CODE_SIGN_GET_PKEY_ERROR:
			msgId = R.string.code_sign_get_pkey_error;
			break;
		case ServerLogicalException.CODE_SIGN_SIGNATURE_INVALID:
			msgId = R.string.code_sign_signature_invalid;
			break;

		// pay
		case ServerLogicalException.CODE_PAY_ID_OR_PWD_EMPTY:
			msgId = R.string.code_pay_id_or_pwd_empty;
			break;
		case ServerLogicalException.CODE_PAY_USER_NOT_EXIST:
			msgId = R.string.code_pay_user_not_exist;
			break;
		case ServerLogicalException.CODE_PAY_USER_PWD_ERROR:
			msgId = R.string.code_pay_user_pwd_error;
			break;
		case ServerLogicalException.CODE_PAY_NOT_ENOUGH_MONEY:
			msgId = R.string.code_pay_not_enough_money;
			break;
		case ServerLogicalException.CODE_PAY_CONFLICT:
			msgId = R.string.code_pay_conflict;
			break;

		// prepaid
		case ServerLogicalException.CODE_PREPAID_USER_NOT_EXIST:
			msgId = R.string.code_prepaid_user_not_exist;
			break;
		case ServerLogicalException.CODE_PREPAID_ACCOUNT_NOT_EXIST:
			msgId = R.string.code_prepaid_account_not_exist;
			break;
		case ServerLogicalException.CODE_PREPAID_3TD_PART_ERROR:
			msgId = R.string.code_prepaid_3td_part_error;
			break;
		case ServerLogicalException.CODE_PREPAID_CONFLICT:
			msgId = R.string.code_prepaid_conflict;
			break;

		// order
		case ServerLogicalException.CODE_ORDER_NOT_EXIST_BY_ORDERID:
			msgId = R.string.code_order_not_exist_by_orderid;
			break;
		case ServerLogicalException.CODE_ORDER_NOT_EXIST_BY_USERID:
			msgId = R.string.code_order_not_exist_by_userid;
			break;

		// game system!
		case ServerLogicalException.CODE_GAME_NO_REQUIRED_DATA:
			msgId = R.string.code_game_no_required_data;
			break;
		case ServerLogicalException.CODE_GAME_ID_NOT_EXIST:
			msgId = R.string.code_game_id_not_exist;
			break;
		// square
		case ServerLogicalException.CODE_SQUARE_POST_FAIL:
			msgId = R.string.code_square_query_post_fail;
			break;
		case ServerLogicalException.CODE_SQUARE_UPPOST_FAIL:
			msgId = R.string.code_square_uppost_fail;
			break;
		case ServerLogicalException.CODE_SQUARE_REPLY_POST_FAIL:
			msgId = R.string.code_square_reply_post_fail;
			break;
		case ServerLogicalException.CODE_SQUARE_QUERY_POST_FAIL:
			msgId = R.string.code_square_query_post_fail;
			break;
		// comment
		case ServerLogicalException.CODE_COMMENT_REPLY_FAIL:
			msgId = R.string.code_comment_reply_fail;
			break;
		// user
		case ServerLogicalException.CODE_USER_FEEDBACK_FAIL:
			msgId = R.string.code_user_feedback_fail;
			break;
		case ServerLogicalException.CODE_USER_MODIFY_FAIL:
			msgId = R.string.code_user_modify_fail;
			break;
		case ServerLogicalException.CODE_USER_QUERY_FAIL:
			msgId = R.string.code_user_query_fail;
			break;
		case ServerLogicalException.CODE_USER_INSERT_FAIL:
			msgId = R.string.code_user_insert_fail;
			break;
		default:
			// default toast message
			msgId = R.string.com_default_prompt_msg;
			break;
		}

		if (msgId > 0) {
			showToast(msgId);
		}
	}

	/**
	 * 显示提示信息.
	 * 
	 * @param resId
	 *            提示信息的资源id
	 */
	private void showToast(int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

}
