package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtAdminCsvHst;
import jp.bo.bocc.entity.dto.ShtAdminCsvHstDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class AdminCsvHstMapper {

    public static List<ShtAdminCsvHstDTO> mapEntitiesIntoDTOs(Iterable<ShtAdminCsvHst> entities) {
        List<ShtAdminCsvHstDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtAdminCsvHstDTO mapEntityIntoDTO(ShtAdminCsvHst e) {
        ShtAdminCsvHstDTO dto = new ShtAdminCsvHstDTO();
        dto.setAdminCsvHstId(e.getAdminCsvHstId());
        dto.setAdmin(e.getAdmin());
        dto.setAdminCsvHstType(e.getAdminCsvHstType());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());

        return dto;
    }

    public static ShtAdminCsvHst mapDTOIntoEntity(ShtAdminCsvHstDTO dto) {
        ShtAdminCsvHst entity = new ShtAdminCsvHst();
        entity.setAdminCsvHstId(dto.getAdminCsvHstId());
        entity.setAdmin(dto.getAdmin());
        entity.setAdminCsvHstType(dto.getAdminCsvHstType());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());
        return entity;

    }


}
