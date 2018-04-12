package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtAdminLog;
import jp.bo.bocc.enums.PatrolActionEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by NguyenThuong on 4/14/2017.
 */
public class AdminLogRepositoryImpl implements AdminLogRepositoryCustom {

    private final static Logger LOGGER = Logger.getLogger(AdminCsvHstRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Override
    public ShtAdminLog getLatestAdminIdHandleUser(Long userId, List<PatrolActionEnum> listPatrolAction) {
        String sql = " SELECT adl from ShtAdminLog adl WHERE adl.shmUser.id = :userId AND adl.adminLogType IN :listPatrolAction ORDER BY adl.createdAt DESC ";
        Query query = em.createQuery(sql);
        query.setMaxResults(1);
        query.setParameter("userId", userId);
        query.setParameter("listPatrolAction", listPatrolAction);
        List<ShtAdminLog> result = query.getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            return result.get(0);
        }
        return null;
    }
}
