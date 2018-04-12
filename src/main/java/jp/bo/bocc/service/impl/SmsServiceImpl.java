package jp.bo.bocc.service.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtMsgSms;
import jp.bo.bocc.repository.SmsRepository;
import jp.bo.bocc.service.SmsService;
import jp.bo.bocc.system.config.Log;
import jp.bo.bocc.system.config.audit.Auditor;
import jp.bo.bocc.system.exception.InternalServerErrorException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author manhnt
 */
@Service("smsService")
@Transactional
public class SmsServiceImpl implements SmsService {

	private final static Logger LOGGER = Logger.getLogger(SmsServiceImpl.class.getName());

	@Autowired
	private SmsRepository repository;

	@Autowired
	private MessageSource messageSource;

	@Value("${sms_provider.enable}")
	private boolean enable;

	@Value("${sms_provider.url}")
	private String url;

	@Value("${sms_provider.userid}")
	private String userid;

	@Value("${sms_provider.password}")
	private String password;

	@Override
	public ShtMsgSms sendRegistrationCode(String phoneNumber, String content, String code) {
		return send0(phoneNumber, content, code);
	}

	@Override
	public ShtMsgSms send(String phoneNumber, String content) {
		return send0(phoneNumber, content, content);
	}

	private ShtMsgSms send0(String phoneNumber, String sendingContent, String storingContent) {
        LOGGER.info("BEGIN: Send sms to phone number:" + phoneNumber);
		ShtMsgSms sms = new ShtMsgSms();
		sms.setPhoneNumber(phoneNumber);
		sms.setContent(storingContent);
		sms.setType(ShtMsgSms.SMSType.REGISTRATION);

		try {
			sendSms(phoneNumber, sendingContent);
		} catch (Exception e) {
			throw new InternalServerErrorException(messageSource.getMessage("SH_E100131", null, null), e);
		}

        LOGGER.info("END: Send sms to phone number:" + phoneNumber);
		return repository.save(sms);
	}

	@Override
	@Transactional(readOnly = true)
	public ShtMsgSms getRegistrationSMSByPhoneNo(String phoneNumber, ShmUser user) {
		List<ShtMsgSms> messages = repository.findByPhoneNumberAndType(phoneNumber, ShtMsgSms.SMSType.REGISTRATION);

		Optional<ShtMsgSms> optionalSms = messages.stream()
				.filter(sms -> sms.getCreatedBy() != null && (user == null || sms.getCreatedBy().getUserId().equals(user.getId())))
				.reduce((sms1, sms2) -> {
					if (sms1.getCreatedAt().isAfter(sms2.getCreatedAt())) {
						return sms1;
					} else {
						return sms2;
					}
				});

		return optionalSms.orElse(null);
	}

	@Override
	public List<ShtMsgSms> findSmsByUserId(Long userId) {

		Auditor auditor = new Auditor();
		auditor.setUserId(userId);

		List<ShtMsgSms> smsList = repository.findByCreatedBy(auditor);
		return smsList.stream().filter(sms -> sms.getCreatedBy().getUserType() == Auditor.UserType.NORMAL_USER).collect(Collectors.toList());
	}

	private void sendSms(String phoneNumber, String content) throws UnirestException {
			LOGGER.info("BEGIN: send message to phone - "+ phoneNumber );
		if (enable) {
			HttpResponse<String> response = Unirest.get(url)
					.queryString("userid", userid)
					.queryString("password", password)
					.queryString("telno", phoneNumber)
					.queryString("body", content)
					.asString();

			String resBody = response.getBody();
			LOGGER.info("END: send message to phone - "+ phoneNumber );
			Log.SERVICE_LOG.info("[SMS PROVIDER RESPONSE]\n" + resBody);
		}
	}

}
