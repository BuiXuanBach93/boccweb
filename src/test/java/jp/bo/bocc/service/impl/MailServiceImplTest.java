package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import jp.bo.bocc.service.MailService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Namlong on 5/23/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class MailServiceImplTest {

    @Autowired
    MailService mailService;

    @Test
    public void sendEmailForFirstContact() throws Exception {
        ShtTalkPurcDTO shtTalkPurcDTO = new ShtTalkPurcDTO();
        shtTalkPurcDTO.setPostName("POST NAME");
        shtTalkPurcDTO.setOwnerPostNickName("NICK NAME");
        shtTalkPurcDTO.setEmailOwnerPost("ownerpost@gmail.com");
        mailService.sendEmailForFirstContact(shtTalkPurcDTO , "nicknameSendContact","content");
    }

}