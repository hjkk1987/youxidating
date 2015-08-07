package com.sxhl.market.model.exception;

public class NetWorkNotFoundException extends Exception{
	private static final long serialVersionUID = -8714305957835301255L;
	
	public NetWorkNotFoundException() {
		super();
	}
	
	public NetWorkNotFoundException(String detailMessage,Throwable throwable) {
		super(detailMessage,throwable);
	}
	
	public NetWorkNotFoundException(String detailMessage){
		this(detailMessage,null);
	}
	
	public NetWorkNotFoundException(Throwable throwable){
		this(null,throwable);
	}

}
