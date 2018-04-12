package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import jp.bo.bocc.entity.dto.ShmUserForViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Namlong on 4/10/2017.
 */
public class UserMapper {

    public static List<ShmUserDTO> mapEntitiesIntoDTOs(Iterable<ShmUser> entities) {
        List<ShmUserDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    public static List<ShmUserDTO> mapEntitiesIntoDTOs(List<ShmUser> entities) {
        return entities.stream()
                .map(UserMapper::mapEntityIntoDTO)
                .collect(toList());
    }

    public static ShmUserDTO mapEntityIntoDTO(ShmUser entity) {
        ShmUserDTO dto = new ShmUserDTO();
        dto.setUserId(entity.getId());
        dto.setUserBsid(entity.getBsid());
        dto.setUserFirstName(entity.getFirstName());
        dto.setUserLastName(entity.getLastName());
        dto.setUserNickName(entity.getNickName());
        dto.setUserPwd(entity.getPassword());
        dto.setUserEmail(entity.getEmail());
        dto.setUserPhone(entity.getPhone());
        dto.setUserGender(entity.getGender());
        dto.setUserCtrlStatus((long)entity.getCtrlStatus().ordinal());
        dto.setUserPtrlStatus((long)entity.getPtrlStatus().ordinal());
        dto.setUserDateOfBirth(entity.getDateOfBirth());
        dto.setUserDescr(entity.getDescription());
        dto.setUserJob(entity.getJob());
        dto.setCareer(entity.getCareer());
        dto.setAvatar(entity.getAvatar());
        dto.setIsInPatrol(entity.getIsInPatrol());
        dto.setCmnDeleteFlag(entity.getDeleteFlag());
        dto.setCmnEntryDate(entity.getCreatedAt());
        dto.setCmnLastUpdtDate(entity.getUpdatedAt());
        dto.setCmnEntryUserNo(entity.getCreatedBy());
        dto.setCmnEntryUserType(entity.getCreatedByType());
        dto.setCmnLastUpdtUserNo(entity.getUpdatedBy());
        dto.setCmnLastUpdtUserType(entity.getUpdatedByType());
        dto.setPostNumber(entity.getPostNumber());
        dto.setIsInPatrol(entity.getIsInPatrol());
        dto.setTimePatrol(entity.getTimePatrol());
        final ShmAddr address = entity.getAddress();
        if (address != null)
            dto.setAddressTxt(address.getAreaName());
        return dto;
    }

    public static ShmUserForViewDTO mapUserDTOIntoUserForOtherViewDTO(ShmUserDTO shmUserDTO) {
        ShmUserForViewDTO dto = new ShmUserForViewDTO();
        dto.setUserId(shmUserDTO.getUserId());
        dto.setUserFirstName(shmUserDTO.getUserFirstName());
        dto.setUserLastName(shmUserDTO.getUserLastName());
        dto.setUserNickName(shmUserDTO.getUserNickName());
        dto.setUserPhone(shmUserDTO.getUserPhone());
        dto.setUserGender(shmUserDTO.getUserGender());
        dto.setUserDateOfBirth(shmUserDTO.getUserDateOfBirth());
        dto.setUserDescr(shmUserDTO.getUserDescr());
        dto.setUserJob(shmUserDTO.getUserJob());
        dto.setAvatar(shmUserDTO.getAvatar());
        dto.setAddressTxt(shmUserDTO.getAddressTxt());
        dto.setCareer(shmUserDTO.getCareer());
        dto.setPostNumber(shmUserDTO.getPostNumber());
        dto.setAge(shmUserDTO.getAge());
        return dto;
    }

    public static ShmUser mapDTOIntoEntity(ShmUserDTO dto) {
        ShmUser entity = new ShmUser();
        entity.setId(dto.getUserId());
        entity.setBsid(dto.getUserBsid());
        entity.setFirstName(dto.getUserFirstName());
        entity.setLastName(dto.getUserLastName());
        entity.setNickName(dto.getUserNickName());
        entity.setPassword(dto.getUserPwd());
        entity.setEmail(dto.getUserEmail());
        entity.setPhone(dto.getUserPhone());
        entity.setGender(dto.getUserGender());
        entity.setDateOfBirth(dto.getUserDateOfBirth());
        entity.setDescription(dto.getUserDescr());
        entity.setJob(dto.getUserJob());
        entity.setAvatar(dto.getAvatar());
        entity.setDeleteFlag(dto.getCmnDeleteFlag());
        entity.setCreatedAt(dto.getCmnEntryDate());
        entity.setUpdatedAt(dto.getCmnEntryDate());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setCreatedBy(dto.getCmnEntryUserNo());
        entity.setUpdatedBy(dto.getCmnLastUpdtUserNo());
        entity.setUpdatedByType(dto.getCmnLastUpdtUserType());
        return entity;

    }

    public static Page<ShmUserDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShmUser> source) {
        List<ShmUserDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }
}
