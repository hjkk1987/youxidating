package com.sxhl.market.model.net.http.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.sxhl.market.utils.DebugTool;
import com.sxhl.market.utils.Logtrace;
import com.sxhl.market.utils.NetUtil;

/**
 * @ClassName: FileDownloader
 * @Description: 文件下载类，负责管理单个文件中的下载线程
 * @author: Liuqin
 * @date 2012-12-10 上午11:19:58
 * 
 */
public class FileDownloader {
	private static final String TAG = "FileDownloader";
	private Context context;
	// private FileService fileService;
	/* 已下载文件长度，即各线程下载数据长度的总和 */
	private int downloadSize = 0;
	/* 线程数 */
	private DownloadThread[] threads;
	/* 本地保存文件 */
	private File saveFileTmp;
	/* 每条线程下载的长度 */
	private int block;

	// 是否停止下载标志
	private volatile boolean isStopDownload = false;
	private FileDownInfo fileDownInfo = null;
	private FileDownDAO fileDownDAO;
	/* 缓存各线程下载的长度 */
	private Map<Integer, Integer> threadInfos = new ConcurrentHashMap<Integer, Integer>();
	private DownloadListenner listenner;

	// for test download speed
	private static final boolean isTestSpeed = false;
	private long startDownloadSize = 0;
	private long startDownloadTimes = 0;

	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return threads.length;
	}

	/**
	 * 获取文件大小
	 * 
	 * @return
	 */
	public int getFileSize() {
		return fileDownInfo == null ? -1 : fileDownInfo.getFileSize();
	}

	/**
	 * @Title: getDownLen
	 * @Description: 获取已下载的文件长度
	 * @return
	 * @throws
	 */
	public int getDownLen() {
		return fileDownInfo == null ? -1 : fileDownInfo.getDownLen();
	}

	/**
	 * @Title: update
	 * @Description: 更新下载大小
	 * @param threadId
	 *            线程id
	 * @param downLen
	 *            该线程下载的总长度
	 * @param offset
	 *            该线程最后下载的位置
	 * @throws
	 */
	// protected synchronized void update(int threadId, int downLen ,int offset)
	// {
	protected void update(int threadId, int downLen, int offset) {
		downloadSize += offset;
		fileDownInfo.setDownLen(downloadSize);
		if (isUpdateProgressDB) {
			this.threadInfos.put(threadId, downloadSize);
		}
	}

	/**
	 * @Title: updateProgressDB
	 * @Description: 更新线程下载信息到数据库
	 * @throws
	 */
	protected void updateProgressDB(int threadId, int downLen) {
		// Logtrace.warn(TAG,
		// "updateDB,percent:"+(downLen*100)/downloadSize+"%");
		this.threadInfos.put(threadId, downLen);
		fileDownDAO.updateThreadInfos(fileDownInfo.getFileId(), threadInfos);
	}

	protected void updateProgressDB() {
		fileDownDAO.updateThreadInfos(fileDownInfo.getFileId(), threadInfos);
	}

	public FileDownloader(Context context) {
		this.context = context;
	}

	/**
	 * @Title: init
	 * @Description: 初始化下载
	 * @param downInfo
	 * @param threadNum
	 * @return
	 * @throws
	 */
	public boolean init(FileDownInfo downInfo, int threadNum,
			DownloadListenner listenner) {
		try {
			this.listenner = listenner;

			// File target=new
			// File(downInfo.getLocalDir(),downInfo.getLocalFilename());
			// if(target.exists()){
			//
			// onDownloadFinish();
			// return;
			// }

			fileDownDAO = new FileDownDAO(context);
			FileDownInfo info = fileDownDAO.findByFileId(downInfo.getFileId());
			boolean isExist = false;
			if (info != null && info.getFileId().equals(downInfo.getFileId())) {
				String localFileName = downInfo.getLocalFilename();
				if (localFileName == null || localFileName.length() <= 0) {
					localFileName = Uri.encode(downInfo.getDownUrl());
					downInfo.setLocalFilename(localFileName);
				}

				if (info.getDownUrl().equals(downInfo.getDownUrl())
						&& info.getLocalDir().equals(downInfo.getLocalDir())
						&& localFileName.equals(info.getLocalFilename())) {
					isExist = true;

					if (info.getDownLen() > 0) {
						File targetFile = new File(info.getLocalDir(),
								localFileName
										+ DownloadTask.DOWNLOADING_EXT_NAME);
						if (!targetFile.exists()
								|| targetFile.length() != info.getFileSize()) {
							// 文件不存在或长度不一致
							isExist = false;
						}
					}
				}

				if (!isExist) {
					fileDownDAO.delete(info);
				}
			}
			if (isExist) {
				// 数据库存在该文件下载信息
				this.fileDownInfo = info;
				this.fileDownInfo.setExtraData(downInfo.getExtraData());
				this.fileDownInfo.setThreadId(downInfo.getThreadId());
				this.fileDownInfo.setObject(downInfo.getObject());
			} else {
				int size = 0;
				for (int i = 0; i < 3 && size <= 0; i++) {
					size = DownloadThread
							.httpGetFileLength(downInfo.getDownUrl(),
									!NetUtil.isWifiOpen(context));// 根据响应获取文件大小
				}
				if (size <= 0) {
					throw new RuntimeException("Unknown file size");
				} else {
					DebugTool.info(TAG, "get fileSize:" + size);
					downInfo.setFileSize(size);
					if (size <= 10 * 1024) {
						// 小于10K的文件只用一个线程
						threadNum = 1;
					}
				}
				// if (downInfo.getLocalFilename().length() <= 0) {
				// downInfo.setLocalFilename(Uri.encode(downInfo.getDownUrl()));
				// }
				downInfo.setDownLen(0);
				downInfo.getThreadsInfo().clear();
				this.fileDownInfo = downInfo;
			}

			this.threadInfos = fileDownInfo.getThreadsInfo();
			File dir = new File(fileDownInfo.getLocalDir());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			this.saveFileTmp = new File(dir, fileDownInfo.getLocalFilename()
					+ DownloadTask.DOWNLOADING_EXT_NAME);

			// 计算每条线程下载的数据长度
			this.block = (fileDownInfo.getFileSize() % threadNum) == 0 ? fileDownInfo
					.getFileSize() / threadNum
					: fileDownInfo.getFileSize() / threadNum + 1;
			this.threads = new DownloadThread[threadNum];
			if (threadInfos.size() != threadNum) {
				threadInfos.clear();
				for (int i = 0; i < threadNum; i++) {
					// 初始化每条线程已经下载的数据长度为0
					threadInfos.put(i, 0);
				}
				// 保存文件下载信息到数据库
				fileDownInfo.setThreadCount(threadNum);
				fileDownInfo.setDownLen(0);
				fileDownDAO.save(fileDownInfo);
			}
			this.downloadSize = fileDownInfo.getDownLen();
			onDownloadSized();
			return true;
		} catch (Exception e) {
			fileDownDAO.closeDB();
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @Title: download
	 * @Description: 开始下载
	 * @param listener
	 *            下载状态监听器
	 * @return
	 * @throws Exception
	 */
	public int download() throws Exception {
		try {
			// 记录开始位置
			DebugTool.info(TAG, "download start");
			startDownloadSize = downloadSize;
			startDownloadTimes = System.currentTimeMillis();

			if (!saveFileTmp.exists()
					|| saveFileTmp.length() != fileDownInfo.getFileSize()) {
				RandomAccessFile randOut = new RandomAccessFile(
						this.saveFileTmp, "rwd");
				// FileChannel fc = randOut.getChannel();
				// FileLock lock = fc.tryLock();
				// if(lock==null){
				// randOut.close();
				// throw new RuntimeException("Error to get save file lock");
				// }
				if (fileDownInfo.getFileSize() > 0) {
					randOut.setLength(fileDownInfo.getFileSize());
				}
				// lock.release();
				randOut.close();
				DebugTool.info(TAG, "create file end");
			}

			if (isStopDownload) {
				return downloadSize;
			}

			String url = fileDownInfo.getDownUrl();
			for (int i = 0; i < this.threads.length; i++) {// 开启线程进行下载
				int downLength = threadInfos.get(i);
				if (downLength < this.block
						&& downloadSize < fileDownInfo.getFileSize()) {// 判断线程是否已经完成下载,否则继续下载
					this.threads[i] = new DownloadThread(context, this, url,
							this.saveFileTmp, this.block, threadInfos.get(i), i);
					this.threads[i].setPriority(7);
					this.threads[i].start();
				} else {
					this.threads[i] = null;
				}
			}

			FileDownInfo info = new FileDownInfo();
			info.setFileId(fileDownInfo.getFileId());
			info.setFileSize(fileDownInfo.getFileSize());
			info.setLocalFilename(fileDownInfo.getLocalFilename());
			Intent downProgressIntent = new Intent(
					DownloadTask.ACTION_ON_DOWNLOAD_PROGRESS);

			boolean notFinish = true;// 下载未完成
			final int filesize = fileDownInfo.getFileSize();
			int lastDownPercent = -1;
			int currentDownPercent = 0;
			setThreadId(Thread.currentThread().getId());
			// DebugTool.warn(TAG,
			// "start down,threadId:"+Thread.currentThread().getId());

			if (threads != null && threads.length > 0 && threads[0] != null) {
				isUpdateProgressDB = threads[0].getBlockNum() == 1 ? true
						: false;
			}
			while (notFinish) {// 循环判断所有线程是否完成下载
				Thread.sleep(900);
				notFinish = false;// 假定全部线程下载完成
				for (int i = 0; i < threads.length; i++) {
					if (threads[i] != null
							&& threads[i].getDownloadState() != 1) {// 如果发现线程未完成下载
						notFinish = true;// 设置标志为下载没有完成
						break;
					}
				}

				if (isStopDownload) {
					// DebugTool.debug(TAG,
					// "isStopDownload:true,threadId:"+Thread.currentThread().getId());
					for (int i = 0; i < threads.length; i++) {
						if (threads[i] != null) {
							threads[i].setStop(true);
						}
					}
					// onDownloadStop(context,fileDownInfo,listenner);
					// DebugTool.debug(TAG,
					// "return,ThreadId:"+Thread.currentThread().getId());
					return downloadSize;
				}

				currentDownPercent = (int) ((long) downloadSize * 100 / (long) filesize);
				if (currentDownPercent != lastDownPercent) {
					lastDownPercent = currentDownPercent;
					if (isUpdateProgressDB) {
						updateProgressDB();
					}
					info.setDownLen(downloadSize);
					onDownloadProgress(info, downProgressIntent);
					// DebugTool.info(TAG,
					// "pro:"+currentDownPercent+" threadId:"+Thread.currentThread().getId());
				}

				if (isTestSpeed) {
					long elapseTime = ((System.currentTimeMillis() - startDownloadTimes) / 1000);
					if (elapseTime > 0) {
						DebugTool
								.info(TAG,
										"speed:"
												+ ((downloadSize - startDownloadSize) / elapseTime));
					}
				}
			}
			Log.e("downloadSize", "downloadSize : " + downloadSize
					+ "filesize : " + filesize);
			if (downloadSize < filesize) {
				// 下载长度不匹配
				throw new Exception("file size not match");
			}

			// 文件改名
			this.saveFileTmp.renameTo(new File(fileDownInfo.getLocalDir(),
					fileDownInfo.getLocalFilename()));
			fileDownDAO.delete(fileDownInfo);
			onDownloadFinish();
		} catch (FileNotFoundException e) {
			try {
				e.printStackTrace();
				File file = new File(fileDownInfo.getLocalDir(),
						fileDownInfo.getLocalFilename());
				if (file.exists()
						&& file.length() == fileDownInfo.getFileSize()) {
					// 文件已经下载完成
					fileDownDAO.delete(fileDownInfo);
					onDownloadFinish();
				} else {
					throw e;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				fileDownDAO.closeDB();

				if (threads != null) {
					for (int i = 0; i < threads.length; i++) {
						if (threads[i] != null) {
							threads[i].setStop(true);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.downloadSize;
	}

	/**
	 * 获取Http响应头字段
	 * 
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(
			HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}

	/**
	 * 打印Http头字段
	 * 
	 * @param http
	 */
	public static void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			print(key + entry.getValue());
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public void setStopDownload(boolean isStopDownload) {
		this.isStopDownload = isStopDownload;
	}

	private void onDownloadFinish() {
		if (listenner != null) {
			listenner.onDownloadFinish(fileDownInfo);
		}

		Intent downIntent = new Intent(DownloadTask.ACTION_ON_DOWNLOAD_FINISH);
		downIntent.putExtra(DownloadTask.FILE_DOWN_INFO_KEY, fileDownInfo);
		context.sendBroadcast(downIntent);

		Report.getInstance().reportToServer(fileDownInfo.getFileId());
	}

	private void onDownloadProgress(FileDownInfo info, Intent downProgressIntent) {
		downProgressIntent.putExtra(DownloadTask.FILE_DOWN_INFO_KEY, info);
		context.sendBroadcast(downProgressIntent);

		if (listenner != null) {
			listenner.onDownloadProgress(info);
		}
	}

	private void onDownloadSized() {
		Intent downIntent = new Intent(DownloadTask.ACTION_ON_DOWNLOAD_SIZE);
		downIntent.putExtra(DownloadTask.FILE_DOWN_INFO_KEY, fileDownInfo);
		context.sendBroadcast(downIntent);

		if (listenner != null) {
			listenner.onDownloadSized(fileDownInfo);
		}
	}

	public static void onDownloadStop(Context context,
			FileDownInfo fileDownInfo, DownloadListenner listenner) {
		Intent downIntent = new Intent(DownloadTask.ACTION_ON_DOWNLOAD_STOP);
		downIntent.putExtra(DownloadTask.FILE_DOWN_INFO_KEY, fileDownInfo);
		context.sendBroadcast(downIntent);

		if (listenner != null) {
			listenner.onDownloadStop(fileDownInfo);
		}
	}

	/**
	 * threadId
	 * 
	 * @return the threadId
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * @param threadId
	 *            the threadId to set
	 */
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	private long threadId;

	private boolean isUpdateProgressDB = false;
}
