package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class SafeException extends ServiceException {
	public SafeException(HttpStatus statusCode, String messsage) {
		super(statusCode, messsage);
	}
}
