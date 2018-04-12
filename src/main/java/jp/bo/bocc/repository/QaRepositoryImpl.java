package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.ShtTalkQa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DonBach on 4/12/2017.
 */

@Transactional
public class QaRepositoryImpl implements QaRepositoryCustom{

    @PersistenceContext
    EntityManager em;

    @Autowired
    QaRepository qaRepository;

    @Autowired
    UserReadMsgRepository userReadMsgRepository;

    @Value("${user.system.id}")
    private Long systemUserId;

    @Override
    public boolean haveFeedback(ShtQa shtQa) {
        if(shtQa.getQaStatus() != ShtQa.QaStatusEnum.INPROGRESS){
            return false;
        }
        long qaId = shtQa.getQaId();
        StringBuilder stringBuilder = new StringBuilder(" SELECT FROM_ADMIN FROM (SELECT * FROM SHT_TALK_QA tq WHERE tq.QA_ID = "+qaId+" ORDER BY tq.CMN_ENTRY_DATE DESC) WHERE ROWNUM <=1 ");
        Query query = em.createNativeQuery(stringBuilder.toString());

        List<Object> result = query.getResultList();
        if(result != null && !result.isEmpty() && Integer.parseInt(result.get(0).toString()) == 0){
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShtQa> getListQaByUserId(Long userId, Pageable pageable) {
        StringBuilder sql = new StringBuilder(" SELECT sq.qa_id, stq.lastCreated, sq.user_Id FROM SHT_QA sq LEFT JOIN " +
                " (SELECT qa_id, MAX(CMN_ENTRY_DATE) AS lastCreated  " +
                " FROM SHT_TALK_QA GROUP BY qa_id) stq " +
                " ON sq.qa_id = stq.qa_id WHERE sq.user_Id = " + userId + " OR (sq.QA_CONTENT_TYPE = 2 AND sq.user_id = "+systemUserId.intValue()+
                " AND (SELECT usr.CMN_ENTRY_DATE FROM SHM_USER usr WHERE usr.USER_ID = "+userId+") <= sq.CMN_ENTRY_DATE) ORDER BY stq.lastCreated DESC ");
        Query query = em.createNativeQuery(sql.toString());
        int total = query.getResultList().size();
        final int pageSize = pageable.getPageSize();
        query.setMaxResults(pageSize);
        final int offset = pageable.getOffset();
        query.setFirstResult(offset);
        List<Object[]> listData = query.getResultList();
        List<ShtQa> shtQas = new ArrayList<>();
        if (listData != null && !listData.isEmpty()) {
            for (Object[] obj : listData) {
                if (obj != null) {
                    Long qaId = new Long(obj[0].toString());
                    ShtQa qaResult = qaRepository.getQaByQaId(qaId);
                    if (qaResult != null) {
                        if(qaResult.getShmUser() != null && qaResult.getShmUser().getId().intValue() != systemUserId.intValue()){
                            qaResult.setCountNewMsg(countNewMessage(qaId, false));
                        }else{
                            Long checkRead = userReadMsgRepository.checkUserReadQa(userId, qaId);
                            if(checkRead == 0){
                                qaResult.setCountNewMsg(1);
                            }else{
                                qaResult.setCountNewMsg(0);
                            }
                        }
                        String firstMsg = getFirstMsg(qaId);
                        String lastMsg = getLastMsg(qaId);
                        qaResult.setFirstQaMsg(firstMsg);
                        qaResult.setLastQaMsg(lastMsg);
                        shtQas.add(qaResult);
                    }
                }
            }
        }
        Page<ShtQa> result = new PageImpl<ShtQa>(shtQas, pageable, total);
        return result;
    }

    @Transactional(readOnly = true)
    public List<ShtTalkQa> getListTalkQaByCondition(LocalDateTime createdAt, Boolean isBefore, Long qaId, int size) {
        String sql = "";
        if(isBefore){
            sql = "SELECT stq " +
                    " FROM ShtTalkQa stq " +
                    " WHERE stq.shtQa.id = :qaId " +
                    " AND stq.createdAt < :createdAt " +
                    " ORDER BY stq.createdAt DESC ";
        }else{
            sql = "SELECT stq " +
                    " FROM ShtTalkQa stq " +
                    " WHERE stq.shtQa.id = :qaId " +
                    " AND stq.createdAt > :createdAt " +
                    " ORDER BY stq.createdAt DESC ";
        }
        Query query = em.createQuery(sql, ShtTalkQa.class);
        query.setParameter("qaId", qaId);
        query.setParameter("createdAt", createdAt);
        query.setMaxResults(size);
        List<ShtTalkQa> resultList = query.getResultList();
        return resultList;
    }

    public int countNewMessage(Long qaId, boolean isAdmin) {
        StringBuilder sql = new StringBuilder(" SELECT COUNT(*) FROM  SHT_TALK_QA WHERE QA_ID = "+qaId+" AND TALK_QA_MSG_STATUS = 0 ");
        if(isAdmin){
            sql.append(" AND FROM_ADMIN = 0 ");
        }else{
            sql.append(" AND FROM_ADMIN = 1 ");
        }
        Query query = em.createNativeQuery(sql.toString());
        Object result = query.getSingleResult();
        try{
            return Integer.parseInt(result.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String getFirstMsg(Long qaId){
        String msg = null;
        StringBuilder stringBuilder = new StringBuilder(" SELECT TALK_QA_MSG FROM (SELECT * FROM SHT_TALK_QA tq WHERE tq.QA_ID = "+qaId+" ORDER BY tq.CMN_ENTRY_DATE ASC) WHERE ROWNUM <=1 ");
        Query query = em.createNativeQuery(stringBuilder.toString());
        List<Object> result = query.getResultList();
        if(result != null && result.size() > 0 && result.get(0) != null){
            msg = result.get(0).toString();
        }
        return msg;
    }

    @Override
    public Long getFirstMsgId(Long qaId){
        Long msgId = null;
        StringBuilder stringBuilder = new StringBuilder(" SELECT TALK_QA_ID FROM (SELECT * FROM SHT_TALK_QA tq WHERE tq.QA_ID = "+qaId+" ORDER BY tq.CMN_ENTRY_DATE ASC) WHERE ROWNUM <=1 ");
        Query query = em.createNativeQuery(stringBuilder.toString());
        List<Object> result = query.getResultList();
        if(result != null && result.size() > 0 && result.get(0) != null){
            msgId = new Long(result.get(0).toString());
        }
        return msgId;
    }

    @Override
    public ShtQa getQaByAdminIdAndUserIdAndPushType(Long adminId, Long userId, ShtQa.QaContentTypeEnum qaType) {
        ShtQa result = null;
        String sql = " SELECT sq FROM ShtQa sq INNER JOIN ShtTalkQa stq ON stq.shtQa.qaId = sq.qaId " +
                " WHERE stq.shmAdmin.adminId = :adminId AND sq.shmUser.id = :userId AND sq.qaContentType = :qaType ORDER BY stq.createdAt DESC ";
//        String sql = " SELECT * FROM SHT_QA sq INNER JOIN SHT_TALK_QA stq ON sq.QA_ID = stq.QA_ID " +
//                " WHERE stq.ADMIN_ID = :adminId AND sq.USER_ID = :userId AND sq.QA_CONTENT_TYPE = :qaType ORDER BY stq.CMN_ENTRY_DATE DESC ";
        Query query = em.createQuery(sql);
        query.setFirstResult(0);
        query.setMaxResults(1);
        query.setParameter("adminId", adminId);
        query.setParameter("userId", userId);
        query.setParameter("qaType", qaType);

        List<Object> resultList = query.getResultList();
        if(resultList != null && resultList.size() > 0 && resultList.get(0) != null){
            result = (ShtQa) resultList.get(0);
        }
        return result;
    }

    public String getLastMsg(Long qaId){
        String msg = null;
        StringBuilder stringBuilder = new StringBuilder(" SELECT TALK_QA_MSG FROM (SELECT * FROM SHT_TALK_QA tq WHERE tq.QA_ID = "+qaId+" ORDER BY tq.CMN_ENTRY_DATE DESC) WHERE ROWNUM <=1 ");
        Query query = em.createNativeQuery(stringBuilder.toString());
        List<Object> result = query.getResultList();
        if(result != null && result.size() > 0){
            msg = result.get(0).toString();
        }
        return msg;
    }

    @Override
    public Page<ShtQa> getListQaOrder(Pageable page, String whereClause) {
        StringBuilder sql = new StringBuilder(" SELECT sq.QA_ID, stq.LAST_CREATED from SHT_QA sq LEFT JOIN " +
                " (SELECT QA_ID, MAX(CMN_ENTRY_DATE) AS LAST_CREATED " +
                " FROM SHT_TALK_QA GROUP BY QA_ID) stq " +
                " ON sq.QA_ID = stq.QA_ID WHERE sq.QA_CONTENT_TYPE = 1 AND "+whereClause+" ORDER BY stq.LAST_CREATED DESC ");
        Query query = em.createNativeQuery(sql.toString());
        int total = query.getResultList().size();
        final int pageSize = page.getPageSize();
        query.setMaxResults(pageSize);
        final int offset = page.getOffset();
        query.setFirstResult(offset);
        List<Object[]> listData = query.getResultList();
        List<ShtQa> shtQas = new ArrayList<>();
        if(listData != null && !listData.isEmpty()){
            for (Object[] obj: listData) {
                if(obj != null){
                    Long qaId = new Long(obj[0].toString());
                    ShtQa qaResult = qaRepository.getOne(qaId);
                    if(obj.length > 1 && obj[1] != null){
                        String dateTimeStr = obj[1].toString();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                        LocalDateTime lastUpdate = LocalDateTime.parse(dateTimeStr, formatter);
                        qaResult.setLastUpdateAt(lastUpdate);
                    }
                    shtQas.add(qaResult);
                }
            }
        }
        Page<ShtQa> result = new PageImpl<ShtQa>(shtQas, page, total);
        return result;
    }

}
