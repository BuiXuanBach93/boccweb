package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShtAdminLog;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import jp.bo.bocc.enums.PatrolActionEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.helper.FileUtils;
import jp.bo.bocc.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haipv
 */
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final static Logger LOGGER = Logger.getLogger(UserRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    AdminLogRepository adminLogRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    UserService userService;

    final String PROCESS_STATUS = "processStatus";
    final String UPDATED_AT_FROM = "updatedAtFrom";
    final String UPDATED_AT_TO = "updatedDateTo";
    final String CENSORED_FROM = "censoredFrom";
    final String CENSORED_TO = "censoredTo";
    final String PATROL_ADMIN_NAME = "patrolAdminName";
    final String USER_CTRL_STATUS = "userCtrlStatus";
    final String PENDING_STATUS_TEXT = "保留中";
    final String UPDATE_AFTER_CENSORE = "修正完了";

    @Value("${default.time.startdate}")
    private String defaultStartDate;

    @Value("${db.schema.sync.name.view}")
    private String viewTable;

    @Value("${db.schema.sync.name}")
    private String schemaName;

    @Override
    public Map<String, List<Long>> findUserListRegistInMonth(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT us.USER_ID, us.USER_BSID FROM SHM_USER us WHERE us.CMN_ENTRY_DATE BETWEEN TO_DATE('");
        sql.append(startDate + "', 'YYYY/MM/DD') AND");
        sql.append(" TO_DATE('" + endDate + "', 'YYYY/MM/DD')");
        sql.append(" AND us.USER_STATUS in (4,5,6) ORDER BY us.USER_BSID");

        List<Object[]> objectList = em.createNativeQuery(sql.toString()).getResultList();
        List<Long> idList = new ArrayList<>();
        Map<String, List<Long>> result = new HashMap<>();
        for (Object[] obj : objectList) {
            Long userId = Long.parseLong(String.valueOf(obj[0]));
            String bsid = String.valueOf(obj[1]);

            if (!result.containsKey(bsid)) {
                idList = new ArrayList<>();
                idList.add(userId);
                result.put(bsid, idList);
            } else {
                idList.add(userId);
            }
        }
        return result;
    }

    @Override
    public Long getUserNumberRegisByDay(String day) {
        String sql = "SELECT COUNT(*) FROM SHM_USER WHERE USER_STATUS IN (4,5,6) AND TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM/dd') LIKE '" + day + "'";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getUserNumberRegisByMonth(String month) {
        String sql = "SELECT COUNT(*) FROM SHM_USER WHERE USER_STATUS IN (4,5,6) AND TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM') LIKE '" + month + "'";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public String getMinRegDate() {
        String sql = "select MIN(TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM/dd')) FROM SHM_USER";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return singleResult.toString();
    }

    @Override
    public List<String> getBsFromOtherSchema() {
        String sql = "SELECT M_MEMBR_NO FROM "+schemaName+"."+ viewTable;
        Query query = em.createNativeQuery(sql);
        List resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<ShmUser> getMissingLeftSyncUsersBeforeNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sysDate = LocalDateTime.now().format(formatter);
        StringBuilder sql = new StringBuilder("SELECT su FROM ShmUser su WHERE su.status = 5 AND su.syncExcId IS NOT NULL AND ");
        sql.append(" su.leftDate < TO_DATE('"+sysDate+"','yyyy-mm-dd')");
        javax.persistence.Query query = em.createQuery(sql.toString());

        final List resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<ShmUser> getUserListByFirstNameAndLastName(List<String> userNames) {
        StringBuilder sql = new StringBuilder("SELECT su FROM ShmUser su where");
        for (String userName : userNames) {
            if(StringUtils.isNotEmpty(userName)){
                userName = userName.replace("%","").replace(";","").replace("'","");
            }
            if (userNames.indexOf(userName) == 0)
                sql.append(" concat(su.firstName, su.lastName) LIKE '%" + userName + "%'");
            else {
                sql.append(" OR concat(su.firstName, su.lastName) LIKE '%" + userName + "%'");
            }
        }
        javax.persistence.Query query = em.createQuery(sql.toString());

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ShmUserDTO> getUserListForPatrolSite(String processStatus, Date updatedAtFrom, Date updatedAtTo, String patrolAdminNames, Date censoredFrom, Date censoredTo, Pageable pageable, String imageServer, boolean filterPendingStatus, boolean filterUpdateAfterCensoring) {
        StringBuilder sql = new StringBuilder("SELECT su.USER_ID, su.USER_AVTR, su.USER_NICK_NAME, su.USER_PTRL_STATUS, " +
                "su.CMN_LAST_UPDT_DATE, su.USER_CTRL_STATUS, su.USER_TIME_PATROL, su.USER_UPDATE_AT " +
                "FROM SHM_USER su " +
                "WHERE su.USER_STATUS IN (4) AND su.CMN_DELETE_FLAG = 0 AND (su.USER_IS_IN_PATROL is null or su.USER_IS_IN_PATROL = 0) ");
        Map<String, Object> params = new HashedMap();

        buildSql(params, processStatus, updatedAtFrom, updatedAtTo, patrolAdminNames, censoredFrom, censoredTo, sql);

        javax.persistence.Query query = em.createNativeQuery(sql.toString());
        setParams(query, params, processStatus, updatedAtFrom, updatedAtTo, patrolAdminNames, censoredFrom, censoredTo, sql);
        Integer totalItems = 0;
        Page<ShmUserDTO> page = null;
        final List<Object[]> resultListAll = query.getResultList();
        final int pageSize = pageable.getPageSize();
        final int offset = pageable.getOffset();
        List<ShmUserDTO> resultAfterFilter = null;
            if (resultListAll != null) {
                totalItems = resultListAll.size();
            }
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);
            List<Object[]> resultList = query.getResultList();
            List<ShmUserDTO> result = buildResult(resultList, imageServer, filterPendingStatus, filterUpdateAfterCensoring);
            resultAfterFilter = buildResultAfterFilter(result, filterPendingStatus, filterUpdateAfterCensoring, patrolAdminNames);
            page = new PageImpl<ShmUserDTO>(resultAfterFilter, pageable, totalItems);

        return page;
    }

    private List<ShmUserDTO> buildResultAfterFilter(List<ShmUserDTO> listFromDb, boolean filterPendingStatus, boolean filterUpdateAfterCensoring, String patrolAdminNames) {
        List<ShmUserDTO> result = listFromDb;
        if (StringUtils.isNotEmpty(patrolAdminNames)) {
            result = listFromDb.stream().filter(item -> {
                String patrolAdminName = item.getPatrolAdminName();
                if (StringUtils.isNotEmpty(patrolAdminName) && patrolAdminName.contains(patrolAdminNames.trim())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        if (filterPendingStatus) {
            result = result.stream().filter(item ->
                            PENDING_STATUS_TEXT.equals(item.getPendingStatusPatrolSite())
            ).collect(Collectors.toList());
        } else if (filterUpdateAfterCensoring) {
            result = result.stream().filter(item ->
                            UPDATE_AFTER_CENSORE.equals(item.getUserUpdateAfterSuspended())
            ).collect(Collectors.toList());
        }
        if (filterPendingStatus && filterUpdateAfterCensoring) {
            result = result.stream().filter(item -> {
                        if (UPDATE_AFTER_CENSORE.equals(item.getUserUpdateAfterSuspended()) && PENDING_STATUS_TEXT.equals(item.getPendingStatusPatrolSite())) {
                            return true;
                        }
                        return false;

                    }
            ).collect(Collectors.toList());
        }

        return result;

    }

    private List<ShmUserDTO> buildResult(List<Object[]> resultList, String imageServer, boolean filterPendingStatus, boolean filterUpdateAfterCensoring) {
        try {
            List<ShmUserDTO> result = new ArrayList<>();
            for (Object[] o : resultList) {
                ShmUserDTO shmUserDTO = new ShmUserDTO();
                final Long userId = ConverterUtils.getLongValue(o[0]);
                shmUserDTO.setUserId(userId);
                String okStt = String.valueOf(PatrolActionEnum.PATROL_USER_OK.ordinal());
                List<PatrolActionEnum> patrolActionEnumList = new ArrayList<>();
                patrolActionEnumList.add(PatrolActionEnum.PATROL_USER_OK);
                patrolActionEnumList.add(PatrolActionEnum.PATROL_USER_RESERVED);
                patrolActionEnumList.add(PatrolActionEnum.PATROL_USER_SUSPENDED);
                ShtAdminLog shtAdminLog = adminLogRepository.getLatestAdminIdHandleUser(userId, patrolActionEnumList);
                if (shtAdminLog != null) {
                    ShmAdmin shmAdmin = shtAdminLog.getShmAdmin();
                    if (shmAdmin != null) {
                        shmUserDTO.setPatrolAdminId(shmAdmin.getAdminId());
                        shmUserDTO.setPatrolAdminName(shmAdmin.getAdminName());
                    }
                }
                final Long fileId = ConverterUtils.getLongValue(o[1]);
                if (fileId != null) {
                    final ShrFile file = fileRepository.findOne(fileId);
                    shmUserDTO.setUserAvatarPath(imageServer + FileUtils.buildImagePathByFileForUserAvatar(file));
                }
                shmUserDTO.setUserNickName(ConverterUtils.convertStringSafe(o[2]));
                shmUserDTO.setUserPtrlStatus(ConverterUtils.getLongValue(o[3]));
                shmUserDTO.setCmnLastUpdtDate(ConverterUtils.getLocalDateTime(o[4]));
//                shmUserDTO.setUserPtrlCtrlDate(ConverterUtils.getLocalDateTime(o[5]));
                Long userCtrlStatus = ConverterUtils.getLongValue(o[5]);
                shmUserDTO.setUserCtrlStatus(userCtrlStatus);
                shmUserDTO.setTimePatrol(ConverterUtils.getLocalDateTime(o[6]));
                shmUserDTO.setUserUpdateAt(ConverterUtils.getLocalDateTime(o[7]));
                String updateAfterSuspendTxt = "";
                String pendingStatusPatrolSite = "";
                List<Long> shtAdminLogs = adminLogRepository.countUserEverPending(userId);
                if (CollectionUtils.isNotEmpty(shtAdminLogs)) {
                    pendingStatusPatrolSite = PENDING_STATUS_TEXT;
                }
                shmUserDTO.setPendingStatusPatrolSite(pendingStatusPatrolSite);
                List<java.time.LocalDateTime> lastDateSuspended = adminLogRepository.getLastDateSuspendedUser(userId);
                LocalDateTime userUpdateAt = shmUserDTO.getUserUpdateAt();
                if (CollectionUtils.isNotEmpty(lastDateSuspended) &&  userUpdateAt != null && userUpdateAt.isAfter(lastDateSuspended.get(0))) {
                    updateAfterSuspendTxt = UPDATE_AFTER_CENSORE;
                }
                shmUserDTO.setUserUpdateAfterSuspended(updateAfterSuspendTxt);
                result.add(shmUserDTO);
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
        return null;
    }

    private List<Long> convertStringAdIdToList(String patrolAdminId) {
        String[] array = patrolAdminId.split(",");
        List<Long> result = new ArrayList<>();
        for (String id : array) {
            Long adminId = Long.parseLong(id.trim());
            result.add(adminId);
        }

        return result;
    }

    private void setParams(Query query, Map<String, Object> params, String processStatus, Date updatedAtFrom, Date updatedAtTo,
                           String patrolAdminId, Date censoredFrom, Date censoredTo, StringBuilder sql) {
        if (params.containsKey(PROCESS_STATUS)) {
            query.setParameter(PROCESS_STATUS, ShmUser.PtrlStatus.valueOf(processStatus).ordinal());
        }

        setParamForDate(query, params, updatedAtFrom, updatedAtTo, UPDATED_AT_FROM, UPDATED_AT_TO);

        setParamForDate(query, params, censoredFrom, censoredTo, CENSORED_FROM, CENSORED_TO);

        if (params.containsKey(PATROL_ADMIN_NAME)) {
            query.setParameter(PATROL_ADMIN_NAME, patrolAdminId);
        }

       /* if (params.containsKey(USER_CTRL_STATUS)) {
            query.setParameter(USER_CTRL_STATUS, filters);
        }*/
    }

    private void setParamForDate(Query query, Map<String, Object> params, Date from, Date to, String censoredFromConst, String censoredToConst) {
        if (from != null && to != null) {
            query.setParameter(censoredFromConst, from, TemporalType.TIMESTAMP);
            query.setParameter(censoredToConst, to, TemporalType.TIMESTAMP);
        } else if (from == null && to != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String dateInString = defaultStartDate;
            try {
                Date startDate = formatter.parse(dateInString);
                query.setParameter(censoredFromConst, startDate, TemporalType.TIMESTAMP);
                query.setParameter(censoredToConst, to, TemporalType.TIMESTAMP);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (from != null && to == null) {
            query.setParameter(censoredFromConst, from, TemporalType.TIMESTAMP);
            query.setParameter(censoredToConst, new Date(), TemporalType.TIMESTAMP);
        }
    }

    private void buildSql(Map<String, Object> params, String processStatus, Date updatedAtFrom, Date updatedAtTo,
                          String patrolAdminName, Date censoredFrom, Date censoredTo, StringBuilder sql) {
        if (StringUtils.isNotEmpty(processStatus)) {
            params.put(PROCESS_STATUS, processStatus);
            sql.append(" AND su.USER_PTRL_STATUS = :").append(PROCESS_STATUS);
        }

        if (updatedAtFrom != null || updatedAtTo != null) {
            params.put(UPDATED_AT_FROM, updatedAtFrom);
            params.put(UPDATED_AT_TO, updatedAtTo);
            sql.append(" AND su.USER_UPDATE_AT BETWEEN :").append(UPDATED_AT_FROM);
            sql.append(" AND :").append(UPDATED_AT_TO).append(" ");
        }

        if (censoredFrom != null || censoredTo != null) {
            params.put(CENSORED_FROM, censoredFrom);
            params.put(CENSORED_TO, censoredTo);
            sql.append(" AND su.USER_TIME_PATROL BETWEEN  :").append(CENSORED_FROM);
            sql.append(" AND :").append(CENSORED_TO).append(" ");
        }

        sql.append(" ORDER BY su.USER_UPDATE_AT DESC ");
    }

    @Override
    public List<ShmUser> getUserListByFirstNameAndLastName(String userNames) {
        StringBuilder sql = new StringBuilder("SELECT su FROM ShmUser su WHERE 1 = 1 AND ");
        sql.append(" concat(su.firstName, su.lastName) LIKE '%" + userNames + "%'");
        javax.persistence.Query query = em.createQuery(sql.toString());

        final List resultList = query.getResultList();
        return resultList;
    }

}
