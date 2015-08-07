package com.sxhl.market.model.exception;
/** 
 * @author  
 * time�?012-8-7 下午3:26:20 
 * description: 
 */
public class XmlParseException extends Exception{
	private static final long serialVersionUID = -2460886193438055563L;

	public XmlParseException() {
		super();
	}

	public XmlParseException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public XmlParseException(String detailMessage) {
		super(detailMessage);
	}

	public XmlParseException(Throwable throwable) {
		super(throwable);
	}

}
