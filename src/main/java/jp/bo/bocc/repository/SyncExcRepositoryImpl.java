package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtSyncExc;
import jp.bo.bocc.entity.dto.ShmUserBsDTO;
import jp.bo.bocc.helper.ConverterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DonBach
 */
public class SyncExcRepositoryImpl implements SyncExcRepositoryCustom {
    private final static Logger LOGGER = Logger.getLogger(SyncExcRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Value("${db.schema.sync.name}")
    private String schemaName;

    @Value("${db.schema.sync.name.view}")
    private String viewTable;

    @Override
    public int countNewMsgSystemPushAll(Long userId) {
        String sql = "SELECT COUNT(sq.QA_ID) FROM SHT_QA sq WHERE sq.QA_CONTENT_TYPE = 2 AND sq.QA_ID NOT IN ( SELECT surm.QA_ID FROM SHT_USER_READ_MSG surm WHERE sq.QA_ID = surm.QA_ID AND surm.USER_ID = :userId)";
        Query query = em.createNativeQuery(sql);
        query.setParameter("userId", userId);
        final Object singleResult = query.getSingleResult();
        if (singleResult != null)
            return ConverterUtils.convertBigDecimalToInt(singleResult);
        else
            return 0;
    }

    @Override
    public String getLastDateSyncData() {
        String sql = "SELECT MAX(TO_CHAR(QUERY_TO_DATE,'yyyy-MM-dd')) FROM SHT_SYNC_EXC";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        if (singleResult != null)
            return singleResult.toString();
        else
            return null;
    }

    @Override
    public List<Long> getUserDataFromBSSystem(String lastSyncDate) {
        String sql = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sysDate = LocalDateTime.now().format(formatter);
        if(StringUtils.isEmpty(lastSyncDate)){
            sql = "SELECT USER_ID FROM SHM_USER SUR, "+schemaName+"."+ viewTable +" WHERE SUR.USER_STATUS = 4 AND SUR.USER_BSID = M_MEMBR_NO AND (M_MEMBR_WTDRW_DATE IS NOT NULL OR C_WTDRW_DATE IS NOT NULL) AND (M_MEMBR_WTDRW_DATE <= TO_DATE('"+sysDate+"','yyyy-mm-dd') OR C_WTDRW_DATE <= TO_DATE('"+sysDate+"','yyyy-mm-dd'))";
        }else{
            sql = "SELECT USER_ID FROM SHM_USER SUR, "+schemaName+"." + viewTable +" WHERE SUR.USER_STATUS = 4 AND SUR.USER_BSID = M_MEMBR_NO AND (M_MEMBR_WTDRW_DATE IS NOT NULL OR C_WTDRW_DATE IS NOT NULL) " +
                    " AND ((TO_DATE('"+lastSyncDate+"','yyyy-mm-dd') < M_MEMBR_WTDRW_DATE AND M_MEMBR_WTDRW_DATE <= TO_DATE('"+sysDate+"','yyyy-mm-dd')) " +
                    " OR  (TO_DATE('"+lastSyncDate+"','yyyy-mm-dd') < C_WTDRW_DATE AND C_WTDRW_DATE <= TO_DATE('"+sysDate+"','yyyy-mm-dd')))";
        }
        Query query = em.createNativeQuery(sql);
        List<BigDecimal> listData = query.getResultList();
        List<Long> userIds = new ArrayList<>();
        if (listData != null && !listData.isEmpty()) {
            for (int i = 0; i< listData.size(); i++) {
                userIds.add(listData.get(i).longValue());
            }
        }
        return userIds;
    }

    @Override
    public List<Long> getUserDataFromBSSystemFirstTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fistDayOfMonth = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String firstDayStr = fistDayOfMonth.format(formatter);
        String sql = "SELECT USER_ID FROM SHM_USER SUR, "+schemaName+"."+ viewTable +" WHERE SUR.USER_STATUS = 4 AND SUR.USER_BSID = M_MEMBR_NO AND (M_MEMBR_WTDRW_DATE IS NOT NULL OR C_WTDRW_DATE IS NOT NULL) AND (M_MEMBR_WTDRW_DATE <= TO_DATE('"+firstDayStr+"','yyyy-mm-dd') OR C_WTDRW_DATE <= TO_DATE('"+firstDayStr+"','yyyy-mm-dd'))";
        Query query = em.createNativeQuery(sql);
        List<BigDecimal> listData = query.getResultList();
        List<Long> userIds = new ArrayList<>();
        if (listData != null && !listData.isEmpty()) {
            for (int i = 0; i< listData.size(); i++) {
                userIds.add(listData.get(i).longValue());
            }
        }
        return userIds;
    }

    @Override
    public List<Long> getUserDataFromBSSystem(ShtSyncExc lastSyncExc) {
        String sql = "";
        if(lastSyncExc == null || lastSyncExc.getQueryFromDate() == null || lastSyncExc.getQueryToDate() == null){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fromDate = lastSyncExc.getQueryFromDate().format(formatter);
        String toDate = lastSyncExc.getQueryToDate().format(formatter);
        sql = "SELECT USER_ID FROM SHM_USER SUR, "+schemaName+"."+ viewTable +" WHERE SUR.USER_STATUS = 4 AND SUR.USER_BSID = M_MEMBR_NO AND (M_MEMBR_WTDRW_DATE IS NOT NULL OR C_WTDRW_DATE IS NOT NULL) " +
                    " AND ((TO_DATE('"+fromDate+"','yyyy-mm-dd') < M_MEMBR_WTDRW_DATE AND M_MEMBR_WTDRW_DATE <= TO_DATE('"+toDate+"','yyyy-mm-dd')) " +
                    " OR  (TO_DATE('"+fromDate+"','yyyy-mm-dd') < C_WTDRW_DATE AND C_WTDRW_DATE <= TO_DATE('"+toDate+"','yyyy-mm-dd')))";

        Query query = em.createNativeQuery(sql);
        List<BigDecimal> listData = query.getResultList();
        List<Long> userIds = new ArrayList<>();
        if (listData != null && !listData.isEmpty()) {
            for (int i = 0; i< listData.size(); i++) {
                userIds.add(listData.get(i).longValue());
            }
        }
        return userIds;
    }

    @Override
    public Long getLastSyncExcId() {
        String sql = "SELECT MAX(SYNC_EXC_ID) FROM SHT_SYNC_EXC";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        if (singleResult != null)
            return new Long(singleResult.toString());
        else
            return null;
    }
}
