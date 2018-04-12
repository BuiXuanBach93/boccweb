package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class UnknownException extends ServiceException {

	public UnknownException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Error!");
	}

	public UnknownException(Throwable e) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Error!\n Cause by: " + e.getClass().getName() + ": " + e.getMessage(), e);
	}
}
