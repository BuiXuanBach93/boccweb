package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmAdmin;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by HaiTH on 3/15/2017.
 */
public interface AdminService {

    ShmAdmin showAdminById(long adminId);

    void updateAdminUser (ShmAdmin shmAdmin, long id) throws Exception;

    void updateAdminPassword (ShmAdmin shmAdmin, long id) throws Exception;

    void deleteAdminUser (long id);

    void createAdminUser (ShmAdmin shmAdmin);

    void exportCSVForTorinoCC(ShmAdmin admin, Date timeRequest, HttpServletResponse response) throws IOException;

    Page<ShmAdmin> getListAdminUser(Integer pageNumber);

    Page<ShmAdmin> searchAdminUser(ShmAdmin shmAdmin,Integer pageNumber);

    boolean findAdminEmail(String email);

    ShmAdmin getAdminByEmail(String email);

    @PreAuthorize("hasAnyAuthority('SUPPER_ADMIN')")
    ShmAdmin getAdminForOnlySuperAdmin(String email);

    @PreAuthorize("hasAnyAuthority('SUPPER_ADMIN', 'ADMIN')")
    ShmAdmin getAdminForSuperAdminAndAdmin(String email);

    @PreAuthorize("hasAnyAuthority('SUPPER_ADMIN', 'ADMIN', 'CUSTOMER_SUPPORT')")
    ShmAdmin getAdminForSuperAdminAndAdminAndCusSupport(String email);

    @PreAuthorize("hasAnyAuthority('SUPPER_ADMIN', 'ADMIN', 'SITE_PATROL')")
    ShmAdmin getAdminForSuperAdminAndAdminAndSitePatrol(String email);

    @PreAuthorize("hasAuthority('SUPPER_ADMIN')")
    ShmAdmin getShmAdminByAdminNameAndDeleteFlag(String adminName);

    ShmAdmin getAdminById(long id);
}
