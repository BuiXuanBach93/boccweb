package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtUserRev;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by DonBach on 4/4/2017.
 */
public interface UserRevRepository extends JpaRepository<ShtUserRev, Long>, UserRevRepositoryCustom{
    @Query("SELECT ur FROM ShtUserRev ur WHERE ur.userRevId =:id")
    ShtUserRev getUserRevById(@Param("id") long id);

    @Query("SELECT COUNT(*) FROM ShtUserRev ur WHERE ur.toShmUser.id =:userId and ur.userRevRate =:userReviewRate")
    Long countReviewByUserId(@Param("userId") Long userId, @Param("userReviewRate") ShtUserRev.UserReviewRate userReviewRate);

    @Query("SELECT ur FROM ShtUserRev ur WHERE ur.toShmUser.id =:userId and ur.userRevRate =:userReviewRate")
    List<ShtUserRev> getReviewByUserId(@Param("userId") Long userId, @Param("userReviewRate") ShtUserRev.UserReviewRate userReviewRate);

    @Query("SELECT COUNT(sur.userRevId) FROM ShtUserRev sur INNER JOIN sur.toShmUser su WHERE sur.toShmUser.id = :userId")
    Long getTotalReviewByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(*) FROM ShtUserRev sur WHERE sur.shtTalkPurc.id =:talkPurcId AND sur.fromShmUser.id =:fromUserId")
    Long getReviewByTalkPurcId(@Param("talkPurcId") Long talkPurcId, @Param("fromUserId") Long fromUserId);

    @Query(value = "SELECT COUNT(*) FROM SHT_USER_REV usrv LEFT JOIN SHM_USER us ON usrv.USER_REVIEW_FROM_USER_ID = us.USER_ID WHERE usrv.USER_REVIEW_FROM_USER_ID = ?1", nativeQuery = true)
    long countReviewByFromUser(Long userId);

    @Query(value = "SELECT COUNT(*) FROM SHT_USER_REV usrv LEFT JOIN SHM_USER us ON usrv.USER_REVIEW_FROM_USER_ID = us.USER_ID WHERE usrv.USER_REVIEW_TO_USER_ID = ?1", nativeQuery = true)
    long countReviewByToUser(Long userId);
}
