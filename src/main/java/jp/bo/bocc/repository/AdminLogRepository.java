package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtAdminLog;
import jp.bo.bocc.enums.PatrolActionEnum;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by NguyenThuong on 4/14/2017.
 */
public interface AdminLogRepository extends JpaRepository<ShtAdminLog, Long>, AdminLogRepositoryCustom {

    ShtAdminLog findTop1ByShmUser_IdOrderByCreatedAtDesc(Long userId);

    List<ShtAdminLog> findByAdminLogTypeAndShmUserOrderByCreatedAtAsc(PatrolActionEnum logType, ShmUser shmUser);

    List<ShtAdminLog> findByAdminLogTypeAndShmPostOrderByCreatedAtAsc(PatrolActionEnum logType, ShmPost shmPost);

    @Query("SELECT adl FROM ShtAdminLog adl WHERE adl.shmUser.id = :userId AND adl.shmAdmin.adminId IN :adminIdList ORDER BY adl.createdAt")
    List<ShtAdminLog> getUserPatrols(@Param("userId") Long userId, @Param("adminIdList")List<Long> adminIdList);

    @Query("SELECT adl FROM ShtAdminLog adl WHERE adl.shmUser.id = :userId AND adl.shmAdmin.adminId IN :adminIdList ORDER BY adl.createdAt")
    List<ShtAdminLog> findTop1ByUserIdAndAdminIdOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("adminIdList")List<Long> adminIdList, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM SHT_ADMIN_LOG adLog WHERE adLog.ADMIN_LOG_TYPE IN (0,1,2) AND adLog.POST_ID = ?1", nativeQuery = true)
    long countTotalProcessPostByPostId(Long postId);

    @Query(value = "SELECT COUNT(*) FROM SHT_ADMIN_LOG adLog WHERE adLog.ADMIN_LOG_TYPE IN (3,4,5) AND adLog.USER_ID = ?1", nativeQuery = true)
    long countTotalProcessUserByUserId(Long userId);

    @Query("SELECT adl FROM ShtAdminLog adl INNER JOIN adl.shmAdmin sa WHERE adl.shmUser.id = :userId AND adl.adminLogTitle = :adminLogTitle AND sa.adminName LIKE %:adminName% ORDER BY adl.adminLogId DESC")
    List<ShtAdminLog> getListAdminHandleOkUser(@Param("userId") Long userId, @Param("adminLogTitle") String adminLogTitle, @Param("adminName") String adminName);

    @Query("SELECT adl FROM ShtAdminLog adl INNER JOIN adl.shmAdmin sa WHERE adl.shmUser.id = ?1 ORDER BY adl.createdAt DESC")
    List<ShtAdminLog> findLatestAdminHandleUser(Long userId);

    @Query("SELECT adl.createdAt FROM ShtAdminLog adl WHERE adl.shmUser.id = ?1 AND adl.adminLogType = 5 ORDER BY adl.createdAt DESC")
    List<java.time.LocalDateTime> getLastDateSuspendedUser(Long userId);

    @Query("SELECT adl.createdAt FROM ShtAdminLog adl WHERE adl.shmPost.postId = ?1 AND adl.adminLogType = 2 and ROWNUM = 1 ORDER BY adl.createdAt DESC")
    List<LocalDateTime> getLastDateSuspendPost(Long postId);

    @Query("SELECT adl.adminLogId FROM ShtAdminLog adl WHERE adl.shmPost.postId = ?1 AND adl.adminLogType = 1 ORDER BY adl.createdAt DESC")
    List<Long> countPostEverPending(Long postId);

    @Query("SELECT adl FROM ShtAdminLog adl WHERE adl.shmPost.postId = ?1 ORDER BY adl.createdAt DESC")
    List<ShtAdminLog> getLastAdminProcessPost(Long postId);

    List<ShtAdminLog> findByShmPost_PostIdOrderByCreatedAtDesc(Long postId);

    List<ShtAdminLog> findByShmUser_IdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT adl.adminLogId FROM ShtAdminLog adl WHERE adl.shmUser.id = ?1 AND adl.adminLogType =4 ORDER BY adl.createdAt DESC")
    List<Long> countUserEverPending(Long userId);

    @Query("SELECT adl FROM ShtAdminLog adl WHERE adl.shmUser.id = ?1 AND adl.adminLogType = 5 ORDER BY adl.createdAt DESC")
    List<ShtAdminLog> getLastTimeSuspendUser(Long userId);
}
