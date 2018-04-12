package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtAdminCsvHst;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Dell on 6/7/2017.
 */
public class AdminCsvHstRepositoryImpl implements AdminCsvHstRepositoryCustom {

    private final static Logger LOGGER = Logger.getLogger(AdminCsvHstRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Override
    public Page<ShtAdminCsvHst> getAdminCsvHst(Pageable pageable) {
        String sql = " SELECT sach FROM ShtAdminCsvHst sach ORDER BY sach.updatedAt DESC ";
        Query query = em.createQuery(sql);
        int totalPage = query.getResultList().size();
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        final List<ShtAdminCsvHst> resultList = query.getResultList();
        Page<ShtAdminCsvHst> result = new PageImpl<ShtAdminCsvHst>(resultList, pageable, totalPage);
        return result;
    }
}
