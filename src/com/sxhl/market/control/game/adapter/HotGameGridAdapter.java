package com.sxhl.market.control.game.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.model.entity.GameInfo;

public class HotGameGridAdapter extends BaseImgGroupAdapter<GameInfo> {
	public HotGameGridAdapter(Context context) {
		super(context);
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.game_layout_comm_gridview_item, null);
			viewHolder.imgAvatar = (ImageView) convertView
					.findViewById(R.id.game_iv_gvItemAvatar);
			viewHolder.tvGameName = (TextView) convertView
					.findViewById(R.id.game_tv_gvItemGameName);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		GameInfo gameInfo = group.get(position);
		if(gameInfo!=null){
			viewHolder.tvGameName.setText(gameInfo.getGameName());
			setImageSize(110, 110);
			bindRoundImg(gameInfo.getMinPhoto(), viewHolder.imgAvatar,10);
		}

		return convertView;
	}

	

	private class ViewHolder {
		private ImageView imgAvatar;
		private TextView tvGameName;
	}
}
