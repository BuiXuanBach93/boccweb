package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import jp.bo.bocc.service.FireBaseService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by Namlong on 8/2/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class FireBaseServiceImplTest extends TestCase {
//    @Autowired
//    FireBaseService fireBaseService;

    /*@Test
    public void testPutNewMsgInToFireBaseDB() throws Exception {
    }

    @Test
    public void testCreateFirstTalkPurcIntoFirebaseDB() throws Exception {
        ShtTalkPurcDTO talkPurcCreateBody = new ShtTalkPurcDTO();
        talkPurcCreateBody.setTalkPurcId(22L);
        talkPurcCreateBody.setTalkPurcMsgCreatorNickName("nickNameCreator");
        talkPurcCreateBody.setOwnerPostId(102L);
        talkPurcCreateBody.setPartnerId(2L);
        String myString = "取引を希望されました";
        talkPurcCreateBody.setMsgContent(myString);
        talkPurcCreateBody.setMsgId(3L);
        talkPurcCreateBody.setMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.NORMAL);
        talkPurcCreateBody.setCreatedAt(LocalDateTime.now());
       // fireBaseService.createFirstTalkPurcIntoFirebaseDB(talkPurcCreateBody);
    }

    @Test
    public void testCreateCustomToken() throws Exception {
       // fireBaseService.createCustomToken(12L);
    }

    @Test
    public void testSendMsgInTalkPurcIntoFirebaseDB() throws Exception {
        TalkPurcCreateBody talkPurcCreateBody = new TalkPurcCreateBody();
        talkPurcCreateBody.setTalkPurcId(22L);
        talkPurcCreateBody.setTalkPurcMsgCreatorNickName("nickNameCreator");
        talkPurcCreateBody.setOwnerPostId(102L);
        String myString = "取引を希望されました";
        talkPurcCreateBody.setMsgContent(myString);
        talkPurcCreateBody.setMsgId(3L);
        talkPurcCreateBody.setMsgType(ShtTalkPurcMsg.TalkPurcMsgTypeEnum.NORMAL);
        talkPurcCreateBody.setCreatedAt(LocalDateTime.now());
      //  fireBaseService.sendNewMsgInToFireBaseDB(talkPurcCreateBody);
    }*/

}