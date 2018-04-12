package jp.bo.bocc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jp.bo.bocc.helper.StringUtils;
import jp.bo.bocc.system.apiconfig.bean.AppContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.security.NoSuchAlgorithmException;

/**
 * @author manhnt
 */
@Entity
@Table(name = "SHT_USER_TOKEN")
@DynamicInsert  @DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_USER_TOKEN\" SET CMN_DELETE_FLAG = 1 WHERE TOKEN_ID = ?")
public class ShtUserToken extends BaseEntity {

	public ShtUserToken(ShmUser user, TokenType tokenType) throws NoSuchAlgorithmException {
		this.tokenString = StringUtils.generateUniqueToken();
		this.user = user;
		this.tokenType = tokenType;
		this.expireIn = Long.valueOf(AppContext.getEnv().getProperty("token.exp." + tokenType.name()));
	}

	@Id
	@Column(name = "TOKEN_ID", nullable = false)
	@Getter @Setter
	private String tokenString;

	@JoinColumn(name = "USER_ID", nullable = false)
	@ManyToOne(targetEntity = ShmUser.class)
	@Getter @Setter
	@JsonIgnore
	private ShmUser user;

	@Column(name = "TOKEN_TYPE", nullable = false)
	@Getter @Setter
	@Enumerated
	private TokenType tokenType;

	@Column(name = "TOKEN_EXPIRE_IN", nullable = false)
	@Getter @Setter
	private long expireIn;

	@Formula("CASE WHEN (CURRENT_DATE - CMN_ENTRY_DATE) * 86400 < TOKEN_EXPIRE_IN OR TOKEN_EXPIRE_IN < 0 THEN 0 ELSE 1 END")
	private boolean expired = false;

	public boolean isExpired() {
		return expireIn == 0 || expired;
	}

	public enum TokenType {
		ACCESS_TOKEN, REFRESH_TOKEN, MAIL_ACTIVE_TOKEN, REGISTRATION_TOKEN, RESET_PASSWORD_TOKEN
	}
}
