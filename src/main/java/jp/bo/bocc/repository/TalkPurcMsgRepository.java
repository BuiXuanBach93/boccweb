package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
public interface TalkPurcMsgRepository extends JpaRepository<ShtTalkPurcMsg, Long>, TalkPurcMsgRepositoryCustom{

    @Query("SELECT stpm FROM ShtTalkPurcMsg stpm WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND stpm.talkPurcMsgStatus = :talkPurcMsgStatus AND stpm.shmUserCreator.id <> :userId")
    List<ShtTalkPurcMsg> getNewMsgInTalkPurcIdFromOther(@Param("talkPurcId") Long talkPurcId, @Param("talkPurcMsgStatus") ShtTalkPurcMsg.TalkPurcMsgStatusEnum talkPurcMsgStatus, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE ShtTalkPurcMsg stpm SET stpm.talkPurcMsgStatus = 1 WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND (stpm.shmUserCreator.id <> :creatorId OR stpm.talkPurcMsgType =:talkPurcMsgType)")
    void updateNewMsgToReceived(@Param("talkPurcId") Long talkPurcId, @Param("creatorId") Long creatorId, @Param("talkPurcMsgType") ShtTalkPurcMsg.TalkPurcMsgTypeEnum talkPurcMsgType);

    @Modifying
    @Transactional
    @Query("UPDATE ShtTalkPurcMsg stpm SET stpm.isActive = 0 WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND stpm.talkPurcMsgType =:talkPurcMsgType")
    void inActiveSystemMsgForUser(@Param("talkPurcId") Long talkPurcId, @Param("talkPurcMsgType") ShtTalkPurcMsg.TalkPurcMsgTypeEnum talkPurcMsgType);

    ShtTalkPurcMsg findTop1ByShtTalkPurc_TalkPurcIdOrderByCreatedAtDesc(Long talkPurcId);

    @Query("SELECT COUNT(stpm) FROM ShtTalkPurcMsg stpm INNER JOIN stpm.shtTalkPurc WHERE stpm.talkPurcMsgStatus = 0 AND stpm.isActive = 1 AND stpm.talkPurcMsgType IN (0,1,4,7,10) AND stpm.shtTalkPurc.talkPurcId = ?2 AND stpm.shmUserCreator.id <> ?1")
    int countNewMsgInTalkSentForOwnerPost(Long userIdUserApp, Long talkPurcId);

    @Query("SELECT COUNT(stpm) FROM ShtTalkPurcMsg stpm INNER JOIN stpm.shtTalkPurc WHERE stpm.talkPurcMsgStatus = 0 AND stpm.isActive = 1 AND stpm.talkPurcMsgType IN (0,2,3,5,6,8,9,10) AND stpm.shtTalkPurc.talkPurcId = ?2 AND stpm.shmUserCreator.id <> ?1")
    int countNewMsgInTalkSentForPartnerPost(Long userIdUserApp, Long talkPurcId);

    @Query("SELECT COUNT(stpm) FROM ShtTalkPurcMsg stpm WHERE stpm.talkPurcMsgStatus = 0 AND stpm.isActive = 1 AND stpm.talkPurcMsgType IN (0,2,3,5) AND stpm.shtTalkPurc.talkPurcId = ?2 AND stpm.shmUserCreator.id <> ?1")
    int countNewMsgSentToOwnerPostInTalkPurc(Long ownerPostId, Long talkPurcId);

    List<ShtTalkPurcMsg> findByShtTalkPurcOrderByCreatedAtDesc(ShtTalkPurc talkPurc);

    List<ShtTalkPurcMsg> findAllByOrderByTalkPurcMsgIdAsc();

    @Query("SELECT COUNT(stpm) FROM ShtTalkPurcMsg stpm WHERE stpm.talkPurcMsgStatus = 0 AND stpm.isActive = 1 AND (stpm.shtTalkPurc.shmUser.id = ?1 OR stpm.shtTalkPurc.shmPost.shmUser.id  = ?1) AND stpm.shmUserCreator.id <> ?1")
    Long countNewMsgByUserId(Long userIdUserApp);
}
