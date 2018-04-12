package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtUserRev;
import jp.bo.bocc.entity.dto.ShtUserRevDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class UserRevMapper {
    public static List<ShtUserRevDTO> mapEntitiesIntoDTOs(Iterable<ShtUserRev> entities) {
        List<ShtUserRevDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtUserRevDTO mapEntityIntoDTO(ShtUserRev e) {
        ShtUserRevDTO dto = new ShtUserRevDTO();
        dto.setUserRevId(e.getUserRevId());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        return dto;
    }

    public static ShtUserRev mapDTOIntoEntity(ShtUserRevDTO dto) {
        ShtUserRev entity = new ShtUserRev();
        entity.setUserRevId(dto.getUserRevId());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());


        return entity;
    }

    public static Page<ShtUserRevDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtUserRev> source) {
        List<ShtUserRevDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }

}
