package com.sxhl.market.control.game.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.model.database.PersistentSynUtils;
import com.sxhl.market.model.entity.CollectionInfo;
import com.sxhl.market.model.entity.GameInfo;
import com.sxhl.market.model.entity.Group;

/**
 * 
 * @ClassName: GameTopicDetailAdapter
 * @Description: TODO
 * @author
 * @date 2012-12-14 下午4:13:00
 */
public class GameTopicDetailAdapter extends BaseImgGroupAdapter<GameInfo> {
	private ButtonClickInter buttonClick;
	private LayoutInflater mInflater = null;
	private Resources resources;
	Context context;

	public GameTopicDetailAdapter(Context context) {
		super(context);

	}

	public GameTopicDetailAdapter(Context context,
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
			/*holder.topicGameName = (TextView) convertView
					.findViewById(R.id.imagename);
			holder.topicBigImage = (ImageView) convertView
					.findViewById(R.id.bigimage);
			holder.topicResume = (TextView) convertView
					.findViewById(R.id.resumeText);*/
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
			/*holder.linearLayout = (LinearLayout) convertView
					.findViewById(R.id.topic_detail_item);*/
			holder.topicRatingbar.setMax(5);
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}

		GameInfo gameInfo = group.get(position);
		holder.topicGameName.setText(gameInfo.getGameName());
		setImageSize(700, 260);
		bindRoundImg(gameInfo.getMaxPhoto(), holder.topicBigImage, 10);
		holder.topicResume.setText(gameInfo.getRemark());
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
		/*
		 * BtnDownListenner mBtnDownListenner=new BtnDownListenner(context);
		 * mBtnDownListenner.listen(holder.topicDownLoad, group.get(position));
		 */
		holder.topicDownLoad.setOnClickListener(downLoadBtn);

		holder.topicDownLoad.setTag(new Integer(position));
		return convertView;
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
		TextView topicGameName;
		ImageView topicBigImage;
		TextView topicResume;
		ImageView topicMinImage;
		TextView topicName;
		RatingBar topicRatingbar;
		TextView topicStarlevel;
		TextView topicDownload;
		TextView topicFilesize;
		Button topicCollection;
		Button topicDownLoad;
		LinearLayout linearLayout;
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
