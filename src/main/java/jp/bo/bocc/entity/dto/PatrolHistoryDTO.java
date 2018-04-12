package jp.bo.bocc.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.time.LocalDateTime;

/**
 * @author NguyenThuong on 5/5/2017.
 */
public class PatrolHistoryDTO {

    @Setter @Getter
    private Long shmAdminId;

    @Setter @Getter
    private String content;

    @Setter @Getter
    private LocalDateTime createdAt;

    @Setter @Getter
    private String shmAdminName;

    @Getter @Setter
    @Transient
    private int sequentNumber;
}
