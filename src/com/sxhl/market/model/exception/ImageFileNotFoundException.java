package com.sxhl.market.model.exception;
/** 
 * @author  
 * time�?012-8-7 下午3:24:37 
 * description: 
 */
public class ImageFileNotFoundException extends Exception{
	
	private static final long serialVersionUID = 7108078798705507385L;

	public ImageFileNotFoundException() {
		super();
	}

	public ImageFileNotFoundException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ImageFileNotFoundException(String detailMessage) {
		super(detailMessage);
	}

	public ImageFileNotFoundException(Throwable throwable) {
		super(throwable);
	}

}
