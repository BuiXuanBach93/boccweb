package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmAdmin;

import java.util.List;

/**
 * @author NguyenThuong on 5/29/2017.
 */
public interface AdminRepositoryCustom {

    List<ShmAdmin> searchAdminByAdminName(List<String> adminNames);

    String getListAdminIdByListAdminName(String operatorNames);

    List<Long> getListAdminIdByListAdminNameNew(String patrolAdminNames);
}
