package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.entity.dto.ShmAddrDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/20/2017.
 */
public class AddrMapper {
    public static List<ShmAddrDTO> mapEntitiesIntoDTOs(Iterable<ShmAddr> entities) {
        List<ShmAddrDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShmAddrDTO mapEntityIntoDTO(ShmAddr e) {
        ShmAddrDTO dto = new ShmAddrDTO();
        dto.setAddrId(e.getAddressId());
        dto.setAddrAreaCode(e.getAreaCode());
        dto.setAddrAreaName(e.getAreaName());
        return dto;
    }

    public static ShmAddr mapDTOIntoEntity(ShmAddrDTO dto) {
        ShmAddr entity = new ShmAddr();
        entity.setAddressId(dto.getAddrId());
        entity.setAreaCode(dto.getAddrAreaCode());
        return entity;

    }

    public static Page<ShmAddrDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShmAddr> source) {
        List<ShmAddrDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
