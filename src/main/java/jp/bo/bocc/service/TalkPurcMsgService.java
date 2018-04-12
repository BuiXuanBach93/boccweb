package jp.bo.bocc.service;

import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.controller.api.response.CounterMsgResponse;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import jp.bo.bocc.entity.dto.ShtTalkPurcMsgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author NguyenThuong on 4/1/2017.
 */
public interface TalkPurcMsgService {

    List<ShtTalkPurcMsg> getTalkMsgList(long talkPurcId);

    Page<ShtTalkPurcMsgDTO> findTalkPurcMsgInTalkPurc(Pageable pageable, Long shtTalkPurcId);

    TalkPurcCreateBody sendMessage(Long talkPurcId, String msgContent, Long userId, List<String> productImgList) throws Exception;

    ShtTalkPurcMsg saveMsg(ShtTalkPurcMsg shtTalkPurcMsg);

    /**
     * get msg with time.<br/>
     * isBefore is true - select all msg is before the time <br/>
     * else return other msgs.
     * @param msgTime
     * @param isBefore
     * @param talkPurcId
     * @param size
     * @param userIdUseApp
     * @return
     */
    List<ShtTalkPurcMsgDTO> findTalkPurcMsgInTalkPurcWithTime(String msgTime, String isBefore, Long talkPurcId, int size, Long userIdUseApp);

    /**
     * Count total new msg in all talk by postId.
     * @param postId
     * @param userId
     * @return
     */
    int countNewMsgForAllTalkByPostIdForOwnerPost(Long postId, Long userId);

    /**
     * Count total new msg in all talk by postId.
     * @param postId
     * @param userId
     * @return
     */
    int countNewMsgForAllTalkByPostIdFromOther(Long postId, Long userId);

    /**
     * Counter messages for mailbox.
     * @param userIdUsingApp
     * @return
     */
    CounterMsgResponse counterMsgResponseForTopPage(Long userIdUsingApp);

    /**
     * Count total new msg from admin.
     * @param userIdUsingApp
     * @return
     */
    int counNewMsgFromAdmin(Long userIdUsingApp);

    void inActiveSystemMsgForUser(Long talkPurcId, ShtTalkPurcMsg.TalkPurcMsgTypeEnum talkPurcMsgType);

    void handleTalkAfter48h(ShtTalkPurc talkPurc);

    void handleTalkAfter48h();

    CounterMsgResponse hasNewMsgResponseForTopPage(Long userIdUsingApp);

    void receiveNewMsg(boolean blocked, Long talkPurcId, Long userId);

    /**
     * Select all records
     * @return
     */
    List<ShtTalkPurcMsg> findAllOrderByTalkPurcMsgIdAsc();

    Long countNewMsgByUserId(Long userId);
}
