package com.sxhl.market.view.costom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class CustomGallery extends Gallery {
	private OnClickInterface mInterface;

	public CustomGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	public void setOnClickInterface(OnClickInterface mInterface) {
		this.mInterface = mInterface;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		int keyCode;
		if (isScrollingLeft(e1, e2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		// 设置移动事件
		onKeyDown(keyCode, null);
		return true;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			// onScroll(null,null, RecommendGameActivity.GALLERY_SPACEING+1,0);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			// onScroll(null,null,
			// (RecommendGameActivity.GALLERY_SPACEING+1)*-1,0);
		} else if (keyCode == KeyEvent.KEYCODE_A) {
			if (mInterface != null) {
				mInterface.onClick();
			}
		}
		// DebugTool.debug(Configuration.DEBUG_TAG,"点击："+keyCode);
		return super.onKeyDown(keyCode, event);
	}

	public interface OnClickInterface {
		public void onClick();
	}

	/**
	 * 
	 * @author fcs
	 * @Description:向左移动还是向右移动 true向左移动 false 向右移动
	 * @date 2013-6-1 下午2:26:22
	 */
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		if (e1 != null && e2 != null) {
			return e2.getX() > e1.getX();
		}
		return true;
	}

}
