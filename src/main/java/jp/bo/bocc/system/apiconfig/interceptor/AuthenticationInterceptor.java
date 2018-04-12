package jp.bo.bocc.system.apiconfig.interceptor;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShrSysConfig;
import jp.bo.bocc.entity.ShtUserToken;
import jp.bo.bocc.enums.SysConfigEnum;
import jp.bo.bocc.helper.StringUtils;
import jp.bo.bocc.helper.VersionUtils;
import jp.bo.bocc.service.SysConfigService;
import jp.bo.bocc.service.TokenService;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.config.Log;
import jp.bo.bocc.system.exception.InternalServerErrorException;
import jp.bo.bocc.system.exception.ServiceExpiredVersionException;
import jp.bo.bocc.system.exception.ServiceUnavailableException;
import jp.bo.bocc.system.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

/**
 * @author manhnt
 * By default, we assume all methods are using token-based authentication, unless specified by @NoAuthentication or @BasicAuthentication
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private RequestContext requestContext;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
    MessageSource messageSource;

    @Autowired
    SysConfigService sysConfigService;

	@Autowired
	private Environment env;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// check maintain mode

		String restrictIps = env.getProperty("ip.hblab1") + "," + env.getProperty("ip.hblab2") + "," + env.getProperty("ip.bo") + "," + env.getProperty("ip.ib");
		String ipRequest = request.getHeader("X-Forwarded-For");
		if (ipRequest == null || ipRequest.length() == 0 || "unknown".equalsIgnoreCase(ipRequest)) {
			ipRequest = request.getHeader("Proxy-Client-IP");
		}
		if (ipRequest == null || ipRequest.length() == 0 || "unknown".equalsIgnoreCase(ipRequest)) {
			ipRequest = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipRequest == null || ipRequest.length() == 0 || "unknown".equalsIgnoreCase(ipRequest)) {
			ipRequest = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ipRequest == null || ipRequest.length() == 0 || "unknown".equalsIgnoreCase(ipRequest)) {
			ipRequest = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ipRequest == null || ipRequest.length() == 0 || "unknown".equalsIgnoreCase(ipRequest)) {
			ipRequest = request.getRemoteAddr();
		}
		Log.SECURITY_LOG.warn("############## IP REQUEST : " + ipRequest);
		if(!restrictIps.contains(ipRequest)){
			ShrSysConfig sysConfig = sysConfigService.getSysConfig(SysConfigEnum.MAINTAIN_MODE);
			if (sysConfig != null && sysConfig.getSysConfigValue() == 1) {
				throw new ServiceUnavailableException(sysConfig.getSysConfigMsg(), env.getProperty("url.maintain"));
			}
		}
        // check version valid
		ShrSysConfig sysConfigValidVersion = sysConfigService.getSysConfig(SysConfigEnum.VALID_VERSION);
        String headerVersionApp = request.getHeader("Version");
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(headerVersionApp) && sysConfigValidVersion != null
				&& org.apache.commons.lang3.StringUtils.isNotEmpty(sysConfigValidVersion.getSysConfigValues())){
			VersionUtils validVersion = new VersionUtils(sysConfigValidVersion.getSysConfigValues());
			VersionUtils currentVersion = new VersionUtils(headerVersionApp);
			if(currentVersion.compareTo(validVersion) < 0){
				throw new ServiceExpiredVersionException(sysConfigValidVersion.getSysConfigMsg(), null,sysConfigValidVersion.getSysConfigValues());
			}
//        	Double validVersion = new Double(sysConfigValidVersion.getSysConfigValues());
//        	Double currentVersion = new Double(headerVersionApp);
//        	if(validVersion != null && currentVersion != null && currentVersion < validVersion){
//				throw new ServiceExpiredVersionException(sysConfigValidVersion.getSysConfigMsg(), null,sysConfigValidVersion.getSysConfigValues());
//			}
		}

        if (!(handler instanceof HandlerMethod)) {
			Log.SECURITY_LOG.warn("Handler is not HandlerMethod. Actual type is " + handler.getClass().getName());
			throw new InternalServerErrorException(messageSource.getMessage("SH_E100101", null, null));
		} else {
			String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			try {
				if (authHeader == null) return handleAnonymousRequest(request, response, (HandlerMethod) handler);
				else return handleAuthenticatedRequest(response, (HandlerMethod) handler, authHeader);
			} catch (Exception e) {
				Log.SECURITY_LOG.warn("[AuthenticationInterceptor] " + request.getMethod() + " " + request.getRequestURI() + "\n" + e.getMessage());
				throw e;
			}
		}
	}

	private boolean handleAuthenticatedRequest(HttpServletResponse response, HandlerMethod handler, String authHeader) {

        /** Basic authentication */
		if(authHeader.startsWith("Basic ")) {
			//Verify basic authentication method is being used
			Method method = handler.getMethod();
			if(!method.isAnnotationPresent(BasicAuthentication.class)) {
				throw new UnauthorizedException(messageSource.getMessage("SH_E100102", null, null));
			}
			String emailAndPw ;
			try {
				emailAndPw = new String(StringUtils.base64Decode(authHeader.substring("Basic ".length())), "UTF8");
			} catch (UnsupportedEncodingException | IllegalArgumentException e) {
				throw new UnauthorizedException(messageSource.getMessage("SH_E100103", null, null));
			}
			int index = emailAndPw.indexOf(':');
			if (index < 0) throw new UnauthorizedException(messageSource.getMessage("SH_E100104", null, null));
			String email = emailAndPw.substring(0, index);
			String password = emailAndPw.substring(index + 1);

			ShmUser user = userService.findUserByEmailAndPassword(email, password);
			if (user == null) throw new UnauthorizedException(messageSource.getMessage("SH_E100105", null, null));
			validateNormalUser(user);
			requestContext.setUser(user);
			return true;
		}

		/** Token authentication */
		else if (authHeader.startsWith("Bearer ")) {
			Method method = handler.getMethod();
			if(method.isAnnotationPresent(BasicAuthentication.class)) {
				throw new UnauthorizedException(messageSource.getMessage("SH_E100106", null, null));
			}
			String tokenString = authHeader.substring("Bearer ".length());
			ShtUserToken token = tokenService.getToken(tokenString);
			if (token == null) throw new UnauthorizedException(messageSource.getMessage("SH_E100107", null, null));
			if (token.isExpired()) throw new UnauthorizedException(messageSource.getMessage("SH_E100108", null, null));

			if (token.getTokenType() == ShtUserToken.TokenType.REFRESH_TOKEN
					&& !method.isAnnotationPresent(RefreshTokenAuthentication.class)) {
				throw new UnauthorizedException(messageSource.getMessage("SH_E100109", null, null));
			}

			if (token.getTokenType() == ShtUserToken.TokenType.REGISTRATION_TOKEN
					&& !method.isAnnotationPresent(RegAuthentication.class)) {
				throw new UnauthorizedException(messageSource.getMessage("SH_E100109", null, null));
			}

			if (token.getTokenType() == ShtUserToken.TokenType.ACCESS_TOKEN
					&& !method.isAnnotationPresent(AccessTokenAuthentication.class)) {
				throw new UnauthorizedException(messageSource.getMessage("SH_E100109", null, null));
			}

			//Check user not activated or left
			ShmUser user = token.getUser();
			switch (token.getTokenType()) {
				case ACCESS_TOKEN:
				case REFRESH_TOKEN:
				case RESET_PASSWORD_TOKEN:
					validateNormalUser(user);
					break;
				default:
					break;
			}

			requestContext.setUser(user);
			requestContext.setToken(token);
			return true;
		}

		/** Unauthenticated request */
		else {
			throw new UnauthorizedException(messageSource.getMessage("SH_E100102", null, null));
		}
	}

	private void validateNormalUser(ShmUser user) {
		if (user.getStatus() == ShmUser.Status.LEFT) {
			throw new UnauthorizedException(messageSource.getMessage("SH_E100124", null, null));
		}
		if (user.getStatus() != ShmUser.Status.ACTIVATED && user.getStatus() != ShmUser.Status.TEND_TO_LEAVE) {
			throw new UnauthorizedException(messageSource.getMessage("SH_E100110", null, null));
		}
	}

	private boolean handleAnonymousRequest(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
		//Verify noauthentication method is being used
		Method method = handler.getMethod();
		if (method.isAnnotationPresent(NoAuthentication.class)
				|| method.getDeclaringClass().isAnnotationPresent(NoAuthentication.class)) {
			return true;
		} else {
			throw new UnauthorizedException(messageSource.getMessage("SH_E100112", null, null));
		}
	}
}
