package com.sxhl.market.control.manage.adapter;

import java.io.File;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.app.Constant;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.control.manage.activity.MyGameActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.MyGameInfo;
import com.sxhl.market.model.net.http.download.DownloadTask;
import com.sxhl.market.utils.AppUtil;
import com.sxhl.market.utils.BitmapUtils;

public class MyGameAdapter extends BaseImgGroupAdapter<MyGameInfo> {
	public static final int IMAGE_ROUND_RATIO = 8;
	private Context mContext;
	private LayoutInflater mInflater;
	private PackageManager mPackageManager;
	private DownloadTask mDownTask;

	public MyGameAdapter(Context context) {
		super(context);
		this.mContext = context;

		mInflater = LayoutInflater.from(context);
		this.mPackageManager = context.getPackageManager();
		mDownTask = DownloadTask.getInstance(context);
		setImageSize(110, 110);
		mImageFetcher.setLoadingImage(R.drawable.minphoto);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.manage_mygame_gv_layout,
					null);
			holder = new ViewHolder();
			holder.updateIcon = (ImageView) convertView
					.findViewById(R.id.manage_iv_icon_remind_update);
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.manage_iv_icon);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.manage_tv_name);
			holder.LinearLayoutTipBar = (LinearLayout) convertView
					.findViewById(R.id.manage_linearlayout_tipbar);
			holder.tvProgress = (TextView) convertView
					.findViewById(R.id.manage_tv_progress);
			holder.IvState = (ImageView) convertView
					.findViewById(R.id.manage_iv_state);
			holder.LinearLayoutTipBar.setTag(R.id.manage_iv_state,
					holder.IvState);
			holder.LinearLayoutTipBar.setTag(R.id.manage_tv_progress,
					holder.tvProgress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MyGameInfo myGameInfo = group.get(position);
		holder.LinearLayoutTipBar.setTag(myGameInfo.getGameId());
		holder.tvName.setText(myGameInfo.getName());
		int state = myGameInfo.getState() & 0xff;
		holder.updateIcon.setVisibility(View.GONE);
		if (state == Constant.GAME_STATE_NOT_DOWNLOAD) {
			// 未下载
			notDownloadHandle(myGameInfo, holder);
		} else if (state == Constant.GAME_STATE_NOT_INSTALLED) {
			// 未安装
			notInstalledHandle(myGameInfo, holder);
		} else if (state == Constant.GAME_STATE_INSTALLED) {
			// 已安装
			installedHandle(myGameInfo, holder);
		} else if (state == Constant.GAME_STATE_DOWNLOAD_ERROR) {
			// 下载失败
			downloadErrorHandle(myGameInfo, holder);
		} else if (state == Constant.GAME_STATE_NEED_UPDATE) {
			if (myGameInfo.getIconUrl() != null
					&& !myGameInfo.getIconUrl().equals("")) {
				bindRoundImg(myGameInfo.getIconUrl(), holder.ivIcon,
						IMAGE_ROUND_RATIO);
			} else {
				try {
					bindRoundImg(null, holder.ivIcon, IMAGE_ROUND_RATIO,
							mContext, myGameInfo.getPackageName(),
							myGameInfo.getLaunchAct(), null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					holder.ivIcon.setImageResource(R.drawable.minphoto);
					e.printStackTrace();
				}
			}

			holder.updateIcon.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	private void notDownloadHandle(MyGameInfo myGameInfo, ViewHolder holder) {
		// DebugTool.info("md", "not download name:"+myGameInfo.getName());
		bindRoundImg(myGameInfo.getIconUrl(), holder.ivIcon, IMAGE_ROUND_RATIO);
		holder.LinearLayoutTipBar.setVisibility(View.VISIBLE);
		holder.IvState.setVisibility(View.VISIBLE);
		if (mDownTask.isDownloading(myGameInfo.getGameId())) {
			holder.IvState.setImageResource(R.drawable.down_ing);
			int percentage = mDownTask.getDownloadingPercentage(myGameInfo
					.getGameId());
			if (percentage != -1) {
				holder.tvProgress.setText(percentage + "%");
				holder.tvProgress.setVisibility(View.VISIBLE);
			} else {
				// 等待
				holder.tvProgress.setVisibility(View.GONE);
			}
		} else {
			// 未下载
			holder.IvState.setImageResource(R.drawable.down_pause);
			holder.tvProgress.setVisibility(View.GONE);
		}
	}

	private void notInstalledHandle(MyGameInfo myGameInfo, ViewHolder holder) {
		// File apkFile=new
		// File(myGameInfo.getLocalDir(),myGameInfo.getLocalFilename());
		// if(apkFile.exists()){
		if (myGameInfo.getLocalFilename().toLowerCase()
				.endsWith(MyGameActivity.ZIP_EXT)) {
			bindRoundImg(myGameInfo.getIconUrl(), holder.ivIcon,
					IMAGE_ROUND_RATIO);
			holder.LinearLayoutTipBar.setVisibility(View.GONE);
		} else {
			File apkFile = new File(myGameInfo.getLocalDir(),
					myGameInfo.getLocalFilename());
			ApplicationInfo appInfo = AppUtil.getApkInfoByPath(mContext,
					apkFile.getAbsolutePath());
			if (appInfo != null) {
				// 未安装
				// holder.ivIcon.setImageDrawable(mPackageManager.getApplicationIcon(appInfo));
				Drawable bd = (Drawable) mPackageManager
						.getApplicationIcon(appInfo);
				holder.ivIcon.setImageBitmap(BitmapUtils
						.getRoundedCornerBitmap(drawableToBitmap(bd),
								IMAGE_ROUND_RATIO));
				mPackageManager.getApplicationIcon(appInfo);
				holder.LinearLayoutTipBar.setVisibility(View.GONE);
			} else {
				apkFile.delete();
				myGameInfo.setState(Constant.GAME_STATE_NOT_DOWNLOAD);
				PersistentSynUtils.update(myGameInfo);
				notDownloadHandle(myGameInfo, holder);
			}
		}
		// } else {
		// //已下载的文件已经不存在
		// myGameInfo.setState(Constant.GAME_STATE_NOT_DOWNLOAD);
		// PersistentSynUtils.update(myGameInfo);
		// notDownloadHandle(myGameInfo,holder);
		// }
	}

	private void installedHandle(MyGameInfo myGameInfo, ViewHolder holder) {
		// try {
		// Drawable
		// bd=(Drawable)mPackageManager.getApplicationIcon(myGameInfo.getPackageName());
		// holder.ivIcon.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(drawableToBitmap(bd),
		// IMAGE_ROUND_RATIO));
		// } catch (NameNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// // 设置一张默认图片
		// bindRoundImg(myGameInfo.getIconUrl(), holder.ivIcon,
		// IMAGE_ROUND_RATIO);
		// }

		try {
			bindRoundImg(myGameInfo.getLaunchAct(), holder.ivIcon,
					IMAGE_ROUND_RATIO, mContext, myGameInfo.getPackageName(),
					myGameInfo.getLaunchAct(), myGameInfo.getIconUrl());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			holder.ivIcon.setImageResource(R.drawable.minphoto);
		}
		holder.LinearLayoutTipBar.setVisibility(View.GONE);
	}

	private void downloadErrorHandle(MyGameInfo myGameInfo, ViewHolder holder) {
		// bindImg(myGameInfo.getIconUrl(), holder.ivIcon);
		bindRoundImg(myGameInfo.getIconUrl(), holder.ivIcon, IMAGE_ROUND_RATIO);
		holder.LinearLayoutTipBar.setVisibility(View.VISIBLE);
		holder.IvState.setImageResource(R.drawable.down_error);
		holder.tvProgress.setVisibility(View.GONE);
	}

	private class ViewHolder {
		// 应用图标
		private ImageView ivIcon;
		// 应用名称
		private TextView tvName;
		// 下载状态栏
		private LinearLayout LinearLayoutTipBar;
		// 下载状态图标
		private ImageView IvState;
		// 文字进度
		private TextView tvProgress;
		// 更新图标
		private ImageView updateIcon;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
}
