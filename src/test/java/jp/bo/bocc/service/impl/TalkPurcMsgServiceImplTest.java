package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtTalkPurcMsg;
import jp.bo.bocc.entity.dto.ShtTalkPurcMsgDTO;
import jp.bo.bocc.repository.TalkPurcRepository;
import jp.bo.bocc.repository.TalkQaRepository;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NguyenThuong on 4/1/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class TalkPurcMsgServiceImplTest {

    @Autowired
    TalkPurcMsgServiceImpl messageService;

    @Autowired
    TalkQaRepository talkQaRepository;

    @Autowired
    TalkPurcRepository talkPurcRepository;

    /**
     * prepare data. insert data into TalkPurcMsg table.
     * @throws Exception
     */
    @Test
    public void findTalkPurcMsgInTalkPurc() throws Exception {
        Pageable pageable = new PageRequest(0, 3);
        final Page<ShtTalkPurcMsgDTO> talkPurcMsgInTalkPurc = messageService.findTalkPurcMsgInTalkPurc(pageable,2L);
        Assert.assertTrue(talkPurcMsgInTalkPurc.getContent().get(0).getTalkPurcId() == 2);
    }

    @Test
    public void findTalkPurcMsgInTalkPurcWithTimeBeforeTime() throws Exception {
        final List<ShtTalkPurcMsgDTO> talkPurcMsgInTalkPurc = messageService.findTalkPurcMsgInTalkPurcWithTime("2017-04-18T21:21:11","TRUE",2L, 1, 1L);
        Assert.assertTrue(talkPurcMsgInTalkPurc.get(0).getTalkPurcId() == 2);
    }

    @Test
    public void findTalkPurcMsgInTalkPurcWithTimeAfterTime() throws Exception {
        final List<ShtTalkPurcMsgDTO> talkPurcMsgInTalkPurc = messageService.findTalkPurcMsgInTalkPurcWithTime("2017-04-18T21:21:11","FALSE",2L, 1, 1L);
        Assert.assertTrue(talkPurcMsgInTalkPurc.isEmpty());
    }

    /**
     * Test create message successfully, no exception.
     * @throws Exception
     */
    @Test
    @Rollback(value = false)
    public void testCreateMessage() throws Exception {
        messageService.sendMessage(2L, "Hello", 2L, null);
        Assert.assertTrue(true);
    }

    /**
     * Test create message successfully, no exception.
     * UserId using app: 2
     * User having id 1 sent message to User having Id 2.
     * ShtTalkPurcMsg: id =2, talkPurcMsgStatus=0, creatorId 1
     * Expect: update talkPurcMsgStatus=1
     *
     * @throws Exception
     */
    @Test
    @Rollback(value = false)
    public void testReceiveMessage() throws Exception {
//        messageService.receiveNewMsg(2L, 2L, userId);
        final List<ShtTalkPurcMsg> talkMsgList = messageService.getTalkMsgList(2L);
        for (ShtTalkPurcMsg s : talkMsgList) {
            Assert.assertTrue(s.getTalkPurcMsgStatus() == ShtTalkPurcMsg.TalkPurcMsgStatusEnum.RECEIVED);
        }
    }

    /**
     * Prepare data: User 2 block user 1
     * User 1 send message to User 2
     * User 2 show talkPurc.
     * TalkPurcId: 1
     * All msgs have sending status.
     * Expect: message always sending status.
     */
    @Test
    @Rollback(value = false)
    public void testSendMessage() throws Exception {
        List productList = new ArrayList();
        productList.add("abcdsddfgsdfgsdf");
        productList.add("123234345sdfsd43avdsfa");
        //User 1 send msg
        messageService.sendMessage(1L, null, 87L, productList);

        //user 2 receive msg
//        messageService.receiveNewMsg(1L, 2L, userId);

        final List<ShtTalkPurcMsg> talkMsgList = messageService.getTalkMsgList(1L);
        for (ShtTalkPurcMsg s : talkMsgList) {
//            Assert.assertTrue(s.getTalkPurcMsgStatus() == ShtTalkPurcMsg.TalkPurcMsgStatusEnum.SENDING);
        }
    }

    @Test
    public void counterMsgResponseForTopPage() throws Exception {
       Long count = talkQaRepository.countNewMsgByUserId(108117L);
       System.out.print(count);
    }

}
