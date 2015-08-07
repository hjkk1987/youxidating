package com.sxhl.market.QRtools.QRcreate;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public final class QRTools {
	private static final int BLACK = 0xff000000;
	private static final int WHITE = 0xffffffff;
	private static Bitmap logoBitmap;// logo图片

	/**
	 * 生成帶logo二維碼 logo图片不能为空 ，大小不能为0
	 * 
	 * @param context
	 *            上下文参数
	 * @param str
	 *            二维码里面包含的信息
	 * @param widthAndHeight
	 *            二维码的大小（正方形边长）
	 * @param logoWidAndHeight
	 *            图中logo的半径大小
	 * @param logoRes
	 *            logo图片id
	 * @param whiteEdgeHeight
	 *            白色边缘宽度，是左右相加的总值
	 * @return bitmap
	 * @throws WriterException
	 */
	public static Bitmap createQRCode(Context context, String str,
			int widthAndHeight, int logoWidAndHeight, int logoRes,
			int whiteEdgeHeight) throws WriterException {
		int realWidthAndHeight = widthAndHeight - whiteEdgeHeight;
		final int IMAGE_HALFWIDTH = logoWidAndHeight;
		logoBitmap = BitmapFactory.decodeResource(context.getResources(),
				logoRes);
		Matrix m = new Matrix();

		float sx = (float) 2 * IMAGE_HALFWIDTH / logoBitmap.getWidth();
		float sy = (float) 2 * IMAGE_HALFWIDTH / logoBitmap.getHeight();
		m.setScale(sx, sy);
		logoBitmap = Bitmap.createBitmap(logoBitmap, 0, 0,
				logoBitmap.getWidth(), logoBitmap.getHeight(), m, false);
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new QRCodeWriter().encode(str,
				BarcodeFormat.QR_CODE, realWidthAndHeight, realWidthAndHeight,
				hints);

		int[] rec = matrix.getEnclosingRectangle();
		int resWidth = rec[2] + 1;
		int resHeight = rec[3] + 1;
		BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
		resMatrix.clear();
		for (int i = 0; i < resWidth; i++) {
			for (int j = 0; j < resHeight; j++) {
				if (matrix.get(i + rec[0], j + rec[1])) {
					resMatrix.set(i, j);
				}
			}
		}
		// 2
		int width = resMatrix.getWidth();
		int height = resMatrix.getHeight();
		int halfW = width / 2;
		int halfH = height / 2;

		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
						&& y > halfH - IMAGE_HALFWIDTH
						&& y < halfH + IMAGE_HALFWIDTH) {
					pixels[y * width + x] = logoBitmap.getPixel(x - halfW
							+ IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
				} else {
					if (resMatrix.get(x, y)) {
						pixels[y * width + x] = BLACK;
					} else {
						pixels[y * width + x] = WHITE;
					}
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		float scale = ((realWidthAndHeight * 1.0f) / (bitmap.getHeight() * 1.0f));

		Bitmap bit = small(bitmap, scale);
		int[] realPixels = new int[widthAndHeight * widthAndHeight];
//		int halfWid = widthAndHeight / 2;
		int halfEdgeWid = whiteEdgeHeight / 2;
		for (int y = 0; y < widthAndHeight; y++) {
			for (int x = 0; x < widthAndHeight; x++) {

				if (x > halfEdgeWid && x < widthAndHeight - halfEdgeWid
						&& y > halfEdgeWid && y < widthAndHeight - halfEdgeWid) {
					realPixels[y*widthAndHeight+x]=bit.getPixel(x-halfEdgeWid, y-halfEdgeWid);
				}else {
					realPixels[y * widthAndHeight + x] = WHITE;
				}
			}
		}
	
		Bitmap resBitmap = Bitmap.createBitmap(widthAndHeight, widthAndHeight,
				Bitmap.Config.ARGB_8888);
		resBitmap.setPixels(realPixels, 0, widthAndHeight, 0, 0, widthAndHeight, widthAndHeight);
		
		return resBitmap;
	}

	/**
	 * 生成不带logo二维码
	 * 
	 * @param context
	 *            上下文参数
	 * @param str
	 *            二维码里面包含的信息
	 * @param widthAndHeight
	 *            二维码的大小（正方形边长）
	 * @param whiteEdgeHeight
	 *            白色边缘宽度，是左右相加的总值
	 * @return bitmap
	 * @throws WriterException
	 */
	public static Bitmap createQRCode(Context context, String str,
			int widthAndHeight, int whiteEdgeHeight) throws WriterException {
		int realWidthAndHeight = widthAndHeight - whiteEdgeHeight;
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new QRCodeWriter().encode(str,
				BarcodeFormat.QR_CODE, realWidthAndHeight, realWidthAndHeight,
				hints);

		int[] rec = matrix.getEnclosingRectangle();
		int resWidth = rec[2] + 1;
		int resHeight = rec[3] + 1;
		BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
		resMatrix.clear();
		for (int i = 0; i < resWidth; i++) {
			for (int j = 0; j < resHeight; j++) {
				if (matrix.get(i + rec[0], j + rec[1])) {
					resMatrix.set(i, j);
				}
			}
		}
		// 2
		int width = resMatrix.getWidth();
		int height = resMatrix.getHeight();

		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				if (resMatrix.get(x, y)) {
					pixels[y * width + x] = BLACK;
				} else {
					pixels[y * width + x] = WHITE;
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		float scale = ((realWidthAndHeight * 1.0f) / (bitmap.getHeight() * 1.0f));
		Bitmap bit = small(bitmap, scale);
		int[] realPixels = new int[widthAndHeight * widthAndHeight];
//		int halfWid = widthAndHeight / 2;
		int halfEdgeWid = whiteEdgeHeight / 2;
		for (int y = 0; y < widthAndHeight; y++) {
			for (int x = 0; x < widthAndHeight; x++) {

				if (x > halfEdgeWid && x < widthAndHeight - halfEdgeWid
						&& y > halfEdgeWid && y < widthAndHeight - halfEdgeWid) {
					realPixels[y*widthAndHeight+x]=bit.getPixel(x-halfEdgeWid, y-halfEdgeWid);
				}else {
					realPixels[y * widthAndHeight + x] = WHITE;
				}
			}
		}
	
		Bitmap resBitmap = Bitmap.createBitmap(widthAndHeight, widthAndHeight,
				Bitmap.Config.ARGB_8888);
		resBitmap.setPixels(realPixels, 0, widthAndHeight, 0, 0, widthAndHeight, widthAndHeight);
		return resBitmap;
	}

	private static Bitmap small(Bitmap bitmap1, float scale) {
		Matrix matrix1 = new Matrix();
		matrix1.postScale(scale, scale); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap1, 0, 0,
				bitmap1.getWidth(), bitmap1.getHeight(), matrix1, true);
		return resizeBmp;
	}

	

}