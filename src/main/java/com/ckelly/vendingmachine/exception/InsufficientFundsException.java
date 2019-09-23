package com.ckelly.vendingmachine.exception;

public class InsufficientFundsException extends Exception {

	private static final long serialVersionUID = -524466332454342489L;

	public InsufficientFundsException(String deficit) {
		super("Insufficient Funds! Please deposit: " + deficit);
	}
    
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
