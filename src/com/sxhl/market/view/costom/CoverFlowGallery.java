package com.sxhl.market.view.costom;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

import com.sxhl.market.utils.ScreenZoomUtils;


public class CoverFlowGallery extends Gallery {

	public CoverFlowGallery(Context context) {
		super(context);
		this.setStaticTransformationsEnabled(true);// 允许
	}

	public CoverFlowGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	public CoverFlowGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setStaticTransformationsEnabled(true);
	}
	private Camera mCamera = new Camera();
	private int mMaxRotationAngle = 50;
	private int mMaxZoom = -490;
	//Gallery x轴中心点
	private int mCoveflowCenter;
	private boolean mAlphaMode = true;
	private boolean mCircleMode = false;

	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	public boolean getCircleMode() {
		return mCircleMode;
	}

	public void setCircleMode(boolean isCircle) {
		mCircleMode = isCircle;
	}

	public boolean getAlphaMode() {
		return mAlphaMode;
	}

	public void setAlphaMode(boolean isAlpha) {
		mAlphaMode = isAlpha;
	}

	public int getMaxZoom() {
		return mMaxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		mMaxZoom = maxZoom;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}
	//重写Garray方法 ，产生层叠和放大效果
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		//子view 的X轴中心点
		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);
		if (childCenter == mCoveflowCenter) {
			transformImageBitmap((ImageView) child, t, 0, 0);
		} else {
			//右边
			if(childCenter<mCoveflowCenter){
				rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);		
			//左边
			}else{
				rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
			}
			
			// Log.d("test", "recanglenum:"+Math.floor ((mCoveflowCenter -
			// childCenter) / childWidth));
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
						: mMaxRotationAngle;
			}
			transformImageBitmap((ImageView) child, t, rotationAngle,
					(int) Math.floor((mCoveflowCenter - childCenter)/ (childWidth==0?1:childWidth)));
		}
		return true;
	}

	/**
	 * This is called during layout when the size of this view has changed. If
	 * you were just added to the view hierarchy, you're called with the old
	 * values of 0.
	 * 
	 * @param w
	 *            Current width of this view.
	 * @param h
	 *            Current height of this view.
	 * @param oldw
	 *            Old width of this view.
	 * @param oldh
	 *            Old height of this view.
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * Transform the Image Bitmap by the Angle passed
	 * 
	 * @param imageView
	 *            ImageView the ImageView whose bitmap we want to rotate
	 * @param t
	 *            transformation
	 * @param rotationAngle
	 *            the Angle by which to rotate the Bitmap
	 */
	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle, int d) {
		child.clearColorFilter();
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
		final int rotation = Math.abs(rotationAngle);
		//mCamera.translate(0.0f, 0.0f, 100.0f);
		// As the angle of the view gets less, zoom in
		if (rotation <= mMaxRotationAngle) {
			float zoomAmount = (float) (mMaxZoom + (rotation));
			if(rotationAngle>0){
				mCamera.translate((int)(rotationAngle*0.4-1), 0.0f, zoomAmount+100f);
		        ColorMatrix cm = new ColorMatrix(); 
		        cm.setSaturation(0); 
		        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm); 
		        child.setColorFilter(cf);
			}else if(rotationAngle<0){
				mCamera.translate((int)(rotationAngle*0.85+ScreenZoomUtils.transformZoomW_1208(22)), 0.0f, zoomAmount+100f);
		        ColorMatrix cm = new ColorMatrix(); 
		        cm.setSaturation(0); 
		        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm); 
				child.setColorFilter(cf);
			}else{
				mCamera.translate(0.0f, 0.0f, zoomAmount+ 90.0f);
			}
			if (mCircleMode) {
				if (rotation < 40)
					mCamera.translate(0.0f, 155, 0.0f);
				else
					mCamera.translate(0.0f, (255 - rotation * 2.5f), 0.0f);
			}
			if (mAlphaMode) {
				((ImageView) (child)).setAlpha((int) (255 - rotation*1.5));
			}
		}
		
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		mCamera.restore();
	}
	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		// TODO Auto-generated method stub
        int selectedIndex = getSelectedItemPosition() - getFirstVisiblePosition();
        if (selectedIndex < 0) 
        {
            return i;
        }
        
        if (i < selectedIndex)
        {
            return i;
        }
        else if (i >= selectedIndex)
        {
            return childCount - 1 - i + selectedIndex;
        }
        else
        {
            return i;
        }
	}
}