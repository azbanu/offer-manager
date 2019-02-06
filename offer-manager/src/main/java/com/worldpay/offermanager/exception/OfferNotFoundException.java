package com.worldpay.offermanager.exception;

public class OfferNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -876544566389765L;

	
	public OfferNotFoundException(String message) {
		super(message);
	}
	
}
