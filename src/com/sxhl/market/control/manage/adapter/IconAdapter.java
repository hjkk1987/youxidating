package com.sxhl.market.control.manage.adapter;

import java.util.List;
import java.util.Map;

import com.sxhl.market.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class IconAdapter extends BaseAdapter {
	List<Map<String, Integer>> mListItem = null;
	private LayoutInflater mInflater = null;
	ImageViewOnClick mClick;
	public IconAdapter(Context context, List<Map<String, Integer>> listItems,ImageViewOnClick imageViewOnClick) {
		super();
		this.mInflater = LayoutInflater.from(context);
		mListItem = listItems;
		mClick=imageViewOnClick;
	}
	public IconAdapter(Context context, List<Map<String, Integer>> listItems) {
		super();
		this.mInflater = LayoutInflater.from(context);
		mListItem = listItems;
	}
	@Override
	public int getCount() {
		return mListItem.size();
	}

	@Override
	public Object getItem(int position) {
		return mListItem.get(position);
	}

	ImageView imageView = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_article_sendicon,
					null);
		}
		imageView = (ImageView) convertView.findViewById(R.id.image);
		Map<String, Integer> map = mListItem.get(position);
		if (map != null) {
			imageView.setBackgroundResource(map.get("image"));
		}
//		imageView.setOnClickListener(imageViewOnclick);
//		imageView.setTag(new Integer(position));
		return convertView;

	}

	OnClickListener imageViewOnclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Integer postion=(Integer) v.getTag();
			Map<String, Integer> map=(Map<String, Integer>) getItem(postion);
			mClick.imageOnClick(map);
		}
	};

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public interface ImageViewOnClick {
		public void imageOnClick(Map<String, Integer> map);
	}
}
