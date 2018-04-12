package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Namlong on 3/14/2017.
 */
@Repository
public interface PostRepository extends JpaRepository<ShmPost, Long>, PostRepositoryCustom, JpaSpecificationExecutor<ShmPost> {

    @Query(value = "SELECT COUNT(*) FROM SHM_POST post WHERE post.POST_USER_ID = ?1", nativeQuery = true)
    long countPostById(Long userId);

    List<ShmPost> findByShmUser(ShmUser user);

    @Query(value = "SELECT COUNT(*) FROM SHM_POST post WHERE post.POST_USER_ID = ?1 and post.POST_TYPE = ?2", nativeQuery = true)
    long countPostByPostIdAndPostType(Long userId, int postType);

    @Query(value = "SELECT COUNT(*) FROM SHM_POST post WHERE post.POST_USER_ID = ?1 and post.POST_SELL_STATUS = ?2", nativeQuery = true)
    long countPostByPostIdAndPostSellStatus(Long userId, int postStatus);

    @Query(value = "SELECT COUNT(*) FROM SHM_POST post WHERE post.POST_USER_ID = ?1 and post.POST_CTRL_STATUS = ?2 AND post.POST_SELL_STATUS <> 4 ", nativeQuery = true)
    long countPostByPostIdAndPostCtrlStatus(Long userId, int postCtrlStatus);

    @Query(value = "SELECT COUNT(*) FROM SHM_POST post WHERE post.POST_USER_ID = ?1 and post.POST_TYPE = ?2 and post.POST_PARTNER_ID is not null", nativeQuery = true)
    long countPostByPostIdAndPostTypeAndPartnerId(Long userId, int postType);

    @Query(value = "SELECT COUNT(distinct post.POST_PARTNER_ID) FROM SHM_POST post WHERE post.POST_USER_ID = ?1 and post.POST_TYPE = ?2 and post.POST_PARTNER_ID is not null", nativeQuery = true)
    long countUniquePostByPostIdAndPostTypeAndPartnerId(Long userId, int postType);

    @Query(value = "SELECT post.postLikeTimes FROM ShmPost post WHERE post.postId = :postId ")
    int getCurrentLikeTimeByPostId(@Param("postId") Long postId);

    @Modifying
    @Query(value = "UPDATE ShmPost post SET post.postLikeTimes = :likeTime WHERE post.postId = :postId ")
    void updateLikeTime(@Param("postId") Long postId,@Param("likeTime") long i);

    @Query(value = "SELECT post.postId FROM ShmPost post WHERE post.postId = :postId ")
    Long isOwnerPost(Long userId);

    @Query(value = "SELECT * FROM (SELECT * FROM SHM_POST post  WHERE post.POST_PTRL_STATUS = 0 \n" +
            "                AND post.POST_CTRL_STATUS = 0 \n" +
            "                AND (post.POST_IS_IN_PATROL IS NULL OR post.POST_IS_IN_PATROL = 0) \n" +
            "                AND post.POST_SELL_STATUS <> 4 \n" +
            "                AND post.CMN_DELETE_FLAG = 0\n" +
            "                OR (post.USER_UPDATE_AT > post.POST_TIME_PATROL AND post.POST_SELL_STATUS <> 4 \n" +
            "                        AND post.CMN_DELETE_FLAG = 0 AND post.POST_TIME_PATROL IS NOT NULL AND (post.POST_IS_IN_PATROL IS NULL OR post.POST_IS_IN_PATROL = 0))\n" +
            "                ORDER BY POST.USER_UPDATE_AT DESC ) \n" +
            "WHERE ROWNUM = 1", nativeQuery = true)
    ShmPost findPostPatrol();

    List<ShmPost> findByTimePatrolGreaterThanAndIsInPatrol(LocalDateTime limitTime, boolean isPatrol);

    @Query(value = "SELECT post.postId FROM ShmPost post WHERE postId.postSellStatus = 4 and post.postCtrlStatus = 1 and post.deleteFlag = 0")
    List<Long> findPostSuspendAndDelete();

    List<ShmPost> findByGroupId(Long groupId);
}

