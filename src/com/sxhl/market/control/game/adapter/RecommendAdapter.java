package com.sxhl.market.control.game.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sxhl.market.R;
import com.sxhl.market.control.common.adapter.BaseImgGroupAdapter;
import com.sxhl.market.model.entity.AdInfo;
import com.sxhl.market.model.entity.GameInfo;

/**
 * 
 * @ClassName: RecommendAdapter
 * @Description: 推荐adapter
 * @author 孔德升
 * @date 2012-12-4 下午8:53:08
 */
public class RecommendAdapter extends BaseImgGroupAdapter<AdInfo> {
	private LayoutInflater mInflater = null;
	private RecommedButtonInferface reButtonInferface = null;
	private int scrrenW;
	public RecommendAdapter(Context context) {
		super(context);
		
	}
	public void setScrrenW(int width){
		this.scrrenW=width;
	}
	
	@Override
	public int getCount() {
		return group.size() / 3;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position / 3;
	}
	
	public RecommendAdapter(Context context,
			RecommedButtonInferface buttonInferface) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		reButtonInferface = buttonInferface;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// 观察convertView随ListView滚动情况
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_game_recommend,
					null);
			holder = new ViewHolder();
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		holder.mIvLeft=convertView.findViewById(R.id.game_layout_rem_left);
		holder.mIvRight=convertView.findViewById(R.id.game_layout_rem_right);
		holder.mIvLeft.setLayoutParams(new LinearLayout.LayoutParams(scrrenW/2-10,LinearLayout.LayoutParams.MATCH_PARENT));
		holder.mIvRight.setLayoutParams(new LinearLayout.LayoutParams(scrrenW/2-10,LinearLayout.LayoutParams.MATCH_PARENT));
		// 得到各个控件的对象
		holder.bigImage = (ImageView) convertView
				.findViewById(R.id.bigImage);
		holder.middleImage1 = (ImageView) convertView
				.findViewById(R.id.middleImage1);
	
		holder.middleImage2 = (ImageView) convertView
				.findViewById(R.id.middleImage2);
		//以上个为一组
		List<AdInfo> objlist = group.subList(position * 3, position * 3 + 3);
		setImageSize(700,260);
		bindRoundImg(objlist.get(0).getUrl(), holder.bigImage,10);
		setImageSize(348, 260);
		bindRoundImg(objlist.get(1).getUrl(), holder.middleImage1,10);
		bindRoundImg(objlist.get(2).getUrl(), holder.middleImage2,10);
		holder.bigImage.setOnClickListener(bigImageOnclick);
		holder.middleImage1.setOnClickListener(bigImageOnclick);
		holder.middleImage2.setOnClickListener(bigImageOnclick);
		//在控件中绑定该控件的信息在集合中的位置
		holder.bigImage.setTag(Integer.valueOf(position * 3));
		holder.middleImage1.setTag(Integer.valueOf(position * 3 + 1));
		holder.middleImage2.setTag(Integer.valueOf(position * 3 + 2));
		return convertView;
	}

	

	public OnClickListener bigImageOnclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Integer position = (Integer) v.getTag();
			//史诗点击跳转事件，实现了接口，传过来的。
			reButtonInferface.bigImageOnClick(group.get(position));
		}
	};

	/**
	 * 
	 * @ClassName: ViewHolder
	 * @Description:加载控件
	 * @author 孔德升
	 * @date 2012-12-4 下午8:54:11
	 */
	public final class ViewHolder {
		public ImageView bigImage;
		public ImageView middleImage1;
		public ImageView middleImage2;
		public View mIvLeft,mIvRight;
	}

	/**
	 * 使用接口的好处是通用性，当传来的是什么数据，就携带什么数据
	 * @ClassName: RecommedButtonInferface
	 * @Description: 回调接口
	 * @author 孔德升
	 * @date 2012-12-4 下午8:53:37
	 */
	public interface RecommedButtonInferface {
		public void bigImageOnClick(AdInfo object);
	}

}
