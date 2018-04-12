package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HaiTH on 3/15/2017.
 */
public interface AdminRepository extends JpaRepository<ShmAdmin, Long>, JpaSpecificationExecutor<ShmAdmin>, AdminRepositoryCustom {

    ShmAdmin findOne(Long adminId);

    ShmAdmin findByAdminNameAndDeleteFlag(String adminName, boolean flag);

    ShmAdmin findByAdminEmailAndDeleteFlag(String email, boolean flag);

    ShmAdmin findByAdminEmailIgnoreCase(String email);

    @Query("SELECT sa FROM ShmAdmin sa WHERE sa.deleteFlag <> :deleteFlag ORDER BY sa.createdAt DESC ")
    Page<ShmAdmin> findAllOrderByCreatedAt(@Param("deleteFlag") Boolean deleteFlag, Pageable pageable);

    @Query("SELECT sa.adminId FROM ShmAdmin sa WHERE sa.deleteFlag <> 1 AND sa.adminName LIKE  %?1% ")
    List<Long> getListAdminIdBySingleAdminName(String patrolAdminNames);
}
