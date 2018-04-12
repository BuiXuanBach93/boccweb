package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class InternalServerErrorException extends ServiceException {
	public InternalServerErrorException(String messsage) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, messsage);
	}

	public InternalServerErrorException(String messsage, Throwable e) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, messsage, e);
	}
}
