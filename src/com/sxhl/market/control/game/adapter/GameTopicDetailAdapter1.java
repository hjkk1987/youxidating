package com.sxhl.market.control.game.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.sxhl.market.R;
import com.sxhl.market.app.BaseApplication;
import com.sxhl.market.app.Constant;
import com.sxhl.market.app.UrlConstant;
import com.sxhl.market.control.common.activity.BaseActivity;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.control.game.activity.CommDetailActivity;
import com.sxhl.market.control.user.activity.NewLoginAndRegisterActivity;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.CollectionInfo;
import com.sxhl.market.model.entity.Comment;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;
import com.sxhl.market.model.entity.ScreenShootInfo;
import com.sxhl.market.model.entity.UserInfo;
import com.sxhl.market.model.net.http.HttpApi;
import com.sxhl.market.model.net.http.HttpReqParams;
import com.sxhl.market.model.task.AsynTaskListener;
import com.sxhl.market.model.task.BaseTask;
import com.sxhl.market.model.task.TaskManager;
import com.sxhl.market.model.task.TaskResult;
import com.sxhl.market.view.InnerPullDownView.OnPullDownListener;
import com.sxhl.market.view.PullDownView;
import com.sxhl.market.view.costom.CustomGallery;
import com.sxhl.market.view.costom.GameDetailAndComments;

/**
 * 
 * @ClassName: GameTopicDetailAdapter
 * @Description: TODO
 * @author
 * @date 2012-12-14 下午4:13:00
 */
public class GameTopicDetailAdapter1 extends BaseImgGroupAdapter<GameInfo>{
	private ButtonClickInter buttonClick;
	private LayoutInflater mInflater = null;
	private Resources resources;
	Context context;
	
	public GameTopicDetailAdapter1(Context context) {
		super(context);

	}

	public GameTopicDetailAdapter1(Context context,
			ButtonClickInter buttonClickInter) {
		super(context);
		this.resources = context.getResources();
		mInflater = LayoutInflater.from(context);
		buttonClick = buttonClickInter;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.game_topic_detail_item,
					null);
			holder.topicMinImage = (ImageView) convertView
					.findViewById(R.id.topic_icon);
			holder.topicName = (TextView) convertView
					.findViewById(R.id.topic_name);
			holder.topicRatingbar = (RatingBar) convertView
					.findViewById(R.id.topic_barStartLevel);
			holder.topicStarlevel = (TextView) convertView
					.findViewById(R.id.topic_StartLevel);
			holder.topicDownload = (TextView) convertView
					.findViewById(R.id.topic_downloadCounts);
			holder.topicFilesize = (TextView) convertView
					.findViewById(R.id.topic_size);
			holder.topicCollection = (Button) convertView
					.findViewById(R.id.topic_colletionl);
			holder.topicDownLoad = (Button) convertView
					.findViewById(R.id.topic_download);
			holder.topicRatingbar.setMax(5);
			
			holder.detailAndComments = (GameDetailAndComments)convertView.findViewById(R.id.detailandcomments);
			
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		GameInfo gameInfo = group.get(position);
		holder.detailAndComments.setmGameInfo(gameInfo);
		setImageSize(110, 110); 
		bindRoundImg(gameInfo.getMinPhoto(), holder.topicMinImage, 10);
		holder.topicName.setText(gameInfo.getGameName());
		try {
			holder.topicRatingbar.setProgress((int) gameInfo.getStartLevel());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			holder.topicRatingbar.setProgress(5);
		}
		Group<CollectionInfo> collectionInfos = PersistentSynUtils.getModelList(
				CollectionInfo.class, "gameid='" + gameInfo.getGameId() + "'");
		if (collectionInfos != null && collectionInfos.size() > 0) {
			holder.topicCollection.setText(resources
					.getString(R.string.game_already_savegame));
			holder.topicCollection.setTag(new Integer(-1));
		} else {
			holder.topicCollection.setText(resources
					.getString(R.string.game_manage_savegame));
			holder.topicCollection.setTag(new Integer(position));
		}
		holder.topicStarlevel.setText((int) gameInfo.getStartLevel()
				+ this.resources.getString(R.string.game_share));
		holder.topicDownload.setText(gameInfo.getGameDownCount()
				+ this.resources.getString(R.string.game_down_num));
		holder.topicFilesize.setText(gameInfo.getGameSize() / (1024 * 1024)
				+ this.resources.getString(R.string.game_MB));
		holder.topicCollection.setOnClickListener(collectionBtn);

		holder.topicDownLoad.setOnClickListener(downLoadBtn);

		holder.topicDownLoad.setTag(new Integer(position));
		
		// 游戏详情
		holder.detailAndComments.getGameDescriptionName().setText(gameInfo.getGameName());
		/*holder.detailAndComments.getRatingBarHandleControl().setMax(5);
		holder.detailAndComments.getRatingBarHandleControl().setProgress(gameInfo.getControllability());*/
		if (gameInfo.getRemark() != null) {
			holder.detailAndComments.getTvDescription().setText("		" + gameInfo.getRemark());
		} else {
			holder.detailAndComments.getTvDescription().setText("");
		}
		GralleryAdapter mGralleryAdapter = new GralleryAdapter();
		mGralleryAdapter.setGroup(null);
		holder.detailAndComments.getmGallery().setAdapter(mGralleryAdapter);
		if (gameInfo.getImgs() != null) {
			Group<ScreenShootInfo> mGameImgList = gameInfo.getImgs();
			String[] str = new String[mGameImgList.size()];
			for (int i = 0; i < mGameImgList.size(); i++) {
				str[i] = mGameImgList.get(i).getPhotoUrl();
			}
			mGralleryAdapter.dataChange(str);
		}
		
		return convertView;
	}

	private void setPullDownViewProperties(PullDownView mPullDownView) {
		// 加载数据 本类使用
		// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
		mPullDownView.enableAutoFetchMore(true, 1);
		mPullDownView.setHideFooter();// 隐藏 并禁用尾部
		// mPullDownView.setShowFooter();// 显示并启用自动获取更多
		// mPullDownView.setHideHeader();// 隐藏并且禁用头部刷新
		mPullDownView.setShowHeader();// 显示并且可以使用头部刷新
	}
	
	@SuppressWarnings("unused")
	private int getStartLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	private OnClickListener collectionBtn = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Integer integer = (Integer) v.getTag();
			if (integer != -1) {
				((Button) v).setText(resources
						.getString(R.string.game_already_savegame));
				GameInfo gameInfo = (GameInfo) getItem(integer);
				buttonClick.collectionBtn(gameInfo);
			}
		}
	};

	private OnClickListener downLoadBtn = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Integer integer = (Integer) v.getTag();
			GameInfo gameInfo = (GameInfo) getItem(integer);
			buttonClick.downLoadBtn(gameInfo);
		}
	};

	/**
	 * 
	 * @ClassName: ViewHolder
	 * @Description: 加载控件
	 * @author 孔德升
	 * @date 2012-12-14 下午5:05:46
	 */
	class ViewHolder {
		ImageView topicMinImage;
		TextView topicName;
		RatingBar topicRatingbar;
		TextView topicStarlevel;
		TextView topicDownload;
		TextView topicFilesize;
		Button topicCollection;
		Button topicDownLoad;
		GameDetailAndComments detailAndComments;
	}
	
	class GralleryAdapter extends BaseAdapter {
		private String[] list;

		public GralleryAdapter() {
			// TODO Auto-generated constructor stub
			super();
		}

		private void setGroup(String[] data) {

			if (data != null) {
				// list = new String[data.length];
				// for (int i = 0; i < list.length; i++) {
				//
				// list[i] = data[list.length - i - 1];
				//
				// }
				list = data;
			} else {
				list = new String[0];
			}
		}

		private void dataChange(String[] data) {
			setGroup(data);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = new ImageView(context);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					Gallery.LayoutParams.MATCH_PARENT,
					Gallery.LayoutParams.MATCH_PARENT));
			imageView.setBackgroundResource(R.drawable.middlephoto);
			mImageFetcher.setImageSize(600, 340);
			mImageFetcher.loadImage(list[position], imageView, 8);
			imageView.setScaleType(ScaleType.FIT_XY);
			return imageView;
		}

	}
	
	/**
	 * 
	 * @ClassName: ButtonClickInter
	 * @Description: 按钮回调接口
	 * @author 孔德升
	 * @date 2012-12-14 下午5:05:34
	 */
	public interface ButtonClickInter {
		public void downLoadBtn(GameInfo gameInfo);

		public void collectionBtn(GameInfo gameInfo);
	}
	
}
