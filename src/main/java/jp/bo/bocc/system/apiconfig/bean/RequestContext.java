package jp.bo.bocc.system.apiconfig.bean;

import jp.bo.bocc.entity.ShtUserToken;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.system.config.audit.Auditor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

/**
 * @author manhnt
 */
public class RequestContext {

	@Autowired
	private AuditorAware<Auditor> auditor;

	@Getter
	private ShmUser user;

	@Getter @Setter
	private ShtUserToken token;

	public void setUser(ShmUser user) {
		this.user = user;
		if(user != null){
			auditor.getCurrentAuditor().setUserId(user.getId());
			auditor.getCurrentAuditor().setUserType(Auditor.UserType.NORMAL_USER);
		}
	}
}
