package com.sxhl.market.control.manage.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyGameViewpagerAdapter extends PagerAdapter {
	private List<View> view;

	public MyGameViewpagerAdapter(List<View> view) {
		if (view == null) {
			this.view = new ArrayList<View>();
		} else {
			this.view = view;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return view.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager) container).addView(view.get(position));
		return view.get(position);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView((View) object);
	}

}
