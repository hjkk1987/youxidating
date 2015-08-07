package com.sxhl.market.model.parsers.dom;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** 
 * @author  
 * time：2012-9-10 上午10:29:35 
 * description: XML解析
 */
public class XmlParse {
	
	/**
	 * 
	 * @param m_string
	 * @return
	 */
	public static Element getRootElement(String m_string){
		Document document = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = builder.parse(new InputSource(new ByteArrayInputStream(m_string.getBytes("utf-8"))));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document.getDocumentElement();
	}
	
}
