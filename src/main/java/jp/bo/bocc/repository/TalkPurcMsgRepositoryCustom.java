package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtTalkPurcMsg;
import jp.bo.bocc.entity.dto.ShtTalkPurcMsgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
public interface TalkPurcMsgRepositoryCustom {

    Page<ShtTalkPurcMsgDTO> findTalkPurcMsgInTalkPurc(Pageable pageable, Long shtTalkPurcId);

    Long countTalkWithStatusByPostId(long postId, ShtTalkPurcMsg.TalkPurcMsgStatusEnum talkPurcMsgStatus, List<Long> listTalkpurcBlocked);

    List<ShtTalkPurcMsg> findTalkPurcMsgAfterTime(LocalDateTime localDateTime, Long talkPurcId, int size, String msgTypeForOwnerPost);

    List<ShtTalkPurcMsg> findTalkPurcMsgBeforeTime(LocalDateTime localDateTime, Long talkPurcId, int size, String msgTypeForOwnerPost);

    Integer countNewMsgInTalkSentByAnother(Long partnerUserId, Long talkPurcId);

    String getLatestMsgInTalkPurc(@Param("talkPurcId") Long talkPurcId);

    ShtTalkPurcMsg getLatestMsgInTalkPurcForUser(Long talkPurcId, Boolean isOwner);

    /**
     * count new msg by postId
     * @param postId
     * @return
     */
    int countNewMsgByPostId(Long postId);

    int countNewMsgInTalkPurcForOwner(Long userId, Long postId);

    int countNewMsgForAllTalkFromOthers(Long userIdUsingApp, Long postId);

    ShtTalkPurcMsg getLatestMsgInTalkPurcReceivedAfterBlocktime(Long talkPurcId, Boolean isOwner);
}
