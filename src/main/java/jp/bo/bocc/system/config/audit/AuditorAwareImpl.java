package jp.bo.bocc.system.config.audit;

import org.springframework.data.domain.AuditorAware;

/**
 * @author manhnt
 */
public class AuditorAwareImpl implements AuditorAware<Auditor> {

	private final Auditor auditor = new Auditor();

	@Override
	public Auditor getCurrentAuditor() {
		return auditor;
	}
}
