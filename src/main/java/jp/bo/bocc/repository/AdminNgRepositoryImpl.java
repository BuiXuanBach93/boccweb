package jp.bo.bocc.repository;

import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import jp.bo.bocc.helper.ConverterUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Namlong on 7/28/2017.
 */
public class AdminNgRepositoryImpl implements AdminNgRepositoryCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public Page<ShmAdminNgDTO> getShmAdminNgWithAdminInfo(ShmAdminNgDTO adminNgDTO, Pageable pageRequest) {
        String sql = "SELECT\n" +
                "    san.ADMIN_NG_ID,\n" +
                "    san.ADMIN_NG_CONTENT ,\n" +
                "    sa.ADMIN_ID,\n" +
                "    sa.ADMIN_NAME,\n" +
                "    san.CMN_ENTRY_DATE createdAt,\n" +
                "    san.CMN_LAST_UPDT_DATE\n, " +
                "    san.SHM_ADMIN_ACTION\n" +
                "FROM\n" +
                "    SHM_ADMIN_NG san\n" +
                "LEFT JOIN\n" +
                "    SHM_ADMIN sa\n" +
                "ON\n" +
                "    sa.ADMIN_ID = san.ADMIN_ID\n" +
                " WHERE san.ADMIN_NG_CONTENT LIKE :adminNgContent " +
                "ORDER BY\n" +
                "    san.CMN_ENTRY_DATE DESC,\n" +
                "    san.CMN_LAST_UPDT_DATE DESC";
        final Query nativeQuery = em.createNativeQuery(sql);
        String adminNgContent = adminNgDTO.getAdminNgContent();
        if (org.apache.commons.lang3.StringUtils.isEmpty(adminNgContent))
            adminNgContent = "";
        nativeQuery.setParameter("adminNgContent", "%" + adminNgContent + "%");
        final List resultList = nativeQuery.getResultList();
        long totalItem = 0;
        if (CollectionUtils.isNotEmpty(resultList))
            totalItem = resultList .size();
        nativeQuery.setFirstResult(pageRequest.getOffset());
        nativeQuery.setMaxResults(pageRequest.getPageSize());
        final List<Object[]> resultListObj = nativeQuery.getResultList();
        List<ShmAdminNgDTO> dto = builtResult(resultListObj);
        Page<ShmAdminNgDTO> result = new PageImpl<ShmAdminNgDTO>(dto, pageRequest, totalItem);
        return result;
    }

    private List<ShmAdminNgDTO> builtResult(List<Object[]> resultList) {
        List<ShmAdminNgDTO> result = new ArrayList<>();
        for (Object[] obj : resultList) {
            ShmAdminNgDTO dto = new ShmAdminNgDTO();
            dto.setAdminNgId(ConverterUtils.convertBigDecimalToLong(obj[0]));
            dto.setAdminNgContent(ConverterUtils.convertStringSafe(obj[1]));
            dto.setAdminId(ConverterUtils.convertBigDecimalToLong(obj[2]));
            dto.setAdminName(ConverterUtils.convertStringSafe(obj[3]));
            dto.setCreatedAt(ConverterUtils.convertTimestampToLocaldatetime(obj[4]));
            dto.setUpdatedAt(ConverterUtils.convertTimestampToLocaldatetime(obj[5]));
            dto.setAdminAction(ConverterUtils.convertStringSafe(obj[6]));
            result.add(dto);
        }
        return result;
    }
}
