package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtUserToken;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.repository.TokenRepository;
import jp.bo.bocc.service.TokenService;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.system.exception.InternalServerErrorException;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author manhnt
 */
@Service("tokenService")
@Transactional
public class TokenServiceImpl implements TokenService {

	@Autowired
	private Environment env;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenRepository tokenRepository;

	@Override
	public Pair<ShtUserToken, ShtUserToken> createAccessToken(ShmUser user) {
		try {
			ShtUserToken refreshToken = new ShtUserToken(user, ShtUserToken.TokenType.REFRESH_TOKEN); // -1 means "never expire"
			ShtUserToken accessToken = new ShtUserToken(user, ShtUserToken.TokenType.ACCESS_TOKEN);

			tokenRepository.save(refreshToken);
			tokenRepository.save(accessToken);

			return Pair.of(accessToken, refreshToken);

		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("Cannot generate tokenString", e);
		}
	}

	@Override
	public Pair<ShtUserToken, ShtUserToken> refreshAccessToken(ShmUser user, ShtUserToken refreshToken) {
		try {
			ShtUserToken accessToken = new ShtUserToken(user, ShtUserToken.TokenType.ACCESS_TOKEN);
			tokenRepository.save(accessToken);

			return Pair.of(accessToken, refreshToken);

		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("Cannot generate tokenString", e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ShtUserToken getToken(String tokenString) {
		return tokenRepository.findOne(tokenString);
	}

	@Override
	public ShtUserToken createRegistrationToken(ShmUser user) throws ServiceException {
		try {
			//Expire all previous token
			List<ShtUserToken> regTokens = tokenRepository.findByUserAndTokenType(user, ShtUserToken.TokenType.REGISTRATION_TOKEN);
			regTokens.forEach(token -> token.setExpireIn(0));

			ShtUserToken regToken = new ShtUserToken(user, ShtUserToken.TokenType.REGISTRATION_TOKEN);
			return tokenRepository.save(regToken);
		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("Cannot generate registration token", e);
		}
	}

	@Override
	public ShtUserToken createEmailActivationToken(ShmUser user) {
		try {
			ShtUserToken activationToken = new ShtUserToken(user, ShtUserToken.TokenType.MAIL_ACTIVE_TOKEN);
			return tokenRepository.save(activationToken);
		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("Cannot generate activation token", e);
		}
	}

	@Override
	public List<ShtUserToken> findAccessTokenByUser(ShmUser user) {
		return tokenRepository.findByUserAndTokenType(user, ShtUserToken.TokenType.ACCESS_TOKEN);
	}

	@Override
	public void expiredUserTokenElse(Long userId, String tokenString) {
		tokenRepository.expiredUserTokenElse(userId,tokenString);
	}

	@Override
	public void expiredUserToken(Long userId, String tokenString) {
		tokenRepository.expiredUserToken(userId,tokenString);
	}
}
