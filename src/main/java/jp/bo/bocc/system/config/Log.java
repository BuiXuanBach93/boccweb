package jp.bo.bocc.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author manhnt
 */
public class Log {
	public static final Logger APP_ROOT_LOG = LoggerFactory.getLogger("jp.bo.bocc");

	public static final Logger SECURITY_LOG = LoggerFactory.getLogger("jp.bo.bocc.security");

	public static final Logger SERVICE_LOG = LoggerFactory.getLogger("jp.bo.bocc.service");
	public static final Logger SERVICE_VALIDATION_LOG = LoggerFactory.getLogger("jp.bo.bocc.service-validation");

	public static final Logger CONTROLLER_LOG = LoggerFactory.getLogger("jp.bo.bocc.controller");
	public static final Logger CONTROLLER_VALIDATION_LOG = LoggerFactory.getLogger("jp.bo.bocc.controller-validation");

	public static final Logger REPOSITORY_LOG = LoggerFactory.getLogger("jp.bo.bocc.repository");

	public static final Logger API_EXCEPTION_LOG = LoggerFactory.getLogger("jp.bo.bocc.api-exception");
}
