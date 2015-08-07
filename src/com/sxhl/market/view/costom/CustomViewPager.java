package com.sxhl.market.view.costom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

	public CustomViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("OnTouchEvent.....");
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		System.out.println("OnInterceptTouchEvent.....");
		return false;
	}

}
