package jp.bo.bocc.repository;

import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by HaiTH on 3/22/2017.
 */
public interface AdminNgRepositoryCustom {

    Page<ShmAdminNgDTO> getShmAdminNgWithAdminInfo(ShmAdminNgDTO adminNgDTO, Pageable pageRequest);
}
