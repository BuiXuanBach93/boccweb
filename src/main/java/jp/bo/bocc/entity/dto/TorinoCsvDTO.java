package jp.bo.bocc.entity.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author NguyenThuong on 6/9/2017.
 */
public class TorinoCsvDTO {

    @Getter @Setter
    private String companyName;

    @Getter @Setter
    private String inputDate;

    @Getter @Setter
    private int index;

    @Getter @Setter
    private String bsid;

    @Getter @Setter
    private String memberName;

    @Getter @Setter
    private int menuNumber;

    @Getter @Setter
    private String planId;

    @Getter @Setter
    private int st;

    @Getter @Setter
    private int sortResult;

    @Getter @Setter
    private String startDate;

    @Getter @Setter
    private long totalPerson;

    @Getter @Setter
    private long totalInUse;

    @Getter @Setter
    private Long title;

    @Getter @Setter
    private int stProcess;

    @Getter @Setter
    private String blankValues;
}
