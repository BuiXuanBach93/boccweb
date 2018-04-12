package jp.bo.bocc.repository;

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
public class GroupPublishRepositoryImpl implements GroupPublishRepositoryCustom {

    @PersistenceContext
    EntityManager em;


    @Override
    public Page<ShtGroupPublish> getListGroupPubish(ShtGroupPublish groupPublish, Pageable pageRequest) {
        String sql = "SELECT stg " +
                " FROM ShtGroupPublish stg " +
                " WHERE stg.groupName LIKE :groupName " +
                " ORDER BY stg.createdAt DESC ";

        Query query = em.createQuery(sql, ShtGroupPublish.class);
        if (org.apache.commons.lang3.StringUtils.isEmpty(groupPublish.getGroupName()))
            groupPublish.setGroupName("");
        query.setParameter("groupName", "%"+groupPublish.getGroupName() + "%");
        final List list = query.getResultList();
        long totalItem = 0;
        if (CollectionUtils.isNotEmpty(list))
            totalItem = list .size();
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        List<ShtGroupPublish> resultList = query.getResultList();
        Page<ShtGroupPublish> result = new PageImpl<ShtGroupPublish>(resultList, pageRequest, totalItem);
        return result;
    }
}
