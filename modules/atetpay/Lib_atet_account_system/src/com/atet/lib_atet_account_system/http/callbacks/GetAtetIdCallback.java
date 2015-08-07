package com.atet.lib_atet_account_system.http.callbacks;

import com.atet.lib_atet_account_system.model.AtetIdInfo;

public interface GetAtetIdCallback extends BaseCallback {
	/** 获取atetid信息成功 */
	public abstract void getAtetIdSuccessed(AtetIdInfo respData);

	/** 获取atetid信息错误 */
	public abstract void getAtetIdError();

	/** 获取atetid信息失败，从服务器返回了错误代码 */
	public abstract void getAtetIdFailed(int backCode);
}
