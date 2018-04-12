package jp.bo.bocc.repository;

import jp.bo.bocc.helper.ConverterUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by DoonBach
 */
public class UserReadMsgRepositoryImpl implements UserReadMsgRepositoryCustom {
    private final static Logger LOGGER = Logger.getLogger(UserReadMsgRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Value("${user.system.id}")
    private Long systemUserId;

    @Override
    public int countNewMsgSystemPushAll(Long userId) {
        String sql = "SELECT COUNT(sq.QA_ID) FROM SHT_QA sq WHERE sq.QA_CONTENT_TYPE = 2 AND sq.USER_ID = " + systemUserId +
                " AND sq.CMN_ENTRY_DATE >= (SELECT usr.CMN_ENTRY_DATE FROM SHM_USER usr WHERE usr.USER_ID = :userId) " +
                " AND sq.QA_ID NOT IN ( SELECT surm.QA_ID FROM SHT_USER_READ_MSG surm WHERE sq.QA_ID = surm.QA_ID AND surm.USER_ID = :userId)";
        Query query = em.createNativeQuery(sql);
        query.setParameter("userId", userId);
        final Object singleResult = query.getSingleResult();
        if (singleResult != null)
            return ConverterUtils.convertBigDecimalToInt(singleResult);
        else
            return 0;
    }
}
