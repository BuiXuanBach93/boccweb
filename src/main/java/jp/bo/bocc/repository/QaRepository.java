package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtQa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by DonBach on 4/5/2017.
 */
public interface QaRepository extends JpaRepository<ShtQa, Long>, JpaSpecificationExecutor<ShtQa>, QaRepositoryCustom{

    @Query(value = "SELECT COUNT(*) FROM SHT_QA qa WHERE qa.USER_ID = ?1", nativeQuery = true)
    int countQaByUserId(Long userId);

    @Query(value = "SELECT qa FROM ShtQa qa WHERE qa.qaId =:qaId")
    ShtQa getQaByQaId(@Param("qaId") Long qaId);

    @Modifying
    @Query(value = "UPDATE ShtTalkQa tqa SET tqa.talkQaMsgStatus = 1 WHERE tqa.shtQa.id = :qaId ")
    void readedMsgUpdate(@Param("qaId") Long qaId);
}
