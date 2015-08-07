package com.sxhl.market.model.exception;

public class StorageSpaceNotEnoughException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public StorageSpaceNotEnoughException(){
        super();
    }
    
    public StorageSpaceNotEnoughException(String detailMessage,Throwable throwable){
        super(detailMessage,throwable);
    }
    
    public StorageSpaceNotEnoughException(String detailMessage){
        super(detailMessage);
    }
    
    public StorageSpaceNotEnoughException(Throwable throwable) {
        super(throwable);
    }

}
