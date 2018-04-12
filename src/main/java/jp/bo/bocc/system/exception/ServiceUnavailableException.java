package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * @author manhnt
 */
public class ServiceUnavailableException extends ServiceException {
	public ServiceUnavailableException(String messsage) {
		super(HttpStatus.SERVICE_UNAVAILABLE, messsage);
	}

	public ServiceUnavailableException(String messsage, Throwable e) {
		super(HttpStatus.SERVICE_UNAVAILABLE, messsage, e);
	}

	public ServiceUnavailableException(String sysConfigMsg, String url) {
		super(HttpStatus.SERVICE_UNAVAILABLE, sysConfigMsg, url);
	}
}
