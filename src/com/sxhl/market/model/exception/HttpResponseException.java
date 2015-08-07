package com.sxhl.market.model.exception;

public class HttpResponseException extends Exception{
	private static final long serialVersionUID = -6837419529206940075L;
	
	public HttpResponseException(){
		super();
	}
	
	public HttpResponseException(String detailMessage,Throwable throwable){
		super(detailMessage,throwable);
	}
	
	public HttpResponseException(String detailMessage){
		super(detailMessage);
	}
	
	public HttpResponseException(Throwable throwable){
		super(throwable);
	}

}
