package com.sxhl.market.model.exception;
/** 
 * @author  
 * time�?012-8-7 下午3:23:19 
 * description: 
 */
public class ImageDownloadException extends Exception{
	private static final long serialVersionUID = -1428472538942317363L;

	public ImageDownloadException() {
		super();
	}

	public ImageDownloadException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ImageDownloadException(String detailMessage) {
		super(detailMessage);
	}

	public ImageDownloadException(Throwable throwable) {
		super(throwable);
	}

}
