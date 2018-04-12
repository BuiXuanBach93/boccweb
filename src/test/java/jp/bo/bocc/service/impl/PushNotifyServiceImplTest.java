package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.web.request.PushRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.service.AdminService;
import jp.bo.bocc.service.PushNotifyService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Namlong on 12/13/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class PushNotifyServiceImplTest {

    @Autowired
    PushNotifyService pushNotifyService;

    @Autowired
    AdminService adminService;

    @Test
    public void testAddPush() throws Exception {
        PushRequest pushNotify = new PushRequest();
        pushNotify.setPushTitle("Push title");
        pushNotify.setPushContent("PushCOntent");
        pushNotify.setPushAndroid(true);
        pushNotify.setPushIos(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        pushNotify.setTimerDateStr(sdf.format(new Date()));
        ShmAdmin shmAdmin = new ShmAdmin();
        shmAdmin.setAdminId(1L);
        shmAdmin.setAdminName("admin");
        pushNotifyService.addPush(pushNotify, shmAdmin);
    }

    @Test
    public void testAddPushTopic() throws Exception {
        PushRequest pushNotify = new PushRequest();
        pushNotify.setPushTitle("Push title");
        pushNotify.setPushContent("PushCOntent");
        pushNotify.setPushAndroid(true);
        pushNotify.setPushIos(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        pushNotify.setTimerDateStr(sdf.format(new Date()));
        ShmAdmin shmAdmin = new ShmAdmin();
        shmAdmin.setAdminId(1L);
        shmAdmin.setAdminName("admin");
        pushNotifyService.addPush(pushNotify, shmAdmin);
    }

}