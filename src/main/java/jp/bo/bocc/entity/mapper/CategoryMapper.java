package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShmCategory;
import jp.bo.bocc.entity.dto.ShmCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/20/2017.
 */
public class CategoryMapper {
    public static List<ShmCategoryDTO> mapEntitiesIntoDTOs(Iterable<ShmCategory> entities) {
        List<ShmCategoryDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShmCategoryDTO mapEntityIntoDTO(ShmCategory e) {
        ShmCategoryDTO dto = new ShmCategoryDTO();
        dto.setCategoryId(e.getCategoryId());
        dto.setCategoryName(e.getCategoryName());
//        dto.setCategoryIcon(e.getCategoryIcon());
        dto.setCategoryParentId(e.getCategoryParentId());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());
        return dto;
    }

    public static ShmCategory mapDTOIntoEntity(ShmCategoryDTO dto) {
        ShmCategory entity = new ShmCategory();
        entity.setCategoryId(dto.getCategoryId());
        entity.setCategoryName(dto.getCategoryName());
//        entity.setCategoryIcon(dto.getCategoryIcon());
        entity.setCategoryParentId(dto.getCategoryParentId());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());
        return entity;

    }


    public static Page<ShmCategoryDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShmCategory> source) {
        List<ShmCategoryDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }

}
