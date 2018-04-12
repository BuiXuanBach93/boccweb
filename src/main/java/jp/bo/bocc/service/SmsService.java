package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtMsgSms;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author manhnt
 */
public interface SmsService {

	/**
	 * Send registra
	 * @param phoneNumber
	 * @param content
	 * @param code
	 */
	ShtMsgSms sendRegistrationCode(String phoneNumber, String content, String code);

	/**
	 * Send an sms and persist the info
	 * @param phoneNumber
	 * @param content
	 */
	ShtMsgSms send(String phoneNumber, String content);

	ShtMsgSms getRegistrationSMSByPhoneNo(String phoneNumber, ShmUser user);

	/**
	 * List all sms
	 * @param userId
	 * @return
	 */
	List<ShtMsgSms> findSmsByUserId(Long userId);
}
