package com.ckelly.vendingmachine.exception;

public class NoSuchItemException extends Exception {
    public NoSuchItemException(String message) {
        super(message);
    }
    
    public NoSuchItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
