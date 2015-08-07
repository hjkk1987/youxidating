package com.sxhl.market.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;

/**
 * 关联本地媒体相册浏览器工具
 * @author fxl
 * time: 2012-9-24 下午3:44:08
 */
public class MediaSannerTool {
	
	private MediaScannerConnection mediaScanConn;
	
	private Context context;
	
	private String path;
	
	/**
	 * 文件路径默认为sd卡中的"/EacanNews"文件夹
	 * @param context
	 */
	public MediaSannerTool(Context context) {
		this(context,Environment.getExternalStorageDirectory().getAbsolutePath()+"/EacanNews/");
	}
	
	public MediaSannerTool(Context context, String path) {
		this.context = context;
		this.path = path;
	}
	
	/**
	 * 关联相册
	 */
	public void startScan(String fileName) {
		path += fileName;
		if (mediaScanConn != null)
			mediaScanConn.disconnect();
		mediaScanConn = new MediaScannerConnection(context,new MediaScannerClient());
		mediaScanConn.connect();
	}
	
	private class MediaScannerClient implements MediaScannerConnectionClient {

		public MediaScannerClient() {
			super();
		}

		public void onMediaScannerConnected() {
			mediaScanConn.scanFile(path, "image/*");
		}

		public void onScanCompleted(String path, Uri uri) {
			mediaScanConn.disconnect();
		}

	}

}
