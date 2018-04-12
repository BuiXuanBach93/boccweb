package jp.bo.bocc.service.impl;

import jp.bo.bocc.service.UserNtfService;
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
 * Created by DonBach on 5/28/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class UserNtfSerivceImplTest {

    @Autowired
    UserNtfService userNtfService;

    @Test
    public void deleteCurrentDeviceToken() throws Exception {
        userNtfService.deleteCurrentDeviceToken(189L,"49b7cffd0f0545d3539aefdccf85815731982fe3e459aeb6e7acdbf6484a5db2");
    }

}