package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.api.request.DeviceBodyRequest;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserToken;
import jp.bo.bocc.service.TokenService;
import jp.bo.bocc.service.UserNtfService;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.BasicAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.RefreshTokenAuthentication;
import jp.bo.bocc.system.apiconfig.resolver.JsonArg;
import jp.bo.bocc.system.exception.UnauthorizedException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author manhnt
 */
@RestController
public class AccessTokenController {

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private RequestContext requestContext;

	@Autowired
	private UserNtfService userNtfService;

	@PostMapping("/accessTokens")
	@BasicAuthentication
    @ResponseBody
	public Map<String, Object> createAccessToken(@RequestBody DeviceBodyRequest deviceBodyRequest) {
		ShmUser user = requestContext.getUser();
		if (user == null) {
			throw new UnauthorizedException("Please provide your account info in HTTP header");
		}
		Pair<ShtUserToken, ShtUserToken> tokenPair = tokenService.createAccessToken(user);
		userService.saveDeviceTokenLogin(user.getId(), deviceBodyRequest.getDeviceToken(), deviceBodyRequest.getOsType());
		return serializeAccessToken(tokenPair);
	}

	@PostMapping("/accessTokens/refresh")
	@RefreshTokenAuthentication
	public Map<String, Object> refreshAccessToken() {
		ShmUser user = requestContext.getUser();
		if (user == null) {
			throw new UnauthorizedException("Please provide your account info in HTTP header");
		}
		ShtUserToken refreshToken = requestContext.getToken();
		if (refreshToken == null) {
			throw new UnauthorizedException("Please provide your refresh token");
		}

		Pair<ShtUserToken, ShtUserToken> tokenPair = tokenService.refreshAccessToken(user, refreshToken);
		return serializeAccessToken(tokenPair);

	}

	private Map<String, Object> serializeAccessToken(final Pair<ShtUserToken, ShtUserToken> tokenPair) {
		ShtUserToken accessToken = tokenService.getToken(tokenPair.getLeft().getTokenString());
		ShtUserToken refreshToken = tokenPair.getRight();
		return new LinkedHashMap<String, Object>(){{
			put("accessToken", accessToken.getTokenString());
			put("expired", accessToken.isExpired());
			put("createdAt", accessToken.getCreatedAt());
			put("updatedAt", accessToken.getUpdatedAt());
			put("refreshToken", refreshToken.getTokenString());
		}};
	}

    @PostMapping("/pushDeviceToken")
    @AccessTokenAuthentication
    public ResponseEntity pushDeviceToken(@RequestBody DeviceBodyRequest deviceBodyRequest) {
        ShmUser user = requestContext.getUser();
        if (user == null) {
            throw new UnauthorizedException("Please provide your account info in HTTP header");
        }
        Pair<ShtUserToken, ShtUserToken> tokenPair = tokenService.createAccessToken(user);
        userService.saveDeviceTokenLogin(user.getId(), deviceBodyRequest.getDeviceToken(), deviceBodyRequest.getOsType());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

	@PostMapping("/accessTokens/expire")
	@AccessTokenAuthentication
	public ResponseEntity expireAccessToken(@JsonArg String deviceToken) {
		ShmUser user = requestContext.getUser();
		if (user == null) {
			throw new UnauthorizedException("Please provide your account info in HTTP header");
		}

		tokenService.expiredUserToken(user.getId(),requestContext.getToken().getTokenString());

		if(deviceToken != null){
			final boolean resultUnsubsribe = userService.unSubscribeTopic(user.getId(), deviceToken);
			if (resultUnsubsribe)
				userNtfService.deleteUserDeviceToken(user.getId(),deviceToken);
		}
		// clear user context and token
		requestContext.setUser(null);
		requestContext.setToken(null);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
