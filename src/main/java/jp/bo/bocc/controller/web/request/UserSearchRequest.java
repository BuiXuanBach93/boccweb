package jp.bo.bocc.controller.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author NguyenThuong on 3/15/2017.
 */
public class UserSearchRequest {

    @Getter @Setter
    private String firstName;

    @Getter @Setter
    private String lastName;

    @Getter @Setter
    private Integer male;

    @Getter @Setter
    private Integer female;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date birthDay;

    @Getter @Setter
    private String legalId;

    @Getter @Setter
    private String bsid;

    @Getter @Setter
    private List<Short> userStt;

    @Getter @Setter
    private Short activeStt;

    @Getter @Setter
    private Short reservedStt;

    @Getter @Setter
    private Short leftStt;

    @Getter @Setter
    private String phoneNumber;

    @Getter @Setter
    private Long provinceId;

    @Getter @Setter
    private Long districtId;

    @Getter @Setter
    private String email;

    @Getter @Setter
    private Integer userType;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date fromDate;

    @Getter @Setter
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date toDate;

    @Getter @Setter
    private int page;

    @Getter @Setter
    private int size;
}
