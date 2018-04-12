package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtUserTalkPurc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Namlong on 4/7/2017.
 */
public interface UserTalkPurcRepository extends JpaRepository<ShtUserTalkPurc, Long> {

    @Query("SELECT sub FROM ShtUserTalkPurc sub WHERE sub.shmUserFrom.id = :fromUser AND sub.shmUserTo.id = :toUser")
    List<ShtUserTalkPurc> findByUserFromAndUserTo(@Param("fromUser") Long fromUser, @Param("toUser") Long toUser);

    @Query("SELECT sub.userBlockedTIme FROM ShtUserTalkPurc sub WHERE sub.shmUserFrom.id =:userIdFrom AND sub.shmUserTo.id =:userIdTo AND sub.userTalkPurcBlock = 1 AND ROWNUM <= 1 ")
    LocalDateTime getUserBlockTime(@Param("userIdFrom") Long userIdFrom,@Param("userIdTo") Long userIdTo);

    @Query("SELECT count(*) FROM ShtUserTalkPurc sub WHERE sub.shmUserFrom.id =:userIdFrom AND sub.shmUserTo.id =:userIdTo AND sub.userTalkPurcBlock = 1")
    Long countUserBlock(@Param("userIdFrom")Long userId,@Param("userIdTo") Long userIdTo);

    @Query("SELECT count(*) FROM ShtUserTalkPurc sub WHERE sub.shmUserFrom.id =:userIdFrom AND sub.shmUserTo.id =:userIdTo AND sub.userTalkPurcMute = 1")
    Long countUserMute(@Param("userIdFrom")Long userId,@Param("userIdTo") Long userIdTo);

    @Query("SELECT count(*) FROM ShtUserTalkPurc sub WHERE sub.shmUserFrom.id =:userIdFrom AND sub.shmUserTo.id =:userIdTo AND ( sub.userTalkPurcMute = 1 OR sub.userTalkPurcBlock = 1 )")
    Long countUserMutedOrBlockd(@Param("userIdFrom")Long userId,@Param("userIdTo") Long userIdTo);
}
