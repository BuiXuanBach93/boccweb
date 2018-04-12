package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtMsgSms;
import jp.bo.bocc.system.config.audit.Auditor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author manhnt
 */
public interface SmsRepository extends JpaRepository<ShtMsgSms, Long> {
	List<ShtMsgSms> findByPhoneNumberAndType(String phoneNumber, ShtMsgSms.SMSType type);

	List<ShtMsgSms> findByCreatedBy(Auditor auditor);
}
