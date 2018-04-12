package jp.bo.bocc.service.impl;

import jp.bo.bocc.service.UserFvrtService;
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
 * Created by Namlong on 4/2/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class UserFvrtServiceImplTest {
    @Autowired
    UserFvrtService userFvrtService;

    @Test
    public void getUserFvrt() throws Exception {

    }

}