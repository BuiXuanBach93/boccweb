package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserNtf;
import jp.bo.bocc.enums.OSTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Namlong on 5/18/2017.
 */
public interface UserNtfRepository extends JpaRepository<ShtUserNtf, Long>{

    @Query("SELECT COUNT(sun) FROM ShtUserNtf sun WHERE sun.shmUser.id = :userId AND sun.userNtfDeviceToken = :userNtfDeviceToken ")
    Long getUserAndDeviceTokenExisted(@Param("userId") Long userId, @Param("userNtfDeviceToken") String deviceToken);

    @Modifying
    @Query(value = "UPDATE ShtUserNtf sun SET sun.deleteFlag = 1 WHERE sun.shmUser.id = :userId AND sun.userNtfDeviceToken = :userNtfDeviceToken")
    void deleteCurrentDeviceToken(@Param("userId") Long userId,@Param("userNtfDeviceToken") String userNtfDeviceToken);

    @Query("SELECT sun.userNtfDeviceToken FROM ShtUserNtf sun WHERE sun.shmUser.id = :userId AND sun.userNtfOsType = :osType ")
    List<String> getDeviceTokenByUserId(@Param("osType")OSTypeEnum osType,@Param("userId") Long userId);

    @Query("SELECT sun FROM ShtUserNtf sun WHERE sun.shmUser.id = :userId ")
    List<ShtUserNtf> getAllDeviceTokenByUserId(@Param("userId") Long userId);

    @Query("SELECT sun FROM ShtUserNtf sun WHERE sun.shmUser.id = :userId AND sun.userNtfOsType = 1 ")
    List<ShtUserNtf> getAllAndroidDeviceTokenByUserId(@Param("userId") Long userId);

    @Query("SELECT sun FROM ShtUserNtf sun WHERE sun.shmUser.id = :userId AND sun.userNtfOsType = 0 ")
    List<ShtUserNtf> getAllIOsDeviceTokenByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(sun) FROM ShtUserNtf sun WHERE sun.shmUser.status = 4 AND sun.userNtfOsType = 1 ")
    Long countAndroidDeviceActive();

    @Query("SELECT COUNT(sun) FROM ShtUserNtf sun WHERE sun.shmUser.status = 4 AND sun.userNtfOsType = 0 ")
    Long countIOSDeviceActive();

    @Query("SELECT DISTINCT(sun.shmUser) FROM ShtUserNtf sun WHERE sun.shmUser.status = 4")
    List<ShmUser> getListUserActive();

    @Query("SELECT DISTINCT(sun.shmUser) FROM ShtUserNtf sun WHERE sun.shmUser.status = 4 AND sun.userNtfOsType = 1")
    List<ShmUser> getListAndroidUserActive();

    @Query("SELECT DISTINCT(sun.shmUser) FROM ShtUserNtf sun WHERE sun.shmUser.status = 4 AND sun.userNtfOsType = 0")
    List<ShmUser> getListIOsUserActive();

    @Modifying
    @Query(value = "UPDATE ShtUserNtf sun SET sun.deleteFlag = 1, sun.userNtfSubscribed = false WHERE sun.shmUser.id = :userId AND sun.userNtfDeviceToken = :userNtfDeviceToken ")
    void deleteUserDeviceToken(@Param("userId") Long userId,@Param("userNtfDeviceToken") String userNtfDeviceToken);

    @Query(value = "SELECT DISTINCT(sun) FROM ShtUserNtf sun INNER JOIN sun.shmUser su WHERE su.deleteFlag =0 AND su.status =4 AND (sun.userNtfSubscribed <> 1 OR sun.userNtfSubscribed IS NULL) ")
    List<ShtUserNtf> getAllDeviceTokenForActiveUserNotSubscribed();

    @Modifying
    @Query(value = "UPDATE ShtUserNtf sun SET sun.userNtfSubscribed = 1 WHERE  sun.userNtfId = :userNtfId")
    void subscribeDeviceToken(@Param("userNtfId") Long userNtfId);

    @Query("SELECT sun FROM ShtUserNtf sun WHERE sun.shmUser.id = :userId ")
    List<ShtUserNtf> getAllSubscribeARNByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT sun FROM ShtUserNtf sun WHERE sun.shmUser.id = :userId AND sun.userNtfDeviceToken = :userNtfDeviceToken")
    ShtUserNtf findByUserIdAndDeviceToken(@Param("userId") Long userId,@Param("userNtfDeviceToken") String userNtfDeviceToken);
}
