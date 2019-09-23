package com.ckelly.vendingmachine.exception;

public class NoSuchItemException extends Exception {

	private static final long serialVersionUID = 8799234644425135893L;

	public NoSuchItemException() {
		super("We do not carry the requested item. We apologize for any inconvenience this may have caused.");
	}
	
    public NoSuchItemException(String message) {
        super(message);
    }
    
    public NoSuchItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
