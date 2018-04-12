package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import jp.bo.bocc.helper.ConverterUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class TalkPurcMapper {

    public static List<ShtTalkPurcDTO> mapEntitiesIntoDTOs(Iterable<ShtTalkPurc> entities) {
        List<ShtTalkPurcDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    public static ShtTalkPurcDTO mapEntityIntoDTO(ShtTalkPurc e) {
        ShtTalkPurcDTO dto = new ShtTalkPurcDTO();
        dto.setTalkPurcId(e.getTalkPurcId());
        dto.setShmPostId(e.getShmPost().getPostId());
        dto.setShmUserId(e.getShmUser().getId());
        dto.setTalkPurcStatus(e.getTalkPurcStatus());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());
        return dto;
    }

    public static ShtTalkPurc mapDTOIntoEntity(ShtTalkPurcDTO dto) {
        ShtTalkPurc entity = new ShtTalkPurc();
        entity.setTalkPurcId(dto.getTalkPurcId());
        entity.setTalkPurcStatus(dto.getTalkPurcStatus());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());
        return entity;
    }

    public static Page<ShtTalkPurcDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtTalkPurc> source) {
        List<ShtTalkPurcDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }

    public static Page<ShtTalkPurcDTO> mapEntityItemIntoDTOPage(Pageable pageRequest, List<Object[]> listFromDB) {
        List<ShtTalkPurcDTO> dtos = buildDataForListTalkPurc(listFromDB);
        return new PageImpl<>(dtos, pageRequest, dtos.size());
    }

    private static List<ShtTalkPurcDTO> buildDataForListTalkPurc(List<Object[]> listFromDB) {
        List<ShtTalkPurcDTO> dtos = new ArrayList<>();
        listFromDB.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtTalkPurcDTO mapEntityIntoDTO(Object[] e) {
        ShtTalkPurcDTO dto = new ShtTalkPurcDTO();
        dto.setTalkPurcId(ConverterUtils.getLongValue(e[0]));
        dto.setLastMesageInTalkPurcList(String.valueOf(e[1]));
        dto.setUserNickname(String.valueOf(e[2]));
        dto.setShmUserId(ConverterUtils.getLongValue(e[3]));
        dto.setTalkPurcMsgCreator(ConverterUtils.getLongValue(e[4]));
        return dto;
    }
}
