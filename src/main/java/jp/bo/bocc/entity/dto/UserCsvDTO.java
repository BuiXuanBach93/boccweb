package jp.bo.bocc.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author NguyenThuong on 6/2/2017.
 */
public class UserCsvDTO {

    @Getter @Setter
    private long userId;

    @Getter @Setter
    private String nickName;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String phoneNumber;

    @Getter @Setter
    private String legalId;

    @Getter @Setter
    private String bsid;

    @Getter @Setter
    private String province;

    @Getter @Setter
    private String district;

    @Getter @Setter
    private String sex;

    @Getter @Setter
    private LocalDate birthDay;

    @Getter @Setter
    private String birthDayTxt;

    @Getter @Setter
    private String career;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private long totalSellProduct;

    @Getter @Setter
    private long totalRequestProduct;

    @Getter @Setter
    private long totalSellTransaction;

    @Getter @Setter
    private long totalSellTransactionUnique;

    @Getter @Setter
    private long totalRequestTransaction;

    @Getter @Setter
    private long totalRequestTransactionUnique;

    @Getter @Setter
    private long totalTransaction;

    @Getter @Setter
    private long totalTransactionUnique;

    @Getter @Setter
    private long totalReview;

    @Getter @Setter
    private long totalReviewMe;

    @Getter @Setter
    private long totalReport;

    @Getter @Setter
    private long totalReportMe;

    @Getter @Setter
    private String suspendFlag;

    @Getter @Setter
    private String leftDate;
}