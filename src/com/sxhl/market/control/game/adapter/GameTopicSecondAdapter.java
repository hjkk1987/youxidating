package com.sxhl.market.control.game.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.model.entity.GameInfo;

public class GameTopicSecondAdapter extends BaseImgGroupAdapter<GameInfo> {
	private Context context;

	public GameTopicSecondAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.lv_item_hot_game, null);
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.gameIcon);
			viewHolder.gameName = (TextView) convertView
					.findViewById(R.id.gameName);
			viewHolder.starLevelRatingBar = (RatingBar) convertView
					.findViewById(R.id.startLevelRatingBar);
			viewHolder.starLevelTextview = (TextView) convertView
					.findViewById(R.id.startLevelTV);
			viewHolder.downTimes = (TextView) convertView
					.findViewById(R.id.downTimes);
			viewHolder.size = (TextView) convertView
					.findViewById(R.id.gameSize);
			viewHolder.starLevelRatingBar.setMax(5);
			// viewHolder.description = (TextView)
			// convertView.findViewById(R.id.description);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		GameInfo gameInfo = group.get(position);
		viewHolder.gameName.setText(gameInfo.getGameName());
		viewHolder.size.setText(gameInfo.getGameSize() / (1024 * 1024)
				+ context.getResources().getString(R.string.game_MB));
		viewHolder.starLevelRatingBar.setProgress((int)gameInfo.getStartLevel());
		viewHolder.starLevelTextview.setText((int)gameInfo.getStartLevel()
				+ context.getResources().getString(R.string.game_share));
		viewHolder.downTimes.setText(gameInfo.getGameDownCount()
				+ context.getResources().getString(R.string.game_down_num));
		setImageSize(110, 110);
		bindRoundImg(gameInfo.getMinPhoto(), viewHolder.icon, 10);

		// for test
		return convertView;
	}

//	private int getStartLevel() {
//		// TODO Auto-generated method stub
//		return 0;
//	}

	private class ViewHolder {
		// 图标
		private ImageView icon = null;
		// 游戏名称
		private TextView gameName = null;
		// 星星等级
		private RatingBar starLevelRatingBar = null;
		// 数字等级
		private TextView starLevelTextview = null;
		// 下载次数
		private TextView downTimes = null;
		// 大小
		private TextView size = null;
		/*
		 * // 简介 private TextView description = null;
		 */
	}
}
