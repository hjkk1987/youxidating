package com.sxhl.market.model.exception;

public class IllegalArgumentException extends Exception{
	private static final long serialVersionUID=7930985569129865101L;
	
	public IllegalArgumentException(){
		super();
	}
	
	public IllegalArgumentException(String detailMessage,Throwable throwable){
		super(detailMessage,throwable);
	}
	
	public IllegalArgumentException(String detailMessage){
		super(detailMessage);
	}
	
	public IllegalArgumentException(Throwable throwable){
		super(throwable);
	}

}
