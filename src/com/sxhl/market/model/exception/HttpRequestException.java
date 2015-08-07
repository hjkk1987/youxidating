package com.sxhl.market.model.exception;

public class HttpRequestException extends Exception{
	private static final long serialVersionUID= 6526592819041935721L;
	public HttpRequestException(){
		super();
	}
	
	public HttpRequestException(String detailMessage,Throwable throwable){
		super(detailMessage,throwable);
	}
	
	public HttpRequestException(String detailMessage){
		super(detailMessage);
	}
	
	public HttpRequestException(Throwable throwable) {
		super(throwable);
	}
}
