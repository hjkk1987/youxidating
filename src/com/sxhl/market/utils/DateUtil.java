package com.sxhl.market.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.sxhl.market.R;
import com.sxhl.market.app.Configuration;

/**
 * @author wsd
 * @Description:时间格式化工具
 * @date 2012-12-10 下午4:30:02
 */
public class DateUtil {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	/**
	 * @Title 格式化标准日期
	 * @Description
	 * @param
	 * @return 返回格式如：1970-01-01 :00:00
	 * @throws
	 */
	public static String formatStandardDate(long paramDate) {
		String retDate = null;
		try {
			retDate = sdf1.format(new Date(paramDate));
		} catch (Exception e) {
			retDate = sdf1.format(new Date());
			// retDate="1970-01-01 00:00";
		}
		return retDate;
	}

	public static String formatDate(long paramDate) {
		String retDate = null;
		try {
			retDate = sdf.format(new Date(paramDate));
		} catch (Exception e) {
			retDate = sdf.format(new Date());
			// retDate="1970-01-01 00:00";
		}
		return retDate;
	}

	public static String formatDate(long paramDate, Context context) {
		long currentTime = new Date().getTime();
		long blankTime = currentTime - paramDate;
		String retTime;

		if (blankTime > 86400000L) {
			retTime = sdf.format(new Date(paramDate));
		} else if (blankTime < 1800000L) {
			retTime = context.getString(R.string.moment_ago);
		} else if (blankTime > 1800000L && blankTime < 3600000L) {
			retTime = context.getString(R.string.halfhour_ago);
		} else {
			Object[] arr1 = new Object[2];
			arr1[0] = Long.valueOf(blankTime / 3600000L);
			arr1[1] = context.getString(R.string.hour_ago);
			retTime = String.format("%d%s", arr1);
		}
		return retTime;
	}

	public static String formatDate(Date paramDate, Context context) {
		long currentTime = new Date().getTime();
		long occurTime = paramDate.getTime();
		long blankTime = currentTime - occurTime;
		String retTime;

		if (blankTime > 86400000L) {
			retTime = sdf.format(paramDate);
		} else if (blankTime < 1800000L) {
			retTime = context.getString(R.string.moment_ago);
		} else if (blankTime > 1800000L && blankTime < 3600000L) {
			retTime = context.getString(R.string.halfhour_ago);
		} else {
			Object[] arr1 = new Object[2];
			arr1[0] = Long.valueOf(blankTime / 3600000L);
			arr1[1] = context.getString(R.string.hour_ago);
			retTime = String.format("%d%s", arr1);
		}
		return retTime;
	}

	public static String getIntervalDays(Context context, Long sl, Long el) {
		if (el == null) {
			el = new Date().getTime();
		}
		Long ei = el / 1000 - sl / 1000;
		// 根据毫秒数计算间隔天数
		int month = (int) (ei / (60 * 60 * 24 * 12));
		int days = (int) (ei / (60 * 60 * 24));
		int housrs = (int) (ei / (60 * 60));
		int Minutes = (int) (ei / (60));
		long ss = ei;
		if (ss <= 60) {
			return context.getString(R.string.second_ago, ss);
		} else if (Minutes <= 60) {
			return context.getString(R.string.minute_ago, Minutes);
		} else if (housrs <= 24) {
			return housrs + " " + context.getString(R.string.hour_ago);
		} else if (days <= 30) {
			return context.getString(R.string.day_ago, days);
		} else {
			return context.getString(R.string.moon_ago, month);
		}

	}
}
