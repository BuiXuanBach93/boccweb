package jp.bo.bocc.entity.mapper;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.dto.ShrFileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiTH on 3/20/2017.
 */
public class FileMapper {

    public static List<ShrFileDTO> mapEntitiesIntoDTOs(Iterable<ShrFile> entities) {
        List<ShrFileDTO> dtos = new ArrayList<>();
        entities.forEach(e -> dtos.add(mapEntityIntoDTO(e)));
        return dtos;
    }

    private static ShrFileDTO mapEntityIntoDTO(ShrFile e) {
        ShrFileDTO dto = new ShrFileDTO();
        dto.setFileId(e.getId());
        dto.setFileProvider(e.getProvider());
        dto.setFileOrgName(e.getOriginalName());
        dto.setFileName(e.getName());
        dto.setFileWidth(e.getWidth());
        dto.setFileHeight(e.getHeight());
        dto.setFileSize(e.getSize());
        dto.setFileDir(e.getDir());

        return dto;
    }

    public static ShrFile mapDTOIntoEntity(ShrFileDTO dto) {
        ShrFile entity = new ShrFile();
        entity.setId(dto.getFileId());
        entity.setProvider(dto.getFileProvider());
        entity.setOriginalName(dto.getFileOrgName());
        entity.setName(dto.getFileName());

        return entity;

    }

    public static Page<ShrFileDTO> mapEntityPageIntoDTOPage(Pageable pageRequest, Page<ShrFile> source) {
        List<ShrFileDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, pageRequest, source.getTotalElements());
    }

}
