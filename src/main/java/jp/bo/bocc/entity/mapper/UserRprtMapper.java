package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtUserRprt;
import jp.bo.bocc.entity.dto.ShtUserRprtDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class UserRprtMapper {

    public static List<ShtUserRprtDTO> mapEntitiesIntoDTOs(Iterable<ShtUserRprt> entities) {
        List<ShtUserRprtDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtUserRprtDTO mapEntityIntoDTO(ShtUserRprt e) {
        ShtUserRprtDTO dto = new ShtUserRprtDTO();
        dto.setUserRprtId(e.getUserRprtId());
        dto.setUserRprtCont(e.getUserRprtCont());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        return dto;
    }

    public static ShtUserRprt mapDTOIntoEntity(ShtUserRprtDTO dto) {
        ShtUserRprt entity = new ShtUserRprt();
        entity.setUserRprtId(dto.getUserRprtId());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        return entity;
    }

    public static Page<ShtUserRprtDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtUserRprt> source) {
        List<ShtUserRprtDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }

}
