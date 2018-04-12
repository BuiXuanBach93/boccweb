package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtSyncExc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by buixu on 11/14/2017.
 */
@Repository
public interface SyncExcRepository extends JpaRepository<ShtSyncExc, Long>, SyncExcRepositoryCustom{

    @Query("SELECT COUNT(surm) FROM ShtUserReadMsg surm WHERE surm.userId = ?1 AND surm.qaId = ?2")
    Long checkUserReadQa(Long userId, Long qaId);

}
