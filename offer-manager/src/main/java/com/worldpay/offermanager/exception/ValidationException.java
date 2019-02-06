package com.worldpay.offermanager.exception;

public class ValidationException extends RuntimeException{
    private static final long serialVersionUID = -376544566389765L;


    public ValidationException(String message) {
        super(message);
    }
}
