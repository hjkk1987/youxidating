package com.sxhl.market.model.exception;
/** 
 * @author  
 * time�?012-8-7 下午3:27:26 
 * description: 
 */
public class SDCardNotFoundException extends Exception {

	private static final long serialVersionUID = 5655964499790618665L;

	public SDCardNotFoundException() {
		super();
	}

	public SDCardNotFoundException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SDCardNotFoundException(String detailMessage) {
		super(detailMessage);
	}

	public SDCardNotFoundException(Throwable throwable) {
		super(throwable);
	}
}
