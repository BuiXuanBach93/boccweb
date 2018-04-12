package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.dto.ShtQaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class QaMapper {
    public static List<ShtQaDTO> mapEntitiesIntoDTOs(Iterable<ShtQa> entities) {
        List<ShtQaDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtQaDTO mapEntityIntoDTO(ShtQa e) {
        ShtQaDTO dto = new ShtQaDTO();
        dto.setQaId(e.getQaId());
        dto.setQaType(e.getQaType());
        dto.setQaStatus(e.getQaStatus());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());

        return dto;
    }

    public static ShtQa mapDTOIntoEntity(ShtQaDTO dto) {
        ShtQa entity = new ShtQa();
        entity.setQaId(dto.getQaId());
        entity.setQaType(dto.getQaType());
        entity.setQaStatus(dto.getQaStatus());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());

        return entity;
    }

    public static Page<ShtQaDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtQa> source) {
        List<ShtQaDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }

}
