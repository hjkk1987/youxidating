package com.sxhl.market.control.user.adapter;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.control.user.activity.MyGiftDetailActivity;
import com.sxhl.market.model.entity.MyGiftInfo;

/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 我的礼包适配器，按领取时间排序
 */
public class MyGiftAdapter extends BaseImgGroupAdapter<MyGiftInfo> {
	public static final int IMAGE_ROUND_RATIO = 8;
	Context context;
	LayoutInflater mInflater;
	ArrayList<Boolean> headLineStates;

	public MyGiftAdapter(Context context, ArrayList<Boolean> headLineStates) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.headLineStates = headLineStates;
	}

	/**
	 * @param headLineStates
	 *  @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 设置领取日期headLine
	 */
	public void setHeadLineStates(ArrayList<Boolean> headLineStates) {
		this.headLineStates = headLineStates;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.mygift_item, null);
			holder.tv_myGifyHeadLine = (TextView) convertView
					.findViewById(R.id.head_line_text);
			holder.iv_myGiftIcon = (ImageView) convertView
					.findViewById(R.id.iv_mygift_minphoto);
			holder.tv_myGiftName = (TextView) convertView
					.findViewById(R.id.tv_mygift_name);
			holder.tv_myGiftFrom = (TextView) convertView
					.findViewById(R.id.tv_mygift_from);
			holder.lin_myGiftItem = (LinearLayout) convertView
					.findViewById(R.id.linear_mygift_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final MyGiftInfo myGiftInfo = group.get(position);
		if (myGiftInfo != null) {
			bindRoundImg(myGiftInfo.getIcon(), holder.iv_myGiftIcon,
					IMAGE_ROUND_RATIO);
			holder.tv_myGiftName.setText(myGiftInfo.getName());
			holder.tv_myGiftFrom.setText(R.string.gift_from);
			if (headLineStates.get(position)) {
				holder.tv_myGifyHeadLine.setVisibility(View.VISIBLE);
				holder.tv_myGifyHeadLine.setText(myGiftInfo
						.getFormatReceiveTime());
			} else {
				holder.tv_myGifyHeadLine.setVisibility(View.GONE);
			}
			holder.lin_myGiftItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent in = new Intent(context, MyGiftDetailActivity.class);
					in.putExtra("myGiftInfo", myGiftInfo);
					context.startActivity(in);
				}
			});
		}
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	class ViewHolder {
		ImageView iv_myGiftIcon;
		TextView tv_myGiftName;
		TextView tv_myGiftFrom;
		TextView tv_myGifyHeadLine;
		LinearLayout lin_myGiftItem;

	}
}
