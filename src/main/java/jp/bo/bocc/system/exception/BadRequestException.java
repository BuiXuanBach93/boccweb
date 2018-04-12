package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class BadRequestException extends ServiceException {
	public BadRequestException(String messsage) {
		super(HttpStatus.BAD_REQUEST, messsage);
	}

	public BadRequestException(String messsage, Throwable e) {
		super(HttpStatus.BAD_REQUEST, messsage, e);
	}
}
