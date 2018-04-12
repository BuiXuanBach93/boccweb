package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmAdmin;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author NguyenThuong on 5/29/2017.
 */
public class AdminRepositoryImpl implements AdminRepositoryCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public String getListAdminIdByListAdminName(String operatorNames) {
        List<ShmAdmin> admins = searchAdminByAdminName(jp.bo.bocc.helper.StringUtils.convertStringToList(operatorNames, "[,、\\s+]+"));
        String operatorId = "";
        for (ShmAdmin ad : admins) {
            operatorId = operatorId + "," + ad.getAdminId();
        }
        if (operatorId.startsWith(","))
            operatorId = operatorId.substring(1, operatorId.length()).trim();
        return operatorId;
    }

    @Override
    public List<ShmAdmin> searchAdminByAdminName(List<String> adminNames) {
        StringBuilder sql = new StringBuilder("SELECT ad FROM ShmAdmin ad WHERE ");
        for (String adName : adminNames) {
            if(StringUtils.isNotEmpty(adName)){
                adName = adName.replace("%","").replace(";","").replace("'","");
            }
            if (adminNames.indexOf(adName) == 0)
                sql.append(" ad.adminName LIKE '%" + adName + "%'");
            else {
                sql.append(" OR ad.adminName LIKE '%" + adName + "%'");
            }
        }
        javax.persistence.Query query = em.createQuery(sql.toString());

        return query.getResultList();
    }

    @Override
    public List<Long> getListAdminIdByListAdminNameNew(String patrolAdminNames) {
        List<Long> result = new ArrayList<>();
        if(StringUtils.isNotEmpty(patrolAdminNames)){
            patrolAdminNames = patrolAdminNames.replace("%","").replace(";","").replace("'","");
        }
        if (StringUtils.isNotEmpty(patrolAdminNames)) {
            List<String> adminNameList = new ArrayList<>();
            if (patrolAdminNames.contains(",")) {
                adminNameList = Arrays.asList(patrolAdminNames.split(","));
            } else if (patrolAdminNames.contains("、")) {
                adminNameList = Arrays.asList(patrolAdminNames.split("、"));
            } else {
                adminNameList.add(patrolAdminNames);
            }
            StringBuilder sql = new StringBuilder("SELECT ad.adminId FROM ShmAdmin ad WHERE ");
            for (String adName : adminNameList) {
                if(StringUtils.isNotEmpty(adName)){
                    adName = adName.replace("%","").replace(";","").replace("'","");
                }
                if (adminNameList.indexOf(adName) == 0)
                    sql.append(" ad.adminName LIKE '%" + adName.trim() + "%'");
                else {
                    sql.append(" OR ad.adminName LIKE '%" + adName.trim() + "%'");
                }
            }
            javax.persistence.Query query = em.createQuery(sql.toString());

            result = query.getResultList();
        }
        return result;
    }

}
