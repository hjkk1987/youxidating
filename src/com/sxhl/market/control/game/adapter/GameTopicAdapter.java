package com.sxhl.market.control.game.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.model.entity.GameType;

public class GameTopicAdapter extends BaseImgGroupAdapter<GameType> {
	private Context mContext;

	public GameTopicAdapter(Context context) {
		super(context);
		mContext = context;
		setImageSize(120, 120);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.game_topic_item, null);
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.lv_icon);
			viewHolder.topicname = (TextView) convertView
					.findViewById(R.id.topicname);
			viewHolder.topicCount = (TextView) convertView
					.findViewById(R.id.topiccount);
			viewHolder.deteil = (TextView) convertView
					.findViewById(R.id.topic_resume);
			viewHolder.readCount = (TextView) convertView
					.findViewById(R.id.topic_read_count);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		GameType gameType = group.get(position);
		/*if(gameType.getTypeId().equals("-1")){
			viewHolder.icon.setImageResource(R.drawable.newgamerecommand);
		}
		else{
			bindRoundImg(gameType.getIcon(), viewHolder.icon, 20);
		}*/
		bindRoundImg(gameType.getIcon(), viewHolder.icon, 20);
		if(position==0){
			viewHolder.topicCount.setText("("+mContext.getSharedPreferences("topic.count", mContext.MODE_PRIVATE).getInt("newgamecount", 0)+")");
		}
		else if(position==1){
			viewHolder.topicCount.setText("("+mContext.getSharedPreferences("topic.count", mContext.MODE_PRIVATE).getInt("rankingcount", 0)+")");
		}
		else{
			viewHolder.topicCount.setText("("+gameType.getGames()+")");
		}
		//
		viewHolder.topicname.setText(gameType.getName());
		viewHolder.deteil.setText(gameType.getRemark());
		viewHolder.readCount.setText("");
		// gameType.getReadTimes()
		return convertView;
	}

	private class ViewHolder {
		// 图标
		private ImageView icon;
		// 游戏专题名称
		private TextView topicname;
		//游戏数量
		private TextView topicCount;
		// 游戏专题简介
		private TextView deteil;
		// 游戏的阅读次数
		private TextView readCount;
	}
}
