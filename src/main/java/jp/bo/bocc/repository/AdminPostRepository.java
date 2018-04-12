package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtAdminPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by HaiTH on 3/15/2017.
 */
public interface AdminPostRepository extends JpaRepository<ShtAdminPost, Long>, JpaSpecificationExecutor<ShtAdminPost> {

    @Query("SELECT sap FROM ShtAdminPost sap WHERE sap.shmAdmin.adminId = ?1")
    List<ShtAdminPost> getDataNotProcessByAdmin(Long adminId);

    @Query("SELECT sap FROM ShtAdminPost sap WHERE sap.shmAdmin.adminId <> ?2 AND sap.shmPost.postId = ?1 ")
    ShtAdminPost checkPostInPatrolProcess(Long postId, Long adminId);

    @Query("SELECT sap FROM ShtAdminPost sap WHERE sap.shmAdmin.adminId <> ?1 AND sap.shmPost.postId = ?2 ")
    ShtAdminPost getDataByAdminIdAndPostId(Long adminId, Long postId);
}
