package jp.bo.bocc.system.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by buixu on 9/4/2017.
 */
public class ServiceExpiredVersionException extends ServiceException {
    public ServiceExpiredVersionException(HttpStatus statusCode, String messsage) {
        super(statusCode, messsage);
    }

    public ServiceExpiredVersionException(String sysConfigMsg, String url, String validVersion) {
        super(HttpStatus.HTTP_VERSION_NOT_SUPPORTED, sysConfigMsg, url, validVersion);
    }
}
