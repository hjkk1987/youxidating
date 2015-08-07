package com.sxhl.market.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.os.Environment;

/**
 * 文件操作工具类
 * @author fxl
 * time: 2012-9-24 下午4:31:11
 */
public class FileTool {

	/** 默认的文件保存路径 */
	private static final String defaultFolderName = "/EacanNews";

	/**
	 * 从网络上获取图片
	 * 
	 * @param imageUrl
	 *            需获取图片的url地址
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] getImage(String imageUrl) throws Exception {
		InputStream inStream = getImageStream(imageUrl);
		if (inStream != null) {
			return readStream(inStream);
		}
		return null;
	}

	/**
	 * 从网络上获取图片
	 * 
	 * @param path
	 *            需获取图片的url地址
	 * @return InputStream
	 * @throws Exception
	 */
	public InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	/**
	 * 把输入流转成字节数组
	 * 
	 * @param inStream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @param path 相对SD卡保存路径，如为null或""字符串将默认为 "/EacanNews"
	 * @throws IOException
	 */
	public static void saveFile(Bitmap bm, String fileName, String path) throws IOException {
		File extFile = Environment.getExternalStorageDirectory();
		if(null == path || "".equals(path.trim()))
			path = defaultFolderName;
		File dirFile = new File(extFile, path);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}

		File myCaptureFile = new File(dirFile, fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}

	/**
	 * 保存文件
	 * 
	 * @param is
	 * @param fileName
	 * @param path 相对保存路径，如为null或""字符串将默认为 "/EacanNews"
	 * @throws IOException
	 */
	public static void saveFile(InputStream is, String fileName, String path)
			throws IOException {
		BufferedInputStream bufferis = new BufferedInputStream(is);
		try {
			File extFile = Environment.getExternalStorageDirectory();
			if(null == path || "".equals(path.trim()))
				path = defaultFolderName;
			File dirFile = new File(extFile, path);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}

			File myCaptureFile = new File(dirFile, fileName);
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					myCaptureFile));

			byte[] b = new byte[2048];
			int count;

			while ((count = bufferis.read(b)) > 0) {
				os.write(b, 0, count);
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
