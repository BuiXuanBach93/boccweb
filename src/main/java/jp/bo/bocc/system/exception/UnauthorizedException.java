package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class UnauthorizedException extends ServiceException {
	public UnauthorizedException(String messsage, Throwable e) {
		super(HttpStatus.UNAUTHORIZED, messsage, e);
	}

	public UnauthorizedException(String messsage) {
		super(HttpStatus.UNAUTHORIZED, messsage);
	}
}
