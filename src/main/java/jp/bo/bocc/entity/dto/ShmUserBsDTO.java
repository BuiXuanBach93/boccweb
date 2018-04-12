package jp.bo.bocc.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Created by buixu on 3/7/2018.
 */
public class ShmUserBsDTO {
    @Getter @Setter
    private String bsId;

    @Getter @Setter
    private String individualDate;

    @Getter @Setter
    private String generalDate;
}
