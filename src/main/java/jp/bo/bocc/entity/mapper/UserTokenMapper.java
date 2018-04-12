package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShtUserToken;
import jp.bo.bocc.entity.dto.ShtUserTokenDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public class UserTokenMapper {

    public static List<ShtUserTokenDTO> mapEntitiesIntoDTOs(Iterable<ShtUserToken> entities) {
        List<ShtUserTokenDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShtUserTokenDTO mapEntityIntoDTO(ShtUserToken e) {
        ShtUserTokenDTO dto = new ShtUserTokenDTO();
        dto.setTokenId(e.getTokenString());
        dto.setTokenExpireIn(e.getExpireIn());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        return dto;
    }

    public static ShtUserToken mapDTOIntoEntity(ShtUserTokenDTO dto) {
        ShtUserToken entity = new ShtUserToken();
        entity.setTokenString(dto.getTokenId());
        entity.setExpireIn(dto.getTokenExpireIn());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        return entity;
    }

    public static Page<ShtUserTokenDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShtUserToken> source) {
        List<ShtUserTokenDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
