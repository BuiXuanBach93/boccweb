package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtAdminCsvHst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Created by Dell on 6/7/2017.
 */
public interface AdminCsvHstRepositoryCustom {
    Page<ShtAdminCsvHst> getAdminCsvHst(Pageable pageable);
}
