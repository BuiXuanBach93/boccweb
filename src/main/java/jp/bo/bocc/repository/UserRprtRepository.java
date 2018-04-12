package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserRprt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by DonBach on 3/31/2017.
 */
public interface UserRprtRepository extends JpaRepository<ShtUserRprt, Long> {
    @Query("SELECT sur FROM ShtUserRprt sur WHERE sur.shmPost.postId = :postId AND sur.shmUser.id = :userId AND sur.deleteFlag = 0 ")
    ShtUserRprt findUserRprtByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM SHT_USER_RPRT rprt WHERE rprt.USER_ID = ?1", nativeQuery = true)
    long countReportByUserId(long userId);

    @Query(value = "SELECT COUNT(*) FROM SHT_USER_RPRT rprt WHERE rprt.POST_ID IN (SELECT post.POST_ID FROM SHM_POST post WHERE post.POST_USER_ID = ?1)", nativeQuery = true)
    long countReportMe(long userId);

    @Query(value = "SELECT COUNT(userRprtId) FROM ShtUserRprt sur WHERE sur.shmPost.postId = ?1)")
    long countReportTime(long postId);

    @Query(value = "SELECT su FROM ShtUserRprt sur INNER JOIN sur.shmUser su WHERE sur.shmPost.postId = ?1 ORDER BY sur.createdAt DESC )")
    List<ShmUser> getLastUserReportPost(Long postId);

    @Query("SELECT count(*) FROM ShtUserRprt sur WHERE sur.shmPost.postId = :postId AND sur.deleteFlag = 0 ")
    Long countTotalReportByPostId(@Param("postId") Long postId);

    @Query("SELECT sur FROM ShtUserRprt sur WHERE sur.shmPost.postId = :postId AND sur.deleteFlag = 0 ORDER BY sur.createdAt DESC ")
    List<ShtUserRprt> getPostReport(@Param("postId") Long postId);
}
