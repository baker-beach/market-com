package com.bakerbeach.market.integration.handler;

public class EventHandlerException extends Exception {
	private static final long serialVersionUID = 1L;

	public EventHandlerException() {
	}

	public EventHandlerException(String message) {
		super(message);
	}

	public EventHandlerException(Throwable cause) {
		super(cause);
	}

	public EventHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventHandlerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
