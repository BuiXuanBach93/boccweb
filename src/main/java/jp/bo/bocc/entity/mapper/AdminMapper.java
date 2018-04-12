package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.dto.ShmAdminDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/20/2017.
 */
public class AdminMapper {

    public static List<ShmAdminDTO> mapEntitiesIntoDTOs(Iterable<ShmAdmin> entities) {
        List<ShmAdminDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShmAdminDTO mapEntityIntoDTO(ShmAdmin e) {
        ShmAdminDTO dto = new ShmAdminDTO();
        dto.setAdminId(e.getAdminId());
        dto.setAdminName(e.getAdminName());
        dto.setAdminPwd(e.getAdminPwd());
        dto.setAdminEmail(e.getAdminEmail());
        dto.setAdminRole(e.getAdminRole());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());

        return dto;
    }

    public static ShmAdmin mapDTOIntoEntity(ShmAdminDTO dto) {
        ShmAdmin entity = new ShmAdmin();
        entity.setAdminId(dto.getAdminId());
        entity.setAdminName(dto.getAdminName());
        entity.setAdminPwd(dto.getAdminPwd());
        entity.setAdminEmail(dto.getAdminEmail());
        entity.setAdminRole(dto.getAdminRole());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());;
        return entity;

    }


    public static Page<ShmAdminDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShmAdmin> source) {
        List<ShmAdminDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
