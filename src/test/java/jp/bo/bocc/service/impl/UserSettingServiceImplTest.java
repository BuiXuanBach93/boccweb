package jp.bo.bocc.service.impl;

import jp.bo.bocc.service.UserSettingService;
import jp.bo.bocc.system.config.Profiles;
import jp.bo.bocc.system.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles({Profiles.APP, Profiles.TEST})
@Transactional
public class UserSettingServiceImplTest {

    @Autowired
    UserSettingService userSettingService;

    @Test
    public void testCheckReceiveMailOn() throws Exception {
        final boolean out = userSettingService.checkReceiveMailOn(2L);
        if (out == true) {
            System.out.println("True");
        }
    }

    @Test
    public void testCheckReceivePushOn() throws Exception {
        final boolean out = userSettingService.checkReceivePushOn(2L);
        if (out == true) {
            System.out.println("True");
        }
    }
}