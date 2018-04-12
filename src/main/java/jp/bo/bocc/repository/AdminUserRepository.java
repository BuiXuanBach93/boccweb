package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtAdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by HaiTH on 3/15/2017.
 */
public interface AdminUserRepository extends JpaRepository<ShtAdminUser, Long>, JpaSpecificationExecutor<ShtAdminUser> {
    @Query("SELECT sau FROM ShtAdminUser sau WHERE sau.shmAdmin.adminId = ?1")
    List<ShtAdminUser> getDataNotProcessByAdmin(Long adminId);

    @Query("SELECT sau FROM ShtAdminUser sau WHERE sau.shmAdmin.adminId = ?1 AND sau.shmUser.id = ?2")
    ShtAdminUser getUserProcessingByAdmin(Long adminId, Long userId);

}
