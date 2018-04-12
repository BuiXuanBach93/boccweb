package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShmAdminNg;
import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/20/2017.
 */
public class AdminNgMapper {
    public static List<ShmAdminNgDTO> mapEntitiesIntoDTOs(Iterable<ShmAdminNg> entities) {
        List<ShmAdminNgDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShmAdminNgDTO mapEntityIntoDTO(ShmAdminNg e) {
        ShmAdminNgDTO dto = new ShmAdminNgDTO();
        dto.setAdminNgId(e.getAdminNgId());
        dto.setAdminNgContent(e.getAdminNgContent());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());
        return dto;
    }

    public static ShmAdminNg mapDTOIntoEntity(ShmAdminNgDTO dto) {
        ShmAdminNg entity = new ShmAdminNg();
        entity.setAdminNgId(dto.getAdminNgId());
        entity.setAdminNgContent(dto.getAdminNgContent());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedAt(dto.getCmnLastUpdtDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedByType(dto.getCmnEntryUserType());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());
        return entity;
    }


    public static Page<ShmAdminNgDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShmAdminNg> source) {
        List<ShmAdminNgDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }

}
