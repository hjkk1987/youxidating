package com.sxhl.market.view.costom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerInListview extends ViewPager {

	public ViewPagerInListview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ViewPagerInListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
        getParent().requestDisallowInterceptTouchEvent(true);    
        return super.dispatchTouchEvent(ev);  
    }  
}
