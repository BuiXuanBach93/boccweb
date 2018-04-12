package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtUserFvrt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by DonBach on 3/31/2017.
 */
public interface UserFvrtRepository extends JpaRepository<ShtUserFvrt, Long>{
    @Query("SELECT suf FROM ShtUserFvrt suf WHERE suf.shmPost.postId = :postId AND suf.shmUser.id = :userId ")
    ShtUserFvrt findUserFvrtByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT COUNT(suf) FROM ShtUserFvrt suf WHERE suf.shmPost.postId = :postId AND suf.userFvrtStatus = 1 ")
    Long countLikeTimeByPostId(@Param("postId") Long postId);

    @Query("SELECT suf FROM ShtUserFvrt suf WHERE suf.shmPost.postId = :postId AND suf.userFvrtStatus = 1 ")
    List<ShtUserFvrt> getUserFvrts(@Param("postId") Long postId);
}
