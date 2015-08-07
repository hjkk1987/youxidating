package com.sxhl.market.control.game.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.model.entity.Comment;
import com.sxhl.market.utils.DateUtil;

public class CommentAdapter extends BaseImgGroupAdapter<Comment> {

	public CommentAdapter(Context context) {
		super(context);
		setImageSize(100, 100);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.game_layout_comm_item,
					null);
			holder = new ViewHolder();
			holder.imgCommAvatar = (ImageView) convertView
					.findViewById(R.id.imgCommAvatar);
			holder.tvCommUserName = (TextView) convertView
					.findViewById(R.id.tvCommUserName);
			holder.tvCommCreateTime = (TextView) convertView
					.findViewById(R.id.tvCommDate);
			holder.tvCommContent = (TextView) convertView
					.findViewById(R.id.tvCommentWord);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Comment comment = group.get(position);
		// holder.imgAvator.setImageResource(R.drawable.mayun);
		holder.tvCommUserName.setText(comment.getNickName());
		holder.tvCommCreateTime.setText(DateUtil.formatStandardDate(comment
				.getCreateTime()));
		holder.tvCommContent.setText(comment.getCommContent());
		bindRoundImg(comment.getAvator(), holder.imgCommAvatar, 7);
		return convertView;
	}

}

class ViewHolder {
	// 头像
	ImageView imgCommAvatar;
	// 用户名
	TextView tvCommUserName;
	// 创建时间
	TextView tvCommCreateTime;
	// 内容
	TextView tvCommContent;
}