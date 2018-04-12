package jp.bo.bocc.repository;

import jp.bo.bocc.helper.ConverterUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Namlong on 4/24/2017.
 */
public class TalkQaRepositoryImpl implements TalkQaRepositoryCustom {
    private final static Logger LOGGER = Logger.getLogger(TalkQaRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Value("${user.system.id}")
    private Long systemUserId;

    @Override
    public int countNewMsgFromAdminForUser(Long userIdUsingApp) {
        String sql = "SELECT COUNT(stq.TALK_QA_ID) FROM SHT_TALK_QA stq INNER JOIN SHT_QA sq ON sq.QA_ID = stq.QA_ID WHERE sq.USER_ID = :userId AND sq.USER_ID <> 999999999 AND stq.TALK_QA_MSG_STATUS = 0 AND stq.FROM_ADMIN = 1";
        Query query = em.createNativeQuery(sql);
        query.setParameter("userId", userIdUsingApp);
        final Object singleResult = query.getSingleResult();
        if (singleResult != null)
            return ConverterUtils.convertBigDecimalToInt(singleResult);
        else
            return 0;
    }
}
