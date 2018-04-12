package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtTalkPurcMsg;
import jp.bo.bocc.entity.dto.ShtTalkPurcMsgDTO;
import jp.bo.bocc.helper.ConverterUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
public class TalkPurcMsgRepositoryImpl implements TalkPurcMsgRepositoryCustom {

    private final static Logger LOGGER = Logger.getLogger(TalkPurcMsgRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Transactional(readOnly = true)
    @Override
    public Page<ShtTalkPurcMsgDTO> findTalkPurcMsgInTalkPurc(Pageable pageable, Long shtTalkPurcId) {
        String sql = "SELECT stpm.talkPurcMsgId, stpm.talkPurcMsgCont,  stpm.talkPurcMsgStatus, stpm.createdAt, stpm.shmUserCreator.id, stpm.talkPurcMsgType FROM ShtTalkPurcMsg stpm WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND stpm.isActive = 1 ORDER BY stpm.createdAt DESC";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("talkPurcId", shtTalkPurcId);
        int total = query.getResultList().size();
        final int offset = pageable.getOffset();
        query.setFirstResult(offset);
        final int pageSize = pageable.getPageSize();
        query.setMaxResults(pageSize);
        List<Object[]> resultList = query.getResultList();
        List<ShtTalkPurcMsgDTO> builder = buildListTalkPurcMsg(resultList);
        Page<ShtTalkPurcMsgDTO> result = new PageImpl<ShtTalkPurcMsgDTO>(builder, pageable, total);
        return result;
    }

    private List<ShtTalkPurcMsgDTO> buildListTalkPurcMsg(List<Object[]> resultList) {
        List<ShtTalkPurcMsgDTO> result = new ArrayList<>();
        ShtTalkPurcMsgDTO item = null;
        for (Object[] obj : resultList) {
            item = new ShtTalkPurcMsgDTO();
            item.setTalkPurcId(ConverterUtils.getLongValue(obj[0]));
            item.setTalkPurcMsgCont(String.valueOf(obj[1]));
            final Integer index = ConverterUtils.getIntValue(obj[2]);
            if (index != null) {
                item.setTalkPurcMsgStatus(ShtTalkPurcMsg.TalkPurcMsgStatusEnum.values()[index].toString());
            }
            item.setCmnEntryDate(ConverterUtils.getLocalDateTime(obj[3]));
            item.setShmUserCreatorId(ConverterUtils.getLongValue(obj[4]));
            result.add(item);
        }
        return result;
    }

    @Override
    public Long countTalkWithStatusByPostId(long postId, ShtTalkPurcMsg.TalkPurcMsgStatusEnum talkPurcMsgStatus, List<Long> listTalkpurcBlocked) {
        String sql = "SELECT COUNT(stpm.talkPurcMsgId) FROM ShtTalkPurcMsg stpm INNER JOIN stpm.shtTalkPurc stp INNER JOIN stp.shmPost sp WHERE sp.postId = :postId " +
                " AND stpm.talkPurcMsgStatus = :talkPurcMsgStatus AND stpm.isActive = 1";
        if (CollectionUtils.isNotEmpty(listTalkpurcBlocked))
            sql += "AND stp.talkPurcId NOT IN :listTalkpurcBlocked";
        Query query = em.createQuery(sql);
        query.setParameter("postId", postId);
        query.setParameter("talkPurcMsgStatus", talkPurcMsgStatus);
        if (CollectionUtils.isNotEmpty(listTalkpurcBlocked))
            query.setParameter("listTalkpurcBlocked", listTalkpurcBlocked);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShtTalkPurcMsg> findTalkPurcMsgAfterTime(LocalDateTime createdAt, Long talkPurcId, int size, String msgType) {
        String sql = "SELECT stpm " +
                "FROM ShtTalkPurcMsg stpm WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId " +
                "AND stpm.createdAt > :createdAt " +
                " AND stpm.talkPurcMsgType IN ("+msgType+") AND stpm.isActive = 1 " +
                "ORDER BY stpm.createdAt DESC";
        javax.persistence.Query query = em.createQuery(sql, ShtTalkPurcMsg.class);
        query.setParameter("talkPurcId", talkPurcId);
        query.setParameter("createdAt", createdAt);
        query.setMaxResults(size);
        List<ShtTalkPurcMsg> resultList = query.getResultList();
        return resultList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShtTalkPurcMsg> findTalkPurcMsgBeforeTime(LocalDateTime createdAt, Long talkPurcId, int size, String msgType) {
        String sql = "SELECT stpm " +
                " FROM ShtTalkPurcMsg stpm " +
                " WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId " +
                " AND stpm.createdAt < :createdAt " +
                " AND stpm.talkPurcMsgType IN (" + msgType + ") AND stpm.isActive = 1 " +
                " ORDER BY stpm.createdAt DESC ";
        javax.persistence.Query query = em.createQuery(sql, ShtTalkPurcMsg.class);
        query.setParameter("talkPurcId", talkPurcId);
        query.setParameter("createdAt", createdAt);
        query.setMaxResults(size);
        List<ShtTalkPurcMsg> resultList = query.getResultList();
        return resultList;
    }

    @Transactional(readOnly = true)
    @Override
    public Integer countNewMsgInTalkSentByAnother(Long shmUserId, Long talkPurcId) {
        String sql = " SELECT COUNT(stpm.talkPurcMsgId) FROM ShtTalkPurcMsg stpm " +
                "WHERE stpm.shmUserCreator.id = :shmUserCreatorId  AND stpm.isActive = 1" +
                "AND stpm.talkPurcMsgStatus = :talkPurcMsgStatus " +
                "AND (stpm.talkPurcMsgType =:normalType  OR stpm.talkPurcMsgType =:sysForOwnerType OR  stpm.talkPurcMsgType =:sysForPartnerType ) " +
                "AND stpm.shtTalkPurc.talkPurcId = :talkPurcId";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("shmUserCreatorId", shmUserId);
        query.setParameter("talkPurcId", talkPurcId);
        query.setParameter("talkPurcMsgStatus", ShtTalkPurcMsg.TalkPurcMsgStatusEnum.SENDING);
        query.setParameter("normalType", ShtTalkPurcMsg.TalkPurcMsgTypeEnum.NORMAL);
        query.setParameter("sysForOwnerType", ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_OWNER);
        query.setParameter("sysForPartnerType", ShtTalkPurcMsg.TalkPurcMsgTypeEnum.SYS_MSG_FOR_PARTNER);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getIntValue(singleResult);
    }

    @Override
    public String getLatestMsgInTalkPurc(@Param("talkPurcId") Long talkPurcId) {
        String sql = "SELECT stpm.talkPurcMsgCont FROM ShtTalkPurcMsg stpm WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND stpm.isActive = 1 ORDER BY stpm.createdAt DESC ";
        javax.persistence.Query query = em.createQuery(sql);
        query.setMaxResults(1);
        query.setParameter("talkPurcId", talkPurcId);
        final Object singleResult = query.getSingleResult();
        return String.valueOf(singleResult);
    }

    @Override
    public ShtTalkPurcMsg getLatestMsgInTalkPurcForUser(@Param("talkPurcId") Long talkPurcId, Boolean isOwner) {
        String sql = "";
        if(isOwner){
            sql = "SELECT stpm FROM ShtTalkPurcMsg stpm WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND stpm.talkPurcMsgType IN (0,1,4,7,10) AND stpm.isActive = 1 ORDER BY stpm.createdAt DESC ";
        }else{
            sql = "SELECT stpm FROM ShtTalkPurcMsg stpm WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND stpm.talkPurcMsgType IN (0,3,5,6,8,9,10) AND stpm.isActive = 1 ORDER BY stpm.createdAt DESC ";
        }
        javax.persistence.Query query = em.createQuery(sql,ShtTalkPurcMsg.class);
        query.setMaxResults(1);
        query.setParameter("talkPurcId", talkPurcId);
        List<ShtTalkPurcMsg> resultList = query.getResultList();
        if(resultList != null){
            return resultList.get(0);
        }else{
            return null;
        }
    }

    @Override
    public int countNewMsgByPostId(Long postId) {
        String sql = "SELECT COUNT(stpm.talkPurcMsgId) FROM ShtTalkPurcMsg stpm INNER JOIN stpm.shtTalkPurc stp WHERE stp.shmPost.postId = :postId AND stpm.talkPurcMsgStatus = 0 AND stpm.isActive = 1";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("postId", postId);
        final Object singleResult = query.getSingleResult();
        if (singleResult != null)
            return Integer.valueOf(singleResult.toString());
        return 0;
    }

    @Override
    public int countNewMsgInTalkPurcForOwner(Long userId, Long postId) {
        String sql = "SELECT COUNT(stpm.TALK_PURC_MSG_ID) FROM SHT_TALK_PURC_MSG stpm " +
                "WHERE stpm.CMN_DELETE_FLAG = 0 AND stpm.TALK_PURC_MSG_CREATOR <> :userId AND stpm.TALK_PURC_MSG_STATUS = 0 AND stpm.TALK_PURC_MSG_TYPE IN (0,1,4,7,10) " +
                " AND stpm.TALK_PURC_MSG_CREATOR NOT IN (SELECT DISTINCT sutp.SHT_USER_TALK_PURC_TO FROM SHT_USER_TALK_PURC sutp WHERE sutp.USER_TALK_PURC_BLOCK =1 AND sutp.SHT_USER_TALK_PURC_FROM = :userId) " +
                " AND stpm.TALK_PURC_ID IN " +
                "(SELECT DISTINCT stp.TALK_PURC_ID FROM SHT_TALk_PURC stp WHERE stp.TALK_PURC_PART_ID <> :userId AND stp.TALK_PURC_POST_ID IN ( SELECT DISTINCT sp.POST_ID FROM SHM_POST sp WHERE sp.POST_USER_ID = :userId)) ";
        if (postId != null){
            sql = "SELECT COUNT(stpm.TALK_PURC_MSG_ID) FROM SHT_TALK_PURC_MSG stpm " +
                    "WHERE stpm.CMN_DELETE_FLAG = 0 AND stpm.TALK_PURC_MSG_CREATOR <> :userId AND stpm.TALK_PURC_MSG_STATUS = 0 AND stpm.TALK_PURC_MSG_TYPE IN (0,1,4,7,10) " +
                    " AND stpm.TALK_PURC_MSG_CREATOR NOT IN (SELECT DISTINCT sutp.SHT_USER_TALK_PURC_TO FROM SHT_USER_TALK_PURC sutp WHERE sutp.USER_TALK_PURC_BLOCK =1 AND sutp.SHT_USER_TALK_PURC_FROM = :userId) " +
                    " AND stpm.TALK_PURC_ID IN " +
                    "(SELECT DISTINCT stp.TALK_PURC_ID FROM SHT_TALk_PURC stp WHERE stp.TALK_PURC_PART_ID <> :userId AND stp.TALK_PURC_POST_ID = :postId) ";
        }
        javax.persistence.Query query = em.createNativeQuery(sql);
        query.setParameter("userId", userId);
        if (postId != null){
            query.setParameter("postId", postId);
        }
        final Object singleResult = query.getSingleResult();
        if (singleResult != null)
            return Integer.valueOf(singleResult.toString());
        return 0;
    }

    @Override
    public int countNewMsgForAllTalkFromOthers(Long userIdUsingApp, Long postId) {
        String sql = "SELECT COUNT(stpm.TALK_PURC_MSG_ID) FROM SHT_TALK_PURC_MSG stpm " +
                "WHERE stpm.CMN_DELETE_FLAG = 0 AND stpm.TALK_PURC_MSG_CREATOR <> :userId AND stpm.TALK_PURC_MSG_STATUS = 0 AND stpm.TALK_PURC_MSG_TYPE IN (0,2,3,5,6,8,9,10) " +
                " AND stpm.TALK_PURC_MSG_CREATOR NOT IN (SELECT DISTINCT sutp.SHT_USER_TALK_PURC_TO FROM SHT_USER_TALK_PURC sutp WHERE sutp.USER_TALK_PURC_BLOCK =1 AND sutp.SHT_USER_TALK_PURC_FROM = :userId) " +
                " AND stpm.TALK_PURC_ID IN " +
                "( SELECT DISTINCT stp.TALK_PURC_ID FROM SHT_TALk_PURC stp WHERE stp.TALK_PURC_PART_ID = :userId AND stp.TALK_PURC_POST_ID IN ( SELECT DISTINCT sp.POST_ID FROM SHM_POST sp WHERE sp.POST_USER_ID <> :userId)) ";
        if (postId != null){
            sql = "SELECT COUNT(stpm.TALK_PURC_MSG_ID) FROM SHT_TALK_PURC_MSG stpm " +
                    "WHERE stpm.CMN_DELETE_FLAG = 0 AND stpm.TALK_PURC_MSG_CREATOR <> :userId AND stpm.TALK_PURC_MSG_STATUS = 0 AND stpm.TALK_PURC_MSG_TYPE IN (0,2,3,5,6,8,9,10) " +
                    " AND stpm.TALK_PURC_MSG_CREATOR NOT IN (SELECT DISTINCT sutp.SHT_USER_TALK_PURC_TO FROM SHT_USER_TALK_PURC sutp WHERE sutp.USER_TALK_PURC_BLOCK =1 AND sutp.SHT_USER_TALK_PURC_FROM = :userId) " +
                    " AND stpm.TALK_PURC_ID IN " +
                    "( SELECT DISTINCT stp.TALK_PURC_ID FROM SHT_TALk_PURC stp WHERE stp.TALK_PURC_PART_ID = :userId AND stp.TALK_PURC_POST_ID = :postId) ";
        }
        javax.persistence.Query query = em.createNativeQuery(sql);
        query.setParameter("userId", userIdUsingApp);
        if (postId != null){
            query.setParameter("postId", postId);
        }
        final Object singleResult = query.getSingleResult();
        if (singleResult != null)
            return Integer.valueOf(singleResult.toString());
        return 0;
    }

    @Override
    public ShtTalkPurcMsg getLatestMsgInTalkPurcReceivedAfterBlocktime(Long talkPurcId, Boolean isOwner) {
        String sql = "";
        if(isOwner){
            sql = "SELECT stpm FROM ShtTalkPurcMsg stpm WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND stpm.talkPurcMsgType IN (0,1,4,7,10) AND stpm.isActive = 1 AND stpm.talkPurcMsgStatus = 1 ORDER BY stpm.createdAt DESC ";
        }else{
            sql = "SELECT stpm FROM ShtTalkPurcMsg stpm WHERE stpm.shtTalkPurc.talkPurcId = :talkPurcId AND stpm.talkPurcMsgType IN (0,3,5,6,8,9,10) AND stpm.isActive = 1 AND stpm.talkPurcMsgStatus = 1 ORDER BY stpm.createdAt DESC ";
        }
        javax.persistence.Query query = em.createQuery(sql, ShtTalkPurcMsg.class);
        query.setMaxResults(1);
        query.setParameter("talkPurcId", talkPurcId);
        List<ShtTalkPurcMsg> resultList = query.getResultList();
        if(CollectionUtils.isNotEmpty(resultList)){
            return resultList.get(0);
        }else{
            return null;
        }
    }

}
