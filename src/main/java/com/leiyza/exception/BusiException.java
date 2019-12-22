package com.leiyza.exception;

public class BusiException extends Exception {
    private String errorCode;
    private String errorMsg;
    public BusiException(){
    }
    public BusiException(String errorMsg,String errorCode){
        super(errorMsg);
        this.errorCode=errorCode;
        this.errorMsg=errorMsg;
    }
    public BusiException(String errorMsg){
        super(errorMsg);
        this.errorMsg=errorMsg;
    }
    public String getErrorCode(){
        return errorCode;
    }

    public String getErrorMsg(){
        return errorMsg;
    }
}
