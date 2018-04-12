package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserToken;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author manhnt
 */
public interface TokenService {

	Pair<ShtUserToken, ShtUserToken> createAccessToken(ShmUser user) throws ServiceException;

	Pair<ShtUserToken, ShtUserToken> refreshAccessToken(ShmUser user, ShtUserToken refreshToken) throws ServiceException;

	ShtUserToken getToken(String tokenString);

	ShtUserToken createRegistrationToken(ShmUser user) throws ServiceException;

	ShtUserToken createEmailActivationToken(ShmUser user);

	List<ShtUserToken> findAccessTokenByUser(ShmUser user);

	void expiredUserTokenElse(Long userId, String tokenString);

	void expiredUserToken(Long userId, String tokenString);

}
