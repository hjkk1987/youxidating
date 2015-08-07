package com.sxhl.market.view;

import com.sxhl.market.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;

/**
 * @author yindangchao
 * @date 2015/3/14 15:20
 * @discription 自定义radiobutton，用于加更新红点提示
 */
public class MyRadioButton extends RadioButton {
	int wid;
	int hei;
	Paint paint;
	int state = 0;

	public MyRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyRadioButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (this.state == 0) {
			return;
		}
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(2);
		wid = getWidth();
		hei = getHeight();
		Bitmap bit = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.ico_game_update_remind));
		canvas.drawBitmap(bit, (wid * 4) / 7, hei / 4, paint);
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 显示更新提示
	 */
	public void showRemindPoint() {
		// TODO Auto-generated method stub
		this.state = 1;
		invalidate();
	}

	/**
	 * @author yindangchao
	 * @date 2015/3/14 15:20
	 * @discription 隐藏更新提示
	 */
	public void hidRemindPoint() {
		// TODO Auto-generated method stub
		this.state = 0;
		invalidate();
	}

}
