package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtAdminLog;
import jp.bo.bocc.enums.PatrolActionEnum;

import java.util.List;

/**
 * Created by NguyenThuong on 4/14/2017.
 */
public interface AdminLogRepositoryCustom {
    ShtAdminLog getLatestAdminIdHandleUser(Long userId, List<PatrolActionEnum> listPatrolAction);
}
