package com.sxhl.market.model.parsers.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomArrayObject {

	private HashMap<String, Object> attributeMap = new HashMap<String, Object>();

	private List<Node> list = new ArrayList<Node>();

	public DomArrayObject(Node node) {
		// list属性
		NamedNodeMap nameMap = node.getAttributes();
		int mapLength = nameMap.getLength();
		for (int i = 0; i < mapLength; i++) {
			Node temp = nameMap.item(i);
			attributeMap.put(temp.getNodeName(), temp.getNodeValue());
		}
		// list子节点对象
		NodeList childNodes = node.getChildNodes();
		int length = childNodes.getLength();
		for (int i = 0; i < length; i++) {
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				list.add(child);
			}
		}
	}

	public int getIntAttribute(String key) {
		return (Integer) attributeMap.get(key);
	}

	public String getStringAttribute(String key) {
		return (String) attributeMap.get(key);
	}

	public DomObject getDomObject(int location) {
		return new DomObject(list.get(location));
	}

	public DomArrayObject getDomArrayObject(int location) {
		return new DomArrayObject(list.get(location));
	}

	public int size() {
		return list.size();
	}

	public String toString() {
		Set<String> sets = attributeMap.keySet();
		StringBuilder builder = new StringBuilder("{");
		for (Object key : sets) {
			builder.append("attribute:[" + key + " = " + attributeMap.get(key)
					+ "]");
		}
		return builder.toString() + "size:" + list.size() + "}";
	}
}
