package com.sxhl.market.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.sxhl.market.R;
/** 
 * @author  
 * time：2012-8-6 下午6:27:43 
 * description:view移动和activity切换 
 */

public class AnimationTool {
	//进入退出效果
	public static final int STYLE_ZOOMIN_ZOOMOUT = 0;
	
	//从左向右滑入效果
	public static final int STYLE_LEFT_RIGHT =1;
	
	/**
	 * @param view
	 *            要移动的view
	 * @param fromXDelta
	 *            起始x 位置
	 * @param toXDelta
	 *            目标x 位置
	 * @param fromYDelta
	 *            起始y 位置
	 * @param toYDelta
	 *            目标y 位置
	 * @param duration
	 *            移动的时间间隔
	 */
	public static void moveToAnother ( View view , float fromXDelta ,
			float toXDelta , float fromYDelta , float toYDelta , int duration ) {
		Animation anim_move = new TranslateAnimation ( fromXDelta , toXDelta ,fromYDelta , toYDelta );
		anim_move.setDuration ( duration );
		view.setAnimation ( anim_move );
		anim_move.startNow ();
	}

	/**
	 * 执行Activity 切换动画
	 * 
	 * @param context
	 *            上下文
	 * @param styleIndex
	 *            风格索引 风格索引的引用参考上面定义的常量
	 */
	public static void doActivityChangeStyle ( Context context , int styleIndex ) {
		ArrayList < int [ ] > mlist = getStyles ();
		if ( styleIndex < mlist.size () ) {
			( ( Activity ) context ).overridePendingTransition (
					mlist.get ( styleIndex ) [ 0 ] ,
					mlist.get ( styleIndex ) [ 1 ] );
		}
		else {
			/** 如果ArrayList 越界 就默认动画STYLE_ZOOMIN_ZOOMOUT风格 */
			( ( Activity ) context ).overridePendingTransition (
					mlist.get ( 0 ) [ 0 ] , mlist.get ( 0 ) [ 1 ] );
		}
	}

	/**
	 * 获取风格列表
	 * 
	 * @return ArrayList < int [ ] >
	 */
	private static ArrayList < int [ ] > getStyles () {
		ArrayList < int [ ] > mlist = new ArrayList < int [ ] > ();
		/** 仿照Iphone的进入和退出效果 **/
		mlist.add ( new int [ ] { R.anim.com_zoomin , R.anim.com_zoomout } );

		/** 由左向右滑入的效果 **/
		mlist.add ( new int [ ] { android.R.anim.slide_out_right ,
				android.R.anim.slide_in_left } );
		return mlist;
	}

}
