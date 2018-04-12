package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtKpiStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
@Repository
public interface KpiStorageRepository extends JpaRepository<ShtKpiStorage, Long> {

    List<ShtKpiStorage> findByQueryTime(String queryTime);
}
