package com.sxhl.market.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	/**
	 * 图像背景圆角处理
	 * @author fcs
	 * @Description:Bitmap bitmap要处理的图片  roundPx 图片弯角的圆度一般是5到10之间
	 * @date 2013-2-25 下午2:03:37
	 */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx) {
        //创建与原图大小一样的bitmap文件
    	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
            bitmap.getHeight(), Config.ARGB_8888);
        //实例画布，绘制的bitmap将保存至output中
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        bitmap=null;
        return output;
    }
    /**
     * bitmap缩放
     * @author fcs
     * @Description: width要缩放的宽度 height要缩放的高度
     * @date 2013-2-25 下午7:41:02
     */
    public static Bitmap getBitmapDeflation(Bitmap bitmap,int width,int height){
    	if(null==bitmap){
    		return null;
    	}
    	float scaleWidth=0f;
    	float scaleHeight=0f;
    	//获取bitmap宽高
    	int bitmapWidth=bitmap.getWidth();
    	int bitmapHeight=bitmap.getHeight();
    	//计算缩放比,图片的宽高小于指定的宽高则不缩放
    	//计算缩放比,图片的宽高小于指定的宽高则不缩放
    	if(width<bitmapWidth){
    		scaleWidth=((float)width)/bitmapWidth;
    	}else{
    		scaleWidth=1.001f;
    	}
    	if(height<bitmapHeight){
        	scaleHeight=((float)height)/bitmapHeight;
    	}else{
    		scaleHeight=1.001f;
    	}
    	Matrix matrix=new Matrix();
    	matrix.postScale(scaleWidth, scaleHeight);
    	
    	Bitmap newBitmap=Bitmap.createBitmap(bitmap,0,0,bitmapWidth,bitmapHeight,matrix,true);
    	bitmap.recycle();
    	bitmap=null;
    	
    	return newBitmap;
    }
}
