package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Namlong on 3/20/2017.
 */
public class PostMapper {

    public static List<ShmPostDTO> mapEntitiesIntoDTOs(Iterable<ShmPost> entities) {
        List<ShmPostDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    public static List<ShmPostDTO> mapEntitiesIntoDTOs(List<ShmPost> entities) {
        return entities.stream()
                .map(PostMapper::mapEntityIntoDTO)
                .collect(toList());
    }

    public static ShmPostDTO mapEntityIntoDTO(ShmPost e) {
        ShmPostDTO dto = new ShmPostDTO();
        dto.setPostId(e.getPostId());
        dto.setPostName(e.getPostName());
        dto.setPostDescription(e.getPostDescription());
        dto.setPostCategory(e.getPostCategory());
        dto.setPostPrice(e.getPostPrice());
        dto.setPostLikeTimes(e.getPostLikeTimes());
        dto.setPostReportTimes(e.getPostReportTimes());
        dto.setPostType(e.getPostType());
        if(e.getOwnedByCurrentUser() != null){
            dto.setOwnedByCurrentUser(e.getOwnedByCurrentUser());
        }
        if(e.getShmUser() != null){
            dto.setOwnerStatus(e.getShmUser().getStatus());
            dto.setOwnerCtrlStatus(e.getShmUser().getCtrlStatus());
        }
        dto.setPostOriginalImages(e.getPostOriginalImages());
        dto.setPostThumbnailImages(e.getPostThumbnailImages());
        dto.setPostAddrTxt(e.getPostAddrTxt());
        dto.setPostSellStatus(e.getPostSellStatus());
        dto.setPostCtrlStatus(e.getPostCtrlStatus());
        dto.setCmnDeleteFlag(e.getDeleteFlag());
        dto.setCmnEntryDate(e.getCreatedAt());
        dto.setCmnLastUpdtDate(e.getUpdatedAt());
        dto.setCmnEntryUserNo(e.getCreatedBy());
        dto.setCmnEntryUserType(e.getCreatedByType());
        dto.setCmnLastUpdtUserNo(e.getUpdatedBy());
        dto.setCmnLastUpdtUserType(e.getUpdatedByType());
        dto.setCurrentUserFavoriteStatus(e.getCurrentUserFavoriteStatus());
        dto.setDestinationPublishType(e.getDestinationPublishType() == null ? ShmPost.DestinationPublishType.ALL.toString() : e.getDestinationPublishType().toString());
        return dto;
    }

    public static ShmPost mapDTOIntoEntity(ShmPostDTO dto) {
        ShmPost entity = new ShmPost();
        entity.setPostId(dto.getPostId());
        entity.setPostName(dto.getPostName());
        entity.setPostDescription(dto.getPostDescription());
        entity.setPostCategory(dto.getPostCategory());
        entity.setPostPrice(dto.getPostPrice());
        entity.setPostLikeTimes(dto.getPostLikeTimes());
        entity.setPostReportTimes(dto.getPostReportTimes());
        entity.setPostType(dto.getPostType());
        entity.setPostAddrTxt(dto.getPostAddrTxt());
        entity.setPostSellStatus(dto.getPostSellStatus());
        entity.setPostCtrlStatus(dto.getPostCtrlStatus());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());
        return entity;

    }

    public static Page<ShmPostDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShmPost> source) {
        List<ShmPostDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
