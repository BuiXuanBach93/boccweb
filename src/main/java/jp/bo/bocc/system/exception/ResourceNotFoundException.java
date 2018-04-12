package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class ResourceNotFoundException extends ServiceException {
	public ResourceNotFoundException(String messsage) {
		super(HttpStatus.NOT_FOUND, messsage);
	}

	public ResourceNotFoundException(String messsage, Throwable e) {
		super(HttpStatus.NOT_FOUND, messsage, e);
	}
}
