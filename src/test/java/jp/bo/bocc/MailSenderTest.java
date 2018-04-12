package jp.bo.bocc;

import jp.bo.bocc.service.MailService;
import jp.bo.bocc.system.apiconfig.bean.AppContext;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author manhnt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
public class MailSenderTest {

	private static final Pattern TITLE_REGEX = Pattern.compile("<title>(.*)</title>");
	private static final Pattern BODY_REGEX = Pattern.compile("<body>(.*)</body>", Pattern.DOTALL);

	@Autowired
	private MailService mailService;

	@Ignore
	@Test
	public void testSendMail() throws IOException {

		InputStream in = this.getClass().getClassLoader().getResourceAsStream(AppContext.getEnv().getProperty("user.reg.mail.template"));
		String template = IOUtils.toString(in);
		Matcher matcher = TITLE_REGEX.matcher(template);
		Matcher matcher2 = BODY_REGEX.matcher(template);

		String title = null;
		String body = null;

		if(matcher.find()) {
			title = matcher.group(1);
		}
		if(matcher2.find()) {
			body = matcher2.group(1);
		}

		mailService.sendEmail("workermarket@boccdev.jp", "tienmanhahai@gmail.com", title, body);
	}
}
