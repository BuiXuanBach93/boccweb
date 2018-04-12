package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtMemoQa;
import jp.bo.bocc.entity.dto.ShtMemoQaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class MemoQaMapper {

    public static List<ShtMemoQaDTO> mapEntitiesIntoDTOs(Iterable<ShtMemoQa> entities) {
        List<ShtMemoQaDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtMemoQaDTO mapEntityIntoDTO(ShtMemoQa e) {
        ShtMemoQaDTO dto = new ShtMemoQaDTO();
        dto.setMemoId(e.getMemoId());
        dto.setQa(e.getQa());
        dto.setMemoCont(e.getContent());
        dto.setAdmin(e.getAdmin());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());

        return dto;
    }

    public static ShtMemoQa mapDTOIntoEntity(ShtMemoQaDTO dto) {
        ShtMemoQa entity = new ShtMemoQa();
        entity.setMemoId(dto.getMemoId());
        entity.setContent(dto.getMemoCont());
        entity.setQa(dto.getQa());
        entity.setAdmin(dto.getAdmin());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        return entity;
    }

    public static Page<ShtMemoQaDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtMemoQa> source) {
        List<ShtMemoQaDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
