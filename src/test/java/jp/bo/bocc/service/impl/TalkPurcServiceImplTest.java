package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import jp.bo.bocc.service.TalkPurcService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Namlong on 3/27/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class TalkPurcServiceImplTest {

    @Autowired
    TalkPurcService talkPurcService;

    /**
     * Run R__SAMPLE_SHT_TALK_PURC.sql script
     * Data existed in table SHT_TALK_PURC with TALK_PURC_POST_ID=1, TALK_PURC_PART_ID=2
     *
     * @throws Exception
     */

    @Test
    public void countNewMsgNumberByPostId() throws Exception {
        final Long newMsgNumber = talkPurcService.countNewMsgNumberByPostId(929L,368L);
        Assert.assertNotNull(newMsgNumber);
    }

    @Test
    public void findTalkPurcByPostIdAndPartnerId() throws Exception {
        final Long talkPurcByPostIdAndPartnerId = talkPurcService.findTalkPurcByPostIdAndPartnerId(1L, 2L);
        Assert.assertNotNull(talkPurcByPostIdAndPartnerId);
    }

    /**
     * Data does NOT exist in table SHT_TALK_PURC with TALK_PURC_POST_ID=99, TALK_PURC_PART_ID=3000
     * @throws Exception
     */
    @Test
    public void findTalkPurcByPostIdAndPartnerIdNotFound() throws Exception {
        final Long talkPurcByPostIdAndPartnerId = talkPurcService.findTalkPurcByPostIdAndPartnerId(99L, 300L);
        Assert.assertNull(talkPurcByPostIdAndPartnerId);
    }

    /**
     * Run R__SAMPLE_SHT_TALK_PURC.sql script
     * Data existed in table SHT_TALK_PURC with TALK_PURC_POST_ID=1, TALK_PURC_PART_ID=2
     *
     * @throws Exception
     */
    @Test
    public void isFirstPurchaseTrue() throws Exception {
        final boolean firstPurchase = talkPurcService.isFirstPurchase(1L, 2L);
        Assert.assertTrue(firstPurchase);
    }

    /**
     * This is not the first time for purchasing talk.
     * @throws Exception
     */
    @Test
    public void isFirstPurchaseFalse() throws Exception {
        final boolean firstPurchase = talkPurcService.isFirstPurchase(555L, 444L);
        Assert.assertFalse(firstPurchase);
    }

    /**
     * Data does NOT exist with with TALK_PURC_POST_ID=2, TALK_PURC_PART_ID=200
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @Rollback(value = false)
    public void createTalkPurc() throws Exception {
        TalkPurcCreateBody request = new TalkPurcCreateBody();
        request.setMsgContent("What");
        request.setPartId(2L);
        request.setPostId(2L);
        ShmUser shmUser = new ShmUser();
        shmUser.setId(2L);
        talkPurcService.createTalkPurc(shmUser, request);
        Assert.assertTrue(true);
    }

    /**
     * Select post has talk in conversation.
     * @throws Exception
     */
    @Test
    public void testGetAllTalkPurcMsg() throws Exception{
        final Page<ShtTalkPurcDTO> allTalkPurcHasConversation = talkPurcService.findAllTalkPurcHasConversation(new PageRequest(0, 2), 2L, null);
        Assert.assertTrue(allTalkPurcHasConversation.getContent().size() > 0);
    }

    /**
     * Seelct all new message in talk purchase.
     * There two new msg in talkPurc: 1 - postId: 1
     * expect 4 new msg (having status is SENDING)
     */
    @Test
    public void testCountTalkHaveNewMsgByPostId(){
//        final Long aLong = talkPurcService.countTalkHaveNewMsgByPostId(1L,1L);
//        Assert.assertTrue(aLong == 2);

    }

    @Test
    public void findPartnerInTalkPurc() throws Exception {
        ShmUser shmUser = talkPurcService.findPartnerInTalkPurc(421L);
        Assert.assertNotNull(shmUser);
    }

}