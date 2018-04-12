package jp.bo.bocc.system.exception;

import jp.bo.bocc.system.config.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author manhnt
 */
@ControllerAdvice(annotations = RestController.class)
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {Throwable.class})
	protected ResponseEntity<Object> handleException(Throwable ex, WebRequest request) {
		ServiceException serviceException;
		if (ex instanceof ServiceException) {
			serviceException = (ServiceException) ex;
			Log.API_EXCEPTION_LOG.debug("Got an exception", ex); //Known exception. DEBUG only
		} else {
			serviceException = new UnknownException(ex);
			Log.API_EXCEPTION_LOG.error("Got an exception", ex); //Unknown exception. This is actually an error!
		}

		return handleExceptionInternal(serviceException, serviceException, new HttpHeaders(), serviceException.statusCode, request);
	}
}