package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.entity.ShtPushNotify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author DonBach
 */
public interface PushNotifyRepository extends JpaRepository<ShtPushNotify, Long>, PushNotifyRepositoryCustom {
    @Query(value = "SELECT pn FROM ShtPushNotify pn WHERE pn.pushId =:pushId")
    ShtPushNotify getPushByPushId(@Param("pushId") Long pushId);
}
