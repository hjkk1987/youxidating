package com.sxhl.market.model.exception;

public class DBInvalidException extends RuntimeException{
	private static final long serialVersionUID= 6526592819041935721L;
	public DBInvalidException(){
		super();
	}
	
	public DBInvalidException(String detailMessage,Throwable throwable){
		super(detailMessage,throwable);
	}
	
	public DBInvalidException(String detailMessage){
		super(detailMessage);
	}
	
	public DBInvalidException(Throwable throwable) {
		super(throwable);
	}
}
