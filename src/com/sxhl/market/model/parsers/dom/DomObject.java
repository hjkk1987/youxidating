package com.sxhl.market.model.parsers.dom;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomObject {

	private HashMap<String, Object> content = new HashMap<String, Object>();

	/**
	 * 
	 * @param node
	 *            对象实例节点
	 */
	public DomObject(Node node) {
		NodeList childNodes = node.getChildNodes();
		int length = childNodes.getLength();
		for (int i = 0; i < length; i++) {
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Node childValueNode = child.getFirstChild();
				if (childValueNode == null)
					continue;
				if (childValueNode.getFirstChild() == null) {
					String childValue = childValueNode.getNodeValue();
					content.put(child.getNodeName(), childValue);
				} else {
					// 数组
					content.put(child.getNodeName(), child);
				}
			}
		}
	}

	public DomArrayObject getDomArray(String key) {
		return new DomArrayObject((Node) content.get(key));
	}

	/**
	 * 为对象的所有属性赋值
	 * 
	 * @param child
	 */
	public Object setFieldsData(Class<?> cls) {
		Object child = null;
		try {
			child = cls.getConstructor(new Class[] {}).newInstance(
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
		// 获得对象的所有属性
		Field[] fields = child.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field mField = fields[i];
			mField.setAccessible(true);
			String name = mField.getName();
			if (content.containsKey(name)) {
				try {
					if (isInteger(mField)) {
						mField.set(child,
								Integer.parseInt((String) content.get(name)));
					} else {
						mField.set(child, content.get(name));
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return child;
	}

	/**
	 * 判断是否为整形
	 * 
	 * @param item
	 * @return
	 */
	private static boolean isInteger(Field item) {
		if (item.getType().getName().toString().equals("int")
				|| item.getType().getName().toString()
						.equals("java.lang.Integer")) {
			return true;
		}
		return false;
	}

	public String toString() {
		Set<String> sets = content.keySet();
		StringBuilder builder = new StringBuilder("{");
		for (Object key : sets) {
			builder.append(key + "=" + content.get(key) + ",");
		}
		return builder.toString() + "}";
	}

}
