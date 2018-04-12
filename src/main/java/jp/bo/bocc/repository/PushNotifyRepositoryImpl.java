package jp.bo.bocc.repository;


import jp.bo.bocc.entity.ShtPushNotify;
import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.ShtTalkQa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author DonBach
 */
public class PushNotifyRepositoryImpl implements PushNotifyRepositoryCustom {

    @PersistenceContext
    EntityManager em;


    @Override
    public Page<ShtPushNotify> getPushNotifys(Pageable pageable, String sortType) {
        StringBuilder sql = new StringBuilder(" SELECT pn FROM ShtPushNotify pn WHERE pn.deleteFlag = 0 ORDER BY pn.sendDate " + sortType);
        Query query = em.createQuery(sql.toString(), ShtPushNotify.class);
        int total = query.getResultList().size();
        final int pageSize = pageable.getPageSize();
        query.setMaxResults(pageSize);
        final int offset = pageable.getOffset();
        query.setFirstResult(offset);
        List<ShtPushNotify> listPush = query.getResultList();
        for (ShtPushNotify push: listPush) {
            push.setPushNumber(push.getAndroidNumber() + push.getIosNumber());
            push.setReadNumber(push.getAndroidReadNumber() + push.getIosReadNumber());
        }
        Page<ShtPushNotify> result = new PageImpl<ShtPushNotify>(listPush, pageable, total);
        return result;
    }
}
