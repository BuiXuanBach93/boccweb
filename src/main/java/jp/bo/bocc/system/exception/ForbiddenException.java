package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class ForbiddenException extends ServiceException {
	public ForbiddenException(String messsage) {
		super(HttpStatus.FORBIDDEN, messsage);
	}

	public ForbiddenException(String messsage, Throwable e) {
		super(HttpStatus.FORBIDDEN, messsage, e);
	}
}
