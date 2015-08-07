package com.sxhl.market.utils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Shaodong Wu
 * @version 创建时间：2012-9-21 下午1:19:59 Comments
 */
public class TempDataTool{
	/**
	 * 获取某一类型的列表
	 * 
	 * @param clazz
	 * @param size
	 * @return
	 * 
	 *         List<PushIdentify> list = (List<PushIdentify>)
	 *         TempDataTool.getTempData( PushIdentify.class, 20);
	 */
	public static List<? extends Object> getTempData(Class clazz, int size) {
		List<Object> tempList = new ArrayList<Object>();
		for (int i = 0; i < size; i++) {
			Object child = null;
			try {
				child = clazz.getConstructor(new Class[] {}).newInstance(
						new Object[] {});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			setFieldsData(child, i);
			tempList.add(child);
		}
		return tempList;
	}

	/**
	 * 为对象的所有属性赋值
	 * 
	 * @param child
	 */
	private static void setFieldsData(Object child, int row) {
		// 获得对象的所有属性
		Field[] fields = child.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field mField = fields[i];
			mField.setAccessible(true);
			try {
				if (isString(mField)) {
					setStringData(child, mField, row);
				} else if (isInteger(mField)) {
					mField.set(child, row+1);
				}else if(isLong(mField)){
					if(mField.getName().equals("createTime")){
						mField.set(child, new Date().getTime() / 1000);
					}else{
						mField.set(child, row+1);
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置字符串类型的值
	 * 
	 * @param mField
	 * @param i
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static void setStringData(Object child, Field mField, int i)
			throws IllegalArgumentException, IllegalAccessException {
		if (mField.getName().equals("createTime")
				|| mField.getName().equals("datetime")) {
			mField.set(child, String.valueOf(new Date().getTime() / 1000));
		} else {
			mField.set(child, "第"+(i+1)+"行的"+mField.getName());
		}
	}

	/**
	 * 判断是否是字符串类型
	 * 
	 * @param item
	 * @return
	 */
	public static boolean isString(Field item) {

		if (item.getType().getName().toString().equals("java.lang.String")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为整形
	 * 
	 * @param item
	 * @return
	 */
	public static boolean isInteger(Field item) {
		if (item.getType().getName().toString().equals("int")) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是否为Long
	 * 
	 * @param item
	 * @return
	 */
	public static boolean isLong(Field item) {
		if (item.getType().getName().toString().equals("long")) {
			return true;
		}
		return false;
	}
}
