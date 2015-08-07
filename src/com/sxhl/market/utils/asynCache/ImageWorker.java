/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sxhl.market.utils.asynCache;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sxhl.market.R;
import com.sxhl.market.R.raw;
import com.sxhl.market.utils.DebugTool;

/**
 * This class wraps up completing some arbitrary long running work when loading
 * a bitmap to an ImageView. It handles things like using a memory and disk
 * cache, running the work in a background thread and setting a placeholder
 * image.
 */
public abstract class ImageWorker {
	private static final String TAG = "ImageWorker";
	private int imageId;
	private int mImageWidth;
	private int mImageHeight;
	public Handler mHandler = new Handler();

	/**
	 * 
	 * running. 获取当前的图片id
	 * 
	 */
	public int getLoadingImage() {
		return imageId;
	}

	/**
	 * Set placeholder bitmap that shows when the the background thread is
	 * running.
	 * 
	 * @param resId
	 */
	public void setLoadingImage(int resId) {
		imageId = resId;
	}

	public void setImageSize(int width, int height) {
		mImageWidth = width;
		mImageHeight = height;
	}

	/**
	 * 清除旧版本缓存下来的图片
	 * 
	 * clearOrderImageCache:TODO
	 * 
	 * @author:LiuQin
	 * @date 2014-10-18 下午8:46:09
	 */
	public static void clearOrderImageCache(final Context context) {
		final SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		boolean isClearOrderCache = sp.getBoolean("isClearOrderCache", false);
		if (!isClearOrderCache) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					final String IMAGE_CACHE_DIR = "images";
					try {
						File cache;
						try {
							cache = getExternalCacheDir(context,
									IMAGE_CACHE_DIR);
							if (cache != null) {
								deleteContents(cache);
								DebugTool
										.info(TAG,
												"[clearOrderImageCache] clear external cache end");
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}

						try {
							cache = new File(context.getCacheDir()
									+ File.separator + IMAGE_CACHE_DIR);
							if (cache != null) {
								deleteContents(cache);
								DebugTool
										.info(TAG,
												"[clearOrderImageCache] clear internal cache end");
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}

						sp.edit().putBoolean("isClearOrderCache", true)
								.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	public static void deleteContents(File dir) throws IOException {
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IllegalArgumentException("not a directory: " + dir);
		}
		for (File file : files) {
			if (file.isDirectory()) {
				deleteContents(file);
			}
			if (!file.delete()) {
				throw new IOException("failed to delete file: " + file);
			}
		}
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	public static File getExternalCacheDir(Context context, String uniqueName) {
		if (hasFroyo()) {
			return new File(context.getExternalCacheDir() + File.separator
					+ uniqueName);
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/" + uniqueName;
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	private static boolean isMain() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	/**
	 * Load an image specified by the data parameter into an ImageView (override
	 * {@link ImageWorker#processBitmap(Object)} to define the processing
	 * logic). A memory and disk cache will be used if an {@link ImageCache} has
	 * been set using {@link ImageWorker#setImageCache(ImageCache)}. If the
	 * image is found in the memory cache, it is set immediately, otherwise an
	 * {@link AsyncTask} will be created to asynchronously load the bitmap.
	 * 
	 * @param data
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 */
	public void loadImage(String data, ImageView imageView) {
		loadImage(data, imageView, 1);
	}

	// /**加载图片的倒影方法*/
	// public void loadReflectImage(String data, ImageView imageView,ImageView
	// shadowView) {
	// loadReflectImage(data, imageView,shadowView, 2);
	// }

	// /**加载有控件截图倒影的图片*/
	// public void loadImage(String data, ImageView imageView,ImageView
	// shadowView,View shadowParentView){
	// loadImage(data, imageView,shadowView,shadowParentView, 2);
	// }

	/**
	 * @author wsd
	 * @Description:重载方法，增加圆角处理
	 * @date 2013-2-26 下午4:23:07
	 */
	public void loadImage(final String data, final ImageView imageView,
			final float roundPx) {
		DebugTool.info(TAG, "[loadImage] roundPx:" + roundPx);
		final int wid = mImageWidth;
		final int hei = mImageHeight;
		if (wid > 0 && wid <= 150) {
			setLoadingImage(R.drawable.minphoto);
		} else if (wid <= 400) {
			setLoadingImage(R.drawable.middlephoto);
		} else {
			setLoadingImage(R.drawable.maxphoto);
		}
		imageView.setBackgroundResource(imageId);
		displayImage(data, imageView, roundPx, null);
		imageView.setBackgroundDrawable(null);
	}

	// /**
	// * @author wsd
	// * @Description:重载方法，增加控件倒影处理
	// * @date 2013-2-26 下午4:23:07
	// */
	// private void loadImage(final String data, final ImageView imageView,final
	// ImageView shadowView,final View shadowParentView, final float roundPx) {
	// DebugTool.info(TAG, "[loadImage] ");
	//
	// SimpleImageLoadingListener listener=new SimpleImageLoadingListener(){
	// @Override
	// public void onLoadingComplete(String imageUri, View view,
	// Bitmap loadedImage) {
	// // TODO Auto-generated method stub
	// super.onLoadingComplete(imageUri, view, loadedImage);
	// final Bitmap parmBitmap =
	// ImageReflectUtil.convertViewToBitmap(shadowParentView);
	// if(parmBitmap!=null){
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// final Bitmap shadowBitmap =
	// ImageReflectUtil.createCutReflectedImage(parmBitmap,0,true);
	// if(shadowBitmap==null){
	// return;
	// }
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// shadowView.setImageBitmap(shadowBitmap);
	// }
	// });
	// }
	// }).start();
	// }
	// }
	// };
	// displayImage(data, imageView, roundPx, listener);
	// }

	// /**
	// * @author wsd
	// * @Description:重载方法，增加图片倒影处理
	// * @date 2013-2-26 下午4:23:07
	// */
	// private void loadReflectImage(final String data, final ImageView
	// imageView,final ImageView shadowView, final float roundPx) {
	// DebugTool.info(TAG, "[loadReflectImage] ");
	// SimpleImageLoadingListener listener=new SimpleImageLoadingListener(){
	// @Override
	// public void onLoadingComplete(String imageUri, View view,
	// final Bitmap loadedImage) {
	// // TODO Auto-generated method stub
	// super.onLoadingComplete(imageUri, view, loadedImage);
	// if(loadedImage==null){
	// return;
	// }
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// final Bitmap shadowBitmap =
	// ImageReflectUtil.createCutReflectedImage(loadedImage,0,false);
	// if(shadowBitmap==null){
	// return;
	// }
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// shadowView.setImageBitmap(shadowBitmap);
	// }
	// });
	// }
	// }).start();
	// }
	// };
	// displayImage(data, imageView, roundPx, listener);
	// }

	/**
	 * 
	 * @Title: loadImage3
	 * @Description: TODO 加载完数据后，去掉背景
	 * @param @param data
	 * @param @param imageView
	 * @param @param roundPx
	 * @return void
	 * @throws
	 */
	@SuppressLint("NewApi")
	public void loadImage3(Object data, ImageView imageView, float roundPx) {
	}

	public void loadImage(Object data, ImageView imageView, float roundPx,
			int alpha) {
	}

	public void loadImage2(final String data, final ImageView imageView,
			final float roundPx, Context context, String pkgName,
			String actName, final String url) throws Exception {
		setLoadingImage(R.drawable.minphoto);
		imageView.setBackgroundResource(imageId);
		
		
		final String uri = "appicon://" + pkgName + "/" + actName;
		if (!TextUtils.isEmpty(url)
				&& (url.startsWith("http://") || url.startsWith("https://"))) {
			SimpleImageLoadingListener listener = new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view,
						final Bitmap loadedImage) {
					// TODO Auto-generated method stub
					super.onLoadingComplete(imageUri, view, loadedImage);
					DebugTool.info(TAG,
							"[onLoadingComplete] load app url icon success");
				}

				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					// TODO Auto-generated method stub
					super.onLoadingFailed(imageUri, view, failReason);
					DebugTool.info(TAG,
							"[onLoadingFailed] load app url icon failed");
					displayImage(uri, imageView, roundPx, null);
				}
			};
			displayImage(url, imageView, roundPx, listener);
		} else {
			displayImage(uri, imageView, roundPx, null);
		}
		imageView.setBackgroundDrawable(null);
	}

	public void displayImage(final String data, final ImageView imageView,
			final float roundPx, final ImageLoadingListener listener) {
		DebugTool.info(TAG, "[displayImage] uri:" + data);
		if (!isMain()) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ImageLoader.getInstance().displayImage(
							data,
							imageView,
							UILDisplayOpiton.getDefaultPhotoOption(imageId,
									(int) roundPx), listener);
				}
			});
		} else {
			ImageLoader.getInstance().displayImage(
					data,
					imageView,
					UILDisplayOpiton.getDefaultPhotoOption(imageId,
							(int) roundPx), listener);
		}
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filename
	 *            The full path of the file to decode
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
	 * object when decoding bitmaps using the decode* methods from
	 * {@link BitmapFactory}. This implementation calculates the closest
	 * inSampleSize that will result in the final decoded bitmap having a width
	 * and height equal to or larger than the requested width and height. This
	 * implementation does not ensure a power of 2 is returned for inSampleSize
	 * which can be faster when decoding but results in a larger bitmap which
	 * isn't as useful for caching purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee a final image
			// with both dimensions larger than or equal to the requested height
			// and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}
}
