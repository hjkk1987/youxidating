package com.sxhl.market.control.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.control.user.activity.GiftListActivity;
import com.sxhl.market.model.entity.GiftGameInfo;
/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 有礼包的游戏列表适配器
 */
public class GiftGameAdapter extends BaseImgGroupAdapter<GiftGameInfo> {
	public static final int IMAGE_ROUND_RATIO = 8;
	private Context mContext;
	private LayoutInflater mInflater;

	public GiftGameAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHodler holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.gift_game_item, null);
			holder = new ViewHodler();
			holder.iv_maxPhoto = (ImageView) convertView
					.findViewById(R.id.imageView_game_maxPhoto);
			holder.iv_minPhoto = (ImageView) convertView
					.findViewById(R.id.imageView_game_minPhoto);
			holder.tv_gameName = (TextView) convertView
					.findViewById(R.id.tv_gameName);
			holder.tv_gift_num = (TextView) convertView
					.findViewById(R.id.tv_gift_num);
			holder.btn_receive_gift = (Button) convertView
					.findViewById(R.id.btn_receive_gift);
			convertView.setTag(holder);
		} else {
			holder = (ViewHodler) convertView.getTag();
		}

		final GiftGameInfo giftGameInfo = group.get(position);
		if (giftGameInfo != null) {

			bindRoundImg(giftGameInfo.getMaxPhoto(), holder.iv_maxPhoto,
					IMAGE_ROUND_RATIO);
			bindRoundImg(giftGameInfo.getMinPhoto(), holder.iv_minPhoto,
					IMAGE_ROUND_RATIO);
			holder.tv_gameName.setText(giftGameInfo.getGameName());
			holder.tv_gift_num
					.setText("共有" + giftGameInfo.getGiftNum() + "个礼包");
			holder.btn_receive_gift.setText(R.string.gift_receive);
			holder.btn_receive_gift.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(mContext, GiftListActivity.class);
					i.putExtra("giftGameInfo", giftGameInfo);
					mContext.startActivity(i);
				}
			});
			holder.iv_maxPhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent(mContext, GiftListActivity.class);
					i.putExtra("giftGameInfo", giftGameInfo);
					mContext.startActivity(i);
				}
			});
		}
		return convertView;
	}

	class ViewHodler {
		ImageView iv_maxPhoto;
		ImageView iv_minPhoto;
		TextView tv_gameName;
		TextView tv_gift_num;
		Button btn_receive_gift;
	}

}
