package com.sxhl.market.utils.version;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 数据储存 XML文件
 * 
 */
public class PreferencesHelper {

	SharedPreferences sp;

	SharedPreferences.Editor editor;

	Context context;
	// 文件名称
	private String name = "config";

	public PreferencesHelper(Context c) {
		context = c;
		sp = context.getSharedPreferences(name, 0);
		editor = sp.edit();
	}

	/**
	 * 储存值
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 获取值
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		return sp.getString(key, null);
	}

	public void remove(String name) {
		editor.remove(name);

	}
	
	/**
	 * 储存值
	 * 
	 * @param key
	 * @param value
	 */
	public void setIntValue(String key, int value) {
		editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 获取值
	 * 
	 * @param key
	 * @return
	 */
	public int getIntValue(String key) {
		return sp.getInt(key, 0);
	}

}
