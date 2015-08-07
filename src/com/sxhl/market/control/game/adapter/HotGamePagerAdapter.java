package com.sxhl.market.control.game.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

public class HotGamePagerAdapter extends PagerAdapter{
	List<ImageView> imageViews;
	public HotGamePagerAdapter(List<ImageView> imageViews) {
		// TODO Auto-generated constructor stub
		this.imageViews=imageViews;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager)container).addView(imageViews.get(position));
		return imageViews.get(position);
	}
	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)container).removeView(imageViews.get(position));
	}

}
