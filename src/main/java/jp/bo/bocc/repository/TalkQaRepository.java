package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtTalkQa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by DonBach on 4/5/2017.
 */
public interface TalkQaRepository extends JpaRepository<ShtTalkQa, Long>, TalkQaRepositoryCustom{

    @Query("SELECT sq.qaId FROM ShtQa sq WHERE sq.shmUser.id = ?1")
    List<Long> getListQaIdFromUser(Long userIdUsingApp);

    @Query("SELECT stq FROM ShtTalkQa stq WHERE stq.shtQa.id = ?1 ORDER BY stq.createdAt ASC")
    List<ShtTalkQa> getListMsgByQaId(Long qaId);

    @Query("SELECT COUNT(stq) FROM ShtTalkQa stq WHERE stq.talkQaMsgStatus = 0 AND stq.fromAdmin = 1 AND stq.shtQa.shmUser.id = ?1")
    Long countNewMsgByUserId(Long userIdUserApp);
}
