package com.cat.net.exception;

/**
 * 重复协议异常
 * @author Jeremy
 */
public class RepeatProtoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5386321677764909258L;

	public RepeatProtoException() {
		super();
	}

	public RepeatProtoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RepeatProtoException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepeatProtoException(String message) {
		super(message);
	}

	public RepeatProtoException(Throwable cause) {
		super(cause);
	}

}
