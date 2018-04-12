package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class ResourceConflictException extends ServiceException {
	public ResourceConflictException(String messsage) {
		super(HttpStatus.CONFLICT, messsage);
	}

	public ResourceConflictException(String messsage, Throwable e) {
		super(HttpStatus.CONFLICT, messsage, e);
	}
}
