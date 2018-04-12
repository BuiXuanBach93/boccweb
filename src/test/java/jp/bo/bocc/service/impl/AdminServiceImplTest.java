package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.service.AdminService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Namlong on 4/18/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class AdminServiceImplTest {
    @Autowired
    AdminService adminService;
    @Test
    public void getListAdminUser() throws Exception {
        final Page<ShmAdmin> listAdminUser = adminService.getListAdminUser(1);
        Assert.assertTrue(listAdminUser.getSize() > 0);
    }
}