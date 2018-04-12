package jp.bo.bocc.service;

import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
public interface TalkPurcService {

    /**
     * Find talk purchase by postId and partner id about a post.
     * @param postId
     * @param partnerId
     * @return
     */
    Long findTalkPurcByPostIdAndPartnerId(Long postId, Long partnerId);

    /**
     * Check is the first talk purc.
     * isFirstTalkPurc - return TRUE <br/>
     * else return FALSE
     * @param postId
     * @param partnerId
     * @return
     */
    boolean isFirstPurchase(Long postId, Long partnerId);

    /**
     * Create talk for purchasing, update post in conversation.
     * @param shmUser
     * @param shtTalkPurc
     */
    ShtTalkPurcDTO createTalkPurc(ShmUser shmUser, TalkPurcCreateBody shtTalkPurc) throws Exception;

    /**
     * Get all talk purc by talk Id.
     * @param postId
     * @return
     */
    Page<ShtTalkPurcDTO> findAllTalkPurcByPostId(Pageable pageable, Long postId);

    /**
     * Get all talk purc by talk Id.
     * @param postId
     * @return
     */
    List<Long> findAllTalkPurcIdByPostId(Long postId);


    /**
     * Get talkpurc by id
     */
    ShtTalkPurc getTalkPurc(long id);

    /**
     * Update talk status to Tend_To_Sell
     */
    void sendOrderRequest(ShtTalkPurc talkPurc, ShmUser curentUser, boolean mutedBlockFlag) throws Exception;

    Page<ShtTalkPurcDTO> findAllTalkPurcHasConversation(Pageable pageable, Long postId, ShmUser user);

    /**
     * count talks of post
     * @param postId
     * @return
     */
    Long countTalkByPostId(long postId);

    List<ShtTalkPurc> findTalksByPostId(ShmPost post);

    List<ShtTalkPurc> findTalksByPostIdAndStatus(ShmPost post, ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus);

    List<ShtTalkPurc> findTalksByOwnerIdAndStatus(Long userId, ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus);

    List<ShtTalkPurc> findTalksByOwnerIdOpen(Long userId);

    List<ShtTalkPurc> findTalkListByPostId(Long postId);

    List<ShtTalkPurc> findTalksByPartnerIdAndStatus(Long partnerId, ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus);

    List<ShtTalkPurc> findTalksByPartnerIdOpen(Long partnerId);

    void interruptJob(Long talkPurcId) throws SchedulerException;

    /**
     *
     * @param id
     * @return
     */
    ShtTalkPurc getTalkPurcById(long id);

    /**
     * accept order request from post owner
     * @param talkPurc
     */
    void acceptOrderRequest(ShtTalkPurc talkPurc, ShmUser currentUser) throws Exception;

    /**
     * Reject request order from post owner
     * @param talkPurc
     */
    void rejectOrderRequest(ShtTalkPurc talkPurc, ShmUser currentUser) throws Exception;

    ShmUser findPartnerInTalkPurc(Long talkPurcId);

    Long countNewMsgNumberByPostId(Long postId, Long userId);

    /**
     *
     * @param postId
     * @return
     */
    Long countTalkPurcByPostId(long postId);

    Long countTalkPurcTendToSellByPostId(long postId);

    ShtTalkPurc save(ShtTalkPurc talkPurc);

    List<ShtTalkPurc> getTalkPurcsExpiredTendToSell(LocalDateTime tendToSellTime);

    void validateTalkPurc(ShtTalkPurc talkPurc) throws Exception;

    void validateTalkPurcBeforeTendToSell(ShtTalkPurc talkPurc) throws Exception;

    void validatePostStatus(ShmPost shmPost);

    LocalDateTime getUserBlockTime(Long talkPurcId, Long userIdUseApp);

    int coutNewMsgInTalkpurc(Long ownerUserId, Long talkPurcId);

    boolean checkUserBlocked(Long senderUserId, Long partnerId);

    boolean checkUserMutedPartner(Long senderUserId, Long partnerId);

    boolean checkUserMutedOrBlocked(Long senderUserId, Long recepientId);
}
