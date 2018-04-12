package jp.bo.bocc.entity.dto;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.system.config.audit.Auditor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Created by NguyenThuong on 3/27/2017.
 */
public class BaseDTO {

    @Getter @Setter
    private Boolean cmnDeleteFlag;

    @Getter @Setter
    private LocalDateTime cmnEntryDate;

    @Getter @Setter
    private LocalDateTime cmnLastUpdtDate;

    @Getter @Setter
    private Auditor cmnEntryUserNo;

    @Getter @Setter
    private Auditor.UserType cmnEntryUserType;

    @Getter @Setter
    private Auditor cmnLastUpdtUserNo;

    @Getter @Setter
    private Auditor.UserType cmnLastUpdtUserType;

    @Getter @Setter
    private LocalDateTime serverDate = LocalDateTime.now();

    @Getter @Setter
    private ShrFile userAppAvatarPath;

    @Getter @Setter
    private Long userAppId;

    @Getter @Setter
    private String userAppNickName;

    @Getter @Setter
    private LocalDateTime createdAt;

    @Getter @Setter
    private LocalDateTime updatedAt;
}
