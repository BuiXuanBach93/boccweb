package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.helper.ConverterUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
public class TalkPurcRepositoryImpl implements TalkPurcRepositoryCustom {

    private final static Logger LOGGER = Logger.getLogger(TalkPurcRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Override
    public Long countTalkPurcByPostId(Long postId) {
        StringBuilder sql = new StringBuilder(" SELECT COUNT(*) FROM ShtTalkPurc st WHERE st.shmPost.id = :postId ");
        Query query = em.createQuery(sql.toString());
        query.setParameter("postId", postId);
        return (Long)query.getSingleResult();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Object[]> getListTalkByPostId(Long postId) {
        StringBuilder sql = new StringBuilder(" SELECT stp.TALK_PURC_ID" +
                ", stpm.TALK_PURC_MSG_CONT, su.USER_NICK_NAME, su.USER_ID, stpm.TALK_PURC_MSG_CREATOR FROM SHT_TALK_PURC stp "
                + "INNER JOIN SHM_USER su ON su.USER_ID = stp.TALK_PURC_PART_ID "
                + "INNER JOIN SHT_TALK_PURC_MSG stpm ON stpm.TALK_PURC_ID = stp.TALK_PURC_ID "
                + "WHERE stp.TALK_PURC_POST_ID = :postId ORDER BY stpm.CMN_ENTRY_DATE DESC ");
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("postId", postId);
        final List<Object[]> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public ShtTalkPurc getTalkPurcById(long id) {
        StringBuilder sql = new StringBuilder("SELECT stp FROM ShtTalkPurc stp WHERE stp.talkPurcId =:id ");
        Query query = em.createQuery(sql.toString());
        query.setParameter("id", id);
        return (ShtTalkPurc) query.getSingleResult();
    }

    @Override
    public Long countNewMsgNumberByPostId(long postId, long userId) {
        String sql = "SELECT COUNT(*) FROM SHT_TALK_PURC_MSG tpm LEFT JOIN SHT_TALK_PURC tp ON tpm.TALK_PURC_ID = tp.TALK_PURC_ID WHERE tpm.TALK_PURC_MSG_STATUS = 0 AND tpm.TALK_PURC_MSG_CREATOR <> "+userId+" AND tp.TALK_PURC_POST_ID = " + postId;
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

}
