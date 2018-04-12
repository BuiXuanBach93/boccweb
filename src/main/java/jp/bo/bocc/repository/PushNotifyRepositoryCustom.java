package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtPushNotify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author DonBach
 */
public interface PushNotifyRepositoryCustom  {
    Page<ShtPushNotify> getPushNotifys(Pageable pageRequest, String sortType);
}
