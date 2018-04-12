package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtAdminCsvHst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by ThaiVd on 6/6/2017.
 */
public interface AdminCsvHstService {

    Page<ShtAdminCsvHst> getAdminCsvHst(Pageable pageable);

    ShtAdminCsvHst save(ShtAdminCsvHst adminCsvHst);

    ShtAdminCsvHst save(ShmAdmin admin, ShtAdminCsvHst.CSV_TYPE csvType);
}
