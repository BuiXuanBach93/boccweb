package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtUserRprt;
import jp.bo.bocc.service.UserRprtService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Namlong on 4/15/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class UserRprtServiceImplTest {

    @Autowired
    UserRprtService userRprtService;

    @Test
    public void countTotalReportByPostId() throws Exception {
        final Long count = userRprtService.countTotalReportByPostId(127L);
        Assert.assertTrue(count == 1);
    }

    @Test
    public void getUserRprt() throws Exception {
        final ShtUserRprt userRprt = userRprtService.getUserRprt(130, 189);
        Assert.assertTrue(userRprt != null);
    }

}