package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtUserRev;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by DonBach on 4/4/2017.
 */
@Transactional
public class UserRevRepositoryImpl implements UserRevRepositoryCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public Page<ShtUserRev> getReviewToUser(Long userId, Pageable pageable, ShtUserRev.UserReviewRate userReviewRate) {
        StringBuilder sql = null;
        if(userReviewRate != null){
           sql = new StringBuilder("SELECT ur FROM ShtUserRev ur WHERE ur.toShmUser.id =:userId and ur.userRevRate =:userReviewRate ORDER BY ur.createdAt DESC ");
        }else {
           sql = new StringBuilder("SELECT ur FROM ShtUserRev ur WHERE ur.toShmUser.id =:userId ORDER BY ur.createdAt DESC ");
        }
        Query query = em.createQuery(sql.toString());
        query.setParameter("userId", userId);
        if(userReviewRate != null){
            query.setParameter("userReviewRate", userReviewRate);
        }
        int totalItems = query.getResultList().size();
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        final List<ShtUserRev> resultList = query.getResultList();
        Page<ShtUserRev> result = new PageImpl<ShtUserRev>(resultList, pageable, totalItems);
        return result;
    }
}
