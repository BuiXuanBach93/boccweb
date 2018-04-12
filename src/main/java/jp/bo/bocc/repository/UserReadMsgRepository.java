package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtUserReadMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
@Repository
public interface UserReadMsgRepository extends JpaRepository<ShtUserReadMsg, Long>, UserReadMsgRepositoryCustom{

    @Query("SELECT COUNT(surm) FROM ShtUserReadMsg surm WHERE surm.userId = ?1 AND surm.qaId = ?2")
    Long checkUserReadQa(Long userId, Long qaId);
}
