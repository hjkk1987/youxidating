package com.sxhl.market.model.net.http.download;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import java.util.concurrent.ThreadPoolExecutor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.sxhl.market.utils.DebugTool;

/**
 * @ClassName: DownloadTask
 * @Description: 下载任务,负责管理多个任务的同时下载
 * @author: Liuqin
 * @date 2012-12-10 上午11:07:11
 * 
 */
public class DownloadTask {
	private static final String TAG = "DownloadTask";
	// 为避免不同应用接收消息，应使用各自的模块名称
	public static final String MODULE_NAME = "com.sxhl.market";
	// 下载临时文件扩展名
	public static final String DOWNLOADING_EXT_NAME = ".tmp";
	// 线程池中的活动线程数量
	private static final int FIX_THREADS = 1;
	// 每个文件的默认下载线程数量
	private static final int DOWN_THREAD = 1;

	// BrocastReceiver的Action
	public static final String ACTION_ON_DOWNLOAD_START = MODULE_NAME
			+ ".onDownloadStart";
	public static final String ACTION_ON_DOWNLOAD_PROGRESS = MODULE_NAME
			+ ".onDownloadProgress";
	public static final String ACTION_ON_DOWNLOAD_FINISH = MODULE_NAME
			+ ".onDownloadFinish";
	public static final String ACTION_ON_DOWNLOAD_ERROR = MODULE_NAME
			+ ".onDownloadError";
	public static final String ACTION_ON_DOWNLOAD_WAIT = MODULE_NAME
			+ ".onDownloadWait";
	public static final String ACTION_ON_DOWNLOAD_SIZE = MODULE_NAME
			+ ".onDownloadSize";
	public static final String ACTION_ON_DOWNLOAD_STOP = MODULE_NAME
			+ ".onDownloadStop";
	public static final String ACTION_ON_DOWNLOAD_COMPLETED = MODULE_NAME
			+ ".onDownloadCompleted";

	public static final String ACTION_ON_APP_INSTALLED = MODULE_NAME
			+ ".app_installed";
	public static final String ACTION_ON_APP_DOWNLOADED = MODULE_NAME
			+ ".app_downloaded";
	public static final String ACTION_ON_APP_DOWNLOAD_ERROR = MODULE_NAME
			+ ".app_download_error";

	// 下载由等待队列进入开始状态
	public static final int STATE_ON_DOWNLOAD_START = 100;
	// 下载进度更新
	public static final int STATE_ON_DOWNLOAD_PROGRESS = 101;
	// 下载完成
	public static final int STATE_ON_DOWNLOAD_FINISH = 102;
	// 下载发生错误
	public static final int STATE_ON_DOWNLOAD_ERROR = 103;
	// 等待下载
	public static final int STATE_ON_DOWNLOAD_WAIT = 104;
	// 获取文件长度
	public static final int STATE_ON_DOWNLOAD_SIZE = 105;
	// 下载停止时
	public static final int STATE_ON_DOWNLOAD_STOP = 106;
	// 所有下载完毕：下载队列已空，不一定都是成功
	public static final int STATE_ON_DOWNLOAD_COMPLETED = 107;

	public static final int OP_INSTALL = 111;
	public static final int OP_UNINSTALL = 112;

	public static final String FILE_DOWN_INFO_KEY = "fileDownInfo";
	public static final String ERR_MSG_KEY = "errMsgKey";

	private static DownloadTask instance = null;
	private Context context;
	private ThreadPoolExecutor executor = null;
	// 文件下载队列
	private volatile Map<String, DownloadRunnable> downQueue = new HashMap<String, DownloadRunnable>();
	// private volatile Map<String,FileDownloader> downQueue=new
	// HashMap<String,FileDownloader>();
	private Map<String, FileDownInfo> downInfos = new HashMap<String, FileDownInfo>();
	private String lock = "";
	private DownloadListenner listenner = null;

	private DownloadTask(Context context) {
		// this.context=context;
		this.context = context.getApplicationContext();
		executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(FIX_THREADS);
		listenner = new ApkDownListenner(context);
	}

	public static synchronized DownloadTask getInstance(Context context) {
		if (instance == null) {
			instance = new DownloadTask(context);
		}
		return instance;
	}

	/**
	 * @Title: download
	 * @Description: 把任务添加到下载线程中，这时还不一定开始下载
	 * @param fileDownInfo
	 *            必须设置唯一的文件id，下载地址，和本地保存目录。可定义线程数，最多5个线程，默认3个
	 * @throws
	 */
	public void download(FileDownInfo fileDownInfo) {
		if (instance == null) {
			getInstance(context);
		}

		if (fileDownInfo == null || getstrlen(fileDownInfo.getFileId()) <= 0
				|| getstrlen(fileDownInfo.getDownUrl()) <= 0
				|| getstrlen(fileDownInfo.getLocalDir()) <= 0) {
			onDownloadError(fileDownInfo, "Invalid argument:FileDownInfo");
			return;
		}
		FileDownInfo info = null;
		try {
			info = (FileDownInfo) fileDownInfo.clone();

			// File targetFile=new
			// File(fileDownInfo.getLocalDir(),fileDownInfo.getLocalFilename());
			// if(targetFile.exists() && targetFile.length()>0){
			// //文件已经存在，无需再下载
			// onDownloadWait(fileDownInfo);
			//
			// fileDownInfo.setFileSize((int)targetFile.length());
			// if(listenner!=null){
			// listenner.onDownloadFinish(fileDownInfo);
			// }
			// Intent downIntent=new
			// Intent(DownloadTask.ACTION_ON_DOWNLOAD_FINISH);
			// downIntent.putExtra(DownloadTask.FILE_DOWN_INFO_KEY,
			// fileDownInfo);
			// context.sendBroadcast(downIntent);
			// return;
			// }
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			onDownloadError(fileDownInfo, "Clone FileDownInfo Error");
			return;
		}

		DownloadRunnable runnable = putIntoQueue(info);
		if (runnable != null) {
			onDownloadWait(fileDownInfo);
			if (executor != null) {
				executor.execute(runnable);
			}
		}
	}

	public boolean isDownloading(String fileId) {
		synchronized (lock) {
			if (downQueue.containsKey(fileId)) {
				// 如果下载队列存在该文件
				return true;
			}
		}
		return false;
	}

	public DownloadRunnable putIntoQueue(FileDownInfo info) {
		DownloadRunnable runnable = null;
		synchronized (lock) {
			if (!downQueue.containsKey(info.getFileId())) {
				runnable = new DownloadRunnable(info);
				// 把任务添加到下载队列
				downQueue.put(info.getFileId(), runnable);
				downInfos.put(info.getFileId(), info);
			}
		}
		return runnable;
	}

	/**
	 * @Title: setDownloadPause
	 * @Description: 暂停指定文件id的下载
	 * @param fileId
	 * @throws
	 */
	public void setDownloadStop(String fileId) {
		synchronized (lock) {
			if (downQueue.containsKey(fileId)) {
				// 如果下载队列存在该文件
				DownloadRunnable runnable = downQueue.get(fileId);
				FileDownloader fileDownloader = null;
				if (runnable != null) {
					// 如果正在下载中，停止
					fileDownloader = runnable.getFileDownloader();
					runnable.setStopDownload();
					DebugTool.warn("id:" + fileId + " ------stop");
				}
				// if(fileDownloader==null){
				FileDownInfo fileDownInfo = downInfos.get(fileId);
				if (fileDownInfo != null) {
					FileDownloader.onDownloadStop(context, fileDownInfo,
							listenner);
				}
				// }
				// 文件移出下载队列
				downQueue.remove(fileId);
				downInfos.remove(fileId);
			}
		}
	}

	/**
	 * @Title: setAllDownloadPause
	 * @Description: 暂停所有下载任务
	 * @throws
	 */
	public void setAllDownloadStop() {
		synchronized (lock) {
			DebugTool.warn("------file down:stop all");
			Iterator<Map.Entry<String, DownloadRunnable>> it = downQueue
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, DownloadRunnable> entry = it.next();
				DownloadRunnable runnable = entry.getValue();
				FileDownloader fileDownloader = null;
				if (runnable != null) {
					fileDownloader = runnable.getFileDownloader();
					runnable.setStopDownload();
				}

				FileDownInfo fileDownInfo = downInfos.get(entry.getKey());
				if (fileDownInfo != null) {
					FileDownloader.onDownloadStop(context, fileDownInfo,
							listenner);
				}

				it.remove();
			}
		}
	}

	/**
	 * @ClassName: DownloadRunnable
	 * @Description: 下载任务
	 * @author: Liuqin
	 * 
	 */
	class DownloadRunnable implements Runnable {
		private FileDownInfo fileDownInfo = null;
		private FileDownloader fileDownloader = null;
		private volatile boolean isStop = false;

		public DownloadRunnable(FileDownInfo fileDownInfo) {
			this.fileDownInfo = fileDownInfo;
		}

		private void setStopDownload() {
			this.isStop = true;
			if (fileDownloader != null) {
				fileDownloader.setStopDownload(true);
			}
		}

		public void run() {
			try {
				synchronized (lock) {
					if (isStop) {
						return;
					}
					// 任务是否还在下载队列
					if (!downQueue.containsKey(fileDownInfo.getFileId())) {
						DebugTool.debug("file is cancel down:"
								+ fileDownInfo.getFileId());
						return;
					}
					fileDownloader = new FileDownloader(context);
				}

				onDownloadStart(fileDownInfo);

				if (isFinishDownloaded(fileDownInfo)) {
					return;
				}

				int threadNum = fileDownInfo.getThreadCount();
				if (threadNum <= 0 || threadNum > FileDownDAO.MAX_THREAD) {
					threadNum = DOWN_THREAD;
				}

				DebugTool
						.info(TAG, "download url:" + fileDownInfo.getDownUrl());
				if (fileDownloader.init(fileDownInfo, threadNum, listenner)) {
					fileDownloader.download();
				} else {
					onDownloadError(fileDownInfo, "init error");
				}
			} catch (Exception e) {
				e.printStackTrace();
				synchronized (lock) {
					if (downQueue.containsKey(fileDownInfo.getFileId())) {
						onDownloadError(fileDownInfo,
								"run error:" + e.toString());
					}
				}
			} finally {
				synchronized (lock) {
					if (!isStop) {
						downQueue.remove(fileDownInfo.getFileId());
						downInfos.remove(fileDownInfo.getFileId());
					}
					if (downQueue.size() <= 0) {
						onDownloadCompleted();
					}
				}
			}
		}

		public FileDownloader getFileDownloader() {
			return fileDownloader;
		}
	}

	private boolean isFinishDownloaded(FileDownInfo fileDownInfo) {
		File targetFile = new File(fileDownInfo.getLocalDir(),
				fileDownInfo.getLocalFilename());
		if (targetFile.exists() && targetFile.length() > 0) {
			// 文件已经存在，无需再下载
			fileDownInfo.setFileSize((int) targetFile.length());
			if (listenner != null) {
				listenner.onDownloadFinish(fileDownInfo);
			}
			Intent downIntent = new Intent(
					DownloadTask.ACTION_ON_DOWNLOAD_FINISH);
			downIntent.putExtra(DownloadTask.FILE_DOWN_INFO_KEY, fileDownInfo);
			context.sendBroadcast(downIntent);
			return true;
		}
		return false;
	}

	private int getstrlen(String str) {
		return str == null ? 0 : str.length();
	}

	private void onDownloadWait(FileDownInfo fileDownInfo) {
		Intent downIntent = new Intent(ACTION_ON_DOWNLOAD_WAIT);
		downIntent.putExtra(FILE_DOWN_INFO_KEY, fileDownInfo);
		context.sendBroadcast(downIntent);

		if (listenner != null) {
			listenner.onDownloadWait(fileDownInfo);
		}
	}

	private void onDownloadStart(FileDownInfo fileDownInfo) {
		Intent downIntent = new Intent(ACTION_ON_DOWNLOAD_START);
		downIntent.putExtra(FILE_DOWN_INFO_KEY, fileDownInfo);
		context.sendBroadcast(downIntent);

		if (listenner != null) {
			listenner.onDownloadStart(fileDownInfo);
		}
	}

	private void onDownloadError(FileDownInfo fileDownInfo, String msg) {
		onDownloadError(fileDownInfo, msg, false);
	}

	private void onDownloadError(FileDownInfo fileDownInfo, String msg,
			boolean isOrder) {
		Intent downIntent = new Intent(ACTION_ON_DOWNLOAD_ERROR);
		downIntent.putExtra(FILE_DOWN_INFO_KEY, fileDownInfo);
		downIntent.putExtra(DownloadTask.ERR_MSG_KEY, msg);
		if (isOrder) {
			context.sendOrderedBroadcast(downIntent, null);
		} else {
			context.sendBroadcast(downIntent);
		}

		if (listenner != null) {
			listenner.onDownloadError(fileDownInfo, msg);
		}
	}

	private void onDownloadCompleted() {
		Intent downIntent = new Intent(ACTION_ON_DOWNLOAD_COMPLETED);
		context.sendBroadcast(downIntent);

		if (listenner != null) {
			listenner.onDownloadCompleted();
		}
	}

	/**
	 * @Title: regBrocastReceiver
	 * @Description: 注册广播接收器
	 * @param receiver
	 * @throws
	 */
	public void regBrocastReceiver(BroadcastReceiver receiver) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadTask.ACTION_ON_DOWNLOAD_START);
		filter.addAction(DownloadTask.ACTION_ON_DOWNLOAD_PROGRESS);
		filter.addAction(DownloadTask.ACTION_ON_DOWNLOAD_FINISH);
		filter.addAction(DownloadTask.ACTION_ON_DOWNLOAD_ERROR);
		filter.addAction(DownloadTask.ACTION_ON_DOWNLOAD_WAIT);
		filter.addAction(DownloadTask.ACTION_ON_DOWNLOAD_SIZE);
		filter.addAction(DownloadTask.ACTION_ON_DOWNLOAD_STOP);
		filter.addAction(DownloadTask.ACTION_ON_DOWNLOAD_COMPLETED);

		filter.addAction(DownloadTask.ACTION_ON_APP_INSTALLED);
		filter.addAction(DownloadTask.ACTION_ON_APP_DOWNLOADED);

		context.registerReceiver(receiver, filter);
	}

	/**
	 * @Title: unregBrocastReceiver
	 * @Description: 取消广播接收器
	 * @param receiver
	 * @throws
	 */
	public void unregBrocastReceiver(BroadcastReceiver receiver) {
		try {
			context.unregisterReceiver(receiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDownloadingPercentage(String fileId) {
		DownloadRunnable runnable = downQueue.get(fileId);
		if (runnable != null) {
			FileDownloader fileDownloader = runnable.getFileDownloader();
			if (fileDownloader != null) {
				int downLen = fileDownloader.getDownLen();
				int size = fileDownloader.getFileSize();
				if (downLen != -1 && size != -1) {
					return (int) ((long) downLen * 100 / (long) size);
				}
			}
		}
		return -1;
	}

	public Set<String> getDownloadingFileIds() {
		Set<String> fileIds;
		synchronized (lock) {
			fileIds = downQueue.keySet();
		}
		return fileIds;
	}

	public int getDownloadingFileSize() {
		synchronized (lock) {
			return downQueue.size();
		}
	}

	public void setListenner(DownloadListenner listenner) {
		this.listenner = listenner;
	}

	public DownloadListenner getListenner() {
		return listenner;
	}

	public void cancelDownload(FileDownInfo fileDownInfo) {
		try {
			setDownloadStop(fileDownInfo.getFileId());

			File tmpFile = new File(fileDownInfo.getLocalDir(),
					fileDownInfo.getLocalFilename() + DOWNLOADING_EXT_NAME);
			if (tmpFile.exists()) {
				tmpFile.delete();
			}

			FileDownDAO fileDownDAO = new FileDownDAO(context);
			fileDownDAO.delete(fileDownInfo);
			fileDownDAO.closeDB();
			fileDownDAO = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void recycle() {
		try {
			instance = null;
			setAllDownloadStop();
			if (listenner != null) {
				((ApkDownListenner) listenner).recycle();
			}
			if (executor != null) {
				executor.shutdown();
				executor = null;
			}
			Report.getInstance().recycle();
		} catch (Exception e) {
			e.printStackTrace();
			DebugTool.error(TAG, e.toString(), null);
		}
	}

	private FileDownDAO fileDownDAO = null;

	public int getDownedPercent(String fileId) {
		if (fileDownDAO == null) {
			fileDownDAO = new FileDownDAO(context);
		}
		return fileDownDAO.getDownloadedPercent(fileId);
	}

	public void closeFileDownDAO() {
		if (fileDownDAO != null) {
			fileDownDAO.closeDB();
		}
	}
}
