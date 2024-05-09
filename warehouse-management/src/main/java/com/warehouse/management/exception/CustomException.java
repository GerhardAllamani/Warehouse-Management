package com.warehouse.management.exception;

import com.warehouse.management.model.Error;

public class CustomException extends Exception{

    private int code;
    private String message;
    private Error error;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public CustomException(int code,String reason, String message){
        super(message);
        Error error = new Error();
        error.setCode(code);
        error.setReason(reason);
        error.setMessage(message);
        this.error = error;
        this.code = code;
        this.message = message;
    }

    public CustomException(Error error){
        super(error.getMessage());
        this.error = error;
    }

}
