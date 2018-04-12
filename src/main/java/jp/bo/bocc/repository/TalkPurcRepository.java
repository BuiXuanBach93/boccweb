package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtTalkPurc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
public interface TalkPurcRepository extends JpaRepository<ShtTalkPurc, Long>, TalkPurcRepositoryCustom{

    @Query("SELECT stp.talkPurcId FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :talkPurcPostId AND stp.shmUser.id = :talkPurcPartId AND ROWNUM <= 1")
    Long findTalkPurcByPostIdAndPartnerId(@Param("talkPurcPostId") Long postId, @Param("talkPurcPartId") Long partnerId);

    List<ShtTalkPurc> findByShmPostOrderByUpdatedAtDesc(ShmPost post);

    @Query("SELECT stp.talkPurcId FROM ShtTalkPurc stp INNER JOIN stp.shmPost sp WHERE stp.shmPost.postId = :postId AND sp.shmUser.id = :userId")
    List<Long> findListTalkPurcIdForOwner(@Param("postId") Long postId,@Param("userId") Long userId);

    @Query("SELECT stp.shmUser FROM ShtTalkPurc stp WHERE stp.talkPurcId = :talkPurcId")
    ShmUser findPartnerInTalkPurc(@Param("talkPurcId") Long talkPurcId);

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :postId ")
    List<ShtTalkPurc> getListTalkPurcByPostId(@Param("postId")Long postId);

    @Query("SELECT COUNT(*) FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :postId ")
    Long countTalkPurcByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(*) FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :postId AND stp.talkPurcStatus = 1 ")
    Long countTalkPurcTendToSellByPostId(@Param("postId") Long postId);

    @Query("SELECT stp.talkPurcId FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :postId AND stp.shmUser.id = :userId")
    List<Long> findListTalkPurcIdForPartner(@Param("postId") Long postId,@Param("userId") Long userId);

    @Query("SELECT stp.talkPurcId FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :postId ")
    List<Long> findListTalkPurcIdByPostId(@Param("postId") Long postId);

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :postId AND stp.talkPurcStatus = :talkPurcStatus ")
    List<ShtTalkPurc> findListTalkPurcIdByPostIdAndStatus(@Param("postId") Long postId, @Param("talkPurcStatus") ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus );

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.shmPost.shmUser.id = :userId AND stp.talkPurcStatus = :talkPurcStatus ")
    List<ShtTalkPurc> findTalksByOwnerIdAndStatus(@Param("userId") Long userId, @Param("talkPurcStatus") ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus);

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.shmUser.id = :userId AND stp.talkPurcStatus = :talkPurcStatus ")
    List<ShtTalkPurc> findTalksByPartnerIdAndStatus(@Param("userId") Long userId, @Param("talkPurcStatus") ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus);

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.shmPost.shmUser.id = :userId AND stp.talkPurcStatus IN (0,1) ")
    List<ShtTalkPurc> findTalksByOwnerIdOpen(@Param("userId") Long userId);

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :postId AND stp.talkPurcStatus IN (0,1) ")
    List<ShtTalkPurc> findTalkListByPostId(@Param("postId") Long postId);

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.shmUser.id = :userId AND stp.talkPurcStatus IN (0,1) ")
    List<ShtTalkPurc> findTalksByPartnerIdOpen(@Param("userId") Long userId);

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.shmPost.postId = :postId and stp.talkPurcId <> :talkPurcId ")
    List<ShtTalkPurc> getListOtherTalkByPostId(@Param("postId") Long postId, @Param("talkPurcId") Long talkPurcId);

    @Query("SELECT stp FROM ShtTalkPurc stp WHERE stp.talkPurcStatus = 1 and stp.talkPurcTendToSellTime <= :tendToSellTime ")
    List<ShtTalkPurc> getTalkPurcsExpiredTendToSell(@Param("tendToSellTime") LocalDateTime tendToSellTime);
}
