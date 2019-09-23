package com.ckelly.vendingmachine.exception;

public class NoItemInventoryException extends Exception {

	private static final long serialVersionUID = -1633173183360970891L;

	public NoItemInventoryException() {
		super("SOLD OUT! Please select another item.");
	}
	
    public NoItemInventoryException(String message) {
        super(message);
    }
    
    public NoItemInventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
