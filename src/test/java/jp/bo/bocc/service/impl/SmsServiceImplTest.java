package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtMsgSms;
import jp.bo.bocc.service.SmsService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import jp.bo.bocc.system.config.audit.Auditor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author manhnt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class SmsServiceImplTest {

	@Autowired
	private SmsService smsService;

	@Autowired
	private AuditorAware<Auditor> auditorProvider;

	@Test
	public void testSave() throws Exception {

		ShtMsgSms sms = smsService.sendRegistrationCode("08000000000", "ABCD", "ABCD");
		assertThat(sms.getContent(), is("ABCD"));
		assertThat(sms.getType(), is(ShtMsgSms.SMSType.REGISTRATION));
	}

	@Test
	public void testGetRegistrationSMSByPhoneNo() throws Exception {
		//Send a new sms
		smsService.sendRegistrationCode("08000000000", "4DB5", "4DB5");

		//Retrieve the info
		ShtMsgSms sms = smsService.getRegistrationSMSByPhoneNo("08000000000", null);
		assertThat(sms.getContent(), is("4DB5"));
	}

	@Test
	public void testFindSmsByUserId() throws Exception {
		auditorProvider.getCurrentAuditor().setUserType(Auditor.UserType.NORMAL_USER);
		auditorProvider.getCurrentAuditor().setUserId(11L);
		//Send a new sms
		smsService.send("08000000000", "4DB5");

		//Retrieve the info
		List<ShtMsgSms> smsList = smsService.findSmsByUserId(11L);
		assertThat(smsList.size(), is(1));
		assertThat(smsList.get(0).getContent(), is("4DB5"));
	}
}