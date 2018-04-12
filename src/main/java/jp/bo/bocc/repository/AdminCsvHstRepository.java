package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtAdminCsvHst;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author NguyenThuong on 6/5/2017.
 */
public interface AdminCsvHstRepository extends JpaRepository<ShtAdminCsvHst, Long>, AdminCsvHstRepositoryCustom {
}
