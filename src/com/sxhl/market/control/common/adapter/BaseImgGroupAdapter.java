package com.sxhl.market.control.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.model.entity.AutoType;
import com.sxhl.market.utils.asynCache.ImageFetcher;

/**
 * @ClassName: BaseImgGroupAdapter.java
 * @Description: 第二缓存方案
 * @author 吴绍东
 * @date 2013-2-26 下午3:09:47
 */
public abstract class BaseImgGroupAdapter<T extends AutoType> extends
		BaseGroupAdapter<T> {
	protected LayoutInflater mInflater;
	protected ImageFetcher mImageFetcher;

	public BaseImgGroupAdapter(Context context) {
		super(context);
		mInflater = LayoutInflater.from(context);
		mImageFetcher = ((BaseActivity) context).getmImageFetcher();
	}

	/**
	 * @author wsd
	 * @Description:为一个ImageView绑定一张图片
	 * @date 2012-12-7 下午4:16:35
	 */

	public void bindImg(String imageUrl, ImageView imageView) {
		mImageFetcher.loadImage(imageUrl, imageView);
	}

	// public void bindImg(String imageUrl,ImageView imageView,int width,int
	// height){
	// Uri url = Uri.parse(imageUrl);
	// try {
	// Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(url));
	// Bitmap bitmap2=BitmapUtils.getBitmapDeflation(bitmap, width, height);
	// imageView.setImageBitmap(bitmap2);
	// } catch (IOException e) {
	// imageView.setImageResource(R.drawable.mygame_default_icon);
	// }
	// }
	/**
	 * 
	 * @author fcs
	 * @Description:为一个imageView绑定一张圆角图片，roundPx设置圆角的大小（5-10）
	 * @date 2013-2-25 下午2:22:37
	 */
	public void bindRoundImg(String imageUrl, ImageView imageView, float roundPx) {
		mImageFetcher.loadImage(imageUrl, imageView, roundPx);
	}

	public void bindRoundImg(String id, ImageView imageView, float roundPx,
			Context context, String pkgName, String actName, String imageUrl)
			throws Exception {
		mImageFetcher.loadImage2(id, imageView, roundPx, context, pkgName,
				actName, imageUrl);
	}

	/**
	 * Set the target image width and height.
	 * 
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width, int height) {
		mImageFetcher.setImageSize(width, height);
	}

	/**
	 * @author wsd
	 * @Description:为一个ImageView绑定一张图片
	 * @date 2012-12-7 下午4:16:35
	 */

	public void bindImg(String imageUrl, ImageView imageView,
			int defaultImageResource) {
		mImageFetcher.loadImage(imageUrl, imageView);
	}

}
/**
 * @author wsd
 * @Description:带图片异步加载的适配器
 * @date 2012-12-6 下午8:53:01
 */
// abstract class BaseImgGroupAdapter<T extends AutoType> extends
// BaseGroupAdapter<T> implements ObservableAdapter{
// //远程资源管理器
// protected RemoteResourceManager mRrm;
// private RemoteResourceManagerObserver mResourcesObserver;
// protected Handler mHandler = new Handler();
// protected LayoutInflater mInflater;
// public BaseImgGroupAdapter(Context context) {
// super(context);
// mRrm = BaseApplication.getRemoteResourceManager();
// mResourcesObserver = new RemoteResourceManagerObserver();
// mInflater = LayoutInflater.from(context);
// mRrm.addObserver(mResourcesObserver);
// }
// /**
// * @author wsd
// * @Description:为一个ImageView绑定一张图片
// * @date 2012-12-7 下午4:16:35
// */
//
// public void bindImg(String imageUrl,ImageView imageView){
// Uri url = Uri.parse(imageUrl);
// try {
// Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(url));
// // Bitmap bitmap = BitmapUtils.getBitmap(mRrm.getInputStream(url));
// imageView.setImageBitmap(bitmap);
// } catch (IOException e) {
// imageView.setImageResource(R.drawable.mygame_default_icon);
// }
// }
// /**
// *
// * @author fcs
// * @Description:为一个imageView绑定一张圆角图片，roundPx设置圆角的大小（5-10）
// * @date 2013-2-25 下午2:22:37
// */
// public void bindRoundImg(String imageUrl,ImageView imageView,int roundPx){
// Uri url = Uri.parse(imageUrl);
// try {
// Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(url));
// // Bitmap bitmap = BitmapUtils.getBitmap(mRrm.getInputStream(url));
// imageView.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(bitmap,roundPx));
// } catch (IOException e) {
// imageView.setImageResource(R.drawable.mygame_default_icon);
// }
// }
// /* *//**
// * 设置头像图片
// * @author fcs
// * @Description:
// * @date 2013-2-25 上午10:39:54
// *//*
// public void bindImg(String imageUrl,DoubleImageView imageView){
// Uri url = Uri.parse(imageUrl);
// try {
// Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(url));
// // Bitmap bitmap = BitmapUtils.getBitmap(mRrm.getInputStream(url));
// imageView.setImageBitmap(bitmap);
// } catch (IOException e) {
// imageView.setImageResource(R.drawable.mygame_default_icon);
// }
// }*/
// /**
// * @author wsd
// * @Description:为一个ImageView绑定一张图片
// * @date 2012-12-7 下午4:16:35
// */
//
// public void bindImg(String imageUrl,ImageView imageView,int
// defaultImageResource){
// Uri url = Uri.parse(imageUrl);
// try {
// Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(url));
// imageView.setImageBitmap(bitmap);
// } catch (IOException e) {
// imageView.setImageResource(defaultImageResource);
// }
// }
//
// /**
// * @author wsd
// * @Description:移除这个适配器在远程资源管理器上注册的观察者对象
// * 这个唯一的机会来移除观察者，若不调用将会造成内存泄露
// * @date 2012-12-6 下午8:45:39
// */
// public void removeObserver() {
// mRrm.deleteObserver(mResourcesObserver);
// }
// /**
// * @author wsd
// * @Description:远程资源管理器的观察者
// * 当有图片下载下来就通知适配器更新数据
// * @date 2012-12-6 下午8:45:39
// */
// private class RemoteResourceManagerObserver implements Observer {
// @Override
// public void update(Observable observable, Object data) {
// mHandler.post(new Runnable() {
// @Override
// public void run() {
// notifyDataSetChanged();
// }
// });
// }
// }
//
// }

