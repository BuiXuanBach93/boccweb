package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author manhnt
 */
public interface TokenRepository extends JpaRepository<ShtUserToken, String> {
	List<ShtUserToken> findByUserAndTokenType(ShmUser user, ShtUserToken.TokenType registrationToken);

	@Modifying
	@Query(value = "UPDATE ShtUserToken token SET token.expireIn = 0 WHERE token.user.id = :userId AND token.tokenString NOT LIKE :tokenString AND token.expireIn <> 0 ")
	void expiredUserTokenElse(@Param("userId") Long userId, @Param("tokenString") String tokenString);

	@Modifying
	@Query(value = "UPDATE ShtUserToken token SET token.expireIn = 0 WHERE token.user.id = :userId AND token.tokenString LIKE :tokenString AND token.expireIn <> 0 ")
	void expiredUserToken(@Param("userId") Long userId, @Param("tokenString") String tokenString);
}
