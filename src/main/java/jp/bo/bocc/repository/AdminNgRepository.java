package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmAdminNg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by HaiTH on 3/22/2017.
 */
public interface AdminNgRepository extends JpaRepository<ShmAdminNg, Long>, JpaSpecificationExecutor<ShmAdminNg>, AdminNgRepositoryCustom {
}
