package com.sxhl.market.control.manage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.model.entity.CollectionInfo;
import com.sxhl.market.utils.StringTool;

/**
 * @ClassName: CollectionAdapter
 * @Description: 收藏适配器
 * @author 孔德升
 * @date 2012-12-5 上午9:40:05
 */
public class CollectionAdapter extends BaseImgGroupAdapter<CollectionInfo> {
	private Context mContext;
	private LayoutInflater mInflater = null;
	private CollectionButtonClickInterface mCollectionButtonClickInterface;

	public CollectionAdapter(Context context,
			CollectionButtonClickInterface clickInterface) {
		super(context);
		mContext = context;
		this.mInflater = LayoutInflater.from(context);
		mCollectionButtonClickInterface = clickInterface;
		setImageSize(110, 110);
		mImageFetcher.setLoadingImage(R.drawable.minphoto);
	}

	public CollectionAdapter(Context context) {
		super(context);
	}

	// @SuppressLint("UseValueOf")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// 观察convertView随ListView滚动情况
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.manage_collection_lv_layout, null);
			holder = new ViewHolder();
			// 得到各个控件的对象
			holder.tvIcon = (ImageView) convertView
					.findViewById(R.id.manage_iv_icon);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.manage_tv_name);
			holder.proessBarStarLevel = (RatingBar) convertView
					.findViewById(R.id.manage_ratingbar_starlevel);
			holder.tvStarLevel = (TextView) convertView
					.findViewById(R.id.manage_tv_startlevel);
			holder.tvDowntimes = (TextView) convertView
					.findViewById(R.id.manage_tv_downtimes);
			holder.tvSize = (TextView) convertView
					.findViewById(R.id.manage_tv_size);
			holder.btnDownInstall = (Button) convertView
					.findViewById(R.id.manage_btn_downinstall);
			holder.btnDelete = (Button) convertView
					.findViewById(R.id.manage_btn_delcollection);
			holder.proessBarStarLevel.setMax(5);
			// 绑定ViewHolder对象
			convertView.setTag(holder);
		} else {
			// 取出ViewHolder对象
			holder = (ViewHolder) convertView.getTag();
		}
		CollectionInfo gameInfo = (CollectionInfo) group.get(position);
		if (null != gameInfo) {
			holder.tvName.setText(gameInfo.getName());

			holder.proessBarStarLevel.setProgress((int) Float
					.parseFloat(gameInfo.getStarLevel()));
			holder.tvStarLevel.setText(gameInfo.getStarLevel()
					+ mContext.getString(R.string.game_share));
			holder.tvSize.setText(String.valueOf(StringTool
					.StringToFloat(gameInfo.getSize()))
					+ mContext.getString(R.string.game_MB));
			holder.tvDowntimes.setText(String.valueOf(gameInfo.getDownCounts())
					+ mContext.getString(R.string.game_down_num));
			bindRoundImg(gameInfo.getIconMin(), holder.tvIcon, 10);
			convertView.setOnClickListener(detail);
			// 为下载安装按钮提供监听
			holder.btnDownInstall
					.setOnClickListener(new DownLoadorInstallListern());
			// 为移除收藏提供监听
			holder.btnDelete.setOnClickListener(new CollectionDeleteListern());
		}
		convertView.setTag(convertView.getId(), Integer.valueOf(position));
		// 为每个按钮设置一个tag..
		holder.btnDownInstall.setTag(Integer.valueOf(position));
		holder.btnDelete.setTag(Integer.valueOf(position));
		return convertView;
	}

	// 注册监听器
	public class DownLoadorInstallListern implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v != null) {
				Integer position = (Integer) v.getTag();
				CollectionInfo gameInfo = (CollectionInfo) group.get(position);
				mCollectionButtonClickInterface.softDownLoadOrInstall(gameInfo);
			}
		}
	}

	public class CollectionDeleteListern implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v != null) {
				Integer position = (Integer) v.getTag();
				CollectionInfo gameInfo = (CollectionInfo) group.get(position);
				mCollectionButtonClickInterface.softDelete(gameInfo);
			}
		}

	}

	private OnClickListener detail = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Integer position = (Integer) v.getTag(v.getId());
			if (position < group.size()) {
				CollectionInfo gameInfo = (CollectionInfo) group.get(position);
				mCollectionButtonClickInterface.jtoGameDetail(gameInfo);
			}

		}
	};

	/**
	 * 
	 * @ClassName: ViewHolder
	 * @Description: 显示控件对象
	 * @author 孔德升
	 * @date 2012-12-5 下午1:37:00
	 */
	private final class ViewHolder {
		// 软件图标
		private ImageView tvIcon;
		// 软件名称
		private TextView tvName;
		// 软件星级
		private RatingBar proessBarStarLevel;
		// 软件星级评分
		private TextView tvStarLevel;
		// 软件下载次数
		private TextView tvDowntimes;
		// 软件大小
		private TextView tvSize;
		// 软件下载安装按钮
		private Button btnDownInstall;
		// 软件移除收藏按钮
		private Button btnDelete;
	}

	/**
	 * @ClassName: CollectionButtonClickInterface
	 * @Description: 定义接口 实现按钮点击操作
	 * @author 孔德升
	 * @date 2012-12-5 下午1:37:34
	 */
	public interface CollectionButtonClickInterface {
		public void softDownLoadOrInstall(CollectionInfo gameInfo);

		public void softDelete(CollectionInfo gameInfo);

		public void jtoGameDetail(CollectionInfo gameInfo);
	}
}
