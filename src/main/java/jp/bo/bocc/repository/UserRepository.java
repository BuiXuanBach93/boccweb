package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author manhnt
 */
public interface UserRepository extends JpaRepository<ShmUser, Long>, JpaSpecificationExecutor<ShmUser>, UserRepositoryCustom {

	List<ShmUser> findByEmailAndPassword(String email, String password);

	List<ShmUser> findByBsid(String bsid);

	List<ShmUser> findByEmail(String email);

	List<ShmUser> findByPhone(String phoneNumber);

	List<ShmUser> findByFirstName(String fisrtName);

	List<ShmUser> findByNickName(String nickName);

	List<ShmUser> findByEmailAndPhone(String email, String phoneNumber);

	List<ShmUser> findByStatus(ShmUser.Status status);

	List<ShmUser> findByStatusAndBsid(ShmUser.Status status, String bsid);

	@Query("SELECT us FROM ShmUser us WHERE us.firstName LIKE %?1% OR us.lastName LIKE %?1%")
	List<ShmUser> findLikeByFirstNameOrLastName(String userName);

//	@Query("SELECT us FROM ShmUser us WHERE us.status = 5 AND us.leftDate < SYSDATE AND us.syncExcId IS NOT NULL")
//	List<ShmUser> getMissingLeftSyncUsersBeforeNow();

	@Query("SELECT us.id FROM ShmUser us WHERE us.id <> :userId ORDER BY us.userUpdateAt DESC ")
	Page<Long> getNextUserId(@Param("userId") Long userId, Pageable pageable);

	@Query(value = "SELECT * FROM (SELECT * \n" +
			"FROM SHM_USER us \n" +
			"        WHERE us.USER_PTRL_STATUS = 0 AND us.USER_CTRL_STATUS = 0 AND us.USER_STATUS IN (4,5) AND (us.USER_IS_IN_PATROL IS NULL  OR us.USER_IS_IN_PATROL = 0) AND us.CMN_DELETE_FLAG = 0\n" +
			"                OR (us.USER_STATUS IN (4,5) AND (us.USER_IS_IN_PATROL IS NULL  OR us.USER_IS_IN_PATROL = 0) AND us.USER_UPDATE_AT > us.USER_TIME_PATROL AND us.USER_TIME_PATROL IS NOT NULL AND us.CMN_DELETE_FLAG = 0)\n" +
			"        ORDER BY us.USER_UPDATE_AT DESC)\n" +
			"WHERE ROWNUM = 1", nativeQuery = true)
	ShmUser findUserPatrol();

	List<ShmUser> findByTimePatrolGreaterThanAndIsInPatrol(LocalDateTime timePatrol, boolean isPatrol);

	@Query("SELECT us.id FROM ShmUser us WHERE us.status = 6 and us.ctrlStatus = 2 and us.deleteFlag = 0")
	List<Long> getUserSuspendAndLeft();

	ShmUser findByIdAndStatus(Long postUserId, ShmUser.Status activated);

	@Query("SELECT us FROM ShmUser us WHERE us.status = 4 and us.bsid LIKE ?1% and us.deleteFlag = 0")
	List<ShmUser> getActiveUserByLegalId(String legalId);

	@Query("SELECT us FROM ShmUser us WHERE us.status = 5 and us.syncExcId = :syncExcId and us.deleteFlag = 0")
	List<ShmUser> getTendToLeaveUserBySyncExcId(@Param("syncExcId")Long syncExcId);
}
