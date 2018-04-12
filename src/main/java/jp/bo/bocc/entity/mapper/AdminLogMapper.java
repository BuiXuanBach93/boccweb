package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtAdminLog;
import jp.bo.bocc.entity.dto.ShtAdminLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class AdminLogMapper {

    public static List<ShtAdminLogDTO> mapEntitiesIntoDTOs(Iterable<ShtAdminLog> entities) {
        List<ShtAdminLogDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtAdminLogDTO mapEntityIntoDTO(ShtAdminLog e) {
        ShtAdminLogDTO dto = new ShtAdminLogDTO();
        dto.setAdminLogId(e.getAdminLogId());
//        dto.setAdminId(e.getAdminId());
        dto.setAdminLogCont(e.getAdminLogCont());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());

        return dto;
    }

    public static ShtAdminLog mapDTOIntoEntity(ShtAdminLogDTO dto) {
        ShtAdminLog entity = new ShtAdminLog();
        entity.setAdminLogId(dto.getAdminLogId());
//        entity.setAdminId(dto.getAdminId());
        return entity;
    }

    public static Page<ShtAdminLogDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtAdminLog> source) {
        List<ShtAdminLogDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
