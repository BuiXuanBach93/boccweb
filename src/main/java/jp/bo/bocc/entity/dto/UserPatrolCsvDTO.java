package jp.bo.bocc.entity.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author NguyenThuong on 6/6/2017.
 */
public class UserPatrolCsvDTO {

    @Getter @Setter
    private String nickName;

    @Getter @Setter
    private String adminName;

    @Getter @Setter
    private long totalComment;

    @Getter @Setter
    private String commentNewest;
}
