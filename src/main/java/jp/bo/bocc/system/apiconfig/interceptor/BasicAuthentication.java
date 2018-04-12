package jp.bo.bocc.system.apiconfig.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation for APIs that require basic authentication (username/password).
 * Currently only createAccessTokenApi is using it.
 * @author manhnt
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BasicAuthentication {
}
