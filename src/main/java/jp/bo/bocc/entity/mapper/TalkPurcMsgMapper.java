package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtTalkPurcMsg;
import jp.bo.bocc.entity.dto.ShtTalkPurcMsgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class TalkPurcMsgMapper {

    public static List<ShtTalkPurcMsgDTO> mapEntitiesIntoDTOs(Iterable<ShtTalkPurcMsg> entities) {
        List<ShtTalkPurcMsgDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    public static ShtTalkPurcMsgDTO mapEntityIntoDTO(ShtTalkPurcMsg e) {
        ShtTalkPurcMsgDTO dto = new ShtTalkPurcMsgDTO();
        dto.setTalkPurcMsgId(e.getTalkPurcMsgId());
        dto.setTalkPurcMsgCont(e.getTalkPurcMsgCont());
        dto.setTalkPurcMsgStatus(e.getTalkPurcMsgStatus().toString());
        dto.setMsgType(e.getTalkPurcMsgType());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());

        return dto;
    }

    public static ShtTalkPurcMsg mapDTOIntoEntity(ShtTalkPurcMsgDTO dto) {
        ShtTalkPurcMsg entity = new ShtTalkPurcMsg();
        entity.setTalkPurcMsgId(dto.getTalkPurcMsgId());
        entity.setTalkPurcMsgCont(dto.getTalkPurcMsgCont());
        entity.setTalkPurcMsgStatus(ShtTalkPurcMsg.TalkPurcMsgStatusEnum.valueOf(dto.getTalkPurcMsgStatus()));
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());
        return entity;

    }

    public static Page<ShtTalkPurcMsgDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtTalkPurcMsg> source) {
        List<ShtTalkPurcMsgDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
