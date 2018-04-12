package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtBanner;
import jp.bo.bocc.entity.ShtCategorySetting;
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
public class BannerRepositoryImpl implements BannerRepositoryCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public Page<ShtBanner> getListBanner(ShtBanner banner, Pageable pageRequest) {
        String sql = "SELECT stb " +
                " FROM ShtBanner stb " +
                " ORDER BY stb.createdAt DESC ";

        Query query = em.createQuery(sql, ShtBanner.class);
        final List list = query.getResultList();
        long totalItem = 0;
        if (CollectionUtils.isNotEmpty(list))
            totalItem = list .size();
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        List<ShtBanner> resultList = query.getResultList();
        Page<ShtBanner> result = new PageImpl<ShtBanner>(resultList, pageRequest, totalItem);
        return result;
    }
}
