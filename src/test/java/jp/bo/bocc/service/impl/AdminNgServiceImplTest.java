package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import jp.bo.bocc.service.AdminNgService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Namlong on 7/28/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class AdminNgServiceImplTest extends TestCase {
    @Autowired
    AdminNgService adminNgService;

    @Test
    public void testGetListAdminNg() throws Exception {
        final Page<ShmAdminNgDTO> listAdminNg = adminNgService.getListAdminNg(null, new PageRequest(0, 2));
        System.out.println(listAdminNg);
    }

}