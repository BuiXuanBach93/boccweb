package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtTalkQa;
import jp.bo.bocc.entity.dto.ShtTalkQaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class TalkQaMapper {

    public static List<ShtTalkQaDTO> mapEntitiesIntoDTOs(Iterable<ShtTalkQa> entities) {
        List<ShtTalkQaDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtTalkQaDTO mapEntityIntoDTO(ShtTalkQa e) {
        ShtTalkQaDTO dto = new ShtTalkQaDTO();
        dto.setTalkQaId(e.getTalkQaId());
        dto.setTalkQaMsg(e.getTalkQaMsg());

        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());
        return dto;
    }

    public static ShtTalkQa mapDTOIntoEntity(ShtTalkQaDTO dto) {
        ShtTalkQa entity = new ShtTalkQa();
        entity.setTalkQaMsg(dto.getTalkQaMsg());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());
        return entity;
    }

    public static Page<ShtTalkQaDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtTalkQa> source) {
        List<ShtTalkQaDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
