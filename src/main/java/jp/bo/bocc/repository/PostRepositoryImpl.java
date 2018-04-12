package jp.bo.bocc.repository;

import jp.bo.bocc.controller.web.request.PostPatrolRequest;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShtBanner;
import jp.bo.bocc.entity.ShtCategorySetting;
import jp.bo.bocc.enums.SortFieldPostEnum;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.service.BannerService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Namlong on 3/22/2017.
 */
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final static Logger LOGGER = Logger.getLogger(PostRepositoryImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    CategorySettingRepository categorySettingRepository;

    @Autowired
    BannerService bannerService;

    private final String PRICE_ZERO = "priceZero";
    private final String LIST_DISTRICT_ID = "listDistrictId";
    private final String DISTRICT_ID = "addrDistrictCode";
    private final String BSID = "bsid";
    private final String TEXT_SEARCH = "listTextSearch";
    private final String LIST_HASH_TAG = "listHashTag";
    private final String LIST_HASH_TAG_ITEM = "listHashTagItem_";
    private final String POST_TYPE = "postType";
    private final String CATEGORY_ID_SUPER = "categoryIdSuper";
    private final String CATEGORY_ID_CHILD = "categoryIdChild";
    private final String GROUP_PUBLISH_ID = "groupId";
    private final String USER_ID = "userId";

    @Transactional(readOnly = true)
    @Override
    public Page<ShmPost> searchPost(Pageable pageable, final List<String> listHashTag, String textSearch, final String postType, final List<Long> listDistrictId,
                                    final Long addrDistrictId, final String bsid, Long categoryIdSuper, Long categoryIdChild, String sortField, Long price, boolean isSameCompany, Long groupId, Long userId, Long categorySettingId, Long bannerId) {
        StringBuilder sql = new StringBuilder(" SELECT sp FROM ShmPost sp ");
        Map<String, Object> params = new HashMap<String, Object>();

        PageImpl<ShmPost> shmPosts = null;

        ShmPost.PostType postTypeInDb = null;
        if (postType != null)
            postTypeInDb = ShmPost.PostType.valueOf(postType);

        //build sql
        buildSql(listHashTag, textSearch, postTypeInDb, listDistrictId, addrDistrictId, bsid, sql, params, categoryIdSuper, categoryIdChild, sortField, price, isSameCompany, groupId, userId, categorySettingId, bannerId);
        Query query = em.createQuery(sql.toString(), ShmPost.class);
        if (params.size() > 0) {
            if (params.containsKey(LIST_DISTRICT_ID)) {
                query.setParameter(LIST_DISTRICT_ID, listDistrictId);
            } else if (params.containsKey(DISTRICT_ID)) {
                query.setParameter(DISTRICT_ID, addrDistrictId);
            }
            if (params.containsKey(BSID)) {
                query.setParameter(BSID, bsid + "%");
            }
            if(params.containsKey(GROUP_PUBLISH_ID)){
                query.setParameter(GROUP_PUBLISH_ID,groupId);
            }
            if(params.containsKey(USER_ID)){
                query.setParameter(USER_ID, userId);
            }
            if (params.containsKey(LIST_HASH_TAG)) {
                for (int i = 0; i < listHashTag.size(); i++) {
                    query.setParameter(LIST_HASH_TAG_ITEM + i, "%" + listHashTag.get(i) + "%");
                }
            } else if (params.containsKey(TEXT_SEARCH)) {
                if (params.containsKey(TEXT_SEARCH)) {
                    query.setParameter(TEXT_SEARCH, "%" + textSearch.replace(";","").replace("%","\\%").replace("'","") + "%");
                }
            }
            if (params.containsKey(POST_TYPE)) {
                query.setParameter(POST_TYPE, postTypeInDb);
            }
            if (params.containsKey(CATEGORY_ID_SUPER) && !params.containsKey(CATEGORY_ID_CHILD)) {
                query.setParameter(CATEGORY_ID_SUPER, categoryIdSuper);
            } else if (params.containsKey(CATEGORY_ID_SUPER) && params.containsKey(CATEGORY_ID_CHILD)) {
                query.setParameter(CATEGORY_ID_CHILD, categoryIdChild);

            }
            if (price != null && price == 0) {
                query.setParameter(PRICE_ZERO, 0L);
            }
        }
        int total = query.getResultList().size();
        final int offset = pageable.getOffset();
        query.setFirstResult(offset);
        final int pageSize = pageable.getPageSize();
        query.setMaxResults(pageSize);
        List<ShmPost> resultList = query.getResultList();
        Page<ShmPost> result = new PageImpl<ShmPost>(resultList, pageable, total);
        return result;
    }

    /**
     * Search post for search post.
     * @param listHashTag
     * @param textSearch
     * @param postType
     * @param listDistrictId
     * @param addrDistrictId
     * @param bsid
     * @param sql
     * @param params
     * @param categoryIdSuper
     * @param categoryIdChild
     * @param sortField
     * @param price
     */
    private void buildSql(List<String> listHashTag, String textSearch, ShmPost.PostType postType, List<Long> listDistrictId, Long addrDistrictId,
                          String bsid, StringBuilder sql, Map<String, Object> params, Long categoryIdSuper, Long categoryIdChild,
                          String sortField, Long price, boolean isSameCompany, Long groupId, Long userId, Long categorySettingId, Long bannerId) {
        if (addrDistrictId != null || CollectionUtils.isNotEmpty(listDistrictId)) {
            sql.append(" INNER JOIN sp.shmAddr addr ");
        }

        if (StringUtils.isNotEmpty(bsid) && isSameCompany) {
            sql.append(" INNER JOIN sp.shmUser su ");
            params.put(BSID, bsid);
        }

        if (categoryIdSuper != null) {
            sql.append(" INNER JOIN sp.postCategory spc ");
            params.put(CATEGORY_ID_SUPER, categoryIdSuper);
            if (categoryIdChild != null) {
                params.put(CATEGORY_ID_CHILD, categoryIdChild);
            }
        }
        sql.append(" WHERE sp.postSellStatus <> 4 AND sp.postCtrlStatus <> 1 AND sp.deleteFlag = 0 ");
        if (price != null && price.longValue() == 0) {
            sql.append(" AND sp.postPrice = :").append(PRICE_ZERO);
        }
        if (CollectionUtils.isNotEmpty(listHashTag)) {
            params.put(LIST_HASH_TAG, listHashTag);
            for (int i = 0; i < listHashTag.size(); i++) {
                sql.append(" AND UPPER(sp.postHashTagVal) LIKE :").append(LIST_HASH_TAG_ITEM).append(i + " ");
            }
        } else if (StringUtils.isNotEmpty(textSearch)) {
            sql.append(" AND ( UPPER(sp.postName) LIKE :").append(TEXT_SEARCH).append(" ESCAPE '\\' ");
            sql.append(" OR UPPER(sp.postDescription) LIKE :").append(TEXT_SEARCH).append(" ESCAPE '\\' ");
            sql.append(" OR UPPER(sp.postHashTagVal) LIKE :").append(TEXT_SEARCH).append(" ESCAPE '\\' ").append(" ) ");
            params.put(TEXT_SEARCH, textSearch);
        }
        if (postType != null) {
            sql.append(" AND sp.postType = :").append(POST_TYPE);
            params.put(POST_TYPE, postType);
        }
        if (addrDistrictId != null) {
            params.put(DISTRICT_ID, addrDistrictId);
            sql.append(" AND addr.addressId = :").append(DISTRICT_ID);
        } else if (CollectionUtils.isNotEmpty(listDistrictId)) {
            params.put(LIST_DISTRICT_ID, listDistrictId);
            sql.append(" AND addr.addressId IN :").append(LIST_DISTRICT_ID);
        }
        if (params.containsKey(BSID)) {
            sql.append(" AND su.bsid LIKE :").append(BSID);
        }
        if (bsid == null && !isSameCompany) {
            sql.append(" AND (sp.destinationPublishType = 0 or sp.destinationPublishType is null)");
        }
        if (bsid != null && !isSameCompany && groupId == null) {
            sql.append(" AND ((sp.destinationPublishType = 0 or sp.destinationPublishType is null) OR ( sp.shmUser.id = :").append(USER_ID).append(") OR (sp.destinationPublishType = 1 AND sp.shmUser.bsid LIKE :").append(BSID).append(")) ");
            params.put(USER_ID, userId);
            params.put(BSID, bsid);
        }
        if(bsid != null && !isSameCompany && groupId != null){
            sql.append(" AND ((sp.destinationPublishType = 0 or sp.destinationPublishType is null) OR ( sp.shmUser.id = :").append(USER_ID).append(") OR (sp.destinationPublishType = 1 AND sp.shmUser.bsid LIKE :").append(BSID).append(") OR (sp.destinationPublishType = 2 AND sp.groupId = :").append(GROUP_PUBLISH_ID).append("))");
            params.put(USER_ID, userId);
            params.put(BSID, bsid);
            params.put(GROUP_PUBLISH_ID, groupId);
        }
        if (params.containsKey(CATEGORY_ID_SUPER) && params.containsKey(CATEGORY_ID_CHILD)) {
            sql.append(" AND spc.categoryId = :").append(CATEGORY_ID_CHILD);
        } else if (params.containsKey(CATEGORY_ID_SUPER) && !params.containsKey(CATEGORY_ID_CHILD)) {
            sql.append(" AND spc.categoryParentId  = :").append(CATEGORY_ID_SUPER);
        }
        sql.append(" AND sp.shmUser.status NOT IN (5,6) AND sp.shmUser.ctrlStatus <> 2 ");

        // Begin search by categorySetting
        buildSqlWithCategorySetting(sql, params, bsid, categorySettingId);
        //End search by categorySetting

        // Begin search by banner
        buildSqlWithBanner(sql, params, bsid, bannerId);
        //End search by banner


        sortForListPost(sql, sortField, price,false);
    }

    private void buildSqlWithBanner(StringBuilder sql, Map<String, Object> params, String bsid, Long bannerId){
        if(bannerId != null){
            ShtBanner banner = bannerService.getBannerById(bannerId);
            if(banner != null && banner.getBannerStatus() == ShtBanner.BannerStatusEnum.ACTIVE){
               if(banner.getDestinationType() == ShtBanner.DestinationTypeEnum.CATEGORY && banner.getCategoryId() != null){
                   buildSqlWithCategorySetting(sql, params, bsid, banner.getCategoryId());
               }
               if(banner.getDestinationType() == ShtBanner.DestinationTypeEnum.POST_ID && StringUtils.isNotEmpty(banner.getPostIds())){
                   buildSqlWithPostIds(sql, banner.getPostIds());
               }
                if(banner.getDestinationType() == ShtBanner.DestinationTypeEnum.KEYWORD && StringUtils.isNotEmpty(banner.getKeywords())){
                    buildSqlWithKeywords(sql, banner.getKeywords());
                }
            }
        }
    }

    private void buildSqlWithCategorySetting(StringBuilder sql, Map<String, Object> params, String bsid, Long categorySettingId){
        if(categorySettingId != null){
            ShtCategorySetting categorySetting = categorySettingRepository.getOne(categorySettingId);
            if(categorySetting != null && categorySetting.getCategoryStatus() == ShtCategorySetting.CategoryStatusEnum.ACTIVE){
                if (categorySetting.getCategoryFilterType() == ShtCategorySetting.CategoryFilterTypeEnum.PRIVATE && bsid != null) {
                    buildSqlWithCategoryPrivate(sql, params, bsid, categorySetting);
                }
                if(categorySetting.getCategoryFilterType() == ShtCategorySetting.CategoryFilterTypeEnum.KEYWORD && StringUtils.isNotEmpty(categorySetting.getKeywords())){
                    buildSqlWithKeywords(sql,categorySetting.getKeywords());
                }
                if(categorySetting.getCategoryFilterType() == ShtCategorySetting.CategoryFilterTypeEnum.POST_ID && StringUtils.isNotEmpty(categorySetting.getPostIds())){
                    buildSqlWithPostIds(sql, categorySetting.getPostIds());
                }
            }
        }
    }

    private void buildSqlWithCategoryPrivate(StringBuilder sql, Map<String, Object> params, String bsid, ShtCategorySetting categorySetting){
        sql.append(" AND sp.shmUser.bsid LIKE :").append(BSID);
        params.put(BSID, bsid);
    }

    private void buildSqlWithPostIds(StringBuilder sql, String postIdStrs){
        String[] postIds = postIdStrs.split(",");
        String postIdStr = "";
                // just defend code
        if(postIds.length > 0){
            for(int i = 0; i < postIds.length ; i++){
                postIdStr += postIds[i].trim() + ",";
            }
        }
        if(StringUtils.isNotEmpty(postIdStr)){
            postIdStr = postIdStr.substring(0, postIdStr.length() - 1);
            postIdStr = "(" + postIdStr + ")";
            sql.append(" AND sp.postId IN ").append(postIdStr).append(" ");
        }
    }

    private void buildSqlWithKeywords(StringBuilder sql, String keywordStrs){
        String[] keywords = keywordStrs.split(",");
        if(keywords.length > 0){
            StringBuilder builderExtSearchSql = new StringBuilder("");
            for(int i = 0; i < keywords.length ; i++){
                if(StringUtils.isNotEmpty(keywords[i].trim())){
                    if(i == 0){
                        builderExtSearchSql.append(" ( UPPER(sp.postName) LIKE '%").append(keywords[i].replace(";","").replace("%","\\%").replace("'","").toUpperCase()).append("%' ESCAPE '\\' ");
                        builderExtSearchSql.append(" OR UPPER(sp.postDescription) LIKE '%").append(keywords[i].replace(";","").replace("%","\\%").replace("'","").toUpperCase()).append("%' ESCAPE '\\' ");
                        builderExtSearchSql.append(" OR UPPER(sp.postHashTagVal) LIKE '%").append(keywords[i].replace(";","").replace("%","\\%").replace("'","").toUpperCase()).append("%' ESCAPE '\\' ").append(" )");
                    }else{
                        builderExtSearchSql.append(" OR ( UPPER(sp.postName) LIKE '%").append(keywords[i].replace(";","").replace("%","\\%").replace("'","").toUpperCase()).append("%' ESCAPE '\\' ");
                        builderExtSearchSql.append(" OR UPPER(sp.postDescription) LIKE '%").append(keywords[i].replace(";","").replace("%","\\%").replace("'","").toUpperCase()).append("%' ESCAPE '\\' ");
                        builderExtSearchSql.append(" OR UPPER(sp.postHashTagVal) LIKE '%").append(keywords[i].replace(";","").replace("%","\\%").replace("'","").toUpperCase()).append("%' ESCAPE '\\' ").append(" ) ");
                    }
                }
            }
            if(StringUtils.isNotEmpty(builderExtSearchSql.toString())){
                String extSearchSql = builderExtSearchSql.toString();
                extSearchSql = " AND ( " + extSearchSql + " ) ";
                sql.append(extSearchSql);
            }
        }
    }


    @Transactional(readOnly = true)
    @Override
    public List<Long> getPostIdByUserId(Long userId) {
        StringBuilder sql = new StringBuilder(" SELECT sp.postId FROM ShmPost sp INNER JOIN sp.shmUser su WHERE su.id = :userId ");
        Query query = em.createQuery(sql.toString());
        query.setParameter("userId", userId);
        List<Long> resultList = query.getResultList();
        return resultList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPostPatrolList(PostPatrolRequest postSearchRequest) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT outer.POST_IMAGES, outer.POST_NAME, outer.USER_NICK_NAME, outer.POST_PTRL_STATUS, outer.USER_UPDATE_AT, outer.POST_CTRL_STATUS, outer.POST_REPORT_TIMES, outer.rn, outer.total, outer.POST_ID, outer.ADMIN_ID, outer.ADMIN_NAME, outer.CMN_ENTRY_DATE\n " +
                "  FROM (SELECT ROWNUM rn, inner.ADMIN_NAME, inner.ADMIN_ID, inner.USER_NICK_NAME, inner.POST_IMAGES, inner.POST_NAME, inner.POST_PTRL_STATUS, inner.USER_UPDATE_AT, inner.POST_TIME_PATROL, inner.POST_CTRL_STATUS, inner.CMN_ENTRY_DATE, inner.POST_REPORT_TIMES, inner.POST_ID, count(inner.POST_ID) over() as total\n " +
                "          FROM (SELECT DISTINCT adm.ADMIN_NAME, adm.ADMIN_ID, us.USER_NICK_NAME, post.POST_IMAGES, post.POST_NAME, post.POST_PTRL_STATUS, post.USER_UPDATE_AT, post.POST_TIME_PATROL, post.POST_CTRL_STATUS, post.CMN_ENTRY_DATE, post.POST_REPORT_TIMES, post.POST_ID\n " +
                "                    FROM SHM_POST post ");
        String operatorIds = "";
        if (!org.apache.commons.lang.StringUtils.isEmpty(postSearchRequest.getOperatorNames())) {
            List<Long> listAmdinId = adminRepository.getListAdminIdByListAdminNameNew(postSearchRequest.getOperatorNames());
            if (listAmdinId.size() == 0)
                return new ArrayList<>();
            operatorIds = listAmdinId.stream().map(Object::toString).collect(Collectors.joining(","));
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(operatorIds)) {
            builder.append(" INNER JOIN SHT_ADMIN_LOG log ON post.POST_ID = log.POST_ID AND log.ADMIN_ID in (" + operatorIds + ")");
        } else {
            builder.append(" LEFT JOIN SHT_ADMIN_LOG log ON post.POST_ID = log.POST_ID ");
        }

        builder.append(" LEFT JOIN SHM_ADMIN adm ON log.ADMIN_ID = adm.ADMIN_ID\n " +
                " INNER JOIN SHM_USER us ON us .USER_ID = post.POST_USER_ID" +
                " WHERE (post.POST_IS_IN_PATROL = 0 or post.POST_IS_IN_PATROL is null) and post.POST_SELL_STATUS != 4 AND post.CMN_DELETE_FLAG = 0 ");

        if (postSearchRequest.getPostStatus() != null) {
            builder.append(" AND post.POST_PTRL_STATUS = " + postSearchRequest.getPostStatus());
        }

        if (postSearchRequest.getReportStt() != null)
            builder.append(" AND post.POST_REPORT_TIMES > 0 ");

        String conditions = "";

        if (postSearchRequest.getFromUpdateAt() != null && postSearchRequest.getToUpdateAt() == null) {
            conditions = " AND post.USER_UPDATE_AT >= TIMESTAMP'" + dateTimeFormat(postSearchRequest.getFromUpdateAt().toString(), 0, false) + "' ";
            builder.append(conditions);
        }

        if (postSearchRequest.getFromUpdateAt() == null && postSearchRequest.getToUpdateAt() != null) {
            conditions = " AND post.USER_UPDATE_AT <= TIMESTAMP'" + dateTimeFormat(postSearchRequest.getToUpdateAt().toString(), 1, true) + "' ";
            builder.append(conditions);
        }

        if (postSearchRequest.getFromUpdateAt() != null && postSearchRequest.getToUpdateAt() != null) {
            conditions = " AND post.USER_UPDATE_AT BETWEEN TIMESTAMP'" + dateTimeFormat(postSearchRequest.getFromUpdateAt().toString(), 0, false) + "' " +
                    "AND TIMESTAMP'" + dateTimeFormat(postSearchRequest.getToUpdateAt().toString(), 1, true) + "' ";
            builder.append(conditions);
        }

        if (postSearchRequest.getFromCompleteAt() != null && postSearchRequest.getToCompleteAt() == null) {
            builder.append(" AND post.POST_TIME_PATROL >= TIMESTAMP'" + dateTimeFormat(postSearchRequest.getFromCompleteAt().toString(), 0, false) + "' ");
        }

        if (postSearchRequest.getFromCompleteAt() == null && postSearchRequest.getToCompleteAt() != null) {
            builder.append(" AND post.POST_TIME_PATROL <= TIMESTAMP'" + dateTimeFormat(postSearchRequest.getToCompleteAt().toString(), 1, true) + "' ");
        }

        if (postSearchRequest.getFromCompleteAt() != null && postSearchRequest.getToCompleteAt() != null) {
            builder.append(" AND post.POST_TIME_PATROL BETWEEN TIMESTAMP'" + dateTimeFormat(postSearchRequest.getFromCompleteAt().toString(), 0, false) + "' " +
                    "AND TIMESTAMP'" + dateTimeFormat(postSearchRequest.getToCompleteAt().toString(), 1, true) + "' ");
        }

        builder.append(" ORDER BY post.USER_UPDATE_AT DESC) inner) outer\n" +
                " WHERE outer.rn > " + postSearchRequest.getStartPage() + " AND outer.rn <= " + postSearchRequest.getEndPage());

        return em.createNativeQuery(builder.toString().replace("[", "(").replace("]", ")")).getResultList();
    }

    private String dateTimeFormat(String dateTime, int plus, boolean isEndDate) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z uuuu").withLocale(Locale.US);
        ZonedDateTime zdt = ZonedDateTime.parse(dateTime, format);
        LocalDateTime ld = zdt.toLocalDateTime().plusDays(plus);

        if (isEndDate)
            ld = zdt.toLocalDateTime().plusDays(plus).minusSeconds(1);

        DateTimeFormatter fLocalDate = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

        return ld.format(fLocalDate);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countPostByUserId(Long userId) {
        StringBuilder sql = new StringBuilder(" SELECT COUNT(*) FROM ShmPost sp WHERE sp.shmUser.id = :userId AND sp.postSellStatus <> 4 ");
        Query query = em.createQuery(sql.toString());
        query.setParameter("userId", userId);
        return (Long) query.getSingleResult();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Object[]> getOwnerPostHaveTalkPurc(Long userId, Pageable pageable) {
        String sql = "    SELECT sp.POST_ID, sp.POST_NAME, sp.POST_DESCRIPTION, sp.POST_CATEGORY_ID, sp.POST_PRICE, sp.POST_LIKE_TIMES, " +
                "         sp.POST_REPORT_TIMES, sp.POST_TYPE, sp.POST_IMAGES, sp.POST_ADDR, sp.POST_ADDR_TXT, sp.POST_SELL_STATUS, " +
                "         sp.POST_HASH_TAG_VAL, sp.POST_CTRL_STATUS " +
                "         FROM SHM_POST sp INNER JOIN " +
                "                 (SELECT  tm.TALK_PURC_POST_ID, MAX(tm.LAST_CREATED) AS LAST_CREATED FROM (SELECT stp.TALK_PURC_POST_ID, stp.TALK_PURC_ID, stpm.LAST_CREATED FROM SHT_TALK_PURC stp INNER JOIN " +
                "                 ( SELECT TALK_PURC_ID, MAX(CMN_ENTRY_DATE) AS LAST_CREATED FROM SHT_TALK_PURC_MSG WHERE CMN_DELETE_FLAG = 0 GROUP BY TALK_PURC_ID ) stpm " +
                "                 ON stp.TALK_PURC_ID = stpm.TALK_PURC_ID) tm GROUP BY tm.TALK_PURC_POST_ID ) ptm " +
                "         ON sp.POST_ID = ptm.TALK_PURC_POST_ID WHERE sp.POST_USER_ID =:userId ORDER BY ptm.LAST_CREATED DESC ";
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("userId", userId);
        int totalPage = query.getResultList().size();
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        final List<Object[]> resultList = query.getResultList();
        Page<Object[]> result = new PageImpl<Object[]>(resultList, pageable, totalPage);
        return result;
    }

    @Override
    public Long countTotalPostByUserIdList(List<Long> userIdList) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total_post\n" +
                "FROM shm_post post \n" +
                "WHERE post.POST_USER_ID IN ");
        sql.append(userIdList);
        String after = sql.toString().replace("[", "(").replace("]", ")");
        List<Object[]> result =  em.createNativeQuery(after).getResultList();
        return Long.parseLong(String.valueOf(result.get(0)));
    }

    @Override
    public Long countTotalPostMatchingByUserIdList(List<Long> userIdList) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total_matching\n" +
                "FROM shm_post post\n" +
                "WHERE post.POST_PARTNER_ID IN ");
        sql.append(userIdList);
        String after = sql.toString().replace("[", "(").replace("]", ")");
        List<Object[]> result =  em.createNativeQuery(after).getResultList();
        return Long.parseLong(String.valueOf(result.get(0)));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Object[]> getPostFromOtherHavingConversation(Long userId, Pageable pageable) {
        String sql = "    SELECT sp.POST_ID, sp.POST_NAME, sp.POST_DESCRIPTION, sp.POST_CATEGORY_ID, sp.POST_PRICE, sp.POST_LIKE_TIMES, " +
                "         sp.POST_REPORT_TIMES, sp.POST_TYPE, sp.POST_IMAGES, sp.POST_ADDR, sp.POST_ADDR_TXT, sp.POST_SELL_STATUS, " +
                "         sp.POST_HASH_TAG_VAL, sp.POST_CTRL_STATUS, ptm.TALK_PURC_ID, ptm.LAST_CREATED " +
                "         FROM SHM_POST sp INNER JOIN " +
                "                 (SELECT  tm.TALK_PURC_POST_ID,tm.TALK_PURC_ID, MAX(tm.LAST_CREATED) AS LAST_CREATED FROM (SELECT stp.TALK_PURC_POST_ID, stp.TALK_PURC_ID, stpm.LAST_CREATED FROM SHT_TALK_PURC stp INNER JOIN " +
                "                 ( SELECT TALK_PURC_ID, MAX(CMN_ENTRY_DATE) AS LAST_CREATED FROM SHT_TALK_PURC_MSG WHERE CMN_DELETE_FLAG = 0 GROUP BY TALK_PURC_ID ) stpm " +
                "                 ON stp.TALK_PURC_ID = stpm.TALK_PURC_ID WHERE stp.TALK_PURC_PART_ID = :userId) tm GROUP BY tm.TALK_PURC_POST_ID, tm.TALK_PURC_ID ) ptm " +
                "         ON sp.POST_ID = ptm.TALK_PURC_POST_ID WHERE sp.POST_USER_ID <> :userId ORDER BY ptm.LAST_CREATED DESC ";

        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("userId", userId);
        int totalItems = query.getResultList().size();
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        final List<Object[]> resultList = query.getResultList();
        Page<Object[]> result = new PageImpl<Object[]>(resultList, pageable, totalItems);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getListPostIdForOwnerPostHavingConversation(Long userIdUsingApp) {
        String sql = "SELECT stp.TALK_PURC_POST_ID postId FROM SHT_TALK_PURC stp WHERE stp.TALK_PURC_POST_ID IN " +
                "(SELECT sp.POST_ID FROM SHM_POST sp WHERE sp.POST_USER_ID = :userId AND sp.POST_SELL_STATUS <> 4 )";
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("userId", userIdUsingApp);
        final List<Object> resultList = query.getResultList();
        return ConverterUtils.convertResultListToListLong(resultList);
    }

    @Override
    public List<Long> getListPostIdFormOthersHavingConversation(Long userIdUsingApp) {
        String sql = "SELECT DISTINCT sp.POST_ID FROM SHM_POST sp INNER JOIN SHT_TALK_PURC stp ON sp.POST_ID = stp.TALK_PURC_POST_ID WHERE stp.TALK_PURC_PART_ID = :userId AND sp.postSellStatus <> 4 ";
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("userId", userIdUsingApp);
        final List resultList = query.getResultList();
        return ConverterUtils.convertResultListToListLong(resultList);
    }

    @Override
    public Page<ShmPost> getPostHistory(Pageable pageable, String postType, String sortField, Long price, Long userId) {
        StringBuilder sql = new StringBuilder(" SELECT sp FROM ShmPost sp INNER JOIN sp.shmUser su WHERE su.id = :userId AND sp.postSellStatus <> 4 ");
        Map<String, Object> params = new HashMap<String, Object>();

        PageImpl<ShmPost> shmPosts = null;

        ShmPost.PostType postTypeInDb = null;

        try {
            if (StringUtils.isNotEmpty(postType))
                postTypeInDb = ShmPost.PostType.valueOf(postType);
        } catch (IllegalArgumentException e) {
            LOGGER.error("ERROR: post type is not valid.");
        }

        //build sql
        buildSqlForPostHistoryFavorite(postTypeInDb, sql, params, sortField, price,true);
        Query query = em.createQuery(sql.toString(), ShmPost.class);
        setPostTypeAndPrice(params, postTypeInDb, query);
        query.setParameter("userId", userId);

        int total = query.getResultList().size();
        List<ShmPost> resultList = new ArrayList<>();
        resultList = buildResult(pageable, query, total, resultList);
        Page<ShmPost> result = new PageImpl<ShmPost>(resultList, pageable, total);
        return result;
    }

    private void buildSqlForPostHistoryFavorite(ShmPost.PostType postType, StringBuilder sql, Map<String, Object> params, String sortField, Long price, Boolean zeroPriceInclude) {
        if (price != null && price.longValue() == 0) {
            sql.append(" AND sp.postPrice = :").append(PRICE_ZERO);
            params.put(PRICE_ZERO, price);
        }
        if (postType != null) {
            sql.append(" AND sp.postType = :").append(POST_TYPE);
            params.put(POST_TYPE, postType);
        }
        sortForListPostIncludePriceZero(sql, sortField, price, zeroPriceInclude);
    }

    /**
     * Sort results ascending price with price = 0
     * @param sql
     * @param sortField
     * @param price
     * @param zeroPriceInclude
     */
    private void sortForListPostIncludePriceZero(StringBuilder sql, String sortField, Long price, Boolean zeroPriceInclude) {
        if (SortFieldPostEnum.PRICE_ASC.getValue().equals(sortField)) {
            if (zeroPriceInclude) {
                sql.append(" ORDER BY sp.postPrice ASC, sp.postId DESC ");
            } else {
                sql.append(" AND sp.postPrice <> 0 ORDER BY sp.postPrice ASC, sp.postId DESC ");
            }
        } else if (SortFieldPostEnum.PRICE_DESC.getValue().equals(sortField)) {
            sql.append(" ORDER BY sp.postPrice DESC, sp.postId DESC ");
        } else if (SortFieldPostEnum.LIKE_TIMES_DESC.getValue().equals(sortField)) {
            sql.append(" ORDER BY sp.postLikeTimes DESC, sp.postId DESC ");
        } else if (SortFieldPostEnum.LIKE_AT_DESC.getValue().equals(sortField)) {
            sql.append(" ORDER BY suf.createdAt DESC ");
        } else {
            sql.append(" ORDER BY sp.createdAt DESC, sp.postId DESC ");
        }
    }

    /**
     * zeroPriceInclude is TRUE, include records having price = 0
     * @param sql
     * @param sortField
     * @param price
     * @param zeroPriceInclude
     */
    private void sortForListPost(StringBuilder sql, String sortField, Long price, Boolean zeroPriceInclude) {
        if (SortFieldPostEnum.PRICE_ASC.getValue().equals(sortField)) {
            if (price != null && price.longValue() == 0)
                sql.append(" ORDER BY sp.createdAt DESC ");
            else{
                if(zeroPriceInclude){
                    sql.append(" ORDER BY sp.postPrice ASC, sp.postId DESC ");
                }else{
                    sql.append(" AND sp.postPrice <> 0 ORDER BY sp.postPrice ASC, sp.postId DESC ");
                }
            }
        } else if (SortFieldPostEnum.PRICE_DESC.getValue().equals(sortField)) {
            sql.append(" ORDER BY sp.postPrice DESC, sp.postId DESC ");
        } else if (SortFieldPostEnum.LIKE_TIMES_DESC.getValue().equals(sortField)) {
            sql.append(" ORDER BY sp.postLikeTimes DESC, sp.postId DESC ");
        } else if(SortFieldPostEnum.LIKE_AT_DESC.getValue().equals(sortField)){
            sql.append(" ORDER BY suf.createdAt DESC ");
        }else {
            sql.append(" ORDER BY sp.createdAt DESC, sp.postId DESC ");
        }
    }

    @Override
    public Page<ShmPost> getFavoritePostHistory(Pageable pageable, String postType, String sortField, Long price, Long userId) {
        StringBuilder sql = new StringBuilder(" SELECT sp FROM ShtUserFvrt suf RIGHT JOIN suf.shmPost sp RIGHT JOIN suf.shmUser su WHERE su.id = :userId AND suf.userFvrtStatus = 1 " +
                " AND sp.shmUser.status NOT IN (5,6) AND sp.shmUser.ctrlStatus <> 2 ");
        Map<String, Object> params = new HashMap<String, Object>();

        PageImpl<ShmPost> shmPosts = null;

        ShmPost.PostType postTypeInDb = null;

        try {
            if (StringUtils.isNotEmpty(postType))
                postTypeInDb = ShmPost.PostType.valueOf(postType);
        } catch (IllegalArgumentException e) {
            LOGGER.error("ERROR: post type is not valid.");
        }

        //build sqldb
        buildSqlForPostHistoryFavorite(postTypeInDb, sql, params, sortField, price, true);
        Query query = em.createQuery(sql.toString(), ShmPost.class);
        setPostTypeAndPrice(params, postTypeInDb, query);
        query.setParameter("userId", userId);

        int total = query.getResultList().size();
        List<ShmPost> resultList = new ArrayList<>();
        resultList = buildResult(pageable, query, total, resultList);
        Page<ShmPost> result = new PageImpl<ShmPost>(resultList, pageable, total);
        return result;
    }

    @Override
    public Page<ShmPost> getPostByUserId(Long userId, Long groupId, String companyId, Pageable pageable) {
        StringBuilder sql = null;
        if (StringUtils.isEmpty(companyId)) {
            sql = new StringBuilder(" SELECT sp FROM ShmPost sp WHERE sp.shmUser.id = :userId AND sp.postSellStatus <> 4 AND sp.postCtrlStatus <> 1 AND (sp.destinationPublishType = 0 or sp.destinationPublishType is null) ORDER BY sp.createdAt DESC ");
        } else {
            if(groupId == null){
                sql = new  StringBuilder(" SELECT sp FROM ShmPost sp WHERE sp.shmUser.id = :userId AND sp.postSellStatus <> 4 AND sp.postCtrlStatus <> 1 AND ((sp.destinationPublishType = 0 or sp.destinationPublishType is null) " +
                        "OR (sp.destinationPublishType = 1 AND sp.shmUser.bsid like :bsid)) ORDER BY sp.createdAt DESC ");
            }else{
                sql = new  StringBuilder(" SELECT sp FROM ShmPost sp WHERE sp.shmUser.id = :userId AND sp.postSellStatus <> 4 AND sp.postCtrlStatus <> 1 AND ((sp.destinationPublishType = 0 or sp.destinationPublishType is null) " +
                        "OR (sp.destinationPublishType = 1 AND sp.shmUser.bsid like :bsid) OR (sp.destinationPublishType = 2 AND sp.groupId like :groupId)) ORDER BY sp.createdAt DESC ");
            }
        }
        Query query = em.createQuery(sql.toString());
        query.setParameter("userId", userId);
        if (StringUtils.isNotEmpty(companyId)) {
            query.setParameter("bsid", companyId + "%");
        }
        if(groupId != null){
            query.setParameter("groupId", groupId);
        }
        int totalItems = query.getResultList().size();
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        final List<ShmPost> resultList = query.getResultList();
        Page<ShmPost> result = new PageImpl<ShmPost>(resultList, pageable, totalItems);
        return result;
    }

    @Override
    public Long getPostNumberByMonth(String month) {
        String sql = "SELECT COUNT(*) FROM SHM_POST WHERE TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM') LIKE '" + month + "'";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getOwnerPostByMonth(String month) {
        String sql = "SELECT COUNT(*) FROM (SELECT DISTINCT POST_USER_ID FROM SHM_POST WHERE TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM') LIKE '" + month + "')";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getPartnerByMonth(String month) {
        String sql = "SELECT COUNT(*) FROM (SELECT DISTINCT POST_PARTNER_ID FROM SHM_POST sp WHERE sp.POST_PARTNER_ID IS NOT NULL AND EXISTS (SELECT * FROM SHT_TALK_PURC stp WHERE sp.POST_ID = stp.TALK_PURC_POST_ID AND " +
                " EXISTS (SELECT * FROM SHT_TALK_PURC_MSG stpm WHERE stpm.TALK_PURC_ID = stp.TALK_PURC_ID AND stpm.TALK_PURC_MSG_TYPE = 2 AND " +
                " TO_CHAR(stpm.CMN_ENTRY_DATE,'yyyy/MM') LIKE '" + month + "')))";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getTransByMonth(String month) {
        String sql = "SELECT COUNT(*) FROM SHM_POST sp WHERE sp.POST_PARTNER_ID IS NOT NULL AND EXISTS (SELECT * FROM SHT_TALK_PURC stp WHERE sp.POST_ID = stp.TALK_PURC_POST_ID AND " +
                " EXISTS (SELECT * FROM SHT_TALK_PURC_MSG stpm WHERE stpm.TALK_PURC_ID = stp.TALK_PURC_ID AND stpm.TALK_PURC_MSG_TYPE = 2 AND " +
                " TO_CHAR(stpm.CMN_ENTRY_DATE,'yyyy/MM') LIKE '" + month + "'))";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getActorPerMonth(String month) {
        String sql = "SELECT COUNT(*) FROM (SELECT  DISTINCT POST_USER_ID FROM ((SELECT DISTINCT POST_USER_ID FROM SHM_POST WHERE TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM') LIKE '" + month + "') UNION (SELECT DISTINCT POST_PARTNER_ID FROM SHM_POST sp WHERE sp.POST_PARTNER_ID IS NOT NULL AND EXISTS (SELECT * FROM SHT_TALK_PURC stp WHERE sp.POST_ID = stp.TALK_PURC_POST_ID AND " +
                " EXISTS (SELECT * FROM SHT_TALK_PURC_MSG stpm WHERE stpm.TALK_PURC_ID = stp.TALK_PURC_ID AND stpm.TALK_PURC_MSG_TYPE = 2 AND " +
                " TO_CHAR(stpm.CMN_ENTRY_DATE,'yyyy/MM') LIKE '" + month + "')))))";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getPostNumberPerDay(String day) {
        String sql = "SELECT COUNT(*) FROM SHM_POST WHERE TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM/dd') LIKE '" + day + "'";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getOwnerPostPerDay(String day) {
        String sql = "SELECT COUNT(*) FROM (SELECT DISTINCT POST_USER_ID FROM SHM_POST WHERE TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM/dd') LIKE '" + day + "')";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getPartnerPerDay(String day) {
        String sql = "SELECT COUNT(*) FROM (SELECT DISTINCT POST_PARTNER_ID FROM SHM_POST sp WHERE sp.POST_PARTNER_ID IS NOT NULL AND EXISTS (SELECT * FROM SHT_TALK_PURC stp WHERE sp.POST_ID = stp.TALK_PURC_POST_ID AND " +
                " EXISTS (SELECT * FROM SHT_TALK_PURC_MSG stpm WHERE stpm.TALK_PURC_ID = stp.TALK_PURC_ID AND stpm.TALK_PURC_MSG_TYPE = 2 AND " +
                " TO_CHAR(stpm.CMN_ENTRY_DATE,'yyyy/MM/dd') LIKE '" + day + "')))";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getTransPerDay(String day) {
        String sql = "SELECT COUNT(*) FROM SHM_POST sp WHERE sp.POST_PARTNER_ID IS NOT NULL AND EXISTS (SELECT * FROM SHT_TALK_PURC stp WHERE sp.POST_ID = stp.TALK_PURC_POST_ID AND " +
                " EXISTS (SELECT * FROM SHT_TALK_PURC_MSG stpm WHERE stpm.TALK_PURC_ID = stp.TALK_PURC_ID AND stpm.TALK_PURC_MSG_TYPE = 2 AND " +
                " TO_CHAR(stpm.CMN_ENTRY_DATE,'yyyy/MM/dd') LIKE '" + day + "'))";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    @Override
    public Long getActorPerDay(String day) {
        String sql = "SELECT COUNT(*) FROM (SELECT  DISTINCT POST_USER_ID FROM ((SELECT DISTINCT POST_USER_ID FROM SHM_POST WHERE TO_CHAR(CMN_ENTRY_DATE,'yyyy/MM/dd') LIKE '" + day + "') UNION (SELECT DISTINCT POST_PARTNER_ID FROM SHM_POST sp WHERE sp.POST_PARTNER_ID IS NOT NULL AND EXISTS (SELECT * FROM SHT_TALK_PURC stp WHERE sp.POST_ID = stp.TALK_PURC_POST_ID AND " +
                " EXISTS (SELECT * FROM SHT_TALK_PURC_MSG stpm WHERE stpm.TALK_PURC_ID = stp.TALK_PURC_ID AND stpm.TALK_PURC_MSG_TYPE = 2 AND " +
                " TO_CHAR(stpm.CMN_ENTRY_DATE,'yyyy/MM/dd') LIKE '" + day + "')))))";
        Query query = em.createNativeQuery(sql);
        final Object singleResult = query.getSingleResult();
        return ConverterUtils.getLongValue(singleResult);
    }

    private List<ShmPost> buildResult(Pageable pageable, Query query, int total, List<ShmPost> resultList) {
        if (total > 0) {
            final int offset = pageable.getOffset();
            query.setFirstResult(offset);
            final int pageSize = pageable.getPageSize();
            query.setMaxResults(pageSize);
            resultList = query.getResultList();
        }
        return resultList;
    }

    private void setPostTypeAndPrice(Map<String, Object> params, ShmPost.PostType postTypeInDb, Query query) {
        if (params.size() > 0) {
            if (params.containsKey(POST_TYPE)) {
                query.setParameter(POST_TYPE, postTypeInDb);
            }
            if (params.containsKey(PRICE_ZERO)) {
                query.setParameter(PRICE_ZERO, 0L);
            }
        }
    }
}
