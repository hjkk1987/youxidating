package com.sxhl.market.model.net.http.download;

/**
 * @ClassName: DownloadListenner
 * @Description: 下载状态监听
 * @author: Liuqin
 * @date 2012-12-29 下午1:45:05
 * 
 */
public interface DownloadListenner {
	/**
	 * @Title: onDownloadWait
	 * @Description: 进入等待队列
	 * @param fileDownInfo
	 * @throws
	 */
	public void onDownloadWait(FileDownInfo fileDownInfo);

	/**
	 * @Title: onDownloadStart
	 * @Description: 开始下载
	 * @param fileDownInfo
	 * @throws
	 */
	public void onDownloadStart(FileDownInfo fileDownInfo);

	/**
	 * @Title: onDownloadSized
	 * @Description: 获取到文件长度
	 * @param fileDownInfo
	 * @throws
	 */
	public void onDownloadSized(FileDownInfo fileDownInfo);

	/**
	 * @Title: onDownloadProgress
	 * @Description: 下载进度
	 * @param fileDownInfo
	 * @throws
	 */
	public void onDownloadProgress(FileDownInfo fileDownInfo);

	/**
	 * @Title: onDownloadStop
	 * @Description: 下载停止
	 * @param fileDownInfo
	 * @throws
	 */
	public void onDownloadStop(FileDownInfo fileDownInfo);

	/**
	 * @Title: onDownloadFinish
	 * @Description: 下载成功完成
	 * @param fileDownInfo
	 * @throws
	 */
	public void onDownloadFinish(FileDownInfo fileDownInfo);

	/**
	 * @Title: onDownloadCompleted
	 * @Description: 所有下载完成，下载队列为空，但不一定全部成功
	 * @throws
	 */
	public void onDownloadCompleted();

	/**
	 * @Title: onDownloadError
	 * @Description: 下载发生错误
	 * @param fileDownInfo
	 * @param errMsg
	 * @throws
	 */
	public void onDownloadError(FileDownInfo fileDownInfo, String errMsg);
}
