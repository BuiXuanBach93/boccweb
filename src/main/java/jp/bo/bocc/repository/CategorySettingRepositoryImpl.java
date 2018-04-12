package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtCategorySetting;
import jp.bo.bocc.entity.ShtGroupPublish;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by buixu on 11/17/2017.
 */
public class CategorySettingRepositoryImpl implements CategorySettingRepositoryCustom {

    @PersistenceContext
    EntityManager em;


    @Override
    public Page<ShtCategorySetting> getListCategorySetting(ShtCategorySetting categorySetting, Pageable pageRequest) {
        String sql = "SELECT stc " +
                " FROM ShtCategorySetting stc " +
                " ORDER BY stc.createdAt DESC ";

        Query query = em.createQuery(sql, ShtCategorySetting.class);
        final List list = query.getResultList();
        long totalItem = 0;
        if (CollectionUtils.isNotEmpty(list))
            totalItem = list .size();
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        List<ShtCategorySetting> resultList = query.getResultList();
        Page<ShtCategorySetting> result = new PageImpl<ShtCategorySetting>(resultList, pageRequest, totalItem);
        return result;
    }
}
